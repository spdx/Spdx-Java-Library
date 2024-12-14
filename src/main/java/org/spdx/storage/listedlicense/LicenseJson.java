/**
 * Copyright (c) 2019 Source Auditor Inc.
 * <p>
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
import java.util.Set;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.InvalidSpdxPropertyException;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.CrossRef;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v3_0_1.SpdxConstantsV3;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicense;
import org.spdx.licenseTemplate.InvalidLicenseTemplateException;
import org.spdx.storage.PropertyDescriptor;

/**
 * Simple POJO to hold the license data loaded from a JSON file
 * <p>
 * Licenses in the JSON format can be found at spdx.org/licenses/[licenseid].json
 * 
 * @author Gary O'Neall 
 *
 */
@SuppressWarnings("unused")
public class LicenseJson {
	
	static final Map<PropertyDescriptor, String> PROPERTY_DESCRIPTOR_TO_VALUE_NAME;
	static final List<PropertyDescriptor> ALL_PROPERTY_DESCRIPTORS;
	static final Set<PropertyDescriptor> COLLECTION_PROPERTIES;
	
	static {
		Map<PropertyDescriptor, String> descriptorsToValue = new HashMap<>();
		Set<PropertyDescriptor> collectionProperties = new HashSet<>();
		descriptorsToValue.put(SpdxConstantsV3.PROP_LICENSE_TEXT, "licenseText");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LICENSE_TEXT, "licenseText");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LICENSE_TEXT_HTML, "licenseTextHtml");
		descriptorsToValue.put(SpdxConstantsV3.PROP_NAME, "name");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LICENSE_NAME, "name");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_NAME, "name");
		descriptorsToValue.put(SpdxConstantsV3.PROP_STANDARD_LICENSE_HEADER, "standardLicenseHeader");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_STD_LICENSE_NOTICE, "standardLicenseHeader");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_STD_LICENSE_HEADER_TEMPLATE, "standardLicenseHeaderTemplate");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LICENSE_HEADER_HTML, "standardLicenseHeaderHtml");
		descriptorsToValue.put(SpdxConstantsV3.PROP_STANDARD_LICENSE_TEMPLATE, "standardLicenseTemplate");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_STD_LICENSE_TEMPLATE, "standardLicenseTemplate");
		descriptorsToValue.put(SpdxConstantsV3.PROP_IS_OSI_APPROVED, "isOsiApproved");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_STD_LICENSE_OSI_APPROVED, "isOsiApproved");
		descriptorsToValue.put(SpdxConstantsV3.PROP_IS_FSF_LIBRE, "isFsfLibre");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_STD_LICENSE_FSF_LIBRE, "isFsfLibre");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_EXAMPLE, "example");
		descriptorsToValue.put(SpdxConstantsV3.PROP_IS_DEPRECATED_LICENSE_ID, "isDeprecatedLicenseId");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, "isDeprecatedLicenseId");
		descriptorsToValue.put(SpdxConstantsV3.PROP_DEPRECATED_VERSION, "deprecatedVersion");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LIC_DEPRECATED_VERSION, "deprecatedVersion");
		descriptorsToValue.put(SpdxConstantsV3.PROP_COMMENT, "comment");
		descriptorsToValue.put(SpdxConstantsCompatV2.RDFS_PROP_COMMENT, "comment");
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_LICENSE_ID, "licenseId");
		descriptorsToValue.put(SpdxConstantsV3.PROP_SEE_ALSO, "seeAlso");
		collectionProperties.add(SpdxConstantsV3.PROP_SEE_ALSO);
		descriptorsToValue.put(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, "seeAlso");
		collectionProperties.add(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		descriptorsToValue.put(SpdxConstantsCompatV2.PROP_CROSS_REF, "crossRef");
		collectionProperties.add(SpdxConstantsCompatV2.PROP_CROSS_REF);
		descriptorsToValue.put(SpdxConstantsV3.PROP_LICENSE_XML, "licenseXml");
		descriptorsToValue.put(SpdxConstantsV3.PROP_OBSOLETED_BY, "obsoletedBy");
		descriptorsToValue.put(SpdxConstantsV3.PROP_LIST_VERSION_ADDED, "listVersionAdded");
		descriptorsToValue.put(SpdxConstantsV3.PROP_CREATION_INFO, "creationInfo");
		
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
	String obsoletedBy;
	String licenseXml;
	String listVersionAdded;
	
	public LicenseJson(String id) {
		this.licenseId = id;
	}
	
	public LicenseJson() {
		
	}

	public void setTypedProperty(String propertyName, String valueId, String type) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException("Invalid type for Listed License SPDX Property: "+type);
	}

	public void setPrimativeValue(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor.getName());
		}
		switch (propertyName) {
			case "licenseText":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				licenseText = (String)value;
				break;
			case "licenseTextHtml":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				licenseTextHtml = (String)value;
				break;
			case "name":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				name = (String)value;
				break;
			case "seeAlso":
			case "crossRef": throw new InvalidSpdxPropertyException("Expected list type for "+propertyDescriptor);
			case "standardLicenseHeader":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				standardLicenseHeader = (String)value;
				break;
			case "standardLicenseHeaderTemplate":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				standardLicenseHeaderTemplate = (String)value;
				break;
			case "standardLicenseHeaderHtml":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				standardLicenseHeaderHtml = (String)value;
				break;
			case "standardLicenseTemplate":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				standardLicenseTemplate = (String)value;
				break;
			case "isOsiApproved":
				if (!(value instanceof Boolean)) {
				throw new InvalidSpdxPropertyException("Expected Boolean type for "+propertyDescriptor);
				}
				isOsiApproved = (Boolean)value;
				break;
			case "isFsfLibre":
				if (!(value instanceof Boolean)) {
				throw new InvalidSpdxPropertyException("Expected Boolean type for "+propertyDescriptor);
				}
				isFsfLibre = (Boolean)value;
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
			case "licenseId":
				if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				licenseId = (String)value;
				break;
			case "obsoletedBy": if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				obsoletedBy = (String)value;
				break;
			case "licenseXml": if (!(value instanceof String)) {
				throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
			}
			licenseXml = (String)value;
			break;
			case "listVersionAdded": if (!(value instanceof String)) {
				throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
			}
			listVersionAdded = (String)value;
			break;
			default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
		}
	}

	public void clearPropertyValueList(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor.getName());
		}
		if ("seeAlso".equals(propertyName)) {
			seeAlso.clear();
		} else if ("crossRef".equals(propertyName)) {
			crossRef.clear();
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
		
	}

	/**
	 * Add a cross-reference to a value list
	 * @param propertyDescriptor descriptor for the property
	 * @param value cross ref value to add
	 * @return true as specified by <code>Collections.add</code>
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public boolean addCrossRefValueToList(PropertyDescriptor propertyDescriptor, CrossRefJson value) throws InvalidSPDXAnalysisException {
		if (SpdxConstantsCompatV2.PROP_CROSS_REF.equals(propertyDescriptor)) {
			return crossRef.add(value);
		} else {
			throw new InvalidSpdxPropertyException(propertyDescriptor + "is not a crossRef list type");
		}
	}
	
	/**
	 * Add a primitive value to a value list
	 * @param propertyDescriptor descriptor for the property
	 * @param value value to add to the list
	 * @return true as specified by <code>Collections.add</code>
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public boolean addPrimitiveValueToList(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSPDXAnalysisException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor.getName());
		}
		if ("seeAlso".equals(propertyName)) {
			if (!(value instanceof String)) {
				throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
			}
			return seeAlso.add((String)value);
		} else if (SpdxConstantsCompatV2.PROP_CROSS_REF.getName().equals(propertyName)) {
			if (!(value instanceof CrossRefJson)) {
				throw new InvalidSpdxPropertyException("Expected CrossRefJson type for "+propertyDescriptor);
			}
			return crossRef.add((CrossRefJson)value);
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
	}
	
	@SuppressWarnings("SuspiciousMethodCalls")
    public boolean removePrimitiveValueToList(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor.getName());
		}
		if ("seeAlso".equals(propertyName)) {
			return seeAlso.remove(value);
		} else if ("crossRef".equals(propertyName)) {
			return crossRef.remove(value);
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
	}

	public List<?> getValueList(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			return new ArrayList<>(); // unsupported property
		}
		if ("seeAlso".equals(propertyName)) {
			return seeAlso;
		} else if ("crossRef".equals(propertyName)) {
			return crossRef;
		} else if (COLLECTION_PROPERTIES.contains(propertyDescriptor)) {
			return new ArrayList<>();  // not supported in JSON - just return empty
		} else {
			throw new InvalidSpdxPropertyException(propertyName + "is not a list type");
		}
	}

	public Object getValue(PropertyDescriptor descriptor) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(descriptor);
		if (Objects.isNull(propertyName)) {
			return null; // unsupported property type
		}
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
			case "licenseXml": return licenseXml;
			case "listVersionAdded": return listVersionAdded;
			case "obsoletedBy": return obsoletedBy;
			default: return null; // unsupported property type
		}
	}

	public void removeProperty(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor.getName());
		}
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
		case "licenseXml": licenseXml = null; break;
		case "listVersionAdded": listVersionAdded = null; break;
		case "obsoletedBy": obsoletedBy = null; break;
		default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
	}
	}
	
	/**
	 * @param fromLicense license to copy from
	 * @throws InvalidSPDXAnalysisException on error accessing license properties 
	 */
	public void copyFrom(ListedLicense fromLicense) throws InvalidSPDXAnalysisException {
		this.comment = fromLicense.getComment().orElse(null);
		this.deprecatedVersion = fromLicense.getDeprecatedVersion().orElse(null);
		this.example = null;
		this.isDeprecatedLicenseId = fromLicense.getIsDeprecatedLicenseId().orElse(false);
		this.isFsfLibre = fromLicense.getIsFsfLibre().orElse(null);
		this.licenseText = fromLicense.getLicenseText();
		this.licenseTextHtml = null;
		this.name = fromLicense.getName().orElse(null);
		this.isOsiApproved = fromLicense.getIsOsiApproved().orElse(false);
		this.seeAlso = new ArrayList<>(fromLicense.getSeeAlsos());
		this.standardLicenseHeader = fromLicense.getStandardLicenseHeader().orElse(null);
		this.standardLicenseHeaderHtml = null;
		this.standardLicenseTemplate = fromLicense.getStandardLicenseTemplate().orElse(null);
		this.crossRef.clear();
		this.obsoletedBy = fromLicense.getObsoletedBy().orElse(null);
		this.licenseXml = fromLicense.getLicenseXml().orElse(null);
		this.listVersionAdded = fromLicense.getListVersionAdded().orElse(null);
	}

	public void copyFrom(SpdxListedLicense fromLicense) throws InvalidLicenseTemplateException, InvalidSPDXAnalysisException {
		this.licenseComments = null;
		this.comment = fromLicense.getComment();
		if (Objects.nonNull(this.comment) && this.comment.isEmpty()) {
			this.comment = null;
		}
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
		this.seeAlso = new ArrayList<>(fromLicense.getSeeAlso());
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

	public boolean isPropertyValueAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor.getName());
		}
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
		case "licenseXml":
		case "listVersionAdded":
		case "obsoletedBy":
		case "licenseId": return String.class.isAssignableFrom(clazz);
		case "seeAlso":
		case "crossRef": return false;
		case "isOsiApproved":
		case "isFsfLibre":
		case "isDeprecatedLicenseId": return Boolean.class.isAssignableFrom(clazz);
		default: throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyName);
	}

	}

	public boolean isCollectionMembersAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) throws InvalidSpdxPropertyException {
		String propertyName = PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(propertyDescriptor);
		if (Objects.isNull(propertyName)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor.getName());
		}
		if (SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO.getName().equals(propertyName)) {
			return String.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsCompatV2.PROP_CROSS_REF.getName().equals(propertyName)) {
			return CrossRef.class.isAssignableFrom(clazz);
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

	public boolean isCollectionProperty(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		if (!PROPERTY_DESCRIPTOR_TO_VALUE_NAME.containsKey(propertyDescriptor)) {
			throw new InvalidSpdxPropertyException("Invalid property for SPDX listed license:"+propertyDescriptor.getName());
		}
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
