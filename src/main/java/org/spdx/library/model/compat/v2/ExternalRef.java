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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SimpleUriValue;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.model.compat.v2.enumerations.ReferenceCategory;
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
	 * @param objectUri
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param objectUri
	 * @param create
	 * @param copyManager
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstantsCompatV2.CLASS_SPDX_EXTERNAL_REFERENCE;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = new ArrayList<>();
		try {
			ReferenceCategory referenceCategory = getReferenceCategory();
			if (ReferenceCategory.MISSING.equals(referenceCategory)) {
				retval.add("Missing or invalid reference category");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting reference category: "+e.getMessage());
		}
		try {
			ReferenceType referenceType = getReferenceType();
			if (ReferenceType.MISSING_REFERENCE_TYPE_URI.equals(referenceType.getIndividualURI())) {
				retval.add("Missing or invalid reference type");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting reference type: "+e.getMessage());
		}
		try {
			String referenceLocator = getReferenceLocator();
			if (referenceLocator.isEmpty()) {
				retval.add("Missing or invalid reference locator");
			}else if (referenceLocator.contains(" ")) {
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
		String myReferenceType;
		int retval = 0;
		try {
			myReferenceType = this.getReferenceType().getIndividualURI();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Invalid reference type during compare",e);
			myReferenceType = ReferenceType.MISSING_REFERENCE_TYPE_URI;
		}
		String compRefType;
		try {
			compRefType = o.getReferenceType().getIndividualURI();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Invalid reference type during compare",e);
			compRefType = ReferenceType.MISSING_REFERENCE_TYPE_URI;
		}
		retval = myReferenceType.compareTo(compRefType);
		if (retval == 0) {
			String myReferenceLocator = "";
			String compareReferenceLocator = "";
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
			retval = myReferenceLocator.compareTo(compareReferenceLocator);
		}
		if (retval == 0) {
			ReferenceCategory referenceCategory = ReferenceCategory.MISSING;
			ReferenceCategory compareReferenceCategory = ReferenceCategory.MISSING;
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
			retval = referenceCategory.toString().compareTo(compareReferenceCategory.toString());
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
		return getStringPropertyValue(SpdxConstantsCompatV2.RDFS_PROP_COMMENT);
	}
	
	/**
	 * @param comment comment to set
	 * @return this to build additional setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef setComment(String comment) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstantsCompatV2.RDFS_PROP_COMMENT, comment);
		return this;
	}
	
	/**
	 * @return the referenceCategory
	 */
	public ReferenceCategory getReferenceCategory() throws InvalidSPDXAnalysisException {
		Optional<?> retval = this.getEnumPropertyValue(SpdxConstantsCompatV2.PROP_REFERENCE_CATEGORY);
		if (retval.isPresent()) {
			if (!(retval.get() instanceof ReferenceCategory)) {
				throw new InvalidSPDXAnalysisException("Invalid type for reference category: "+retval.get().getClass().toString());
			}
			return (ReferenceCategory)retval.get();
		} else {
			return ReferenceCategory.MISSING;
		}
	}
	
	/**
	 * Set the reference category
	 * @param referenceCategory
	 * @return this to build additional setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef setReferenceCategory(ReferenceCategory referenceCategory) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(referenceCategory)) {
				throw new InvalidSPDXAnalysisException("Can not set required referenceCategory to null");
			}
		}
		if (ReferenceCategory.MISSING.equals(referenceCategory)) {
			throw new InvalidSPDXAnalysisException("Can not set required referenceCategory to MISSING");
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_REFERENCE_CATEGORY, referenceCategory);
		return this;
	}

	/**
	 * @return the referenceType.  If the refrenceType is not in the modelStore, the constant ReferenceType.MISSING is returned
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ReferenceType getReferenceType() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstantsCompatV2.PROP_REFERENCE_TYPE);
		if (!retval.isPresent()) {
			return ReferenceType.getMissingReferenceType();
		}
		if (retval.get() instanceof ReferenceType) {
			return (ReferenceType)retval.get();
		} else if (retval.get() instanceof SimpleUriValue) {
			return new ReferenceType((SimpleUriValue)retval.get());
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
		if (Objects.isNull(referenceType)) {
			if (strict) {
				throw new InvalidSPDXAnalysisException("Can not set required referenceType to null");
			} else {
				setPropertyValue(SpdxConstantsCompatV2.PROP_REFERENCE_TYPE, null);
			}
		} else {
			if (ReferenceType.MISSING_REFERENCE_TYPE_URI.equals(referenceType.getIndividualURI())) {
				throw new InvalidSPDXAnalysisException("Can not set referenceType to MISSING");
			}
			if (strict && !SpdxVerificationHelper.isValidUri(referenceType.getIndividualURI())) {
				throw new InvalidSPDXAnalysisException("Invalid URI for referenceType");
			}
			setPropertyValue(SpdxConstantsCompatV2.PROP_REFERENCE_TYPE, referenceType);
		}
		return this;
	}
	
	/**
	 * @return the referenceLocator.  If not found, a blank string is returned
	 */
	public String getReferenceLocator() throws InvalidSPDXAnalysisException {
		Optional<String> retval = getStringPropertyValue(SpdxConstantsCompatV2.PROP_REFERENCE_LOCATOR);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			return "";
		}
	}
	
	/**
	 * Set the reference locator
	 * @param referenceLocator
	 * @return this to build additional setter
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalRef setReferenceLocator(String referenceLocator) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(referenceLocator)) {
				throw new InvalidSPDXAnalysisException("Can not set required reference locator to null");
			}
			if (referenceLocator.isEmpty()) {
				throw new InvalidSPDXAnalysisException("Can not set required reference locator to an empty string");
			}
			if (referenceLocator.contains(" ")) {
				throw new InvalidSPDXAnalysisException("Reference locator contains spaces");
			}
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_REFERENCE_LOCATOR, referenceLocator);
		return this;
	}
	
	@Override public boolean equivalent(ModelObject compare, boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
		if (!(compare instanceof ExternalRef)) {
			return false;
		}
		ExternalRef compareEf = (ExternalRef)compare;
		return Objects.equals(this.getReferenceLocator(), compareEf.getReferenceLocator()) &&
				Objects.equals(getComment(), compareEf.getComment()) &&
				Objects.equals(getReferenceCategory(), compareEf.getReferenceCategory());
		// we ignore the reference type since it may be local to the document and a different URI
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			ReferenceCategory referenceCategory = getReferenceCategory();
			if (ReferenceCategory.MISSING.equals(referenceCategory)) {
				sb.append("[NONE] ");
			} else {
				sb.append(referenceCategory.toString());
				sb.append(' ');
			}
		} catch (InvalidSPDXAnalysisException e) {
			sb.append("[ERROR] ");
		}
		try {
			ReferenceType referenceType = getReferenceType();
			if (!ReferenceType.MISSING_REFERENCE_TYPE_URI.equals(referenceType.getIndividualURI())) {
				sb.append(referenceType.getIndividualURI());
				sb.append(' ');
			} else {
				sb.append("[NONE] ");
			}
		} catch (InvalidSPDXAnalysisException e) {
			sb.append("[ERROR] ");
		}
		try {
			String referenceLocator = getReferenceLocator();
			if (!referenceLocator.isEmpty()) {
				sb.append(referenceLocator);
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
