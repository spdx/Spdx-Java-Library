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
 
package org.spdx.library.model.v3.licensing;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.licensing.WithAdditionOperator.WithAdditionOperatorBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class WithAdditionOperatorTest extends TestCase {

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
	
	public static WithAdditionOperatorBuilder builderForWithAdditionOperatorTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		WithAdditionOperatorBuilder retval = new WithAdditionOperatorBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				.setSubjectAddition(new LicenseAddition())
				.setSubjectLicense(new ExtendableLicense())
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.licensing.WithAdditionOperator#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		WithAdditionOperator testWithAdditionOperator = builderForWithAdditionOperatorTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testWithAdditionOperator.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.licensing.WithAdditionOperator#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		WithAdditionOperator testWithAdditionOperator = builderForWithAdditionOperatorTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Licensing.WithAdditionOperator", testWithAdditionOperator.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.licensing.WithAdditionOperator#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		WithAdditionOperator testWithAdditionOperator = builderForWithAdditionOperatorTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("WithAdditionOperator: "+TEST_OBJECT_URI, testWithAdditionOperator.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.licensing.WithAdditionOperator#Element(org.spdx.library.model.v3.licensing.WithAdditionOperator.WithAdditionOperatorBuilder)}.
	 */
	public void testWithAdditionOperatorWithAdditionOperatorBuilder() throws InvalidSPDXAnalysisException {
		builderForWithAdditionOperatorTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		WithAdditionOperator testWithAdditionOperator = builderForWithAdditionOperatorTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		WithAdditionOperator test2WithAdditionOperator = builderForWithAdditionOperatorTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testWithAdditionOperator.equivalent(test2WithAdditionOperator));
		assertTrue(test2WithAdditionOperator.equivalent(testWithAdditionOperator));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.licensing.WithAdditionOperator#setSubjectAddition}.
	 */
	public void testWithAdditionOperatorsetSubjectAddition() throws InvalidSPDXAnalysisException {
		WithAdditionOperator testWithAdditionOperator = builderForWithAdditionOperatorTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testWithAdditionOperator.getSubjectAddition());
//		testWithAdditionOperator.setSubjectAddition(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testWithAdditionOperator.getSubjectAddition());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.licensing.WithAdditionOperator#setSubjectLicense}.
	 */
	public void testWithAdditionOperatorsetSubjectLicense() throws InvalidSPDXAnalysisException {
		WithAdditionOperator testWithAdditionOperator = builderForWithAdditionOperatorTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testWithAdditionOperator.getSubjectLicense());
//		testWithAdditionOperator.setSubjectLicense(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testWithAdditionOperator.getSubjectLicense());
		fail("Not yet implemented");
	}
}