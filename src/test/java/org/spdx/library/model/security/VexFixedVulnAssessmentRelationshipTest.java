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
 
package org.spdx.library.model.security;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.security.VexFixedVulnAssessmentRelationship.VexFixedVulnAssessmentRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class VexFixedVulnAssessmentRelationshipTest extends TestCase {

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
	
	public static VexFixedVulnAssessmentRelationshipBuilder builderForVexFixedVulnAssessmentRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		VexFixedVulnAssessmentRelationshipBuilder retval = new VexFixedVulnAssessmentRelationshipBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.VexFixedVulnAssessmentRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		VexFixedVulnAssessmentRelationship testVexFixedVulnAssessmentRelationship = builderForVexFixedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testVexFixedVulnAssessmentRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VexFixedVulnAssessmentRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		VexFixedVulnAssessmentRelationship testVexFixedVulnAssessmentRelationship = builderForVexFixedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Security.VexFixedVulnAssessmentRelationship", testVexFixedVulnAssessmentRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VexFixedVulnAssessmentRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		VexFixedVulnAssessmentRelationship testVexFixedVulnAssessmentRelationship = builderForVexFixedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("VexFixedVulnAssessmentRelationship: "+TEST_OBJECT_URI, testVexFixedVulnAssessmentRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VexFixedVulnAssessmentRelationship#Element(org.spdx.library.model.security.VexFixedVulnAssessmentRelationship.VexFixedVulnAssessmentRelationshipBuilder)}.
	 */
	public void testVexFixedVulnAssessmentRelationshipVexFixedVulnAssessmentRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForVexFixedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		VexFixedVulnAssessmentRelationship testVexFixedVulnAssessmentRelationship = builderForVexFixedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		VexFixedVulnAssessmentRelationship test2VexFixedVulnAssessmentRelationship = builderForVexFixedVulnAssessmentRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testVexFixedVulnAssessmentRelationship.equivalent(test2VexFixedVulnAssessmentRelationship));
		assertTrue(test2VexFixedVulnAssessmentRelationship.equivalent(testVexFixedVulnAssessmentRelationship));
		// TODO change some parameters for negative tests
	}
}