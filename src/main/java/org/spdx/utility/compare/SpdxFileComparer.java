/**
 * Copyright (c) 2020 Source Auditor Inc.
 * <p>
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

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.Annotation;
import org.spdx.library.model.v2.Checksum;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.SpdxFile;
import org.spdx.library.model.v2.SpdxItem;
import org.spdx.library.model.v2.license.AnyLicenseInfo;

/**
 * Compares two SPDX files.  The <code>compare(fileA, fileB)</code> method will perform the comparison and
 * store the results.  <code>isDifferenceFound()</code> will return true of any 
 * differences were found.
 * @author Gary O'Neall
 *
 */
public class SpdxFileComparer extends SpdxItemComparer {
	private boolean inProgress = false;
	private boolean differenceFound = false;
	private boolean contributorsEquals = true;
	private boolean noticeTextEquals = true;

	/**
	 *  Map of checksums found in one document but not another
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<Checksum>>> uniqueChecksums = new HashMap<>();

	private boolean checksumsEquals = true;
	private boolean typesEquals = true;

	
	public SpdxFileComparer(Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap) {
		super(extractedLicenseIdMap);
	}
	
	/**
	 * Add a file to the comparer and compare to the existing files
	 * @param spdxDocument document containing the file
	 * @param spdxFile file to add
	 * @throws SpdxCompareException on compare errors
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public void addDocumentFile(SpdxDocument spdxDocument,
			SpdxFile spdxFile) throws SpdxCompareException, InvalidSPDXAnalysisException {
		checkInProgress();
		inProgress = true;
		Iterator<Entry<SpdxDocument, SpdxItem>> iter = this.documentItem.entrySet().iterator();
		Entry<SpdxDocument, SpdxItem> entry;
		SpdxFile filesB = null;
		while (iter.hasNext() && filesB == null) {
			entry = iter.next();
			if (entry.getValue() instanceof SpdxFile) {
				filesB = (SpdxFile)entry.getValue();
			}
		}
		if (filesB != null) {
			// Checksums
			compareNewFileChecksums(spdxDocument, spdxFile.getChecksums());
			// Type
			if (!SpdxComparer.collectionsEquals(spdxFile.getFileTypes(), filesB.getFileTypes())) {
				this.typesEquals = false;
				this.differenceFound = true;
			}
			// contributors
			if (!SpdxComparer.stringCollectionsEqual(spdxFile.getFileContributors(), filesB.getFileContributors())) {
				this.contributorsEquals = false;
				this.differenceFound = true;
			}
			// notice text
			if (!SpdxComparer.stringsEqual(spdxFile.getNoticeText(), filesB.getNoticeText())) {
				this.noticeTextEquals = false;
				this.differenceFound = true;
			}
		}

		super.addDocumentItem(spdxDocument, spdxFile);
		inProgress = false;
	}
	
	/**
	 * Compare the checks for a new file being added to the existing
	 * package checksums filling in the unique checksums map
	 * @param spdxDocument document containing the checksums
	 * @param checksums checksums to compare
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private void compareNewFileChecksums(SpdxDocument spdxDocument,
			Collection<Checksum> checksums) throws InvalidSPDXAnalysisException {

		Map<SpdxDocument, List<Checksum>> docUniqueChecksums = new HashMap<>();
		this.uniqueChecksums.put(spdxDocument, docUniqueChecksums);
        for (Entry<SpdxDocument, SpdxItem> entry : this.documentItem.entrySet()) {
            if (entry.getValue() instanceof SpdxFile) {
                Collection<Checksum> compareChecksums = ((SpdxFile) entry.getValue()).getChecksums();
                List<Checksum> uniqueChecksums = SpdxComparer.findUniqueChecksums(checksums, compareChecksums);
                if (!uniqueChecksums.isEmpty()) {
                    this.checksumsEquals = false;
                    this.differenceFound = true;
                }
                docUniqueChecksums.put(entry.getKey(), uniqueChecksums);
                Map<SpdxDocument, List<Checksum>> compareUniqueChecksums = this.uniqueChecksums.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
                uniqueChecksums = SpdxComparer.findUniqueChecksums(compareChecksums, checksums);
                if (!uniqueChecksums.isEmpty()) {
                    this.checksumsEquals = false;
                    this.differenceFound = true;
                }
                compareUniqueChecksums.put(spdxDocument, uniqueChecksums);
            }
        }
	}

	
	public SpdxFile getFile(SpdxDocument spdxDocument) throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		SpdxItem item = this.getItem(spdxDocument);
		if (item instanceof SpdxFile) {
			return (SpdxFile) item;
		} else {
			return null;
		}
	}

	/**
	 * @return the checksumsEquals
	 */
	public boolean isChecksumsEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return checksumsEquals;
	}
	
	/**
	 * Get the checksums which are present in the file contained document A but not in document B
	 * @param docA document to compare
	 * @param docB document to compare
	 * @return  the checksums which are present in the file contained document A but not in document B
	 * @throws SpdxCompareException on compare errors
	 */
	public List<Checksum> getUniqueChecksums(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		Map<SpdxDocument, List<Checksum>> uniqueMap = this.uniqueChecksums.get(docA);
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<Checksum> retval = uniqueMap.get(docB);
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}

	/**
	 * @return the typesEquals
	 */
	public boolean isTypesEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return typesEquals;
	}

	/**
	 * checks to make sure there is not a compare in progress
	 * @throws SpdxCompareException on compare errors
	 * 
	 */
	@Override
    protected void checkInProgress() throws SpdxCompareException {
		super.checkInProgress();
		if (inProgress) {
			throw new SpdxCompareException("File compare in progress - can not obtain compare results until compare has completed");
		}
	}

	/**
	 * @return the contributorsEquals
	 */
	public boolean isContributorsEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return contributorsEquals;
	}

	/**
	 * @return the noticeTextEquals
	 */
	public boolean isNoticeTextEquals() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return noticeTextEquals;
	}

	/**
	 * @return true if any differences are found
	 * @throws SpdxCompareException on compare errors
	 */
	@Override
    public boolean isDifferenceFound() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return differenceFound || super.isDifferenceFound();
	}


	/**
	 * Return a file difference for the file contained in two different documents
	 * @param docA document containing files to compare
	 * @param docB document containing files to compare
	 * @return file differences between docA and docB
	 * @throws SpdxCompareException on compare errors
	 */
	public SpdxFileDifference getFileDifference(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		try {
			SpdxItem itemA = this.documentItem.get(docA);
			if (!(itemA instanceof SpdxFile)) {
				throw new SpdxCompareException("No SPDX File associated with "+docA.getName());
			}
			SpdxFile fileA = (SpdxFile)itemA;
			SpdxItem itemB = this.documentItem.get(docB);
			if (!(itemB instanceof SpdxFile)) {
				throw new SpdxCompareException("No SPDX File associated with "+docB.getName());
			}
			SpdxFile fileB = (SpdxFile)itemB;
			List<AnyLicenseInfo> uniqueLicenseInfoInFilesA = this.getUniqueSeenLicenses(docA, docB);
			List<AnyLicenseInfo> uniqueLicenseInfoInFilesB = this.getUniqueSeenLicenses(docB, docA);
			boolean licenseInfoInFilesEquals = uniqueLicenseInfoInFilesA.isEmpty() &&
                    uniqueLicenseInfoInFilesB.isEmpty();
			List<Checksum> uniqueChecksumsA = this.getUniqueChecksums(docA, docB);
			List<Checksum> uniqueChecksumsB = this.getUniqueChecksums(docB, docA);
			boolean checksumsEquals = uniqueChecksumsA.isEmpty() &&
                    uniqueChecksumsB.isEmpty();
			List<Relationship> uniqueRelationshipA = this.getUniqueRelationship(docA, docB);
			List<Relationship> uniqueRelationshipB = this.getUniqueRelationship(docB, docA);
			boolean relationshipsEquals = uniqueRelationshipA.isEmpty() &&
                    uniqueRelationshipB.isEmpty();
			List<Annotation> uniqueAnnotationsA = this.getUniqueAnnotations(docA, docB);
			List<Annotation> uniqueAnnotationsB = this.getUniqueAnnotations(docB, docA);
			boolean annotationsEquals = uniqueAnnotationsA.isEmpty() &&
                    uniqueAnnotationsB.isEmpty();
			
			return new SpdxFileDifference(fileA, fileB, 
					fileA.getLicenseConcluded().equals(fileB.getLicenseConcluded()),
					licenseInfoInFilesEquals, uniqueLicenseInfoInFilesA, uniqueLicenseInfoInFilesB,					
					checksumsEquals, uniqueChecksumsA, uniqueChecksumsB, 				
					relationshipsEquals, uniqueRelationshipB, uniqueRelationshipB,
					annotationsEquals, uniqueAnnotationsA, uniqueAnnotationsB);
		} catch (InvalidSPDXAnalysisException e) {
			throw (new SpdxCompareException("Error reading SPDX file properties: "+e.getMessage(),e));
		}
	}	
}
