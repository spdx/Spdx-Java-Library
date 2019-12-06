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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.license.ListedLicenses;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.IModelStore.ModelUpdate;

/**
 * @author Gary O'Neall
 * 
 * Superclass for all SPDX model objects
 * 
 * Provides the primary interface to the storage class that access and stores the data for 
 * the model objects.
 * 
 * This class includes several helper methods to manage the storage and retrieval of properties.
 * 
 * Each model object is in itself stateless.  All state is maintained in the Model Store.  
 * The Document URI uniquely identifies the document containing the model object.
 * 
 * The concrete classes are expected to implements getters for the model class properties which translate
 * into calls to the getTYPEPropertyValue where TYPE is the type of value to be returned and the property name
 * is passed as a parameter.
 * 
 * There are 2 methods of setting values:
 *   - call the setPropertyValue, clearValueCollection or addValueToCollection methods - this will call the modelStore and store the
 *     value immediately
 *   - Gather a list of updates by calling the updatePropertyValue, updateClearValueList, or updateAddPropertyValue
 *     methods.  These methods return a ModelUpdate which can be applied later by calling the <code>apply()</code> method.
 *     A convenience method <code>Write.applyUpdatesInOneTransaction</code> will perform all updates within
 *     a single transaction. This method may result in higher performance updates for some Model Store implementations.
 *     Note that none of the updates will be applied until the storage manager update method is invoked.
 * 
 * Property values are restricted to the following types:
 *   - String - Java Strings
 *   - Booolean - Java Boolean or primitive boolean types
 *   - ModelObject - A concrete subclass of this type
 *   - Collection<T> - A Collection of type T where T is one of the supported non-collection types
 *     
 * This class also handles the conversion of a ModelObject to and from a TypeValue for storage in the ModelStore.
 *
 */
public abstract class ModelObject implements SpdxConstants {

	private IModelStore modelStore;
	private String documentUri;
	private String id;
	/**
	 * If set to true, a reference made to a model object stored in a different modelStore and/or
	 * document will be copied to this modelStore and documentUri
	 */
	private boolean copyOnReference = true;

	/**
	 * Create a new Model Object using an Anonomous ID with the defualt store and default document URI
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ModelObject() throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore().getNextId(IdType.Anonomous, DefaultModelStore.getDefaultDocumentUri()));
	}
	
	/**
	 * Open or create a model object with the default store and default document URI
	 * @param id ID for this object - must be unique within the SPDX document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ModelObject(String id) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), id, true);
	}
	
	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this model
	 * @param id ID for this object - must be unique within the SPDX document
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelObject(IModelStore modelStore, String documentUri, String id, boolean create) throws InvalidSPDXAnalysisException {
		this.modelStore = modelStore;
		this.documentUri = documentUri;
		this.id = id;
		if (modelStore == null) {
			throw new InvalidSPDXAnalysisException("Missing required model store") ;
		}
		if (!modelStore.exists(documentUri, id)) {
			if (create) {
				modelStore.create(documentUri, id, getType());
			} else {
				throw new SpdxIdNotFoundException(id+" does not exist in document "+documentUri);
			}
		}
	}
	
	// Abstract methods that must be implemented in the subclasses
	/**
	 * @return The class name for this object.  Class names are defined in the constants file
	 */
	public abstract String getType();
	
	/**
	 * @return Any verification errors or warnings associated with this object
	 */
	public abstract List<String> verify();
	
	/**
	 * @return the Document URI for this object
	 */
	public String getDocumentUri() {
		return this.documentUri;
	}
	
	/**
	 * @return ID for the object
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the model store for this object
	 */
	public IModelStore getModelStore() {
		return this.modelStore;
	}
	
