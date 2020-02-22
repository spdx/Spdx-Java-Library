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
import org.spdx.library.model.IndividualUriValue;
import org.spdx.storage.IModelStore;

/**
 * Special class of license to represent no asserted license license in the file or packages
 * @author Gary O'Neall
 *
 */
public class SpdxNoAssertionLicense extends AnyLicenseInfo implements IndividualUriValue {
	
	static final String NOASSERTION_LICENSE_ID = "NOASSERTION_LICENSE_ID";
	
	/**
	 * Create a new No Assertion license with the default store and default document URI
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxNoAssertionLicense() throws InvalidSPDXAnalysisException {
		super(NOASSERTION_LICENSE_ID);
	}

	public SpdxNoAssertionLicense(IModelStore modelStore, String documentUri)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, NOASSERTION_LICENSE_ID, null, true);
	}
	
	static final int NO_ASSERTION_HASHCODE = 89;	// prime number - all NoAssertion licenses should have the same hashcode

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#toString()
	 */
	@Override
	public String toString() {
		return SpdxConstants.NOASSERTION_VALUE;
	}
	
	@Override
	public int hashCode() {
		return NO_ASSERTION_HASHCODE;
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		return (o instanceof SpdxNoAssertionLicense);		// All instances of this type are considered equal
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#verify()
	 */
	@Override
	protected List<String> _verify(List<String> verifiedIds) {
		return new ArrayList<>();
	}

	@Override
	public String getType() {
		return SpdxConstants.CLASS_NOASSERTION_LICENSE;
	}

	@Override
	public String getIndividualURI() {
		return SpdxConstants.URI_VALUE_NOASSERTION;
	}
}
