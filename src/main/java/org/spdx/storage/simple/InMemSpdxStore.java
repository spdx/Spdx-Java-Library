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
package org.spdx.storage.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.DuplicateSpdxIdException;
import org.spdx.library.model.ModelCollection;
import org.spdx.library.model.SpdxIdInUseException;
import org.spdx.library.model.SpdxIdNotFoundException;
import org.spdx.library.model.TypedValue;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;

/**
 * @author Gary O'Neall
 *
 * In memory implementation of an SPDX store.
 *
 * This implementation primarily uses <code>ConcurrentHashMaps</code>.
 *
 * It is designed to be thread-safe and low CPU utilization.  It may use significant amounts of memory
 * for larger SPDX documents.
 *
 */
public class InMemSpdxStore implements IModelStore {

	static final Logger logger = LoggerFactory.getLogger(InMemSpdxStore.class.getName());

	static final String GENERATED = "gnrtd";
	public static Pattern LICENSE_ID_PATTERN_GENERATED =
			Pattern.compile(SpdxConstants.NON_STD_LICENSE_ID_PRENUM+GENERATED+"(\\d+)$");	// Pattern for generated license IDs

	static Pattern DOCUMENT_ID_PATTERN_GENERATED = Pattern.compile(SpdxConstants.EXTERNAL_DOC_REF_PRENUM+GENERATED+"(\\d+)$");
	static Pattern SPDX_ID_PATTERN_GENERATED = Pattern.compile(SpdxConstants.SPDX_ELEMENT_REF_PRENUM+GENERATED+"(\\d+)$");
	static final String ANON_PREFIX = "__anon__";
	static Pattern ANON_ID_PATTERN_GENERATED = Pattern.compile(ANON_PREFIX+GENERATED+"(\\d+)$");
	private static final Set<String> LITERAL_VALUE_SET = new HashSet<String>(Arrays.asList(SpdxConstants.LITERAL_VALUES));

	/**
	 * Map of Document URI to items stored in the document.  The key for the items map is the lowercase of the item ID.
	 */
	protected Map<String, Map<String, StoredTypedItem>> documentValues = Collections.synchronizedMap(new LinkedHashMap<>());
	private int nextNextLicenseId = 0;
	private int nextNextDocumentId = 0;
	private int nextNextSpdxId = 0;
	private int nextAnonId = 0;

	private final ReadWriteLock transactionLock = new ReentrantReadWriteLock();
	private final ReadWriteLock referenceCountLock = new ReentrantReadWriteLock();

	private final IModelStoreLock readLock = new IModelStoreLock() {

		@Override
		public void unlock() {
			transactionLock.readLock().unlock();
		}

	};

	private final IModelStoreLock writeLock = new IModelStoreLock() {

		@Override
		public void unlock() {
			transactionLock.writeLock().unlock();
		}

	};

	@Override
	public boolean exists(String documentUri, String id) {
		Map<String, StoredTypedItem> idMap = documentValues.get(documentUri);
		if (idMap == null) {
			return false;
		}
		return idMap.containsKey(id.toLowerCase());
	}

	@Override
	public void create(String documentUri, String id, String type) throws InvalidSPDXAnalysisException {
		StoredTypedItem value = new StoredTypedItem(documentUri, id, type);
		Map<String, StoredTypedItem> idMap = documentValues.get(documentUri);
		while (idMap == null) {
			idMap = documentValues.putIfAbsent(documentUri, Collections.synchronizedMap(new LinkedHashMap<String, StoredTypedItem>()));
		}
		updateNextIds(id);
		if (Objects.nonNull(idMap.putIfAbsent(id.toLowerCase(), value))) {
			throw new DuplicateSpdxIdException("ID "+id+" already exists.");
		}
	}

