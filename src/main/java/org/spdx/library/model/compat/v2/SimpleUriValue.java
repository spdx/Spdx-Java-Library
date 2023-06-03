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
package org.spdx.library.model.compat.v2;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.model.compat.v2.enumerations.SpdxEnumFactory;
import org.spdx.library.model.compat.v2.license.ExternalExtractedLicenseInfo;
import org.spdx.library.model.compat.v2.license.SpdxNoAssertionLicense;
import org.spdx.library.model.compat.v2.license.SpdxNoneLicense;
import org.spdx.storage.IModelStore;

/**
 * Simple class to just store a URI value.  The method toModelObject will convert / inflate the value back to
 * either an Enum (if the URI matches), an ExternalSpdxElement if it matches the pattern of an external SPDX element 
 * or returns itself otherwise
 * 
 * @author Gary O'Neall
 *
 */
public class SimpleUriValue implements IndividualUriValue {
	
	static final Logger logger = LoggerFactory.getLogger(SimpleUriValue.class);
	
	private String uri;

	/**
	 * returns hash based on URI of the IndividualUriValue
	 * @param individualUri IndividualUriValue to obtain a hash from
	 * @return hash based on URI of the IndividualUriValue
	 */
	public static int getIndividualUriValueHash(IndividualUriValue individualUri) {
		return 11 ^ individualUri.getIndividualURI().hashCode();
	}
	
	/**
	 * Compares an object to an individual URI and returns true if the URI values are equal
	 * @param individualUri IndividualUriValue to compare
	 * @param comp Object to compare
	 * @return true if the individualUri has the same URI as comp and comp is of type IndividualUriValue
	 */
	public static boolean isIndividualUriValueEquals(IndividualUriValue individualUri, Object comp) {
		if (!(comp instanceof IndividualUriValue)) {
			return false;
		}
		return Objects.equals(individualUri.getIndividualURI(), ((IndividualUriValue)comp).getIndividualURI());
	}

	public SimpleUriValue(IndividualUriValue fromIndividualValue) throws InvalidSPDXAnalysisException {
		this(fromIndividualValue.getIndividualURI());
	}
	
	public SimpleUriValue(String uri) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(uri, "URI can not be null");
		this.uri = uri;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.IndividualValue#getIndividualURI()
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
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 * @return Enum, ExternalSpdxElement or itself depending on the pattern
	 * @throws InvalidSPDXAnalysisException
	 */
	public Object toModelObject(IModelStore store, String documentUri, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Object retval = SpdxEnumFactory.uriToEnum.get(uri);
		if (Objects.nonNull(retval)) {
			return retval;
		} else if (SpdxConstantsCompatV2.EXTERNAL_SPDX_ELEMENT_URI_PATTERN.matcher(uri).matches()) {
			return ExternalSpdxElement.uriToExternalSpdxElement(uri, store, documentUri, copyManager);
		} else if (SpdxConstantsCompatV2.EXTERNAL_EXTRACTED_LICENSE_URI_PATTERN.matcher(uri).matches()) {
			return ExternalExtractedLicenseInfo.uriToExternalExtractedLicense(uri, store, documentUri, copyManager);
		} else if (SpdxConstantsCompatV2.REFERENCE_TYPE_URI_PATTERN.matcher(uri).matches()) {
			return new ReferenceType(this);
		} else if (SpdxConstantsCompatV2.URI_VALUE_NONE.equals(uri)) {
			// Default value is a license, although it can also be a string or an SpdxElement
			// the caller should override the type based on the type expected
			return new SpdxNoneLicense(store, documentUri);
		} else if (SpdxConstantsCompatV2.URI_VALUE_NOASSERTION.equals(uri)) {
			return new SpdxNoAssertionLicense(store, documentUri);
		} else {
			logger.warn("URI "+uri+" does not match any model object or enumeration");
			return this;
		}
	}
	
	@Override
	public boolean equals(Object comp) {
		return isIndividualUriValueEquals(this, comp);
	}

	@Override
	public int hashCode() {
		return getIndividualUriValueHash(this);
	}
}
