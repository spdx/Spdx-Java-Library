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
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;

/**
 * An SpdxElement is any thing described in SPDX, either a document or an SpdxItem. 
 * SpdxElements can be related to other SpdxElements.
 * 
 * If a subproperty is used for the name property name, getNamePropertyName should be overridden.
 * 
 * @author Gary O'Neall
 */
public abstract class SpdxElement extends ModelObject {

	static final Logger logger = LoggerFactory.getLogger(SpdxElement.class);
	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxElement() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxElement(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxElement(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<>();
		try {
			retval.addAll(verifyCollection(getAnnotations(), "Annotation Error: "));
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting annotations: "+e.getMessage());
		}
		try {
			retval.addAll(verifyCollection(getRelationships(), "Relationship error: "));
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting relationships: "+e.getMessage());
		}
		addNameToWarnings(retval);
		return retval;
	}
	
	/**
	 * Add the name of the element to all strings in the list
	 * @param warnings
	 */
	protected void addNameToWarnings(List<String> warnings) {
		if (warnings == null) {
			return;
		}
		String localName = "[UNKNOWN]";
		try {
			Optional<String> name = getName();
			if (name.isPresent()) {
				localName = name.get();
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting name",e);
		}
		for (int i = 0; i < warnings.size(); i++) {
			warnings.set(i, warnings.get(i)+" in "+localName);
		}
	}
	
	/**
	 * @return Annotations
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Annotation> getAnnotations() throws InvalidSPDXAnalysisException {
		return (Collection<Annotation>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_ANNOTATION);
	}
	
	/**
	 * Add an annotation
	 * @param annotation
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public boolean addAnnotation(Annotation annotation) throws InvalidSPDXAnalysisException {
		return ((Collection<Annotation>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_ANNOTATION)).add(annotation);
	}
	
	/**
	 * Remove an annotation
	 * @param annotation
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public boolean removeAnnotation(Annotation annotation) throws InvalidSPDXAnalysisException {
		return ((Collection<Annotation>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_ANNOTATION)).remove(annotation);
	}
	
	/**
	 * @return Relationships
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Relationship> getRelationships() throws InvalidSPDXAnalysisException {
		return (Collection<Relationship>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_RELATIONSHIP);
	}
	
	/**
	 * Add a relationship
	 * @param relationship
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public boolean addRelationship(Relationship relationship) throws InvalidSPDXAnalysisException {
		return ((Collection<Relationship>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_RELATIONSHIP)).add(relationship);
	}
	
	/**
	 * Remove a relationship
	 * @param relationship
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public boolean removeRelationship(Relationship relationship) throws InvalidSPDXAnalysisException {
		return ((Collection<Relationship>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_RELATIONSHIP)).remove(relationship);
	}
	
	/**
	 * @return the comment
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<String> getComment() throws InvalidSPDXAnalysisException {
		return this.getStringPropertyValue(SpdxConstants.RDFS_PROP_COMMENT);
	}
	
	/**
	 * Sets the comment
	 * @param comment
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		this.setPropertyValue(SpdxConstants.RDFS_PROP_COMMENT, comment);
	}
	
	
	/**
	 * @return the property name used for the Name property.  Override this function if using a subproperty of SPDX Name
	 */
	protected String getNamePropertyName() {
		return SpdxConstants.PROP_NAME;
	}
	
	/**
	 * @return the name
	 */
	public Optional<String> getName() throws InvalidSPDXAnalysisException {
		return this.getStringPropertyValue(getNamePropertyName());
	}
	
	/**
	 * Set the name
	 * @param name
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setName(String name) throws InvalidSPDXAnalysisException {
		this.setPropertyValue(getNamePropertyName(), name);
	}
	
	/**
	 * @param relatedSpdxElement The SPDX Element that is related
	 * @param relationshipType Type of relationship - See the specification for a description of the types
	 * @param comment optional comment for the relationship
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public Relationship createRelationship(SpdxElement relatedElement, 
			RelationshipType relationshipType, @Nullable String comment) throws InvalidSPDXAnalysisException {
		return super.createRelationship(this, relatedElement, relationshipType, comment);
	}
}
