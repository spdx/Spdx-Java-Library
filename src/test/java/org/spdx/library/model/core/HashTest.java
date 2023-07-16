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
import org.spdx.library.model.core.Hash.HashBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class HashTest extends TestCase {

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
	
	public static HashBuilder builderForHashTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		HashBuilder retval = new HashBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setalgorithm(HashAlgorithm.ENUM)
		.sethashValue("A string")
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Hash#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Hash testHash = builderForHashTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testHash.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Hash#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Hash testHash = builderForHashTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Hash", testHash.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Hash#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Hash testHash = builderForHashTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Hash: "+TEST_OBJECT_URI, testHash.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Hash#Element(org.spdx.library.model.core.Hash.HashBuilder)}.
	 */
	public void testHashHashBuilder() throws InvalidSPDXAnalysisException {
		Hash testHash = builderForHashTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Hash testHash = builderForHashTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Hash test2Hash = builderForHashTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testHash.equivalent(test2Hash));
		assertTrue(test2Hash.equivalent(testHash));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Hash#setAlgorithm}.
	 */
	public void testHashsetAlgorithm() throws InvalidSPDXAnalysisException {
		Hash testHash = builderForHashTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testHash.getAlgorithm());
//		testHash.setAlgorithm(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testHash.getAlgorithm());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Hash#setHashValue}.
	 */
	public void testHashsetHashValue() throws InvalidSPDXAnalysisException {
		Hash testHash = builderForHashTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testHash.getHashValue());
//		testHash.setHashValue(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testHash.getHashValue());
		fail("Not yet implemented");
	}

/*
*/

}