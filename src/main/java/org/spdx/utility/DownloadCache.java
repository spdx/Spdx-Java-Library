/**
 * Copyright (c) 2023 Peter Monks
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.spdx.Configuration;

/**
 * This singleton class provides a flexible download cache for the rest of the library.  If enabled, URLs that are
 * requested using this class will have their content automatically cached locally on disk (in a directory that adheres
 * to the XDG Base Directory Specification - https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html),
 * and any subsequent requests will be served out of that cache.  Cache entries will also be automatically checked every
 * so often for staleness using HTTP ETag requests (which are more efficient than full HTTP requests).  The interval
 * between such checks is configurable (and can even be turned off, which makes every download request re-check the URL
 * for staleness).
 *
 * The cache is configured via these Configuration options:
 * * org.spdx.storage.listedlicense.enableCache:
 *   Controls whether the cache is enabled or not. Defaults to false i.e. the cache is disabled.
 * * org.spdx.storage.listedlicense.cacheCheckIntervalSecs:
 *   How many seconds should the cache wait between issuing ETag requests to determine whether cached content is
 *   stale? Defaults to 86,400 seconds (24 hours).
 */
public final class DownloadCache {
    private static final Logger logger = LoggerFactory.getLogger(DownloadCache.class);

    private static final int READ_TIMEOUT = 5000;
    private static final int IO_BUFFER_SIZE = 8192;
    private static final long DEFAULT_CACHE_CHECK_INTERVAL_SECS = 86400;   // 24 hours, in seconds

    static final List<String> WHITE_LIST = Collections.unmodifiableList(Arrays.asList(
            "spdx.org", "spdx.dev", "spdx.com", "spdx.info")); // Allowed host names for the SPDX listed licenses

    private static DownloadCache singleton;

    // See https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html
    private final String cacheDir = ((System.getenv("XDG_CACHE_HOME") == null ||
            System.getenv("XDG_CACHE_HOME").trim() == "") ?
            System.getProperty("user.home") + File.separator + ".cache" :
            System.getenv("XDG_CACHE_HOME")) +
            File.separator + "Spdx-Java-Library";

    private final String CONFIG_PROPERTY_CACHE_ENABLED = "org.spdx.storage.listedlicense.enableCache";
    private final String CONFIG_PROPERTY_CACHE_CHECK_INTERVAL_SECS = "org.spdx.storage.listedlicense.cacheCheckIntervalSecs";
    private final boolean cacheEnabled;
    private final long cacheCheckIntervalSecs;

    private final DateTimeFormatter iso8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'").withZone(ZoneOffset.UTC);

    /**
     * This class is a singleton - use getInstance() to obtain the instance.
     */
    private DownloadCache() {
        boolean tmpCacheEnabled = Boolean.parseBoolean(Configuration.getInstance().getProperty(CONFIG_PROPERTY_CACHE_ENABLED, "false"));
        if (tmpCacheEnabled) {
            try {
                final File cacheDirectory = new File(cacheDir);
                Files.createDirectories(cacheDirectory.toPath());
            } catch (IOException ioe) {
                logger.warn("Unable to create cache directory '" + cacheDir + "'; continuing with cache disabled.", ioe);
                tmpCacheEnabled = false;
            }
        }
        cacheEnabled = tmpCacheEnabled;
        long tmpCacheCheckIntervalSecs = DEFAULT_CACHE_CHECK_INTERVAL_SECS;
        try {
            tmpCacheCheckIntervalSecs = Long.parseLong(Configuration.getInstance().getProperty(CONFIG_PROPERTY_CACHE_CHECK_INTERVAL_SECS));
        } catch(NumberFormatException nfe) {
            // Ignore parse failures - in this case we use the default value of 24 hours
        }
        cacheCheckIntervalSecs = tmpCacheCheckIntervalSecs;
    }

    /**
     * @return The singleton instance of the DownloadCache class.
     */
    public static DownloadCache getInstance() {
        if (singleton == null) {
            singleton = new DownloadCache();
        }
        return singleton;
    }

