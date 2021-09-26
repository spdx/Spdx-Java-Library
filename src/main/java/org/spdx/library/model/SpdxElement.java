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
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

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
	
	private Collection<Annotation> annotations = null;
	private Collection<Relationship> relationships = null;
	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxElement() throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore().getNextId(IdType.Anonymous, DefaultModelStore.getDefaultDocumentUri()));
		
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxElement(String id) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), id,
				DefaultModelStore.getDefaultCopyManager(), true);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxElement(IModelStore modelStore, String documentUri, String id, ModelCopyManager copyManager,
			boolean create)	throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
		// we can not create the annotations and relationships until referenced since ExternalSpdxElement can not create them
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(List<String> verifiedElementIds) {
		List<String> retval = new ArrayList<>();
		if (verifiedElementIds.contains(this.getId())) {
			return retval;
		}
		verifiedElementIds.add(this.getId());
		// verify ID format
		IdType idType = this.getModelStore().getIdType(this.getId());
		if (IdType.SpdxId.equals(idType)) {
			if (!SpdxVerificationHelper.verifySpdxId(this.getId())) {
				retval.add("Invalid SPDX ID: "+this.getId()+".  Must match the pattern "+SpdxConstants.SPDX_ELEMENT_REF_PATTERN);
			}
		} else if (!IdType.Anonymous.equals(idType)) {
			retval.add("Invalid ID for SPDX Element: "+this.getId()+".  Must be either a valid SPDX ID or Anonymous.");
		}
		try {
			retval.addAll(verifyCollection(getAnnotations(), "Annotation Error: ", verifiedElementIds));
		} catch (InvalidSPDXAnalysisException e1) {
			retval.add("Error getting annotation: "+e1.getMessage());
		}
		try {
			retval.addAll(verifyCollection(getRelationships(), "Relationship error: ", verifiedElementIds));
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting relationships: "+e.getMessage());
		}
		addNameToWarnings(retval);
		return retval;
	}
	
	@SuppressWarnings("unchecked")
	private synchronized void checkCreateAnnotations() throws InvalidSPDXAnalysisException {
		if (Objects.isNull(this.annotations)) {
			this.annotations = (Collection<Annotation>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstants.PROP_ANNOTATION, Annotation.class);
		}
	}
	
	@SuppressWarnings("unchecked")
	private synchronized void checkCreateRelationships() throws InvalidSPDXAnalysisException {
		if (Objects.isNull(this.relationships)) {
			this.relationships = (Collection<Relationship>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstants.PROP_RELATIONSHIP, Relationship.class);
		}
	}
	
	/**
	 * Add the name of the element to all strings in the list
	 * @return the same last after being modified (Note: a new list is not created - this modifies the warnings list)
	 * @param warnings
	 */
	protected List<String> addNameToWarnings(List<String> warnings) {
		if (warnings == null) {
			return new ArrayList<>();
		}
		if (warnings.isEmpty()) {
		    return warnings;
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
		return warnings;
	}
	
	/**
	 * @return Annotations
	 * @throws InvalidSPDXAnalysisException
	 */
	public Collection<Annotation> getAnnotations() throws InvalidSPDXAnalysisException {
		checkCreateAnnotations();
		return annotations;
	}
	
	/**
	 * Clears and resets the annotations collection to the parameter
	 * @param annotations
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxElement setAnnotations(Collection<Annotation> annotations) throws InvalidSPDXAnalysisException {
		checkCreateAnnotations();
		Objects.requireNonNull(annotations, "Annotations can not be null");
		this.annotations.clear();
		this.annotations.addAll(annotations);
		return this;
	}
	
	/**
	 * Add an annotation
	 * @param annotation
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean addAnnotation(Annotation annotation) throws InvalidSPDXAnalysisException {
		checkCreateAnnotations();
		return annotations.add(annotation);
	}
	
	/**
	 * Remove an annotation
	 * @param annotation
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean removeAnnotation(Annotation annotation) throws InvalidSPDXAnalysisException {
		checkCreateAnnotations();
		return annotations.remove(annotation);
	}
	
	/**
	 * @return Relationships
	 * @throws InvalidSPDXAnalysisException
	 */
	public Collection<Relationship> getRelationships() throws InvalidSPDXAnalysisException {
		checkCreateRelationships();
		return relationships;
	}
	
	/**
	 * clear and reset the relationships to the paramater relationship
	 * @param relationships
	 * @return this to chain sets
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxElement setRelationships(Collection<Relationship> relationships) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(relationships, "Relationships can not be null");
		checkCreateRelationships();
		this.relationships.clear();
		this.relationships.addAll(relationships);
		return this;
	}
	
	/**
	 * Add a relationship
	 * @param relationship
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean addRelationship(Relationship relationship) throws InvalidSPDXAnalysisException {
		checkCreateRelationships();
		return relationships.add(relationship);
	}
	
	/**
	 * Remove a relationship
	 * @param relationship
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean removeRelationship(Relationship relationship) throws InvalidSPDXAnalysisException {
		checkCreateRelationships();
		return relationships.remove(relationship);
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
	 * @return this so that you can chain setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxElement setName(String name) throws InvalidSPDXAnalysisException {
		this.setPropertyValue(getNamePropertyName(), name);
		return this;
	}
}
