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

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ByteOffsetPointerTest extends TestCase {
	
	String REFERENCED_ELEMENT_NAME1 = "Element1";
	String REFERENCED_ELEMENT_NAME2 = "Element2";
	SpdxElement REFERENCED1;
	SpdxElement REFERENCED2;
	GenericModelObject gmo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		REFERENCED1 = new GenericSpdxElement();
		REFERENCED1.setName("referenced1");
		REFERENCED2 = new GenericSpdxElement();
		REFERENCED2.setName("referenced2");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVerify() throws InvalidSPDXAnalysisException {
		ByteOffsetPointer bop = gmo.createByteOffsetPointer(REFERENCED1, 15);
		List<String> result = bop.verify();
		assertEquals(0, result.size());
		// Null referenced
		ByteOffsetPointer bop2 = gmo.createByteOffsetPointer(REFERENCED1, 15);
		bop2.setStrict(false);
		bop2.setReference(null);
		result = bop2.verify();
		assertEquals(1, result.size());
		ByteOffsetPointer bop3 = gmo.createByteOffsetPointer(REFERENCED1, 0);
		bop3.setStrict(false);
		bop3.setOffset(-1);
		result = bop3.verify();
		assertEquals(1, result.size());
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ByteOffsetPointer bop = gmo.createByteOffsetPointer(REFERENCED1, 15);
		ByteOffsetPointer bop2 = gmo.createByteOffsetPointer(REFERENCED1, 15);
		assertTrue(bop.equivalent(bop2));
		ByteOffsetPointer bop3 = gmo.createByteOffsetPointer(REFERENCED1, 55);
		assertFalse(bop.equivalent(bop3));
		assertFalse(bop3.equivalent(bop));
		ByteOffsetPointer bop4 = gmo.createByteOffsetPointer(REFERENCED2, 15);
		assertFalse(bop.equivalent(bop4));
		assertFalse(bop4.equivalent(bop));
	}
	
	public void testSetOffset() throws InvalidSPDXAnalysisException {
		ByteOffsetPointer bop = gmo.createByteOffsetPointer(REFERENCED1, 15);
		assertEquals(15, bop.getOffset());
		ByteOffsetPointer bop2 = new ByteOffsetPointer(bop.getModelStore(), bop.getDocumentUri(), 
				bop.getId(), bop.getCopyManager(), false);
		assertEquals(15, bop2.getOffset());
		bop.setOffset(55);
		assertEquals(55, bop.getOffset());
		assertEquals(55, bop2.getOffset());
	}
	
	public void testSetReference() throws InvalidSPDXAnalysisException {
		ByteOffsetPointer bop = gmo.createByteOffsetPointer(REFERENCED1, 15);
		assertEquals(REFERENCED1.getName(), bop.getReference().getName());
		ByteOffsetPointer bop2 = new ByteOffsetPointer(bop.getModelStore(), bop.getDocumentUri(), 
				bop.getId(), bop.getCopyManager(), false);
		assertEquals(REFERENCED1.getName(), bop2.getReference().getName());
		bop.setReference(REFERENCED2);
		assertEquals(REFERENCED2.getName(), bop.getReference().getName());
		assertEquals(REFERENCED2.getName(), bop2.getReference().getName());
	}
	
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		ByteOffsetPointer bop = gmo.createByteOffsetPointer(REFERENCED1, 15);
		ByteOffsetPointer bop2 = gmo.createByteOffsetPointer(REFERENCED1, 15);
		ByteOffsetPointer bop3 = gmo.createByteOffsetPointer(REFERENCED2, 15);
		ByteOffsetPointer bop4 = gmo.createByteOffsetPointer(REFERENCED1, 18);
		assertEquals(0, bop.compareTo(bop2));
		assertTrue(bop.compareTo(bop3) < 0);
		assertTrue(bop4.compareTo(bop) > 0);
	}
}
