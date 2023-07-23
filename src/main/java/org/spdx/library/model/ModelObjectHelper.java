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
package org.spdx.library.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.IndividualUriValue;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SimpleUriValue;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.SpdxObjectNotInStoreException;
import org.spdx.library.TypedValue;
import org.spdx.library.model.core.ExternalMap;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;
import org.spdx.storage.IModelStore.IModelStoreLock;

/**
 * A set of static methods to help with common ModelObject functions
 * 
 * @author Gary O'Neall
 *
 */
public class ModelObjectHelper {
	
	private ModelObjectHelper() {
		// this is only a static helper class
	}
	
	/**
	 * Get an object value for a property
	 * @param modelStore Model store for the object
	 * @param objectUri the Object URI or anonymous ID
	 * @param propertyDescriptor property descriptor for the property
	 * @param copyManager if non null, any ModelObject property value not stored in the modelStore under the stDocumentUri will be copied to make it available
	 * @param externalMap map of URI's to ExternalMaps for any external elements
	 * @return value associated with a property
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Optional<Object> getObjectPropertyValue(IModelStore modelStore, String objectUri,
			PropertyDescriptor propertyDescriptor, ModelCopyManager copyManager,
			@Nullable Map<String, ExternalMap> externalMap) throws InvalidSPDXAnalysisException {
		IModelStoreLock lock = modelStore.enterCriticalSection(false);
		// NOTE: we use a write lock since the ModelStorageClassConverter may end up creating objects in the store
		try {
			if (!modelStore.exists(objectUri)) {
				return Optional.empty();
			} else if (modelStore.isCollectionProperty(objectUri, propertyDescriptor)) {
				return Optional.of(new ModelCollection<>(modelStore, objectUri, propertyDescriptor, copyManager, null, externalMap));
			} else {
				return optionalStoredObjectToModelObject(modelStore.getValue(objectUri,
						propertyDescriptor), modelStore, copyManager, externalMap);
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Set a property value for a property descriptor, creating the property if necessary
	 * @param modelStore Model store for the properties
	 * @param objectUri URI or anonymous ID for the object
	 * @param propertyDescriptor Descriptor for the property associated with this object
	 * @param value Value to associate with the property
	 * @param copyManager if non null, any ModelObject property value not stored in the modelStore under the stDocumentUri will be copied to make it available
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static void setPropertyValue(IModelStore modelStore, String objectUri, 
			PropertyDescriptor propertyDescriptor, @Nullable Object value, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore, "Model Store can not be null");
		Objects.requireNonNull(objectUri, "Object Uri or anonymous ID can not be null");
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		if (value == null) {
			// we just remove the value
			removeProperty(modelStore, objectUri, propertyDescriptor);
		} else if (value instanceof Collection) {
			replacePropertyValueCollection(modelStore, objectUri, propertyDescriptor, (Collection<?>)value, copyManager);
		} else {
			modelStore.setValue(objectUri, propertyDescriptor, modelObjectToStoredObject(value, modelStore, copyManager));
		}
	}
	
	/**
	 * Removes a property and its value from the model store if it exists
	 * @param modelStore Model store for the properties
	 * @param objectUri URI or anonymous ID for the object
	 * @param propertyDescriptor Descriptor for the property associated with this object to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static void removeProperty(IModelStore modelStore, String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		modelStore.removeProperty(objectUri, propertyDescriptor);
	}
	
	/**
	 * Clears a collection of values associated with a property creating the property if it does not exist
	 * @param modelStore Model store for the properties
	 * @param objectUri URI or anonymous ID for the object
	 * @param propertyDescriptor Descriptor for the property
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static void clearValueCollection(IModelStore modelStore, String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		modelStore.clearValueCollection(objectUri, propertyDescriptor);
	}
	
	/**
	 * Add a value to a collection of values associated with a property. If a value
	 * is a ModelObject and does not belong to the document, it will be copied into
	 * the object store
	 * 
	 * @param modelStore  Model store for the properties
	 * @param objectUri URI or anonymous ID for the object
	 * @param propertyDescriptor  Descriptor for the property
	 * @param value         to add
	 * @param copyManager
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static void addValueToCollection(IModelStore modelStore, String objectUri, 
			PropertyDescriptor propertyDescriptor, Object value, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(value, "Value can not be null");
		modelStore.addValueToCollection(objectUri, propertyDescriptor, 
				modelObjectToStoredObject(value, modelStore, copyManager));
	}
	
	/**
	 * Replace the entire value collection for a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param modelStore Model store for the properties
	 * @param objectUri URI or anonymous ID for the object
	 * @param propertyDescriptor Descriptor for the property
	 * @param values collection of new properties
	 * @param copyManager if non-null, any ModelObject property value not stored in the modelStore under the stDocumentUri will be copied to make it available
	 * @throws InvalidSPDXAnalysisException 
	 */
	protected static void replacePropertyValueCollection(IModelStore modelStore, String objectUri, 
			PropertyDescriptor propertyDescriptor, Collection<?> values, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		clearValueCollection(modelStore, objectUri, propertyDescriptor);
		for (Object value:values) {
			addValueToCollection(modelStore, objectUri, propertyDescriptor, value, copyManager);
		}
	}
	
