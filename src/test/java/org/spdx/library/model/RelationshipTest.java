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


import java.text.SimpleDateFormat;
import java.util.Date;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class RelationshipTest extends TestCase {
	
	static final String DOCUMENT_NAMESPACE = "http://doc/name/space#";
	static final String ELEMENT_NAME1 = "element1";
	static final String ELEMENT_NAME2 = "element2";
	static final String ELEMENT_COMMENT1 = "comment1";
	static final String ELEMENT_COMMENT2 = "comment2";

	static final String DATE_NOW = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT).format(new Date());
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
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), true);
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.MD5, "595f44fec1e92a71d3e9e77456ba80d1");
		ExternalDocumentRef externalDoc = gmo.createExternalDocumentRef(gmo.getModelStore().getNextId(IdType.DocumentRef,gmo.getDocumentUri()), 
				"https://external.doc/one", checksum);
		ANNOTATION1 = gmo.createAnnotation("Annotator1",
			AnnotationType.OTHER, DATE_NOW, "Comment1");
		ANNOTATION2 = gmo.createAnnotation("Annotator2",
				AnnotationType.REVIEW, DATE_NOW, "Comment2");
		RELATED_ELEMENT1 = new GenericSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), 
				gmo.getModelStore().getNextId(IdType.Anonymous, gmo.getDocumentUri()), true);
		RELATED_ELEMENT1.setName("relatedElementName1");
		RELATED_ELEMENT1.setComment("related element comment 1");
		RELATED_ELEMENT2 = new ExternalSpdxElement(gmo.getModelStore(), gmo.getDocumentUri(), 
				externalDoc.getId() + ":SPDXRef-10", true);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.Relationship#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		RelationshipType relationshipType1  = RelationshipType.DESCENDANT_OF;
		String comment1 = "Comment1";
		Relationship relationship = new Relationship(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getModelStore().getNextId(IdType.Anonymous, gmo.getDocumentUri()), true);
		assertEquals(2, relationship.verify().size());
		relationship.setRelatedSpdxElement(RELATED_ELEMENT1);
		assertEquals(1, relationship.verify().size());
		relationship.setRelationshipType(relationshipType1);
		assertEquals(0, relationship.verify().size());
		relationship.setComment(comment1);
		assertEquals(0, relationship.verify().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.Relationship#setRelationshipType(org.spdx.library.model.enumerations.RelationshipType)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSetRelationshipType() throws InvalidSPDXAnalysisException {
		RelationshipType relationshipType1  = RelationshipType.DESCENDANT_OF;
		String comment1 = "Comment1";
		Relationship relationship = gmo.createRelationship(null, RELATED_ELEMENT1, relationshipType1, comment1);
		assertEquals(RELATED_ELEMENT1, relationship.getRelatedSpdxElement().get());
		assertEquals(relationshipType1, relationship.getRelationshipType().get());
		assertEquals(comment1, relationship.getComment().get());
		RelationshipType relationshipType2  = RelationshipType.COPY_OF;
		relationship.setRelationshipType(relationshipType2);
		assertEquals(relationshipType2, relationship.getRelationshipType().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.Relationship#setComment(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSetComment() throws InvalidSPDXAnalysisException {
		RelationshipType relationshipType1  = RelationshipType.DESCENDANT_OF;
		String comment1 = "Comment1";
		Relationship relationship = gmo.createRelationship(null, RELATED_ELEMENT1, relationshipType1, comment1);
		assertEquals(RELATED_ELEMENT1, relationship.getRelatedSpdxElement().get());
		assertEquals(relationshipType1, relationship.getRelationshipType().get());
		assertEquals(comment1, relationship.getComment().get());
		String comment2 = "Comment Number 2";
		relationship.setComment(comment2);
		assertEquals(comment2, relationship.getComment().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.Relationship#setRelatedSpdxElement(org.spdx.library.model.SpdxElement)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSetRelatedSpdxElement() throws InvalidSPDXAnalysisException {
		RelationshipType relationshipType1  = RelationshipType.DESCENDANT_OF;
		String comment1 = "Comment1";
		Relationship relationship = gmo.createRelationship(null, RELATED_ELEMENT1, relationshipType1, comment1);
		assertEquals(RELATED_ELEMENT1, relationship.getRelatedSpdxElement().get());
		assertEquals(relationshipType1, relationship.getRelationshipType().get());
		assertEquals(comment1, relationship.getComment().get());
		relationship.setRelatedSpdxElement(RELATED_ELEMENT2);
		assertEquals(RELATED_ELEMENT2, relationship.getRelatedSpdxElement().get());
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		RelationshipType relationshipType1  = RelationshipType.DESCENDANT_OF;
		String comment1 = "Comment1";
		Relationship relationship = gmo.createRelationship(null, RELATED_ELEMENT1, relationshipType1, comment1);
		assertEquals(RELATED_ELEMENT1, relationship.getRelatedSpdxElement().get());
		assertEquals(relationshipType1, relationship.getRelationshipType().get());
		assertEquals(comment1, relationship.getComment().get());
		assertTrue(relationship.equivalent(relationship));
		Relationship relationship2 = gmo.createRelationship(null, RELATED_ELEMENT1, relationshipType1, comment1);
		assertTrue(relationship.equivalent(relationship2));
		assertTrue(relationship.equivalent(relationship2));
		// related SPDX element
		relationship2.setRelatedSpdxElement(RELATED_ELEMENT2);
		assertEquals(RELATED_ELEMENT2, relationship2.getRelatedSpdxElement().get());
		assertEquals(RELATED_ELEMENT1, relationship.getRelatedSpdxElement().get());
		assertFalse(RELATED_ELEMENT1.equivalent(RELATED_ELEMENT2));
		assertFalse(relationship.getRelatedSpdxElement().get().equivalent(relationship2.getRelatedSpdxElement().get()));
		assertFalse(relationship.equivalent(relationship2));
		relationship2.setRelatedSpdxElement(RELATED_ELEMENT1);
		assertTrue(relationship2.equivalent(relationship));
		// relationship type
		relationship2.setRelationshipType(RelationshipType.DYNAMIC_LINK);
		assertFalse(relationship.equivalent(relationship2));
		relationship2.setRelationshipType(relationshipType1);
		assertTrue(relationship2.equivalent(relationship));
		// comment
		relationship2.setComment("yet a different comment");
		assertFalse(relationship.equivalent(relationship2));
		relationship2.setComment(comment1);
		assertTrue(relationship2.equivalent(relationship));
	}

	/**
	 * Test method for {@link org.spdx.library.model.Relationship#compareTo(org.spdx.library.model.Relationship)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		RelationshipType relationshipType1  = RelationshipType.DESCENDANT_OF;
		String comment1 = "Comment1";
		Relationship relationship = gmo.createRelationship(null, RELATED_ELEMENT2, relationshipType1, comment1);
		Relationship compare = gmo.createRelationship(null, RELATED_ELEMENT2, relationshipType1, comment1);
		assertEquals(0, relationship.compareTo(compare));
		assertEquals(0, compare.compareTo(relationship));
		compare.setComment(null);
		assertEquals(1, relationship.compareTo(compare));
		assertEquals(-1, compare.compareTo(relationship));
		compare.setRelatedSpdxElement(RELATED_ELEMENT1);
		assertTrue(relationship.compareTo(compare) < 0);
		assertTrue(compare.compareTo(relationship) > 0);
		compare.setRelationshipType(RelationshipType.ANCESTOR_OF);
		assertTrue(relationship.compareTo(compare) > 0);
		assertTrue(compare.compareTo(relationship) < 0);
	}

}
