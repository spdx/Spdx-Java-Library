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
import org.spdx.library.model.v3.core.Artifact.ArtifactBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ArtifactTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String VALID_UNTIL_TIME_TEST_VALUE = "test validUntilTime";
	static final String BUILT_TIME_TEST_VALUE = "test builtTime";
	static final String RELEASE_TIME_TEST_VALUE = "test releaseTime";
	static final String STANDARD_TEST_VALUE1 = "test 1 standard";
	static final String STANDARD_TEST_VALUE2 = "test 2 standard";
	static final String STANDARD_TEST_VALUE3 = "test 3 standard";
	static final List<String> STANDARD_TEST_LIST1 = Arrays.asList(new String[] { STANDARD_TEST_VALUE1, STANDARD_TEST_VALUE2 });
	static final List<String> STANDARD_TEST_LIST2 = Arrays.asList(new String[] { STANDARD_TEST_VALUE3 });
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static ArtifactBuilder builderForArtifactTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ArtifactBuilder retval = new ArtifactBuilder(modelStore, objectUri, copyManager)
				.setValidUntilTime(VALID_UNTIL_TIME_TEST_VALUE)
				.setBuiltTime(BUILT_TIME_TEST_VALUE)
				.setReleaseTime(RELEASE_TIME_TEST_VALUE)
				.addStandard(STANDARD_TEST_VALUE1)
				.addStandard(STANDARD_TEST_VALUE2)
				//TODO: Add in test values
				/********************
				.setSuppliedBy(new Agent())
				.addOriginatedBy(Agent)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testArtifact.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Artifact", testArtifact.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Artifact: "+TEST_OBJECT_URI, testArtifact.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#Element(org.spdx.library.model.v3.core.Artifact.ArtifactBuilder)}.
	 */
	public void testArtifactArtifactBuilder() throws InvalidSPDXAnalysisException {
		builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Artifact test2Artifact = builderForArtifactTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testArtifact.equivalent(test2Artifact));
		assertTrue(test2Artifact.equivalent(testArtifact));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#setSuppliedBy}.
	 */
	public void testArtifactsetSuppliedBy() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(Optional.of(TEST_VALUE), testArtifact.getSuppliedBy());
//		testArtifact.setSuppliedBy(NEW_TEST_VALUE);
//		assertEquals(Optional.of(NEW_TEST_VALUE), testArtifact.getSuppliedBy());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#setValidUntilTime}.
	 */
	public void testArtifactsetValidUntilTime() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(VALID_UNTIL_TIME_TEST_VALUE), testArtifact.getValidUntilTime());
		testArtifact.setValidUntilTime("new validUntilTime value");
		assertEquals(Optional.of("new validUntilTime value"), testArtifact.getValidUntilTime());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#setBuiltTime}.
	 */
	public void testArtifactsetBuiltTime() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(BUILT_TIME_TEST_VALUE), testArtifact.getBuiltTime());
		testArtifact.setBuiltTime("new builtTime value");
		assertEquals(Optional.of("new builtTime value"), testArtifact.getBuiltTime());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#setReleaseTime}.
	 */
	public void testArtifactsetReleaseTime() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(RELEASE_TIME_TEST_VALUE), testArtifact.getReleaseTime());
		testArtifact.setReleaseTime("new releaseTime value");
		assertEquals(Optional.of("new releaseTime value"), testArtifact.getReleaseTime());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#getOriginatedBy}.
	 */
	public void testArtifactgetOriginatedBys() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testArtifact.getOriginatedBys())));
//		testArtifact.getOriginatedBys().clear();
//		testArtifact.getOriginatedBys().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testArtifact.getOriginatedBys())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Artifact#getStandards}.
	 */
	public void testArtifactgetStandards() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(STANDARD_TEST_LIST1, new ArrayList<>(testArtifact.getStandards())));
		testArtifact.getStandards().clear();
		testArtifact.getStandards().addAll(STANDARD_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(STANDARD_TEST_LIST2, new ArrayList<>(testArtifact.getStandards())));
	}
}