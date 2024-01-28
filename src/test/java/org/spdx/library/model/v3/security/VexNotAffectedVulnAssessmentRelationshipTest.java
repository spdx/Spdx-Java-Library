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
import org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship.VexNotAffectedVulnAssessmentRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class VexNotAffectedVulnAssessmentRelationshipTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String IMPACT_STATEMENT_TIME_TEST_VALUE = "test impactStatementTime";
	static final String IMPACT_STATEMENT_TEST_VALUE = "test impactStatement";
	static final VexJustificationType JUSTIFICATION_TYPE_TEST_VALUE1 = VexJustificationType.values()[0];
	static final VexJustificationType JUSTIFICATION_TYPE_TEST_VALUE2 = VexJustificationType.values()[1];
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static VexNotAffectedVulnAssessmentRelationshipBuilder builderForVexNotAffectedVulnAssessmentRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		VexNotAffectedVulnAssessmentRelationshipBuilder retval = new VexNotAffectedVulnAssessmentRelationshipBuilder(modelStore, objectUri, copyManager)
				.setImpactStatementTime(IMPACT_STATEMENT_TIME_TEST_VALUE)
				.setImpactStatement(IMPACT_STATEMENT_TEST_VALUE)
				.setJustificationType(JUSTIFICATION_TYPE_TEST_VALUE1)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		VexNotAffectedVulnAssessmentRelationship testVexNotAffectedVulnAssessmentRelationship = builderForVexNotAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testVexNotAffectedVulnAssessmentRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		VexNotAffectedVulnAssessmentRelationship testVexNotAffectedVulnAssessmentRelationship = builderForVexNotAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Security.VexNotAffectedVulnAssessmentRelationship", testVexNotAffectedVulnAssessmentRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		VexNotAffectedVulnAssessmentRelationship testVexNotAffectedVulnAssessmentRelationship = builderForVexNotAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("VexNotAffectedVulnAssessmentRelationship: "+TEST_OBJECT_URI, testVexNotAffectedVulnAssessmentRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship#Element(org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship.VexNotAffectedVulnAssessmentRelationshipBuilder)}.
	 */
	public void testVexNotAffectedVulnAssessmentRelationshipVexNotAffectedVulnAssessmentRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForVexNotAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		VexNotAffectedVulnAssessmentRelationship testVexNotAffectedVulnAssessmentRelationship = builderForVexNotAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		VexNotAffectedVulnAssessmentRelationship test2VexNotAffectedVulnAssessmentRelationship = builderForVexNotAffectedVulnAssessmentRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testVexNotAffectedVulnAssessmentRelationship.equivalent(test2VexNotAffectedVulnAssessmentRelationship));
		assertTrue(test2VexNotAffectedVulnAssessmentRelationship.equivalent(testVexNotAffectedVulnAssessmentRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship#setJustificationType}.
	 */
	public void testVexNotAffectedVulnAssessmentRelationshipsetJustificationType() throws InvalidSPDXAnalysisException {
		VexNotAffectedVulnAssessmentRelationship testVexNotAffectedVulnAssessmentRelationship = builderForVexNotAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(JUSTIFICATION_TYPE_TEST_VALUE1), testVexNotAffectedVulnAssessmentRelationship.getJustificationType());
		testVexNotAffectedVulnAssessmentRelationship.setJustificationType(JUSTIFICATION_TYPE_TEST_VALUE2);
		assertEquals(Optional.of(JUSTIFICATION_TYPE_TEST_VALUE2), testVexNotAffectedVulnAssessmentRelationship.getJustificationType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship#setImpactStatementTime}.
	 */
	public void testVexNotAffectedVulnAssessmentRelationshipsetImpactStatementTime() throws InvalidSPDXAnalysisException {
		VexNotAffectedVulnAssessmentRelationship testVexNotAffectedVulnAssessmentRelationship = builderForVexNotAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(IMPACT_STATEMENT_TIME_TEST_VALUE), testVexNotAffectedVulnAssessmentRelationship.getImpactStatementTime());
		testVexNotAffectedVulnAssessmentRelationship.setImpactStatementTime("new impactStatementTime value");
		assertEquals(Optional.of("new impactStatementTime value"), testVexNotAffectedVulnAssessmentRelationship.getImpactStatementTime());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.security.VexNotAffectedVulnAssessmentRelationship#setImpactStatement}.
	 */
	public void testVexNotAffectedVulnAssessmentRelationshipsetImpactStatement() throws InvalidSPDXAnalysisException {
		VexNotAffectedVulnAssessmentRelationship testVexNotAffectedVulnAssessmentRelationship = builderForVexNotAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(IMPACT_STATEMENT_TEST_VALUE), testVexNotAffectedVulnAssessmentRelationship.getImpactStatement());
		testVexNotAffectedVulnAssessmentRelationship.setImpactStatement("new impactStatement value");
		assertEquals(Optional.of("new impactStatement value"), testVexNotAffectedVulnAssessmentRelationship.getImpactStatement());
	}
}