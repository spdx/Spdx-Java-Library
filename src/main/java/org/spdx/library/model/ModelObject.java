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

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.Version;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.ReferenceCategory;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.library.model.enumerations.SpdxEnumFactory;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ConjunctiveLicenseSet;
import org.spdx.library.model.license.CrossRef.CrossRefBuilder;
import org.spdx.library.model.license.DisjunctiveLicenseSet;
import org.spdx.library.model.license.ListedLicenses;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.library.model.license.SpdxNoneLicense;
import org.spdx.library.model.pointer.ByteOffsetPointer;
import org.spdx.library.model.pointer.LineCharPointer;
import org.spdx.library.model.pointer.SinglePointer;
import org.spdx.library.model.pointer.StartEndPointer;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
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
 *   - {@literal Collection<T>} - A Collection of type T where T is one of the supported non-collection types
 *     
 * This class also handles the conversion of a ModelObject to and from a TypeValue for storage in the ModelStore.
 *
 */
public abstract class ModelObject {
	
	static final Logger logger = LoggerFactory.getLogger(ModelObject.class);

	private IModelStore modelStore;
	private String documentUri;
	private String id;
	
	/**
	 * If non null, a reference made to a model object stored in a different modelStore and/or
	 * document will be copied to this modelStore and documentUri
	 */
	private ModelCopyManager copyManager = null;
	
	/**
	 * if true, checks input values for setters to verify valid SPDX inputs
	 */
	protected boolean strict = true;
	
	// the following fields are for debugging when equivalent returns false
	enum NotEquivalent {
		DIFFERENT_CLASS, MISSING_PROPERTY, PROPERTY_NOT_EQUIVALENT, COMPARE_PROPERTY_MISSING};
	static class NotEquivalentReason {
		NotEquivalent reason;
		String property = null;
		
		public NotEquivalentReason(NotEquivalent reason) {
			this.reason = reason;
		}
		
		public NotEquivalentReason(NotEquivalent reason, String property) {
			this(reason);
			this.property = property;
		}

		/**
		 * @return the reason
		 */
		public NotEquivalent getReason() {
			return reason;
		}

		/**
		 * @param reason the reason to set
		 */
		public void setReason(NotEquivalent reason) {
			this.reason = reason;
		}

		/**
		 * @return the property
		 */
		public String getProperty() {
			return property;
		}

		/**
		 * @param property the property to set
		 */
		public void setProperty(String property) {
			this.property = property;
		}
	}
	
	
	NotEquivalentReason lastNotEquivalentReason = null;
	

	/**
	 * Create a new Model Object using an Anonymous ID with the defualt store and default document URI
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ModelObject() throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore().getNextId(IdType.Anonymous, DefaultModelStore.getDefaultDocumentUri()));
	}
	
	/**
	 * Open or create a model object with the default store and default document URI
	 * @param id ID for this object - must be unique within the SPDX document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ModelObject(String id) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), id, 
				DefaultModelStore.getDefaultCopyManager(), true);
	}
	
	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this model
	 * @param id ID for this object - must be unique within the SPDX document
	 * @param copyManager - if supplied, model objects will be implictly copied into this model store and document URI when referenced by setting methods
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelObject(IModelStore modelStore, String documentUri, String id, @Nullable ModelCopyManager copyManager, 
			boolean create) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore, "Model Store can not be null");
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		Objects.requireNonNull(id, "ID can not be null");
		this.modelStore = modelStore;
		this.documentUri = documentUri;
		this.id = id;
		this.copyManager = copyManager;
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
	 * Implementation of the specific verifications for this model object
	 * @param specVersion Version of the SPDX spec to verify against
	 * @param verifiedElementIds list of all Element Id's which have already been verified - prevents infinite recursion
	 * @return Any verification errors or warnings associated with this object
	 */
	protected abstract List<String> _verify(List<String> verifiedElementIds, String specVersion);
	
	/**
	 * @param specVersion Version of the SPDX spec to verify against
	 * @param verifiedIElementds list of all element Id's which have already been verified - prevents infinite recursion
	 * @return Any verification errors or warnings associated with this object
	 */
	public List<String> verify(List<String> verifiedIElementds, String specVersion) {
		if (verifiedIElementds.contains(this.id)) {
			return new ArrayList<>();
		} else {
			// The verifiedElementId is added in the SpdxElement._verify method
			return _verify(verifiedIElementds, specVersion);
		}
	}
	
	/**
	 * Verifies against the more recent supported specification version
	 * @return Any verification errors or warnings associated with this object
	 */
	public List<String> verify() {
		return verify(Version.CURRENT_SPDX_VERSION);
	}
	/**
	 * @param specVersion Version of the SPDX spec to verify against
	 * @return Any verification errors or warnings associated with this object
	 */
	public List<String> verify(String specVersion) {
		return verify(new ArrayList<String>(), specVersion);
	}
	
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
	
	/**
	 * @return if strict input checking is enabled
	 */
	public boolean isStrict() {
		return strict;
	}
	
	/**
	 * @param strict if true, inputs will be validated against the SPDX spec
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
	/**
	 * Enter a critical section. leaveCriticialSection must be called.
	 * @param readLockRequested true implies a read lock, false implies write lock.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public IModelStoreLock enterCriticalSection(boolean readLockRequested) throws InvalidSPDXAnalysisException {
		return this.getModelStore().enterCriticalSection(this.getDocumentUri(), readLockRequested);
	}
	
	/**
	 * Leave a critical section. Releases the lock form the matching enterCriticalSection
	 */
	public void leaveCriticalSection(IModelStoreLock lock) {
		this.getModelStore().leaveCriticalSection(lock);
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
	protected Optional<Object> getObjectPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(modelStore, documentUri, id, propertyName, copyManager);
		if (retval.isPresent() && retval.get() instanceof ModelObject && !strict) {
			((ModelObject)retval.get()).setStrict(strict);
		}
		return retval;
	}
	
