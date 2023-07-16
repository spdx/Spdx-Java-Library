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
import org.spdx.library.model.core.Payload.PayloadBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class PayloadTest extends TestCase {

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
	
	public static PayloadBuilder builderForPayloadTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		PayloadBuilder retval = new PayloadBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setcreationInfo(new CreationInfo())
		.getnamespaces.add(NamespaceMap)
		.getimports.add(ExternalMap)
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Payload#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Payload testPayload = builderForPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testPayload.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Payload#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Payload testPayload = builderForPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Payload", testPayload.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Payload#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Payload testPayload = builderForPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Payload: "+TEST_OBJECT_URI, testPayload.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Payload#Element(org.spdx.library.model.core.Payload.PayloadBuilder)}.
	 */
	public void testPayloadPayloadBuilder() throws InvalidSPDXAnalysisException {
		Payload testPayload = builderForPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Payload testPayload = builderForPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Payload test2Payload = builderForPayloadTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testPayload.equivalent(test2Payload));
		assertTrue(test2Payload.equivalent(testPayload));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Payload#setCreationInfo}.
	 */
	public void testPayloadsetCreationInfo() throws InvalidSPDXAnalysisException {
		Payload testPayload = builderForPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testPayload.getCreationInfo());
//		testPayload.setCreationInfo(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testPayload.getCreationInfo());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Payload#getNamespaces}.
	 */
	public void testPayloadsetNamespaces() throws InvalidSPDXAnalysisException {
		Payload testPayload = builderForPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testPayload.getNamespaces()));
//		testPayload.getNamespaces().clear();
//		testPayload.getNamespaces().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testPayload.getNamespaces()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Payload#getImports}.
	 */
	public void testPayloadsetImports() throws InvalidSPDXAnalysisException {
		Payload testPayload = builderForPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testPayload.getImports()));
//		testPayload.getImports().clear();
//		testPayload.getImports().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testPayload.getImports()));
		fail("Not yet implemented");
	}

/*
*/

}