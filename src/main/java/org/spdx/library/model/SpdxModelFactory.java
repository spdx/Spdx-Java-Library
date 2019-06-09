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
package org.spdx.library.model;

import org.spdx.storage.IModelStore;

/**
 * @author gary
 *
 */
public class SpdxModelFactory {

	public static ModelObject createModelObject(IModelStore modelStore, String documentUri, String retval,
			String type) {
		// I think this is going to be one massive switch statement based on the CLASS names
		throw new RuntimeException("Not implemented");
	}
	
	//TODO Implement

}
