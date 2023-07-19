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
 
package org.spdx.library.model.core;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.core.PositiveIntegerRange.PositiveIntegerRangeBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class PositiveIntegerRangeTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final Integer END_TEST_VALUE = 55;
	static final Integer BEGIN_TEST_VALUE = 55;
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static PositiveIntegerRangeBuilder builderForPositiveIntegerRangeTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		PositiveIntegerRangeBuilder retval = new PositiveIntegerRangeBuilder(modelStore, objectUri, copyManager)
				.setEnd(END_TEST_VALUE)
				.setBegin(BEGIN_TEST_VALUE)
				//TODO: Add in test values
				/********************
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.PositiveIntegerRange#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		PositiveIntegerRange testPositiveIntegerRange = builderForPositiveIntegerRangeTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testPositiveIntegerRange.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.PositiveIntegerRange#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		PositiveIntegerRange testPositiveIntegerRange = builderForPositiveIntegerRangeTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.PositiveIntegerRange", testPositiveIntegerRange.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.PositiveIntegerRange#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		PositiveIntegerRange testPositiveIntegerRange = builderForPositiveIntegerRangeTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("PositiveIntegerRange: "+TEST_OBJECT_URI, testPositiveIntegerRange.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.core.PositiveIntegerRange#Element(org.spdx.library.model.core.PositiveIntegerRange.PositiveIntegerRangeBuilder)}.
	 */
	public void testPositiveIntegerRangePositiveIntegerRangeBuilder() throws InvalidSPDXAnalysisException {
		builderForPositiveIntegerRangeTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		PositiveIntegerRange testPositiveIntegerRange = builderForPositiveIntegerRangeTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		PositiveIntegerRange test2PositiveIntegerRange = builderForPositiveIntegerRangeTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testPositiveIntegerRange.equivalent(test2PositiveIntegerRange));
		assertTrue(test2PositiveIntegerRange.equivalent(testPositiveIntegerRange));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.PositiveIntegerRange#setEnd}.
	 */
	public void testPositiveIntegerRangesetEnd() throws InvalidSPDXAnalysisException {
		PositiveIntegerRange testPositiveIntegerRange = builderForPositiveIntegerRangeTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(END_TEST_VALUE, testPositiveIntegerRange.getEnd());
		testPositiveIntegerRange.setEnd(new Integer(653));
		assertEquals(new Integer(653), testPositiveIntegerRange.getEnd());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.core.PositiveIntegerRange#setBegin}.
	 */
	public void testPositiveIntegerRangesetBegin() throws InvalidSPDXAnalysisException {
		PositiveIntegerRange testPositiveIntegerRange = builderForPositiveIntegerRangeTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(BEGIN_TEST_VALUE, testPositiveIntegerRange.getBegin());
		testPositiveIntegerRange.setBegin(new Integer(653));
		assertEquals(new Integer(653), testPositiveIntegerRange.getBegin());
	}
}