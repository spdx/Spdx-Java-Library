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
import org.spdx.library.model.software.Sbom.SbomBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class SbomTest extends TestCase {

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
	
	public static SbomBuilder builderForSbomTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SbomBuilder retval = new SbomBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				.addSbomType(SBOMType.ENUM)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.Sbom#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Sbom testSbom = builderForSbomTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testSbom.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.Sbom#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Sbom testSbom = builderForSbomTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Software.Sbom", testSbom.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.Sbom#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Sbom testSbom = builderForSbomTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Sbom: "+TEST_OBJECT_URI, testSbom.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.Sbom#Element(org.spdx.library.model.software.Sbom.SbomBuilder)}.
	 */
	public void testSbomSbomBuilder() throws InvalidSPDXAnalysisException {
		builderForSbomTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Sbom testSbom = builderForSbomTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Sbom test2Sbom = builderForSbomTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testSbom.equivalent(test2Sbom));
		assertTrue(test2Sbom.equivalent(testSbom));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.Sbom#getSbomType}.
	 */
	public void testSbomgetSbomTypes() throws InvalidSPDXAnalysisException {
		Sbom testSbom = builderForSbomTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testSbom.getSbomTypes())));
//		testSbom.getSbomTypes().clear();
//		testSbom.getSbomTypes().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testSbom.getSbomTypes())));
		fail("Not yet implemented");
	}
}