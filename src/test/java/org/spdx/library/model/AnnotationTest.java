/**
 * Copyright (c) 2019 Source Auditor Inc.
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class AnnotationTest extends TestCase {
	
	static final String ANNOTATOR1 = "Person: Annotator1";
	static final String ANNOTATOR2 = "Person: Annotator2";
	static final String COMMENT1 = "Comment1";
	static final String COMMENT2 = "Comment2";
	String date;
	String oldDate;
	static AnnotationType REVIEW_ANNOTATION = AnnotationType.REVIEW;
	static AnnotationType OTHER_ANNOTATION = AnnotationType.OTHER;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		DateFormat format = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		date = format.format(new Date());
		oldDate = format.format(new Date(10101));
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.Annotation#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Annotation a = new GenericModelObject().createAnnotation(ANNOTATOR1, OTHER_ANNOTATION, date, COMMENT1);
		assertEquals(0, a.verify().size());
		a.setAnnotationType(null);
		a.setAnnotator(null);
		a.setAnnotationDate(null);
		a.setComment(null);
		assertEquals(4, a.verify().size());
	}
	
	public void testSetAnnotationType() throws InvalidSPDXAnalysisException {
		Annotation a = new GenericModelObject().createAnnotation(ANNOTATOR1, OTHER_ANNOTATION, date, COMMENT1);
		assertEquals(ANNOTATOR1, a.getAnnotator().get());
		assertEquals(OTHER_ANNOTATION, a.getAnnotationType().get());
		assertEquals(date, a.getAnnotationDate().get());
		assertEquals(COMMENT1, a.getComment().get());
		a.setAnnotationType(REVIEW_ANNOTATION);
		assertEquals(REVIEW_ANNOTATION, a.getAnnotationType().get());
		Annotation copy = new Annotation(a.getModelStore(), a.getDocumentUri(), a.getId(), false);
		assertEquals(REVIEW_ANNOTATION, copy.getAnnotationType().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.Annotation#setAnnotator(java.lang.String)}.
	 */
	public void testSetAnnotator() throws InvalidSPDXAnalysisException {
		Annotation a = new GenericModelObject().createAnnotation(ANNOTATOR1, OTHER_ANNOTATION, date, COMMENT1);	
		assertEquals(ANNOTATOR1, a.getAnnotator().get());
		assertEquals(OTHER_ANNOTATION, a.getAnnotationType().get());
		assertEquals(date, a.getAnnotationDate().get());
		assertEquals(COMMENT1, a.getComment().get());
		a.setAnnotator(ANNOTATOR2);
		assertEquals(ANNOTATOR2, a.getAnnotator().get());
		Annotation copy = new Annotation(a.getModelStore(), a.getDocumentUri(), a.getId(), false);
		assertEquals(ANNOTATOR2, copy.getAnnotator().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.Annotation#setComment(java.lang.String)}.
	 */
	public void testSetComment() throws InvalidSPDXAnalysisException {
		Annotation a = new GenericModelObject().createAnnotation(ANNOTATOR1, OTHER_ANNOTATION, date, COMMENT1);
		assertEquals(ANNOTATOR1, a.getAnnotator().get());
		assertEquals(OTHER_ANNOTATION, a.getAnnotationType().get());
		assertEquals(date, a.getAnnotationDate().get());
		assertEquals(COMMENT1, a.getComment().get());
		a.setComment(COMMENT2);
		assertEquals(COMMENT2, a.getComment().get());
		Annotation copy = new Annotation(a.getModelStore(), a.getDocumentUri(), a.getId(), false);
		assertEquals(COMMENT2, copy.getComment().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.Annotation#setAnnotationDate(java.lang.String)}.
	 */
	public void testSetAnnotationDate() throws InvalidSPDXAnalysisException {
		Annotation a = new GenericModelObject().createAnnotation(ANNOTATOR1, OTHER_ANNOTATION, date, COMMENT1);
		assertEquals(ANNOTATOR1, a.getAnnotator().get());
		assertEquals(OTHER_ANNOTATION, a.getAnnotationType().get());
		assertEquals(date, a.getAnnotationDate().get());
		assertEquals(COMMENT1, a.getComment().get());
		a.setAnnotationDate(oldDate);
		assertEquals(oldDate, a.getAnnotationDate().get());
		Annotation copy = new Annotation(a.getModelStore(), a.getDocumentUri(), a.getId(), false);
		assertEquals(oldDate, copy.getAnnotationDate().get());
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Annotation a1 = new GenericModelObject().createAnnotation(ANNOTATOR1, OTHER_ANNOTATION, date, COMMENT1);
		assertTrue(a1.equivalent(a1));
		Annotation a2 = new GenericModelObject().createAnnotation(ANNOTATOR1, OTHER_ANNOTATION, date, COMMENT1);
		assertTrue(a1.equivalent(a2));
		// annotator
		a2.setAnnotator(ANNOTATOR2);
		assertFalse(a1.equivalent(a2));
		a2.setAnnotator(ANNOTATOR1);
		assertTrue(a2.equivalent(a1));
		// annotationType
		a2.setAnnotationType(REVIEW_ANNOTATION);
		assertFalse(a1.equivalent(a2));
		a2.setAnnotationType(OTHER_ANNOTATION);
		assertTrue(a2.equivalent(a1));
		// comment
		a2.setComment(COMMENT2);
		assertFalse(a1.equivalent(a2));
		a2.setComment(COMMENT1);
		assertTrue(a2.equivalent(a1));
		// date
		a2.setAnnotationDate(oldDate);
		assertFalse(a1.equivalent(a2));
		a2.setAnnotationDate(date);
		assertTrue(a2.equivalent(a1));
	}
}
