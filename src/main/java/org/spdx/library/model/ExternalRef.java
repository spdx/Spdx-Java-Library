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
import org.spdx.library.model.enumerations.ReferenceCategory;
import org.spdx.storage.IModelStore;

/**
 * An External Reference allows a Package to reference an external source of
 * additional information, metadata, enumerations, asset identifiers, or downloadable content believed to
 * be relevant to the Package.
 * 
 * @author Gary O'Neall
 */
public class ExternalRef extends ModelObject implements Comparable<ExternalRef> {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_EXTERNAL_REFERENCE;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<>();
		try {
			Optional<ReferenceCategory> referenceCategory = getReferenceCategory();
			if (!referenceCategory.isPresent()) {
				retval.add("Missing or invalid reference category");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting reference category: "+e.getMessage());
		}
		try {
			Optional<ReferenceType> referenceType = getReferenceType();
			if (!referenceType.isPresent()) {
				retval.add("Missing or invalid reference type");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting reference type: "+e.getMessage());
		}
		try {
			Optional<String> referenceLocator = getReferenceLocator();
			if (!referenceLocator.isPresent() || referenceLocator.get().isEmpty()) {
				retval.add("Missing or invalid reference locator");
			}else if (referenceLocator.get().contains(" ")) {
				retval.add("Reference locator contains spaces");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting reference locator: "+e.getMessage());
		}
		return retval;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ExternalRef o) {
		Optional<ReferenceType> myReferenceType = Optional.empty();
		int retval = 0;
		try {
			myReferenceType = this.getReferenceType();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Invalid reference type during compare",e);
		}
		Optional<ReferenceType> compRefType = Optional.empty();
		try {
			compRefType = o.getReferenceType();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Invalid reference type during compare",e);
		}
		if (!myReferenceType.isPresent()) {
			if (compRefType.isPresent()) {
				retval = 1;
			}
		} else if (!compRefType.isPresent()) { 
			retval = -1;
		} else {
			retval = myReferenceType.get().compareTo(compRefType.get());
		}
		if (retval == 0) {
			Optional<String> myReferenceLocator = Optional.empty();
			Optional<String> compareReferenceLocator = Optional.empty();
			try {
				myReferenceLocator = this.getReferenceLocator();
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Invalid reference locator during compare",e);
			}
			try {
				compareReferenceLocator = o.getReferenceLocator();
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Invalid compare reference locator during compare",e);
			}
			
			if (!myReferenceLocator.isPresent()) {
				if (compareReferenceLocator.isPresent()) {
					retval = 1;
				}
			} else if (!compareReferenceLocator.isPresent()) { 
				retval = -1;
			} else {
				retval = myReferenceLocator.get().compareTo(compareReferenceLocator.get());
			}
		}
		if (retval == 0) {
			Optional<ReferenceCategory> referenceCategory = Optional.empty();
			Optional<ReferenceCategory> compareReferenceCategory = Optional.empty();
			try {
				referenceCategory = getReferenceCategory();
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Invalid reference category during compare",e);
			}
			try {
				compareReferenceCategory = o.getReferenceCategory();
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Invalid compare reference category during compare",e);
			}
			if (!referenceCategory.isPresent()) {
				if (compareReferenceCategory.isPresent()) {
					return 1;
				} else {
					return 0;
				}
			} else {
				retval = referenceCategory.get().toString().compareTo(compareReferenceCategory.get().toString());
			}
		}
		if (retval == 0) {
			Optional<String> myComment = Optional.empty();
			Optional<String> compareComment = Optional.empty();
			try {
				myComment = getComment();
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Invalid comment during compare",e);
			}
			try {
				compareComment = o.getComment();
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Invalid compare comment during compare",e);
			}
			if (!myComment.isPresent()) {
				if (compareComment.isPresent()) {
					retval = 1;
				}
			} else if (!compareComment.isPresent()) {
				retval = -1;
			} else {
				retval = myComment.get().compareTo(compareComment.get());
			}
		}
		return retval;
	}
	
	/**
	 * @return the comment
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<String> getComment() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.RDFS_PROP_COMMENT);
	}
	
	/**
	 * @param comment comment to set
	 * @return this to build additional setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef setComment(String comment) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.RDFS_PROP_COMMENT, comment);
		return this;
	}
	
	/**
	 * @return the referenceCategory
	 */
	@SuppressWarnings("unchecked")
	public Optional<ReferenceCategory> getReferenceCategory() throws InvalidSPDXAnalysisException {
		return (Optional<ReferenceCategory>)(Optional<?>)this.getEnumPropertyValue(SpdxConstants.PROP_REFERENCE_CATEGORY);
	}
	
	/**
	 * Set the reference category
	 * @param referenceCategory
	 * @return this to build additional setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef setReferenceCategory(ReferenceCategory referenceCategory) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_REFERENCE_CATEGORY, referenceCategory);
		return this;
	}

	/**
	 * @return the referenceType
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public Optional<ReferenceType> getReferenceType() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstants.PROP_REFERENCE_TYPE);
		if (!retval.isPresent()) {
			return Optional.empty();
		}
		if (retval.get() instanceof ReferenceType) {
			return (Optional<ReferenceType>)(Optional<?>)retval;
		} else if (retval.get() instanceof SimpleUriValue) {
			return Optional.of(new ReferenceType((SimpleUriValue)retval.get()));
		} else {
			throw new InvalidSPDXAnalysisException("Invalid type returned for reference type: "+retval.get().getClass().toString());
		}
	}
	
	/**
	 * Set the reference type
	 * @param referenceType
	 * @return this to build additional setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef setReferenceType(ReferenceType referenceType) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_REFERENCE_TYPE, referenceType);
		return this;
	}
	
	/**
	 * @return the referenceLocator
	 */
	public Optional<String> getReferenceLocator() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_REFERENCE_LOCATOR);
	}
	
	/**
	 * Set the reference locator
	 * @param referenceLocator
	 * @return this to build additional setter
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef setReferenceLocator(String referenceLocator) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_REFERENCE_LOCATOR, referenceLocator);
		return this;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			Optional<ReferenceCategory> referenceCategory = getReferenceCategory();
			if (referenceCategory.isPresent()) {
				sb.append(referenceCategory.get().toString());
				sb.append(' ');
			} else {
				sb.append("[NONE] ");
			}
		} catch (InvalidSPDXAnalysisException e) {
			sb.append("[ERROR] ");
		}
		try {
			Optional<ReferenceType> referenceType = getReferenceType();
			if (referenceType.isPresent()) {
				sb.append(referenceType.get().getIndividualURI());
				sb.append(' ');
			} else {
				sb.append("[NONE] ");
			}
		} catch (InvalidSPDXAnalysisException e) {
			sb.append("[ERROR] ");
		}
		try {
			Optional<String> referenceLocator = getReferenceLocator();
			if (referenceLocator.isPresent()) {
				sb.append(referenceLocator.get());
				sb.append(' ');
			} else {
				sb.append("[NONE]");
			}
		} catch (InvalidSPDXAnalysisException e) {
			sb.append("[ERROR] ");
		}
		try {
			Optional<String> comment = getComment();
			if (comment.isPresent()) {
				sb.append('(');
				sb.append(comment.get());
				sb.append(')');
			}
		} catch (InvalidSPDXAnalysisException e) {
			sb.append("([ERROR])");
		}
		return sb.toString();
	}
}
