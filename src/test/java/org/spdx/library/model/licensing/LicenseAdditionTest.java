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
 
package org.spdx.library.model.licensing;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.licensing.LicenseAddition.LicenseAdditionBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class LicenseAdditionTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String STANDARD_ADDITION_TEMPLATE_TEST_VALUE = "test standardAdditionTemplate";
	static final String ADDITION_TEXT_TEST_VALUE = "test additionText";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static LicenseAdditionBuilder builderForLicenseAdditionTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		LicenseAdditionBuilder retval = new LicenseAdditionBuilder(modelStore, objectUri, copyManager)
				.setIsDeprecatedAdditionId(true)
				.setStandardAdditionTemplate(STANDARD_ADDITION_TEMPLATE_TEST_VALUE)
				.setAdditionText(ADDITION_TEXT_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.LicenseAddition#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		LicenseAddition testLicenseAddition = builderForLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testLicenseAddition.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.licensing.LicenseAddition#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		LicenseAddition testLicenseAddition = builderForLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Licensing.LicenseAddition", testLicenseAddition.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.licensing.LicenseAddition#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		LicenseAddition testLicenseAddition = builderForLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("LicenseAddition: "+TEST_OBJECT_URI, testLicenseAddition.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.licensing.LicenseAddition#Element(org.spdx.library.model.licensing.LicenseAddition.LicenseAdditionBuilder)}.
	 */
	public void testLicenseAdditionLicenseAdditionBuilder() throws InvalidSPDXAnalysisException {
		builderForLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		LicenseAddition testLicenseAddition = builderForLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		LicenseAddition test2LicenseAddition = builderForLicenseAdditionTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testLicenseAddition.equivalent(test2LicenseAddition));
		assertTrue(test2LicenseAddition.equivalent(testLicenseAddition));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.LicenseAddition#setIsDeprecatedAdditionId}.
	 */
	public void testLicenseAdditionsetIsDeprecatedAdditionId() throws InvalidSPDXAnalysisException {
		LicenseAddition testLicenseAddition = builderForLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(new Boolean(true)), testLicenseAddition.getIsDeprecatedAdditionId());
		testLicenseAddition.setIsDeprecatedAdditionId(false);
		assertEquals(Optional.of(new Boolean(false)), testLicenseAddition.getIsDeprecatedAdditionId());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.LicenseAddition#setStandardAdditionTemplate}.
	 */
	public void testLicenseAdditionsetStandardAdditionTemplate() throws InvalidSPDXAnalysisException {
		LicenseAddition testLicenseAddition = builderForLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(STANDARD_ADDITION_TEMPLATE_TEST_VALUE), testLicenseAddition.getStandardAdditionTemplate());
		testLicenseAddition.setStandardAdditionTemplate("new standardAdditionTemplate value");
		assertEquals(Optional.of("new standardAdditionTemplate value"), testLicenseAddition.getStandardAdditionTemplate());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.LicenseAddition#setAdditionText}.
	 */
	public void testLicenseAdditionsetAdditionText() throws InvalidSPDXAnalysisException {
		LicenseAddition testLicenseAddition = builderForLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(ADDITION_TEXT_TEST_VALUE, testLicenseAddition.getAdditionText());
		testLicenseAddition.setAdditionText("new additionText value");
		assertEquals("new additionText value", testLicenseAddition.getAdditionText());
	}
}