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
package org.spdx.library;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.IModelCopyManager;
import org.spdx.core.IndividualUriValue;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.SimpleUriValue;
import org.spdx.core.TypedValue;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.PropertyDescriptor;

/**
 * This class helps facilitate copying objects from one model to another.
 * 
 * In addition to the copy functions (methods), this object keeps track of 
 * what was copied where so that the same object is not copied twice.
 * 
 * This object can be passed into the constructor for ModelObjects to allow the objects to be copied.
 * 
 * @author Gary O'Neall
 *
 */
public class ModelCopyManager implements IModelCopyManager {
	
	static final Logger logger = LoggerFactory.getLogger(ModelCopyManager.class);
	
	/**
	 * Map of copied ID's fromModelStore, toModelStore, fromObjectUri, toObjectUri
	 * Used to keep track of copied ID's to make sure we don't copy them more than once
	 */
	private ConcurrentHashMap<IModelStore, ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, String>>> COPIED_IDS = 
			new ConcurrentHashMap<>();

	/**
	 * Create a ModelCopyManager with default options
	 */
	public ModelCopyManager() {
		// Required empty constructor
	}
	
	/**
	 * @param fromStore Store copied from
	 * @param fromObjectUri Object URI in the from tsotre
	 * @param toStore store copied to
	 * @return the objectId which has already been copied, or null if it has not been copied
	 */
	public String getCopiedObjectUri(IModelStore fromStore, String fromObjectUri,
			IModelStore toStore) {
		ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, String>> fromStoreMap = COPIED_IDS.get(fromStore);
		if (Objects.isNull(fromStoreMap)) { 
			return null;
		}
		ConcurrentHashMap<String, String> toStoreMap = fromStoreMap.get(toStore);
		if (Objects.isNull(toStoreMap)) {
			return null;
		}
		return toStoreMap.get(fromObjectUri);
	}

	/**
	 * Record a copied ID between model stores
	 * @param fromStore Store copied from
	 * @param fromObjectUri URI for the from Object
	 * @param toObjectUri URI for the to Object
	 * @return any copied to ID for the same stores, URI's, nameSpace and fromID
	 */
	public String putCopiedId(IModelStore fromStore, String fromObjectUri, IModelStore toStore,
			String toObjectUri) {
		ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, String>> fromStoreMap = COPIED_IDS.get(fromStore);
		while (Objects.isNull(fromStoreMap)) { 
			fromStoreMap = COPIED_IDS.putIfAbsent(fromStore, new ConcurrentHashMap<>());
		}
		ConcurrentHashMap<String, String> toStoreMap = fromStoreMap.get(toStore);
		while (Objects.isNull(toStoreMap)) {
			toStoreMap = fromStoreMap.putIfAbsent(toStore, new ConcurrentHashMap<>());
		}
		
		if (toStoreMap.containsKey(fromObjectUri)) {
			logger.warn("Object URI already exists for the originating "+ fromObjectUri);
		}
		return toStoreMap.put(fromObjectUri, toObjectUri);
	}
	
	/**
	 * Copy an item from one Model Object Store to another
	 * @param toStore Model Store to copy to
	 * @param toId Id to use in the copy
	 * @param toDocumentUri Target document URI
	 * @param fromStore Model Store containing the source item
	 * @param fromDocumentUri Document URI for the source item
	 * @param fromId ID source ID
	 * @param toSpecVersion version of the spec the to value should comply with
	 * @param toNamespace Namespace to use if an ID needs to be generated for the to object
	 * @throws InvalidSPDXAnalysisException
	 */
	public void copy(IModelStore toStore, String toObjectUri, 
			IModelStore fromStore, String fromObjectUri, String toSpecVersion,
			@Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(toStore, "ToStore can not be null");
		Objects.requireNonNull(toObjectUri, "To Object URI can not be null");
		Objects.requireNonNull(fromStore, "FromStore can not be null");
		Objects.requireNonNull(fromObjectUri, "From ObjectUri can not be null");
		Objects.requireNonNull(toSpecVersion, "To spec version can not be null");
		if (fromStore.equals(toStore) && fromObjectUri.equals(toObjectUri)) {
			return;	// trying to copy the same thing!
		}
		Optional<TypedValue> fromTv = fromStore.getTypedValue(fromObjectUri);
		if (!fromTv.isPresent()) {
			throw new InvalidSPDXAnalysisException("Missing from object URI "+fromObjectUri);
		}
		String toType = ModelCopyConverter.convertType(fromTv.get(), toSpecVersion);
		Optional<TypedValue> toTv = toStore.getTypedValue(toObjectUri);
		if (toTv.isPresent()) {
			if (!toType.equals(toTv.get().getType())) {
				throw new InvalidSPDXAnalysisException("Incompatible type for copy.  Stored type is " +
								toTv.get().getType() + " and requested type is "+toType);
			}
		} else {
			toTv = Optional.of(new TypedValue(toObjectUri, toType, toSpecVersion));
			toStore.create(toTv.get());
		}
		putCopiedId(fromStore, fromObjectUri, toStore, toObjectUri);
		
		List<PropertyDescriptor> fromPropertyDescriptors = fromStore.getPropertyValueDescriptors(fromObjectUri);
		for (PropertyDescriptor propDesc:fromPropertyDescriptors) {
			if (fromStore.isCollectionProperty(fromObjectUri, propDesc)) {
			    copyCollectionProperty(toStore, toTv.get(), fromStore, fromTv.get(), propDesc, toNamespace);
			} else {
			    copyIndividualProperty(toStore, toTv.get(), fromStore, fromTv.get(), propDesc, toNamespace);
			}
		}
	}
	
