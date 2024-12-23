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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.Checksum;
import org.spdx.library.model.v2.ExternalRef;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.SpdxFile;
import org.spdx.library.model.v2.SpdxItem;
import org.spdx.library.model.v2.SpdxPackage;

/**
 * Compares two SPDX package.
 * <p>
 * The <code>compare(pkgA, pkgB)</code> method will perform the comparison and
 * store the results.  <code>isDifferenceFound()</code> will return true of any 
 * differences were found.
 * 
 * @author Gary O'Neall
 */
public class SpdxPackageComparer extends SpdxItemComparer {
	private boolean inProgress = false;
	private boolean differenceFound = false;
	private boolean packageVersionsEquals = true;
	private boolean packageFilenamesEquals = true;
	private boolean packageSuppliersEquals = true;
	private boolean packageDownloadLocationsEquals = true;
	private boolean packageVerificationCodesEquals = true;
	private boolean packageChecksumsEquals = true;
	private boolean packageSourceInfosEquals = true;
	private boolean declaredLicensesEquals = true;
	private boolean packageSummaryEquals = true;
	private boolean packageDescriptionsEquals = true;
	private boolean packageOriginatorsEqual = true;
	private boolean packageHomePagesEquals = true;
	private boolean packageFilesEquals = true;
	private boolean filesAnalyzedEquals = true;
	private boolean externalRefsEquals = true;
	/**
	 * Map of documents to a map of documents with unique checksums
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<Checksum>>> uniqueChecksums = new HashMap<>();

	/**
	 * Map of documents to a map of documents with unique files
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<SpdxFile>>> uniqueFiles = new HashMap<>();
	
	/**
	 * Map of all file differences founds between any two spdx document packages
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<SpdxFileDifference>>> fileDifferences = new HashMap<>();
	
	/**
	 * Map of documents to a map of documents with unique external refs
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<ExternalRef>>> uniqueExternalRefs = new HashMap<>();
	
	/**
	 * Map of documents to a map of documents with external refs with differences
	 */
	private final Map<SpdxDocument, Map<SpdxDocument, List<SpdxExternalRefDifference>>> externalRefDifferences = new HashMap<>();
	private final Comparator<? super ExternalRef> externalRefTypeNameComparator = (Comparator<ExternalRef>) (arg0, arg1) -> {
        if (arg0 == null) {
            if (arg1 == null) {
                return 0;
            } else {
                return -1;
            }
        }
        if (arg1 == null) {
            return 1;
        }
        try {
            if (arg0.getReferenceType() == null) {
                if (arg1.getReferenceType() != null) {
                    return -1;
                }
            } else {
                int retval = arg0.getReferenceType().compareTo(arg1.getReferenceType());
                if (retval != 0) {
                    return retval;
                }
            }
        } catch (InvalidSPDXAnalysisException e) {
            // just compare the locators
        }
        try {
            if (arg0.getReferenceLocator() == null) {
                if (arg1.getReferenceLocator() == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return arg0.getReferenceLocator().compareTo(arg1.getReferenceLocator());
            }
        } catch (InvalidSPDXAnalysisException e) {
            return -1;
        }
    };
	
	/**
	 * @param extractedLicenseIdMap map of all extracted license IDs for any SPDX documents to be added to the comparer
	 */
	public SpdxPackageComparer(Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> extractedLicenseIdMap) {
		super(extractedLicenseIdMap);
	}
	
	/**
	 * Add a package to the comparer and performs the comparison to any existing documents
	 * @param spdxDocument document containing the package
	 * @param spdxPackage packaged to be added
	 * @throws SpdxCompareException on compare errors
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public void addDocumentPackage(SpdxDocument spdxDocument,
			SpdxPackage spdxPackage) throws SpdxCompareException, InvalidSPDXAnalysisException {
		checkInProgress();
		Optional<String> packageName = spdxPackage.getName();
		if (this.name == null && packageName.isPresent()) {
			this.name = packageName.get();
		} else if (!Objects.equals(this.name,packageName.orElse(""))) {
			throw new SpdxCompareException("Names do not match for item being added to comparer: "+
			        packageName+", expecting "+this.name);
		}
		inProgress = true;
		Iterator<Entry<SpdxDocument, SpdxItem>> iter = this.documentItem.entrySet().iterator();
		SpdxPackage pkg2 = null;
		Map<String, String> licenseXlationMap = null;
		while (iter.hasNext() && pkg2 == null) {
			Entry<SpdxDocument, SpdxItem> entry = iter.next();
			if (entry.getValue() instanceof SpdxPackage) {
				pkg2 = (SpdxPackage)entry.getValue();
				licenseXlationMap = this.extractedLicenseIdMap.get(spdxDocument).get(entry.getKey());
			}
		}
		if (pkg2 != null) {
			Optional<String> v1 = spdxPackage.getVersionInfo();
			Optional<String> v2 = pkg2.getVersionInfo();
			if (!SpdxComparer.stringsEqual(v1, v2)) {
				this.packageVersionsEquals = false;
				this.differenceFound = true;
			}
			if (!SpdxComparer.stringsEqual(spdxPackage.getPackageFileName(), pkg2.getPackageFileName())) {
				this.packageFilenamesEquals = false;
				this.differenceFound = true;
			}
			if (!SpdxComparer.stringsEqual(spdxPackage.getSupplier(), pkg2.getSupplier())) {
				this.packageSuppliersEquals = false;
				this.differenceFound = true;
			}
			if (!SpdxComparer.stringsEqual(spdxPackage.getOriginator(), pkg2.getOriginator())) {
				this.packageOriginatorsEqual = false;
				this.differenceFound = true;
			}
			if (!SpdxComparer.stringsEqual(spdxPackage.getDownloadLocation(), pkg2.getDownloadLocation())) {
				this.packageDownloadLocationsEquals = false;
				this.differenceFound = true;
			}
			try {
				if (!SpdxComparer.compareVerificationCodes(spdxPackage.getPackageVerificationCode(), pkg2.getPackageVerificationCode())) {
					this.packageVerificationCodesEquals = false;
					this.differenceFound = true;
				}
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting package verification codes: "+e.getMessage(),e);
			}
			try {
				compareNewPackageChecksums(spdxDocument, spdxPackage.getChecksums());
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting package checksums: "+e.getMessage(),e);
			}
			if (!SpdxComparer.stringsEqual(spdxPackage.getSourceInfo(), pkg2.getSourceInfo())) {
				this.packageSourceInfosEquals = false;
				this.differenceFound = true;
			}
			try {
				if (!LicenseCompareHelper.isLicenseEqual(spdxPackage.getLicenseDeclared(), 
						pkg2.getLicenseDeclared(), licenseXlationMap)) {
					this.declaredLicensesEquals = false;
					this.differenceFound = true;
				}
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting declared license: "+e.getMessage(),e);
			}
			if (!SpdxComparer.stringsEqual(spdxPackage.getSummary(), pkg2.getSummary())) {
				this.packageSummaryEquals = false;
				this.differenceFound = true;
			}
			if (!SpdxComparer.stringsEqual(spdxPackage.getDescription(), pkg2.getDescription())) {
				this.packageDescriptionsEquals = false;
				this.differenceFound = true;
			}
			if (!SpdxComparer.stringsEqual(spdxPackage.getHomepage(), pkg2.getHomepage())) {
				this.packageHomePagesEquals = false;
				this.differenceFound = true;
			}
			try {
				if (spdxPackage.isFilesAnalyzed() != pkg2.isFilesAnalyzed()) {
					this.filesAnalyzedEquals = false;
					this.differenceFound = true;
				}
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting filesAnalyzed: "+e.getMessage(),e);
			}
			try {
				compareNewPackageFiles(spdxDocument, spdxPackage.getFiles());
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting package files: "+e.getMessage(),e);
			}
			try {
				compareNewPackageExternalRefs(spdxDocument, spdxPackage.getExternalRefs());
			} catch (InvalidSPDXAnalysisException e) {
				throw new SpdxCompareException("SPDX error getting package external refs: "+e.getMessage(),e);
			}
		}
		inProgress = false;
		super.addDocumentItem(spdxDocument, spdxPackage);
	}
	
	/**
	 * @param spdxDocument document containing the external refs
	 * @param externalRefCollection collection of external refs to compare
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	private void compareNewPackageExternalRefs(SpdxDocument spdxDocument,
			Collection<ExternalRef> externalRefCollection) throws InvalidSPDXAnalysisException {
		ExternalRef[] externalRefs = externalRefCollection.toArray(new ExternalRef[0]);
		Arrays.sort(externalRefs);
        Map<SpdxDocument, List<ExternalRef>> docUniqueExternalRefs = this.uniqueExternalRefs.computeIfAbsent(spdxDocument, k -> new HashMap<>());
        Map<SpdxDocument, List<SpdxExternalRefDifference>> docExternalRefDiffs = this.externalRefDifferences.computeIfAbsent(spdxDocument, k -> new HashMap<>());

        for (Entry<SpdxDocument, SpdxItem> entry : this.documentItem.entrySet()) {
            if (entry.getValue() instanceof SpdxPackage) {
                Collection<ExternalRef> compareExternalRefCollection = ((SpdxPackage) entry.getValue()).getExternalRefs();
                ExternalRef[] compareExternalRefs = compareExternalRefCollection.toArray(new ExternalRef[0]);
                Arrays.sort(compareExternalRefs);
                List<SpdxExternalRefDifference> externalRefDifferences = findExternalRefDifferences(
						externalRefs, compareExternalRefs);
                if (!externalRefDifferences.isEmpty()) {
                    this.externalRefsEquals = false;
                    this.differenceFound = true;
                }
                docExternalRefDiffs.put(entry.getKey(), externalRefDifferences);
                Map<SpdxDocument, List<SpdxExternalRefDifference>> compareExternalRefDiffs = this.externalRefDifferences.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
                compareExternalRefDiffs.put(spdxDocument, externalRefDifferences);

                List<ExternalRef> uniqueRefs = findUniqueExternalRefs(externalRefs, compareExternalRefs);
                if (!uniqueRefs.isEmpty()) {
                    this.externalRefsEquals = false;
                    this.differenceFound = true;
                }
                uniqueExternalRefs.put(entry.getKey(), docUniqueExternalRefs);
                Map<SpdxDocument, List<ExternalRef>> compareUniqueRefs = this.uniqueExternalRefs.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
                uniqueRefs = findUniqueExternalRefs(compareExternalRefs, externalRefs);
                if (!uniqueRefs.isEmpty()) {
                    this.externalRefsEquals = false;
                    this.differenceFound = true;
                }
                compareUniqueRefs.put(spdxDocument, uniqueRefs);
            }
        }
	}

	/**
	 * @param externalRefsA A external refs to compare
	 * @param externalRefsB B external refs to compare
	 * @return list of external refs found in A but not B
     */
	private List<ExternalRef> findUniqueExternalRefs(
			ExternalRef[] externalRefsA, ExternalRef[] externalRefsB) {
		int bIndex = 0;
		int aIndex = 0;
		List<ExternalRef> alRetval = new ArrayList<>();
		while (aIndex < externalRefsA.length) {
			if (bIndex >= externalRefsB.length) {
				alRetval.add(externalRefsA[aIndex]);
				aIndex++;
			} else {
				int compareVal = externalRefTypeNameComparator.compare(externalRefsA[aIndex], externalRefsB[bIndex]);
				if (compareVal == 0) {
					// external Refs are equal
					aIndex++;
					bIndex++;
				} else if (compareVal > 0) {
					// externalRefsA is greater than externalRefsB
					bIndex++;
				} else {
					// externalRefsB is greater tha externalRefsA
					alRetval.add(externalRefsA[aIndex]);
					aIndex++;
				}
			}
		}
		return alRetval;
	}

	/**
	 * @param externalRefsA A external refs to compare
	 * @param externalRefsB B external refs to compare
	 * @return list of differences between external refs
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private List<SpdxExternalRefDifference> findExternalRefDifferences(
			ExternalRef[] externalRefsA, ExternalRef[] externalRefsB) throws InvalidSPDXAnalysisException {
		List<SpdxExternalRefDifference> retval = new ArrayList<>();
		int aIndex = 0;
		int bIndex = 0;
		Arrays.sort(externalRefsA, externalRefTypeNameComparator );
		Arrays.sort(externalRefsB, externalRefTypeNameComparator);
		while (aIndex < externalRefsA.length && bIndex < externalRefsB.length) {
			int compare = externalRefTypeNameComparator.compare(externalRefsA[aIndex], externalRefsB[bIndex]);
			if (compare == 0) {
				if (!Objects.equals(externalRefsA[aIndex].getComment(), externalRefsB[bIndex].getComment()) ||
						!Objects.equals(externalRefsA[aIndex].getReferenceCategory(), externalRefsB[bIndex].getReferenceCategory())) {
					retval.add(new SpdxExternalRefDifference(externalRefsA[aIndex], externalRefsB[bIndex]));
				}
				aIndex++;
				bIndex++;
			} else if (compare > 0) {
				// externalRefsA is greater than externalRefsB
				bIndex++;
			} else {
				// externalRefsB is greater than externalRefsA
				aIndex++;
			}
		}
		return retval;
	}

	/**
	 * @param spdxDocument document containing the files
	 * @param filesCollection files to compare
	 * @throws SpdxCompareException on compare error
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private void compareNewPackageFiles(SpdxDocument spdxDocument,
			Collection<SpdxFile> filesCollection) throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxFile[] files = filesCollection.toArray(new SpdxFile[0]);
		Arrays.sort(files);
        Map<SpdxDocument, List<SpdxFile>> docUniqueFiles = this.uniqueFiles.computeIfAbsent(spdxDocument, k -> new HashMap<>());
        Map<SpdxDocument, List<SpdxFileDifference>> docDifferentFiles = this.fileDifferences.computeIfAbsent(spdxDocument, k -> new HashMap<>());
        for (Entry<SpdxDocument, SpdxItem> entry : this.documentItem.entrySet()) {
            if (entry.getValue() instanceof SpdxPackage) {
                Collection<SpdxFile> compareFilesCollection = ((SpdxPackage) entry.getValue()).getFiles();
                SpdxFile[] compareFiles = compareFilesCollection.toArray(new SpdxFile[0]);
                Arrays.sort(compareFiles);
                List<SpdxFileDifference> fileDifferences =
                        SpdxComparer.findFileDifferences(spdxDocument, entry.getKey(), files, compareFiles, this.extractedLicenseIdMap);
                if (!fileDifferences.isEmpty()) {
                    this.packageFilesEquals = false;
                    this.differenceFound = true;
                }
                docDifferentFiles.put(entry.getKey(), fileDifferences);
                Map<SpdxDocument, List<SpdxFileDifference>> compareDifferentFiles = this.fileDifferences.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
                compareDifferentFiles.put(spdxDocument, fileDifferences);
                List<SpdxFile> uniqueFiles = SpdxComparer.findUniqueFiles(files, compareFiles);
                if (!uniqueFiles.isEmpty()) {
                    this.packageFilesEquals = false;
                    this.differenceFound = true;
                }
                docUniqueFiles.put(entry.getKey(), uniqueFiles);
                Map<SpdxDocument, List<SpdxFile>> compareUniqueFiles = this.uniqueFiles.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
                uniqueFiles = SpdxComparer.findUniqueFiles(compareFiles, files);
                if (!uniqueFiles.isEmpty()) {
                    this.packageFilesEquals = false;
                    this.differenceFound = true;
                }
                compareUniqueFiles.put(spdxDocument, uniqueFiles);
            }
        }
	}

	/**
	 * Compare the checks for a new package being added to the existing
	 * package checksums filling in the unique checksums map
	 * @param spdxDocument document containing the checksums
	 * @param checksums checksums to compare
	 * @throws SpdxCompareException on compare errors
	 */
	private void compareNewPackageChecksums(SpdxDocument spdxDocument,
			Collection<Checksum> checksums) throws SpdxCompareException {
		try {
			Map<SpdxDocument, List<Checksum>> docUniqueChecksums = new HashMap<>();
			this.uniqueChecksums.put(spdxDocument, docUniqueChecksums);
            for (Entry<SpdxDocument, SpdxItem> entry : this.documentItem.entrySet()) {
                if (entry.getValue() instanceof SpdxPackage) {
                    Collection<Checksum> compareChecksums = ((SpdxPackage) entry.getValue()).getChecksums();
                    List<Checksum> uniqueChecksums = SpdxComparer.findUniqueChecksums(checksums, compareChecksums);
                    if (!uniqueChecksums.isEmpty()) {
                        this.packageChecksumsEquals = false;
                        this.differenceFound = true;
                    }
                    docUniqueChecksums.put(entry.getKey(), uniqueChecksums);
                    Map<SpdxDocument, List<Checksum>> compareUniqueChecksums = this.uniqueChecksums.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
                    uniqueChecksums = SpdxComparer.findUniqueChecksums(compareChecksums, checksums);
                    if (!uniqueChecksums.isEmpty()) {
                        this.packageChecksumsEquals = false;
                        this.differenceFound = true;
                    }
                    compareUniqueChecksums.put(spdxDocument, uniqueChecksums);
                }
            }
		} catch (InvalidSPDXAnalysisException e) {
			throw new SpdxCompareException("SPDX error getting package checksums: "+e.getMessage(),e);
		}
	}

	/**
	 * @return the inProgress
	 */
	@Override
    public boolean isInProgress() {
		return inProgress;
	}

	/**
	 * @return the differenceFound
	 * @throws SpdxCompareException on compare errors
	 */
	@Override
    public boolean isDifferenceFound() throws SpdxCompareException {
		checkInProgress();
		return differenceFound || super.isDifferenceFound();
	} 

	/**
	 * checks to make sure there is not a compare in progress
	 * @throws SpdxCompareException on compare errors
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
	 * @return the packageVersionsEquals
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isPackageVersionsEquals() throws SpdxCompareException {
		checkInProgress();
		return packageVersionsEquals;
	}

	/**
	 * @return the packageFilenamesEquals
	 */
	public boolean isPackageFilenamesEquals() throws SpdxCompareException {
		checkInProgress();
		return packageFilenamesEquals;
	}

	/**
	 * @return the packageSuppliersEquals
	 */
	public boolean isPackageSuppliersEquals() throws SpdxCompareException {
		checkInProgress();
		return packageSuppliersEquals;
	}

	/**
	 * @return the packageDownloadLocationsEquals
	 */
	public boolean isPackageDownloadLocationsEquals() throws SpdxCompareException {
		checkInProgress();
		return packageDownloadLocationsEquals;
	}

	/**
	 * @return the packageVerificationCodesEquals
	 */
	public boolean isPackageVerificationCodesEquals() throws SpdxCompareException {
		checkInProgress();
		return packageVerificationCodesEquals;
	}
	
	

	/**
	 * @return the filesAnalyzedEquals
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isFilesAnalyzedEquals() throws SpdxCompareException {
		checkInProgress();
		return filesAnalyzedEquals;
	}

	/**
	 * @return the packageChecksumsEquals
	 */
	public boolean isPackageChecksumsEquals() throws SpdxCompareException {
		checkInProgress();
		return packageChecksumsEquals;
	}

	/**
	 * @return the packageSourceInfosEquals
	 */
	public boolean isPackageSourceInfosEquals() throws SpdxCompareException {
		checkInProgress();
		return packageSourceInfosEquals;
	}

	/**
	 * @return the declaredLicensesEquals
	 */
	public boolean isDeclaredLicensesEquals() throws SpdxCompareException {
		checkInProgress();
		return declaredLicensesEquals;
	}

	/**
	 * @return the packageSummaryEquals
	 */
	public boolean isPackageSummaryEquals() throws SpdxCompareException {
		checkInProgress();
		return packageSummaryEquals;
	}

	/**
	 * @return the packageDescriptionsEquals
	 */
	public boolean isPackageDescriptionsEquals() throws SpdxCompareException {
		checkInProgress();
		return packageDescriptionsEquals;
	}

	/**
	 * @return the packageOriginatorsEqual
	 */
	public boolean isPackageOriginatorsEqual() throws SpdxCompareException {
		checkInProgress();
		return packageOriginatorsEqual;
	}

	/**
	 * @return the packageHomePagesEquals
	 */
	public boolean isPackageHomePagesEquals() throws SpdxCompareException {
		checkInProgress();
		return packageHomePagesEquals;
	}
	

	/**
	 * @return the externalRefsEquals
	 * @throws SpdxCompareException on compare errors
	 */
	public boolean isExternalRefsEquals() throws SpdxCompareException {
		checkInProgress();
		return externalRefsEquals;
	}

	/**
	 * Return the package associated with the document
	 * @param document document
	 * @return The package associated with the document
	 */
	public SpdxPackage getDocPackage(SpdxDocument document) {
		SpdxItem retItem = this.documentItem.get(document);
		if (retItem instanceof SpdxPackage) {
			return (SpdxPackage)retItem;
		} else {
			return null;
		}
	}

	/**
	 * Get the checksums which are present in document A but not in document B
	 * @return the uniqueChecksums
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
	
	public List<ExternalRef> getUniqueExternalRefs(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		Map<SpdxDocument, List<ExternalRef>> uniqueMap = this.uniqueExternalRefs.get(docA);
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<ExternalRef> retval = uniqueMap.get(docB);
		if (retval == null) {
			return new ArrayList<>();
		} else {
			return retval;
		}
	}
	
	public List<SpdxExternalRefDifference> getExternalRefDifferences(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		Map<SpdxDocument, List<SpdxExternalRefDifference>> externalRefDiffMap = this.externalRefDifferences.get(docA);
		if (externalRefDiffMap == null) {
			return new ArrayList<>();
		}
		List<SpdxExternalRefDifference> retval = externalRefDiffMap.get(docB);
		if (retval == null) {
			return new ArrayList<>();
		} else {
			return retval;
		}
	}

	/**
	 * @return the packageFilesEquals
	 */
	public boolean isPackageFilesEquals() throws SpdxCompareException {
		checkInProgress();
		return packageFilesEquals;
	}

	/**
	 * Get any fileDifferences which are in docA but not in docB
	 * @param docA A document to compare
	 * @param docB B document compare
	 * @return any fileDifferences which are in docA but not in docB
	 */
	public List<SpdxFileDifference> getFileDifferences(SpdxDocument docA, 
			SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		Map<SpdxDocument, List<SpdxFileDifference>> uniqueMap = this.fileDifferences.get(docA);
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<SpdxFileDifference> retval = uniqueMap.get(docB);
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}


	/**
	 * Return any unique files by name which are in docA but not in docB
	 * @param docA A document to compare
	 * @param docB B document compare
	 * @return any unique files by name which are in docA but not in docB
	 */
	public List<SpdxFile> getUniqueFiles(SpdxDocument docA, SpdxDocument docB) throws SpdxCompareException {
		checkInProgress();
		Map<SpdxDocument, List<SpdxFile>> uniqueMap = this.uniqueFiles.get(docA);
		if (uniqueMap == null) {
			return new ArrayList<>();
		}
		List<SpdxFile> retval = uniqueMap.get(docB);
		if (retval == null) {
			return new ArrayList<>();
		}
		return retval;
	}

	/**
	 * @return package name
	 */
	public String getPackageName() throws SpdxCompareException {
		checkInProgress();
		return this.name;
	}

	/**
	 * @return number of package
	 * @throws SpdxCompareException on compare errors
	 */
	public int getNumPackages() throws SpdxCompareException {
		checkInProgress();
		return this.documentItem.size();
	}
	
}