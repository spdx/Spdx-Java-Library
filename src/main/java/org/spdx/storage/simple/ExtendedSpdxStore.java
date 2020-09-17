/**
 * Copyright (c) 2020 Source Auditor Inc.
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
package org.spdx.storage.simple;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.TypedValue;
import org.spdx.storage.IModelStore;

/**
 * A simple abstract SPDX store that stores everything in an underlying model store which is initialized in the
 * constructor.  
 * 
 * This class can be useful for subclassing and overriding specific methods and/or implementing serialization
 * with a choice of underlying message stores.
 * 
 * @author Gary O'Neall
 *
 */
public abstract class ExtendedSpdxStore implements IModelStore {
	
	private IModelStore baseStore;
	
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
	public boolean exists(String documentUri, String id) {
		return baseStore.exists(documentUri, id);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#create(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void create(String documentUri, String id, String type) throws InvalidSPDXAnalysisException {
		baseStore.create(documentUri, id, type);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getPropertyValueNames(java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> getPropertyValueNames(String documentUri, String id) throws InvalidSPDXAnalysisException {
		return baseStore.getPropertyValueNames(documentUri, id);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#setValue(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setValue(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		baseStore.setValue(documentUri, id, propertyName, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<Object> getValue(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		return baseStore.getValue(documentUri, id, propertyName);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)
	 */
	@Override
	public String getNextId(IdType idType, String documentUri) throws InvalidSPDXAnalysisException {
		return baseStore.getNextId(idType, documentUri);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#removeProperty(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void removeProperty(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		baseStore.removeProperty(documentUri, id, propertyName);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getDocumentUris()
	 */
	@Override
	public List<String> getDocumentUris() {
		return baseStore.getDocumentUris();
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
	public IModelStoreLock enterCriticalSection(String documentUri, boolean readLockRequested)
			throws InvalidSPDXAnalysisException {
		return baseStore.enterCriticalSection(documentUri, readLockRequested);
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
	public boolean removeValueFromCollection(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.removeValueFromCollection(documentUri, id, propertyName, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#collectionSize(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int collectionSize(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		return baseStore.collectionSize(documentUri, id, propertyName);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#collectionContains(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean collectionContains(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.collectionContains(documentUri, id, propertyName, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#clearValueCollection(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void clearValueCollection(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		baseStore.clearValueCollection(documentUri, id, propertyName);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#addValueToCollection(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean addValueToCollection(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.addValueToCollection(documentUri, id, propertyName, value);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#listValues(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Iterator<Object> listValues(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		return baseStore.listValues(documentUri, id, propertyName);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isCollectionMembersAssignableTo(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean isCollectionMembersAssignableTo(String documentUri, String id, String propertyName, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return baseStore.isCollectionMembersAssignableTo(documentUri, id, propertyName, clazz);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isPropertyValueAssignableTo(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean isPropertyValueAssignableTo(String documentUri, String id, String propertyName, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return baseStore.isPropertyValueAssignableTo(documentUri, id, propertyName, clazz);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#isCollectionProperty(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isCollectionProperty(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		return baseStore.isCollectionProperty(documentUri, id, propertyName);
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getIdType(java.lang.String)
	 */
	@Override
	public IdType getIdType(String id) {
		return baseStore.getIdType(id);
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
	public Optional<TypedValue> getTypedValue(String documentUri, String id) throws InvalidSPDXAnalysisException {
		return baseStore.getTypedValue(documentUri, id);
	}
	
	/**
	 * Clear all values for the document
	 * @param documentUri
	 * @throws InvalidSPDXAnalysisException
	 */
	protected void clear(String documentUri) throws InvalidSPDXAnalysisException {
		IModelStoreLock lock = this.enterCriticalSection(documentUri, false);
		try {
			for (TypedValue item:this.getAllItems(documentUri, null).collect(Collectors.toList())) {
				for (String propertyName:this.getPropertyValueNames(documentUri, item.getId())) {
					this.removeProperty(documentUri, item.getId(), propertyName);
				}
			}
		} finally {
			this.leaveCriticalSection(lock);
		}
		
	}
	
	@Override
	public void delete(String documentUri, String elementId) throws InvalidSPDXAnalysisException {
		baseStore.delete(documentUri, elementId);
	}
	
	@Override
	public void close() throws Exception {
		baseStore.close();
	}

}