	//The following methods are to manage the properties associated with the model object
	/**
	 * @return all names of property values currently associated with this object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public List<String> getPropertyValueNames() throws InvalidSPDXAnalysisException {
		return modelStore.getPropertyValueNames(documentUri, id);
	}
	
	/**
	 * Get an object value for a property
	 * @param propertyName Name of the property
	 * @return value associated with a property
	 */
	public Optional<Object> getObjectPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		return getObjectPropertyValue(modelStore, documentUri, id, propertyName);
	}
	
	/**
	 * Get an object value for a property
	 * @param stModelStore
	 * @param stDocumentUri
	 * @param stId
	 * @param propertyName
	 * @return value associated with a property
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Optional<Object> getObjectPropertyValue(IModelStore stModelStore, String stDocumentUri,
			String stId, String propertyName) throws InvalidSPDXAnalysisException {
		if (stModelStore.isCollectionProperty(stDocumentUri, stId, propertyName)) {
			return Optional.of(new ModelCollection<>(stModelStore, stDocumentUri, stId, propertyName));
		}
		Optional<Object> result =  stModelStore.getValue(stDocumentUri, stId, propertyName);
		if (result.isPresent() && result.get() instanceof TypedValue) {
			TypedValue tv = (TypedValue)result.get();
			result = Optional.of(SpdxModelFactory.createModelObject(stModelStore, stDocumentUri, tv.getId(), tv.getType()));
		}
		return result;
	}

	/**
	 * Set a property value for a property name, creating the property if necessary
	 * @param stModelStore Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId ID of the item to associate the property with
	 * @param propertyName Name of the property associated with this object
	 * @param value Value to associate with the property
	 * @param copyOnReference if true, any ModelObject property value not stored in the stModelStore under the stDocumentUri will be copied to make it available
	 * @throws InvalidSPDXAnalysisException
	 */
	public static void setPropertyValue(IModelStore stModelStore, String stDocumentUri, 
			String stId, String propertyName, Object value, boolean copyOnReference) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(stModelStore);
		Objects.requireNonNull(stDocumentUri);
		Objects.requireNonNull(stId);
		Objects.requireNonNull(propertyName);
		if (value == null) {
			// we just remove the value
			removeProperty(stModelStore, stDocumentUri, stId, propertyName);
		} else if (value instanceof ModelObject) {
			ModelObject mValue = (ModelObject)value;
			if (!mValue.getModelStore().equals(stModelStore)) {
				if (!copyOnReference) {
					throw(new InvalidSPDXAnalysisException("Can set a property value to a Model Object stored in a different model store"));
				}
				if (!stModelStore.exists(stDocumentUri, mValue.getId())) {
					stModelStore.create(stDocumentUri, mValue.getId(), mValue.getType());
				}
				copy(stModelStore, stDocumentUri, mValue.getId(), mValue.getModelStore(), mValue.getDocumentUri(), mValue.getId(), mValue.getType());
			}
			stModelStore.setValue(stDocumentUri, stId, propertyName, mValue.toTypedValue());
		} else if (value instanceof Collection) {
			replacePropertyValueCollection(stModelStore, stDocumentUri, stId, propertyName, (Collection<?>)value, copyOnReference);
		} else if (value instanceof String || value instanceof Boolean) {
			stModelStore.setValue(stDocumentUri, stId, propertyName, value);
		} else {
			throw new SpdxInvalidTypeException("Property value type not supported: "+value.getClass().getName());
		}
	}
	
	/**
	 * Set a property value for a property name, creating the property if necessary
	 * @param propertyName Name of the property associated with this object
	 * @param value Value to associate with the property
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setPropertyValue(String propertyName, Object value) throws InvalidSPDXAnalysisException {
		setPropertyValue(this.modelStore, this.documentUri, this.id, propertyName, value, copyOnReference);
	}
	
	/**
	 * Create an update when, when applied by the ModelStore, sets a property value for a property name, creating the property if necessary
	 * @param propertyName Name of the property associated with this object
	 * @param value Value to associate with the property
	 * @return an update which can be applied by invoking the apply method
	 */
	public ModelUpdate updatePropertyValue(String propertyName, Object value) {
		return () ->{
			setPropertyValue(this.modelStore, this.documentUri, this.id, propertyName, value, copyOnReference);
		};
	}
	
	/**
	 * @param propertyName Name of a property
	 * @return the Optional String value associated with a property, null if no value is present
	 * @throws SpdxInvalidTypeException
	 */
	public Optional<String> getStringPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> result = getObjectPropertyValue(propertyName);
		Optional<String> retval;
		if (result.isPresent()) {
			if (!(result.get() instanceof String)) {
				throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type String");
			}
			retval = Optional.of((String)result.get());
		} else {
			retval = Optional.empty();
		}
		return retval;
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return the Optional Boolean value for a property
	 * @throws SpdxInvalidTypeException
	 */
	public Optional<Boolean> getBooleanPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> result = getObjectPropertyValue(propertyName);
		Optional<Boolean> retval;
		if (result.isPresent()) {
			if (!(result.get() instanceof Boolean)) {
				throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type Boolean");
			}
			retval = Optional.of((Boolean)result.get());
		} else {
			retval = Optional.empty();
		}
		return retval;
	}
	
	/**
	 * Removes a property and its value from the model store if it exists
	 * @param stModelStore Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId ID of the item to associate the property with
	 * @param propertyName Name of the property associated with this object to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	public static void removeProperty(IModelStore stModelStore, String stDocumentUri, String stId, String propertyName) throws InvalidSPDXAnalysisException {
		stModelStore.removeProperty(stDocumentUri, stId, propertyName);
	}
	
	/**
	 * Removes a property and its value from the model store if it exists
	 * @param propertyName Name of the property associated with this object to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	public void removeProperty(String propertyName) throws InvalidSPDXAnalysisException {
		removeProperty(modelStore, documentUri, id, propertyName);
	}
	
	/**
	 * Create an update when, when applied by the ModelStore, removes a property and its value from the model store if it exists
	 * @param propertyName Name of the property associated with this object to be removed
	 * @return  an update which can be applied by invoking the apply method
	 */
	public ModelUpdate updateRemoveProperty(String propertyName) {
		return () -> {
			removeProperty(modelStore, documentUri, id, propertyName);
		};
	}
	
	// The following methods manage collections of values associated with a property
	/**
	 * Clears a collection of values associated with a property creating the property if it does not exist
	 * @param stModelStore Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId ID of the item to associate the property with
	 * @param propertyName Name of the property
	 * @throws InvalidSPDXAnalysisException
	 */
	public static void clearValueCollection(IModelStore stModelStore, String stDocumentUri, String stId, String propertyName) throws InvalidSPDXAnalysisException {
		stModelStore.clearValueCollection(stDocumentUri, stId, propertyName);
	}
	
	/**
	 * Clears a collection of values associated with a property
	 * @param propertyName Name of the property
	 */
	public void clearValueCollection(String propertyName) throws InvalidSPDXAnalysisException {
		clearValueCollection(modelStore, documentUri, id, propertyName);
	}
	
	/**
	 * Create an update when, when applied by the ModelStore, clears a collection of values associated with a property
	 * @param propertyName Name of the property
	 * @return an update which can be applied by invoking the apply method
	 */
	public ModelUpdate updateClearValueCollection(String propertyName) {
		return () ->{
			clearValueCollection(modelStore, documentUri, id, propertyName);
		};
	}
	
	/**
	 * Add a value to a collection of values associated with a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param stModelStore Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId ID of the item to associate the property with
	 * @param propertyName  Name of the property
	 * @param value to add
	 * @param copyOnReference if true, any ModelObject property value not stored in the stModelStore under the stDocumentUri will be copied to make it available
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static void addValueToCollection(IModelStore stModelStore, String stDocumentUri, String stId, 
			String propertyName, Object value, boolean copyOnReference) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(value);
		if (value instanceof ModelObject) {
			ModelObject mValue = (ModelObject)value;
			if (!mValue.getModelStore().equals(stModelStore)) {
				if (!stModelStore.exists(stDocumentUri, mValue.getId())) {
					if (!copyOnReference) {
						throw(new InvalidSPDXAnalysisException("Can set a property value to a Model Object stored in a different model store"));
					}
					copy(stModelStore, stDocumentUri, mValue.getId(), mValue.getModelStore(), mValue.getDocumentUri(), mValue.getId(), mValue.getType());
				}
			}
			stModelStore.addValueToCollection(stDocumentUri, stId, propertyName, new TypedValue(stDocumentUri, mValue.getId(), mValue.getType()));
		} else if (value instanceof String || value instanceof Boolean) {
			stModelStore.addValueToCollection(stDocumentUri, stId, propertyName, value);
		} else {
			throw new SpdxInvalidTypeException("Unsupported type to add to a collection: "+value.getClass().getName());
		}
	}
	
	/**
	 * Copy an item from one Model Object Store to another
	 * @param toStore Model Store to copy to
	 * @param toDocumentUri Target document URI
	 * @param toId target Id
	 * @param fromStore Model Store containing the source item
	 * @param fromDocumentUri Document URI for the source item
	 * @param fromId ID source ID
	 * @param stType Type to copy
	 * @throws InvalidSPDXAnalysisException
	 */
	public static void copy(IModelStore toStore, String toDocumentUri, String toId, IModelStore fromStore, String fromDocumentUri, String fromId, String stType) throws InvalidSPDXAnalysisException {
		if (!toStore.exists(toDocumentUri, toId)) {
			toStore.create(toDocumentUri, toId, stType);
		}
		List<String> propertyNames = fromStore.getPropertyValueNames(fromDocumentUri, fromId);
		for (String propName:propertyNames) {
			if (fromStore.isCollectionProperty(fromDocumentUri, fromId, propName)) {
				List<Object> fromList = fromStore.getValueList(fromDocumentUri, fromId, propName);
				for (Object listItem:fromList) {
					if (listItem instanceof TypedValue) {
						TypedValue listItemTv = (TypedValue)listItem;
						copy(toStore, toDocumentUri, listItemTv.getId(), fromStore, fromDocumentUri, listItemTv.getId(), listItemTv.getType());
						toStore.addValueToCollection(toDocumentUri, toId, propName, new TypedValue(toDocumentUri, listItemTv.getId(), listItemTv.getType()));
					} else {
						toStore.addValueToCollection(toDocumentUri, toId, propName, listItem);
					}
				}
			} else {
				Optional<Object> result =  fromStore.getValue(fromDocumentUri, fromId, propName);
				if (result.isPresent()) {
					if (result.get() instanceof TypedValue) {
						TypedValue tv = (TypedValue)result.get();
						copy(toStore, toDocumentUri, tv.getId(), fromStore, fromDocumentUri, tv.getId(), tv.getType());
						toStore.setValue(toDocumentUri, toId, propName, new TypedValue(toDocumentUri, tv.getId(), tv.getType()));
					} else {
						toStore.setValue(toDocumentUri, toId, propName, result.get());
					}
				}
			}
		}
	}
	
	/**
	 * Add a value to a collection of values associated with a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param propertyName  Name of the property
	 * @param value to add
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void addPropertyValueToCollection(String propertyName, Object value) throws InvalidSPDXAnalysisException {
		addValueToCollection(modelStore, documentUri, id, propertyName, value, copyOnReference);
	}
	
	/**
	 * Create an update when, when applied, adds a value to a collection of values associated with a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param propertyName  Name of the property
	 * @param value to add
	 * @return an update which can be applied by invoking the apply method
	 */
	public ModelUpdate updateAddPropertyValueToCollection(String propertyName, Object value) {
		return () ->{
			addValueToCollection(modelStore, documentUri, id, propertyName, value, copyOnReference);
		};
	}
	
	/**
	 * Replace the entire value collection for a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param stModelStore Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId ID of the item to associate the property with
	 * @param propertyName name of the property
	 * @param values collection of new properties
	 * @param copyOnReference if true, any ModelObject property value not stored in the stModelStore under the stDocumentUri will be copied to make it available
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static void replacePropertyValueCollection(IModelStore stModelStore, String stDocumentUri, String stId, 
			String propertyName, Collection<?> values, boolean copyOnReference) throws InvalidSPDXAnalysisException {
		clearValueCollection(stModelStore, stDocumentUri, stId, propertyName);
		for (Object value:values) {
			addValueToCollection(stModelStore, stDocumentUri, stId, propertyName, value, copyOnReference);
		}
	}
	

	/**
	 * Remove a property value from a collection
	 * @param stModelStore Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId ID of the item to associate the property with
	 * @param propertyName name of the property
	 * @param value Value to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	public static void removePropertyValueFromCollection(IModelStore stModelStore, String stDocumentUri, String stId, 
			String propertyName, Object value) throws InvalidSPDXAnalysisException {
		if (value instanceof ModelObject) {
			stModelStore.removeValueFromCollection(stDocumentUri, stId, propertyName, ((ModelObject)value).toTypedValue());
		} else {
			stModelStore.removeValueFromCollection(stDocumentUri, stId, propertyName, value);
		}
	}
	
	/**
	 * Remove a property value from a collection
	 * @param propertyName name of the property
	 * @param value Value to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	public void removePropertyValueFromCollection(String propertyName, Object value) throws InvalidSPDXAnalysisException {
		removePropertyValueFromCollection(modelStore, documentUri, id, propertyName, value);
	}
	
	/**
	 * Create an update when, when applied, removes a property value from a collection
	 * @param propertyName name of the property
	 * @param value Value to be removed
	 * @return an update which can be applied by invoking the apply method
	 */
	public ModelUpdate updateRemovePropertyValueFromCollection(String propertyName, Object value) {
		return () -> {
			removePropertyValueFromCollection(modelStore, documentUri, id, propertyName, value);
		};
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return collection of values associated with a property
	 */
	public ModelCollection<Object> getObjectPropertyValueCollection(String propertyName) throws InvalidSPDXAnalysisException {
		return new ModelCollection<Object>(this.modelStore, this.documentUri, this.id, propertyName);
	}
	
	/**
	 * @param propertyName Name of property
	 * @return Collection of Strings associated with the property
	 * @throws SpdxInvalidTypeException
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getStringCollection(String propertyName) throws InvalidSPDXAnalysisException {
		if (!isCollectionMembersAssignableTo(propertyName, String.class)) {
			throw new SpdxInvalidTypeException("Property "+propertyName+" does not contain a collection of Strings");
		}
		return (Collection<String>)(Collection<?>)getObjectPropertyValueCollection(propertyName);
	}
	
	public boolean isCollectionMembersAssignableTo(String propertyName, Class<?> clazz) throws InvalidSPDXAnalysisException {
		if (ModelObject.class.isAssignableFrom(clazz)) {
			return modelStore.isCollectionMembersAssignableTo(this.documentUri, this.id, propertyName, TypedValue.class);
			//TODO: We should also check the specific types in the typed values for a match
		} else {
			return modelStore.isCollectionMembersAssignableTo(this.documentUri, this.id, propertyName, clazz);
		}
	}
	
	/**
	 * @param compare
	 * @return true if all the properties have the same or equivalent values
	 */
	public boolean equivalent(ModelObject compare) throws InvalidSPDXAnalysisException {
		if (!this.getClass().equals(compare.getClass())) {
			return false;
		}
		List<String> propertyValueNames = getPropertyValueNames();
		List<String> comparePropertyValueNames = new ArrayList<String>(compare.getPropertyValueNames());	// create a copy since we're going to modify it
		for (String propertyName:propertyValueNames) {
			if (comparePropertyValueNames.contains(propertyName)) {
				Optional<Object> myValue = this.getObjectPropertyValue(propertyName);
				Optional<Object> compareValue = compare.getObjectPropertyValue(propertyName);
				if (!myValue.isPresent()) {
					if (compareValue.isPresent()) {
						return false;
					}
				} else if (!compareValue.isPresent()) {
					return false;
				} else if (myValue.get() instanceof ModelCollection && compareValue.get() instanceof ModelCollection) {
					List<?> myList = ((ModelCollection<?>)myValue.get()).toImmutableList();
					List<?> compareList = ((ModelCollection<?>)compareValue.get()).toImmutableList();
					if (!listsEquivalent(myList, compareList)) {
						return false;
					}
				} else if (myValue.get() instanceof List && compareValue.get() instanceof List) {
					if (!listsEquivalent((List<?>)myValue.get(), (List<?>)compareValue.get())) {
						return false;
					}
				} else if (myValue.get() instanceof ModelObject && compareValue.get() instanceof ModelObject) {
					if (!((ModelObject)myValue.get()).equivalent(((ModelObject)compareValue.get()))) {
						return false;
					}
					
				} else if (!Objects.equals(myValue, compareValue)) {	// Present, not a list, and not a TypedValue
					return false;
				}
				comparePropertyValueNames.remove(propertyName);
			} else {
				// No property value
				if (!this.getObjectPropertyValue(propertyName).isPresent()) {
					return false;
				}
			}
		}
		for (String propertyName:comparePropertyValueNames) {	// check any remaining property values
			if (!compare.getObjectPropertyValue(propertyName).isPresent()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param l1
	 * @param l2
	 * @return true if the two lists are equivalent
	 * @throws InvalidSPDXAnalysisException
	 */
	private boolean listsEquivalent(List<?> l1, List<?> l2) throws InvalidSPDXAnalysisException {
		int numRemainingComp = l2.size();
		for (Object item:l1) {
			if (l2.contains(item)) {
				numRemainingComp--;
			} else {
				if (item instanceof ModelObject) {
					// Need to check for equiv.
					boolean found = false;
					for (Object compareItem:l2) {
						if (compareItem instanceof ModelObject) {
							if (((ModelObject)item).equivalent(((ModelObject)compareItem))) {
								found = true;
								break;
							}
						}
					}
					if (found) {
						numRemainingComp--;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return numRemainingComp <= 0;
	}

	@Override
	public int hashCode() {
		if (this.id != null) {
			return this.id.toLowerCase().hashCode() ^ this.documentUri.hashCode();
		} else {
			return 0;
		}
	}
	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ModelObject)) {
			// covers o == null, as null is not an instance of anything
			return false;
		}
		ModelObject comp = (ModelObject)o;
		return Objects.equals(id, comp.getId()) && Objects.equals(documentUri, comp.getDocumentUri());
	}
	

	
	/**
	 * Clone a new object using a different model store
	 * @param modelStore
	 * @return
	 */
	public ModelObject clone(IModelStore modelStore) {
		try {
			return SpdxModelFactory.createModelObject(modelStore, this.documentUri, this.id, this.getType());
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Copy all the properties from the source object
	 * @param source
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void copyFrom(ModelObject source) throws InvalidSPDXAnalysisException {
		copy(this.modelStore, this.documentUri, this.id, source.getModelStore(), source.getDocumentUri(), source.getId(), this.getType());
	}
	
	/**
	 * @param id String for the object
	 * @return type of the ID
	 */
	IdType idToIdType(String id) {
		if (id.startsWith(NON_STD_LICENSE_ID_PRENUM)) {
			return IdType.LicenseRef;
		} else if (id.startsWith(SPDX_ELEMENT_REF_PRENUM)) {
			return IdType.SpdxId;
		} else if (id.startsWith(EXTERNAL_DOC_REF_PRENUM)) {
			return IdType.DocumentRef;
		} else if (ListedLicenses.getListedLicenses().isSpdxListedLicenseId(id)) {
			return IdType.ListedLicense;
		} else if ("none".equalsIgnoreCase(id) || "noassertion".equalsIgnoreCase(id)) {
			return IdType.Literal;
		} else {
			return IdType.Anonomous;
		}
	}
	
	TypedValue toTypedValue() throws InvalidSPDXAnalysisException {
		return new TypedValue(this.documentUri, this.id, this.getType());
	}
}
