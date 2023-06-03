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

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxObjectNotInStoreException;
import org.spdx.storage.IModelStore;

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
	
	static final Logger logger = LoggerFactory.getLogger(ModelStorageClassConverter.class);
	
	public static void reset() {tempMgr = new ModelCopyManager();}//TODO: This should be removed
	static ModelCopyManager tempMgr = new ModelCopyManager();//TODO: This is temporary - move to a parameter to ModelObject - this is not currently threadsafe

	/**
	 * Converts any typed value or individual value objects to a ModelObject,
	 * returning an existing ModelObject if it exists or creates a new ModelObject
	 * 
	 * @param value       Value which may be a TypedValue
	 * @param documentUri Document URI to use when converting a typedValue
	 * @param modelStore  ModelStore to use in fetching or creating
	 * @param copyManager if not null, copy any referenced ID's outside of this
	 *                    document/model store
	 * @return the object itself unless it is a TypedValue, in which case a
	 *         ModelObject is returned
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Object storedObjectToModelObject(Object value, String documentUri, IModelStore modelStore,
			ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (value instanceof IndividualUriValue) {	// Note: this must be before the check for TypedValue
			SimpleUriValue suv = new SimpleUriValue((IndividualUriValue)value);
			return suv.toModelObject(modelStore, documentUri, null);
		} else if (value instanceof TypedValue) {
			TypedValue tv = (TypedValue)value;
			return SpdxModelFactory.createModelObject(modelStore, documentUri, tv.getId(), tv.getType(), copyManager);
		} else {
			return value;
		}
	};
	
	/**
	 * Converts any typed value or IndividualValue objects to a ModelObject,
	 * returning an existing ModelObject if it exists or creates a new ModelObject
	 * 
	 * @param value         Value which may be a TypedValue
	 * @param stDocumentUri Document URI to use when converting a typedValue
	 * @param stModelStore  ModelStore to use in fetching or creating
	 * @param copyManager   if not null, copy any referenced ID's outside of this
	 *                      document/model store
	 * @return the object itself unless it is a TypedValue, in which case a
	 *         ModelObject is returned
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Optional<Object> optionalStoredObjectToModelObject(Optional<Object> value, 
			String stDocumentUri, IModelStore stModelStore, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (value.isPresent() && value.get() instanceof IndividualUriValue) {
			return Optional.of(new SimpleUriValue((IndividualUriValue)value.get()).toModelObject(stModelStore, stDocumentUri, copyManager));
		} else if (value.isPresent() && value.get() instanceof TypedValue) {
			TypedValue tv = (TypedValue)value.get();
			return Optional.of(SpdxModelFactory.createModelObject(stModelStore, stDocumentUri, tv.getId(), tv.getType(), copyManager));
		} else {
			return value;
		}
	}
	
	/**
	 * Converts a stored object to it's appropriate model object type
	 * @param value
	 * @param stDocumentUri
	 * @param stModelStore
	 * @param copyManager if not null, copy any referenced ID's outside of this document/model store
	 * @return Model Object appropriate type
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Object modelObjectToStoredObject(Object value, String stDocumentUri, 
			IModelStore stModelStore, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (value instanceof IndividualUriValue) {
			// Convert to a simple URI value to save storage
			return new SimpleUriValue((IndividualUriValue)value);
		} else if (value instanceof ModelObject) {
			ModelObject mValue = (ModelObject)value;
			if (!mValue.getModelStore().equals(stModelStore) || !mValue.getDocumentUri().equals(stDocumentUri)) {
				if (Objects.nonNull(copyManager)) {
					return copyManager.copy(stModelStore, stDocumentUri, 
							mValue.getModelStore(), mValue.getDocumentUri(), mValue.getId(), mValue.getType());
				} else {
					throw new SpdxObjectNotInStoreException("Can not set a property value to a Model Object stored in a different model store");
				}
			} else {
				return mValue.toTypedValue();
			}
		} else if (value instanceof Integer || value instanceof String || value instanceof Boolean || value instanceof IndividualUriValue) {
			return value;
		} else if (Objects.isNull(value)) {
			throw new SpdxInvalidTypeException("Property value is null");
		} else {
			throw new SpdxInvalidTypeException("Property value type not supported: "+value.getClass().getName());
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
