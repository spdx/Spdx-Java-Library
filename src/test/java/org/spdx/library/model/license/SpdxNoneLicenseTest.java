package org.spdx.library.model.license;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.GenericSpdxItem;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class SpdxNoneLicenseTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		DefaultModelStore.reset();
	}

	public void testHashCodeEquals() throws InvalidSPDXAnalysisException {
		SpdxNoneLicense l1 = new SpdxNoneLicense();
		IModelStore store = new InMemSpdxStore();
		SpdxNoneLicense l2 = new SpdxNoneLicense(store, "https://doc.uri");
		assertEquals(l1.hashCode(), l2.hashCode());
		assertEquals(l1, l2);
		assertTrue(l1.equals(l2));
		assertTrue(l2.equals(l1));
	}
	
	public void testStoreRetrieveNoneLicense() throws InvalidSPDXAnalysisException {
		GenericSpdxItem item = new GenericSpdxItem();
		item.setLicenseConcluded(new SpdxNoneLicense());
		AnyLicenseInfo result = item.getLicenseConcluded();
		SpdxNoneLicense expected = new SpdxNoneLicense();
		assertEquals(expected, result);
	}
}
