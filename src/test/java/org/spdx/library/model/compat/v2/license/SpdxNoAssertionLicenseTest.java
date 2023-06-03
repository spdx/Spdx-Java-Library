package org.spdx.library.model.compat.v2.license;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class SpdxNoAssertionLicenseTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testHashCodeEquals() throws InvalidSPDXAnalysisException {
		SpdxNoAssertionLicense l1 = new SpdxNoAssertionLicense();
		IModelStore store = new InMemSpdxStore();
		SpdxNoAssertionLicense l2 = new SpdxNoAssertionLicense(store, "https://doc.uri");
		assertEquals(l1.hashCode(), l2.hashCode());
		assertEquals(l1, l2);
		assertTrue(l1.equals(l2));
		assertTrue(l2.equals(l1));
	}

}
