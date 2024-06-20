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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.ModelRegistryException;
import org.spdx.core.SpdxInvalidIdException;
import org.spdx.core.SpdxInvalidTypeException;
import org.spdx.core.TypedValue;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.core.Agent;
import org.spdx.library.model.v3.core.CreationInfo;
import org.spdx.storage.PropertyDescriptor;

/**
 * @author Gary O'Neall
 * 
 * Storage for the creator agent of the license list
 *
 */
public class LicenseCreatorAgent {
	
	static final String OBJECT_URI_PREFIX = "https://spdx.org/licenses/creatoragent/";
	public static final List<PropertyDescriptor> ALL_PROPERTY_DESCRIPTORS = Collections.unmodifiableList(Arrays.asList(
			SpdxConstantsV3.PROP_CREATION_INFO, SpdxConstantsV3.PROP_NAME, SpdxConstantsV3.PROP_DESCRIPTION));
	static final List<String> EMPTY = Collections.unmodifiableList(new ArrayList<>());
	static final String NAME = "SPDX Legal Team";
	static final String DESCRIPTION = "This object is created and maintained by the SPDX legal team (https://spdx.dev/engage/participate/legal-team/)";
	private String objectUri;
	private TypedValue typedValue;
	private TypedValue creationInfoTV;
	
	public LicenseCreatorAgent(String licenseListVersion) throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		this.objectUri = OBJECT_URI_PREFIX + licenseListVersion.replace('.','_');
		this.typedValue = new TypedValue(objectUri, SpdxConstantsV3.CORE_AGENT, SpdxConstantsV3.MODEL_SPEC_VERSION);
		this.creationInfoTV = new TypedValue(LicenseCreationInfo.CREATION_INFO_URI, SpdxConstantsV3.CORE_CREATION_INFO, SpdxConstantsV3.MODEL_SPEC_VERSION);
	}
	
	public String getObjectUri() {
		return this.objectUri;
	}

	/**
	 * @return
	 */
	public TypedValue getTypedValue() {
		return this.typedValue;
	}

	/**
	 * @param propertyDescriptor
	 * @return
	 */
	public List<?> getValueList(PropertyDescriptor propertyDescriptor) {
		return EMPTY;
	}

	/**
	 * @param propertyDescriptor
	 * @return value if present, otherwise null
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Object getValue(PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		if (SpdxConstantsV3.PROP_CREATION_INFO.equals(propertyDescriptor)) {
			return this.creationInfoTV;
		} else if (SpdxConstantsV3.PROP_NAME.equals(propertyDescriptor)) {
			return NAME;
		} else if (SpdxConstantsV3.PROP_DESCRIPTION.equals(propertyDescriptor)) {
			return DESCRIPTION;
		} else {
			return null;
		}
	}

	/**
	 * @param propertyDescriptor
	 * @param clazz
	 * @return
	 */
	public boolean isCollectionMembersAssignableTo(
			PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		if (SpdxConstantsV3.PROP_EXTERNAL_REF.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.core.ExternalRef.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_VERIFIED_USING.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.core.IntegrityMethod.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTENSION.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.extension.Extension.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_EXTERNAL_IDENTIFIER.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.core.ExternalIdentifier.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_CREATED_USING.equals(propertyDescriptor)) {
			return org.spdx.library.model.v3.core.Tool.class.isAssignableFrom(clazz);
		} else if (SpdxConstantsV3.PROP_CREATED_BY.equals(propertyDescriptor)) {
			return (Agent.class.equals(clazz) || LicenseCreatorAgent.class.equals(clazz));
		} else {
			return false;
		}
	}

	/**
	 * @param propertyDescriptor
	 * @param clazz
	 * @return
	 */
	public boolean isPropertyValueAssignableTo(
			PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		return (String.class.equals(clazz) && 
				(SpdxConstantsV3.PROP_NAME.equals(propertyDescriptor) || 
						SpdxConstantsV3.PROP_DESCRIPTION.equals(propertyDescriptor))) ||
				((CreationInfo.class.equals(clazz) || LicenseCreationInfo.class.equals(clazz))
						&& SpdxConstantsV3.PROP_CREATION_INFO.equals(propertyDescriptor));
	}

	/**
	 * @param propertyDescriptor
	 * @return
	 */
	public boolean isCollectionProperty(PropertyDescriptor propertyDescriptor) {
		return false;
	}

}
