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
import org.spdx.library.model.v3.simplelicensing.SimpleLicensingText.SimpleLicensingTextBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class SimpleLicensingTextTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String LICENSE_TEXT_TEST_VALUE = "test licenseText";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static SimpleLicensingTextBuilder builderForSimpleLicensingTextTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SimpleLicensingTextBuilder retval = new SimpleLicensingTextBuilder(modelStore, objectUri, copyManager)
				.setLicenseText(LICENSE_TEXT_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.SimpleLicensingText#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SimpleLicensingText testSimpleLicensingText = builderForSimpleLicensingTextTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testSimpleLicensingText.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.SimpleLicensingText#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		SimpleLicensingText testSimpleLicensingText = builderForSimpleLicensingTextTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("SimpleLicensing.SimpleLicensingText", testSimpleLicensingText.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.SimpleLicensingText#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		SimpleLicensingText testSimpleLicensingText = builderForSimpleLicensingTextTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("SimpleLicensingText: "+TEST_OBJECT_URI, testSimpleLicensingText.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.SimpleLicensingText#Element(org.spdx.library.model.v3.simplelicensing.SimpleLicensingText.SimpleLicensingTextBuilder)}.
	 */
	public void testSimpleLicensingTextSimpleLicensingTextBuilder() throws InvalidSPDXAnalysisException {
		builderForSimpleLicensingTextTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		SimpleLicensingText testSimpleLicensingText = builderForSimpleLicensingTextTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		SimpleLicensingText test2SimpleLicensingText = builderForSimpleLicensingTextTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testSimpleLicensingText.equivalent(test2SimpleLicensingText));
		assertTrue(test2SimpleLicensingText.equivalent(testSimpleLicensingText));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.simplelicensing.SimpleLicensingText#setLicenseText}.
	 */
	public void testSimpleLicensingTextsetLicenseText() throws InvalidSPDXAnalysisException {
		SimpleLicensingText testSimpleLicensingText = builderForSimpleLicensingTextTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(LICENSE_TEXT_TEST_VALUE, testSimpleLicensingText.getLicenseText());
		testSimpleLicensingText.setLicenseText("new licenseText value");
		assertEquals("new licenseText value", testSimpleLicensingText.getLicenseText());
	}
}