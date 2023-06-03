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

import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.compat.v2.Annotation;
import org.spdx.library.model.compat.v2.Relationship;
import org.spdx.library.model.compat.v2.SpdxItem;
import org.spdx.library.model.compat.v2.license.AnyLicenseInfo;

/**
 *  Contains the results of a comparison between two SPDX items with the same name
 * @author Gary O'Neall
 *
 */
public class SpdxItemDifference {
	
	private String name;
	private String commentA;
	private String commentB;
	private String concludedLicenseA;
	private String concludedLicenseB;
	private boolean concludedLicenseEquals;
	private String copyrightA;
	private String copyrightB;
	private String licenseCommentsA;
	private String licenseCommentsB;
	private boolean seenLicensesEqual;
	private List<AnyLicenseInfo> uniqueSeenLicensesA;
	private List<AnyLicenseInfo> uniqueSeenLicensesB;
	private boolean relationshipsEquals;
	private List<Relationship> uniqueRelationshipA;
	private List<Relationship> uniqueRelationshipB;
	private boolean annotationsEquals;
	private List<Annotation> uniqueAnnotationsA;
	private List<Annotation> uniqueAnnotationsB;
	
	public SpdxItemDifference(SpdxItem itemA, SpdxItem itemB, 
			boolean concludedLicensesEqual, boolean seenLicensesEqual,
			List<AnyLicenseInfo> uniqueSeenLicensesA,
			List<AnyLicenseInfo> uniqueSeenLicensesB,
			boolean relationshipsEquals,
			List<Relationship> uniqueRelationshipA,
			List<Relationship> uniqueRelationshipB,
			boolean annotationsEquals,
			List<Annotation> uniqueAnnotationsA,
			List<Annotation> uniqueAnnotationsB
			) throws SpdxCompareException, InvalidSPDXAnalysisException {
	    Optional<String> oNameA = itemA.getName();
		if (oNameA.isPresent()) {
			this.name = oNameA.get();
		} else {
			this.name = "";
		}
		Optional<String> oCommentA = itemA.getComment();
		if (oCommentA.isPresent()) {
			this.commentA = oCommentA.get();
		} else {
			this.commentA = "";
		}
		Optional<String> oCommentB = itemB.getComment();
		if (oCommentB.isPresent()) {
			this.commentB = oCommentB.get();
		} else {
			this.commentB = "";
		}
		this.concludedLicenseA = itemA.getLicenseConcluded().toString();
		this.concludedLicenseB = itemB.getLicenseConcluded().toString();
		this.concludedLicenseEquals = concludedLicensesEqual;
		this.copyrightA = itemA.getCopyrightText();
		if (this.copyrightA == null) {
			this.copyrightA = "";
		}
		this.copyrightB = itemB.getCopyrightText();
		if (this.copyrightB == null) {
			this.copyrightB = "";
		}
		Optional<String> oLicenseCommentsA = itemA.getLicenseComments();
		if ( oLicenseCommentsA.isPresent()) {
			this.licenseCommentsA = oLicenseCommentsA.get();
		} else {
			this.licenseCommentsA = "";
		}
		Optional<String> oLicenseCommentsB = itemB.getLicenseComments();
		if (oLicenseCommentsB.isPresent()) {
			this.licenseCommentsB = oLicenseCommentsB.get();
		} else {
			this.licenseCommentsB = "";
		}
		this.seenLicensesEqual = seenLicensesEqual;
		this.uniqueSeenLicensesA = uniqueSeenLicensesA;
		this.uniqueSeenLicensesB = uniqueSeenLicensesB;
		this.relationshipsEquals = relationshipsEquals;
		this.uniqueRelationshipA = uniqueRelationshipA;
		this.uniqueRelationshipB = uniqueRelationshipB;
		this.annotationsEquals = annotationsEquals;
		this.uniqueAnnotationsA = uniqueAnnotationsA;
		this.uniqueAnnotationsB = uniqueAnnotationsB;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the commentA
	 */
	public String getCommentA() {
		return commentA;
	}

	/**
	 * @return the commentB
	 */
	public String getCommentB() {
		return commentB;
	}

	/**
	 * @return the concludedLicenseA
	 */
	public String getConcludedLicenseA() {
		return concludedLicenseA;
	}

	/**
	 * @return the concludedLicenseB
	 */
	public String getConcludedLicenseB() {
		return concludedLicenseB;
	}

	/**
	 * @return the concludedLicenseEquals
	 */
	public boolean isConcludedLicenseEquals() {
		return concludedLicenseEquals;
	}
	/**
	 * @return the copyrightA
	 */
	public String getCopyrightA() {
		return copyrightA;
	}

	/**
	 * @return the copyrightB
	 */
	public String getCopyrightB() {
		return copyrightB;
	}

	/**
	 * @return the licenseCommentsA
	 */
	public String getLicenseCommentsA() {
		return licenseCommentsA;
	}

	/**
	 * @return the licenseCommentsB
	 */
	public String getLicenseCommentsB() {
		return licenseCommentsB;
	}

	/**
	 * @return the seenLicensesEqual
	 */
	public boolean isSeenLicensesEquals() {
		return seenLicensesEqual;
	}

	/**
	 * @return the uniqueSeenLicensesA
	 */
	public List<AnyLicenseInfo> getUniqueSeenLicensesA() {
		return uniqueSeenLicensesA;
	}

	/**
	 * @return the uniqueSeenLicensesB
	 */
	public List<AnyLicenseInfo> getUniqueSeenLicensesB() {
		return uniqueSeenLicensesB;
	}
	
	public boolean isCommentsEquals() {
		return SpdxComparer.stringsEqual(commentA, commentB);
	}
	
	public boolean isCopyrightsEqual() {
		return SpdxComparer.stringsEqual(copyrightA, copyrightB);
	}
	
	public boolean isLicenseCommentsEqual() {
		return SpdxComparer.stringsEqual(licenseCommentsA, licenseCommentsB);
	}

	/**
	 * @return the relationshipsEquals
	 */
	public boolean isRelationshipsEquals() {
		return relationshipsEquals;
	}

	/**
	 * @return the uniqueRelationshipA
	 */
	public List<Relationship> getUniqueRelationshipA() {
		return uniqueRelationshipA;
	}

	/**
	 * @return the uniqueRelationshipB
	 */
	public List<Relationship> getUniqueRelationshipB() {
		return uniqueRelationshipB;
	}

	/**
	 * @return the annotationsEquals
	 */
	public boolean isAnnotationsEquals() {
		return annotationsEquals;
	}

	/**
	 * @return the uniqueAnnotationsA
	 */
	public List<Annotation> getUniqueAnnotationsA() {
		return uniqueAnnotationsA;
	}

	/**
	 * @return the uniqueAnnotationsB
	 */
	public List<Annotation> getUniqueAnnotationsB() {
		return uniqueAnnotationsB;
	}
	
}