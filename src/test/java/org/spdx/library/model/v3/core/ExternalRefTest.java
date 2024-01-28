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
import org.spdx.library.model.v3.core.ExternalRef.ExternalRefBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ExternalRefTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String COMMENT_TEST_VALUE = "test comment";
	static final String CONTENT_TYPE_TEST_VALUE = "test contentType";
	static final ExternalRefType EXTERNAL_REF_TYPE_TEST_VALUE1 = ExternalRefType.values()[0];
	static final ExternalRefType EXTERNAL_REF_TYPE_TEST_VALUE2 = ExternalRefType.values()[1];
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
	
	public static ExternalRefBuilder builderForExternalRefTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ExternalRefBuilder retval = new ExternalRefBuilder(modelStore, objectUri, copyManager)
				.setComment(COMMENT_TEST_VALUE)
				.setContentType(CONTENT_TYPE_TEST_VALUE)
				.addLocator(LOCATOR_TEST_VALUE1)
				.addLocator(LOCATOR_TEST_VALUE2)
				.setExternalRefType(EXTERNAL_REF_TYPE_TEST_VALUE1)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalRef#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ExternalRef testExternalRef = builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testExternalRef.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalRef#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ExternalRef testExternalRef = builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.ExternalRef", testExternalRef.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalRef#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ExternalRef testExternalRef = builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExternalRef: "+TEST_OBJECT_URI, testExternalRef.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalRef#Element(org.spdx.library.model.v3.core.ExternalRef.ExternalRefBuilder)}.
	 */
	public void testExternalRefExternalRefBuilder() throws InvalidSPDXAnalysisException {
		builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ExternalRef testExternalRef = builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ExternalRef test2ExternalRef = builderForExternalRefTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testExternalRef.equivalent(test2ExternalRef));
		assertTrue(test2ExternalRef.equivalent(testExternalRef));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalRef#setExternalRefType}.
	 */
	public void testExternalRefsetExternalRefType() throws InvalidSPDXAnalysisException {
		ExternalRef testExternalRef = builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(EXTERNAL_REF_TYPE_TEST_VALUE1), testExternalRef.getExternalRefType());
		testExternalRef.setExternalRefType(EXTERNAL_REF_TYPE_TEST_VALUE2);
		assertEquals(Optional.of(EXTERNAL_REF_TYPE_TEST_VALUE2), testExternalRef.getExternalRefType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalRef#setComment}.
	 */
	public void testExternalRefsetComment() throws InvalidSPDXAnalysisException {
		ExternalRef testExternalRef = builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(COMMENT_TEST_VALUE), testExternalRef.getComment());
		testExternalRef.setComment("new comment value");
		assertEquals(Optional.of("new comment value"), testExternalRef.getComment());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalRef#setContentType}.
	 */
	public void testExternalRefsetContentType() throws InvalidSPDXAnalysisException {
		ExternalRef testExternalRef = builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(CONTENT_TYPE_TEST_VALUE), testExternalRef.getContentType());
		testExternalRef.setContentType("new contentType value");
		assertEquals(Optional.of("new contentType value"), testExternalRef.getContentType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalRef#getLocators}.
	 */
	public void testExternalRefgetLocators() throws InvalidSPDXAnalysisException {
		ExternalRef testExternalRef = builderForExternalRefTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(LOCATOR_TEST_LIST1, new ArrayList<>(testExternalRef.getLocators())));
		testExternalRef.getLocators().clear();
		testExternalRef.getLocators().addAll(LOCATOR_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(LOCATOR_TEST_LIST2, new ArrayList<>(testExternalRef.getLocators())));
	}
}