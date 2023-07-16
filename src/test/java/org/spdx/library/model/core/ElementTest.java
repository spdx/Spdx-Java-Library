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
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.core.Element.ElementBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ElementTest extends TestCase {

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
	
	public static ElementBuilder builderForElementTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ElementBuilder retval = new ElementBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setdescription("A string")
		.setsummary("A string")
		.getexternalReference.add(ExternalReference)
		.getexternalIdentifier.add(ExternalIdentifier)
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Element#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testElement.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Element#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Element", testElement.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Element#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Element: "+TEST_OBJECT_URI, testElement.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Element#Element(org.spdx.library.model.core.Element.ElementBuilder)}.
	 */
	public void testElementElementBuilder() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Element test2Element = builderForElementTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testElement.equivalent(test2Element));
		assertTrue(test2Element.equivalent(testElement));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Element#setDescription}.
	 */
	public void testElementsetDescription() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testElement.getDescription());
//		testElement.setDescription(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testElement.getDescription());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Element#setSummary}.
	 */
	public void testElementsetSummary() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testElement.getSummary());
//		testElement.setSummary(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testElement.getSummary());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Element#getExternalReference}.
	 */
	public void testElementsetExternalReference() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElement.getExternalReference()));
//		testElement.getExternalReference().clear();
//		testElement.getExternalReference().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElement.getExternalReference()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Element#getExternalIdentifier}.
	 */
	public void testElementsetExternalIdentifier() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElement.getExternalIdentifier()));
//		testElement.getExternalIdentifier().clear();
//		testElement.getExternalIdentifier().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElement.getExternalIdentifier()));
		fail("Not yet implemented");
	}

/*
*/

}