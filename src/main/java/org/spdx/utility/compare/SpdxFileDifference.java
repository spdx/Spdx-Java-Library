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
import java.util.List;
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.Relationship;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.enumerations.FileType;
import org.spdx.library.model.license.AnyLicenseInfo;

/**
 * Contains the results of a comparison between two SPDX files with the same name
 * @author Gary O'Neall
 *
 */
public class SpdxFileDifference extends SpdxItemDifference {

	private List<FileType> fileTypeA;
	private List<FileType> fileTypeB;
	private List<String> contributorsA;
	private String noticeA;
	private List<String> contributorsB;
	private String noticeB;
	private List<String> dependantFileNamesA;
	private List<String> dependantFileNamesB;
	private boolean checksumsEquals;
	private List<Checksum> uniqueChecksumsA;
	private List<Checksum> uniqueChecksumsB;
	private String spdxIdA;
	private String spdxIdB;

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
		this.fileTypeA = Arrays.asList(fileA.getFileTypes().toArray(new FileType[fileA.getFileTypes().size()]));
		this.fileTypeB = Arrays.asList(fileB.getFileTypes().toArray(new FileType[fileB.getFileTypes().size()]));	
		this.contributorsA = Arrays.asList(fileA.getFileContributors().toArray(new String[fileA.getFileContributors().size()]));
		this.contributorsB = Arrays.asList(fileB.getFileContributors().toArray(new String[fileB.getFileContributors().size()]));
		this.dependantFileNamesA = new ArrayList<>();
		for (SpdxFile dependantFile:fileA.getFileDependency()) {
		    if (dependantFile.getName().isPresent()) {
		        dependantFileNamesA.add(dependantFile.getName().get());
		    }
		}
        this.dependantFileNamesB = new ArrayList<>();
        for (SpdxFile dependantFile:fileB.getFileDependency()) {
            if (dependantFile.getName().isPresent()) {
                dependantFileNamesB.add(dependantFile.getName().get());
            }
        }
		if (fileA.getNoticeText().isPresent()) {
			this.noticeA = fileA.getNoticeText().get();
		} else {
			this.noticeA = "";
		}
		if (fileB.getNoticeText().isPresent()) {
			this.noticeB = fileB.getNoticeText().get();
		} else {
			this.noticeB = "";
		}
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
	 * @return
	 */
	public boolean isTypeEqual() {
		return SpdxComparer.listsEquals(fileTypeA, fileTypeB);
	}

	/**
	 * @return
	 */
	public boolean isChecksumsEquals() {
		return this.checksumsEquals;
	}

	/**
	 * @return
	 */
	public String getContributorsAAsString() {
		return stringListToString(this.contributorsA);
	}
	
	/**
	 * @return
	 */
	public String getContributorsBAsString() {
		return stringListToString(this.contributorsB);
	}
	
	
	
	static String stringListToString(List<String> s) {
		StringBuilder sb = new StringBuilder();
		if (s != null && s.size() > 0) {
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
	 * @return
	 */
	public String getFileDependenciesAAsString() {
		return stringListToString(this.dependantFileNamesA);
	}
	
	/**
	 * @return
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
	 * @return
	 */
	public String getSpdxIdA() {
		return this.spdxIdA;
	}
	
	/**
	 * @return
	 */
	public String getSpdxIdB() {
		return this.spdxIdB;
	}
	
}
