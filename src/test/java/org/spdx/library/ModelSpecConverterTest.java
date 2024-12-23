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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;

/**
 * @author Gary O'Neall
 */
public class ModelSpecConverterTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	private String buildJsonSpdx2Types() {
		HashSet rdfsClasses = new HashSet<>(
				Arrays.asList(new String[] {SpdxConstantsCompatV2.CLASS_POINTER_START_END_POINTER, 
						SpdxConstantsCompatV2.CLASS_POINTER_BYTE_OFFSET_POINTER, 
						SpdxConstantsCompatV2.CLASS_POINTER_COMPOUNT_POINTER, 
						SpdxConstantsCompatV2.CLASS_POINTER_LINE_CHAR_POINTER, 
						SpdxConstantsCompatV2.CLASS_SINGLE_POINTER})
				);
		
		StringBuilder sb = new StringBuilder("   \"typeUriMap\" : {\n");
		for (int i = 0; i < SpdxConstantsCompatV2.ALL_SPDX_CLASSES.length; i++) {
			String v2Class = SpdxConstantsCompatV2.ALL_SPDX_CLASSES[i];
			sb.append("      \"");
			if (SpdxConstantsCompatV2.CLASS_DOAP_PROJECT.equals(v2Class)) {
				sb.append(SpdxConstantsCompatV2.DOAP_NAMESPACE);
			} else if (rdfsClasses.contains(v2Class)) {
				sb.append(SpdxConstantsCompatV2.RDFS_NAMESPACE);
			} else {
				sb.append(SpdxConstantsCompatV2.SPDX_NAMESPACE);
			}
			sb.append(v2Class);
			sb.append("\" : \"\"");
			if (i < SpdxConstantsCompatV2.ALL_SPDX_CLASSES.length - 1) {
				sb.append(",");
			}
			sb.append("\n");
		}
		sb.append("   }");
		return sb.toString();
	}

	@Test
	public void test() {
		// Just collects the info
		String v2Types = buildJsonSpdx2Types();
		int i = 0;
		i++;
	}

}
