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
package org.spdx.library.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.storage.IModelStore;

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
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxItem(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxItem(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/**
	 * @return Property name for licenseInfoFromFiles.  Override if using a subproperty of "licenseDeclared".
	 */
	protected String getLicenseInfoFromFilesPropertyName() {
		return SpdxConstants.PROP_PACKAGE_LICENSE_INFO_FROM_FILES;
	}
	
	/**
	 * @return the licenseConcluded
	 */
	public Optional<AnyLicenseInfo> getLicenseConcluded() throws InvalidSPDXAnalysisException {
		return getAnyLicenseInfoPropertyValue(SpdxConstants.PROP_LICENSE_CONCLUDED);
	}
	
	/**
	 * Set the licenseConcluded
	 * @param license
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setLicenseConcluded(@Nullable AnyLicenseInfo license) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LICENSE_CONCLUDED, license);
	}
	
	@SuppressWarnings("unchecked")
	Collection<AnyLicenseInfo> getLicenseInfoFromFiles() throws InvalidSPDXAnalysisException {
		return (Collection<AnyLicenseInfo>)(Collection<?>)this.getObjectPropertyValueCollection(getLicenseInfoFromFilesPropertyName(), AnyLicenseInfo.class);
	}
	
	/**
	 * @return the copyrightText
	 */
	public Optional<String> getCopyrightText() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_COPYRIGHT_TEXT);
	}
	
	/**
	 * @param copyrightText the copyrightText to set
	 */
	public void setCopyrightText(@Nullable String copyrightText) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_COPYRIGHT_TEXT, copyrightText);
	}
	
	/**
	 * @return the licenseComment
	 */
	public Optional<String> getLicenseComments() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_LIC_COMMENTS);
	}
	
	/**
	 * @param licenseComments the licenseComment to set
	 */
	public void setLicenseComments(String licenseComments) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LIC_COMMENTS, licenseComments);
	}
	
	@Override
	public List<String> verify() {
		List<String> retval = super.verify();
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
			concluded = getLicenseConcluded();
			if (!concluded.isPresent()) {
				retval.add("Missing required concluded license for "+name);
			} else {
				retval.addAll(concluded.get().verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting license concluded: "+e.getMessage());
		}
		Optional<String> copyrightText;
		try {
			copyrightText = getComment();
			if (!copyrightText.isPresent() || copyrightText.get().isEmpty()) {
				retval.add("Missing required copyright text for "+name);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting comment: "+e.getMessage());
		}

		try {
			this.verifyCollection(getLicenseInfoFromFiles(), "SPDX Item "+name+" ");
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting license information from files: "+e.getMessage());
		}
		addNameToWarnings(retval);
		return retval;
	}
}
