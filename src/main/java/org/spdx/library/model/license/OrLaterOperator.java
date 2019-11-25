/**
 * Copyright (c) 2015 Source Auditor Inc.
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
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;


/**
 * A license that has an or later operator (e.g. GPL-2.0+)
 * @author Gary O'Neall
 *
 */
public class OrLaterOperator extends AnyLicenseInfo {
	
	public OrLaterOperator() throws InvalidSPDXAnalysisException {
		super();
	}

	public OrLaterOperator(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	public OrLaterOperator(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}
	
	/**
	 * @return the license
	 * @throws SpdxInvalidTypeException 
	 */
	public SimpleLicensingInfo getLicense() throws InvalidSPDXAnalysisException {
		Object retval = getObjectPropertyValue(PROP_LICENSE_SET_MEMEBER);
		if (!(retval instanceof SimpleLicensingInfo)) {
			throw new SpdxInvalidTypeException("Expecting SimpleLicensingInfo for or operator license type.  Found "+retval.getClass().toString());
		}
		return (SimpleLicensingInfo)retval;
	}

	/**
	 * @param license the license to set
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setLicense(SimpleLicensingInfo license) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_LICENSE_SET_MEMEBER, license);
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#toString()
	 */
	@Override
	public String toString() {
		SimpleLicensingInfo license;
		try {
			license = getLicense();
		} catch (InvalidSPDXAnalysisException e) {
			return "ERROR GETTING ORLATER LICENSE";
		}
		if (license == null) {
			return "UNDEFINED OR EXCEPTION";
		}
		return license.toString() + "+";
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<>();
		SimpleLicensingInfo license;
		try {
			license = getLicense();
			if (license == null) {
				retval.add("Missing required license for a License Or Later operator");
			} else {
				retval.addAll(license.verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Exception getting license for OrLater: "+e.getMessage());
		}
		return retval;
	}

	@Override
	public String getType() {
		return CLASS_OR_LATER_OPERATOR;
	}
}