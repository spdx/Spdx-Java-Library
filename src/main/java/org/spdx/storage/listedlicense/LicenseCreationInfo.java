/**
 * Copyright (c) 2024 Source Auditor Inc.
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.spdx.core.ModelRegistryException;
import org.spdx.core.SpdxInvalidIdException;
import org.spdx.core.SpdxInvalidTypeException;
import org.spdx.core.TypedValue;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v3_0_1.SpdxConstantsV3;
import org.spdx.library.model.v3_0_1.core.Agent;
import org.spdx.storage.PropertyDescriptor;

/**
 * @author Gary O'Neall
 * 
 * Creation information for the listed license store
 *
 */
public class LicenseCreationInfo {

	static final String CREATION_INFO_URI = "__creation_info";
	
	public static final List<PropertyDescriptor> ALL_PROPERTY_DESCRIPTORS;
	public static final Map<PropertyDescriptor, Object> PROPERTY_TO_STATIC_VALUE;
	
	static {
		Map<PropertyDescriptor, Object> ptosv = new HashMap<>();
		ptosv.put(SpdxConstantsV3.PROP_COMMENT, "This is a generated SPDX License object from the SPDX license list");
		ptosv.put(SpdxConstantsV3.PROP_CREATED, null); // this needs to be filled in with the creation date of the license list itself
		ptosv.put(SpdxConstantsV3.PROP_CREATED_BY, null); // this also needs to be created
		ptosv.put(SpdxConstantsV3.PROP_SPEC_VERSION, SpdxConstantsV3.MODEL_SPEC_VERSION);
		PROPERTY_TO_STATIC_VALUE = Collections.unmodifiableMap(ptosv);
		ALL_PROPERTY_DESCRIPTORS = Collections.unmodifiableList(new ArrayList<>(ptosv.keySet()));
	};
	
	private static List<String> EMPTY = Collections.unmodifiableList(new ArrayList<>());
	
	private String created;
	private List<TypedValue> creators;
	
	TypedValue typedValue;

	/**
	 * @param licenseListCreator creator of the license
	 * @param licenseListReleaseDate release date of the license list
	 * @throws ModelRegistryException if there is support for the model version
	 * @throws SpdxInvalidTypeException if the license type is invalid
	 * @throws SpdxInvalidIdException if the license ID is invalid
	 */
	public LicenseCreationInfo(LicenseCreatorAgent licenseListCreator,
			String licenseListReleaseDate) throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		this.created = Pattern.matches("^\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ$", licenseListReleaseDate)  ? 
				licenseListReleaseDate : new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT).format(new Date());
		this.creators = Collections.unmodifiableList(Arrays.asList(licenseListCreator.getTypedValue()));
		this.typedValue = new TypedValue(LicenseCreationInfo.CREATION_INFO_URI, SpdxConstantsV3.CORE_CREATION_INFO, SpdxConstantsV3.MODEL_SPEC_VERSION);
	}
	
	/**
	 * @return the typed value
	 */
	public TypedValue getTypedValue() {
		return this.typedValue;
	}

	/**
	 * @param propertyDescriptor
	 * @return true if it is a collection property
	 */
	public boolean isCollectionProperty(PropertyDescriptor propertyDescriptor) {
		return SpdxConstantsV3.PROP_CREATED_BY.equals(propertyDescriptor);
	}

	/**
	 * @param propertyDescriptor property descriptor for the collection
	 * @return list of values for a collection
	 */
	public List<?> getValueList(PropertyDescriptor propertyDescriptor) {
		if (SpdxConstantsV3.PROP_CREATED_BY.equals(propertyDescriptor)) {
			return creators;
		} else {
			return EMPTY;
		}
	}

	/**
	 * @param propertyDescriptor property descriptor for the collection
	 * @return value if present, otherwise null
	 */
	public Object getValue(PropertyDescriptor propertyDescriptor) {
		if (SpdxConstantsV3.PROP_CREATED_BY.equals(propertyDescriptor)) {
			return creators;
		} else if (SpdxConstantsV3.PROP_CREATED.equals(propertyDescriptor)) {
			return created;
		} else {
			return PROPERTY_TO_STATIC_VALUE.get(propertyDescriptor);
		}
	}

	/**
	 * @param propertyDescriptor property descriptor for the collection
	 * @param clazz class to check
	 * @return true if the property is a collection and a value of type clazz can be assigned
	 */
	public boolean isCollectionMembersAssignableTo(
			PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		if (SpdxConstantsV3.PROP_EXTERNAL_REF.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.core.ExternalRef.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_VERIFIED_USING.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.core.IntegrityMethod.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTENSION.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.extension.Extension.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTERNAL_IDENTIFIER.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.core.ExternalIdentifier.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_CREATED_USING.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3_0_1.core.Tool.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_CREATED_BY.equals(propertyDescriptor)) {
			return (Agent.class.equals(clazz) || LicenseCreatorAgent.class.equals(clazz));
		} else {
			return false;
		}
	}

	/**
	 * @param propertyDescriptor property descriptor for the collection
	 * @param clazz class to check
	 * @return true if a value of type clazz can be assigned to the property
	 */
	public boolean isPropertyValueAssignableTo(
			PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		return String.class.equals(clazz) &&
				(SpdxConstantsV3.PROP_COMMENT.equals(propertyDescriptor) ||
						SpdxConstantsV3.PROP_SPEC_VERSION.equals(propertyDescriptor) ||
						SpdxConstantsV3.PROP_CREATED.equals(propertyDescriptor));
	}
}
