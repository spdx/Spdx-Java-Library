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
package org.spdx.library.referencetype;


import java.net.URI;
import java.net.URISyntaxException;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ListedReferenceTypesTest extends TestCase {
	
	static final String[] LISTED_REFERENCE_TYPE_NAMES = new String[] {
			"cpe22Type","cpe23Type","maven-central","npm","nuget","bower","debian"
		};

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		ListedReferenceTypes.resetListedReferenceTypes();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.referencetype.ListedReferenceTypes#isListedReferenceType(java.net.URI)}.
	 */
	public void testIsListedReferenceType() throws URISyntaxException {
		for (String refName:LISTED_REFERENCE_TYPE_NAMES) {
			URI uri = new URI(SpdxConstants.SPDX_LISTED_REFERENCE_TYPES_PREFIX + refName);
			assertTrue(ListedReferenceTypes.getListedReferenceTypes().isListedReferenceType(uri));
		}
		URI wrongNamespace = new URI("http://wrong/"+LISTED_REFERENCE_TYPE_NAMES[0]);
		assertFalse(ListedReferenceTypes.getListedReferenceTypes().isListedReferenceType(wrongNamespace));
		URI notValidName = new URI(SpdxConstants.SPDX_LISTED_REFERENCE_TYPES_PREFIX+"wrong");
		assertFalse(ListedReferenceTypes.getListedReferenceTypes().isListedReferenceType(notValidName));
	}

	/**
	 * Test method for {@link org.spdx.library.referencetype.ListedReferenceTypes#getListedReferenceUri(java.lang.String)}.
	 */
	public void testGetListedReferenceUri() throws InvalidSPDXAnalysisException {
		URI result = ListedReferenceTypes.getListedReferenceTypes().getListedReferenceUri(LISTED_REFERENCE_TYPE_NAMES[0]);
		assertEquals(SpdxConstants.SPDX_LISTED_REFERENCE_TYPES_PREFIX + LISTED_REFERENCE_TYPE_NAMES[0], result.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.referencetype.ListedReferenceTypes#getListedReferenceName(java.net.URI)}.
	 */
	public void testGetListedReferenceName() throws URISyntaxException, InvalidSPDXAnalysisException {
		for (String refName:LISTED_REFERENCE_TYPE_NAMES) {
			URI uri = new URI(SpdxConstants.SPDX_LISTED_REFERENCE_TYPES_PREFIX + refName);
			assertEquals(refName, ListedReferenceTypes.getListedReferenceTypes().getListedReferenceName(uri));
		}
	}

}
