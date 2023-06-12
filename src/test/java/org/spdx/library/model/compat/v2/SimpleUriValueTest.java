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

import org.spdx.library.DefaultModelStore;
import org.spdx.library.IndividualUriValue;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SimpleUriValue;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SimpleUriValueTest extends TestCase {
	
	Enum<?> TEST_ENUM = RelationshipType.DESCRIBES;
	
	static final String ENUM_URI = RelationshipType.DESCRIBES.getIndividualURI();
	static final String EXTERNAL_DOC_NAMSPACE = "https://test/namespace1";
	static final String EXTERNAL_SPDX_ELEMENT_ID = SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM + "TEST";
	static final String EXTERNAL_SPDX_URI = EXTERNAL_DOC_NAMSPACE + "#" + EXTERNAL_SPDX_ELEMENT_ID;
	static final String NON_INTERESTING_URI = "https://nothing/to/see/here";

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset(SpdxMajorVersion.VERSION_2);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.compat.v2.SimpleUriValue#SimpleUriValue(org.spdx.library.compat.v2.IndividualUriValue)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSimpleUriValueIndividualValue() throws InvalidSPDXAnalysisException {
		SimpleUriValue value1 = new SimpleUriValue(NON_INTERESTING_URI);
		SimpleUriValue value2 = new SimpleUriValue(new IndividualUriValue() {

			@Override
			public String getIndividualURI() {
				return NON_INTERESTING_URI;
			}
			
		});
		assertEquals(NON_INTERESTING_URI, value1.getIndividualURI());
		assertEquals(NON_INTERESTING_URI, value2.getIndividualURI());
		assertEquals(value1, value2);
	}

	/**
	 * Test method for {@link org.spdx.library.compat.v2.SimpleUriValue#toModelObject(org.spdx.storage.IModelStore, java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testToModelObject() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject();
		new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getCopyManager(), true);
		Object result = new SimpleUriValue(EXTERNAL_SPDX_URI).toModelObject(gmo.getModelStore(), gmo.getCopyManager(), gmo.getDocumentUri());
		assertTrue(result instanceof ExternalSpdxElement);
		ExternalSpdxElement externalElement = (ExternalSpdxElement)result;
		assertEquals(EXTERNAL_SPDX_ELEMENT_ID, externalElement.getExternalElementId());
		assertEquals(EXTERNAL_SPDX_URI, externalElement.getExternalSpdxElementURI());
		
		result = new SimpleUriValue(ENUM_URI).toModelObject(gmo.getModelStore(), gmo.getCopyManager(), gmo.getDocumentUri());
		assertEquals(TEST_ENUM, result);
		
		result = new SimpleUriValue(NON_INTERESTING_URI).toModelObject(gmo.getModelStore(), gmo.getCopyManager(), gmo.getDocumentUri());
		assertTrue(result instanceof SimpleUriValue);
		assertEquals(NON_INTERESTING_URI, ((SimpleUriValue)result).getIndividualURI());
	}

}
