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
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.licensing.License.LicenseBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class LicenseTest extends TestCase {

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
	
	public static LicenseBuilder builderForLicenseTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		LicenseBuilder retval = new LicenseBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setisFsfLibre(true)
		.setisDeprecatedLicenseId(true)
		.setisOsiApproved(true)
		.setstandardLicenseTemplate("A string")
		.setstandardLicenseHeader("A string")
		.setlicenseText("A string")
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testLicense.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Licensing.License", testLicense.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("License: "+TEST_OBJECT_URI, testLicense.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#Element(org.spdx.library.model.licensing.License.LicenseBuilder)}.
	 */
	public void testLicenseLicenseBuilder() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		License test2License = builderForLicenseTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testLicense.equivalent(test2License));
		assertTrue(test2License.equivalent(testLicense));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#setIsFsfLibre}.
	 */
	public void testLicensesetIsFsfLibre() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testLicense.getIsFsfLibre());
//		testLicense.setIsFsfLibre(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testLicense.getIsFsfLibre());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#setIsDeprecatedLicenseId}.
	 */
	public void testLicensesetIsDeprecatedLicenseId() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testLicense.getIsDeprecatedLicenseId());
//		testLicense.setIsDeprecatedLicenseId(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testLicense.getIsDeprecatedLicenseId());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#setIsOsiApproved}.
	 */
	public void testLicensesetIsOsiApproved() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testLicense.getIsOsiApproved());
//		testLicense.setIsOsiApproved(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testLicense.getIsOsiApproved());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#setStandardLicenseTemplate}.
	 */
	public void testLicensesetStandardLicenseTemplate() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testLicense.getStandardLicenseTemplate());
//		testLicense.setStandardLicenseTemplate(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testLicense.getStandardLicenseTemplate());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#setStandardLicenseHeader}.
	 */
	public void testLicensesetStandardLicenseHeader() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testLicense.getStandardLicenseHeader());
//		testLicense.setStandardLicenseHeader(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testLicense.getStandardLicenseHeader());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.licensing.License#setLicenseText}.
	 */
	public void testLicensesetLicenseText() throws InvalidSPDXAnalysisException {
		License testLicense = builderForLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testLicense.getLicenseText());
//		testLicense.setLicenseText(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testLicense.getLicenseText());
		fail("Not yet implemented");
	}

/*
*/

}