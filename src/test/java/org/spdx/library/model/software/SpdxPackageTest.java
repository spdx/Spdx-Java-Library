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
 
package org.spdx.library.model.software;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.software.SpdxPackage.SpdxPackageBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class SpdxPackageTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String DOWNLOAD_LOCATION_TEST_VALUE = "test downloadLocation";
	static final String SOURCE_INFO_TEST_VALUE = "test sourceInfo";
	static final String PACKAGE_VERSION_TEST_VALUE = "test packageVersion";
	static final String HOME_PAGE_TEST_VALUE = "test homePage";
	static final String PACKAGE_URL_TEST_VALUE = "test packageUrl";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static SpdxPackageBuilder builderForSpdxPackageTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SpdxPackageBuilder retval = new SpdxPackageBuilder(modelStore, objectUri, copyManager)
				.setDownloadLocation(DOWNLOAD_LOCATION_TEST_VALUE)
				.setSourceInfo(SOURCE_INFO_TEST_VALUE)
				.setPackageVersion(PACKAGE_VERSION_TEST_VALUE)
				.setHomePage(HOME_PAGE_TEST_VALUE)
				.setPackageUrl(PACKAGE_URL_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testSpdxPackage.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Software.SpdxPackage", testSpdxPackage.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("SpdxPackage: "+TEST_OBJECT_URI, testSpdxPackage.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#Element(org.spdx.library.model.software.SpdxPackage.SpdxPackageBuilder)}.
	 */
	public void testSpdxPackageSpdxPackageBuilder() throws InvalidSPDXAnalysisException {
		builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		SpdxPackage test2SpdxPackage = builderForSpdxPackageTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testSpdxPackage.equivalent(test2SpdxPackage));
		assertTrue(test2SpdxPackage.equivalent(testSpdxPackage));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#setDownloadLocation}.
	 */
	public void testSpdxPackagesetDownloadLocation() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(DOWNLOAD_LOCATION_TEST_VALUE, testSpdxPackage.getDownloadLocation());
		testSpdxPackage.setDownloadLocation("new downloadLocation value");
		assertEquals("new downloadLocation value", testSpdxPackage.getDownloadLocation());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#setSourceInfo}.
	 */
	public void testSpdxPackagesetSourceInfo() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(SOURCE_INFO_TEST_VALUE, testSpdxPackage.getSourceInfo());
		testSpdxPackage.setSourceInfo("new sourceInfo value");
		assertEquals("new sourceInfo value", testSpdxPackage.getSourceInfo());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#setPackageVersion}.
	 */
	public void testSpdxPackagesetPackageVersion() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(PACKAGE_VERSION_TEST_VALUE, testSpdxPackage.getPackageVersion());
		testSpdxPackage.setPackageVersion("new packageVersion value");
		assertEquals("new packageVersion value", testSpdxPackage.getPackageVersion());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#setHomePage}.
	 */
	public void testSpdxPackagesetHomePage() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(HOME_PAGE_TEST_VALUE, testSpdxPackage.getHomePage());
		testSpdxPackage.setHomePage("new homePage value");
		assertEquals("new homePage value", testSpdxPackage.getHomePage());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxPackage#setPackageUrl}.
	 */
	public void testSpdxPackagesetPackageUrl() throws InvalidSPDXAnalysisException {
		SpdxPackage testSpdxPackage = builderForSpdxPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(PACKAGE_URL_TEST_VALUE, testSpdxPackage.getPackageUrl());
		testSpdxPackage.setPackageUrl("new packageUrl value");
		assertEquals("new packageUrl value", testSpdxPackage.getPackageUrl());
	}
}