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
import org.spdx.library.model.license.LicenseException;

/**
 * Simple POJO to hold the license exception data loaded from a JSON file
 * 
 * Licenses in the JSON format can be found at spdx.org/licenses/[exceptionid].json
 * 
 * @author Gary O'Neall 
 *
 */
public class ExceptionJson {
	
	static final List<String> PROPERTY_VALUE_NAMES = Collections.unmodifiableList(Arrays.asList(
			"licenseExceptionText", "name",  "licenseExceptionTemplate",
			"example", "isDeprecatedLicenseId", "deprecatedVersion", 
			"comment", "licenseExceptionId", "seeAlso"));	//NOTE: This list must be updated if any new properties are added

	String licenseExceptionText;
	String name;
	List<String> seeAlso = new ArrayList<>();
	String licenseExceptionTemplate;
	String example;
	Boolean isDeprecatedLicenseId;
	String deprecatedVersion;
	String licenseComments;	//TODO:  This is for legacy JSON files - this should be removed in 3.0.  See https://github.com/spdx/spdx-spec/issues/158
	String comment;
	String licenseExceptionId;
	
	public ExceptionJson(String id) {
		this.licenseExceptionId = id;
	}
	
	public ExceptionJson() {
		
	}

	public List<String> getPropertyValueNames() {
		return PROPERTY_VALUE_NAMES;
	}

	public void setTypedProperty(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
	}

	public void setPrimativeValue(String propertyName, Object value) throws InvalidSpdxPropertyException {
		switch (propertyName) {
			case "licenseExceptionText":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				licenseExceptionText = (String)value;
				break;
			case "name":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				name = (String)value;
				break;
			case "seeAlso":throw new InvalidSpdxPropertyException("Expected list type for "+propertyName);
			case "licenseExceptionTemplate":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				licenseExceptionTemplate = (String)value;
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
			case "licenseExceptionId":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				licenseExceptionId = (String)value;
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

	public void addValueToList(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
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

	public List<?> getValueList(String propertyName) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(propertyName)) {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		return seeAlso;
	}

	public Object getValue(String propertyName) throws InvalidSpdxPropertyException {
		switch (propertyName) {
			case "licenseExceptionText": return licenseExceptionText;
			case "name": return name;
			case "seeAlso":return seeAlso;
			case "licenseExceptionTemplate": return licenseExceptionTemplate;
			case "example": return example;
			case "isDeprecatedLicenseId": return isDeprecatedLicenseId;
			case "deprecatedVersion": return deprecatedVersion;
			case "comment": 
				if (comment != null) return comment;
				return licenseComments;
			case "licenseExceptionId": return licenseExceptionId;
			default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
		}
	}

	public void removeProperty(String propertyName) throws InvalidSpdxPropertyException  {
		switch (propertyName) {
		case "licenseExceptionText": licenseExceptionText = null; break;
		case "name": name = null; break;
		case "seeAlso":seeAlso.clear(); break;
		case "licenseExceptionTemplate": licenseExceptionTemplate = null; break;
		case "example": example = null; break;
		case "isDeprecatedLicenseId": isDeprecatedLicenseId = null; break;
		case "deprecatedVersion": deprecatedVersion = null; break;
		case "comment": 
			comment = null;
			licenseComments = null; break;
		case "licenseExceptionId": licenseExceptionId = null; break;
		default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
	}

	}

	@SuppressWarnings("deprecation")
	public void copyFrom(LicenseException fromException) throws InvalidSPDXAnalysisException {
		this.comment = fromException.getComment();
		this.deprecatedVersion = fromException.getDeprecatedVersion();
		this.example = fromException.getExample();
		this.isDeprecatedLicenseId = fromException.isDeprecated();
		this.licenseComments = null;
		this.licenseExceptionId = fromException.getId();
		this.licenseExceptionTemplate = fromException.getLicenseExceptionTemplate();
		this.licenseExceptionText = fromException.getLicenseExceptionText();
		this.name = fromException.getName();
		this.seeAlso = new ArrayList<String>(fromException.getSeeAlso());
	}

	public boolean removePrimitiveValueToList(String propertyName, Object value) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(propertyName)) {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		return seeAlso.remove(value);
	}

	public boolean isPropertyValueAssignableTo(String propertyName, Class<?> clazz) throws InvalidSpdxPropertyException {
		switch (propertyName) {
		case "licenseExceptionText":
		case "name":
		case "licenseExceptionTemplate": 
		case "example": 
		case "comment": 
		case "deprecatedVersion":
		case "licenseExceptionId": return String.class.isAssignableFrom(clazz);
		case "seeAlso": return false;
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
