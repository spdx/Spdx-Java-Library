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

import java.util.Collection;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;

/**
 * Contains the results of a comparison between two SPDX non-standard licenses
 * where the license text is equivalent and the license comment, license ID, or
 * other fields are different
 * @author Gary O'Neall
 *
 */
public class SpdxLicenseDifference {

	private final String licenseText;
	private final String licenseNameA;
	/**
	 * @return the licenseText
	 */
	public String getLicenseText() {
		return licenseText;
	}

	/**
	 * @return the licenseNameA
	 */
	public String getLicenseNameA() {
		return licenseNameA;
	}

	/**
	 * @return the licenseNameB
	 */
	public String getLicenseNameB() {
		return licenseNameB;
	}

	/**
	 * @return the licenseNamesEqual
	 */
	public boolean isLicenseNamesEqual() {
		return licenseNamesEqual;
	}

	/**
	 * @return the idA
	 */
	public String getIdA() {
		return IdA;
	}

	/**
	 * @return the idB
	 */
	public String getIdB() {
		return IdB;
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
	 * @return the commentsEqual
	 */
	public boolean isCommentsEqual() {
		return commentsEqual;
	}

	/**
	 * @return the sourceUrlsA
	 */
	public Collection<String> getSourceUrlsA() {
		return sourceUrlsA;
	}

	/**
	 * @return the sourceUrlsB
	 */
	public Collection<String> getSourceUrlsB() {
		return sourceUrlsB;
	}

	/**
	 * @return the sourceUrlsEqual
	 */
	public boolean isSourceUrlsEqual() {
		return sourceUrlsEqual;
	}

	private final String licenseNameB;
	private final boolean licenseNamesEqual;
	private final String IdA;
	private final String IdB;
	private final String commentA;
	private final String commentB;
	private final boolean commentsEqual;
	private final Collection<String> sourceUrlsA;
	private final Collection<String> sourceUrlsB;
	private final boolean sourceUrlsEqual;

	/**
	 * @param licenseA A license to compare
	 * @param licenseB B license to compare
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public SpdxLicenseDifference(
			ExtractedLicenseInfo licenseA,
			ExtractedLicenseInfo licenseB) throws InvalidSPDXAnalysisException {
		this.licenseText = licenseA.getExtractedText();
		this.licenseNameA = licenseA.getName();
		this.licenseNameB = licenseB.getName();
		this.licenseNamesEqual = SpdxComparer.stringsEqual(licenseNameA, licenseNameB);
		this.IdA = licenseA.getLicenseId();
		this.IdB = licenseB.getLicenseId();
		this.commentA = licenseA.getComment();
		this.commentB = licenseB.getComment();
		this.commentsEqual = SpdxComparer.stringsEqual(commentA, commentB);
		this.sourceUrlsA = licenseA.getSeeAlso();
		this.sourceUrlsB = licenseB.getSeeAlso();
		this.sourceUrlsEqual = SpdxComparer.stringCollectionsEqual(sourceUrlsA, sourceUrlsB);			
	}
	
}