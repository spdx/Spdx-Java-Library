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

import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;

/**
 * Simple class to just store a URI value.  The method toModelObject will convert / inflate the value back to
 * either an Enum (if the URI matches), an ExternalSpdxElement if it matches the pattern of an external SPDX element 
 * or returns itself otherwise
 * 
 * @author Gary O'Neall
 *
 */
public class SimpleUriValue implements IndividualValue {
	
	private String uri;


	public SimpleUriValue(IndividualValue fromIndividualValue) throws InvalidSPDXAnalysisException {
		this(fromIndividualValue.getIndividualURI());
	}
	
	public SimpleUriValue(String uri) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(uri);
		this.uri = uri;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.IndividualValue#getIndividualURI()
	 */
	@Override
	public String getIndividualURI() {
		return uri;
	}
	
	/**
	 * inflate the value back to either an Enum (if the URI matches), an ExternalSpdxElement if it matches the pattern of an external SPDX element 
	 * or returns itself otherwise
	 * @param store
	 * @param documentUri
	 * @return Enum, ExternalSpdxElement or itself depending on the pattern
	 * @throws InvalidSPDXAnalysisException
	 */
	public Object toModelObject(IModelStore store, String documentUri) throws InvalidSPDXAnalysisException {
		Object retval = SpdxModelFactory.uriToEnum.get(uri);
		if (Objects.nonNull(retval)) {
			return retval;
		} else if (SpdxConstants.EXTERNAL_SPDX_ELEMENT_URI_PATTERN.matcher(uri).matches()) {
			return ExternalSpdxElement.uriToExternalSpdxElement(uri, store, documentUri, true);
		} else {
			return this;
		}
	}
	
	@Override
	public boolean equals(Object comp) {
		if (!(comp instanceof SimpleUriValue)) {
			return false;
		}
		return Objects.equals(this.uri, ((SimpleUriValue)comp).getIndividualURI());
	}

	@Override
	public int hashCode() {
		return 11 ^ this.uri.hashCode();
	}
}
