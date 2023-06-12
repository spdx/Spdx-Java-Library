/**
 * Copyright (c) 2023 Source Auditor Inc.
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
package org.spdx.storage.compat.v2;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.SpdxInvalidIdException;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.TypedValue;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;

/**
 * @author gary
 *
 */
public class CompatibleModelStoreWrapper implements IModelStore {
	
	private IModelStore baseStore;
	
	public CompatibleModelStoreWrapper(IModelStore baseStore) {
		Objects.requireNonNull(baseStore, "A base store must be provided for the CompatibileModelStoreWrapper");
		this.baseStore = baseStore;
	}

	@Override
	public void close() throws Exception {
		baseStore.close();
	}

	/**
	 * @param documentUri a nameSpace for the ID
	 * @param objectUri unique ID within the SPDX document
	 * @return true if the objectUri already exists for the documentUri
	 */
	public boolean exists(String documentUri, String id) {
		return exists(documentUriIdToUri(documentUri, id, baseStore));
	}
	
	/**
	 * @param documentUri SPDX v2 Document URI
	 * @param id ID consistent with SPDX v2 spec
	 * @param store store used for the Document URI
	 * @return true if the objectUri already exists for the documentUri
	 */
	public static String documentUriIdToUri(String documentUri, String id, IModelStore store) {
		return documentUriIdToUri(documentUri, id, store.getIdType(id).equals(IdType.Anonymous));
	}
	
	/**
	 * @param documentUri SPDX v2 Document URI
	 * @param id ID consistent with SPDX v2 spec
	 * @param anonymous true of this is an anonymous ID
	 * @return a URI based on the document URI and ID - if anonymous is true, the ID is returned
	 */
	public static String documentUriIdToUri(String documentUri, String id, boolean anonymous) {
		if (anonymous) {
			return id;
		} else if (documentUri.contains("://spdx.org/licenses/"))  {
			return documentUri + id;
		} else {
			return documentUri + "#" + id;
		}
	}
	
	/**
	 * Convenience method to convert an SPDX 2.X style typed value to the current TypedValue
	 * @param documentUri SPDX v2 Document URI
	 * @param id ID consistent with SPDX v2 spec
	 * @param anonymous true of this is an anonymous ID
	 * @param type SPDX type
	 * @return TypedValue with the proper Object URI formed by the documentUri and ID
	 * @throws SpdxInvalidIdException
	 * @throws SpdxInvalidTypeException
	 */
	public static TypedValue typedValueFromDocUri(String documentUri, String id, boolean anonymous, String type) throws SpdxInvalidIdException, SpdxInvalidTypeException {
		return new TypedValue(documentUriIdToUri(documentUri, id, anonymous), type);
	}

	@Override
	public boolean exists(String uri) {
		return baseStore.exists(uri);
	}
	
	/**
	 * @param documentUri SPDX v2 spec document URI
	 * @param objectUri SPDX ID
	 * @param type type
	 * @throws InvalidSPDXAnalysisException
	 */
	public void create(String documentUri, String id, String type)
			throws InvalidSPDXAnalysisException {
		baseStore.create(documentUriIdToUri(documentUri, id, baseStore), type);
	}
	
	@Override
	public void create(String objectUri, String type) throws InvalidSPDXAnalysisException {
		baseStore.create(objectUri, type);
	}

	@Override
	public List<PropertyDescriptor> getPropertyValueDescriptors(
			String objectUri) throws InvalidSPDXAnalysisException {
		return baseStore.getPropertyValueDescriptors(objectUri);
	}
	
	public List<PropertyDescriptor> getPropertyValueDescriptors(
			String documentUri, String id) throws InvalidSPDXAnalysisException {
		return getPropertyValueDescriptors(documentUriIdToUri(documentUri, id, baseStore));
	}
	
	@Override
	public void setValue(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		baseStore.setValue(objectUri, propertyDescriptor, value);
	}

	public void setValue(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		setValue(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, value);
	}

	@Override
	public Optional<Object> getValue(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.getValue(objectUri, propertyDescriptor);
	}
	
