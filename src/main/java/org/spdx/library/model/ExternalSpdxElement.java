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
import java.util.Optional;
import java.util.regex.Matcher;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;


/**
 * This is an SPDX element which is in an external document.  The ID must be in the form SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.pattern()
 * 
 * @author Gary O'Neall
 */
public class ExternalSpdxElement extends SpdxElement {
	
	// Note: the default empty constructor is not allowed since the element ID must follow a specific pattern

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalSpdxElement(String id) throws InvalidSPDXAnalysisException {
		super(id);
		if (!SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.matcher(id).matches()) {
			throw(new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form "+
					SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.pattern()));
		}
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalSpdxElement(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
		if (!SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.matcher(id).matches()) {
			throw(new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:SPDXID"));
		}
	}
	
	/**
	 * @return external document ID for the external reference
	 * @throws InvalidSPDXAnalysisException
	 */
	public String getExternalDocumentId() throws InvalidSPDXAnalysisException {
		Matcher matcher = SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.matcher(this.getId());
		if (!matcher.matches()) {
			throw(new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:SPDXID"));
		}
		return matcher.group(1);
	}
	
	/**
	 * @return element ID used in the external document
	 * @throws InvalidSPDXAnalysisException
	 */
	public String getExternalElementId() throws InvalidSPDXAnalysisException {
		Matcher matcher = SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.matcher(this.getId());
		if (!matcher.matches()) {
			throw(new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:SPDXID"));
		}
		return matcher.group(2);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_EXTERNAL_SPDX_ELEMENT;
	}
	
	@Override
	public List<String> verify() {
		// we don't want to call super.verify since we really don't require those fields
		List<String> retval = new ArrayList<>();
		String id = this.getId();
		if (!SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.matcher(id).matches()) {				
			retval.add("Invalid id format for an external document reference.  Must be of the form "+SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.pattern());
		}
		return retval;
	}

	public String getExternalSpdxElementURI() throws InvalidSPDXAnalysisException {
		Matcher matcher = SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.matcher(this.getId());
		if (!matcher.matches()) {
			logger.error("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:SPDXID");
			throw new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:SPDXID");
		}
		String externalDocumentUri;
		externalDocumentUri = externalDocumentIdToNamespace(matcher.group(1));
		if (externalDocumentUri.endsWith("#")) {
			return externalDocumentUri + matcher.group(2);
		} else {
			return externalDocumentUri + "#" + matcher.group(2);
		}
	}

	private String externalDocumentIdToNamespace(String externalDocumentId) throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(getModelStore(), getDocumentUri(), 
				externalDocumentId, SpdxConstants.PROP_EXTERNAL_SPDX_DOCUMENT);
		if (!retval.isPresent()) {
			throw(new InvalidSPDXAnalysisException("No external document reference exists for document ID "+externalDocumentId));
		}
		if (!(retval.get() instanceof IndividualValue)) {
			logger.error("Invalid type returned for external document.  Expected IndividualValue, actual "+retval.get().getClass().toString());
			throw new InvalidSPDXAnalysisException("Invalid type returned for external document.");
		}
		return ((IndividualValue)retval.get()).getIndividualURI();
	}
	
	@Override
	public boolean equivalent(ModelObject compare) {
		if (!(compare instanceof ExternalSpdxElement)) {
			return false;
		}
		return getId().equals(compare.getId());
	}
}
