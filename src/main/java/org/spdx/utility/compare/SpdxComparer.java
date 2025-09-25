/**
 * SPDX-FileCopyrightText: Copyright (c) 2020 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.utility.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.Annotation;
import org.spdx.library.model.v2.Checksum;
import org.spdx.library.model.v2.ExternalDocumentRef;
import org.spdx.library.model.v2.ModelObjectV2;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.SpdxCreatorInformation;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.SpdxElement;
import org.spdx.library.model.v2.SpdxFile;
import org.spdx.library.model.v2.SpdxPackage;
import org.spdx.library.model.v2.SpdxPackageVerificationCode;
import org.spdx.library.model.v2.SpdxSnippet;
import org.spdx.library.model.v2.license.AnyLicenseInfo;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;
import org.spdx.licenseTemplate.LicenseTextHelper;

import javax.annotation.Nullable;

/**
 * Performs a comparison between two or more SPDX documents and holds the results of the comparison
 * <p>
 * The main function to perform the comparison is <code>compare(spdxdoc1, spdxdoc2)</code>
 * <p>
 * For files, the comparison results are separated into unique files based on the file names
 * which can be obtained by the method <code>getUniqueFiles(index1, index2)</code>.  If two
 * documents contain files with the same name, but different data, the differences for these
 * files can be obtained through the method <code>getFileDifferences(index1, index2)</code>
 * <p>
 * Multi-threading considerations: This class is "mostly" threadsafe in that the calls to 
 * perform the comparison are synchronized and a flag is used to throw an error for any
 * calls to getters when a compare is in progress.  There is a small theoretical window in the
 * getters where the compare operation is started in the middle of a get operation.
 * 
 * @author Gary O'Neall
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class SpdxComparer {
	
	static final Logger logger = LoggerFactory.getLogger(SpdxComparer.class);
	
	private List<SpdxDocument> spdxDocs = null;
	private boolean differenceFound = false;
	private boolean compareInProgress = false;
	
	// Document level results
	private boolean spdxVersionsEqual = true;
	private boolean documentCommentsEqual = true;
	private boolean dataLicenseEqual = true;
	private boolean licenseListVersionEquals = true;
	private boolean documentContentsEquals = true;
	private boolean creatorCommentsEqual = true;
	private boolean creationDatesEqual = true;
		
	// Extracted Licensing Info results
	/**
	 * Holds a map of all SPDX documents which have extracted license infos unique relative to other SPDX document
	 * based on the reviewer name.  The results of the map is another map of all SPDX documents in 
	 * the comparison which do not contain some of the reviewers in the key document.  See the
	 * implementation of compareReviewers for details
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<ExtractedLicenseInfo>>> uniqueExtractedLicenses = new HashMap<>();
	/**
	 * Map of any SPDX documents that have extracted license infos with equivalent text but different comments, objectUri's or other fields
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<SpdxLicenseDifference>>> licenseDifferences = new HashMap<>();
	/**
	 * Maps the license ID's for the extracted license infos of the documents being compared.  License ID's are mapped based on the text
	 * being equivalent 
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap = new HashMap<>();

	private boolean creatorInformationEquals;
	
	private final Map<SpdxDocument, Map<SpdxDocument, List<String>>> uniqueCreators = new HashMap<>();
	
	// file compare results
	/**
	 * Holds a map of all SPDX documents which have files unique relative to other SPDX document
	 * based on the file name.  The results of the map is another map of all SPDX documents in 
	 * the comparison which do not contain some of the files in the key document.  See the
	 * implementation of compareFiles for details
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<SpdxFile>>> uniqueFiles = new HashMap<>();
	
	/**
	 * Holds a map of any SPDX documents which have file differences.  A file difference
	 * is an SPDXReview with the same filename name but a different file property
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<SpdxFileDifference>>> fileDifferences = new HashMap<>();

	// Package compare results
	/**
	 * Holds a map of all SPDX documents which have packages unique relative to other SPDX document
	 * based on the package name and package version.  The results of the map is another map of all SPDX documents in 
	 * the comparison which do not contain some of the packages in the key document.  See the
	 * implementation of comparePackages for details
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<SpdxPackage>>> uniquePackages = new HashMap<>();
	
	/**
	 * Map of package names to package comparisons
	 */
	private final Map<String, SpdxPackageComparer> packageComparers = new HashMap<>();
	
	// Annotation comparison results
	private final Map<SpdxDocument, Map<SpdxDocument, List<Annotation>>> uniqueDocumentAnnotations = new HashMap<>();

	// Document Relationships comparison results
	private final Map<SpdxDocument, Map<SpdxDocument, List<Relationship>>> uniqueDocumentRelationships = new HashMap<>();

	// External Document References comparison results
	private final Map<SpdxDocument, Map<SpdxDocument, List<ExternalDocumentRef>>> uniqueExternalDocumentRefs = new HashMap<>();
	
	// Snippet references comparison results
	private final Map<SpdxDocument, Map<SpdxDocument, List<SpdxSnippet>>> uniqueSnippets = new HashMap<>();
	private final Map<String, SpdxSnippetComparer>  snippetComparers = new HashMap<>();

	private final Map<Integer, Boolean> equivalentElements = new HashMap<>(); // Key is the hash of the hashes of the 2 element
	
	public SpdxComparer() {
		// Default empty constructor
	}
	
	/**
	 * Compares 2 SPDX documents
	 * @param spdxDoc1 first doc
	 * @param spdxDoc2 second doc
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 * @throws SpdxCompareException Customize Toolbar…
	 */
	public void compare(SpdxDocument spdxDoc1, SpdxDocument spdxDoc2) throws InvalidSPDXAnalysisException, SpdxCompareException {
		compare(Arrays.asList(spdxDoc1, spdxDoc2));
	}
	
	/**
	 * Compares multiple SPDX documents
	 * @param spdxDocuments documents to compare
	 * @throws SpdxCompareException on SPDX parsing errors
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public synchronized void compare(List<SpdxDocument> spdxDocuments) throws InvalidSPDXAnalysisException, SpdxCompareException {
		//TODO: Add a monitor function which allows for cancel
		clearCompareResults();
		this.spdxDocs = spdxDocuments;
		differenceFound = false;
		performCompare();	
	}

	/**
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 * @throws SpdxCompareException Customize Toolbar…
	 * 
	 */
	private void performCompare() throws InvalidSPDXAnalysisException, SpdxCompareException {
		compareInProgress = true;
		differenceFound = false;
		compareExtractedLicenseInfos();	// note - this must be done first to build the translation map of IDs
		compareDocumentFields();
		compareSnippets();
		compareFiles();
		comparePackages();
		compareCreators();
		compareDocumentAnnotations();
		compareDocumentRelationships();
		compareExternalDocumentRefs();
		compareInProgress = false;	
	}

	/**
	 * Compare the snippets in the documents
	 * @throws SpdxCompareException on compare errors
	 */
	@SuppressWarnings("unchecked")
	private void compareSnippets() throws SpdxCompareException {
		// This will be a complete NXN comparison of all documents filling in the uniqueSnippets map
		if (this.spdxDocs == null || this.spdxDocs.isEmpty()) {
			return;
		}
		this.uniqueSnippets.clear();
		this.snippetComparers.clear();
		// N x N comparison of all snippets
		for (int i = 0; i < spdxDocs.size(); i++) {
			List<SpdxSnippet> snippetsA;
			Stream<SpdxSnippet> snippetStreamA = null;
			try {
			    snippetStreamA = (Stream<SpdxSnippet>)SpdxModelFactory.getSpdxObjects(spdxDocs.get(i).getModelStore(), null, 
			    				SpdxConstantsCompatV2.CLASS_SPDX_SNIPPET, spdxDocs.get(i).getDocumentUri(), null);
				snippetsA = snippetStreamA.collect(Collectors.toList());
			} catch (InvalidSPDXAnalysisException e) {
				try {
					throw new SpdxCompareException("Error collecting snippets from SPDX document "+spdxDocs.get(i).getName(), e);
				} catch (InvalidSPDXAnalysisException e1) {
					throw new SpdxCompareException("Error collecting snippets from SPDX document ", e);
				}
			} finally {
			    if (Objects.nonNull(snippetStreamA)) {
			        snippetStreamA.close();
			    }
			}
			// note - the snippet arrays MUST be sorted for the comparator methods to work
			Collections.sort(snippetsA);
			addSnippetComparers(spdxDocs.get(i), snippetsA);
			Map<SpdxDocument, List<SpdxSnippet>> uniqueAMap = this.uniqueSnippets.get(spdxDocs.get(i));
			if (uniqueAMap == null) {
				uniqueAMap = new HashMap<>();
			}
			for (int j = 0; j < spdxDocs.size(); j++) {
				if (j == i) {
					continue;
				}
				List<SpdxSnippet> snippetsB;
				Stream<SpdxSnippet> snippetStreamB = null;
				try {
				    snippetStreamB = (Stream<SpdxSnippet>)SpdxModelFactory.getSpdxObjects(spdxDocs.get(j).getModelStore(),
				    		null, SpdxConstantsCompatV2.CLASS_SPDX_SNIPPET, spdxDocs.get(j).getDocumentUri(), null);
					snippetsB = snippetStreamB.collect(Collectors.toList());
				} catch (InvalidSPDXAnalysisException e) {
					try {
						throw new SpdxCompareException("Error collecting snippets from SPDX document "+spdxDocs.get(j).getName(), e);
					} catch (InvalidSPDXAnalysisException e1) {
						throw new SpdxCompareException("Error collecting snippets from SPDX document ", e);
					}
				} finally {
				    if (Objects.nonNull(snippetStreamB)) {
				        snippetStreamB.close();
				    }
				}
				//Note that the files arrays must be sorted for the find methods to work
				Collections.sort(snippetsB);
				List<SpdxSnippet> uniqueAB = findUniqueSnippets(snippetsA, snippetsB);
				if (!uniqueAB.isEmpty()) {
					uniqueAMap.put(spdxDocs.get(j), uniqueAB);
				}
			}
			if (!uniqueAMap.isEmpty()) {
				this.uniqueSnippets.put(spdxDocs.get(i), uniqueAMap);
			}
		}
		if (!_isSnippetsEqualsNoCheck()) {
			this.differenceFound = true;
		}		
	}

	/**
	 * @param snippetsA source snippets
	 * @param snippetsB snippets to compare
	 * @return list of snippets which are in B but not in A
	 */
	private List<SpdxSnippet> findUniqueSnippets(List<SpdxSnippet> snippetsA,
			List<SpdxSnippet> snippetsB) {
		int bIndex = 0;
		int aIndex = 0;
		List<SpdxSnippet> alRetval = new ArrayList<>();
		while (aIndex < snippetsA.size()) {
			if (bIndex >= snippetsB.size()) {
				alRetval.add(snippetsA.get(aIndex));
				aIndex++;
			} else {
				int compareVal = snippetsA.get(aIndex).compareTo(snippetsB.get(bIndex));
				if (compareVal == 0) {
					// snippets are equal
					aIndex++;
					bIndex++;
				} else if (compareVal > 0) {
					// snippetsA is greater than snippetsB
					bIndex++;
				} else {
					// snippetsB is greater than snippetsA
					alRetval.add(snippetsA.get(aIndex));
					aIndex++;
				}
			}
		}
		return alRetval;
	}

	/**
	 * @param spdxDocument Document containing the snippets
	 * @param snippets the snippets
	 * @throws SpdxCompareException on error
	 */
	private void addSnippetComparers(
			SpdxDocument spdxDocument,
			List<SpdxSnippet> snippets) throws SpdxCompareException {
		for (SpdxSnippet snippet:snippets) {
			SpdxSnippetComparer comparer = this.snippetComparers.get(snippet.toString());
			if (comparer == null) {
				comparer = new SpdxSnippetComparer(extractedLicenseIdMap);
				this.snippetComparers.put(snippet.toString(), comparer);
			}
			try {
				comparer.addDocumentSnippet(spdxDocument, snippet);
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("Exception comparing SPDX snippets",e);
			}
		}
	}

	/**
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 * 
	 */
	private void compareExternalDocumentRefs() throws InvalidSPDXAnalysisException {
		// this will be a N x N comparison of all external document relationships to fill the
		// hashmap uniqueExternalDocumentRefs
		for (int i = 0; i < spdxDocs.size(); i++) {
			Collection<ExternalDocumentRef> externalDocRefsA = spdxDocs.get(i).getExternalDocumentRefs();
			Map<SpdxDocument, List<ExternalDocumentRef>> uniqueAMap = uniqueExternalDocumentRefs.get(spdxDocs.get(i));
			if (uniqueAMap == null) {
				uniqueAMap = new HashMap<>();
				// We will put this into the hashmap at the end of this method if it is not empty
			}
			for (int j = 0; j < spdxDocs.size(); j++) {
				if (j == i) {
					continue;	// skip comparing to ourself
				}
				Collection<ExternalDocumentRef> externalDocRefsB = spdxDocs.get(j).getExternalDocumentRefs();

				// find any external refs in A that are not in B
				List<ExternalDocumentRef> uniqueA = findUniqueExternalDocumentRefs(externalDocRefsA, externalDocRefsB, equivalentElements);
				if (!uniqueA.isEmpty()) {
					uniqueAMap.put(spdxDocs.get(j), uniqueA);					
				}
			}
			if (!uniqueAMap.isEmpty()) {
				this.uniqueExternalDocumentRefs.put(spdxDocs.get(i), uniqueAMap);
			}
		}
		if (!this._isExternalDocumentRefsEqualsNoCheck()) {
			this.differenceFound = true;
		}	
	}

	/**
	 * Compare all of the document level relationships
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private void compareDocumentRelationships() throws InvalidSPDXAnalysisException {
		// this will be a N x N comparison of all document level relationships to fill the
		// hashmap uniqueDocumentRelationships
		for (int i = 0; i < spdxDocs.size(); i++) {
			Collection<Relationship> relationshipsA = spdxDocs.get(i).getRelationships();
			Map<SpdxDocument, List<Relationship>> uniqueAMap = uniqueDocumentRelationships.get(spdxDocs.get(i));
			if (uniqueAMap == null) {
				uniqueAMap = new HashMap<>();
				// We will put this into the hashmap at the end of this method if it is not empty
			}
			for (int j = 0; j < spdxDocs.size(); j++) {
				if (j == i) {
					continue;	// skip comparing to ourself
				}
				Collection<Relationship> relationshipsB = spdxDocs.get(j).getRelationships();

				// find any creators in A that are not in B
				List<Relationship> uniqueA = findUniqueRelationships(relationshipsA, relationshipsB, equivalentElements);
				if (!uniqueA.isEmpty()) {
					uniqueAMap.put(spdxDocs.get(j), uniqueA);					
				}
			}
			if (!uniqueAMap.isEmpty()) {
				this.uniqueDocumentRelationships.put(spdxDocs.get(i), uniqueAMap);
			}
		}
		if (!this._isDocumentRelationshipsEqualsNoCheck()) {
			this.differenceFound = true;
		}	
	}

	/**
	 * Compare all of the Document level annotations
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private void compareDocumentAnnotations() throws InvalidSPDXAnalysisException {
		// this will be a N x N comparison of all document level annotations to fill the
		// hashmap uniqueAnnotations
		for (int i = 0; i < spdxDocs.size(); i++) {
			Collection<Annotation> annotationsA = spdxDocs.get(i).getAnnotations();
			Map<SpdxDocument, List<Annotation>> uniqueAMap = uniqueDocumentAnnotations.get(spdxDocs.get(i));
			if (uniqueAMap == null) {
				uniqueAMap = new HashMap<>();
				// We will put this into the hashmap at the end of this method if it is not empty
			}
			for (int j = 0; j < spdxDocs.size(); j++) {
				if (j == i) {
					continue;	// skip comparing to ourself
				}
				Collection<Annotation> annotationsB = spdxDocs.get(j).getAnnotations();

				// find any creators in A that are not in B
				List<Annotation> uniqueA = findUniqueAnnotations(annotationsA, annotationsB);
				if (!uniqueA.isEmpty()) {
					uniqueAMap.put(spdxDocs.get(j), uniqueA);					
				}
			}
			if (!uniqueAMap.isEmpty()) {
				this.uniqueDocumentAnnotations.put(spdxDocs.get(i), uniqueAMap);
			}
		}
		if (!this._isDocumentAnnotationsEqualsNoCheck()) {
			this.differenceFound = true;
		}	
	}

	/**
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 * @throws SpdxCompareException on compare errors
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void compareFiles() throws InvalidSPDXAnalysisException, SpdxCompareException {
		this.uniqueFiles.clear();
		this.fileDifferences.clear();
		// N x N comparison of all files
		for (int i = 0; i < spdxDocs.size(); i++) {
			List<SpdxFile> filesListA;
			Stream<SpdxFile> fileStreamA = (Stream<SpdxFile>) SpdxModelFactory.getSpdxObjects(
					spdxDocs.get(i).getModelStore(), null, SpdxConstantsCompatV2.CLASS_SPDX_FILE, 
					spdxDocs.get(i).getDocumentUri(), null);
			filesListA = fileStreamA.collect(Collectors.toList());
			fileStreamA.close();
			// note - the file arrays MUST be sorted for the comparator methods to work
			Collections.sort(filesListA);
			SpdxFile[] filesA = filesListA.toArray(new SpdxFile[0]);
			Map<SpdxDocument, List<SpdxFile>> uniqueAMap = this.uniqueFiles.get(spdxDocs.get(i));
			if (uniqueAMap == null) {
				uniqueAMap = new HashMap<>();
			}
			// this map will be added to uniqueFiles at the end if we find anything
			Map<SpdxDocument, List<SpdxFileDifference>> diffMap = this.fileDifferences.get(spdxDocs.get(i));
			if (diffMap == null) {
				diffMap = new HashMap<>();
			}
			for (int j = 0; j < spdxDocs.size(); j++) {
				if (j == i) {
					continue;
				}
				 Stream<SpdxFile> fileStreamB = (Stream<SpdxFile>)SpdxModelFactory.getSpdxObjects(
						 spdxDocs.get(j).getModelStore(), null, SpdxConstantsCompatV2.CLASS_SPDX_FILE, 
						 spdxDocs.get(j).getDocumentUri(), null);
				 List<SpdxFile> filesListB = fileStreamB.collect(Collectors.toList());
				 fileStreamB.close();
				 //Note that the files arrays must be sorted for the find methods to work
				Collections.sort(filesListB);
				SpdxFile[] filesB = filesListB.toArray(new SpdxFile[0]);
				List<SpdxFile> uniqueAB = findUniqueFiles(filesA, filesB);
				if (!uniqueAB.isEmpty()) {
					uniqueAMap.put(spdxDocs.get(j), uniqueAB);
				}
				List<SpdxFileDifference> differences = findFileDifferences(spdxDocs.get(i), spdxDocs.get(j),
						filesA, filesB, this.extractedLicenseIdMap);
				if (!differences.isEmpty()) {
					diffMap.put(spdxDocs.get(j), differences);
				}
			}
			if (!uniqueAMap.isEmpty()) {
				this.uniqueFiles.put(spdxDocs.get(i), uniqueAMap);
			}
			if (!diffMap.isEmpty()) {
				this.fileDifferences.put(spdxDocs.get(i), diffMap);
			}
		}
		if (!_isFilesEqualsNoCheck()) {
			this.differenceFound = true;
		}
	}
	
	/**
	 * Collect all of the packages present in the SPDX document including packages 
	 * embedded in other relationships within documents
	 * @param spdxDocument document containing packages
	 * @return list of packages
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	@SuppressWarnings("unchecked")
	protected List<SpdxPackage> collectAllPackages(SpdxDocument spdxDocument) throws InvalidSPDXAnalysisException {
		Stream<SpdxPackage> packageStream = (Stream<SpdxPackage>) SpdxModelFactory.getSpdxObjects(
				spdxDocument.getModelStore(), null, SpdxConstantsCompatV2.CLASS_SPDX_PACKAGE, 
				spdxDocument.getDocumentUri(), null);
	    List<SpdxPackage> retval = packageStream.collect(Collectors.toList());
	    packageStream.close();
	    return retval;
	}

	/**
	 * Collect all of the files present in the SPDX document including files within documents
	 * and files embedded in packages
	 * @param spdxDocument document containing files
	 * @return files collected
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	@SuppressWarnings("unchecked")
	public List<SpdxFile> collectAllFiles(SpdxDocument spdxDocument) throws InvalidSPDXAnalysisException {
		Stream<SpdxFile> fileElementStream = (Stream<SpdxFile>) SpdxModelFactory.getSpdxObjects(
				spdxDocument.getModelStore(), null, SpdxConstantsCompatV2.CLASS_SPDX_FILE, 
				spdxDocument.getDocumentUri(), null);
		List<SpdxFile> retval = fileElementStream.collect(Collectors.toList());
		fileElementStream.close();
		return retval;
	}

	/**
	 * Returns an array of files differences between A and B where the names
	 * are the same, but one or more properties are different for that file
	 * @param filesA source files
	 * @param filesB files to compare
	 * @return a list of files differences between A and B where the names are the same, but one or more properties are different for that file
	 * @throws SpdxCompareException on compare errors
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	static List<SpdxFileDifference> findFileDifferences(SpdxDocument docA, SpdxDocument docB,
			SpdxFile[] filesA, SpdxFile[] filesB, 
			Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> licenseIdXlationMap) throws SpdxCompareException, InvalidSPDXAnalysisException {
		
		List<SpdxFileDifference> alRetval = new ArrayList<>();
		int aIndex = 0;
		int bIndex = 0;
		while (aIndex < filesA.length && bIndex < filesB.length) {
			int compare = 0;
			Optional<String> nameA = filesA[aIndex].getName();
			Optional<String> nameB = filesB[bIndex].getName();
			if (nameA.isPresent() && nameB.isPresent()) {
				compare = nameA.get().compareTo(nameB.get());
			}
			if (compare == 0) {
				SpdxFileComparer fileComparer = new SpdxFileComparer(licenseIdXlationMap);
				fileComparer.addDocumentFile(docA, filesA[aIndex]);
				fileComparer.addDocumentFile(docB, filesB[bIndex]);
				if (fileComparer.isDifferenceFound()) {
					alRetval.add(fileComparer.getFileDifference(docA, docB));
				}
				aIndex++;
				bIndex++;
			} else if (compare > 0) {
				// fileA is greater than fileB
				bIndex++;
			} else {
				// fileB is greater than fileA
				aIndex++;
			}
		}
		return alRetval;
	}

	/**
	 * finds any packages in A that are not in B.  Packages are considered the
	 * same if they have the same package name and the same package version.
	 * NOTE: The arrays must be sorted by file name
	 * @param pkgsA source packages
	 * @param pkgsB packages to compare
	 * @return any packages in A that are not in B
	 */
	static List<SpdxPackage> findUniquePackages(List<SpdxPackage> pkgsA, List<SpdxPackage> pkgsB) {
		int bIndex = 0;
		int aIndex = 0;
		List<SpdxPackage> alRetval = new ArrayList<>();
		while (aIndex < pkgsA.size()) {
			if (bIndex >= pkgsB.size()) {
				alRetval.add(pkgsA.get(aIndex));
				aIndex++;
			} else {
				int compareVal = pkgsA.get(aIndex).compareTo(pkgsB.get(bIndex));
				if (compareVal == 0) {
					// packages are equal
					aIndex++;
					bIndex++;
				} else if (compareVal > 0) {
					// pkgA is greater than pkgB
					bIndex++;
				} else {
					// pkgB is greater than pkgA
					alRetval.add(pkgsA.get(aIndex));
					aIndex++;
				}
			}
		}
		return alRetval;
	}
	/**
	 * finds any files in A that are not in B.  NOTE: The arrays must be sorted by file name
	 * @param filesA source files
	 * @param filesB files to compare
	 * @return any files in A that are not in B
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	static List<SpdxFile> findUniqueFiles(SpdxFile[] filesA, SpdxFile[] filesB) throws InvalidSPDXAnalysisException {
		int bIndex = 0;
		int aIndex = 0;
		List<SpdxFile> alRetval = new ArrayList<>();
		while (aIndex < filesA.length) {
			if (bIndex >= filesB.length) {
				alRetval.add(filesA[aIndex]);
				aIndex++;
			} else {
				int compareVal = compareStrings(filesA[aIndex].getName(), filesB[bIndex].getName());
				if (compareVal == 0) {
					// files are equal
					aIndex++;
					bIndex++;
				} else if (compareVal > 0) {
					// fileA is greater than fileB
					bIndex++;
				} else {
					// fileB is greater tha fileA
					alRetval.add(filesA[aIndex]);
					aIndex++;
				}
			}
		}
		return alRetval;
	}

	/**
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 * 
	 */
	private void compareCreators() throws InvalidSPDXAnalysisException {
		this.creatorInformationEquals = true;
		this.licenseListVersionEquals = true;
		// this will be a N x N comparison of all creators to fill the
		// hashmap uniqueCreators
		for (int i = 0; i < spdxDocs.size(); i++) {
			SpdxCreatorInformation creatorInfoA = spdxDocs.get(i).getCreationInfo();
			Collection<String> creatorsA = Objects.requireNonNull(creatorInfoA).getCreators();
			Map<SpdxDocument, List<String>> uniqueAMap = uniqueCreators.get(spdxDocs.get(i));
			if (uniqueAMap == null) {
				uniqueAMap = new HashMap<>();
				// We will put this into the hashmap at the end of this method if it is not empty
			}
			for (int j = 0; j < spdxDocs.size(); j++) {
				if (j == i) {
					continue;	// skip comparing to ourself
				}
				SpdxCreatorInformation creatorInfoB = spdxDocs.get(j).getCreationInfo();
				Collection<String> creatorsB = Objects.requireNonNull(creatorInfoB).getCreators();

				// find any creators in A that are not in B
				List<String> uniqueA = findUniqueString(creatorsA, creatorsB);
				if (!uniqueA.isEmpty()) {
					uniqueAMap.put(spdxDocs.get(j), uniqueA);					
				}
				// compare creator comments
				if (!stringsEqual(creatorInfoA.getComment(), creatorInfoB.getComment())) {
					this.creatorCommentsEqual = false;
					this.creatorInformationEquals = false;
				}
				// compare creation dates
				if (!stringsEqual(creatorInfoA.getCreated(), creatorInfoB.getCreated())) {
					this.creationDatesEqual = false;
					this.creatorInformationEquals = false;
				}
				// compare license list versions
				if (!stringsEqual(creatorInfoA.getLicenseListVersion(), creatorInfoB.getLicenseListVersion())) {
					this.creatorInformationEquals = false;
					this.licenseListVersionEquals = false;
				}
			}
			if (!uniqueAMap.isEmpty()) {
				this.uniqueCreators.put(spdxDocs.get(i), uniqueAMap);
				this.creatorInformationEquals = false;
			}
		}
		if (!this.creatorInformationEquals) {
			this.differenceFound = true;
		}	
	}

	/**
	 * Finds any strings which are in A but not in B
	 * @param stringsA source strings
	 * @param stringsB strings to compare
	 * @return any strings which are in A but not in B
	 */
	private List<String> findUniqueString(Collection<String> stringsA, Collection<String> stringsB) {
		if (stringsA == null) {
			return new ArrayList<>();
		}
		List<String> al = new ArrayList<>();
		for (String stringA:stringsA) {
			boolean found = false;
			if (Objects.nonNull(stringsB)) {
				for (String stringB:stringsB) {
					if (compareStrings(stringA, stringB) == 0) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				al.add(stringA);
			}
		}
		return al;
	}

	/**
	 * Compares the SPDX documents and sets the appropriate flags
	 * @throws SpdxCompareException on compare errors
	 */
	private void comparePackages() throws SpdxCompareException {
		if (this.spdxDocs == null || this.spdxDocs.isEmpty()) {
			return;
		}
		this.uniquePackages.clear();
		this.packageComparers.clear();
		// N x N comparison of all files
		for (int i = 0; i < spdxDocs.size(); i++) {
			List<SpdxPackage> pkgsA;
			try {
				pkgsA = collectAllPackages(spdxDocs.get(i));
			} catch (InvalidSPDXAnalysisException e) {
				try {
					throw new SpdxCompareException("Error collecting packages from SPDX document "+spdxDocs.get(i).getName(), e);
				} catch (InvalidSPDXAnalysisException e1) {
					throw new SpdxCompareException("Error collecting packages from SPDX document ", e);
				}
			}
			// note - the package arrays MUST be sorted for the comparator methods to work
			Collections.sort(pkgsA);
			addPackageComparers(spdxDocs.get(i), pkgsA, this.extractedLicenseIdMap);
			Map<SpdxDocument, List<SpdxPackage>> uniqueAMap = this.uniquePackages.get(spdxDocs.get(i));
			if (uniqueAMap == null) {
				uniqueAMap = new HashMap<>();
			}
			for (int j = 0; j < spdxDocs.size(); j++) {
				if (j == i) {
					continue;
				}
				List<SpdxPackage> pkgsB;
				try {
					pkgsB = collectAllPackages(spdxDocs.get(j));
				} catch (InvalidSPDXAnalysisException e) {
					try {
						throw new SpdxCompareException("Error collecting packages from SPDX document "+spdxDocs.get(i).getName(), e);
					} catch (InvalidSPDXAnalysisException e1) {
						throw new SpdxCompareException("Error collecting packages from SPDX document ", e);
					}
				}
				//Note that the files arrays must be sorted for the find methods to work
				Collections.sort(pkgsB);
				List<SpdxPackage> uniqueAB = findUniquePackages(pkgsA, pkgsB);
				if (!uniqueAB.isEmpty()) {
					uniqueAMap.put(spdxDocs.get(j), uniqueAB);
				}
			}
			if (!uniqueAMap.isEmpty()) {
				this.uniquePackages.put(spdxDocs.get(i), uniqueAMap);
			}
		}
		if (!_isPackagesEqualsNoCheck()) {
			this.differenceFound = true;
		}		
	}

	/**
	 * add all the document packages to the multi-comparer
	 * @param spdxDocument document containing the packages
	 * @param pkgs package to add
	 * @param extractedLicenseIdMap map of documents to extracted licenses and ids
	 * @throws SpdxCompareException on compare errors
	 */
	private void addPackageComparers(SpdxDocument spdxDocument,
			List<SpdxPackage> pkgs, Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap) throws SpdxCompareException {
		try {
			List<String> addedPackageNames = new ArrayList<>();
			for (SpdxPackage pkg:pkgs) {
				if (!pkg.getName().isPresent()) {
					logger.warn("Missing package name for package comparer.  Skipping unnamed package");
					continue;
				}
				Optional<String> pkgName = pkg.getName();
				if (pkgName.isPresent()) {
				    if (addedPackageNames.contains(pkgName.get())) {
                        logger.warn("Duplicate package names: {}.  Only comparing the first instance", pkgName.get());
	                    continue;
	                }
	                SpdxPackageComparer mpc = this.packageComparers.get(pkgName.get());
	                if (mpc == null) {
	                    mpc = new SpdxPackageComparer(extractedLicenseIdMap);
	                    this.packageComparers.put(pkgName.get(), mpc);
	                }
	                mpc.addDocumentPackage(spdxDocument, pkg);
	                addedPackageNames.add(pkgName.get()); 
				}
				
			}
		} catch (InvalidSPDXAnalysisException ex) {
			throw new SpdxCompareException("Error getting package name", ex);
		}
	}

	/**
	 * Compares two licenses from two different SPDX documents taking into account
	 * the extracted license infos whose ID's may be different between the two documents
	 * Note: The ExtractedLicenseIDMap must be initialized before this method is invoked
	 * @param doc1 Index of the SPDX document for license1
	 * @param license1 license to compare
	 * @param doc2 Index of the SPDX document for license2
	 * @param license2 license to compare
	 * @return true if the licenses are equivalent
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean compareLicense(int doc1,
			AnyLicenseInfo license1, int doc2,
			AnyLicenseInfo license2) throws SpdxCompareException {
		this.checkDocsIndex(doc1);
		this.checkDocsIndex(doc2);
		Map<SpdxDocument, Map<String, String>> hm = this.extractedLicenseIdMap.get(this.spdxDocs.get(doc1));
		if (hm == null) {
			throw new SpdxCompareException("Compare License Error - Extracted license objectUri map has not been initialized.");
		}
		Map<String, String> xlationMap = hm.get(this.spdxDocs.get(doc2));
		if (xlationMap == null) {
			throw new SpdxCompareException("Compare License Exception - Extracted license objectUri map has not been initialized.");
		}
		try {
			return LicenseCompareHelper.isLicenseEqual(license1, license2, xlationMap);
		} catch (InvalidSPDXAnalysisException e) {
			throw new SpdxCompareException("Error comparing licenses", e);
		}
	}

	/**
	 * @param verificationCode verification code to compare
	 * @param verificationCode2 verification code to compare
	 * @return true if the verification codes are equal
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static boolean compareVerificationCodes(
			Optional<SpdxPackageVerificationCode> verificationCode,
			Optional<SpdxPackageVerificationCode> verificationCode2) throws InvalidSPDXAnalysisException {
		if (!verificationCode.isPresent()) {
			return !verificationCode2.isPresent();
		}
		if (!verificationCode2.isPresent()) {
			return false;
		}
		if (!stringsEqual(verificationCode.get().getValue(), verificationCode2.get().getValue())) {
			return false;
		}
        return stringCollectionsEqual(verificationCode.get().getExcludedFileNames(),
                verificationCode2.get().getExcludedFileNames());
    }

	/**
	 * Compare the document level fields and sets the difference found depending on any differences
	 * @throws SpdxCompareException on compare errors
	 */
	private void compareDocumentFields() throws SpdxCompareException {
		compareDataLicense();
		compareDocumentComments();
		compareSpdxVersions();
		compareDocumentContents();
		if (!this.dataLicenseEqual || !this.spdxVersionsEqual || !this.documentCommentsEqual) {
			this.differenceFound = true;
		}
	}
	
	private void compareDocumentContents() throws SpdxCompareException {
		documentContentsEquals = true;
		try {
			for (int i = 0; i < spdxDocs.size()-1; i++) {
				Collection<SpdxElement> itemsA = spdxDocs.get(i).getDocumentDescribes();
				for (int j = i+1; j < spdxDocs.size(); j++) {
					Collection<SpdxElement> itemsB = spdxDocs.get(j).getDocumentDescribes();
					if (!collectionsEquivalent(itemsA, itemsB, equivalentElements)) {
						this.documentContentsEquals = false;
						this.differenceFound = true;
						return;
					}
				}
			}
		} catch(InvalidSPDXAnalysisException ex) {
			throw new SpdxCompareException("Error getting SPDX document items: "+ex.getMessage(), ex);
		}
	}

	/**
	 * @throws SpdxCompareException on compare errors
	 * 
	 */
	private void compareSpdxVersions() throws SpdxCompareException {
		try {
			String docVer1;
			docVer1 = spdxDocs.get(0).getSpecVersion();
			this.spdxVersionsEqual = true;
			for (int i = 1; i < spdxDocs.size(); i++) {
				if (!spdxDocs.get(i).getSpecVersion().equals(docVer1)) {
					this.spdxVersionsEqual = false;
					break;
				}
			}
		} catch(InvalidSPDXAnalysisException ex) {
			throw new SpdxCompareException("Error getting SPDX version",ex);
		}
	}

	/**
	 * @throws SpdxCompareException on compare errors
	 * 
	 */
	private void compareDocumentComments() throws SpdxCompareException {
		try {
		Optional<String> comment1 = this.spdxDocs.get(0).getComment();
		this.documentCommentsEqual = true;
		for (int i = 1; i < spdxDocs.size(); i++) {
			Optional<String> comment2 = this.spdxDocs.get(i).getComment();
			if (!stringsEqual(comment1, comment2)) {
				this.documentCommentsEqual = false;
				break;
			}
		}
		} catch(InvalidSPDXAnalysisException ex) {
			throw new SpdxCompareException("Error getting document comments",ex);
		}
	}

	/**
	 * @throws SpdxCompareException on compare errors
	 * 
	 */
	private void compareDataLicense() throws SpdxCompareException {
		try {
			AnyLicenseInfo lic1 = this.spdxDocs.get(0).getDataLicense();
			this.dataLicenseEqual = true;
			for (int i = 1; i < spdxDocs.size(); i++) {
				if (!lic1.equals(spdxDocs.get(i).getDataLicense())) {
					this.dataLicenseEqual = false;
					break;
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			throw new SpdxCompareException("SPDX analysis error during compare data license: "+e.getMessage(),e);
		}
	}

	/**
	 * Compares the extracted license infos in all documents and builds the 
	 * maps for translating IDs as well as capturing any differences between the
	 * extracted licensing information
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
     */
	private void compareExtractedLicenseInfos() throws InvalidSPDXAnalysisException {
		for (int i = 0; i < spdxDocs.size(); i++) {
			Collection<ExtractedLicenseInfo> extractedLicensesA = spdxDocs.get(i).getExtractedLicenseInfos();
			Map<SpdxDocument, List<ExtractedLicenseInfo>> uniqueMap = new HashMap<>();
				Map<SpdxDocument, List<SpdxLicenseDifference>> differenceMap = new HashMap<>();
				Map<SpdxDocument, Map<String, String>> licenseIdMap = new HashMap<>();

			for (int j = 0; j < spdxDocs.size(); j++) {
				if (i == j) {
					continue;	// no need to compare to ourself;
				}
				Map<String, String> idMap = new HashMap<>();
				List<SpdxLicenseDifference> alDifferences = new ArrayList<>();
				Collection<ExtractedLicenseInfo> extractedLicensesB = spdxDocs.get(j).getExtractedLicenseInfos();
				List<ExtractedLicenseInfo> uniqueLicenses = new ArrayList<>();
				compareLicenses(extractedLicensesA, extractedLicensesB,
						idMap, alDifferences, uniqueLicenses);
				// unique
				if (!uniqueLicenses.isEmpty()) {
					uniqueMap.put(spdxDocs.get(j), uniqueLicenses);
				}
				// differences
				if (!alDifferences.isEmpty()) {
					differenceMap.put(spdxDocs.get(j), alDifferences);
				}
				// map
				licenseIdMap.put(spdxDocs.get(j), idMap);
			}
			if (!uniqueMap.isEmpty()) {
				this.uniqueExtractedLicenses.put(spdxDocs.get(i), uniqueMap);
			}
			if (!differenceMap.isEmpty()) {
				this.licenseDifferences.put(spdxDocs.get(i), differenceMap);
			}
			this.extractedLicenseIdMap.put(spdxDocs.get(i), licenseIdMap);
		}
		if (!_isExtractedLicensingInfoEqualsNoCheck()) {
			this.differenceFound = true;
		}
	}

	/**
	 * Compares two collections of non standard licenses
	 * @param extractedLicensesA licenses to compare
	 * @param extractedLicensesB licenses to compare
	 * @param idMap Map of license IDs for licenses considered equal
	 * @param alDifferences Array list of license differences found where the license text is equivalent but other properties are different
	 * @param uniqueLicenses ArrayList if licenses found in the A but not found in B
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private void compareLicenses(Collection<ExtractedLicenseInfo> extractedLicensesA,
			Collection<ExtractedLicenseInfo> extractedLicensesB,
			Map<String, String> idMap,
			List<SpdxLicenseDifference> alDifferences,
			List<ExtractedLicenseInfo> uniqueLicenses) throws InvalidSPDXAnalysisException {
		idMap.clear();
		alDifferences.clear();
		uniqueLicenses.clear();
		for (ExtractedLicenseInfo licA:extractedLicensesA) {
			boolean foundMatch = false;
			boolean foundTextMatch = false;
			for (ExtractedLicenseInfo licB:extractedLicensesB) {
				if (LicenseTextHelper.isLicenseTextEquivalent(licA.getExtractedText(), 
						licB.getExtractedText())) {
					foundTextMatch = true;
					if (!foundMatch) {
						idMap.put(licA.getObjectUri(), licB.getObjectUri());
						// always add to the map any matching licenses.  If more than one, add
						// the license matches where the entire license match.  This condition checks
						// to make sure we are not over-writing an exact match
					}
					if (nonTextLicenseFieldsEqual(licA, licB)) {
						foundMatch = true;
					} else {
						alDifferences.add(new SpdxLicenseDifference(licA, licB));
					}
				}
			}
			if (!foundTextMatch) {	// we treat the licenses as equivalent if the text matches even if other fields do not match
				uniqueLicenses.add(licA);
			}
		}
	}

	/**
	 * Compares the non-license text and non-objectUri fields and returns true
	 * if all relevant fields are equal
	 * @param spdxNonStandardLicenseA license to compare
	 * @param spdxNonStandardLicenseB license to compare
	 * @return true if all relevant fields are equal
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private boolean nonTextLicenseFieldsEqual(
			ExtractedLicenseInfo spdxNonStandardLicenseA,
			ExtractedLicenseInfo spdxNonStandardLicenseB) throws InvalidSPDXAnalysisException {
		
		// license name
		if (!stringsEqual(spdxNonStandardLicenseA.getName(),
				spdxNonStandardLicenseB.getName())) {
			return false;
		}

		// comment;
		if (!stringsEqual(spdxNonStandardLicenseA.getComment(),
					spdxNonStandardLicenseB.getComment())) {
			return false;
		}
		// Source URL's
        return stringCollectionsEqual(spdxNonStandardLicenseA.getSeeAlso(), spdxNonStandardLicenseB.getSeeAlso());
    }

	/**
	 * Compares 2 collections and returns true if the contents are equal
	 * ignoring order and trimming strings.  Nulls are also considered as equal to other nulls.
	 * @param stringsA string to compare
	 * @param stringsB string to compare
	 * @return true if the contents are equal ignoring order and trimming strings
	 */
	public static boolean stringCollectionsEqual(Collection<String> stringsA, Collection<String> stringsB) {
		if (stringsA == null) {
			return stringsB == null;
		} else {
			if (stringsB == null) {
				return false;
			}
			if (stringsA.size() != stringsB.size()) {
				return false;
			}
			for (String stA:stringsA) {
				if (!stringsB.contains(stA)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Compares 2 lists and returns true if the contents are equal
	 * ignoring order and trimming strings.  Nulls are also considered as equal to other nulls.
	 * @param stringsA string to compare
	 * @param stringsB string to compare
	 * @return true if the contents are equal ignoring order and trimming strings
	 */
	static boolean stringListsEqual(List<String> stringsA, List<String> stringsB) {
		if (stringsA == null) {
			return stringsB == null;
		} else {
			if (stringsB == null) {
				return false;
			}
			if (stringsA.size() != stringsB.size()) {
				return false;
			}
			for (String stA:stringsA) {
				if (!stringsB.contains(stA)) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * returns true if the two objects are equal considering nulls
	 * @param o1 object to compare
	 * @param o2 object to compare
	 * @return true if the two objects are equal considering nulls
	 */
	public static boolean objectsEqual(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}
	
	/**
	 * @param elementA element to compare
	 * @param elementB element to compare
	 * @return true of the elements are present and equivalent
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean elementsEquivalent(Optional<? extends ModelObjectV2> elementA,
											 Optional<? extends ModelObjectV2> elementB,
											 Map<Integer, Boolean> equivalentElements) throws InvalidSPDXAnalysisException {
		return elementsEquivalent(elementA.orElse(null), elementB.orElse(null), equivalentElements);
	}

	public static boolean elementsEquivalent(@Nullable ModelObjectV2 elementA,
											@Nullable ModelObjectV2 elementB,
											 Map<Integer, Boolean> equivalentElements) throws InvalidSPDXAnalysisException {
		if (Objects.nonNull(elementA)) {
			if (Objects.nonNull(elementB)) {
				int key = Objects.hash(elementA, elementB);
				int key2 = Objects.hash(elementA, elementB);
				Boolean equiv = equivalentElements.get(key);
				if (Objects.isNull(equiv)) {
					equiv = elementA.equivalent(elementB);
					equivalentElements.put(key, equiv);
				}
				return equiv;
			} else {
				return false;
			}
		} else {
			return Objects.isNull(elementB);
		}
	}
	
	/**
	 * @param collectionA collection
	 * @param collectionB collection
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 * @return true if the collections all contain equivalent items
	 */
	public static boolean collectionsEquivalent(Collection<? extends ModelObjectV2> collectionA,
												Collection<? extends ModelObjectV2> collectionB,
												Map<Integer, Boolean> equivalentElements) throws InvalidSPDXAnalysisException {
		if (Objects.isNull(collectionA)) {
			return Objects.isNull(collectionB);
		}
		if (Objects.isNull(collectionB)) {
			return false;
		}
		if (collectionA.size() != collectionB.size()) {
			return false;
		}
		//DEBUG:
		int size = collectionA.size();
		int count = 0;
		//ENDDEBUG:
		for (ModelObjectV2 elementA:collectionA) {

			if (Objects.isNull(elementA)) {
				continue;
			}
			boolean found = false;
			for (ModelObjectV2 elementB:collectionB) {
				if (elementsEquivalent(elementA, elementB, equivalentElements)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
			//DEBUG:
			if (size > 10000) {
				System.out.println("Compared item "+count++ + "out of "+size);
			}
			//ENDDEBUG:
		}
		return true;
	}

	/**
	 * Compare two object lists
	 * @param a1 list
	 * @param a2 list
	 * @return true if 2 lists are equal
	 */
	public static boolean listsEquals(List<?> a1, List<?> a2) {
		if (a1 == null) {
			return a2 == null;
		} else {
			if (a2 == null) {
				return false;
			}
			if (a1.size() != a2.size()) {
				return false;
			}
			for (Object o1:a1) {
				if (!a2.contains(o1)) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Compare two object lists
	 * @param a1 collection
	 * @param a2 collection
	 * @return true of the collections are equal
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean collectionsEquals(Collection<?> a1, Collection<?> a2) {
		if (a1 == null) {
			return a2 == null;
		} else {
			if (a2 == null) {
				return false;
			}
			if (a1.size() != a2.size()) {
				return false;
			}
			for (Object o1:a1) {
				if (!a2.contains(o1)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Compares two strings returning true if they are equal
	 * considering null values and trimming the strings. and normalizing
	 * linefeeds.  Empty strings are treated as the same as null values.
	 * @param stringA first string to compare
	 * @param stringB second string to compare
	 * @return same result as compareTo
	 */
	public static boolean stringsEqual(String stringA, String stringB) {
		String compA;
		String compB;
		if (stringA == null) {
			compA = "";
		} else {
			compA = stringA.replace("\r\n", "\n").trim();
		}
		if (stringB == null) {
			compB = "";
		} else {
			compB = stringB.replace("\r\n", "\n").trim();
		}
		return (compA.equals(compB));
	}
	
	/**
	 * Compares two strings returning true if they are equal
	 * considering null values and trimming the strings. and normalizing
	 * linefeeds.  Empty strings are treated as the same as null values.
	 * @param stringA first string to compare
	 * @param stringB second string to compare
	 * @return same result as compareTo
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean stringsEqual(Optional<String> stringA, Optional<String> stringB) {
		String compA;
		String compB;
        compA = stringA.map(s -> s.replace("\r\n", "\n").trim()).orElse("");
        compB = stringB.map(s -> s.replace("\r\n", "\n").trim()).orElse("");
		return (compA.equals(compB));
	}
	
	/**
	 * Compares two strings including trimming the string and taking into account
	 * they may be null.  Null is considered a smaller value
	 * @param stringA first string to compare
	 * @param stringB second string to compare
	 * @return same result as compareTo
	 */
	public static int compareStrings(String stringA, String stringB) {
		if (stringA == null) {
			if (stringB == null) {
				return 0;
			} else {
				return -1;
			}
		}
		if (stringB == null) {
			return 1;
		}
		return (stringA.trim().compareTo(stringB.trim()));
	}
	
	/**
	 * Compares two strings including trimming the string and taking into account
	 * they may be null.  Null is considered a smaller value
	 * @param stringA first string to compare
	 * @param stringB second string to compare
	 * @return same result as compareTo
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static int compareStrings(Optional<String> stringA, Optional<String> stringB) {
		if (!stringA.isPresent()) {
			if (!stringB.isPresent()) {
				return 0;
			} else {
				return -1;
			}
		}
        return stringB.map(s -> (stringA.get().trim().compareTo(s.trim()))).orElse(1);
    }

	/**
	 * clears all the compare results
	 */
	private void clearCompareResults() {
		this.differenceFound = false;
		this.licenseDifferences.clear();
		this.uniqueExtractedLicenses.clear();
		this.extractedLicenseIdMap.clear();
		this.uniqueCreators.clear();
	}

	/**
	 * @return true if any difference is found
	 */
	public boolean isDifferenceFound() {
		return this.differenceFound;
	}

	/**
	 * @return true if the SPDX spec versions are equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isSpdxVersionEqual() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return this.spdxVersionsEqual;
	}

	/**
	 * checks to make sure there is not a compare in progress
	 * @throws SpdxCompareException on compare errors
	 * 
	 */
	private void checkInProgress() throws SpdxCompareException {
		if (compareInProgress) {
			throw new SpdxCompareException("Compare in progress - can not obtain compare results until compare has completed");
		}
	}

	/**
	 * Validates that the spdx documents field has been initialized
	 * @throws SpdxCompareException on compare errors
	 */
	private void checkDocsField() throws SpdxCompareException {
		if (this.spdxDocs == null) {
			throw new SpdxCompareException("No compare has been performed");
		}
		if (this.spdxDocs.size() < 2) {
			throw new SpdxCompareException("Insufficient documents compared - must provide at least 2 SPDX documents");
		}
	}
	
	private void checkDocsIndex(int index) throws SpdxCompareException {
		if (this.spdxDocs == null) {
			throw new SpdxCompareException("No compare has been performed");
		}
		if (index < 0) {
			throw new SpdxCompareException("Invalid index for SPDX document compare - must be greater than or equal to zero");
		}
		if (index >= spdxDocs.size()) {
			throw new SpdxCompareException("Invalid index for SPDX document compare - SPDX document index "+ index +" does not exist.");
		}
	}

	/**
	 * @param docIndex Reference to which document number - 0 is the first document parameter in compare
	 * @return the SPDX document at the index
	 * @throws SpdxCompareException on compare errors
	 */
	public SpdxDocument getSpdxDoc(int docIndex) throws SpdxCompareException {
		this.checkDocsField();
		if (this.spdxDocs ==  null) {
			return null;
		}
		if (docIndex < 0) {
			return null;
		}
		if (docIndex > this.spdxDocs.size()) {
			return null;
		}
		return this.spdxDocs.get(docIndex);
	}

	/**
	 * @return true if the data licenses are equals
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isDataLicenseEqual() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return this.dataLicenseEqual;
	}

	/**
	 * @return true if the document comments are equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isDocumentCommentsEqual() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return this.documentCommentsEqual;
	}
	
	private boolean _isExternalDocumentRefsEqualsNoCheck() {
        for (Entry<SpdxDocument, Map<SpdxDocument, List<ExternalDocumentRef>>> spdxDocumentMapEntry : this.uniqueExternalDocumentRefs.entrySet()) {
            for (List<ExternalDocumentRef> externalDocumentRefs : spdxDocumentMapEntry.getValue().values()) {
                if (!externalDocumentRefs.isEmpty()) {
                    return false;
                }
            }
        }
		return true;
	}


	/**
	 * @return true if the external document refs are equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isExternalDocumentRefsEquals() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return _isExternalDocumentRefsEqualsNoCheck();
	}

	/**
	 * @return true if the external document refs are equal
	 * @throws SpdxCompareException on compare errors
	 */
	@Deprecated
	public boolean isExternalDcoumentRefsEquals() throws SpdxCompareException {
		return isExternalDocumentRefsEquals();
	}

	/**
	 * @return true if the extracted licensing infos are equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isExtractedLicensingInfosEqual() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return _isExtractedLicensingInfoEqualsNoCheck();
	}

	/**
	 * @return true if the extracted licensing infos are equal
	 */
	private boolean _isExtractedLicensingInfoEqualsNoCheck() {
		// check for unique extracted license infos
        for (Entry<SpdxDocument, Map<SpdxDocument, List<ExtractedLicenseInfo>>> entry : this.uniqueExtractedLicenses.entrySet()) {
            for (Entry<SpdxDocument, List<ExtractedLicenseInfo>> spdxDocumentListEntry : entry.getValue().entrySet()) {
                List<ExtractedLicenseInfo> licenses = spdxDocumentListEntry.getValue();
                if (licenses != null && !licenses.isEmpty()) {
                    return false;
                }
            }
        }
		// check differences
        for (Entry<SpdxDocument, Map<SpdxDocument, List<SpdxLicenseDifference>>> spdxDocumentMapEntry : this.licenseDifferences.entrySet()) {
            for (Entry<SpdxDocument, List<SpdxLicenseDifference>> spdxDocumentListEntry : spdxDocumentMapEntry.getValue().entrySet()) {
                List<SpdxLicenseDifference> differences = spdxDocumentListEntry.getValue();
                if (differences != null && !differences.isEmpty()) {
                    return false;
                }
            }
        }
		return true;
	}

	/**
	 * Retrieves any unique extracted licenses from the first SPDX document index
	 * relative to the second - unique is determined by the license text matching
	 * @param docIndexA source document index
	 * @param docIndexB index of the compare document
	 * @return ny unique extracted licenses fromt the first SPDX document index relative to the second - unique is determined by the license text matching
	 * @throws SpdxCompareException on compare errors
	 */
	public List<ExtractedLicenseInfo> getUniqueExtractedLicenses(int docIndexA, int docIndexB) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		checkDocsIndex(docIndexA);
		checkDocsIndex(docIndexB);
		Map<SpdxDocument, List<ExtractedLicenseInfo>> uniques = this.uniqueExtractedLicenses.get(spdxDocs.get(docIndexA));
		if (uniques != null) {
			List<ExtractedLicenseInfo> retval = uniques.get(spdxDocs.get(docIndexB));
			if (retval != null) {
				return retval;
			} else {
				return new ArrayList<>();
			}
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * Retrieves any licenses which where the text matches in both documents but
	 * other fields are different
	 * @param docIndexA source document index
	 * @param docIndexB index of the compare document
	 * @return any creators which are in the SPDX document 1 which are not in document 2
	 * @throws SpdxCompareException on compare errors
	 */
	public List<SpdxLicenseDifference> getExtractedLicenseDifferences(int docIndexA, int docIndexB) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		checkDocsIndex(docIndexA);
		checkDocsIndex(docIndexB);
		Map<SpdxDocument, List<SpdxLicenseDifference>> differences = this.licenseDifferences.get(spdxDocs.get(docIndexA));
		if (differences != null) {
			List<SpdxLicenseDifference> retval = differences.get(spdxDocs.get(docIndexB));
			if (retval != null) {
				return retval;
			} else {
				return new ArrayList<>();
			}
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * @return true if all creation information fields equals
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isCreatorInformationEqual() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this.creatorInformationEquals;
	}
	
	/**
	 * @return true all creator comments equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isCreatorCommentsEqual() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this.creatorCommentsEqual;
	}
	
	/**
	 * @return true if all creation information fields equals
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isCreatorDatesEqual() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this.creationDatesEqual;
	}

	/**
	 * Returns any creators which are in the SPDX document 1 which are not in document 2
	 * @param doc1index source document index
	 * @param doc2index index of the compare document
	 * @return any creators which are in the SPDX document 1 which are not in document 2
	 * @throws SpdxCompareException on compare errors
	 */
	public List<String> getUniqueCreators(int doc1index, int doc2index) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		Map<SpdxDocument, List<String>> uniques = this.uniqueCreators.get(this.getSpdxDoc(doc1index));
		if (uniques == null) {
			return new ArrayList<>();
		}
		List<String> retval = uniques.get(this.getSpdxDoc(doc2index));
		if (retval == null) {
			return new ArrayList<>();
		} else {
			return retval;
		}
	}

	/**
	 * @return true if the files are equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isFilesEquals() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this._isFilesEqualsNoCheck();
	}
	
	/**
	 * @return true if the files are equal
	 * @throws SpdxCompareException on compare errors
	 */
	@Deprecated
	public boolean isfilesEquals() throws SpdxCompareException {
		return isFilesEquals();
	}
	
	/**
	 * @return true if the packages are equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isPackagesEquals() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this._isPackagesEqualsNoCheck();
	}

	/**
	 * @return true if the document annotations are equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isDocumentAnnotationsEquals() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return _isDocumentAnnotationsEqualsNoCheck();
	}
	/**
	 * @return true if the document annotations are equals
	 */
	private boolean _isDocumentAnnotationsEqualsNoCheck() {
        for (Entry<SpdxDocument, Map<SpdxDocument, List<Annotation>>> spdxDocumentMapEntry : this.uniqueDocumentAnnotations.entrySet()) {
            for (List<Annotation> annotations : spdxDocumentMapEntry.getValue().values()) {
                if (!annotations.isEmpty()) {
                    return false;
                }
            }
        }
		return true;
	}
	
	/**
	 * @return true if the document relationships are equal
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isDocumentRelationshipsEquals() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return _isDocumentRelationshipsEqualsNoCheck();
	}
	/**
	 * Compares document relationships without checking validity
	 * @return true if the documents relationships are equal
	 */
	private boolean _isDocumentRelationshipsEqualsNoCheck() {
        for (Entry<SpdxDocument, Map<SpdxDocument, List<Relationship>>> spdxDocumentMapEntry : this.uniqueDocumentRelationships.entrySet()) {
            for (List<Relationship> relationships : spdxDocumentMapEntry.getValue().values()) {
                if (!relationships.isEmpty()) {
                    return false;
                }
            }
        }
		return true;
	}

	/**
	 * @return true if the files are equal
	 */
	private boolean _isFilesEqualsNoCheck() {
		if (!this.uniqueFiles.isEmpty()) {
			return false;
		}
        return this.fileDifferences.isEmpty();
    }

	/**
	 * @return true if the packages are equal
	 * @throws SpdxCompareException on compare errors
	 */
	private boolean _isPackagesEqualsNoCheck() throws SpdxCompareException {
        for (Entry<SpdxDocument, Map<SpdxDocument, List<SpdxPackage>>> spdxDocumentMapEntry : this.uniquePackages.entrySet()) {
            for (List<SpdxPackage> spdxPackages : spdxDocumentMapEntry.getValue().values()) {
                if (!spdxPackages.isEmpty()) {
                    return false;
                }
            }
        }
        for (SpdxPackageComparer spdxPackageComparer : this.packageComparers.values()) {
            if (spdxPackageComparer.isDifferenceFound()) {
                return false;
            }
        }
		return true;
	}
	/**
	 * Return any files which are in spdx document index 1 but not in spdx document index 2
	 * @param docindex1 index of source document
	 * @param docindex2 index of document to compare
	 * @return any files which are in spdx document index 1 but not in spdx document index 2
	 * @throws SpdxCompareException on errors doing compare
	 */
	public List<SpdxFile> getUniqueFiles(int docindex1, int docindex2) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		this.checkDocsIndex(docindex1);
		this.checkDocsIndex(docindex2);
		Map<SpdxDocument, List<SpdxFile>> uniqueMap = this.uniqueFiles.get(this.spdxDocs.get(docindex1));
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<SpdxFile> retval = uniqueMap.get(this.spdxDocs.get(docindex2));
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}

	/**
	 * Returns any file differences found between the first and second SPDX documents
	 * as specified by the document index
	 * @param docindex1 index of source document
	 * @param docindex2 index of document to compare
	 * @return any file differences found between the first and second SPDX documents
	 * as specified by the document index
	 * @throws SpdxCompareException on errors doing compare
	 */
	public List<SpdxFileDifference> getFileDifferences(int docindex1, int docindex2) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		this.checkDocsIndex(docindex1);
		this.checkDocsIndex(docindex2);
		Map<SpdxDocument, List<SpdxFileDifference>> uniqueMap = this.fileDifferences.get(this.spdxDocs.get(docindex1));
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<SpdxFileDifference> retval = uniqueMap.get(this.spdxDocs.get(docindex2));
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}
	
	/**
	 * Return any files which are in spdx document index 1 but not in spdx document index 2
	 * @param docindex1 index of source document
	 * @param docindex2 index of document to compare
	 * @return any files which are in spdx document index 1 but not in spdx document index 2
	 * @throws SpdxCompareException on errors doing compare
	 */
	public List<SpdxPackage> getUniquePackages(int docindex1, int docindex2) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		this.checkDocsIndex(docindex1);
		this.checkDocsIndex(docindex2);
		Map<SpdxDocument, List<SpdxPackage>> uniqueMap = this.uniquePackages.get(this.spdxDocs.get(docindex1));
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<SpdxPackage> retval = uniqueMap.get(this.spdxDocs.get(docindex2));
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}
	
	/**
	 * Return any external document references which are in spdx document index 1 but not in spdx document index 2
	 * @param docindex1 index of source document
	 * @param docindex2 index of document to compare
	 * @return any external document references which are in spdx document index 1 but not in spdx document index 2
	 * @throws SpdxCompareException on errors doing compare
	 */
	public List<ExternalDocumentRef> getUniqueExternalDocumentRefs(int docindex1, int docindex2) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		this.checkDocsIndex(docindex1);
		this.checkDocsIndex(docindex2);
		Map<SpdxDocument, List<ExternalDocumentRef>> uniqueMap = this.uniqueExternalDocumentRefs.get(this.spdxDocs.get(docindex1));
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<ExternalDocumentRef> retval = uniqueMap.get(this.spdxDocs.get(docindex2));
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}

	/**
	 * Return any document annotations which are in spdx document index 1 but not in spdx document index 2
	 * @param docindex1 index of source document
	 * @param docindex2 index of document to compare
	 * @return any document annotations which are in spdx document index 1 but not in spdx document index 2
	 * @throws SpdxCompareException on errors doing compare
	 */
	public List<Annotation> getUniqueDocumentAnnotations(int docindex1, int docindex2) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		this.checkDocsIndex(docindex1);
		this.checkDocsIndex(docindex2);
		Map<SpdxDocument, List<Annotation>> uniqueMap = this.uniqueDocumentAnnotations.get(this.spdxDocs.get(docindex1));
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<Annotation> retval = uniqueMap.get(this.spdxDocs.get(docindex2));
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}
	
	/**
	 * Return any document annotations which are in spdx document index 1 but not in spdx document index 2
	 * @param docindex1 index of first document to compare
	 * @param docindex2 index of second document to compare
	 * @return document annotations in spdx document index 1 but not in spdx document index 2
	 * @throws SpdxCompareException On error in comparison
	 */
	public List<Relationship> getUniqueDocumentRelationship(int docindex1, int docindex2) throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		this.checkDocsIndex(docindex1);
		this.checkDocsIndex(docindex2);
		Map<SpdxDocument, List<Relationship>> uniqueMap = this.uniqueDocumentRelationships.get(this.spdxDocs.get(docindex1));
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<Relationship> retval = uniqueMap.get(this.spdxDocs.get(docindex2));
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}
	
	/**
	 * @return Package comparers where there is at least one difference
	 * @throws SpdxCompareException On error in comparison
	 */
	public List<SpdxPackageComparer> getPackageDifferences() throws SpdxCompareException {
		Collection<SpdxPackageComparer> comparers = this.packageComparers.values();
		Iterator<SpdxPackageComparer> iter = comparers.iterator();
		List<SpdxPackageComparer> retval = new ArrayList<>();		
		while (iter.hasNext()) {
			SpdxPackageComparer comparer = iter.next();
			if (comparer.isDifferenceFound()) {
				retval.add(comparer);
			}
		}
		return retval;
	}
	
	/**
	 * @return all package comparers
	 */
	public SpdxPackageComparer[] getPackageComparers() {
		return this.packageComparers.values().toArray(
                new SpdxPackageComparer[0]);
	}

	/**
	 * @return the number of documents being compared
	 */
	public int getNumSpdxDocs() {
		return this.spdxDocs.size();
	}

	/**
	 * @return true if the license list version is equal
	 * @throws SpdxCompareException On error in comparison
	 */
	public boolean isLicenseListVersionEqual() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this.licenseListVersionEquals;
	}

	/**
	 * Find any SPDX checksums which are in checksumsA but not in checksumsB
	 * @param checksumsA checksum to compare
	 * @param checksumsB checksum to compare
	 * @return any SPDX checksums which are in checksumsA but not in checksumsB
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static List<Checksum> findUniqueChecksums(Collection<Checksum> checksumsA,
			Collection<Checksum> checksumsB) throws InvalidSPDXAnalysisException {
		List<Checksum> retval = new ArrayList<>();
		if (checksumsA != null) {
			for (Checksum ckA:checksumsA) {
				if (Objects.isNull(ckA)) {
					continue;
				}
				boolean found = false;
				if (Objects.nonNull(checksumsB)) {
					for (Checksum ckB:checksumsB) {
						if (ckA.equivalent(ckB)) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					retval.add(ckA);
				}
			}
		}
		return retval;
	}
	
	/**
	 * Find any SPDX annotations which are in annotationsA but not in annotationsB
	 * @param annotationsA source annotations
	 * @param annotationsB annotations to be compared against
	 * @return list of unique annotations in annotationsB
	 * @throws InvalidSPDXAnalysisException On SPDX parsing errors
	 */
	public static List<Annotation> findUniqueAnnotations(Collection<Annotation> annotationsA,
			Collection<Annotation> annotationsB) throws InvalidSPDXAnalysisException {
		List<Annotation> retval = new ArrayList<>();
		if (Objects.nonNull(annotationsA)) {
			for (Annotation annA:annotationsA) {
				boolean found = false;
				if (Objects.nonNull(annotationsB)) {
					for (Annotation annB:annotationsB) {
						if (annA.equivalent(annB)) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					retval.add(annA);
				}
			}
		}
		return retval;
	}

	/**
	 * Find unique relationships that are present in relationshipsA but not relationshipsB
	 * @param relationshipsA relationship to compare
	 * @param relationshipsB relationship to compare
	 * @return unique relationships that are present in relationshipsA but not relationshipsB
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static List<Relationship> findUniqueRelationships(
			Collection<Relationship> relationshipsA,
			Collection<Relationship> relationshipsB,
			Map<Integer, Boolean> equivalentElements) throws InvalidSPDXAnalysisException {
		List<Relationship> retval = new ArrayList<>();
		if (relationshipsA == null) {
			return retval;
		}
		for (Relationship relA:relationshipsA) {
			if (Objects.isNull(relA)) {
				continue;
			}
			boolean found = false;
			if (relationshipsB != null) {
				for (Relationship relB:relationshipsB) {
					if (elementsEquivalent(relA, relB, equivalentElements)) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				retval.add(relA);
			}
		}
		return retval;
	}
	
	/**
	 * Find unique relationships that are present in relationshipsA but not relationshipsB
	 * @param externalDocRefsA doc ref to compare
	 * @param externalDocRefsB doc ref to compare
	 * @return list of unique relationship
	 * @throws InvalidSPDXAnalysisException On error in comparison
	 */
	public static List<ExternalDocumentRef> findUniqueExternalDocumentRefs(
			Collection<ExternalDocumentRef> externalDocRefsA, Collection<ExternalDocumentRef> externalDocRefsB,
			Map<Integer, Boolean> equivalentElements) throws InvalidSPDXAnalysisException {
		List<ExternalDocumentRef> retval = new ArrayList<>();
		if (externalDocRefsA == null) {
			return new ArrayList<>();
		}
		for (ExternalDocumentRef docRefA:externalDocRefsA) {
			if (Objects.isNull(docRefA)) {
				continue;
			}
			boolean found = false;
			if (externalDocRefsB != null) {
				for (ExternalDocumentRef docRefB:externalDocRefsB) {
					if (compareStrings(docRefA.getSpdxDocumentNamespace(),
							docRefB.getSpdxDocumentNamespace()) == 0 &&
							elementsEquivalent(docRefA.getChecksum(), docRefB.getChecksum(), equivalentElements)) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				retval.add(docRefA);
			}
		}
		return retval;
	}

	/**
	 * @return list of SPDX documents
	 */
	public List<SpdxDocument> getSpdxDocuments() {
		return this.spdxDocs;
	}

	/**
	 * @return true if the document contents are equals
	 * @throws SpdxCompareException On error in comparison
	 */
	public boolean isDocumentContentsEquals() throws SpdxCompareException {
		checkInProgress();
		return this.documentContentsEquals;
	}

	/**
	 * @return true if the snippets are equals
	 * @throws SpdxCompareException On error in comparison
	 */
	public boolean isSnippetsEqual() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this._isSnippetsEqualsNoCheck();
	}

	/**
	 * @return true if the snippets are equals
	 * @throws SpdxCompareException On error in comparison
	 */
	private boolean _isSnippetsEqualsNoCheck() throws SpdxCompareException {
        for (Entry<SpdxDocument, Map<SpdxDocument, List<SpdxSnippet>>> spdxDocumentMapEntry : this.uniqueSnippets.entrySet()) {
            for (List<SpdxSnippet> spdxSnippets : spdxDocumentMapEntry.getValue().values()) {
                if (!spdxSnippets.isEmpty()) {
                    return false;
                }
            }
        }
        for (SpdxSnippetComparer spdxSnippetComparer : this.snippetComparers.values()) {
            if (spdxSnippetComparer.isDifferenceFound()) {
                return false;
            }
        }
		return true;
	}

	/**
	 * @return all snippet comparers
	 */
	public SpdxSnippetComparer[] getSnippetComparers() {
		return this.snippetComparers.values().toArray(
                new SpdxSnippetComparer[0]);
	}
}
