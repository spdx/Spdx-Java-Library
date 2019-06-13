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
package org.spdx.library.model;

import java.util.ArrayList;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;

/**
 * Special class of license to represent no asserted license license in the file or packages
 * @author Gary O'Neall
 *
 */
public class SpdxNoAssertionLicense extends AnyLicenseInfo {
	
	SpdxNoAssertionLicense(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
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
	public List<String> verify() {
		return new ArrayList<>();
	}

	@Override
	public String getType() {
		return SpdxConstants.NOASSERTION_VALUE;
	}
}
