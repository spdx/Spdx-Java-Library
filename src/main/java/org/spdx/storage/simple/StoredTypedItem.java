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
 *
 */
public class StoredTypedItem extends TypedValue {

	static final Logger logger = LoggerFactory.getLogger(TypedValue.class);

	private static final String NO_ID_ID = "__NO_ID__";  // ID to use in list has map for non-typed values
	
	private ConcurrentHashMap<PropertyDescriptor, Object> properties = new ConcurrentHashMap<>();
	
	private int referenceCount = 0;
	
	private final ReadWriteLock countLock = new ReentrantReadWriteLock();
	
	public StoredTypedItem(String objectUri, String type, String specVersion) throws InvalidSPDXAnalysisException {
		super(objectUri, type, specVersion);
	}
	
	/**
	 * @return property descriptors for all properties having a value
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
	 * Increment the reference count for this stored type item - the number of times this item is referenced
	 * @return new number of times this item is referenced
	 */
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
	 * @return new number of times this item is referenced
	 * @throws SpdxInvalidTypeException
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
     * @return new number of times this item is referenced
     * @throws SpdxInvalidTypeException
     */
    public int getReferenceCount() throws SpdxInvalidTypeException {
           countLock.readLock().lock();
           try {
               return this.referenceCount;
           } finally {
               countLock.readLock().unlock();
           }
    }
	
