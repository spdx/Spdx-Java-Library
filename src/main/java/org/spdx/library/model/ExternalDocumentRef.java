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
import java.util.Objects;
import java.util.Optional;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * Information about an external SPDX document reference including the checksum.  
 * This allows for verification of the external references.
 * 
 * @author Gary O'Neall
 */
public class ExternalDocumentRef extends ModelObject implements Comparable<ExternalDocumentRef> {

	public ExternalDocumentRef() throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore().getNextId(IdType.DocumentRef, DefaultModelStore.getDefaultDocumentUri()));
	}
	
	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalDocumentRef(String id) throws InvalidSPDXAnalysisException {
		super(id);
		if (!SpdxVerificationHelper.isValidExternalDocRef(id)) {
			throw new InvalidSPDXAnalysisException("Invalid external document reference ID "+id+
					".  Must be of the format "+SpdxConstants.EXTERNAL_DOC_REF_PATTERN.pattern());
		}
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalDocumentRef(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
		if (!SpdxVerificationHelper.isValidExternalDocRef(id)) {
			throw new InvalidSPDXAnalysisException("Invalid external document reference ID "+id+
					".  Must be of the format "+SpdxConstants.EXTERNAL_DOC_REF_PATTERN.pattern());
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_EXTERNAL_DOC_REF;
	}
	
	/**
	 * Gets the absolute URI representing the document
	 * @param document
	 * @return
	 */
	private String documentToDocumentUri(SpdxDocument document) {
		Objects.requireNonNull(document);
		String retval = document.getDocumentUri();
		if (retval.endsWith("#")) {
			retval = retval.substring(0, retval.length()-1);
		}
		return retval;
	}
	
	/**
	 * @return the checksum
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public Optional<Checksum> getChecksum() throws InvalidSPDXAnalysisException {
		return (Optional<Checksum>)(Optional<?>)getObjectPropertyValue(SpdxConstants.PROP_EXTERNAL_DOC_CHECKSUM);
	}
	
	/**
	 * @param checksum the checksum to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setChecksum(Checksum checksum) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_EXTERNAL_DOC_CHECKSUM, checksum);
	}
	
	/**
	 * @return the spdxDocumentNamespace
	 */
	@SuppressWarnings("unchecked")
	public Optional<String> getSpdxDocumentNamespace() throws InvalidSPDXAnalysisException {
		Optional<Object> docNamespace = getObjectPropertyValue(SpdxConstants.PROP_EXTERNAL_SPDX_DOCUMENT);
		if (!docNamespace.isPresent()) {
			return Optional.empty();
		}
		if (docNamespace.get() instanceof IndividualValue) {
			String docUri = ((IndividualValue)(docNamespace.get())).getIndividualURI();
			if (Objects.isNull(docUri)) {
				logger.warn("Missing individual URI in doc namespace");
				return Optional.empty();
			}
			return Optional.of(docUri);
		} else if (docNamespace.get() instanceof String) {
			logger.warn("Spdx Document Namespace is of type literal string.  Reccomended type is IndividualValue");
			return (Optional<String>)(Optional<?>)docNamespace;
		} else {
			logger.error("SPDX document namespace is not of type IndividualValue or String.  Type="+docNamespace.get().getClass().toString());
			throw new SpdxInvalidTypeException("SPDX document namespace is not of type IndividualValue or String");
		}
	}
	
	public void setSpdxDocumentNamespace(String documentNamespace) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_EXTERNAL_SPDX_DOCUMENT,
				new IndividualValue() {
					@Override
					public String getIndividualURI() {
						return documentNamespace;
					}
		});
	}
	
	/**
	 * Returns the SPDX document if it exists within the same model store, otherwise it returns Optional.empty
	 * @return the spdxDocument
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public Optional<SpdxDocument> getSpdxDocument() throws InvalidSPDXAnalysisException {
		Optional<String> docNamespace = getSpdxDocumentNamespace();
		if (!docNamespace.isPresent()) {
			return Optional.empty();
		}
		if (this.getModelStore().exists(docNamespace.get(), SpdxConstants.SPDX_DOCUMENT_ID)) {
			return (Optional<SpdxDocument>)(Optional<?>)Optional.of(SpdxModelFactory.createModelObject(
					getModelStore(), docNamespace.get(), SpdxConstants.SPDX_DOCUMENT_ID, 
					SpdxConstants.CLASS_SPDX_DOCUMENT));
		} else {
			return Optional.empty();
		}
	}

	/**
	 * @param spdxDocument the spdxDocument to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setSpdxDocument(SpdxDocument spdxDocument) throws InvalidSPDXAnalysisException {
		setSpdxDocumentNamespace(documentToDocumentUri(spdxDocument));
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<>();
		String uri = "UNKNOWN";
		Optional<String> spdxDocumentNamespace;
		try {
			spdxDocumentNamespace = getSpdxDocumentNamespace();
			if (!spdxDocumentNamespace.isPresent()) {
				retval.add("Missing required external document URI");
			} else {
				uri = spdxDocumentNamespace.get();
				if (!SpdxVerificationHelper.isValidUri(uri)) {
					retval.add("Invalid URI for external Spdx Document URI: "+spdxDocumentNamespace.get());
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("error getting document namespace",e);
			retval.add("Error getting document namespace");
		}

		Optional<Checksum> checksum;
		try {
			checksum = getChecksum();
			if (!checksum.isPresent()) {
				retval.add("Missing checksum for external document "+uri);
			} else {
				retval.addAll(checksum.get().verify());
				if (!checksum.get().getAlgorithm().isPresent() || 
						checksum.get().getAlgorithm().get() != ChecksumAlgorithm.SHA1) {
					retval.add("Checksum algorithm is not SHA1 for external reference "+uri);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("error getting checksum",e);
			retval.add("Error getting checksum for "+uri);
		}
		return retval;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ExternalDocumentRef o) {
		int retval = 0;
		Optional<String> myDocumentNamespace;
		try {
			myDocumentNamespace = getSpdxDocumentNamespace();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting document namepsace",e);
			myDocumentNamespace = Optional.empty();
		}
		Optional<String> compareDocumentNamespace;
		try {
			compareDocumentNamespace = o.getSpdxDocumentNamespace();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare document namepsace",e);
			compareDocumentNamespace = Optional.empty();
		}
		if (!compareDocumentNamespace.isPresent()) {
			if (myDocumentNamespace.isPresent()) {
				return 1;
			}
		} else if (!myDocumentNamespace.isPresent()) {
			return -1;
		} else {
			retval = myDocumentNamespace.get().compareTo(compareDocumentNamespace.get());
		}
		if (retval != 0) {
			return retval;
		}
		Optional<Checksum> myChecksum;
		try {
			myChecksum = getChecksum();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting checksum",e);
			myChecksum = Optional.empty();
		}
		Optional<Checksum> comparechecksum;
		try {
			comparechecksum = o.getChecksum();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare checksum",e);
			comparechecksum = Optional.empty();
		}
		if (!comparechecksum.isPresent()) {
			if (myChecksum.isPresent()) {
				return 1;
			}
		} else if (!myChecksum.isPresent()) {
			return -1;
		} else {
			retval = myChecksum.get().compareTo(comparechecksum.get());
		}
		return retval;
	}

}
