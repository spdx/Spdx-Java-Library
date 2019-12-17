package org.spdx.library.model;

import java.util.Collection;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

public class ExternalSpdxElementTest extends TestCase {
	
	static final String DOCID1 = SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "DOCID1";
	static final String SPDXID1 = SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "SPDXID1";
	static final String DOCURI1 = "http://doc/uri/one";
	static final String ID1 = DOCID1 + ":" + SPDXID1;

	static final String DOCID2 = SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "DOCID1";
	static final String SPDXID2 = SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "SPDXID2";
	static final String DOCURI2 = "http://doc/uri/two";
	static final String ID2 = DOCID2 + ":" + SPDXID2;

	static final String DOCID3 = SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "DOCID3";
	static final String SPDXID3 = SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "SPDXID3";
	static final String DOCURI3 = "http://doc/uri/three";
	static final String ID3 = DOCID3 + ":" + SPDXID3;
	
	Checksum CHECKSUM1;
	Checksum CHECKSUM2;
	Checksum CHECKSUM3;
	
	GenericModelObject gmo;
	SpdxDocument doc;

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		doc = new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), true);
		CHECKSUM1 = gmo.createChecksum(ChecksumAlgorithm.SHA1, "A94A8FE5CCB19BA61C4C0873D391E987982FBBD3");
		CHECKSUM2 = gmo.createChecksum(ChecksumAlgorithm.SHA1, "1086444D91D3A28ECA55124361F6DE2B93A9AE91");
		CHECKSUM3 = gmo.createChecksum(ChecksumAlgorithm.SHA1, "571D85D7752CB4E5C6D919BAC21FD2BAAE9F2FCA");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVerify() throws InvalidSPDXAnalysisException {
		gmo.createExternalDocumentRef(DOCID1, DOCURI1, CHECKSUM1);
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		assertEquals(0, externalElement.verify().size());
	}

	public void testGetExternalDocumentId() throws InvalidSPDXAnalysisException {
		gmo.createExternalDocumentRef(DOCID1, DOCURI1, CHECKSUM1);
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		assertEquals(DOCID1, externalElement.getExternalDocumentId());
	}

	public void testGetExternalElementId() throws InvalidSPDXAnalysisException {
		gmo.createExternalDocumentRef(DOCID1, DOCURI1, CHECKSUM1);
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		assertEquals(SPDXID1, externalElement.getExternalElementId());
	}

	public void testGetIndividualURI() throws InvalidSPDXAnalysisException {
		gmo.createExternalDocumentRef(DOCID1, DOCURI1, CHECKSUM1);
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		String expected = DOCURI1 + "#" + SPDXID1;
		assertEquals(expected, externalElement.getExternalSpdxElementURI());
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		gmo.createExternalDocumentRef(DOCID1, DOCURI1, CHECKSUM1);
		gmo.createExternalDocumentRef(DOCID2, DOCURI2, CHECKSUM2);
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		assertTrue(externalElement.equivalent(externalElement));
		ExternalSpdxElement externalElement2 = new ExternalSpdxElement(ID1);
		assertTrue(externalElement.equivalent(externalElement2));
		ExternalSpdxElement externalElement3 = new ExternalSpdxElement(ID2);
		assertFalse(externalElement.equivalent(externalElement3));
	}
	
	public void testUseInRelationship() throws InvalidSPDXAnalysisException {
		gmo.createExternalDocumentRef(DOCID1, DOCURI1, CHECKSUM1);
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		GenericSpdxElement element = new GenericSpdxElement(externalElement.getModelStore(), 
				externalElement.getDocumentUri(), 
				externalElement.getModelStore().getNextId(IdType.Anonomous, externalElement.getDocumentUri()), true);
		element.setName("Element1Name");
		Relationship relationship = element.createRelationship(externalElement, RelationshipType.AMENDS, "External relationship");
		GenericSpdxElement compare = new GenericSpdxElement(element.getModelStore(), element.getDocumentUri(),
				element.getId(), false);
		element.addRelationship(relationship);
		assertEquals("Element1Name", compare.getName().get());
		assertEquals("Element1Name", element.getName().get());
		Collection<Relationship> relCollection = compare.getRelationships();
		Relationship[] relArray = relCollection.toArray(new Relationship[1]);
		Relationship compareRelationship = relArray[0];
		assertEquals(RelationshipType.AMENDS, compareRelationship.getRelationshipType().get());
		assertEquals("External relationship", compareRelationship.getComment().get());
		ExternalSpdxElement compareRelatedElement = (ExternalSpdxElement)compareRelationship.getRelatedSpdxElement().get();
		assertEquals(ID1, compareRelatedElement.getId());
		assertEquals(DOCID1, compareRelatedElement.getExternalDocumentId());
		assertEquals(SPDXID1, compareRelatedElement.getExternalElementId());
		assertEquals( DOCURI1 + "#" + SPDXID1, compareRelatedElement.getExternalSpdxElementURI());
	}
	
	public void testUriToExternalSpdxElementId() throws InvalidSPDXAnalysisException {
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.MD5, "595f44fec1e92a71d3e9e77456ba80d1");
		gmo.createExternalDocumentRef(DOCID1, DOCURI1, checksum);
		String uri = DOCURI1 + "#" + SPDXID1;
		String expected = DOCID1 + ":" + SPDXID1;
		String result = ExternalSpdxElement.uriToExternalSpdxElementId(uri, gmo.getModelStore(),
				gmo.getDocumentUri(), false);
		assertEquals(expected, result);
		uri = DOCURI2 + "#" + SPDXID2;
		String generatedDocId = "DocumentRef-0";
		expected = generatedDocId + ":" + SPDXID2;
		try {
			result = ExternalSpdxElement.uriToExternalSpdxElementId(uri, gmo.getModelStore(),
					gmo.getDocumentUri(), false);
			fail("Expected to fail since DOCID2 has not been created");
		} catch (InvalidSPDXAnalysisException e) {
			// expected
		}
		result = ExternalSpdxElement.uriToExternalSpdxElementId(uri, gmo.getModelStore(),
				gmo.getDocumentUri(), true);
		assertEquals(expected, result);
		uri = DOCURI2 + "#" + SPDXID3;
		expected = generatedDocId + ":" + SPDXID3;
		result = ExternalSpdxElement.uriToExternalSpdxElementId(uri, gmo.getModelStore(),
				gmo.getDocumentUri(), false);
		assertEquals(expected, result);
	}
}
