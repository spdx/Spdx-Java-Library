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
import org.spdx.library.model.v3.expandedlicensing.ListedLicenseException;
import org.spdx.library.model.v3.expandedlicensing.ListedLicense;
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
public class SpdxListedLicenseLocalStoreTest extends TestCase {

	private static final String APACHE_ID = "Apache-2.0";
	private static final String LICENSE_LIST_URI = "http://spdx.org/licenses/";
	private static final String LICENSE_LIST_VERSION = "3.17";
	private static final String APACHE_LICENSE_NAME = "Apache License 2.0";
	
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
	 * @throws Exception 
	 */
	public void testExists() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertTrue(slll.exists(LICENSE_LIST_URI + APACHE_ID));
		assertFalse(slll.exists(LICENSE_LIST_URI + "Unknown"));
		assertTrue(slll.exists(LICENSE_LIST_URI + ECOS_EXCEPTION_ID));
		slll.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#create(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	public void testCreate() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String nextId = slll.getNextId(IdType.ListedLicense);
		slll.create(new TypedValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, "SPDX-2.3"));
		String result = (String)slll.getValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);
		
		nextId = slll.getNextId(IdType.ListedLicense);
		slll.create(new TypedValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, "SPDX-2.3"));
		result = (String)slll.getValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
		slll.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)}.
	 * @throws Exception 
	 */
	public void testGetNextId() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String nextId = slll.getNextId(IdType.ListedLicense);
		String nextNextId = slll.getNextId(IdType.ListedLicense);
		assertTrue(nextId.compareTo(nextNextId) < 0);
		slll.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getSpdxListedLicenseIds()}.
	 * @throws Exception 
	 */
	public void testGetSpdxListedLicenseIds() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		List<String> result = slll.getSpdxListedLicenseIds();
		assertTrue(result.size() >= 373);
		assertTrue(result.contains(APACHE_ID));
		slll.close();
	}
	
	public void testGetSpdxListedExceptionIds() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		List<String> result = slll.getSpdxListedExceptionIds();
		assertTrue(result.size() >= NUM_3_7_EXCEPTION);
		assertTrue(result.contains(ECOS_EXCEPTION_ID));
		slll.close();
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.SpdxListedLicenseModelStore#getLicenseListVersion()}.
	 * @throws Exception 
	 */
	public void testGetLicenseListVersion() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertTrue(LICENSE_LIST_VERSION.compareTo(slll.getLicenseListVersion()) <= 0);
		slll.close();
	}
	
	public void testGetValue() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String result = (String)slll.getValue(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		
		result = (String)slll.getValue(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
		slll.close();
	}
	
	public void testSetValue() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String result = (String)slll.getValue(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(APACHE_LICENSE_NAME, result);
		String newName = "new name";
		slll.setValue(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_NAME, newName);
		result = (String)slll.getValue(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(newName, result);
		
		result = (String)slll.getValue(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(ECOS_LICENSE_NAME, result);
		slll.setValue(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME, newName);
		result = (String)slll.getValue(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_NAME).get();
		assertEquals(newName, result);
		slll.close();
	}

	public void testCreateLicenseV2() throws InvalidSPDXAnalysisException, InvalidLicenseTemplateException {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		SpdxV2ListedLicenseModelStore modelStore = new SpdxV2ListedLicenseModelStore(slll);
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
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		SpdxV3ListedLicenseModelStore modelStore = new SpdxV3ListedLicenseModelStore(slll);
		ListedLicense result = new ListedLicense(modelStore, 
				LICENSE_LIST_URI + APACHE_ID, null, true, null);
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
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		SpdxV2ListedLicenseModelStore v2store = new SpdxV2ListedLicenseModelStore(slll);
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
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		SpdxV3ListedLicenseModelStore v3store = new SpdxV3ListedLicenseModelStore(slll);
		ListedLicenseException result = new ListedLicenseException(v3store, 
				LICENSE_LIST_URI+ECOS_EXCEPTION_ID, null, true, null);
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
	public void testList() throws InvalidSPDXAnalysisException {
		SpdxListedLicenseLocalStore slls  = new SpdxListedLicenseLocalStore();
		SpdxV2ListedLicenseModelStore spdx2localstore = new SpdxV2ListedLicenseModelStore(slls);
		// Exception
		org.spdx.library.model.v2.license.ListedLicenseException exception = (org.spdx.library.model.v2.license.ListedLicenseException)org.spdx.library.model.v2.SpdxModelFactoryCompatV2.createModelObjectV2(spdx2localstore, LICENSE_LIST_URI, ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, null);
		String seeAlso1 = "seeAlso1";
		String seeAlso2 = "seeAlso2";
		List<String> seeAlsos = Arrays.asList(new String[]{seeAlso1, seeAlso2});
		exception.setSeeAlso(seeAlsos);
		// getValueList
		List<Object> result = new ArrayList<>();
		Iterator<Object> resultIter = spdx2localstore.listValues(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		while (resultIter.hasNext()) {
			result.add(resultIter.next());
		}
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(spdx2localstore.collectionContains(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), spdx2localstore.collectionSize(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		String seeAlso3 = "seeAlso3";
		assertFalse(spdx2localstore.collectionContains(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(spdx2localstore.addValueToCollection(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(spdx2localstore.collectionContains(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, spdx2localstore.collectionSize(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(spdx2localstore.removeValueFromCollection(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(spdx2localstore.collectionContains(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), spdx2localstore.collectionSize(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(spdx2localstore.removeValueFromCollection(LICENSE_LIST_URI + ECOS_EXCEPTION_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		
		// License
		ModelCopyManager copyManager = new ModelCopyManager();
		SpdxListedLicense license = (SpdxListedLicense)org.spdx.library.model.v2.SpdxModelFactoryCompatV2.createModelObjectV2(spdx2localstore, LICENSE_LIST_URI, APACHE_ID, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, copyManager);
		license.setSeeAlso(seeAlsos);
		// getValueList
		result.clear();
		resultIter = spdx2localstore.listValues(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		while (resultIter.hasNext()) {
			result.add(resultIter.next());
		}
		assertEquals(seeAlsos.size(), result.size());
		for (String seeAlso:seeAlsos) {
			assertTrue(result.contains(seeAlso));
			// collectionContains
			assertTrue(spdx2localstore.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso));
		}
		// collectionSize
		assertEquals(seeAlsos.size(), spdx2localstore.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// addValueToCollection
		assertFalse(spdx2localstore.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(spdx2localstore.addValueToCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertTrue(spdx2localstore.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size()+1, spdx2localstore.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		// remove value
		assertTrue(spdx2localstore.removeValueFromCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertFalse(spdx2localstore.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		assertEquals(seeAlsos.size(), spdx2localstore.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(spdx2localstore.removeValueFromCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, seeAlso3));
		
		// license crossRefs
		license.getCrossRef().clear();
		assertEquals(0, spdx2localstore.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
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
		resultIter = spdx2localstore.listValues(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF);
		List<TypedValue> tvResult = new ArrayList<>();
		while (resultIter.hasNext()) {
			TypedValue tv = (TypedValue)resultIter.next();
			tvResult.add(tv);
			result.add(new CrossRef(spdx2localstore, LICENSE_LIST_URI, tv.getObjectUri(), copyManager, false));
		}
		List<CrossRef> result2 = (List<CrossRef>)(List<?>)Arrays.asList(license.getCrossRef().toArray());
		assertEquals(2, result.size());
		assertEquals(2, result2.size());
		assertEquals(2, spdx2localstore.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
		assertTrue(UnitTestHelper.isListsEquivalent(crossRefs, (List<CrossRef>)(List<?>)result));
		assertTrue(UnitTestHelper.isListsEquivalent(crossRefs, result2));
		for (TypedValue tv:tvResult) {
			assertTrue(spdx2localstore.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, tv));
		}
		for (CrossRef crossRef:crossRefs) {
			// collectionContains
			assertTrue(license.getCrossRef().contains(crossRef));
		}
		CrossRef crossRef3 = new CrossRef(simpleModelStore, docUri, simpleModelStore.getNextId(IdType.Anonymous), copyManager, true);
		crossRef3.setUrl("http://url3");
		String newCrossRefId = spdx2localstore.getNextId(IdType.Anonymous);
		spdx2localstore.create(new TypedValue(LICENSE_LIST_URI + newCrossRefId, SpdxConstantsCompatV2.CLASS_CROSS_REF, "SPDX-2.3"));
		spdx2localstore.setValue(LICENSE_LIST_URI + newCrossRefId, SpdxConstantsCompatV2.PROP_CROSS_REF_URL, "http://url3");
		TypedValue newCrossRefTv = new TypedValue(newCrossRefId, SpdxConstantsCompatV2.CLASS_CROSS_REF, "SPDX-2.3");
		spdx2localstore.addValueToCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv);
		assertEquals(3, spdx2localstore.collectionSize(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF));
		assertTrue(spdx2localstore.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv));
		boolean found = false;
		for (CrossRef cr:license.getCrossRef()) {
			if (cr.equivalent(crossRef3)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
		spdx2localstore.removeValueFromCollection(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv);
		assertFalse(spdx2localstore.collectionContains(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, newCrossRefTv));
		found = false;
		for (CrossRef cr:license.getCrossRef()) {
			if (cr.equivalent(crossRef3)) {
				found = true;
				break;
			}
		}
		assertFalse(found);
	}
	
	public void testIsCollectionMembersAssignableTo() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertTrue(slll.isCollectionMembersAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, String.class));
		assertFalse(slll.isCollectionMembersAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, Boolean.class));
		assertFalse(slll.isCollectionMembersAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_LICENSE_TEXT, String.class));
		assertTrue(slll.isCollectionMembersAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, CrossRef.class));
		assertFalse(slll.isCollectionMembersAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_CROSS_REF, String.class));
		slll.close();
	}
	
	public void testIsPropertyValueAssignableTo() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertFalse(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, String.class, "SPDX-2.3"));
		assertTrue(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_LICENSE_TEXT, String.class, "SPDX-2.3"));
		assertFalse(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_LICENSE_TEXT, Boolean.class, "SPDX-2.3"));

		assertFalse(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, String.class, "SPDX-2.3"));
		assertTrue(slll.isPropertyValueAssignableTo(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, Boolean.class, "SPDX-2.3"));
		slll.close();
	}
	
	public void testIsCollectionProperty() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		assertTrue(slll.isCollectionProperty(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO));
		assertFalse(slll.isCollectionProperty(LICENSE_LIST_URI + APACHE_ID, SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED));
		slll.close();
	}
	
	public void testDelete() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		String nextId = slll.getNextId(IdType.ListedLicense);
		slll.create(new TypedValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, "SPDX-2.3"));
		String result = (String)slll.getValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.PROP_LICENSE_ID).get();
		assertEquals(nextId, result);
		assertTrue(slll.exists(LICENSE_LIST_URI + nextId));
		slll.delete(LICENSE_LIST_URI + nextId);
		assertFalse(slll.exists(LICENSE_LIST_URI + nextId));
		
		nextId = slll.getNextId(IdType.ListedLicense);
		slll.create(new TypedValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, "SPDX-2.3"));
		result = (String)slll.getValue(LICENSE_LIST_URI + nextId, SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID).get();
		assertEquals(nextId, result);
		assertTrue(slll.exists(LICENSE_LIST_URI + nextId));
		slll.delete(LICENSE_LIST_URI + nextId);
		assertFalse(slll.exists(LICENSE_LIST_URI + nextId));
		slll.close();
	}
	
	public void testCreationInfo() throws Exception {
		SpdxListedLicenseLocalStore slll = new SpdxListedLicenseLocalStore();
		SpdxV3ListedLicenseModelStore modelStore = new SpdxV3ListedLicenseModelStore(slll);
		ListedLicense license = new ListedLicense(modelStore, LICENSE_LIST_URI + APACHE_ID, null, 
				true, null);
		assertTrue(license.verify().isEmpty());
		CreationInfo creationInfo = license.getCreationInfo();
		assertFalse(creationInfo.getCreated().isEmpty());
		List<Agent> createdBys = new ArrayList<>(creationInfo.getCreatedBys());
		assertEquals(1, createdBys.size());
		assertFalse(createdBys.get(0).getName().get().isEmpty());
	}

}