	/**
	 * @param propertyDescriptor Descriptor for the property
	 * @param value Value to be set
	 * @throws SpdxInvalidTypeException 
	 */
	public void setValue(PropertyDescriptor propertyDescriptor, Object value) throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(value, "Value can not be null");
		if (value instanceof CoreModelObject) {
			throw new SpdxInvalidTypeException("Can not store Model Object in store.  Convert to TypedValue first");
		} else if (value instanceof List || value instanceof Collection) {
			throw new SpdxInvalidTypeException("Can not store list values directly.  Use addValueToCollection.");
		} else if (!value.getClass().isPrimitive() &&
				!String.class.isAssignableFrom(value.getClass()) &&
				!Boolean.class.isAssignableFrom(value.getClass()) &&
				!Integer.class.isAssignableFrom(value.getClass()) &&
				!TypedValue.class.isAssignableFrom(value.getClass()) &&
				!(value instanceof IndividualUriValue)) {
			throw new SpdxInvalidTypeException(value.getClass().toString()+" is not a supported class to be stored.");
		}
		properties.put(propertyDescriptor, value);
	}
	
	/**
	 * Sets the value list for the property to an empty list creating the propertyDescriptor if it does not exist
	 * @param propertyDescriptor descriptor for the property
	 * @throws SpdxInvalidTypeException
	 */
	public void clearPropertyValueList(PropertyDescriptor propertyDescriptor) throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "property descriptor can not be null");
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
	 * Adds a value to a property list for a String or Boolean type of value creating the propertyDescriptor if it does not exist
	 * @param propertyDescriptor Descriptor for the property
	 * @param value Value to be set
	 * @throws SpdxInvalidTypeException
	 */
	public boolean addValueToList(PropertyDescriptor propertyDescriptor, Object value) throws SpdxInvalidTypeException {
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
			throw new SpdxInvalidTypeException(value.getClass().toString()+" is not a supported class to be stored.");
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
			idValueMap.putIfAbsent(id, new ArrayList<Object>());
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
	 * @param propertyDescriptor descriptor for the property
	 * @param value to be removed
	 * @return true if the value was removed, false if the value did not exist
	 * @throws SpdxInvalidTypeException for an invalid type
	 */
	public boolean removeTypedValueFromList(PropertyDescriptor propertyDescriptor, TypedValue value) throws SpdxInvalidTypeException {
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
	 * Removes a property from a list if it exists
	 * @param propertyDescriptor descriptor for the property
	 * @param value
	 * @throws SpdxInvalidTypeException 
	 */
	public boolean removeValueFromList(PropertyDescriptor propertyDescriptor, Object value) throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "property descriptor can not be null");
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
	 * @param propertyDescriptor Descriptor for the property
	 * @return List of values associated with the objectUri, propertyDescriptor and document
	 * @throws SpdxInvalidTypeException
	 */
	public Iterator<Object> getValueList(PropertyDescriptor propertyDescriptor) throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "property descriptor can not be null");
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
	 * @param propertyDescriptor Descriptor for the property
	 * @return the single value associated with the objectUri, propertyDescriptor and document
	 */
	public Object getValue(PropertyDescriptor propertyDescriptor) {
		Objects.requireNonNull(propertyDescriptor, "property descriptor can not be null");
		return properties.get(propertyDescriptor);
	}
	
	/**
	 * Removes a property from the document for the given ID if the property exists.  Does not raise any exception if the propertyDescriptor does not exist
	 * @param propertyDescriptor Descriptor for the property
	 */
	public void removeProperty(PropertyDescriptor propertyDescriptor) {
		Objects.requireNonNull(propertyDescriptor, "property descriptor can not be null");
		properties.remove(propertyDescriptor);
	}

	/**
	 * Copy all values for this item from another store
	 * @param fromDocumentUri 
	 * @param store
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void copyValuesFrom(IModelStore store) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(store, "Store can not be null");
		List<PropertyDescriptor> propertyDiscriptors = store.getPropertyValueDescriptors(this.getObjectUri());
		for (PropertyDescriptor propertydescriptor:propertyDiscriptors) {
			Optional<Object> value = store.getValue(getObjectUri(), propertydescriptor);
			if (value.isPresent()) {
				this.setValue(propertydescriptor, value.get());
			}
		}
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @return Size of the collection
	 * @throws SpdxInvalidTypeException 
	 */
	@SuppressWarnings("rawtypes")
	public int collectionSize(PropertyDescriptor propertyDescriptor) throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "property descriptor can not be null");
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
			for (Object value:((ConcurrentHashMap<?, ?>)map).values()) {
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
	 * @param propertyDescriptor descriptor for the property
	 * @param value value to be checked
	 * @return true if value is in the list associated with the property descriptor
	 * @throws SpdxInvalidTypeException
	 */
	public boolean collectionContains(PropertyDescriptor propertyDescriptor, Object value) throws SpdxInvalidTypeException {
		Objects.requireNonNull(propertyDescriptor, "property descriptor can not be null");
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
	 * @param propertyDescriptor descriptor for the property
	 * @param clazz class to test against
	 * @return true if the property with the propertyDescriptor can be assigned to clazz
	 * @throws ModelRegistryException 
	 */
	public boolean isCollectionMembersAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) throws ModelRegistryException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(clazz, "Class can not be null");
		Object map = properties.get(propertyDescriptor);
		if (map == null) {
			return true; // It is still assignable to since it is unassigned
		}
		if (!(map instanceof ConcurrentHashMap<?,?>)) {
			logger.warn("Checking collection properites on a non-collection stored item");
			return false;
		}
		@SuppressWarnings("unchecked")
		ConcurrentHashMap<String, List<Object>> idValueMap = (ConcurrentHashMap<String, List<Object>>)map;
		for (List<Object> valueList:idValueMap.values()) {
			for (Object value:valueList) {
				if (!isAssignableTo(value, clazz, getSpecVersion())) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * @param value value to test
	 * @param clazz class to see if the value can be assigned to
	 * @param specVersion version of the spec
	 * @return true if value can be assigned to clazz
	 * @throws ModelRegistryException if the model registry is not property initialized
	 */
	private boolean isAssignableTo(Object value, Class<?> clazz, String specVersion) throws ModelRegistryException {
		if (clazz.isAssignableFrom(value.getClass())) {
			return true;
		}
		if (value instanceof TypedValue) {
			TypedValue typedValue = (TypedValue)value;
			try {
				return clazz.isAssignableFrom(ModelRegistry.getModelRegistry().typeToClass(typedValue.getType(), typedValue.getSpecVersion()));
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
	 * @param propertyDescriptor descriptor for the property
	 * @param clazz class to test against
	 * @param specVersion Version of the spec to test for
	 * @return true if the property can be assigned to type clazz for the latest SPDX spec version
	 * @throws ModelRegistryException if the registry is not property initialized
	 */
	public boolean isPropertyValueAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz, String specVersion) throws ModelRegistryException {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(clazz, "Class can not be null");
		Object value = properties.get(propertyDescriptor);
		if (value == null) {
			return false;
		}
		return isAssignableTo(value, clazz, specVersion);
	}

	/**
	 * @param propertyDescriptor property descriptor
	 * @return true if there is a list associated with the property descriptor
	 */
	public boolean isCollectionProperty(PropertyDescriptor propertyDescriptor) {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Object value = properties.get(propertyDescriptor);
		return value instanceof ConcurrentHashMap;
	}

	/**
	 * @param elementId objectUri for the element to check
	 * @return true if an element using the objectUri is used as a value in a collection
	 */
	public boolean usesId(String elementId) {
		if (Objects.isNull(elementId)) {
			return false;
		}
		Iterator<Object> allValues = this.properties.values().iterator();
		while (allValues.hasNext()) {
			Object value = allValues.next();
			if (value instanceof List && ((List<?>)value).size() > 0 && ((List<?>)value).get(0) instanceof TypedValue) {
				for (Object listValue:(List<?>)value) {
					if (listValue instanceof TypedValue && ((TypedValue) listValue).getObjectUri().toLowerCase().equals(elementId.toLowerCase())) {
						return true;
					}
				}
			} else if (value instanceof TypedValue) {
				if (((TypedValue)value).getObjectUri().toLowerCase().equals(elementId.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
}