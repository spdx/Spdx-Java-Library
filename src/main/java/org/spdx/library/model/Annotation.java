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
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.storage.IModelStore;

/**
 * @author gary
 *
 */
public class Annotation extends ModelObject {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public Annotation() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public Annotation(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public Annotation(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_ANNOTATION;
	}

	public AnnotationType getAnnotationType() throws InvalidSPDXAnalysisException {
		Optional<Enum<?>> retval = getEnumPropertyValue(SpdxConstants.PROP_ANNOTATION_TYPE);
		if (retval.isPresent() && !(retval.get() instanceof AnnotationType)) {
			throw new SpdxInvalidTypeException("Invalid enum type for "+retval.get().toString());
		}
		if (retval.isPresent()) {
			if (!(retval.get() instanceof AnnotationType)) {
				throw new SpdxInvalidTypeException("Invalid enum type for "+retval.get().toString());
			}
			return (AnnotationType)retval.get();
		} else {
			logger.warn("Missing required annotation type, returning type missing for "+getId());
			return AnnotationType.MISSING;
		}
	}
	
	public void setAnnotationType(AnnotationType type) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (type == null) {
				throw new InvalidSPDXAnalysisException("Annotation type is required - null value for type is not accepted");
			}
			if (AnnotationType.MISSING.equals(type)) {
				throw new InvalidSPDXAnalysisException("Can not set value to MISSING for annotation type.  This is reserved for when the value is not present in the store.");
			}
		}
		setPropertyValue(SpdxConstants.PROP_ANNOTATION_TYPE, type);
	}
	
	/**
	 * @return the annotator
	 * @throws InvalidSPDXAnalysisException 
	 */
	public String getAnnotator() throws InvalidSPDXAnalysisException {
		Optional<String> retval = getStringPropertyValue(SpdxConstants.PROP_ANNOTATOR);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			logger.warn("Missing required annotator for "+getId());
			return "";
		}
	}
	
	/**
	 * @param annotator
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setAnnotator(String annotator) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (annotator == null || annotator.isEmpty()) {
				throw new InvalidSPDXAnalysisException("Annotator is required - can not be null or empty");
			}
			String verify = SpdxVerificationHelper.verifyAnnotator(annotator);
			if (verify != null && !verify.isEmpty()) {
				throw new InvalidSPDXAnalysisException(verify);
			}
		}
		setPropertyValue(SpdxConstants.PROP_ANNOTATOR, annotator);
	}
	
	/**
	 * @return the comment
	 */
	public String getComment() throws InvalidSPDXAnalysisException {
		Optional<String> retval = getStringPropertyValue(SpdxConstants.RDFS_PROP_COMMENT);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			logger.warn("Missing required comment for "+getId());
			return "";
		}
	}
	
	/**
	 * Set the comment
	 * @param comment
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (comment == null || comment.isEmpty()) {
				throw new InvalidSPDXAnalysisException("Comment is required - can not be null or empty");
			}
		}
		setPropertyValue(SpdxConstants.RDFS_PROP_COMMENT, comment);
	}
	
	/**
	 * @return the date
	 */
	public String getAnnotationDate() throws InvalidSPDXAnalysisException {
		Optional<String> retval = getStringPropertyValue(SpdxConstants.PROP_ANNOTATION_DATE);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			logger.warn("Missing required annotation date for "+getId());
			return "";
		}
	}
	
	/**
	 * Set the annotation date
	 * @param date
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setAnnotationDate(String date) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (date == null || date.isEmpty()) {
				throw new InvalidSPDXAnalysisException("Date is required - can not be null or empty");
			}
			String dateVerify = SpdxVerificationHelper.verifyDate(date);
			if (dateVerify != null && !dateVerify.isEmpty()) {
				throw new InvalidSPDXAnalysisException(dateVerify);
			}
		}
		setPropertyValue(SpdxConstants.PROP_ANNOTATION_DATE, date);
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.model.IRdfModel#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<String>();
		try {
			if (AnnotationType.MISSING.equals(getAnnotationType())) {
				retval.add("Missing annotationtype for Annotation");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting annotationtype for Annotation: "+e.getMessage());
		}
		try {
			String annotator = getAnnotator();
			String v = SpdxVerificationHelper.verifyAnnotator(annotator);
			if (v != null && !v.isEmpty()) {
				retval.add(v + ":" + annotator);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting annotator for Annotation: "+e.getMessage());
		}
		try {
			if (getComment().isEmpty()) {
				retval.add("Missing required comment for Annotation");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting comment for Annotation: "+e.getMessage());
		}
		try {
			String date = getAnnotationDate();
			if (date.isEmpty()) {
				retval.add("Missing required date for Annotation");
			} else {
				String dateVerify = SpdxVerificationHelper.verifyDate(date);
				if (dateVerify != null && !dateVerify.isEmpty()) {
					retval.add(dateVerify);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting date for Annotation: "+e.getMessage());
		}
		
		return retval;
	}
}
