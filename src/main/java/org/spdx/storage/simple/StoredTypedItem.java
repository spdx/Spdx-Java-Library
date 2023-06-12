package org.spdx.storage.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.IndividualUriValue;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.TypedValue;
import org.spdx.library.model.compat.v2.ModelObject;
import org.spdx.library.model.compat.v2.enumerations.SpdxEnumFactory;
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
	
	static Set<String> SPDX_CLASSES = new HashSet<>(Arrays.asList(SpdxConstantsCompatV2.ALL_SPDX_CLASSES));

	private ConcurrentHashMap<PropertyDescriptor, Object> properties = new ConcurrentHashMap<>();
	
	private int referenceCount = 0;
	
	private final ReadWriteLock countLock = new ReentrantReadWriteLock();
	
	public StoredTypedItem(String objectUri, String type) throws InvalidSPDXAnalysisException {
		super(objectUri, type);
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
		if (value instanceof ModelObject) {
			throw new SpdxInvalidTypeException("Can not store Model Object in store.  Convert to TypedValue first");
		} else if (value instanceof List || value instanceof Collection) {
			throw new SpdxInvalidTypeException("Can not store list values directly.  Use addValueToCollection.");
		} else if (!value.getClass().isPrimitive() && 
				!value.getClass().isAssignableFrom(String.class) &&
				!value.getClass().isAssignableFrom(Boolean.class) &&
				!value.getClass().isAssignableFrom(Integer.class) &&
				!value.getClass().isAssignableFrom(TypedValue.class) &&
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
		if (value instanceof ModelObject) {
			throw new SpdxInvalidTypeException("Can not store Model Object in store.  Convert to TypedValue first");
		} else if (!value.getClass().isPrimitive() && 
				!value.getClass().isAssignableFrom(String.class) &&
				!value.getClass().isAssignableFrom(Boolean.class) &&
				!value.getClass().isAssignableFrom(Integer.class) &&
				!value.getClass().isAssignableFrom(TypedValue.class) &&
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
	 * @return true if the property with the propertyDescriptor can be assigned to clazz for the latest SPDX version
	 */
	public boolean isCollectionMembersAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		return isCollectionMembersAssignableTo(propertyDescriptor, clazz, SpdxMajorVersion.latestVersion());
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @param clazz class to test against
	 * @param specVersion Version of the SPDX Spec
	 * @return true if the property with the propertyDescriptor can be assigned to clazz
	 */
	public boolean isCollectionMembersAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz, SpdxMajorVersion specVersion) {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(clazz, "Class can not be null");
		Object map = properties.get(propertyDescriptor);
		if (map == null) {
			return true; // It is still assignable to since it is unassigned
		}
		if (!(map instanceof ConcurrentHashMap<?,?>)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		ConcurrentHashMap<String, List<Object>> idValueMap = (ConcurrentHashMap<String, List<Object>>)map;
		for (List<Object> valueList:idValueMap.values()) {
			for (Object value:valueList) {
				if (!clazz.isAssignableFrom(value.getClass())) {
					if (value instanceof IndividualUriValue) {
						String uri = ((IndividualUriValue)value).getIndividualURI();
						Enum<?> spdxEnum = SpdxEnumFactory.uriToEnum.get(uri);
						if (Objects.nonNull(spdxEnum)) {
							if (!clazz.isAssignableFrom(spdxEnum.getClass())) {
								return false;
							}
						} else if (!(SpdxConstantsCompatV2.URI_VALUE_NOASSERTION.equals(uri) ||
								SpdxConstantsCompatV2.URI_VALUE_NONE.equals(uri))) {
							return false;
						}
					} else if (value instanceof TypedValue) {
						try {
							if (clazz != TypedValue.class && !clazz.isAssignableFrom(SpdxModelFactory.typeToClass(((TypedValue)value).getType(), specVersion))) {
								return false;
							}
						} catch (InvalidSPDXAnalysisException e) {
							logger.error("Error converting typed value to class",e);
							return false;
						}
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * @param propertyDescriptor descriptor for the property
	 * @param clazz class to test against
	 * @return true if the property can be assigned to type clazz for the latest SPDX spec version
	 */
	public boolean isPropertyValueAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		return isPropertyValueAssignableTo(propertyDescriptor, clazz, SpdxMajorVersion.latestVersion());
	}

	/**
	 * @param propertyDescriptor descriptor for the property
	 * @param clazz class to test against
	 * @param specVersion Version of the SPDX Spec
	 * @return true if the property can be assigned to type clazz
	 */
	public boolean isPropertyValueAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz, SpdxMajorVersion specVersion) {
		Objects.requireNonNull(propertyDescriptor, "Property descriptor can not be null");
		Objects.requireNonNull(clazz, "Class can not be null");
		Object value = properties.get(propertyDescriptor);
		if (value == null) {
			return false;
		}
		if (clazz.isAssignableFrom(value.getClass())) {
			return true;
		}
		if (value instanceof TypedValue) {
			try {
				return clazz.isAssignableFrom(SpdxModelFactory.typeToClass(((TypedValue)value).getType(), specVersion));
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Error converting typed value to class",e);
				return false;
			}
		}
		if (value instanceof IndividualUriValue) {
			String uri = ((IndividualUriValue)value).getIndividualURI();
			if (SpdxConstantsCompatV2.URI_VALUE_NOASSERTION.equals(uri)) {
				return true;
			}
			if (SpdxConstantsCompatV2.URI_VALUE_NONE.equals(uri)) {
				return true;
			}
			Enum<?> spdxEnum = SpdxEnumFactory.uriToEnum.get(uri);
			if (Objects.nonNull(spdxEnum)) {
				return clazz.isAssignableFrom(spdxEnum.getClass());
			} else {
				return false;
			}
		}
		return false;
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