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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.storage.IModelStore;

/**
 * A Relationship represents a relationship between two SpdxElements.
 * 
 * @author Gary O'Neall
 *
 */
public class Relationship extends ModelObject implements Comparable<Relationship> {
	
	/**
	 * Source of the relationship. This is not stored since the spdxElement should contain a reference to this
	 */
	private SpdxElement owningSpdxElement = null;

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_RELATIONSHIP;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<>();
		Optional<SpdxElement> relatedSpdxElement;
		try {
			relatedSpdxElement = getRelatedSpdxElement();
			if (!relatedSpdxElement.isPresent()) {
				retval.add("Missing related SPDX element");
			} else {
				retval.addAll(relatedSpdxElement.get().verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting related SPDX element for relationship: "+e.getMessage());
		}
		try {
			if (!getRelationshipType().isPresent()) {
				retval.add("Missing relationship type");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting relationship type: "+e.getMessage());
		}
		return retval;
	}
	
	/**
	 * @return the relationshipType
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public Optional<RelationshipType> getRelationshipType() throws InvalidSPDXAnalysisException {
		Optional<?> retval = getEnumPropertyValue(SpdxConstants.PROP_RELATIONSHIP_TYPE);
		if (retval.isPresent() && !(retval.get() instanceof RelationshipType)) {
			throw new SpdxInvalidTypeException("Invalid type for relationship type individual value: "+retval.get().toString());
		}
		return (Optional<RelationshipType>)retval;
	}
	
	/**
	 * Set the relationship type
	 * @param type
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setRelationshipType(RelationshipType type) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(type);
		setPropertyValue(SpdxConstants.PROP_RELATIONSHIP_TYPE, type);
	}
	
	/**
	 * @return the comment
	 */
	public Optional<String> getComment() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.RDFS_PROP_COMMENT);
	}
	
	/**
	 * @param comment the comment to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.RDFS_PROP_COMMENT, comment);
	}
	
	/**
	 * @return the relatedSpdxElement
	 */
	@SuppressWarnings("unchecked")
	public Optional<SpdxElement> getRelatedSpdxElement() throws InvalidSPDXAnalysisException {
		return (Optional<SpdxElement>)(Optional<?>)getObjectPropertyValue(SpdxConstants.PROP_RELATED_SPDX_ELEMENT);
	}
	
	/**
	 * @param relatedSpdxElement the relatedSpdxElement to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setRelatedSpdxElement(SpdxElement relatedSpdxElement) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_RELATED_SPDX_ELEMENT, relatedSpdxElement);
	}
	
	/**
	 * @return the SPDX element which owns this relationship
	 */
	public Optional<SpdxElement> getOwningSpdxElement() {
		return Optional.ofNullable(owningSpdxElement);
	}
	
	/**
	 * @param element the SPDX element which owns this relationship
	 */
	public void setOwningSpdxElement(SpdxElement element) {
		this.owningSpdxElement = element;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Relationship o) {
		Optional<RelationshipType> myRelationshipType;
		try {
			myRelationshipType = getRelationshipType();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting my relationship type",e);
			myRelationshipType = Optional.empty();
		}
		Optional<RelationshipType> oRelationshipType;
		try {
			oRelationshipType = o.getRelationshipType();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare relationship type",e);
			oRelationshipType = Optional.empty();
		}
		if (!oRelationshipType.isPresent()) {
			if (myRelationshipType.isPresent()) {
				return 1;
			}
		}
		if (!myRelationshipType.isPresent()) {
			return -1;
		}
		int retval = myRelationshipType.get().toString().compareTo(oRelationshipType.get().toString());
		if (retval != 0) {
			return retval;
		}
		Optional<SpdxElement> compareRelatedElement;
		try {
			compareRelatedElement = o.getRelatedSpdxElement();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare related element",e);
			compareRelatedElement = Optional.empty();
		}
		Optional<SpdxElement> myRelatedElement;
		try {
			myRelatedElement = getRelatedSpdxElement();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare related element",e);
			myRelatedElement = Optional.empty();
		}
		if (!compareRelatedElement.isPresent()) {
			if (myRelatedElement.isPresent()) {
				return 1;
			}
		}
		if (!myRelatedElement.isPresent()) {
			return -1;
		}
		retval = myRelatedElement.get().getId().compareTo(compareRelatedElement.get().getId());
		if (retval != 0) {
			return retval;
		}
		Optional<String> compComment;
		try {
			compComment = o.getComment();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting my comment",e);
			compComment = Optional.empty();
		}
		Optional<String> myComment;
		try {
			myComment = getComment();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare comment",e);
			myComment = Optional.empty();
		}
		if (!compComment.isPresent()) {
			if (myComment.isPresent()) {
				return 1;
			}
		}
		if (!myComment.isPresent()) {
			return -1;
		}
		return myComment.get().compareTo(compComment.get());
	}
}
