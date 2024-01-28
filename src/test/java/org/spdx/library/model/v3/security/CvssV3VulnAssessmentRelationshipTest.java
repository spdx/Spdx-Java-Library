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
 
package org.spdx.library.model.v3.security;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship.CvssV3VulnAssessmentRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class CvssV3VulnAssessmentRelationshipTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final Integer SCORE_TEST_VALUE = 55;
	static final String VECTOR_TEST_VALUE = "test vector";
	static final String SEVERITY_TEST_VALUE = "test severity";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static CvssV3VulnAssessmentRelationshipBuilder builderForCvssV3VulnAssessmentRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		CvssV3VulnAssessmentRelationshipBuilder retval = new CvssV3VulnAssessmentRelationshipBuilder(modelStore, objectUri, copyManager)
				.setScore(SCORE_TEST_VALUE)
				.setVector(VECTOR_TEST_VALUE)
				.setSeverity(SEVERITY_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		CvssV3VulnAssessmentRelationship testCvssV3VulnAssessmentRelationship = builderForCvssV3VulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testCvssV3VulnAssessmentRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		CvssV3VulnAssessmentRelationship testCvssV3VulnAssessmentRelationship = builderForCvssV3VulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Security.CvssV3VulnAssessmentRelationship", testCvssV3VulnAssessmentRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		CvssV3VulnAssessmentRelationship testCvssV3VulnAssessmentRelationship = builderForCvssV3VulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("CvssV3VulnAssessmentRelationship: "+TEST_OBJECT_URI, testCvssV3VulnAssessmentRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship#Element(org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship.CvssV3VulnAssessmentRelationshipBuilder)}.
	 */
	public void testCvssV3VulnAssessmentRelationshipCvssV3VulnAssessmentRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForCvssV3VulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		CvssV3VulnAssessmentRelationship testCvssV3VulnAssessmentRelationship = builderForCvssV3VulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		CvssV3VulnAssessmentRelationship test2CvssV3VulnAssessmentRelationship = builderForCvssV3VulnAssessmentRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testCvssV3VulnAssessmentRelationship.equivalent(test2CvssV3VulnAssessmentRelationship));
		assertTrue(test2CvssV3VulnAssessmentRelationship.equivalent(testCvssV3VulnAssessmentRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship#setScore}.
	 */
	public void testCvssV3VulnAssessmentRelationshipsetScore() throws InvalidSPDXAnalysisException {
		CvssV3VulnAssessmentRelationship testCvssV3VulnAssessmentRelationship = builderForCvssV3VulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(SCORE_TEST_VALUE, testCvssV3VulnAssessmentRelationship.getScore());
		testCvssV3VulnAssessmentRelationship.setScore(new Integer(653));
		assertEquals(new Integer(653), testCvssV3VulnAssessmentRelationship.getScore());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship#setVector}.
	 */
	public void testCvssV3VulnAssessmentRelationshipsetVector() throws InvalidSPDXAnalysisException {
		CvssV3VulnAssessmentRelationship testCvssV3VulnAssessmentRelationship = builderForCvssV3VulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(VECTOR_TEST_VALUE), testCvssV3VulnAssessmentRelationship.getVector());
		testCvssV3VulnAssessmentRelationship.setVector("new vector value");
		assertEquals(Optional.of("new vector value"), testCvssV3VulnAssessmentRelationship.getVector());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.CvssV3VulnAssessmentRelationship#setSeverity}.
	 */
	public void testCvssV3VulnAssessmentRelationshipsetSeverity() throws InvalidSPDXAnalysisException {
		CvssV3VulnAssessmentRelationship testCvssV3VulnAssessmentRelationship = builderForCvssV3VulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(SEVERITY_TEST_VALUE), testCvssV3VulnAssessmentRelationship.getSeverity());
		testCvssV3VulnAssessmentRelationship.setSeverity("new severity value");
		assertEquals(Optional.of("new severity value"), testCvssV3VulnAssessmentRelationship.getSeverity());
	}
}