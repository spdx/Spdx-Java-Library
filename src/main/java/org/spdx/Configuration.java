/**
 * Copyright (c) 2023 Source Auditor Inc.
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
package org.spdx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The configuration class for the Spdx-Java-Library. When a caller attempts to retrieve a configuration property, it
 * will first be checked in the Java system properties (i.e. set via `-D` command line options to the JVM, or by
 * programmatic calls to `System.setProperty()` in code), and will then fallback on a properties file in the classpath.
 * That file must be called `/resources/spdx-java-library.properties`.
 *
 * Please see the documentation for specifics on what configuration options Spdx-Java-Library supports, and how they
 * impact the library's behavior.
 */
public final class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class.getName());
    private static final String PROPERTIES_DIR = "resources";
    private static final String CONFIGURATION_PROPERTIES_FILENAME = PROPERTIES_DIR + "/" + "spdx-java-library.properties";
    private static final String DEPRECATED_CONFIGURATION_PROPERTIES_FILENAME = PROPERTIES_DIR + "/" + "licenses.properties";   // Deprecated filename

    private static Configuration singleton;
    private final Properties properties;

    private Configuration() {
        Properties tmpProperties = loadProperties(CONFIGURATION_PROPERTIES_FILENAME);
        if (tmpProperties == null) {
            // This is to preserve backwards compatibility with version 1.1.7 of the library and earlier
            tmpProperties = loadProperties(DEPRECATED_CONFIGURATION_PROPERTIES_FILENAME);
            if (tmpProperties != null) {
                logger.warn("You are using a deprecated configuration properties filename ('" + DEPRECATED_CONFIGURATION_PROPERTIES_FILENAME + "'). Please consider migrating to the new name ('" + CONFIGURATION_PROPERTIES_FILENAME + "').");
            }
        }
        properties = tmpProperties;
    }

    /**
     * @return The singleton instance of the Configuration class.
     */
    public static Configuration getInstance() {
        if (singleton == null) {
            singleton = new Configuration();
        }
        return singleton;
    }

    /**
     * @param propertyName The name of the configuration property to retrieve.
     * @return The value of the given property name, or null if it wasn't found.
     */
    public String getProperty(final String propertyName) {
        return getProperty(propertyName, null);
    }

    /**
     * @param propertyName The name of the configuration property to retrieve.
     * @param defaultValue The default value to return, if the property isn't found.
     * @return The value of the given property name, or defaultValue if it wasn't found.
     */
    public String getProperty(final String propertyName, final String defaultValue) {
        return System.getProperty(propertyName, properties == null ? defaultValue : properties.getProperty(propertyName, defaultValue));
    }

    /**
     * Tries to load properties from the CLASSPATH, using the provided filename, ignoring errors
     * encountered during the process (e.g., the properties file doesn't exist, etc.).
     *
     * @param propertiesFileName the name of the file to load, including path (if any)
     * @return a (possibly empty) set of properties
     */
    private static Properties loadProperties(final String propertiesFileName) {
        Properties result = null;
        if (propertiesFileName != null) {
            InputStream in = null;
            try {
                in = Configuration.class.getResourceAsStream("/" + propertiesFileName);
                if (in != null) {
                    result = new Properties();
                    result.load(in);
                }
            } catch (IOException e) {
                // Ignore it and fall through
                logger.warn("IO Exception reading configuration properties file '" + propertiesFileName + "': " + e.getMessage(), e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore it and fall through
                        logger.warn("Unable to close configuration properties file '" + propertiesFileName + "': " + e.getMessage(), e);
                    }
                }
            }
        }
        return result;
    }

}
