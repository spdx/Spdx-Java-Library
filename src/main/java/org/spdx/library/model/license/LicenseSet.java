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

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A specific form of license information where there is a set of licenses
 * represented
 * @author Gary O'Neall
 *
 */
public abstract class LicenseSet extends AnyLicenseInfo {
	
	
	public LicenseSet() throws InvalidSPDXAnalysisException {
		super();
	}

	public LicenseSet(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	LicenseSet(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/**
	 * Default model store and document URI initialized with members
	 * @param members
	 * @throws InvalidSPDXAnalysisException 
	 */
	public LicenseSet(Collection<AnyLicenseInfo> members) throws InvalidSPDXAnalysisException {
		super();
		for (AnyLicenseInfo member:members) {
			addMember(member);
		}
	}

	/**
	 * Sets the members of the license set.  Clears any previous members
	 * @param licenseInfos New members for the set
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setMembers(Collection<AnyLicenseInfo> licenseInfos) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LICENSE_SET_MEMEBER, licenseInfos);
	}
	
	/**
	 * @return Members of the license set
	 * @throws SpdxInvalidTypeException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<AnyLicenseInfo> getMembers() throws InvalidSPDXAnalysisException {
		if (!isCollectionMembersAssignableTo(SpdxConstants.PROP_LICENSE_SET_MEMEBER, AnyLicenseInfo.class)) {
			throw new SpdxInvalidTypeException("Expecting AnyLicenseInfo for license set member type");
		}
		return (Collection<AnyLicenseInfo>)(Collection<?>)(getObjectPropertyValueCollection(SpdxConstants.PROP_LICENSE_SET_MEMEBER, AnyLicenseInfo.class));
	}
	
	/**
	 * Adds a member to the set
	 * @param member
	 * @throws InvalidSPDXAnalysisException
	 */
	public void addMember(AnyLicenseInfo member) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(member);
		this.addPropertyValueToCollection(SpdxConstants.PROP_LICENSE_SET_MEMEBER, member);
	}
	
	public void removeMember(AnyLicenseInfo member) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(member);
		this.removePropertyValueFromCollection(SpdxConstants.PROP_LICENSE_SET_MEMEBER, member);
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
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Exception getting license set members: "+e.getMessage());
		}
		return retval;
	}
}
