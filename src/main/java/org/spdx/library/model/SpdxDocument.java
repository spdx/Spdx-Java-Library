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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.Version;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.SpdxListedLicense;
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
	@SuppressWarnings("unchecked")
	public Optional<SpdxCreatorInformation> getCreationInfo() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstants.PROP_SPDX_CREATION_INFO);
		if (retval.isPresent() && !(retval.get() instanceof SpdxCreatorInformation)) {
			throw new SpdxInvalidTypeException("Invalid tpe for CreationInfo: "+retval.get().getClass().toString());
		}
		return (Optional<SpdxCreatorInformation>)(Optional<?>)retval;
	}
	
	/**
	 * @param creationInfo the creationInfo to set
	 */
	public void setCreationInfo(SpdxCreatorInformation creationInfo) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_SPDX_CREATION_INFO, creationInfo);
	}

	/**
	 * @return the dataLicense
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<AnyLicenseInfo> getDataLicense() throws InvalidSPDXAnalysisException {
		return getAnyLicenseInfoPropertyValue(SpdxConstants.PROP_SPDX_DATA_LICENSE);
	}
	
	/**
	 * @param dataLicense the dataLicense to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDataLicense(AnyLicenseInfo dataLicense) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_SPDX_DATA_LICENSE, dataLicense);
	}
	
	/**
	 * @return the externalDocumentRefs
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<ExternalDocumentRef> getExternalDocumentRefs() throws InvalidSPDXAnalysisException {
		return (Collection<ExternalDocumentRef>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_SPDX_EXTERNAL_DOC_REF, ExternalDocumentRef.class);
	}
	
	/**
	 * @return the extractedLicenseInfos
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<ExtractedLicenseInfo> getExtractedLicenseInfos() throws InvalidSPDXAnalysisException {
		return (Collection<ExtractedLicenseInfo>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_SPDX_EXTRACTED_LICENSES, ExtractedLicenseInfo.class);
	}
	

	/**
	 * @return the specVersion
	 */
	public Optional<String> getSpecVersion() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_SPDX_VERSION);
	}
	
	/**
	 * @param specVersion the specVersion to set
	 */
	public void setSpecVersion(String specVersion) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_SPDX_VERSION, specVersion);
	}
	

	
	@Override
	public List<String> verify() {
		List<String> retval = super.verify();
		// specVersion
		String docSpecVersion = "";	// note - this is used later in verify to verify version specific info
		try {
			Optional<String> specVersion = getSpecVersion();
			if (!specVersion.isPresent()) {
				retval.add("Missing required SPDX version");
				docSpecVersion = "UNKNOWN";
			} else {
				docSpecVersion = specVersion.get();
				String verify = Version.verifySpdxVersion(docSpecVersion);
				if (verify != null) {
					retval.add(verify);
				}			
			}
		} catch(InvalidSPDXAnalysisException e) {
			retval.add("Error getting spec version: "+e.getMessage());
		}
		
		// creationInfo
		try {
			Optional<SpdxCreatorInformation> creator = this.getCreationInfo();
			if (!creator.isPresent()) {
				retval.add("Missing required Creator");
			} else {
				retval.addAll(creator.get().verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting creator information: "+e.getMessage());
		}
		// Extracted licensine infos
		try {
			for (ExtractedLicenseInfo licInfo:getExtractedLicenseInfos()) {
				retval.addAll(licInfo.verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting extracted licensing info: "+e.getMessage());
		}
		// data license
		try {
			Optional<AnyLicenseInfo> dataLicense = this.getDataLicense();
			if (!dataLicense.isPresent()) {
				retval.add("Missing required data license");
			} else if (!(dataLicense.get() instanceof SpdxListedLicense)) {
				retval.add("Invalid license type for data license - must be an SPDX Listed license");
			} else if (!((SpdxListedLicense)dataLicense.get()).getLicenseId().equals(SpdxConstants.SPDX_DATA_LICENSE_ID)) {
				retval.add("Incorrect data license for SPDX version 1.0 document - found "+
						((SpdxListedLicense)dataLicense.get()).getLicenseId()+", expected "+
						SpdxConstants.SPDX_DATA_LICENSE_ID);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting data license: "+e.getMessage());
		}
		// External document references
		try {
			for (ExternalDocumentRef externalRef:getExternalDocumentRefs()) {
				retval.addAll(externalRef.verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting external document references: "+e.getMessage());
		}
		// documentDescribes relationships
		try {
			if (getDocumentDescribes().size() == 0) {
				retval.add("Document must have at least one relationship of type DOCUMENT_DESCRIBES");
				// Note - relationships are verified in the superclass.  This should also recursively
				// verify any other important objects.
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting document describes: "+e.getMessage());
		}
		//TODO: Figure out what to do with checking any "dangling items" not linked to the describes by
		return retval;
	}
	
}
