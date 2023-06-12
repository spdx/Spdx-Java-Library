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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringEscapeUtils;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.model.compat.v2.ModelObject;
import org.spdx.licenseTemplate.SpdxLicenseTemplateHelper;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.ModelUpdate;
import org.spdx.utility.compare.LicenseCompareHelper;

/**
 * Describes a license
 * 
 * All licenses have an ID.  
 * Subclasses should extend this class to add additional properties.
 * 
 * @author Gary O'Neall
 *
 */
public abstract class License extends SimpleLicensingInfo {

	static final String XML_LITERAL = "^^http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral";

	/**
	 * True if the template in the model uses HTML tags.  If this flag is true, the text will
	 * be converted on import from the model.
	 */
	private boolean templateInHtml = false;
	
	
	/**
	 * Open or create a model object with the default store and default document URI
	 * @param objectUri ID for this object - must be unique within the SPDX document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public License(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * Create a new License object
	 * @param modelStore container which includes the license
	 * @param documentUri URI for the SPDX document containing the license
	 * @param objectUri identifier for the license
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	License(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/**
	 * @return the text of the license
	 * @throws SpdxInvalidTypeException 
	 */
	public String getLicenseText() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(SpdxConstantsCompatV2.PROP_LICENSE_TEXT);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}

	/**
	 * @param text the license text to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setLicenseText(String text) throws InvalidSPDXAnalysisException {
		this.setPropertyValue(SpdxConstantsCompatV2.PROP_LICENSE_TEXT, text);
	}
	
	/**
	 * @return the standardLicenseHeader
	 * @throws SpdxInvalidTypeException 
	 */
	public String getStandardLicenseHeader() throws InvalidSPDXAnalysisException {
		Optional<String> standardLicenseHeader =  getStringPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_NOTICE);
		if (standardLicenseHeader.isPresent()) {
			return StringEscapeUtils.unescapeHtml4(standardLicenseHeader.get());
		} else {
			return "";
		}
	}
	
	/**
	 * @return standard license header template
	 * @throws SpdxInvalidTypeException 
	 */
	public String getStandardLicenseHeaderTemplate() throws InvalidSPDXAnalysisException {
		Optional<String> standardLicenseHeaderTemplate = getStringPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_HEADER_TEMPLATE);
		if (standardLicenseHeaderTemplate.isPresent()) {
			return StringEscapeUtils.unescapeHtml4(standardLicenseHeaderTemplate.get());
		} else {
			return "";
		}
	}
	
	/**
	 * @param standardLicenseHeaderTemplate
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setStandardLicenseHeaderTemplate(String standardLicenseHeaderTemplate) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_HEADER_TEMPLATE, standardLicenseHeaderTemplate);
	}
	
	/**
	 * @param standardLicenseHeader the standardLicenseHeader to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setStandardLicenseHeader(String standardLicenseHeader) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_NOTICE, standardLicenseHeader);
	}
	/**
	 * @return the template
	 * @throws SpdxInvalidTypeException 
	 */
	public String getStandardLicenseTemplate() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_TEMPLATE);			
		if (!o.isPresent()) {
			return "";
		}
		String standardLicenseTemplate = o.get();
		if (standardLicenseTemplate != null && standardLicenseTemplate.endsWith(XML_LITERAL)) {
			standardLicenseTemplate = standardLicenseTemplate.substring(0, standardLicenseTemplate.length()-XML_LITERAL.length());
		}
		if (standardLicenseTemplate != null && this.templateInHtml) {
			standardLicenseTemplate = SpdxLicenseTemplateHelper.htmlToText(standardLicenseTemplate);
		}
		return standardLicenseTemplate;
	}
	/**
	 * @param template the template to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setStandardLicenseTemplate(String template) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_TEMPLATE, template);
	}
	
	@Override
	public String toString() {
		// must be only the ID if we want to reuse the 
		// toString for creating parseable license info strings
		if (this.getId() == null) {
			return "NULL LICENSE";
		} else {
			return this.getId();
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = new ArrayList<>();
		String id = this.getLicenseId();
		if (id == null || id.isEmpty()) {
			retval.add("Missing required license ID");
		}
		String name;
		try {
			name = this.getName();
			if (name == null || name.isEmpty()) {
				retval.add("Missing required license name");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for name");
		}
		try {
			this.getComment();
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for comment");
		}
		try {
			this.getSeeAlso();
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for seeAlso");
		}
		try {
			this.getStandardLicenseHeader();
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for standard license header");
		}
		try {
			this.getStandardLicenseTemplate();
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for standard license template");
		}
		//TODO Add test for template
		try {
			this.getStandardLicenseHeaderTemplate();
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for standard license header template");
		}
		//TODO add test for license header template
		String licenseText;
		try {
			licenseText = this.getLicenseText();
			if (licenseText == null || licenseText.isEmpty()) {
				retval.add("Missing required license text for " + id);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for license text");
		}
		return retval;
	}
	
	/**
	 * @return true if FSF describes the license as free / libre, false if FSF describes the license as not free / libre or if FSF does not reference the license
	 * @throws SpdxInvalidTypeException 
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean isFsfLibre() throws InvalidSPDXAnalysisException {
		Optional<Boolean> libre = getBooleanPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_FSF_LIBRE);
		if (!libre.isPresent()) {
			return false;
		}
		return libre.get();
	}
	
	/**
	 * @return true if FSF specified this license as not free/libre, false if it has been specified by the FSF as free / libre or if it has not been specified
	 * @throws SpdxInvalidTypeException 
	 */
	public boolean isNotFsfLibre() throws InvalidSPDXAnalysisException {
		Optional<Boolean> fsfLibre = getBooleanPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_FSF_LIBRE);
		return fsfLibre.isPresent() && !fsfLibre.get();
	}
	
	/**
	 * @return true if FSF describes the license as free / libre, false if FSF describes the license as not free / libre, null if FSF does not reference the license
	 * @throws SpdxInvalidTypeException 
	 */
	public Boolean getFsfLibre() throws InvalidSPDXAnalysisException {
		Optional<Boolean> libre = getBooleanPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_FSF_LIBRE);
		if (libre.isPresent()) {
			return libre.get();
		} else {
			return null;
		}
	}
	
	
	/**
	 * @return true if the license is listed as an approved license on the OSI website
	 * @throws SpdxInvalidTypeException 
	 */
	public boolean isOsiApproved() throws InvalidSPDXAnalysisException {
		Optional<Boolean> osiApproved = getBooleanPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_OSI_APPROVED);
		return osiApproved.isPresent() && osiApproved.get();
	}
	
	/**
	 * @return true if this license is marked as being deprecated
	 * @throws SpdxInvalidTypeException 
	 */
	public boolean isDeprecated() throws InvalidSPDXAnalysisException {
		Optional<Boolean> deprecated = getBooleanPropertyValue(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED);
		return deprecated.isPresent() && deprecated.get();
	}
	
	public void setOsiApproved(Boolean osiApproved) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_OSI_APPROVED, osiApproved);
	}
	
	/**
	 * @param fsfLibre true if FSF describes the license as free / libre, false if FSF describes the license as not free / libre, null if FSF does not reference the license
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setFsfLibre(Boolean fsfLibre) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_STD_LICENSE_FSF_LIBRE, fsfLibre);
	}
	
	/**
	 * @param deprecated true if this license is deprecated
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDeprecated(Boolean deprecated) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, deprecated);
	}
	
	/**
	 * @param deprecated
	 * @return a ModelUpdate that can be applied through the ModelObject
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelUpdate updateSetDeprecated(Boolean deprecated) throws InvalidSPDXAnalysisException {
		return updatePropertyValue(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, deprecated);
	}
	
	@Override
	public boolean equivalent(ModelObject compare, boolean ignoreExternalReferences) throws InvalidSPDXAnalysisException {
		if (compare instanceof License) {
			return LicenseCompareHelper.isLicenseTextEquivalent(this.getLicenseText(), ((License)compare).getLicenseText());
		} else {
			return super.equivalent(compare, ignoreExternalReferences);
		}
	}
}