	public Optional<Object> getValue(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return getValue(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public String getNextId(IdType idType, String documentUri)
			throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(documentUri, "SPDX V2 requires a namespace for generating next ID's");
		if (documentUri.contains("://spdx.org/licenses")) {
			return baseStore.getNextId(idType, documentUri);
		} else {
			return baseStore.getNextId(idType, documentUri + "#");
		}
	}

	@Override
	public void removeProperty(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		baseStore.removeProperty(objectUri, propertyDescriptor);
	}
	
	public void removeProperty(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		removeProperty(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public Stream<TypedValue> getAllItems(String nameSpace, String typeFilter)
			throws InvalidSPDXAnalysisException {
		return baseStore.getAllItems(nameSpace, typeFilter);
	}

	@Override
	public IModelStoreLock enterCriticalSection(boolean readLockRequested) throws InvalidSPDXAnalysisException {
		return baseStore.enterCriticalSection(readLockRequested);
	}

	public IModelStoreLock enterCriticalSection(String documentUri,
			boolean readLockRequested) throws InvalidSPDXAnalysisException {
		return enterCriticalSection(readLockRequested);
	}

	@Override
	public void leaveCriticalSection(IModelStoreLock lock) {
		baseStore.leaveCriticalSection(lock);
	}

	@Override
	public boolean removeValueFromCollection(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.removeValueFromCollection(objectUri, propertyDescriptor, value);
	}
	
	public boolean removeValueFromCollection(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return removeValueFromCollection(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, value);
	}

	@Override
	public int collectionSize(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.collectionSize(objectUri, propertyDescriptor);
	}
	
	public int collectionSize(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return collectionSize(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public boolean collectionContains(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.collectionContains(objectUri, propertyDescriptor, value);
	}
	
	public boolean collectionContains(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return collectionContains(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, value);
	}

	@Override
	public void clearValueCollection(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		baseStore.clearValueCollection(objectUri, propertyDescriptor);
	}
	
	public void clearValueCollection(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		clearValueCollection(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public boolean addValueToCollection(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.addValueToCollection(objectUri, propertyDescriptor, value);
	}
	
	public boolean addValueToCollection(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return addValueToCollection(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, value);
	}

	@Override
	public Iterator<Object> listValues(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.listValues(objectUri, propertyDescriptor);
	}
	
	public Iterator<Object> listValues(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return listValues(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public boolean isCollectionMembersAssignableTo(String objectUri,
			PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return baseStore.isCollectionMembersAssignableTo(objectUri, propertyDescriptor, clazz);
	}
	
	public boolean isCollectionMembersAssignableTo(String documentUri,
			String id, PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return isCollectionMembersAssignableTo(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, clazz);
	}

	@Override
	public boolean isPropertyValueAssignableTo(String objectUri,
			PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return baseStore.isPropertyValueAssignableTo(objectUri, propertyDescriptor, clazz);
	}
	
	public boolean isPropertyValueAssignableTo(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return isPropertyValueAssignableTo(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, clazz);
	}

	@Override
	public boolean isCollectionProperty(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.isCollectionProperty(objectUri, propertyDescriptor);
	}
	
	public boolean isCollectionProperty(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return isCollectionProperty(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public IdType getIdType(String objectUri) {
		return baseStore.getIdType(objectUri);
	}

	@Override
	public Optional<String> getCaseSensisitiveId(String documentUri,
			String caseInsensisitiveId) {
		return baseStore.getCaseSensisitiveId(documentUri, caseInsensisitiveId);
	}

	@Override
	public Optional<TypedValue> getTypedValue(String objectUri)
			throws InvalidSPDXAnalysisException {
		return baseStore.getTypedValue(objectUri);
	}
	
	public Optional<TypedValue> getTypedValue(String documentUri, String id)
			throws InvalidSPDXAnalysisException {
		return getTypedValue(documentUriIdToUri(documentUri, id, baseStore));
	}

	@Override
	public void delete(String documentUri)
			throws InvalidSPDXAnalysisException {
		baseStore.delete(documentUri);
	}

	public void delete(String documentUri, String id)
			throws InvalidSPDXAnalysisException {
		delete(documentUriIdToUri(documentUri, id, baseStore));
	}

	@Override
	public SpdxMajorVersion getSpdxVersion() {
		return SpdxMajorVersion.VERSION_2;
	}

}