	/**
	 * Get an object value for a property
	 * @param stModelStore
	 * @param stDocumentUri
	 * @param stId
	 * @param propertyName
	 * @param copyManager if non null, any ModelObject property value not stored in the stModelStore under the stDocumentUri will be copied to make it available
	 * @return value associated with a property
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static Optional<Object> getObjectPropertyValue(IModelStore stModelStore, String stDocumentUri,
			String stId, String propertyName, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (!stModelStore.exists(stDocumentUri, stId)) {
			return Optional.empty();
		} else if (stModelStore.isCollectionProperty(stDocumentUri, stId, propertyName)) {
			return Optional.of(new ModelCollection<>(stModelStore, stDocumentUri, stId, propertyName, copyManager, null));
		} else {
			return ModelStorageClassConverter.optionalStoredObjectToModelObject(stModelStore.getValue(stDocumentUri, stId, propertyName), 
					stDocumentUri, stModelStore, copyManager);
		}
	}

	/**
	 * Set a property value for a property name, creating the property if necessary
	 * @param stModelStore Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId ID of the item to associate the property with
	 * @param propertyName Name of the property associated with this object
	 * @param value Value to associate with the property
	 * @param copyManager if non null, any ModelObject property value not stored in the stModelStore under the stDocumentUri will be copied to make it available
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static void setPropertyValue(IModelStore stModelStore, String stDocumentUri, 
			String stId, String propertyName, @Nullable Object value, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(stModelStore, "Model Store can not be null");
		Objects.requireNonNull(stDocumentUri, "Document Uri can not be null");
		Objects.requireNonNull(stId, "ID can not be null");
		Objects.requireNonNull(propertyName, "Property name can not be null");
		if (value == null) {
			// we just remove the value
			removeProperty(stModelStore, stDocumentUri, stId, propertyName);
		} else if (value instanceof Collection) {
			replacePropertyValueCollection(stModelStore, stDocumentUri, stId, propertyName, (Collection<?>)value, copyManager);
		} else {
			stModelStore.setValue(stDocumentUri, stId, propertyName, ModelStorageClassConverter.modelObjectToStoredObject(
					value, stDocumentUri, stModelStore, copyManager));
		}
	}
	
	/**
	 * Set a property value for a property name, creating the property if necessary
	 * @param propertyName Name of the property associated with this object
	 * @param value Value to associate with the property
	 * @throws InvalidSPDXAnalysisException 
	 */
	protected void setPropertyValue(String propertyName, @Nullable Object value) throws InvalidSPDXAnalysisException {
		if (this instanceof IndividualUriValue) {
			throw new InvalidSPDXAnalysisException("Can not set a property for the literal value "+((IndividualUriValue)this).getIndividualURI());
		}
		setPropertyValue(this.modelStore, this.documentUri, this.id, propertyName, value, copyManager);
	}
	
	/**
	 * Create an update when, when applied by the ModelStore, sets a property value for a property name, creating the property if necessary
	 * @param propertyName Name of the property associated with this object
	 * @param value Value to associate with the property
	 * @return an update which can be applied by invoking the apply method
	 */
	protected ModelUpdate updatePropertyValue(String propertyName, Object value) {
		return () ->{
			setPropertyValue(this.modelStore, this.documentUri, this.id, propertyName, value, copyManager);
		};
	}
	
	/**
	 * @param propertyName Name of a property
	 * @return the Optional String value associated with a property, null if no value is present
	 * @throws SpdxInvalidTypeException
	 */
	protected Optional<String> getStringPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> result = getObjectPropertyValue(propertyName);
		if (result.isPresent()) {
			if (result.get() instanceof String) {
				return Optional.of((String)result.get());
			} else if (result.get() instanceof IndividualUriValue) {
				String uri = ((IndividualUriValue)result.get()).getIndividualURI();
				if (SpdxConstants.URI_VALUE_NONE.equals(uri)) {
					return Optional.of(SpdxConstants.NONE_VALUE);
				} else if (SpdxConstants.URI_VALUE_NOASSERTION.equals(uri)) {
					return Optional.of(SpdxConstants.NOASSERTION_VALUE);
				} else {
					logger.error("Can not convert a URI value to String: "+uri);
					throw new SpdxInvalidTypeException("Can not convert a URI value to String: "+uri);
				}
			} else {
				logger.error("Property "+propertyName+" is not of type String");
				throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type String");
			}
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * @param propertyName Name of a property
	 * @return the Optional Integer value associated with a property, null if no value is present
	 * @throws InvalidSPDXAnalysisException
	 */
	protected Optional<Integer> getIntegerPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> result = getObjectPropertyValue(propertyName);
		Optional<Integer> retval;
		if (result.isPresent()) {
			if (!(result.get() instanceof Integer)) {
				throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type Integer");
			}
			retval = Optional.of((Integer)result.get());
		} else {
			retval = Optional.empty();
		}
		return retval;
	}
	
