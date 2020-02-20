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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.Relationship;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.SpdxItem;
import org.spdx.library.model.license.AnyLicenseInfo;

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
	private Map<SpdxDocument, Map<SpdxDocument, List<Checksum>>> uniqueChecksums = new HashMap<>();

	private boolean checksumsEquals = true;
	private boolean typesEquals = true;

	
	public SpdxFileComparer(Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap) {
		super(extractedLicenseIdMap);
	}
	
	/**
	 * Add a file to the comparer and compare to the existing files
	 * @param spdxDocument document containing the file
	 * @param spdxFile
	 * @throws SpdxCompareException
	 * @throws InvalidSPDXAnalysisException 
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
	 * @param spdxDocument
	 * @param checksums
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	private void compareNewFileChecksums(SpdxDocument spdxDocument,
			Collection<Checksum> checksums) throws SpdxCompareException, InvalidSPDXAnalysisException {

		Map<SpdxDocument, List<Checksum>> docUniqueChecksums = new HashMap<>();
		this.uniqueChecksums.put(spdxDocument, docUniqueChecksums);
		Iterator<Entry<SpdxDocument,SpdxItem>> iter = this.documentItem.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<SpdxDocument,SpdxItem> entry = iter.next();
			if (entry.getValue() instanceof SpdxFile) {
				Collection<Checksum> compareChecksums = ((SpdxFile)entry.getValue()).getChecksums();
				List<Checksum> uniqueChecksums = SpdxComparer.findUniqueChecksums(checksums, compareChecksums);
				if (uniqueChecksums.size() > 0) {
					this.checksumsEquals = false;
					this.differenceFound = true;
				}
				docUniqueChecksums.put(entry.getKey(), uniqueChecksums);
				Map<SpdxDocument, List<Checksum>> compareUniqueChecksums = this.uniqueChecksums.get(entry.getKey());
				if (compareUniqueChecksums == null) {
					compareUniqueChecksums = new HashMap<>();
					this.uniqueChecksums.put(entry.getKey(), compareUniqueChecksums);
				}
				uniqueChecksums = SpdxComparer.findUniqueChecksums(compareChecksums, checksums);
				if (uniqueChecksums.size() > 0) {
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
	 * @param docA
	 * @param docB
	 * @return
	 * @throws SpdxCompareException 
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
	 * @throws SpdxCompareException 
	 * 
	 */
	@Override
    protected void checkInProgress() throws SpdxCompareException {
		super.checkInProgress();
		if (inProgress) {
			throw(new SpdxCompareException("File compare in progress - can not obtain compare results until compare has completed"));
		}
	}
	
	private void checkCompareMade() throws SpdxCompareException {
		if (this.documentItem.size() < 1) {
			throw(new SpdxCompareException("Trying to obgain results of a file compare before a file compare has been performed"));
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
	 * @return
	 * @throws SpdxCompareException 
	 */
	@Override
    public boolean isDifferenceFound() throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		return differenceFound || super.isDifferenceFound();
	}


	/**
	 * Return a file difference for the file contained in two different documents
	 * @param docA
	 * @param docB
	 * @return
	 * @throws SpdxCompareException
	 */
	public SpdxFileDifference getFileDifference(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		checkCompareMade();
		try {
			SpdxItem itemA = this.documentItem.get(docA);
			if (itemA == null || !(itemA instanceof SpdxFile)) {
				throw(new SpdxCompareException("No SPDX File associated with "+docA.getName()));
			}
			SpdxFile fileA = (SpdxFile)itemA;
			SpdxItem itemB = this.documentItem.get(docB);
			if (itemB == null || !(itemB instanceof SpdxFile)) {
				throw(new SpdxCompareException("No SPDX File associated with "+docB.getName()));
			}
			SpdxFile fileB = (SpdxFile)itemB;
			List<AnyLicenseInfo> uniqueLicenseInfoInFilesA = this.getUniqueSeenLicenses(docA, docB);
			List<AnyLicenseInfo> uniqueLicenseInfoInFilesB = this.getUniqueSeenLicenses(docB, docA);
			boolean licenseInfoInFilesEquals = uniqueLicenseInfoInFilesA.size() == 0 &&
					uniqueLicenseInfoInFilesB.size() == 0;
			List<Checksum> uniqueChecksumsA = this.getUniqueChecksums(docA, docB);
			List<Checksum> uniqueChecksumsB = this.getUniqueChecksums(docB, docA);
			boolean checksumsEquals = uniqueChecksumsA.size() == 0 && 
					uniqueChecksumsB.size() == 0;
			List<Relationship> uniqueRelationshipA = this.getUniqueRelationship(docA, docB);
			List<Relationship> uniqueRelationshipB = this.getUniqueRelationship(docB, docA);
			boolean relationshipsEquals = uniqueRelationshipA.size() == 0 &&
					uniqueRelationshipB.size() == 0;
			List<Annotation> uniqueAnnotationsA = this.getUniqueAnnotations(docA, docB);
			List<Annotation> uniqueAnnotationsB = this.getUniqueAnnotations(docB, docA);
			boolean annotationsEquals = uniqueAnnotationsA.size() == 0 &&
					uniqueAnnotationsB.size() == 0;
			
			return new SpdxFileDifference(fileA, fileB, 
					fileA.getLicenseConcluded().equals(fileB.getLicenseConcluded()),
					licenseInfoInFilesEquals, uniqueLicenseInfoInFilesA, uniqueLicenseInfoInFilesB,					
					checksumsEquals, uniqueChecksumsA, uniqueChecksumsB, 				
					relationshipsEquals, uniqueRelationshipB, uniqueRelationshipB,
					annotationsEquals, uniqueAnnotationsA, uniqueAnnotationsB);
		} catch (InvalidSPDXAnalysisException e) {
			throw (new SpdxCompareException("Error reading SPDX file propoerties: "+e.getMessage(),e));
		}
	}	
}
