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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.enumerations.RelationshipType;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class RelatedElementCollectionTest extends TestCase {
	
	List<SpdxElement> relatedDescendentOfElements;
	List<Relationship> descendedOfRelationships;
	List<SpdxElement> relatedDescribesOfElements;
	List<Relationship> describesOfRelationships;
	List<SpdxElement> relatedAmendsElements;
	List<Relationship> amendsOfRelationships;
	List<SpdxElement> allRelatedElements;
	List<Relationship> allRelationships;
	
	GenericSpdxElement element;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		element = new GenericSpdxElement();
		relatedDescendentOfElements = new ArrayList<>();
		descendedOfRelationships = new ArrayList<>();
		relatedDescendentOfElements.add(new GenericSpdxElement());
		for (SpdxElement re:relatedDescendentOfElements) {
			re.setName("Generic Element "+re.getId());
			Relationship r = element.createRelationship(re, RelationshipType.DESCENDANT_OF, "Descendant of "+re.getId());
			descendedOfRelationships.add(r);
			element.addRelationship(r);
		}
		relatedDescribesOfElements = new ArrayList<>();
		describesOfRelationships = new ArrayList<>();
		relatedDescribesOfElements.add(new GenericSpdxElement());
		relatedDescribesOfElements.add(new GenericSpdxElement());
		relatedDescribesOfElements.add(new GenericSpdxElement());
		for (SpdxElement re:relatedDescribesOfElements) {
			re.setName("Generic Element "+re.getId());
			Relationship r = element.createRelationship(re, RelationshipType.DESCRIBES, "Describes "+re.getId());
			describesOfRelationships.add(r);
			element.addRelationship(r);
		}
		relatedAmendsElements = new ArrayList<>();
		amendsOfRelationships = new ArrayList<>();
		relatedAmendsElements.add(new GenericSpdxElement());
		relatedAmendsElements.add(new GenericSpdxElement());
		for (SpdxElement re:relatedAmendsElements) {
			re.setName("Generic Element "+re.getId());
			Relationship r = element.createRelationship(re, RelationshipType.AMENDS, "Amends "+re.getId());
			amendsOfRelationships.add(r);
			element.addRelationship(r);
		}
		allRelatedElements = new ArrayList<>();
		allRelatedElements.addAll(relatedDescendentOfElements);
		allRelatedElements.addAll(relatedDescribesOfElements);
		allRelatedElements.addAll(relatedAmendsElements);
		allRelationships = new ArrayList<>();
		allRelationships.addAll(descendedOfRelationships);
		allRelationships.addAll(describesOfRelationships);
		allRelationships.addAll(amendsOfRelationships);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#toImmutableList()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testToImmutableList() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describesCollection = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		RelatedElementCollection descendantCollection = new RelatedElementCollection(element, RelationshipType.DESCENDANT_OF);
		RelatedElementCollection ammendsCollection = new RelatedElementCollection(element, RelationshipType.AMENDS);
		RelatedElementCollection allCollection = new RelatedElementCollection(element, null);
		assertListsSame(relatedDescribesOfElements, describesCollection.toImmutableList());
		assertListsSame(relatedDescendentOfElements, descendantCollection.toImmutableList());
		assertListsSame(relatedAmendsElements, ammendsCollection.toImmutableList());
		assertListsSame(allRelatedElements, allCollection.toImmutableList());
	}

	private void assertListsSame(List<SpdxElement> l1, List<SpdxElement> l2) {
		assertEquals(l1.size(), l2.size());
		for (Object item:l1) {
			assertTrue(l2.contains(item));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#size()}.
	 */
	public void testSize() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describesCollection = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		RelatedElementCollection descendantCollection = new RelatedElementCollection(element, RelationshipType.DESCENDANT_OF);
		RelatedElementCollection ammendsCollection = new RelatedElementCollection(element, RelationshipType.AMENDS);
		RelatedElementCollection allCollection = new RelatedElementCollection(element, null);
		assertEquals(relatedDescribesOfElements.size(), describesCollection.size());
		assertEquals(relatedDescendentOfElements.size(), descendantCollection.size());
		assertEquals(relatedAmendsElements.size(), ammendsCollection.size());
		assertEquals(allRelatedElements.size(), allCollection.size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#isEmpty()}.
	 */
	public void testIsEmpty() throws InvalidSPDXAnalysisException {
		SpdxElement ge = new GenericSpdxElement();
		RelatedElementCollection describeGe = new RelatedElementCollection(ge, RelationshipType.DESCRIBES);
		assertTrue(describeGe.isEmpty());
		SpdxElement relatedElement = new GenericSpdxElement();
		relatedElement.setName("related");
		Relationship relationship = ge.createRelationship(relatedElement, RelationshipType.DESCRIBES, "describes");
		ge.addRelationship(relationship);
		assertFalse(describeGe.isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#contains(java.lang.Object)}.
	 */
	public void testContains() throws InvalidSPDXAnalysisException {
		SpdxElement ge = new GenericSpdxElement();
		RelatedElementCollection describeGe = new RelatedElementCollection(ge, RelationshipType.DESCRIBES);
		SpdxElement relatedElement = new GenericSpdxElement();
		assertFalse(describeGe.contains(relatedElement));
		relatedElement.setName("related");
		Relationship relationship = ge.createRelationship(relatedElement, RelationshipType.DESCRIBES, "describes");
		ge.addRelationship(relationship);
		assertTrue(describeGe.contains(relatedElement));
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#iterator()}.
	 */
	public void testIterator() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describesCollection = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		RelatedElementCollection descendantCollection = new RelatedElementCollection(element, RelationshipType.DESCENDANT_OF);
		RelatedElementCollection ammendsCollection = new RelatedElementCollection(element, RelationshipType.AMENDS);
		RelatedElementCollection allCollection = new RelatedElementCollection(element, null);
		Iterator<SpdxElement> iter = describesCollection.iterator();
		while (iter.hasNext()) {
			assertTrue(relatedDescribesOfElements.contains(iter.next()));
		}
		iter = descendantCollection.iterator();
		while (iter.hasNext()) {
			assertTrue(relatedDescendentOfElements.contains(iter.next()));
		}
		iter = ammendsCollection.iterator();
		while (iter.hasNext()) {
			assertTrue(relatedAmendsElements.contains(iter.next()));
		}
		iter = allCollection.iterator();
		while (iter.hasNext()) {
			assertTrue(allRelatedElements.contains(iter.next()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#toArray()}.
	 */
	public void testToArray() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describesCollection = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		RelatedElementCollection descendantCollection = new RelatedElementCollection(element, RelationshipType.DESCENDANT_OF);
		RelatedElementCollection ammendsCollection = new RelatedElementCollection(element, RelationshipType.AMENDS);
		RelatedElementCollection allCollection = new RelatedElementCollection(element, null);
		assertTrue(Arrays.deepEquals(relatedDescribesOfElements.toArray(), describesCollection.toArray()));
		assertTrue(Arrays.deepEquals(relatedDescendentOfElements.toArray(), descendantCollection.toArray()));
		assertTrue(Arrays.deepEquals(relatedAmendsElements.toArray(), ammendsCollection.toArray()));
		assertTrue(Arrays.deepEquals(allRelatedElements.toArray(), allCollection.toArray()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#add(org.spdx.library.model.SpdxElement)}.
	 */
	public void testAdd() throws InvalidSPDXAnalysisException {
		SpdxElement ge = new GenericSpdxElement();
		RelatedElementCollection describeGe = new RelatedElementCollection(ge, RelationshipType.DESCRIBES);
		SpdxElement relatedElement = new GenericSpdxElement();
		relatedElement.setName("related");
		describeGe.add(relatedElement);
		assertTrue(describeGe.contains(relatedElement));
		Relationship[] allRelationships = ge.getRelationships().toArray(new Relationship[1]);
		assertEquals(RelationshipType.DESCRIBES, allRelationships[0].getRelationshipType().get());
		assertEquals(relatedElement, allRelationships[0].getRelatedSpdxElement().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#remove(java.lang.Object)}.
	 */
	public void testRemove() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describesCollection = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		assertListsSame(relatedDescribesOfElements, describesCollection.toImmutableList());
		describesCollection.remove(relatedDescribesOfElements.get(0));
		assertEquals(relatedDescribesOfElements.size()-1,describesCollection.size());
		for (int i = 1; i < relatedDescribesOfElements.size()-1; i++) {
			assertTrue(describesCollection.contains(relatedDescribesOfElements.get(i)));
		}
		assertFalse(describesCollection.contains(relatedDescribesOfElements.get(0)));
		assertEquals(allRelatedElements.size()-1, element.getRelationships().size());
		for (Relationship rel:element.getRelationships()) {
			if (rel.getRelationshipType().equals(RelationshipType.DESCRIBES)) {
				assertFalse(rel.getRelatedSpdxElement().equals(relatedDescribesOfElements.get(0)));
			}
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#containsAll(java.util.Collection)}.
	 */
	public void testContainsAll() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describesCollection = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		RelatedElementCollection descendantCollection = new RelatedElementCollection(element, RelationshipType.DESCENDANT_OF);
		RelatedElementCollection ammendsCollection = new RelatedElementCollection(element, RelationshipType.AMENDS);
		RelatedElementCollection allCollection = new RelatedElementCollection(element, null);
		assertTrue(describesCollection.containsAll(relatedDescribesOfElements));
		assertTrue(descendantCollection.containsAll(relatedDescendentOfElements));
		assertTrue(ammendsCollection.containsAll(relatedAmendsElements));
		assertTrue(allCollection.containsAll(allRelatedElements));
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#addAll(java.util.Collection)}.
	 */
	public void testAddAll() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describeGe = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		assertListsSame(relatedDescribesOfElements, describeGe.toImmutableList());
		SpdxElement relatedElement1 = new GenericSpdxElement();
		relatedElement1.setName("related1");
		SpdxElement relatedElement2 = new GenericSpdxElement();
		relatedElement2.setName("related2");
		List<SpdxElement> addedDescibes = new ArrayList<>(Arrays.asList(new SpdxElement[] {relatedElement1, relatedElement2}));
		List<SpdxElement> expected = new ArrayList<>(relatedDescribesOfElements);
		expected.addAll(addedDescibes);
		describeGe.addAll(addedDescibes);
		assertListsSame(expected, describeGe.toImmutableList());
		assertEquals(allRelatedElements.size()+addedDescibes.size(), element.getRelationships().size());
		describeGe.addAll(relatedDescribesOfElements);
		assertListsSame(expected, describeGe.toImmutableList());
		assertEquals(allRelatedElements.size()+addedDescibes.size(), element.getRelationships().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#removeAll(java.util.Collection)}.
	 */
	public void testRemoveAll() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describeGe = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		SpdxElement relatedElement1 = new GenericSpdxElement();
		relatedElement1.setName("related1");
		SpdxElement relatedElement2 = new GenericSpdxElement();
		relatedElement2.setName("related2");
		List<SpdxElement> addedDescibes = new ArrayList<>(Arrays.asList(new SpdxElement[] {relatedElement1, relatedElement2}));
		List<SpdxElement> expected = new ArrayList<>(relatedDescribesOfElements);
		expected.addAll(addedDescibes);
		describeGe.addAll(addedDescibes);
		assertListsSame(expected, describeGe.toImmutableList());
		assertEquals(allRelatedElements.size()+addedDescibes.size(), element.getRelationships().size());
		
		describeGe.removeAll(relatedDescribesOfElements);
		assertListsSame(addedDescibes, describeGe.toImmutableList());
		assertEquals(allRelatedElements.size()+addedDescibes.size()-relatedDescribesOfElements.size(), element.getRelationships().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#retainAll(java.util.Collection)}.
	 */
	public void testRetainAll() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describeGe = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		SpdxElement relatedElement1 = new GenericSpdxElement();
		relatedElement1.setName("related1");
		SpdxElement relatedElement2 = new GenericSpdxElement();
		relatedElement2.setName("related2");
		List<SpdxElement> addedDescibes = new ArrayList<>(Arrays.asList(new SpdxElement[] {relatedElement1, relatedElement2}));
		List<SpdxElement> expected = new ArrayList<>(relatedDescribesOfElements);
		expected.addAll(addedDescibes);
		describeGe.addAll(addedDescibes);
		assertListsSame(expected, describeGe.toImmutableList());
		assertEquals(allRelatedElements.size()+addedDescibes.size(), element.getRelationships().size());
		
		describeGe.retainAll(addedDescibes);
		assertListsSame(addedDescibes, describeGe.toImmutableList());
		assertEquals(allRelatedElements.size()+addedDescibes.size()-relatedDescribesOfElements.size(), element.getRelationships().size());

	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#clear()}.
	 */
	public void testClear() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describesCollection = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		RelatedElementCollection allCollection = new RelatedElementCollection(element, null);
		assertEquals(relatedDescribesOfElements.size(), describesCollection.size());
		assertEquals(allRelatedElements.size(), allCollection.size());
		describesCollection.clear();
		assertEquals(0, describesCollection.size());
		assertEquals(allRelatedElements.size()-relatedDescribesOfElements.size(), allCollection.size());
		allCollection.clear();
		assertEquals(0, allCollection.size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.RelatedElementCollection#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() throws InvalidSPDXAnalysisException {
		RelatedElementCollection describesCollection1 = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		RelatedElementCollection allCollection1 = new RelatedElementCollection(element, null);
		RelatedElementCollection describesCollection2 = new RelatedElementCollection(element, RelationshipType.DESCRIBES);
		RelatedElementCollection allCollection2 = new RelatedElementCollection(element, null);
		assertTrue(describesCollection1.equals(describesCollection2));
		assertTrue(describesCollection2.equals(describesCollection1));
		assertTrue(allCollection1.equals(allCollection2));
		assertTrue(allCollection2.equals(allCollection1));
		assertFalse(describesCollection1.equals(allCollection1));
		assertFalse(allCollection2.equals(describesCollection2));
	}

}
