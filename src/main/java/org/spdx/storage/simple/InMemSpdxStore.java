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
package org.spdx.storage.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.DuplicateSpdxIdException;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.ModelCollection;
import org.spdx.core.SpdxIdInUseException;
import org.spdx.core.SpdxIdNotFoundException;
import org.spdx.core.TypedValue;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;

/**
 * In memory implementation of an SPDX store
 * <p>
 * This implementation primarily uses <code>ConcurrentHashMaps</code>.
 * <p>
 * It is designed to be thread-safe and low CPU utilization.  It may use significant amounts of memory
 * for larger SPDX documents.
 *
 * @author Gary O'Neall
 */
public class InMemSpdxStore implements IModelStore {

	static final Logger logger = LoggerFactory.getLogger(InMemSpdxStore.class.getName());

	static final String GENERATED = "gnrtd";
	/**
	 * Pattern for the generated license ID
	 */
	public static Pattern LICENSE_ID_PATTERN_GENERATED =
			Pattern.compile(".*"+SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+GENERATED+"(\\d+)$");	// Pattern for generated license IDs

	static Pattern DOCUMENT_ID_PATTERN_GENERATED = Pattern.compile(".*"+SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM+GENERATED+"(\\d+)$");
	static Pattern SPDX_ID_PATTERN_GENERATED = Pattern.compile(".*"+SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM+GENERATED+"(\\d+)$");
	/**
	 * Prefix for anonymous ids
	 */
	public static final String ANON_PREFIX = "__anon__";
	static Pattern ANON_ID_PATTERN_GENERATED = Pattern.compile(ANON_PREFIX+GENERATED+"(\\d+)$");

	/**
	 * Map of property object URI's to typed value items
	 */
	protected Map<String, StoredTypedItem> typedValueMap = Collections.synchronizedMap(new LinkedHashMap<>());

	private int nextNextLicenseId = 0;
	private int nextNextDocumentId = 0;
	private int nextNextSpdxId = 0;
	private int nextAnonId = 0;

	private final ReadWriteLock transactionLock = new ReentrantReadWriteLock();
	private final ReadWriteLock referenceCountLock = new ReentrantReadWriteLock();

	private final IModelStoreLock readLock = () -> transactionLock.readLock().unlock();

	private final IModelStoreLock writeLock = () -> transactionLock.writeLock().unlock();
	
	
	public InMemSpdxStore() {

	}
	
	@Override
	public boolean exists(String objectUri) {
		return typedValueMap.containsKey(objectUri.toLowerCase());
	}

	@Override
	public void create(TypedValue typedValue) throws InvalidSPDXAnalysisException {
		StoredTypedItem value = new StoredTypedItem(typedValue.getObjectUri(), typedValue.getType(), typedValue.getSpecVersion());
		updateNextIds(typedValue.getObjectUri());
		if (Objects.nonNull(this.typedValueMap.putIfAbsent(typedValue.getObjectUri().toLowerCase(), value))) {
			throw new DuplicateSpdxIdException("Object URI "+typedValue.getObjectUri()+" already exists.");
		}
	}

	/**
	 * Check to see if the next ID indexes need to be updated based on the name provided
	 * @param objectUri Anonymous or URI ID
	 */
	void updateNextIds(String objectUri) {
		if (objectUri == null) {
			return;
		}
		Matcher licenseRefMatcher = LICENSE_ID_PATTERN_GENERATED.matcher(objectUri);
		if (licenseRefMatcher.matches()) {
			checkUpdateNextLicenseId(licenseRefMatcher);
			return;
		}
		Matcher documentRefMatcher = DOCUMENT_ID_PATTERN_GENERATED.matcher(objectUri);
		if (documentRefMatcher.matches()) {
			checkUpdateNextDocumentId(documentRefMatcher);
			return;
		}
		Matcher spdxRefMatcher = SPDX_ID_PATTERN_GENERATED.matcher(objectUri);
		if (spdxRefMatcher.matches()) {
			checkUpdateNextSpdxId(spdxRefMatcher);
			return;
		}
		Matcher anonRefMatcher = ANON_ID_PATTERN_GENERATED.matcher(objectUri);
		if (anonRefMatcher.matches()) {
			checkUpdateNextAnonId(anonRefMatcher);
            //noinspection UnnecessaryReturnStatement
            return;
		}
	}

