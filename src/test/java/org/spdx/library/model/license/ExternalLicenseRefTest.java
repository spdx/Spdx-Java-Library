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
package org.spdx.library.model.license;

import java.util.ArrayList;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.ExternalDocumentRef;
import org.spdx.library.model.GenericModelObject;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ExternalLicenseRefTest extends TestCase {

	static final String DOCID1 = SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "DOCID1";
	static final String LICENSEREF1 = SpdxConstants.NON_STD_LICENSE_ID_PRENUM + "LICENSEREF1";
	static final String DOCURI1 = "http://doc/uri/one";
	static final String ID1 = DOCID1 + ":" + LICENSEREF1;

	static final String DOCID2 = SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "DOCID1";
	static final String LICENSEREF2 = SpdxConstants.NON_STD_LICENSE_ID_PRENUM + "LICENSEREF2";
	static final String DOCURI2 = "http://doc/uri/two";
	static final String ID2 = DOCID2 + ":" + LICENSEREF2;

	static final String DOCID3 = SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "DOCID3";
	static final String LICENSEREF3 = SpdxConstants.NON_STD_LICENSE_ID_PRENUM + "LICENSEREF3";
	static final String DOCURI3 = "http://doc/uri/three";
	static final String ID3 = DOCID3 + ":" + LICENSEREF3;
	
	static final String LICENSEREF4 = SpdxConstants.NON_STD_LICENSE_ID_PRENUM + "LICENSEREF3";
	static final String DOCURI4 = "http://doc/uri/four";
	
	Checksum CHECKSUM1;
	Checksum CHECKSUM2;
	Checksum CHECKSUM3;
	
	GenericModelObject gmo;
	SpdxDocument doc;
	ExternalDocumentRef docRef1;
	ExternalDocumentRef docRef2;
	ExternalDocumentRef docRef3;

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		doc = new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getCopyManager(), true);
		CHECKSUM1 = gmo.createChecksum(ChecksumAlgorithm.SHA1, "A94A8FE5CCB19BA61C4C0873D391E987982FBBD3");
		CHECKSUM2 = gmo.createChecksum(ChecksumAlgorithm.SHA1, "1086444D91D3A28ECA55124361F6DE2B93A9AE91");
		CHECKSUM3 = gmo.createChecksum(ChecksumAlgorithm.SHA1, "571D85D7752CB4E5C6D919BAC21FD2BAAE9F2FCA");
		docRef1 = gmo.createExternalDocumentRef(DOCID1, DOCURI1, CHECKSUM1);
		docRef2 = gmo.createExternalDocumentRef(DOCID2, DOCURI2, CHECKSUM2);
		docRef3 = gmo.createExternalDocumentRef(DOCID3, DOCURI3, CHECKSUM3);
	}
	
	public void testVerify() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		assertEquals(0, elr.verify().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#equivalent(org.spdx.library.model.ModelObject)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testEquivalentModelObject() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr1 = new ExternalExtractedLicenseInfo(ID1);
		assertTrue(elr1.equivalent(elr1));
		ExternalExtractedLicenseInfo elr2 = new ExternalExtractedLicenseInfo(ID1);
		assertTrue(elr1.equivalent(elr2));
		ExternalExtractedLicenseInfo elr3 = new ExternalExtractedLicenseInfo(ID2);
		assertFalse(elr2.equivalent(elr3));
	}
	
	public void testUriToExternalLicenseRefId() throws InvalidSPDXAnalysisException {
		String uri = DOCURI1 + "#" + LICENSEREF1;
		String expected = DOCID1 + ":" + LICENSEREF1;
		String result = ExternalExtractedLicenseInfo.uriToExternalExtractedLicenseId(uri, gmo.getModelStore(),
				gmo.getDocumentUri(), null);
		assertEquals(expected, result);
		uri = DOCURI4 + "#" + LICENSEREF4;
		String generatedDocId = "DocumentRef-gnrtd0";
		expected = generatedDocId + ":" + LICENSEREF4;
		try {
			result = ExternalExtractedLicenseInfo.uriToExternalExtractedLicenseId(uri, gmo.getModelStore(),
					gmo.getDocumentUri(), null);
			fail("Expected to fail since DOCID4 has not been created");
		} catch (InvalidSPDXAnalysisException e) {
			// expected
		}
		result = ExternalExtractedLicenseInfo.uriToExternalExtractedLicenseId(uri, gmo.getModelStore(),
				gmo.getDocumentUri(), gmo.getCopyManager());
		assertEquals(expected, result);
		String LicenseRef5 = SpdxConstants.NON_STD_LICENSE_ID_PRENUM + "LICENSEREF5";
		uri = DOCURI4 + "#" + LicenseRef5;
		expected = generatedDocId + ":" + LicenseRef5;
		result = ExternalExtractedLicenseInfo.uriToExternalExtractedLicenseId(uri, gmo.getModelStore(),
				gmo.getDocumentUri(), null);
		assertEquals(expected, result);
	}
	
	public void testUriToExternalLicenseRef() throws InvalidSPDXAnalysisException {
		String externalLicenseUri = DOCURI1 + "#" + LICENSEREF1;
		ExternalExtractedLicenseInfo result = ExternalExtractedLicenseInfo.uriToExternalExtractedLicense(externalLicenseUri, DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), null);
		assertEquals(LICENSEREF1, result.getExternalLicenseRef());
		assertEquals(ID1, result.getId());
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#getComment()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetComment() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		try {
		elr.getComment();	// Just testing to make sure it doesn't exception
		} catch(Exception ex) {
		    fail("Exception getting comment"+ex.getMessage());
		}
    }

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#setComment(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSetComment() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		try {
			elr.setComment("New comment");
			fail("This should have failed!");
		} catch(InvalidSPDXAnalysisException ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#getSeeAlso()}.
	 */
	public void testGetSeeAlso() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		assertTrue(elr.getSeeAlso().isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#setSeeAlso(java.util.Collection)}.
	 */
	public void testSetSeeAlso() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		try {
			elr.setSeeAlso(new ArrayList<String>());
			fail("This should have failed!");
		} catch(InvalidSPDXAnalysisException ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#getExtractedText()}.
	 */
	public void testGetExtractedText() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		assertTrue(elr.getExtractedText().contains(LICENSEREF1));
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#setExtractedText(java.lang.String)}.
	 */
	public void testSetExtractedText() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		try {
			elr.setExtractedText("New text");
			fail("This should have failed!");
		} catch(InvalidSPDXAnalysisException ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#getExternalDocumentId()}.
	 */
	public void testGetExternalDocumentId() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		assertEquals(DOCID1, elr.getExternalDocumentId());
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#getExternalLicenseRef()}.
	 */
	public void testGetExternalLicenseRef() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		assertEquals(LICENSEREF1, elr.getExternalLicenseRef());
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#getExternalExtractedLicenseURI()}.
	 */
	public void testGetExternalLicenseRefURI() throws InvalidSPDXAnalysisException {
		String expected = DOCURI1 + "#" + LICENSEREF1;
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		assertEquals(expected, elr.getExternalExtractedLicenseURI());
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#externalExtractedLicenseIdToURI(java.lang.String, org.spdx.storage.IModelStore, java.lang.String, org.spdx.library.ModelCopyManager)}.
	 */
	public void testExternalLicenseRefIdToURI() throws InvalidSPDXAnalysisException {
		String expected = DOCURI1 + "#" + LICENSEREF1;
		assertEquals(expected, 
				ExternalExtractedLicenseInfo.externalExtractedLicenseIdToURI(
						ID1, DefaultModelStore.getDefaultModelStore(), 
						DefaultModelStore.getDefaultDocumentUri(), null));
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExternalExtractedLicenseInfo#getIndividualURI()}.
	 */
	public void testGetIndividualURI() throws InvalidSPDXAnalysisException {
		String expected = DOCURI1 + "#" + LICENSEREF1;
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		assertEquals(expected, elr.getIndividualURI());
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ExtractedLicenseInfo#compareTo(org.spdx.library.model.license.ExtractedLicenseInfo)}.
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr1 = new ExternalExtractedLicenseInfo(ID1);
		ExternalExtractedLicenseInfo elrSame = new ExternalExtractedLicenseInfo(ID1);
		ExternalExtractedLicenseInfo elr2 = new ExternalExtractedLicenseInfo(ID2);
		assertEquals(0, elr1.compareTo(elrSame));
		assertTrue(elr1.compareTo(elr2) != 0);
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.SimpleLicensingInfo#getName()}.
	 */
	public void testGetName() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		assertTrue(elr.getName().isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.SimpleLicensingInfo#setName(java.lang.String)}.
	 */
	public void testSetName() throws InvalidSPDXAnalysisException {
		ExternalExtractedLicenseInfo elr = new ExternalExtractedLicenseInfo(ID1);
		try {
			elr.setName("New name");
			fail("This should have failed!");
		} catch(InvalidSPDXAnalysisException ex) {
			// expected
		}
	}

}