	/**
	 * Remove a property value from a collection
	 * @param modelStore Model store for the properties
	 * @param objectUri URI or anonymous ID for the object
	 * @param propertyDescriptor descriptor for the property
	 * @param value Value to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static void removePropertyValueFromCollection(IModelStore modelStore, String objectUri, 
			PropertyDescriptor propertyDescriptor, Object value) throws InvalidSPDXAnalysisException {
		modelStore.removeValueFromCollection(objectUri, propertyDescriptor, modelObjectToStoredObject(value, modelStore, null));
	}
	
	/**
	 * Converts any typed value or IndividualValue objects to a ModelObject,
	 * returning an existing ModelObject if it exists or creates a new ModelObject
	 * 
	 * @param value         Value which may be a TypedValue
	 * @param modelStore  ModelStore to use in fetching or creating
	 * @param copyManager   if not null, copy any referenced ID's outside of this
	 *                      document/model store
	 * @param externalMap map of URI's to ExternalMaps for any external elements
	 * @return the object itself unless it is a TypedValue, in which case a
	 *         ModelObject is returned
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Optional<Object> optionalStoredObjectToModelObject(Optional<Object> value, 
			IModelStore modelStore, ModelCopyManager copyManager, @Nullable Map<String, ExternalMap> externalMap) throws InvalidSPDXAnalysisException {
		if (value.isPresent() && value.get() instanceof IndividualUriValue) {
			return Optional.of(new SimpleUriValue((IndividualUriValue)value.get()).toModelObject(modelStore, copyManager, 
					null, externalMap));
		} else if (value.isPresent() && value.get() instanceof TypedValue) {
			TypedValue tv = (TypedValue)value.get();
			return Optional.of(SpdxModelFactory.createModelObject(modelStore, 
					tv.getObjectUri(), tv.getType(), copyManager));
		} else {
			return value;
		}
	}
	
	/**
	 * Converts a stored object to it's appropriate model object type
	 * @param value
	 * @param modelStore
	 * @param copyManager if not null, copy any referenced ID's outside of this document/model store
	 * @return Model Object appropriate type
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Object modelObjectToStoredObject(Object value, IModelStore modelStore, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (value instanceof IndividualUriValue) {
			// Convert to a simple URI value to save storage
			return new SimpleUriValue((IndividualUriValue)value);
		} else if (value instanceof ModelObject) {
			ModelObject mValue = (ModelObject)value;
			if (!mValue.getModelStore().equals(modelStore)) {
				if (Objects.nonNull(copyManager)) {
					return copyManager.copy(modelStore, mValue.getModelStore(), mValue.getObjectUri(), mValue.getType(), null, null, null, null);
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
	 * Converts any typed value or individual value objects to a ModelObject,
	 * returning an existing ModelObject if it exists or creates a new ModelObject
	 * 
	 * @param value       Value which may be a TypedValue
	 * @param modelStore  ModelStore to use in fetching or creating
	 * @param copyManager if not null, copy any referenced ID's outside of this
	 *                    document/model store
	 * @param externalMap map of URI's to ExternalMaps for any external elements
	 * @return the object itself unless it is a TypedValue, in which case a
	 *         ModelObject is returned
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Object storedObjectToModelObject(Object value, IModelStore modelStore,
			ModelCopyManager copyManager, @Nullable Map<String, ExternalMap> externalMap) throws InvalidSPDXAnalysisException {
		if (value instanceof IndividualUriValue) {	// Note: this must be before the check for TypedValue
			SimpleUriValue suv = new SimpleUriValue((IndividualUriValue)value);
			return suv.toModelObject(modelStore, copyManager, null, externalMap);
		} else if (value instanceof TypedValue) {
			TypedValue tv = (TypedValue)value;
			return SpdxModelFactory.getModelObject(modelStore, tv.getObjectUri(), tv.getType(), copyManager, true);
		} else {
			return value;
		}
	};
	
	/**
	 * Convert the class to the appropriate stored class
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
	
	/**
	 * @param clazz
	 * @return true if the clazz implements the IndividualUriValue interface
	 */
	public static boolean implementsIndividualUriValue(Class<?> clazz) {
		for (Class<?> intefaceClass:clazz.getInterfaces()) {
			if (intefaceClass.equals(IndividualUriValue.class)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifies all elements in a collection
	 * @param specVersion version of the SPDX specification to verify against
	 * @param collection collection to be verifies
	 * @param verifiedIds verifiedIds list of all Id's which have already been verifieds - prevents infinite recursion
	 * @param warningPrefix String to prefix any warning messages
	 */
	public static List<String> verifyCollection(Collection<? extends ModelObject> collection, String warningPrefix, Set<String> verifiedIds, String specVersion) {
		List<String> retval = new ArrayList<>();
		for (ModelObject mo:collection) {
			for (String warning:mo.verify(verifiedIds, specVersion)) {
				if (Objects.nonNull(warningPrefix)) {
					retval.add(warningPrefix + warning);
				} else {
					retval.add(warning);
				}
			}
		}
		return retval;
	}
}
