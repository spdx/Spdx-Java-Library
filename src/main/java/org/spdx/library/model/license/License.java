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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.licenseTemplate.SpdxLicenseTemplateHelper;
import org.spdx.storage.IModelStore;

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
	 * True if the text in the  model uses HTML tags.  If this flag is true, the text will
	 * be converted on import from the model.
	 */
	private boolean textInHtml = true;
	/**
	 * True if the template in the model uses HTML tags.  If this flag is true, the text will
	 * be converted on import from the model.
	 */
	private boolean templateInHtml = false;
	
	/**
	 * Create a new License object
	 * @param modelStore container which includes the license
	 * @param documentUri URI for the SPDX document containing the license
	 * @param id identifier for the license
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	License(IModelStore modelStore, String documentUri, String id, boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/**
	 * @return the text of the license
	 * @throws SpdxInvalidTypeException 
	 */
	public String getLicenseText() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(PROP_LICENSE_TEXT);
	}

	/**
	 * @param text the license text to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setLicenseText(String text) throws InvalidSPDXAnalysisException {
		this.setPropertyValue(PROP_LICENSE_TEXT, text);
		this.textInHtml = false;	// stored in the clear
	}
	
	/**
	 * @return the standardLicenseHeader
	 * @throws SpdxInvalidTypeException 
	 */
	public String getStandardLicenseHeader() throws InvalidSPDXAnalysisException {
		String standardLicenseHeader =  getStringPropertyValue(PROP_STD_LICENSE_NOTICE);
		if (standardLicenseHeader != null) {
			standardLicenseHeader = StringEscapeUtils.unescapeHtml4(standardLicenseHeader);
		}
		return standardLicenseHeader;
	}
	
	/**
	 * @return standard license header template
	 * @throws SpdxInvalidTypeException 
	 */
	public String getStandardLicenseHeaderTemplate() throws InvalidSPDXAnalysisException {
		String standardLicenseHeaderTemplate = getStringPropertyValue(PROP_STD_LICENSE_HEADER_TEMPLATE);
		if (standardLicenseHeaderTemplate != null) {
			standardLicenseHeaderTemplate = StringEscapeUtils.unescapeHtml4(standardLicenseHeaderTemplate);
		}
		return standardLicenseHeaderTemplate;
	}
	
	/**
	 * @param standardLicenseHeaderTemplate
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setStandardLicenseHeaderTemplate(String standardLicenseHeaderTemplate) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_STD_LICENSE_HEADER_TEMPLATE, standardLicenseHeaderTemplate);
	}
	
	/**
	 * @param standardLicenseHeader the standardLicenseHeader to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setStandardLicenseHeader(String standardLicenseHeader) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_STD_LICENSE_NOTICE, standardLicenseHeader);
	}
	/**
	 * @return the template
	 * @throws SpdxInvalidTypeException 
	 */
	public String getStandardLicenseTemplate() throws InvalidSPDXAnalysisException {
		String standardLicenseTemplate = getStringPropertyValue(PROP_STD_LICENSE_TEMPLATE);		
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
		setPropertyValue(PROP_STD_LICENSE_TEMPLATE, template);
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
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#verify()
	 */
	@Override
	public List<String> verify() {
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
		Boolean libre = getBooleanPropertyValue(PROP_STD_LICENSE_FSF_LIBRE);
		if (libre == null) {
			return false;
		}
		return libre;
	}
	
	/**
	 * @return true if FSF specified this license as not free/libre, false if it has been specified by the FSF as free / libre or if it has not been specified
	 * @throws SpdxInvalidTypeException 
	 */
	public boolean isNotFsfLibre() throws InvalidSPDXAnalysisException {
		Boolean fsfLibre = getBooleanPropertyValue(PROP_STD_LICENSE_FSF_LIBRE);
		return fsfLibre != null && !fsfLibre;
	}
	
	/**
	 * @return true if FSF describes the license as free / libre, false if FSF describes the license as not free / libre, null if FSF does not reference the license
	 * @throws SpdxInvalidTypeException 
	 */
	public Boolean getFsfLibre() throws InvalidSPDXAnalysisException {
		return getBooleanPropertyValue(PROP_STD_LICENSE_FSF_LIBRE);
	}
	
	
	/**
	 * @return true if the license is listed as an approved license on the OSI website
	 * @throws SpdxInvalidTypeException 
	 */
	public boolean isOsiApproved() throws InvalidSPDXAnalysisException {
		Boolean osiApproved = getBooleanPropertyValue(PROP_STD_LICENSE_OSI_APPROVED);
		return osiApproved != null && osiApproved;
	}
	
	/**
	 * @return true if this license is marked as being deprecated
	 * @throws SpdxInvalidTypeException 
	 */
	public boolean isDeprecated() throws InvalidSPDXAnalysisException {
		Boolean deprecated = getBooleanPropertyValue(PROP_LIC_ID_DEPRECATED);
		return deprecated != null && deprecated;
	}
	
	public void setOsiApproved(Boolean osiApproved) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_STD_LICENSE_OSI_APPROVED, osiApproved);
	}
	
	/**
	 * @param fsfLibre true if FSF describes the license as free / libre, false if FSF describes the license as not free / libre, null if FSF does not reference the license
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setFsfLibre(Boolean fsfLibre) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_STD_LICENSE_FSF_LIBRE, fsfLibre);
	}
	
	/**
	 * @param deprecated true if this license is deprecated
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDeprecated(Boolean deprecated) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_LIC_ID_DEPRECATED, deprecated);
	}
}
