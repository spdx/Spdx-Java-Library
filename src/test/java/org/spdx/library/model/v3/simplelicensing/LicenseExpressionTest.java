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
 
package org.spdx.library.model.v3.simplelicensing;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.simplelicensing.LicenseExpression.LicenseExpressionBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class LicenseExpressionTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String LICENSE_EXPRESSION_TEST_VALUE = "test licenseExpression";
	static final String LICENSE_LIST_VERSION_TEST_VALUE = "test licenseListVersion";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static LicenseExpressionBuilder builderForLicenseExpressionTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		LicenseExpressionBuilder retval = new LicenseExpressionBuilder(modelStore, objectUri, copyManager)
				.setLicenseExpression(LICENSE_EXPRESSION_TEST_VALUE)
				.setLicenseListVersion(LICENSE_LIST_VERSION_TEST_VALUE)
				//TODO: Add in test values
				/********************
				.addCustomIdToUri(DictionaryEntry)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.LicenseExpression#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		LicenseExpression testLicenseExpression = builderForLicenseExpressionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testLicenseExpression.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.LicenseExpression#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		LicenseExpression testLicenseExpression = builderForLicenseExpressionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("SimpleLicensing.LicenseExpression", testLicenseExpression.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.LicenseExpression#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		LicenseExpression testLicenseExpression = builderForLicenseExpressionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("LicenseExpression: "+TEST_OBJECT_URI, testLicenseExpression.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.LicenseExpression#Element(org.spdx.library.model.v3.simplelicensing.LicenseExpression.LicenseExpressionBuilder)}.
	 */
	public void testLicenseExpressionLicenseExpressionBuilder() throws InvalidSPDXAnalysisException {
		builderForLicenseExpressionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		LicenseExpression testLicenseExpression = builderForLicenseExpressionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		LicenseExpression test2LicenseExpression = builderForLicenseExpressionTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testLicenseExpression.equivalent(test2LicenseExpression));
		assertTrue(test2LicenseExpression.equivalent(testLicenseExpression));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.LicenseExpression#setLicenseExpression}.
	 */
	public void testLicenseExpressionsetLicenseExpression() throws InvalidSPDXAnalysisException {
		LicenseExpression testLicenseExpression = builderForLicenseExpressionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(LICENSE_EXPRESSION_TEST_VALUE, testLicenseExpression.getLicenseExpression());
		testLicenseExpression.setLicenseExpression("new licenseExpression value");
		assertEquals("new licenseExpression value", testLicenseExpression.getLicenseExpression());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.LicenseExpression#setLicenseListVersion}.
	 */
	public void testLicenseExpressionsetLicenseListVersion() throws InvalidSPDXAnalysisException {
		LicenseExpression testLicenseExpression = builderForLicenseExpressionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(LICENSE_LIST_VERSION_TEST_VALUE), testLicenseExpression.getLicenseListVersion());
		testLicenseExpression.setLicenseListVersion("new licenseListVersion value");
		assertEquals(Optional.of("new licenseListVersion value"), testLicenseExpression.getLicenseListVersion());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.LicenseExpression#getCustomIdToUri}.
	 */
	public void testLicenseExpressiongetCustomIdToUris() throws InvalidSPDXAnalysisException {
		LicenseExpression testLicenseExpression = builderForLicenseExpressionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testLicenseExpression.getCustomIdToUris())));
//		testLicenseExpression.getCustomIdToUris().clear();
//		testLicenseExpression.getCustomIdToUris().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testLicenseExpression.getCustomIdToUris())));
		fail("Not yet implemented");
	}
}