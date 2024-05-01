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
import org.spdx.library.model.v3.core.Relationship.RelationshipBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class RelationshipTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String START_TIME_TEST_VALUE = "test startTime";
	static final String END_TIME_TEST_VALUE = "test endTime";
	static final RelationshipCompleteness COMPLETENESS_TEST_VALUE1 = RelationshipCompleteness.values()[0];
	static final RelationshipCompleteness COMPLETENESS_TEST_VALUE2 = RelationshipCompleteness.values()[1];
	static final RelationshipType RELATIONSHIP_TYPE_TEST_VALUE1 = RelationshipType.values()[0];
	static final RelationshipType RELATIONSHIP_TYPE_TEST_VALUE2 = RelationshipType.values()[1];
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static RelationshipBuilder builderForRelationshipTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		RelationshipBuilder retval = new RelationshipBuilder(modelStore, objectUri, copyManager)
				.setStartTime(START_TIME_TEST_VALUE)
				.setEndTime(END_TIME_TEST_VALUE)
				.setCompleteness(COMPLETENESS_TEST_VALUE1)
				.setRelationshipType(RELATIONSHIP_TYPE_TEST_VALUE1)
				//TODO: Add in test values
				/********************
				.setFrom(new Element())
				.addTo(Element)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testRelationship.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Relationship", testRelationship.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Relationship: "+TEST_OBJECT_URI, testRelationship.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#Element(org.spdx.library.model.v3.core.Relationship.RelationshipBuilder)}.
	 */
	public void testRelationshipRelationshipBuilder() throws InvalidSPDXAnalysisException {
		builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Relationship test2Relationship = builderForRelationshipTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testRelationship.equivalent(test2Relationship));
		assertTrue(test2Relationship.equivalent(testRelationship));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#setFrom}.
	 */
	public void testRelationshipsetFrom() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testRelationship.getFrom());
//		testRelationship.setFrom(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testRelationship.getFrom());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#setCompleteness}.
	 */
	public void testRelationshipsetCompleteness() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(COMPLETENESS_TEST_VALUE1), testRelationship.getCompleteness());
		testRelationship.setCompleteness(COMPLETENESS_TEST_VALUE2);
		assertEquals(Optional.of(COMPLETENESS_TEST_VALUE2), testRelationship.getCompleteness());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#setRelationshipType}.
	 */
	public void testRelationshipsetRelationshipType() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(RELATIONSHIP_TYPE_TEST_VALUE1, testRelationship.getRelationshipType());
		testRelationship.setRelationshipType(RELATIONSHIP_TYPE_TEST_VALUE2);
		assertEquals(RELATIONSHIP_TYPE_TEST_VALUE2, testRelationship.getRelationshipType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#setStartTime}.
	 */
	public void testRelationshipsetStartTime() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(START_TIME_TEST_VALUE), testRelationship.getStartTime());
		testRelationship.setStartTime("new startTime value");
		assertEquals(Optional.of("new startTime value"), testRelationship.getStartTime());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#setEndTime}.
	 */
	public void testRelationshipsetEndTime() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(END_TIME_TEST_VALUE), testRelationship.getEndTime());
		testRelationship.setEndTime("new endTime value");
		assertEquals(Optional.of("new endTime value"), testRelationship.getEndTime());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Relationship#getTo}.
	 */
	public void testRelationshipgetTos() throws InvalidSPDXAnalysisException {
		Relationship testRelationship = builderForRelationshipTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testRelationship.getTos())));
//		testRelationship.getTos().clear();
//		testRelationship.getTos().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testRelationship.getTos())));
		fail("Not yet implemented");
	}
}