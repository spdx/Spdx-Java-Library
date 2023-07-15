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
import org.spdx.library.model.core.ExternalMap.ExternalMapBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ExternalMapTest extends TestCase {

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
	
	public static ExternalMapBuilder builderForExternalMapTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ExternalMapBuilder retval = new ExternalMapBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setdefiningDocument("A string")
		.setlocationHint("A string")
		.setexternalId("A string")
		.getverifiedUsing.add(IntegrityMethod)
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testExternalMap.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.ExternalMap", testExternalMap.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExternalMap: "+TEST_OBJECT_URI, testExternalMap.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#Element(org.spdx.library.model.core.ExternalMap.ExternalMapBuilder)}.
	 */
	public void testExternalMapExternalMapBuilder() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ExternalMap test2ExternalMap = builderForExternalMapTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testExternalMap.equivalent(test2ExternalMap));
		assertTrue(test2ExternalMap.equivalent(testExternalMap));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#setDefiningDocument}.
	 */
	public void testExternalMapsetDefiningDocument() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testExternalMap.getDefiningDocument());
//		testExternalMap.setDefiningDocument(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testExternalMap.getDefiningDocument());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#setLocationHint}.
	 */
	public void testExternalMapsetLocationHint() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testExternalMap.getLocationHint());
//		testExternalMap.setLocationHint(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testExternalMap.getLocationHint());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#setExternalId}.
	 */
	public void testExternalMapsetExternalId() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testExternalMap.getExternalId());
//		testExternalMap.setExternalId(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testExternalMap.getExternalId());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#getVerifiedUsing}.
	 */
	public void testExternalMapsetVerifiedUsing() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testExternalMap.getVerifiedUsing()));
//		testExternalMap.getVerifiedUsing().clear();
//		testExternalMap.getVerifiedUsing().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testExternalMap.getVerifiedUsing()));
		fail("Not yet implemented");
	}

/*
*/

}