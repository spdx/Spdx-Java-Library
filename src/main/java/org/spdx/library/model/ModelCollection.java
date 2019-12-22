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
package org.spdx.library.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.storage.IModelStore;

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
	private String propertyName;
	
	/**
	 * @param modelStore Storage for the model collection
	 * @param documentUri SPDX Document URI for a document associated with this model collection
	 * @param id ID for this collection - must be unique within the SPDX document
	 * @param clazz The class of the elements to be stored in the collection
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelCollection(IModelStore modelStore, String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore);
		this.modelStore = modelStore;
		Objects.requireNonNull(documentUri);
		this.documentUri = documentUri;
		Objects.requireNonNull(id);
		this.id = id;
		Objects.requireNonNull(propertyName);
		this.propertyName = propertyName;
		if (!modelStore.exists(documentUri, id)) {
			throw new SpdxIdNotFoundException(id+" does not exist in document "+documentUri);
		}
	}

	@Override
	public int size() {
		try {
			return this.modelStore.collectionSize(this.documentUri, this.id, this.propertyName);
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isEmpty() {
		try {
			return this.modelStore.collectionSize(this.documentUri, this.id, this.propertyName) == 0;
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean contains(Object o) {

		try {
			if (o instanceof ModelObject) {
				return this.modelStore.collectionContains(this.documentUri, this.id, this.propertyName, ((ModelObject)o).toTypedValue());
			} else {
				return this.modelStore.collectionContains(this.documentUri, this.id, this.propertyName, o);
			}
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Converts any typed or individual value objects to a ModelObject
	 */
	private Function<Object, Object> checkConvertTypedValue = value -> {
		try {
			return ModelStorageClassConverter.storedObjectToModelObject(value, documentUri, modelStore);
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	};
	
	public List<Object> toImmutableList() {		
		// TODO: Change implementation of the model store to return an iterator rather than a list rather than use the list for all internal
		// functions
		try {
			List<Object> modelStoreList = modelStore.getValueList(documentUri, id, propertyName);
			if (Objects.isNull(modelStoreList)) {
				return Collections.emptyList();
			}
			return (List<Object>) Collections.unmodifiableList(modelStoreList.stream().map(checkConvertTypedValue)
					.collect(Collectors.toList()));
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterator<Object> iterator() {
		return toImmutableList().iterator();
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
			if (element instanceof ModelObject) {
				return modelStore.addValueToCollection(documentUri, id, propertyName, ((ModelObject)element).toTypedValue());
			} else {
				return modelStore.addValueToCollection(documentUri, id, propertyName, element);
			}
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean remove(Object element) {
		try {
			if (element instanceof ModelObject) {
				return modelStore.removeValueFromCollection(documentUri, id, propertyName, ((ModelObject)element).toTypedValue());
			} else {
				return modelStore.removeValueFromCollection(documentUri, id, propertyName, element);
			}
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
			modelStore.clearValueCollection(documentUri, id, propertyName);
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}
}