	/**
	 * Copies an individual property value (non-collection property value)
     * @param toStore Model Store to copy to
     * @param toTV to typedValue to copy the property to
     * @param fromStore Model Store containing the source item
     * @param fromTV typedValue to copy the property from
     * @param fromPropDescriptor Descriptor for the property to be copied from
	 * @param toNamespace Namespace to use if an ID needs to be generated for the to object
	 * @throws InvalidSPDXAnalysisException
	 */
	private void copyIndividualProperty(IModelStore toStore, TypedValue toTv, IModelStore fromStore,
            TypedValue fromTv, PropertyDescriptor fromPropDescriptor,
			@Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		IModelStoreLock fromStoreLock = fromStore.enterCriticalSection(false);
		//Note: we use a write lock since the RDF store may end up creating a property to check if it is a collection
		Optional<Object> result = Optional.empty();
		try {
			if (fromStore.isCollectionProperty(fromTv.getObjectUri(), fromPropDescriptor)) {
	            throw new InvalidSPDXAnalysisException("Property "+fromPropDescriptor+" is a collection type");
	        }
			result = fromStore.getValue(fromTv.getObjectUri(), fromPropDescriptor);
		} finally {
			fromStoreLock.unlock();
		}
		if (result.isPresent()) {
			ModelCopyConverter.copyConvertedPropertyValue(toStore, toTv, fromStore, fromTv, fromPropDescriptor, result.get(), toNamespace, this);
		}
    }

    /**
	 * Copies a property which is is a collection
     * @param toStore Model Store to copy to
     * @param toTv typed value to copy to
     * @param fromStore Model Store containing the source item
     * @param fromTv typed value to copy from
	 * @param fromPropDescriptor Descriptor for the property to be copied from
	 * @param toNamespace Namespace to use if an ID needs to be generated for the to object
	 * @throws InvalidSPDXAnalysisException on conversion or store error
	 */
	private void copyCollectionProperty(IModelStore toStore, TypedValue toTv, IModelStore fromStore,
            TypedValue fromTv, PropertyDescriptor fromPropDescriptor, 
			@Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		IModelStoreLock fromStoreLock = fromStore.enterCriticalSection(false);
		//Note: we use a write lock since the RDF store may end up creating a property to check if it is a collection
		Iterator<Object> fromListIter = null;
		try {
			if (!fromStore.isCollectionProperty(fromTv.getObjectUri(), fromPropDescriptor)) {
		        throw new InvalidSPDXAnalysisException("Property "+fromPropDescriptor+" is not a collection type");
		    }
		    fromListIter = fromStore.listValues(fromTv.getObjectUri(), fromPropDescriptor);
		} finally {
			fromStoreLock.unlock();
		}
        while (fromListIter.hasNext()) {
            Object listItem = fromListIter.next();
            ModelCopyConverter.addConvertedPropertyValue(toStore, toTv, fromStore, fromTv, fromPropDescriptor, listItem, toNamespace, this);
        }
    }

    /**
	 * Copy an item from one Model Object Store to another using the source ID for the target unless it is anonymous
	 * @param toStore Model Store to copy to
	 * @param fromStore Model Store containing the source item
	 * @param sourceUri URI for the Source object
	 * @param toSpecVersion Version of the SPDX spec the to value complies with
	 * @param toNamespace Namespace to use if an ID needs to be generated for the to object - must be a unique prefix to the store
	 * @return Object URI for the copied object
	 * @throws InvalidSPDXAnalysisException
	 */
	public TypedValue copy(IModelStore toStore, IModelStore fromStore, 
			String sourceUri, String toSpecVersion, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(toStore, "To Store can not be null");
		Objects.requireNonNull(fromStore, "From Store can not be null");
		Objects.requireNonNull(sourceUri, "Source URI can not be null");
		Objects.requireNonNull(toSpecVersion, "To specVersion can not be null");

		String toObjectUri = getCopiedObjectUri(fromStore, sourceUri, toStore);
		if (Objects.isNull(toObjectUri)) {
			Optional<TypedValue> fromTv = fromStore.getTypedValue(sourceUri);
			if (!fromTv.isPresent()) {
				throw new InvalidSPDXAnalysisException(sourceUri + " does not exist in the from Store");
			}
			toObjectUri = toSpecVersion.startsWith("SPDX-2") ? sourceUriToObjectUriV2Compat(sourceUri, 
					fromStore.getIdType(sourceUri), toStore, toNamespace, SpdxConstantsCompatV2.CLASS_EXTERNAL_DOC_REF.equals(fromTv.get().getType())) :
				sourceUriToObjectUri(sourceUri, fromStore.getIdType(sourceUri), toStore, toNamespace);
			copy(toStore, toObjectUri, fromStore, sourceUri, toSpecVersion, toNamespace);
		}
		return toStore.getTypedValue(toObjectUri).get();
	}

