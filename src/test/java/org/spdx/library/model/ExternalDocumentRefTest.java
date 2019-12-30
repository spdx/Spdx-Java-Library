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


import java.util.Optional;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ExternalDocumentRefTest extends TestCase {
	
	static final String SHA1_VALUE1 = "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12";
	static final String SHA1_VALUE2 = "2222e1c67a2d28fced849ee1bb76e7391b93eb12";
	Checksum CHECKSUM1;
	Checksum CHECKSUM2;
	static final String DOCUMENT_URI1 = "http://spdx.org/docs/uniquevalue1";
	static final String DOCUMENT_URI2 = "http://spdx.org/docs/uniquevalue2";
	static final String DOCUMENT_ID1 = "DocumentRef-1";
	static final String DOCUMENT_ID2 = "DocumentRef-2";
	
	GenericModelObject gmo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getCopyManager(), true);
		CHECKSUM1 = gmo.createChecksum(ChecksumAlgorithm.SHA1, SHA1_VALUE1);
		CHECKSUM2 = gmo.createChecksum(ChecksumAlgorithm.SHA1, SHA1_VALUE2);
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ExternalDocumentRef edf = gmo.createExternalDocumentRef(DOCUMENT_ID1, DOCUMENT_URI1, CHECKSUM1);
		Checksum checksumCopy = gmo.createChecksum(CHECKSUM1.getAlgorithm().get(), CHECKSUM1.getValue().get());
		ExternalDocumentRef edf2 = gmo.createExternalDocumentRef(DOCUMENT_ID2, DOCUMENT_URI1, checksumCopy);
		assertTrue(edf.equivalent(edf2));
		edf2.setSpdxDocumentNamespace(DOCUMENT_URI2);
		assertFalse(edf.equivalent(edf2));
		edf2.setSpdxDocumentNamespace(DOCUMENT_URI1);
		assertTrue(edf.equivalent(edf2));
		// Checksum
		edf2.setChecksum(CHECKSUM2);
		assertFalse(edf.equivalent(edf2));
		edf2.setChecksum(CHECKSUM1);
		assertTrue(edf.equivalent(edf2));
	}

	/**
	 * Test method for {@link org.spdx.library.model.ExternalDocumentRef#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		ExternalDocumentRef edf = gmo.createExternalDocumentRef(DOCUMENT_ID1, DOCUMENT_URI1, CHECKSUM1);
		assertEquals(0, edf.verify().size());
		edf.setChecksum(null);
		assertEquals(1, edf.verify().size());
		edf.setSpdxDocumentNamespace(null);
		assertEquals(2, edf.verify().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ExternalDocumentRef#setChecksum(org.spdx.library.model.Checksum)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSetChecksum() throws InvalidSPDXAnalysisException {
		ExternalDocumentRef edf = gmo.createExternalDocumentRef(DOCUMENT_ID1, DOCUMENT_URI1, CHECKSUM1);
		ExternalDocumentRef edf2 = new ExternalDocumentRef(edf.getModelStore(), edf.getDocumentUri(), edf.getId(), edf.getCopyManager(), false);
		assertEquals(CHECKSUM1, edf.getChecksum().get());
		assertEquals(CHECKSUM1, edf2.getChecksum().get());
		edf.setChecksum(CHECKSUM2);
		assertEquals(CHECKSUM2, edf.getChecksum().get());
		assertEquals(CHECKSUM2, edf2.getChecksum().get());
	}

	public void testsetSpdxDocumentNamespace() throws InvalidSPDXAnalysisException {
		ExternalDocumentRef edf = gmo.createExternalDocumentRef(DOCUMENT_ID1, DOCUMENT_URI1, CHECKSUM1);
		assertEquals(DOCUMENT_URI1, edf.getSpdxDocumentNamespace().get());
		edf.setSpdxDocumentNamespace(DOCUMENT_URI2);
		assertEquals(DOCUMENT_URI2, edf.getSpdxDocumentNamespace().get());
	}
	/**
	 * Test method for {@link org.spdx.library.model.ExternalDocumentRef#setSpdxDocument(org.spdx.library.model.SpdxDocument)}.
	 */
	public void testSetSpdxDocument() throws InvalidSPDXAnalysisException {
		SpdxDocument doc1 = new SpdxDocument(gmo.getModelStore(), DOCUMENT_URI1, gmo.getCopyManager(), true);
		SpdxDocument doc2 = new SpdxDocument(gmo.getModelStore(), DOCUMENT_URI2, gmo.getCopyManager(), true);
		doc1.setName("DocumentName1");
		doc2.setName("DocumentName2");
		ExternalDocumentRef edf = gmo.createExternalDocumentRef(DOCUMENT_ID1, DOCUMENT_URI2, CHECKSUM1);
		ExternalDocumentRef edf2 = new ExternalDocumentRef(edf.getModelStore(), edf.getDocumentUri(), edf.getId(), edf.getCopyManager(), false);
		assertEquals(DOCUMENT_URI2, edf.getSpdxDocumentNamespace().get());
		assertEquals(DOCUMENT_URI2, edf2.getSpdxDocumentNamespace().get());
		edf.setSpdxDocument(doc1);
		assertEquals(DOCUMENT_URI1, edf.getSpdxDocumentNamespace().get());
		assertEquals("DocumentName1", edf.getSpdxDocument().get().getName().get());
		assertEquals(DOCUMENT_URI1, edf2.getSpdxDocumentNamespace().get());
		assertEquals("DocumentName1", edf2.getSpdxDocument().get().getName().get());
		edf.setSpdxDocument(doc2);
		assertEquals(DOCUMENT_URI2, edf.getSpdxDocumentNamespace().get());
		assertEquals("DocumentName2", edf.getSpdxDocument().get().getName().get());
		assertEquals(DOCUMENT_URI2, edf2.getSpdxDocumentNamespace().get());
		assertEquals("DocumentName2", edf2.getSpdxDocument().get().getName().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ExternalDocumentRef#setExternalDocumentId(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSetExternalDocumentId() throws InvalidSPDXAnalysisException {
		ExternalDocumentRef edf = gmo.createExternalDocumentRef(DOCUMENT_ID1, DOCUMENT_URI1, CHECKSUM1);
		ExternalDocumentRef edf2 = new ExternalDocumentRef(edf.getModelStore(), edf.getDocumentUri(), edf.getId(), edf.getCopyManager(), false);
		assertEquals(DOCUMENT_URI1, edf.getSpdxDocumentNamespace().get());
		assertEquals(DOCUMENT_URI1, edf2.getSpdxDocumentNamespace().get());

		edf.setSpdxDocumentNamespace(DOCUMENT_URI2);
		assertEquals(DOCUMENT_URI2, edf.getSpdxDocumentNamespace().get());
		assertEquals(DOCUMENT_URI2, edf2.getSpdxDocumentNamespace().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ExternalDocumentRef#compareTo(org.spdx.library.model.ExternalDocumentRef)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		ExternalDocumentRef edf = gmo.createExternalDocumentRef(DOCUMENT_ID1, DOCUMENT_URI1, CHECKSUM1);
		ExternalDocumentRef edf2 = gmo.createExternalDocumentRef(DOCUMENT_ID2, DOCUMENT_URI1, CHECKSUM1);
		assertEquals(0, edf.compareTo(edf2));
		assertEquals(0, edf2.compareTo(edf));
		edf.setChecksum(CHECKSUM2);
		assertTrue(edf.compareTo(edf2) < 0);
		assertTrue(edf2.compareTo(edf) > 0);
		edf.setSpdxDocumentNamespace(DOCUMENT_URI2);
		assertTrue(edf.compareTo(edf2) > 0);
		assertTrue(edf2.compareTo(edf) < 0);
		edf.setSpdxDocumentNamespace(null);
		assertTrue(edf.compareTo(edf2) < 0);
		assertTrue(edf2.compareTo(edf) > 0);
		edf2.setSpdxDocumentNamespace(null);
		assertTrue(edf.compareTo(edf2) < 0);
		assertTrue(edf2.compareTo(edf) > 0);
		edf.setChecksum(null);
		assertTrue(edf.compareTo(edf2) < 0);
		assertTrue(edf2.compareTo(edf) > 0);
		edf2.setChecksum(null);
		assertEquals(0, edf.compareTo(edf2));
		assertEquals(0, edf2.compareTo(edf));
	}
	
	public void testGetExternalDocRefByDocNamespace() throws InvalidSPDXAnalysisException {
		// need a document to tie the external refs to
		SpdxModelFactory.createModelObject(gmo.getModelStore(), gmo.getDocumentUri(), 
				SpdxConstants.SPDX_DOCUMENT_ID, SpdxConstants.CLASS_SPDX_DOCUMENT, gmo.getCopyManager());
		// test empty
		Optional<ExternalDocumentRef> result = ExternalDocumentRef.getExternalDocRefByDocNamespace(gmo.getModelStore(), gmo.getDocumentUri(), 
				DOCUMENT_URI1, null);
		assertFalse(result.isPresent());
		// test create
		result = ExternalDocumentRef.getExternalDocRefByDocNamespace(gmo.getModelStore(), gmo.getDocumentUri(), 
				DOCUMENT_URI1, gmo.getCopyManager());
		assertTrue(result.isPresent());
		assertEquals(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "0", result.get().getId());
		// test non matching
		result = ExternalDocumentRef.getExternalDocRefByDocNamespace(gmo.getModelStore(), gmo.getDocumentUri(), 
				DOCUMENT_URI2, null);
		assertFalse(result.isPresent());
		// test add second
		result = ExternalDocumentRef.getExternalDocRefByDocNamespace(gmo.getModelStore(), gmo.getDocumentUri(), 
				DOCUMENT_URI2, gmo.getCopyManager());
		assertTrue(result.isPresent());
		assertEquals(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "1", result.get().getId());
		// test match
		result = ExternalDocumentRef.getExternalDocRefByDocNamespace(gmo.getModelStore(), gmo.getDocumentUri(), 
				DOCUMENT_URI1, null);
		assertTrue(result.isPresent());
		assertEquals(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "0", result.get().getId());
		result = ExternalDocumentRef.getExternalDocRefByDocNamespace(gmo.getModelStore(), gmo.getDocumentUri(), 
				DOCUMENT_URI2, gmo.getCopyManager());
		assertTrue(result.isPresent());
		assertEquals(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "1", result.get().getId());
	}

}
