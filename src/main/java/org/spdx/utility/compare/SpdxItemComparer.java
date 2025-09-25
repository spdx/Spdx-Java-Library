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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.Annotation;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.SpdxItem;
import org.spdx.library.model.v2.license.AnyLicenseInfo;


/**
 * Compares two SPDX items
 * <p>
 * The <code>compare(itemA, itemB)</code> method will perform the comparison and
 * store the results.  <code>isDifferenceFound()</code> will return true of any 
 * differences were found.
 *
 * @author Gary O'Neall
 */
public class SpdxItemComparer {
	private boolean itemInProgress = false;
	private boolean itemDifferenceFound = false;
	private boolean concludedLicenseEquals = true;
	private boolean seenLicenseEquals = true;
	protected String name = null;
	/**
	 * Map of unique extractedLicenseInfos between two documents
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<AnyLicenseInfo>>> uniqueLicenseInfosInFiles = new HashMap<>();
	
	private boolean commentsEquals = true;
	private boolean copyrightsEquals = true;
	private boolean licenseCommentsEquals = true;
	private boolean relationshipsEquals = true;
	private boolean attributionTextEquals = true;
	/**
	 * Map of unique relationships between two documents
	 */
	Map<SpdxDocument, Map<SpdxDocument, List<Relationship>>> uniqueRelationships = new HashMap<>();
	
	private boolean annotationsEquals = true;
	/**
	 * Map of unique annotations between two documents
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<Annotation>>> uniqueAnnotations = new HashMap<>();
	
	/**
	 * Map of SPDX document to Items
	 */
	protected Map<SpdxDocument, SpdxItem> documentItem = new HashMap<>();
	
	/**
	 * Mapping of all extracted license info ID's between all SPDX documents included in the comparer
	 */
	protected Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap;

	protected Map<Integer, Boolean> equivalentElements;

	public SpdxItemComparer(Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap) {
		this(extractedLicenseIdMap, new HashMap<>());
	}
	
	public SpdxItemComparer(Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap,
							Map<Integer, Boolean> equivalentElements) {
		this.extractedLicenseIdMap = extractedLicenseIdMap;
		this.equivalentElements = equivalentElements;
	}
	
