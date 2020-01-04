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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.FileType;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.IModelStore.IdType;

/**
 * A File represents a named sequence of information 
 * that is contained in a software package.
 * @author Gary O'Neall
 */
public class SpdxFile extends SpdxItem implements Comparable<SpdxFile> {
	
	Collection<FileType> fileTypes; 
	Collection<Checksum> checksums;
	private Collection<String> fileContributors;

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxFile() throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore().getNextId(IdType.Anonymous, DefaultModelStore.getDefaultDocumentUri()));
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxFile(String id) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), id, 
				DefaultModelStore.getDefaultCopyManager(), true);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public SpdxFile(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
		fileTypes = (Collection<FileType>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstants.PROP_FILE_TYPE, FileType.class);
		checksums = (Collection<Checksum>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstants.PROP_FILE_CHECKSUM, Checksum.class);
		fileContributors = this.getStringCollection(SpdxConstants.PROP_FILE_CONTRIBUTOR);
	}

	protected SpdxFile(SpdxFileBuilder spdxFileBuilder) throws InvalidSPDXAnalysisException {
		this(spdxFileBuilder.modelStore, spdxFileBuilder.documentUri, spdxFileBuilder.id, 
				spdxFileBuilder.copyManager, true);
		setCopyrightText(spdxFileBuilder.copyrightText);
		setName(spdxFileBuilder.name);
		setLicenseConcluded(spdxFileBuilder.concludedLicense);
		addChecksum(spdxFileBuilder.sha1);
		getLicenseInfoFromFiles().addAll(spdxFileBuilder.licenseInfosFromFile);
		
		// optional parameters - SpdxElement
		getAnnotations().addAll(spdxFileBuilder.annotations);
		getRelationships().addAll(spdxFileBuilder.relationships);
		setComment(spdxFileBuilder.comment);
		
		// optional parameters - SpdxItem
		setLicenseComments(spdxFileBuilder.licenseComments);
		
		// optional parameters - SpdxFile
		Iterator<Checksum> iter = spdxFileBuilder.checksums.iterator();
		while (iter.hasNext()) {
			Checksum cksum = iter.next();
			if (!cksum.equals(spdxFileBuilder.sha1)) {
				getChecksums().add(cksum);
			}
		}
		getFileContributors().addAll(spdxFileBuilder.fileContributors);
		getFileTypes().addAll(spdxFileBuilder.fileTypes);
		setNoticeText(spdxFileBuilder.noticeText);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_FILE;
	}
	
	/**
	 * @return the Sha1 checksum value for this file, or a blank string if no 
	 * sha1 checksum has been set
	 */
	public String getSha1() throws InvalidSPDXAnalysisException {
		for (Checksum checksum:checksums) {
			if (checksum.getAlgorithm().equals(ChecksumAlgorithm.SHA1)) {
				if (!checksum.getValue().isEmpty()) {
					return checksum.getValue();
				}
			}
		}
		return "";
	}
	
	@Override
	public SpdxFile setCopyrightText(@Nullable String copyrightText) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(copyrightText) || copyrightText.isEmpty()) {
				throw new InvalidSPDXAnalysisException("Can not set required copyright text to null or empty");
			}
		}
		super.setCopyrightText(copyrightText);
		return this;
	}
	
	@Override
	public SpdxFile setName(@Nullable String name) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(name) || name.isEmpty()) {
				throw new InvalidSPDXAnalysisException("Can not set required name to null or empty");
			}
		}
		super.setName(name);
		return this;
	}
	
	@Override 
	public SpdxFile setLicenseConcluded(@Nullable AnyLicenseInfo license) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(license)) {
				throw new InvalidSPDXAnalysisException("Can not set required concluded license to null");
			}
		}
		super.setLicenseConcluded(license);
		return this;
	}
	
	@Override
	public SpdxFile setLicenseComments(String licenseComments) throws InvalidSPDXAnalysisException {
		super.setLicenseComments(licenseComments);
		return this;
	}
	
	@Override
	protected String getLicenseInfoFromFilesPropertyName() {
		return SpdxConstants.PROP_FILE_SEEN_LICENSE;
	}
	
	@Override
	protected String getNamePropertyName() {
		return SpdxConstants.PROP_FILE_NAME;
	}
	
	/**
	 * @return File types for the file
	 * @throws InvalidSPDXAnalysisException
	 */
	public Collection<FileType> getFileTypes() throws InvalidSPDXAnalysisException {
		return fileTypes;
	}
	
	/**
	 * Add a file type to this file
	 * @param fileType
	 * @return true if the list was modified
	 * @throws InvalidSPDXAnalysisException 
	 */
	public boolean addFileType(FileType fileType) throws InvalidSPDXAnalysisException {
		return fileTypes.add(fileType);
	}
	
	/**
	 * @return the checksums
	 */
	public Collection<Checksum> getChecksums() {
		return checksums;
	}
	
	/**
	 * Add a checksum
	 * @param checksum
	 * @return true if the list was modified
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean addChecksum(Checksum checksum) throws InvalidSPDXAnalysisException {
		return checksums.add(checksum);
	}
	
	/**
	 * @return the fileContributors
	 */
	public Collection<String> getFileContributors() {
		return fileContributors;
	}
	
	/**
	 * Add a file contributor to the file contributors collection
	 * @param contributor
	 * @return
	 */
	public boolean addFileContributor(String contributor) {
		if (Objects.nonNull(contributor)) {
			return fileContributors.add(contributor);
		} else {
			return false;
		}
	}
	
	/**
	 * @return the noticeText
	 */
	public Optional<String> getNoticeText() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_FILE_NOTICE);
	}
	
	/**
	 * @param noticeText the noticeText to set
	 * @return this so you can chain setters
	 */
	public SpdxFile setNoticeText(@Nullable String noticeText) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_FILE_NOTICE, noticeText);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = super.verify();
		String fileName = "UNKNOWN";
		try {
			Optional<String> myName = this.getName();
			if (myName.isPresent()) {
				fileName = myName.get();
			} else {
				retval.add("Missing required file name");
			}
		} catch(InvalidSPDXAnalysisException e) {
			retval.add("Error getting file name");
		}
		for (Checksum checksum:checksums) {
			retval.addAll(addNameToWarnings(checksum.verify()));
		}
		String sha1;
		try {
			sha1 = getSha1();
			if (sha1 == null || sha1.isEmpty()) {
				retval.add("Missing required SHA1 hashcode value for "+fileName);
			} else {
				String warning = SpdxVerificationHelper.verifyChecksumString(sha1, ChecksumAlgorithm.SHA1);
				if (warning != null) {
					retval.add(warning + " for file "+fileName);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting sha1");
		}
		return retval;
	}

	@Override
	public int compareTo(SpdxFile o) {
		String name = "";
		try {
			Optional<String> myName = getName();
			if (myName.isPresent()) {
				name = myName.get();
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting my name on compare");
		}
		String compName = "";
		try {
			Optional<String> compareName = o.getName();
			if (compareName.isPresent()) {
				compName = compareName.get();
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare name on compare");
		}
		return name.compareTo(compName);
	}
	
	public static class SpdxFileBuilder {
		// required fields - Model Object
		IModelStore modelStore;
		String documentUri;
		String id;
		ModelCopyManager copyManager;
		
		// required fields - SpdxElement
		String name;
		
		// required fields - SpdxItem
		AnyLicenseInfo concludedLicense;
		Collection<AnyLicenseInfo> licenseInfosFromFile;
		String copyrightText;
		
		// required fields - SpdxFile
		Checksum sha1;
		
		// optional fields - SpdxElement
		Collection<Annotation> annotations = new ArrayList<Annotation>();
		Collection<Relationship> relationships = new ArrayList<Relationship>();
		String comment = null;
		
		// optional fields - SpdxItem
		String licenseComments = null;
		
		// optional fields - SpdxFile
		Collection<Checksum> checksums = new ArrayList<Checksum>();
		Collection<String> fileContributors = new ArrayList<String>();
		Collection<FileType> fileTypes = new ArrayList<FileType>();
		String noticeText = null;
		
		/**
		 * Build a file with the required parameters
		 * @param modelStore Storage for the model objects
		 * @param documentUri SPDX Document URI for a document associated with this model
		 * @param id ID for this object - must be unique within the SPDX document
		 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
		 * @param name - File name
		 * @param concludedLicense license concluded
		 * @param licenseInfosFromFile collection of seen licenses
		 * @param copyrightText Copyright text
		 * @param sha1 - Sha1 checksum value
		 */
		public SpdxFileBuilder(IModelStore modelStore, String documentUri, String id, 
				@Nullable ModelCopyManager copyManager, String name,
				AnyLicenseInfo concludedLicense, Collection<AnyLicenseInfo> licenseInfosFromFile,
				String copyrightText, Checksum sha1) {
			Objects.requireNonNull(modelStore);
			Objects.requireNonNull(documentUri);
			Objects.requireNonNull(id);
			Objects.requireNonNull(name);
			Objects.requireNonNull(concludedLicense);
			Objects.requireNonNull(licenseInfosFromFile);
			Objects.requireNonNull(copyrightText);
			Objects.requireNonNull(sha1);
			this.modelStore = modelStore;
			this.documentUri = documentUri;
			this.id = id;
			this.name = name;
			this.concludedLicense = concludedLicense;
			this.licenseInfosFromFile = licenseInfosFromFile;
			this.copyrightText = copyrightText;
			this.sha1 = sha1;
			this.copyManager = copyManager;
		}
		
		/**
		 * @param annotations Annotations
		 * @return this to continue the build
		 */
		public SpdxFileBuilder setAnnotations(Collection<Annotation> annotations) {
			Objects.requireNonNull(annotations);
			this.annotations = annotations;
			return this;
		}
		
		/**
		 * @param annotation Annotation to add
		 * @return this to continue the build
		 */
		public SpdxFileBuilder addAnnotation(Annotation annotation) {
			Objects.requireNonNull(annotation);
			this.annotations.add(annotation);
			return this;
		}
		
		/**
		 * @param relationships Relationships
		 * @return this to continue the build
		 */
		public SpdxFileBuilder setRelationship(Collection<Relationship> relationships) {
			Objects.requireNonNull(relationships);
			this.relationships = relationships;
			return this;
		}
		
		/**
		 * @param relationship Relationship to add
		 * @return this to continue the build
		 */
		public SpdxFileBuilder addRelationship(Relationship relationship) {
			Objects.requireNonNull(relationship);
			this.relationships.add(relationship);
			return this;
		}
		
		/**
		 * @param comment Comment
		 * @return this to continue the build
		 */
		public SpdxFileBuilder setComment(@Nullable String comment) {
			this.comment = comment;
			return this;
		}
		/**
		 * @param licenseComments
		 * @return this to continue the build
		 */
		public SpdxFileBuilder setLicenseComments(@Nullable String licenseComments) {
			this.licenseComments = licenseComments;
			return this;
		}
		
		/**
		 * @param checksums Checksum
		 * @return this to continue the build
		 */
		public SpdxFileBuilder setChecksums(Collection<Checksum> checksums) {
			Objects.requireNonNull(checksums);
			this.checksums = checksums;
			return this;
		}
		
		/**
		 * @param checksum Checksum to add
		 * @return this to continue the build
		 */
		public SpdxFileBuilder addChecksum(Checksum checksum) {
			Objects.requireNonNull(checksum);
			this.checksums.add(checksum);
			return this;
		}
		
		/**
		 * @param fileContributors Contributors to the file
		 * @return this to continue the build
		 */
		public SpdxFileBuilder setFileContributors(Collection<String> fileContributors) {
			Objects.requireNonNull(fileContributors);
			this.fileContributors = fileContributors;
			return this;
		}
		
		/**
		 * @param fileContributor File contributor to add
		 * @return this to continue the build
		 */
		public SpdxFileBuilder addFileContributor(String fileContributor) {
			Objects.requireNonNull(fileContributor);
			this.fileContributors.add(fileContributor);
			return this;
		}
		
		/**
		 * @param fileTypes file types
		 * @return this to continue the build
		 */
		public SpdxFileBuilder setFileTypes(Collection<FileType> fileTypes) {
			Objects.requireNonNull(fileTypes);
			this.fileTypes = fileTypes;
			return this;
		}
		
		/**
		 * @param fileType file type to add
		 * @return this to continue the build
		 */
		public SpdxFileBuilder addFileType(FileType fileType) {
			Objects.requireNonNull(fileType);
			this.fileTypes.add(fileType);
			return this;
		}
		
		/**
		 * @param noticeText Notice text found in the file
		 * @return this to continue the build
		 */
		public SpdxFileBuilder setNoticeText(@Nullable String noticeText) {
			this.noticeText = noticeText;
			return this;
		}
		
		public SpdxFile build() throws InvalidSPDXAnalysisException {
			IModelStoreLock lock = modelStore.enterCriticalSection(documentUri, false);
			try {
				return new SpdxFile(this);
			} finally {
				modelStore.leaveCriticalSection(lock);
			}
		}
	}
}