	/**
	 * @param sourceUri source URI copied from
	 * @param idType idType from the sourceUri
	 * @param toStore model store to store the copied item
	 * @param toNamespace namespace for the generated elements for "to"
	 * @return an object URI suitable for SPDX V3 and later
	 * @throws InvalidSPDXAnalysisException 
	 */
	private String sourceUriToObjectUri(String sourceUri, IdType idType, IModelStore toStore, 
			String toNamespace) throws InvalidSPDXAnalysisException {
		if (IdType.Anonymous.equals(idType)) {
			return toStore.getNextId(IdType.Anonymous);
		}
		if (!toStore.exists(sourceUri)) {
			return sourceUri;
		}
		if (Objects.isNull(toNamespace) || toNamespace.isEmpty() || 
				sourceUri.startsWith(toNamespace)) {
			logger.warn(sourceUri + " already exists - possibly overwriting properties due to a copy from a different model store.");
			return sourceUri;
		}
		switch (idType) {
			case LicenseRef: return toNamespace + toStore.getNextId(IdType.LicenseRef);
			case DocumentRef: return toNamespace + toStore.getNextId(IdType.DocumentRef);
			case SpdxId: return toNamespace + toStore.getNextId(IdType.SpdxId);
			case ListedLicense: return sourceUri;
			case Anonymous:
			case Unkown:
			default: return toStore.getNextId(IdType.Anonymous);
		}
	}

	/**
	 * @param sourceUri source URI copied from
	 * @param idType idType from the sourceUri
	 * @param toStore model store to store the copied item
	 * @param toNamespace namespace for the generated elements for "to"
	 * @param isExternalDocRef true if the type of the value to be copied is an ExternalDocRef
	 * @return an object URI suitable for SPDX V2
	 * @throws InvalidSPDXAnalysisException 
	 */
	private String sourceUriToObjectUriV2Compat(String sourceUri, IdType idType, 
			IModelStore toStore, String toNamespace, boolean isExternalDocRef) throws InvalidSPDXAnalysisException {
		if ((isExternalDocRef || !(IdType.Anonymous.equals(idType) ||
				IdType.ListedLicense.equals(idType) || IdType.Unkown.equals(idType)))
				&& (Objects.isNull(toNamespace) || toNamespace.isEmpty())) {
			throw new InvalidSPDXAnalysisException("A to namespace or document URI must be provided to copy SPDX element for SPDX spec version 2");
		}
		if (sourceUri.startsWith(toNamespace) && !toStore.exists(sourceUri)) {
			return sourceUri;
		}
		if (IdType.ListedLicense.equals(idType)) {
			return sourceUri;
		}
		String toUri = null;
		if (Objects.nonNull(toNamespace)) {
			int poundIndex = sourceUri.lastIndexOf('#');
			if (poundIndex > 0) {
				toUri = toNamespace + sourceUri.substring(poundIndex + 1);
			}
		}
		
		boolean notNullAndNotExists = Objects.nonNull(toUri) && !toStore.exists(toUri); // notExists and nonNull
		if (isExternalDocRef) {
			if (!toStore.exists(toUri) && IdType.DocumentRef.equals(toStore.getIdType(toUri))) {
				return toUri;
			} else {
				return toNamespace + toStore.getNextId(IdType.DocumentRef);
			}
		}
		switch (idType) {
			case LicenseRef: return notNullAndNotExists && IdType.LicenseRef.equals(toStore.getIdType(toUri)) ? toUri : 
				toNamespace + toStore.getNextId(IdType.LicenseRef);
			case DocumentRef: return notNullAndNotExists && IdType.DocumentRef.equals(toStore.getIdType(toUri)) ? toUri : 
				toNamespace + toStore.getNextId(IdType.DocumentRef);
			case SpdxId: return notNullAndNotExists && IdType.SpdxId.equals(toStore.getIdType(toUri)) ? toUri : 
				toNamespace + toStore.getNextId(IdType.SpdxId);
			case ListedLicense: return sourceUri;
			case Anonymous:
			case Unkown:
			default: return toStore.getNextId(IdType.Anonymous);
		}
	}
}
