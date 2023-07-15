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
import org.spdx.library.model.core.CreationInfo.CreationInfoBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class CreationInfoTest extends TestCase {

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
	
	public static CreationInfoBuilder builderForCreationInfoTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		CreationInfoBuilder retval = new CreationInfoBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setcomment("A string")
		.getcreatedUsing.add(Tool)
		.getcreatedBy.add(Agent)
		.getcreated.add("Test string")
		.getdataLicense.add("Test string")
		.getspecVersion.add("Test string")
		.getprofile.add(ProfileIdentifierType.ENUM)
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testCreationInfo.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.CreationInfo", testCreationInfo.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("CreationInfo: "+TEST_OBJECT_URI, testCreationInfo.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#Element(org.spdx.library.model.core.CreationInfo.CreationInfoBuilder)}.
	 */
	public void testCreationInfoCreationInfoBuilder() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		CreationInfo test2CreationInfo = builderForCreationInfoTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testCreationInfo.equivalent(test2CreationInfo));
		assertTrue(test2CreationInfo.equivalent(testCreationInfo));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#setComment}.
	 */
	public void testCreationInfosetComment() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testCreationInfo.getComment());
//		testCreationInfo.setComment(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testCreationInfo.getComment());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#getCreatedUsing}.
	 */
	public void testCreationInfosetCreatedUsing() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testCreationInfo.getCreatedUsing()));
//		testCreationInfo.getCreatedUsing().clear();
//		testCreationInfo.getCreatedUsing().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testCreationInfo.getCreatedUsing()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#getCreatedBy}.
	 */
	public void testCreationInfosetCreatedBy() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testCreationInfo.getCreatedBy()));
//		testCreationInfo.getCreatedBy().clear();
//		testCreationInfo.getCreatedBy().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testCreationInfo.getCreatedBy()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#getCreated}.
	 */
	public void testCreationInfogetCreated() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testCreationInfo.getCreated()));
//		testCreationInfo.getCreated().clear();
//		testCreationInfo.getCreated().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testCreationInfo.getCreated()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#getDataLicense}.
	 */
	public void testCreationInfogetDataLicense() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testCreationInfo.getDataLicense()));
//		testCreationInfo.getDataLicense().clear();
//		testCreationInfo.getDataLicense().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testCreationInfo.getDataLicense()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#getSpecVersion}.
	 */
	public void testCreationInfogetSpecVersion() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testCreationInfo.getSpecVersion()));
//		testCreationInfo.getSpecVersion().clear();
//		testCreationInfo.getSpecVersion().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testCreationInfo.getSpecVersion()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.CreationInfo#getProfile}.
	 */
	public void testCreationInfogetProfile() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testCreationInfo.getProfile()));
//		testCreationInfo.getProfile().clear();
//		testCreationInfo.getProfile().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testCreationInfo.getProfile()));
		fail("Not yet implemented");
	}

/*
*/

}