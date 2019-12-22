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

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;

/**
 * An SpdxDocument is a summary of the contents, provenance, ownership and licensing 
 * analysis of a specific software package. 
 * This is, effectively, the top level of SPDX information.
 * 
 * @author Gary O'Neall
 */
public class SpdxDocument extends SpdxElement {
	
	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for the document associated with this model
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxDocument(IModelStore modelStore, String documentUri, boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, SpdxConstants.SPDX_DOCUMENT_ID, create);
	}
	
	/**
	 * Obtains or creates an SPDX document using the default document store
	 * @param documentUri
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxDocument(String documentUri) throws InvalidSPDXAnalysisException {
		super(DefaultModelStore.getDefaultModelStore(), documentUri, SpdxConstants.SPDX_DOCUMENT_ID, true);
	}


	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_DOCUMENT;
	}
	
	@Override
	protected String getNamePropertyName() {
		return SpdxConstants.PROP_NAME;
	}


	public Collection<SpdxElement> getDocumentDescribes() throws InvalidSPDXAnalysisException {
		return new RelatedElementCollection(this, RelationshipType.DESCRIBES);
	}
	
	/**
	 * @return the creationInfo
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SPDXCreatorInformation getCreationInfo() throws InvalidSPDXAnalysisException {
		
	}
	
	/**
	 * @param creationInfo the creationInfo to set
	 */
	public void setCreationInfo(SPDXCreatorInformation creationInfo) throws InvalidSPDXAnalysisException {
		
	}

	/**
	 * @return the dataLicense
	 * @throws InvalidSPDXAnalysisException 
	 */
	public AnyLicenseInfo getDataLicense() throws InvalidSPDXAnalysisException {
		
	}
	
	/**
	 * @param dataLicense the dataLicense to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDataLicense(AnyLicenseInfo dataLicense) throws InvalidSPDXAnalysisException {
		
	}
	
	/**
	 * @return the externalDocumentRefs
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Collection<ExternalDocumentRef> getExternalDocumentRefs() throws InvalidSPDXAnalysisException {
		
	}
	
	/**
	 * @return the extractedLicenseInfos
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Collection<ExtractedLicenseInfo> getExtractedLicenseInfos() throws InvalidSPDXAnalysisException {
		
	}
	

	/**
	 * @return the specVersion
	 */
	public String getSpecVersion() throws InvalidSPDXAnalysisException {
		
	}
	
	/**
	 * @param specVersion the specVersion to set
	 */
	public void setSpecVersion(String specVersion) throws InvalidSPDXAnalysisException {
		
	}
	
	@Override
	public List<String> verify() {
		List<String> retval = super.verify();
		// specVersion
		String docSpecVersion = "";	// note - this is used later in verify to verify version specific info
		if (this.specVersion == null || this.specVersion.isEmpty()) {
			retval.add("Missing required SPDX version");
			docSpecVersion = "UNKNOWN";
		} else {
			docSpecVersion = this.specVersion;
			String verify = this.documentContainer.verifySpdxVersion(docSpecVersion);
			if (verify != null) {
				retval.add(verify);
			}			
		}
		// creationInfo
		try {
			SPDXCreatorInformation creator = this.getCreationInfo();
			if (creator == null) {
				retval.add("Missing required Creator");
			} else {
				List<String> creatorVerification = creator.verify();
				retval.addAll(creatorVerification);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid creator information: "+e.getMessage());
		}
		// Reviewers
		try {
			SPDXReview[] reviews = this.getReviewers();
			if (reviews != null) {
				for (int i = 0; i < reviews.length; i++) {
					List<String> reviewerVerification = reviews[i].verify();
					retval.addAll(reviewerVerification);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid reviewers: "+e.getMessage());
		}
		// Extracted licensine infos
		try {
			ExtractedLicenseInfo[] extractedLicInfos = this.getExtractedLicenseInfos();
			if (extractedLicInfos != null) {
				for (int i = 0; i < extractedLicInfos.length; i++) {
					List<String> extractedLicInfoVerification = extractedLicInfos[i].verify();
					retval.addAll(extractedLicInfoVerification);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid extracted licensing info: "+e.getMessage());
		}
		// data license
		if (!docSpecVersion.equals(SpdxDocumentContainer.POINT_EIGHT_SPDX_VERSION) && 
				!docSpecVersion.equals(SpdxDocumentContainer.POINT_NINE_SPDX_VERSION)) { // added as a mandatory field in 1.0
			try {
				AnyLicenseInfo dataLicense = this.getDataLicense();
				if (dataLicense == null) {
					retval.add("Missing required data license");
				} else {
					if (!(dataLicense instanceof SpdxListedLicense)) {
						retval.add("Invalid license type for data license - must be an SPDX Listed license");
					} else {
						if (docSpecVersion.equals(SpdxDocumentContainer.ONE_DOT_ZERO_SPDX_VERSION)) 
							{ 
							if (!((SpdxListedLicense)dataLicense).getLicenseId().equals(
									SpdxDocumentContainer.SPDX_DATA_LICENSE_ID_VERSION_1_0)) {
								retval.add("Incorrect data license for SPDX version 1.0 document - found "+
										((SpdxListedLicense)dataLicense).getLicenseId()+", expected "+
										SpdxDocumentContainer.SPDX_DATA_LICENSE_ID_VERSION_1_0);
							}
						} else {
							if (!((SpdxListedLicense)dataLicense).getLicenseId().equals(
									SpdxDocumentContainer.SPDX_DATA_LICENSE_ID)) {
								retval.add("Incorrect data license for SPDX document - found "+
										((SpdxListedLicense)dataLicense).getLicenseId()+
									", expected "+SpdxDocumentContainer.SPDX_DATA_LICENSE_ID);
							}					
						}
					}
				}
			} catch (InvalidSPDXAnalysisException e) {
				retval.add("Invalid data license: "+e.getMessage());
			}
		}
		// External document references
		try {
			ExternalDocumentRef[] externalRefs = this.getExternalDocumentRefs();
			for (int i = 0; i < externalRefs.length; i++) {
				retval.addAll(externalRefs[i].verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid external document references: "+e.getMessage());
		}
		// documentDescribes relationships
		try {
			SpdxItem[] items = getDocumentDescribes();
			if (items.length == 0) {
				retval.add("Document must have at least one relationship of type DOCUMENT_DESCRIBES");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid document items: "+e.getMessage());
		}
		try {
			List<SpdxElement> allElements = documentContainer.findAllElements();
			for (SpdxElement element:allElements) {
				if (!element.getId().equals(this.getId())) {
					retval.addAll(element.verify());
				}				
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid elements: "+e.getMessage());
		}
		return retval;
	}
	
}
