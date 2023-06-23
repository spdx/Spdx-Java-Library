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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.NotEquivalentReason;
import org.spdx.library.SpdxIdNotFoundException;
import org.spdx.library.TypedValue;
import org.spdx.library.model.compat.v2.SpdxIdInUseException;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.IModelStore.IdType;

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
 * 
 * The concrete classes are expected to implements getters for the model class properties which translate
 * into calls to the getTYPEPropertyValue where TYPE is the type of value to be returned and the property descriptor
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
	private String objectUri;
	
	/**
	 * If non null, a reference made to a model object stored in a different modelStore and/or
	 * document will be copied to this modelStore and documentUri
	 */
	private ModelCopyManager copyManager = null;
	/**
	 * if true, checks input values for setters to verify valid SPDX inputs
	 */
	protected boolean strict = true;
	
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
	 * @param objectUri Anonymous ID or URI for the model object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ModelObject(String objectUri) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), objectUri, 
				DefaultModelStore.getDefaultCopyManager(), true);
	}
	
	/**
	 * Creates a new model object
	 * @param modelStore Storage for the model objects - Must support model V3 classes
	 * @param objectUri Anonymous ID or URI for the model object
	 * @param copyManager - if supplied, model objects will be implictly copied into this model store and document URI when referenced by setting methods
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelObject(IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager, 
			boolean create) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore, "Model Store can not be null");
		Objects.requireNonNull(objectUri, "Object URI can not be null");
		
		if (!SpdxMajorVersion.VERSION_3.equals(modelStore.getSpdxVersion())) {
			logger.error("Trying to create an SPDX version 3 model object in an SPDX version 2 model store");
			throw new InvalidSPDXAnalysisException("Trying to create an SPDX version 3 model object in an SPDX version 2 model store");
		}
		this.modelStore = modelStore;
		this.copyManager = copyManager;
		Optional<TypedValue> existing = modelStore.getTypedValue(objectUri);
		if (existing.isPresent()) {
			if (create && !existing.get().getType().equals(getType())) {
				logger.error("Can not create "+objectUri+".  It is already in use with type "+existing.get().getType()+" which is incompatible with type "+getType());
				throw new SpdxIdInUseException("Can not create "+objectUri+".  It is already in use with type "+existing.get().getType()+" which is incompatible with type "+getType());
			}
		} else {
			if (create) {
				IModelStoreLock lock = enterCriticalSection(false);
				// re-check since previous check was done outside of the lock
				try {
					if (!modelStore.exists(objectUri)) {
						modelStore.create(objectUri, getType());
					}
				} finally {
					lock.unlock();
				}
			} else {
				logger.error(objectUri+" does not exist");
				throw new SpdxIdNotFoundException(objectUri+" does not exist");
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
	protected abstract List<String> _verify(Set<String> verifiedElementIds, String specVersion);
	
	/**
	 * Enter a critical section. leaveCriticialSection must be called.
	 * @param readLockRequested true implies a read lock, false implies write lock.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public IModelStoreLock enterCriticalSection(boolean readLockRequested) throws InvalidSPDXAnalysisException {
		return modelStore.enterCriticalSection(readLockRequested);
	}
	
	/**
	 * Leave a critical section. Releases the lock form the matching enterCriticalSection
	 */
	public void leaveCriticalSection(IModelStoreLock lock) {
		modelStore.leaveCriticalSection(lock);
	}

}
