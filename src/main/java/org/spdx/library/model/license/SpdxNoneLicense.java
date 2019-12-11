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
package org.spdx.library.model.license;

import java.util.ArrayList;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.IndividuallValue;
import org.spdx.storage.IModelStore;

/**
 * A special license meaning that no license was found
 * @author Gary O'Neall
 *
 */
public class SpdxNoneLicense extends AnyLicenseInfo implements IndividuallValue {
	
	static final int NONE_LICENSE_HASHCODE = 147; // prime number - all none licenses should have the same hashcde
	static final String NONE_LICENSE_ID = "SPDX_NONE_LICENSE";
	
	/**
	 * Create a new NoneLicense with the default store and default document URI
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxNoneLicense() throws InvalidSPDXAnalysisException {
		super(NONE_LICENSE_ID);
	}

	public SpdxNoneLicense(IModelStore modelStore, String documentUri)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, NONE_LICENSE_ID, true);
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#toString()
	 */
	@Override
	public String toString() {
		return SpdxConstants.NONE_VALUE;
	}
	
	@Override
	public int hashCode() {
		return NONE_LICENSE_HASHCODE;
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		return (o instanceof SpdxNoneLicense);		// All Instances of this type are considered equal
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#verify()
	 */
	@Override
	public List<String> verify() {
		return new ArrayList<>();
	}

	@Override
	public String getType() {
		return SpdxConstants.CLASS_NONE_LICENSE;
	}

	@Override
	public String getIndividualURI() {
		return SpdxConstants.URI_VALUE_NONE;
	}
	
	
}
