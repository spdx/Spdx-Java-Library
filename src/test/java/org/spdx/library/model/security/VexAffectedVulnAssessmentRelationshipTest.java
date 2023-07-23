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
import org.spdx.library.model.security.VexAffectedVulnAssessmentRelationship.VexAffectedVulnAssessmentRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class VexAffectedVulnAssessmentRelationshipTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String ACTION_STATEMENT_TEST_VALUE = "test actionStatement";
	static final String ACTION_STATEMENT_TIME_TEST_VALUE1 = "test 1 actionStatementTime";
	static final String ACTION_STATEMENT_TIME_TEST_VALUE2 = "test 2 actionStatementTime";
	static final String ACTION_STATEMENT_TIME_TEST_VALUE3 = "test 3 actionStatementTime";
	static final List<String> ACTION_STATEMENT_TIME_TEST_LIST1 = Arrays.asList(new String[] { ACTION_STATEMENT_TIME_TEST_VALUE1, ACTION_STATEMENT_TIME_TEST_VALUE2 });
	static final List<String> ACTION_STATEMENT_TIME_TEST_LIST2 = Arrays.asList(new String[] { ACTION_STATEMENT_TIME_TEST_VALUE3 });
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static VexAffectedVulnAssessmentRelationshipBuilder builderForVexAffectedVulnAssessmentRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		VexAffectedVulnAssessmentRelationshipBuilder retval = new VexAffectedVulnAssessmentRelationshipBuilder(modelStore, objectUri, copyManager)
				.setActionStatement(ACTION_STATEMENT_TEST_VALUE)
				.addActionStatementTime(ACTION_STATEMENT_TIME_TEST_VALUE1)
				.addActionStatementTime(ACTION_STATEMENT_TIME_TEST_VALUE2)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.VexAffectedVulnAssessmentRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		VexAffectedVulnAssessmentRelationship testVexAffectedVulnAssessmentRelationship = builderForVexAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testVexAffectedVulnAssessmentRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VexAffectedVulnAssessmentRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		VexAffectedVulnAssessmentRelationship testVexAffectedVulnAssessmentRelationship = builderForVexAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Security.VexAffectedVulnAssessmentRelationship", testVexAffectedVulnAssessmentRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VexAffectedVulnAssessmentRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		VexAffectedVulnAssessmentRelationship testVexAffectedVulnAssessmentRelationship = builderForVexAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("VexAffectedVulnAssessmentRelationship: "+TEST_OBJECT_URI, testVexAffectedVulnAssessmentRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VexAffectedVulnAssessmentRelationship#Element(org.spdx.library.model.security.VexAffectedVulnAssessmentRelationship.VexAffectedVulnAssessmentRelationshipBuilder)}.
	 */
	public void testVexAffectedVulnAssessmentRelationshipVexAffectedVulnAssessmentRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForVexAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		VexAffectedVulnAssessmentRelationship testVexAffectedVulnAssessmentRelationship = builderForVexAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		VexAffectedVulnAssessmentRelationship test2VexAffectedVulnAssessmentRelationship = builderForVexAffectedVulnAssessmentRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testVexAffectedVulnAssessmentRelationship.equivalent(test2VexAffectedVulnAssessmentRelationship));
		assertTrue(test2VexAffectedVulnAssessmentRelationship.equivalent(testVexAffectedVulnAssessmentRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.VexAffectedVulnAssessmentRelationship#setActionStatement}.
	 */
	public void testVexAffectedVulnAssessmentRelationshipsetActionStatement() throws InvalidSPDXAnalysisException {
		VexAffectedVulnAssessmentRelationship testVexAffectedVulnAssessmentRelationship = builderForVexAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(ACTION_STATEMENT_TEST_VALUE), testVexAffectedVulnAssessmentRelationship.getActionStatement());
		testVexAffectedVulnAssessmentRelationship.setActionStatement("new actionStatement value");
		assertEquals(Optional.of("new actionStatement value"), testVexAffectedVulnAssessmentRelationship.getActionStatement());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.VexAffectedVulnAssessmentRelationship#getActionStatementTimes}.
	 */
	public void testVexAffectedVulnAssessmentRelationshipgetActionStatementTimes() throws InvalidSPDXAnalysisException {
		VexAffectedVulnAssessmentRelationship testVexAffectedVulnAssessmentRelationship = builderForVexAffectedVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(ACTION_STATEMENT_TIME_TEST_LIST1, new ArrayList<>(testVexAffectedVulnAssessmentRelationship.getActionStatementTimes())));
		testVexAffectedVulnAssessmentRelationship.getActionStatementTimes().clear();
		testVexAffectedVulnAssessmentRelationship.getActionStatementTimes().addAll(ACTION_STATEMENT_TIME_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(ACTION_STATEMENT_TIME_TEST_LIST2, new ArrayList<>(testVexAffectedVulnAssessmentRelationship.getActionStatementTimes())));
	}
}