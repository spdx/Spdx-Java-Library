package org.spdx.library;
import java.util.List;
import java.util.Optional;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.LicenseException;
import org.spdx.library.model.v2.license.ListedLicenseException;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.storage.CompatibleModelStoreWrapper;

import junit.framework.TestCase;

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
		SpdxModelFactory.init();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.license.ListedLicenses#isSpdxListedLicenseId(java.lang.String)}.
	 */
	public void testIsSpdxListedLicenseID() {
		assertTrue(ListedLicenses.getListedLicenses().isSpdxListedLicenseId("Apache-2.0"));
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.license.ListedLicenses#getListedLicenseById(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetListedLicenseById() throws InvalidSPDXAnalysisException {
		String id = "Apache-2.0";
		SpdxListedLicense result = ListedLicenses.getListedLicenses().getListedLicenseByIdCompatV2(id);
		assertEquals(id, result.getLicenseId());
	}

	public void testGetLicenseIbyIdLocal() throws InvalidSPDXAnalysisException {
		System.setProperty("SPDXParser.OnlyUseLocalLicenses", "true");
		ListedLicenses.resetListedLicenses();
		try {
			String id = "Apache-2.0";
			SpdxListedLicense result = ListedLicenses.getListedLicenses().getListedLicenseByIdCompatV2(id);
			assertEquals(id, result.getLicenseId());
		} finally {
			System.setProperty("SPDXParser.OnlyUseLocalLicenses", "false");
			ListedLicenses.resetListedLicenses();
		}
	}
	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.license.ListedLicenses#getSpdxListedLicenseIds()}.
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
		LicenseException result = ListedLicenses.getListedLicenses().getListedExceptionByIdCompatV2(id);
		assertEquals(id, result.getLicenseExceptionId());
	}
	
	public void testGetExceptionbyIdLocal() throws InvalidSPDXAnalysisException {
		System.setProperty("SPDXParser.OnlyUseLocalLicenses", "true");
		ListedLicenses.resetListedLicenses();
		try {
			String id = "Classpath-exception-2.0";
			assertTrue(ListedLicenses.getListedLicenses().isSpdxListedExceptionId(id));
			LicenseException result = ListedLicenses.getListedLicenses().getListedExceptionByIdCompatV2(id);
			assertEquals(id, result.getLicenseExceptionId());
		} finally {
			System.setProperty("SPDXParser.OnlyUseLocalLicenses", "false");
			ListedLicenses.resetListedLicenses();
		}
	}
	
	public void testGetExceptionIds() throws InvalidSPDXAnalysisException {
		assertTrue(ListedLicenses.getListedLicenses().getSpdxListedExceptionIds().size() >= NUM_3_7_EXCEPTION);
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
	    SpdxListedLicense lic = ListedLicenses.getListedLicenses().getListedLicenseByIdCompatV2(id);
	    Optional<Object> idProp = lic.getModelStore().getValue(
	    		CompatibleModelStoreWrapper.documentUriIdToUri(lic.getDocumentUri(), id, false), SpdxConstantsCompatV2.PROP_LICENSE_ID);
	    assertTrue(idProp.isPresent());
	    assertTrue(idProp.get() instanceof String);
	    assertEquals(id, idProp.get());
	}
	
	   public void testGetExceptionIdProperty() throws InvalidSPDXAnalysisException {
	        String id = "Classpath-exception-2.0";
	        ListedLicenseException ex = ListedLicenses.getListedLicenses().getListedExceptionByIdCompatV2(id);
	        Optional<Object> idProp = ex.getModelStore().getValue(
	        		CompatibleModelStoreWrapper.documentUriIdToUri(ex.getDocumentUri(), id, false), SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID);
	        assertTrue(idProp.isPresent());
	        assertTrue(idProp.get() instanceof String);
	        assertEquals(id, idProp.get());
	    }
}