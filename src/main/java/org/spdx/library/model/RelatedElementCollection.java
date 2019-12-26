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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;

/**
 * Collection of SPDX elements related to an SpdxElement
 * 
 * @author Gary O'Neall
 *
 */
/**
 * @author gary
 *
 */
public class RelatedElementCollection implements Collection<SpdxElement> {
	
	static final Logger logger = LoggerFactory.getLogger(RelatedElementCollection.class);
	
	ModelCollection<Relationship> relationshipCollection;
	private RelationshipType relationshipTypeFilter;

	private SpdxElement owningElement;

	/**
	 * @param modelStore model store storing the information
	 * @param documentUri document URI for the elements
	 * @param id ID of the SpdxElement containing the relationship
	 * @param relationshipTypeFilter relationship type to filter the results collection on - if null, do not filter
	 * @throws InvalidSPDXAnalysisException 
	 */
	public RelatedElementCollection(SpdxElement owningElement,
			@Nullable RelationshipType relationshipTypeFilter) throws InvalidSPDXAnalysisException {
		this.owningElement = owningElement;
		this.relationshipCollection = new ModelCollection<Relationship>(owningElement.getModelStore(),
				owningElement.getDocumentUri(), owningElement.getId(), SpdxConstants.PROP_RELATIONSHIP, Relationship.class);
		this.relationshipTypeFilter = relationshipTypeFilter;
	}
	
	public List<SpdxElement> toImmutableList() {
		List<SpdxElement> retval = new ArrayList<>();
		relationshipCollection.toImmutableList().forEach(item -> {
			if (item instanceof Relationship) {
				Relationship relationship = (Relationship)item;
				try {
					Optional<RelationshipType> relationshipType = relationship.getRelationshipType();
					if (Objects.isNull(this.relationshipTypeFilter) || 
							(relationshipType.isPresent() && this.relationshipTypeFilter.equals(relationshipType.get()))) {
						Optional<SpdxElement> relatedElement = relationship.getRelatedSpdxElement();
						if (relatedElement.isPresent()) {
							retval.add(relatedElement.get());
						}
					}
				} catch (InvalidSPDXAnalysisException e) {
					logger.warn("error getting relationship type - skipping relationship",e);
				}
			}
		});
		return Collections.unmodifiableList(retval);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return toImmutableList().size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return toImmutableList().isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return toImmutableList().contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<SpdxElement> iterator() {
		return toImmutableList().iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public SpdxElement[] toArray() {
		List<SpdxElement> list = toImmutableList();
		return list.toArray(new SpdxElement[list.size()]);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return toImmutableList().toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(SpdxElement e) {
		if (Objects.isNull(this.relationshipTypeFilter)) {
			logger.error("Ambiguous relationship type - can not add element");
			throw new RuntimeException("Can not add element to RelatedElementCollection due to ambiguous relationship type.  Add a relationshipTypeFilter to resolve.");
		}
		if (Objects.isNull(e) || contains(e)) {
			return false;
		}
		try {
			Relationship relationship = owningElement.createRelationship(e, relationshipTypeFilter, "");
			return owningElement.addRelationship(relationship);
		} catch (InvalidSPDXAnalysisException e1) {
			logger.error("Error adding relationship",e1);
			throw new RuntimeException(e1);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		if (o instanceof Relationship) {
			return this.relationshipCollection.remove(o);
		} else if (o instanceof SpdxElement) {
			if (Objects.isNull(this.relationshipTypeFilter)) {
				logger.error("Ambiguous relationship type - can not add element");
				throw new RuntimeException("Can not remove element from RelatedElementCollection due to ambiguous relationship type.  Add a relationshipTypeFilter to resolve.");
			}
			List<Object> relationships = this.relationshipCollection.toImmutableList();
			for (Object rel:relationships) {
				if (rel instanceof Relationship) {
					Relationship relationship = (Relationship)rel;
					try {
						if (relationship.getRelatedSpdxElement().isPresent() && 
								relationship.getRelatedSpdxElement().get().equals(o) &&
								relationship.getRelationshipType().isPresent() && 
								relationship.getRelationshipType().get().equals(relationshipTypeFilter)) {
							return relationshipCollection.remove(relationship);
						}
					} catch (InvalidSPDXAnalysisException e) {
						logger.error("Error getting relationship properties - skipping removal of element",e);
					}
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return toImmutableList().containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends SpdxElement> c) {
		boolean modified = false;
		for (SpdxElement element:c) {
			if (add(element)) {
				modified = true;
			}
		}
		return modified;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object element:c) {
			if (remove(element)) {
				modified = true;
			}
		}
		return modified;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		List<SpdxElement> existingElements = toImmutableList();
		boolean modified = false;
		for (SpdxElement existingElement:existingElements) {
			if (!c.contains(existingElement)) {
				if (remove(existingElement)) {
					modified = true;
				}
			}
		}
		return modified;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		if (Objects.isNull(relationshipTypeFilter)) {
			relationshipCollection.clear();
		} else {
			List<SpdxElement> existingElements = toImmutableList();
			for (SpdxElement existingElement:existingElements) {
				remove(existingElement);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RelatedElementCollection)) {
			return false;
		}
		RelatedElementCollection compare = (RelatedElementCollection)o;
		return Objects.equals(this.owningElement, compare.getOwningElement()) && 
				Objects.equals(relationshipTypeFilter, compare.getRelationshipTypeFilter());
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#hashCode()
	 */
	@Override
	public int hashCode() {
		if (Objects.isNull(relationshipTypeFilter)) {
			return 33 ^ this.owningElement.hashCode();
		} else {
			return 33 ^ this.owningElement.hashCode() ^ this.relationshipTypeFilter.hashCode();
		}
	}

	/**
	 * @return the relationshipCollection
	 */
	public ModelCollection<Relationship> getRelationshipCollection() {
		return relationshipCollection;
	}

	/**
	 * @return the relationshipTypeFilter
	 */
	public RelationshipType getRelationshipTypeFilter() {
		return relationshipTypeFilter;
	}

	/**
	 * @return the owningElement
	 */
	public SpdxElement getOwningElement() {
		return owningElement;
	}
}
