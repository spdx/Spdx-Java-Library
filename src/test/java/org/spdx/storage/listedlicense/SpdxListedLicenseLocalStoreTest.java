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
public class SpdxListedLicenseLocalStoreTest extends TestCase {

	private static final String APACHE_ID = "Apache-2.0";
	private static final String LICENSE_LIST_URI = "https://spdx.org/licenses/";
	private static final String LICENSE_LIST_VERSION = "3.7";
	private static final String APACHE_LICENSE_NAME = "Apache License 2.0";
	
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
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertTrue(slll.exists(LICENSE_LIST_URI, APACHE_ID));
		assertFalse(slll.exists(LICENSE_LIST_URI, "Unknown"));
		assertTrue(slll.exists(LICENSE_LIST_URI, ECOS_EXCEPTION_ID));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#create(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testCreate() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String nextId = slll.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		slll.create(LICENSE_LIST_URI, nextId, SpdxConstants.CLASS_SPDX_LISTED_LICENSE);
		String result = (String)slll.getValue(LICENSE_LIST_URI, nextId, SpdxConstants.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);
		
		nextId = slll.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		slll.create(LICENSE_LIST_URI, nextId, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION);
		result = (String)slll.getValue(LICENSE_LIST_URI, nextId, SpdxConstants.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)}.
	 */
	public void testGetNextId() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String nextId = slll.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		String nextNextId = slll.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		assertTrue(nextId.compareTo(nextNextId) < 0);
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getSpdxListedLicenseIds()}.
	 */
	public void testGetSpdxListedLicenseIds() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		List<String> result = slll.getSpdxListedLicenseIds();
		assertTrue(result.size() >= 373);
		assertTrue(result.contains(APACHE_ID));
	}
	
	public void testGetSpdxListedExceptionIds() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		List<String> result = slll.getSpdxListedExceptionIds();
		assertTrue(result.size() >= NUM_3_7_EXCEPTION);
		assertTrue(result.contains(ECOS_EXCEPTION_ID));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getLicenseListVersion()}.
	 */
	public void testGetLicenseListVersion() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertTrue(LICENSE_LIST_VERSION.compareTo(slll.getLicenseListVersion()) <= 0);
	}
	
	public void testGetValue() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String result = (String)slll.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		
		result = (String)slll.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
	}
	
	public void testSetValue() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String result = (String)slll.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		String newName = "new name";
		slll.setValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_NAME, newName);
		result = (String)slll.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(newName, result);
		
		result = (String)slll.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
		slll.setValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.PROP_NAME, newName);
		result = (String)slll.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.PROP_NAME).get();
		assertEquals(newName, result);
	}

	public void testCreateLicense() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		SpdxListedLicense result = (SpdxListedLicense)SpdxModelFactory.createModelObject(slll, LICENSE_LIST_URI, APACHE_ID, SpdxConstants.CLASS_SPDX_LISTED_LICENSE, null);
		assertEquals(APACHE_ID, result.getLicenseId());
		assertEquals(APACHE_LICENSE_NAME, result.getName());
		String licenseText = result.getLicenseText();
		assertTrue(licenseText.length() > 100);
		assertTrue(result.getComment().length() > 5);
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
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		LicenseException result = (LicenseException)SpdxModelFactory.createModelObject(slll, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION, null);
		assertEquals(ECOS_EXCEPTION_ID, result.getLicenseExceptionId());
		assertEquals(ECOS_EXCEPTION_ID, result.getId());
		assertTrue(result.getComment().length() > 5);
		assertEquals(LICENSE_LIST_URI, result.getDocumentUri());
		assertTrue(result.getLicenseExceptionText().length() > 100);
		assertEquals(ECOS_LICENSE_NAME, result.getName());
		List<String> lResult = new ArrayList<String>(result.getSeeAlso());
		assertTrue(lResult.size() > 0);
		assertTrue(lResult.get(0).length() > 10);
		assertEquals(SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION, (result.getType()));
		assertFalse(result.isDeprecated());
	}
	
	public void testList() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		// Exception
		LicenseException exception = (LicenseException)SpdxModelFactory.createModelObject(slll, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION, null);
		String seeAlso1 = "seeAlso1";
		String seeAlso2 = "seeAlso2";
		List<String> seeAlsos = Arrays.asList(new String[]{seeAlso1, seeAlso2});
		exception.setSeeAlso(seeAlsos);
		// getValueList
		List<Object> result = slll.getValueList(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO);
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(slll.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), slll.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		String seeAlso3 = "seeAlso3";
		assertFalse(slll.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(slll.addValueToCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(slll.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, slll.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(slll.removeValueFromCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(slll.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), slll.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		assertFalse(slll.removeValueFromCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		
		// License
		SpdxListedLicense license = (SpdxListedLicense)SpdxModelFactory.createModelObject(slll, LICENSE_LIST_URI, APACHE_ID, SpdxConstants.CLASS_SPDX_LISTED_LICENSE, null);
		license.setSeeAlso(seeAlsos);
		// getValueList
		result = slll.getValueList(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO);
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(slll.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), slll.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		assertFalse(slll.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(slll.addValueToCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(slll.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, slll.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(slll.removeValueFromCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(slll.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), slll.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		assertFalse(slll.removeValueFromCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlso3));
	}
	
	public void testIsCollectionMembersAssignableTo() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertTrue(slll.isCollectionMembersAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, String.class));
		assertFalse(slll.isCollectionMembersAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, Boolean.class));
		assertFalse(slll.isCollectionMembersAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_LICENSE_TEXT, String.class));
	}
	
	public void testIsPropertyValueAssignableTo() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertFalse(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO, String.class));
		assertTrue(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_LICENSE_TEXT, String.class));
		assertFalse(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_LICENSE_TEXT, Boolean.class));

		assertFalse(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_LIC_ID_DEPRECATED, String.class));
		assertTrue(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_LIC_ID_DEPRECATED, Boolean.class));
	}
	
	public void testIsCollectionProperty() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertTrue(slll.isCollectionProperty(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.RDFS_PROP_SEE_ALSO));
		assertFalse(slll.isCollectionProperty(LICENSE_LIST_URI, APACHE_ID, SpdxConstants.PROP_LIC_ID_DEPRECATED));
	}
	
	public void testDelete() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String nextId = slll.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		slll.create(LICENSE_LIST_URI, nextId, SpdxConstants.CLASS_SPDX_LISTED_LICENSE);
		String result = (String)slll.getValue(LICENSE_LIST_URI, nextId, SpdxConstants.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);
		assertTrue(slll.exists(LICENSE_LIST_URI, nextId));
		slll.delete(LICENSE_LIST_URI, nextId);
		assertFalse(slll.exists(LICENSE_LIST_URI, nextId));
		
		nextId = slll.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		slll.create(LICENSE_LIST_URI, nextId, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION);
		result = (String)slll.getValue(LICENSE_LIST_URI, nextId, SpdxConstants.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
		assertTrue(slll.exists(LICENSE_LIST_URI, nextId));
		slll.delete(LICENSE_LIST_URI, nextId);
		assertFalse(slll.exists(LICENSE_LIST_URI, nextId));
	}

}
