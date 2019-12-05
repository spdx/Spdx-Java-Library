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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
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
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SpdxIdNotFoundException;
import org.spdx.library.model.SpdxModelFactory;
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
	
	static Pattern DOCUMENT_ID_PATTERN_NUMERIC = Pattern.compile(SpdxConstants.EXTERNAL_DOC_REF_PRENUM+"(\\d+)$");
	static Pattern SPDX_ID_PATTERN_NUMERIC = Pattern.compile(SpdxConstants.SPDX_ELEMENT_REF_PRENUM+"(\\d+)$");
	static final String ANON_PREFIX = "__anon__";
	static Pattern ANON_ID_PATTERN_NUMERIC = Pattern.compile(ANON_PREFIX+"(\\d+)$");
	
	/**
	 * Map of Document URI to items stored in the document
	 */
	ConcurrentHashMap<String, ConcurrentHashMap<String, StoredTypedItem>> documentValues = new ConcurrentHashMap<>();
	private int nextNextLicenseId = 0;
	private int nextNextDocumentId = 0;
	private int nextNextSpdxId = 0;
	private int nextAnonId = 0;
	
	private final ReadWriteLock transactionLock = new ReentrantReadWriteLock();
	
	public class InMemStoreTransaction implements ModelTransaction {

		Lock lock;
		@Override
		public void begin(ReadWrite readWrite) throws IOException {
			Objects.requireNonNull(readWrite);
			if (ReadWrite.READ == readWrite) {
				lock = transactionLock.readLock();
			} else {
				lock = transactionLock.writeLock();
			}
			lock.lock();
		}

		@Override
		public void commit() throws IOException {
			if (lock != null) {
				lock.unlock();
				lock = null;
			}
		}

		@Override
		public void close() throws IOException {
			if (lock != null) {
				logger.warn("Uncommitted transaction");
				lock.unlock();
			}
		}
	}

	@Override
	public boolean exists(String documentUri, String id) {
		ConcurrentHashMap<String, StoredTypedItem> idMap = documentValues.get(documentUri);
		if (idMap == null) {
			return false;
		}
		return idMap.containsKey(id);
	}

	@Override
	public void create(String documentUri, String id, String type) throws InvalidSPDXAnalysisException {
		StoredTypedItem value = new StoredTypedItem(documentUri, id, type);
		ConcurrentHashMap<String, StoredTypedItem> idMap = documentValues.get(documentUri);
		while (idMap == null) {
			idMap = documentValues.putIfAbsent(documentUri, new ConcurrentHashMap<String, StoredTypedItem>());
		}
		updateNextIds(id);
		idMap.putIfAbsent(id, value);
		Object checkWhatWasPut = idMap.get(id);
		if (!value.equals(checkWhatWasPut)) {
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
		Matcher licenseRefMatcher = SpdxConstants.LICENSE_ID_PATTERN_NUMERIC.matcher(id);
		if (licenseRefMatcher.matches()) {
			checkUpdateNextLicenseId(licenseRefMatcher);
			return;
		}
		Matcher documentRefMatcher = DOCUMENT_ID_PATTERN_NUMERIC.matcher(id);
		if (documentRefMatcher.matches()) {
			checkUpdateNextDocumentId(documentRefMatcher);
			return;
		}
		Matcher spdxRefMatcher = SPDX_ID_PATTERN_NUMERIC.matcher(id);
		if (spdxRefMatcher.matches()) {
			checkUpdateNextSpdxId(spdxRefMatcher);
			return;
		}
		Matcher anonRefMatcher = ANON_ID_PATTERN_NUMERIC.matcher(id);
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

	private StoredTypedItem getItem(String documentUri, String id) throws InvalidSPDXAnalysisException {
		ConcurrentHashMap<String, StoredTypedItem> idMap = documentValues.get(documentUri);
		if (idMap == null) {
			throw new SpdxIdNotFoundException("Document URI "+documentUri+" was not found in the memory store.  The ID must first be created before getting property values.");
		}
		StoredTypedItem item = idMap.get(id);
		if (item == null) {
			throw new SpdxIdNotFoundException("ID "+id+" was not found in the memory store.  The ID must first be created before getting property values.");
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
		getItem(documentUri, id).setValue(propertyName, value);
	}

	@Override
	public void clearValueCollection(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		getItem(documentUri, id).clearPropertyValueList(propertyName);
	}

	@Override
	public boolean addValueToCollection(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).addValueToList(propertyName, value);
	}
	

	@Override
	public boolean removeValueFromCollection(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).removeValueFromList(propertyName, value);
	}

	@Override
	public List<Object> getValueList(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		return getItem(documentUri, id).getValueList(propertyName);
	}

	@Override
	public Optional<Object> getValue(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		StoredTypedItem item = getItem(documentUri, id);
		if (item.isCollectionProperty(propertyName)) {
			return Optional.of(new ModelCollection<>(this, documentUri, id, propertyName));
		} else {
			return Optional.ofNullable(item.getValue(propertyName));
		}
	}

	@Override
	public synchronized String getNextId(IdType idType, String documentUri) throws InvalidSPDXAnalysisException {
		switch (idType) {
		case Anonomous: return ANON_PREFIX+String.valueOf(nextAnonId++);
		case LicenseRef: return SpdxConstants.NON_STD_LICENSE_ID_PRENUM+String.valueOf(nextNextLicenseId++);
		case DocumentRef: return SpdxConstants.EXTERNAL_DOC_REF_PRENUM+String.valueOf(nextNextDocumentId++);
		case SpdxId: return SpdxConstants.SPDX_ELEMENT_REF_PRENUM+String.valueOf(nextNextSpdxId++);
		case ListedLicense: throw new InvalidSPDXAnalysisException("Can not generate a license ID for a Listed License");
		case Literal: throw new InvalidSPDXAnalysisException("Can not generate a license ID for a Literal");
		default: throw new InvalidSPDXAnalysisException("Unknown ID type for next ID: "+idType.toString());
		}
	}

	@Override
	public void removeProperty(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		getItem(documentUri, id).removeProperty(propertyName);
	}

	@Override
	public List<String> getDocumentUris() {
		return Collections.unmodifiableList(new ArrayList<String>(this.documentValues.keySet()));
	}

	@Override
	public Stream<? extends ModelObject> getAllItems(String documentUri, Optional<String> typeFilter)
			throws InvalidSPDXAnalysisException {
		List<ModelObject> allItems = new ArrayList<>();
		Iterator<ConcurrentHashMap<String, StoredTypedItem>> docIter = this.documentValues.values().iterator();
		while (docIter.hasNext()) {
			ConcurrentHashMap<String, StoredTypedItem> itemMap = docIter.next();
			Iterator<StoredTypedItem> valueIter = itemMap.values().iterator();
			while (valueIter.hasNext()) {
				StoredTypedItem item = valueIter.next();
				allItems.add(SpdxModelFactory.createModelObject(this, item.getDocumentUri(), item.getId(), item.getType()));
			}
		}
		return Collections.unmodifiableList(allItems).stream();
	}

	@Override
	public ModelTransaction beginTransaction(ReadWrite readWrite) throws IOException {
		InMemStoreTransaction transaction = new InMemStoreTransaction();
		transaction.begin(readWrite);
		return transaction;
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

}
