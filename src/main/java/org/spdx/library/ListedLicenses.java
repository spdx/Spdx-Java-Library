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
package org.spdx.library;

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
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.SpdxIdNotFoundException;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.SpdxModelFactoryCompatV2;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v3_0_1.core.CreationInfo;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicenseException;
import org.spdx.storage.IModelStore;
import org.spdx.storage.listedlicense.IListedLicenseStore;
import org.spdx.storage.listedlicense.SpdxListedLicenseLocalStore;
import org.spdx.storage.listedlicense.SpdxListedLicenseModelStore;
import org.spdx.storage.listedlicense.SpdxListedLicenseWebStore;
import org.spdx.storage.listedlicense.SpdxV2ListedLicenseModelStore;
import org.spdx.storage.listedlicense.SpdxV3ListedLicenseModelStore;

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
	private IListedLicenseStore baseModelStore;
	private SpdxV2ListedLicenseModelStore licenseStoreV2;
	private SpdxV3ListedLicenseModelStore licenseStoreV3;
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
        			baseModelStore = new SpdxListedLicenseWebStore();
        		} catch(InvalidSPDXAnalysisException ex) {
        			logger.error("Unable to access the most current listed licenses from https://spdx.org/licenses - using locally cached licenses: "+ex.getMessage());
        			baseModelStore = null;
        		}
        	}
        	if (baseModelStore == null) {
        		try {
        			baseModelStore = new SpdxListedLicenseLocalStore();
        		} catch(InvalidSPDXAnalysisException ex) {
        			logger.error("Error loading cached SPDX licenses");
        			throw new RuntimeException("Unexpected error loading SPDX Listed Licenses");
        		}
        	}
        	licenseStoreV2 = new SpdxV2ListedLicenseModelStore(baseModelStore);
        	licenseStoreV3 = new SpdxV3ListedLicenseModelStore(baseModelStore);
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
		return baseModelStore.isSpdxListedLicenseId(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, licenseId);
	}
    
    /**
     * @param exceptionId case insensitive
     * @return true if the exceptionId belongs to an SPDX listed exception
     */
    public boolean isSpdxListedExceptionId(String exceptionId) {
    	return this.baseModelStore.isSpdxListedExceptionId(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, exceptionId);
    }
	
	/**
	 * @param licenseId SPDX Listed License ID
	 * @return an SPDX spec version 2 SPDX listed license or null if the ID is not in the SPDX license list
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxListedLicense getListedLicenseByIdCompatV2(String licenseId) throws InvalidSPDXAnalysisException {
		try {
			return (SpdxListedLicense)SpdxModelFactoryCompatV2.getModelObjectV2(this.licenseStoreV2, 
					SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, licenseId, 
					SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, null, false);
		} catch (SpdxIdNotFoundException ex) {
			return null;
		}
	}
	
	/**
	 * @param exceptionId SPDX Listed License Exception ID
	 * @return an SPDX spec version 2 SPDX listed license exception or null if the ID is not in the SPDX license list
	 * @throws InvalidSPDXAnalysisException
	 */
	public org.spdx.library.model.v2.license.ListedLicenseException getListedExceptionByIdCompatV2(String exceptionId) throws InvalidSPDXAnalysisException {
		try {
			return (org.spdx.library.model.v2.license.ListedLicenseException)SpdxModelFactoryCompatV2.getModelObjectV2(
					this.licenseStoreV2, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, 
					exceptionId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, null, false);
		} catch (SpdxIdNotFoundException ex) {
			return null;
		}
	}
	
	/**
	 * @param licenseId SPDX Listed License ID
	 * @return SPDX listed license or null if the ID is not in the SPDX license list
	 * @throws InvalidSPDXAnalysisException
	 */
	public ListedLicense getListedLicenseById(String licenseId) throws InvalidSPDXAnalysisException {
		try {
			return new ListedLicense(this.licenseStoreV3, SpdxListedLicenseModelStore.licenseOrExceptionIdToObjectUri(licenseId), null, 
					false, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX);
		} catch (SpdxIdNotFoundException ex) {
			return null;
		}
		
	}
	
	public ListedLicenseException getListedExceptionById(String exceptionId) throws InvalidSPDXAnalysisException {
		try {
			return new ListedLicenseException(this.licenseStoreV3, SpdxListedLicenseModelStore.licenseOrExceptionIdToObjectUri(exceptionId), null, 
					false, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX);
		} catch (SpdxIdNotFoundException ex) {
			return null;
		}
		
	}
	
	/**
	 * @return List of all SPDX listed license IDs
	 */
    public List<String> getSpdxListedLicenseIds() {
        listedLicenseModificationLock.readLock().lock();
        try {
            return this.baseModelStore.getSpdxListedLicenseIds();
        } finally {
			listedLicenseModificationLock.readLock().unlock();
        }
    }
    
	/**
	 * @return The version of the loaded license list in the form M.N, where M is the major release and N is the minor release.
	 * If no license list is loaded, returns {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#DEFAULT_LICENSE_LIST_VERSION}.
	 */
	public String getLicenseListVersion() {
		return this.baseModelStore.getLicenseListVersion();
	}

	/**
	 * @return list of SPDX exception IDs
	 */
	public List<String> getSpdxListedExceptionIds() {
		return this.baseModelStore.getSpdxListedExceptionIds();
	}

	/**
	 * @param licenseId case insensitive license ID
	 * @return the case sensitive license ID
	 */
	public Optional<String> listedLicenseIdCaseSensitive(String licenseId) {
		return this.baseModelStore.listedLicenseIdCaseSensitive(licenseId);
	}

	/**
	 * @param exceptionId case insensitive exception ID
	 * @return case sensitive ID
	 */
	public Optional<String> listedExceptionIdCaseSensitive(String exceptionId) {
		return this.baseModelStore.listedExceptionIdCaseSensitive(exceptionId);
	}
	
	/**
	 * @return model store for listed licenses using the version 3 SPDX model
	 */
	public IModelStore getLicenseModelStore() {
		return this.licenseStoreV3;
	}
	
	public IModelStore getLicenseModelStoreCompatV2() {
		return this.licenseStoreV2;
	}

	/**
	 * @return the CreationInfo used for all SPDX listed licenses and listed exceptions
	 * @throws InvalidSPDXAnalysisException on error inflating the creation info
	 */
	public CreationInfo getListedLicenseCreationInfo() throws InvalidSPDXAnalysisException {
		return licenseStoreV3.getListedLicenseCreationInfo();
	}

}