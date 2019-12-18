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

import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * This static helper class converts objects used in the model to and from objects used by the SPI / storage class.
 * 
 * The storage SPI supports the following types:
 * 
 *  <code>String</code>
 *  <code>Boolean</code>
 *  <code>TypedValue</code> - this class is used to store most (if not all) of the ModelStore classes
 *  <code>IndividualUriValue</code> - this interface type represents a value which can be stored as a unique
 *  value.  It is used to represent Enum types in the model and the ExternalSpdxElement model object.  Note that
 *  IndividualUriValues can not have any properties associated with them.
 *  
 * @author Gary O'Neall
 *
 */
public class ModelStorageClassConverter {

	/**
	 * Converts any typed value or individual value objects to a ModelObject, returning an existing ModelObject if it exists or creates a new ModelObject
	 * @param value Value which may be a TypedValue
	 * @param documenentUri Document URI to use when converting a typedValue
	 * @param modelStore ModelStore to use in fetching or creating
	 * @return the object itself unless it is a TypedValue, in which case a ModelObject is returned
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Object storedObjectToModelObject(Object value, String documentUri, IModelStore modelStore) throws InvalidSPDXAnalysisException {
		if (value instanceof IndividualUriValue) {	// Note: this must be before the check for TypedValue
			SimpleUriValue suv = new SimpleUriValue((IndividualUriValue)value);
			return suv.toModelObject(modelStore, documentUri);
		} else if (value instanceof TypedValue) {
			TypedValue tv = (TypedValue)value;
			return SpdxModelFactory.createModelObject(modelStore, documentUri, tv.getId(), tv.getType());
		} else {
			return value;
		}
	};
	
	/**
	 * Converts any typed value or IndividualValue objects to a ModelObject, returning an existing ModelObject if it exists or creates a new ModelObject
	 * @param value Value which may be a TypedValue
	 * @param documenentUri Document URI to use when converting a typedValue
	 * @param stModelStore ModelStore to use in fetching or creating
	 * @return the object itself unless it is a TypedValue, in which case a ModelObject is returned
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Optional<Object> optionalStoredObjectToModelObject(Optional<Object> value, String stDocumentUri, IModelStore stModelStore) throws InvalidSPDXAnalysisException {
		if (value.isPresent() && value.get() instanceof IndividualUriValue) {
			return Optional.of(new SimpleUriValue((IndividualUriValue)value.get()).toModelObject(stModelStore, stDocumentUri));
		} else if (value.isPresent() && value.get() instanceof TypedValue) {
			TypedValue tv = (TypedValue)value.get();
			return Optional.of(SpdxModelFactory.createModelObject(stModelStore, stDocumentUri, tv.getId(), tv.getType()));
		} else {
			return value;
		}
	}
	
	/**
	 * Converts a stored object to it's appropriate model object type
	 * @param value
	 * @param stDocumentUri
	 * @param stModelStore
	 * @return Model Object appropriate type
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Object modelObjectToStoredObject(Object value, String stDocumentUri, IModelStore stModelStore, boolean copyOnReference) throws InvalidSPDXAnalysisException {
		if (value instanceof IndividualUriValue) {
			// Convert to a simple URI value to save storage
			return new SimpleUriValue((IndividualUriValue)value);
		} else if (value instanceof ModelObject) {
			ModelObject mValue = (ModelObject)value;
			if (!mValue.getModelStore().equals(stModelStore)) {
				if (!copyOnReference) {
					throw(new InvalidSPDXAnalysisException("Can set a property value to a Model Object stored in a different model store"));
				}
				if (!stModelStore.exists(stDocumentUri, mValue.getId())) {
					stModelStore.create(stDocumentUri, mValue.getId(), mValue.getType());
				}
				return copy(stModelStore, stDocumentUri, 
						mValue.getModelStore(), mValue.getDocumentUri(), mValue.getId(), mValue.getType());
			} else {
				return mValue.toTypedValue();
			}
		} else if (value instanceof String || value instanceof Boolean || value instanceof IndividualUriValue) {
			return value;
		} else {
			throw new SpdxInvalidTypeException("Property value type not supported: "+value.getClass().getName());
		}
	}

	/**
	 * Copy an item from one Model Object Store to another using the soure ID for the target unless it is anonomous
	 * @param toStore Model Store to copy to
	 * @param toDocumentUri Target document URI
	 * @param fromStore Model Store containing the source item
	 * @param fromDocumentUri Document URI for the source item
	 * @param sourceId ID source ID
	 * @param stType Type to copy
	 * @return ID for the copied object
	 * @throws InvalidSPDXAnalysisException
	 */
	public static TypedValue copy(IModelStore toStore, String toDocumentUri, IModelStore fromStore, String fromDocumentUri, String sourceId, String stType) throws InvalidSPDXAnalysisException {
		String toId = sourceId;
		if (fromStore.getIdType(sourceId).equals(IdType.Anonomous)) {
			toId = toStore.getNextId(IdType.Anonomous, toDocumentUri);
		}
		copy(toStore, toDocumentUri, toId, fromStore, fromDocumentUri, sourceId, stType);
		return new TypedValue(toId, stType);
	}
	
