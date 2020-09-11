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
package org.spdx.storage.listedlicense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.license.LicenseException;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.licenseTemplate.InvalidLicenseTemplateException;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SpdxListedLicenseWebStoreTest extends TestCase {

	private static final String APACHE_ID = "Apache-2.0";
	private static final String LICENSE_LIST_URI = "https://spdx.org/licenses/";
	private static final String LICENSE_LIST_VERSION = "3.7";
	private static final String APACHE_LICENSE_NAME = "Apache License 2.0";
	private static final int NUM_3_7_LICENSES = 373;
	
	private static final String ECOS_EXCEPTION_ID = "eCos-exception-2.0";
	private static final int NUM_3_7_EXCEPTION = 36;
	private static final String ECOS_LICENSE_NAME = "eCos exception 2.0";

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
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#exists(java.lang.String, java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testExists() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		assertTrue(sllw.exists(LICENSE_LIST_URI, APACHE_ID));
		assertFalse(sllw.exists(LICENSE_LIST_URI, "Unknown"));
		assertTrue(sllw.exists(LICENSE_LIST_URI, ECOS_EXCEPTION_ID));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#create(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testCreate() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, nextId, SpdxConstants.CLASS_SPDX_LISTED_LICENSE);
		String result = (String)sllw.getValue(LICENSE_LIST_URI, nextId, SpdxConstants.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);

		nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, nextId, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION);
		result = (String)sllw.getValue(LICENSE_LIST_URI, nextId, SpdxConstants.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)}.
	 */
	public void testGetNextId() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		String nextNextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		assertTrue(nextId.compareTo(nextNextId) < 0);
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getSpdxListedLicenseIds()}.
	 */
	public void testGetSpdxListedLicenseIds() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		List<String> result = sllw.getSpdxListedLicenseIds();
		assertTrue(result.size() >= NUM_3_7_LICENSES);
		assertTrue(result.contains(APACHE_ID));
	}
	
	public void testGetSpdxListedExceptionIds() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		List<String> result = sllw.getSpdxListedExceptionIds();
		assertTrue(result.size() >= NUM_3_7_EXCEPTION);
		assertTrue(result.contains(ECOS_EXCEPTION_ID));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getLicenseListVersion()}.
	 */
	public void testGetLicenseListVersion() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		assertTrue(compareVersionStrings(LICENSE_LIST_VERSION,sllw.getLicenseListVersion()) <= 0);
	}
	
	int compareVersionStrings(String versionA, String versionB) {
		String[] versionPartsA = versionA.split("\\.");
		String[] versionPartsB = versionB.split("\\.");
		// loop through all the parts and return immediately if there is a non-equal result
		for (int i = 0; i < versionPartsA.length; i++) {
			int result = 0;
			if (versionPartsB.length < i-1) {
				return 1;	// versionA > versionB since everything else is equal and it has one more part
			}
			try {
				result = Integer.compare(Integer.parseInt(versionPartsA[i].trim()), Integer.parseInt(versionPartsB[i].trim()));

			} catch(NumberFormatException ex) {
				// compare using text
				result = versionPartsA[i].trim().compareTo(versionPartsB[i].trim());
			}
			if (result != 0) {
				return result;
			}
		}
		if (versionPartsB.length > versionPartsA.length) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public void testGetValue() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String result = (String)sllw.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		
		result = (String)sllw.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
	}
	
	public void testSetValue() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String result = (String)sllw.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		String newName = "new name";
		sllw.setValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_NAME, newName);
		result = (String)sllw.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(newName, result);
		
		result = (String)sllw.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
		sllw.setValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.PROP_NAME, newName);
		result = (String)sllw.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(newName, result);
	}

	public void testCreateLicense() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		SpdxListedLicense result = (SpdxListedLicense)SpdxModelFactory.createModelObject(sllw, LICENSE_LIST_URI, APACHE_ID, SpdxConstants.CLASS_SPDX_LISTED_LICENSE, null);
		assertEquals(APACHE_ID, result.getLicenseId());
		assertEquals(APACHE_LICENSE_NAME, result.getName());
		String licenseText = result.getLicenseText();
		assertTrue(licenseText.length() > 100);
		assertTrue(result.getComment().length() > 5);
		String sResult = result.getDeprecatedVersion();
		assertEquals(LICENSE_LIST_URI, result.getDocumentUri());
		assertTrue(result.getFsfLibre());
		assertFalse(result.isDeprecated());
		assertTrue(result.isFsfLibre());
		assertFalse(result.isNotFsfLibre());
		assertTrue(result.isOsiApproved());
		assertEquals(APACHE_ID, result.getId());
		assertTrue(result.getLicenseHeaderHtml().length() > 100);
		assertTrue(result.getLicenseTextHtml().length() > 100);
		List<String> lResult = new ArrayList<String>(result.getSeeAlso());
		assertTrue(lResult.size() > 0);
		assertTrue(lResult.get(0).length() > 10);
		assertTrue(result.getStandardLicenseHeader().length() > 100);
		assertEquals(SpdxConstants.CLASS_SPDX_LISTED_LICENSE, (result.getType()));
	}
	
	public void testCreateException() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		LicenseException result = (LicenseException)SpdxModelFactory.createModelObject(sllw, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION, null);
		assertEquals(ECOS_EXCEPTION_ID, result.getLicenseExceptionId());
		assertEquals(ECOS_EXCEPTION_ID, result.getId());
		assertTrue(result.getComment().length() > 5);
		String sResult = result.getDeprecatedVersion();
		assertEquals(LICENSE_LIST_URI, result.getDocumentUri());
		sResult = result.getExample();
		sResult = result.getLicenseExceptionTemplate();
		assertTrue(result.getLicenseExceptionText().length() > 100);
		assertEquals(ECOS_LICENSE_NAME, result.getName());
		List<String> lResult = new ArrayList<String>(result.getSeeAlso());
		assertTrue(lResult.size() > 0);
		assertTrue(lResult.get(0).length() > 10);
		assertEquals(SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION, (result.getType()));
		assertFalse(result.isDeprecated());
	}
	
	public void testList() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		// Exception
		LicenseException exception = (LicenseException)SpdxModelFactory.createModelObject(sllw, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION, null);
		String seeAlso1 = "seeAlso1";
		String seeAlso2 = "seeAlso2";
		List<String> seeAlsos = Arrays.asList(new String[]{seeAlso1, seeAlso2});
		exception.setSeeAlso(seeAlsos);
		// getValueList
		List<Object> result = sllw.getValueList(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO);
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(sllw.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), sllw.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		String seeAlso3 = "seeAlso3";
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(sllw.addValueToCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(sllw.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, sllw.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(sllw.removeValueFromCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), sllw.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		assertFalse(sllw.removeValueFromCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		
		// License
		SpdxListedLicense license = (SpdxListedLicense)SpdxModelFactory.createModelObject(sllw, LICENSE_LIST_URI, APACHE_ID, SpdxConstants.CLASS_SPDX_LISTED_LICENSE, null);
		license.setSeeAlso(seeAlsos);
		// getValueList
		result = sllw.getValueList(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO);
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(sllw.addValueToCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(sllw.removeValueFromCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		assertFalse(sllw.removeValueFromCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
	}
	
	public void testIsCollectionProperty() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		assertTrue(sllw.isCollectionProperty(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		assertFalse(sllw.isCollectionProperty(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_LIC_ID_DEPRECATED));
	}
	
	public void testDelete() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, nextId, SpdxConstants.CLASS_SPDX_LISTED_LICENSE);
		String result = (String)sllw.getValue(LICENSE_LIST_URI, nextId, SpdxConstants.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);
		assertTrue(sllw.exists(LICENSE_LIST_URI, nextId));
		sllw.delete(LICENSE_LIST_URI, nextId);
		assertFalse(sllw.exists(LICENSE_LIST_URI, nextId));
		
		nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, nextId, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION);
		result = (String)sllw.getValue(LICENSE_LIST_URI, nextId, SpdxConstants.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
		assertTrue(sllw.exists(LICENSE_LIST_URI, nextId));
		sllw.delete(LICENSE_LIST_URI, nextId);
		assertFalse(sllw.exists(LICENSE_LIST_URI, nextId));
	}
}
