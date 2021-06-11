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
package org.spdx.library.model.license;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.storage.listedlicense.IListedLicenseStore;
import org.spdx.storage.listedlicense.SpdxListedLicenseLocalStore;
import org.spdx.storage.listedlicense.SpdxListedLicenseWebStore;

/**
 * Singleton class which holds the listed licenses
 * 
 * @author Gary O'Neall
 *
 */
public class ListedLicenses {
	
	static final Logger logger = LoggerFactory.getLogger(ListedLicenses.class.getName());
	private static final String PROPERTIES_DIR = "resources";
	private static final String LISTED_LICENSE_PROPERTIES_FILENAME = PROPERTIES_DIR + "/" + "licenses.properties";

	Properties licenseProperties;
    boolean onlyUseLocalLicenses;
	private IListedLicenseStore licenseModelStore;
	private static ListedLicenses listedLicenses = null;
	/**
	 * Lock for any modifications to the underlying licenseModelStore
	 */
	private static final ReadWriteLock listedLicenseModificationLock = new ReentrantReadWriteLock();
	
	/**
	 * This constructor should only be called by the getListedLicenses method
	 */
	private ListedLicenses() {
		licenseProperties = loadLicenseProperties();
		onlyUseLocalLicenses = Boolean.parseBoolean(
	            System.getProperty("SPDXParser.OnlyUseLocalLicenses", licenseProperties.getProperty("OnlyUseLocalLicenses", "false")));
		initializeLicenseModelStore();
	}
	
	/**
	 * Tries to load properties from LISTED_LICENSE_PROPERTIES_FILENAME, ignoring errors
	 * encountered during the process (e.g., the properties file doesn't exist, etc.).
	 * 
	 * @return a (possibly empty) set of properties
	 */
    private static Properties loadLicenseProperties() {
        listedLicenseModificationLock.writeLock().lock();
        try {
            Properties licenseProperties = new Properties();
            InputStream in = null;
            try {
                in = ListedLicenses.class.getResourceAsStream("/" + LISTED_LICENSE_PROPERTIES_FILENAME);
                if (in != null) {
                    licenseProperties.load(in);
                }
            } catch (IOException e) {
                // Ignore it and fall through
                logger.warn("IO Exception reading listed license properties file: " + e.getMessage());
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.warn("Unable to close listed license properties file: " + e.getMessage());
                    }
                }
            }
            return licenseProperties;
        } finally {
            listedLicenseModificationLock.writeLock().unlock();
        }
    }
	
    private void initializeLicenseModelStore() {
        listedLicenseModificationLock.writeLock().lock();
        try {
        	if (!this.onlyUseLocalLicenses) {
        		try {
        			licenseModelStore = new SpdxListedLicenseWebStore();
        		} catch(InvalidSPDXAnalysisException ex) {
        			logger.warn("Unable to open SPDX listed license model store - using locally cached licenses",ex);
        			licenseModelStore = null;
        		}
        	}
        	if (licenseModelStore == null) {
        		try {
        			licenseModelStore = new SpdxListedLicenseLocalStore();
        		} catch(InvalidSPDXAnalysisException ex) {
        			logger.error("Error loading cached SPDX licenses");
        			throw new RuntimeException("Unexpected error loading SPDX Listed Licenses");
        		}
        	}
        } finally {
            listedLicenseModificationLock.writeLock().unlock();
        }
	}



	public static ListedLicenses getListedLicenses() {
	    
	    ListedLicenses retval = null;
	    listedLicenseModificationLock.readLock().lock();
	    try {
	        retval = listedLicenses;
	    } finally {
	        listedLicenseModificationLock.readLock().unlock();
	    }
	    if (Objects.isNull(retval)) {
	        listedLicenseModificationLock.writeLock().lock();
	        try {
	            if (listedLicenses == null) {
	                listedLicenses = new ListedLicenses();
	            }
	            retval = listedLicenses;
	        } finally {
	            listedLicenseModificationLock.writeLock().unlock();
	        }
	    }
        return retval;
    }
	
	/**
	 * Resets all of the cached license information and reloads the license IDs
	 * NOTE: This method should be used with caution, it will negatively impact
	 * performance.
	 * @return
	 */
    public static ListedLicenses resetListedLicenses() {
        listedLicenseModificationLock.writeLock().lock();
        try {
            listedLicenses = new ListedLicenses();
            return listedLicenses;
        } finally {
            listedLicenseModificationLock.writeLock().unlock();
        }
    }
	

	/**
	 * @param licenseId case insensitive
	 * @return true if the licenseId belongs to an SPDX listed license
	 */
    public boolean isSpdxListedLicenseId(String licenseId) {
		return this.licenseModelStore.isSpdxListedLicenseId(SpdxConstants.LISTED_LICENSE_URL, licenseId);
	}
    
    /**
     * @param exceptionId case insensitive
     * @return true if the exceptionId belongs to an SPDX listed exception
     */
    public boolean isSpdxListedExceptionId(String exceptionId) {
    	return this.licenseModelStore.isSpdxListedExceptionId(SpdxConstants.LISTED_LICENSE_URL, exceptionId);
    }
	
	/**
	 * @param licenseId SPDX Listed License ID
	 * @return SPDX listed license or null if the ID is not in the SPDX license list
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxListedLicense getListedLicenseById(String licenseId) throws InvalidSPDXAnalysisException {
		return (SpdxListedLicense)SpdxModelFactory.createModelObject(this.licenseModelStore, SpdxConstants.LISTED_LICENSE_URL, licenseId, SpdxConstants.CLASS_SPDX_LISTED_LICENSE, null);
	}
	
	public ListedLicenseException getListedExceptionById(String exceptionId) throws InvalidSPDXAnalysisException {
		return (ListedLicenseException)SpdxModelFactory.createModelObject(this.licenseModelStore, SpdxConstants.LISTED_LICENSE_URL, exceptionId, SpdxConstants.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, null);
	}
	
	/**
	 * @return List of all SPDX listed license IDs
	 */
    public List<String> getSpdxListedLicenseIds() {
        listedLicenseModificationLock.readLock().lock();
        try {
            return this.licenseModelStore.getSpdxListedLicenseIds();
        } finally {
			listedLicenseModificationLock.readLock().unlock();
        }
    }
    
	/**
	 * @return The version of the loaded license list in the form M.N, where M is the major release and N is the minor release.
	 * If no license list is loaded, returns {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#DEFAULT_LICENSE_LIST_VERSION}.
	 */
	public String getLicenseListVersion() {
		return this.licenseModelStore.getLicenseListVersion();
	}

	/**
	 * @return list of SPDX exception IDs
	 */
	public List<String> getSpdxListedExceptionIds() {
		return this.licenseModelStore.getSpdxListedExceptionIds();
	}

	/**
	 * @param licenseId case insensitive license ID
	 * @return the case sensitive license ID
	 */
	public Optional<String> listedLicenseIdCaseSensitive(String licenseId) {
		return this.licenseModelStore.listedLicenseIdCaseSensitive(licenseId);
	}

	/**
	 * @param exceptionId case insensitive exception ID
	 * @return case sensitive ID
	 */
	public Optional<String> listedExceptionIdCaseSensitive(String exceptionId) {
		return this.licenseModelStore.listedExceptionIdCaseSensitive(exceptionId);
	}  

}
