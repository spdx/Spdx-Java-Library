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
package org.spdx.library.model.compat.v2.license;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.model.compat.v2.ModelObject;
import org.spdx.storage.IModelStore;

/**
 * A set of licenses where all of the licenses apply
 * @author Gary O'Neall
 *
 */
public class ConjunctiveLicenseSet extends LicenseSet {
	
	public ConjunctiveLicenseSet() throws InvalidSPDXAnalysisException {
		super();
	}

	public ConjunctiveLicenseSet(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public ConjunctiveLicenseSet(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("(");
		boolean moreThanOne = false;
		Iterator<AnyLicenseInfo> iter;
		try {
			iter = this.getMembers().iterator();
			while (iter.hasNext()) {
				if (moreThanOne) {
					sb.append(" AND ");
				}
				moreThanOne = true;
				sb.append(iter.next().toString());
			}
			sb.append(')');
			return sb.toString();
		} catch (InvalidSPDXAnalysisException e) {
			return "ERROR RETRIEVING LICENSE SET MEMBERS";
		}
	}
	

	/**
	 * Conjunctive license sets can contain other conjunctive license sets as members.  Logically,
	 * the members of these "sub-conjunctive license sets" could be direct members and have the same
	 * meaning.
	 * @return all members "flattening out" conjunctive license sets which are members of this set
	 * @throws SpdxInvalidTypeException 
	 */
	public List<AnyLicenseInfo> getFlattenedMembers() throws InvalidSPDXAnalysisException {
		HashSet<AnyLicenseInfo> retval = new HashSet<>();	// Use a set since any duplicated elements would be still considered equal
		Iterator<AnyLicenseInfo> iter = this.getMembers().iterator();
		while (iter.hasNext()) {
			AnyLicenseInfo li = iter.next();
			if (li instanceof ConjunctiveLicenseSet) {
				// we need to flatten this out
				List<AnyLicenseInfo> members = ((ConjunctiveLicenseSet)li).getFlattenedMembers();
				for (int i = 0; i < members.size(); i++) {
					retval.add(members.get(i));
				}
			} else {
				retval.add(li);
			}
		}
		List<AnyLicenseInfo> retvallist = new ArrayList<>();
		retvallist.addAll(retval);
		return retvallist;
	}

	@Override
	public int hashCode() {
		// We override equals and hashcode to take into account flattening of the license set
		// Calculate a hashcode by XOR'ing all of the hashcodes of the license set
		int retval = 41;	// Prime number
		List<AnyLicenseInfo> allMembers;
		try {
			allMembers = this.getFlattenedMembers();
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException("Error getting license set members",e);
		}
		for (AnyLicenseInfo licenseInfo:allMembers) {
			retval = retval ^ licenseInfo.hashCode();
		}
		return retval;
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// We override equals and hashcode to take into account flattening of the license set
		if (o == this) {
			return true;
		}
		if (!(o instanceof ConjunctiveLicenseSet)) {
			// covers o == null, as null is not an instance of anything
			return false;
		}
		ConjunctiveLicenseSet comp = (ConjunctiveLicenseSet)o;
		List<AnyLicenseInfo> compInfos;
		try {
			compInfos = comp.getFlattenedMembers();
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException("Error getting compare license set members",e);
		}
		List<AnyLicenseInfo> myInfos;
		try {
			myInfos = this.getFlattenedMembers();
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException("Error getting license set members",e);
		}
		if (compInfos.size() != myInfos.size()) {
			return false;
		}
		for (AnyLicenseInfo myInfo:myInfos) {
			if (!compInfos.contains(myInfo)) {
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.model.IRdfModel#equivalent(org.spdx.rdfparser.model.IRdfModel)
	 */
	@Override
	public boolean equivalent(ModelObject compare) throws InvalidSPDXAnalysisException {
		if (!(compare instanceof ConjunctiveLicenseSet)) {
			return false;
		}
		return setsEquivalent((ConjunctiveLicenseSet)compare);
	}

	protected boolean setsEquivalent(ConjunctiveLicenseSet compare) throws InvalidSPDXAnalysisException {
		List<AnyLicenseInfo> compInfos;
		try {
			compInfos = compare.getFlattenedMembers();
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException("Error getting compare license set members",e);
		}
		List<AnyLicenseInfo> myInfos;
		try {
			myInfos = this.getFlattenedMembers();
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException("Error getting license set members",e);
		}
		if (compInfos.size() != myInfos.size()) {
			return false;
		}
		for (AnyLicenseInfo myInfo:myInfos) {
			if (!compInfos.contains(myInfo)) {
				boolean found = false;
				for (AnyLicenseInfo compInfo:compInfos) {
					if (myInfo.equivalent(compInfo)) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String getType() {
		return SpdxConstantsCompatV2.CLASS_SPDX_CONJUNCTIVE_LICENSE_SET;
	}
}
