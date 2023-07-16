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
import org.spdx.library.model.core.Artifact.ArtifactBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ArtifactTest extends TestCase {

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
	
	public static ArtifactBuilder builderForArtifactTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ArtifactBuilder retval = new ArtifactBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setreleaseTime("A string")
		.setvalidUntilTime("A string")
		.setbuiltTime("A string")
		.getoriginatedBy.add(Agent)
		.getsuppliedBy.add(Agent)
		.getstandard.add("Test string")
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testArtifact.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Artifact", testArtifact.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Artifact: "+TEST_OBJECT_URI, testArtifact.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#Element(org.spdx.library.model.core.Artifact.ArtifactBuilder)}.
	 */
	public void testArtifactArtifactBuilder() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Artifact test2Artifact = builderForArtifactTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testArtifact.equivalent(test2Artifact));
		assertTrue(test2Artifact.equivalent(testArtifact));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#setReleaseTime}.
	 */
	public void testArtifactsetReleaseTime() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testArtifact.getReleaseTime());
//		testArtifact.setReleaseTime(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testArtifact.getReleaseTime());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#setValidUntilTime}.
	 */
	public void testArtifactsetValidUntilTime() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testArtifact.getValidUntilTime());
//		testArtifact.setValidUntilTime(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testArtifact.getValidUntilTime());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#setBuiltTime}.
	 */
	public void testArtifactsetBuiltTime() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testArtifact.getBuiltTime());
//		testArtifact.setBuiltTime(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testArtifact.getBuiltTime());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#getOriginatedBy}.
	 */
	public void testArtifactsetOriginatedBy() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testArtifact.getOriginatedBy()));
//		testArtifact.getOriginatedBy().clear();
//		testArtifact.getOriginatedBy().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testArtifact.getOriginatedBy()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#getSuppliedBy}.
	 */
	public void testArtifactsetSuppliedBy() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testArtifact.getSuppliedBy()));
//		testArtifact.getSuppliedBy().clear();
//		testArtifact.getSuppliedBy().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testArtifact.getSuppliedBy()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.Artifact#getStandard}.
	 */
	public void testArtifactgetStandard() throws InvalidSPDXAnalysisException {
		Artifact testArtifact = builderForArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testArtifact.getStandard()));
//		testArtifact.getStandard().clear();
//		testArtifact.getStandard().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testArtifact.getStandard()));
		fail("Not yet implemented");
	}

/*
*/

}