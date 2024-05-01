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
import org.spdx.library.model.v3.core.LifecycleScopedRelationship.LifecycleScopedRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class LifecycleScopedRelationshipTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final LifecycleScopeType SCOPE_TEST_VALUE1 = LifecycleScopeType.values()[0];
	static final LifecycleScopeType SCOPE_TEST_VALUE2 = LifecycleScopeType.values()[1];
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static LifecycleScopedRelationshipBuilder builderForLifecycleScopedRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		LifecycleScopedRelationshipBuilder retval = new LifecycleScopedRelationshipBuilder(modelStore, objectUri, copyManager)
				.setScope(SCOPE_TEST_VALUE1)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.LifecycleScopedRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		LifecycleScopedRelationship testLifecycleScopedRelationship = builderForLifecycleScopedRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testLifecycleScopedRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.LifecycleScopedRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		LifecycleScopedRelationship testLifecycleScopedRelationship = builderForLifecycleScopedRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.LifecycleScopedRelationship", testLifecycleScopedRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.LifecycleScopedRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		LifecycleScopedRelationship testLifecycleScopedRelationship = builderForLifecycleScopedRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("LifecycleScopedRelationship: "+TEST_OBJECT_URI, testLifecycleScopedRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.LifecycleScopedRelationship#Element(org.spdx.library.model.v3.core.LifecycleScopedRelationship.LifecycleScopedRelationshipBuilder)}.
	 */
	public void testLifecycleScopedRelationshipLifecycleScopedRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForLifecycleScopedRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		LifecycleScopedRelationship testLifecycleScopedRelationship = builderForLifecycleScopedRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		LifecycleScopedRelationship test2LifecycleScopedRelationship = builderForLifecycleScopedRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testLifecycleScopedRelationship.equivalent(test2LifecycleScopedRelationship));
		assertTrue(test2LifecycleScopedRelationship.equivalent(testLifecycleScopedRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.LifecycleScopedRelationship#setScope}.
	 */
	public void testLifecycleScopedRelationshipsetScope() throws InvalidSPDXAnalysisException {
		LifecycleScopedRelationship testLifecycleScopedRelationship = builderForLifecycleScopedRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(SCOPE_TEST_VALUE1), testLifecycleScopedRelationship.getScope());
		testLifecycleScopedRelationship.setScope(SCOPE_TEST_VALUE2);
		assertEquals(Optional.of(SCOPE_TEST_VALUE2), testLifecycleScopedRelationship.getScope());
	}
}