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
import org.spdx.library.model.core.ExternalReference.ExternalReferenceBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ExternalReferenceTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String CONTENT_TYPE_TEST_VALUE = "test contentType";
	static final String COMMENT_TEST_VALUE = "test comment";
	static final String LOCATOR_TEST_VALUE1 = "test 1 locator";
	static final String LOCATOR_TEST_VALUE2 = "test 2 locator";
	static final String LOCATOR_TEST_VALUE3 = "test 3 locator";
	static final List<String> LOCATOR_TEST_LIST1 = Arrays.asList(new String[] { LOCATOR_TEST_VALUE1, LOCATOR_TEST_VALUE2 });
	static final List<String> LOCATOR_TEST_LIST2 = Arrays.asList(new String[] { LOCATOR_TEST_VALUE3 });
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static ExternalReferenceBuilder builderForExternalReferenceTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ExternalReferenceBuilder retval = new ExternalReferenceBuilder(modelStore, objectUri, copyManager)
				.setContentType(CONTENT_TYPE_TEST_VALUE)
				.setComment(COMMENT_TEST_VALUE)
				.addLocator(LOCATOR_TEST_VALUE1)
				.addLocator(LOCATOR_TEST_VALUE2)
				//TODO: Add in test values
				/********************
				.setExternalReferenceType(ExternalReferenceType.ENUM)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalReference#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ExternalReference testExternalReference = builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testExternalReference.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalReference#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ExternalReference testExternalReference = builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.ExternalReference", testExternalReference.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalReference#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ExternalReference testExternalReference = builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExternalReference: "+TEST_OBJECT_URI, testExternalReference.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalReference#Element(org.spdx.library.model.core.ExternalReference.ExternalReferenceBuilder)}.
	 */
	public void testExternalReferenceExternalReferenceBuilder() throws InvalidSPDXAnalysisException {
		builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ExternalReference testExternalReference = builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ExternalReference test2ExternalReference = builderForExternalReferenceTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testExternalReference.equivalent(test2ExternalReference));
		assertTrue(test2ExternalReference.equivalent(testExternalReference));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalReference#setExternalReferenceType}.
	 */
	public void testExternalReferencesetExternalReferenceType() throws InvalidSPDXAnalysisException {
		ExternalReference testExternalReference = builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testExternalReference.getExternalReferenceType());
//		testExternalReference.setExternalReferenceType(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testExternalReference.getExternalReferenceType());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalReference#setContentType}.
	 */
	public void testExternalReferencesetContentType() throws InvalidSPDXAnalysisException {
		ExternalReference testExternalReference = builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(CONTENT_TYPE_TEST_VALUE, testExternalReference.getContentType());
		testExternalReference.setContentType("new contentType value");
		assertEquals("new contentType value", testExternalReference.getContentType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalReference#setComment}.
	 */
	public void testExternalReferencesetComment() throws InvalidSPDXAnalysisException {
		ExternalReference testExternalReference = builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(COMMENT_TEST_VALUE, testExternalReference.getComment());
		testExternalReference.setComment("new comment value");
		assertEquals("new comment value", testExternalReference.getComment());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalReference#getLocators}.
	 */
	public void testExternalReferencegetLocators() throws InvalidSPDXAnalysisException {
		ExternalReference testExternalReference = builderForExternalReferenceTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(LOCATOR_TEST_LIST1, new ArrayList<>(testExternalReference.getLocators())));
		testExternalReference.getLocators().clear();
		testExternalReference.getLocators().addAll(LOCATOR_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(LOCATOR_TEST_LIST2, new ArrayList<>(testExternalReference.getLocators())));
		fail("Not yet implemented");
	}
}