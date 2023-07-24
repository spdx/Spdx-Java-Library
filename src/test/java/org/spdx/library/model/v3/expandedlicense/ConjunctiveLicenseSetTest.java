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
 
package org.spdx.library.model.v3.expandedlicense;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.expandedlicense.ConjunctiveLicenseSet.ConjunctiveLicenseSetBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ConjunctiveLicenseSetTest extends TestCase {

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
	
	public static ConjunctiveLicenseSetBuilder builderForConjunctiveLicenseSetTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ConjunctiveLicenseSetBuilder retval = new ConjunctiveLicenseSetBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				.addMember(AnyLicenseInfo)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicense.ConjunctiveLicenseSet#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ConjunctiveLicenseSet testConjunctiveLicenseSet = builderForConjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testConjunctiveLicenseSet.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicense.ConjunctiveLicenseSet#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ConjunctiveLicenseSet testConjunctiveLicenseSet = builderForConjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExpandedLicense.ConjunctiveLicenseSet", testConjunctiveLicenseSet.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicense.ConjunctiveLicenseSet#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ConjunctiveLicenseSet testConjunctiveLicenseSet = builderForConjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ConjunctiveLicenseSet: "+TEST_OBJECT_URI, testConjunctiveLicenseSet.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicense.ConjunctiveLicenseSet#Element(org.spdx.library.model.v3.expandedlicense.ConjunctiveLicenseSet.ConjunctiveLicenseSetBuilder)}.
	 */
	public void testConjunctiveLicenseSetConjunctiveLicenseSetBuilder() throws InvalidSPDXAnalysisException {
		builderForConjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ConjunctiveLicenseSet testConjunctiveLicenseSet = builderForConjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ConjunctiveLicenseSet test2ConjunctiveLicenseSet = builderForConjunctiveLicenseSetTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testConjunctiveLicenseSet.equivalent(test2ConjunctiveLicenseSet));
		assertTrue(test2ConjunctiveLicenseSet.equivalent(testConjunctiveLicenseSet));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicense.ConjunctiveLicenseSet#getMember}.
	 */
	public void testConjunctiveLicenseSetgetMembers() throws InvalidSPDXAnalysisException {
		ConjunctiveLicenseSet testConjunctiveLicenseSet = builderForConjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testConjunctiveLicenseSet.getMembers())));
//		testConjunctiveLicenseSet.getMembers().clear();
//		testConjunctiveLicenseSet.getMembers().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testConjunctiveLicenseSet.getMembers())));
		fail("Not yet implemented");
	}
}