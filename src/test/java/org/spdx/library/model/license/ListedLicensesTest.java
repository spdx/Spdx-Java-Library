package org.spdx.library.model.license;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;

import junit.framework.TestCase;
import org.spdx.storage.listedlicense.SpdxListedLicenseModelStore;

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

/**
 * @author yevster@gmail.com, Black Duck Software
 *         SPDX-License-Identifier: Apache-2.0
 */
public class ListedLicensesTest extends TestCase {
	
	@SuppressWarnings("unused")
	private static final String LICENSE_LIST_VERSION = "3.7";
	private static final int NUM_3_7_LICENSES = 373;
	private static final int NUM_3_7_EXCEPTION = 36;

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
	 * Test method for {@link org.spdx.library.model.license.ListedLicenses#isSpdxListedLicenseId(java.lang.String)}.
	 */
	public void testIsSpdxListedLicenseID() {
		assertTrue(ListedLicenses.getListedLicenses().isSpdxListedLicenseId("Apache-2.0"));
	}

	/**
	 * Test method for {@link org.spdx.library.model.license.ListedLicenses#getListedLicenseById(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetListedLicenseById() throws InvalidSPDXAnalysisException {
		String id = "Apache-2.0";
		SpdxListedLicense result = ListedLicenses.getListedLicenses().getListedLicenseById(id);
		assertEquals(id, result.getLicenseId());
	}

	public void testGetListedLicenseByIdReturnsNull() throws InvalidSPDXAnalysisException {
		String id = "XXXX";
		SpdxListedLicense result = ListedLicenses.getListedLicenses().getListedLicenseById(id);
		assertNull(result);
	}

	public void testGetLicenseIbyIdLocal() throws InvalidSPDXAnalysisException {
		System.setProperty("SPDXParser.OnlyUseLocalLicenses", "true");
		ListedLicenses.resetListedLicenses();
		try {
			String id = "Apache-2.0";
			SpdxListedLicense result = ListedLicenses.getListedLicenses().getListedLicenseById(id);
			assertEquals(id, result.getLicenseId());
		} finally {
			System.setProperty("SPDXParser.OnlyUseLocalLicenses", "false");
			ListedLicenses.resetListedLicenses();
		}
	}
	/**
	 * Test method for {@link org.spdx.library.model.license.ListedLicenses#getSpdxListedLicenseIds()}.
	 */
	public void testGetSpdxListedLicenseIds() {
		List<String> result = ListedLicenses.getListedLicenses().getSpdxListedLicenseIds();
		assertTrue(result.size() >= NUM_3_7_LICENSES);
		assertTrue(result.contains("Apache-2.0"));
	}
	
	public void testGetListedExceptionById() throws InvalidSPDXAnalysisException {
		ListedLicenses.resetListedLicenses();
		String id = "Classpath-exception-2.0";
		assertTrue(ListedLicenses.getListedLicenses().isSpdxListedExceptionId(id));
		LicenseException result = ListedLicenses.getListedLicenses().getListedExceptionById(id);
		assertEquals(id, result.getLicenseExceptionId());
	}
	
	public void testGetListedExceptionByIdReturnsNull() throws InvalidSPDXAnalysisException {
		ListedLicenses.resetListedLicenses();
		String id = "XXXX";
		assertFalse(ListedLicenses.getListedLicenses().isSpdxListedExceptionId(id));
		LicenseException result = ListedLicenses.getListedLicenses().getListedExceptionById(id);
		assertNull(result);
	}

	public void testGetExceptionbyIdLocal() throws InvalidSPDXAnalysisException {
		System.setProperty("SPDXParser.OnlyUseLocalLicenses", "true");
		ListedLicenses.resetListedLicenses();
		try {
			String id = "Classpath-exception-2.0";
			assertTrue(ListedLicenses.getListedLicenses().isSpdxListedExceptionId(id));
			LicenseException result = ListedLicenses.getListedLicenses().getListedExceptionById(id);
			assertEquals(id, result.getLicenseExceptionId());
		} finally {
			System.setProperty("SPDXParser.OnlyUseLocalLicenses", "false");
			ListedLicenses.resetListedLicenses();
		}
	}
	
	public void testGetExceptionIds() throws InvalidSPDXAnalysisException {
		List<String> result = ListedLicenses.getListedLicenses().getSpdxListedExceptionIds();
		assertTrue(result.size() >= NUM_3_7_EXCEPTION);
		assertTrue(result.contains("389-exception"));
	}
	
	public void testListedLicenseIdCaseSensitive() {
		String expected = "Apache-2.0";
		String lower = expected.toLowerCase();
		assertEquals(expected, ListedLicenses.getListedLicenses().listedLicenseIdCaseSensitive(lower).get());
		assertFalse(ListedLicenses.getListedLicenses().listedLicenseIdCaseSensitive("NotaLicenseId").isPresent());
	}
	
	public void testListedExceptionIdCaseSensitive() {
		String expected = "Classpath-exception-2.0";
		String lower = expected.toLowerCase();
		assertEquals(expected, ListedLicenses.getListedLicenses().listedExceptionIdCaseSensitive(lower).get());
		assertFalse(ListedLicenses.getListedLicenses().listedExceptionIdCaseSensitive("NotAnExceptionId").isPresent());
	}
	
	public void testGetLicenseIdProperty() throws InvalidSPDXAnalysisException {
	    String id = "Apache-2.0";
	    SpdxListedLicense lic = ListedLicenses.getListedLicenses().getListedLicenseById(id);
	    Optional<Object> idProp = lic.getModelStore().getValue(lic.getDocumentUri(), id, SpdxConstants.PROP_LICENSE_ID);
	    assertTrue(idProp.isPresent());
	    assertTrue(idProp.get() instanceof String);
	    assertEquals(id, idProp.get());
	}
	
	   public void testGetExceptionIdProperty() throws InvalidSPDXAnalysisException {
	        String id = "Classpath-exception-2.0";
	        ListedLicenseException ex = ListedLicenses.getListedLicenses().getListedExceptionById(id);
	        Optional<Object> idProp = ex.getModelStore().getValue(ex.getDocumentUri(), id, SpdxConstants.PROP_LICENSE_EXCEPTION_ID);
	        assertTrue(idProp.isPresent());
	        assertTrue(idProp.get() instanceof String);
	        assertEquals(id, idProp.get());
	    }

	public void testLicenseListInitializeListedLicenses() throws InvalidSPDXAnalysisException {
		try {
			ListedLicenses.initializeListedLicenses(new SpdxListedLicenseModelStore() {
				@Override
				public InputStream getTocInputStream() throws IOException {
					return ListedLicensesTest.class.getResourceAsStream("licenses.json");
				}

				@Override
				public InputStream getExceptionTocInputStream() throws IOException {
					return ListedLicensesTest.class.getResourceAsStream("exceptions.json");
				}

				@Override
				public InputStream getLicenseInputStream(String licenseId) throws IOException {
					throw new UnsupportedOperationException("this shouldn't be used in tests");
				}

				@Override
				public InputStream getExceptionInputStream(String exceptionId) throws IOException {
					throw new UnsupportedOperationException("this shouldn't be used in tests");
				}

				@Override
				public void close() throws Exception {
					// Nothing to do for the either the in-memory or the web store
				}
			});
			List<String> licenseIds = ListedLicenses.getListedLicenses().getSpdxListedLicenseIds();
			assertEquals(1, licenseIds.size());
			assertFalse(licenseIds.contains("Apache-2.0"));
			assertTrue(licenseIds.contains("TEST"));

			List<String> exceptionIds = ListedLicenses.getListedLicenses().getSpdxListedExceptionIds();
			assertEquals(1, licenseIds.size());
			assertFalse(exceptionIds.contains("389-exception"));
			assertTrue(exceptionIds.contains("TEST-exception"));
		} finally {
			// since ListedLicenses in a singleton, reset it after running this test
			ListedLicenses.resetListedLicenses();
		}
	}
}
