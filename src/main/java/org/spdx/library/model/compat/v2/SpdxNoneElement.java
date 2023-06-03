/**
 * Copyright (c) 2020 Source Auditor Inc.
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
package org.spdx.library.model.compat.v2;

import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.storage.IModelStore;

/**
 * This SPDX element represents no SPDX element at all.
 * 
 * This element should only be used on the right hand side of relationships to represent no SPDX element
 * is related to the subject.
 * 
 * This element has no properties and a fixed ID of "NONE".
 *  
 * @author Gary O'Neall
 *
 */
public class SpdxNoneElement extends SpdxConstantElement {
	
	public static final String NONE_ELEMENT_ID = "NONE";
	
	/**
	 * Create a None element with default model store and document URI
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxNoneElement() throws InvalidSPDXAnalysisException {
		super(NONE_ELEMENT_ID);
	}
	
	/**
	 * @param modelStore where the model is stored
	 * @param documentUri Unique document URI
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxNoneElement(IModelStore modelStore, String documentUri)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, NONE_ELEMENT_ID);
	}
	
	@Override
	public String toString() {
		return SpdxConstantsCompatV2.NONE_VALUE;
	}
	
	@Override
	public Optional<String> getName() throws InvalidSPDXAnalysisException {
		return Optional.of("NONE");
	}
	
	@Override
	public Optional<String> getComment() throws InvalidSPDXAnalysisException {
		return Optional.of("This is a NONE element which represents that NO element is related");
	}

	@Override
	public String getIndividualURI() {
		return SpdxConstantsCompatV2.URI_VALUE_NONE;
	}

}
