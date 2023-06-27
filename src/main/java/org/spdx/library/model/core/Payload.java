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
package org.spdx.library.model.core;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.ModelObject;
import org.spdx.storage.IModelStore;

/**
 * TODO: Add description from model markdown once complete
 * 
 * @author Gary O'Neall
 *
 */
public abstract class Payload extends ModelObject {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public Payload() throws InvalidSPDXAnalysisException {
		super();
	}
	
	/**
	 * Creates a new Payload object
	 * @param modelStore Storage for the model objects - Must support model V3 classes
	 * @param objectUri Anonymous ID or URI for the model object
	 * @param copyManager - if supplied, model objects will be implictly copied into this model store and document URI when referenced by setting methods
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public Payload(IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager, 
			boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, objectUri, copyManager, create);
	}
	
	/**
	 * Open or create a Payload object with the default store
	 * @param objectUri Anonymous ID or URI for the model object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Payload(String objectUri) throws InvalidSPDXAnalysisException {
		super(objectUri);
	}
	
	//TODO: Add in creationInfo and nameSpace maps

}
