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
package org.spdx.library.model;

import java.util.HashMap;
import java.util.Map;

import org.spdx.library.IndividualUriValue;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SimpleUriValue;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.library.model.core.ExternalMap;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

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
	static final Map<String, ExternalMap> EXTERNAL_MAP = new HashMap<>();

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
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
	public void testToModelObjectV2() throws InvalidSPDXAnalysisException {
		IModelStore store = new InMemSpdxStore(SpdxMajorVersion.VERSION_2);
		ModelCopyManager copyManager = new ModelCopyManager();
		org.spdx.library.model.compat.v2.GenericModelObject gmo = 
				new org.spdx.library.model.compat.v2.GenericModelObject(store, "http://doc", "id", copyManager, true);
		new org.spdx.library.model.compat.v2.SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getCopyManager(), true);
		Object result = new SimpleUriValue(EXTERNAL_SPDX_URI).toModelObject(gmo.getModelStore(), gmo.getCopyManager(),
				gmo.getDocumentUri(), EXTERNAL_MAP);
		assertTrue(result instanceof org.spdx.library.model.compat.v2.ExternalSpdxElement);
		org.spdx.library.model.compat.v2.ExternalSpdxElement externalElement = (org.spdx.library.model.compat.v2.ExternalSpdxElement)result;
		assertEquals(EXTERNAL_SPDX_ELEMENT_ID, externalElement.getExternalElementId());
		assertEquals(EXTERNAL_SPDX_URI, externalElement.getExternalSpdxElementURI());
		
		result = new SimpleUriValue(ENUM_URI).toModelObject(gmo.getModelStore(), gmo.getCopyManager(),
				gmo.getDocumentUri(), EXTERNAL_MAP);
		assertEquals(TEST_ENUM, result);
		
		result = new SimpleUriValue(NON_INTERESTING_URI).toModelObject(gmo.getModelStore(), gmo.getCopyManager(),
				gmo.getDocumentUri(), EXTERNAL_MAP);
		assertTrue(result instanceof SimpleUriValue);
		assertEquals(NON_INTERESTING_URI, ((SimpleUriValue)result).getIndividualURI());
	}
	
	public void testToModelObject() throws InvalidSPDXAnalysisException {
		fail("Unimplemented");
	}

}
