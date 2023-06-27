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
package org.spdx.library.model.licensing;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.core.Element;
import org.spdx.storage.IModelStore;

/**
 * 
 * Abstract class representing a license combination consisting of one or more
 * licenses (optionally including additional text), which may be combined
 * according to the SPDX license expression syntax.
 * 
 * An AnyLicenseInfo is used by licensing properties of software artifacts.
 * It can be a NoneLicense, a NoAssertionLicense,
 * single license (either on the SPDX License List or a custom-defined license);
 * a single license with an "or later" operator applied; the foregoing with
 * additional text applied; or a set of licenses combined by applying "AND" and
 * "OR" operators recursively.
 * 
 * @author Gary O'Neall
 *
 */
public abstract class AnyLicenseInfo extends Element {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public AnyLicenseInfo() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param modelStore Storage for the model objects - Must support model V3 classes
	 * @param objectUri Anonymous ID or URI for the model object
	 * @param copyManager - if supplied, model objects will be implictly copied into this model store and document URI when referenced by setting methods
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public AnyLicenseInfo(IModelStore modelStore, String objectUri,
			ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, objectUri, copyManager, create);
	}

	/**
	 * Open or create an Element object with the default store
	 * @param objectUri Anonymous ID or URI for the model object
	 * @throws InvalidSPDXAnalysisException
	 */
	public AnyLicenseInfo(String objectUri)
			throws InvalidSPDXAnalysisException {
		super(objectUri);
	}
	
	//TODO: Implement properties

}
