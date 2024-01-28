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
import org.spdx.library.model.v3.core.Annotation.AnnotationBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class AnnotationTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String STATEMENT_TEST_VALUE = "test statement";
	static final AnnotationType ANNOTATION_TYPE_TEST_VALUE1 = AnnotationType.values()[0];
	static final AnnotationType ANNOTATION_TYPE_TEST_VALUE2 = AnnotationType.values()[1];
	static final String CONTENT_TYPE_TEST_VALUE1 = "test 1 contentType";
	static final String CONTENT_TYPE_TEST_VALUE2 = "test 2 contentType";
	static final String CONTENT_TYPE_TEST_VALUE3 = "test 3 contentType";
	static final List<String> CONTENT_TYPE_TEST_LIST1 = Arrays.asList(new String[] { CONTENT_TYPE_TEST_VALUE1, CONTENT_TYPE_TEST_VALUE2 });
	static final List<String> CONTENT_TYPE_TEST_LIST2 = Arrays.asList(new String[] { CONTENT_TYPE_TEST_VALUE3 });
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static AnnotationBuilder builderForAnnotationTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		AnnotationBuilder retval = new AnnotationBuilder(modelStore, objectUri, copyManager)
				.setStatement(STATEMENT_TEST_VALUE)
				.addContentType(CONTENT_TYPE_TEST_VALUE1)
				.addContentType(CONTENT_TYPE_TEST_VALUE2)
				.setAnnotationType(ANNOTATION_TYPE_TEST_VALUE1)
				//TODO: Add in test values
				/********************
				.setSubject(new Element())
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Annotation#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Annotation testAnnotation = builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testAnnotation.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Annotation#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Annotation testAnnotation = builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Core.Annotation", testAnnotation.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Annotation#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Annotation testAnnotation = builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Annotation: "+TEST_OBJECT_URI, testAnnotation.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Annotation#Element(org.spdx.library.model.v3.core.Annotation.AnnotationBuilder)}.
	 */
	public void testAnnotationAnnotationBuilder() throws InvalidSPDXAnalysisException {
		builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Annotation testAnnotation = builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Annotation test2Annotation = builderForAnnotationTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testAnnotation.equivalent(test2Annotation));
		assertTrue(test2Annotation.equivalent(testAnnotation));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Annotation#setSubject}.
	 */
	public void testAnnotationsetSubject() throws InvalidSPDXAnalysisException {
		Annotation testAnnotation = builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testAnnotation.getSubject());
//		testAnnotation.setSubject(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testAnnotation.getSubject());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Annotation#setAnnotationType}.
	 */
	public void testAnnotationsetAnnotationType() throws InvalidSPDXAnalysisException {
		Annotation testAnnotation = builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(ANNOTATION_TYPE_TEST_VALUE1, testAnnotation.getAnnotationType());
		testAnnotation.setAnnotationType(ANNOTATION_TYPE_TEST_VALUE2);
		assertEquals(ANNOTATION_TYPE_TEST_VALUE2, testAnnotation.getAnnotationType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Annotation#setStatement}.
	 */
	public void testAnnotationsetStatement() throws InvalidSPDXAnalysisException {
		Annotation testAnnotation = builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(STATEMENT_TEST_VALUE), testAnnotation.getStatement());
		testAnnotation.setStatement("new statement value");
		assertEquals(Optional.of("new statement value"), testAnnotation.getStatement());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.core.Annotation#getContentTypes}.
	 */
	public void testAnnotationgetContentTypes() throws InvalidSPDXAnalysisException {
		Annotation testAnnotation = builderForAnnotationTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(CONTENT_TYPE_TEST_LIST1, new ArrayList<>(testAnnotation.getContentTypes())));
		testAnnotation.getContentTypes().clear();
		testAnnotation.getContentTypes().addAll(CONTENT_TYPE_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(CONTENT_TYPE_TEST_LIST2, new ArrayList<>(testAnnotation.getContentTypes())));
	}
}