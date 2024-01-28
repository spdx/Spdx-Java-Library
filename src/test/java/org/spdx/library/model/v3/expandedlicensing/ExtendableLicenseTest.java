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
import org.spdx.library.model.v3.expandedlicensing.ExtendableLicense.ExtendableLicenseBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ExtendableLicenseTest extends TestCase {

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
	
	public static ExtendableLicenseBuilder builderForExtendableLicenseTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ExtendableLicenseBuilder retval = new ExtendableLicenseBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ExtendableLicense#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ExtendableLicense testExtendableLicense = builderForExtendableLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testExtendableLicense.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ExtendableLicense#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ExtendableLicense testExtendableLicense = builderForExtendableLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExpandedLicensing.ExtendableLicense", testExtendableLicense.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ExtendableLicense#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ExtendableLicense testExtendableLicense = builderForExtendableLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExtendableLicense: "+TEST_OBJECT_URI, testExtendableLicense.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ExtendableLicense#Element(org.spdx.library.model.v3.expandedlicensing.ExtendableLicense.ExtendableLicenseBuilder)}.
	 */
	public void testExtendableLicenseExtendableLicenseBuilder() throws InvalidSPDXAnalysisException {
		builderForExtendableLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ExtendableLicense testExtendableLicense = builderForExtendableLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ExtendableLicense test2ExtendableLicense = builderForExtendableLicenseTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testExtendableLicense.equivalent(test2ExtendableLicense));
		assertTrue(test2ExtendableLicense.equivalent(testExtendableLicense));
		// TODO change some parameters for negative tests
	}
}