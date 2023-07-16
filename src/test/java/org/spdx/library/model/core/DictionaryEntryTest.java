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
import org.spdx.library.model.core.DictionaryEntry.DictionaryEntryBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class DictionaryEntryTest extends TestCase {

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
	
	public static DictionaryEntryBuilder builderForDictionaryEntryTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		DictionaryEntryBuilder retval = new DictionaryEntryBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setkey("A string")
		.setvalue("A string")
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.DictionaryEntry#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		DictionaryEntry testDictionaryEntry = builderForDictionaryEntryTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testDictionaryEntry.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.DictionaryEntry#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		DictionaryEntry testDictionaryEntry = builderForDictionaryEntryTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.DictionaryEntry", testDictionaryEntry.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.DictionaryEntry#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		DictionaryEntry testDictionaryEntry = builderForDictionaryEntryTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("DictionaryEntry: "+TEST_OBJECT_URI, testDictionaryEntry.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.DictionaryEntry#Element(org.spdx.library.model.core.DictionaryEntry.DictionaryEntryBuilder)}.
	 */
	public void testDictionaryEntryDictionaryEntryBuilder() throws InvalidSPDXAnalysisException {
		DictionaryEntry testDictionaryEntry = builderForDictionaryEntryTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		DictionaryEntry testDictionaryEntry = builderForDictionaryEntryTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		DictionaryEntry test2DictionaryEntry = builderForDictionaryEntryTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testDictionaryEntry.equivalent(test2DictionaryEntry));
		assertTrue(test2DictionaryEntry.equivalent(testDictionaryEntry));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.DictionaryEntry#setKey}.
	 */
	public void testDictionaryEntrysetKey() throws InvalidSPDXAnalysisException {
		DictionaryEntry testDictionaryEntry = builderForDictionaryEntryTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDictionaryEntry.getKey());
//		testDictionaryEntry.setKey(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDictionaryEntry.getKey());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.DictionaryEntry#setValue}.
	 */
	public void testDictionaryEntrysetValue() throws InvalidSPDXAnalysisException {
		DictionaryEntry testDictionaryEntry = builderForDictionaryEntryTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDictionaryEntry.getValue());
//		testDictionaryEntry.setValue(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDictionaryEntry.getValue());
		fail("Not yet implemented");
	}

/*
*/

}