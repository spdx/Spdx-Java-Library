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
package org.spdx.library.model.compat.v2;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.Version;
import org.spdx.library.model.compat.v2.license.AnyLicenseInfo;
import org.spdx.library.model.compat.v2.license.SpdxNoAssertionLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;

/**
 * An SpdxItem is a potentially copyrightable work.
 * @author Gary O'Neall
 */
public abstract class SpdxItem extends SpdxElement {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxItem() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param objectUri
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxItem(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param objectUri
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxItem(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/**
	 * @return Property name for licenseInfoFromFiles.  Override if using a subproperty of "licenseDeclared".
	 */
	protected PropertyDescriptor getLicenseInfoFromFilesPropertyName() {
		return SpdxConstantsCompatV2.PROP_PACKAGE_LICENSE_INFO_FROM_FILES;
	}
	
	/**
	 * @return the licenseConcluded
	 */
	public AnyLicenseInfo getLicenseConcluded() throws InvalidSPDXAnalysisException {
		Optional<AnyLicenseInfo> retval = getAnyLicenseInfoPropertyValue(SpdxConstantsCompatV2.PROP_LICENSE_CONCLUDED);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			logger.warn("No license concluded stored, returning NOASSERTION");
			return new SpdxNoAssertionLicense(getModelStore(), getDocumentUri());
		}
	}
	
	/**
	 * Set the licenseConcluded
	 * @param license
	 * @return this so you can chain setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxItem setLicenseConcluded(@Nullable AnyLicenseInfo license) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_LICENSE_CONCLUDED, license);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<AnyLicenseInfo> getLicenseInfoFromFiles() throws InvalidSPDXAnalysisException {
		return (Collection<AnyLicenseInfo>)(Collection<?>)this.getObjectPropertyValueSet(getLicenseInfoFromFilesPropertyName(), AnyLicenseInfo.class);
	}
	
	/**
	 * @return the copyrightText, empty string if no copyright was set
	 */
	public String getCopyrightText() throws InvalidSPDXAnalysisException {
		Optional<String> retval = getStringPropertyValue(SpdxConstantsCompatV2.PROP_COPYRIGHT_TEXT);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			return "";
		}
	}
	
	/**
	 * @param copyrightText the copyrightText to set
	 * @return myself - so you can chain setters
	 */
	public SpdxItem setCopyrightText(@Nullable String copyrightText) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_COPYRIGHT_TEXT, copyrightText);
		return this;
	}
	
	/**
	 * @return attribution text collection
	 * @throws InvalidSPDXAnalysisException
	 */
	public Collection<String> getAttributionText() throws InvalidSPDXAnalysisException {
		return getStringCollection(SpdxConstantsCompatV2.PROP_ATTRIBUTION_TEXT);
	}
	
	@Override 
	public SpdxItem setName(String name) throws InvalidSPDXAnalysisException {
		super.setName(name);
		return this;
	}
	
	/**
	 * @return the licenseComment
	 */
	public Optional<String> getLicenseComments() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstantsCompatV2.PROP_LIC_COMMENTS);
	}
	
	/**
	 * @param licenseComments the licenseComment to set
	 * @return this so you chan chain setters
	 */
	public SpdxItem setLicenseComments(String licenseComments) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_LIC_COMMENTS, licenseComments);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.SpdxElement#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = super._verify(verifiedIds, specVersion);
		String name = "UNKNOWN";
		Optional<String> myName;
		try {
			myName = this.getName();
			if (myName.isPresent()) {
				name = myName.get();
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting name: "+e.getMessage());
		}

		Optional<AnyLicenseInfo> concluded;
		try {
			concluded = getAnyLicenseInfoPropertyValue(SpdxConstantsCompatV2.PROP_LICENSE_CONCLUDED);
			if (!concluded.isPresent()) {
				if (Version.versionLessThan(specVersion, Version.TWO_POINT_THREE_VERSION)) {
					retval.add("Missing required concluded license for "+name);
				}
			} else {
				retval.addAll(concluded.get().verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting license concluded: "+e.getMessage());
		}
		String copyrightText;
		try {
			copyrightText = getCopyrightText();
			if (copyrightText.isEmpty() && Version.versionLessThan(specVersion, Version.TWO_POINT_THREE_VERSION)) {
				retval.add("Missing required copyright text for "+name);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting comment: "+e.getMessage());
		}

		try {
			this.verifyCollection(getLicenseInfoFromFiles(), "SPDX Item "+name+" ", verifiedIds, specVersion);
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting license information from files: "+e.getMessage());
		}
		addNameToWarnings(retval);
		return retval;
	}
}
