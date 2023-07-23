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
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.core.IntegrityMethod.IntegrityMethodBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class IntegrityMethodTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String COMMENT_TEST_VALUE = "test comment";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static IntegrityMethodBuilder builderForIntegrityMethodTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		IntegrityMethodBuilder retval = new IntegrityMethodBuilder(modelStore, objectUri, copyManager)
				.setComment(COMMENT_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.IntegrityMethod#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		IntegrityMethod testIntegrityMethod = builderForIntegrityMethodTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testIntegrityMethod.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.IntegrityMethod#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		IntegrityMethod testIntegrityMethod = builderForIntegrityMethodTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.IntegrityMethod", testIntegrityMethod.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.IntegrityMethod#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		IntegrityMethod testIntegrityMethod = builderForIntegrityMethodTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("IntegrityMethod: "+TEST_OBJECT_URI, testIntegrityMethod.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.IntegrityMethod#Element(org.spdx.library.model.core.IntegrityMethod.IntegrityMethodBuilder)}.
	 */
	public void testIntegrityMethodIntegrityMethodBuilder() throws InvalidSPDXAnalysisException {
		builderForIntegrityMethodTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		IntegrityMethod testIntegrityMethod = builderForIntegrityMethodTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		IntegrityMethod test2IntegrityMethod = builderForIntegrityMethodTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testIntegrityMethod.equivalent(test2IntegrityMethod));
		assertTrue(test2IntegrityMethod.equivalent(testIntegrityMethod));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.IntegrityMethod#setComment}.
	 */
	public void testIntegrityMethodsetComment() throws InvalidSPDXAnalysisException {
		IntegrityMethod testIntegrityMethod = builderForIntegrityMethodTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(COMMENT_TEST_VALUE), testIntegrityMethod.getComment());
		testIntegrityMethod.setComment("new comment value");
		assertEquals(Optional.of("new comment value"), testIntegrityMethod.getComment());
	}
}