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

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.storage.IModelStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A specific form of license information where there is a set of licenses
 * represented
 * @author Gary O'Neall
 *
 */
public abstract class LicenseSet extends AnyLicenseInfo {
	
	LicenseSet(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/**
	 * Sets the members of the license set.  Clears any previous members
	 * @param licenseInfos New members for the set
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setMembers(List<AnyLicenseInfo> licenseInfos) throws InvalidSPDXAnalysisException {
		replacePropertyValueList(PROP_LICENSE_SET_MEMEBER, licenseInfos);
	}
	
	/**
	 * @return Members of the license set
	 * @throws SpdxInvalidTypeException 
	 */
	@SuppressWarnings("unchecked")
	public List<AnyLicenseInfo> getMembers() throws SpdxInvalidTypeException {
		List<?> retval = getObjectPropertyValueList(PROP_LICENSE_SET_MEMEBER);
		if (!retval.isEmpty() && !(retval.get(0) instanceof AnyLicenseInfo)) {
			throw new SpdxInvalidTypeException("Expecting AnyLicenseInfo for license set member type.  Found "+retval.get(0).getClass().toString());
		}
		return (List<AnyLicenseInfo>)retval;
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<>();
		Iterator<AnyLicenseInfo> iter;
		try {
			iter = getMembers().iterator();
			while (iter.hasNext()) {
				retval.addAll(iter.next().verify());
			}
		} catch (SpdxInvalidTypeException e) {
			retval.add("Exception getting license set members: "+e.getMessage());
		}
		return retval;
	}
}
