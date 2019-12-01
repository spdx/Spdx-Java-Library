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
package org.spdx.storage.listedlicense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.InvalidSpdxPropertyException;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.licenseTemplate.InvalidLicenseTemplateException;

/**
 * Simple POJO to hold the license data loaded from a JSON file
 * 
 * Licenses in the JSON format can be found at spdx.org/licenses/[licenseid].json
 * 
 * @author Gary O'Neall 
 *
 */
public class LicenseJson {
	
	static final List<String> PROPERTY_VALUE_NAMES = Collections.unmodifiableList(Arrays.asList(
			"licenseText", "licenseTextHtml", "name", "standardLicenseHeader",
			"standardLicenseHeaderTemplate", "standardLicenseHeaderHtml", "standardLicenseTemplate",
			"isOsiApproved", "isFsfLibre", "example", "isDeprecatedLicenseId", "deprecatedVersion", 
			"comment", "licenseId", "seeAlso"));	//NOTE: This list must be updated if any new properties are added

	String licenseText;
	String licenseTextHtml;
	String name;
	List<String> seeAlso = new ArrayList<>();
	String standardLicenseHeader;
	String standardLicenseHeaderTemplate;
	String standardLicenseHeaderHtml;
	String standardLicenseTemplate;
	Boolean isOsiApproved;
	Boolean isFsfLibre;
	String example;
	Boolean isDeprecatedLicenseId;
	String deprecatedVersion;
	String licenseComments;	//TODO:  This is for legacy JSON files - this should be removed in 3.0.  See https://github.com/spdx/spdx-spec/issues/158
	String comment;
	String licenseId;
	
	public LicenseJson(String id) {
		this.licenseId = id;
	}
	
	public LicenseJson() {
		
	}

	public List<String> getPropertyValueNames() {
		return PROPERTY_VALUE_NAMES;
	}

	public void setTypedProperty(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
	}

	public void setPrimativeValue(String propertyName, Object value) throws InvalidSpdxPropertyException {
		switch (propertyName) {
			case "licenseText":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				licenseText = (String)value;
				break;
			case "licenseTextHtml":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				licenseTextHtml = (String)value;
				break;
			case "name":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				name = (String)value;
				break;
			case "seeAlso":throw new InvalidSpdxPropertyException("Expected list type for "+propertyName);
			case "standardLicenseHeader":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				standardLicenseHeader = (String)value;
				break;
			case "standardLicenseHeaderTemplate":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				standardLicenseHeaderTemplate = (String)value;
				break;
			case "standardLicenseHeaderHtml":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				standardLicenseHeaderHtml = (String)value;
				break;
			case "standardLicenseTemplate":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				standardLicenseTemplate = (String)value;
				break;
			case "isOsiApproved":
				if (!(value instanceof Boolean)) {
				throw new InvalidSpdxPropertyException("Expected Boolean type for "+propertyName);
				}
				isOsiApproved = (Boolean)value;
				break;
			case "isFsfLibre":
				if (!(value instanceof Boolean)) {
				throw new InvalidSpdxPropertyException("Expected Boolean type for "+propertyName);
				}
				isFsfLibre = (Boolean)value;
				break;
			case "example":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				example = (String)value;
				break;
			case "isDeprecatedLicenseId":
				if (!(value instanceof Boolean)) {
				throw new InvalidSpdxPropertyException("Expected Boolean type for "+propertyName);
				}
				isDeprecatedLicenseId = (Boolean)value;
				break;
			case "deprecatedVersion":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				deprecatedVersion = (String)value;
				break;
			case "comment":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				licenseComments = (String)value;
				comment = (String)value;
				break;
			case "licenseId":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				licenseId = (String)value;
				break;
			default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
		}
	}

