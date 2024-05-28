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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.Configuration;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.storage.IModelStore;
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
	
	static final Pattern PATCH_VERSION_PATTERN = Pattern.compile(".+\\..+\\..+");

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
		// Note: this code is confusing as this property changed names several times over time, and we want to preserve
		// backwards compatibility for those downstream library users who are using the old/deprecated names
		onlyUseLocalLicenses = Boolean.parseBoolean(Configuration.getInstance().getProperty("org.spdx.useJARLicenseInfoOnly",
				                                      Configuration.getInstance().getProperty("SPDXParser.OnlyUseLocalLicenses",
				                                        Configuration.getInstance().getProperty("OnlyUseLocalLicenses", "false"))));
		initializeLicenseModelStore();
	}

	/**
	 * This constructor should only be called by the initializeListedLicenses method,
	 * to programmatically configure licenseModelStore from the application consuming this library
	 */
	private ListedLicenses(IListedLicenseStore licenseModelStore) {
		this.licenseModelStore = licenseModelStore;
	}

    private void initializeLicenseModelStore() {
        listedLicenseModificationLock.writeLock().lock();
        try {
        	if (!this.onlyUseLocalLicenses) {
        		try {
        			licenseModelStore = new SpdxListedLicenseWebStore();
        		} catch(InvalidSPDXAnalysisException ex) {
        			logger.error("Unable to access the most current listed licenses from https://spdx.org/licenses - using locally cached licenses: "+ex.getMessage(), ex);
        			licenseModelStore = null;
        		}
        	}
        	if (licenseModelStore == null) {
        		try {
        			licenseModelStore = new SpdxListedLicenseLocalStore();
        		} catch(InvalidSPDXAnalysisException ex) {
        			logger.error("Error loading cached SPDX licenses", ex);
        			throw new RuntimeException("Unexpected error loading SPDX Listed Licenses", ex);
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
	 * Initializes the listed licenses singleton from a provided cache. This will
	 * ignore all configuration around fetching remote licenses.
	 *
	 * @param licenseStore a preconfigured licenseStore, see {@link SpdxListedLicenseLocalStore} for
	 *                     an example.
	 * @return a singleton instance
	 */
	public static ListedLicenses initializeListedLicenses(IListedLicenseStore licenseStore) {
			listedLicenseModificationLock.writeLock().lock();
			try {
				listedLicenses = new ListedLicenses(licenseStore);
				return listedLicenses;
			} finally {
				listedLicenseModificationLock.writeLock().unlock();
			}
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
		String retval = this.licenseModelStore.getLicenseListVersion();
		if (PATCH_VERSION_PATTERN.matcher(retval).matches()) {
			retval = retval.substring(0, retval.lastIndexOf('.'));
		}
		return retval;
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
	
	/**
	 * @return model store for listed licenses
	 */
	public IModelStore getLicenseModelStore() {
		return this.licenseModelStore;
	}

}
