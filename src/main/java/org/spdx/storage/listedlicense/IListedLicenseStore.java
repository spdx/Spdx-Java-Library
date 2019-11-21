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

import java.util.List;

import org.spdx.storage.IModelStore;

/**
 * @author Gary O'Neall
 * 
 * Extends the model store to include interfaces specific to listed licenses
 *
 */
public interface IListedLicenseStore extends IModelStore {

	/**
	 * @return List of all SPDX listed license IDs
	 */
	List<String> getSpdxListedLicenseIds();

	/**
	 * @return The version of the loaded license list in the form M.N, where M is the major release and N is the minor release.
	 */
	String getLicenseListVersion();

	/**
	 * @param listedLicenseDocumentUri
	 * @param licenseId
	 * @return true if the licenseId belongs to an SPDX listed license
	 */
	boolean isSpdxListedLicenseId(String listedLicenseDocumentUri, String licenseId);

	/**
	 * @param listedLicenseDocumentUri
	 * @param exceptionId
	 * @return true if the exceptionId belongs to an SPDX listed exception
	 */
	boolean isSpdxListedExceptionId(String listedLicenseDocumentUri, String exceptionId);

	/**
	 * @return list of SPDX exception IDs
	 */
	List<String> getSpdxListedExceptionIds();

}
