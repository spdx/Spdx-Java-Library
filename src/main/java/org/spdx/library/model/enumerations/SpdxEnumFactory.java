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
package org.spdx.library.model.enumerations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gary
 *
 */
public class SpdxEnumFactory {
	
	/**
	 * Map of enum URI's to their Enum values
	 */
	public static Map<String, Enum<?>> uriToEnum;
	
	static {
		Map<String, Enum<?>> map = new HashMap<>();
		for (AnnotationType annotationType:AnnotationType.values()) {
			map.put(annotationType.getIndividualURI(), annotationType);
		}
		for (RelationshipType relationshipType:RelationshipType.values()) {
			map.put(relationshipType.getIndividualURI(), relationshipType);
		}
		for (ChecksumAlgorithm algorithm:ChecksumAlgorithm.values()) {
			map.put(algorithm.getIndividualURI(), algorithm);
		}
		for (ReferenceCategory referenceCategory:ReferenceCategory.values()) {
			map.put(referenceCategory.getIndividualURI(), referenceCategory);
		}
		uriToEnum = Collections.unmodifiableMap(map);
	}

	private SpdxEnumFactory() {
		// this is only a static class
	}

}