	/**
	 * Checks to see if the next generated anon ID needs to be updated and update if needed
	 * @param anonRefMatcher Matcher for generated anon IDs
	 */
	private synchronized void checkUpdateNextAnonId(Matcher anonRefMatcher) {
		String strNum = anonRefMatcher.group(1);
		int num = Integer.parseInt(strNum);
		if (num >= this.nextAnonId ) {
			this.nextAnonId = num + 1;
		}
	}

	/**
	 * Checks to see if the next generated SPDX ID needs to be updated and update if needed
	 * @param spdxRefMatcher Matcher for generated  SPDX IDs
	 */
	private synchronized void checkUpdateNextSpdxId(Matcher spdxRefMatcher) {
		String strNum = spdxRefMatcher.group(1);
		int num = Integer.parseInt(strNum);
		if (num >= this.nextNextSpdxId) {
			this.nextNextSpdxId = num + 1;
		}
	}

	/**
	 * Checks to see if the next generated document ID needs to be updated and update if needed
	 * @param documentRefMatcher Matcher for generated document IDs
	 */
	private synchronized void checkUpdateNextDocumentId(Matcher documentRefMatcher) {
		String strNum = documentRefMatcher.group(1);
		int num = Integer.parseInt(strNum);
		if (num >= this.nextNextDocumentId) {
			this.nextNextDocumentId = num + 1;
		}
	}

	/**
	 * Checks to see if the next generated license ID needs to be updated and update if needed
	 * @param licenseRefMatcher Matcher for generated license IDs
	 */
	private synchronized void checkUpdateNextLicenseId(Matcher licenseRefMatcher) {
		String strNum = licenseRefMatcher.group(1);
		int num = Integer.parseInt(strNum);
		if (num >= this.nextNextLicenseId) {
			this.nextNextLicenseId = num + 1;
		}
	}