	public void clearPropertyValueList(String propertyName) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(propertyName)) {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		seeAlso.clear();
	}

	public boolean addPrimitiveValueToList(String propertyName, Object value) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(propertyName)) {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		if (!(value instanceof String)) {
			throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
		}
		return seeAlso.add((String)value);
	}
	
	public boolean removePrimitiveValueToList(String propertyName, Object value) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(propertyName)) {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		return seeAlso.remove(value);
	}

	public List<String> getValueList(String propertyName) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(propertyName)) {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		return seeAlso;
	}

	public Object getValue(String propertyName) throws InvalidSpdxPropertyException {
		switch (propertyName) {
			case "licenseText": return licenseText;
			case "licenseTextHtml": return licenseTextHtml;
			case "name": return name;
			case "seeAlso": return seeAlso;
			case "standardLicenseHeader": return standardLicenseHeader;
			case "standardLicenseHeaderTemplate": return standardLicenseHeaderTemplate;
			case "standardLicenseHeaderHtml": return standardLicenseHeaderHtml;
			case "standardLicenseTemplate": return standardLicenseTemplate;
			case "isOsiApproved": return isOsiApproved;
			case "isFsfLibre": return isFsfLibre;
			case "example": return example;
			case "isDeprecatedLicenseId": return isDeprecatedLicenseId;
			case "deprecatedVersion": return deprecatedVersion;
			case "comment": 
				if (comment != null) return comment;
				return licenseComments;
			case "licenseId": return licenseId;
			default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
		}
	}

	public void removeProperty(String propertyName) throws InvalidSpdxPropertyException {
		switch (propertyName) {
		case "licenseText": licenseText = null; break;
		case "licenseTextHtml": licenseTextHtml = null; break;
		case "name": name = null; break;
		case "seeAlso":seeAlso.clear(); break;
		case "standardLicenseHeader": standardLicenseHeader = null; break;
		case "standardLicenseHeaderTemplate": standardLicenseHeaderTemplate = null; break;
		case "standardLicenseHeaderHtml": standardLicenseHeaderHtml = null; break;
		case "standardLicenseTemplate": standardLicenseTemplate = null; break;
		case "isOsiApproved": isOsiApproved = null; break;
		case "isFsfLibre": isFsfLibre = null; break;
		case "example": example = null; break;
		case "isDeprecatedLicenseId": isDeprecatedLicenseId = null; break;
		case "deprecatedVersion": deprecatedVersion = null; break;
		case "comment": 
			comment = null;
			licenseComments = null; break;
		case "licenseId": licenseId = null; break;
		default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
	}
	}

	public void copyFrom(SpdxListedLicense fromLicense) throws InvalidLicenseTemplateException, InvalidSPDXAnalysisException {
		this.comment = fromLicense.getComment();
		this.deprecatedVersion = fromLicense.getDeprecatedVersion();
		this.example = null;
		this.isDeprecatedLicenseId = fromLicense.isDeprecated();
		this.isFsfLibre = fromLicense.getFsfLibre();
		this.licenseComments = null;
		this.licenseId = fromLicense.getId();
		this.licenseText = fromLicense.getLicenseText();
		this.licenseTextHtml = fromLicense.getLicenseTextHtml();
		this.name = fromLicense.getName();
		this.seeAlso = new ArrayList<String>(fromLicense.getSeeAlso());
		this.standardLicenseHeader = fromLicense.getStandardLicenseHeader();
		this.standardLicenseHeaderHtml = fromLicense.getLicenseHeaderHtml();
		this.standardLicenseHeaderTemplate = fromLicense.getStandardLicenseHeaderTemplate();
		this.standardLicenseTemplate = fromLicense.getStandardLicenseTemplate();
	}

	public boolean isPropertyValueAssignableTo(String propertyName, Class<?> clazz) throws InvalidSpdxPropertyException {
		switch (propertyName) {
		case "licenseText":
		case "licenseTextHtml":
		case "name":
		case "standardLicenseHeader":
		case "standardLicenseHeaderTemplate":
		case "standardLicenseHeaderHtml":
		case "standardLicenseTemplate":
		case "example":
		case "deprecatedVersion":
		case "comment":
		case "licenseId": return String.class.isAssignableFrom(clazz);
		case "seeAlso": return false;
		case "isOsiApproved":
		case "isFsfLibre":
		case "isDeprecatedLicenseId": return Boolean.class.isAssignableFrom(clazz);
		default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
	}

	}

	public boolean isCollectionMembersAssignableTo(String propertyName, Class<?> clazz) {
		if (!SpdxConstants.RDFS_PROP_SEE_ALSO.equals(propertyName)) {
			return false;
		}
		return String.class.isAssignableFrom(clazz);
	}
}
