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
import org.spdx.library.model.v3.core.Element.ElementBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ElementTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String SUMMARY_TEST_VALUE = "test summary";
	static final String DESCRIPTION_TEST_VALUE = "test description";
	static final String COMMENT_TEST_VALUE = "test comment";
	static final String NAME_TEST_VALUE = "test name";
	
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
		ElementBuilder retval = new ElementBuilder(modelStore, objectUri, copyManager)
				.setSummary(SUMMARY_TEST_VALUE)
				.setDescription(DESCRIPTION_TEST_VALUE)
				.setComment(COMMENT_TEST_VALUE)
				.setName(NAME_TEST_VALUE)
				//TODO: Add in test values
				/********************
				.setCreationInfo(new CreationInfo())
				.addExternalRef(ExternalRef)
				.addExtension(Extension)
				.addExternalIdentifier(ExternalIdentifier)
				.addVerifiedUsing(IntegrityMethod)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testElement.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Element", testElement.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Element: "+TEST_OBJECT_URI, testElement.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#Element(org.spdx.library.model.v3.core.Element.ElementBuilder)}.
	 */
	public void testElementElementBuilder() throws InvalidSPDXAnalysisException {
		builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Element test2Element = builderForElementTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testElement.equivalent(test2Element));
		assertTrue(test2Element.equivalent(testElement));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#setCreationInfo}.
	 */
	public void testElementsetCreationInfo() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testElement.getCreationInfo());
//		testElement.setCreationInfo(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testElement.getCreationInfo());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#setSummary}.
	 */
	public void testElementsetSummary() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(SUMMARY_TEST_VALUE), testElement.getSummary());
		testElement.setSummary("new summary value");
		assertEquals(Optional.of("new summary value"), testElement.getSummary());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#setDescription}.
	 */
	public void testElementsetDescription() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(DESCRIPTION_TEST_VALUE), testElement.getDescription());
		testElement.setDescription("new description value");
		assertEquals(Optional.of("new description value"), testElement.getDescription());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#setComment}.
	 */
	public void testElementsetComment() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(COMMENT_TEST_VALUE), testElement.getComment());
		testElement.setComment("new comment value");
		assertEquals(Optional.of("new comment value"), testElement.getComment());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#setName}.
	 */
	public void testElementsetName() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(NAME_TEST_VALUE), testElement.getName());
		testElement.setName("new name value");
		assertEquals(Optional.of("new name value"), testElement.getName());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#getExternalRef}.
	 */
	public void testElementgetExternalRefs() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElement.getExternalRefs())));
//		testElement.getExternalRefs().clear();
//		testElement.getExternalRefs().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElement.getExternalRefs())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#getExtension}.
	 */
	public void testElementgetExtensions() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElement.getExtensions())));
//		testElement.getExtensions().clear();
//		testElement.getExtensions().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElement.getExtensions())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#getExternalIdentifier}.
	 */
	public void testElementgetExternalIdentifiers() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElement.getExternalIdentifiers())));
//		testElement.getExternalIdentifiers().clear();
//		testElement.getExternalIdentifiers().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElement.getExternalIdentifiers())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Element#getVerifiedUsing}.
	 */
	public void testElementgetVerifiedUsings() throws InvalidSPDXAnalysisException {
		Element testElement = builderForElementTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testElement.getVerifiedUsings())));
//		testElement.getVerifiedUsings().clear();
//		testElement.getVerifiedUsings().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testElement.getVerifiedUsings())));
		fail("Not yet implemented");
	}
}