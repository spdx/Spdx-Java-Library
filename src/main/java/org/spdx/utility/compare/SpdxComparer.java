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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.ExternalDocumentRef;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.Relationship;
import org.spdx.library.model.SpdxCreatorInformation;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxElement;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.SpdxPackage;
import org.spdx.library.model.SpdxPackageVerificationCode;
import org.spdx.library.model.SpdxSnippet;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ExtractedLicenseInfo;
/**
 * Performs a comparison between two or more SPDX documents and holds the results of the comparison
 * The main function to perform the comparison is <code>compare(spdxdoc1, spdxdoc2)</code>
 * 
 * For files, the comparison results are separated into unique files based on the file names
 * which can be obtained by the method <code>getUniqueFiles(index1, index2)</code>.  If two
 * documents contain files with the same name, but different data, the differences for these
 * files can be obtained through the method <code>getFileDifferences(index1, index2)</code>
 * 
 * Multi-threading considerations: This class is "mostly" threadsafe in that the calls to 
 * perform the comparison are synchronized and a flag is used to throw an error for any
 * calls to getters when a compare is in progress.  There is a small theoretical window in the
 * getters where the compare operation is started in the middle of a get operation.
 * 
 * @author Gary O'Neall
 *
 */
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
		
	// Extracted Licensing Info results
	/**
	 * Holds a map of all SPDX documents which have extracted license infos unique relative to other SPDX document
	 * based on the reviewer name.  The results of the map is another map of all SPDX documents in 
	 * the comparison which do not contain some of the reviewers in the key document.  See the
	 * implementation of compareReviewers for details
	 */
	private Map<SpdxDocument, Map<SpdxDocument, List<ExtractedLicenseInfo>>> uniqueExtractedLicenses = new HashMap<>();
	/**
	 * Map of any SPDX documents that have extraced license infos with equivalent text but different comments, id's or other fields
	 */
	private Map<SpdxDocument, Map<SpdxDocument, List<SpdxLicenseDifference>>> licenseDifferences = new HashMap<>();
	/**
	 * Maps the license ID's for the extracted license infos of the documents being compared.  License ID's are mapped based on the text
	 * being equivalent 
	 */
	private Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap = new HashMap<>();

	private boolean creatorInformationEquals;
	
	private Map<SpdxDocument, Map<SpdxDocument, List<String>>> uniqueCreators = new HashMap<>();
	
	// file compare results
	/**
	 * Holds a map of all SPDX documents which have files unique relative to other SPDX document
	 * based on the file name.  The results of the map is another map of all SPDX documents in 
	 * the comparison which do not contain some of the files in the key document.  See the
	 * implementation of compareFiles for details
	 */
	private Map<SpdxDocument, Map<SpdxDocument, List<SpdxFile>>> uniqueFiles = new HashMap<>();
	
	/**
	 * Holds a map of any SPDX documents which have file differences.  A file difference
	 * is an SPDXReview with the same filename name but a different file property
	 */
	private Map<SpdxDocument, Map<SpdxDocument, List<SpdxFileDifference>>> fileDifferences = new HashMap<>();

	// Package compare results
	/**
	 * Holds a map of all SPDX documents which have packages unique relative to other SPDX document
	 * based on the package name and package version.  The results of the map is another map of all SPDX documents in 
	 * the comparison which do not contain some of the packages in the key document.  See the
	 * implementation of comparePackages for details
	 */
	private Map<SpdxDocument, Map<SpdxDocument, List<SpdxPackage>>> uniquePackages = new HashMap<>();
	
	/**
	 * Map of package names to package comparisons
	 */
	private Map<String, SpdxPackageComparer> packageComparers = new HashMap<>();
	
	// Annotation comparison results
	private Map<SpdxDocument, Map<SpdxDocument, List<Annotation>>> uniqueDocumentAnnotations = new HashMap<>();

	// Document Relationships comparison results
	private Map<SpdxDocument, Map<SpdxDocument, List<Relationship>>> uniqueDocumentRelationships = new HashMap<>();

	// External Document References comparison results
	private Map<SpdxDocument, Map<SpdxDocument, List<ExternalDocumentRef>>> uniqueExternalDocumentRefs = new HashMap<>();
	
	// Snippet references comparison results
	private Map<SpdxDocument, Map<SpdxDocument, List<SpdxSnippet>>> uniqueSnippets = new HashMap<>();
	private Map<String, SpdxSnippetComparer>  snippetComparers = new HashMap<>();
	
	public SpdxComparer() {
		
	}
	
	/**
	 * Compares 2 SPDX documents
	 * @param spdxDoc1
	 * @param spdxDoc2
	 * @throws InvalidSPDXAnalysisException
	 * @throws SpdxCompareException
	 */
	public void compare(SpdxDocument spdxDoc1, SpdxDocument spdxDoc2) throws InvalidSPDXAnalysisException, SpdxCompareException {
		compare(Arrays.asList((new SpdxDocument[] {spdxDoc1, spdxDoc2})));
	}
	
	/**
	 * Compares multiple SPDX documents
	 * @param spdxDocuments
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	public synchronized void compare(List<SpdxDocument> spdxDocuments) throws InvalidSPDXAnalysisException, SpdxCompareException {
		//TODO: Add a monitor function which allows for cancel
		clearCompareResults();
		this.spdxDocs = spdxDocuments;
		differenceFound = false;
		performCompare();	
	}

	/**
	 * @throws InvalidSPDXAnalysisException 
	 * @throws SpdxCompareException 
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
	 * @throws SpdxCompareException 
	 */
	@SuppressWarnings("unchecked")
	private void compareSnippets() throws SpdxCompareException {
		// This will be a complete NXN comparison of all documents filling in the uniqueSnippets map
		if (this.spdxDocs == null || this.spdxDocs.size() < 1) {
			return;
		}
		this.uniqueSnippets.clear();
		this.snippetComparers.clear();
		// N x N comparison of all snippets
		for (int i = 0; i < spdxDocs.size(); i++) {
			List<SpdxSnippet> snippetsA;
			try {
				snippetsA = (List<SpdxSnippet>)SpdxModelFactory.getElements(spdxDocs.get(i).getModelStore(), spdxDocs.get(i).getDocumentUri(), null, 
						SpdxSnippet.class).collect(Collectors.toList());
			} catch (InvalidSPDXAnalysisException e) {
				try {
					throw(new SpdxCompareException("Error collecting snippets from SPDX document "+spdxDocs.get(i).getName(), e));
				} catch (InvalidSPDXAnalysisException e1) {
					throw(new SpdxCompareException("Error collecting snippets from SPDX document ", e));
				}
			}
			// note - the snippet arrays MUST be sorted for the comparator methods to work
			Collections.sort(snippetsA);
			addSnippetComparers(spdxDocs.get(i), snippetsA, this.extractedLicenseIdMap);
			Map<SpdxDocument, List<SpdxSnippet>> uniqueAMap = this.uniqueSnippets.get(spdxDocs.get(i));
			if (uniqueAMap == null) {
				uniqueAMap = new HashMap<>();
			}
			for (int j = 0; j < spdxDocs.size(); j++) {
				if (j == i) {
					continue;
				}
				List<SpdxSnippet> snippetsB;
				try {
					snippetsB = (List<SpdxSnippet>)SpdxModelFactory.getElements(spdxDocs.get(j).getModelStore(), spdxDocs.get(j).getDocumentUri(), null, 
							SpdxSnippet.class).collect(Collectors.toList());
				} catch (InvalidSPDXAnalysisException e) {
					try {
						throw(new SpdxCompareException("Error collecting snippets from SPDX document "+spdxDocs.get(j).getName(), e));
					} catch (InvalidSPDXAnalysisException e1) {
						throw(new SpdxCompareException("Error collecting snippets from SPDX document ", e));
					}
				}
				//Note that the files arrays must be sorted for the find methods to work
				Collections.sort(snippetsB);
				List<SpdxSnippet> uniqueAB = findUniqueSnippets(snippetsA, snippetsB);
				if (uniqueAB != null && uniqueAB.size() > 0) {
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
	 * @param snippetsA
	 * @param snippetsB
	 * @return
	 */
	private List<SpdxSnippet> findUniqueSnippets(List<SpdxSnippet> snippetsA,
			List<SpdxSnippet> snippetsB) {
		int bIndex = 0;
		int aIndex = 0;
		List<SpdxSnippet> alRetval = new ArrayList<>();;
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
	 * @param spdxDocument
	 * @param snippets
	 * @param extractedLicenseIdMap2
	 * @throws SpdxCompareException 
	 */
	private void addSnippetComparers(
			SpdxDocument spdxDocument,
			List<SpdxSnippet> snippets,
			Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap2) throws SpdxCompareException {
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
	 * @throws InvalidSPDXAnalysisException 
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
				List<ExternalDocumentRef> uniqueA = findUniqueExternalDocumentRefs(externalDocRefsA, externalDocRefsB);
				if (uniqueA != null && uniqueA.size() > 0) {
					uniqueAMap.put(spdxDocs.get(j), uniqueA);					
				}
			}
			if (uniqueAMap.keySet().size() > 0) {
				this.uniqueExternalDocumentRefs.put(spdxDocs.get(i), uniqueAMap);
			}
		}
		if (!this._isExternalDcoumentRefsEqualsNoCheck()) {
			this.differenceFound = true;
		}	
	}

	/**
	 * Compare all of the document level relationships
	 * @throws InvalidSPDXAnalysisException 
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
				List<Relationship> uniqueA = findUniqueRelationships(relationshipsA, relationshipsB);
				if (uniqueA != null && uniqueA.size() > 0) {
					uniqueAMap.put(spdxDocs.get(j), uniqueA);					
				}
			}
			if (uniqueAMap.keySet().size() > 0) {
				this.uniqueDocumentRelationships.put(spdxDocs.get(i), uniqueAMap);
			}
		}
		if (!this._isDocumentRelationshipsEqualsNoCheck()) {
			this.differenceFound = true;
		}	
	}

	/**
	 * Compare all of the Document level annotations
	 * @throws InvalidSPDXAnalysisException 
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
				if (uniqueA != null && uniqueA.size() > 0) {
					uniqueAMap.put(spdxDocs.get(j), uniqueA);					
				}
			}
			if (uniqueAMap.keySet().size() > 0) {
				this.uniqueDocumentAnnotations.put(spdxDocs.get(i), uniqueAMap);
			}
		}
		if (!this._isDocumentAnnotationsEqualsNoCheck()) {
			this.differenceFound = true;
		}	
	}

	/**
	 * @throws InvalidSPDXAnalysisException 
	 * @throws SpdxCompareException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void compareFiles() throws InvalidSPDXAnalysisException, SpdxCompareException {
		this.uniqueFiles.clear();
		this.fileDifferences.clear();
		// N x N comparison of all files
		for (int i = 0; i < spdxDocs.size(); i++) {
			List<SpdxFile> filesListA;
			filesListA = (List<SpdxFile>)SpdxModelFactory.getElements(spdxDocs.get(i).getModelStore(), spdxDocs.get(i).getDocumentUri(),
					null, SpdxFile.class).collect(Collectors.toList());
			// note - the file arrays MUST be sorted for the comparator methods to work
			Collections.sort(filesListA);
			SpdxFile[] filesA = filesListA.toArray(new SpdxFile[filesListA.size()]);
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
				List<SpdxFile> filesListB;
				filesListB = (List<SpdxFile>)SpdxModelFactory.getElements(spdxDocs.get(j).getModelStore(), spdxDocs.get(j).getDocumentUri(),
						null, SpdxFile.class).collect(Collectors.toList());
				//Note that the files arrays must be sorted for the find methods to work
				Collections.sort(filesListB);
				SpdxFile[] filesB = filesListB.toArray(new SpdxFile[filesListB.size()]);
				List<SpdxFile> uniqueAB = findUniqueFiles(filesA, filesB);
				if (uniqueAB != null && uniqueAB.size() > 0) {
					uniqueAMap.put(spdxDocs.get(j), uniqueAB);
				}
				List<SpdxFileDifference> differences = findFileDifferences(spdxDocs.get(i), spdxDocs.get(j),
						filesA, filesB, this.extractedLicenseIdMap);
				if (differences != null && differences.size() > 0) {
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
	 * @param spdxDocument
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	protected List<SpdxPackage> collectAllPackages(SpdxDocument spdxDocument) throws InvalidSPDXAnalysisException {
		return (List<SpdxPackage>)SpdxModelFactory.getElements(
				spdxDocument.getModelStore(), spdxDocument.getDocumentUri(), 
				null, SpdxPackage.class).collect(Collectors.toList());
	}

	/**
	 * Collect all of the files present in the SPDX document including files within documents
	 * and files embedded in packages
	 * @param spdxDocument
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public List<SpdxFile> collectAllFiles(SpdxDocument spdxDocument) throws InvalidSPDXAnalysisException {
		return (List<SpdxFile>)SpdxModelFactory.getElements(spdxDocument.getModelStore(), spdxDocument.getDocumentUri(), 
				null, SpdxFile.class).collect(Collectors.toList());
	}

	/**
	 * Returns an array of files differences between A and B where the names
	 * are the same, but one or more properties are different for that file
	 * @param filesA
	 * @param filesB
	 * @return
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	static List<SpdxFileDifference> findFileDifferences(SpdxDocument docA, SpdxDocument docB,
			SpdxFile[] filesA, SpdxFile[] filesB, 
			Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> licenseIdXlationMap) throws SpdxCompareException, InvalidSPDXAnalysisException {
		
		List<SpdxFileDifference> alRetval = new ArrayList<>();
		int aIndex = 0;
		int bIndex = 0;
		while (aIndex < filesA.length && bIndex < filesB.length) {
			int compare = 0;
			if (filesA[aIndex].getName().isPresent() && filesB[bIndex].getName().isPresent()) {
				compare = filesA[aIndex].getName().get().compareTo(filesB[bIndex].getName().get());
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
	 * @param pkgsA
	 * @param pkgsB
	 * @return
	 */
	static List<SpdxPackage> findUniquePackages(List<SpdxPackage> pkgsA, List<SpdxPackage> pkgsB) {
		int bIndex = 0;
		int aIndex = 0;
		List<SpdxPackage> alRetval = new ArrayList<>();;
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
	 * @param filesA
	 * @param filesB
	 * @return
	 * @throws InvalidSPDXAnalysisException 
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
	 * @throws InvalidSPDXAnalysisException 
	 * 
	 */
	private void compareCreators() throws InvalidSPDXAnalysisException {
		this.creatorInformationEquals = true;
		this.licenseListVersionEquals = true;
		// this will be a N x N comparison of all creators to fill the
		// hashmap uniqueCreators
		for (int i = 0; i < spdxDocs.size(); i++) {
			SpdxCreatorInformation creatorInfoA = spdxDocs.get(i).getCreationInfo();
			Collection<String> creatorsA = creatorInfoA.getCreators();
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
				Collection<String> creatorsB = creatorInfoB.getCreators();

				// find any creators in A that are not in B
				List<String> uniqueA = findUniqueString(creatorsA, creatorsB);
				if (uniqueA != null && uniqueA.size() > 0) {
					uniqueAMap.put(spdxDocs.get(j), uniqueA);					
				}
				// compare creator comments
				if (!stringsEqual(creatorInfoA.getComment(), creatorInfoB.getComment())) {
					this.creatorInformationEquals = false;
				}
				// compare creation dates
				if (!stringsEqual(creatorInfoA.getCreated(), creatorInfoB.getCreated())) {
					this.creatorInformationEquals = false;
				}
				// compare license list versions
				if (!stringsEqual(creatorInfoA.getLicenseListVersion(), creatorInfoB.getLicenseListVersion())) {
					this.creatorInformationEquals = false;
					this.licenseListVersionEquals = false;
				}
			}
			if (uniqueAMap.keySet().size() > 0) {
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
	 * @param stringsA
	 * @param stringsB
	 * @return
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
	 * @throws SpdxCompareException 
	 */
	private void comparePackages() throws SpdxCompareException {
		if (this.spdxDocs == null || this.spdxDocs.size() < 1) {
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
					throw(new SpdxCompareException("Error collecting packages from SPDX document "+spdxDocs.get(i).getName(), e));
				} catch (InvalidSPDXAnalysisException e1) {
					throw(new SpdxCompareException("Error collecting packages from SPDX document ", e));
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
						throw(new SpdxCompareException("Error collecting packages from SPDX document "+spdxDocs.get(i).getName(), e));
					} catch (InvalidSPDXAnalysisException e1) {
						throw(new SpdxCompareException("Error collecting packages from SPDX document ", e));
					}
				}
				//Note that the files arrays must be sorted for the find methods to work
				Collections.sort(pkgsB);
				List<SpdxPackage> uniqueAB = findUniquePackages(pkgsA, pkgsB);
				if (uniqueAB != null && uniqueAB.size() > 0) {
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
	 * @param spdxDocument
	 * @param pkgs
	 * @param extractedLicenseIdMap 
	 * @throws SpdxCompareException 
	 */
	private void addPackageComparers(SpdxDocument spdxDocument,
			List<SpdxPackage> pkgs, Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap) throws SpdxCompareException {
		try {
			List<String> addedPackageNames = new ArrayList<String>();
			for (SpdxPackage pkg:pkgs) {
				if (!pkg.getName().isPresent()) {
					logger.warn("Missing package name for package comparer.  Skipping unnamed package");
					continue;
				}
				if (addedPackageNames.contains(pkg.getName().get())) {
					logger.warn("Duplicate package names: "+pkg.getName().get()+".  Only comparing the first instance");
					continue;
				}
				SpdxPackageComparer mpc = this.packageComparers.get(pkg.getName().get());
				if (mpc == null) {
					mpc = new SpdxPackageComparer(extractedLicenseIdMap);
					this.packageComparers.put(pkg.getName().get(), mpc);
				}
				mpc.addDocumentPackage(spdxDocument, pkg);
				addedPackageNames.add(pkg.getName().get());
			}
		} catch (InvalidSPDXAnalysisException ex) {
			throw new SpdxCompareException("Error getting package name", ex);
		}
	}

	/**
	 * Compares two licenses from two different SPDX documents taking into account
	 * the extracted license infos who's ID's may be different between the two documents
	 * Note: The ExtracedLicenseIDMap must be initialized before this method is invoked
	 * @param doc1 Index of the SPDX document for license1
	 * @param license1
	 * @param doc2 Index of the SPDX document for license2
	 * @param license2
	 * @return true if the licenses are equivalent
	 * @throws SpdxCompareException 
	 */
	public boolean compareLicense(int doc1,
			AnyLicenseInfo license1, int doc2,
			AnyLicenseInfo license2) throws SpdxCompareException {
		this.checkDocsIndex(doc1);
		this.checkDocsIndex(doc2);
		Map<SpdxDocument, Map<String, String>> hm = this.extractedLicenseIdMap.get(this.spdxDocs.get(doc1));
		if (hm == null) {
			throw(new SpdxCompareException("Compare License Error - Extracted license id map has not been initialized."));
		}
		Map<String, String> xlationMap = hm.get(this.spdxDocs.get(doc2));
		if (xlationMap == null) {
			throw(new SpdxCompareException("Compare License Exception - Extracted license id map has not been initialized."));
		}
		try {
			return LicenseCompareHelper.isLicenseEqual(license1, license2, xlationMap);
		} catch (InvalidSPDXAnalysisException e) {
			throw new SpdxCompareException("Error comparing licenses", e);
		}
	}

	/**
	 * @param verificationCode
	 * @param verificationCode2
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
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
		if (!stringCollectionsEqual(verificationCode.get().getExcludedFileNames(), 
				verificationCode2.get().getExcludedFileNames())) {
			return false;
		}
		return true;		
	}

	/**
	 * Compare the document level fields and sets the difference found depending on any differences
	 * @throws SpdxCompareException 
	 */
	private void compareDocumentFields() throws SpdxCompareException {
		compareDataLicense();
		compareDocumentComments();
		compareSpdxVerions();
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
					if (!collectionsEquivalent(itemsA, itemsB)) {
						this.documentContentsEquals = false;
						this.differenceFound = true;
						return;
					}
				}
			}
		} catch(InvalidSPDXAnalysisException ex) {
			throw(new SpdxCompareException("Error getting SPDX document items: "+ex.getMessage()));
		}
	}

	/**
	 * @throws SpdxCompareException 
	 * 
	 */
	private void compareSpdxVerions() throws SpdxCompareException {
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
	 * @throws SpdxCompareException 
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
	 * @throws SpdxCompareException 
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
			throw(new SpdxCompareException("SPDX analysis error during compare data license: "+e.getMessage(),e));
		}
	}

	/**
	 * Compares the extracted license infos in all documents and builds the 
	 * maps for translating IDs as well as capturing any differences between the
	 * extracted licensing information
	 * @throws InvalidSPDXAnalysisException 
	 * @throws SpdxCompareException 
	 */
	private void compareExtractedLicenseInfos() throws InvalidSPDXAnalysisException, SpdxCompareException {
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
				if (uniqueLicenses.size() > 0) {
					uniqueMap.put(spdxDocs.get(j), uniqueLicenses);
				}
				// differences
				if (alDifferences.size() > 0) {
					differenceMap.put(spdxDocs.get(j), alDifferences);
				}
				// map
				licenseIdMap.put(spdxDocs.get(j), idMap);
			}
			if (uniqueMap.keySet().size() > 0) {
				this.uniqueExtractedLicenses.put(spdxDocs.get(i), uniqueMap);
			}
			if (differenceMap.keySet().size() > 0) {
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
	 * @param extractedLicensesA
	 * @param extractedLicensesB
	 * @param idMap Map of license IDs for licenses considered equal
	 * @param alDifferences Array list of license differences found where the license text is equivalent but other properties are different
	 * @param uniqueLicenses ArrayList if licenses found in the A but not found in B
	 * @throws InvalidSPDXAnalysisException 
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
				if (LicenseCompareHelper.isLicenseTextEquivalent(licA.getExtractedText(), 
						licB.getExtractedText())) {
					foundTextMatch = true;
					if (!foundMatch) {
						idMap.put(licA.getLicenseId(), licB.getLicenseId());
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
	 * Compares the non-license text and non-id fields and returns true
	 * if all relevant fields are equal
	 * @param spdxNonStandardLicenseA
	 * @param spdxNonStandardLicenseB
	 * @return
	 * @throws InvalidSPDXAnalysisException 
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
		if (!stringCollectionsEqual(spdxNonStandardLicenseA.getSeeAlso(), spdxNonStandardLicenseB.getSeeAlso())) {
			return false;
		}
		// if we made it here, everything is equal
		return true;
	}

	/**
	 * Compares 2 collections and returns true if the contents are equal
	 * ignoring order and trimming strings.  Nulls are also considered as equal to other nulls.
	 * @param stringsA
	 * @param stringsB
	 * @return
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
	 * @param stringsA
	 * @param stringsB
	 * @return
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
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean objectsEqual(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}
	
	/**
	 * @param elementA
	 * @param elementB
	 * @return true of the elements are present and equivalent
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static boolean elementsEquivalent(Optional<? extends ModelObject> elementA, Optional<? extends ModelObject> elementB) throws InvalidSPDXAnalysisException {
		if (elementA.isPresent()) {
			if (elementB.isPresent()) {
				return elementA.get().equivalent(elementB.get());
			} else {
				return false;
			}
		} else {
			return !elementB.isPresent();
		}
	}
	
	/**
	 * @param collectionA
	 * @param collectionB
	 * @throws InvalidSPDXAnalysisException 
	 * @return true if the collections all contain equivalent items
	 */
	public static boolean collectionsEquivalent(Collection<? extends ModelObject> collectionA, Collection<? extends ModelObject> collectionB) throws InvalidSPDXAnalysisException {
		if (Objects.isNull(collectionA)) {
			return Objects.isNull(collectionB);
		}
		if (Objects.isNull(collectionB)) {
			return false;
		}
		if (collectionA.size() != collectionB.size()) {
			return false;
		}
		for (ModelObject elementA:collectionA) {
			if (Objects.isNull(elementA)) {
				continue;
			}
			boolean found = false;
			for (ModelObject elementB:collectionB) {
				if (Objects.isNull(elementB)) {
					continue;
				}
				if (elementA.equivalent(elementB)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compare two object lists
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static boolean listsEquals(List<? extends Object> a1, List<? extends Object> a2) {
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
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static boolean collectionsEquals(Collection<? extends Object> a1, Collection<? extends Object> a2) {
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
	 * @param stringA
	 * @param stringB
	 * @return
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
	 * @param stringA
	 * @param stringB
	 * @return
	 */
	public static boolean stringsEqual(Optional<String> stringA, Optional<String> stringB) {
		String compA;
		String compB;
		if (!stringA.isPresent()) {
			compA = "";
		} else {
			compA = stringA.get().replace("\r\n", "\n").trim();
		}
		if (!stringB.isPresent()) {
			compB = "";
		} else {
			compB = stringB.get().replace("\r\n", "\n").trim();
		}
		return (compA.equals(compB));
	}
	
	/**
	 * Compares two strings including trimming the string and taking into account
	 * they may be null.  Null is considered a smaller value
	 * @param stringA
	 * @param stringB
	 * @return
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
	 * @param stringA
	 * @param stringB
	 * @return
	 */
	public static int compareStrings(Optional<String> stringA, Optional<String> stringB) {
		if (!stringA.isPresent()) {
			if (!stringB.isPresent()) {
				return 0;
			} else {
				return -1;
			}
		}
		if (!stringB.isPresent()) {
			return 1;
		}
		return (stringA.get().trim().compareTo(stringB.get().trim()));
	}

	/**
	 * 
	 */
	private void clearCompareResults() {
		this.differenceFound = false;
		this.licenseDifferences.clear();
		this.uniqueExtractedLicenses.clear();
		this.extractedLicenseIdMap.clear();
		this.uniqueCreators.clear();
	}

	/**
	 * @return
	 */
	public boolean isDifferenceFound() {
		return this.differenceFound;
	}

	/**
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isSpdxVersionEqual() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return this.spdxVersionsEqual;
	}

	/**
	 * checks to make sure there is not a compare in progress
	 * @throws SpdxCompareException 
	 * 
	 */
	private void checkInProgress() throws SpdxCompareException {
		if (compareInProgress) {
			throw(new SpdxCompareException("Compare in progress - can not obtain compare results until compare has completed"));
		}
	}

	/**
	 * Validates that the spdx dcouments field has been initialized
	 * @throws SpdxCompareException 
	 */
	private void checkDocsField() throws SpdxCompareException {
		if (this.spdxDocs == null) {
			throw(new SpdxCompareException("No compare has been performed"));
		}
		if (this.spdxDocs.size() < 2) {
			throw(new SpdxCompareException("Insufficient documents compared - must provide at least 2 SPDX documents"));
		}
	}
	
	private void checkDocsIndex(int index) throws SpdxCompareException {
		if (this.spdxDocs == null) {
			throw(new SpdxCompareException("No compare has been performed"));
		}
		if (index < 0) {
			throw(new SpdxCompareException("Invalid index for SPDX document compare - must be greater than or equal to zero"));
		}
		if (index >= spdxDocs.size()) {
			throw(new SpdxCompareException("Invalid index for SPDX document compare - SPDX document index "+String.valueOf(index)+" does not exist."));
		}
	}

	/**
	 * @param docIndex Reference to which document number - 0 is the first document parameter in compare
	 * @return
	 * @throws SpdxCompareException 
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
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isDataLicenseEqual() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return this.dataLicenseEqual;
	}

	/**
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isDocumentCommentsEqual() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return this.documentCommentsEqual;
	}
	
	private boolean _isExternalDcoumentRefsEqualsNoCheck() {
		Iterator<Entry<SpdxDocument, Map<SpdxDocument, List<ExternalDocumentRef>>>> iter = this.uniqueExternalDocumentRefs.entrySet().iterator();
		while (iter.hasNext()) {
			Iterator<List<ExternalDocumentRef>> docIterator = iter.next().getValue().values().iterator();
			while (docIterator.hasNext()) {
				if (docIterator.next().size() > 0) {
					return false;
				}
			}
		}
		return true;
	}


	public boolean isExternalDcoumentRefsEquals() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return _isExternalDcoumentRefsEqualsNoCheck();
	}

	
	public boolean isExtractedLicensingInfosEqual() throws SpdxCompareException {
		checkInProgress();
		checkDocsField();
		return _isExtractedLicensingInfoEqualsNoCheck();
	}

	/**
	 * @return
	 */
	private boolean _isExtractedLicensingInfoEqualsNoCheck() {
		// check for unique extraced license infos
		Iterator<Entry<SpdxDocument, Map<SpdxDocument, List<ExtractedLicenseInfo>>>> uniqueIter = 
			this.uniqueExtractedLicenses.entrySet().iterator();
		while (uniqueIter.hasNext()) {
			Entry<SpdxDocument, Map<SpdxDocument, List<ExtractedLicenseInfo>>> entry = uniqueIter.next();
			Iterator<Entry<SpdxDocument, List<ExtractedLicenseInfo>>> entryIter = entry.getValue().entrySet().iterator();
			while(entryIter.hasNext()) {
				List<ExtractedLicenseInfo> licenses = entryIter.next().getValue();
				if (licenses != null && licenses.size() > 0) {
					return false;
				}
			}
		}
		// check differences
		Iterator<Entry<SpdxDocument, Map<SpdxDocument,List<SpdxLicenseDifference>>>> diffIterator = this.licenseDifferences.entrySet().iterator();
		while (diffIterator.hasNext()) {
			Iterator<Entry<SpdxDocument,List<SpdxLicenseDifference>>> entryIter = diffIterator.next().getValue().entrySet().iterator();
			while (entryIter.hasNext()) {
				List<SpdxLicenseDifference> differences = entryIter.next().getValue();
				if (differences != null && differences.size() > 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Retrieves any unique extracted licenses fromt the first SPDX document index
	 * relative to the second - unique is determined by the license text matching
	 * @param docIndexA
	 * @param docIndexB
	 * @return
	 * @throws SpdxCompareException 
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
	 * @param docIndexA
	 * @param docIndexB
	 * @return
	 * @throws SpdxCompareException 
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
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isCreatorInformationEqual() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this.creatorInformationEquals;
	}

	/**
	 * Returns any creators which are in the SPDX document 1 which are not in document 2
	 * @param doc1index
	 * @param doc2index
	 * @return
	 * @throws SpdxCompareException 
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
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isfilesEquals() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this._isFilesEqualsNoCheck();
	}
	
	/**
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isPackagesEquals() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this._isPackagesEqualsNoCheck();
	}

	/**
	 * @return
	 * @throws SpdxCompareException
	 */
	public boolean isDocumentAnnotationsEquals() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return _isDocumentAnnotationsEqualsNoCheck();
	}
	/**
	 * @return
	 */
	private boolean _isDocumentAnnotationsEqualsNoCheck() {
		Iterator<Entry<SpdxDocument, Map<SpdxDocument, List<Annotation>>>> iter = this.uniqueDocumentAnnotations.entrySet().iterator();
		while (iter.hasNext()) {
			Iterator<List<Annotation>> docIterator = iter.next().getValue().values().iterator();
			while (docIterator.hasNext()) {
				if (docIterator.next().size() > 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * @return
	 * @throws SpdxCompareException
	 */
	public boolean isDocumentRelationshipsEquals() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return _isDocumentRelationshipsEqualsNoCheck();
	}
	/**
	 * @return
	 */
	private boolean _isDocumentRelationshipsEqualsNoCheck() {
		Iterator<Entry<SpdxDocument, Map<SpdxDocument, List<Relationship>>>> iter = this.uniqueDocumentRelationships.entrySet().iterator();
		while (iter.hasNext()) {
			Iterator<List<Relationship>> docIterator = iter.next().getValue().values().iterator();
			while (docIterator.hasNext()) {
				if (docIterator.next().size() > 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return
	 */
	private boolean _isFilesEqualsNoCheck() {
		if (!this.uniqueFiles.isEmpty()) {
			return false;
		}
		if (!this.fileDifferences.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * @return
	 * @throws SpdxCompareException 
	 */
	private boolean _isPackagesEqualsNoCheck() throws SpdxCompareException {
		Iterator<Entry<SpdxDocument, Map<SpdxDocument, List<SpdxPackage>>>> iter = this.uniquePackages.entrySet().iterator();
		while (iter.hasNext()) {
			Iterator<List<SpdxPackage>> docIterator = iter.next().getValue().values().iterator();
			while (docIterator.hasNext()) {
				if (docIterator.next().size() > 0) {
					return false;
				}
			}
		}
		Iterator<SpdxPackageComparer> diffIter = this.packageComparers.values().iterator();
		while (diffIter.hasNext()) {
			if (diffIter.next().isDifferenceFound()) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Return any files which are in spdx document index 1 but not in spdx document index 2
	 * @param docindex1
	 * @param docindex2
	 * @return
	 * @throws SpdxCompareException 
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
	 * @param docindex1
	 * @param docindex2
	 * @return
	 * @throws SpdxCompareException 
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
	 * @param docindex1
	 * @param docindex2
	 * @return
	 * @throws SpdxCompareException 
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
	 * @param docindex1
	 * @param docindex2
	 * @return
	 * @throws SpdxCompareException
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
	 * @param docindex1
	 * @param docindex2
	 * @return
	 * @throws SpdxCompareException
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
	 * @param docindex1
	 * @param docindex2
	 * @return
	 * @throws SpdxCompareException
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
	 * @throws SpdxCompareException 
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
				new SpdxPackageComparer[this.packageComparers.values().size()]);
	}

	/**
	 * @return
	 */
	public int getNumSpdxDocs() {
		return this.spdxDocs.size();
	}

	/**
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isLicenseListVersionEqual() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this.licenseListVersionEquals;
	}

	/**
	 * Find any SPDX checksums which are in elementsA but not in elementsB
	 * @param checksumsA
	 * @param checksumsB
	 * @return
	 * @throws InvalidSPDXAnalysisException 
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
	 * @param annotationsA
	 * @param annotationsB
	 * @return
	 * @throws InvalidSPDXAnalysisException 
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
	 * @param relationshipsA
	 * @param relationshipsB
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static List<Relationship> findUniqueRelationships(
			Collection<Relationship> relationshipsA, Collection<Relationship> relationshipsB) throws InvalidSPDXAnalysisException {
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
					if (relA.equivalent(relB)) {
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
	 * @param externalDocRefsA
	 * @param externalDocRefsB
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static List<ExternalDocumentRef> findUniqueExternalDocumentRefs(
			Collection<ExternalDocumentRef> externalDocRefsA, Collection<ExternalDocumentRef> externalDocRefsB) throws InvalidSPDXAnalysisException {
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
							elementsEquivalent(docRefA.getChecksum(), docRefB.getChecksum())) {
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
	 * @return
	 */
	public List<SpdxDocument> getSpdxDocuments() {
		return this.spdxDocs;
	}

	/**
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isDocumentContentsEquals() throws SpdxCompareException {
		checkInProgress();
		return this.documentContentsEquals;
	}

	/**
	 * @return
	 * @throws SpdxCompareException 
	 */
	public boolean isSnippetsEqual() throws SpdxCompareException {
		this.checkDocsField();
		this.checkInProgress();
		return this._isSnippetsEqualsNoCheck();
	}

	/**
	 * @return
	 * @throws SpdxCompareException 
	 */
	private boolean _isSnippetsEqualsNoCheck() throws SpdxCompareException {
		Iterator<Entry<SpdxDocument, Map<SpdxDocument, List<SpdxSnippet>>>> iter = this.uniqueSnippets.entrySet().iterator();
		while (iter.hasNext()) {
			Iterator<List<SpdxSnippet>> docIterator = iter.next().getValue().values().iterator();
			while (docIterator.hasNext()) {
				if (docIterator.next().size() > 0) {
					return false;
				}
			}
		}
		Iterator<SpdxSnippetComparer> diffIter = this.snippetComparers.values().iterator();
		while (diffIter.hasNext()) {
			if (diffIter.next().isDifferenceFound()) {
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
				new SpdxSnippetComparer[this.snippetComparers.values().size()]);
	}
}
