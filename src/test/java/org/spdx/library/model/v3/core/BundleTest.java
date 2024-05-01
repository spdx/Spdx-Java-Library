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
import org.spdx.library.model.v3.core.Bundle.BundleBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class BundleTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String CONTEXT_TEST_VALUE = "test context";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static BundleBuilder builderForBundleTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		BundleBuilder retval = new BundleBuilder(modelStore, objectUri, copyManager)
				.setContext(CONTEXT_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Bundle#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Bundle testBundle = builderForBundleTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testBundle.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Bundle#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Bundle testBundle = builderForBundleTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Bundle", testBundle.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Bundle#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Bundle testBundle = builderForBundleTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Bundle: "+TEST_OBJECT_URI, testBundle.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Bundle#Element(org.spdx.library.model.v3.core.Bundle.BundleBuilder)}.
	 */
	public void testBundleBundleBuilder() throws InvalidSPDXAnalysisException {
		builderForBundleTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Bundle testBundle = builderForBundleTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Bundle test2Bundle = builderForBundleTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testBundle.equivalent(test2Bundle));
		assertTrue(test2Bundle.equivalent(testBundle));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Bundle#setContext}.
	 */
	public void testBundlesetContext() throws InvalidSPDXAnalysisException {
		Bundle testBundle = builderForBundleTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(CONTEXT_TEST_VALUE), testBundle.getContext());
		testBundle.setContext("new context value");
		assertEquals(Optional.of("new context value"), testBundle.getContext());
	}
}