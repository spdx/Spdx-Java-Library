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
package org.spdx.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.spdx.library.InvalidSPDXAnalysisException;

/**
 * A model store that can be serialized and de-serialized to and from a <code>Stream</code>
 * @author Gary O'Neall
 *
 */
public interface ISerializableModelStore extends IModelStore {

	/**
	 * Serialize the items stored in the documentUri.  The specific format for serialization depends on the document store.
	 * @param documentUri URI for the document to be serialized
	 * @param stream output stream to serialize to
	 * @throws InvalidSPDXAnalysisException
	 * @throws IOException
	 */
	public void serialize(String documentUri, OutputStream stream)  throws InvalidSPDXAnalysisException, IOException;
	
	/**
	 * Deserialize / read an SPDX document from a stream
	 * @param stream input stream to deserialize from
	 * @param overwrite if true, allow any existing documents with the same documentUri to be overwritten
	 * @return document URI of the document
	 * @throws InvalidSPDXAnalysisException
	 * @throws IOException
	 */
	public String deSerialize(InputStream stream, boolean overwrite) throws InvalidSPDXAnalysisException, IOException;
}
