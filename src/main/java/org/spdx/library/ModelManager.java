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
package org.spdx.library;

import org.spdx.core.ModelRegistry;
import org.spdx.library.model.v2.SpdxModelInfoV2_X;
import org.spdx.library.model.v3.SpdxModelInfoV3_0;

/**
 * Main entrypoint for the SPDX Java Library
 * 
 * This is a static class used to manage the different versions of the SPDX spec by
 * creating different model classes based on the version of the spec.
 * 
 * Since the release of the SPDX spec version 3.0, the Java classes were generated.
 * 
 * Each generated set of classes generated for a specific version are in a separate library / Jar file.
 * 
 * These generated classes are registered in the Core model registry
 * 
 * @author Gary O'Neall
 *
 */
public class ModelManager {
	
	static {
		// register the supported spec version models
		ModelRegistry.getModelRegistry().registerModel(new SpdxModelInfoV2_X());
		ModelRegistry.getModelRegistry().registerModel(new SpdxModelInfoV3_0());
	}

	/**
	 * Static class private constructor
	 */
	private ModelManager() {
		// Static class
	}

}