    /**
     * Recursively removes a directory. USE WITH CAUTION!
     *
     * @param dir The directory to delete.
     */
    private static void rmdir(final File dir) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] contents = dir.listFiles();
            if (contents != null) {
                for (final File f : contents) {
                    if (f.isDirectory()) {
                        rmdir(f);
                    } else {
                        f.delete();
                    }
                }
            }
            dir.delete();
        }
    }

    /**
     * Resets (deletes) the local cache.
     */
    public void resetCache() throws IOException {
        final File cacheDirectory = new File(cacheDir);

        rmdir(cacheDirectory);
        Files.createDirectories(cacheDirectory.toPath());
    }

    /**
     * @param url The URL to get an input stream for.
     * @return An InputStream for url, or null if url is null.  Note that this InputStream may be of different concrete
     *        types, depending on whether the content is being served out of cache or not.
     * @throws IOException When an IO error of some kind occurs.
     */
    public InputStream getUrlInputStream(final URL url) throws IOException {
        return getUrlInputStream(url, true);
    }

    /**
     *
     * @param url The URL to get an input stream for.
     * @param restrictRedirects A flag that controls whether redirects returned by url are restricted to known SPDX
     *                          hosts or not. Defaults to true. USE EXTREME CAUTION WHEN TURNING THIS OFF!
     * @return An InputStream for url, or null if url is null.  Note that this InputStream may be of different concrete
     *        types, depending on whether the content is being served out of cache or not.
     * @throws IOException When an IO error of some kind occurs.
     */
    public InputStream getUrlInputStream(final URL url, final boolean restrictRedirects) throws IOException {
        InputStream result = null;
        if (url != null) {
            if (cacheEnabled) {
                result = getUrlInputStreamThroughCache(url, restrictRedirects);
            } else {
                result = getUrlInputStreamDirect(url, restrictRedirects);
            }
        }
        return result;
    }

    /**
     * @param url The URL to get an input stream for, ignoring the local cache.
     * @param restrictRedirects A flag that controls whether redirects returned by url are restricted to known SPDX
     *                          hosts or not. Defaults to true. USE EXTREME CAUTION WHEN TURNING THIS OFF!
     * @return An InputStream for url, or null if url is null.
     * @throws IOException When an IO error of some kind occurs.
     */
    private InputStream getUrlInputStreamDirect(URL url, boolean restrictRedirects) throws IOException {
        InputStream       result     = null;
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setReadTimeout(READ_TIMEOUT);
        final URL redirectUrl = processPossibleRedirect(connection, restrictRedirects);

        if (redirectUrl != null) {
            url        = redirectUrl;
            connection = (HttpURLConnection)redirectUrl.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
        }
        final int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            result = connection.getInputStream();
        } else {
            throw new IOException("Unexpected HTTP status code from " + url.toString() + ": " + status);
        }
        return result;
    }

    /**
     * @param url The URL to get an input stream for, leveraging the local cache.
     * @param restrictRedirects A flag that controls whether redirects returned by url are restricted to known SPDX
     *                          hosts or not. Defaults to true. USE EXTREME CAUTION WHEN TURNING THIS OFF!
     * @return An InputStream for url, or null if url is null.  Note that this InputStream may be of different concrete
     *        types, depending on whether the content is being served out of cache or not.
     * @throws IOException When an IO error of some kind occurs.
     */
    private InputStream getUrlInputStreamThroughCache(final URL url, boolean restrictRedirects) throws IOException {
        final String cacheKey           = base64Encode(url);
        final File   cachedFile         = new File(cacheDir, cacheKey);
        final File   cachedMetadataFile = new File(cacheDir, cacheKey + ".metadata.json");

        if (cachedFile.exists() && cachedMetadataFile.exists()) {
            try {
                checkCache(url, restrictRedirects);
            } catch (IOException ioe) {
                // We know we have a locally cached file here, so if we happen to get an exception we can safely ignore
                // it and fall back on the (possibly stale) cached content file.  This makes the code more robust in the
                // presence of network errors when the cache has previously been populated.
            }
        } else {
            cacheMiss(url, restrictRedirects);
        }

        // At this point the cached file definitely exists
        return new BufferedInputStream(new FileInputStream(cachedFile));
    }

    /**
     * Checks the cache for content from the given url, and brings the cached content up to date if it's stale.
     * @param url The url to check.
     * @param restrictRedirects A flag that controls whether redirects returned by url are restricted to known SPDX
     *                          hosts or not. Defaults to true. USE EXTREME CAUTION WHEN TURNING THIS OFF!
     * @throws IOException When an IO error of some kind occurs.
     */
    private void checkCache(final URL url, boolean restrictRedirects) throws IOException {
        final String                 cacheKey           = base64Encode(url);
        final File                   cachedMetadataFile = new File(cacheDir, cacheKey + ".metadata.json");
        final HashMap<String,String> cachedMetadata     = readMetadataFile(cachedMetadataFile);

        if (cachedMetadata != null) {
            final Instant lastChecked = parseISO8601String(cachedMetadata.get("lastChecked"));
            final long    difference  = lastChecked  != null ? Math.abs(ChronoUnit.SECONDS.between(Instant.now(), lastChecked)) : Long.MAX_VALUE;

            if (difference > cacheCheckIntervalSecs) {
                // It's been a while since we checked the cached download of this URL for staleness, so make an ETag request
                logger.debug("Cache check interval exceeded; checking for updates to " + String.valueOf(url));
                final String eTag = cachedMetadata.get("eTag");
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setRequestProperty("If-None-Match", eTag);
                final int status = connection.getResponseCode();
                if (status != HttpURLConnection.HTTP_NOT_MODIFIED) {
                    // The content of the URL has changed, which we handle the same as a cache miss (i.e. we re-download
                    // the content, and write a new metadata file from scratch)
                    cacheMiss(url, connection, restrictRedirects);
                } else {
                    // The content hasn't changed, so just update the lastChecked metadata but otherwise do nothing
                    logger.debug("Cache hit for " + String.valueOf(url));
                    cachedMetadata.put("lastChecked", iso8601.format(Instant.now()));
                    writeMetadataFile(cachedMetadataFile, cachedMetadata);
                }
            } else {
                // We checked recently, so don't need to do anything - the cached content will be used
                logger.debug("Within cache check interval; skipping check of updates to " + String.valueOf(url));
            }
        } else {
            // Metadata doesn't exist - treat it as a cache miss
            cacheMiss(url, restrictRedirects);
        }
    }

    /**
     * Process a cache miss, which involves downloading the content from the given url, and writing out an associated
     * metadata file (in JSON format) containing sufficient information for the cache to check for staleness in the
     * future.
     * @param connection The open HTTP connection to download and cache.
     * @param restrictRedirects A flag that controls whether redirects returned by url are restricted to known SPDX
     *                          hosts or not. Defaults to true. USE EXTREME CAUTION WHEN TURNING THIS OFF!
     * @throws IOException When an IO error of some kind occurs.
     */
    private void cacheMiss(URL url, HttpURLConnection connection, boolean restrictRedirects) throws IOException {
        logger.debug("Cache miss for " + String.valueOf(url));

        final URL redirectUrl = processPossibleRedirect(connection, restrictRedirects);
        if (redirectUrl != null) {
            url        = redirectUrl;
            connection = (HttpURLConnection)redirectUrl.openConnection();
        }
        final int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            final String cacheKey = base64Encode(url);
            final File cachedFile = new File(cacheDir, cacheKey);
            writeContentFile(connection.getInputStream(), cachedFile);
            final File cachedMetadataFile = new File(cacheDir, cacheKey + ".metadata.json");
            final HashMap<String, String> metadata = new HashMap<String, String>();
            metadata.put("eTag", connection.getHeaderField("ETag"));
            metadata.put("downloadedAt", iso8601.format(Instant.now()));
            metadata.put("lastChecked", iso8601.format(Instant.now()));
            metadata.put("sourceUrl", url.toString());
            writeMetadataFile(cachedMetadataFile, metadata);
        } else {
            throw new IOException("Unexpected HTTP status code from " + url.toString() + ": " + status);
        }
    }

    /**
     * Process a cache miss, which involves downloading the content from the given url, and writing out an associated
     * metadata file (in JSON format) containing sufficient information for the cache to check for staleness in the
     * future.
     * @param url The url to download and cache.
     * @param restrictRedirects A flag that controls whether redirects returned by url are restricted to known SPDX
     *                          hosts or not. Defaults to true. USE EXTREME CAUTION WHEN TURNING THIS OFF!
     * @throws IOException When an IO error of some kind occurs.
     */
    private void cacheMiss(final URL url, boolean restrictRedirects) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setReadTimeout(READ_TIMEOUT);
        cacheMiss(url, connection, restrictRedirects);
    }


    /**
     * Processes an HTTP redirect (if any) returned by the given connection, returning the URL
     * @param connection The connection to check for a redirect.
     * @param restrictRedirects A flag that controls whether redirects returned by url are restricted to known SPDX
     *                          hosts or not. Defaults to true. USE EXTREME CAUTION WHEN TURNING THIS OFF!
     * @return The redirect URL, or null if there wasn't one.
     * @throws IOException When an IO error of some kind occurs.
     */
    private URL processPossibleRedirect(final HttpURLConnection connection, final boolean restrictRedirects) throws IOException {
        URL result = null;
        final int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER) {
            // redirect
            final String redirectUrlStr = connection.getHeaderField("Location");
            if (Objects.isNull(redirectUrlStr) || redirectUrlStr.isEmpty()) {
                throw new IOException("Empty redirect URL response");
            }
            try {
                result = new URL(redirectUrlStr);
            } catch (Exception ex) {
                throw new IOException("Invalid redirect URL", ex);
            }
            if (!result.getProtocol().toLowerCase().startsWith("http")) {
                throw new IOException("Invalid redirect protocol");
            }
            if (restrictRedirects && !WHITE_LIST.contains(result.getHost())) {
                throw new IOException("Invalid redirect host - not on the allowed 'white list'");
            }
        }
        return result;
    }

    /**
     * Reads a metadata file out of local cache.
     * @param metadataFile The metadata file to read.
     * @return The metadata read from the file, or null if the file doesn't exist or there was an error while reading
     *         it.
     */
    private HashMap<String,String> readMetadataFile(final File metadataFile) {
        HashMap<String,String> result = null;
        try {
            final Reader r = new BufferedReader(new FileReader(metadataFile));
            result = new Gson().fromJson(r, new TypeToken<HashMap<String, String>>(){}.getType());
        }
        catch (IOException ioe) {
            result = null;  // Treat metadata read errors as a cache miss
        }
        return result;
    }

    /**
     * Writes a metadata file to the local cache.
     * @param metadataFile The metadata file to write. Note: if it already exists it will be silently overwritten.
     * @param metadata The metadata to write to the file.
     * @throws IOException When an IO error of some kind occurs.
     */
    private void writeMetadataFile(final File metadataFile, HashMap<String,String> metadata) throws IOException {
        final Writer w = new BufferedWriter(new FileWriter(metadataFile));
        try {
            new Gson().toJson(metadata, new TypeToken<HashMap<String, String>>(){}.getType(), w);
        } finally {
            w.flush();
            w.close();
        }
    }

    /**
     * Writes a content file to the local cache.
     * @param is The InputStream to read the content from. Note: this InputStream must be open at the time this
     *           method is called, and will be fully consumed and closed by this method.
     * @param cachedFile The content file to write to. Note: if it already exists it will be silently overwritten.
     * @throws IOException When an IO error of some kind occurs.
     */
    private void writeContentFile(final InputStream is, final File cachedFile) throws IOException {
        final OutputStream cacheFileOutputStream = new BufferedOutputStream(new FileOutputStream(cachedFile));
        try {
            byte[] ioBuffer = new byte[IO_BUFFER_SIZE];
            int length;
            while ((length = is.read(ioBuffer)) != -1) {
                cacheFileOutputStream.write(ioBuffer, 0, length);
            }
        } finally {
            is.close();
            cacheFileOutputStream.flush();
            cacheFileOutputStream.close();
        }
    }

    /**
     * Attempts to parse s as if it were an ISO8601 formatted String.
     * @param s The string to attempt to parse.
     * @return The Instant for that ISO8601 value if parsing succeeded, or null if it didn't.
     */
    private final Instant parseISO8601String(final String s) {
        Instant result = null;
        if (s != null) {
            try {
                result = Instant.parse(s);
            } catch (final DateTimeParseException dtpe) {
                result = null;
            }
        }
        return result;
    }

    /**
     * @param s The String to BASE64 encode.
     * @return The BASE64 encoding of s (as UTF-8).
     */
    private String base64Encode(final String s) {
        String result = null;
        if (s != null) {
            result = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
        }
        return result;
    }

    /**
     * @param u The URL to BASE64 encode.
     * @return The BASE64 encoding of u (as a UTF-8 encoded String).
     */
    private String base64Encode(final URL u) {
        String result = null;
        if (u != null) {
            result = base64Encode(u.toString());
        }
        return result;
    }

}
