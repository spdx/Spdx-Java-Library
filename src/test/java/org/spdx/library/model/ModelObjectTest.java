/**
 * Copyright (c) 2023 Source Auditor Inc.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ModelObjectForTesting.ModelObjectForTestingBuilder;
import org.spdx.library.model.core.ExternalMap;
import org.spdx.library.model.core.HashAlgorithm;
import org.spdx.library.model.expandedlicense.ConjunctiveLicenseSet;
import org.spdx.library.model.licensing.AnyLicenseInfo;
import org.spdx.library.model.licensing.CustomLicense;
import org.spdx.library.model.licensing.ListedLicense;
import org.spdx.library.model.licensing.ListedLicenseException;
import org.spdx.library.model.licensing.ListedLicenseException.ListedLicenseExceptionBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ModelObjectTest extends TestCase {
	
	private static final String TEST_OBJECT_URI = "https://spdx.test/testId";
	private static final String TEST_OBJECT_URI2 = "https://spdx.test/testId2";
	private static final Object TEST_VALUE1 = "value1";
	private static final PropertyDescriptor TEST_PROPERTY1 = new PropertyDescriptor("property1", SpdxConstants.CORE_NAMESPACE);
	private static final PropertyDescriptor TEST_PROPERTY2 = new PropertyDescriptor("property2", SpdxConstants.CORE_NAMESPACE);
	static final String TEST_TYPE1 = "Core.Element";
	static final String TEST_TYPE2 = "Core.Annotation";
	
	static final String EXTERNAL_ID1 = "http://externalMap1";
	static final String EXTERNAL_DEFINING_DOCUMENT1 = "http://externalDoc1";
	static final String EXTERNAL_ID2 = "http://externalMap2";
	static final String EXTERNAL_DEFINING_DOCUMENT2 = "http://externalDoc2";
	
	static final PropertyDescriptor[] TEST_STRING_VALUE_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("valueProp1", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("valueProp2", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("valueProp3", SpdxConstants.CORE_NAMESPACE)};
	static final Object[] TEST_STRING_VALUE_PROPERTY_VALUES = new Object[] {"value1", "value2", "value3"};
	static final PropertyDescriptor[] TEST_INTEGER_VALUE_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("intProp1", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("intProp2", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("Intprop3", SpdxConstants.CORE_NAMESPACE)};
	static final Object[] TEST_INTEGER_VALUE_PROPERTY_VALUES = new Object[] {Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(-1)};
	static final PropertyDescriptor[] TEST_BOOLEAN_VALUE_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("boolProp1", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("boolProp2", SpdxConstants.CORE_NAMESPACE)};
	static final Object[] TEST_BOOLEAN_VALUE_PROPERTY_VALUES = new Object[] {true, false};
	static final PropertyDescriptor[] TEST_LIST_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("listProp1", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("listProp2", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("listProp3", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("listProp4", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("listProp5", SpdxConstants.CORE_NAMESPACE)};
	static final PropertyDescriptor[] TEST_MODEL_OJBECT_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("typeProp1", SpdxConstants.CORE_NAMESPACE),
			new PropertyDescriptor("typeProp2", SpdxConstants.CORE_NAMESPACE)};
	static final PropertyDescriptor[] TEST_ENUM_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("enumProp1", SpdxConstants.CORE_NAMESPACE),
			new PropertyDescriptor("enumProp2", SpdxConstants.CORE_NAMESPACE)};
	static final HashAlgorithm[] TEST_ENUM_VALUES = new HashAlgorithm[] {HashAlgorithm.MD5, HashAlgorithm.SHA1};
	static final PropertyDescriptor[] TEST_ANYLICENSEINFO_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("anylicenseProp1", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("anylicenseProp2", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("anylicenseProp3", SpdxConstants.CORE_NAMESPACE)};
	static final PropertyDescriptor[] TEST_ANYLICENSEINFO_LIST_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("anylicenseListProp1", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("anylicensListProp2", SpdxConstants.CORE_NAMESPACE), 
			new PropertyDescriptor("anylicenseListProp3", SpdxConstants.CORE_NAMESPACE)};
	
	ModelObject[] TEST_MODEL_OBJECT_PROP_VALUES;
	List<?>[] TEST_LIST_PROPERTY_VALUES;
	AnyLicenseInfo[] TEST_ANYLICENSEINFO_PROP_VALUES;
	List<?>[] TEST_ANYLICENSEINFO_LIST_PROP_VALUES;
	Map<PropertyDescriptor, Object> ALL_PROPERTY_VALUES;
	
	IModelStore store;
	ModelCopyManager copyManager;
	Map<String, ExternalMap> externalMap;


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		store = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		externalMap = new HashMap<>();
		externalMap.put(EXTERNAL_ID1, new ExternalMap.ExternalMapBuilder(store, EXTERNAL_ID1, copyManager)
				.setDefiningDocument(EXTERNAL_DEFINING_DOCUMENT1)
				.setExternalId(EXTERNAL_ID1)
				.build());
		externalMap.put(EXTERNAL_ID2, new ExternalMap.ExternalMapBuilder(store, EXTERNAL_ID2, copyManager)
				.setDefiningDocument(EXTERNAL_DEFINING_DOCUMENT2)
				.setExternalId(EXTERNAL_ID2)
				.build());
		ListedLicenseExceptionBuilder llbldr = new ListedLicenseExceptionBuilder(store, "http://spdx.org/licenses/Autoconf-exception-2.0", copyManager);
		llbldr.setAdditionText("Autoconf exception 2.0 text");
		llbldr.setName("Autoconf exception 2.0 name");
		ListedLicenseException lex = llbldr.build();
		
		CustomLicense eli1 = new CustomLicense(store.getNextId(IdType.LicenseRef, null));
		eli1.setName("eli1");
		eli1.setLicenseText("eli1 text");
		ConjunctiveLicenseSet cls = new ConjunctiveLicenseSet(store, store.getNextId(IdType.Anonymous, null), copyManager, true);
		
		cls.addMember(new ListedLicense(store, "Apache-2.0", copyManager, true));
		cls.addMember(eli1);
		TEST_LIST_PROPERTY_VALUES = new List<?>[] {Arrays.asList("ListItem1", "listItem2", "listItem3"), 
			Arrays.asList(true, false, true),
			Arrays.asList(new ModelObject[] {lex, eli1}),
			Arrays.asList(new HashAlgorithm[] {HashAlgorithm.SHA256, HashAlgorithm.SHA1}),
			Arrays.asList(new Integer[] {1, 3, 5})};
		TEST_MODEL_OBJECT_PROP_VALUES = new ModelObject[] {lex, eli1};
		TEST_ANYLICENSEINFO_PROP_VALUES = new AnyLicenseInfo[] {new ListedLicense(store, "Apache-2.0", copyManager, true),
				eli1, new SpdxNoneLicense()};
		TEST_ANYLICENSEINFO_LIST_PROP_VALUES = new List<?>[] {Arrays.asList(new AnyLicenseInfo[] {new ListedLicense(store, "MIT", copyManager, true), 
				eli1, new ListedLicense(store, "GPL-2.0-only", copyManager, true)}),
			Arrays.asList(new AnyLicenseInfo[] {new SpdxNoAssertionLicense()}),
			Arrays.asList(new AnyLicenseInfo[] {cls, eli1})
		};
		
		ALL_PROPERTY_VALUES = new HashMap<>();
		for (int i = 0; i < TEST_STRING_VALUE_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_STRING_VALUE_PROPERTIES[i], 
					TEST_STRING_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_BOOLEAN_VALUE_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_BOOLEAN_VALUE_PROPERTIES[i], 
					TEST_BOOLEAN_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_LIST_PROPERTIES[i], 
					 TEST_LIST_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_MODEL_OJBECT_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_MODEL_OJBECT_PROPERTIES[i], 
					TEST_MODEL_OBJECT_PROP_VALUES[i]);
		}
		for (int i = 0; i < TEST_ENUM_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_ENUM_PROPERTIES[i], 
					TEST_ENUM_VALUES[i]);
		}
		for (int i = 0; i < TEST_ANYLICENSEINFO_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_ANYLICENSEINFO_PROPERTIES[i], 
					TEST_ANYLICENSEINFO_PROP_VALUES[i]);
		}
		for (int i = 0; i < TEST_ANYLICENSEINFO_LIST_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_ANYLICENSEINFO_LIST_PROPERTIES[i], 
					TEST_ANYLICENSEINFO_LIST_PROP_VALUES[i]);
		}
		for (int i = 0; i < TEST_INTEGER_VALUE_PROPERTIES.length; i++) {
			ALL_PROPERTY_VALUES.put(TEST_INTEGER_VALUE_PROPERTIES[i], 
					TEST_INTEGER_VALUE_PROPERTY_VALUES[i]);
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#ModelObject(org.spdx.storage.IModelStore, java.lang.String, org.spdx.library.ModelCopyManager, boolean)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testModelObjectIModelStoreStringModelCopyManagerBoolean() throws InvalidSPDXAnalysisException {
		try {
			new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, false);
			fail("This should not have worked since created is set to false and the ID does not exist");
		} catch (InvalidSPDXAnalysisException ex) {
			// expected
		}
		ModelObjectForTesting moft = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, true);
		moft.setPropertyValue(TEST_PROPERTY1, TEST_VALUE1);
		ModelObjectForTesting moft2 = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, false);
		assertTrue(moft2.getStringPropertyValue(TEST_PROPERTY1).isPresent());
		assertEquals(moft2.getStringPropertyValue(TEST_PROPERTY1).get(), TEST_VALUE1);
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#ModelObject(org.spdx.library.mobdel.ModelObjectBuilder)}.
	 */
	public void testModelObjectModelObjectBuilder() throws InvalidSPDXAnalysisException {
		ModelObjectForTesting moft = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, true);
		moft.setPropertyValue(TEST_PROPERTY1, TEST_VALUE1);
		moft.setExternalMap(externalMap);
		ModelObjectForTestingBuilder moftBuilder = new ModelObjectForTestingBuilder(moft, TEST_OBJECT_URI2);
		ModelObjectForTesting moft2 = new ModelObjectForTesting(moftBuilder);
		assertEquals(moft.getCopyManager(), moft2.getCopyManager());
		assertEquals(moft.getModelStore(), moft2.getModelStore());
		assertEquals(moft.getExternalMap(), moft2.getExternalMap());
		assertFalse(moft2.getStringPropertyValue(TEST_PROPERTY1).isPresent());
	}
	
	public void testSetExternalMap() throws InvalidSPDXAnalysisException {
		ModelObjectForTesting moft = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, true);
		assertTrue(moft.getExternalMap().isEmpty());
		moft.setExternalMap(externalMap);
		assertEquals(externalMap, moft.getExternalMap());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#verify(java.lang.String)}.
	 */
	public void testVerifyString() throws InvalidSPDXAnalysisException {
		ModelObjectForTesting moft = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, true);
		assertTrue(moft.verify().isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getObjectUri()}.
	 */
	public void testGetObjectUri() throws InvalidSPDXAnalysisException {
		ModelObjectForTesting moft = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, true);
		assertEquals(TEST_OBJECT_URI, moft.getObjectUri());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getModelStore()}.
	 */
	public void testGetModelStore() throws InvalidSPDXAnalysisException {
		ModelObjectForTesting moft = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, true);
		assertEquals(store, moft.getModelStore());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#setStrict(boolean)}.
	 */
	public void testSetStrict() throws InvalidSPDXAnalysisException {
		ModelObjectForTesting moft = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, true);
		assertFalse(moft.isStrict());
		moft.setStrict(true);
		assertTrue(moft.isStrict());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getPropertyValueDescriptors()}.
	 */
	public void testGetPropertyValueDescriptors() throws InvalidSPDXAnalysisException {
		ModelObjectForTesting moft = new ModelObjectForTesting(store, TEST_OBJECT_URI, copyManager, true);
		List<PropertyDescriptor> result = moft.getPropertyValueDescriptors();
		assertEquals(0, result.size());
		addTestValues(moft);
		result =moft.getPropertyValueDescriptors();
		assertEquals(ALL_PROPERTY_VALUES.size(), result.size());
		for (PropertyDescriptor property:ALL_PROPERTY_VALUES.keySet()) {
			assertTrue(result.contains(property));
		}
	}
	
	protected void addTestValues(ModelObject mo) throws InvalidSPDXAnalysisException {
		for (Entry<PropertyDescriptor, Object> entry:ALL_PROPERTY_VALUES.entrySet()) {
			mo.setPropertyValue(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getObjectPropertyValue(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testGetObjectPropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#setPropertyValue(org.spdx.storage.PropertyDescriptor, java.lang.Object)}.
	 */
	public void testSetPropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updatePropertyValue(org.spdx.storage.PropertyDescriptor, java.lang.Object)}.
	 */
	public void testUpdatePropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getStringPropertyValue(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testGetStringPropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getIntegerPropertyValue(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testGetIntegerPropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getEnumPropertyValue(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testGetEnumPropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getBooleanPropertyValue(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testGetBooleanPropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getAnyLicenseInfoPropertyValue(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testGetAnyLicenseInfoPropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getElementPropertyValue(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testGetElementPropertyValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#removeProperty(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testRemoveProperty() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateRemoveProperty(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testUpdateRemoveProperty() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#clearValueCollection(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testClearValueCollection() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateClearValueCollection(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testUpdateClearValueCollection() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#addPropertyValueToCollection(org.spdx.storage.PropertyDescriptor, java.lang.Object)}.
	 */
	public void testAddPropertyValueToCollection() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateAddPropertyValueToCollection(org.spdx.storage.PropertyDescriptor, java.lang.Object)}.
	 */
	public void testUpdateAddPropertyValueToCollection() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#removePropertyValueFromCollection(org.spdx.storage.PropertyDescriptor, java.lang.Object)}.
	 */
	public void testRemovePropertyValueFromCollection() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#updateRemovePropertyValueFromCollection(org.spdx.storage.PropertyDescriptor, java.lang.Object)}.
	 */
	public void testUpdateRemovePropertyValueFromCollection() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getObjectPropertyValueSet(org.spdx.storage.PropertyDescriptor, java.lang.Class)}.
	 */
	public void testGetObjectPropertyValueSet() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getObjectPropertyValueCollection(org.spdx.storage.PropertyDescriptor, java.lang.Class)}.
	 */
	public void testGetObjectPropertyValueCollection() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getStringCollection(org.spdx.storage.PropertyDescriptor)}.
	 */
	public void testGetStringCollection() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#isCollectionMembersAssignableTo(org.spdx.storage.PropertyDescriptor, java.lang.Class)}.
	 */
	public void testIsCollectionMembersAssignableTo() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#equivalent(org.spdx.library.model.ModelObject)}.
	 */
	public void testEquivalentModelObject() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#clone(org.spdx.storage.IModelStore)}.
	 */
	public void testCloneIModelStore() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#copyFrom(org.spdx.library.model.ModelObject)}.
	 */
	public void testCopyFrom() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#copyFromV2(org.spdx.library.model.compat.v2.ModelObject)}.
	 */
	public void testCopyFromV2() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#setCopyManager(org.spdx.library.ModelCopyManager)}.
	 */
	public void testSetCopyManager() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#getCopyManager()}.
	 */
	public void testGetCopyManager() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.ModelObject#toTypedValue()}.
	 */
	public void testToTypedValue() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

}