	/**
	 * Add a new item to the comparer and compare the contents of the item
	 * to all items which have been previously added
	 * @param spdxDocument document containing the item
	 * @param spdxItem item to add
	 * @throws SpdxCompareException on compare error
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	public void addDocumentItem(SpdxDocument spdxDocument,
			SpdxItem spdxItem) throws SpdxCompareException, InvalidSPDXAnalysisException {
		if (this.itemInProgress) {
			throw new SpdxCompareException("Trying to add a document item while another document item is being added.");
		}
		Optional<String> oName = spdxItem.getName();
		if (this.name == null) {
            oName.ifPresent(s -> this.name = s);
		} else if (oName.isPresent() && !this.name.equals(oName.get()) && !(this instanceof SpdxSnippetComparer)) {
			throw new SpdxCompareException("Names do not match for item being added to comparer: "+
					spdxItem.getName()+", expecting "+this.name);
		}
		this.itemInProgress = true;
		Iterator<Entry<SpdxDocument, SpdxItem>> iter = this.documentItem.entrySet().iterator();
		if (iter.hasNext()) {
			Entry<SpdxDocument, SpdxItem> entry = iter.next();
			SpdxItem itemB = entry.getValue();
			Map<String, String> licenseXlationMap = this.extractedLicenseIdMap.get(spdxDocument).get(entry.getKey());
			if (!SpdxComparer.stringsEqual(spdxItem.getComment(), itemB.getComment())) {
				this.commentsEquals = false;
				this.itemDifferenceFound = true;
			}
			// Concluded License
			if (!LicenseCompareHelper.isLicenseEqual(spdxItem.getLicenseConcluded(), 
					itemB.getLicenseConcluded(), licenseXlationMap)) {
				this.concludedLicenseEquals = false;
				this.itemDifferenceFound = true;
			}
			// Copyrights
			if (!SpdxComparer.stringsEqual(spdxItem.getCopyrightText(), itemB.getCopyrightText())) {
				this.copyrightsEquals = false;
				this.itemDifferenceFound = true;
			}
			// license comments
			if (!SpdxComparer.stringsEqual(spdxItem.getLicenseComments(),
					itemB.getLicenseComments())) {
				this.licenseCommentsEquals = false;
				this.itemDifferenceFound = true;
			}
			// attributionText
			if (!SpdxComparer.collectionsEquals(spdxItem.getAttributionText(), itemB.getAttributionText())) {
				this.attributionTextEquals = false;
				this.itemDifferenceFound = true;
			}
			// Seen licenses
			compareLicenseInfosInFiles(spdxDocument, spdxItem.getLicenseInfoFromFiles());
			// relationships
			compareRelationships(spdxDocument, spdxItem.getRelationships());
			// Annotations
			compareAnnotation(spdxDocument, spdxItem.getAnnotations());
		}
		this.documentItem.put(spdxDocument, spdxItem);
		this.itemInProgress = false;
	}	
	
	/**
	 * Compares annotations and initializes the uniqueAnnotations
	 * as well as the annotationsEquals flag and sets the differenceFound to
	 * true if a difference was found for a newly added item
	 * @param spdxDocument document containing the item
	 * @param annotations annotations to compare
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	private void compareAnnotation(SpdxDocument spdxDocument,
			Collection<Annotation> annotations) throws InvalidSPDXAnalysisException {
        Map<SpdxDocument, List<Annotation>> uniqueDocAnnotations = this.uniqueAnnotations.computeIfAbsent(spdxDocument, k -> new HashMap<>());
        for (Entry<SpdxDocument, SpdxItem> entry : this.documentItem.entrySet()) {
            Map<SpdxDocument, List<Annotation>> compareDocAnnotations = this.uniqueAnnotations.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
            Collection<Annotation> compareAnnotations = entry.getValue().getAnnotations();
            List<Annotation> uniqueAnnotations = SpdxComparer.findUniqueAnnotations(annotations, compareAnnotations);
            if (!uniqueAnnotations.isEmpty()) {
                this.annotationsEquals = false;
                this.itemDifferenceFound = true;
            }
            uniqueDocAnnotations.put(entry.getKey(), uniqueAnnotations);
            uniqueAnnotations = SpdxComparer.findUniqueAnnotations(compareAnnotations, annotations);
            if (!uniqueAnnotations.isEmpty()) {
                this.annotationsEquals = false;
                this.itemDifferenceFound = true;
            }
            compareDocAnnotations.put(spdxDocument, uniqueAnnotations);
        }
	}

	/**
	 * Compares relationships and initializes the uniqueRelationships 
	 * as well as the relationshipsEquals flag and sets the differenceFound to
	 * true if a difference was found for a newly added item
	 * @param spdxDocument document containing the item
	 * @param relationships relationships to compare
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	private void compareRelationships(SpdxDocument spdxDocument,
			Collection<Relationship> relationships) throws InvalidSPDXAnalysisException {
        Map<SpdxDocument, List<Relationship>> uniqueDocRelationship = this.uniqueRelationships.computeIfAbsent(spdxDocument, k -> new HashMap<>());
        for (Entry<SpdxDocument, SpdxItem> entry : this.documentItem.entrySet()) {
            Map<SpdxDocument, List<Relationship>> uniqueCompareRelationship = this.uniqueRelationships.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
            Collection<Relationship> compareRelationships = entry.getValue().getRelationships();
            List<Relationship> uniqueRelationships = SpdxComparer.findUniqueRelationships(relationships,
					compareRelationships, equivalentElements);
            if (!uniqueRelationships.isEmpty()) {
                this.relationshipsEquals = false;
                this.itemDifferenceFound = true;
            }
            uniqueDocRelationship.put(entry.getKey(), uniqueRelationships);
            uniqueRelationships = SpdxComparer.findUniqueRelationships(compareRelationships, relationships, equivalentElements);
            if (!uniqueRelationships.isEmpty()) {
                this.relationshipsEquals = false;
                this.itemDifferenceFound = true;
            }
            uniqueCompareRelationship.put(spdxDocument, uniqueRelationships);
        }
	}

	/**
	 * Compares seen licenses and initializes the uniqueSeenLicenses 
	 * as well as the seenLicenseEquals flag and sets the differenceFound to
	 * true if a difference was found for a newly added item
	 * @param spdxDocument document containing the item
	 * @param licenses licenses to compare
	 * @throws SpdxCompareException on compare errors
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	private void compareLicenseInfosInFiles(SpdxDocument spdxDocument,
			Collection<AnyLicenseInfo> licenses) throws SpdxCompareException, InvalidSPDXAnalysisException {
        Map<SpdxDocument, List<AnyLicenseInfo>> uniqueDocLicenses =
                this.uniqueLicenseInfosInFiles.computeIfAbsent(spdxDocument, k -> new HashMap<>());
        for (Entry<SpdxDocument, SpdxItem> entry : this.documentItem.entrySet()) {
            Map<SpdxDocument, List<AnyLicenseInfo>> uniqueCompareLicenses =
                    this.uniqueLicenseInfosInFiles.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
            Collection<AnyLicenseInfo> compareLicenses = entry.getValue().getLicenseInfoFromFiles();
            List<AnyLicenseInfo> uniqueInDoc = new ArrayList<>();
            List<AnyLicenseInfo> uniqueInCompare = new ArrayList<>();
            Map<String, String> licenseXlationMap = this.extractedLicenseIdMap.get(spdxDocument).get(entry.getKey());
            compareLicenseCollections(licenses, compareLicenses, uniqueInDoc, uniqueInCompare, licenseXlationMap);
            if (!uniqueInDoc.isEmpty() || !uniqueInCompare.isEmpty()) {
                this.seenLicenseEquals = false;
                this.itemDifferenceFound = true;
            }
            uniqueDocLicenses.put(entry.getKey(), uniqueInDoc);
            uniqueCompareLicenses.put(spdxDocument, uniqueInCompare);
        }
	}
		
	/**
	 * Compares to arrays of licenses updating the alUniqueA and alUniqueB to
	 * include any licenses found in A but not B and B but not A resp.
	 * @param licensesA A licenses to compare
	 * @param licensesB B licenses to compare
	 * @param alUniqueA list of licenses that are in A but not in B
	 * @param alUniqueB list of licenses that are in B but not in A
	 * @param licenseXlationMap map to translate non-listed license IDs
	 * @throws SpdxCompareException on compare errors
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	private void compareLicenseCollections(Collection<AnyLicenseInfo> licensesA,
			Collection<AnyLicenseInfo> licensesB,
			List<AnyLicenseInfo> alUniqueA,
			List<AnyLicenseInfo> alUniqueB,
			Map<String, String> licenseXlationMap) throws SpdxCompareException, InvalidSPDXAnalysisException {
		// a bit brute force, but sorting licenses is a bit complex
		// an N x M comparison of the licenses to determine which ones are unique
		for (AnyLicenseInfo licA:licensesA) {
			boolean found = false;
			for (AnyLicenseInfo licB:licensesB) {
				if (LicenseCompareHelper.isLicenseEqual(licA, licB, licenseXlationMap)) {
					found = true;
					break;
				}
			}
			if (!found) {
				alUniqueA.add(licA);
			}
		}
		for (AnyLicenseInfo licB:licensesB) {
			boolean found = false;
			for (AnyLicenseInfo licA:licensesA) {
				if (LicenseCompareHelper.isLicenseEqual(licA, licB, licenseXlationMap)) {
					found = true;
					break;
				}
			}
			if (!found) {
				alUniqueB.add(licB);
			}
		}
	}

	/**
	 * @return the concludedLicenseEquals
	 */
	public boolean isConcludedLicenseEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return concludedLicenseEquals;
	}

	/**
	 * @return the seenLicenseEquals
	 */
	public boolean isSeenLicenseEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return seenLicenseEquals;
	}


	/**
	 * Get any licenses found in docA but not in docB
	 * @param docA A document to compare
	 * @param docB B document to compare
	 * @return any licenses found in docA but not in docB
	 * @throws SpdxCompareException on compare errors
	 */
	public List<AnyLicenseInfo> getUniqueSeenLicenses(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		Map<SpdxDocument, List<AnyLicenseInfo>> unique =  this.uniqueLicenseInfosInFiles.get(docA);
		if (unique == null) {
			return new ArrayList<>();
		}
		List<AnyLicenseInfo> retval = unique.get(docB);
		if (retval == null) {
			return new ArrayList<>();
		} else {
			return retval;
		}
	}
	
	/**
	 * @return the commentsEquals
	 */
	public boolean isCommentsEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return commentsEquals;
	}

	/**
	 * @return the copyrightsEquals
	 */
	public boolean isCopyrightsEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return copyrightsEquals;
	}

	/**
	 * @return the licenseCommentsEquals
	 */
	public boolean isLicenseCommentsEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return licenseCommentsEquals;
	}

	/**
	 * @return the licenseCommentsEquals
	 */
	@Deprecated
	public boolean isLicenseCommmentsEquals() throws SpdxCompareException {
		return isLicenseCommentsEquals();
	}

	/**
	 * checks to make sure there is not a compare in progress
	 * @throws SpdxCompareException on compare errors
	 * 
	 */
	protected void checkInProgress() throws SpdxCompareException {
		if (itemInProgress) {
			throw new SpdxCompareException("File compare in progress - can not obtain compare results until compare has completed");
		}
	}
	
	/**
	 * @throws SpdxCompareException if no comparisons have been made
	 */
	protected void checkCompareMade() throws SpdxCompareException {
		if (this.documentItem.isEmpty()) {
			throw new SpdxCompareException("Trying to obtain results of a file compare before a file compare has been performed");
		}	
	}


	/**
	 * @return true if any difference is found in the item
	 * @throws SpdxCompareException on compare error
	 */
	public boolean isDifferenceFound() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return this.itemDifferenceFound;
	}
	
	
	/**
	 * @return the inProgress
	 */
	public boolean isInProgress() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return itemInProgress;
	}


	/**
	 * Get the item contained by the document doc
	 * @param doc document containing the item
	 * @return the item contained by the document doc
	 * @throws SpdxCompareException on compare error
	 */
	public SpdxItem getItem(SpdxDocument doc) throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return this.documentItem.get(doc);
	}

	/**
	 * @return the relationshipsEquals
	 */
	public boolean isRelationshipsEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return relationshipsEquals;
	}


	/**
	 * Get relationships that are in docA but not in docB
	 * @param docA A document to compare
	 * @param docB B document to compare
	 * @return relationships that are in docA but not in docB
	 * @throws SpdxCompareException on compare error
	 */
	public List<Relationship> getUniqueRelationship(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		Map<SpdxDocument, List<Relationship>> unique = this.uniqueRelationships.get(docA);
		if (unique == null) {
			return  new ArrayList<>();
		}
		List<Relationship> retval = unique.get(docB);
		if (retval == null) {
			return  new ArrayList<>();
		} else {
			return retval;
		}
	}

	/**
	 * @return the annotationsEquals
	 */
	public boolean isAnnotationsEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return annotationsEquals;
	}


	/**
	 * @return the attributionTextEquals
	 * @throws SpdxCompareException on compare error
	 */
	public boolean isAttributionTextEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return attributionTextEquals;
	}

	/**
	 * Get annotations that are in docA but not in docB
	 * @param docA A document to compare
	 * @param docB B document to compare
	 * @return annotations that are in docA but not in docB
	 * @throws SpdxCompareException on compare error
	 */
	public List<Annotation> getUniqueAnnotations(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		Map<SpdxDocument, List<Annotation>> unique = this.uniqueAnnotations.get(docA);
		if (unique == null) {
			return  new ArrayList<>();
		}
		List<Annotation> retval = unique.get(docB);
		if (retval == null) {
			return  new ArrayList<>();
		} else {
			return retval;
		}
	}
}
