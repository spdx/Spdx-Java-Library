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
import org.spdx.library.model.software.SpdxFile.SpdxFileBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class SpdxFileTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String CONTENT_TYPE_TEST_VALUE = "test contentType";
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static SpdxFileBuilder builderForSpdxFileTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SpdxFileBuilder retval = new SpdxFileBuilder(modelStore, objectUri, copyManager)
				.setContentType(CONTENT_TYPE_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxFile#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SpdxFile testSpdxFile = builderForSpdxFileTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testSpdxFile.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxFile#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		SpdxFile testSpdxFile = builderForSpdxFileTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Software.SpdxFile", testSpdxFile.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxFile#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		SpdxFile testSpdxFile = builderForSpdxFileTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("SpdxFile: "+TEST_OBJECT_URI, testSpdxFile.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxFile#Element(org.spdx.library.model.software.SpdxFile.SpdxFileBuilder)}.
	 */
	public void testSpdxFileSpdxFileBuilder() throws InvalidSPDXAnalysisException {
		builderForSpdxFileTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		SpdxFile testSpdxFile = builderForSpdxFileTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		SpdxFile test2SpdxFile = builderForSpdxFileTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testSpdxFile.equivalent(test2SpdxFile));
		assertTrue(test2SpdxFile.equivalent(testSpdxFile));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SpdxFile#setContentType}.
	 */
	public void testSpdxFilesetContentType() throws InvalidSPDXAnalysisException {
		SpdxFile testSpdxFile = builderForSpdxFileTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(CONTENT_TYPE_TEST_VALUE, testSpdxFile.getContentType());
		testSpdxFile.setContentType("new contentType value");
		assertEquals("new contentType value", testSpdxFile.getContentType());
	}
}