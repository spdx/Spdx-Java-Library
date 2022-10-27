/**
 * Copyright (c) 2020 Source Auditor Inc.
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
package org.spdx.library.model.license;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;

import javax.annotation.Nullable;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ExternalDocumentRef;
import org.spdx.library.model.ExternalSpdxElement;
import org.spdx.library.model.IndividualUriValue;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SimpleUriValue;
import org.spdx.storage.IModelStore;

/**
 * @author Gary O'Neall
 * 
 * This class represents an ExtractedLicenseInfo which is stored in an external SPDX document.
 * 
 * Note that the actual properties for this ExtractedLicenseInfo is in an external document so
 * it is not accessible through this class.
 * 
 * The set methods will cause an exception.
 * 
 * The <code>getExtractedText()</code> will return text that indicates the actual license text
 * is in an external document.
 * 
 * The ID must be in the form <code>SpdxConstants.EXTERNAL_LICENSE_REF_PATTERN.pattern()</code>
 *
 */
public class ExternalExtractedLicenseInfo extends AbstractExtractedLicenseInfo implements IndividualUriValue {
	
	// Note: the default empty constructor is not allowed since the license ID must follow a specific pattern

	public ExternalExtractedLicenseInfo(String id) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), id, 
				DefaultModelStore.getDefaultCopyManager(), true);
	}
	
	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalExtractedLicenseInfo(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
		if (!SpdxConstants.EXTERNAL_EXTRACTED_LICENSE_PATTERN.matcher(id).matches()) {
			throw new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:LicenseRef-XXX");
		}
		getExternalExtractedLicenseURI();	//this will check to make sure the external document reference is available
	}
	
	/**
	 * @return external document ID for the external reference
	 * @throws InvalidSPDXAnalysisException
	 */
	public String getExternalDocumentId() throws InvalidSPDXAnalysisException {
		Matcher matcher = SpdxConstants.EXTERNAL_EXTRACTED_LICENSE_PATTERN.matcher(this.getId());
		if (!matcher.matches()) {
			throw new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:LicenseRef-XXX");
		}
		return matcher.group(1);
	}
	
	/**
	 * @return element ID used in the external document
	 * @throws InvalidSPDXAnalysisException
	 */
	public String getExternalLicenseRef() throws InvalidSPDXAnalysisException {
		Matcher matcher = SpdxConstants.EXTERNAL_EXTRACTED_LICENSE_PATTERN.matcher(this.getId());
		if (!matcher.matches()) {
			throw new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:LicenseRef-XXX");
		}
		return matcher.group(2);
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_EXTERNAL_EXTRACTED_LICENSE;
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.SpdxElement#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(List<String> verifiedIds, String specVersion) {
		// we don't want to call super.verify since we really don't require those fields
		List<String> retval = new ArrayList<>();
		String id = this.getId();
		Matcher matcher = SpdxConstants.EXTERNAL_EXTRACTED_LICENSE_PATTERN.matcher(id);
		if (!matcher.matches()) {				
			retval.add("Invalid id format for an external document reference.  Must be of the form "+SpdxConstants.EXTERNAL_ELEMENT_REF_PATTERN.pattern());
		} else {
			try {
				ExternalSpdxElement.externalDocumentIdToNamespace(matcher.group(1), getModelStore(), getDocumentUri(), getCopyManager());
			} catch (InvalidSPDXAnalysisException e) {
				retval.add(e.getMessage());
			}
		}
		return retval;
	}
	
	/**
	 * @return the URI associated with this external SPDX Extracted License
	 * @throws InvalidSPDXAnalysisException
	 */
	public String getExternalExtractedLicenseURI() throws InvalidSPDXAnalysisException {
		return externalExtractedLicenseIdToURI(getId(), getModelStore(), getDocumentUri(), getCopyManager());
	}
	
	/**
	 * @param externalExtractedLicenseId
	 * @param stModelStore
	 * @param stDocumentUri
	 * @param copyManager
	 * @return The URI associated with the external LicenseRef with the ID externalLicenseRefId
	 * @throws InvalidSPDXAnalysisException
	 */
	public static String externalExtractedLicenseIdToURI(String externalExtractedLicenseId,
			IModelStore stModelStore, String stDocumentUri, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Matcher matcher = SpdxConstants.EXTERNAL_EXTRACTED_LICENSE_PATTERN.matcher(externalExtractedLicenseId);
		if (!matcher.matches()) {
			logger.error("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:LicenseRef-XXX");
			throw new InvalidSPDXAnalysisException("Invalid id format for an external document reference.  Must be of the form ExternalSPDXRef:LicenseRef-XXX");
		}
		String externalDocumentUri;
		externalDocumentUri = ExternalSpdxElement.externalDocumentIdToNamespace(matcher.group(1), stModelStore, stDocumentUri, copyManager);
		if (externalDocumentUri.endsWith("#")) {
			return externalDocumentUri + matcher.group(2);
		} else {
			return externalDocumentUri + "#" + matcher.group(2);
		}
	}
	
	/**
	 * @param externalLicenseUri URI of the form
	 *                           externaldocumentnamespace#LicenseRef-XXXXX
	 * @param stModelStore
	 * @param stDocumentUri
	 * @return ExternalSpdxRef an ExternalLicenseRef based on a URI of the form
	 *         externaldocumentnamespace#LicenseRef-XXXXX
	 * @param copyManager if non-null, create the external doc ref if it is not
	 *                    already in the ModelStore
	 * @throws InvalidSPDXAnalysisException
	 */
	public static ExternalExtractedLicenseInfo uriToExternalExtractedLicense(String externalLicenseUri, IModelStore stModelStore,
			String stDocumentUri, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		return new ExternalExtractedLicenseInfo(stModelStore, stDocumentUri, uriToExternalExtractedLicenseId(
				externalLicenseUri, stModelStore, stDocumentUri, copyManager), copyManager, true);
	}
	
	/**
	 * Convert a URI to an ID for an External Extracted License
	 * @param uri URI with the external document namespace and the external Extracted License in the form namespace#LicenseRef-XXXX
	 * @param stModelStore
	 * @param stDocumentUri
	 * @param copyManager if non-null, create the external doc ref if it is not already in the ModelStore
	 * @return external SPDX element ID in the form DocumentRef-XX:LicenseRef-XXXX
	 * @throws InvalidSPDXAnalysisException
	 */
	public static String uriToExternalExtractedLicenseId(String uri,
			IModelStore stModelStore, String stDocumentUri, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(uri, "URI can not be null");
		Matcher matcher = SpdxConstants.EXTERNAL_EXTRACTED_LICENSE_URI_PATTERN.matcher(uri);
		if (!matcher.matches()) {
			throw new InvalidSPDXAnalysisException("Invalid URI format: "+uri+".  Expects namespace#LicenseRef-XXXX");
		}
		Optional<ExternalDocumentRef> externalDocRef = ExternalDocumentRef.getExternalDocRefByDocNamespace(stModelStore, stDocumentUri, 
				matcher.group(1), copyManager);
		if (!externalDocRef.isPresent()) {
			logger.error("Could not find or create the external document reference for document namespace "+ matcher.group(1));
			throw new InvalidSPDXAnalysisException("Could not find or create the external document reference for document namespace "+ matcher.group(1));
		}
		return externalDocRef.get().getId() + ":" + matcher.group(2);
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#equivalent(org.spdx.library.model.ModelObject)
	 */
	@Override
	public boolean equivalent(ModelObject compare) {
		if (!(compare instanceof ExternalExtractedLicenseInfo)) {
			return false;
		}
		return getId().equals(compare.getId());
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#equivalent(org.spdx.library.model.ModelObject, boolean)
	 */
	@Override
	public boolean equivalent(ModelObject compare, boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
		return equivalent(compare);
	}

	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.IndividualUriValue#getIndividualURI()
	 */
	@Override
	public String getIndividualURI() {
		try {
			return getExternalExtractedLicenseURI();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error getting external LicenseRef URI",e);
			throw new RuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.license.AbstractExtractedLicenseInfo#getExtractedText()
	 */
	@Override
	public String getExtractedText() throws InvalidSPDXAnalysisException {
		return "The text for this license can be found in the external document "
				+ getExternalDocumentId()
				+ " license Ref "
				+ getExternalLicenseRef()
				+ ".";
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.license.SimpleLicensingInfo#getComment()
	 */
	@Override
	public String getComment() throws InvalidSPDXAnalysisException {
		return "This is an external LicenseRef - see the document containing the license for any comments";
	}
	
	/**
	 * @param comment the comment to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Override
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		throw new InvalidSPDXAnalysisException("Can not set comment for an external LicenseRef.  "
				+ "Changes to the license need to be made within the document containing the license.");
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.license.SimpleLicensingInfo#getSeeAlso()
	 */
	@Override
	public Collection<String> getSeeAlso() throws InvalidSPDXAnalysisException {
		return new ArrayList<>();
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.license.SimpleLicensingInfo#setSeeAlso(java.util.Collection)
	 */
	@Override
	public void setSeeAlso(Collection<String> seeAlsoUrl) throws InvalidSPDXAnalysisException {
		throw new InvalidSPDXAnalysisException("Can not set seeAlso for an external LicenseRef.  "
				+ "Changes to the license need to be made within the document containing the license.");
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.license.SimpleLicensingInfo#getName()
	 */
	@Override
	public String getName() throws InvalidSPDXAnalysisException {
		return "";
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.license.SimpleLicensingInfo#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) throws InvalidSPDXAnalysisException {
		throw new InvalidSPDXAnalysisException("Can not set name for an external LicenseRef.  "
				+ "Changes to the license need to be made within the document containing the license.");
	}
	
	@Override
	public boolean equals(Object comp) {
		return SimpleUriValue.isIndividualUriValueEquals(this, comp);
	}

	@Override
	public int hashCode() {
		return SimpleUriValue.getIndividualUriValueHash(this);
	}
}
