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
 
package org.spdx.library.model.v3.expandedlicensing;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet.DisjunctiveLicenseSetBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class DisjunctiveLicenseSetTest extends TestCase {

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
	
	public static DisjunctiveLicenseSetBuilder builderForDisjunctiveLicenseSetTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		DisjunctiveLicenseSetBuilder retval = new DisjunctiveLicenseSetBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				.addMember(AnyLicenseInfo)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		DisjunctiveLicenseSet testDisjunctiveLicenseSet = builderForDisjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testDisjunctiveLicenseSet.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		DisjunctiveLicenseSet testDisjunctiveLicenseSet = builderForDisjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExpandedLicensing.DisjunctiveLicenseSet", testDisjunctiveLicenseSet.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		DisjunctiveLicenseSet testDisjunctiveLicenseSet = builderForDisjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("DisjunctiveLicenseSet: "+TEST_OBJECT_URI, testDisjunctiveLicenseSet.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet#Element(org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet.DisjunctiveLicenseSetBuilder)}.
	 */
	public void testDisjunctiveLicenseSetDisjunctiveLicenseSetBuilder() throws InvalidSPDXAnalysisException {
		builderForDisjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		DisjunctiveLicenseSet testDisjunctiveLicenseSet = builderForDisjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		DisjunctiveLicenseSet test2DisjunctiveLicenseSet = builderForDisjunctiveLicenseSetTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testDisjunctiveLicenseSet.equivalent(test2DisjunctiveLicenseSet));
		assertTrue(test2DisjunctiveLicenseSet.equivalent(testDisjunctiveLicenseSet));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet#getMember}.
	 */
	public void testDisjunctiveLicenseSetgetMembers() throws InvalidSPDXAnalysisException {
		DisjunctiveLicenseSet testDisjunctiveLicenseSet = builderForDisjunctiveLicenseSetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testDisjunctiveLicenseSet.getMembers())));
//		testDisjunctiveLicenseSet.getMembers().clear();
//		testDisjunctiveLicenseSet.getMembers().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testDisjunctiveLicenseSet.getMembers())));
		fail("Not yet implemented");
	}
}