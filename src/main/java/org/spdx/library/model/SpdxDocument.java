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
package org.spdx.library.model;

import java.util.ArrayList;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;

/**
 * An SpdxDocument is a summary of the contents, provenance, ownership and licensing 
 * analysis of a specific software package. 
 * This is, effectively, the top level of SPDX information.
 * 
 * @author Gary O'Neall
 */
public class SpdxDocument extends ModelObject {
	
	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for the document associated with this model
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxDocument(IModelStore modelStore, String documentUri, boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, SpdxConstants.SPDX_DOCUMENT_ID, create);
	}
	
	/**
	 * Obtains or creates an SPDX document using the default document store
	 * @param documentUri
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxDocument(String documentUri) throws InvalidSPDXAnalysisException {
		super(DefaultModelStore.getDefaultModelStore(), documentUri, SpdxConstants.SPDX_DOCUMENT_ID, true);
	}


	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_DOCUMENT;
	}

	@Override
	public List<String> verify() {
		// TODO Implement
		return new ArrayList<>();
	}

}