	@SuppressWarnings("unchecked")
	protected Optional<Enum<?>> getEnumPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> result = getObjectPropertyValue(propertyName);
		if (!result.isPresent()) {
			return Optional.empty();
		}
		if (result.get() instanceof Enum) {
			return (Optional<Enum<?>>)(Optional<?>)result;
		}
		if (!(result.get() instanceof IndividualUriValue)) {
			throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type Individual Value or enum");
		}
		Enum<?> retval = SpdxEnumFactory.uriToEnum.get(((IndividualUriValue)result.get()).getIndividualURI());
		if (Objects.isNull(retval)) {
			logger.error("Unknown individual value for enum: "+((IndividualUriValue)result.get()).getIndividualURI());
			throw new InvalidSPDXAnalysisException("Unknown individual value for enum: "+((IndividualUriValue)result.get()).getIndividualURI());
		} else {
			return Optional.of(retval);
		}
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return the Optional Boolean value for a property
	 * @throws SpdxInvalidTypeException
	 */
	protected Optional<Boolean> getBooleanPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> result = getObjectPropertyValue(propertyName);
		if (result.isPresent()) {
			if (result.get() instanceof Boolean) {
				return Optional.of((Boolean)result.get());
			} else if (result.get() instanceof String) {
				// try to convert
				String sResult = ((String)result.get()).toLowerCase();
				if ("true".equals(sResult)) {
					return Optional.of(Boolean.valueOf(true));
				} else if ("false".equals(sResult)) {
					return Optional.of(Boolean.valueOf(false));
				} else {
					throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type Boolean");
				}
			} else {
				throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type Boolean");
			}
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Converts property values to an AnyLicenseInfo if possible - if NONE or NOASSERTION URI value, convert to the appropriate license
	 * @param propertyName
	 * @return AnyLicenseInfo
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	protected Optional<AnyLicenseInfo> getAnyLicenseInfoPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> result = getObjectPropertyValue(propertyName);
		if (!result.isPresent()) {
			return Optional.empty();
		} else if (result.get() instanceof AnyLicenseInfo) {
			return (Optional<AnyLicenseInfo>)(Optional<?>)result;
		} else if (result.get() instanceof IndividualUriValue) {
			String uri = ((IndividualUriValue)result.get()).getIndividualURI();
			if (SpdxConstants.URI_VALUE_NONE.equals(uri)) {
				return Optional.of(new SpdxNoneLicense());
			} else if (SpdxConstants.URI_VALUE_NOASSERTION.equals(uri)) {
				return Optional.of(new SpdxNoAssertionLicense());
			} else {
				logger.error("Can not convert a URI value to a license: "+uri);
				throw new SpdxInvalidTypeException("Can not convert a URI value to a license: "+uri);
			}
		} else {
			logger.error("Invalid type for AnyLicenseInfo property: "+result.get().getClass().toString());
			throw new SpdxInvalidTypeException("Invalid type for AnyLicenseInfo property: "+result.get().getClass().toString());
		}
	}
	