	/**
	 * Check to see if the next ID indexes need to be updated based on the name provided
	 * @param id
	 */
	void updateNextIds(String id) {
		if (id == null) {
			return;
		}
		Matcher licenseRefMatcher = LICENSE_ID_PATTERN_GENERATED.matcher(id);
		if (licenseRefMatcher.matches()) {
			checkUpdateNextLicenseId(licenseRefMatcher);
			return;
		}
		Matcher documentRefMatcher = DOCUMENT_ID_PATTERN_GENERATED.matcher(id);
		if (documentRefMatcher.matches()) {
			checkUpdateNextDocumentId(documentRefMatcher);
			return;
		}
		Matcher spdxRefMatcher = SPDX_ID_PATTERN_GENERATED.matcher(id);
		if (spdxRefMatcher.matches()) {
			checkUpdateNextSpdxId(spdxRefMatcher);
			return;
		}
		Matcher anonRefMatcher = ANON_ID_PATTERN_GENERATED.matcher(id);
		if (anonRefMatcher.matches()) {
			checkUpdateNextAnonId(anonRefMatcher);
			return;
		}
	}

	private synchronized void checkUpdateNextAnonId(Matcher anonRefMatcher) {
		String strNum = anonRefMatcher.group(1);
		int num = Integer.parseInt(strNum);
		if (num >= this.nextAnonId ) {
			this.nextAnonId = num + 1;
		}
	}

	private synchronized void checkUpdateNextSpdxId(Matcher spdxRefMatcher) {
		String strNum = spdxRefMatcher.group(1);
		int num = Integer.parseInt(strNum);
		if (num >= this.nextNextSpdxId) {
			this.nextNextSpdxId = num + 1;
		}
	}

	private synchronized void checkUpdateNextDocumentId(Matcher documentRefMatcher) {
		String strNum = documentRefMatcher.group(1);
		int num = Integer.parseInt(strNum);
		if (num >= this.nextNextDocumentId) {
			this.nextNextDocumentId = num + 1;
		}
	}

	private synchronized void checkUpdateNextLicenseId(Matcher licenseRefMatcher) {
		String strNum = licenseRefMatcher.group(1);
		int num = Integer.parseInt(strNum);
		if (num >= this.nextNextLicenseId) {
			this.nextNextLicenseId = num + 1;
		}
	}

