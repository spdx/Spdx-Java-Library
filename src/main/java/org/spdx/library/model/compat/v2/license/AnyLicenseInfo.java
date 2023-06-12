/**
 * Copyright (c) 2011 Source Auditor Inc.
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
 *
*/
package org.spdx.library.model.compat.v2.license;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.compat.v2.ModelObject;
import org.spdx.storage.IModelStore;

/**
 * This abstract class represents several ways of describing licensing information.
 * License info can be described as a set of conjunctive licenses (where all licenses
 * terms must apply), a set of disjunctive licenses (where there is a choice of one
 * license among the set described) or a specific license.  The specific licenses
 * are of a SimpleLicensingInfoType
 * @author Gary O'Neall
 *
 */
public abstract class AnyLicenseInfo extends ModelObject {
	
	static final Logger logger = LoggerFactory.getLogger(AnyLicenseInfo.class.getName());
	
	/**
	 * Create a new Model Object using an Anonymous ID with the defualt store and default document URI
	 * @throws InvalidSPDXAnalysisException 
	 */
	public AnyLicenseInfo() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * Open or create a model object with the default store and default document URI
	 * @param objectUri ID for this object - must be unique within the SPDX document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public AnyLicenseInfo(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * Create a new LicenseInfo object
	 * @param modelStore container which includes the license
	 * @param documentUri URI for the SPDX document containing the license
	 * @param objectUri identifier for the license
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	AnyLicenseInfo(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}
	
	// force subclasses to implement toString
	@Override
    public abstract String toString();
}
