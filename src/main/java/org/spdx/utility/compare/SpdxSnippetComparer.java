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
package org.spdx.utility.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.compat.v2.SpdxDocument;
import org.spdx.library.model.compat.v2.SpdxFile;
import org.spdx.library.model.compat.v2.SpdxItem;
import org.spdx.library.model.compat.v2.SpdxSnippet;
import org.spdx.library.model.compat.v2.pointer.StartEndPointer;

/**
 * Compares two SPDX snippets.  The <code>compare(snippetA, snippetB)</code> method will perform the comparison and
 * store the results.  <code>isDifferenceFound()</code> will return true of any 
 * differences were found.
 * @author Gary O'Neall
 *
 */
public class SpdxSnippetComparer extends SpdxItemComparer {

	private boolean inProgress = false;
	private boolean differenceFound = false;
	private boolean byteRangeEquals = true;
	private boolean lineRangeEquals = true;
	private boolean snippetFromFilesEquals = true;
	private boolean nameEquals = true;
	/**
	 * Map of any difference between snippet from files where the file names are equal
	 */
	Map<SpdxDocument, Map<SpdxDocument, SpdxFileDifference>> snippetFromFileDifferences = new HashMap<>();
	/**
	 * Map of snippetFromFiles where the file names are different (and therefore considered a unique file)
	 */
	Map<SpdxDocument, Map<SpdxDocument, SpdxFile>> uniqueSnippetFromFile = new HashMap<>();
	/**
	 * @param extractedLicenseIdMap map of all extracted license IDs for any SPDX documents to be added to the comparer
	 */
	public SpdxSnippetComparer(Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap) {
		super(extractedLicenseIdMap);
	}

	/**
	 * Add a snippet to the comparer and performs the comparison to any existing documents
	 * @param spdxDocument document containing the package
	 * @param snippet snippet to be added
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void addDocumentSnippet(SpdxDocument spdxDocument,
			SpdxSnippet snippet) throws SpdxCompareException, InvalidSPDXAnalysisException {
		checkInProgress();
		if (this.name == null) {
			this.name = snippet.toString();
		}
		inProgress = true;
		Iterator<Entry<SpdxDocument, SpdxItem>> iter = this.documentItem.entrySet().iterator();
		SpdxSnippet snippet2 = null;
		SpdxDocument document2 = null;
		while (iter.hasNext() && snippet2 == null) {
			Entry<SpdxDocument, SpdxItem> entry = iter.next();
			if (entry.getValue() instanceof SpdxSnippet) {
				snippet2 = (SpdxSnippet)entry.getValue();
				document2 = entry.getKey();
			}
		}
		if (snippet2 != null && snippet2.getByteRange() != null) {
			try {
				if (!snippet2.getByteRange().equivalent(snippet.getByteRange())) {
					this.byteRangeEquals = false;
					this.differenceFound = true;
				}
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting byte range: "+e.getMessage());
			}
			try {
			    Optional<StartEndPointer> lineRange = snippet.getLineRange();
			    Optional<StartEndPointer> lineRange2 = snippet2.getLineRange();
				if (lineRange2.isPresent()) {
					if (lineRange.isPresent()) {
						if (!lineRange2.get().equivalent(lineRange.get())) {
							this.lineRangeEquals = false;
							this.differenceFound = true;
						}
					} else {
						this.lineRangeEquals = false;
						this.differenceFound = true;
					}
				} else if (lineRange.isPresent()) {
					this.lineRangeEquals = false;
					this.differenceFound = true;
				}
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting line range: "+e.getMessage());
			}
			try {
				SpdxFile fromFile = snippet.getSnippetFromFile();
				SpdxFile fromFile2 = snippet2.getSnippetFromFile();
				compareSnippetFromFiles(spdxDocument, fromFile, document2, fromFile2);
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting snippet from file: "+e.getMessage());
			}
			try {
				if (!SpdxComparer.stringsEqual(snippet2.getName(), snippet.getName())) {
					this.nameEquals = false;
					this.differenceFound = true;
				}
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error comparing snippet names: "+e.getMessage());
			}
		}
		inProgress = false;
		super.addDocumentItem(spdxDocument, snippet);
	}
	
	/**
	 * Compares the snippetFromFiles and updates the properties isSnippetFromFilesEquals,
	 * uniqueSnippetFromFiles, and snippetFromFilesDifferences
	 * @param fromFile
	 * @param fromFile2
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	private void compareSnippetFromFiles(SpdxDocument spdxDocument, SpdxFile fromFile, 
			SpdxDocument document2, SpdxFile fromFile2) throws SpdxCompareException, InvalidSPDXAnalysisException {
		if (fromFile == null) {
			if (fromFile2 != null) {
				Map<SpdxDocument, SpdxFile> unique = this.uniqueSnippetFromFile.get(document2);
				if (unique == null) {
					unique = new HashMap<>();
					this.uniqueSnippetFromFile.put(document2, unique);
				}
				unique.put(spdxDocument, fromFile2);
				this.snippetFromFilesEquals = false;
			}
		} else if (fromFile2 == null) {
			Map<SpdxDocument, SpdxFile> unique = this.uniqueSnippetFromFile.get(spdxDocument);
			if (unique == null) {
				unique = new HashMap<>();
				this.uniqueSnippetFromFile.put(spdxDocument, unique);
			}
			unique.put(document2, fromFile);
			this.snippetFromFilesEquals = false;
		} else if (!Objects.equals(fromFile2.getName(), fromFile.getName())) {
			Map<SpdxDocument, SpdxFile> unique = this.uniqueSnippetFromFile.get(spdxDocument);
			if (unique == null) {
				unique = new HashMap<>();
				this.uniqueSnippetFromFile.put(spdxDocument, unique);
			}
			unique.put(document2, fromFile);
			Map<SpdxDocument, SpdxFile> unique2 = this.uniqueSnippetFromFile.get(document2);
			if (unique2 == null) {
				unique2 = new HashMap<>();
				this.uniqueSnippetFromFile.put(document2, unique2);
			}
			unique2.put(spdxDocument, fromFile2);
			this.snippetFromFilesEquals = false;
		} else {
			SpdxFileComparer fileCompare = new SpdxFileComparer(this.extractedLicenseIdMap);
			fileCompare.addDocumentFile(spdxDocument, fromFile);
			fileCompare.addDocumentFile(document2, fromFile2);
			if (fileCompare.isDifferenceFound()) {
				this.snippetFromFilesEquals = false;
				Map<SpdxDocument, SpdxFileDifference> comparerMap = new HashMap<>();
				this.snippetFromFileDifferences.put(spdxDocument, comparerMap);
				Map<SpdxDocument, SpdxFileDifference> comparerMap2 = this.snippetFromFileDifferences.get(document2);
				if (comparerMap2 == null) {
					comparerMap2 = new HashMap<>();
					this.snippetFromFileDifferences.put(document2, comparerMap2);
				}
				comparerMap.put(document2, fileCompare.getFileDifference(spdxDocument, document2));
				comparerMap2.put(spdxDocument, fileCompare.getFileDifference(document2, spdxDocument));
			}
		}
		if (!this.snippetFromFilesEquals) {
			this.differenceFound = true;
		}
	}

	/**
	 * @return the differenceFound
	 * @throws SpdxCompareException 
	 */
	@Override
    public boolean isDifferenceFound() throws SpdxCompareException {
		checkInProgress();
		return differenceFound || super.isDifferenceFound();
	} 
	