	/**
	 * Converts property values to an SpdxElement if possible - if NONE or NOASSERTION URI value, convert to the appropriate SpdxElement
	 * @param propertyName name of the property
	 * @return SpdxElement stored
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	protected Optional<SpdxElement> getElementPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Optional<Object> result = getObjectPropertyValue(propertyName);
		if (!result.isPresent()) {
			return Optional.empty();
		} else if (result.get() instanceof SpdxElement) {
			return (Optional<SpdxElement>)(Optional<?>)result;
		} else if (result.get() instanceof IndividualUriValue) {
			String uri = ((IndividualUriValue)result.get()).getIndividualURI();
			if (SpdxConstants.URI_VALUE_NONE.equals(uri)) {
				return Optional.of(new SpdxNoneElement());
			} else if (SpdxConstants.URI_VALUE_NOASSERTION.equals(uri)) {
				return Optional.of(new SpdxNoAssertionElement());
			} else {
				logger.error("Can not convert a URI value to an SPDX element: "+uri);
				throw new SpdxInvalidTypeException("Can not convert a URI value to an SPDX element: "+uri);
			}
		} else {
			logger.error("Invalid type for SpdxElement property: "+result.get().getClass().toString());
			throw new SpdxInvalidTypeException("Invalid type for SpdxElement property: "+result.get().getClass().toString());
		}
	}
	
	/**
	 * Removes a property and its value from the model store if it exists
	 * @param stModelStore Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId ID of the item to associate the property with
	 * @param propertyName Name of the property associated with this object to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static void removeProperty(IModelStore stModelStore, String stDocumentUri, String stId, String propertyName) throws InvalidSPDXAnalysisException {
		stModelStore.removeProperty(stDocumentUri, stId, propertyName);
	}
	
	/**
	 * Removes a property and its value from the model store if it exists
	 * @param propertyName Name of the property associated with this object to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	protected void removeProperty(String propertyName) throws InvalidSPDXAnalysisException {
		removeProperty(modelStore, documentUri, id, propertyName);
	}
	
	/**
	 * Create an update when, when applied by the ModelStore, removes a property and its value from the model store if it exists
	 * @param propertyName Name of the property associated with this object to be removed
	 * @return  an update which can be applied by invoking the apply method
	 */
	protected ModelUpdate updateRemoveProperty(String propertyName) {
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
	protected static void clearValueCollection(IModelStore stModelStore, String stDocumentUri, String stId, String propertyName) throws InvalidSPDXAnalysisException {
		stModelStore.clearValueCollection(stDocumentUri, stId, propertyName);
	}
	
	/**
	 * Clears a collection of values associated with a property
	 * @param propertyName Name of the property
	 */
	protected void clearValueCollection(String propertyName) throws InvalidSPDXAnalysisException {
		clearValueCollection(modelStore, documentUri, id, propertyName);
	}
	
	/**
	 * Create an update when, when applied by the ModelStore, clears a collection of values associated with a property
	 * @param propertyName Name of the property
	 * @return an update which can be applied by invoking the apply method
	 */
	protected ModelUpdate updateClearValueCollection(String propertyName) {
		return () ->{
			clearValueCollection(modelStore, documentUri, id, propertyName);
		};
	}
	
	/**
	 * Add a value to a collection of values associated with a property. If a value
	 * is a ModelObject and does not belong to the document, it will be copied into
	 * the object store
	 * 
	 * @param stModelStore  Model store for the properties
	 * @param stDocumentUri Unique document URI
	 * @param stId          ID of the item to associate the property with
	 * @param propertyName  Name of the property
	 * @param value         to add
	 * @param copyManager
	 * @throws InvalidSPDXAnalysisException
	 */
	protected static void addValueToCollection(IModelStore stModelStore, String stDocumentUri, String stId, 
			String propertyName, Object value, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(value, "Value can not be null");
		stModelStore.addValueToCollection(stDocumentUri, stId, propertyName, 
				ModelStorageClassConverter.modelObjectToStoredObject(value, stDocumentUri, stModelStore, copyManager));
	}
	
	/**
	 * Add a value to a collection of values associated with a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param propertyName  Name of the property
	 * @param value to add
	 * @throws InvalidSPDXAnalysisException 
	 */
	protected void addPropertyValueToCollection(String propertyName, Object value) throws InvalidSPDXAnalysisException {
		addValueToCollection(modelStore, documentUri, id, propertyName, value, copyManager);
	}
	
	/**
	 * Create an update when, when applied, adds a value to a collection of values associated with a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param propertyName  Name of the property
	 * @param value to add
	 * @return an update which can be applied by invoking the apply method
	 */
	protected ModelUpdate updateAddPropertyValueToCollection(String propertyName, Object value) {
		return () ->{
			addValueToCollection(modelStore, documentUri, id, propertyName, value, copyManager);
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
	 * @param copyManager if non-null, any ModelObject property value not stored in the stModelStore under the stDocumentUri will be copied to make it available
	 * @throws InvalidSPDXAnalysisException 
	 */
	protected static void replacePropertyValueCollection(IModelStore stModelStore, String stDocumentUri, String stId, 
			String propertyName, Collection<?> values, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		clearValueCollection(stModelStore, stDocumentUri, stId, propertyName);
		for (Object value:values) {
			addValueToCollection(stModelStore, stDocumentUri, stId, propertyName, value, copyManager);
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
	protected static void removePropertyValueFromCollection(IModelStore stModelStore, String stDocumentUri, String stId, 
			String propertyName, Object value) throws InvalidSPDXAnalysisException {
		stModelStore.removeValueFromCollection(stDocumentUri, stId, propertyName, 
				ModelStorageClassConverter.modelObjectToStoredObject(value, stDocumentUri, stModelStore, null));
	}
	
	/**
	 * Remove a property value from a collection
	 * @param propertyName name of the property
	 * @param value Value to be removed
	 * @throws InvalidSPDXAnalysisException
	 */
	protected void removePropertyValueFromCollection(String propertyName, Object value) throws InvalidSPDXAnalysisException {
		removePropertyValueFromCollection(modelStore, documentUri, id, propertyName, value);
	}
	
	/**
	 * Create an update when, when applied, removes a property value from a collection
	 * @param propertyName name of the property
	 * @param value Value to be removed
	 * @return an update which can be applied by invoking the apply method
	 */
	protected ModelUpdate updateRemovePropertyValueFromCollection(String propertyName, Object value) {
		return () -> {
			removePropertyValueFromCollection(modelStore, documentUri, id, propertyName, value);
		};
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return Set of values associated with a property
	 */
	protected ModelSet<?> getObjectPropertyValueSet(String propertyName, Class<?> type) throws InvalidSPDXAnalysisException {
		return new ModelSet<Object>(this.modelStore, this.documentUri, this.id, propertyName, this.copyManager, type);
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return Collection of values associated with a property
	 */
	protected ModelCollection<?> getObjectPropertyValueCollection(String propertyName, Class<?> type) throws InvalidSPDXAnalysisException {
		return new ModelCollection<Object>(this.modelStore, this.documentUri, this.id, propertyName, this.copyManager, type);
	}
	
	/**
	 * @param propertyName Name of property
	 * @return Collection of Strings associated with the property
	 * @throws SpdxInvalidTypeException
	 */
	@SuppressWarnings("unchecked")
	protected Collection<String> getStringCollection(String propertyName) throws InvalidSPDXAnalysisException {
		if (!isCollectionMembersAssignableTo(propertyName, String.class)) {
			throw new SpdxInvalidTypeException("Property "+propertyName+" does not contain a collection of Strings");
		}
		return (Collection<String>)(Collection<?>)getObjectPropertyValueSet(propertyName, String.class);
	}
	
	protected boolean isCollectionMembersAssignableTo(String propertyName, Class<?> clazz) throws InvalidSPDXAnalysisException {
		return modelStore.isCollectionMembersAssignableTo(this.documentUri, this.id, propertyName, 
				ModelStorageClassConverter.modelClassToStoredClass(clazz));
	}
	
	/**
	 * @param compare
	 * @return true if all the properties have the same or equivalent values
	 */
	public boolean equivalent(ModelObject compare) throws InvalidSPDXAnalysisException {
		return equivalent(compare, false);
	}
	
	/**
	 * @param compare
	 * @param ignoreRelatedElements if true, do not compare properties relatedSpdxElement - used to prevent infinite recursion
	 * @return true if all the properties have the same or equivalent values
	 */
	public boolean equivalent(ModelObject compare, boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
		if (!this.getClass().equals(compare.getClass())) {
			lastNotEquivalentReason = new NotEquivalentReason(NotEquivalent.DIFFERENT_CLASS);
			return false;
		}
		List<String> propertyValueNames = getPropertyValueNames();
		List<String> comparePropertyValueNames = new ArrayList<String>(compare.getPropertyValueNames());	// create a copy since we're going to modify it
		for (String propertyName:propertyValueNames) {
			if (ignoreRelatedElements && isRelatedElement(propertyName)) {
				continue;
			}
			if (comparePropertyValueNames.contains(propertyName)) {
				if (!propertyValuesEquivalent(propertyName, this.getObjectPropertyValue(propertyName), 
				        compare.getObjectPropertyValue(propertyName), ignoreRelatedElements)) {
					lastNotEquivalentReason = new NotEquivalentReason(
							NotEquivalent.PROPERTY_NOT_EQUIVALENT, propertyName);
				    return false;
				}
				comparePropertyValueNames.remove(propertyName);
			} else {
				// No property value
			    Optional<Object> propertyValueOptional = this.getObjectPropertyValue(propertyName);
				if (propertyValueOptional.isPresent()) {
					Object propertyValue = propertyValueOptional.get();
					if (isEquivalentToNull(propertyValue)) {
						continue;
					}
					lastNotEquivalentReason = new NotEquivalentReason(
							NotEquivalent.COMPARE_PROPERTY_MISSING, propertyName);
					return false;
				}
			}
		}
		for (String propertyName:comparePropertyValueNames) { // check any remaining property values
			if (ignoreRelatedElements && isRelatedElement(propertyName)) {
				continue;
			}
			Optional<Object> comparePropertyValueOptional = compare.getObjectPropertyValue(propertyName);
			if (!comparePropertyValueOptional.isPresent()) {
				continue;
			}
			Object comparePropertyValue = comparePropertyValueOptional.get();
			if (isEquivalentToNull(comparePropertyValue)) {
				continue;
			}
			lastNotEquivalentReason = new NotEquivalentReason(
					NotEquivalent.MISSING_PROPERTY, propertyName);
			return false;
		}
		return true;
	}
	
	// Some values are treated like null in comparisons - in particular empty model collections and 
	// "no assertion" values.
	private boolean isEquivalentToNull(Object propertyValue) {
		if (propertyValue instanceof ModelCollection) {
			return ((ModelCollection<?>) propertyValue).size() == 0;
		} else {
			return isNoAssertion(propertyValue);
		}
	}

	private boolean isRelatedElement(String propertyName) {
		return SpdxConstants.PROP_RELATED_SPDX_ELEMENT.equals(propertyName);
	}

	private boolean isEmptyModelCollection(Object value) {
		return (value instanceof ModelCollection)
				&& (((ModelCollection<?>) value).size() == 0);
	}
	
	private boolean isNoAssertion(Object propertyValue) {
		return propertyValue instanceof SpdxNoAssertionLicense ||
						propertyValue.equals(SpdxConstants.NOASSERTION_VALUE);
	}

	/**
	 * @param propertyName Name of the property
	 * @param valueA value to compare
	 * @param valueB value to compare
	 * @param ignoreRelatedElements if true, do not compare properties relatedSpdxElement - used to prevent infinite recursion
	 * @return true if the property values are equivalent
	 * @throws InvalidSPDXAnalysisException
	 */
	private boolean propertyValuesEquivalent(String propertyName, Optional<Object> valueA,
            Optional<Object> valueB, boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
	    if (!valueA.isPresent()) {
            if (valueB.isPresent()) {
                return isEmptyModelCollection(valueB.get());
            }
        } else if (!valueB.isPresent()) {
            return isEmptyModelCollection(valueA.get());
        } else if (valueA.get() instanceof ModelCollection && valueB.get() instanceof ModelCollection) {
            List<?> myList = ((ModelCollection<?>)valueA.get()).toImmutableList();
            List<?> compareList = ((ModelCollection<?>)valueB.get()).toImmutableList();
            if (!listsEquivalent(myList, compareList, ignoreRelatedElements)) {
                return false;
            }
        } else if (valueA.get() instanceof List && valueB.get() instanceof List) {
            if (!listsEquivalent((List<?>)valueA.get(), (List<?>)valueB.get(), ignoreRelatedElements)) {
                return false;
            }
        } else if (valueA.get() instanceof IndividualUriValue && valueB.get() instanceof IndividualUriValue) {
            if (!Objects.equals(((IndividualUriValue)valueA.get()).getIndividualURI(), ((IndividualUriValue)valueB.get()).getIndividualURI())) {
                return false;
            }
            // Note: we must check the IndividualValue before the ModelObject types since the IndividualValue takes precedence
        } else if (valueA.get() instanceof ModelObject && valueB.get() instanceof ModelObject) {
            if (!((ModelObject)valueA.get()).equivalent(((ModelObject)valueB.get()), 
                    isRelatedElement(propertyName) ? true : ignoreRelatedElements)) {
                return false;
            }
            
        } else if (!OptionalObjectsEquivalent(valueA, valueB)) { // Present, not a list, and not a TypedValue
            return false;
        }
	    return true;
    }

    /**
	 * Compares 2 simple optional objects considering NONE and NOASSERTION values which are equivalent to their strings
	 * @param valueA
	 * @param valueB
	 * @return
	 */
	private boolean OptionalObjectsEquivalent(Optional<Object> valueA, Optional<Object> valueB) {
		if (Objects.equals(valueA, valueB)) {
			return true;
		}
		if (!valueA.isPresent()) {
			return false;
		}
		if (!valueB.isPresent()) {
			return false;
		}
		if (valueA.get() instanceof IndividualUriValue) {
			if (SpdxConstants.URI_VALUE_NONE.equals(((IndividualUriValue)valueA.get()).getIndividualURI()) && SpdxConstants.NONE_VALUE.equals(valueB.get())) {
				return true;
			}
			if (SpdxConstants.URI_VALUE_NOASSERTION.equals(((IndividualUriValue)valueA.get()).getIndividualURI()) && SpdxConstants.NOASSERTION_VALUE.equals(valueB.get())) {
				return true;
			}
		}
		if (valueB.get() instanceof IndividualUriValue) {
			if (SpdxConstants.URI_VALUE_NONE.equals(((IndividualUriValue)valueB.get()).getIndividualURI()) && SpdxConstants.NONE_VALUE.equals(valueA.get())) {
				return true;
			}
			if (SpdxConstants.URI_VALUE_NOASSERTION.equals(((IndividualUriValue)valueB.get()).getIndividualURI()) && SpdxConstants.NOASSERTION_VALUE.equals(valueA.get())) {
				return true;
			}
		}
		if (valueA.get() instanceof String && valueB.get() instanceof String) {
			return normalizeString((String)valueA.get()).equals(normalizeString((String)valueB.get()));
		}
		return false;
	}

	/**
	 * Normalize a string for dos and linux linefeeds
	 * @param s
	 * @return linux style only linefeeds
	 */
	private Object normalizeString(String s) {
		return s.replaceAll("\r\n", "\n").trim();
	}

	/**
	 * @param l1
	 * @param l2
	 * @param ignoreRelatedElements if true, do not compare properties relatedSpdxElement - used to prevent infinite recursion
	 * @return true if the two lists are equivalent
	 * @throws InvalidSPDXAnalysisException
	 */
	private boolean listsEquivalent(List<?> l1, List<?> l2, boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
		if (l1.size() != l2.size()) {
			return false;
		}
		int numRemainingComp = l2.size();
		for (Object item:l1) {
			if (l2.contains(item)) {
				numRemainingComp--;
			} else if (item instanceof IndividualUriValue && l2.contains(new SimpleUriValue((IndividualUriValue)item))) {
				numRemainingComp--;
			} else {
				if (item instanceof ModelObject) {
					// Need to check for equiv.
					boolean found = false;
					for (Object compareItem:l2) {
						if (compareItem instanceof ModelObject) {
							if (((ModelObject)item).equivalent(((ModelObject)compareItem), ignoreRelatedElements)) {
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
		if (getModelStore().getIdType(id).equals(IdType.Anonymous)) {
			return Objects.equals(modelStore, comp.getModelStore()) && Objects.equals(id, comp.getId()) && Objects.equals(documentUri, comp.getDocumentUri());
		} else {
			return Objects.equals(id, comp.getId()) && Objects.equals(documentUri, comp.getDocumentUri());
		}
	}
	

	
	/**
	 * Clone a new object using a different model store
	 * @param modelStore
	 * @return
	 */
	public ModelObject clone(IModelStore modelStore) {
		if (Objects.isNull(this.copyManager)) {
			throw new RuntimeException("A copy manager must be provided to clone");
		}
		if (this.modelStore.equals(modelStore)) {
			throw new RuntimeException("Can not clone to the same model store");
		}
		Objects.requireNonNull(modelStore, "Model store for clone must not be null");
		if (modelStore.exists(this.documentUri, this.id)) {
			throw new RuntimeException("Can not clone - "+this.id+" already exists.");
		}
		try {
			ModelObject retval = SpdxModelFactory.createModelObject(modelStore, this.documentUri, this.id, this.getType(), this.copyManager);
			retval.copyFrom(this);
			return retval;
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
		if (Objects.isNull(copyManager)) {
			throw new InvalidSPDXAnalysisException("Copying is not enabled for "+id);
		}
		copyManager.copy(this.modelStore, this.documentUri, this.id, 
				source.getModelStore(), source.getDocumentUri(), source.getId(), this.getType());
	}
	
	public void setCopyManager(ModelCopyManager copyManager) {
		this.copyManager = copyManager;
	}
	
	/**
	 * @return the copy manager - value may be null if copies are not allowd
	 */
	public ModelCopyManager getCopyManager() {
		return this.copyManager;
	}
	
	/**
	 * @param id String for the object
	 * @return type of the ID
	 */
	protected IdType idToIdType(String id) {
		if (id.startsWith(SpdxConstants.NON_STD_LICENSE_ID_PRENUM)) {
			return IdType.LicenseRef;
		} else if (id.startsWith(SpdxConstants.SPDX_ELEMENT_REF_PRENUM)) {
			return IdType.SpdxId;
		} else if (id.startsWith(SpdxConstants.EXTERNAL_DOC_REF_PRENUM)) {
			return IdType.DocumentRef;
		} else if (ListedLicenses.getListedLicenses().isSpdxListedLicenseId(id)) {
			return IdType.ListedLicense;
		} else if ("none".equalsIgnoreCase(id) || "noassertion".equalsIgnoreCase(id)) {
			return IdType.Literal;
		} else {
			return IdType.Anonymous;
		}
	}
	
	protected TypedValue toTypedValue() throws InvalidSPDXAnalysisException {
		return new TypedValue(this.id, this.getType());
	}
	
	/**
	 * Verifies all elements in a collection
	 * @param specVersion version of the SPDX specification to verify against
	 * @param collection collection to be verifies
	 * @param verifiedIds verifiedIds list of all Id's which have already been verifieds - prevents infinite recursion
	 * @param warningPrefix String to prefix any warning messages
	 */
	protected List<String> verifyCollection(Collection<? extends ModelObject> collection, String warningPrefix, List<String> verifiedIds, String specVersion) {
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
	
	// The following methods are helper methods to create Model Object subclasses using the same model store and document as this Model Object

	/**
	 * @param annotator This field identifies the person, organization or tool that has commented on a file, package, or the entire document.
	 * @param annotationType This field describes the type of annotation.  Annotations are usually created when someone reviews the file, and if this is the case the annotation type should be REVIEW.   If the author wants to store extra information about one of the elements during creation, it is recommended to use the type of OTHER.
	 * @param date Identify when the comment was made.  This is to be specified according to the combined date and time in the UTC format, as specified in the ISO 8601 standard.
	 * @param comment
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public Annotation createAnnotation(String annotator, AnnotationType annotationType, String date,
			String comment) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(annotator, "Annotator can not be null");
		Objects.requireNonNull(annotationType, "AnnotationType can not be null");
		Objects.requireNonNull(date, "Date can not be null");
		Objects.requireNonNull(comment, "Comment can not be null");
		Annotation retval = new Annotation(this.modelStore, this.documentUri, 
				this.modelStore.getNextId(IdType.Anonymous, this.documentUri), copyManager, true);
		retval.setAnnotationDate(date);
		retval.setAnnotationType(annotationType);
		retval.setAnnotator(annotator);
		retval.setComment(comment);
		return retval;
	}
	
	/**
	 * @param relatedElement   The SPDX Element that is related
	 * @param relationshipType Type of relationship - See the specification for a
	 *                         description of the types
	 * @param comment          optional comment for the relationship
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship createRelationship(SpdxElement relatedElement, 
			RelationshipType relationshipType, @Nullable String comment) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(relatedElement, "Related Element can not be null");
		Objects.requireNonNull(relationshipType, "Relationship type can not be null");
		Relationship retval = new Relationship(this.modelStore, this.documentUri, 
				this.modelStore.getNextId(IdType.Anonymous, this.documentUri), this.copyManager, true);
		retval.setRelatedSpdxElement(relatedElement);
		retval.setRelationshipType(relationshipType);
		if (Objects.nonNull(comment)) {
			retval.setComment(comment);
		}
		return retval;
	}
	
	/**
	 * @param algorithm Checksum algorithm
	 * @param value Checksum value
	 * @return Checksum using the same model store and document URI as this Model Object
	 * @throws InvalidSPDXAnalysisException
	 */
	public Checksum createChecksum(ChecksumAlgorithm algorithm, String value) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(algorithm, "Algorithm can not be null");
		Objects.requireNonNull(value, "Value can not be null");
		Checksum retval = new Checksum(this.modelStore, this.documentUri, 
				this.modelStore.getNextId(IdType.Anonymous, this.documentUri), this.copyManager, true);
		retval.setAlgorithm(algorithm);
		retval.setValue(value);
		return retval;
	}
	
	/**
	 * @param value Verification code calculated value
	 * @param excludedFileNames file names of files excluded from the verification code calculation
	 * @return Package verification code using the same model store and document URI as this Model Object
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackageVerificationCode createPackageVerificationCode(String value, Collection<String> excludedFileNames) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(value, "Value can not be null");
		Objects.requireNonNull(excludedFileNames, "Excluded Files can not be null");
		SpdxPackageVerificationCode retval = new SpdxPackageVerificationCode(this.modelStore, this.documentUri, 
				this.modelStore.getNextId(IdType.Anonymous, this.documentUri), this.copyManager, true);
		retval.setValue(value);
		retval.getExcludedFileNames().addAll(excludedFileNames);
		return retval;
	}
	
	/**
	 * @param externalDocumentUri Document URI for the external document
	 * @param checksum Checksum of the external Document
	 * @param externalDocumentId ID to be used internally within this SPDX document
	 * @return ExternalDocumentRef using the same model store and document URI as this Model Object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ExternalDocumentRef createExternalDocumentRef(String externalDocumentId, String externalDocumentUri, 
			Checksum checksum) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(externalDocumentUri, "External document URI can not be null");
		Objects.requireNonNull(checksum, "Checksum can not be null");
		Objects.requireNonNull(externalDocumentId, "External document ID can not be null");
		if (!SpdxVerificationHelper.isValidExternalDocRef(externalDocumentId)) {
			throw new InvalidSPDXAnalysisException("Invalid external document reference ID "+externalDocumentId+
					".  Must be of the format "+SpdxConstants.EXTERNAL_DOC_REF_PATTERN.pattern());
		}
		if (!SpdxVerificationHelper.isValidUri(externalDocumentUri)) {
			throw new InvalidSPDXAnalysisException("Invalid external document URI: "+externalDocumentUri);
		}
		IModelStoreLock lock = getModelStore().enterCriticalSection( getDocumentUri(), false);
		try {
			if (getModelStore().exists(getDocumentUri(), externalDocumentId)) {
				return new ExternalDocumentRef(getModelStore(), getDocumentUri(), 
						externalDocumentId, this.copyManager, false);
			} else {
				ExternalDocumentRef retval = new ExternalDocumentRef(getModelStore(), getDocumentUri(), 
						externalDocumentId, this.copyManager, true);
				retval.setChecksum(checksum);
				retval.setSpdxDocumentNamespace(externalDocumentUri);
				// Need to add this to the list of document URI's
				ModelObject.addValueToCollection(getModelStore(), getDocumentUri(), 
						SpdxConstants.SPDX_DOCUMENT_ID, 
						SpdxConstants.PROP_SPDX_EXTERNAL_DOC_REF, retval, copyManager);
				return retval;
			}
		} finally {
			getModelStore().leaveCriticalSection(lock);
		}
	}

	/**
	 * @param creators Creators Identify who (or what, in the case of a tool) created the SPDX file.  If the SPDX file was created by an individual, indicate the person's name. 
	 * @param date When the SPDX file was originally created. The date is to be specified according to combined date and time in UTC format as specified in ISO 8601 standard. 
	 * @return creationInfo using the same modelStore and documentUri as this object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxCreatorInformation createCreationInfo(List<String> creators, String date) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(creators, "Creators can not be null");
		Objects.requireNonNull(date, "Date can not be null");
		SpdxCreatorInformation retval = new SpdxCreatorInformation(modelStore, documentUri, 
				modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		retval.getCreators().addAll(creators);
		retval.setCreated(date);
		return retval;
	}
	
	/**
	 * @param category Reference category
	 * @param referenceType Reference type
	 * @param locator Reference locator
	 * @param comment Optional comment
	 * @return ExternalRef using the same modelStore and documentUri as this object
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef createExternalRef(ReferenceCategory category, ReferenceType referenceType, 
			String locator, @Nullable String comment) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(category, "Category can not be null");
		Objects.requireNonNull(referenceType, "Reference type can not be null");
		Objects.requireNonNull(locator, "Locator can not be null");
		ExternalRef retval = new ExternalRef(modelStore, documentUri, 
				modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		retval.setReferenceCategory(category);
		retval.setReferenceType(referenceType);
		retval.setReferenceLocator(locator);
		retval.setComment(comment);
		return retval;
	}
	
	/**
	 * Create an SpdxFileBuilder with all of the required properties - the build() method will build the file
	 * @param id - ID - must be an SPDX ID type
	 * @param name - File name
	 * @param concludedLicense license concluded
	 * @param seenLicense collection of seen licenses
	 * @param copyrightText Copyright text
	 * @param sha1 Sha1 checksum
	 * @return SPDX file using the same modelStore and documentUri as this object
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxFile.SpdxFileBuilder createSpdxFile(String id, String name, AnyLicenseInfo concludedLicense,
			Collection<AnyLicenseInfo> seenLicense, String copyrightText, Checksum sha1) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(id, "ID can not be null");
		Objects.requireNonNull(name, "Name can not be null");
		Objects.requireNonNull(sha1, "Sha1 can not be null");
		return new SpdxFile.SpdxFileBuilder(modelStore, documentUri, id, copyManager,
				name, concludedLicense, seenLicense, copyrightText, sha1);
	}
	
	/**
	 * Create an SpdxPackageBuilder with all required fields for a filesAnalyzed=false using this objects model store and document URI
	 * @param id - ID - must be an SPDX ID type
	 * @param name - File name
	 * @param concludedLicense license concluded
	 * @param copyrightText Copyright text
	 * @param licenseDeclared Declared license for the package
	 * @return SpdxPackageBuilder with all required fields for a filesAnalyzed=false
	 */
	public SpdxPackage.SpdxPackageBuilder createPackage(String id, String name,
				AnyLicenseInfo concludedLicense, 
				String copyrightText, AnyLicenseInfo licenseDeclared) {
		Objects.requireNonNull(id, "ID can not be null");
		Objects.requireNonNull(name, "Name can not be null");
		return new SpdxPackage.SpdxPackageBuilder(modelStore, documentUri, id, copyManager,
				name, concludedLicense, copyrightText, licenseDeclared);
	}

	/**
	 * @param referencedElement
	 * @param offset
	 * @return ByteOffsetPointer using the same modelStore and documentUri as this object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ByteOffsetPointer createByteOffsetPointer(SpdxElement referencedElement, int offset) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(referencedElement, "Referenced element can not be null");
		ByteOffsetPointer retval = new ByteOffsetPointer(modelStore, documentUri, 
				modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		retval.setReference(referencedElement);
		retval.setOffset(offset);
		return retval;
	}

	/**
	 * @param referencedElement
	 * @param lineNumber
	 * @return LineCharPointer using the same modelStore and documentUri as this object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public LineCharPointer createLineCharPointer(SpdxElement referencedElement, int lineNumber) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(referencedElement, "Referenced element can not be null");
		LineCharPointer retval = new LineCharPointer(modelStore, documentUri, 
				modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		retval.setReference(referencedElement);
		retval.setLineNumber(lineNumber);
		return retval;
	}
	
	/**
	 * @param startPointer
	 * @param endPointer
	 * @return StartEndPointer using the same modelStore and documentUri as this object
	 * @throws InvalidSPDXAnalysisException
	 */
	public StartEndPointer createStartEndPointer(SinglePointer startPointer, SinglePointer endPointer) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(startPointer, "Start pointer can not be null");
		Objects.requireNonNull(endPointer, "End pointer can not be null");
		StartEndPointer retval = new StartEndPointer(modelStore, documentUri, 
				modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		retval.setStartPointer(startPointer);
		retval.setEndPointer(endPointer);
		return retval;
	}
	
	/**
	 * Create an SpdxSnippetBuilder with all of the required properties - the build() method will build the file
	 * @param id - ID - must be an SPDX ID type
	 * @param name - File name
	 * @param concludedLicense license concluded
	 * @param seenLicense collection of seen licenses
	 * @param copyrightText Copyright text
	 * @param snippetFromFile File where the snippet is located
	 * @param startByte first byte of the snippet in the file
	 * @param endByte last byte of the snippet in the file
	 * @return SPDX snippet using the same modelStore and documentUri as this object
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxSnippet.SpdxSnippetBuilder createSpdxSnippet(String id, String name, AnyLicenseInfo concludedLicense,
			Collection<AnyLicenseInfo> seenLicense, String copyrightText, 
			SpdxFile snippetFromFile, int startByte, int endByte) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(id, "ID can not be null");
		Objects.requireNonNull(name, "Name can not be null");
		return new SpdxSnippet.SpdxSnippetBuilder(modelStore, documentUri, id, copyManager,
				name, concludedLicense, seenLicense, copyrightText, snippetFromFile, startByte, endByte);
	}
	
	/**
	 * @param members
	 * @return  ConjunctiveLicenseSet with default model store and document URI initialized with members
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ConjunctiveLicenseSet createConjunctiveLicenseSet(Collection<AnyLicenseInfo> members) throws InvalidSPDXAnalysisException {
		ConjunctiveLicenseSet retval = new ConjunctiveLicenseSet(modelStore, documentUri, 
				modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		retval.setMembers(members);
		return retval;
	}
	
	/**
	 * @param members
	 * @return  DisjunctiveLicenseSet with default model store and document URI initialized with members
	 * @throws InvalidSPDXAnalysisException 
	 */
	public DisjunctiveLicenseSet createDisjunctiveLicenseSet(Collection<AnyLicenseInfo> members) throws InvalidSPDXAnalysisException {
		DisjunctiveLicenseSet retval = new DisjunctiveLicenseSet(modelStore, documentUri, 
				modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		retval.setMembers(members);
		return retval;
	}
	
	/**
	 * Create a CrossRef Builder with an Anonymous ID type using the same model store and document URI
	 * @param url URL for the cross reference
	 * @return a CrossRefBuilder which you can call <code>build()</code> on to build the CrossRef
	 * @throws InvalidSPDXAnalysisException
	 */
	public CrossRefBuilder createCrossRef(String url) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(url, "URL can not be null");
		return new CrossRefBuilder(this.modelStore, this.documentUri, 
				this.modelStore.getNextId(IdType.Anonymous,  this.documentUri), this.copyManager, url);
	}
	
	@Override
	public String toString() {
		return this.getType() + " " + this.id;
	}
}
