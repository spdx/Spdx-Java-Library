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
import org.spdx.conversion.ISpdxConverter;
import org.spdx.conversion.Spdx2to3Converter;
import org.spdx.core.IModelCopyManager;
import org.spdx.core.IndividualUriValue;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.SimpleUriValue;
import org.spdx.core.TypedValue;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v3.SpdxConstantsV3;
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
	private ConcurrentHashMap<IModelStore, ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, String>>> copiedIds = 
			new ConcurrentHashMap<>();
	
	/**
	 * Map of fromModelStore, toModelStore, fromSpecMajorVersion, toSpecMajorVersion to a converter
	 */
	private ConcurrentHashMap<IModelStore, 
			ConcurrentHashMap<IModelStore, ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion,
			ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion, ISpdxConverter>>>> spdxConverters = new ConcurrentHashMap<>();

	/**
	 * Create a ModelCopyManager with default options
	 */
	public ModelCopyManager() {
		// Required empty constructor
	}
	
	/**
	 * @param fromSpecVersion from spec version
	 * @param toSpecVersion to spec version
	 * @return true if no conversion is needed from the fromSpecVersion to the toSpecVersion
	 */
	public boolean versionsCompatible(String fromSpecVersion,
			String toSpecVersion) {
		Objects.requireNonNull(fromSpecVersion);
		Objects.requireNonNull(toSpecVersion);
		return fromSpecVersion.startsWith("SPDX-2") && toSpecVersion.startsWith("SPDX-2") ||
				fromSpecVersion.startsWith("3.0.") && toSpecVersion.startsWith("3.0.");
	}
	
	/**
	 * @param fromSpecVersion from spec version
	 * @param toSpecVersion to spec version
	 * @return true if the fromSpecVersion can be converted to the toSpecVersion
	 */
	public boolean canConvert(String fromSpecVersion,
			String toSpecVersion) {
		Objects.requireNonNull(fromSpecVersion);
		Objects.requireNonNull(toSpecVersion);
		return fromSpecVersion.startsWith("SPDX-2") && toSpecVersion.startsWith("3.0.");
	}
	
	/**
	 * @param fromStore Store copied from
	 * @param fromObjectUri Object URI in the from tsotre
	 * @param toStore store copied to
	 * @return the objectId which has already been copied, or null if it has not been copied
	 */
	public @Nullable String getCopiedObjectUri(IModelStore fromStore, String fromObjectUri,
			IModelStore toStore) {
		ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, String>> fromStoreMap = copiedIds.get(fromStore);
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
	 * @param fromStore store converting from
	 * @param toStore store converting to
	 * @param fromSpecVersion spec version converting from
	 * @param toSpecVersion spec version converting to
	 * @param create if true, create the spec converter if it does not already exist
	 * @return a converter for the fromStore fromSpecVersion to the toStore toSpecVersion
	 * @throws InvalidSPDXAnalysisException 
	 */
	public @Nullable ISpdxConverter getSpecConverter(IModelStore fromStore, IModelStore toStore,
			String fromSpecVersion, String toSpecVersion, boolean create) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(fromStore, "From store can not be null");
		Objects.requireNonNull(toStore, "To store can not be null");
		Objects.requireNonNull(fromSpecVersion, "From spec version can not be null");
		Objects.requireNonNull(toSpecVersion, "To spec version can not be null");
		SpdxConstantsV3.SpdxMajorVersion fromMajorVersion;
		if (fromSpecVersion.startsWith("SPDX-2")) {
			fromMajorVersion = SpdxConstantsV3.SpdxMajorVersion.VERSION_2;
		} else if (fromSpecVersion.startsWith("3.")) {
			fromMajorVersion = SpdxConstantsV3.SpdxMajorVersion.VERSION_3;
		} else {
			throw new InvalidSPDXAnalysisException("Invalid from spec version: "+fromSpecVersion);
		}
		SpdxConstantsV3.SpdxMajorVersion toMajorVersion;
		if (toSpecVersion.startsWith("SPDX-2")) {
			toMajorVersion = SpdxConstantsV3.SpdxMajorVersion.VERSION_2;
		} else if (toSpecVersion.startsWith("3.")) {
			toMajorVersion = SpdxConstantsV3.SpdxMajorVersion.VERSION_3;
		} else {
			throw new InvalidSPDXAnalysisException("Invalid from spec version: "+fromSpecVersion);
		}
		ConcurrentHashMap<IModelStore, ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion,
			ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion, ISpdxConverter>>> modelStoreMap = spdxConverters.get(fromStore);
		if (Objects.isNull(modelStoreMap)) {
			if (!create) {
				return null;
			}
			modelStoreMap = new ConcurrentHashMap<>();
			ConcurrentHashMap<IModelStore, ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion,
				ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion, ISpdxConverter>>> previousMap = spdxConverters.putIfAbsent(fromStore, modelStoreMap);
			if (Objects.nonNull(previousMap)) {
				modelStoreMap = previousMap;
			}
		}
		ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion,
			ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion, ISpdxConverter>> fromVersionMap = modelStoreMap.get(toStore);
		if (Objects.isNull(fromVersionMap)) {
			if (!create) {
				return null;
			}
			fromVersionMap = new ConcurrentHashMap<>();
			ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion,
				ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion, ISpdxConverter>> previousMap = modelStoreMap.put(toStore, fromVersionMap);
			if (Objects.nonNull(previousMap)) {
				fromVersionMap = previousMap;
			}
		}
		ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion, ISpdxConverter> toVersionMap = fromVersionMap.get(fromMajorVersion);
		if (Objects.isNull(toVersionMap)) {
			if (!create) {
				return null;
			}
			toVersionMap = new ConcurrentHashMap<>();
			ConcurrentHashMap<SpdxConstantsV3.SpdxMajorVersion, ISpdxConverter> previous = fromVersionMap.put(fromMajorVersion, toVersionMap);
			if (Objects.nonNull(previous)) {
				toVersionMap = previous;
			}
		}
		ISpdxConverter retval = toVersionMap.get(toMajorVersion);
		if (Objects.isNull(retval)) {
			if (!create) {
				return null;
			}
			if (SpdxConstantsV3.SpdxMajorVersion.VERSION_2.equals(fromMajorVersion) &&
					SpdxConstantsV3.SpdxMajorVersion.VERSION_3.equals(toMajorVersion)) {
				retval = new Spdx2to3Converter(toStore, this, null, toSpecVersion, null);
				//TODO: Add a creation info and uri prefix
				ISpdxConverter previous = toVersionMap.putIfAbsent(toMajorVersion, retval);
				if (Objects.nonNull(previous)) {
					retval = previous;
				}
				throw new InvalidSPDXAnalysisException("Not completely implemented - need to add creation info");
			} else {
				throw new InvalidSPDXAnalysisException("No SPDX conversion code is available from spec version " + fromSpecVersion + " to "+toSpecVersion);
			}
		}
		return retval;
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
		ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, String>> fromStoreMap = copiedIds.get(fromStore);
		while (Objects.isNull(fromStoreMap)) { 
			fromStoreMap = copiedIds.putIfAbsent(fromStore, new ConcurrentHashMap<>());
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
	

	/* (non-Javadoc)
	 * @see org.spdx.core.IModelCopyManager#copy(org.spdx.storage.IModelStore, java.lang.String, org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String)
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
		if (versionsCompatible(fromTv.get().getSpecVersion(), toSpecVersion)) {
			copyCompatible(toStore, toObjectUri, fromStore, fromTv.get(), toSpecVersion, toNamespace);
		} else if (canConvert(fromTv.get().getSpecVersion(), toSpecVersion)) {
			copyConverted(toStore, toObjectUri, fromStore, fromTv.get(), toSpecVersion, toNamespace);
		} else {
			throw new InvalidSPDXAnalysisException("Incompatible versions - can not convert from " + 
					fromTv.get().getSpecVersion() + " to version "+toSpecVersion);
		}
	}
	
	/**
	 * Converts and copies an item from one Model Object Store to another
	 * @param toStore Model Store to copy to
	 * @param toObjectUri URI for the destination object
	 * @param fromStore Model Store containing the source item
	 * @param fromTv TypedValue of the item to copy from
	 * @param toSpecVersion version of the spec the to value should comply with
	 * @param toNamespace optional namespace of the to property
	 * @throws InvalidSPDXAnalysisException on any SPDX related error
	 */
	private void copyConverted(IModelStore toStore, String toObjectUri, 
			IModelStore fromStore, TypedValue fromTv, String toSpecVersion,
			@Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}
	
	/**
	 * Copy an item from one Model Object Store to another for compatible versions that don't need conversion
	 * @param toStore Model Store to copy to
	 * @param toObjectUri URI for the destination object
	 * @param fromStore Model Store containing the source item
	 * @param fromTv TypedValue of the item to copy from
	 * @param toSpecVersion version of the spec the to value should comply with
	 * @param toNamespace optional namespace of the to property
	 * @throws InvalidSPDXAnalysisException on any SPDX related error
	 */
	private void copyCompatible(IModelStore toStore, String toObjectUri, 
			IModelStore fromStore, TypedValue fromTv, String toSpecVersion,
			@Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		if (!toStore.exists(toObjectUri)) {
			toStore.create(new TypedValue(toObjectUri, fromTv.getType(), toSpecVersion));
		}
		putCopiedId(fromStore, fromTv.getObjectUri(), toStore, toObjectUri);	
		List<PropertyDescriptor> propertyDescriptors = fromStore.getPropertyValueDescriptors(fromTv.getObjectUri());
		for (PropertyDescriptor propDesc:propertyDescriptors) {
			if (fromStore.isCollectionProperty(fromTv.getObjectUri(), propDesc)) {
			    copyCollectionProperty(toStore, toObjectUri, fromStore, fromTv.getObjectUri(), propDesc, toSpecVersion, toNamespace);
			} else {
			    copyIndividualProperty(toStore, toObjectUri, fromStore, fromTv.getObjectUri(), propDesc, toSpecVersion, toNamespace);
			}
		}
	}
	
	/**
	 * Copies an individual property value (non-collection property value)
     * @param toStore Model Store to copy to
     * @param toObjectUri to object URI to copy to
     * @param fromStore Model Store containing the source item
     * @param fromObjectUri object to copy from
     * @param propDescriptor Descriptor for the property
     * @param toSpecVersion Version of the SPDX spec the to value complies with
	 * @param toNamespace Namespace to use if an ID needs to be generated for the to object
	 * @throws InvalidSPDXAnalysisException
	 */
	private void copyIndividualProperty(IModelStore toStore, String toObjectUri, IModelStore fromStore,
            String fromObjectUri, PropertyDescriptor propDescriptor,
			String toSpecVersion, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		IModelStoreLock fromStoreLock = fromStore.enterCriticalSection(false);
		//Note: we use a write lock since the RDF store may end up creating a property to check if it is a collection
		Optional<Object> result = Optional.empty();
		try {
			if (fromStore.isCollectionProperty(fromObjectUri, propDescriptor)) {
	            throw new InvalidSPDXAnalysisException("Property "+propDescriptor+" is a collection type");
	        }
			result =  fromStore.getValue(fromObjectUri, propDescriptor);
		} finally {
			fromStoreLock.unlock();
		}
        if (result.isPresent()) {
            if (result.get() instanceof IndividualUriValue) {
                toStore.setValue(toObjectUri, propDescriptor, new SimpleUriValue((IndividualUriValue)result.get()));
            } else if (result.get() instanceof TypedValue) {
                TypedValue tv = (TypedValue)result.get();
                if (fromStore.equals(toStore)) {
                    toStore.setValue(toObjectUri, propDescriptor, tv);
                } else {
                    toStore.setValue(toObjectUri, propDescriptor, 
                            copy(toStore, fromStore, tv.getObjectUri(), toSpecVersion, toNamespace));
                }
            } else {
                toStore.setValue(toObjectUri, propDescriptor, result.get());
            }
        }
    }

    /**
	 * Copies a property which is is a collection
     * @param toStore Model Store to copy to
     * @param toObjectUri URI to copy to
     * @param fromStore Model Store containing the source item
     * @param fromDocumentUri Object URI to copy from
	 * @param propDescriptor Descriptor for the property
	 * @param toSpecVersion Version of the SPDX spec the to value complies with
	 * @param toNamespace Namespace to use if an ID needs to be generated for the to object
	 * @throws InvalidSPDXAnalysisException
	 */
	private void copyCollectionProperty(IModelStore toStore, String toObjectUri, IModelStore fromStore,
            String fromObjectUri, PropertyDescriptor propDescriptor, 
			String toSpecVersion, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		IModelStoreLock fromStoreLock = fromStore.enterCriticalSection(false);
		//Note: we use a write lock since the RDF store may end up creating a property to check if it is a collection
		Iterator<Object> fromListIter = null;
		try {
			if (!fromStore.isCollectionProperty(fromObjectUri, propDescriptor)) {
		        throw new InvalidSPDXAnalysisException("Property "+propDescriptor+" is not a collection type");
		    }
		    fromListIter = fromStore.listValues(fromObjectUri, propDescriptor);
		} finally {
			fromStoreLock.unlock();
		}
        while (fromListIter.hasNext()) {
            Object listItem = fromListIter.next();
            Object toStoreItem;
            if (listItem instanceof IndividualUriValue) {
                toStoreItem = new SimpleUriValue((IndividualUriValue)listItem);
            } else if (listItem instanceof TypedValue) {
                TypedValue listItemTv = (TypedValue)listItem;
                if (toStore.equals(fromStore)) {
                    toStoreItem = listItemTv;
                } else {
                    toStoreItem = copy(toStore, fromStore, listItemTv.getObjectUri(), 
                    		toSpecVersion, toNamespace);
                }
            } else {
                toStoreItem = listItem;
            }
            toStore.addValueToCollection(toObjectUri, propDescriptor, toStoreItem);
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
