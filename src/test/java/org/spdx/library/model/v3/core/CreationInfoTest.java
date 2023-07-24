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
 
package org.spdx.library.model.v3.core;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.core.CreationInfo.CreationInfoBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class CreationInfoTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String DATA_LICENSE_TEST_VALUE = "test dataLicense";
	static final String COMMENT_TEST_VALUE = "test comment";
	static final ProfileIdentifierType PROFILE_TEST_VALUE1 = ProfileIdentifierType.values()[0];
	static final ProfileIdentifierType PROFILE_TEST_VALUE2 = ProfileIdentifierType.values()[1];
	static final List<ProfileIdentifierType> PROFILE_TEST_LIST1 = Arrays.asList(new ProfileIdentifierType[] { PROFILE_TEST_VALUE1, PROFILE_TEST_VALUE2 });
	static final List<ProfileIdentifierType> PROFILE_TEST_LIST2 = Arrays.asList(new ProfileIdentifierType[] { PROFILE_TEST_VALUE1 });
	
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
		CreationInfoBuilder retval = new CreationInfoBuilder(modelStore, objectUri, copyManager)
				.setDataLicense(DATA_LICENSE_TEST_VALUE)
				.setComment(COMMENT_TEST_VALUE)
				.addProfile(PROFILE_TEST_VALUE1)
				.addProfile(PROFILE_TEST_VALUE2)
				//TODO: Add in test values
				/********************
				.setCreated(new DateTime())
				.setSpecVersion(new SemVer())
				.addCreatedBy(Agent)
				.addCreatedUsing(Tool)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testCreationInfo.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.CreationInfo", testCreationInfo.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("CreationInfo: "+TEST_OBJECT_URI, testCreationInfo.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#Element(org.spdx.library.model.v3.core.CreationInfo.CreationInfoBuilder)}.
	 */
	public void testCreationInfoCreationInfoBuilder() throws InvalidSPDXAnalysisException {
		builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		CreationInfo test2CreationInfo = builderForCreationInfoTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testCreationInfo.equivalent(test2CreationInfo));
		assertTrue(test2CreationInfo.equivalent(testCreationInfo));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#setCreated}.
	 */
	public void testCreationInfosetCreated() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(Optional.of(TEST_VALUE), testCreationInfo.getCreated());
//		testCreationInfo.setCreated(NEW_TEST_VALUE);
//		assertEquals(Optional.of(NEW_TEST_VALUE), testCreationInfo.getCreated());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#setSpecVersion}.
	 */
	public void testCreationInfosetSpecVersion() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testCreationInfo.getSpecVersion());
//		testCreationInfo.setSpecVersion(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testCreationInfo.getSpecVersion());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#setDataLicense}.
	 */
	public void testCreationInfosetDataLicense() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(DATA_LICENSE_TEST_VALUE), testCreationInfo.getDataLicense());
		testCreationInfo.setDataLicense("new dataLicense value");
		assertEquals(Optional.of("new dataLicense value"), testCreationInfo.getDataLicense());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#setComment}.
	 */
	public void testCreationInfosetComment() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(COMMENT_TEST_VALUE), testCreationInfo.getComment());
		testCreationInfo.setComment("new comment value");
		assertEquals(Optional.of("new comment value"), testCreationInfo.getComment());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#getCreatedBy}.
	 */
	public void testCreationInfogetCreatedBys() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testCreationInfo.getCreatedBys())));
//		testCreationInfo.getCreatedBys().clear();
//		testCreationInfo.getCreatedBys().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testCreationInfo.getCreatedBys())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#getCreatedUsing}.
	 */
	public void testCreationInfogetCreatedUsings() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testCreationInfo.getCreatedUsings())));
//		testCreationInfo.getCreatedUsings().clear();
//		testCreationInfo.getCreatedUsings().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testCreationInfo.getCreatedUsings())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.CreationInfo#getProfile}.
	 */
	public void testCreationInfogetProfiles() throws InvalidSPDXAnalysisException {
		CreationInfo testCreationInfo = builderForCreationInfoTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(PROFILE_TEST_LIST1, new ArrayList<>(testCreationInfo.getProfiles())));
		testCreationInfo.getProfiles().clear();
		testCreationInfo.getProfiles().addAll(PROFILE_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(PROFILE_TEST_LIST2, new ArrayList<>(testCreationInfo.getProfiles())));
	}
}