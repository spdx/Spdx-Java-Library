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
import org.spdx.library.model.core.ExternalMap.ExternalMapBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ExternalMapTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String DEFINING_DOCUMENT_TEST_VALUE = "test definingDocument";
	static final String LOCATION_HINT_TEST_VALUE = "test locationHint";
	static final String EXTERNAL_ID_TEST_VALUE = "test externalId";
	
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
		ExternalMapBuilder retval = new ExternalMapBuilder(modelStore, objectUri, copyManager)
				.setDefiningDocument(DEFINING_DOCUMENT_TEST_VALUE)
				.setLocationHint(LOCATION_HINT_TEST_VALUE)
				.setExternalId(EXTERNAL_ID_TEST_VALUE)
				//TODO: Add in test values
				/********************
				.addVerifiedUsing(IntegrityMethod)
				***************/
				;
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
		builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
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
		assertEquals(DEFINING_DOCUMENT_TEST_VALUE, testExternalMap.getDefiningDocument());
		testExternalMap.setDefiningDocument("new definingDocument value");
		assertEquals("new definingDocument value", testExternalMap.getDefiningDocument());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#setLocationHint}.
	 */
	public void testExternalMapsetLocationHint() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(LOCATION_HINT_TEST_VALUE, testExternalMap.getLocationHint());
		testExternalMap.setLocationHint("new locationHint value");
		assertEquals("new locationHint value", testExternalMap.getLocationHint());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#setExternalId}.
	 */
	public void testExternalMapsetExternalId() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(EXTERNAL_ID_TEST_VALUE, testExternalMap.getExternalId());
		testExternalMap.setExternalId("new externalId value");
		assertEquals("new externalId value", testExternalMap.getExternalId());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalMap#getVerifiedUsing}.
	 */
	public void testExternalMapgetVerifiedUsings() throws InvalidSPDXAnalysisException {
		ExternalMap testExternalMap = builderForExternalMapTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testExternalMap.getVerifiedUsings())));
//		testExternalMap.getVerifiedUsings().clear();
//		testExternalMap.getVerifiedUsings().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testExternalMap.getVerifiedUsings())));
		fail("Not yet implemented");
	}
}