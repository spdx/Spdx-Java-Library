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
import org.spdx.library.model.v3.expandedlicensing.ListedLicense.ListedLicenseBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ListedLicenseTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String LIST_VERSION_ADDED_TEST_VALUE = "test listVersionAdded";
	static final String DEPRECATED_VERSION_TEST_VALUE = "test deprecatedVersion";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static ListedLicenseBuilder builderForListedLicenseTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ListedLicenseBuilder retval = new ListedLicenseBuilder(modelStore, objectUri, copyManager)
				.setListVersionAdded(LIST_VERSION_ADDED_TEST_VALUE)
				.setDeprecatedVersion(DEPRECATED_VERSION_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ListedLicense#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ListedLicense testListedLicense = builderForListedLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testListedLicense.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ListedLicense#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ListedLicense testListedLicense = builderForListedLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExpandedLicensing.ListedLicense", testListedLicense.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ListedLicense#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ListedLicense testListedLicense = builderForListedLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ListedLicense: "+TEST_OBJECT_URI, testListedLicense.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ListedLicense#Element(org.spdx.library.model.v3.expandedlicensing.ListedLicense.ListedLicenseBuilder)}.
	 */
	public void testListedLicenseListedLicenseBuilder() throws InvalidSPDXAnalysisException {
		builderForListedLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ListedLicense testListedLicense = builderForListedLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ListedLicense test2ListedLicense = builderForListedLicenseTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testListedLicense.equivalent(test2ListedLicense));
		assertTrue(test2ListedLicense.equivalent(testListedLicense));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ListedLicense#setListVersionAdded}.
	 */
	public void testListedLicensesetListVersionAdded() throws InvalidSPDXAnalysisException {
		ListedLicense testListedLicense = builderForListedLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(LIST_VERSION_ADDED_TEST_VALUE), testListedLicense.getListVersionAdded());
		testListedLicense.setListVersionAdded("new listVersionAdded value");
		assertEquals(Optional.of("new listVersionAdded value"), testListedLicense.getListVersionAdded());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.expandedlicensing.ListedLicense#setDeprecatedVersion}.
	 */
	public void testListedLicensesetDeprecatedVersion() throws InvalidSPDXAnalysisException {
		ListedLicense testListedLicense = builderForListedLicenseTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(DEPRECATED_VERSION_TEST_VALUE), testListedLicense.getDeprecatedVersion());
		testListedLicense.setDeprecatedVersion("new deprecatedVersion value");
		assertEquals(Optional.of("new deprecatedVersion value"), testListedLicense.getDeprecatedVersion());
	}
}