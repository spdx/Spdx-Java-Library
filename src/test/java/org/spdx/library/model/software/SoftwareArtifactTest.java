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
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.software.SoftwareArtifact.SoftwareArtifactBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class SoftwareArtifactTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String CONTENT_IDENTIFIER_TEST_VALUE = "test contentIdentifier";
	static final String ATTRIBUTION_TEXT_TEST_VALUE = "test attributionText";
	static final String COPYRIGHT_TEXT_TEST_VALUE = "test copyrightText";
	static final SoftwarePurpose PRIMARY_PURPOSE_TEST_VALUE1 = SoftwarePurpose.values()[0];
	static final SoftwarePurpose PRIMARY_PURPOSE_TEST_VALUE2 = SoftwarePurpose.values()[1];
	static final SoftwarePurpose ADDITIONAL_PURPOSE_TEST_VALUE1 = SoftwarePurpose.values()[0];
	static final SoftwarePurpose ADDITIONAL_PURPOSE_TEST_VALUE2 = SoftwarePurpose.values()[1];
	static final List<SoftwarePurpose> ADDITIONAL_PURPOSE_TEST_LIST1 = Arrays.asList(new SoftwarePurpose[] { ADDITIONAL_PURPOSE_TEST_VALUE1, ADDITIONAL_PURPOSE_TEST_VALUE2 });
	static final List<SoftwarePurpose> ADDITIONAL_PURPOSE_TEST_LIST2 = Arrays.asList(new SoftwarePurpose[] { ADDITIONAL_PURPOSE_TEST_VALUE1 });
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static SoftwareArtifactBuilder builderForSoftwareArtifactTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SoftwareArtifactBuilder retval = new SoftwareArtifactBuilder(modelStore, objectUri, copyManager)
				.setContentIdentifier(CONTENT_IDENTIFIER_TEST_VALUE)
				.setAttributionText(ATTRIBUTION_TEXT_TEST_VALUE)
				.setCopyrightText(COPYRIGHT_TEXT_TEST_VALUE)
				.setPrimaryPurpose(PRIMARY_PURPOSE_TEST_VALUE1)
				.addAdditionalPurpose(ADDITIONAL_PURPOSE_TEST_VALUE1)
				.addAdditionalPurpose(ADDITIONAL_PURPOSE_TEST_VALUE2)
				//TODO: Add in test values
				/********************
				.setDeclaredLicense(TEST_ANYLICENSE_INFO)
				.setConcludedLicense(TEST_ANYLICENSE_INFO)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testSoftwareArtifact.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Software.SoftwareArtifact", testSoftwareArtifact.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("SoftwareArtifact: "+TEST_OBJECT_URI, testSoftwareArtifact.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#Element(org.spdx.library.model.software.SoftwareArtifact.SoftwareArtifactBuilder)}.
	 */
	public void testSoftwareArtifactSoftwareArtifactBuilder() throws InvalidSPDXAnalysisException {
		builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		SoftwareArtifact test2SoftwareArtifact = builderForSoftwareArtifactTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testSoftwareArtifact.equivalent(test2SoftwareArtifact));
		assertTrue(test2SoftwareArtifact.equivalent(testSoftwareArtifact));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#setDeclaredLicense}.
	 */
	public void testSoftwareArtifactsetDeclaredLicense() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(Optional.of(TEST_VALUE), testSoftwareArtifact.getDeclaredLicense());
//		testSoftwareArtifact.setDeclaredLicense(NEW_TEST_VALUE);
//		assertEquals(Optional.of(NEW_TEST_VALUE), testSoftwareArtifact.getDeclaredLicense());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#setConcludedLicense}.
	 */
	public void testSoftwareArtifactsetConcludedLicense() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(Optional.of(TEST_VALUE), testSoftwareArtifact.getConcludedLicense());
//		testSoftwareArtifact.setConcludedLicense(NEW_TEST_VALUE);
//		assertEquals(Optional.of(NEW_TEST_VALUE), testSoftwareArtifact.getConcludedLicense());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#setPrimaryPurpose}.
	 */
	public void testSoftwareArtifactsetPrimaryPurpose() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(PRIMARY_PURPOSE_TEST_VALUE1), testSoftwareArtifact.getPrimaryPurpose());
		testSoftwareArtifact.setPrimaryPurpose(PRIMARY_PURPOSE_TEST_VALUE2);
		assertEquals(Optional.of(PRIMARY_PURPOSE_TEST_VALUE2), testSoftwareArtifact.getPrimaryPurpose());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#setContentIdentifier}.
	 */
	public void testSoftwareArtifactsetContentIdentifier() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(CONTENT_IDENTIFIER_TEST_VALUE), testSoftwareArtifact.getContentIdentifier());
		testSoftwareArtifact.setContentIdentifier("new contentIdentifier value");
		assertEquals(Optional.of("new contentIdentifier value"), testSoftwareArtifact.getContentIdentifier());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#setAttributionText}.
	 */
	public void testSoftwareArtifactsetAttributionText() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(ATTRIBUTION_TEXT_TEST_VALUE), testSoftwareArtifact.getAttributionText());
		testSoftwareArtifact.setAttributionText("new attributionText value");
		assertEquals(Optional.of("new attributionText value"), testSoftwareArtifact.getAttributionText());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#setCopyrightText}.
	 */
	public void testSoftwareArtifactsetCopyrightText() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(COPYRIGHT_TEXT_TEST_VALUE), testSoftwareArtifact.getCopyrightText());
		testSoftwareArtifact.setCopyrightText("new copyrightText value");
		assertEquals(Optional.of("new copyrightText value"), testSoftwareArtifact.getCopyrightText());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.software.SoftwareArtifact#getAdditionalPurpose}.
	 */
	public void testSoftwareArtifactgetAdditionalPurposes() throws InvalidSPDXAnalysisException {
		SoftwareArtifact testSoftwareArtifact = builderForSoftwareArtifactTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(ADDITIONAL_PURPOSE_TEST_LIST1, new ArrayList<>(testSoftwareArtifact.getAdditionalPurposes())));
		testSoftwareArtifact.getAdditionalPurposes().clear();
		testSoftwareArtifact.getAdditionalPurposes().addAll(ADDITIONAL_PURPOSE_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(ADDITIONAL_PURPOSE_TEST_LIST2, new ArrayList<>(testSoftwareArtifact.getAdditionalPurposes())));
	}
}