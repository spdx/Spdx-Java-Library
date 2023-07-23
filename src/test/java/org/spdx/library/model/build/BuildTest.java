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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.build.Build.BuildBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class BuildTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String BUILD_END_TIME_TEST_VALUE = "test buildEndTime";
	static final String BUILD_TYPE_TEST_VALUE = "test buildType";
	static final String BUILD_START_TIME_TEST_VALUE = "test buildStartTime";
	static final String BUILD_ID_TEST_VALUE = "test buildId";
	static final String CONFIG_SOURCE_ENTRYPOINT_TEST_VALUE1 = "test 1 configSourceEntrypoint";
	static final String CONFIG_SOURCE_ENTRYPOINT_TEST_VALUE2 = "test 2 configSourceEntrypoint";
	static final String CONFIG_SOURCE_ENTRYPOINT_TEST_VALUE3 = "test 3 configSourceEntrypoint";
	static final List<String> CONFIG_SOURCE_ENTRYPOINT_TEST_LIST1 = Arrays.asList(new String[] { CONFIG_SOURCE_ENTRYPOINT_TEST_VALUE1, CONFIG_SOURCE_ENTRYPOINT_TEST_VALUE2 });
	static final List<String> CONFIG_SOURCE_ENTRYPOINT_TEST_LIST2 = Arrays.asList(new String[] { CONFIG_SOURCE_ENTRYPOINT_TEST_VALUE3 });
	static final String CONFIG_SOURCE_URI_TEST_VALUE1 = "test 1 configSourceUri";
	static final String CONFIG_SOURCE_URI_TEST_VALUE2 = "test 2 configSourceUri";
	static final String CONFIG_SOURCE_URI_TEST_VALUE3 = "test 3 configSourceUri";
	static final List<String> CONFIG_SOURCE_URI_TEST_LIST1 = Arrays.asList(new String[] { CONFIG_SOURCE_URI_TEST_VALUE1, CONFIG_SOURCE_URI_TEST_VALUE2 });
	static final List<String> CONFIG_SOURCE_URI_TEST_LIST2 = Arrays.asList(new String[] { CONFIG_SOURCE_URI_TEST_VALUE3 });
	
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
		BuildBuilder retval = new BuildBuilder(modelStore, objectUri, copyManager)
				.setBuildEndTime(BUILD_END_TIME_TEST_VALUE)
				.setBuildType(BUILD_TYPE_TEST_VALUE)
				.setBuildStartTime(BUILD_START_TIME_TEST_VALUE)
				.setBuildId(BUILD_ID_TEST_VALUE)
				.addConfigSourceEntrypoint(CONFIG_SOURCE_ENTRYPOINT_TEST_VALUE1)
				.addConfigSourceEntrypoint(CONFIG_SOURCE_ENTRYPOINT_TEST_VALUE2)
				.addConfigSourceUri(CONFIG_SOURCE_URI_TEST_VALUE1)
				.addConfigSourceUri(CONFIG_SOURCE_URI_TEST_VALUE2)
				//TODO: Add in test values
				/********************
				.addParameters(DictionaryEntry)
				.addConfigSourceDigest(Hash)
				.addEnvironment(DictionaryEntry)
				***************/
				;
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
		builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
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
		assertEquals(Optional.of(BUILD_END_TIME_TEST_VALUE), testBuild.getBuildEndTime());
		testBuild.setBuildEndTime("new buildEndTime value");
		assertEquals(Optional.of("new buildEndTime value"), testBuild.getBuildEndTime());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#setBuildType}.
	 */
	public void testBuildsetBuildType() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(BUILD_TYPE_TEST_VALUE, testBuild.getBuildType());
		testBuild.setBuildType("new buildType value");
		assertEquals("new buildType value", testBuild.getBuildType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#setBuildStartTime}.
	 */
	public void testBuildsetBuildStartTime() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(BUILD_START_TIME_TEST_VALUE), testBuild.getBuildStartTime());
		testBuild.setBuildStartTime("new buildStartTime value");
		assertEquals(Optional.of("new buildStartTime value"), testBuild.getBuildStartTime());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#setBuildId}.
	 */
	public void testBuildsetBuildId() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(BUILD_ID_TEST_VALUE), testBuild.getBuildId());
		testBuild.setBuildId("new buildId value");
		assertEquals(Optional.of("new buildId value"), testBuild.getBuildId());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getParameters}.
	 */
	public void testBuildgetParameterss() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testBuild.getParameterss())));
//		testBuild.getParameterss().clear();
//		testBuild.getParameterss().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testBuild.getParameterss())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getConfigSourceDigest}.
	 */
	public void testBuildgetConfigSourceDigests() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testBuild.getConfigSourceDigests())));
//		testBuild.getConfigSourceDigests().clear();
//		testBuild.getConfigSourceDigests().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testBuild.getConfigSourceDigests())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getEnvironment}.
	 */
	public void testBuildgetEnvironments() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testBuild.getEnvironments())));
//		testBuild.getEnvironments().clear();
//		testBuild.getEnvironments().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testBuild.getEnvironments())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getConfigSourceEntrypoints}.
	 */
	public void testBuildgetConfigSourceEntrypoints() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(CONFIG_SOURCE_ENTRYPOINT_TEST_LIST1, new ArrayList<>(testBuild.getConfigSourceEntrypoints())));
		testBuild.getConfigSourceEntrypoints().clear();
		testBuild.getConfigSourceEntrypoints().addAll(CONFIG_SOURCE_ENTRYPOINT_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(CONFIG_SOURCE_ENTRYPOINT_TEST_LIST2, new ArrayList<>(testBuild.getConfigSourceEntrypoints())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.build.Build#getConfigSourceUris}.
	 */
	public void testBuildgetConfigSourceUris() throws InvalidSPDXAnalysisException {
		Build testBuild = builderForBuildTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(CONFIG_SOURCE_URI_TEST_LIST1, new ArrayList<>(testBuild.getConfigSourceUris())));
		testBuild.getConfigSourceUris().clear();
		testBuild.getConfigSourceUris().addAll(CONFIG_SOURCE_URI_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(CONFIG_SOURCE_URI_TEST_LIST2, new ArrayList<>(testBuild.getConfigSourceUris())));
	}
}