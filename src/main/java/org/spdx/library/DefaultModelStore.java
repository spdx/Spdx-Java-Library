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

import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * @author Gary O'Neall
 * 
 * Singleton class to hold a default model store used when no model store is provided
 * 
 * WARNING: The model store is in memory and will continue to grow as it is utilized.  There is NO garbage collection.
 *
 */
public class DefaultModelStore {
	
	static IModelStore defaultModelStore = new InMemSpdxStore();
	static final String defaultDocumentUri = "http://www.spdx.org/documents/default_doc_uri_for_SPDX_tools";
	
	private DefaultModelStore() {
		// prevent instantiating class
	}
	
	public static IModelStore getDefaultModelStore() {
		return defaultModelStore;
	}
	
	public static String getDefaultDocumentUri() {
		return defaultDocumentUri;
	}
	
	/**
	 * Clears the default model store by replacing the default model store with a new one
	 */
	public static final void reset() {
		defaultModelStore = new InMemSpdxStore();
	}

}
