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
package org.spdx.library.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ConjunctiveLicenseSet;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.LicenseException;
import org.spdx.library.model.license.SimpleLicensingInfo;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.library.model.license.SpdxNoneLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.IModelStore.ModelUpdate;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ModelObjectTest extends TestCase {

	private static final String TEST_ID = "testId";
	private static final Object TEST_VALUE1 = "value1";
	private static final String TEST_PROPERTY1 = "property1";
	static final String TEST_TYPE1 = SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION;
	static final String TEST_TYPE2 = SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO;

	static final String[] TEST_STRING_VALUE_PROPERTIES = new String[] {"valueProp1", "valueProp2", "valueProp3"};
	static final Object[] TEST_STRING_VALUE_PROPERTY_VALUES = new Object[] {"value1", "value2", "value3"};
	static final String[] TEST_BOOLEAN_VALUE_PROPERTIES = new String[] {"boolProp1", "boolProp2"};
	static final Object[] TEST_BOOLEAN_VALUE_PROPERTY_VALUES = new Object[] {true, false};
	static final String[] TEST_LIST_PROPERTIES = new String[] {"listProp1", "listProp2", "listProp3", "listProp4"};
	static final String[] TEST_MODEL_OJBECT_PROPERTIES = new String[] {"typeProp1", "typeProp2"};
	static final String[] TEST_ENUM_PROPERTIES = new String[] {"enumProp1", "enumProp2"};
	static final ChecksumAlgorithm[] TEST_ENUM_VALUES = new ChecksumAlgorithm[] {ChecksumAlgorithm.MD5, ChecksumAlgorithm.SHA1};
	static final String[] TEST_ANYLICENSEINFO_PROPERTIES = new String[] {"anylicenseProp1", "anylicenseProp2", "anylicenseProp3"};
	static final String[] TEST_ANYLICENSEINFO_LIST_PROPERTIES = new String[] {"anylicenseListProp1", "anylicensListProp2", "anylicenseListProp3"};
	
	ModelObject[] TEST_MODEL_OBJECT_PROP_VALUES;
	List<?>[] TEST_LIST_PROPERTY_VALUES;
	AnyLicenseInfo[] TEST_ANYLICENSEINFO_PROP_VALUES;
	List<?>[] TEST_ANYLICENSEINFO_LIST_PROP_VALUES;
	Map<String, Object> ALL_PROPERTY_VALUES;
	
	IModelStore store;
	String docUri;
	ModelCopyManager copyManager;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		DefaultModelStore.reset();
		ModelStorageClassConverter.reset();
		store = DefaultModelStore.getDefaultModelStore();
		docUri = DefaultModelStore.getDefaultDocumentUri();
		copyManager = DefaultModelStore.getDefaultCopyManager();
		LicenseException lex = new LicenseException("Autoconf-exception-2.0", "Autoconf exception 2.0 name", "Autoconf exception 2.0 text");
		ExtractedLicenseInfo eli1 = new ExtractedLicenseInfo(store.getNextId(IdType.LicenseRef, docUri));
		eli1.setName("eli1");
		eli1.setExtractedText("eli1 text");
		ConjunctiveLicenseSet cls = new ConjunctiveLicenseSet();
		cls.addMember(new SpdxListedLicense("Apache-2.0"));
		cls.addMember(eli1);
		TEST_LIST_PROPERTY_VALUES = new List<?>[] {Arrays.asList("ListItem1", "listItem2", "listItem3"), 
			Arrays.asList(true, false, true),
			Arrays.asList(new ModelObject[] {lex, eli1}),
			Arrays.asList(new ChecksumAlgorithm[] {ChecksumAlgorithm.SHA256, ChecksumAlgorithm.SHA1})};
		TEST_MODEL_OBJECT_PROP_VALUES = new ModelObject[] {lex, eli1};
		TEST_ANYLICENSEINFO_PROP_VALUES = new AnyLicenseInfo[] {new SpdxListedLicense("Apache-2.0"), eli1, new SpdxNoneLicense()};
		TEST_ANYLICENSEINFO_LIST_PROP_VALUES = new List<?>[] {Arrays.asList(new AnyLicenseInfo[] {new SpdxListedLicense("MIT"), eli1, new SpdxListedLicense("GPL-2.0-only")}),
			Arrays.asList(new AnyLicenseInfo[] {new SpdxNoAssertionLicense()}),
			Arrays.asList(new AnyLicenseInfo[] {cls, eli1})
		};
		
		ALL_PROPERTY_VALUES = new HashMap<>();
		for (int i = 0; i < TEST_STRING_VALUE_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_STRING_VALUE_PROPERTIES[i], TEST_STRING_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_BOOLEAN_VALUE_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_BOOLEAN_VALUE_PROPERTIES[i], TEST_BOOLEAN_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_LIST_PROPERTIES[i], TEST_LIST_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_MODEL_OJBECT_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_MODEL_OJBECT_PROPERTIES[i], TEST_MODEL_OBJECT_PROP_VALUES[i]);
		}
		for (int i = 0; i < TEST_ENUM_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_ENUM_PROPERTIES[i], TEST_ENUM_VALUES[i]);
		}
		for (int i = 0; i < TEST_ANYLICENSEINFO_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_ANYLICENSEINFO_PROPERTIES[i], TEST_ANYLICENSEINFO_PROP_VALUES[i]);
		}
		for (int i = 0; i < TEST_ANYLICENSEINFO_LIST_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_ANYLICENSEINFO_LIST_PROPERTIES[i], TEST_ANYLICENSEINFO_LIST_PROP_VALUES[i]);
		}
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	protected void addTestValues(ModelObject mo) throws InvalidSPDXAnalysisException {
		for (Entry<String, Object> entry:ALL_PROPERTY_VALUES.entrySet()) {
			mo.setPropertyValue(entry.getKey(), entry.getValue());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected boolean compareLists(Object olist1, Object olist2) throws InvalidSPDXAnalysisException {
		List<Object> list1;
		if (olist1 instanceof List) {
			if (((List<Object>)(olist1)).size() > 0 && ((List<Object>)(olist1)).get(0) instanceof ModelObject) {
				// convert type TypedValue
				list1 = new ArrayList<Object>();
				for (Object o:((List<Object>)(olist1))) {
					list1.add(((ModelObject)o).toTypedValue());
				}
			} else {
				list1 = (List<Object>)(olist1);
			}
		} else if (olist1 instanceof Object[]) {
			if (((Object[])olist1).length > 0 && ((Object[])olist1)[0] instanceof ModelObject) {
				list1 = new ArrayList<Object>();
				for (Object o:((Object[])olist1)) {
					list1.add(((ModelObject)o).toTypedValue());
				}
			} else {
				list1 = Arrays.asList(olist1);
			}
		} else {
			return false;
		}
		List<Object> list2;
		if (olist2 instanceof List) {
			if (((List<Object>)(olist2)).size() > 0 && ((List<Object>)(olist2)).get(0) instanceof ModelObject) {
				// convert type TypedValue
				list2 = new ArrayList<Object>();
				for (Object o:((List<Object>)(olist2))) {
					list2.add(((ModelObject)o).toTypedValue());
				}
			} else {
				list2 = (List<Object>)(olist2);
			}
		} else if (olist2 instanceof Object[]) {
			if (((Object[])olist2).length > 0 && ((Object[])olist2)[0] instanceof ModelObject) {
				list2 = new ArrayList<Object>();
				for (Object o:((Object[])olist2)) {
					list2.add(((ModelObject)o).toTypedValue());
				}
			} else {
				list2 = Arrays.asList(olist2);
			}
		} else {
			return false;
		}
		assertEquals(list1.size(), list2.size());
		if (list1.size() > 0 && list1.get(0) instanceof ModelObject) {
			// convert to type
		}
		for (Object list1item:list1) {
			if (!list2.contains(list1item)) {
				return false;
			}
		}
		return true;
	}
	
	public void testModelObjectCreate() throws InvalidSPDXAnalysisException {
		try {
			new GenericModelObject(store, docUri, TEST_ID, copyManager, false);
			fail("This should not have worked since created is set to false and the ID does not exist");
		} catch (InvalidSPDXAnalysisException ex) {
			// expected
		}
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		gmo.setPropertyValue(TEST_PROPERTY1, TEST_VALUE1);
		GenericModelObject gmo2 = new GenericModelObject(store, docUri, TEST_ID, copyManager, false);
		assertTrue(gmo2.getStringPropertyValue(TEST_PROPERTY1).isPresent());
		assertEquals(gmo2.getStringPropertyValue(TEST_PROPERTY1).get(), TEST_VALUE1);
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getDocumentUri()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetDocumentUri() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		assertEquals(docUri, gmo.getDocumentUri());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getId()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetId() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		assertEquals(TEST_ID, gmo.getId());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getModelStore()}.
	 */
	public void testGetModelStore() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		assertEquals(store, gmo.getModelStore());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getPropertyValueNames()}.
	 */
	public void testGetPropertyValueNames() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		List<String> result = gmo.getPropertyValueNames();
		assertEquals(0, result.size());
		addTestValues(gmo);
		result = gmo.getPropertyValueNames();
		assertEquals(ALL_PROPERTY_VALUES.size(), result.size());
		for (String property:ALL_PROPERTY_VALUES.keySet()) {
			assertTrue(result.contains(property));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getObjectPropertyValue(java.lang.String)}.
	 */
	public void testGetObjectPropertyValue() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		assertEquals(0, gmo.getPropertyValueNames().size());
		addTestValues(gmo);
		for (Entry<String, Object> entry:ALL_PROPERTY_VALUES.entrySet()) {
			Optional<Object> result = gmo.getObjectPropertyValue(entry.getKey());
			assertTrue(result.isPresent());
			if (result.get() instanceof List) {
				assertTrue(compareLists(entry.getValue(), result.get()));
			} else if (result.get() instanceof ModelObject) {
				assertEquals(entry.getValue(), result.get());
			} else if (result.get() instanceof ModelCollection) {
				assertTrue(compareLists(entry.getValue(), ((ModelCollection<?>)result.get()).toImmutableList()));
			} else {
				assertEquals(entry.getValue(), result.get());
			}
		}
	}


	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#setPropertyValue(org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)}.
	 */
	public void testSetPropertyValue() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		String prop = "property";
		String val = "value";
		assertFalse(gmo.getObjectPropertyValue(prop).isPresent());
		gmo.setPropertyValue(prop, val);
		assertTrue(gmo.getObjectPropertyValue(prop).isPresent());
		assertEquals(val, gmo.getObjectPropertyValue(prop).get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updatePropertyValue(java.lang.String, java.lang.Object)}.
	 */
	public void testUpdatePropertyValue() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		String prop = "property";
		String val = "value";
		assertFalse(gmo.getObjectPropertyValue(prop).isPresent());
		ModelUpdate mu = gmo.updatePropertyValue(prop, val);
		assertFalse(gmo.getObjectPropertyValue(prop).isPresent());
		mu.apply();
		assertTrue(gmo.getObjectPropertyValue(prop).isPresent());
		assertEquals(val, gmo.getObjectPropertyValue(prop).get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getStringPropertyValue(java.lang.String)}.
	 */
	public void testGetStringPropertyValue() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		for (int i = 0; i < TEST_STRING_VALUE_PROPERTIES.length; i++) {
			assertEquals(TEST_STRING_VALUE_PROPERTY_VALUES[i], gmo.getStringPropertyValue(TEST_STRING_VALUE_PROPERTIES[i]).get());
		}
		try {
			gmo.getStringPropertyValue(TEST_BOOLEAN_VALUE_PROPERTIES[0]);
			fail("No exception on getting the wrong type");
		} catch(SpdxInvalidTypeException ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getBooleanPropertyValue(java.lang.String)}.
	 */
	public void testGetBooleanPropertyValue() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		for (int i = 0; i < TEST_BOOLEAN_VALUE_PROPERTIES.length; i++) {
			assertEquals(TEST_BOOLEAN_VALUE_PROPERTY_VALUES[i], gmo.getBooleanPropertyValue(TEST_BOOLEAN_VALUE_PROPERTIES[i]).get());
		}
		try {
			gmo.getBooleanPropertyValue(TEST_STRING_VALUE_PROPERTIES[0]);
			fail("No exception on getting the wrong type");
		} catch(SpdxInvalidTypeException ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#removeProperty(org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testRemovePropertyIModelStoreStringStringString() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		String prop = "property";
		String val = "value";
		assertFalse(gmo.getObjectPropertyValue(prop).isPresent());
		gmo.setPropertyValue(prop, val);
		assertTrue(gmo.getObjectPropertyValue(prop).isPresent());
		assertEquals(val, gmo.getObjectPropertyValue(prop).get());
		gmo.removeProperty(prop);
		assertFalse(gmo.getObjectPropertyValue(prop).isPresent());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateRemoveProperty(java.lang.String)}.
	 */
	public void testUpdateRemoveProperty() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		String prop = "property";
		String val = "value";
		assertFalse(gmo.getObjectPropertyValue(prop).isPresent());
		gmo.setPropertyValue(prop, val);
		assertTrue(gmo.getObjectPropertyValue(prop).isPresent());
		assertEquals(val, gmo.getObjectPropertyValue(prop).get());
		ModelUpdate mu = gmo.updateRemoveProperty(prop);
		assertTrue(gmo.getObjectPropertyValue(prop).isPresent());
		assertEquals(val, gmo.getObjectPropertyValue(prop).get());
		mu.apply();
		assertFalse(gmo.getObjectPropertyValue(prop).isPresent());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#clearValueCollection(org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testClearPropertyValueList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		gmo.clearValueCollection(TEST_LIST_PROPERTIES[0]);
		assertEquals(0, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).size());
		for (int i = 1; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateClearValueCollection(java.lang.String)}.
	 */
	public void testUpdateClearPropertyValueList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		ModelUpdate mu = gmo.updateClearValueCollection(TEST_LIST_PROPERTIES[0]);
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
		mu.apply();
		assertEquals(0, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).size());
		for (int i = 1; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#addValueToCollection(org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)}.
	 */
	public void testAddPropertyValueToList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		@SuppressWarnings("unchecked")
		List<String> expected = new ArrayList<String>((List<String>)TEST_LIST_PROPERTY_VALUES[0]);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		String newValue = "newValue";
		expected.add(newValue);
		gmo.addPropertyValueToCollection(TEST_LIST_PROPERTIES[0], newValue);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		for (int i = 1; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateAddPropertyValueToCollection(java.lang.String, java.lang.Object)}.
	 */
	public void testUpdateAddPropertyValueToList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		@SuppressWarnings("unchecked")
		List<String> expected = new ArrayList<String>((List<String>)TEST_LIST_PROPERTY_VALUES[0]);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		String newValue = "newValue";
		ModelUpdate mu = gmo.updateAddPropertyValueToCollection(TEST_LIST_PROPERTIES[0], newValue);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		expected.add(newValue);
		mu.apply();
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		for (int i = 1; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#replacePropertyValueList(org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String, java.util.List)}.
	 */
	public void testReplacePropertyValueList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		@SuppressWarnings("unchecked")
		List<String> expected = new ArrayList<String>((List<String>)TEST_LIST_PROPERTY_VALUES[0]);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		expected = Arrays.asList("newList1", "newList2");
		gmo.setPropertyValue(TEST_LIST_PROPERTIES[0], expected);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		for (int i = 1; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateReplacePropertyValueList(java.lang.String, java.util.List)}.
	 */
	public void testUpdateReplacePropertyValueList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[0], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		List<String> expected = Arrays.asList("newList1", "newList2");
		ModelUpdate mu = gmo.updatePropertyValue(TEST_LIST_PROPERTIES[0], expected);
		assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[0], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		mu.apply();
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		for (int i = 1; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#removePropertyValueFromCollection(org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)}.
	 */
	public void testRemovePropertyValueFromList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		@SuppressWarnings("unchecked")
		List<Object> expected = new ArrayList<Object>((List<Object>)TEST_LIST_PROPERTY_VALUES[0]);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		Object removed = expected.get(0);
		expected.remove(removed);
		gmo.removePropertyValueFromCollection(TEST_LIST_PROPERTIES[0], removed);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		for (int i = 1; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateRemovePropertyValueFromCollection(java.lang.String, java.lang.Object)}.
	 */
	public void testUpdateRemovePropertyValueFromList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		@SuppressWarnings("unchecked")
		List<Object> expected = new ArrayList<Object>((List<Object>)TEST_LIST_PROPERTY_VALUES[0]);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		Object removed = expected.get(0);
		ModelUpdate mu = gmo.updateRemovePropertyValueFromCollection(TEST_LIST_PROPERTIES[0], removed);
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		expected.remove(removed);
		mu.apply();
		assertTrue(compareLists(expected, gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[0], null).toImmutableList()));
		for (int i = 1; i < TEST_LIST_PROPERTIES.length; i++) {
			assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[i], gmo.getObjectPropertyValueSet(TEST_LIST_PROPERTIES[i], null).toImmutableList()));
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getStringPropertyValueList(java.lang.String)}.
	 */
	public void testGetStringPropertyValueCollection() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		Collection<String> result = gmo.getStringCollection(TEST_LIST_PROPERTIES[0]);
		assertTrue(compareLists(TEST_LIST_PROPERTY_VALUES[0], new ArrayList<>(result)));
		try {
			gmo.getStringCollection(TEST_LIST_PROPERTIES[1]);
			fail("No exception on getting the wrong type");
		} catch(SpdxInvalidTypeException ex) {
			// expected
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#equivalent(org.spdx.library.model.ModelObject)}.
	 */
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		assertTrue(gmo.equivalent(gmo));
		// same store
		GenericModelObject gmo2 = new GenericModelObject(store, docUri, "TestId2", copyManager, true);
		addTestValues(gmo2);
		assertTrue(gmo.equivalent(gmo2));
		assertTrue(gmo2.equivalent(gmo));
		// different store
		InMemSpdxStore store2 = new InMemSpdxStore();
		GenericModelObject gmo3 = new GenericModelObject(store2, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo3);
		assertTrue(gmo.equivalent(gmo3));
		assertTrue(gmo3.equivalent(gmo2));
	}
	
	public void testEquivalentModelObjectProp() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		String id1 = "id1";
		String id2 = "id2";
		String text = "licenseText";
		String prop = "prop";
		ExtractedLicenseInfo eli = new ExtractedLicenseInfo(id1, text);
		ExtractedLicenseInfo eli2 = new ExtractedLicenseInfo(id2, text);
		assertTrue(eli.equivalent(eli2));
		assertFalse(eli.equals(eli2));
		gmo.setPropertyValue(prop, eli);
		@SuppressWarnings("unchecked")
		Optional<ExtractedLicenseInfo> testResult = (Optional<ExtractedLicenseInfo>)(Optional<?>)gmo.getObjectPropertyValue(prop);
		assertTrue(testResult.isPresent());
		String licenseText = testResult.get().getExtractedText();
		assertEquals(text, licenseText);
		assertTrue(gmo.equivalent(gmo));
		// different store
		InMemSpdxStore store2 = new InMemSpdxStore();
		GenericModelObject gmo3 = new GenericModelObject(store2, docUri, TEST_ID, copyManager, true);
		gmo3.setPropertyValue(prop, eli2);
		assertTrue(gmo.equivalent(gmo3));
	}
	
	public void testEquivalentModelObjectList() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		String id1 = "id1";
		String id2 = "id2";
		String text = "licenseText";
		String prop = "prop";
		ExtractedLicenseInfo eli = new ExtractedLicenseInfo(id1, text);
		ExtractedLicenseInfo eli2 = new ExtractedLicenseInfo(id2, text);
		String id3 = "id3";
		String id4 = "id4";
		String text2 = "license text 2";
		ExtractedLicenseInfo nextEli = new ExtractedLicenseInfo(id3, text2);
		ExtractedLicenseInfo nextEli2 = new ExtractedLicenseInfo(id4, text2);
		assertTrue(eli.equivalent(eli2));
		assertFalse(eli.equals(eli2));
		assertTrue(nextEli.equivalent(nextEli2));
		assertFalse(nextEli.equals(nextEli2));
		gmo.addPropertyValueToCollection(prop, eli);
		gmo.addPropertyValueToCollection(prop, nextEli);
		assertTrue(gmo.equivalent(gmo));
		// same store
		GenericModelObject gmo2 = new GenericModelObject(store, docUri, "TestId2", copyManager, true);
		gmo2.addPropertyValueToCollection(prop, eli2);
		gmo2.addPropertyValueToCollection(prop, nextEli2);
		assertTrue(gmo.equivalent(gmo2));
		assertTrue(gmo2.equivalent(gmo));
		// different store
		InMemSpdxStore store2 = new InMemSpdxStore();
		GenericModelObject gmo3 = new GenericModelObject(store2, docUri, TEST_ID, copyManager, true);
		gmo3.addPropertyValueToCollection(prop, eli2);
		gmo3.addPropertyValueToCollection(prop, nextEli2);
		assertTrue(gmo.equivalent(gmo3));
		assertTrue(gmo3.equivalent(gmo2));
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		assertTrue(gmo.equals(gmo));
		// different ID's
		GenericModelObject gmo2 = new GenericModelObject(store, docUri, "TestId2", copyManager, true);
		addTestValues(gmo2);
		assertFalse(gmo.equals(gmo2));
		assertFalse(gmo2.equals(gmo));
		// same ID's, different store
		InMemSpdxStore store2 = new InMemSpdxStore();
		GenericModelObject gmo3 = new GenericModelObject(store2, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo3);
		assertTrue(gmo.equals(gmo3));
		assertTrue(gmo3.equals(gmo));
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#clone()}.
	 */
	public void testClone() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		InMemSpdxStore store2 = new InMemSpdxStore();
		ModelObject result = gmo.clone(store2);
		assertTrue(result instanceof GenericModelObject);
		assertTrue(result.equals(gmo));
		assertTrue(result.equivalent(gmo));
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#copyFrom(org.spdx.library.model.ModelObject)}.
	 */
	public void testCopyFrom() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		GenericModelObject gmo2 = new GenericModelObject(store, docUri, "id2", copyManager, true);
		gmo2.copyFrom(gmo);
		assertTrue(gmo.equivalent(gmo2));
		// different store
		InMemSpdxStore store2 = new InMemSpdxStore();
		GenericModelObject gmo3 = new GenericModelObject(store2, docUri, TEST_ID, copyManager, true);
		gmo3.copyFrom(gmo3);
		assertTrue(gmo.equivalent(gmo3));
		assertTrue(gmo3.equivalent(gmo2));
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#idToIdType(java.lang.String)}.
	 */
	public void testIdToIdType() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		assertEquals(IdType.Anonymous, gmo.idToIdType("anything"));
		assertEquals(IdType.DocumentRef, gmo.idToIdType(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "12"));
		assertEquals(IdType.LicenseRef, gmo.idToIdType(SpdxConstants.NON_STD_LICENSE_ID_PRENUM + "12"));
		assertEquals(IdType.ListedLicense, gmo.idToIdType("Apache-2.0"));
		assertEquals(IdType.Literal, gmo.idToIdType(SpdxConstants.NONE_VALUE));
		assertEquals(IdType.Literal, gmo.idToIdType(SpdxConstants.NOASSERTION_VALUE));
		assertEquals(IdType.SpdxId, gmo.idToIdType(SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "12"));
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#toTypedValue()}.
	 */
	public void testToTypeValue() throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		TypedValue result = gmo.toTypedValue();
		assertEquals(TEST_ID, result.getId());
		assertEquals(gmo.getType(), result.getType());
	}
	
	@SuppressWarnings("unchecked")
	public void testGetEnumValue() throws InvalidSPDXAnalysisException, InstantiationException, IllegalAccessException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		for (int i = 0; i < TEST_ENUM_PROPERTIES.length; i++) {
			Optional<Enum<?>> result = (Optional<Enum<?>>)(Optional<?>)gmo.getEnumPropertyValue(TEST_ENUM_PROPERTIES[i]);
			assertTrue(result.isPresent());
			assertEquals(TEST_ENUM_VALUES[i], result.get());
		}
	}
	
	public void testGetObjectValueIndividualValue()  throws InvalidSPDXAnalysisException {
		Enum<?> TEST_ENUM = RelationshipType.DESCRIBES;
		String ENUM_URI = RelationshipType.DESCRIBES.getIndividualURI();
		String EXTERNAL_DOC_NAMSPACE = "https://test/namespace1";
		String EXTERNAL_SPDX_ELEMENT_ID = SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "TEST";
		String EXTERNAL_SPDX_URI = EXTERNAL_DOC_NAMSPACE + "#" + EXTERNAL_SPDX_ELEMENT_ID;
		String NON_INTERESTING_URI = "https://nothing/to/see/here";
		
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		new SpdxDocument(store, docUri, copyManager, true);
		
		// External SPDX element
		SimpleUriValue suv = new SimpleUriValue(EXTERNAL_SPDX_URI);
		gmo.setPropertyValue(TEST_PROPERTY1, suv);
		Optional<Object> result = gmo.getObjectPropertyValue(TEST_PROPERTY1);
		assertTrue(result.isPresent());
		assertTrue(result.get() instanceof ExternalSpdxElement);
		ExternalSpdxElement externalElement = (ExternalSpdxElement)result.get();
		assertEquals(EXTERNAL_SPDX_ELEMENT_ID, externalElement.getExternalElementId());
		assertEquals(EXTERNAL_SPDX_URI, externalElement.getExternalSpdxElementURI());
		
		// Enum value
		suv = new SimpleUriValue(ENUM_URI);
		gmo.setPropertyValue(TEST_PROPERTY1, suv);
		result = gmo.getObjectPropertyValue(TEST_PROPERTY1);
		assertTrue(result.isPresent());
		assertEquals(TEST_ENUM, result.get());
		
		// Simple URI value
		
		suv = new SimpleUriValue(NON_INTERESTING_URI);
		gmo.setPropertyValue(TEST_PROPERTY1, suv);
		result = gmo.getObjectPropertyValue(TEST_PROPERTY1);
		assertTrue(result.isPresent());
		assertTrue(result.get() instanceof SimpleUriValue);
		assertEquals(NON_INTERESTING_URI, ((SimpleUriValue)result.get()).getIndividualURI());
	}
	
	public void testGetAnyLicenseInfoPropertyValue()  throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		for (int i = 0; i < TEST_ANYLICENSEINFO_PROPERTIES.length; i++) {
			Optional<AnyLicenseInfo> result = gmo.getAnyLicenseInfoPropertyValue(TEST_ANYLICENSEINFO_PROPERTIES[i]);
			assertTrue(result.isPresent());
			assertEquals(TEST_ANYLICENSEINFO_PROP_VALUES[i], result.get());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testAnyLicenseCollection()  throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		for (int i = 0; i < TEST_ANYLICENSEINFO_LIST_PROPERTIES.length; i++) {
			ModelCollection<AnyLicenseInfo> result = (ModelCollection<AnyLicenseInfo>)(ModelCollection<?>)gmo.getObjectPropertyValueSet(TEST_ANYLICENSEINFO_LIST_PROPERTIES[i], AnyLicenseInfo.class);
			assertTrue(compareLists(TEST_ANYLICENSEINFO_LIST_PROP_VALUES[i], result.toImmutableList()));
		}
	}
	
	public void testTypeCheckedCollection()  throws InvalidSPDXAnalysisException {
		GenericModelObject gmo = new GenericModelObject(store, docUri, TEST_ID, copyManager, true);
		addTestValues(gmo);
		gmo.getObjectPropertyValueSet(TEST_ANYLICENSEINFO_LIST_PROPERTIES[0], AnyLicenseInfo.class);
		gmo.getObjectPropertyValueSet(TEST_ANYLICENSEINFO_LIST_PROPERTIES[0], SimpleLicensingInfo.class);
		try {
			gmo.getObjectPropertyValueSet(TEST_ANYLICENSEINFO_LIST_PROPERTIES[0], SpdxFile.class);
			fail("Type check should not have passed");
		} catch (InvalidSPDXAnalysisException ex) {
			// expected
		}
		try {
			gmo.getObjectPropertyValueSet(TEST_ANYLICENSEINFO_LIST_PROPERTIES[0], SpdxListedLicense.class);
			fail("Type check should not have passed");
		} catch (InvalidSPDXAnalysisException ex) {
			// expected
		}
	}
}
