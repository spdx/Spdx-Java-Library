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
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.security.SsvcVulnAssessmentRelationship.SsvcVulnAssessmentRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class SsvcVulnAssessmentRelationshipTest extends TestCase {

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
	
	public static SsvcVulnAssessmentRelationshipBuilder builderForSsvcVulnAssessmentRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SsvcVulnAssessmentRelationshipBuilder retval = new SsvcVulnAssessmentRelationshipBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setdecisionType(SsvcDecisionType.ENUM)
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.SsvcVulnAssessmentRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SsvcVulnAssessmentRelationship testSsvcVulnAssessmentRelationship = builderForSsvcVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testSsvcVulnAssessmentRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.SsvcVulnAssessmentRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		SsvcVulnAssessmentRelationship testSsvcVulnAssessmentRelationship = builderForSsvcVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Security.SsvcVulnAssessmentRelationship", testSsvcVulnAssessmentRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.SsvcVulnAssessmentRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		SsvcVulnAssessmentRelationship testSsvcVulnAssessmentRelationship = builderForSsvcVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("SsvcVulnAssessmentRelationship: "+TEST_OBJECT_URI, testSsvcVulnAssessmentRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.SsvcVulnAssessmentRelationship#Element(org.spdx.library.model.security.SsvcVulnAssessmentRelationship.SsvcVulnAssessmentRelationshipBuilder)}.
	 */
	public void testSsvcVulnAssessmentRelationshipSsvcVulnAssessmentRelationshipBuilder() throws InvalidSPDXAnalysisException {
		SsvcVulnAssessmentRelationship testSsvcVulnAssessmentRelationship = builderForSsvcVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		SsvcVulnAssessmentRelationship testSsvcVulnAssessmentRelationship = builderForSsvcVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		SsvcVulnAssessmentRelationship test2SsvcVulnAssessmentRelationship = builderForSsvcVulnAssessmentRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testSsvcVulnAssessmentRelationship.equivalent(test2SsvcVulnAssessmentRelationship));
		assertTrue(test2SsvcVulnAssessmentRelationship.equivalent(testSsvcVulnAssessmentRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.SsvcVulnAssessmentRelationship#setDecisionType}.
	 */
	public void testSsvcVulnAssessmentRelationshipsetDecisionType() throws InvalidSPDXAnalysisException {
		SsvcVulnAssessmentRelationship testSsvcVulnAssessmentRelationship = builderForSsvcVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testSsvcVulnAssessmentRelationship.getDecisionType());
//		testSsvcVulnAssessmentRelationship.setDecisionType(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testSsvcVulnAssessmentRelationship.getDecisionType());
		fail("Not yet implemented");
	}

/*
*/

}