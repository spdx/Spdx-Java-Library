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
import org.spdx.library.model.v3.core.ElementCollection.ElementCollectionBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ElementCollectionTest extends TestCase {

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
	
	public static ElementCollectionBuilder builderForElementCollectionTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ElementCollectionBuilder retval = new ElementCollectionBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				.addImports(ExternalMap)
				.addElement(Element)
				.addRootElement(Element)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ElementCollection#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ElementCollection testElementCollection = builderForElementCollectionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testElementCollection.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ElementCollection#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ElementCollection testElementCollection = builderForElementCollectionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.ElementCollection", testElementCollection.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ElementCollection#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ElementCollection testElementCollection = builderForElementCollectionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ElementCollection: "+TEST_OBJECT_URI, testElementCollection.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ElementCollection#Element(org.spdx.library.model.v3.core.ElementCollection.ElementCollectionBuilder)}.
	 */
	public void testElementCollectionElementCollectionBuilder() throws InvalidSPDXAnalysisException {
		builderForElementCollectionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ElementCollection testElementCollection = builderForElementCollectionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ElementCollection test2ElementCollection = builderForElementCollectionTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testElementCollection.equivalent(test2ElementCollection));
		assertTrue(test2ElementCollection.equivalent(testElementCollection));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ElementCollection#getImports}.
	 */
	public void testElementCollectiongetImportss() throws InvalidSPDXAnalysisException {
		ElementCollection testElementCollection = builderForElementCollectionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElementCollection.getImportss())));
//		testElementCollection.getImportss().clear();
//		testElementCollection.getImportss().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElementCollection.getImportss())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ElementCollection#getElement}.
	 */
	public void testElementCollectiongetElements() throws InvalidSPDXAnalysisException {
		ElementCollection testElementCollection = builderForElementCollectionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElementCollection.getElements())));
//		testElementCollection.getElements().clear();
//		testElementCollection.getElements().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElementCollection.getElements())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ElementCollection#getRootElement}.
	 */
	public void testElementCollectiongetRootElements() throws InvalidSPDXAnalysisException {
		ElementCollection testElementCollection = builderForElementCollectionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElementCollection.getRootElements())));
//		testElementCollection.getRootElements().clear();
//		testElementCollection.getRootElements().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElementCollection.getRootElements())));
		fail("Not yet implemented");
	}
}