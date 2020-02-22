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
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;


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

	public OrLaterOperator(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}
	
	/**
	 * Create a new OrLaterOperator applied to license using the same ModelStore and DocumentURI as the license
	 * @param license License the OrLater applies to
	 * @throws InvalidSPDXAnalysisException
	 */
	public OrLaterOperator(SimpleLicensingInfo license) throws InvalidSPDXAnalysisException {
		super(license.getModelStore(), license.getDocumentUri(), 
				license.getModelStore().getNextId(IdType.Anonymous, license.getDocumentUri()),
				license.getCopyManager(), true);
		setLicense(license);
	}

	/**
	 * @return the license
	 * @throws SpdxInvalidTypeException 
	 */
	public SimpleLicensingInfo getLicense() throws InvalidSPDXAnalysisException {
		Optional<AnyLicenseInfo> retval = getAnyLicenseInfoPropertyValue(SpdxConstants.PROP_LICENSE_SET_MEMEBER);
		if (!retval.isPresent()) {
			throw new SpdxInvalidTypeException("Missing required license for OrLater operator");
		}
		if (!(retval.get() instanceof SimpleLicensingInfo)) {
			throw new SpdxInvalidTypeException("Expecting SimpleLicensingInfo for or operator license type.  Found "+retval.getClass().toString());
		}
		return (SimpleLicensingInfo)retval.get();
	}

	/**
	 * @param license the license to set
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setLicense(SimpleLicensingInfo license) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LICENSE_SET_MEMEBER, license);
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

	@Override
	protected List<String> _verify(List<String> verifiedIds) {
		List<String> retval = new ArrayList<>();
		SimpleLicensingInfo license;
		try {
			license = getLicense();
			if (license == null) {
				retval.add("Missing required license for a License Or Later operator");
			} else {
				retval.addAll(license.verify(verifiedIds));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Exception getting license for OrLater: "+e.getMessage());
		}
		return retval;
	}

	@Override
	public String getType() {
		return SpdxConstants.CLASS_OR_LATER_OPERATOR;
	}
	
	@Override
	public boolean equals(Object compare) {
		if (!(compare instanceof OrLaterOperator)) {
			return false;
		}
		SimpleLicensingInfo cLic;
		try {
			cLic = ((OrLaterOperator)compare).getLicense();
			return Objects.equals(this.getLicense(), cLic);
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override 
	public int hashCode() {
		int licHashCode = 101;
		try {
			if (this.getLicense() != null) {
				licHashCode = this.getLicense().hashCode() ^ 101;
			}
		} catch (InvalidSPDXAnalysisException e) {
			// Ignore - use the null value
		}
		return licHashCode;
	}
}