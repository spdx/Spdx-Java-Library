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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.compat.v2.CompatibleModelStoreWrapper;

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
	private String relatedElementTypeFilter;
	/**
	 * Keeps track of any created relationships so we can delete them when removed
	 */
	private Set<String> createdRelationshipIds = new HashSet<>();

	private SpdxElement owningElement;
	
	/**
	 * @param owningElement
	 * @param relationshipTypeFilter relationship type to filter the results
	 *                               collection on - if null, do not filter
	 * @throws InvalidSPDXAnalysisException
	 */
	public RelatedElementCollection(SpdxElement owningElement,
			@Nullable RelationshipType relationshipTypeFilter) throws InvalidSPDXAnalysisException {
		this(owningElement, relationshipTypeFilter, null);
	}

	/**
	 * @param owningElement
	 * @param relationshipTypeFilter relationship type to filter the results
	 *                               collection on - if null, do not filter
	 * @param relatedElementTypeFilter filter for only related element types - if null, do not filter
	 * @throws InvalidSPDXAnalysisException
	 */
	public RelatedElementCollection(SpdxElement owningElement,
			@Nullable RelationshipType relationshipTypeFilter,
			@Nullable String relatedElementTypeFilter) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(owningElement, "Owning element can not be null");
		this.owningElement = owningElement;
		this.relationshipCollection = new ModelCollection<Relationship>(owningElement.getModelStore(),
				owningElement.getDocumentUri(), owningElement.getId(), SpdxConstantsCompatV2.PROP_RELATIONSHIP, 
				owningElement.getCopyManager(), Relationship.class);
		this.relationshipTypeFilter = relationshipTypeFilter;
		this.relatedElementTypeFilter = relatedElementTypeFilter;
	}
	
	public List<SpdxElement> toImmutableList() {
		List<SpdxElement> retval = new ArrayList<>();
		for (Object item:relationshipCollection.toImmutableList()) {
			if (item instanceof Relationship) {
				Relationship relationship = (Relationship)item;
				try {
					RelationshipType relationshipType = relationship.getRelationshipType();
					if (Objects.isNull(this.relationshipTypeFilter) || 
							(this.relationshipTypeFilter.equals(relationshipType))) {
						Optional<SpdxElement> relatedElement = relationship.getRelatedSpdxElement();
						if (relatedElement.isPresent()) {
							if (Objects.isNull(this.relatedElementTypeFilter) ||
									this.relatedElementTypeFilter.equals(relatedElement.get().getType())) {
								retval.add(relatedElement.get());
							}
						}
					}
				} catch (InvalidSPDXAnalysisException e) {
					logger.warn("error getting relationship type - skipping relationship",e);
				}
			}
		}
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
		if (!(o instanceof SpdxElement)) {
			return false;
		}
		String elementId = ((SpdxElement)o).getId();
		Iterator<Object> iter = relationshipCollection.iterator();
		while (iter.hasNext()) {
			Object item = iter.next();
			if (item instanceof Relationship) {
				Relationship relationship = (Relationship)item;
				try {
					RelationshipType relationshipType = relationship.getRelationshipType();
					if (Objects.isNull(this.relationshipTypeFilter) || 
							(this.relationshipTypeFilter.equals(relationshipType))) {
						Optional<SpdxElement> relatedElement = relationship.getRelatedSpdxElement();
						if (relatedElement.isPresent()) {
							if (elementId.equals(relatedElement.get().getId())) {
								return true;
							}
						}
					}
				} catch (InvalidSPDXAnalysisException e) {
					logger.warn("error getting relationship type - skipping relationship",e);
				}
			}
		}
		return false;
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
			IModelStoreLock lock = owningElement.getModelStore()
					.enterCriticalSection(owningElement.getDocumentUri(), false);
			try {
				Relationship relationship = owningElement.createRelationship(e, relationshipTypeFilter, null);
				createdRelationshipIds.add(relationship.getId());
				return owningElement.addRelationship(relationship);
			} finally {
				owningElement.getModelStore().leaveCriticalSection(lock);
			}
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
					    Optional<SpdxElement> relatedElement = relationship.getRelatedSpdxElement();
						if (relatedElement.isPresent() && 
						        relatedElement.get().equals(o) &&
								relationship.getRelationshipType().equals(relationshipTypeFilter)) {
							IModelStore modelStore = relationship.getModelStore();
							String documentUri = relationship.getDocumentUri();
							final IModelStoreLock lock = modelStore.enterCriticalSection(false);
							try {
								if (relationshipCollection.remove(relationship)) {
									try {
										if (createdRelationshipIds.contains(relationship.getId())) {
											createdRelationshipIds.remove(relationship.getId());
											modelStore.delete(CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, relationship.getId(), modelStore));
										}
									} catch (SpdxIdInUseException ex) {
										// This is possible if the relationship is in use
										// outside of the RelatedElementCollection - just ignore
										// the exception
									}
									return true;
								} else {
									return false;
								}
							} finally {
								modelStore.leaveCriticalSection(lock);
							}
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
		if (Objects.isNull(relationshipTypeFilter) && Objects.isNull(relatedElementTypeFilter)) {
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
				Objects.equals(relationshipTypeFilter, compare.getRelationshipTypeFilter()) &&
				Objects.equals(relatedElementTypeFilter, compare.getRelatedElementTypeFilter());
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#hashCode()
	 */
	@Override
	public int hashCode() {
		int retval = 33 ^ this.owningElement.hashCode();
		if (Objects.nonNull(relationshipTypeFilter)) {
			retval = retval ^ this.relationshipTypeFilter.hashCode();
		}
		if (Objects.nonNull(relatedElementTypeFilter)) {
			retval = retval ^ this.relatedElementTypeFilter.hashCode();
		}
		return retval;
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
	 * @return the relatedElementTypeFilter
	 */
	public String getRelatedElementTypeFilter() {
		return relatedElementTypeFilter;
	}

	/**
	 * @return the owningElement
	 */
	public SpdxElement getOwningElement() {
		return owningElement;
	}
}
