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
 
package org.spdx.library.model.v3.security;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.security.VexVulnAssessmentRelationship.VexVulnAssessmentRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class VexVulnAssessmentRelationshipTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String STATUS_NOTES_TEST_VALUE = "test statusNotes";
	static final String VEX_VERSION_TEST_VALUE = "test vexVersion";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static VexVulnAssessmentRelationshipBuilder builderForVexVulnAssessmentRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		VexVulnAssessmentRelationshipBuilder retval = new VexVulnAssessmentRelationshipBuilder(modelStore, objectUri, copyManager)
				.setStatusNotes(STATUS_NOTES_TEST_VALUE)
				.setVexVersion(VEX_VERSION_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexVulnAssessmentRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		VexVulnAssessmentRelationship testVexVulnAssessmentRelationship = builderForVexVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testVexVulnAssessmentRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexVulnAssessmentRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		VexVulnAssessmentRelationship testVexVulnAssessmentRelationship = builderForVexVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Security.VexVulnAssessmentRelationship", testVexVulnAssessmentRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexVulnAssessmentRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		VexVulnAssessmentRelationship testVexVulnAssessmentRelationship = builderForVexVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("VexVulnAssessmentRelationship: "+TEST_OBJECT_URI, testVexVulnAssessmentRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexVulnAssessmentRelationship#Element(org.spdx.library.model.v3.security.VexVulnAssessmentRelationship.VexVulnAssessmentRelationshipBuilder)}.
	 */
	public void testVexVulnAssessmentRelationshipVexVulnAssessmentRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForVexVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		VexVulnAssessmentRelationship testVexVulnAssessmentRelationship = builderForVexVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		VexVulnAssessmentRelationship test2VexVulnAssessmentRelationship = builderForVexVulnAssessmentRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testVexVulnAssessmentRelationship.equivalent(test2VexVulnAssessmentRelationship));
		assertTrue(test2VexVulnAssessmentRelationship.equivalent(testVexVulnAssessmentRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexVulnAssessmentRelationship#setStatusNotes}.
	 */
	public void testVexVulnAssessmentRelationshipsetStatusNotes() throws InvalidSPDXAnalysisException {
		VexVulnAssessmentRelationship testVexVulnAssessmentRelationship = builderForVexVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(STATUS_NOTES_TEST_VALUE), testVexVulnAssessmentRelationship.getStatusNotes());
		testVexVulnAssessmentRelationship.setStatusNotes("new statusNotes value");
		assertEquals(Optional.of("new statusNotes value"), testVexVulnAssessmentRelationship.getStatusNotes());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexVulnAssessmentRelationship#setVexVersion}.
	 */
	public void testVexVulnAssessmentRelationshipsetVexVersion() throws InvalidSPDXAnalysisException {
		VexVulnAssessmentRelationship testVexVulnAssessmentRelationship = builderForVexVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(VEX_VERSION_TEST_VALUE), testVexVulnAssessmentRelationship.getVexVersion());
		testVexVulnAssessmentRelationship.setVexVersion("new vexVersion value");
		assertEquals(Optional.of("new vexVersion value"), testVexVulnAssessmentRelationship.getVexVersion());
	}
}