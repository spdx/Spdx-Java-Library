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
 
package org.spdx.library.model.software;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.software.Snippet.SnippetBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class SnippetTest extends TestCase {

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
	
	public static SnippetBuilder builderForSnippetTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SnippetBuilder retval = new SnippetBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setbyteRange(new PositiveIntegerRange())
		.setlineRange(new PositiveIntegerRange())
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.Snippet#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Snippet testSnippet = builderForSnippetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testSnippet.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.Snippet#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Snippet testSnippet = builderForSnippetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Software.Snippet", testSnippet.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.Snippet#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Snippet testSnippet = builderForSnippetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Snippet: "+TEST_OBJECT_URI, testSnippet.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.Snippet#Element(org.spdx.library.model.software.Snippet.SnippetBuilder)}.
	 */
	public void testSnippetSnippetBuilder() throws InvalidSPDXAnalysisException {
		Snippet testSnippet = builderForSnippetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Snippet testSnippet = builderForSnippetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Snippet test2Snippet = builderForSnippetTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testSnippet.equivalent(test2Snippet));
		assertTrue(test2Snippet.equivalent(testSnippet));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.Snippet#setByteRange}.
	 */
	public void testSnippetsetByteRange() throws InvalidSPDXAnalysisException {
		Snippet testSnippet = builderForSnippetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testSnippet.getByteRange());
//		testSnippet.setByteRange(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testSnippet.getByteRange());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.Snippet#setLineRange}.
	 */
	public void testSnippetsetLineRange() throws InvalidSPDXAnalysisException {
		Snippet testSnippet = builderForSnippetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testSnippet.getLineRange());
//		testSnippet.setLineRange(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testSnippet.getLineRange());
		fail("Not yet implemented");
	}

/*
*/

}