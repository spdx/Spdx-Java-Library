/**
 * SPDX-FileCopyrightText: Copyright (c) 2019 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import java.util.Optional;
import java.util.Set;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.InvalidSpdxPropertyException;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v3_0_1.SpdxConstantsV3;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicenseException;
import org.spdx.storage.PropertyDescriptor;


/**
 * Simple POJO to hold the license exception data loaded from a JSON file
 * <p>
 * Licenses in the JSON format can be found at spdx.org/licenses/[exceptionid].json
 * 
 * @author Gary O'Neall
 */
@SuppressWarnings("unused")
public class ExceptionJson {

	/**
	 * All property descriptors for Exceptions
	 */
	public static final List<PropertyDescriptor> ALL_PROPERTY_DESCRIPTORS;
	
	/**
	 * Map of property descriptors to the value name
	 */
	public static final Map<PropertyDescriptor, String> PROPERTY_DESCRIPTOR_TO_VALUE_NAME;
	
	/**
	 * Properties which are collections
	 */
	static final Set<PropertyDescriptor> COLLECTION_PROPERTIES;
	
	static {
		Map<PropertyDescriptor, String> descriptorsToValue = new HashMap<>();
		Set<PropertyDescriptor> collectionProperties = new HashSet<>();
		descriptorsToValue.put(SpdxConstantsV3.PROP_ADDITION_TEXT, "licenseExceptionText");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT, "licenseExceptionText");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT_HTML, "exceptionTextHtml");
		descriptorsToValue.put(SpdxConstantsV3.PROP_NAME, "name");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_NAME, "name");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID, "licenseExceptionId");
		descriptorsToValue.put(SpdxConstantsV3.PROP_STANDARD_ADDITION_TEMPLATE, "licenseExceptionTemplate");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXCEPTION_TEMPLATE, "licenseExceptionTemplate");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXAMPLE, "example");
		descriptorsToValue.put(SpdxConstantsV3.PROP_IS_DEPRECATED_ADDITION_ID, "isDeprecatedLicenseId");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, "isDeprecatedLicenseId");
		descriptorsToValue.put(SpdxConstantsV3.PROP_DEPRECATED_VERSION, "deprecatedVersion");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LIC_DEPRECATED_VERSION, "deprecatedVersion");
		descriptorsToValue.put(SpdxConstantsV3.PROP_COMMENT, "comment");
		descriptorsToValue.put(SpdxConstantsCompatV2.RDFS_PROP_COMMENT, "comment");
		descriptorsToValue.put(SpdxConstantsV3.PROP_SEE_ALSO, "seeAlso");
		collectionProperties.add(SpdxConstantsV3.PROP_SEE_ALSO);
		descriptorsToValue.put(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, "seeAlso");
		collectionProperties.add(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		
		descriptorsToValue.put(SpdxConstantsV3.PROP_LICENSE_XML, "licenseXml");
		descriptorsToValue.put(SpdxConstantsV3.PROP_OBSOLETED_BY, "obsoletedBy");
		descriptorsToValue.put(SpdxConstantsV3.PROP_LIST_VERSION_ADDED, "listVersionAdded");
		
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
	
	/**
	 * Create an ExceptionJson
	 * @param id license exception ID
	 */
	public ExceptionJson(String id) {
		this.licenseExceptionId = id;
	}
	
	/**
	 * Create an ExceptionJson
	 */
	public ExceptionJson() {
		
	}

	/**
	 * @param propertyName property name
	 * @param valueId ID for the value
	 * @param type SPDX type
	 * @throws InvalidSpdxPropertyException on invalid type for the SPDX property
	 */
	public void setTypedProperty(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
	}

	/**
	 * @param propertyDescriptor descriptor for the property to be set
	 * @param value value to set
	 * @throws InvalidSpdxPropertyException on invalid property
	 */
	public void setPrimitiveValue(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
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

	/**
	 * Clears a list of values for a property
	 * @param propertyDescriptor descriptor for the property
	 * @throws InvalidSpdxPropertyException if it is not a list type
	 */
	public void clearPropertyValueList(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor))) {
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a list type");
		}
		seeAlso.clear();
	}

	/**
	 * @param propertyName Name of the property
	 * @param valueId ID for the value
	 * @param type SPDX type
	 * @throws InvalidSpdxPropertyException on invalid type to add a value
	 */
	public void addValueToList(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @param value Value to set
	 * @return true if the value was added
	 * @throws InvalidSpdxPropertyException on SPDX parsing errors
	 */
	public boolean addPrimitiveValueToList(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor))) {
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a list type");
		}
		if (!(value instanceof String)) {
			throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
		}
		return seeAlso.add((String)value);
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @return list of values associated with the property
	 * @throws InvalidSpdxPropertyException if the propertyDescriptor is not for a list type
	 */
	public List<?> getValueList(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		if ("seeAlso".equals(PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor))) {
			return seeAlso;
		} else if (COLLECTION_PROPERTIES.contains(propertyDescriptor)) {
			return new ArrayList<>();
		} else {		
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a list type");
		}
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @return Value associated with the property or null if no value was set
	 * @throws InvalidSpdxPropertyException if the property descriptor is not valid
	 */
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

	/**
	 * Removes the property
	 * @param propertyDescriptor descriptor for the property
	 * @throws InvalidSpdxPropertyException if the property descriptor is not valid
	 */
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
	
	/**
	 * Copies all properties from an exception
	 * @param fromException exception to copy from
	 * @throws InvalidSPDXAnalysisException on error getting values from the exception
	 */
	public void copyFrom(ListedLicenseException fromException) throws InvalidSPDXAnalysisException {
		Optional<String> comment = fromException.getComment(); 
		this.comment = comment.orElse(null);
		this.deprecatedVersion = fromException.getDeprecatedVersion().orElse(null);
		this.isDeprecatedLicenseId = fromException.getIsDeprecatedAdditionId().orElse(false);
		this.licenseExceptionId = SpdxListedLicenseModelStore.objectUriToLicenseOrExceptionId(fromException.getObjectUri());
		this.licenseExceptionTemplate = fromException.getStandardAdditionTemplate().orElse(null);
		this.licenseExceptionText = fromException.getAdditionText();
		this.name = fromException.getName().orElse(null);
		this.seeAlso = new ArrayList<>(fromException.getSeeAlsos());
		this.obsoletedBy = fromException.getObsoletedBy().orElse(null);
		this.listVersionAdded = fromException.getListVersionAdded().orElse(null);
		this.licenseXml = fromException.getLicenseXml().orElse(null);
	}

	/**
	 * Copies from an SPDX version 2 exception
	 * @param fromException exception to copy from
	 * @throws InvalidSPDXAnalysisException on error getting values from the exception
	 */
	@SuppressWarnings("deprecation")
	public void copyFrom(org.spdx.library.model.v2.license.ListedLicenseException fromException) throws InvalidSPDXAnalysisException {
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
		this.seeAlso = new ArrayList<>(fromException.getSeeAlso());
		this.exceptionTextHtml = fromException.getExceptionTextHtml();
		if (Objects.nonNull(this.exceptionTextHtml) && this.exceptionTextHtml.isEmpty()) {
			this.exceptionTextHtml = null;
		}
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @param value value to remove
	 * @return true if the collection was modified
	 * @throws InvalidSpdxPropertyException if the propertyDescriptor is not valid
	 */
	public boolean removePrimitiveValueToList(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		if (!"seeAlso".equals(PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor))) {
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a list type");
		}
        //noinspection SuspiciousMethodCalls
        return seeAlso.remove(value);
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @param clazz class to test assignability
	 * @return true if the propertyDescriptor can be assigned a value of type clazz
	 * @throws InvalidSpdxPropertyException if the propertyDescriptor is not valid
	 */
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

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @param clazz class to test assignability
	 * @return true if the list associated with the propertyDescriptor have a value added of type clazz
	 */
	public boolean isCollectionMembersAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		if (SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO.equals(propertyDescriptor) ||
				SpdxConstantsV3.PROP_SEE_ALSO.equals(propertyDescriptor)) {
			return String.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTERNAL_REF.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.core.ExternalRef.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_VERIFIED_USING.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.core.IntegrityMethod.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTENSION.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.extension.Extension.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTERNAL_IDENTIFIER.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.core.ExternalIdentifier.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_CREATED_USING.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.core.Tool.class.isAssignableFrom(clazz);
		} else {
			return false;
		}
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @return true if the property represents a collection
	 */
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
