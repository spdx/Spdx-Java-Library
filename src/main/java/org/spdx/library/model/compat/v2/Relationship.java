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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.Version;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.storage.IModelStore;

/**
 * A Relationship represents a relationship between two SpdxElements.
 * 
 * @author Gary O'Neall
 *
 */
public class Relationship extends ModelObject implements Comparable<Relationship> {
	
	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param objectUri
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param objectUri
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship(IModelStore modelStore, String documentUri, String id, ModelCopyManager copyManager,
			boolean create)	throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstantsCompatV2.CLASS_RELATIONSHIP;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = new ArrayList<>();
		Optional<SpdxElement> relatedSpdxElement;
		try {
			relatedSpdxElement = getRelatedSpdxElement();
			if (!relatedSpdxElement.isPresent()) {
				retval.add("Missing related SPDX element");
			} else {
				retval.addAll(relatedSpdxElement.get().verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting related SPDX element for relationship: "+e.getMessage());
		}
		try {
			RelationshipType relationshipType = getRelationshipType();
			if (RelationshipType.MISSING.equals(relationshipType)) {
				retval.add("Missing relationship type");
			}
			if (Version.versionLessThan(specVersion, Version.TWO_POINT_THREE_VERSION) &&
					(RelationshipType.REQUIREMENT_DESCRIPTION_FOR.equals(relationshipType) ||
					RelationshipType.SPECIFICATION_FOR.equals(relationshipType))) {
				retval.add(relationshipType.toString()+
					" is not supported in SPDX spec versions less than "+
					Version.TWO_POINT_THREE_VERSION);
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
	public RelationshipType getRelationshipType() throws InvalidSPDXAnalysisException {
		Optional<?> retval = getEnumPropertyValue(SpdxConstantsCompatV2.PROP_RELATIONSHIP_TYPE);
		if (retval.isPresent()) {
			if (!(retval.get() instanceof RelationshipType)) {
				throw new SpdxInvalidTypeException("Invalid type for relationship type individual value: "+retval.get().toString());
			}
			return (RelationshipType)retval.get();
		} else {
			return RelationshipType.MISSING;
		}
	}
	
	/**
	 * Set the relationship type
	 * @param type
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship setRelationshipType(RelationshipType type) throws InvalidSPDXAnalysisException {
		if (RelationshipType.MISSING.equals(type)) {
			throw new InvalidSPDXAnalysisException("Can not set required relationshipType to MISSING");
		}
		if (strict && type == null) {
			throw new InvalidSPDXAnalysisException("Can not set required relationshipType to null");
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_RELATIONSHIP_TYPE, type);
		return this;
	}
	
	/**
	 * @return the comment
	 */
	public Optional<String> getComment() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstantsCompatV2.RDFS_PROP_COMMENT);
	}
	
	/**
	 * @param comment the comment to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Relationship setComment(String comment) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.RDFS_PROP_COMMENT, comment);
		return this;
	}
	
	/**
	 * @return the relatedSpdxElement
	 */
	public Optional<SpdxElement> getRelatedSpdxElement() throws InvalidSPDXAnalysisException {
		return getElementPropertyValue(SpdxConstantsCompatV2.PROP_RELATED_SPDX_ELEMENT);
	}
	
	/**
	 * @param relatedSpdxElement the relatedSpdxElement to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Relationship setRelatedSpdxElement(SpdxElement relatedSpdxElement) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.PROP_RELATED_SPDX_ELEMENT, relatedSpdxElement);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Relationship o) {
		RelationshipType myRelationshipType;
		try {
			myRelationshipType = getRelationshipType();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting my relationship type",e);
			myRelationshipType = RelationshipType.MISSING;
		}
		RelationshipType oRelationshipType;
		try {
			oRelationshipType = o.getRelationshipType();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare relationship type",e);
			oRelationshipType = RelationshipType.MISSING;
		}
		int retval = myRelationshipType.toString().compareTo(oRelationshipType.toString());
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
	
	@Override
	public String toString() {
		try {
			Optional<SpdxElement> relatedElement = getRelatedSpdxElement();
			StringBuilder sb = new StringBuilder();
			sb.append(getRelationshipType().toString());
			sb.append(" ");
			if (relatedElement.isPresent()) {
				sb.append(relatedElement.get().toString());
			} else {
				sb.append("[Missing related element]");
			}
			return sb.toString();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error in toString: ",e);
			return "Error: "+e.getMessage();
		}
	}
}
