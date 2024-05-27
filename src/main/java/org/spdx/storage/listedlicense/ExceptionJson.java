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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.InvalidSpdxPropertyException;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.ListedLicenseException;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicenseException;
import org.spdx.storage.PropertyDescriptor;


/**
 * Simple POJO to hold the license exception data loaded from a JSON file
 * 
 * Licenses in the JSON format can be found at spdx.org/licenses/[exceptionid].json
 * 
 * @author Gary O'Neall 
 *
 */
public class ExceptionJson {

	public static final List<PropertyDescriptor> ALL_PROPERTY_DESCRIPTORS;
	public static final Map<PropertyDescriptor, String> PROPERTY_DESCRIPTOR_TO_VALUE_NAME;
	static final Set<PropertyDescriptor> COLLECTION_PROPERTIES;
	
	static {
		Map<PropertyDescriptor, String> descriptorsToValue = new HashMap<>();
		Set<PropertyDescriptor> collectionProperties = new HashSet<>();
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXPANDED_LICENSING_ADDITION_TEXT, "licenseExceptionText");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT, "licenseExceptionText");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT_HTML, "exceptionTextHtml");
		descriptorsToValue.put(SpdxConstantsV3.PROP_NAME, "name");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_NAME, "name");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID, "licenseExceptionId");
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXPANDED_LICENSING_STANDARD_ADDITION_TEMPLATE, "licenseExceptionTemplate");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXCEPTION_TEMPLATE, "licenseExceptionTemplate");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXAMPLE, "example");
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXPANDED_LICENSING_IS_DEPRECATED_ADDITION_ID, "isDeprecatedLicenseId");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, "isDeprecatedLicenseId");
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXPANDED_LICENSING_DEPRECATED_VERSION, "deprecatedVersion");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LIC_DEPRECATED_VERSION, "deprecatedVersion");
		descriptorsToValue.put(SpdxConstantsV3.PROP_COMMENT, "comment");
		descriptorsToValue.put(SpdxConstantsCompatV2.RDFS_PROP_COMMENT, "comment");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID, "licenseExceptionId");
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO, "seeAlso");
		collectionProperties.add(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO);
		descriptorsToValue.put(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, "seeAlso");
		collectionProperties.add(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXPANDED_LICENSING_LICENSE_XML, "licenseXml");
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXPANDED_LICENSING_OBSOLETED_BY, "obsoletedBy");
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXPANDED_LICENSING_LIST_VERSION_ADDED, "listVersionAdded");
		
		// The following are not implemented in the JSON - they are added so there is no errors when creating V3 object classes
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXTERNAL_REF, "externalRef");
		collectionProperties.add(SpdxConstantsV3.PROP_EXTERNAL_REF);
		descriptorsToValue.put(SpdxConstantsV3.PROP_VERIFIED_USING, "verifiedUsing");
		collectionProperties.add(SpdxConstantsV3.PROP_VERIFIED_USING);
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXTENSION, "extension");
		collectionProperties.add(SpdxConstantsV3.PROP_EXTENSION);
		descriptorsToValue.put(SpdxConstantsV3.PROP_EXTERNAL_IDENTIFIER, "externalIdentifier");
		collectionProperties.add(SpdxConstantsV3.PROP_EXTERNAL_IDENTIFIER);
		descriptorsToValue.put(SpdxConstantsV3.PROP_CREATED_USING, "createdUsing");
		collectionProperties.add(SpdxConstantsV3.PROP_CREATED_USING);
		descriptorsToValue.put(SpdxConstantsV3.PROP_DESCRIPTION, "description");
		descriptorsToValue.put(SpdxConstantsV3.PROP_SUMMARY, "summary");
		PROPERTY_DESCRIPTOR_TO_VALUE_NAME = Collections.unmodifiableMap(descriptorsToValue);
		ALL_PROPERTY_DESCRIPTORS = Collections.unmodifiableList(new ArrayList<>(descriptorsToValue.keySet()));
		COLLECTION_PROPERTIES = Collections.unmodifiableSet(collectionProperties);
	}

	Boolean isDeprecatedLicenseId;
	String licenseExceptionText;
	String name;
	String licenseComments;	//TODO:  This is for legacy JSON files - this should be removed in 3.0.  See https://github.com/spdx/spdx-spec/issues/158
	String comment;
	List<String> seeAlso = new ArrayList<>();
	String licenseExceptionId;
	String licenseExceptionTemplate;
	String example;
	String deprecatedVersion;
	String exceptionTextHtml;
	String licenseXml;
	String obsoletedBy;
	String listVersionAdded;
	
	public ExceptionJson(String id) {
		this.licenseExceptionId = id;
	}
	
	public ExceptionJson() {
		
	}

	public void setTypedProperty(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
	}

	public void setPrimativeValue(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed exception:"+propertyDescriptor.getName());
		}
		switch (propertyName) {
			case "licenseExceptionText":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				licenseExceptionText = (String)value;
				break;
			case "name":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				name = (String)value;
				break;
			case "seeAlso":throw new InvalidSpdxPropertyException("Expected list type for "+propertyDescriptor);
			case "licenseExceptionTemplate":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				licenseExceptionTemplate = (String)value;
				break;
			case "example":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				example = (String)value;
				break;
			case "isDeprecatedLicenseId":
				if (!(value instanceof Boolean)) {
				throw new InvalidSpdxPropertyException("Expected Boolean type for "+propertyDescriptor);
				}
				isDeprecatedLicenseId = (Boolean)value;
				break;
			case "deprecatedVersion":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				deprecatedVersion = (String)value;
				break;
			case "comment":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				licenseComments = (String)value;
				comment = (String)value;
				break;
			case "licenseExceptionId":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				licenseExceptionId = (String)value;
				break;
			case "exceptionTextHtml":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				exceptionTextHtml = (String)value;
				break;
			case "obsoletedBy":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				obsoletedBy = (String)value;
				break;
			case "licenseXml":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				licenseXml = (String)value;
				break;
			case "listVersionAdded":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				listVersionAdded = (String)value;
				break;
			default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor);
		}
	}

	public void clearPropertyValueList(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor))) {
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a list type");
		}
		seeAlso.clear();
	}

	public void addValueToList(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
	}

	public boolean addPrimitiveValueToList(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor))) {
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a list type");
		}
		if (!(value instanceof String)) {
			throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
		}
		return seeAlso.add((String)value);
	}

	public List<?> getValueList(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		if ("seeAlso".equals(PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor))) {
			return seeAlso;
		} else if (COLLECTION_PROPERTIES.contains(propertyDescriptor)) {
			return new ArrayList<>();
		} else {		
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a list type");
		}
	}

	public Object getValue(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			return null; // unsupported property
		}
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
			case "exceptionTextHtml": return exceptionTextHtml;
			case "licenseXml": return licenseXml;
			case "obsoletedBy": return obsoletedBy;
			case "listVersionAdded": return listVersionAdded;
			default: return null; // unsupported property
		}
	}

	public void removeProperty(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException  {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed exception:"+propertyDescriptor.getName());
		}
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
		case "exceptionTextHtml": exceptionTextHtml = null; break;
		case "licenseXml": licenseXml = null; break;
		case "obsoletedBy": obsoletedBy = null; break;
		case "listVersionAdded": listVersionAdded = null; break;
		default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor);
	}

	}
	
	public void copyFrom(ExpandedLicensingListedLicenseException fromException) throws InvalidSPDXAnalysisException {
		this.comment = fromException.getComment().orElse(null);
		this.deprecatedVersion = fromException.getExpandedLicensingDeprecatedVersion().orElse(null);
		this.isDeprecatedLicenseId = fromException.getExpandedLicensingIsDeprecatedAdditionId().orElse(false);
		this.licenseExceptionId = SpdxListedLicenseModelStore.objectUriToLicenseOrExceptionId(fromException.getObjectUri());
		this.licenseExceptionTemplate = fromException.getExpandedLicensingStandardAdditionTemplate().orElse(null);
		this.licenseExceptionText = fromException.getExpandedLicensingAdditionText();
		this.name = fromException.getName().orElse(null);
		this.seeAlso = new ArrayList<String>(fromException.getExpandedLicensingSeeAlsos());
		this.obsoletedBy = fromException.getExpandedLicensingObsoletedBy().orElse(null);
		this.listVersionAdded = fromException.getExpandedLicensingListVersionAdded().orElse(null);
		this.licenseXml = fromException.getExpandedLicensingLicenseXml().orElse(null);
	}

	@SuppressWarnings("deprecation")
	public void copyFrom(ListedLicenseException fromException) throws InvalidSPDXAnalysisException {
		this.comment = null;
		this.licenseComments = fromException.getComment();
		if (Objects.nonNull(this.licenseComments) && this.licenseComments.isEmpty()) {
			this.licenseComments = null;
		}
		this.deprecatedVersion = fromException.getDeprecatedVersion();
		if (Objects.nonNull(this.deprecatedVersion) && this.deprecatedVersion.isEmpty()) {
			this.deprecatedVersion = null;
		}
		this.example = fromException.getExample();
		if (Objects.nonNull(this.example) && this.example.isEmpty()) {
			this.example = null;
		}
		this.isDeprecatedLicenseId = fromException.isDeprecated();
		this.licenseExceptionId = fromException.getId();
		this.licenseExceptionTemplate = fromException.getLicenseExceptionTemplate();
		if (Objects.nonNull(this.licenseExceptionTemplate) && this.licenseExceptionTemplate.isEmpty()) {
			this.licenseExceptionTemplate = null;
		}
		this.licenseExceptionText = fromException.getLicenseExceptionText();
		if (Objects.nonNull(this.licenseExceptionText) && this.licenseExceptionText.isEmpty()) {
			this.licenseExceptionText = null;
		}
		this.name = fromException.getName();
		if (Objects.nonNull(this.name) && this.name.isEmpty()) {
			this.name = null;
		}
		this.seeAlso = new ArrayList<String>(fromException.getSeeAlso());
		this.exceptionTextHtml = fromException.getExceptionTextHtml();
		if (Objects.nonNull(this.exceptionTextHtml) && this.exceptionTextHtml.isEmpty()) {
			this.exceptionTextHtml = null;
		}
	}

	public boolean removePrimitiveValueToList(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor))) {
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a list type");
		}
		return seeAlso.remove(value);
	}

	public boolean isPropertyValueAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed exception:"+propertyDescriptor.getName());
		}
		switch (propertyName) {
		case "licenseExceptionText":
		case "name":
		case "licenseExceptionTemplate": 
		case "example": 
		case "comment": 
		case "deprecatedVersion":
		case "exceptionTextHtml":
		case "obsoletedBy":
		case "licenseXml":
		case "listVersionAdded":
		case "licenseExceptionId": return String.class.isAssignableFrom(clazz);
		case "seeAlso": return false;
		case "isDeprecatedLicenseId": return Boolean.class.isAssignableFrom(clazz);
		default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor);
		}
	}

	public boolean isCollectionMembersAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		if (SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO.equals(propertyDescriptor) ||
				SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO.equals(propertyDescriptor)) {
			return String.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTERNAL_REF.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.core.ExternalRef.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_VERIFIED_USING.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.core.IntegrityMethod.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTENSION.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.extension.ExtensionExtension.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTERNAL_IDENTIFIER.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.core.ExternalIdentifier.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_CREATED_USING.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.core.Tool.class.isAssignableFrom(clazz);
		} else {
			return false;
		}
	}

	public boolean isCollectionProperty(PropertyDescriptor propertyDescriptor) {
		return COLLECTION_PROPERTIES.contains(propertyDescriptor);
	}

	/**
	 * @return all present property descriptors
	 */
	public List<PropertyDescriptor> getPropertyValueDescriptors() {
		List<PropertyDescriptor> retval = new ArrayList<>();
		ALL_PROPERTY_DESCRIPTORS.forEach(propDescriptor -> {
			try {
				if (Objects.nonNull(getValue(propDescriptor))) {
					retval.add(propDescriptor);
				}
			} catch (InvalidSpdxPropertyException e) {
				// ignore - assume missing
			}
		});
		return retval;
	}

}
