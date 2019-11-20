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

import org.spdx.library.model.InvalidSpdxPropertyException;

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
			"licenseComment", "licenseId"));	//NOTE: This list must be updated if any new properties are added
	
	static final List<String> PROPERTY_VALUE_LIST_NAMES = Collections.unmodifiableList(Arrays.asList(
			"seeAlso"
			));	//NOTE: This list must be updated if any new properties are added

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
	String licenseComments;
	String licenseId;
	
	public LicenseJson(String id) {
		this.licenseId = id;
	}
	
	public LicenseJson() {
		
	}

	public List<String> getPropertyValueNames() {
		return PROPERTY_VALUE_NAMES;
	}

	public List<String> getPropertyValueListNames() {
		return PROPERTY_VALUE_LIST_NAMES;
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
			case "licenseComments":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
				}
				licenseComments = (String)value;
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

	public void addValueToList(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
	}

	public void addPrimitiveValueToList(String propertyName, Object value) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(propertyName)) {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		if (!(value instanceof String)) {
			throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
		}
		seeAlso.add((String)value);
	}

	public List<?> getValueList(String propertyName) throws InvalidSpdxPropertyException {
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
			case "seeAlso":throw new InvalidSpdxPropertyException("Expected list type for "+propertyName);
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
			case "licenseComments": return licenseComments;
			case "licenseId": return licenseId;
			default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
		}
	}
}
