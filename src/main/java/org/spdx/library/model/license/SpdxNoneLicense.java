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
import java.util.Set;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.IndividualUriValue;
import org.spdx.library.model.SimpleUriValue;
import org.spdx.storage.IModelStore;

/**
 * A special license meaning that no license was found
 * @author Gary O'Neall
 *
 */
public class SpdxNoneLicense extends AnyLicenseInfo implements IndividualUriValue {
	
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
		super(modelStore, documentUri, NONE_LICENSE_ID, null, true);
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#toString()
	 */
	@Override
	public String toString() {
		return SpdxConstants.NONE_VALUE;
	}
	
	@Override
	public boolean equals(Object comp) {
		return SimpleUriValue.isIndividualUriValueEquals(this, comp);
	}

	@Override
	public int hashCode() {
		return SimpleUriValue.getIndividualUriValueHash(this);
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#verify()
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
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
