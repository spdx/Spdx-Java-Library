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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.pointer.ByteOffsetPointer;
import org.spdx.library.model.pointer.LineCharPointer;
import org.spdx.library.model.pointer.SinglePointer;
import org.spdx.library.model.pointer.StartEndPointer;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.IModelStore.IdType;

/**
 * Snippets can optionally be used when a file is known to have some content that has been included from another original source.  
 * They are useful for denoting when part of a file may have been originally created under another license.
 * Each instance of Snippet Information needs to be associated with a specific File in an SPDX Document.
 * 
 * @author Gary O'Neall
 *
 */
public class SpdxSnippet extends SpdxItem implements Comparable<SpdxSnippet> {
	
	private Collection<StartEndPointer> allRanges;

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxSnippet() throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore().getNextId(IdType.Anonymous, DefaultModelStore.getDefaultDocumentUri()));
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxSnippet(String id) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(),
				id, DefaultModelStore.getDefaultCopyManager(), true);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SpdxSnippet(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
		allRanges = new ModelCollection(modelStore, documentUri, id, SpdxConstants.PROP_SNIPPET_RANGE, copyManager, StartEndPointer.class);
	}
	
	/**
	 * @param spdxSnippetBuilder
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxSnippet(SpdxSnippetBuilder spdxSnippetBuilder) throws InvalidSPDXAnalysisException {
		this(spdxSnippetBuilder.modelStore, spdxSnippetBuilder.documentUri, spdxSnippetBuilder.id, 
				spdxSnippetBuilder.copyManager, true);
		setCopyrightText(spdxSnippetBuilder.copyrightText);
		setName(spdxSnippetBuilder.name);
		setLicenseConcluded(spdxSnippetBuilder.concludedLicense);
		getLicenseInfoFromFiles().addAll(spdxSnippetBuilder.licenseInfosFromFile);
		setSnippetFromFile(spdxSnippetBuilder.snippetFromFile);	// Note: this should be before the byterange so that the file in the byte range is properly set
		setByteRange(spdxSnippetBuilder.startByte, spdxSnippetBuilder.endByte);
		
		// optional parameters - SpdxElement
		getAnnotations().addAll(spdxSnippetBuilder.annotations);
		getRelationships().addAll(spdxSnippetBuilder.relationships);
		setComment(spdxSnippetBuilder.comment);
		
		// optional parameters - SpdxItem
		setLicenseComments(spdxSnippetBuilder.licenseComments);
		getAttributionText().addAll(spdxSnippetBuilder.attributionText);
		
		// optional parameters - SpdxSnippet
		if (spdxSnippetBuilder.startLine > 0) {
			setLineRange(spdxSnippetBuilder.startLine, spdxSnippetBuilder.endLine);
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_SNIPPET;
	}
	
	@Override
	public String getLicenseInfoFromFilesPropertyName() {
		return SpdxConstants.PROP_LICENSE_INFO_FROM_SNIPPETS;
	}
	
	
	/**
	 * @return the snippetFromFile, null if the file is not present in the model
	 * @throws InvalidSPDXAnalysisException 
	 */
	public @Nullable SpdxFile getSnippetFromFile() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstants.PROP_SNIPPET_FROM_FILE);
		if (!retval.isPresent()) {
			return null;
		}
		if (!(retval.get() instanceof SpdxFile)) {
			throw new SpdxInvalidTypeException("Invalid type returned for getSnippetFromFile.  Expected SpdxFile, returned type "+retval.get().getClass().toString());
		}
		return (SpdxFile)retval.get();
	}
	
	/**
	 * @param snippetFromFile the snippetFromFile to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxSnippet setSnippetFromFile(SpdxFile snippetFromFile) throws InvalidSPDXAnalysisException {
		if (strict && Objects.isNull(snippetFromFile)) {
			throw new InvalidSPDXAnalysisException("Can not set required snippetFromFile to null");
		}
		setPropertyValue(SpdxConstants.PROP_SNIPPET_FROM_FILE, snippetFromFile);
		// Update the references in the ranges
		StartEndPointer byteRange = getByteRange();
		if (byteRange != null) {
			if (!strict) {
				byteRange.setStrict(strict);
			}
			if (byteRange.getStartPointer() != null) {
				byteRange.getStartPointer().setReference(snippetFromFile);
			}
			if (byteRange.getEndPointer() != null) {
				byteRange.getEndPointer().setReference(snippetFromFile);
			}
		}
		Optional<StartEndPointer> lineRange = getLineRange();
		if (lineRange.isPresent()) {
			if (!strict) {
				lineRange.get().setStrict(strict);
			}
			if (lineRange.get().getStartPointer() != null) {
				lineRange.get().getStartPointer().setReference(snippetFromFile);
			}
			if (lineRange.get().getEndPointer() != null) {
				lineRange.get().getEndPointer().setReference(snippetFromFile);
			}
		}
		return this;
	}

	/**
	 * @return the byteRange or null if no byte range is present
	 * @throws InvalidSPDXAnalysisException 
	 */
	public StartEndPointer getByteRange() throws InvalidSPDXAnalysisException {
		for (StartEndPointer range:allRanges) {
			if (range.getStartPointer() instanceof ByteOffsetPointer) {
				if (!(range.getEndPointer() instanceof ByteOffsetPointer)) {
					logger.error("Incompatable start and end pointer types - must both be offset or line types");
					throw new InvalidSPDXAnalysisException("Incompatable start and end snippet specification - mixing byte and line ranges");
				}
				return range;
			}
		}
		return null;
	}
	
	/**
	 * @param startByte first byte of the range
	 * @param endByte end byte of the range
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxSnippet setByteRange(int startByte, int endByte) throws InvalidSPDXAnalysisException {
		SpdxFile snippetFromFile = getSnippetFromFile();
		if (strict) {
			if (endByte <= startByte) {
				throw new InvalidSPDXAnalysisException("Ending byte of a range must be greater than the starting byte");
			}
			if (Objects.isNull(snippetFromFile)) {
				throw new InvalidSPDXAnalysisException("Snippets from file must be set prior to setting byte range");
			}
		}
		ByteOffsetPointer startPointer = createByteOffsetPointer(snippetFromFile, startByte);
		ByteOffsetPointer endPointer = createByteOffsetPointer(snippetFromFile, endByte);
		StartEndPointer byteRange = createStartEndPointer(startPointer, endPointer);
		List<StartEndPointer> existing = new ArrayList<StartEndPointer>();
		for (StartEndPointer range:allRanges) {
			if (range.getStartPointer() instanceof ByteOffsetPointer) {
				existing.add(range);
			}
		}
		allRanges.removeAll(existing);
		allRanges.add(byteRange);
		return this;
	}
	
	/**
	 * @return the lineRange
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<StartEndPointer> getLineRange() throws InvalidSPDXAnalysisException {
		for (StartEndPointer range:allRanges) {
			if (range.getStartPointer() instanceof LineCharPointer) {
				if (!(range.getEndPointer() instanceof LineCharPointer)) {
					logger.error("Incompatable start and end pointer types - must both be offset or line types");
					throw new InvalidSPDXAnalysisException("Incompatable start and end snippet specification - mixing byte and line ranges");
				}
				return Optional.of(range);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * @param startLine the start position of lineRange to set, inclusive
	 * @param endLine   the end position of lineRange to set, exclusive
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxSnippet setLineRange(int startLine, int endLine) throws InvalidSPDXAnalysisException {
		SpdxFile snippetFromFile = getSnippetFromFile();
		if (strict) {
			if (endLine <= startLine) {
				throw new InvalidSPDXAnalysisException("Ending line of a range must be greater than the starting line");
			}
			if (Objects.isNull(snippetFromFile)) {
				throw new InvalidSPDXAnalysisException("Snippets from file must be set prior to setting line range");
			}
		}
		LineCharPointer startPointer = createLineCharPointer(snippetFromFile, startLine);
		LineCharPointer endPointer = createLineCharPointer(snippetFromFile, endLine);
		StartEndPointer lineRange = createStartEndPointer(startPointer, endPointer);
		List<StartEndPointer> existing = new ArrayList<StartEndPointer>();
		for (StartEndPointer range:allRanges) {
			if (range.getStartPointer() instanceof LineCharPointer) {
				existing.add(range);
			}
		}
		allRanges.removeAll(existing);
		if (Objects.nonNull(lineRange)) {
			setPointerReferences(lineRange);
			allRanges.add(lineRange);
		}
		return this;
	}
	
	
	/**
	 * Set the reference for the pointers to the snippetFromFile
	 * @param pointer
	 * @throws InvalidSPDXAnalysisException 
	 */
	private void setPointerReferences(@Nullable StartEndPointer pointer) throws InvalidSPDXAnalysisException {
		if (Objects.isNull(pointer)) {
			return;
		}
		SpdxFile fromFile = getSnippetFromFile();
		if (Objects.nonNull(fromFile)) {
			SinglePointer startPointer = pointer.getStartPointer();
			if (Objects.nonNull(startPointer)) {
				startPointer.setReference(fromFile);
			}
			SinglePointer endPointer = pointer.getEndPointer();
			if (Objects.nonNull(endPointer)) {
				endPointer.setReference(fromFile);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.SpdxItem#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = super._verify(verifiedIds, specVersion);
		
		String snippetName = "[Unnamed Snippet]";
		Optional<String> name;
		try {
			name = getName();
			if (name.isPresent()) {
				snippetName = name.get();
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting name: "+e.getMessage());
		}
		SpdxFile snippetFromFile;
		try {
			snippetFromFile = getSnippetFromFile();
			if (snippetFromFile == null) {
				retval.add("Missing snippet from file in Snippet "+snippetName);
			} else {
				retval.addAll(snippetFromFile.verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting snippetFromFile: "+e.getMessage());
		}
		StartEndPointer byteRange;
		try {
			byteRange = getByteRange();
			if (byteRange == null) {
				retval.add("Missing snippet byte range from Snippet "+snippetName);
			} else {
				retval.addAll(byteRange.verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting byteRange: "+e.getMessage());
		}
		Optional<StartEndPointer> lineRange;
		try {
			lineRange = getLineRange();
			if (lineRange.isPresent()) {
				retval.addAll(lineRange.get().verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting lineRange: "+e.getMessage());
		}
		return retval;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SpdxSnippet o) {
		try {
			if (o == null) {
				return 1;
			}
			int retval = 0;
			Optional<String> name = getName();
			Optional<String> compName = o.getName();
			if (name.isPresent()) {
				if (compName.isPresent()) {
					retval = name.get().compareTo(compName.get());
				} else {
					return 1;
				}
			}
			SpdxFile snippetFromFile = getSnippetFromFile();
			SpdxFile compSnippetFromFile = o.getSnippetFromFile();
			if (retval == 0 && snippetFromFile != null) {
					retval = snippetFromFile.compareTo(compSnippetFromFile);
			}
			if (retval == 0) {
				StartEndPointer byteRange = getByteRange();
				StartEndPointer compByteRange = o.getByteRange();
				if (byteRange != null) {
					return byteRange.compareTo(compByteRange);
				} else {
					if (o.getByteRange() == null) {
						return 0;
					} else {
						return 1;
					}
				}
			}
			return retval;
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error getting compare for snippet",e);
			return -1;
		}	
	}
	
	@Override
	public String toString() {
		Optional<String> name;
		try {
			name = getName();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error getting name",e);
			name = Optional.empty();
		}
		if (name.isPresent() && !name.get().isEmpty()) {
			return name.get();
		}
		StringBuilder sb = new StringBuilder();
		SpdxFile snippetFromFile;
		try {
			snippetFromFile = getSnippetFromFile();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error getting snippetFromFile",e);
			snippetFromFile = null;
		}
		if (snippetFromFile != null) {
			Optional<String> fileName;
			try {
				fileName = snippetFromFile.getName();
			} catch (InvalidSPDXAnalysisException e) {
				logger.warn("Error getting snippetFromFile fileName",e);
				fileName = Optional.empty();
			}
			if (fileName.isPresent() && !fileName.get().isEmpty()) {
				sb.append(fileName);
			} else {
				sb.append("FileID ");
				sb.append(snippetFromFile.getId());
			}
			sb.append(": ");
		}
		StartEndPointer byteRange;
		try {
			byteRange = getByteRange();
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error getting byteRange",e);
			byteRange = null;
		}
		if (byteRange != null) {
			sb.append(byteRange.toString());
		} else {
			sb.append("[No byte range set]");
		}
		return sb.toString();
	}
	
	public static class SpdxSnippetBuilder {
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
		
		// required fields - SpdxSnippet
		SpdxFile snippetFromFile;
		int startByte;
		int endByte;
		
		// optional fields - SpdxElement
		Collection<Annotation> annotations = new ArrayList<Annotation>();
		Collection<Relationship> relationships = new ArrayList<Relationship>();
		String comment = null;
		
		// optional fields - SpdxItem
		String licenseComments = null;
		Collection<String> attributionText = new ArrayList<String>();
		
		// optional fields - SpdxSnippet
		int startLine = -1;
		int endLine = -1;
		
		/**
		 * Build a snippet with the required parameters
		 * @param modelStore Storage for the model objects
		 * @param documentUri SPDX Document URI for a document associated with this model
		 * @param id ID for this object - must be unique within the SPDX document
		 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
		 * @param name - File name
		 * @param concludedLicense license concluded
		 * @param licenseInfosFromFile collection of seen licenses
		 * @param copyrightText Copyright text
		 * @param snippetFromFile File where the snippet is located
		 * @param startByte first byte of the snippet in the file
		 * @param endByte end byte of the snippet in the file
		 */
		public SpdxSnippetBuilder(IModelStore modelStore, String documentUri, String id, 
				@Nullable ModelCopyManager copyManager, String name,
				AnyLicenseInfo concludedLicense, Collection<AnyLicenseInfo> licenseInfosFromFile,
				String copyrightText, SpdxFile snippetFromFile, int startByte, int endByte) {
			Objects.requireNonNull(modelStore, "Model store can not be null");
			Objects.requireNonNull(documentUri, "Document URI can not be null");
			Objects.requireNonNull(id, "ID can not be null");
			Objects.requireNonNull(name, "Name can not be null");
			Objects.requireNonNull(snippetFromFile, "Snippet from file can not be null");
			this.modelStore = modelStore;
			this.documentUri = documentUri;
			this.id = id;
			this.name = name;
			this.concludedLicense = concludedLicense;
			this.licenseInfosFromFile = licenseInfosFromFile;
			this.copyrightText = copyrightText;
			this.startByte = startByte;
			this.endByte = endByte;
			this.snippetFromFile = snippetFromFile;
			this.copyManager = copyManager;
		}
		
		/**
		 * @param annotations Annotations
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder setAnnotations(Collection<Annotation> annotations) {
			Objects.requireNonNull(annotations, "Annotations can not be null");
			this.annotations = annotations;
			return this;
		}
		
		/**
		 * @param annotation Annotation to add
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder addAnnotation(Annotation annotation) {
			Objects.requireNonNull(annotation, "Annotation can not be null");
			this.annotations.add(annotation);
			return this;
		}
		
		/**
		 * @param relationships Relationships
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder setRelationship(Collection<Relationship> relationships) {
			Objects.requireNonNull(relationships, "Relationships can not be null");
			this.relationships = relationships;
			return this;
		}
		
		/**
		 * @param relationship Relationship to add
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder addRelationship(Relationship relationship) {
			Objects.requireNonNull(relationship, "Relationship can not be null");
			this.relationships.add(relationship);
			return this;
		}
		
		/**
		 * @param comment Comment
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder setComment(@Nullable String comment) {
			this.comment = comment;
			return this;
		}
		
		/**
		 * @param licenseComments
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder setLicenseComments(@Nullable String licenseComments) {
			this.licenseComments = licenseComments;
			return this;
		}
		
		/**
		 * Set the attribution test
		 * @param attributionText Attribution text for the file
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder setAttributionText(Collection<String> attributionText) {
			Objects.requireNonNull(attributionText, "Attribution text collection can not be null");
			this.attributionText = attributionText;
			return this;
		}
		
		/**
		 * Add attribution to the attribution text collection
		 * @param attribution
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder addAttributionText(String attribution) {
			Objects.requireNonNull(attribution, "Attribution text can not be null");
			this.attributionText.add(attribution);
			return this;
		}
		
		/**
		 * @param startLine first line of the snippet
		 * @param endLine end line of the snippet
		 * @return this to continue the build
		 */
		public SpdxSnippetBuilder setLineRange(int startLine, int endLine) {
			this.startLine = startLine;
			this.endLine = endLine;
			return this;
		}
		
		public SpdxSnippet build() throws InvalidSPDXAnalysisException {
			IModelStoreLock lock = modelStore.enterCriticalSection(documentUri, false);
			try {
				return new SpdxSnippet(this);
			} finally {
				modelStore.leaveCriticalSection(lock);
			}
		}
	}
}
