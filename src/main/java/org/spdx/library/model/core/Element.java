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

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.storage.IModelStore;

/**
 * 
 * Base domain class from which all other SPDX-3.0 domain classes derive.
 * 
 * An Element is a representation of a fundamental concept either directly inherent
 * to the Bill of Materials (BOM) domain or indirectly related to the BOM domain
 * and necessary for contextually characterizing BOM concepts and relationships.
 * Within SPDX-3.0 structure this is the base class acting as a consistent,
 * unifying, and interoperable foundation for all explicit
 * and inter-relatable content objects.
 * 
 * @author Gary O'Neall
 *
 */
public abstract class Element extends Payload {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public Element() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * Creates a new Element object
	 * @param modelStore Storage for the model objects - Must support model V3 classes
	 * @param objectUri Anonymous ID or URI for the model object
	 * @param copyManager - if supplied, model objects will be implictly copied into this model store and document URI when referenced by setting methods
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public Element(IModelStore modelStore, String objectUri,
			ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, objectUri, copyManager, create);
	}

	/**
	 * Open or create an Element object with the default store
	 * @param objectUri Anonymous ID or URI for the model object
	 * @throws InvalidSPDXAnalysisException
	 */
	public Element(String objectUri) throws InvalidSPDXAnalysisException {
		super(objectUri);
	}
	
	//TODO: Implement properties

}
