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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.Annotation;
import org.spdx.library.model.v2.Checksum;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxFile;
import org.spdx.library.model.v2.enumerations.FileType;
import org.spdx.library.model.v2.license.AnyLicenseInfo;


/**
 * Contains the results of a comparison between two SPDX files with the same name
 * @author Gary O'Neall
 *
 */
public class SpdxFileDifference extends SpdxItemDifference {

	private final List<FileType> fileTypeA;
	private final List<FileType> fileTypeB;
	private final List<String> contributorsA;
	private final String noticeA;
	private final List<String> contributorsB;
	private final String noticeB;
	private final List<String> dependantFileNamesA;
	private final List<String> dependantFileNamesB;
	private final boolean checksumsEquals;
	private final List<Checksum> uniqueChecksumsA;
	private final List<Checksum> uniqueChecksumsB;
	private final String spdxIdA;
	private final String spdxIdB;

	@SuppressWarnings("deprecation")
    public SpdxFileDifference(SpdxFile fileA, SpdxFile fileB, 
			boolean concludedLicensesEqual, boolean seenLicensesEqual,
			List<AnyLicenseInfo> uniqueSeenLicensesA,
			List<AnyLicenseInfo> uniqueSeenLicensesB,
			boolean checksumsEquals,
			List<Checksum> uniqueChecksumsA,
			List<Checksum> uniqueChecksumsB,
			boolean relationshipsEquals,
			List<Relationship> uniqueRelationshipA,
			List<Relationship> uniqueRelationshipB,
			boolean annotationsEquals,
			List<Annotation> uniqueAnnotationsA,
			List<Annotation> uniqueAnnotationsB
			) throws InvalidSPDXAnalysisException, SpdxCompareException {
		super(fileA, fileB, concludedLicensesEqual, seenLicensesEqual,
				uniqueSeenLicensesA, uniqueSeenLicensesB, 
				relationshipsEquals, uniqueRelationshipA,  uniqueRelationshipB,
				annotationsEquals, uniqueAnnotationsA,uniqueAnnotationsB);
		this.fileTypeA = Arrays.asList(fileA.getFileTypes().toArray(new FileType[0]));
		this.fileTypeB = Arrays.asList(fileB.getFileTypes().toArray(new FileType[0]));
		this.contributorsA = Arrays.asList(fileA.getFileContributors().toArray(new String[0]));
		this.contributorsB = Arrays.asList(fileB.getFileContributors().toArray(new String[0]));
		this.dependantFileNamesA = new ArrayList<>();
		for (SpdxFile dependantFile:fileA.getFileDependency()) {
		    Optional<String> dependantFileName = dependantFile.getName();
            dependantFileName.ifPresent(dependantFileNamesA::add);
		}
        this.dependantFileNamesB = new ArrayList<>();
        for (SpdxFile dependantFile:fileB.getFileDependency()) {
            Optional<String> dependantFileName = dependantFile.getName();
            dependantFileName.ifPresent(dependantFileNamesB::add);
        }
        Optional<String> noticeTextA =fileA.getNoticeText();
        this.noticeA = noticeTextA.orElse("");
		Optional<String> noticeTextB =fileB.getNoticeText();
        this.noticeB = noticeTextB.orElse("");
		this.checksumsEquals = checksumsEquals;
		this.uniqueChecksumsA = uniqueChecksumsA;
		this.uniqueChecksumsB = uniqueChecksumsB;
		this.spdxIdA = fileA.getId();
		this.spdxIdB = fileB.getId();
	}
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.getName();
	}
	/**
	 * @return the fileTypeA
	 */
	public List<FileType> getFileTypeA() {
		return fileTypeA;
	}

	/**
	 * @return the fileTypeB
	 */
	public List<FileType> getFileTypeB() {
		return fileTypeB;
	}
	
	public boolean isContributorsEqual() {
		return SpdxComparer.stringListsEqual(this.contributorsA, this.contributorsB);
	}
	
	public boolean isNoticeTextsEqual() {
		return SpdxComparer.stringsEqual(this.noticeA, this.noticeB);
	}
	
	public boolean isFileDependenciesEqual() {
		return SpdxComparer.stringListsEqual(this.dependantFileNamesA, this.dependantFileNamesB);
	}

	/**
	 * @return true if the types are equal
	 */
	public boolean isTypeEqual() {
		return SpdxComparer.listsEquals(fileTypeA, fileTypeB);
	}

	/**
	 * @return true if the checksums are equal
	 */
	public boolean isChecksumsEquals() {
		return this.checksumsEquals;
	}

	/**
	 * @return string form af all A contributors
	 */
	public String getContributorsAAsString() {
		return stringListToString(this.contributorsA);
	}
	
	/**
	 * @return string form of all B contributors
	 */
	public String getContributorsBAsString() {
		return stringListToString(this.contributorsB);
	}
	
	
	
	static String stringListToString(List<String> s) {
		StringBuilder sb = new StringBuilder();
		if ((s != null) && (!s.isEmpty())) {
			sb.append(s.get(0));
			for (int i = 1; i < s.size(); i++) {
				if (Objects.nonNull(s.get(i)) && !s.get(i).isEmpty()) {
					sb.append(", ");
					sb.append(s.get(i));
				}
			}
		}
		return sb.toString();
	}

	/**
	 * @return string form of all A file dependencies
	 */
	public String getFileDependenciesAAsString() {
		return stringListToString(this.dependantFileNamesA);
	}
	
	/**
	 * @return string form of all B file dependencies
	 */
	public String getFileDependenciesBAsString() {
		return stringListToString(this.dependantFileNamesB);
	}



	/**
	 * @return the contributorsA
	 */
	public List<String> getContributorsA() {
		return contributorsA;
	}



	/**
	 * @return the noticeA
	 */
	public String getNoticeA() {
		return noticeA;
	}



	/**
	 * @return the contributorsB
	 */
	public List<String> getContributorsB() {
		return contributorsB;
	}



	/**
	 * @return the noticeB
	 */
	public String getNoticeB() {
		return noticeB;
	}



	/**
	 * @return the dependantFileNamesA
	 */
	public List<String> getDependantFileNamesA() {
		return dependantFileNamesA;
	}



	/**
	 * @return the dependantFileNamesB
	 */
	public List<String> getDependantFileNamesB() {
		return dependantFileNamesB;
	}

	/**
	 * @return the uniqueChecksumsA
	 */
	public List<Checksum> getUniqueChecksumsA() {
		return uniqueChecksumsA;
	}



	/**
	 * @return the uniqueChecksumsB
	 */
	public List<Checksum> getUniqueChecksumsB() {
		return uniqueChecksumsB;
	}

	/**
	 * @return SPDX ID for A
	 */
	public String getSpdxIdA() {
		return this.spdxIdA;
	}
	
	/**
	 * @return SPDX ID for B
	 */
	public String getSpdxIdB() {
		return this.spdxIdB;
	}
	
}
