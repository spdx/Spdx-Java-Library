/**
 * Copyright (c) 2020 Source Auditor Inc.
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
package org.spdx.storage.simple;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.TypedValue;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;

/**
 * A simple abstract SPDX store that stores everything in an underlying model store which is initialized in the
 * constructor.  
 * <p>
 * This class can be useful for subclassing and overriding specific methods and/or implementing serialization
 * with a choice of underlying message stores.
 * 
 * @author Gary O'Neall
 */
@SuppressWarnings("unused")
public abstract class ExtendedSpdxStore implements IModelStore {
	
	private final IModelStore baseStore;
	
	/**
	 * @param baseStore store used as the base for this extended SPDX store
	 */
	public ExtendedSpdxStore(IModelStore baseStore) {
		this.baseStore = baseStore;
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#exists(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean exists(String objectUri) {
		return baseStore.exists(objectUri);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#create(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void create(TypedValue typedValue) throws InvalidSPDXAnalysisException {
		baseStore.create(typedValue);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getPropertyValueNames(java.lang.String, java.lang.String)
	 */
	@Override
	public List<PropertyDescriptor> getPropertyValueDescriptors(String objectUri) throws InvalidSPDXAnalysisException {
		return baseStore.getPropertyValueDescriptors(objectUri);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#setValue(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setValue(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		baseStore.setValue(objectUri, propertyDescriptor, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<Object> getValue(String objectUri, PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.getValue(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)
	 */
	@Override
	public String getNextId(IdType idType) throws InvalidSPDXAnalysisException {
		return baseStore.getNextId(idType);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#removeProperty(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void removeProperty(String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		baseStore.removeProperty(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getAllItems(java.lang.String, java.lang.String)
	 */
	@Override
	public Stream<TypedValue> getAllItems(String documentUri, String typeFilter) throws InvalidSPDXAnalysisException {
		return baseStore.getAllItems(documentUri, typeFilter);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#enterCriticalSection(java.lang.String, boolean)
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
	 * @see org.spdx.storage.IModelStore#removeValueFromCollection(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean removeValueFromCollection(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.removeValueFromCollection(objectUri, propertyDescriptor, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#collectionSize(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int collectionSize(String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		return baseStore.collectionSize(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#collectionContains(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean collectionContains(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.collectionContains(objectUri, propertyDescriptor, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#clearValueCollection(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void clearValueCollection(String objectUri, PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		baseStore.clearValueCollection(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#addValueToCollection(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean addValueToCollection(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.addValueToCollection(objectUri, propertyDescriptor, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#listValues(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Iterator<Object> listValues(String objectUri, PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.listValues(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isCollectionMembersAssignableTo(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean isCollectionMembersAssignableTo(String objectUri, PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return baseStore.isCollectionMembersAssignableTo(objectUri, propertyDescriptor, clazz);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isPropertyValueAssignableTo(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean isPropertyValueAssignableTo(String objectUri, PropertyDescriptor propertyDescriptor, 
			Class<?> clazz, String specVersion)	throws InvalidSPDXAnalysisException {
		return baseStore.isPropertyValueAssignableTo(objectUri, propertyDescriptor, clazz, specVersion);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isCollectionProperty(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isCollectionProperty(String objectUri, PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.isCollectionProperty(objectUri, propertyDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getIdType(java.lang.String)
	 */
	@Override
	public IdType getIdType(String id) {
		return baseStore.getIdType(id);
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isAnon(java.lang.String)
	 */
	@Override
	public boolean isAnon(String objectUri) {
		return baseStore.isAnon(objectUri);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getCaseSensisitiveId(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<String> getCaseSensisitiveId(String documentUri, String caseInsensisitiveId) {
		return baseStore.getCaseSensisitiveId(documentUri, caseInsensisitiveId);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getTypedValue(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<TypedValue> getTypedValue(String objectUri) throws InvalidSPDXAnalysisException {
		return baseStore.getTypedValue(objectUri);
	}
	
	/**
	 * Clear all values for the document
	 * @throws InvalidSPDXAnalysisException on errors accessing the store
	 */
	protected void clear() throws InvalidSPDXAnalysisException {
		IModelStoreLock lock = this.enterCriticalSection(false);
		try {
			for (TypedValue item:this.getAllItems(null, null).collect(Collectors.toList())) {
				for (PropertyDescriptor propertyDescriptor:this.getPropertyValueDescriptors(item.getObjectUri())) {
					this.removeProperty(item.getObjectUri(), propertyDescriptor);
				}
				this.delete(item.getObjectUri());
			}
		} finally {
			this.leaveCriticalSection(lock);
		}
		
	}
	
	@Override
	public void delete(String objectUri) throws InvalidSPDXAnalysisException {
		baseStore.delete(objectUri);
	}
	
	@Override
	public void close() throws Exception {
		baseStore.close();
	}
}
