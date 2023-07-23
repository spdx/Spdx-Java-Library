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
import org.spdx.library.model.security.VulnAssessmentRelationship.VulnAssessmentRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class VulnAssessmentRelationshipTest extends TestCase {

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
	
	public static VulnAssessmentRelationshipBuilder builderForVulnAssessmentRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		VulnAssessmentRelationshipBuilder retval = new VulnAssessmentRelationshipBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				.setAssessedElement(Element testElement)
				.setSuppliedBy(Element testElement)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.VulnAssessmentRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		VulnAssessmentRelationship testVulnAssessmentRelationship = builderForVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testVulnAssessmentRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VulnAssessmentRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		VulnAssessmentRelationship testVulnAssessmentRelationship = builderForVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Security.VulnAssessmentRelationship", testVulnAssessmentRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VulnAssessmentRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		VulnAssessmentRelationship testVulnAssessmentRelationship = builderForVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("VulnAssessmentRelationship: "+TEST_OBJECT_URI, testVulnAssessmentRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.security.VulnAssessmentRelationship#Element(org.spdx.library.model.security.VulnAssessmentRelationship.VulnAssessmentRelationshipBuilder)}.
	 */
	public void testVulnAssessmentRelationshipVulnAssessmentRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		VulnAssessmentRelationship testVulnAssessmentRelationship = builderForVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		VulnAssessmentRelationship test2VulnAssessmentRelationship = builderForVulnAssessmentRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testVulnAssessmentRelationship.equivalent(test2VulnAssessmentRelationship));
		assertTrue(test2VulnAssessmentRelationship.equivalent(testVulnAssessmentRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.VulnAssessmentRelationship#setAssessedElement}.
	 */
	public void testVulnAssessmentRelationshipsetAssessedElement() throws InvalidSPDXAnalysisException {
		VulnAssessmentRelationship testVulnAssessmentRelationship = builderForVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(Optional.of(TEST_VALUE), testVulnAssessmentRelationship.getAssessedElement());
//		testVulnAssessmentRelationship.setAssessedElement(NEW_TEST_VALUE);
//		assertEquals(Optional.of(NEW_TEST_VALUE), testVulnAssessmentRelationship.getAssessedElement());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.security.VulnAssessmentRelationship#setSuppliedBy}.
	 */
	public void testVulnAssessmentRelationshipsetSuppliedBy() throws InvalidSPDXAnalysisException {
		VulnAssessmentRelationship testVulnAssessmentRelationship = builderForVulnAssessmentRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(Optional.of(TEST_VALUE), testVulnAssessmentRelationship.getSuppliedBy());
//		testVulnAssessmentRelationship.setSuppliedBy(NEW_TEST_VALUE);
//		assertEquals(Optional.of(NEW_TEST_VALUE), testVulnAssessmentRelationship.getSuppliedBy());
		fail("Not yet implemented");
	}
}