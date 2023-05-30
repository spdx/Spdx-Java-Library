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
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.InvalidSpdxPropertyException;
import org.spdx.library.model.license.CrossRef;
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
			"comment", "licenseId", "seeAlso", "crossRef"));	//NOTE: This list must be updated if any new properties are added

	Boolean isDeprecatedLicenseId;
	Boolean isFsfLibre;
	String licenseText;
	String standardLicenseHeaderTemplate;
	String standardLicenseTemplate;
	String name;
	String licenseComments;	//TODO:  This is for legacy JSON files - this should be removed in 3.0.  See https://github.com/spdx/spdx-spec/issues/158
	String comment;
	String licenseId;
	String standardLicenseHeader;
	List<CrossRefJson> crossRef = new ArrayList<>();
	List<String> seeAlso = new ArrayList<>();
	Boolean isOsiApproved;
	String licenseTextHtml;
	String standardLicenseHeaderHtml;
	String example;
	String deprecatedVersion;
	
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
			case "seeAlso":
			case "crossRef": throw new InvalidSpdxPropertyException("Expected list type for "+propertyName);
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
		if ("seeAlso".equals(propertyName)) {
			seeAlso.clear();
		} else if ("crossRef".equals(propertyName)) {
			crossRef.clear();
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		
	}

	/**
	 * Add a cross reference to a value list
	 * @param propertyName
	 * @param value
	 * @return true as specified by <code>Collections.add</code>
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean addCrossRefValueToList(String propertyName, CrossRefJson value) throws InvalidSPDXAnalysisException {
		if (SpdxConstants.PROP_CROSS_REF.getName().equals(propertyName)) {
			return crossRef.add(value);
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a crossRef list type");
		}
	}
	
	/**
	 * Add a primitive value to a value list
	 * @param propertyName
	 * @param value
	 * @return true as specified by <code>Collections.add</code>
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean addPrimitiveValueToList(String propertyName, Object value) throws InvalidSPDXAnalysisException {
		if ("seeAlso".equals(propertyName)) {
			if (!(value instanceof String)) {
				throw new InvalidSpdxPropertyException("Expected string type for "+propertyName);
			}
			return seeAlso.add((String)value);
		} else if (SpdxConstants.PROP_CROSS_REF.getName().equals(propertyName)) {
			if (!(value instanceof CrossRefJson)) {
				throw new InvalidSpdxPropertyException("Expected CrossRefJson type for "+propertyName);
			}
			return crossRef.add((CrossRefJson)value);
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
	}
	
	public boolean removePrimitiveValueToList(String propertyName, Object value) throws InvalidSpdxPropertyException {
		if ("seeAlso".equals(propertyName)) {
			return seeAlso.remove(value);
		} else if ("crossRef".equals(propertyName)) {
			return crossRef.remove(value);
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
	}

	public List<?> getValueList(String propertyName) throws InvalidSpdxPropertyException {
		if ("seeAlso".equals(propertyName)) {
			return seeAlso;
		} else if ("crossRef".equals(propertyName)) {
			return crossRef;
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
	}

	public Object getValue(String propertyName) throws InvalidSpdxPropertyException {
		switch (propertyName) {
			case "licenseText": return licenseText;
			case "licenseTextHtml": return licenseTextHtml;
			case "name": return name;
			case "seeAlso": return seeAlso;
			case "crossRef": return crossRef;
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
		case "crossRef":crossRef.clear(); break;
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
		/* TODO: Uncomment this in 3.0 and remove the following comment setting code in 3.0
		this.licenseComments = null;
		this.comment = fromLicense.getComment();
		if (Objects.nonNull(this.comment) && this.comment.isEmpty()) {
			this.comment = null;
		}
		*/
		this.comment = null;
		this.licenseComments = fromLicense.getComment();
		if (Objects.nonNull(this.licenseComments) && this.licenseComments.isEmpty()) {
			this.licenseComments = null;
		}
		this.deprecatedVersion = fromLicense.getDeprecatedVersion();
		if (Objects.nonNull(this.deprecatedVersion) && this.deprecatedVersion.isEmpty()) {
			this.deprecatedVersion = null;
		}
		this.example = null;
		this.isDeprecatedLicenseId = fromLicense.isDeprecated();
		this.isFsfLibre = fromLicense.getFsfLibre();
		this.licenseId = fromLicense.getId();
		this.licenseText = fromLicense.getLicenseText();
		if (Objects.nonNull(this.licenseText) && this.licenseText.isEmpty()) {
			this.licenseText = null;
		}
		this.licenseTextHtml = fromLicense.getLicenseTextHtml();
		if (Objects.nonNull(this.licenseTextHtml) && this.licenseTextHtml.isEmpty()) {
			this.licenseTextHtml = null;
		}
		this.name = fromLicense.getName();
		if (Objects.nonNull(this.name) && this.name.isEmpty()) {
			this.name = null;
		}
		this.isOsiApproved = fromLicense.isOsiApproved();
		this.seeAlso = new ArrayList<String>(fromLicense.getSeeAlso());
		this.standardLicenseHeader = fromLicense.getStandardLicenseHeader();
		if (Objects.nonNull(this.standardLicenseHeader) && this.standardLicenseHeader.isEmpty()) {
			this.standardLicenseHeader = null;
		}
		this.standardLicenseHeaderHtml = fromLicense.getLicenseHeaderHtml();
		if (Objects.nonNull(this.standardLicenseHeaderHtml) && this.standardLicenseHeaderHtml.isEmpty()) {
			this.standardLicenseHeaderHtml = null;
		}
		this.standardLicenseHeaderTemplate = fromLicense.getStandardLicenseHeaderTemplate();
		if (Objects.nonNull(this.standardLicenseHeaderTemplate) && this.standardLicenseHeaderTemplate.isEmpty()) {
			this.standardLicenseHeaderTemplate = null;
		}
		this.standardLicenseTemplate = fromLicense.getStandardLicenseTemplate();
		if (Objects.nonNull(this.standardLicenseTemplate) && this.standardLicenseTemplate.isEmpty()) {
			this.standardLicenseTemplate = null;
		}
		this.crossRef.clear();
		for (CrossRef crossRef:fromLicense.getCrossRef()) {
			this.crossRef.add(new CrossRefJson(crossRef));
		}
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
		case "seeAlso":
		case "crossRef": return false;
		case "isOsiApproved":
		case "isFsfLibre":
		case "isDeprecatedLicenseId": return Boolean.class.isAssignableFrom(clazz);
		default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
	}

	}

	public boolean isCollectionMembersAssignableTo(String propertyName, Class<?> clazz) {
		if (SpdxConstants.RDFS_PROP_SEE_ALSO.getName().equals(propertyName)) {
			return String.class.isAssignableFrom(clazz);
		} else if (SpdxConstants.PROP_CROSS_REF.getName().equals(propertyName)) {
			return CrossRef.class.isAssignableFrom(clazz);
		} else {
			return false;
		}
	}

	public boolean isCollectionProperty(String propertyName) {
		return SpdxConstants.RDFS_PROP_SEE_ALSO.getName().equals(propertyName) || 
				SpdxConstants.PROP_CROSS_REF.getName().equals(propertyName);
	}
}
