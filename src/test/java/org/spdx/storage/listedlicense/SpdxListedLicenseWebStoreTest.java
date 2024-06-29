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

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.TypedValue;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.CrossRef;
import org.spdx.library.model.v2.license.LicenseException;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.core.Agent;
import org.spdx.library.model.v3.core.CreationInfo;
import org.spdx.library.model.v3.expandedlicensing.ListedLicense;
import org.spdx.library.model.v3.expandedlicensing.ListedLicenseException;
import org.spdx.licenseTemplate.InvalidLicenseTemplateException;
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
	private static final String LICENSE_LIST_URI = "http://spdx.org/licenses/";
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
		SpdxModelFactory.init();
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
		assertTrue(sllw.exists(LICENSE_LIST_URI + APACHE_ID));
		assertFalse(sllw.exists(LICENSE_LIST_URI + "Unknown"));
		assertTrue(sllw.exists(LICENSE_LIST_URI + ECOS_EXCEPTION_ID));
		sllw.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#create(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testCreate() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense);
		sllw.create(new TypedValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, "SPDX-2.3"));
		String result = (String)sllw.getValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);

		nextId = sllw.getNextId(IdType.ListedLicense);
		sllw.create(new TypedValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, "SPDX-2.3"));
		result = (String)sllw.getValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
		sllw.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)}.
	 */
	public void testGetNextId() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense);
		String nextNextId = sllw.getNextId(IdType.ListedLicense);
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
		String result = (String)sllw.getValue(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		
		result = (String)sllw.getValue(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
		sllw.close();
	}
	
	public void testSetValue() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String result = (String)sllw.getValue(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		String newName = "new name";
		sllw.setValue(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_NAME, newName);
		result = (String)sllw.getValue(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(newName, result);
		
		result = (String)sllw.getValue(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
		sllw.setValue(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME, newName);
		result = (String)sllw.getValue(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(newName, result);
		sllw.close();
	}

	public void testCreateLicenseV2() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		SpdxV2ListedLicenseModelStore modelStore = new SpdxV2ListedLicenseModelStore(sllw);
		SpdxListedLicense result = (SpdxListedLicense)org.spdx.library.model.v2.SpdxModelFactoryCompatV2.createModelObjectV2(modelStore, LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, null);
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
		assertEquals(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, (result.getType()));
		assertTrue(result.verify().isEmpty());
	}
	
	public void testCreateLicenseV3() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		SpdxV3ListedLicenseModelStore modelStore = new SpdxV3ListedLicenseModelStore(sllw);
		ListedLicense result = new ListedLicense(modelStore, LICENSE_LIST_URI + APACHE_ID, null, true, null);
		assertEquals(LICENSE_LIST_URI + APACHE_ID, result.getObjectUri());
		assertEquals(APACHE_LICENSE_NAME, result.getName().get());
		String licenseText = result.getLicenseText();
		assertTrue(licenseText.length() > 100);
		assertTrue(result.getComment().get().length() > 5);
		assertTrue(result.getIsFsfLibre().get());
		assertFalse(result.getIsDeprecatedLicenseId().get());
		assertTrue(result.getIsOsiApproved().get());
		List<String> lResult = new ArrayList<String>(result.getSeeAlsos());
		assertTrue(lResult.size() > 0);
		assertTrue(lResult.get(0).length() > 10);
		assertTrue(result.getStandardLicenseHeader().get().length() > 100);
		assertEquals(SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE, (result.getType()));
		assertTrue(result.verify().isEmpty());
	}
	
	public void testCreateExceptionV2() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		SpdxV2ListedLicenseModelStore v2store = new SpdxV2ListedLicenseModelStore(sllw);
		LicenseException result = (LicenseException)org.spdx.library.model.v2.SpdxModelFactoryCompatV2.createModelObjectV2(v2store, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, null);
		assertEquals(ECOS_EXCEPTION_ID, result.getLicenseExceptionId());
		assertEquals(ECOS_EXCEPTION_ID, result.getId());
		assertTrue(result.getComment().length() > 5);
		assertEquals(LICENSE_LIST_URI, result.getDocumentUri());
		assertTrue(result.getLicenseExceptionText().length() > 100);
		assertEquals(ECOS_LICENSE_NAME, result.getName());
		List<String> lResult = new ArrayList<String>(result.getSeeAlso());
		assertTrue(lResult.size() > 0);
		assertTrue(lResult.get(0).length() > 10);
		assertEquals(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, (result.getType()));
		assertFalse(result.isDeprecated());
		assertTrue(result.verify().isEmpty());
	}
	
	public void testCreateExceptionV3() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		SpdxV3ListedLicenseModelStore v3store = new SpdxV3ListedLicenseModelStore(sllw);
		ListedLicenseException result = new ListedLicenseException(v3store, LICENSE_LIST_URI+ECOS_EXCEPTION_ID, null, true, null);
		assertEquals(LICENSE_LIST_URI+ECOS_EXCEPTION_ID, result.getObjectUri());
		assertTrue(result.getComment().get().length() > 5);
		assertTrue(result.getAdditionText().length() > 100);
		assertEquals(ECOS_LICENSE_NAME, result.getName().get());
		List<String> lResult = new ArrayList<String>(result.getSeeAlsos());
		assertTrue(lResult.size() > 0);
		assertTrue(lResult.get(0).length() > 10);
		assertEquals(SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION, (result.getType()));
		assertFalse(result.getIsDeprecatedAdditionId().get());
		assertTrue(result.verify().isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testList() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		SpdxV2ListedLicenseModelStore v2store = new SpdxV2ListedLicenseModelStore(sllw);
		// Exception
		org.spdx.library.model.v2.license.ListedLicenseException exception = (org.spdx.library.model.v2.license.ListedLicenseException)org.spdx.library.model.v2.SpdxModelFactoryCompatV2.createModelObjectV2(v2store, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, null);
		String seeAlso1 = "seeAlso1";
		String seeAlso2 = "seeAlso2";
		List<String> seeAlsos = Arrays.asList(new String[]{seeAlso1, seeAlso2});
		exception.setSeeAlso(seeAlsos);
		// getValueList
		List<Object> result = new ArrayList<>();
		Iterator<Object> resultIter = v2store.listValues(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		while (resultIter.hasNext()) {
			result.add(resultIter.next());
		}
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(v2store.collectionContains(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), v2store.collectionSize(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		String seeAlso3 = "seeAlso3";
		assertFalse(v2store.collectionContains(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(v2store.addValueToCollection(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(v2store.collectionContains(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, v2store.collectionSize(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(v2store.removeValueFromCollection(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(v2store.collectionContains(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), v2store.collectionSize(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(v2store.removeValueFromCollection(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		
		// License
		ModelCopyManager copyManager = new ModelCopyManager();
		SpdxListedLicense license = (SpdxListedLicense)org.spdx.library.model.v2.SpdxModelFactoryCompatV2.createModelObjectV2(v2store, LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, copyManager);
		license.setSeeAlso(seeAlsos);
		// getValueList
		result.clear();
		resultIter = v2store.listValues(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		while (resultIter.hasNext()) {
			result.add(resultIter.next());
		}
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(v2store.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), v2store.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		assertFalse(v2store.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(v2store.addValueToCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(v2store.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, v2store.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(v2store.removeValueFromCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(v2store.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), v2store.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(v2store.removeValueFromCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		
		// license crossRefs
		license.getCrossRef().clear();
		assertEquals(0, v2store.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
		IModelStore simpleModelStore = new InMemSpdxStore();
		String docUri = "http://some.other.doc";
		CrossRef crossRef1 = new CrossRef(simpleModelStore, docUri, simpleModelStore.getNextId(IdType.Anonymous), copyManager, true);
		crossRef1.setUrl("http://url1");
		CrossRef crossRef2 = new CrossRef(simpleModelStore, docUri, simpleModelStore.getNextId(IdType.Anonymous), copyManager, true);
		crossRef2.setUrl("http://url2");
		List<CrossRef> crossRefs = Arrays.asList(new CrossRef[]{crossRef1, crossRef2});
		license.getCrossRef().add(crossRef1);
		license.getCrossRef().add(crossRef2);
		result.clear();
		resultIter = v2store.listValues(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF);
		List<TypedValue> tvResult = new ArrayList<>();
		while (resultIter.hasNext()) {
			TypedValue tv = (TypedValue)resultIter.next();
			tvResult.add(tv);
			result.add(new CrossRef(v2store, LICENSE_LIST_URI, tv.getObjectUri(), copyManager, false));
		}
		List<CrossRef> result2 = (List<CrossRef>)(List<?>)Arrays.asList(license.getCrossRef().toArray());
		assertEquals(2, result.size());
		assertEquals(2, result2.size());
		assertEquals(2, v2store.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
		assertTrue(UnitTestHelper.isListsEquivalent(crossRefs, (List<CrossRef>)(List<?>)result));
		assertTrue(UnitTestHelper.isListsEquivalent(crossRefs, result2));
		for (TypedValue tv:tvResult) {
			assertTrue(v2store.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, tv));
		}
		for (CrossRef crossRef:crossRefs) {
			// collectionContains
			assertTrue(license.getCrossRef().contains(crossRef));
		}
		CrossRef crossRef3 = new CrossRef(simpleModelStore, docUri, simpleModelStore.getNextId(IdType.Anonymous), copyManager, true);
		crossRef3.setUrl("http://url3");
		String newCrossRefId = v2store.getNextId(IdType.Anonymous);
		v2store.create(new TypedValue(newCrossRefId, SpdxConstantsCompatV2.CLASS_CROSS_REF, "SPDX-2.3"));
		v2store.setValue(newCrossRefId, SpdxConstantsCompatV2.PROP_CROSS_REF_URL, "http://url3");
		TypedValue newCrossRefTv = new TypedValue(newCrossRefId, SpdxConstantsCompatV2.CLASS_CROSS_REF, "SPDX-2.3");
		v2store.addValueToCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv);
		assertEquals(3, v2store.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
		assertTrue(v2store.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv));
		boolean found = false;
		for (CrossRef cr:license.getCrossRef()) {
			if (cr.equivalent(crossRef3)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
		v2store.removeValueFromCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv);
		assertFalse(v2store.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv));
		found = false;
		for (CrossRef cr:license.getCrossRef()) {
			if (cr.equivalent(crossRef3)) {
				found = true;
				break;
			}
		}
		assertFalse(found);
		v2store.close();
	}
	
	public void testIsCollectionProperty() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		assertTrue(sllw.isCollectionProperty(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(sllw.isCollectionProperty(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED));
		assertTrue(sllw.isCollectionMembersAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, CrossRef.class));
		assertFalse(sllw.isCollectionMembersAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, String.class));
		sllw.close();
	}
	
	public void testDelete() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		String nextId = sllw.getNextId(IdType.ListedLicense);
		sllw.create(new TypedValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, "SPDX-2.3"));
		String result = (String)sllw.getValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);
		assertTrue(sllw.exists(LICENSE_LIST_URI + nextId));
		sllw.delete(LICENSE_LIST_URI + nextId);
		assertFalse(sllw.exists(LICENSE_LIST_URI + nextId));
		
		nextId = sllw.getNextId(IdType.ListedLicense);
		sllw.create(new TypedValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, "SPDX-2.3"));
		result = (String)sllw.getValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
		assertTrue(sllw.exists(LICENSE_LIST_URI + nextId));
		sllw.delete(LICENSE_LIST_URI + nextId);
		assertFalse(sllw.exists(LICENSE_LIST_URI + nextId));
		sllw.close();
	}
	
	public void testCreationInfo() throws Exception {
		SpdxListedLicenseWebStore sllw = new SpdxListedLicenseWebStore();
		SpdxV3ListedLicenseModelStore modelStore = new SpdxV3ListedLicenseModelStore(sllw);
		ListedLicense license = new ListedLicense(modelStore, LICENSE_LIST_URI + APACHE_ID, null, true, null);
		assertTrue(license.verify().isEmpty());
		CreationInfo creationInfo = license.getCreationInfo();
		assertFalse(creationInfo.getCreated().isEmpty());
		List<Agent> createdBys = new ArrayList<>(creationInfo.getCreatedBys());
		assertEquals(1, createdBys.size());
		assertFalse(createdBys.get(0).getName().get().isEmpty());
	}
}
