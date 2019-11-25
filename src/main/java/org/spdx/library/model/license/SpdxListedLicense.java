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

import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.licenseTemplate.InvalidLicenseTemplateException;
import org.spdx.licenseTemplate.LicenseTemplateRuleException;
import org.spdx.licenseTemplate.SpdxLicenseTemplateHelper;
import org.spdx.storage.IModelStore;

/**
 * Listed license for SPDX as listed at spdx.org/licenses
 * @author Gary O'Neall
 *
 */
public class SpdxListedLicense extends License {
	
	/**
	 * Open or create a model object with the default store and default document URI
	 * @param id ID for this object - must be unique within the SPDX document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxListedLicense(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * Create a new SPDX Listed License object
	 * @param modelStore container which includes the license
	 * @param documentUri URI for the SPDX document containing the license
	 * @param id identifier for the license
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxListedLicense(IModelStore modelStore, String documentUri, String id, boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	@Override 
	public List<String> verify() {
		List<String> retval = super.verify();
		try {
			if (this.isDeprecated()) {
				retval.add(this.getLicenseId() + " is deprecated.");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for SPDX license isDeprecated");
		}
		return retval;
	}
	
	/**
	 * @return HTML fragment containing the License Text
	 * @throws InvalidLicenseTemplateException 
	 * @throws SpdxInvalidTypeException 
	 */
	public String getLicenseTextHtml() throws InvalidLicenseTemplateException, InvalidSPDXAnalysisException {
		String licenseTextHtml = getStringPropertyValue(PROP_LICENSE_TEXT_HTML);
		if (licenseTextHtml == null) {
			// Format the HTML using the text and template
			String templateText = this.getStandardLicenseTemplate();
			if (templateText != null && !templateText.trim().isEmpty()) {
				try {
					licenseTextHtml = SpdxLicenseTemplateHelper.templateTextToHtml(templateText);
				} catch(LicenseTemplateRuleException ex) {
					throw new InvalidLicenseTemplateException("Invalid license expression found in license text for license "+getName()+":"+ex.getMessage());
				}
			} else {
				licenseTextHtml = SpdxLicenseTemplateHelper.formatEscapeHTML(this.getLicenseText());
			}
		}
		return licenseTextHtml;
	}
	
	/**
	 * Set the licenseTextHtml
	 * @param licenseTextHtml HTML fragment representing the license text
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setLicenseTextHtml(String licenseTextHtml) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_LICENSE_TEXT_HTML, licenseTextHtml);
	}
	
	/**
	 * @return HTML fragment containing the License standard header text
	 * @throws InvalidLicenseTemplateException 
	 * @throws SpdxInvalidTypeException 
	 */
	public String getLicenseHeaderHtml() throws InvalidLicenseTemplateException, InvalidSPDXAnalysisException {
		String licenseHeaderHtml = getStringPropertyValue(PROP_LICENSE_HEADER_HTML);
		if (licenseHeaderHtml == null) {
			// Format the HTML using the text and template
			String templateText = this.getStandardLicenseHeaderTemplate();
			if (templateText != null && !templateText.trim().isEmpty()) {
				try {
					licenseHeaderHtml = SpdxLicenseTemplateHelper.templateTextToHtml(templateText);
				} catch(LicenseTemplateRuleException ex) {
					throw new InvalidLicenseTemplateException("Invalid license expression found in standard license header for license "+getName()+":"+ex.getMessage());
				}
			} else if (this.getStandardLicenseHeader() == null) {
				licenseHeaderHtml = "";
			} else {
				licenseHeaderHtml = SpdxLicenseTemplateHelper.formatEscapeHTML(this.getStandardLicenseHeader());
			}
		}
		return licenseHeaderHtml;
	}
	
	/**
	 * Set the licenseHeaderTemplateHtml
	 * @param licenseHeaderHtml HTML fragment representing the license standard header text
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setLicenseHeaderHtml(String licenseHeaderHtml) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_LICENSE_HEADER_HTML, licenseHeaderHtml);
	}
	
	/**
	 * @return the deprecatedVersion
	 * @throws SpdxInvalidTypeException 
	 */
	public String getDeprecatedVersion() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(PROP_LIC_DEPRECATED_VERSION);
	}

	/**
	 * @param deprecatedVersion the deprecatedVersion to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDeprecatedVersion(String deprecatedVersion) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_LIC_DEPRECATED_VERSION, deprecatedVersion);
	}

	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_LISTED_LICENSE;
	}

}
