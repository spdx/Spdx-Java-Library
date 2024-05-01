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
import org.spdx.library.model.v3.expandedlicensing.CustomLicenseAddition.CustomLicenseAdditionBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class CustomLicenseAdditionTest extends TestCase {

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
	
	public static CustomLicenseAdditionBuilder builderForCustomLicenseAdditionTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		CustomLicenseAdditionBuilder retval = new CustomLicenseAdditionBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.CustomLicenseAddition#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		CustomLicenseAddition testCustomLicenseAddition = builderForCustomLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testCustomLicenseAddition.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.CustomLicenseAddition#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		CustomLicenseAddition testCustomLicenseAddition = builderForCustomLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExpandedLicensing.CustomLicenseAddition", testCustomLicenseAddition.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.CustomLicenseAddition#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		CustomLicenseAddition testCustomLicenseAddition = builderForCustomLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("CustomLicenseAddition: "+TEST_OBJECT_URI, testCustomLicenseAddition.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.CustomLicenseAddition#Element(org.spdx.library.model.v3.expandedlicensing.CustomLicenseAddition.CustomLicenseAdditionBuilder)}.
	 */
	public void testCustomLicenseAdditionCustomLicenseAdditionBuilder() throws InvalidSPDXAnalysisException {
		builderForCustomLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		CustomLicenseAddition testCustomLicenseAddition = builderForCustomLicenseAdditionTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		CustomLicenseAddition test2CustomLicenseAddition = builderForCustomLicenseAdditionTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testCustomLicenseAddition.equivalent(test2CustomLicenseAddition));
		assertTrue(test2CustomLicenseAddition.equivalent(testCustomLicenseAddition));
		// TODO change some parameters for negative tests
	}
}