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
package org.spdx.storage.listedlicense;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

/**
 * @author Gary O'Neall
 *
 */
public class LicenseJsonTOCTest extends TestCase {

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
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJsonTOC#LicenseJsonTOC(java.lang.String, java.lang.String)}.
	 */
	public void testLicenseJsonTOC() {
		LicenseJsonTOC ljt = new LicenseJsonTOC();
		assertTrue(Objects.isNull(ljt.getLicenseListVersion()));
		assertTrue(Objects.isNull(ljt.getReleaseDate()));
		assertEquals(0, ljt.getLicenses().size());
		assertEquals(0, ljt.getLicenseIds().size());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJsonTOC#getLicenseListVersion()}.
	 */
	public void testGetLicenseListVersion() {
		LicenseJsonTOC ljt = new LicenseJsonTOC();
		String version = "version";
		ljt.setLicenseListVersion(version);
		assertEquals(version, ljt.getLicenseListVersion());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJsonTOC#getReleaseDate()}.
	 */
	public void testGetReleaseDate() {
		LicenseJsonTOC ljt = new LicenseJsonTOC();
		String date = "date";
		ljt.setReleaseDate(date);
		assertEquals(date, ljt.getReleaseDate());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJsonTOC#addLicense(org.spdx.library.model.license.SpdxListedLicense, java.lang.String, java.lang.String, boolean)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testAddLicense() throws InvalidSPDXAnalysisException {
		LicenseJsonTOC ljt = new LicenseJsonTOC();
		InMemSpdxStore store = new InMemSpdxStore();
		String docUri = "http://docuri.temp1";
		String licenseId1 = "licenseId1";
		String licHTMLReference1 = "licHTMLReference1";
		String licJSONReference1 = "licJSONReference1";
		boolean deprecated1 = true;
		String name1 = "name1";
		List<String> seeAlso1 = Arrays.asList(new String[]{"http://url1", "http://url2"});

		String licenseId2 = "licenseId2";
		String licHTMLReference2 = "licHTMLReference2";
		String licJSONReference2 = "licJSONReference2";
		boolean deprecated2 = false;
		String name2 = "name1";
		List<String> seeAlso2 = Arrays.asList(new String[]{"http://url3", "http://url2"});
		SpdxListedLicense license1 = new SpdxListedLicense(store, docUri, licenseId1, null, true);
		license1.setName(name1);
		license1.setSeeAlso(seeAlso1);
		ljt.addLicense(license1, licHTMLReference1, licJSONReference1, deprecated1);
		SpdxListedLicense license2 = new SpdxListedLicense(store, docUri, licenseId2, null, true);
		license2.setName(name2);
		license2.setSeeAlso(seeAlso2);
		ljt.addLicense(license2, licHTMLReference2, licJSONReference2, deprecated2);
		assertEquals(2, ljt.getLicenses().size());
		assertEquals(licHTMLReference1, ljt.getLicenses().get(0).getDetailsUrl());
		assertEquals(licenseId1, ljt.getLicenses().get(0).getLicenseId());
		assertEquals(name1, ljt.getLicenses().get(0).getName());
		assertEquals(licJSONReference1, ljt.getLicenses().get(0).getReference());
		assertEquals(0, ljt.getLicenses().get(0).getReferenceNumber());
		assertTrue(UnitTestHelper.isListsEqual(seeAlso1, ljt.getLicenses().get(0).getSeeAlso()));
		
		assertEquals(licHTMLReference2, ljt.getLicenses().get(1).getDetailsUrl());
		assertEquals(licenseId2, ljt.getLicenses().get(1).getLicenseId());
		assertEquals(name2, ljt.getLicenses().get(1).getName());
		assertEquals(licJSONReference2, ljt.getLicenses().get(1).getReference());
		assertEquals(1, ljt.getLicenses().get(1).getReferenceNumber());
		assertTrue(UnitTestHelper.isListsEqual(seeAlso2, ljt.getLicenses().get(1).getSeeAlso()));
	}

}
