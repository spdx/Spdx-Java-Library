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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import org.spdx.storage.IModelStore;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.IModelStore.ModelTransaction;
import org.spdx.storage.IModelStore.ModelUpdate;
import org.spdx.storage.IModelStore.ReadWrite;

/**
 * Static class to provide write operations to the model.  
 * 
 * Some design and some implementation borrowed from Yevster's spdxtra project under the Apache 2.0 license
 * http://yevster.github.io/spdxtra/
 * 
 * @author Gary O'Neall
 *
 */
public class Write {

	/**
	 * Convenience method for applying a small set of updates.
	 * @param modelStore Storage for the model objects
	 * @param updates Updates to be applied in a single transaction
	 * @throws IOException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static void applyUpdatesInOneTransaction(IModelStore modelStore, ModelUpdate... updates) throws InvalidSPDXAnalysisException, IOException {
		applyUpdatesInOneTransaction(modelStore, Arrays.asList(updates));
	}

	/**
	 * Apply a set of model updates in a single transaction.  Updates can be gather from the <code>SpdxModelFactory</code> or the model objects themselves from the update methods
	 * @param modelStore Storage for the model objects
	 * @param updates Updates to be applied in a single transaction
	 * @throws InvalidSPDXAnalysisException
	 * @throws IOException
	 */
	public static void applyUpdatesInOneTransaction(IModelStore modelStore, Iterable<? extends ModelUpdate> updates) throws InvalidSPDXAnalysisException, IOException {
		try (ModelTransaction transaction = modelStore.beginTransaction(ReadWrite.WRITE)) {
			for (ModelUpdate update : updates) {
				update.apply();
			}
			transaction.commit();
		}
	}
	
	/**
	 * Deserialize a model store from an input stream.  The format of the input stream dependes on the Model Store implementation
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this model
	 * @param stream Input stream to deserialize the data
	 * @throws InvalidSPDXAnalysisException
	 * @throws IOException
	 */
	public static void deSerialize(ISerializableModelStore modelStore, String documentUri, InputStream stream) throws InvalidSPDXAnalysisException, IOException {
		try (ModelTransaction transaction = modelStore.beginTransaction(ReadWrite.WRITE)) {
			modelStore.deSerialize(documentUri, stream);
		}
	}
	
	/**
	 * Reads a file into a model store
	 * @param modelStore
	 * @param documentUri
	 * @param filePath
	 * @throws InvalidSPDXAnalysisException
	 * @throws IOException
	 */
	public static void readFile(ISerializableModelStore modelStore, String documentUri, Path filePath) throws InvalidSPDXAnalysisException, IOException {
		Objects.requireNonNull(modelStore);
		Objects.requireNonNull(documentUri);
		Objects.requireNonNull(filePath);
		try (InputStream is = Files.newInputStream(filePath)) {
			deSerialize(modelStore, documentUri, is);
		}
	}

}
