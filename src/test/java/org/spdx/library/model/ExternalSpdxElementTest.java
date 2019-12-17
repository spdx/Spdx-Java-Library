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

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVerify() throws InvalidSPDXAnalysisException {
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		assertEquals(0, externalElement.verify().size());
	}

	public void testGetExternalDocumentId() throws InvalidSPDXAnalysisException {
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		assertEquals(DOCID1, externalElement.getExternalDocumentId());
	}

	public void testGetExternalElementId() throws InvalidSPDXAnalysisException {
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		assertEquals(SPDXID1, externalElement.getExternalElementId());
	}

	public void testGetIndividualURI() throws InvalidSPDXAnalysisException {
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		Checksum checksum = externalElement.createChecksum(ChecksumAlgorithm.MD5, "595f44fec1e92a71d3e9e77456ba80d1");
		externalElement.createExternalDocumentRef(DOCID1, DOCURI1, checksum);
		String expected = DOCURI1 + "#" + SPDXID1;
		assertEquals(expected, externalElement.getExternalSpdxElementURI());
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		assertTrue(externalElement.equivalent(externalElement));
		ExternalSpdxElement externalElement2 = new ExternalSpdxElement(ID1);
		assertTrue(externalElement.equivalent(externalElement2));
		ExternalSpdxElement externalElement3 = new ExternalSpdxElement(ID2);
		assertFalse(externalElement.equivalent(externalElement3));
	}
	
	public void testUseInRelationship() throws InvalidSPDXAnalysisException {
		ExternalSpdxElement externalElement = new ExternalSpdxElement(ID1);
		Checksum checksum = externalElement.createChecksum(ChecksumAlgorithm.MD5, "595f44fec1e92a71d3e9e77456ba80d1");
		externalElement.createExternalDocumentRef(DOCID1, DOCURI1, checksum);
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

}
