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
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
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
public class ModelCopyManager {
	
	static final Logger logger = LoggerFactory.getLogger(ModelCopyManager.class);
	
	/**
	 * Map of copied ID's fromModelStore, toModelStore, fromObjectUri, toObjectUri
	 * Used to keep track of copied ID's to make sure we don't copy them more than once
	 */
	private ConcurrentHashMap<IModelStore, ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, 
	String>>> COPIED_IDS = 
			new ConcurrentHashMap<>();

	/**
	 * Create a CompatV2ModelCopyManager with default options
	 */
	public ModelCopyManager() {
		// Required empty constructor
	}
	
	/**
	 * @param fromStore Store copied from
	 * @param fromObjectUri
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
	 * @param toId ID copied to
	 * @return any copied to ID for the same stores, URI's and fromID
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
	 * @param toObjectUri URI for the destination object
	 * @param fromStore Model Store containing the source item
	 * @param fromObjectUri Object URI for the source item
	 * @param type Type to copy
	 * @param fromNamespace optional namespace of the from property
	 * @param toNamespace optional namespace of the to property
	 * @throws InvalidSPDXAnalysisException
	 */
	public void copy(IModelStore toStore, String toObjectUri, IModelStore fromStore, String fromObjectUri, String type,
			@Nullable String fromNamespace, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		copy(toStore, toObjectUri, fromStore, fromObjectUri, type, false, fromNamespace, toNamespace);
	}

