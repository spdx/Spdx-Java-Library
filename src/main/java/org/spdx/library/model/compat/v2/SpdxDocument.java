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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.Version;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.library.model.compat.v2.license.AnyLicenseInfo;
import org.spdx.library.model.compat.v2.license.ExtractedLicenseInfo;
import org.spdx.library.model.compat.v2.license.SpdxListedLicense;
import org.spdx.library.model.compat.v2.license.SpdxNoneLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;

/**
 * An SpdxDocument is a summary of the contents, provenance, ownership and licensing 
 * analysis of a specific software package. 
 * This is, effectively, the top level of SPDX information.
 * 
 * @author Gary O'Neall
 */
public class SpdxDocument extends SpdxElement {
	
	Collection<SpdxElement> documentDescribes;
	Collection<ExternalDocumentRef> externalDocumentRefs;
	Collection<ExtractedLicenseInfo> extractedLicenseInfos;
	
	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for the document associated with this model
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public SpdxDocument(IModelStore modelStore, String documentUri, ModelCopyManager copyManager, boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, SpdxConstantsCompatV2.SPDX_DOCUMENT_ID, copyManager, create);
		documentDescribes = new RelatedElementCollection(this, RelationshipType.DESCRIBES, null);
		externalDocumentRefs = (Collection<ExternalDocumentRef>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstantsCompatV2.PROP_SPDX_EXTERNAL_DOC_REF, ExternalDocumentRef.class);
		extractedLicenseInfos = (Collection<ExtractedLicenseInfo>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstantsCompatV2.PROP_SPDX_EXTRACTED_LICENSES, ExtractedLicenseInfo.class);
	}
	
	/**
	 * Obtains or creates an SPDX document using the default document store
	 * @param documentUri
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxDocument(String documentUri) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), documentUri, DefaultModelStore.getDefaultCopyManager(), true);
	}


	@Override
	public String getType() {
		return SpdxConstantsCompatV2.CLASS_SPDX_DOCUMENT;
	}
	
	@Override
	protected PropertyDescriptor getNamePropertyDescriptor() {
		return SpdxConstantsCompatV2.PROP_NAME;
	}


	/**
	 * @return collection of items described by this SPDX document
	 * @throws InvalidSPDXAnalysisException
	 */
	public Collection<SpdxElement> getDocumentDescribes() throws InvalidSPDXAnalysisException {
		return documentDescribes;
	}
	

	/**
	 * clear and reset document describes to the paramater collection
	 * @param documentDescribes collection of items described by this SPDX document
	 * @return this to chain setters
	 */
	public SpdxDocument setDocumentDescribes(List<SpdxItem> documentDescribes) {
		Objects.requireNonNull(documentDescribes, "Document describes can not be null");
		this.documentDescribes.clear();
		this.documentDescribes.addAll(documentDescribes);
		return this;
	}
	
	/**
	 * @return the creationInfo, null if no creationInfo in the SPDX document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public @Nullable SpdxCreatorInformation getCreationInfo() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstantsCompatV2.PROP_SPDX_CREATION_INFO);
		if (retval.isPresent()) {
			if (!(retval.get() instanceof SpdxCreatorInformation)) {
				throw new SpdxInvalidTypeException("Invalid tpe for CreationInfo: "+retval.get().getClass().toString());
			}
			return (SpdxCreatorInformation)retval.get();
		} else {
			logger.warn("No creation info for document "+getName());
			return null;
		}
	}
	
	/**
	 * @param creationInfo the creationInfo to set
	 */
	public void setCreationInfo(SpdxCreatorInformation creationInfo) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(creationInfo)) {
				throw new InvalidSPDXAnalysisException("Can not set required creation info to null");
			}
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_SPDX_CREATION_INFO, creationInfo);
	}

	/**
	 * @return the dataLicense
	 * @throws InvalidSPDXAnalysisException 
	 */
	public AnyLicenseInfo getDataLicense() throws InvalidSPDXAnalysisException {
		Optional<AnyLicenseInfo> retval = getAnyLicenseInfoPropertyValue(SpdxConstantsCompatV2.PROP_SPDX_DATA_LICENSE);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			logger.warn("No data license for "+getName());
			return new SpdxNoneLicense();
		}
	}
	
	/**
	 * @param dataLicense the dataLicense to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDataLicense(AnyLicenseInfo dataLicense) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(dataLicense)) {
				throw new InvalidSPDXAnalysisException("Can not set required data license to null");
			}
			if (!(dataLicense instanceof SpdxListedLicense)) {
				throw new InvalidSPDXAnalysisException("Invalid license type for data license - must be an SPDX Listed license");
			} else if (!((SpdxListedLicense)dataLicense).getLicenseId().equals(SpdxConstantsCompatV2.SPDX_DATA_LICENSE_ID)) {
				throw new InvalidSPDXAnalysisException("Incorrect data license.  Must be "+SpdxConstantsCompatV2.SPDX_DATA_LICENSE_ID);
			}
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_SPDX_DATA_LICENSE, dataLicense);
	}
	
	/**
	 * @return the externalDocumentRefs
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Collection<ExternalDocumentRef> getExternalDocumentRefs() throws InvalidSPDXAnalysisException {
		return externalDocumentRefs;
	}

	/**
	 * @return the extractedLicenseInfos
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Collection<ExtractedLicenseInfo> getExtractedLicenseInfos() throws InvalidSPDXAnalysisException {
		return this.extractedLicenseInfos;
	}
	
	/**
	 * Add a license info to the collection of extracted license infos
	 * @param licenseInfo
	 * @return
	 */
	public boolean addExtractedLicenseInfos(ExtractedLicenseInfo licenseInfo) {
		Objects.requireNonNull(licenseInfo, "License info can not be null");
		return this.extractedLicenseInfos.add(licenseInfo);
	}

	/**
	 * Clear the extractedLicenseInfos and add all elements from extractedLicenseInfos
	 * @param extractedLicenseInfos
	 * @return this to enable chaining of sets
	 */
	public SpdxDocument setExtractedLicenseInfos(List<ExtractedLicenseInfo> extractedLicenseInfos) {
		Objects.requireNonNull(extractedLicenseInfos, "Extracted license infos can not be null");
		this.extractedLicenseInfos.clear();
		this.extractedLicenseInfos.addAll(extractedLicenseInfos);
		return this;
	}

	/**
	 * @return the specVersion
	 */
	public String getSpecVersion() throws InvalidSPDXAnalysisException {
		Optional<String> retval = getStringPropertyValue(SpdxConstantsCompatV2.PROP_SPDX_SPEC_VERSION);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			return "";
		}
	}
	
	/**
	 * @param specVersion the specVersion to set
	 */
	public void setSpecVersion(String specVersion) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(specVersion)) {
				throw new InvalidSPDXAnalysisException("Can not set required spec version to null");
			}
			String verify = Version.verifySpdxVersion(specVersion);
			if (Objects.nonNull(verify) && !verify.isEmpty()) {
				throw new InvalidSPDXAnalysisException(verify);
			}
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_SPDX_SPEC_VERSION, specVersion);
	}
	

	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.SpdxElement#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String verifySpecVersion) {
		List<String> retval = new ArrayList<>();
		String specVersion;
		try {
			specVersion = getSpecVersion();
			if (specVersion.isEmpty()) {
				retval.add("Missing required SPDX version");
				specVersion = verifySpecVersion;
			} else {
				String verify = Version.verifySpdxVersion(specVersion);
				if (verify != null) {
					retval.add(verify);
					specVersion = verifySpecVersion;
				}			
			}
		} catch (InvalidSPDXAnalysisException e2) {
			retval.add("Error getting spec version");
			specVersion = verifySpecVersion;
		}
		retval.addAll(super._verify(verifiedIds, specVersion));

		// name
		try {
		    Optional<String> name = getName();
			if (!name.isPresent() || name.get().isEmpty()) {
				retval.add("Missing required document name");
			}
		} catch (InvalidSPDXAnalysisException e1) {
			retval.add("Error getting document name");
		}
	
		// creationInfo
		try {
			SpdxCreatorInformation creator = this.getCreationInfo();
			if (Objects.isNull(creator)) {
				retval.add("Missing required Creator");
			} else {
				retval.addAll(creator.verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting creator information: "+e.getMessage());
		}
		// Extracted licensine infos
		try {
			for (ExtractedLicenseInfo licInfo:getExtractedLicenseInfos()) {
				retval.addAll(licInfo.verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting extracted licensing info: "+e.getMessage());
		}
		// data license
		try {
			AnyLicenseInfo dataLicense = this.getDataLicense();
			if (dataLicense.toString().equals("NONE")) {
				retval.add("Missing required data license");
			} else if (!(dataLicense instanceof SpdxListedLicense)) {
				retval.add("Invalid license type for data license - must be an SPDX Listed license");
			} else if (!((SpdxListedLicense)dataLicense).getLicenseId().equals(SpdxConstantsCompatV2.SPDX_DATA_LICENSE_ID)) {
				retval.add("Incorrect data license for SPDX version 1.0 document - found "+
						((SpdxListedLicense)dataLicense).getLicenseId()+", expected "+
						SpdxConstantsCompatV2.SPDX_DATA_LICENSE_ID);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting data license: "+e.getMessage());
		}
		// External document references
		try {
			for (ExternalDocumentRef externalRef:getExternalDocumentRefs()) {
				retval.addAll(externalRef.verify(verifiedIds, specVersion));
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
			} else {
				for (SpdxElement element:getDocumentDescribes()) {
					retval.addAll(element.verify(verifiedIds, specVersion));
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting document describes: "+e.getMessage());
		}
		//TODO: Figure out what to do with checking any "dangling items" not linked to the describes by
		return retval;
	}

	/**
	 * Clear the externalDocumentRefs and add all elements from externalDocumentRefs
	 * @param externalDocumentRefs
	 * @return this to enable chaining of sets
	 */
	public SpdxDocument setExternalDocumentRefs(Collection<ExternalDocumentRef> externalDocumentRefs) {
		Objects.requireNonNull(externalDocumentRefs, "External document refs can not be null");
		this.externalDocumentRefs.clear();
		this.externalDocumentRefs.addAll(externalDocumentRefs);
		return this;
	}
}
