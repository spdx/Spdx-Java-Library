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

import java.util.Objects;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.compat.v2.ExternalRef;
import org.spdx.library.model.compat.v2.ReferenceType;
import org.spdx.library.model.enumerations.ReferenceCategory;

/**
 * Contains information on differences between two different External Refs.
 * 
 * @author Gary O'Neall
 *
 */
public class SpdxExternalRefDifference {
	
	String commentA;
	String commentB;
	ReferenceCategory catA;
	ReferenceCategory catB;
	private String referenceLocator;
	private ReferenceType referenceType;
	
	SpdxExternalRefDifference(ExternalRef externalRefA, ExternalRef externalRefB) throws InvalidSPDXAnalysisException {
		Optional<String> oCommentA = externalRefA.getComment();
	    if (oCommentA.isPresent()) {
			this.commentA = oCommentA.get();
		} else {
			this.commentA = "";
		}
	    Optional<String> oCommentB = externalRefB.getComment();
		if (oCommentB.isPresent()) {
			this.commentB = oCommentB.get();
		} else {
			this.commentB = "";
		}
		catA = externalRefA.getReferenceCategory();
		catB = externalRefB.getReferenceCategory();
		this.referenceLocator = externalRefA.getReferenceLocator();
		this.referenceType = externalRefA.getReferenceType();
	}
	public boolean isCommentsEqual() {
		return SpdxComparer.stringsEqual(this.commentA, this.commentB);
	}
	
	public boolean isReferenceCategoriesEqual() {
		return Objects.equals(catA, catB);
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
	 * @return the catA
	 */
	public ReferenceCategory getCatA() {
		return catA;
	}
	/**
	 * @return the catB
	 */
	public ReferenceCategory getCatB() {
		return catB;
	}
	/**
	 * @return the referenceLocator
	 */
	public String getReferenceLocator() {
		return referenceLocator;
	}
	/**
	 * @return the referenceType
	 */
	public ReferenceType getReferenceType() {
		return referenceType;
	}
}