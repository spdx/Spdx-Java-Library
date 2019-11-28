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
package org.spdx.library.model.license;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;

/**
 * 
 * Represents an SPDX license exception as defined in the License Expression Language
 * Used with the "with" unary expression.
 * 
 * @author Gary O'Neall
 * 
 */
public class LicenseException extends ModelObject implements SpdxConstants {
	
	/**
	 * Create a new LicenseException object
	 * @param modelStore container which includes the license exception
	 * @param documentUri URI for the SPDX document containing the license exception
	 * @param id identifier for the license exception
	 * @param create if true, create the license exception if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	public LicenseException(IModelStore modelStore, String documentUri, String id, boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}
	
	/**
	 * @return Comment associated with the License Exception
	 * @throws InvalidSPDXAnalysisException 
	 */
	public String getComment() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(RDFS_PROP_COMMENT);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * @param comment the comment to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		setPropertyValue(RDFS_PROP_COMMENT, comment);
	}
	
	/**
	 * Deprecated since SPDX spec 2.0
	 * @return example text
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Deprecated
	public String getExample() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(PROP_EXAMPLE);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * Deprecated since SPDX spec 2.0
	 * @param example
	 * @throws InvalidSPDXAnalysisException
	 */
	@Deprecated
	public void setExample(String example) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_EXAMPLE, example);
	}
	
	/**
	 * @return the id
	 */
	public String getLicenseExceptionId() {
		return getId();
	}

	/**
	 * @return the template
	 * @throws InvalidSPDXAnalysisException 
	 */
	public String getLicenseExceptionTemplate() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(PROP_EXCEPTION_TEMPLATE);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * Set the template text for the exception
	 * @param template template
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setLicenseExceptionTemplate(String template) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_EXCEPTION_TEMPLATE, template);
	}
	
	/**
	 * @return the text
	 * @throws InvalidSPDXAnalysisException 
	 */
	public String getLicenseExceptionText() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(PROP_EXCEPTION_TEXT);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * Sets the text for the exception
	 * @param text text
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setLicenseExceptionText(String text) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_EXCEPTION_TEXT, text);
	}
	
	
	/**
	 * @return the name
	 * @throws SpdxInvalidTypeException 
	 */
	public String getName() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(PROP_STD_LICENSE_NAME);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * @param name the name to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setName(String name) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_STD_LICENSE_NAME, name);
	}
	
	/**
	 * @return the urls which reference the same license information
	 * @throws SpdxInvalidTypeException 
	 */
	public List<String> getSeeAlso() throws InvalidSPDXAnalysisException {
		return getStringPropertyValueList(RDFS_PROP_SEE_ALSO);
	}
	/**
	 * @param seeAlsoUrl the urls which are references to the same license to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setSeeAlso(List<String> seeAlsoUrl) throws InvalidSPDXAnalysisException {
		if (seeAlsoUrl == null) {
			clearPropertyValueList(RDFS_PROP_SEE_ALSO);
		} else {
			setPropertyValue(RDFS_PROP_SEE_ALSO, seeAlsoUrl);
		}
	}
	
	/**
	 * @return true if this license is marked as being deprecated
	 * @throws SpdxInvalidTypeException 
	 */
	public boolean isDeprecated() throws InvalidSPDXAnalysisException {
		Optional<Boolean> deprecated = getBooleanPropertyValue(PROP_LIC_ID_DEPRECATED);
		return deprecated.isPresent() && deprecated.get();
	}
	
	/**
	 * @param deprecated true if this license is deprecated
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDeprecated(Boolean deprecated) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_LIC_ID_DEPRECATED, deprecated);
	}
	
	/**
	 * @return the deprecatedVersion
	 * @throws SpdxInvalidTypeException 
	 */
	public String getDeprecatedVersion() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(PROP_LIC_DEPRECATED_VERSION);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}

	/**
	 * @param deprecatedVersion the deprecatedVersion to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDeprecatedVersion(String deprecatedVersion) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_LIC_DEPRECATED_VERSION, deprecatedVersion);
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return CLASS_SPDX_LICENSE_EXCEPTION;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<>();
		String id = this.getLicenseExceptionId();
		if (id == null || id.isEmpty()) {
			retval.add("Missing required exception ID");
		}
		String name;
		try {
			name = this.getName();
			if (name == null || name.isEmpty()) {
				retval.add("Missing required exception name");
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
			this.getLicenseExceptionTemplate();
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for exception template");
		}
		String exceptionText;
		try {
			exceptionText = this.getLicenseExceptionText();
			if (exceptionText == null || exceptionText.isEmpty()) {
				retval.add("Missing required exception text for " + id);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid type for exception text");
		}
		return retval;
	}
	
	@Override
	public String toString() {
		return this.getId();
	}

}
