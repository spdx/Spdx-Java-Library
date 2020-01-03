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

import javax.annotation.Nullable;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;
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
			if (byteRange.getStartPointer() != null) {
				byteRange.getStartPointer().setReference(snippetFromFile);
			}
			if (byteRange.getEndPointer() != null) {
				byteRange.getEndPointer().setReference(snippetFromFile);
			}
		}
		Optional<StartEndPointer> lineRange = getLineRange();
		if (lineRange.isPresent()) {
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
	 * @param byteRange the byteRange to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxSnippet setByteRange(StartEndPointer byteRange) throws InvalidSPDXAnalysisException {
		if (strict && Objects.isNull(byteRange)) {
			throw new InvalidSPDXAnalysisException("Can not set required byte range to null");
		}
		if (Objects.nonNull(byteRange) && !(byteRange.getStartPointer() instanceof ByteOffsetPointer)) {
			logger.error("Invalid start pointer type for byte offset range.  Must be ByteOffsetPointer");
			throw new InvalidSPDXAnalysisException("Invalid start pointer type for byte offset range.  Must be ByteOffsetPointer");
		}
		if (Objects.nonNull(byteRange) && !(byteRange.getEndPointer() instanceof ByteOffsetPointer)) {
			logger.error("Invalid end pointer type for byte offset range.  Must be ByteOffsetPointer");
			throw new InvalidSPDXAnalysisException("Invalid end pointer type for byte offset range.  Must be ByteOffsetPointer");
		}
		List<StartEndPointer> existing = new ArrayList<StartEndPointer>();
		for (StartEndPointer range:allRanges) {
			if (range.getStartPointer() instanceof ByteOffsetPointer) {
				existing.add(range);
			}
		}
		allRanges.removeAll(existing);
		if (Objects.nonNull(byteRange)) {
			allRanges.add(byteRange);
		}
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
	 * @param lineRange the lineRange to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxSnippet setLineRange(StartEndPointer lineRange) throws InvalidSPDXAnalysisException {
		if (Objects.nonNull(lineRange) && !(lineRange.getStartPointer() instanceof LineCharPointer)) {
			logger.error("Invalid start pointer type for line offset range.  Must be LineCharPointer");
			throw new InvalidSPDXAnalysisException("Invalid start pointer type for line offset range.  Must be LineCharPointer");
		}
		if (Objects.nonNull(lineRange) && !(lineRange.getEndPointer() instanceof LineCharPointer)) {
			logger.error("Invalid end pointer type for line offset range.  Must be LineCharPointer");
			throw new InvalidSPDXAnalysisException("Invalid end pointer type for line offset range.  Must be LineCharPointer");
		}
		List<StartEndPointer> existing = new ArrayList<StartEndPointer>();
		for (StartEndPointer range:allRanges) {
			if (range.getStartPointer() instanceof LineCharPointer) {
				existing.add(range);
			}
		}
		allRanges.removeAll(existing);
		if (Objects.nonNull(lineRange)) {
			allRanges.add(lineRange);
		}
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = super.verify();
		
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
				retval.addAll(snippetFromFile.verify());
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
				retval.addAll(byteRange.verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting byteRange: "+e.getMessage());
		}
		Optional<StartEndPointer> lineRange;
		try {
			lineRange = getLineRange();
			if (lineRange.isPresent()) {
				retval.addAll(lineRange.get().verify());
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
}
