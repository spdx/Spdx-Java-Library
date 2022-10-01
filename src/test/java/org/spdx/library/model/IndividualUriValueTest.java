package org.spdx.library.model;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.ExternalExtractedLicenseInfo;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.library.model.license.SpdxNoneLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class IndividualUriValueTest extends TestCase {
	
	static final String SHA1_CHECKSUM = "399e50ed82067fc273ed02495fbdb149a667ebe9";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	// Test if a simple URI value is equal to the ExternalExtracedLicenseInfo with the same URI value
	public void testEqualUriValueExternalExtractedLicenseInfo() throws InvalidSPDXAnalysisException {
		IModelStore modelStore = new InMemSpdxStore();
		ModelCopyManager copyManager = new ModelCopyManager();
		String id = SpdxConstants.NON_STD_LICENSE_ID_PRENUM+"ID";
		String namespace = "http://example.namespace";
		String externalDocId = SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "externalDoc";
		String externalDocNamespace = "http://example.external.namespace";
		ExternalDocumentRef edr = new ExternalDocumentRef(modelStore, namespace, externalDocId, copyManager, true);
		edr.setChecksum(edr.createChecksum(ChecksumAlgorithm.SHA1, SHA1_CHECKSUM));
		edr.setSpdxDocumentNamespace(externalDocNamespace);
		ExternalExtractedLicenseInfo eel = new ExternalExtractedLicenseInfo(modelStore, namespace, externalDocId + ":" + id, copyManager, true);
		SimpleUriValue suv = new SimpleUriValue(externalDocNamespace + "#" + id);
		assertTrue(eel.equals(suv));
		assertTrue(suv.equals(eel));
	}

	// Test if a simple URI value is equal to the ExternalSpdxElement with the same URI value
	public void testEqualUriValueExternalSpdxElement() throws InvalidSPDXAnalysisException {
		IModelStore modelStore = new InMemSpdxStore();
		ModelCopyManager copyManager = new ModelCopyManager();
		String id = SpdxConstants.SPDX_ELEMENT_REF_PRENUM+"ID";
		String namespace = "http://example.namespace";
		String externalDocId = SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "externalDoc";
		String externalDocNamespace = "http://example.external.namespace";
		ExternalDocumentRef edr = new ExternalDocumentRef(modelStore, namespace, externalDocId, copyManager, true);
		edr.setChecksum(edr.createChecksum(ChecksumAlgorithm.SHA1, SHA1_CHECKSUM));
		edr.setSpdxDocumentNamespace(externalDocNamespace);
		ExternalSpdxElement ese = new ExternalSpdxElement(modelStore, namespace, externalDocId + ":" + id, copyManager, true);
		SimpleUriValue suv = new SimpleUriValue(externalDocNamespace + "#" + id);
		assertTrue(ese.equals(suv));
		assertTrue(suv.equals(ese));
	}
	
	// Test if a simple URI value is equal to the NoAssertionLicense with the same URI value
	public void testEqualUriValueNoAssertionLicense() throws InvalidSPDXAnalysisException {
		SpdxNoAssertionLicense nal = new SpdxNoAssertionLicense();
		SimpleUriValue suv = new SimpleUriValue(SpdxConstants.URI_VALUE_NOASSERTION);
		assertTrue(nal.equals(suv));
		assertTrue(suv.equals(nal));
	}
	
	// Test if a simple URI value is equal to the NoneLicense with the same URI value
	public void testEqualUriValueNoneLicense() throws InvalidSPDXAnalysisException {
		SpdxNoneLicense nl = new SpdxNoneLicense();
		SimpleUriValue suv = new SimpleUriValue(SpdxConstants.URI_VALUE_NONE);
		assertTrue(nl.equals(suv));
		assertTrue(suv.equals(nl));
	}
	
	// Test if a simple URI value is equal to the SpdxNoneElement with the same URI value
	public void testEqualUriValueNone() throws InvalidSPDXAnalysisException {
		SpdxNoneElement ne = new SpdxNoneElement();
		SimpleUriValue suv = new SimpleUriValue(SpdxConstants.URI_VALUE_NONE);
		assertTrue(ne.equals(suv));
		assertTrue(suv.equals(ne));
	}
	
	// Test if a simple URI value is equal to the SpdxNoAssertionElement with the same URI value
	public void testEqualUriValueNoAssertion() throws InvalidSPDXAnalysisException {
		SpdxNoAssertionElement na = new SpdxNoAssertionElement();
		SimpleUriValue suv = new SimpleUriValue(SpdxConstants.URI_VALUE_NOASSERTION);
		assertTrue(na.equals(suv));
		assertTrue(suv.equals(na));
	}
}
