/**
 * Copyright (c) 2023 Source Auditor Inc.
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
 
package org.spdx.library.model.core;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.core.NamespaceMap.NamespaceMapBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class NamespaceMapTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String PREFIX_TEST_VALUE = "test prefix";
	static final String NAMESPACE_TEST_VALUE = "test namespace";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static NamespaceMapBuilder builderForNamespaceMapTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		NamespaceMapBuilder retval = new NamespaceMapBuilder(modelStore, objectUri, copyManager)
				.setPrefix(PREFIX_TEST_VALUE)
				.setNamespace(NAMESPACE_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.NamespaceMap#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		NamespaceMap testNamespaceMap = builderForNamespaceMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testNamespaceMap.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.NamespaceMap#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		NamespaceMap testNamespaceMap = builderForNamespaceMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.NamespaceMap", testNamespaceMap.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.NamespaceMap#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		NamespaceMap testNamespaceMap = builderForNamespaceMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("NamespaceMap: "+TEST_OBJECT_URI, testNamespaceMap.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.NamespaceMap#Element(org.spdx.library.model.core.NamespaceMap.NamespaceMapBuilder)}.
	 */
	public void testNamespaceMapNamespaceMapBuilder() throws InvalidSPDXAnalysisException {
		builderForNamespaceMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		NamespaceMap testNamespaceMap = builderForNamespaceMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		NamespaceMap test2NamespaceMap = builderForNamespaceMapTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testNamespaceMap.equivalent(test2NamespaceMap));
		assertTrue(test2NamespaceMap.equivalent(testNamespaceMap));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.NamespaceMap#setPrefix}.
	 */
	public void testNamespaceMapsetPrefix() throws InvalidSPDXAnalysisException {
		NamespaceMap testNamespaceMap = builderForNamespaceMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(PREFIX_TEST_VALUE, testNamespaceMap.getPrefix());
		testNamespaceMap.setPrefix("new prefix value");
		assertEquals("new prefix value", testNamespaceMap.getPrefix());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.NamespaceMap#setNamespace}.
	 */
	public void testNamespaceMapsetNamespace() throws InvalidSPDXAnalysisException {
		NamespaceMap testNamespaceMap = builderForNamespaceMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(NAMESPACE_TEST_VALUE, testNamespaceMap.getNamespace());
		testNamespaceMap.setNamespace("new namespace value");
		assertEquals("new namespace value", testNamespaceMap.getNamespace());
	}
}