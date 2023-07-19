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
 
package org.spdx.library.model.software;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.software.SoftwareDependencyRelationship.SoftwareDependencyRelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class SoftwareDependencyRelationshipTest extends TestCase {

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
	
	public static SoftwareDependencyRelationshipBuilder builderForSoftwareDependencyRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SoftwareDependencyRelationshipBuilder retval = new SoftwareDependencyRelationshipBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				.setSoftwareLinkage(SoftwareDependencyLinkType.ENUM)
				.setConditionality(DependencyConditionalityType.ENUM)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareDependencyRelationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SoftwareDependencyRelationship testSoftwareDependencyRelationship = builderForSoftwareDependencyRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testSoftwareDependencyRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareDependencyRelationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		SoftwareDependencyRelationship testSoftwareDependencyRelationship = builderForSoftwareDependencyRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Software.SoftwareDependencyRelationship", testSoftwareDependencyRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareDependencyRelationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		SoftwareDependencyRelationship testSoftwareDependencyRelationship = builderForSoftwareDependencyRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("SoftwareDependencyRelationship: "+TEST_OBJECT_URI, testSoftwareDependencyRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareDependencyRelationship#Element(org.spdx.library.model.software.SoftwareDependencyRelationship.SoftwareDependencyRelationshipBuilder)}.
	 */
	public void testSoftwareDependencyRelationshipSoftwareDependencyRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForSoftwareDependencyRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		SoftwareDependencyRelationship testSoftwareDependencyRelationship = builderForSoftwareDependencyRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		SoftwareDependencyRelationship test2SoftwareDependencyRelationship = builderForSoftwareDependencyRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testSoftwareDependencyRelationship.equivalent(test2SoftwareDependencyRelationship));
		assertTrue(test2SoftwareDependencyRelationship.equivalent(testSoftwareDependencyRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareDependencyRelationship#setSoftwareLinkage}.
	 */
	public void testSoftwareDependencyRelationshipsetSoftwareLinkage() throws InvalidSPDXAnalysisException {
		SoftwareDependencyRelationship testSoftwareDependencyRelationship = builderForSoftwareDependencyRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testSoftwareDependencyRelationship.getSoftwareLinkage());
//		testSoftwareDependencyRelationship.setSoftwareLinkage(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testSoftwareDependencyRelationship.getSoftwareLinkage());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareDependencyRelationship#setConditionality}.
	 */
	public void testSoftwareDependencyRelationshipsetConditionality() throws InvalidSPDXAnalysisException {
		SoftwareDependencyRelationship testSoftwareDependencyRelationship = builderForSoftwareDependencyRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testSoftwareDependencyRelationship.getConditionality());
//		testSoftwareDependencyRelationship.setConditionality(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testSoftwareDependencyRelationship.getConditionality());
		fail("Not yet implemented");
	}
}