	/**
	 * Copy an item from one Model Object Store to another
	 * @param toStore Model Store to copy to
	 * @param toId Id to use in the copy
	 * @param toDocumentUri Target document URI
	 * @param fromStore Model Store containing the source item
	 * @param fromDocumentUri Document URI for the source item
	 * @param fromId ID source ID
	 * @param type Type to copy
	 * @param excludeLicenseDetails If true, don't copy over properties of the listed licenses
	 * @param fromNamespace optional namespace of the from property
	 * @param toNamespace optional namespace of the to property
	 * @throws InvalidSPDXAnalysisException
	 */
	public void copy(IModelStore toStore, String toObjectUri, 
			IModelStore fromStore, String fromObjectUri, 
			String type, boolean excludeLicenseDetails,
			@Nullable String fromNamespace, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(toStore, "ToStore can not be null");
		Objects.requireNonNull(toObjectUri, "To Object URI can not be null");
		Objects.requireNonNull(fromStore, "FromStore can not be null");
		Objects.requireNonNull(fromObjectUri, "From ObjectUri can not be null");
		Objects.requireNonNull(type, "Type can not be null");
		if (fromStore.equals(toStore) && fromObjectUri.equals(toObjectUri)) {
			return;	// trying to copy the same thing!
		}
		if (!toStore.exists(toObjectUri)) {
			toStore.create(toObjectUri, type);
		}
		putCopiedId(fromStore, fromObjectUri, toStore, toObjectUri);
		if (!(excludeLicenseDetails && 
				(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE.equals(type) ||
						SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION.equals(type)))) {
			List<PropertyDescriptor> propertyDescriptors = fromStore.getPropertyValueDescriptors(fromObjectUri);
			for (PropertyDescriptor propDesc:propertyDescriptors) {
				if (fromStore.isCollectionProperty(fromObjectUri, propDesc)) {
				    copyCollectionProperty(toStore, toObjectUri, fromStore, fromObjectUri, propDesc, excludeLicenseDetails,
				    		fromNamespace, toNamespace);
				} else {
				    copyIndividualProperty(toStore, toObjectUri, fromStore, fromObjectUri, propDesc, excludeLicenseDetails,
				    		fromNamespace, toNamespace);
				}
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
     * @param excludeLicenseDetails If true, don't copy over properties of the listed licenses
	 * @param fromNamespace optional namespace of the from property
	 * @param toNamespace optional namespace of the to property
	 * @throws InvalidSPDXAnalysisException
	 */
	private void copyIndividualProperty(IModelStore toStore, String toObjectUri, IModelStore fromStore,
            String fromObjectUri, PropertyDescriptor propDescriptor, boolean excludeLicenseDetails,
            @Nullable String fromNamespace, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
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
                            copy(toStore, fromStore, tv.getObjectUri(), tv.getType(), excludeLicenseDetails,
                            		fromNamespace, toNamespace));
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
	 * @param excludeLicenseDetails If true, don't copy over properties of the listed licenses
	 * 	 * @param fromNamespace optional namespace of the from property
	 * @param toNamespace optional namespace of the to property
	 * @throws InvalidSPDXAnalysisException
	 */
	private void copyCollectionProperty(IModelStore toStore, String toObjectUri, IModelStore fromStore,
            String fromObjectUri, PropertyDescriptor propDescriptor, boolean excludeLicenseDetails,
            @Nullable String fromNamespace, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
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
                    		listItemTv.getType(), excludeLicenseDetails, fromNamespace, toNamespace);
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
	 * @param sourceObjectUri source object URI
	 * @param type Type to copy
	 * @param fromNamespace optional namespace of the from property
	 * @param toNamespace optional namespace of the to property
	 * @return Object URI for the copied object
	 * @throws InvalidSPDXAnalysisException
	 */
	public TypedValue copy(IModelStore toStore, IModelStore fromStore, 
			String sourceObjectUri, String type,
			@Nullable String fromNamespace, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		return copy(toStore, fromStore, sourceObjectUri, type, false, fromNamespace, toNamespace);
	}

    /**
	 * Copy an item from one Model Object Store to another using the source ID for the target unless it is anonymous
	 * @param toStore Model Store to copy to
	 * @param fromStore Model Store containing the source item
	 * @param sourceUri URI for the Source object
	 * @param type Type to copy
	 * @param excludeLicenseDetails If true, don't copy over properties of the listed licenses
	 * @param fromNamespace optional namespace of the from property
	 * @param toNamespace optional namespace of the to property
	 * @return Object URI for the copied object
	 * @throws InvalidSPDXAnalysisException
	 */
	public TypedValue copy(IModelStore toStore, IModelStore fromStore, 
			String sourceUri, String type, boolean excludeLicenseDetails,
			@Nullable String fromNamespace, @Nullable String toNamespace) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(toStore, "To Store can not be null");
		Objects.requireNonNull(fromStore, "From Store can not be null");
		Objects.requireNonNull(sourceUri, "Source URI can not be null");
		Objects.requireNonNull(type, "Type can not be null");
		if (fromStore.getSpdxVersion().compareTo(SpdxMajorVersion.VERSION_3) >= 0 && 
				toStore.getSpdxVersion().compareTo(SpdxMajorVersion.VERSION_3) < 0) {
			throw new InvalidSPDXAnalysisException("Can not copy from SPDX spec version 3.0 to SPDX spec version less than 3.0");
		}
		String toObjectUri = getCopiedObjectUri(fromStore, sourceUri, toStore);
		if (Objects.isNull(toObjectUri)) {
			if (!(fromStore.getIdType(sourceUri) == IdType.Anonymous)) {
				if (Objects.nonNull(fromNamespace) && sourceUri.startsWith(fromNamespace) && Objects.nonNull(toNamespace)) {
					toObjectUri = toNamespace + sourceUri.substring(fromNamespace.length());
				} else {
					toObjectUri = sourceUri;
				}
			}
			if (Objects.isNull(toObjectUri) || toStore.exists(toObjectUri)) {
				if (SpdxConstantsCompatV2.CLASS_EXTERNAL_DOC_REF.equals(type)) {
					toObjectUri = toStore.getNextId(IdType.DocumentRef, toNamespace);
				} else {
					switch (fromStore.getIdType(sourceUri)) {
						case Anonymous: toObjectUri = toStore.getNextId(IdType.Anonymous, toNamespace); break;
						case LicenseRef: toObjectUri = toStore.getNextId(IdType.LicenseRef, toNamespace); break;
						case DocumentRef: toObjectUri = toStore.getNextId(IdType.DocumentRef, toNamespace); break;
						case SpdxId: toObjectUri = toStore.getNextId(IdType.SpdxId, toNamespace); break;
						case ListedLicense:
						case Literal:
						case Unkown:
						default: toObjectUri = sourceUri;
					}
				}
			}
			if (Objects.isNull(toObjectUri)) {
				toObjectUri = sourceUri;
			}
			copy(toStore, toObjectUri, fromStore, sourceUri, type, excludeLicenseDetails, fromNamespace, toNamespace);
		}
		return new TypedValue(toObjectUri, type);
	}
}
