/**
 * SPDX-FileCopyrightText: Copyright (c) 2024 Source Auditor Inc.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.TypedValue;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v3_0_1.SpdxConstantsV3;
import org.spdx.library.model.v3_0_1.core.CreationInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;

/**
 * Model store for generating SPDX version 3.X listed license and listed exceptions
 *
 * @author Gary O'Neall
 */
public class SpdxV3ListedLicenseModelStore implements IModelStore {
	
	
	static Set<PropertyDescriptor> SUPPORTED_V3_DESCRIPTORS = new HashSet<>();
	
	static {
		// Licenses
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_LICENSE_TEXT);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_NAME);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_CREATION_INFO);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_STANDARD_LICENSE_HEADER);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_IS_OSI_APPROVED);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_IS_FSF_LIBRE);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_IS_DEPRECATED_LICENSE_ID);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_DEPRECATED_VERSION);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_COMMENT);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_SEE_ALSO);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_STANDARD_LICENSE_TEMPLATE);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_LICENSE_XML);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_OBSOLETED_BY);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_LIST_VERSION_ADDED);
		// Exceptions
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_ADDITION_TEXT);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_STANDARD_ADDITION_TEMPLATE);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_IS_DEPRECATED_ADDITION_ID);
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_DEPRECATED_VERSION);
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_COMMENT);
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_SEE_ALSO);
		
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_LICENSE_XML);
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_OBSOLETED_BY);
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_LIST_VERSION_ADDED);
		
		// CreationInfo
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_COMMENT);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_CREATED);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_CREATED_BY);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_SPEC_VERSION);
		
		// Agent
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_CREATION_INFO);
		// SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_NAME);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_DESCRIPTION);
		
		// Collections created on initialization
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_EXTERNAL_REF);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_VERIFIED_USING);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_EXTENSION);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_EXTERNAL_IDENTIFIER);
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_CREATED_USING);
		
		// Unused properties used
		SUPPORTED_V3_DESCRIPTORS.add(SpdxConstantsV3.PROP_SUMMARY);
	}

	IListedLicenseStore baseStore;
	
	/**
	 * @param baseStore store used for the JSON objects containing the license data
	 */
	public SpdxV3ListedLicenseModelStore(IListedLicenseStore baseStore) {
		this.baseStore = baseStore;
	}

	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		baseStore.close();
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String objectUri) {
		return baseStore.exists(objectUri);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#create(org.spdx.core.TypedValue)
	 */
	@Override
	public void create(TypedValue typedValue)
			throws InvalidSPDXAnalysisException {
		if (!typedValue.getSpecVersion().startsWith("3.")) {
			throw new InvalidSPDXAnalysisException("Can not create an SPDX 2.X version using the SPDX V3 listed license model");
		}
		baseStore.create(typedValue);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getPropertyValueDescriptors(java.lang.String)
	 */
	@Override
	public List<PropertyDescriptor> getPropertyValueDescriptors(
			String objectUri) throws InvalidSPDXAnalysisException {
		List<PropertyDescriptor> retval = new ArrayList<>();
		baseStore.getPropertyValueDescriptors(objectUri).forEach(pd -> {
			if (SUPPORTED_V3_DESCRIPTORS.contains(pd)) {
				retval.add(pd);
			}
		});
		retval.add(SpdxConstantsV3.PROP_CREATION_INFO);
		return retval;
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#setValue(java.lang.String, org.spdx.storage.PropertyDescriptor, java.lang.Object)
	 */
	@Override
	public void setValue(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		baseStore.setValue(objectUri, propertyDescriptor, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getValue(java.lang.String, org.spdx.storage.PropertyDescriptor)
	 */
	@Override
	public Optional<Object> getValue(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.getValue(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getNextId(org.spdx.storage.IModelStore.IdType)
	 */
	@Override
	public String getNextId(IdType idType) throws InvalidSPDXAnalysisException {
		return baseStore.getNextId(idType);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#removeProperty(java.lang.String, org.spdx.storage.PropertyDescriptor)
	 */
	@Override
	public void removeProperty(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		baseStore.removeProperty(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getAllItems(java.lang.String, java.lang.String)
	 */
	@Override
	public Stream<TypedValue> getAllItems(@Nullable String nameSpace, @Nullable String typeFilter)
			throws InvalidSPDXAnalysisException {
		return baseStore.getAllItems(nameSpace, typeFilter).filter(tv -> tv.getSpecVersion().startsWith("3."));
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#enterCriticalSection(boolean)
	 */
	@Override
	public IModelStoreLock enterCriticalSection(boolean readLockRequested)
			throws InvalidSPDXAnalysisException {
		return baseStore.enterCriticalSection(readLockRequested);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#leaveCriticalSection(org.spdx.storage.IModelStore.IModelStoreLock)
	 */
	@Override
	public void leaveCriticalSection(IModelStoreLock lock) {
		baseStore.leaveCriticalSection(lock);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#removeValueFromCollection(java.lang.String, org.spdx.storage.PropertyDescriptor, java.lang.Object)
	 */
	@Override
	public boolean removeValueFromCollection(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.removeValueFromCollection(objectUri, propertyDescriptor, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#collectionSize(java.lang.String, org.spdx.storage.PropertyDescriptor)
	 */
	@Override
	public int collectionSize(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.collectionSize(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#collectionContains(java.lang.String, org.spdx.storage.PropertyDescriptor, java.lang.Object)
	 */
	@Override
	public boolean collectionContains(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.collectionContains(objectUri, propertyDescriptor, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#clearValueCollection(java.lang.String, org.spdx.storage.PropertyDescriptor)
	 */
	@Override
	public void clearValueCollection(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		baseStore.clearValueCollection(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#addValueToCollection(java.lang.String, org.spdx.storage.PropertyDescriptor, java.lang.Object)
	 */
	@Override
	public boolean addValueToCollection(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.addValueToCollection(objectUri, propertyDescriptor, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#listValues(java.lang.String, org.spdx.storage.PropertyDescriptor)
	 */
	@Override
	public Iterator<Object> listValues(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.listValues(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isCollectionMembersAssignableTo(java.lang.String, org.spdx.storage.PropertyDescriptor, java.lang.Class)
	 */
	@Override
	public boolean isCollectionMembersAssignableTo(String objectUri,
			PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.isCollectionMembersAssignableTo(objectUri, propertyDescriptor, clazz);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isPropertyValueAssignableTo(java.lang.String, org.spdx.storage.PropertyDescriptor, java.lang.Class, java.lang.String)
	 */
	@Override
	public boolean isPropertyValueAssignableTo(String objectUri,
			PropertyDescriptor propertyDescriptor, Class<?> clazz,
			String specVersion) throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.isPropertyValueAssignableTo(objectUri, propertyDescriptor, clazz, specVersion);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isCollectionProperty(java.lang.String, org.spdx.storage.PropertyDescriptor)
	 */
	@Override
	public boolean isCollectionProperty(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		if (!SUPPORTED_V3_DESCRIPTORS.contains(propertyDescriptor)) {
			throw new InvalidSPDXAnalysisException("Unsupported property for SPDX V3 Listed License or Exception "+propertyDescriptor.getName());
		}
		return baseStore.isCollectionProperty(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getIdType(java.lang.String)
	 */
	@Override
	public IdType getIdType(String objectUri) {
		return baseStore.getIdType(objectUri);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getCaseSensitiveId(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<String> getCaseSensitiveId(String nameSpace,
			String caseInsensitiveId) {
		return baseStore.getCaseSensitiveId(nameSpace, caseInsensitiveId);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getTypedValue(java.lang.String)
	 */
	@Override
	public Optional<TypedValue> getTypedValue(String objectUri)
			throws InvalidSPDXAnalysisException {
		Optional<TypedValue> baseTypedValue = baseStore.getTypedValue(objectUri);
		if (!baseTypedValue.isPresent() || baseTypedValue.get().getSpecVersion().startsWith("3.")) {
			return baseTypedValue;
		}
		switch (baseTypedValue.get().getType()) {
			case SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE:
				return Optional.of(new TypedValue(objectUri, SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE, SpdxConstantsV3.MODEL_SPEC_VERSION));
			case SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION:
				return Optional.of(new TypedValue(objectUri, SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION, SpdxConstantsV3.MODEL_SPEC_VERSION));
			case SpdxConstantsV3.CORE_CREATION_INFO:
				return Optional.of(new TypedValue(objectUri, SpdxConstantsV3.CORE_CREATION_INFO, SpdxConstantsV3.MODEL_SPEC_VERSION));
			case SpdxConstantsV3.CORE_AGENT:
				return Optional.of(new TypedValue(objectUri, SpdxConstantsV3.CORE_AGENT, SpdxConstantsV3.MODEL_SPEC_VERSION));
			default: throw new InvalidSPDXAnalysisException("Unsupported type for SPDX V2 listed license model store: "+baseTypedValue.get().getType());
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#delete(java.lang.String)
	 */
	@Override
	public void delete(String objectUri) throws InvalidSPDXAnalysisException {
		baseStore.delete(objectUri);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isAnon(java.lang.String)
	 */
	@Override
	public boolean isAnon(String objectUri) {
		return baseStore.isAnon(objectUri);
	}

	/**
	 * @return the CreationInfo used for all SPDX listed licenses and listed exceptions
	 * @throws InvalidSPDXAnalysisException on error inflating the creation info
	 */
	public CreationInfo getListedLicenseCreationInfo() throws InvalidSPDXAnalysisException {
		return baseStore.getListedLicenseCreationInfo();
	}
}
