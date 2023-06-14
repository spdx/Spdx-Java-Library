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
package org.spdx.library.model.compat.v2;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import org.spdx.library.IndividualUriValue;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxIdNotFoundException;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.SpdxObjectNotInStoreException;
import org.spdx.library.model.compat.v2.license.SpdxNoAssertionLicense;
import org.spdx.library.model.compat.v2.license.SpdxNoneLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;
import org.spdx.storage.compat.v2.CompatibleModelStoreWrapper;

/**
 * Collection of elements stored in a ModelStore
 * 
 * @author Gary O'Neall
 *
 */
public class ModelCollection<T extends Object> implements Collection<Object> {

	private IModelStore modelStore;
	private String documentUri;
	private String id;
	private PropertyDescriptor propertyDescriptor;
	private ModelCopyManager copyManager;
	private Class<?> type;
	private boolean licensePrimitiveAssignable;  // If true, NONE and NOASSERTION should be converted to NoneLicense and NoAssertionLicense
	
	class ModelCollectionIterator implements Iterator<Object> {
		
		private Iterator<Object> storageIterator;

		public ModelCollectionIterator(Iterator<Object> storageIterator) {
			this.storageIterator = storageIterator;
		}

		@Override
		public boolean hasNext() {
			return storageIterator.hasNext();
		}

		@Override
		public Object next() {
			return checkConvertTypedValue(storageIterator.next());
		}
		
	}
	
	/**
	 * @param modelStore Storage for the model collection
	 * @param documentUri SPDX Document URI for a document associated with this model collection
	 * @param objectUri ID for this collection - must be unique within the SPDX document
	 * @param propertyDescriptor descriptor for the property use for the model collections
	 * @param copyManager if non-null, use this to copy properties when referenced outside this model store
	 * @param type The class of the elements to be stored in the collection if none, null if not known
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelCollection(IModelStore modelStore, String documentUri, String id, PropertyDescriptor propertyDescriptor,
			@Nullable ModelCopyManager copyManager,
			@Nullable Class<?> type) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore, "Model store can not be null");
		this.modelStore = modelStore;
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		this.documentUri = documentUri;
		Objects.requireNonNull(id, "ID can not be null");
		this.id = id;
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		this.propertyDescriptor = propertyDescriptor;
		this.copyManager = copyManager;
		if (!modelStore.exists(CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, id, modelStore))) {
			throw new SpdxIdNotFoundException(id+" does not exist in document "+documentUri);
		}
		if (Objects.nonNull(type)) {
			this.type = type;
			licensePrimitiveAssignable = type.isAssignableFrom(SpdxNoneLicense.class);
			if (!modelStore.isCollectionMembersAssignableTo(CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, id, modelStore), 
					propertyDescriptor, type)) {
				throw new SpdxInvalidTypeException("Incompatible type for property "+propertyDescriptor+": "+type.toString());
			}
		} else {
			licensePrimitiveAssignable = false;
		}
	}

	@Override
	public int size() {
		try {
			return this.modelStore.collectionSize(
					CompatibleModelStoreWrapper.documentUriIdToUri(this.documentUri, this.id, modelStore), 
					this.propertyDescriptor);
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isEmpty() {
		try {
			return this.modelStore.collectionSize(
					CompatibleModelStoreWrapper.documentUriIdToUri(this.documentUri, this.id, modelStore),
					this.propertyDescriptor) == 0;
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean contains(Object o) {
		try {
			Object storedObject = null;
			try {
				storedObject = ModelStorageClassConverter.modelObjectToStoredObject(o, documentUri, modelStore, copyManager);
			} catch (SpdxObjectNotInStoreException e1) {
				return false;	// The exception is due to the model object not being in the store
			}
			return this.modelStore.collectionContains(
					CompatibleModelStoreWrapper.documentUriIdToUri(this.documentUri, this.id, modelStore),
					this.propertyDescriptor, storedObject);
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Object checkConvertTypedValue(Object value) {
		try {
			Object retval = ModelStorageClassConverter.storedObjectToModelObject(value, documentUri, modelStore, copyManager);
			if (licensePrimitiveAssignable && retval instanceof IndividualUriValue) {
				String uri = ((IndividualUriValue)retval).getIndividualURI();
				if (SpdxConstantsCompatV2.URI_VALUE_NOASSERTION.equals(uri)) {
					retval = new SpdxNoAssertionLicense();
				} else if (SpdxConstantsCompatV2.URI_VALUE_NONE.equals(uri)) {
					retval = new SpdxNoneLicense();
				}
			}
			if (Objects.nonNull(this.type) && !this.type.isAssignableFrom(retval.getClass())) {
				if (retval instanceof IndividualUriValue) {
					throw new SpdxInvalidTypeException("No enumeration was found for URI "+((IndividualUriValue)retval).getIndividualURI()+
							" for type "+type.toString());
				} else {
					throw new SpdxInvalidTypeException("A collection element of type "+retval.getClass().toString()+
							" was found in a collection of type "+type.toString());
				}
			}
			return retval;
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Converts any typed or individual value objects to a ModelObject
	 */
	private Function<Object, Object> checkConvertTypedValue = value -> {
		return checkConvertTypedValue(value);
	};
	
	public List<Object> toImmutableList() {		
		return (List<Object>) Collections.unmodifiableList(StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(this.iterator(), Spliterator.ORDERED), false).map(checkConvertTypedValue)
				.collect(Collectors.toList()));
	}

	@Override
	public Iterator<Object> iterator() {
		try {
			return new ModelCollectionIterator(
					modelStore.listValues(CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, id, modelStore),
							propertyDescriptor));
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object[] toArray() {
		return toImmutableList().toArray();
	}

	@Override
	public <AT> AT[] toArray(AT[] a) {
		return toImmutableList().toArray(a);
	}

	@Override
	public boolean add(Object element) {
		try {
			return modelStore.addValueToCollection(
					CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, id, modelStore), propertyDescriptor, 
					ModelStorageClassConverter.modelObjectToStoredObject(element, documentUri, modelStore, copyManager));
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean remove(Object element) {
		try {
			return modelStore.removeValueFromCollection(CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, id, modelStore),
					propertyDescriptor, ModelStorageClassConverter.modelObjectToStoredObject(element, documentUri, modelStore, null));
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return toImmutableList().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		boolean retval = false;
		Iterator<? extends Object> iter = c.iterator();
		while (iter.hasNext()) {
			if (add(iter.next())) {
				retval = true;
			}
		}
		return retval;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean retval = false;
		Iterator<? extends Object> iter = c.iterator();
		while (iter.hasNext()) {
			if (remove(iter.next())) {
				retval = true;
			}
		}
		return retval;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		List<Object> values = toImmutableList();
		boolean retval = false;
		for (Object value:values) {
			if (!c.contains(value)) {
				if (remove(value)) {
					retval = true;
				}
			}
		}
		return retval;
	}

	@Override
	public void clear() {
		try {
			modelStore.clearValueCollection(CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, id, modelStore), propertyDescriptor);
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the modelStore
	 */
	public IModelStore getModelStore() {
		return modelStore;
	}

	/**
	 * @return the documentUri
	 */
	public String getDocumentUri() {
		return documentUri;
	}

	/**
	 * @return the objectUri
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the propertyDescriptor
	 */
	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}
}
