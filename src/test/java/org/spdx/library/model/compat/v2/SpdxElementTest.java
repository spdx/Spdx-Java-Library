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
package org.spdx.library.model.compat.v2;


import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.compat.v2.enumerations.AnnotationType;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SpdxElementTest extends TestCase {
	
	static final String DOCUMENT_NAMESPACE = "http://doc/name/space#";
	static final String ELEMENT_NAME1 = "element1";
	static final String ELEMENT_NAME2 = "element2";
	static final String ELEMENT_COMMENT1 = "comment1";
	static final String ELEMENT_COMMENT2 = "comment2";

	static final String DATE_NOW = new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT).format(new Date());
	Annotation ANNOTATION1;
	Annotation ANNOTATION2;
	SpdxElement RELATED_ELEMENT1;
	SpdxElement RELATED_ELEMENT2;
	GenericModelObject gmo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset(SpdxMajorVersion.VERSION_2);
		gmo = new GenericModelObject();
		ANNOTATION1 = gmo.createAnnotation("Person: Annotator1",
			AnnotationType.OTHER, DATE_NOW, "Comment1");
		ANNOTATION2 = gmo.createAnnotation("Person: Annotator2",
				AnnotationType.REVIEW, DATE_NOW, "Comment2");
		RELATED_ELEMENT1 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), 
				gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), gmo.getCopyManager(), true);
		RELATED_ELEMENT1.setName("relatedElementName1");
		RELATED_ELEMENT1.setComment("related element comment 1");
		RELATED_ELEMENT2 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), 
				gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), gmo.getCopyManager(), true);
		RELATED_ELEMENT2.setName("relatedElementName2");
		RELATED_ELEMENT2.setComment("related element comment 2");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxElement#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		String id = SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM + "elementId";
		SpdxElement element1 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), id, gmo.getCopyManager(), true);
		List<String> result = element1.verify();
		assertEquals(0, result.size());
		element1.setName(ELEMENT_NAME1);
		result = element1.verify();
		assertEquals(0, result.size());
	}

	public void testAddRemoveAnnotations() throws InvalidSPDXAnalysisException {
		String id = "elementId";
		SpdxElement element1 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), id, gmo.getCopyManager(), true);
		element1.setName(ELEMENT_NAME1);
		Collection<Annotation> annotations = element1.getAnnotations();
		assertEquals(0, annotations.size());
		element1.addAnnotation(ANNOTATION1);
		assertEquals(1, annotations.size());
		assertEquals(1, element1.getAnnotations().size());
		assertTrue(annotations.contains(ANNOTATION1));
		assertTrue(element1.getAnnotations().contains(ANNOTATION1));
		assertFalse(annotations.contains(ANNOTATION2));
		assertFalse(element1.getAnnotations().contains(ANNOTATION2));
		element1.addAnnotation(ANNOTATION2);
		assertEquals(2, annotations.size());
		assertEquals(2, element1.getAnnotations().size());
		assertTrue(annotations.contains(ANNOTATION1));
		assertTrue(element1.getAnnotations().contains(ANNOTATION1));
		assertTrue(annotations.contains(ANNOTATION2));
		assertTrue(element1.getAnnotations().contains(ANNOTATION2));
		element1.removeAnnotation(ANNOTATION2);
		assertEquals(1, annotations.size());
		assertEquals(1, element1.getAnnotations().size());
		assertTrue(annotations.contains(ANNOTATION1));
		assertTrue(element1.getAnnotations().contains(ANNOTATION1));
		assertFalse(annotations.contains(ANNOTATION2));
		assertFalse(element1.getAnnotations().contains(ANNOTATION2));
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxElement#getRelationships()}.
	 */
	public void testGetRemoveRelationships() throws InvalidSPDXAnalysisException {
		String id = "elementId";
		SpdxElement element1 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), id, gmo.getCopyManager(), true);
		element1.setName(ELEMENT_NAME1);
		Collection<Relationship> relationships = element1.getRelationships();
		assertEquals(0, relationships.size());
		assertEquals(0, element1.getRelationships().size());
		Relationship relationship1 = element1.createRelationship(RELATED_ELEMENT1, RelationshipType.ANCESTOR_OF, "comment1");
		Relationship relationship2 = element1.createRelationship(RELATED_ELEMENT2, RelationshipType.COPY_OF, "comment2");
		element1.addRelationship(relationship1);
		assertEquals(1, relationships.size());
		assertEquals(1, element1.getRelationships().size());
		assertTrue(relationships.contains(relationship1));
		assertTrue(element1.getRelationships().contains(relationship1));
		assertFalse(relationships.contains(relationship2));
		assertFalse(element1.getRelationships().contains(relationship2));
		element1.addRelationship(relationship2);
		assertEquals(2, relationships.size());
		assertEquals(2, element1.getRelationships().size());
		assertTrue(relationships.contains(relationship1));
		assertTrue(element1.getRelationships().contains(relationship1));
		assertTrue(relationships.contains(relationship1));
		assertTrue(element1.getRelationships().contains(relationship1));
		element1.removeRelationship(relationship2);
		assertEquals(1, relationships.size());
		assertEquals(1, element1.getRelationships().size());
		assertTrue(relationships.contains(relationship1));
		assertTrue(element1.getRelationships().contains(relationship1));
		assertFalse(relationships.contains(relationship2));
		assertFalse(element1.getRelationships().contains(relationship2));
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxElement#setComment(java.lang.String)}.
	 */
	public void testSetcomment() throws InvalidSPDXAnalysisException {
		String id = "elementId";
		SpdxElement element1 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), id, gmo.getCopyManager(), true);
		element1.setName(ELEMENT_NAME1);
		assertFalse(element1.getComment().isPresent());
		element1.setComment(ELEMENT_COMMENT1);
		assertEquals(ELEMENT_COMMENT1, element1.getComment().get());
		element1.setComment(ELEMENT_COMMENT2);
		assertEquals(ELEMENT_COMMENT2, element1.getComment().get());
		element1.setComment(null);
		assertFalse(element1.getComment().isPresent());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxElement#setName(java.lang.String)}.
	 */
	public void testSetName() throws InvalidSPDXAnalysisException {
		String id = "elementId";
		SpdxElement element1 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), id, gmo.getCopyManager(), true);
		assertFalse(element1.getName().isPresent());
		element1.setName(ELEMENT_NAME1);
		assertEquals(ELEMENT_NAME1, element1.getName().get());
		element1.setName(ELEMENT_NAME2);
		assertEquals(ELEMENT_NAME2, element1.getName().get());
		element1.setName(null);
		assertFalse(element1.getName().isPresent());
	}
	
	public void testEquivalentObject() throws InvalidSPDXAnalysisException {
		String id1 = "elementId1";
		SpdxElement element1 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), id1, gmo.getCopyManager(), true);
		String id2 = "elementId2";
		SpdxElement element2 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), id2, gmo.getCopyManager(), true);
		Relationship relationship1 = element1.createRelationship(RELATED_ELEMENT1, RelationshipType.COPY_OF, "comment1");
		Relationship relationship1_1 = element2.createRelationship(RELATED_ELEMENT1, RelationshipType.COPY_OF, "comment1");
		Relationship relationship2 = element1.createRelationship(RELATED_ELEMENT2, RelationshipType.DYNAMIC_LINK, "comment2");
		Relationship relationship2_2 = element2.createRelationship(RELATED_ELEMENT2, RelationshipType.DYNAMIC_LINK, "comment2");
		element1.addRelationship(relationship1);
		element2.addRelationship(relationship1_1);
		element1.addRelationship(relationship2);
		element2.addRelationship(relationship2_2);
		element1.addAnnotation(ANNOTATION1);
		element2.addAnnotation(ANNOTATION1);
		element1.addAnnotation(ANNOTATION2);
		element2.addAnnotation(ANNOTATION2);
		element1.setName(ELEMENT_NAME1);
		element2.setName(ELEMENT_NAME1);
		element1.setComment(ELEMENT_COMMENT1);
		element2.setComment(ELEMENT_COMMENT1);
		assertTrue(element1.equivalent(element2));
		assertTrue(element2.equivalent(element1));
		// name
		element2.setName(ELEMENT_NAME2);
		assertFalse(element1.equivalent(element2));
		element2.setName(ELEMENT_NAME1);
		assertTrue(element2.equivalent(element1));
		// comment
		element2.setComment(ELEMENT_COMMENT2);
		assertFalse(element1.equivalent(element2));
		element2.setComment(ELEMENT_COMMENT1);
		assertTrue(element2.equivalent(element1));
		element2.removeAnnotation(ANNOTATION2);
		assertFalse(element1.equivalent(element2));
		element2.addAnnotation(ANNOTATION2);
		assertTrue(element2.equivalent(element1));
		// relationships different
		element2.removeRelationship(relationship2_2);
		assertFalse(element1.equivalent(element2));
		element2.addRelationship(relationship2_2);
		assertTrue(element2.equivalent(element1));
	}
}
