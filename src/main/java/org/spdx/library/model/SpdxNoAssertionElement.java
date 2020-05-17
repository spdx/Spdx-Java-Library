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
package org.spdx.library.model;

import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;

/**
 * This SPDX element represents no assertion as to an actual SPDX element.
 * 
 * This element should only be used on the right hand side of relationships to represent no assertion
 * as to what element the subject is actually related to.
 * 
 * This element has no properties and a fixed ID of "NOASSERTION".
 *  
 * @author Gary O'Neall
 *
 */
public class SpdxNoAssertionElement extends SpdxConstantElement {

	public static final String NOASSERTION_ELEMENT_ID = "NONE";
	public static final int NOASSERTION_ELEMENT_HASHCODE = 491;
	
	/**
	 * Create a None element with default model store and document URI
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxNoAssertionElement() throws InvalidSPDXAnalysisException {
		super(NOASSERTION_ELEMENT_ID);
	}
	
	/**
	 * @param modelStore where the model is stored
	 * @param documentUri Unique document URI
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxNoAssertionElement(IModelStore modelStore, String documentUri)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, NOASSERTION_ELEMENT_ID);
	}
	
	@Override
	public String toString() {
		return SpdxConstants.NOASSERTION_VALUE;
	}
	
	@Override
	public int hashCode() {
		return NOASSERTION_ELEMENT_HASHCODE;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof SpdxNoAssertionElement;
	}
	
	@Override
	public Optional<String> getName() throws InvalidSPDXAnalysisException {
		return Optional.of("NOASSERTION");
	}
	
	@Override
	public Optional<String> getComment() throws InvalidSPDXAnalysisException {
		return Optional.of("This is a NOASSERTION element which indicate no assertion is made whether an element is related to this element");
	}

	@Override
	public String getIndividualURI() {
		return SpdxConstants.URI_VALUE_NOASSERTION;
	}
}
