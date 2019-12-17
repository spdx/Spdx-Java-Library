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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.IModelStore.ModelTransaction;
import org.spdx.storage.IModelStore.ReadWrite;

/**
 * Information about an external SPDX document reference including the checksum.  
 * This allows for verification of the external references.
 * 
 * @author Gary O'Neall
 */
public class ExternalDocumentRef extends ModelObject implements Comparable<ExternalDocumentRef> {
	
	/**
	 * Obtain an ExternalDocumentRef which maps to the document URI for the external SPDX document.
	 * @param stModelStore Model Store for the document referring to the external SPDX document
	 * @param stDocumentUri Document URI for the document referring to the external SPDX document
	 * @param externalDocUri Document URI for the external document (a.k.a. eternalDocumentNamespace)
	 * @param createExternalDocRef if true, create the external Doc ref if it is not a property of the SPDX Document
	 * @return
	 */
	public static Optional<ExternalDocumentRef> getExternalDocRefByDocNamespace(IModelStore stModelStore,
			String stDocumentUri, String externalDocUri, boolean createExternalDocRef) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(stModelStore);
		Objects.requireNonNull(stDocumentUri);
		Objects.requireNonNull(externalDocUri);
		ModelTransaction transaction;
		try {
			transaction = stModelStore.beginTransaction(stDocumentUri, ReadWrite.WRITE);
		} catch (IOException e) {
			logger.error("IO Error creating model transaction",e);
			throw new InvalidSPDXAnalysisException("IO Error creating model transaction",e);
		}
		try {
			Collection<Object> existingExternalRefs = new ModelCollection<Object>(stModelStore,stDocumentUri,
					SpdxConstants.SPDX_DOCUMENT_ID, SpdxConstants.PROP_SPDX_EXTERNAL_DOC_REF);
			for (Object externalRef:existingExternalRefs) {
				if (!(externalRef instanceof ExternalDocumentRef)) {
					logger.warn("Incorrect type for an external document ref: "+externalRef.getClass().toString());
				} else {
					Optional<String> externalRefNamespace = ((ExternalDocumentRef)externalRef).getSpdxDocumentNamespace();
					if (!externalRefNamespace.isPresent()) {
						logger.warn("Namespace missing for external doc ref "+((ExternalDocumentRef)externalRef).getId());
					}
					if (externalDocUri.equals(externalRefNamespace.get())) {
						return Optional.of((ExternalDocumentRef)externalRef);
					}
				}
			}
			// if we got here, we didn't find an existing one
			if (createExternalDocRef) {
				ExternalDocumentRef retval = new ExternalDocumentRef(stModelStore, stDocumentUri,
						stModelStore.getNextId(IdType.DocumentRef, stDocumentUri), true);
				retval.setSpdxDocumentNamespace(externalDocUri);
				ModelObject.addValueToCollection(stModelStore, stDocumentUri, SpdxConstants.SPDX_DOCUMENT_ID, 
						SpdxConstants.PROP_SPDX_EXTERNAL_DOC_REF, retval, false);
				return Optional.of(retval);
			} else {
				return Optional.empty();
			}
		} finally {
			try {
				transaction.commit();
				transaction.close();
			} catch (IOException e) {
				logger.error("IO Error creating model transaction",e);
				throw new InvalidSPDXAnalysisException("IO Error creating model transaction",e);
			}
		}
	}

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
		if (docNamespace.get() instanceof IndividualUriValue) {
			String docUri = ((IndividualUriValue)(docNamespace.get())).getIndividualURI();
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
	
	public void setSpdxDocumentNamespace(@Nullable String documentNamespace) throws InvalidSPDXAnalysisException {
		if (Objects.isNull(documentNamespace)) {
			setPropertyValue(SpdxConstants.PROP_EXTERNAL_SPDX_DOCUMENT, null);
		} else {
			setPropertyValue(SpdxConstants.PROP_EXTERNAL_SPDX_DOCUMENT,
					new IndividualUriValue() {
						@Override
						public String getIndividualURI() {
							return documentNamespace;
						}
			});
		}
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
