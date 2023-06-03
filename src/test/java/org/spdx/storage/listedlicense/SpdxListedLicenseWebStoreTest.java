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
import java.util.Iterator;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.model.compat.v2.SpdxModelFactory;
import org.spdx.library.model.compat.v2.TypedValue;
import org.spdx.library.model.compat.v2.license.CrossRef;
import org.spdx.library.model.compat.v2.license.LicenseException;
import org.spdx.library.model.compat.v2.license.ListedLicenseException;
import org.spdx.library.model.compat.v2.license.SpdxListedLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SpdxListedLicenseWebStoreTest extends TestCase {

	private static final String APACHE_ID = "Apache-2.0";
	private static final String LICENSE_LIST_URI = "https://spdx.org/licenses/";
	private static final String LICENSE_LIST_VERSION = "3.17";
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
	public void testExists() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		assertTrue(sllw.exists(LICENSE_LIST_URI, APACHE_ID));
		assertFalse(sllw.exists(LICENSE_LIST_URI, "Unknown"));
		assertTrue(sllw.exists(LICENSE_LIST_URI, ECOS_EXCEPTION_ID));
		sllw.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#create(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testCreate() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE);
		String result = (String)sllw.getValue(LICENSE_LIST_URI, nextId, SpdxConstantsCompatV2.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);

		nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION);
		result = (String)sllw.getValue(LICENSE_LIST_URI, nextId, SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
		sllw.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)}.
	 */
	public void testGetNextId() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		String nextNextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		assertTrue(nextId.compareTo(nextNextId) < 0);
		sllw.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getSpdxListedLicenseIds()}.
	 */
	public void testGetSpdxListedLicenseIds() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		List<String> result = sllw.getSpdxListedLicenseIds();
		assertTrue(result.size() >= NUM_3_7_LICENSES);
		assertTrue(result.contains(APACHE_ID));
		sllw.close();
	}
	
	public void testGetSpdxListedExceptionIds() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		List<String> result = sllw.getSpdxListedExceptionIds();
		assertTrue(result.size() >= NUM_3_7_EXCEPTION);
		assertTrue(result.contains(ECOS_EXCEPTION_ID));
		sllw.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getLicenseListVersion()}.
	 */
	public void testGetLicenseListVersion() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		assertTrue(compareVersionStrings(LICENSE_LIST_VERSION,sllw.getLicenseListVersion()) <= 0);
		sllw.close();
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
	
	public void testGetValue() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String result = (String)sllw.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		
		result = (String)sllw.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
		sllw.close();
	}
	
	public void testSetValue() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String result = (String)sllw.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		String newName = "new name";
		sllw.setValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_NAME, newName);
		result = (String)sllw.getValue(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(newName, result);
		
		result = (String)sllw.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
		sllw.setValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME, newName);
		result = (String)sllw.getValue(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(newName, result);
		sllw.close();
	}

	public void testCreateLicense() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		SpdxListedLicense result = (SpdxListedLicense)SpdxModelFactory.createModelObject(sllw, LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, null);
		assertEquals(APACHE_ID, result.getLicenseId());
		assertEquals(APACHE_LICENSE_NAME, result.getName());
		String licenseText = result.getLicenseText();
		assertTrue(licenseText.length() > 100);
		assertTrue(result.getComment().length() > 5);
		result.getDeprecatedVersion();
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
		assertEquals(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, (result.getType()));
	}
	
	@SuppressWarnings("deprecation")
	public void testCreateException() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		LicenseException result = (LicenseException)SpdxModelFactory.createModelObject(sllw, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, null);
		assertEquals(ECOS_EXCEPTION_ID, result.getLicenseExceptionId());
		assertEquals(ECOS_EXCEPTION_ID, result.getId());
		assertTrue(result.getComment().length() > 5);
		result.getDeprecatedVersion();
		assertEquals(LICENSE_LIST_URI, result.getDocumentUri());
		result.getExample();
		result.getLicenseExceptionTemplate();
		assertTrue(result.getLicenseExceptionText().length() > 100);
		assertEquals(ECOS_LICENSE_NAME, result.getName());
		List<String> lResult = new ArrayList<String>(result.getSeeAlso());
		assertTrue(lResult.size() > 0);
		assertTrue(lResult.get(0).length() > 10);
		assertEquals(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, (result.getType()));
		assertFalse(result.isDeprecated());
	}
	
	@SuppressWarnings("unchecked")
	public void testList() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		// Exception
		ListedLicenseException exception = (ListedLicenseException)SpdxModelFactory.createModelObject(sllw, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, null);
		String seeAlso1 = "seeAlso1";
		String seeAlso2 = "seeAlso2";
		List<String> seeAlsos = Arrays.asList(new String[]{seeAlso1, seeAlso2});
		exception.setSeeAlso(seeAlsos);
		// getValueList
		List<Object> result = new ArrayList<>();
		Iterator<Object> resultIter = sllw.listValues(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		while (resultIter.hasNext()) {
			result.add(resultIter.next());
		}
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(sllw.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), sllw.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		String seeAlso3 = "seeAlso3";
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(sllw.addValueToCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(sllw.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, sllw.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(sllw.removeValueFromCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), sllw.collectionSize(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(sllw.removeValueFromCollection(LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		
		// License
		ModelCopyManager copyManager = new ModelCopyManager();
		SpdxListedLicense license = (SpdxListedLicense)SpdxModelFactory.createModelObject(sllw, LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, copyManager);
		license.setSeeAlso(seeAlsos);
		// getValueList
		result.clear();
		resultIter = sllw.listValues(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		while (resultIter.hasNext()) {
			result.add(resultIter.next());
		}
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(sllw.addValueToCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(sllw.removeValueFromCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(sllw.removeValueFromCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		
		// license crossRefs
		license.getCrossRef().clear();
		assertEquals(0, sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
		IModelStore simpleModelStore = new InMemSpdxStore();
		String docUri = "http://some.other.doc";
		CrossRef crossRef1 = new CrossRef(simpleModelStore, docUri, simpleModelStore.getNextId(IdType.Anonymous, docUri), copyManager, true);
		crossRef1.setUrl("http://url1");
		CrossRef crossRef2 = new CrossRef(simpleModelStore, docUri, simpleModelStore.getNextId(IdType.Anonymous, docUri), copyManager, true);
		crossRef2.setUrl("http://url2");
		List<CrossRef> crossRefs = Arrays.asList(new CrossRef[]{crossRef1, crossRef2});
		license.getCrossRef().add(crossRef1);
		license.getCrossRef().add(crossRef2);
		result.clear();
		resultIter = sllw.listValues(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF);
		List<TypedValue> tvResult = new ArrayList<>();
		while (resultIter.hasNext()) {
			TypedValue tv = (TypedValue)resultIter.next();
			tvResult.add(tv);
			result.add(new CrossRef(sllw, LICENSE_LIST_URI, tv.getId(), copyManager, false));
		}
		List<CrossRef> result2 = (List<CrossRef>)(List<?>)Arrays.asList(license.getCrossRef().toArray());
		assertEquals(2, result.size());
		assertEquals(2, result2.size());
		assertEquals(2, sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
		assertTrue(UnitTestHelper.isListsEquivalent(crossRefs, (List<CrossRef>)(List<?>)result));
		assertTrue(UnitTestHelper.isListsEquivalent(crossRefs, result2));
		for (TypedValue tv:tvResult) {
			assertTrue(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, tv));
		}
		for (CrossRef crossRef:crossRefs) {
			// collectionContains
			assertTrue(license.getCrossRef().contains(crossRef));
		}
		CrossRef crossRef3 = new CrossRef(simpleModelStore, docUri, simpleModelStore.getNextId(IdType.Anonymous, docUri), copyManager, true);
		crossRef3.setUrl("http://url3");
		String newCrossRefId = sllw.getNextId(IdType.Anonymous, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, newCrossRefId, SpdxConstantsCompatV2.CLASS_CROSS_REF);
		sllw.setValue(LICENSE_LIST_URI, newCrossRefId, SpdxConstantsCompatV2.PROP_CROSS_REF_URL, "http://url3");
		TypedValue newCrossRefTv = new TypedValue(newCrossRefId, SpdxConstantsCompatV2.CLASS_CROSS_REF);
		sllw.addValueToCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv);
		assertEquals(3, sllw.collectionSize(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
		assertTrue(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv));
		boolean found = false;
		for (CrossRef cr:license.getCrossRef()) {
			if (cr.equivalent(crossRef3)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
		sllw.removeValueFromCollection(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv);
		assertFalse(sllw.collectionContains(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv));
		found = false;
		for (CrossRef cr:license.getCrossRef()) {
			if (cr.equivalent(crossRef3)) {
				found = true;
				break;
			}
		}
		assertFalse(found);
		sllw.close();
	}
	
	public void testIsCollectionProperty() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		assertTrue(sllw.isCollectionProperty(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(sllw.isCollectionProperty(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED));
		assertTrue(sllw.isCollectionMembersAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, CrossRef.class));
		assertFalse(sllw.isCollectionMembersAssignableTo(LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, String.class));
		sllw.close();
	}
	
	public void testDelete() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE);
		String result = (String)sllw.getValue(LICENSE_LIST_URI, nextId, SpdxConstantsCompatV2.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);
		assertTrue(sllw.exists(LICENSE_LIST_URI, nextId));
		sllw.delete(LICENSE_LIST_URI, nextId);
		assertFalse(sllw.exists(LICENSE_LIST_URI, nextId));
		
		nextId = sllw.getNextId(IdType.ListedLicense, LICENSE_LIST_URI);
		sllw.create(LICENSE_LIST_URI, nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION);
		result = (String)sllw.getValue(LICENSE_LIST_URI, nextId, SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
		assertTrue(sllw.exists(LICENSE_LIST_URI, nextId));
		sllw.delete(LICENSE_LIST_URI, nextId);
		assertFalse(sllw.exists(LICENSE_LIST_URI, nextId));
		sllw.close();
	}
}
