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

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
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
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public Annotation(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_ANNOTATION;
	}

	@SuppressWarnings("unchecked")
	public Optional<AnnotationType> getAnnotationType() throws InvalidSPDXAnalysisException {
		return (Optional<AnnotationType>)getEnumPropertyValue(SpdxConstants.PROP_ANNOTATION_TYPE, AnnotationType.class);
	}
	
	public void setAnnotationType(AnnotationType type) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_ANNOTATION_TYPE, type);
	}
	
	/**
	 * @return the annotator
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<String> getAnnotator() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_ANNOTATOR);
	}
	
	public void setAnnotator(String annotator) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_ANNOTATOR, annotator);
	}
	
	/**
	 * @return the comment
	 */
	public Optional<String> getComment() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.RDFS_PROP_COMMENT);
	}
	
	/**
	 * Set the comment
	 * @param comment
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.RDFS_PROP_COMMENT, comment);
	}
	
	/**
	 * @return the date
	 */
	public Optional<String> getAnnotationDate() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_ANNOTATION_DATE);
	}
	
	/**
	 * Set the annotation date
	 * @param date
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setAnnotationDate(String date) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_ANNOTATION_DATE, date);
	}

	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.model.IRdfModel#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<String>();
		try {
			if (!getAnnotationType().isPresent()) {
				retval.add("Missing annotationtype for Annotation");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting annotationtype for Annotation: "+e.getMessage());
		}
		try {
			Optional<String> annotator;
			annotator = getAnnotator();
			if (!annotator.isPresent()) {
				retval.add("Missing annotator for Annotation");
			} else {
				String v = SpdxVerificationHelper.verifyAnnotator(annotator.get());
				if (v != null && !v.isEmpty()) {
					retval.add(v + ":" + annotator.get());
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting annotator for Annotation: "+e.getMessage());
		}
		try {
			if (!getComment().isPresent()) {
				retval.add("Missing required comment for Annotation");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting comment for Annotation: "+e.getMessage());
		}
		try {
			Optional<String> date;
			date = getAnnotationDate();
			if (!date.isPresent()) {
				retval.add("Missing required date for Annotation");
			} else {
				String dateVerify = SpdxVerificationHelper.verifyDate(date.get());
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
