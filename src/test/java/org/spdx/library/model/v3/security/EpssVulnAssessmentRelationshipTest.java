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
import org.spdx.library.model.v3.security.EpssVulnAssessmentRelationship.EpssVulnAssessmentRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class EpssVulnAssessmentRelationshipTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final Integer PROBABILITY_TEST_VALUE = 55;
	static final String SEVERITY_TEST_VALUE = "test severity";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static EpssVulnAssessmentRelationshipBuilder builderForEpssVulnAssessmentRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		EpssVulnAssessmentRelationshipBuilder retval = new EpssVulnAssessmentRelationshipBuilder(modelStore, objectUri, copyManager)
				.setProbability(PROBABILITY_TEST_VALUE)
				.setSeverity(SEVERITY_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.EpssVulnAssessmentRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		EpssVulnAssessmentRelationship testEpssVulnAssessmentRelationship = builderForEpssVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testEpssVulnAssessmentRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.EpssVulnAssessmentRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		EpssVulnAssessmentRelationship testEpssVulnAssessmentRelationship = builderForEpssVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Security.EpssVulnAssessmentRelationship", testEpssVulnAssessmentRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.EpssVulnAssessmentRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		EpssVulnAssessmentRelationship testEpssVulnAssessmentRelationship = builderForEpssVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("EpssVulnAssessmentRelationship: "+TEST_OBJECT_URI, testEpssVulnAssessmentRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.EpssVulnAssessmentRelationship#Element(org.spdx.library.model.v3.security.EpssVulnAssessmentRelationship.EpssVulnAssessmentRelationshipBuilder)}.
	 */
	public void testEpssVulnAssessmentRelationshipEpssVulnAssessmentRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForEpssVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		EpssVulnAssessmentRelationship testEpssVulnAssessmentRelationship = builderForEpssVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		EpssVulnAssessmentRelationship test2EpssVulnAssessmentRelationship = builderForEpssVulnAssessmentRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testEpssVulnAssessmentRelationship.equivalent(test2EpssVulnAssessmentRelationship));
		assertTrue(test2EpssVulnAssessmentRelationship.equivalent(testEpssVulnAssessmentRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.EpssVulnAssessmentRelationship#setProbability}.
	 */
	public void testEpssVulnAssessmentRelationshipsetProbability() throws InvalidSPDXAnalysisException {
		EpssVulnAssessmentRelationship testEpssVulnAssessmentRelationship = builderForEpssVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(PROBABILITY_TEST_VALUE, testEpssVulnAssessmentRelationship.getProbability());
		testEpssVulnAssessmentRelationship.setProbability(new Integer(653));
		assertEquals(new Integer(653), testEpssVulnAssessmentRelationship.getProbability());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.EpssVulnAssessmentRelationship#setSeverity}.
	 */
	public void testEpssVulnAssessmentRelationshipsetSeverity() throws InvalidSPDXAnalysisException {
		EpssVulnAssessmentRelationship testEpssVulnAssessmentRelationship = builderForEpssVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(SEVERITY_TEST_VALUE), testEpssVulnAssessmentRelationship.getSeverity());
		testEpssVulnAssessmentRelationship.setSeverity("new severity value");
		assertEquals(Optional.of("new severity value"), testEpssVulnAssessmentRelationship.getSeverity());
	}
}