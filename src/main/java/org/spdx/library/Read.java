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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.SpdxPackage;
import org.spdx.library.model.TypedValue;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.ISerializableModelStore;

/**
 * Supports reading SPDX documents from an existing ModelStore
 * 
 * Some design and some implementation borrowed from Yevster's spdxtra project under the Apache 2.0 license
 * http://yevster.github.io/spdxtra/
 * 
 * 
 * @author Gary O'Neall
 *
 */
public class Read {
	
	public static class Document {
		/**
		 * Obtains the SPDX Documents described in the provided model store. A document store
		 * may contain multiple SPDX documents identified by their unique document URI's
		 * 
		 * @param modelStore Storage for the model objects
		 * @return List of SPDX documents contained in the modelStore
		 * @throws InvalidSPDXAnalysisException 
		 * @throws IOException 
		 */
		public static List<SpdxDocument> get(IModelStore modelStore) throws InvalidSPDXAnalysisException, IOException {
			List<String> documentUris = modelStore.getDocumentUris();
			List<SpdxDocument> retval = new ArrayList<SpdxDocument>();
			for (String documentUri:documentUris) {
				retval.add(new SpdxDocument(modelStore, documentUri, null, false));
			}
			return retval;
		}
		
		/**
		 * @param modelStore Storage for the model objects
		 * @param documentUri SPDX Document URI for a document associated with this model
		 * @return the SPDX document
		 * @throws InvalidSPDXAnalysisException
		 * @throws IOException 
		 */
		public static SpdxDocument get(IModelStore modelStore, String documentUri) throws InvalidSPDXAnalysisException, IOException {
			IModelStoreLock lock = modelStore.enterCriticalSection(documentUri, true);
			try {
				return new SpdxDocument(modelStore, documentUri, null, false);				
			} finally {
				modelStore.leaveCriticalSection(lock);
			}
		}
		
		/**
		 * @param modelStore Storage for the model objects
		 * @param documentUri SPDX Document URI for a document associated with this model
		 * @return true if the document exists in the model store
		 */
		public static boolean documentExists(IModelStore modelStore, String documentUri) {
			return modelStore.exists(documentUri, SpdxConstants.SPDX_DOCUMENT_ID);
		}
	}
	
	/**
	 * Serializes an SPDX document stored in the modelStore. The specific format of
	 * the serialization will depend on the modelStore.
	 * 
	 * @param modelStore  Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this
	 *                    model
	 * @param stream      Output stream for serialization
	 * @throws InvalidSPDXAnalysisException
	 * @throws IOException
	 */
	public static void serialize(ISerializableModelStore modelStore, String documentUri, OutputStream stream) throws InvalidSPDXAnalysisException, IOException {
		IModelStoreLock lock = modelStore.enterCriticalSection(documentUri, true);
		try {
			modelStore.serialize(documentUri, stream);
		} finally {
			modelStore.leaveCriticalSection(lock);
		}
	}
	
	/**
	 * Write the SPDX document stored in the modelStore to a file
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this model
	 * @param filePath File path to output the file
	 * @throws InvalidSPDXAnalysisException
	 * @throws IOException
	 */
	public static void writeToFile(ISerializableModelStore modelStore, String documentUri, Path filePath) throws InvalidSPDXAnalysisException, IOException {
		Objects.requireNonNull(modelStore, "Model store can not be null");
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		Objects.requireNonNull(filePath, "File path can not be null");
		Files.createFile(filePath);
		try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
			modelStore.serialize(documentUri, fos);
		}
	}
	
	/**
	 * @param modelStore  Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this
	 *                    model
	 * @param typeFilter  Optional parameter to specify the type of objects to be
	 *                    retrieved
	 * @return Stream of all items store within the document
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Stream<? extends ModelObject> getAllItems(IModelStore modelStore, String documentUri, 
			String typeFilter) throws InvalidSPDXAnalysisException { 
		return modelStore.getAllItems(documentUri, typeFilter).map((TypedValue tv) -> {
			try {
				return SpdxModelFactory.createModelObject(modelStore, documentUri, tv.getId(), tv.getType(), null);
			} catch (InvalidSPDXAnalysisException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this model
	 * @return All packages stored for the document in the model store
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public static Stream<SpdxPackage> getAllPackages(IModelStore modelStore, String documentUri) throws InvalidSPDXAnalysisException {
		return (Stream<SpdxPackage>)(getAllItems(modelStore, documentUri, SpdxConstants.CLASS_SPDX_PACKAGE));
	}
	
	/** The following can be achieve by fetching the list of relationships from the element
	public static Stream<Relationship> getRelationships(Dataset dataset, SpdxElement element, Relationship.Type relationshipType) {
		
	}
	**/
}