	/**
	 * checks to make sure there is not a compare in progress
	 * @throws SpdxCompareException 
	 * 
	 */
	@Override
    protected void checkInProgress() throws SpdxCompareException {
		if (inProgress) {
			throw new SpdxCompareException("File compare in progress - can not obtain compare results until compare has completed");
		}
		super.checkInProgress();
	}
	
	/**
	 * Get any file difference for the Spdx Snippet From File between the two SPDX documents
	 * If the fileName is different, the they are considered unique files and the getUniqueSnippetFromFile should be called
	 * to obtain the unique file
	 * @param docA
	 * @param docB
	 * @return the file difference or null if there is no file difference
	 */
	public SpdxFileDifference getSnippetFromFileDifference(SpdxDocument docA, 
			SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		Map<SpdxDocument, SpdxFileDifference> differenceMap = this.snippetFromFileDifferences.get(docA);
		if (differenceMap == null) {
			return null;
		}
		return differenceMap.get(docB);
	}

	/**
	 * @return the byteRangeEquals
	 */
	public boolean isByteRangeEquals() throws SpdxCompareException {
		checkInProgress();
		return byteRangeEquals;
	}

	/**
	 * @return the lineRangeEquals
	 */
	public boolean isLineRangeEquals() throws SpdxCompareException {
		checkInProgress();
		return lineRangeEquals;
	}

	/**
	 * The snippetFromFiles can be true if there are some unique snippetFromFiles or differences between the snippetFromFiles (or both)
	 * @return the snippetFromFilesEquals
	 */
	public boolean isSnippetFromFilesEquals() throws SpdxCompareException {
		checkInProgress();
		return snippetFromFilesEquals;
	}

	/**
	 * @return the nameEquals
	 */
	public boolean isNameEquals() throws SpdxCompareException {
		checkInProgress();
		return nameEquals;
	}

	/**
	 * Get an SpdxFile that only exists in docA but not docB
	 * @param docA
	 * @param docB
	 * @return
	 */
	public SpdxFile getUniqueSnippetFromFile(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		Map<SpdxDocument, SpdxFile> docMap = this.uniqueSnippetFromFile.get(docA);
		if (docMap == null) {
			return null;
		}
		return docMap.get(docB);
	}

	/**
	 * @return Total number of snippets
	 */
	public int getNumSnippets() {
		return this.documentItem.size();
	}

	/**
	 * @param spdxDocument
	 * @return
	 */
	public SpdxSnippet getDocSnippet(SpdxDocument spdxDocument) {
		SpdxItem retItem = this.documentItem.get(spdxDocument);
		if (retItem != null && retItem instanceof SpdxSnippet) {
			return (SpdxSnippet)retItem;
		} else {
			return null;
		}
	}
}