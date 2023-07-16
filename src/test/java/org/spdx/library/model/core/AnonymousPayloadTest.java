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
import org.spdx.library.model.core.AnonymousPayload.AnonymousPayloadBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class AnonymousPayloadTest extends TestCase {

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
	
	public static AnonymousPayloadBuilder builderForAnonymousPayloadTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		AnonymousPayloadBuilder retval = new AnonymousPayloadBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.AnonymousPayload#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		AnonymousPayload testAnonymousPayload = builderForAnonymousPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testAnonymousPayload.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.AnonymousPayload#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		AnonymousPayload testAnonymousPayload = builderForAnonymousPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.AnonymousPayload", testAnonymousPayload.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.AnonymousPayload#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		AnonymousPayload testAnonymousPayload = builderForAnonymousPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("AnonymousPayload: "+TEST_OBJECT_URI, testAnonymousPayload.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.AnonymousPayload#Element(org.spdx.library.model.core.AnonymousPayload.AnonymousPayloadBuilder)}.
	 */
	public void testAnonymousPayloadAnonymousPayloadBuilder() throws InvalidSPDXAnalysisException {
		AnonymousPayload testAnonymousPayload = builderForAnonymousPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		AnonymousPayload testAnonymousPayload = builderForAnonymousPayloadTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		AnonymousPayload test2AnonymousPayload = builderForAnonymousPayloadTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testAnonymousPayload.equivalent(test2AnonymousPayload));
		assertTrue(test2AnonymousPayload.equivalent(testAnonymousPayload));
		// TODO change some parameters for negative tests
	}

/*
*/

}