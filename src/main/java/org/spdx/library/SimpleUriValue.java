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
package org.spdx.library;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.ExternalElement;
import org.spdx.library.model.compat.v2.enumerations.SpdxEnumFactoryCompatV2;
import org.spdx.library.model.v3.core.ExternalMap;
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
	 * inflate the value back to either an Enum (if the URI matches),  an ExternalSpdxElement if the uri is found in the
	 * externalMap or if it matches the pattern of a V2 compatible external SPDX element, an Individual object, or returns itself otherwise
	 * @param store store to use for the inflated object
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 * @param defaultNamespace optional document namespace when creating V2 compatible external document references
	 * @param externalMap map of URI's to ExternalMaps for any external elements
	 * @return Enum, ExternalSpdxElement or itself depending on the pattern
	 * @throws InvalidSPDXAnalysisException on any store or parsing error
	 */
	public Object toModelObject(IModelStore store, ModelCopyManager copyManager, @Nullable String defaultNamespace,
			@Nullable Map<String, ExternalMap> externalMap) throws InvalidSPDXAnalysisException {
		if (store.getSpdxVersion().compareTo(SpdxMajorVersion.VERSION_3) < 0) {
			if (Objects.isNull(defaultNamespace)) {
				logger.error("Default namespace can not be null for SPDX 2 model stores");
				throw new InvalidSPDXAnalysisException("Default namespace can not be null for SPDX 2 model stores");
			}
			return toModelObjectV2Compat(store, defaultNamespace, copyManager);
		} else {
			return toModelObject(store, copyManager, Objects.isNull(externalMap) ? new HashMap<>() : externalMap);
		}
	}
	
	/**
	 * inflate the value back to either an Enum (if the URI matches),  an ExternalSpdxElement if the uri is found in the
	 * externalMap, an Individual object, or returns itself otherwise
	 * @param store store to use for the inflated object
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 * @param externalMap map of URI's to ExternalMaps for any external elements
	 * @return Enum, ExternalSpdxElement, individual or itself depending on the pattern
	 * @throws InvalidSPDXAnalysisException on any store or parsing error
	 */
	private Object toModelObject(IModelStore store,  ModelCopyManager copyManager, Map<String, ExternalMap> externalMap) throws InvalidSPDXAnalysisException {
		Object retval = SpdxEnumFactory.uriToEnum.get(uri);
		if (Objects.nonNull(retval)) {
			return retval;
		} else if (externalMap.containsKey(uri)) {
			return new ExternalElement(store, uri, copyManager, externalMap.get(uri));
		} else if (SpdxIndividualFactory.uriToIndividual.containsKey(uri)) {
			return SpdxIndividualFactory.uriToIndividual.get(uri);
		} else {
			logger.warn("URI "+uri+" does not match any model object or enumeration");
			return this;
		}
	}
	
	/**
	 * inflate the value back to either an Enum (if the URI matches), an ExternalSpdxElement if it matches the pattern of an external SPDX element 
	 * or returns itself otherwise
	 * @param store store to store the inflated object
	 * @param documentUri document URI to use if creating an external document reference
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 * @return Enum, ExternalSpdxElement or itself depending on the pattern
	 * @throws InvalidSPDXAnalysisException on any store or parsing error
	 */
	private Object toModelObjectV2Compat(IModelStore store,  String documentUri, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Object retval = SpdxEnumFactoryCompatV2.uriToEnum.get(uri);
		if (Objects.nonNull(retval)) {
			return retval;
		} else if (SpdxConstantsCompatV2.EXTERNAL_SPDX_ELEMENT_URI_PATTERN.matcher(uri).matches()) {
			return org.spdx.library.model.compat.v2.ExternalSpdxElement.uriToExternalSpdxElement(uri, store, documentUri, copyManager);
		} else if (SpdxConstantsCompatV2.EXTERNAL_EXTRACTED_LICENSE_URI_PATTERN.matcher(uri).matches()) {
			return org.spdx.library.model.compat.v2.license.ExternalExtractedLicenseInfo.uriToExternalExtractedLicense(uri, store, documentUri, copyManager);
		} else if (SpdxConstantsCompatV2.REFERENCE_TYPE_URI_PATTERN.matcher(uri).matches()) {
			return new org.spdx.library.model.compat.v2.ReferenceType(this);
		} else if (SpdxConstantsCompatV2.URI_VALUE_NONE.equals(uri)) {
			// Default value is a license, although it can also be a string or an SpdxElement
			// the caller should override the type based on the type expected
			return new org.spdx.library.model.compat.v2.license.SpdxNoneLicense(store, documentUri);
		} else if (SpdxConstantsCompatV2.URI_VALUE_NOASSERTION.equals(uri)) {
			return new org.spdx.library.model.compat.v2.license.SpdxNoAssertionLicense(store, documentUri);
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
