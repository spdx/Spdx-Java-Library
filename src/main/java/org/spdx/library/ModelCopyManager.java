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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.model.IndividualUriValue;
import org.spdx.library.model.SimpleUriValue;
import org.spdx.library.model.TypedValue;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

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
	 * Map of copied ID's fromModelStore, toModelStore, fromDocUri, toDocUri, fromId, toId
	 * Used to keep track of copied ID's to make sure we don't copy them more than once
	 */
	private ConcurrentHashMap<IModelStore, ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, 
	ConcurrentHashMap<String, ConcurrentHashMap<String, String>>>>> COPIED_IDS = 
			new ConcurrentHashMap<>();

	/**
	 * Create a ModelCopyManager with default options
	 */
	public ModelCopyManager() {
		
	}
	
	/**
	 * @param fromStore Store copied from
	 * @param fromDocumentUri document copied from
	 * @param fromId ID copied from
	 * @param toStore store copied to
	 * @param toDocumentUri document copied to
	 * @return the ID which has already been copied, or null if it has not been copied
	 */
	public String getCopiedId(IModelStore fromStore, String fromDocumentUri, String fromId,
			IModelStore toStore, String toDocumentUri) {
		ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, String>>>> fromStoreMap = COPIED_IDS.get(fromStore);
		if (Objects.isNull(fromStoreMap)) { 
			return null;
		}
		ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> toStoreMap = fromStoreMap.get(toStore);
		if (Objects.isNull(toStoreMap)) {
			return null;
		}
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> fromDocumentMap = toStoreMap.get(fromDocumentUri);
		if (Objects.isNull(fromDocumentMap)) {
			return null;
		}
		ConcurrentHashMap<String, String> idMap = fromDocumentMap.get(toDocumentUri);
		if (Objects.isNull(idMap)) {
			return null;
		}
		return idMap.get(fromId);
	}

	/**
	 * Record a copied ID between model stores
	 * @param fromStore Store copied from
	 * @param fromDocumentUri document copied from
	 * @param fromId ID copied from
	 * @param toStore store copied to
	 * @param toDocumentUri document copied to
	 * @param toId ID copied to
	 * @return any copied to ID for the same stores, URI's and fromID
	 */
	public String putCopiedId(IModelStore fromStore, String fromDocumentUri, String fromId, IModelStore toStore,
			String toDocumentUri, String toId) {
		ConcurrentHashMap<IModelStore, ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, String>>>> fromStoreMap = COPIED_IDS.get(fromStore);
		while (Objects.isNull(fromStoreMap)) { 
			fromStoreMap = COPIED_IDS.putIfAbsent(fromStore, new ConcurrentHashMap<>());
		}
		ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> toStoreMap = fromStoreMap.get(toStore);
		while (Objects.isNull(toStoreMap)) {
			toStoreMap = fromStoreMap.putIfAbsent(toStore, new ConcurrentHashMap<>());
		}
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> fromDocumentMap = toStoreMap.get(fromDocumentUri);
		while (Objects.isNull(fromDocumentMap)) {
			fromDocumentMap = toStoreMap.putIfAbsent(fromDocumentUri, new ConcurrentHashMap<>());
		}
		ConcurrentHashMap<String, String> idMap = fromDocumentMap.get(toDocumentUri);
		while (Objects.isNull(idMap)) {
			idMap = fromDocumentMap.putIfAbsent(toDocumentUri, new ConcurrentHashMap<>());
		}
		if (idMap.containsKey(fromId)) {
			logger.warn("ID already exists for the originating "+fromDocumentUri+"#"+fromId + ":" + toDocumentUri + "#");
		}
		return idMap.put(fromId, toId);
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
	 * @throws InvalidSPDXAnalysisException
	 */
	public void copy(IModelStore toStore, String toDocumentUri, String toId, IModelStore fromStore, String fromDocumentUri, String fromId, String type) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(toStore, "ToStore can not be null");
		Objects.requireNonNull(toDocumentUri, "To Document URI can not be null");
		Objects.requireNonNull(fromStore, "FromStore can not be null");
		Objects.requireNonNull(fromDocumentUri, "From Document URI can not be null");
		Objects.requireNonNull(fromId, "From ID can not be null");
		Objects.requireNonNull(toId, "To ID can not be null");
		Objects.requireNonNull(type, "Type can not be null");
		if (!toStore.exists(toDocumentUri, toId)) {
			toStore.create(toDocumentUri, toId, type);
		}
		putCopiedId(fromStore, fromDocumentUri, fromId, toStore, toDocumentUri, toId);
		List<String> propertyNames = fromStore.getPropertyValueNames(fromDocumentUri, fromId);
		for (String propName:propertyNames) {
			if (fromStore.isCollectionProperty(fromDocumentUri, fromId, propName)) {
				List<Object> fromList = fromStore.getValueList(fromDocumentUri, fromId, propName);
				for (Object listItem:fromList) {
					Object toStoreItem;
					if (listItem instanceof IndividualUriValue) {
						toStoreItem = new SimpleUriValue((IndividualUriValue)listItem);
					} else if (listItem instanceof TypedValue) {
						TypedValue listItemTv = (TypedValue)listItem;
						toStoreItem = copy(toStore, toDocumentUri, fromStore, fromDocumentUri, 
										listItemTv.getId(), listItemTv.getType());
					} else {
						toStoreItem = listItem;
					}
					toStore.addValueToCollection(toDocumentUri, toId, propName, toStoreItem);
				}
			} else {
				Optional<Object> result =  fromStore.getValue(fromDocumentUri, fromId, propName);
				if (result.isPresent()) {
					if (result.get() instanceof IndividualUriValue) {
						toStore.setValue(toDocumentUri, toId, propName, new SimpleUriValue((IndividualUriValue)result.get()));
					} else if (result.get() instanceof TypedValue) {
						TypedValue tv = (TypedValue)result.get();
						toStore.setValue(toDocumentUri, toId, propName, 
								copy(toStore, toDocumentUri, fromStore, fromDocumentUri, 
										tv.getId(), tv.getType()));
					} else {
						toStore.setValue(toDocumentUri, toId, propName, result.get());
					}
				}
			}
		}
	}
	
	/**
	 * Copy an item from one Model Object Store to another using the source ID for the target unless it is anonymous
	 * @param toStore Model Store to copy to
	 * @param toDocumentUri Target document URI
	 * @param fromStore Model Store containing the source item
	 * @param fromDocumentUri Document URI for the source item
	 * @param sourceId ID source ID
	 * @param type Type to copy
	 * @return ID for the copied object
	 * @throws InvalidSPDXAnalysisException
	 */
	public TypedValue copy(IModelStore toStore, String toDocumentUri, IModelStore fromStore, 
			String fromDocumentUri, String sourceId, String type) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(toStore, "To Store can not be null");
		Objects.requireNonNull(toDocumentUri, "To Document URI can not be null");
		Objects.requireNonNull(fromStore, "From Store can not be null");
		Objects.requireNonNull(fromDocumentUri, "From Document URI can not be null");
		Objects.requireNonNull(sourceId, "Source ID can not be null");
		Objects.requireNonNull(type, "Type can not be null");
		String toId = getCopiedId(fromStore, fromDocumentUri, sourceId, toStore, toDocumentUri);
		if (Objects.isNull(toId)) {
			if (fromStore.getIdType(sourceId) == IdType.Anonymous || toStore.exists(toDocumentUri, sourceId)) {
				if (SpdxConstants.CLASS_EXTERNAL_DOC_REF.equals(type)) {
					toId = toStore.getNextId(IdType.DocumentRef, toDocumentUri);
				} else {
					switch (fromStore.getIdType(sourceId)) {
						case Anonymous: toId = toStore.getNextId(IdType.Anonymous, toDocumentUri); break;
						case LicenseRef: toId = toStore.getNextId(IdType.LicenseRef, toDocumentUri); break;
						case DocumentRef: toId = toStore.getNextId(IdType.DocumentRef, toDocumentUri); break;
						case SpdxId: toId = toStore.getNextId(IdType.SpdxId, toDocumentUri); break;
						case ListedLicense:
						case Literal:
						case Unkown:
						default: toId = sourceId;
					}
				}
			} else {
				toId = sourceId;
			}
			copy(toStore, toDocumentUri, toId, fromStore, fromDocumentUri, sourceId, type);
		}
		return new TypedValue(toId, type);
	}
}
