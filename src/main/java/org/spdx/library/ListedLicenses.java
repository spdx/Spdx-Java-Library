/**
 * SPDX-FileCopyrightText: Copyright (c) 2019 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.library;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.Configuration;
import org.spdx.core.InvalidSPDXAnalysisException;
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
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ListedLicenses {
	
	static final Logger logger = LoggerFactory.getLogger(ListedLicenses.class.getName());

	Properties licenseProperties;
    boolean onlyUseLocalLicenses;
	private IListedLicenseStore baseModelStore;
	private SpdxV2ListedLicenseModelStore licenseStoreV2;
	private SpdxV3ListedLicenseModelStore licenseStoreV3;
	private static ListedLicenses listedLicenses = null;
	private Map<String, SpdxListedLicense> spdxListedLicenseMapCompatV2;
	private Map<String, ListedLicense> spdxListedLicenseMap;
	private Map<String, org.spdx.library.model.v2.license.ListedLicenseException> spdxListedExceptionMapCompatV2;
	private Map<String, ListedLicenseException> spdxListedExceptionMap;

	/**
	 * Lock for any modifications to the underlying licenseModelStore
	 */
	private static final ReadWriteLock listedLicenseModificationLock = new ReentrantReadWriteLock();
	
	/**
	 * This constructor should only be called by the getListedLicenses method
	 */
	private ListedLicenses() {
		onlyUseLocalLicenses = Boolean.parseBoolean(Configuration.getInstance().getProperty("org.spdx.useJARLicenseInfoOnly",
				 "false"));
		initializeLicenseModelStore();
	}

	/**
	 * Initializes the license model store for managing SPDX listed licenses and
	 * exceptions
	 * <p>
	 * This method sets up the appropriate model store based on the configuration.
	 * </p>
	 * <p>
	 * This method also initializes the SPDX version 2 and version 3 model stores
	 * for compatibility.
	 * </p>
	 */
    private void initializeLicenseModelStore() {
        listedLicenseModificationLock.writeLock().lock();
        try {
        	if (!this.onlyUseLocalLicenses) {
        		try {
        			baseModelStore = new SpdxListedLicenseWebStore();
        		} catch(InvalidSPDXAnalysisException ex) {
                    logger.warn("Unable to access the most current listed licenses from https://spdx.org/licenses - using locally cached licenses: {} Note: you can set the org.spdx.useJARLicenseInfoOnly property to true to avoid this warning.", ex.getMessage(), ex);
        			baseModelStore = null;
        		}
        	}
        	if (baseModelStore == null) {
        		try {
        			baseModelStore = new SpdxListedLicenseLocalStore();
        		} catch(InvalidSPDXAnalysisException ex) {
        			logger.error("Error loading cached SPDX licenses", ex);
        			throw new RuntimeException("Unexpected error loading SPDX Listed Licenses", ex);
        		}
        	}
        	licenseStoreV2 = new SpdxV2ListedLicenseModelStore(baseModelStore);
        	licenseStoreV3 = new SpdxV3ListedLicenseModelStore(baseModelStore);
        } finally {
            listedLicenseModificationLock.writeLock().unlock();
        }
	}

	/**
	 * Retrieve the singleton instance of the {@code ListedLicenses} class
	 * <p>
	 * This method ensures that only one instance of the {@code ListedLicenses}
	 * class is created and shared
	 * throughout the application. If the instance does not already exist, it
	 * is initialized.
	 * </p>
	 *
	 * @return The singleton instance of the {@code ListedLicenses} class.
	 */
	public static ListedLicenses getListedLicenses() {
	    ListedLicenses retval;
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
	 * Reset all cached license information and reload the license IDs
	 * <p>
	 * NOTE: This method should be used with caution as it can negatively
	 * impact performance due to the reloading process.
	 * </p>
	 *
	 * @return A new instance of the {@code ListedLicenses} class.
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
	 * Check whether the given license ID belongs to an SPDX listed license
	 *
	 * @param licenseId The case-insensitive SPDX license ID.
	 * @return {@code true} if the license ID belongs to an SPDX listed license,
	 *         {@code false} otherwise.
	 */
    public boolean isSpdxListedLicenseId(String licenseId) {
		return baseModelStore.isSpdxListedLicenseId(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, licenseId);
	}

	/**
	 * Check whether the given exception ID belongs to an SPDX listed exception
	 *
	 * @param exceptionId The case-insensitive SPDX exception ID to check.
	 * @return {@code true} if the exception ID belongs to an SPDX listed exception,
	 *         {@code false} otherwise.
	 */
    public boolean isSpdxListedExceptionId(String exceptionId) {
    	return this.baseModelStore.isSpdxListedExceptionId(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, exceptionId);
    }

	/**
	 * Retrieve an SPDX listed license by its ID
	 * for SPDX specification version 2 compatibility
	 *
	 * @param licenseId The SPDX Listed License ID.
	 * @return The SPDX specification version 2 listed license, or {@code null} if
	 *         the ID is not in the SPDX License List.
	 * @throws InvalidSPDXAnalysisException If an error occurs while retrieving the
	 *                                      license.
	 */
	public SpdxListedLicense getListedLicenseByIdCompatV2(String licenseId) throws InvalidSPDXAnalysisException {
		return getSpdxListedLicensesCompatV2().get(licenseId);
	}

	/**
	 * Retrieve an SPDX listed license exception by its ID
	 * for SPDX specification version 2 compatibility
	 *
	 * @param exceptionId The SPDX Listed License Exception ID.
	 * @return The SPDX specification version 2 listed license exception, or
	 *         {@code null} if the ID is not in the SPDX License List.
	 * @throws InvalidSPDXAnalysisException If an error occurs while retrieving the
	 *                                      exception.
	 */
	public org.spdx.library.model.v2.license.ListedLicenseException getListedExceptionByIdCompatV2(String exceptionId)
			throws InvalidSPDXAnalysisException {
		return getSpdxListedLicenseExceptionsCompatV2().get(exceptionId);
	}

	/**
	 * Retrieve an SPDX listed license by its ID
	 *
	 * @param licenseId The SPDX Listed License ID.
	 * @return The SPDX listed license, or {@code null} if not in the SPDX License List.
	 * @throws InvalidSPDXAnalysisException If an error occurs while retrieving the license.
	 */
	public ListedLicense getListedLicenseById(String licenseId) throws InvalidSPDXAnalysisException {
		return getSpdxListedLicenses().get(licenseId);
	}

	/**
	 * Retrieve an SPDX listed license exception by its ID
	 *
	 * @param exceptionId The SPDX Listed License Exception ID.
	 * @return The SPDX listed license exception, or {@code null} if not in the SPDX
	 *         License List.
	 * @throws InvalidSPDXAnalysisException If an error occurs while retrieving the
	 *                                      exception.
	 */
	public ListedLicenseException getListedExceptionById(String exceptionId) throws InvalidSPDXAnalysisException {
		return getSpdxListedLicenseExceptions().get(exceptionId);
	}

	/**
	 * Retrieve a list of all SPDX listed license IDs
	 *
	 * @return A list of all SPDX Listed License IDs.
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
	 * Retrieve a map of SPDX Listed License IDs to their SPDX listed licenses
	 *
	 * @return A map where the keys are SPDX Listed License IDs and the values are
	 *         {@link ListedLicense} objects.
	 * @throws InvalidSPDXAnalysisException If an error occurs while fetching the
	 *                                      licenses.
	 */
	public Map<String, ListedLicense> getSpdxListedLicenses() throws InvalidSPDXAnalysisException {
		listedLicenseModificationLock.readLock().lock();
		try {
			if (Objects.nonNull(this.spdxListedLicenseMap)) {
				return this.spdxListedLicenseMap;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		listedLicenseModificationLock.writeLock().lock();
		try {
			if (Objects.nonNull(this.spdxListedLicenseMap)) {
				return this.spdxListedLicenseMap;
			}
			Map<String, ListedLicense> allListedLicenses = new HashMap<>();
			for (String licenseId : this.baseModelStore.getSpdxListedLicenseIds()) {
				allListedLicenses.put(licenseId, new ListedLicense(this.licenseStoreV3, SpdxListedLicenseModelStore.licenseOrExceptionIdToObjectUri(licenseId), null,
						false, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX));
			}
			this.spdxListedLicenseMap = Collections.unmodifiableMap(allListedLicenses);
			return this.spdxListedLicenseMap;
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}

	/**
	 * Retrieve a map of SPDX Listed License Exception IDs to their SPDX listed
	 * license exceptions
	 *
	 * @return A map where the keys are SPDX Listed License Exception IDs
	 *         and the values are {@link ListedLicenseException} objects.
	 * @throws InvalidSPDXAnalysisException If an error occurs while fetching the
	 *                                      license exceptions.
	 */
	public Map<String, ListedLicenseException> getSpdxListedLicenseExceptions() throws InvalidSPDXAnalysisException {
		listedLicenseModificationLock.readLock().lock();
		try {
			if (Objects.nonNull(this.spdxListedExceptionMap)) {
				return this.spdxListedExceptionMap;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		listedLicenseModificationLock.writeLock().lock();
		try {
			if (Objects.nonNull(this.spdxListedExceptionMap)) {
				return this.spdxListedExceptionMap;
			}
			Map<String, ListedLicenseException> allListedExceptions = new HashMap<>();
			for (String exceptionId : this.baseModelStore.getSpdxListedExceptionIds()) {
				allListedExceptions.put(exceptionId, new ListedLicenseException(this.licenseStoreV3, SpdxListedLicenseModelStore.licenseOrExceptionIdToObjectUri(exceptionId), null,
						false, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX));
			}
			this.spdxListedExceptionMap = Collections.unmodifiableMap(allListedExceptions);
			return this.spdxListedExceptionMap;
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}

	/**
	 * Retrieve a map of SPDX Listed License IDs to their SPDX listed licenses
	 * for SPDX specification version 2 compatibility
	 *
	 * @return A map where the keys are SPDX Listed License IDs and the values are
	 *         {@link SpdxListedLicense} (SPDX spec version 2) objects.
	 * @throws InvalidSPDXAnalysisException If an error occurs while fetching the
	 *                                      licenses.
	 */
	protected Map<String, SpdxListedLicense> getSpdxListedLicensesCompatV2() throws InvalidSPDXAnalysisException {
		listedLicenseModificationLock.readLock().lock();
		try {
			if (Objects.nonNull(this.spdxListedLicenseMapCompatV2)) {
				return this.spdxListedLicenseMapCompatV2;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		listedLicenseModificationLock.writeLock().lock();
		try {
			if (Objects.nonNull(this.spdxListedLicenseMapCompatV2)) {
				return this.spdxListedLicenseMapCompatV2;
			}
			Map<String, SpdxListedLicense> allListedLicenses = new HashMap<>();
			for (String licenseId : this.baseModelStore.getSpdxListedLicenseIds()) {
				allListedLicenses.put(licenseId, (SpdxListedLicense)SpdxModelFactoryCompatV2.getModelObjectV2(this.licenseStoreV2,
						SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, licenseId,
						SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, null, false));
			}
			this.spdxListedLicenseMapCompatV2 = Collections.unmodifiableMap(allListedLicenses);
			return this.spdxListedLicenseMapCompatV2;
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}

	/**
	 * Retrieve a map of SPDX Listed License Exception IDs to their SPDX listed
	 * license exceptions
	 * for SPDX specification version 2 compatibility
	 *
	 * @return A map where the keys are SPDX Listed License Exception IDs
	 *         and the values are
	 *         {@link org.spdx.library.model.v2.license.ListedLicenseException}
	 *         (SPDX spec version 2) objects.
	 * @throws InvalidSPDXAnalysisException If an error occurs while fetching the
	 *                                      license exceptions.
	 */
	protected Map<String, org.spdx.library.model.v2.license.ListedLicenseException> getSpdxListedLicenseExceptionsCompatV2() throws InvalidSPDXAnalysisException {
		listedLicenseModificationLock.readLock().lock();
		try {
			if (Objects.nonNull(this.spdxListedExceptionMapCompatV2)) {
				return this.spdxListedExceptionMapCompatV2;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		listedLicenseModificationLock.writeLock().lock();
		try {
			if (Objects.nonNull(this.spdxListedExceptionMapCompatV2)) {
				return this.spdxListedExceptionMapCompatV2;
			}
			Map<String, org.spdx.library.model.v2.license.ListedLicenseException> allListedExceptions = new HashMap<>();
			for (String exceptionId : this.baseModelStore.getSpdxListedExceptionIds()) {
				allListedExceptions.put(exceptionId, (org.spdx.library.model.v2.license.ListedLicenseException)SpdxModelFactoryCompatV2.getModelObjectV2(
						this.licenseStoreV2, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX,
						exceptionId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, null, false));
			}
			this.spdxListedExceptionMapCompatV2 = Collections.unmodifiableMap(allListedExceptions);
			return this.spdxListedExceptionMapCompatV2;
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}

	/**
	 * Retrieve the version of the loaded SPDX license list
	 * <p>
	 * The version is returned in the format "M.N", where M is the major release and N is the minor release.
	 * If no license list is loaded, this method returns the default license list version.
	 * </p>
	 *
	 * @return The version of the loaded SPDX license list, or the default version if no license list is loaded.
	 */
	public String getLicenseListVersion() {
		return this.baseModelStore.getLicenseListVersion();
	}

	/**
	 * Retrieve a list of all SPDX listed exception IDs
	 *
	 * @return A list of SPDX listed exception IDs.
	 */
	public List<String> getSpdxListedExceptionIds() {
		return this.baseModelStore.getSpdxListedExceptionIds();
	}

	/**
	 * Retrieve the case-sensitive SPDX license ID for a given case-insensitive
	 * license ID
	 *
	 * @param licenseId The case-insensitive SPDX license ID to look up.
	 * @return An {@link Optional} containing the case-sensitive license ID if
	 *         found, or an empty {@link Optional} if not found.
	 */
	public Optional<String> listedLicenseIdCaseSensitive(String licenseId) {
		return this.baseModelStore.listedLicenseIdCaseSensitive(licenseId);
	}

	/**
	 * Retrieve the case-sensitive SPDX exception ID for a given case-insensitive
	 * exception ID
	 *
	 * @param exceptionId The case-insensitive SPDX exception ID to look up.
	 * @return An {@link Optional} containing the case-sensitive exception ID if
	 *         found, or an empty {@link Optional} if not found.
	 */
	public Optional<String> listedExceptionIdCaseSensitive(String exceptionId) {
		return this.baseModelStore.listedExceptionIdCaseSensitive(exceptionId);
	}

	/**
	 * Retrieve the model store for listed licenses using the SPDX version 3 model
	 *
	 * @return The {@link IModelStore} for SPDX version 3 listed licenses and
	 *         exceptions.
	 */
	public IModelStore getLicenseModelStore() {
		return this.licenseStoreV3;
	}

	/**
	 * Retrieve the model store for listed licenses using the SPDX version 2 model
	 *
	 * @return The {@link IModelStore} for SPDX version 2 listed licenses and
	 *         exceptions.
	 */
	public IModelStore getLicenseModelStoreCompatV2() {
		return this.licenseStoreV2;
	}

	/**
	 * Retrieve the creation information for all SPDX listed licenses and exceptions
	 *
	 * @return The {@link CreationInfo} used for all SPDX listed licenses and
	 *         exceptions.
	 * @throws InvalidSPDXAnalysisException If an error occurs while inflating the
	 *                                      creation information.
	 */
	public CreationInfo getListedLicenseCreationInfo() throws InvalidSPDXAnalysisException {
		return licenseStoreV3.getListedLicenseCreationInfo();
	}

}
