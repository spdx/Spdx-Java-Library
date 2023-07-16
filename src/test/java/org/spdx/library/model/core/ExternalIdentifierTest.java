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
import org.spdx.library.model.core.ExternalIdentifier.ExternalIdentifierBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ExternalIdentifierTest extends TestCase {

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
	
	public static ExternalIdentifierBuilder builderForExternalIdentifierTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ExternalIdentifierBuilder retval = new ExternalIdentifierBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setexternalIdentifierType(ExternalIdentifierType.ENUM)
		.setissuingAuthority("A string")
		.setidentifier("A string")
		.setcomment("A string")
		.getidentifierLocator.add("Test string")
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testExternalIdentifier.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.ExternalIdentifier", testExternalIdentifier.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExternalIdentifier: "+TEST_OBJECT_URI, testExternalIdentifier.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#Element(org.spdx.library.model.core.ExternalIdentifier.ExternalIdentifierBuilder)}.
	 */
	public void testExternalIdentifierExternalIdentifierBuilder() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ExternalIdentifier test2ExternalIdentifier = builderForExternalIdentifierTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testExternalIdentifier.equivalent(test2ExternalIdentifier));
		assertTrue(test2ExternalIdentifier.equivalent(testExternalIdentifier));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#setExternalIdentifierType}.
	 */
	public void testExternalIdentifiersetExternalIdentifierType() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testExternalIdentifier.getExternalIdentifierType());
//		testExternalIdentifier.setExternalIdentifierType(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testExternalIdentifier.getExternalIdentifierType());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#setIssuingAuthority}.
	 */
	public void testExternalIdentifiersetIssuingAuthority() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testExternalIdentifier.getIssuingAuthority());
//		testExternalIdentifier.setIssuingAuthority(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testExternalIdentifier.getIssuingAuthority());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#setIdentifier}.
	 */
	public void testExternalIdentifiersetIdentifier() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testExternalIdentifier.getIdentifier());
//		testExternalIdentifier.setIdentifier(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testExternalIdentifier.getIdentifier());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#setComment}.
	 */
	public void testExternalIdentifiersetComment() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testExternalIdentifier.getComment());
//		testExternalIdentifier.setComment(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testExternalIdentifier.getComment());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.ExternalIdentifier#getIdentifierLocator}.
	 */
	public void testExternalIdentifiergetIdentifierLocator() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testExternalIdentifier.getIdentifierLocator()));
//		testExternalIdentifier.getIdentifierLocator().clear();
//		testExternalIdentifier.getIdentifierLocator().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testExternalIdentifier.getIdentifierLocator()));
		fail("Not yet implemented");
	}

/*
*/

}