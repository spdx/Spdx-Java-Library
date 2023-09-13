/**
 * Copyright (c) 2019 Source Auditor Inc.
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
package org.spdx.storage.listedlicense;

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
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;

/**
 * @author gary   Original code
 * @author pmonks Optional caching of downloaded files
 *
 */
public class SpdxListedLicenseWebStore extends SpdxListedLicenseModelStore {

	private static final int READ_TIMEOUT = 5000;
	private static final int IO_BUFFER_SIZE = 8192;
	
	static final List<String> WHITE_LIST = Collections.unmodifiableList(Arrays.asList(
			"spdx.org", "spdx.dev", "spdx.com", "spdx.info")); // Allowed host names for the SPDX listed licenses

	// See https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html
	private final String cacheDir = ((System.getenv("XDG_CACHE_HOME") == null ||
			System.getenv("XDG_CACHE_HOME").trim() == "") ?
			System.getProperty("user.home") + "/.cache" :
			System.getenv("XDG_CACHE_HOME")) +
			"/Spdx-Java-Library";

	private final boolean cacheEnabled = Boolean.parseBoolean(
			System.getProperty("org.spdx.storage.listedlicense.SpdxListedLicenseWebStore.enableCache"));

	final DateTimeFormatter iso8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'").withZone(ZoneOffset.UTC);

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxListedLicenseWebStore() throws InvalidSPDXAnalysisException {
		super();
		if (cacheEnabled) {
			try {
				final File cacheDirectory = new File(cacheDir);
				Files.createDirectories(cacheDirectory.toPath());
			} catch (IOException ioe) {
				throw new InvalidSPDXAnalysisException("Unable to create cache directory: " + cacheDir, ioe);
			}
		}
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

	private InputStream getUrlInputStream(final URL url) throws IOException {
		InputStream result = null;
		if (cacheEnabled) {
			result = getUrlInputStreamThroughCache(url);
		} else {
			result = getUrlInputStreamDirect(url);
		}
		return result;
	}

	private InputStream getUrlInputStreamThroughCache(final URL url) throws IOException {
		final String cacheKey           = base64Encode(url);
		final File   cachedFile         = new File(cacheDir, cacheKey);
		final File   cachedMetadataFile = new File(cacheDir, cacheKey + ".metadata.json");

		if (cachedFile.exists() && cachedMetadataFile.exists()) {
			final HashMap<String,String> cachedMetadata = readMetadataFile(cachedMetadataFile);

			if (cachedMetadata != null)
			{
				final String            eTag       = cachedMetadata.get("eTag");
				final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setReadTimeout(READ_TIMEOUT);
				connection.setRequestProperty("If-None-Match", eTag);
				final int status = connection.getResponseCode();
				if (status != HttpURLConnection.HTTP_NOT_MODIFIED) {
					cacheMiss(url, connection);
				}
			} else {
				cacheMiss(url);
			}
		} else {
			cacheMiss(url);
		}

		// At this point the cached file definitely exists
		return new BufferedInputStream(new FileInputStream(cachedFile));
	}

	private URL processPossibleRedirect(final HttpURLConnection connection) throws IOException {
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
				throw new IOException("Invalid redirect URL");
			}
			if (!result.getProtocol().toLowerCase().startsWith("http")) {
				throw new IOException("Invalid redirect protocol");
			}
			if (!WHITE_LIST.contains(result.getHost())) {
				throw new IOException("Invalid redirect host - not on the allowed 'white list'");
			}
		}
		return result;
	}

	private HashMap<String,String> readMetadataFile(final File metadataFile) {
		HashMap<String,String> result = null;
		try {
			final Reader r = new BufferedReader(new FileReader(metadataFile));
			result = new Gson().fromJson(r, new TypeToken<HashMap<String, String>>(){}.getType());
		}
		catch (IOException ioe) {
			result = null;  // Treat errors as a cache miss
		}
		return result;
	}

	private void writeMetadataFile(final File metadataFile, HashMap<String,String> metadata) throws IOException {
		final Writer w = new BufferedWriter(new FileWriter(metadataFile));
		try {
			new Gson().toJson(metadata, new TypeToken<HashMap<String, String>>(){}.getType(), w);
		} finally {
			w.flush();
			w.close();
		}
	}

	private void writeContentFile(final InputStream urlInputStream, final File cachedFile) throws IOException {
		final OutputStream cacheFileOutputStream = new BufferedOutputStream(new FileOutputStream(cachedFile));
		try {
			byte[] ioBuffer = new byte[IO_BUFFER_SIZE];
			int length;
			while ((length = urlInputStream.read(ioBuffer)) != -1) {
				cacheFileOutputStream.write(ioBuffer, 0, length);
			}
		} finally {
			urlInputStream.close();
			cacheFileOutputStream.flush();
			cacheFileOutputStream.close();
		}
	}

	private void cacheMiss(URL url, HttpURLConnection connection) throws IOException {
		final URL redirectUrl = processPossibleRedirect(connection);
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
			metadata.put("sourceUrl", url.toString());
			writeMetadataFile(cachedMetadataFile, metadata);
		} else {
			throw new IOException("Unexpected HTTP status code from " + url.toString() + ": " + status);
		}
	}

	private void cacheMiss(final URL url) throws IOException {
		final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setReadTimeout(READ_TIMEOUT);
		cacheMiss(url, connection);
	}

	private InputStream getUrlInputStreamDirect(final URL url) throws IOException {
		InputStream       result     = null;
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setReadTimeout(READ_TIMEOUT);

		final URL redirectUrl = processPossibleRedirect(connection);

		if (redirectUrl != null) {
			connection = (HttpURLConnection)redirectUrl.openConnection();
			connection.setReadTimeout(READ_TIMEOUT);

			final int status = connection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				result = redirectUrl.openConnection().getInputStream();
			} else {
				throw new IOException("Unexpected HTTP status code from " + redirectUrl.toString() + ": " + status);
			}
		} else {
			final int status = connection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				result = connection.getInputStream();
			} else {
				throw new IOException("Unexpected HTTP status code from " + url.toString() + ": " + status);
			}
		}
		return result;
	}

	@Override
	InputStream getTocInputStream() throws IOException {
		return getUrlInputStream(new URL(SpdxConstants.LISTED_LICENSE_URL + LICENSE_TOC_FILENAME));
	}

	@Override
	InputStream getLicenseInputStream(String licenseId) throws IOException {
		return getUrlInputStream(new URL(SpdxConstants.LISTED_LICENSE_URL + licenseId + JSON_SUFFIX));
	}

	@Override
	InputStream getExceptionTocInputStream() throws IOException {
		return getUrlInputStream(new URL(SpdxConstants.LISTED_LICENSE_URL + EXCEPTION_TOC_FILENAME));
	}

	@Override
	InputStream getExceptionInputStream(String exceptionId) throws IOException {
		return getLicenseInputStream(exceptionId);	// Same URL using exception ID rather than license ID
	}
	
	@Override
	public void close() throws Exception {
		// Nothing to do for the either the in-memory or the web store
	}
}
