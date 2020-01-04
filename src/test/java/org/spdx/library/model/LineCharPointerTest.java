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
import org.spdx.library.model.pointer.LineCharPointer;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class LineCharPointerTest extends TestCase {

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

	/**
	 * Test method for {@link org.spdx.library.model.pointer.LineCharPointer#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		LineCharPointer lcp = gmo.createLineCharPointer(REFERENCED1, 15);
		lcp.setStrict(false);
		List<String> result = lcp.verify();
		assertEquals(0, result.size());
		// Null referenced
		LineCharPointer lcp2 = gmo.createLineCharPointer(REFERENCED1, 15);
		lcp2.setStrict(false);
		lcp2.setReference(null);
		result = lcp2.verify();
		assertEquals(1, result.size());
		LineCharPointer lcp3 = gmo.createLineCharPointer(REFERENCED1, -1);
		lcp3.setStrict(false);
		result = lcp3.verify();
		assertEquals(1, result.size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.pointer.LineCharPointer#setLineNumber(java.lang.Integer)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSetLineNumber() throws InvalidSPDXAnalysisException {
		LineCharPointer bop = gmo.createLineCharPointer(REFERENCED1, 15);
		assertEquals(15, bop.getLineNumber());
		LineCharPointer bop2 = new LineCharPointer(bop.getModelStore(), bop.getDocumentUri(), bop.getId(), bop.getCopyManager(), false);
		assertEquals(15, bop2.getLineNumber());
		bop.setLineNumber(55);
		assertEquals(55, bop.getLineNumber());
		assertEquals(55, bop2.getLineNumber());
	}
	
	public void testSetReference() throws InvalidSPDXAnalysisException {
		LineCharPointer lcp = gmo.createLineCharPointer(REFERENCED1, 15);
		assertEquals(REFERENCED1.getName(), lcp.getReference().getName());
		LineCharPointer lcp2 = new LineCharPointer(lcp.getModelStore(), lcp.getDocumentUri(), lcp.getId(), lcp.getCopyManager(), false);
		assertEquals(REFERENCED1.getName(), lcp2.getReference().getName());
		lcp.setReference(REFERENCED2);
		assertEquals(REFERENCED2.getName(), lcp.getReference().getName());
		assertEquals(REFERENCED2.getName(), lcp2.getReference().getName());
	}

	/**
	 * Test method for {@link org.spdx.library.model.pointer.LineCharPointer#compareTo(org.spdx.library.model.SinglePointer)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		LineCharPointer lcp = gmo.createLineCharPointer(REFERENCED1, 15);
		LineCharPointer lcp2 = gmo.createLineCharPointer(REFERENCED1, 15);
		LineCharPointer lcp3 = gmo.createLineCharPointer(REFERENCED2, 15);
		LineCharPointer lcp4 = gmo.createLineCharPointer(REFERENCED1, 18);
		assertEquals(0, lcp.compareTo(lcp2));
		assertTrue(lcp.compareTo(lcp3) < 0);
		assertTrue(lcp4.compareTo(lcp) > 0);
	}

}
