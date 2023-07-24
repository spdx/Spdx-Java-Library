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
 
package org.spdx.library.model.v3.core;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.core.ExternalIdentifier.ExternalIdentifierBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class ExternalIdentifierTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String COMMENT_TEST_VALUE = "test comment";
	static final String IDENTIFIER_TEST_VALUE = "test identifier";
	static final String ISSUING_AUTHORITY_TEST_VALUE = "test issuingAuthority";
	static final ExternalIdentifierType EXTERNAL_IDENTIFIER_TYPE_TEST_VALUE1 = ExternalIdentifierType.values()[0];
	static final ExternalIdentifierType EXTERNAL_IDENTIFIER_TYPE_TEST_VALUE2 = ExternalIdentifierType.values()[1];
	static final String IDENTIFIER_LOCATOR_TEST_VALUE1 = "test 1 identifierLocator";
	static final String IDENTIFIER_LOCATOR_TEST_VALUE2 = "test 2 identifierLocator";
	static final String IDENTIFIER_LOCATOR_TEST_VALUE3 = "test 3 identifierLocator";
	static final List<String> IDENTIFIER_LOCATOR_TEST_LIST1 = Arrays.asList(new String[] { IDENTIFIER_LOCATOR_TEST_VALUE1, IDENTIFIER_LOCATOR_TEST_VALUE2 });
	static final List<String> IDENTIFIER_LOCATOR_TEST_LIST2 = Arrays.asList(new String[] { IDENTIFIER_LOCATOR_TEST_VALUE3 });
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static ExternalIdentifierBuilder builderForExternalIdentifierTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		ExternalIdentifierBuilder retval = new ExternalIdentifierBuilder(modelStore, objectUri, copyManager)
				.setComment(COMMENT_TEST_VALUE)
				.setIdentifier(IDENTIFIER_TEST_VALUE)
				.setIssuingAuthority(ISSUING_AUTHORITY_TEST_VALUE)
				.addIdentifierLocator(IDENTIFIER_LOCATOR_TEST_VALUE1)
				.addIdentifierLocator(IDENTIFIER_LOCATOR_TEST_VALUE2)
				.setExternalIdentifierType(EXTERNAL_IDENTIFIER_TYPE_TEST_VALUE1)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testExternalIdentifier.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.ExternalIdentifier", testExternalIdentifier.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("ExternalIdentifier: "+TEST_OBJECT_URI, testExternalIdentifier.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#Element(org.spdx.library.model.v3.core.ExternalIdentifier.ExternalIdentifierBuilder)}.
	 */
	public void testExternalIdentifierExternalIdentifierBuilder() throws InvalidSPDXAnalysisException {
		builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		ExternalIdentifier test2ExternalIdentifier = builderForExternalIdentifierTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testExternalIdentifier.equivalent(test2ExternalIdentifier));
		assertTrue(test2ExternalIdentifier.equivalent(testExternalIdentifier));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#setExternalIdentifierType}.
	 */
	public void testExternalIdentifiersetExternalIdentifierType() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(EXTERNAL_IDENTIFIER_TYPE_TEST_VALUE1, testExternalIdentifier.getExternalIdentifierType());
		testExternalIdentifier.setExternalIdentifierType(EXTERNAL_IDENTIFIER_TYPE_TEST_VALUE2);
		assertEquals(EXTERNAL_IDENTIFIER_TYPE_TEST_VALUE2, testExternalIdentifier.getExternalIdentifierType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#setComment}.
	 */
	public void testExternalIdentifiersetComment() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(COMMENT_TEST_VALUE), testExternalIdentifier.getComment());
		testExternalIdentifier.setComment("new comment value");
		assertEquals(Optional.of("new comment value"), testExternalIdentifier.getComment());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#setIdentifier}.
	 */
	public void testExternalIdentifiersetIdentifier() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(IDENTIFIER_TEST_VALUE, testExternalIdentifier.getIdentifier());
		testExternalIdentifier.setIdentifier("new identifier value");
		assertEquals("new identifier value", testExternalIdentifier.getIdentifier());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#setIssuingAuthority}.
	 */
	public void testExternalIdentifiersetIssuingAuthority() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(ISSUING_AUTHORITY_TEST_VALUE), testExternalIdentifier.getIssuingAuthority());
		testExternalIdentifier.setIssuingAuthority("new issuingAuthority value");
		assertEquals(Optional.of("new issuingAuthority value"), testExternalIdentifier.getIssuingAuthority());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.ExternalIdentifier#getIdentifierLocators}.
	 */
	public void testExternalIdentifiergetIdentifierLocators() throws InvalidSPDXAnalysisException {
		ExternalIdentifier testExternalIdentifier = builderForExternalIdentifierTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(IDENTIFIER_LOCATOR_TEST_LIST1, new ArrayList<>(testExternalIdentifier.getIdentifierLocators())));
		testExternalIdentifier.getIdentifierLocators().clear();
		testExternalIdentifier.getIdentifierLocators().addAll(IDENTIFIER_LOCATOR_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(IDENTIFIER_LOCATOR_TEST_LIST2, new ArrayList<>(testExternalIdentifier.getIdentifierLocators())));
	}
}