	/**
	 * Gets the item from the hashmap
	 * @param objectUri Anonymous or URI ID
	 * @return the item from the hash map
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	protected StoredTypedItem getItem(String objectUri) throws InvalidSPDXAnalysisException {
		StoredTypedItem item = this.typedValueMap.get(objectUri.toLowerCase());
		if (item == null) {
			throw new SpdxIdNotFoundException("Object URI "+objectUri+" was not found in the memory store.  The ID must first be created before getting or setting property values.");
		}
		return item;
	}

	@Override
	public List<PropertyDescriptor> getPropertyValueDescriptors(String objectUri) throws InvalidSPDXAnalysisException {
		return getItem(objectUri).getPropertyValueDescriptors();
	}

	@Override
	public void setValue(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
	    if (value instanceof TypedValue) {
	        referenceCountLock.writeLock().lock();
            try {
                StoredTypedItem itemToBeStored = getItem(((TypedValue)value).getObjectUri());
                getItem(objectUri).setValue(propertyDescriptor, value);
                itemToBeStored.incReferenceCount();
            } finally {
                referenceCountLock.writeLock().unlock();
            }
	    } else {
	        getItem(objectUri).setValue(propertyDescriptor, value);
	    }
	}

	@Override
	public void clearValueCollection(String objectUri, PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
	    referenceCountLock.writeLock().lock();
        try {
            List<StoredTypedItem> removedItems = new ArrayList<>();
            Iterator<Object> iter = getItem(objectUri).getValueList(propertyDescriptor);
            while (iter.hasNext()) {
                Object nextItem = iter.next();
                if (nextItem instanceof TypedValue) {
                    removedItems.add(getItem(((TypedValue)nextItem).getObjectUri()));
                }
            }
            getItem(objectUri).clearPropertyValueList(propertyDescriptor);
            for (StoredTypedItem item:removedItems) {
                item.decReferenceCount();
            }
        } finally {
            referenceCountLock.writeLock().unlock();
        }
	}

	@Override
	public boolean addValueToCollection(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
	    if (value instanceof TypedValue) {
	        referenceCountLock.writeLock().lock();
	        try {
	            StoredTypedItem itemToBeStored = getItem(((TypedValue)value).getObjectUri());
	            boolean result = getItem(objectUri).addValueToList(propertyDescriptor, value);
	            itemToBeStored.incReferenceCount();
	            return result;
	        } finally {
	            referenceCountLock.writeLock().unlock();
	        }
	    } else {
	        return getItem(objectUri).addValueToList(propertyDescriptor, value);
	    }
	}

	@Override
	public boolean removeValueFromCollection(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
	    if (value instanceof TypedValue) {
	        referenceCountLock.writeLock().lock();
            try {
                StoredTypedItem itemToBeStored = getItem(((TypedValue)value).getObjectUri());
                boolean result = getItem(objectUri).removeValueFromList(propertyDescriptor, value);
                itemToBeStored.decReferenceCount();
                return result;
            } finally {
                referenceCountLock.writeLock().unlock();
            }
        } else {
            return getItem(objectUri).removeValueFromList(propertyDescriptor, value);
        }
	}

	@Override
	public Iterator<Object> listValues(String objectUri, PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return getItem(objectUri).getValueList(propertyDescriptor);
	}

	@Override
	public Optional<Object> getValue(String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		StoredTypedItem item = getItem(objectUri);
		if (item.isCollectionProperty(propertyDescriptor)) {
            logger.warn("Returning a collection for a getValue call for property {}", propertyDescriptor.getName());
			return  Optional.of(new ModelCollection<>(this, objectUri, propertyDescriptor, null, null, item.getSpecVersion(), null));
		} else {
			return Optional.ofNullable(item.getValue(propertyDescriptor));
		}
	}

	@Override
	public synchronized String getNextId(IdType idType) throws InvalidSPDXAnalysisException {
		switch (idType) {
			//TODO: Move the compat constants into it's own constants file
			case Anonymous: return ANON_PREFIX+GENERATED+nextAnonId++;
			case LicenseRef: return SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+GENERATED+nextNextLicenseId++;
			case DocumentRef: return SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM+GENERATED+nextNextDocumentId++;
			case SpdxId: return SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM+GENERATED+nextNextSpdxId++;
			case ListedLicense: throw new InvalidSPDXAnalysisException("Can not generate a license ID for a Listed License");
			default: throw new InvalidSPDXAnalysisException("Unknown ID type for next ID: "+ idType);
		}
	}

	@Override
	public void removeProperty(String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
	    referenceCountLock.writeLock().lock();
        try {
            Object itemToBeRemoved = getItem(objectUri).getValue(propertyDescriptor);
            getItem(objectUri).removeProperty(propertyDescriptor);
            if (itemToBeRemoved instanceof TypedValue) {
                getItem(((TypedValue)itemToBeRemoved).getObjectUri()).decReferenceCount();
            }
        } finally {
            referenceCountLock.writeLock().unlock();
        }
	}

	@Override
	public Stream<TypedValue> getAllItems(@Nullable String nameSpace, @Nullable String typeFilter) {
		Iterator<StoredTypedItem> valueIter = typedValueMap.values().iterator();
		List<TypedValue> allItems = new ArrayList<>();
		while (valueIter.hasNext()) {
			StoredTypedItem item = valueIter.next();
			if ((Objects.isNull(typeFilter) || typeFilter.equals(item.getType())) && 
					(Objects.isNull(nameSpace) || item.getObjectUri().startsWith(nameSpace))) {
				allItems.add(item);
			}
		}
		return Collections.unmodifiableList(allItems).stream();
	}

	@Override
	public int collectionSize(String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		return getItem(objectUri).collectionSize(propertyDescriptor);
	}

	@Override
	public boolean collectionContains(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return getItem(objectUri).collectionContains(propertyDescriptor, value);
	}

	@Override
	public boolean isCollectionMembersAssignableTo(String objectUri, PropertyDescriptor propertyDescriptor,
			Class<?> clazz) throws InvalidSPDXAnalysisException {
		return getItem(objectUri).isCollectionMembersAssignableTo(propertyDescriptor, clazz);
	}

	@Override
	public boolean isPropertyValueAssignableTo(String objectUri, PropertyDescriptor propertyDescriptor, Class<?> clazz, String specVersion)
			throws InvalidSPDXAnalysisException {
		return getItem(objectUri).isPropertyValueAssignableTo(propertyDescriptor, clazz, specVersion);
	}

	@Override
	public boolean isCollectionProperty(String objectUri, PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return getItem(objectUri).isCollectionProperty(propertyDescriptor);
	}
	
	@Override
	public boolean isAnon(String objectUri) {
		return objectUri.startsWith(ANON_PREFIX+GENERATED);
	}

	@Override
	public IdType getIdType(String objectUri) {
		if (isAnon(objectUri)) {
			return IdType.Anonymous;
		}
		if (objectUri.contains(SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM)) {
			return IdType.LicenseRef;
		}
		if (objectUri.contains(SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM)) {
			return IdType.DocumentRef;
		}
		if (objectUri.contains(SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM)) {
			return IdType.SpdxId;
		}
		if (objectUri.contains("://spdx.org/licenses/") || LicenseInfoFactory.isSpdxListedLicenseId(objectUri) || LicenseInfoFactory.isSpdxListedExceptionId(objectUri)) {
			return IdType.ListedLicense;
		} else {
			return IdType.Unknown;
		}
	}

	@Override
	public IModelStoreLock enterCriticalSection(boolean readLockRequested) {
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
	public Optional<String> getCaseSensitiveId(String nameSpace, String caseInsensitiveId) {
		Objects.requireNonNull(nameSpace, "Namespace can not be null");
		Objects.requireNonNull(caseInsensitiveId, "CaseInsensitiveId can not be null");
		String objectUri = nameSpace + "#" + caseInsensitiveId;
		StoredTypedItem item = typedValueMap.get(objectUri.toLowerCase());
		if (Objects.isNull(item)) {
			return Optional.empty();
		}
		return Optional.of(item.getObjectUri().substring(nameSpace.length() + 1));
	}

	@Override
	public Optional<TypedValue> getTypedValue(String objectUri) throws InvalidSPDXAnalysisException {
		try {
			return Optional.of(getItem(objectUri));
		} catch(SpdxIdNotFoundException ex) {
			return Optional.empty();
		}
	}

	/**
	 * Remove all existing elements, properties, and values
	 */
	public void clear() {
		this.typedValueMap.clear();
	}