	/**
	 * Gets the item from the hashmap
	 * @param documentUri
	 * @param id
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	protected StoredTypedItem getItem(String documentUri, String id) throws InvalidSPDXAnalysisException {
		Map<String, StoredTypedItem> idMap = documentValues.get(documentUri);
		if (idMap == null) {
			throw new SpdxIdNotFoundException("Document URI "+documentUri+" was not found in the memory store.  The ID must first be created before getting or setting property values.");
		}
		StoredTypedItem item = idMap.get(id.toLowerCase());
		if (item == null) {
			throw new SpdxIdNotFoundException("ID "+id+" was not found in the memory store.  The ID must first be created before getting or setting property values.");
		}
		return item;
	}

	@Override
	public List<String> getPropertyValueNames(String documentUri, String id) throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).getPropertyValueNames();
	}

	@Override
	public void setValue(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		if (value instanceof TypedValue) {
			referenceCountLock.writeLock().lock();
			try {
				StoredTypedItem itemToBeStored = getItem(documentUri, ((TypedValue)value).getId());
				getItem(documentUri, id).setValue(propertyName, value);
				itemToBeStored.incReferenceCount();
			} finally {
				referenceCountLock.writeLock().unlock();
			}
		} else {
			getItem(documentUri, id).setValue(propertyName, value);
		}
	}

	@Override
	public void clearValueCollection(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		referenceCountLock.writeLock().lock();
		try {
			List<StoredTypedItem> removedItems = new ArrayList<>();
			Iterator<Object> iter = getItem(documentUri, id).getValueList(propertyName);
			while (iter.hasNext()) {
				Object nextItem = iter.next();
				if (nextItem instanceof TypedValue) {
					removedItems.add(getItem(documentUri, ((TypedValue)nextItem).getId()));
				}
			}
			getItem(documentUri, id).clearPropertyValueList(propertyName);
			for (StoredTypedItem item:removedItems) {
				item.decReferenceCount();
			}
		} finally {
			referenceCountLock.writeLock().unlock();
		}

	}

	@Override
	public boolean addValueToCollection(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		if (value instanceof TypedValue) {
			referenceCountLock.writeLock().lock();
			try {
				StoredTypedItem itemToBeStored = getItem(documentUri, ((TypedValue)value).getId());
				boolean result = getItem(documentUri, id).addValueToList(propertyName, value);
				itemToBeStored.incReferenceCount();
				return result;
			} finally {
				referenceCountLock.writeLock().unlock();
			}
		} else {
			return getItem(documentUri, id).addValueToList(propertyName, value);
		}
	}


	@Override
	public boolean removeValueFromCollection(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		if (value instanceof TypedValue) {
			referenceCountLock.writeLock().lock();
			try {
				StoredTypedItem itemToBeStored = getItem(documentUri, ((TypedValue)value).getId());
				boolean result = getItem(documentUri, id).removeValueFromList(propertyName, value);
				itemToBeStored.decReferenceCount();
				return result;
			} finally {
				referenceCountLock.writeLock().unlock();
			}
		} else {
			return getItem(documentUri, id).removeValueFromList(propertyName, value);
		}
	}

	@Override
	public Iterator<Object> listValues(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).getValueList(propertyName);
	}

	@Override
	public Optional<Object> getValue(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		StoredTypedItem item = getItem(documentUri, id);
		if (item.isCollectionProperty(propertyName)) {
			logger.warn("Returning a collection for a getValue call for property "+propertyName);
			return Optional.of(new ModelCollection<>(this, documentUri, id, propertyName, null ,null));
		} else {
			return Optional.ofNullable(item.getValue(propertyName));
		}
	}

	@Override
	public synchronized String getNextId(IdType idType, String documentUri) throws InvalidSPDXAnalysisException {
		switch (idType) {
			case Anonymous: return ANON_PREFIX+GENERATED+String.valueOf(nextAnonId++);
			case LicenseRef: return SpdxConstants.NON_STD_LICENSE_ID_PRENUM+GENERATED+String.valueOf(nextNextLicenseId++);
			case DocumentRef: return SpdxConstants.EXTERNAL_DOC_REF_PRENUM+GENERATED+String.valueOf(nextNextDocumentId++);
			case SpdxId: return SpdxConstants.SPDX_ELEMENT_REF_PRENUM+GENERATED+String.valueOf(nextNextSpdxId++);
			case ListedLicense: throw new InvalidSPDXAnalysisException("Can not generate a license ID for a Listed License");
			case Literal: throw new InvalidSPDXAnalysisException("Can not generate a license ID for a Literal");
			default: throw new InvalidSPDXAnalysisException("Unknown ID type for next ID: "+idType.toString());
		}
	}

	@Override
	public void removeProperty(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		referenceCountLock.writeLock().lock();
		try {
			Object itemToBeRemoved = getItem(documentUri, id).getValue(propertyName);
			getItem(documentUri, id).removeProperty(propertyName);
			if (itemToBeRemoved instanceof TypedValue) {
				getItem(documentUri, ((TypedValue)itemToBeRemoved).getId()).decReferenceCount();
			}
		} finally {
			referenceCountLock.writeLock().unlock();
		}
	}

	@Override
	public List<String> getDocumentUris() {
		return Collections.unmodifiableList(new ArrayList<String>(this.documentValues.keySet()));
	}

	@Override
	public Stream<TypedValue> getAllItems(String documentUri, String typeFilter)
			throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(documentUri, "Document URi can not be null");
		List<TypedValue> allItems = new ArrayList<>();
		Map<String, StoredTypedItem> itemMap = this.documentValues.get(documentUri);
		if (Objects.nonNull(itemMap)) {
			Iterator<StoredTypedItem> valueIter = itemMap.values().iterator();
			while (valueIter.hasNext()) {
				StoredTypedItem item = valueIter.next();
				if (Objects.isNull(typeFilter) || typeFilter.equals(item.getType())) {
					allItems.add(item);
				}
			}
		}
		return Collections.unmodifiableList(allItems).stream();
	}

	@Override
	public int collectionSize(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).collectionSize(propertyName);
	}

	@Override
	public boolean collectionContains(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).collectionContains(propertyName, value);
	}

	@Override
	public boolean isCollectionMembersAssignableTo(String documentUri, String id, String propertyName,
			Class<?> clazz) throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).isCollectionMembersAssignableTo(propertyName, clazz);
	}

	@Override
	public boolean isPropertyValueAssignableTo(String documentUri, String id, String propertyName, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).isPropertyValueAssignableTo(propertyName, clazz);
	}

	@Override
	public boolean isCollectionProperty(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).isCollectionProperty(propertyName);
	}

	@Override
	public IdType getIdType(String id) {
		if (id.startsWith(ANON_PREFIX+GENERATED)) {
			return IdType.Anonymous;
		}
		if (id.startsWith(SpdxConstants.NON_STD_LICENSE_ID_PRENUM)) {
			return IdType.LicenseRef;
		}
		if (id.startsWith(SpdxConstants.EXTERNAL_DOC_REF_PRENUM)) {
			return IdType.DocumentRef;
		}
		if (id.startsWith(SpdxConstants.SPDX_ELEMENT_REF_PRENUM)) {
			return IdType.SpdxId;
		}
		if (LITERAL_VALUE_SET.contains(id)) {
			return IdType.Literal;
		}
		if (LicenseInfoFactory.isSpdxListedLicenseId(id) || LicenseInfoFactory.isSpdxListedExceptionId(id)) {
			return IdType.ListedLicense;
		} else {
			return IdType.Unkown;
		}
	}

	@Override
	public IModelStoreLock enterCriticalSection(String documentUri, boolean readLockRequested) {
		if (readLockRequested) {
			this.transactionLock.readLock().lock();
			return readLock;
		} else {
			this.transactionLock.writeLock().lock();
			return writeLock;
		}
	}

	@Override
	public void leaveCriticalSection(IModelStoreLock lock) {
		lock.unlock();
	}

	@Override
	public Optional<String> getCaseSensisitiveId(String documentUri, String caseInsensisitiveId) {
		Map<String, StoredTypedItem> idMap = documentValues.get(documentUri);
		if (Objects.isNull(idMap)) {
			return Optional.empty();
		}
		StoredTypedItem item = idMap.get(caseInsensisitiveId.toLowerCase());
		if (Objects.isNull(item)) {
			return Optional.empty();
		}
		return Optional.of(item.getId());
	}

	@Override
	public Optional<TypedValue> getTypedValue(String documentUri, String id) throws InvalidSPDXAnalysisException {
		try {
			return Optional.of(getItem(documentUri, id));
		} catch(SpdxIdNotFoundException ex) {
			return Optional.empty();
		}
	}

	/**
	 * Remove all existing elements, properties, and values for a document including the document itself
	 * @param documentUri
	 */
	public void clear(String documentUri) {
		Objects.requireNonNull(documentUri, "Document uri can not be null");
		this.documentValues.put(documentUri, new ConcurrentHashMap<String, StoredTypedItem>());
	}

