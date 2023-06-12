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
package org.spdx.library.model.compat.v2;

import java.util.ArrayList;

import org.spdx.library.InvalidSPDXAnalysisException;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SpdxConstantElementTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#_verify(java.util.List)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void test_verify() throws InvalidSPDXAnalysisException {
		assertEquals(0, new SpdxNoneElement().verify().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#getAnnotations()}.
	 */
	public void testGetAnnotations() throws InvalidSPDXAnalysisException {
		assertEquals(0, new SpdxNoneElement().getAnnotations().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#setAnnotations(java.util.Collection)}.
	 */
	public void testSetAnnotations() throws InvalidSPDXAnalysisException {
		SpdxConstantElement c = new SpdxNoneElement();
		try {
			c.setAnnotations(new ArrayList<Annotation>());
			fail("This should not have worked");
		} catch(Exception ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#addAnnotation(org.spdx.library.model.compat.v2.compat.v2.Annotation)}.
	 */
	public void testAddAnnotation() throws InvalidSPDXAnalysisException {
		SpdxConstantElement c = new SpdxNoneElement();
		try {
			c.addAnnotation(new Annotation());
			fail("This should not have worked");
		} catch(Exception ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#removeAnnotation(org.spdx.library.model.compat.v2.compat.v2.Annotation)}.
	 */
	public void testRemoveAnnotation() throws InvalidSPDXAnalysisException {
		SpdxConstantElement c = new SpdxNoneElement();
		try {
			c.removeAnnotation(new Annotation());
			fail("This should not have worked");
		} catch(Exception ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#getRelationships()}.
	 */
	public void testGetRelationships() throws InvalidSPDXAnalysisException {
		assertEquals(0, new SpdxNoneElement().getRelationships().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#setRelationships(java.util.Collection)}.
	 */
	public void testSetRelationships() throws InvalidSPDXAnalysisException {
		SpdxConstantElement c = new SpdxNoneElement();
		try {
			c.setRelationships(new ArrayList<Relationship>());
			fail("This should not have worked");
		} catch(Exception ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#addRelationship(org.spdx.library.model.compat.v2.compat.v2.Relationship)}.
	 */
	public void testAddRelationship() throws InvalidSPDXAnalysisException {
		SpdxConstantElement c = new SpdxNoneElement();
		try {
			c.addRelationship(new Relationship());
			fail("This should not have worked");
		} catch(Exception ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#removeRelationship(org.spdx.library.model.compat.v2.compat.v2.Relationship)}.
	 */
	public void testRemoveRelationship() throws InvalidSPDXAnalysisException {
		SpdxConstantElement c = new SpdxNoneElement();
		try {
			c.removeRelationship(new Relationship());
			fail("This should not have worked");
		} catch(Exception ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#setComment(java.lang.String)}.
	 */
	public void testSetComment() throws InvalidSPDXAnalysisException {
		SpdxConstantElement c = new SpdxNoneElement();
		try {
			c.setComment("New comment");
			fail("This should not have worked");
		} catch(Exception ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxConstantElement#setName(java.lang.String)}.
	 */
	public void testSetName() throws InvalidSPDXAnalysisException {
		SpdxConstantElement c = new SpdxNoneElement();
		try {
			c.setName("New name");
			fail("This should not have worked");
		} catch(Exception ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxElement#getComment()}.
	 */
	public void testGetComment() throws InvalidSPDXAnalysisException {
		assertTrue(new SpdxNoAssertionElement().getComment().isPresent());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxElement#getName()}.
	 */
	public void testGetName() throws InvalidSPDXAnalysisException {
		assertTrue(new SpdxNoAssertionElement().getName().isPresent());
	}

}
