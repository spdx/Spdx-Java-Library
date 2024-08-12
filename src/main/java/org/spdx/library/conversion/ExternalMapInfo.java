/**
 * Copyright (c) 2024 Source Auditor Inc.
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
package org.spdx.library.conversion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.SpdxModelClassFactory;
import org.spdx.library.model.v3.core.ExternalMap;
import org.spdx.library.model.v3.core.Hash;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * @author Gary O'Neall
 * 
 * Information about an ExternalMap captured from an ExternalDocumentRef
 *
 */
public class ExternalMapInfo {

	private String docRefId;
	private String externalDocumentUri;
	private Optional<Hash> externalDocumentHash;
	private Collection<ExternalMap> docImports;
	
	private Map<String, ExternalMap> existingExternalMap = Collections.synchronizedMap(new HashMap<>());

	/**
	 * @param docRefId ID of the ExternalDocRef from the SPDX Spec version 2 SPDX Document containing the reference 
	 * @param externalDocumentUri External document URI for the external document being referenced 
	 * @param externalDocumentHash Optional Hash of the external SPDX document 
	 * @param docImports SPDX Spec version 3 collection of doc imports
	 */
	public ExternalMapInfo(String docRefId, String externalDocumentUri, Optional<Hash> externalDocumentHash, 
			Collection<ExternalMap> docImports) {
		this.docRefId = docRefId;
		this.externalDocumentUri = externalDocumentUri;
		this.externalDocumentHash = externalDocumentHash;
		this.docImports = docImports;
	}
	
	/**
	 * If the externalUri has not already been added, create an ExternalMap and add it to the docImports
	 * @param externalUri External URI to add
	 * @param modelStore model store where the ExternalMap is to be stored
	 * @return the existing or added External map
	 * @throws InvalidSPDXAnalysisException on error creating the ExternalMap
	 */
	public ExternalMap addExternalMap(String externalUri, IModelStore modelStore) throws InvalidSPDXAnalysisException {
		synchronized(existingExternalMap) {
			ExternalMap retval = existingExternalMap.get(externalUri);
			if (Objects.isNull(retval)) {
				retval = (ExternalMap)SpdxModelClassFactory.getModelObject(modelStore, 
						modelStore.getNextId(IdType.Anonymous), SpdxConstantsV3.CORE_EXTERNAL_MAP, null, true, null);
				retval.setExternalSpdxId(externalUri);
				retval.setLocationHint(this.externalDocumentUri);
				if (externalDocumentHash.isPresent()) {
					retval.getVerifiedUsings().add(externalDocumentHash.get());
				}
				docImports.add(retval);
			}
			return retval;
		}
	}

	/**
	 * @return the docRefId
	 */
	public String getDocRefId() {
		return docRefId;
	}

	/**
	 * @return the externalDocumentUri
	 */
	public String getExternalDocumentUri() {
		return externalDocumentUri;
	}

	/**
	 * @return the externalDocumentHash
	 */
	public Optional<Hash> getExternalDocumentHash() {
		return externalDocumentHash;
	}

	/**
	 * @return the docImports
	 */
	public Collection<ExternalMap> getDocImports() {
		return docImports;
	}

}
