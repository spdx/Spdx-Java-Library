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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ModelObject;
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
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxListedLicense(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}
	
	/**
	 * @param name License name
	 * @param id License ID
	 * @param text License text
	 * @param sourceUrl Optional URLs that reference this license
	 * @param comments Optional comments
	 * @param standardLicenseHeader Optional license header
	 * @param template Optional template
	 * @param osiApproved True if this is an OSI Approved license
	 * @param fsfLibre true if FSF describes the license as free / libre, false if FSF describes the license as not free / libre, null if FSF does not reference the license
	 * @param licenseTextHtml HTML version for the license text
	 * @param isDeprecated True if this license has been designated as deprecated by the SPDX legal team
	 * @param deprecatedVersion License list version when this license was first deprecated (null if not deprecated)
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxListedLicense(String name, String id, String text, Collection<String> sourceUrl, String comments,
			String standardLicenseHeader, String template, boolean osiApproved, Boolean fsfLibre, 
			String licenseTextHtml, boolean isDeprecated, String deprecatedVersion) throws InvalidSPDXAnalysisException {
		this(id);
		setName(name);
		setLicenseText(text);
		setSeeAlso(sourceUrl);
		setComment(comments);
		setStandardLicenseHeader(standardLicenseHeader);
		setStandardLicenseTemplate(template);
		setOsiApproved(osiApproved);
		setFsfLibre(fsfLibre);
		setLicenseTextHtml(licenseTextHtml);
		setDeprecated(isDeprecated);
		setDeprecatedVersion(deprecatedVersion);
	}

	@Override 
	protected List<String> _verify(List<String> verifiedIds) {
		List<String> retval = super._verify(verifiedIds);
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
		Optional<String> licenseTextHtml = getStringPropertyValue(SpdxConstants.PROP_LICENSE_TEXT_HTML);
		if (licenseTextHtml.isPresent()) {
			return licenseTextHtml.get();
		} else {
			// Format the HTML using the text and template
			String templateText = this.getStandardLicenseTemplate();
			if (templateText != null && !templateText.trim().isEmpty()) {
				try {
					return SpdxLicenseTemplateHelper.templateTextToHtml(templateText);
				} catch(LicenseTemplateRuleException ex) {
					throw new InvalidLicenseTemplateException("Invalid license expression found in license text for license "+getName()+":"+ex.getMessage());
				}
			} else {
				return SpdxLicenseTemplateHelper.formatEscapeHTML(this.getLicenseText());
			}
		}
	}
	
	/**
	 * Set the licenseTextHtml
	 * @param licenseTextHtml HTML fragment representing the license text
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setLicenseTextHtml(String licenseTextHtml) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LICENSE_TEXT_HTML, licenseTextHtml);
	}
	
	/**
	 * @return HTML fragment containing the License standard header text
	 * @throws InvalidLicenseTemplateException 
	 * @throws SpdxInvalidTypeException 
	 */
	public String getLicenseHeaderHtml() throws InvalidLicenseTemplateException, InvalidSPDXAnalysisException {
		Optional<String> licenseHeaderHtml = getStringPropertyValue(SpdxConstants.PROP_LICENSE_HEADER_HTML);
		if (licenseHeaderHtml.isPresent()) {
			return licenseHeaderHtml.get();
		} else {
			// Format the HTML using the text and template
			String templateText = this.getStandardLicenseHeaderTemplate();
			if (templateText != null && !templateText.trim().isEmpty()) {
				try {
					return SpdxLicenseTemplateHelper.templateTextToHtml(templateText);
				} catch(LicenseTemplateRuleException ex) {
					throw new InvalidLicenseTemplateException("Invalid license expression found in standard license header for license "+getName()+":"+ex.getMessage());
				}
			} else {
				return SpdxLicenseTemplateHelper.formatEscapeHTML(this.getStandardLicenseHeader());
			}
		}
	}
	
	/**
	 * Set the licenseHeaderTemplateHtml
	 * @param licenseHeaderHtml HTML fragment representing the license standard header text
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setLicenseHeaderHtml(String licenseHeaderHtml) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LICENSE_HEADER_HTML, licenseHeaderHtml);
	}
	
	/**
	 * @return the deprecatedVersion
	 * @throws SpdxInvalidTypeException 
	 */
	public String getDeprecatedVersion() throws InvalidSPDXAnalysisException {
		Optional<String> depVersion = getStringPropertyValue(SpdxConstants.PROP_LIC_DEPRECATED_VERSION);
		if (depVersion.isPresent()) {
			return depVersion.get();
		} else {
			return "";
		}
	}

	/**
	 * @param deprecatedVersion the deprecatedVersion to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDeprecatedVersion(String deprecatedVersion) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LIC_DEPRECATED_VERSION, deprecatedVersion);
	}

	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_LISTED_LICENSE;
	}
	
	@Override
	public boolean equivalent(ModelObject compare) throws InvalidSPDXAnalysisException {
		if (compare instanceof SpdxListedLicense) {
			return this.getLicenseId().equals(((SpdxListedLicense)compare).getLicenseId());	// for listed license, the license ID is the only thing that matters
		} else {
			return super.equivalent(compare);
		}
	}
	
	@Override
	public boolean equals(Object compare) {
		if (!(compare instanceof SpdxListedLicense)) {
			return false;
		}
		return Objects.equals(getLicenseId(),((SpdxListedLicense)compare).getLicenseId());
	}
	
	@Override
	public int hashCode() {
		String licId = getLicenseId();
		if (Objects.isNull(licId)) {
			return 91;
		} else {
			return 91 ^ licId.hashCode();
		}
	}

}