	@Override
	public void delete(String objectUri) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(objectUri, "Missing object URI");
		if (!this.typedValueMap.containsKey(objectUri.toLowerCase())) {
			return;
		}
		referenceCountLock.writeLock().lock();
        try {
            if (getItem(objectUri).getReferenceCount() > 0) {
                // find the element it is used by
                logger.error("Can not object URI {}.  It is in use", objectUri);
                throw new SpdxIdInUseException("Can not object URI "+objectUri+".  It is in use");
            }
            List<PropertyDescriptor> propertyDescriptors = this.getPropertyValueDescriptors(objectUri);
            for (PropertyDescriptor property:propertyDescriptors) {
                if (this.isCollectionProperty(objectUri, property)) {
                    Iterator<Object> iter = this.listValues(objectUri, property);
                    while (iter.hasNext()) {
                        Object val = iter.next();
                        if (val instanceof TypedValue) {
                            getItem(((TypedValue)val).getObjectUri()).decReferenceCount();
                        }
                    }
                } else {
                    Optional<Object> val = getValue(objectUri, property);
                    if (val.isPresent()) {
                        if (val.get() instanceof TypedValue) {
                            getItem(((TypedValue)val.get()).getObjectUri()).decReferenceCount();
                        }
                    }
                }
            }
            if (Objects.isNull(typedValueMap.remove(objectUri.toLowerCase()))) {
                logger.error("Error deleting - object URI {} does not exist.", objectUri);
                throw new SpdxIdNotFoundException("Error deleting - object URI "+objectUri+" does not exist.");
            }
        } finally {
            referenceCountLock.writeLock().unlock();
        }
	}

	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		// Nothing to do for the in-memory store
	}
}
