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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.Version;
import org.spdx.library.model.compat.v2.enumerations.ChecksumAlgorithm;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.IModelStore.IdType;

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
	 * @param copyManager if non-null, create the external Doc ref if it is not a property of the SPDX Document
	 * @return
	 */
	public static Optional<ExternalDocumentRef> getExternalDocRefByDocNamespace(IModelStore stModelStore,
			String stDocumentUri, String externalDocUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(stModelStore, "Model store can not be null");
		Objects.requireNonNull(stDocumentUri, "Document URI can not be null");
		Objects.requireNonNull(externalDocUri, "External document URI can not be null");
		IModelStoreLock lock = stModelStore.enterCriticalSection(stDocumentUri, false);
		try {
			ModelCollection<ExternalDocumentRef> existingExternalRefs = new ModelCollection<ExternalDocumentRef>(stModelStore,stDocumentUri,
					SpdxConstantsCompatV2.SPDX_DOCUMENT_ID, SpdxConstantsCompatV2.PROP_SPDX_EXTERNAL_DOC_REF, copyManager, ExternalDocumentRef.class);
			for (Object externalRef:existingExternalRefs) {
				if (!(externalRef instanceof ExternalDocumentRef)) {
					logger.warn("Incorrect type for an external document ref: "+externalRef.getClass().toString());
				} else {
					String externalRefNamespace = ((ExternalDocumentRef)externalRef).getSpdxDocumentNamespace();
					if (externalRefNamespace.isEmpty()) {
						logger.warn("Namespace missing for external doc ref "+((ExternalDocumentRef)externalRef).getId());
					}
					if (externalDocUri.equals(externalRefNamespace)) {
						return Optional.of((ExternalDocumentRef)externalRef);
					}
				}
			}
			// if we got here, we didn't find an existing one, need to create one
			if (Objects.nonNull(copyManager)) {
				ExternalDocumentRef retval = new ExternalDocumentRef(stModelStore, stDocumentUri,
						stModelStore.getNextId(IdType.DocumentRef, stDocumentUri), copyManager, true);
				retval.setSpdxDocumentNamespace(externalDocUri);
				ModelObject.addValueToCollection(stModelStore, stDocumentUri, SpdxConstantsCompatV2.SPDX_DOCUMENT_ID, 
						SpdxConstantsCompatV2.PROP_SPDX_EXTERNAL_DOC_REF, retval, copyManager);
				return Optional.of(retval);
			} else {
				return Optional.empty();
			}
		} finally {
			stModelStore.leaveCriticalSection(lock);
		}
	}

	/**
	 * Default model store, copy manager, and document URI
	 * @throws InvalidSPDXAnalysisException
	 */
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
			logger.warn("Invalid external document reference ID "+id+
					".  Must be of the format "+SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PATTERN.pattern());
		}
	}

	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this model
	 * @param id ID for this object - must be unique within the SPDX document
	 * @param copyManager - if supplied, model objects will be implicitly copied into this model store and document URI when referenced by setting methods
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalDocumentRef(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
		if (!SpdxVerificationHelper.isValidExternalDocRef(id)) {
			logger.warn("Invalid external document reference ID "+id+
					".  Must be of the format "+SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PATTERN.pattern());
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstantsCompatV2.CLASS_EXTERNAL_DOC_REF;
	}
	
	/**
	 * Gets the absolute URI representing the document
	 * @param document
	 * @return
	 */
	private String documentToDocumentUri(SpdxDocument document) {
		Objects.requireNonNull(document, "Document can not be null");
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
		return (Optional<Checksum>)(Optional<?>)getObjectPropertyValue(SpdxConstantsCompatV2.PROP_EXTERNAL_DOC_CHECKSUM);
	}
	
	/**
	 * @param checksum the checksum to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ExternalDocumentRef setChecksum(Checksum checksum) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(checksum)) {
				throw new InvalidSPDXAnalysisException("Null value for a required checksum");
			}
			List<String> verify = checksum.verify(new HashSet<String>(), Version.CURRENT_SPDX_VERSION);
			if (verify.size() > 0) {
				throw new InvalidSPDXAnalysisException("Invalid checksum: "+verify.get(0));
			}
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_EXTERNAL_DOC_CHECKSUM, checksum);
		return this;
	}
	
	/**
	 * @return the spdxDocumentNamespace or empty string if no namespace
	 */
	public String getSpdxDocumentNamespace() throws InvalidSPDXAnalysisException {
		Optional<Object> docNamespace = getModelStore().getValue(getDocumentUri(), getId(), SpdxConstantsCompatV2.PROP_EXTERNAL_SPDX_DOCUMENT);
		if (!docNamespace.isPresent()) {
			logger.warn("SPDX document namespace not found");
			return "";
		}
		if (docNamespace.get() instanceof IndividualUriValue) {
			String docUri = ((IndividualUriValue)docNamespace.get()).getIndividualURI();
			if (Objects.isNull(docUri)) {
				logger.warn("Missing individual URI in doc namespace");
				return "";
			}
			return docUri;
		} else if (docNamespace.get() instanceof String) {
			logger.warn("Spdx Document Namespace is of type literal string.  Reccomended type is IndividualValue");
			return (String)docNamespace.get();
		} else {
			logger.error("SPDX document namespace is not of type IndividualValue or String.  Type="+docNamespace.get().getClass().toString());
			throw new SpdxInvalidTypeException("SPDX document namespace is not of type IndividualValue or String");
		}
	}
	
	/**
	 * Set the document namespace
	 * @param documentNamespace
	 * @throws InvalidSPDXAnalysisException
	 */
	public ExternalDocumentRef setSpdxDocumentNamespace(String documentNamespace) throws InvalidSPDXAnalysisException {
		if (Objects.isNull(documentNamespace)) {
			if (strict) {
				throw new InvalidSPDXAnalysisException("Null value for a required docment namespace");
			} else {
				setPropertyValue(SpdxConstantsCompatV2.PROP_EXTERNAL_SPDX_DOCUMENT, null);
			}
		} else {
			if (strict && !SpdxVerificationHelper.isValidUri(documentNamespace)) {
				throw new InvalidSPDXAnalysisException("Invalid document namespace.  Must be a valid URI.");
			}
			setPropertyValue(SpdxConstantsCompatV2.PROP_EXTERNAL_SPDX_DOCUMENT,
					new IndividualUriValue() {
						@Override
						public String getIndividualURI() {
							return documentNamespace;
						}
						
						@Override
						public boolean equals(Object comp) {
							return SimpleUriValue.isIndividualUriValueEquals(this, comp);
						}

						@Override
						public int hashCode() {
							return SimpleUriValue.getIndividualUriValueHash(this);
						}
			});
		}
		return this;
	}
	
	/**
	 * Returns the SPDX document if it exists within the same model store, otherwise it returns Optional.empty
	 * @return the spdxDocument
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public Optional<SpdxDocument> getSpdxDocument() throws InvalidSPDXAnalysisException {
		String docNamespace = getSpdxDocumentNamespace();
		if (docNamespace.isEmpty()) {
			return Optional.empty();
		}
		if (this.getModelStore().exists(docNamespace, SpdxConstantsCompatV2.SPDX_DOCUMENT_ID)) {
			return (Optional<SpdxDocument>)(Optional<?>)Optional.of(SpdxModelFactory.createModelObject(
					getModelStore(), docNamespace, SpdxConstantsCompatV2.SPDX_DOCUMENT_ID, 
					SpdxConstantsCompatV2.CLASS_SPDX_DOCUMENT, getCopyManager()));
		} else {
			return Optional.empty();
		}
	}

	/**
	 * @param spdxDocument the spdxDocument to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ExternalDocumentRef setSpdxDocument(SpdxDocument spdxDocument) throws InvalidSPDXAnalysisException {
		setSpdxDocumentNamespace(documentToDocumentUri(spdxDocument));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = new ArrayList<>();
		if (!getId().startsWith(SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM)) {
			retval.add("Invalid external ref ID: "+getId()+".  Must start with "+SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM+".");
		}
		String spdxDocumentNamespace = "UNKNOWN";
		try {
			spdxDocumentNamespace = getSpdxDocumentNamespace();
			if (spdxDocumentNamespace.isEmpty()) {
				retval.add("Missing required external document URI");
			} else {
				if (!SpdxVerificationHelper.isValidUri(spdxDocumentNamespace)) {
					retval.add("Invalid URI for external Spdx Document URI: "+spdxDocumentNamespace);
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
				retval.add("Missing checksum for external document "+getId());
			} else {
				retval.addAll(checksum.get().verify(verifiedIds, Version.CURRENT_SPDX_VERSION));
				if (checksum.get().getAlgorithm() != ChecksumAlgorithm.SHA1) {
					retval.add("Checksum algorithm is not SHA1 for external reference "+getId());
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("error getting checksum",e);
			retval.add("Error getting checksum for "+getId());
		}
		return retval;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ExternalDocumentRef o) {
		String myDocumentNamespace;
		try {
			myDocumentNamespace = getSpdxDocumentNamespace();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting document namepsace",e);
			myDocumentNamespace = "";
		}
		String compareDocumentNamespace;
		try {
			compareDocumentNamespace = o.getSpdxDocumentNamespace();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare document namepsace",e);
			compareDocumentNamespace = "";
		}
		int retval = myDocumentNamespace.compareTo(compareDocumentNamespace);
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
