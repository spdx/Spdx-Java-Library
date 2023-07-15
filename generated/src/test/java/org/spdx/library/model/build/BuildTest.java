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
 
package org.spdx.library.model.build;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.build.Build.BuildBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class BuildTest extends TestCase {

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
	
	public static BuildBuilder builderForBuildTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		BuildBuilder retval = new BuildBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setbuildEndTime("A string")
		.setbuildType("A string")
		.setbuildStartTime("A string")
		.setbuildId("A string")
		.getparameters.add(DictionaryEntry)
		.getconfigSourceDigest.add(Hash)
		.getenvironment.add(DictionaryEntry)
		.getconfigSourceEntrypoint.add("Test string")
		.getconfigSourceUri.add("Test string")
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testBuild.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Build.Build", testBuild.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.build.Build#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Build: "+TEST_OBJECT_URI, testBuild.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.build.Build#Element(org.spdx.library.model.build.Build.BuildBuilder)}.
	 */
	public void testBuildBuildBuilder() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Build test2Build = builderForBuildTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testBuild.equivalent(test2Build));
		assertTrue(test2Build.equivalent(testBuild));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#setBuildEndTime}.
	 */
	public void testBuildsetBuildEndTime() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testBuild.getBuildEndTime());
//		testBuild.setBuildEndTime(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testBuild.getBuildEndTime());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#setBuildType}.
	 */
	public void testBuildsetBuildType() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testBuild.getBuildType());
//		testBuild.setBuildType(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testBuild.getBuildType());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#setBuildStartTime}.
	 */
	public void testBuildsetBuildStartTime() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testBuild.getBuildStartTime());
//		testBuild.setBuildStartTime(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testBuild.getBuildStartTime());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#setBuildId}.
	 */
	public void testBuildsetBuildId() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testBuild.getBuildId());
//		testBuild.setBuildId(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testBuild.getBuildId());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getParameters}.
	 */
	public void testBuildsetParameters() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testBuild.getParameters()));
//		testBuild.getParameters().clear();
//		testBuild.getParameters().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testBuild.getParameters()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getConfigSourceDigest}.
	 */
	public void testBuildsetConfigSourceDigest() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testBuild.getConfigSourceDigest()));
//		testBuild.getConfigSourceDigest().clear();
//		testBuild.getConfigSourceDigest().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testBuild.getConfigSourceDigest()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getEnvironment}.
	 */
	public void testBuildsetEnvironment() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testBuild.getEnvironment()));
//		testBuild.getEnvironment().clear();
//		testBuild.getEnvironment().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testBuild.getEnvironment()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getConfigSourceEntrypoint}.
	 */
	public void testBuildgetConfigSourceEntrypoint() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testBuild.getConfigSourceEntrypoint()));
//		testBuild.getConfigSourceEntrypoint().clear();
//		testBuild.getConfigSourceEntrypoint().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testBuild.getConfigSourceEntrypoint()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getConfigSourceUri}.
	 */
	public void testBuildgetConfigSourceUri() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testBuild.getConfigSourceUri()));
//		testBuild.getConfigSourceUri().clear();
//		testBuild.getConfigSourceUri().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testBuild.getConfigSourceUri()));
		fail("Not yet implemented");
	}

/*
*/

}