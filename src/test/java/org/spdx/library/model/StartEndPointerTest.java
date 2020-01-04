/**
 * Copyright (c) 2020 Source Auditor Inc.
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
package org.spdx.library.model;

import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.pointer.ByteOffsetPointer;
import org.spdx.library.model.pointer.LineCharPointer;
import org.spdx.library.model.pointer.SinglePointer;
import org.spdx.library.model.pointer.StartEndPointer;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class StartEndPointerTest extends TestCase {
	
	String REFERENCED_ELEMENT_NAME1 = "Element1";
	String REFERENCED_ELEMENT_NAME2 = "Element2";
	SpdxElement REFERENCED1;
	SpdxElement REFERENCED2;
	int OFFSET1 = 342;
	ByteOffsetPointer BOP_POINTER1;
	int LINE1 = 113;
	LineCharPointer LCP_POINTER1; 
	int OFFSET2 = 444;
	ByteOffsetPointer BOP_POINTER2;
	int LINE2 = 23422;
	LineCharPointer LCP_POINTER2; 
	GenericModelObject gmo;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		gmo = new GenericModelObject();
		REFERENCED1 = new GenericSpdxElement();
		REFERENCED1.setName(REFERENCED_ELEMENT_NAME1);
		REFERENCED2 = new GenericSpdxElement();
		REFERENCED2.setName(REFERENCED_ELEMENT_NAME2);
		BOP_POINTER1 = gmo.createByteOffsetPointer(REFERENCED1, OFFSET1);
		LCP_POINTER1 = gmo.createLineCharPointer(REFERENCED1, LINE1);
		BOP_POINTER2 = gmo.createByteOffsetPointer(REFERENCED1, OFFSET2);
		LCP_POINTER2 = gmo.createLineCharPointer(REFERENCED2, LINE2);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.pointer.StartEndPointer#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		StartEndPointer sop = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		List<String> result = sop.verify();
		assertEquals(0, result.size());
		StartEndPointer sop2 = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		sop2.setStrict(false);
		sop2.setStartPointer(null);
		result = sop2.verify();
		assertEquals(1, result.size());
		StartEndPointer sop3 = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		sop3.setStrict(false);
		sop3.setEndPointer(null);
		result = sop3.verify();
		assertEquals(1, result.size());
		ByteOffsetPointer invalidByteOffset = gmo.createByteOffsetPointer(REFERENCED1, 0);
		invalidByteOffset.setStrict(false);
		invalidByteOffset.setOffset(-15);
		StartEndPointer sop4 = gmo.createStartEndPointer(BOP_POINTER1, invalidByteOffset);
		result = sop4.verify();
		assertEquals(2, result.size());
		// different types
		StartEndPointer sop5 = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		sop5.setStrict(false);
		sop5.setEndPointer(LCP_POINTER2);
		result = sop5.verify();
		assertEquals(1, result.size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.pointer.StartEndPointer#setEndPointer(org.spdx.library.model.pointer.SinglePointer)}.
	 */
	public void testSetEndPointer() throws InvalidSPDXAnalysisException {
		StartEndPointer sop = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		SinglePointer result = sop.getEndPointer();
		assertTrue(BOP_POINTER2.equivalent(result));
		result = sop.getEndPointer();
		assertTrue(BOP_POINTER2.equivalent(result));
		StartEndPointer sop2 = new StartEndPointer(sop.getModelStore(), sop.getDocumentUri(), sop.getId(), sop.getCopyManager(), false);
		result = sop2.getEndPointer();
		assertTrue(BOP_POINTER2.equivalent(result));
		sop2.setStrict(false);
		sop2.setEndPointer(LCP_POINTER1);
		result = sop2.getEndPointer();
		assertTrue(LCP_POINTER1.equivalent(result));
		result = sop.getEndPointer();
		assertTrue(LCP_POINTER1.equivalent(result));
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		StartEndPointer sop = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		StartEndPointer sop2 = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		assertTrue(sop.equivalent(sop2));
		assertTrue(sop2.equivalent(sop));
		StartEndPointer sop3 = gmo.createStartEndPointer(BOP_POINTER2, BOP_POINTER2);
		assertFalse(sop.equivalent(sop3));
		assertFalse(sop3.equivalent(sop));
		StartEndPointer sop4 = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER1);
		assertFalse(sop.equivalent(sop4));
		assertFalse(sop4.equivalent(sop));
	}

	/**
	 * Test method for {@link org.spdx.library.model.pointer.StartEndPointer#setStartPointer(org.spdx.library.model.pointer.SinglePointer)}.
	 */
	public void testSetStartPointerSinglePointer() throws InvalidSPDXAnalysisException {
		StartEndPointer sop = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		SinglePointer result = sop.getStartPointer();
		assertTrue(BOP_POINTER1.equivalent(result));
		result = sop.getStartPointer();
		assertTrue(BOP_POINTER1.equivalent(result));
		StartEndPointer sop2 = new StartEndPointer(sop.getModelStore(), sop.getDocumentUri(), sop.getId(), sop.getCopyManager(), false);
		result = sop2.getStartPointer();
		assertTrue(BOP_POINTER1.equivalent(result));
		sop2.setStartPointer(BOP_POINTER2);
		result = sop2.getStartPointer();
		assertTrue(BOP_POINTER2.equivalent(result));
		result = sop.getStartPointer();
		assertTrue(BOP_POINTER2.equivalent(result));
	}

	/**
	 * Test method for {@link org.spdx.library.model.pointer.StartEndPointer#compareTo(org.spdx.library.model.pointer.StartEndPointer)}.
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		StartEndPointer sop1 = gmo.createStartEndPointer(BOP_POINTER1, BOP_POINTER2);
		ByteOffsetPointer larger = gmo.createByteOffsetPointer(REFERENCED1, OFFSET1+1);
		StartEndPointer sop2 = gmo.createStartEndPointer(larger, BOP_POINTER2);
		assertTrue(sop1.compareTo(sop2) < 0);
	}

}
