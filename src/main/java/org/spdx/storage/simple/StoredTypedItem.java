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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.CoreModelObject;
import org.spdx.core.IndividualUriValue;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.ModelRegistry;
import org.spdx.core.ModelRegistryException;
import org.spdx.core.SpdxInvalidTypeException;
import org.spdx.core.TypedValue;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;

/**
 * Individual item to be stored in memory
 * 
 * @author Gary O'Neall
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class StoredTypedItem extends TypedValue {

	static final Logger logger = LoggerFactory.getLogger(StoredTypedItem.class);

	private static final String NO_ID_ID = "__NO_ID__";  // ID to use in list has map for non-typed values
	
	private final ConcurrentHashMap<PropertyDescriptor, Object> properties = new ConcurrentHashMap<>();
	
	private int referenceCount = 0;
	
	private final ReadWriteLock countLock = new ReentrantReadWriteLock();

	/**
	 * Construct a new {@link StoredTypedItem} with the specified object URI, type, and
	 * specification version
	 * <p>
	 * This constructor initializes a stored typed item, which represents an individual item to be
	 * stored in memory with its associated properties and metadata.
	 *
	 * @param objectUri The unique URI identifying this stored item.
	 * @param type The type of the stored item.
	 * @param specVersion The version of the SPDX specification associated with this item.
	 * @throws InvalidSPDXAnalysisException If the provided parameters are invalid or violate SPDX
	 *         constraints.
	 */
	public StoredTypedItem(String objectUri, String type, String specVersion) throws InvalidSPDXAnalysisException {
		super(objectUri, type, specVersion);
	}

	/**
	 * Retrieve the property descriptors for all properties that have a value
	 * <p>
	 * This method iterates through the stored properties and collects the descriptors
	 * for all properties that currently have an associated value.
	 * 
	 * @return An unmodifiable {@link List} of {@link PropertyDescriptor} objects representing
	 *         the properties that have values.
	 */
	public List<PropertyDescriptor> getPropertyValueDescriptors() {
		Iterator<Entry<PropertyDescriptor, Object>> iter = this.properties.entrySet().iterator();
		List<PropertyDescriptor> retval = new ArrayList<>();
		while (iter.hasNext()) {
			Entry<PropertyDescriptor, Object> entry = iter.next();
			retval.add(entry.getKey());
		}
		return Collections.unmodifiableList(retval);
	}

	/**
	 * Increment the reference count for this stored type item - the number of times this item is
	 * referenced
	 *
	 * @return The new number of times this item is referenced.
	 */
	@SuppressWarnings("UnusedReturnValue")
    public int incReferenceCount() {
	    countLock.writeLock().lock();
	    try {
	        this.referenceCount++;
	        return this.referenceCount;
	    } finally {
	        countLock.writeLock().unlock();
	    }
	}
	
	/**
	 * Decrement the reference count for this stored type item
	 *
	 * @return The new number of times this item is referenced.
	 * @throws SpdxInvalidTypeException on invalid type
	 */
	public int decReferenceCount() throws SpdxInvalidTypeException {
	       countLock.writeLock().lock();
           try {
               if (this.referenceCount < 1) {
                   throw new SpdxInvalidTypeException("Usage count underflow - usage count decremented more than incremented");
               }
               this.referenceCount--;
               return this.referenceCount;
           } finally {
               countLock.writeLock().unlock();
           }
	}

	/**
	 * Retrieve the current reference count for this stored item
	 *
     * @return The current number of times this item is referenced.
     */
    public int getReferenceCount() {
           countLock.readLock().lock();
           try {
               return this.referenceCount;
           } finally {
               countLock.readLock().unlock();
           }
    }

	/**
	 * Set the value for the specified property descriptor
	 *
	 * @param propertyDescriptor  The descriptor for the property. Must not be {@code null}.
	 * @param value The value to be set. Must not be {@code null}.
	 * @throws SpdxInvalidTypeException on invalid type
	 */
	public void setValue(PropertyDescriptor propertyDescriptor, Object value)
			throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(value, "Value can not be null");
		if (value instanceof CoreModelObject) {
			throw new SpdxInvalidTypeException("Can not store Model Object in store.  Convert to TypedValue first");
		} else if (value instanceof Collection) {
			throw new SpdxInvalidTypeException("Can not store list values directly.  Use addValueToCollection.");
		} else if (!value.getClass().isPrimitive() &&
				!String.class.isAssignableFrom(value.getClass()) &&
				!Boolean.class.isAssignableFrom(value.getClass()) &&
				!Integer.class.isAssignableFrom(value.getClass()) &&
				!Double.class.isAssignableFrom(value.getClass()) &&
				!Float.class.isAssignableFrom((value.getClass())) &&
				!TypedValue.class.isAssignableFrom(value.getClass()) &&
				!(value instanceof IndividualUriValue)) {
			throw new SpdxInvalidTypeException(value.getClass() +" is not a supported class to be stored.");
		}
		properties.put(propertyDescriptor, value);
	}

	/**
	 * Set the value list for the property to an empty list creating the propertyDescriptor if it
	 * does not exist
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @throws SpdxInvalidTypeException on invalid type
	 */
	public void clearPropertyValueList(PropertyDescriptor propertyDescriptor)
			throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Object value = properties.get(propertyDescriptor);
		if (value == null) {
			return;
		}
		if (value instanceof ConcurrentHashMap<?, ?>) {
			((ConcurrentHashMap<?, ?>)value).clear();
		} else {
			throw new SpdxInvalidTypeException("Trying to clear a list for non list type for property "+propertyDescriptor);
		}
		
	}

	/**
	 * Add a value to a property list for a String or Boolean type of value creating the
	 * propertyDescriptor if it does not exist
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @param value Th value to be set. Must not be {@code null}.
	 * @throws SpdxInvalidTypeException on invalid type
	 */
	public boolean addValueToList(PropertyDescriptor propertyDescriptor, Object value)
			throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(value, "Value can not be null");
		if (value instanceof CoreModelObject) {
			throw new SpdxInvalidTypeException("Can not store Model Object in store.  Convert to TypedValue first");
		} else if (!value.getClass().isPrimitive() &&
				!String.class.isAssignableFrom(value.getClass()) &&
				!Boolean.class.isAssignableFrom(value.getClass()) &&
				!Integer.class.isAssignableFrom(value.getClass()) &&
				!TypedValue.class.isAssignableFrom(value.getClass()) &&
				!(value instanceof IndividualUriValue)) {
			throw new SpdxInvalidTypeException(value.getClass() +" is not a supported class to be stored.");
		}
		Object map = properties.get(propertyDescriptor);
		if (map == null) {
			properties.putIfAbsent(propertyDescriptor,  new ConcurrentHashMap<String, List<Object>>());
			map = properties.get(propertyDescriptor);	
			//Note: there is a small timing window where the property could be removed
			if (map == null) {
				return true;
			}
		}
		if (!(map instanceof ConcurrentHashMap<?, ?>)) {
			throw new SpdxInvalidTypeException("Trying to add a list for non list type for property "+propertyDescriptor);
		}
		try {
			@SuppressWarnings("unchecked")
			ConcurrentHashMap<String, List<Object>> idValueMap = (ConcurrentHashMap<String, List<Object>>)map;
			String id;
			if (value instanceof TypedValue) {
				id = ((TypedValue)value).getObjectUri();
			} else {
				id = NO_ID_ID;
			}
			idValueMap.putIfAbsent(id, new ArrayList<>());
			List<Object> list = idValueMap.get(id);
			if (list == null) {
				// handle the very small window where this may have gotten removed
				list = new ArrayList<>();
				idValueMap.putIfAbsent(id, list);
			}
			return list.add(value);
		} catch (Exception ex) {
			throw new SpdxInvalidTypeException("Invalid list type for "+propertyDescriptor);
		}
	}

	/**
	 * Remove a property from a property list if it exists
	 *
	 * @param propertyDescriptor The descriptor for the property.
	 * @param value The value to be removed.
	 * @return {@code true} if the value was removed, {@code false} if the value did not exist.
	 * @throws SpdxInvalidTypeException for an invalid type
	 */
	public boolean removeTypedValueFromList(PropertyDescriptor propertyDescriptor, TypedValue value)
			throws SpdxInvalidTypeException {
		Object map = properties.get(propertyDescriptor);
		if (map == null) {
			return false;
		}
		if (!(map instanceof ConcurrentHashMap<?, ?>)) {
			throw new SpdxInvalidTypeException("Trying to remove from a list for non typed value list type for property "+propertyDescriptor);
		}
		try {
			@SuppressWarnings("unchecked")
			ConcurrentHashMap<String, List<TypedValue>> typedValueMap = (ConcurrentHashMap<String, List<TypedValue>>)map;
			List<TypedValue> list = typedValueMap.get(value.getObjectUri());
			if (list == null) {
				return false;
			}
			return list.remove(value);
		} catch (Exception ex) {
			throw new SpdxInvalidTypeException("Invalid list type for "+propertyDescriptor);
		}
	}

	/**
	 * Remove a property from a property list if it exists
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @param value The value to be removed. Must not be {@code null}.
	 * @return {@code true} if the value was removed, {@code false} if the value did not exist.
	 * @throws SpdxInvalidTypeException on invalid type
	 */
	public boolean removeValueFromList(PropertyDescriptor propertyDescriptor, Object value)
			throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(value, "Value can not be null");
		Object map = properties.get(propertyDescriptor);
		if (map == null) {
			return false;
		}
		if (!(map instanceof ConcurrentHashMap<?, ?>)) {
			throw new SpdxInvalidTypeException("Trying to remove from a list for non list type for property "+propertyDescriptor);
		}
		try {
			@SuppressWarnings("unchecked")
			ConcurrentHashMap<String, List<Object>> idValueMap = (ConcurrentHashMap<String, List<Object>>)map;
			String id;
			if (value instanceof TypedValue) {
				id = ((TypedValue)value).getObjectUri();
			} else {
				id = NO_ID_ID;
			}
			List<Object> list = idValueMap.get(id);
			if (list == null) {
				return false;
			}
			return list.remove(value);
		} catch (Exception ex) {
			throw new SpdxInvalidTypeException("Invalid list type for "+propertyDescriptor);
		}
	}

	/**
	 * Retrieve an iterator over the list of values associated with the specified property
	 * descriptor
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @return An {@link Iterator} over the list of values associated with the property descriptor.
	 *         If no values exist, an empty iterator is returned.
	 * @throws SpdxInvalidTypeException If the property is not associated with a list or if the type
	 *         is invalid.
	 */
	public Iterator<Object> getValueList(PropertyDescriptor propertyDescriptor)
			throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Object list = properties.get(propertyDescriptor);
		if (list == null) {
			return Collections.emptyIterator();
		}
		if (list instanceof ConcurrentHashMap<?, ?>) {
			List<Object> valueList = new ArrayList<>();
			for (Object value : ((ConcurrentHashMap<?, ?>) list).values() ) {
				if (value instanceof Collection) {
					valueList.addAll((Collection<?>)value);
				} else {
					valueList.add(value);
				}
			}
			return valueList.iterator();
		} else {
			throw new SpdxInvalidTypeException("Trying to get a list for non list type for property "+propertyDescriptor);
		}
	}

	/**
	 * Retrieve the value associated with the specified property descriptor
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @return The single value associated with the specified property descriptor, or {@code null}
	 *         if no value exists.
	 */
	public Object getValue(PropertyDescriptor propertyDescriptor) {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		return properties.get(propertyDescriptor);
	}
	
	/**
	 * Remove a property from the document for the given ID if the property exists
	 * <p>
	 * Does not raise any exception if the propertyDescriptor does not exist.
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 */
	public void removeProperty(PropertyDescriptor propertyDescriptor) {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		properties.remove(propertyDescriptor);
	}

	/**
	 * Copy all values for this item from another model store
	 *
	 * @param store The {@link IModelStore} from which to copy values. Must not be {@code null}.
	 * @throws InvalidSPDXAnalysisException If an invalid type is encountered during the copy
	 *         process. This can occur if the values in the source store are not compatible with
	 *         this item's properties.
	 */
	public void copyValuesFrom(IModelStore store) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(store, "Store can not be null");
		List<PropertyDescriptor> propertyDescriptors = store.getPropertyValueDescriptors(this.getObjectUri());
		for (PropertyDescriptor propertydescriptor:propertyDescriptors) {
			Optional<Object> value = store.getValue(getObjectUri(), propertydescriptor);
			if (value.isPresent()) {
				this.setValue(propertydescriptor, value.get());
			}
		}
	}

	/**
	 * Retrieve the size of the collection associated with the specified property descriptor
	 * <p>
	 * This method calculates the total number of elements in the collection associated with the
	 * given property descriptor.
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @return The size of the collection.
	 * @throws SpdxInvalidTypeException If the type is invalid or if the property is not associated
	 *         with a collection.
	 */
	@SuppressWarnings("rawtypes")
	public int collectionSize(PropertyDescriptor propertyDescriptor) throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Object map = properties.get(propertyDescriptor);
		if (map == null) {
			properties.putIfAbsent(propertyDescriptor,  new ConcurrentHashMap<String, List<Object>>());
			map = properties.get(propertyDescriptor);	
			//Note: there is a small timing window where the property could be removed
			if (map == null) {
				return 0;
			}
		}
		if (map instanceof ConcurrentHashMap<?, ?>) {
			int count = 0;
			for (Object value : ((ConcurrentHashMap<?, ?>) map).values()) {
				if (value instanceof Collection) {
					count = count + ((Collection)value).size();
				} else {
					count++;
				}
			}
			return count;
		} else {
			throw new SpdxInvalidTypeException("Trying to get size for a non list type for property "+propertyDescriptor);
		}
	}

	/**
	 * Check whether the specified value exists in the collection associated with the given property
	 * descriptor
	 * <p>
	 * This method verifies if the provided value is present in the collection of values associated
	 * with the specified property descriptor.
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @param value The value to be checked. Must not be {@code null}.
	 * @return {@code true} if the value exists in the collection; {@code false} otherwise.
	 * @throws SpdxInvalidTypeException If the type is invalid or if the property is not associated
	 *         with a collection.
	 */
	public boolean collectionContains(PropertyDescriptor propertyDescriptor, Object value)
			throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(value, "Value can not be null");
		Object map = properties.get(propertyDescriptor);
		if (map == null) {
			properties.putIfAbsent(propertyDescriptor,  new ConcurrentHashMap<String, List<Object>>());
			map = properties.get(propertyDescriptor);	
			//Note: there is a small timing window where the property could be removed
			if (map == null) {
				return false;
			}
		}
		if (map instanceof ConcurrentHashMap<?, ?>) {
			String id;
			if (value instanceof TypedValue) {
				id = ((TypedValue)value).getObjectUri();
			} else {
				id = NO_ID_ID;
			}
			@SuppressWarnings("unchecked")
			ConcurrentHashMap<String, List<Object>> typedValueMap = (ConcurrentHashMap<String, List<Object>>)map;
			List<Object> valueList = typedValueMap.get(id);
			if (valueList == null) {
				return false;
			}
			return valueList.contains(value);
		} else {
			throw new SpdxInvalidTypeException("Trying to find contains for non list type for property "+propertyDescriptor);
		}
	}

	/**
	 * Check whether all members of the collection associated with the specified property descriptor
	 * can be assigned to the specified class
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @param clazz The class to test against. Must not be {@code null}.
	 * @return {@code true} if the property with the {@code propertyDescriptor} can be assigned to
	 *         {@code clazz}; {@code false} otherwise.
	 * @throws ModelRegistryException If the model registry is not properly initialized.
	 */
	public boolean isCollectionMembersAssignableTo(PropertyDescriptor propertyDescriptor,
			Class<?> clazz) throws ModelRegistryException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(clazz, "Class can not be null");
		Object map = properties.get(propertyDescriptor);
		if (map == null) {
			return true; // It is still assignable to since it is unassigned
		}
		if (!(map instanceof ConcurrentHashMap<?,?>)) {
			logger.warn("Checking collection properties on a non-collection stored item");
			return false;
		}
		@SuppressWarnings("unchecked")
		ConcurrentHashMap<String, List<Object>> idValueMap = (ConcurrentHashMap<String, List<Object>>)map;
		for (List<Object> valueList : idValueMap.values()) {
			for (Object value : valueList) {
				if (!isAssignableTo(value, clazz, getSpecVersion())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check whether the given value can be assigned to the specified class
	 *
	 * @param value The value to test.
	 * @param clazz The class to check if the value can be assigned to.
	 * @param specVersion The SPDX specification version to use for type resolution.
	 * @return {@code true} if the value can be assigned to {@code clazz}; {@code false} otherwise.
	 * @throws ModelRegistryException If the model registry is not properly initialized.
	 */
	private boolean isAssignableTo(Object value, Class<?> clazz, String specVersion)
			throws ModelRegistryException {
		if (clazz.isAssignableFrom(value.getClass())) {
			return true;
		}
		if (value instanceof TypedValue) {
			TypedValue typedValue = (TypedValue)value;
			try {
				Class<?> type = ModelRegistry.getModelRegistry().typeToClass(typedValue.getType(), typedValue.getSpecVersion());
				return Objects.nonNull(type) && clazz.isAssignableFrom(type);
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Error converting typed value to class",e);
				return false;
			}
		}
		if (value instanceof IndividualUriValue) {
			String uri = ((IndividualUriValue)value).getIndividualURI();
			
			Enum<?> spdxEnum = ModelRegistry.getModelRegistry().uriToEnum(uri, getSpecVersion());
			if (Objects.nonNull(spdxEnum)) {
				return clazz.isAssignableFrom(spdxEnum.getClass());
			} else {
				Object individual = ModelRegistry.getModelRegistry().uriToIndividual(uri, getSpecVersion(), clazz);
				if (Objects.nonNull(individual)) {
					return clazz.isAssignableFrom(individual.getClass());
				} else {
					// Assume this is external
					return ModelRegistry.getModelRegistry().canBeExternal(clazz, specVersion);
				}
			}
		} else {
			return false;
		}
	}

	/**
	 * Check whether the value associated with the specified property descriptor can be assigned to
	 * the specified class
	 *
	 * @param propertyDescriptor The descriptor for the property. Must not be {@code null}.
	 * @param clazz The class to test against. Must not be {@code null}.
	 * @param specVersion The SPDX specification version to use for type resolution.
	 * @return {@code true} if the property value can be assigned to type {@code clazz} for the
	 *         latest SPDX spec version; {@code false} otherwise.
	 * @throws ModelRegistryException If the model registry is not properly initialized.
	 */
	public boolean isPropertyValueAssignableTo(PropertyDescriptor propertyDescriptor,
			Class<?> clazz, String specVersion) throws ModelRegistryException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(clazz, "Class can not be null");
		Object value = properties.get(propertyDescriptor);
		if (value == null) {
			return false;
		}
		return isAssignableTo(value, clazz, specVersion);
	}

	/**
	 * Check whether the specified property descriptor is associated with a collection
	 *
	 * @param propertyDescriptor The property descriptor to check. Must not be {@code null}.
	 * @return {@code true} if the property descriptor is associated with a collection;
	 *         {@code false} otherwise.
	 */
	public boolean isCollectionProperty(PropertyDescriptor propertyDescriptor) {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Object value = properties.get(propertyDescriptor);
		return value instanceof ConcurrentHashMap;
	}

	/**
	 * Check whether the specified element ID is used as a value in any collection or property
	 *
	 * @param elementId The object URI of the element to check.
	 * @return {@code true} if the element using the object URI is used as a value in a collection;
	 *         {@code false} otherwise.
	 */
	public boolean usesId(String elementId) {
		if (Objects.isNull(elementId)) {
			return false;
		}
        for (Object value : this.properties.values()) {
            if (value instanceof List && !((List<?>) value).isEmpty() && ((List<?>) value).get(0) instanceof TypedValue) {
                for (Object listValue : (List<?>) value) {
                    if (listValue instanceof TypedValue && ((TypedValue) listValue).getObjectUri().equalsIgnoreCase(elementId)) {
                        return true;
                    }
                }
            } else if (value instanceof TypedValue) {
                if (((TypedValue) value).getObjectUri().equalsIgnoreCase(elementId)) {
                    return true;
                }
            }
        }
		return false;
	}
}