	@Override
	public void delete(String documentUri, String id) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(documentUri, "Missing Document URI");
		Objects.requireNonNull(id, "Missing ID");
		Map<String, StoredTypedItem> idMap = documentValues.get(documentUri);
		if (Objects.isNull(idMap)) {
			logger.error("Error deleting - documentUri "+documentUri+" does not exits.");
			throw new SpdxIdNotFoundException("Error deleting - documentUri "+documentUri+" does not exits.");
		}
		referenceCountLock.writeLock().lock();
		try {
			if (getItem(documentUri, id).getReferenceCount() > 0) {
				// find the element it is used by
				logger.error("Can not delete ID "+id+".  It is in use");
				throw new SpdxIdInUseException("Can not delete ID "+id+".  It is in use.");
			}
			List<String> propertyNames = this.getPropertyValueNames(documentUri, id);
			for (String property:propertyNames) {
				if (this.isCollectionProperty(documentUri, id, property)) {
					Iterator<Object> iter = this.listValues(documentUri, id, property);
					while (iter.hasNext()) {
						Object val = iter.next();
						if (val instanceof TypedValue) {
							getItem(documentUri, ((TypedValue)val).getId()).decReferenceCount();
						}
					}
				} else {
					Optional<Object> val = getValue(documentUri, id, property);
					if (val.isPresent()) {
						if (val.get() instanceof TypedValue) {
							getItem(documentUri, ((TypedValue)val.get()).getId()).decReferenceCount();
						}
					}
				}
			}
			if (Objects.isNull(idMap.remove(id.toLowerCase()))) {
				logger.error("Error deleting - ID "+id+" does not exist.");
				throw new SpdxIdNotFoundException("Error deleting - ID "+id+" does not exist.");
			}
		} finally {
			referenceCountLock.writeLock().unlock();
		}
	}

	@Override
	public void close() throws Exception {
		// Nothing to do for the in-memory store
	}
}
