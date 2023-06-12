/**
 * Copyright (c) 2019 Source Auditor Inc.
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
package org.spdx.library.model.compat.v2;

import org.spdx.library.IndividualUriValue;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SimpleUriValue;

/**
 * Type of external reference
 * Note that there are very few required fields for this class in that
 * the external reference type does not need to be provided in the SPDX
 * document for the document to be valid.
 * 
 * @author Gary O'Neall
 */
public class ReferenceType extends SimpleUriValue implements Comparable<ReferenceType> {
	
	public static final String MISSING_REFERENCE_TYPE_URI = "http://spdx.org/rdf/refeferences/MISSING";

	public static ReferenceType getMissingReferenceType() throws InvalidSPDXAnalysisException {
		return new ReferenceType(MISSING_REFERENCE_TYPE_URI);
	}
	
	public ReferenceType(IndividualUriValue uri) throws InvalidSPDXAnalysisException {
		super(uri);
	}
	
	public ReferenceType(String uriValue) throws InvalidSPDXAnalysisException {
		super(uriValue);
	}

	@Override
	public int compareTo(ReferenceType compare) {
		return this.getIndividualURI().compareTo(compare.getIndividualURI());
	}


}
