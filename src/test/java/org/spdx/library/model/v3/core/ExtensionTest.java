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
 
package org.spdx.library.model.v3.core;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.core.Extension.ExtensionBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ExtensionTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static ExtensionBuilder builderForExtensionTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ExtensionBuilder retval = new ExtensionBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Extension#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Extension testExtension = builderForExtensionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testExtension.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Extension#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Extension testExtension = builderForExtensionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Extension", testExtension.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Extension#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Extension testExtension = builderForExtensionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Extension: "+TEST_OBJECT_URI, testExtension.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Extension#Element(org.spdx.library.model.v3.core.Extension.ExtensionBuilder)}.
	 */
	public void testExtensionExtensionBuilder() throws InvalidSPDXAnalysisException {
		builderForExtensionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Extension testExtension = builderForExtensionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Extension test2Extension = builderForExtensionTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testExtension.equivalent(test2Extension));
		assertTrue(test2Extension.equivalent(testExtension));
		// TODO change some parameters for negative tests
	}
}