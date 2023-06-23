package org.spdx.library.model.compat.v2.license;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class CrossRefTest extends TestCase {

	private static final String TEST_URL1 = "http://test1/index.html";
	private static final String TEST_URL2 = "http://test2/index.html";
	private static final Boolean TEST_ISLIVE1 = true;
	private static final Boolean TEST_ISLIVE2 = false;
	private static final Boolean TEST_ISWAYBACK1 = true;
	private static final Boolean TEST_ISWAYBACK2 = false;
	private static final Boolean TEST_ISVALID1 = true;
	private static final Boolean TEST_ISVALID2 = false;
	private static final String TEST_MATCH1 = "true";
	private static final String TEST_MATCH2 = "false";
	private static final Integer TEST_ORDER1 = 1;
	private static final Integer TEST_ORDER2 = 2;
	private static final String TEST_TIMESTAMP1 = "timestamp1";
	private static final String TEST_TIMESTAMP2 = "timestamp2";
	
	private static final String DOCUMENT_URI = "http://test.doc";
	CrossRef TEST_CROSSREF;
	
	IModelStore modelStore;
	ModelCopyManager copyManager;

	public void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore(SpdxMajorVersion.VERSION_2);
		copyManager = new ModelCopyManager();
		TEST_CROSSREF = new CrossRef.CrossRefBuilder(modelStore, DOCUMENT_URI, 
				modelStore.getNextId(IdType.Anonymous, DOCUMENT_URI), copyManager, TEST_URL1)
				.setLive(TEST_ISLIVE1)
				.setMatch(TEST_MATCH1)
				.setOrder(TEST_ORDER1)
				.setTimestamp(TEST_TIMESTAMP1)
				.setValid(TEST_ISVALID1)
				.setWayBackLink(TEST_ISWAYBACK1)
				.build();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetType() {
		assertEquals(SpdxConstantsCompatV2.CLASS_CROSS_REF, TEST_CROSSREF.getType());
	}

	public void testVerify() throws InvalidSPDXAnalysisException {
		assertEquals(0, TEST_CROSSREF.verify().size());
		TEST_CROSSREF.setStrict(false);
		TEST_CROSSREF.setUrl(null);
		assertEquals(1, TEST_CROSSREF.verify().size());
	}

	public void testGetMatch() throws InvalidSPDXAnalysisException {
		assertEquals(TEST_MATCH1, TEST_CROSSREF.getMatch().get());
		CrossRef result = new CrossRef(modelStore, DOCUMENT_URI, TEST_CROSSREF.getId(), copyManager, false);
		assertEquals(result.getMatch(), TEST_CROSSREF.getMatch());
		TEST_CROSSREF.setMatch(TEST_MATCH2);
		assertEquals(TEST_MATCH2, TEST_CROSSREF.getMatch().get());
		assertEquals(TEST_MATCH2, result.getMatch().get());
		TEST_CROSSREF.setMatch(null);
		assertFalse(TEST_CROSSREF.getMatch().isPresent());
		assertFalse(result.getMatch().isPresent());
	}

	public void testGetUrl() throws InvalidSPDXAnalysisException {
		assertEquals(TEST_URL1, TEST_CROSSREF.getUrl().get());
		CrossRef result = new CrossRef(modelStore, DOCUMENT_URI, TEST_CROSSREF.getId(), copyManager, false);
		assertEquals(TEST_URL1, result.getUrl().get());
		TEST_CROSSREF.setUrl(TEST_URL2);
		assertEquals(TEST_URL2, TEST_CROSSREF.getUrl().get());
		assertEquals(TEST_URL2, result.getUrl().get());
	}
	
	public void testIsValid() throws InvalidSPDXAnalysisException {
		assertEquals(TEST_ISVALID1, TEST_CROSSREF.getValid().get());
		CrossRef result = new CrossRef(modelStore, DOCUMENT_URI, TEST_CROSSREF.getId(), copyManager, false);
		assertEquals(TEST_ISVALID1, result.getValid().get());
		TEST_CROSSREF.setValid(TEST_ISVALID2);
		assertEquals(TEST_ISVALID2, TEST_CROSSREF.getValid().get());
		assertEquals(TEST_ISVALID2, result.getValid().get());
		TEST_CROSSREF.setValid(null);
		assertFalse(TEST_CROSSREF.getValid().isPresent());
		assertFalse(result.getValid().isPresent());
	}

	public void testIsLive() throws InvalidSPDXAnalysisException {
		assertEquals(TEST_ISLIVE1, TEST_CROSSREF.getLive().get());
		CrossRef result = new CrossRef(modelStore, DOCUMENT_URI, TEST_CROSSREF.getId(), copyManager, false);
		assertEquals(TEST_ISLIVE1, result.getLive().get());
		TEST_CROSSREF.setLive(TEST_ISLIVE2);
		assertEquals(TEST_ISLIVE2, TEST_CROSSREF.getLive().get());
		assertEquals(TEST_ISLIVE2, result.getLive().get());
		TEST_CROSSREF.setLive(null);
		assertFalse(TEST_CROSSREF.getLive().isPresent());
		assertFalse(result.getLive().isPresent());
	}

	public void testGetTimestamp() throws InvalidSPDXAnalysisException {
		assertEquals(TEST_TIMESTAMP1, TEST_CROSSREF.getTimestamp().get());
		CrossRef result = new CrossRef(modelStore, DOCUMENT_URI, TEST_CROSSREF.getId(), copyManager, false);
		assertEquals(TEST_TIMESTAMP1, result.getTimestamp().get());
		TEST_CROSSREF.setTimestamp(TEST_TIMESTAMP2);
		assertEquals(TEST_TIMESTAMP2, TEST_CROSSREF.getTimestamp().get());
		assertEquals(TEST_TIMESTAMP2, result.getTimestamp().get());
		TEST_CROSSREF.setTimestamp(null);
		assertFalse(TEST_CROSSREF.getTimestamp().isPresent());
		assertFalse(result.getTimestamp().isPresent());
	}

	public void testIsWayBackLink() throws InvalidSPDXAnalysisException {
		assertEquals(TEST_ISWAYBACK1, TEST_CROSSREF.getIsWayBackLink().get());
		CrossRef result = new CrossRef(modelStore, DOCUMENT_URI, TEST_CROSSREF.getId(), copyManager, false);
		assertEquals(TEST_ISWAYBACK1, result.getIsWayBackLink().get());
		TEST_CROSSREF.setIsWayBackLink(TEST_ISWAYBACK2);
		assertEquals(TEST_ISWAYBACK2, TEST_CROSSREF.getIsWayBackLink().get());
		assertEquals(TEST_ISWAYBACK2, result.getIsWayBackLink().get());
		TEST_CROSSREF.setIsWayBackLink(null);
		assertFalse(TEST_CROSSREF.getIsWayBackLink().isPresent());
		assertFalse(result.getIsWayBackLink().isPresent());
	}

	public void testGetOrder() throws InvalidSPDXAnalysisException {
		assertEquals(TEST_ORDER1, TEST_CROSSREF.getOrder().get());
		CrossRef result = new CrossRef(modelStore, DOCUMENT_URI, TEST_CROSSREF.getId(), copyManager, false);
		assertEquals(TEST_ORDER1, result.getOrder().get());
		TEST_CROSSREF.setOrder(TEST_ORDER2);
		assertEquals(TEST_ORDER2, TEST_CROSSREF.getOrder().get());
		assertEquals(TEST_ORDER2, result.getOrder().get());
		TEST_CROSSREF.setOrder(null);
		assertFalse(TEST_CROSSREF.getOrder().isPresent());
		assertFalse(result.getOrder().isPresent());
	}

	public void testSetDetails() throws InvalidSPDXAnalysisException {
		CrossRef result = new CrossRef.CrossRefBuilder(modelStore, DOCUMENT_URI, 
				modelStore.getNextId(IdType.Anonymous, DOCUMENT_URI), copyManager, TEST_URL2).build();
		assertEquals(TEST_URL2, result.getUrl().get());
		assertFalse(result.getLive().isPresent());
		assertFalse(result.getMatch().isPresent());
		assertFalse(result.getValid().isPresent());
		assertFalse(result.getIsWayBackLink().isPresent());
		assertFalse(result.getOrder().isPresent());
		assertFalse(result.getTimestamp().isPresent());
		result.setDetails(TEST_ISVALID2, TEST_ISLIVE2, TEST_ISWAYBACK2, TEST_MATCH2, TEST_TIMESTAMP2);
		assertEquals(result.getLive().get(), TEST_ISLIVE2);
		assertEquals(result.getMatch().get(), TEST_MATCH2);
		assertEquals(result.getValid().get(), TEST_ISVALID2);
		assertEquals(result.getIsWayBackLink().get(), TEST_ISWAYBACK2);
		assertEquals(result.getTimestamp().get(), TEST_TIMESTAMP2);
		assertEquals(result.getUrl().get(), TEST_URL2);
		assertFalse(result.getOrder().isPresent());
	}

	public void testToString() throws InvalidSPDXAnalysisException {
		String result = TEST_CROSSREF.toString();
		assertTrue(result.contains(TEST_TIMESTAMP1));
	}

}
