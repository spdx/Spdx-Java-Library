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
 
package org.spdx.library.model.v3.core;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.core.Person.PersonBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class PersonTest extends TestCase {

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
	
	public static PersonBuilder builderForPersonTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		PersonBuilder retval = new PersonBuilder(modelStore, objectUri, copyManager)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Person#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Person testPerson = builderForPersonTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testPerson.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Person#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Person testPerson = builderForPersonTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Person", testPerson.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Person#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Person testPerson = builderForPersonTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Person: "+TEST_OBJECT_URI, testPerson.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Person#Element(org.spdx.library.model.v3.core.Person.PersonBuilder)}.
	 */
	public void testPersonPersonBuilder() throws InvalidSPDXAnalysisException {
		builderForPersonTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Person testPerson = builderForPersonTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Person test2Person = builderForPersonTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testPerson.equivalent(test2Person));
		assertTrue(test2Person.equivalent(testPerson));
		// TODO change some parameters for negative tests
	}
}