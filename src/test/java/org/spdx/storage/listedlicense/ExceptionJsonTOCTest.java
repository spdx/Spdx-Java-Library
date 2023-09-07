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
import java.util.Map;
import java.util.Objects;

import org.spdx.library.model.license.ListedLicenseException;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ExceptionJsonTOCTest extends TestCase {

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
	 * Test method for {@link org.spdx.storage.listedlicense.ExceptionJsonTOC#ExceptionJsonTOC(java.lang.String, java.lang.String)}.
	 */
	public void testExceptionJsonTOC() throws Exception {
		ExceptionJsonTOC ejt = new ExceptionJsonTOC();
		assertEquals(0, ejt.getExceptions().size());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.ExceptionJsonTOC#getLicenseListVersion()}.
	 */
	public void testGetLicenseListVersion() {
		ExceptionJsonTOC ejt = new ExceptionJsonTOC();
		assertTrue(Objects.isNull(ejt.getLicenseListVersion()));
		String version = "version";
		ejt.setLicenseListVersion(version);
		assertEquals(version, ejt.getLicenseListVersion());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.ExceptionJsonTOC#getReleaseDate()}.
	 */
	public void testGetReleaseDate() {
		ExceptionJsonTOC ejt = new ExceptionJsonTOC();
		assertTrue(Objects.isNull(ejt.getReleaseDate()));
		String date = "date";
		ejt.setReleaseDate(date);
		assertEquals(date, ejt.getReleaseDate());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.ExceptionJsonTOC#addException(org.spdx.library.model.license.ListedLicenseException, java.lang.String, java.lang.String, boolean)}.
	 */
	public void testAddException() throws Exception {
		ExceptionJsonTOC ejt = new ExceptionJsonTOC();
		InMemSpdxStore store = new InMemSpdxStore();
		String docUri = "http://temp.doc.uri";
		String detailsUrl1 = "./details1";
		String exceptionId1 = "id1";
		boolean deprecated1 = true;
		String name1 = "name1";
		String reference1 = "./reference1";
		List<String> seeAlso1 = Arrays.asList(new String[]{"http://seeAlso1", "http://seeAlso2", "http://seeAlso3"});
		String detailsUrl2 = "./details2";
		boolean deprecated2 = false;
		String exceptionId2 = "id2";
		String name2 = "name2";
		String reference2 = "./reference2";
		List<String> seeAlso2 = Arrays.asList(new String[]{"http://seeAlso4"});
		
		ListedLicenseException ex1 = new ListedLicenseException(store, docUri, exceptionId1, null, true);
		ex1.setName(name1);
		ex1.setSeeAlso(seeAlso1);
		ejt.addException(ex1, reference1, detailsUrl1, deprecated1);
		
		ListedLicenseException ex2 = new ListedLicenseException(store, docUri, exceptionId2, null, true);
		ex2.setName(name2);
		ex2.setSeeAlso(seeAlso2);
		ejt.addException(ex2, reference2, detailsUrl2, deprecated2);
		
		List<ExceptionJsonTOC.ExceptionJson> result = ejt.getExceptions();
		assertEquals(2, result.size());
		assertEquals(deprecated1, result.get(0).isDeprecatedLicenseId());
		assertEquals(detailsUrl1.substring(2), result.get(0).getDetailsUrl().substring(result.get(0).getDetailsUrl().lastIndexOf('/') + 1));
		assertEquals(exceptionId1, result.get(0).getLicenseExceptionId());
		assertEquals(name1, result.get(0).getName());
		assertEquals(reference1.substring(2), result.get(0).getReference().substring(result.get(0).getReference().lastIndexOf('/') + 1));
		assertEquals(1, result.get(0).getReferenceNumber());
		assertTrue(UnitTestHelper.isListsEqual(seeAlso1, result.get(0).getSeeAlso()));
		
		assertEquals(deprecated2, result.get(1).isDeprecatedLicenseId());
		assertEquals(detailsUrl2.substring(2), result.get(1).getDetailsUrl().substring(result.get(1).getDetailsUrl().lastIndexOf('/') + 1));
		assertEquals(exceptionId2, result.get(1).getLicenseExceptionId());
		assertEquals(name2, result.get(1).getName());
		assertEquals(reference2.substring(2), result.get(1).getReference().substring(result.get(1).getReference().lastIndexOf('/') + 1));
		assertEquals(2, result.get(1).getReferenceNumber());
		assertTrue(UnitTestHelper.isListsEqual(seeAlso2, result.get(1).getSeeAlso()));
		
		Map<String, String> exceptionIds = ejt.getExceptionIds();
		assertEquals(2, exceptionIds.size());
		assertEquals(exceptionId1, exceptionIds.get(exceptionId1.toLowerCase()));
		assertEquals(exceptionId2, exceptionIds.get(exceptionId2.toLowerCase()));
	}

}