	/**
	 * Copy an item from one Model Object Store to another
	 * @param toStore Model Store to copy to
	 * @param toId Id to use in the copy
	 * @param toDocumentUri Target document URI
	 * @param fromStore Model Store containing the source item
	 * @param fromDocumentUri Document URI for the source item
	 * @param sourceId ID source ID
	 * @param stType Type to copy
	 * @throws InvalidSPDXAnalysisException
	 */
	static void copy(IModelStore toStore, String toDocumentUri, String toId, IModelStore fromStore, String fromDocumentUri, String sourceId, String stType) throws InvalidSPDXAnalysisException {
		if (!toStore.exists(toDocumentUri, toId)) {
			toStore.create(toDocumentUri, toId, stType);
		}
		List<String> propertyNames = fromStore.getPropertyValueNames(fromDocumentUri, sourceId);
		for (String propName:propertyNames) {
			if (fromStore.isCollectionProperty(fromDocumentUri, sourceId, propName)) {
				List<Object> fromList = fromStore.getValueList(fromDocumentUri, sourceId, propName);
				for (Object listItem:fromList) {
					if (listItem instanceof IndividualUriValue) {
						toStore.addValueToCollection(
								toDocumentUri, toId, propName, new SimpleUriValue((IndividualUriValue)listItem));
					} else if (listItem instanceof TypedValue) {
						TypedValue listItemTv = (TypedValue)listItem;
						toStore.addValueToCollection(toDocumentUri, toId, propName, 
								copy(toStore, toDocumentUri, fromStore, fromDocumentUri, 
										listItemTv.getId(), listItemTv.getType()));
					} else {
						toStore.addValueToCollection(toDocumentUri, toId, propName, listItem);
					}
				}
			} else {
				Optional<Object> result =  fromStore.getValue(fromDocumentUri, sourceId, propName);
				if (result.isPresent()) {
					if (result.get() instanceof IndividualUriValue) {
						toStore.setValue(toDocumentUri, toId, propName, new SimpleUriValue((IndividualUriValue)result.get()));
					} else if (result.get() instanceof TypedValue) {
						TypedValue tv = (TypedValue)result.get();
						toStore.setValue(toDocumentUri, toId, propName, 
								copy(toStore, toDocumentUri, fromStore, fromDocumentUri, 
										tv.getId(), tv.getType()));
					} else {
						toStore.setValue(toDocumentUri, toId, propName, result.get());
					}
				}
			}
		}
	}

	/**
	 * Convert the class to the approrpriate stored class
	 * @param clazz Model class
	 * @return class compatible with stored classes
	 */
	public static Class<?> modelClassToStoredClass(Class<?> clazz) {
		if (ModelObject.class.isAssignableFrom(clazz)) {
			return TypedValue.class;
		} else if (implementsIndividualUriValue(clazz)) {
			return SimpleUriValue.class;
		} else {
			return clazz;
		}
	}

	private static boolean implementsIndividualUriValue(Class<?> clazz) {
		for (Class<?> intefaceClass:clazz.getInterfaces()) {
			if (intefaceClass.equals(IndividualUriValue.class)) {
				return true;
			}
		}
		return false;
	}
}
