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
package org.spdx.storage.simple;

import java.util.Arrays;
import java.util.List;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.TypedValue;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.storage.PropertyDescriptor;

import junit.framework.TestCase;

/**
 * @author Gary O'Neall
 */
public class StoredTypedItemTest extends TestCase {

	static final String TEST_ID1 = "TestID1";
	static final String TEST_ID2 = "TestID2";
	static final String TEST_TYPE1 = SpdxConstantsCompatV2.CLASS_ANNOTATION;
	static final String TEST_TYPE2 = SpdxConstantsCompatV2.CLASS_RELATIONSHIP;
	static final String TEST_NAMESPACE1 = "https://test.doc.uri1";
	static final String TEST_NAMESPACE2 = "https://test.doc.uri2";
	static final String TEST_OBJECT_URI1 = TEST_NAMESPACE1 + "#" + TEST_ID1;
	static final String TEST_OBJECT_URI2 = TEST_NAMESPACE2 + "#" + TEST_ID2;
	static final PropertyDescriptor[] TEST_VALUE_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("valueProp1", SpdxConstantsCompatV2.SPDX_NAMESPACE), 
			new PropertyDescriptor("valueProp2", SpdxConstantsCompatV2.SPDX_NAMESPACE), 
			new PropertyDescriptor("valueProp3", SpdxConstantsCompatV2.SPDX_NAMESPACE), 
			new PropertyDescriptor("valueProp4", SpdxConstantsCompatV2.SPDX_NAMESPACE)};
	static final Object[] TEST_VALUE_PROPERTY_VALUES = new Object[] {"value1", true, "value2", null};
	static final PropertyDescriptor[] TEST_LIST_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("listProp1", SpdxConstantsCompatV2.SPDX_NAMESPACE), 
			new PropertyDescriptor("listProp2", SpdxConstantsCompatV2.SPDX_NAMESPACE), 
			new PropertyDescriptor("listProp3", SpdxConstantsCompatV2.SPDX_NAMESPACE)};
	List<?>[] TEST_LIST_PROPERTY_VALUES;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		SpdxModelFactory.init();
		TEST_LIST_PROPERTY_VALUES = new List<?>[] {Arrays.asList("ListItem1", "listItem2", "listItem3"), 
			Arrays.asList(true, false, true),
			Arrays.asList(new TypedValue("typeId1", TEST_TYPE1, "SPDX-2.3"), new TypedValue("typeId2", TEST_TYPE2, "SPDX-2.3"))};
			TEST_VALUE_PROPERTY_VALUES[3] = new TypedValue("typeId3", TEST_TYPE1, "SPDX-2.3");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		StoredTypedItem sti2 = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		assertTrue(sti.equals(sti2));
		assertTrue(sti2.equals(sti));
		StoredTypedItem sti3 = new StoredTypedItem(TEST_OBJECT_URI2, TEST_TYPE1, "SPDX-2.3");
		assertFalse(sti.equals(sti3));
		assertFalse(sti3.equals(sti));
		StoredTypedItem sti4 = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE2, "SPDX-2.3");
		assertFalse(sti.equals(sti4));
		assertFalse(sti4.equals(sti));
	}

	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#getPropertyValueNames(java.lang.String, java.lang.String)}.
	 */
	public void testGetSetPropertyValueNames() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		assertEquals(0, sti.getPropertyValueDescriptors().size());
		for (int i = 0; i < TEST_VALUE_PROPERTIES.length; i++) {
			sti.setValue(TEST_VALUE_PROPERTIES[i], TEST_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		List<PropertyDescriptor> result = sti.getPropertyValueDescriptors();
		assertEquals(TEST_VALUE_PROPERTIES.length + TEST_LIST_PROPERTIES.length, result.size());
		for (PropertyDescriptor prop:TEST_VALUE_PROPERTIES) {
			assertTrue(result.contains(prop));
		}
		for (PropertyDescriptor prop:TEST_LIST_PROPERTIES) {
			assertTrue(result.contains(prop));
		}
	}

	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#setValue(java.lang.String, java.lang.Object)}.
	 */
	public void testGetSetValue() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		Object result = sti.getValue(TEST_VALUE_PROPERTIES[0]);
		assertTrue(result == null);
		sti.setValue(TEST_VALUE_PROPERTIES[0], TEST_VALUE_PROPERTY_VALUES[0]);
		assertEquals(TEST_VALUE_PROPERTY_VALUES[0], sti.getValue(TEST_VALUE_PROPERTIES[0]));
		sti.setValue(TEST_VALUE_PROPERTIES[0], TEST_VALUE_PROPERTY_VALUES[1]);
		assertEquals(TEST_VALUE_PROPERTY_VALUES[1], sti.getValue(TEST_VALUE_PROPERTIES[0]));
	}

	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#clearPropertyValueList(java.lang.String)}.
	 */
	public void testClearPropertyValueList() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		assertEquals(TEST_LIST_PROPERTY_VALUES[0].size(), InMemSpdxStoreTest.toImmutableList(sti.getValueList(TEST_LIST_PROPERTIES[0])).size());
		sti.clearPropertyValueList(TEST_LIST_PROPERTIES[0]);
		assertEquals(0, InMemSpdxStoreTest.toImmutableList(sti.getValueList(TEST_LIST_PROPERTIES[0])).size());
	}


	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#addValueToList(java.lang.String, java.lang.Object)}.
	 */
	public void testAddValueToList() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		assertEquals(TEST_LIST_PROPERTY_VALUES[0].size(), InMemSpdxStoreTest.toImmutableList(sti.getValueList(TEST_LIST_PROPERTIES[0])).size());
		String newValue = "new Value";
		sti.addValueToList(TEST_LIST_PROPERTIES[0], newValue);
		assertEquals(TEST_LIST_PROPERTY_VALUES[0].size()+1, InMemSpdxStoreTest.toImmutableList(sti.getValueList(TEST_LIST_PROPERTIES[0])).size());
		assertTrue(InMemSpdxStoreTest.toImmutableList(sti.getValueList(TEST_LIST_PROPERTIES[0])).contains(newValue));
	}

	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#getValueList(java.lang.String)}.
	 */
	public void testGetValueList() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		assertEquals(TEST_LIST_PROPERTY_VALUES[0].size(), InMemSpdxStoreTest.toImmutableList(sti.getValueList(TEST_LIST_PROPERTIES[0])).size());
		for (Object val:TEST_LIST_PROPERTY_VALUES[0]) {
			assertTrue(InMemSpdxStoreTest.toImmutableList(sti.getValueList(TEST_LIST_PROPERTIES[0])).contains(val));
		}
	}
	
	public void testRemove() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		assertEquals(0, sti.getPropertyValueDescriptors().size());
		for (int i = 0; i < TEST_VALUE_PROPERTIES.length; i++) {
			sti.setValue(TEST_VALUE_PROPERTIES[i], TEST_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		assertEquals(TEST_VALUE_PROPERTIES.length + TEST_LIST_PROPERTIES.length, sti.getPropertyValueDescriptors().size());
		sti.removeProperty(TEST_VALUE_PROPERTIES[0]);
		sti.removeProperty(TEST_LIST_PROPERTIES[0]);
		assertEquals(TEST_VALUE_PROPERTIES.length-1+TEST_LIST_PROPERTIES.length-1, sti.getPropertyValueDescriptors().size());
		assertTrue(sti.getValue(TEST_VALUE_PROPERTIES[0]) == null);
		assertTrue(sti.getValue(TEST_LIST_PROPERTIES[0]) == null);
	}
	
	public void testCollectionSize() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			assertEquals(TEST_LIST_PROPERTY_VALUES[i].size(), sti.collectionSize(TEST_LIST_PROPERTIES[i]));
		}
	}
	
	public void testCollectionContains() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				assertTrue(sti.collectionContains(TEST_LIST_PROPERTIES[i], value));
			}
		}
		assertFalse(sti.collectionContains(TEST_LIST_PROPERTIES[0], "notthere"));
	}
	
	public void testIsPropertyValueAssignableTo() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		// String
		PropertyDescriptor sProperty = new PropertyDescriptor("stringprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.addValueToList(sProperty, "String 1");
		sti.addValueToList(sProperty, "String 2");
		assertTrue(sti.isCollectionMembersAssignableTo(sProperty, String.class));
		assertFalse(sti.isCollectionMembersAssignableTo(sProperty, Boolean.class));
		assertFalse(sti.isCollectionMembersAssignableTo(sProperty, TypedValue.class));
		// Boolean
		PropertyDescriptor bProperty = new PropertyDescriptor("boolprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.addValueToList(bProperty, Boolean.valueOf(true));
		sti.addValueToList(bProperty, Boolean.valueOf(false));
		assertFalse(sti.isCollectionMembersAssignableTo(bProperty, String.class));
		assertTrue(sti.isCollectionMembersAssignableTo(bProperty, Boolean.class));
		assertFalse(sti.isCollectionMembersAssignableTo(bProperty, TypedValue.class));
		// TypedValue
		PropertyDescriptor tvProperty = new PropertyDescriptor("tvprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.addValueToList(tvProperty, new TypedValue(TEST_ID2, TEST_TYPE2, "SPDX-2.3"));
		assertFalse(sti.isCollectionMembersAssignableTo(tvProperty, String.class));
		assertFalse(sti.isCollectionMembersAssignableTo(tvProperty, Boolean.class));
		assertTrue(sti.isCollectionMembersAssignableTo(tvProperty, Relationship.class));
		// Mixed
		PropertyDescriptor mixedProperty = new PropertyDescriptor("mixedprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.addValueToList(mixedProperty, Boolean.valueOf(true));
		sti.addValueToList(mixedProperty, "mixed value");
		assertFalse(sti.isCollectionMembersAssignableTo(mixedProperty, String.class));
		assertFalse(sti.isCollectionMembersAssignableTo(mixedProperty, Boolean.class));
		assertFalse(sti.isCollectionMembersAssignableTo(mixedProperty, TypedValue.class));
		// Empty
		PropertyDescriptor emptyProperty = new PropertyDescriptor("emptyprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		assertTrue(sti.isCollectionMembersAssignableTo(emptyProperty, String.class));
		assertTrue(sti.isCollectionMembersAssignableTo(emptyProperty, Boolean.class));
		assertTrue(sti.isCollectionMembersAssignableTo(emptyProperty, TypedValue.class));
	}
	
	public void testCollectionMembersAssignableTo() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		// String
		PropertyDescriptor sProperty = new PropertyDescriptor("stringprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.setValue(sProperty, "String 1");
		assertTrue(sti.isPropertyValueAssignableTo(sProperty, String.class, "SPDX-2.3"));
		assertFalse(sti.isPropertyValueAssignableTo(sProperty, Boolean.class, "SPDX-2.3"));
		assertFalse(sti.isPropertyValueAssignableTo(sProperty, TypedValue.class, "SPDX-2.3"));
		// Boolean
		PropertyDescriptor bProperty = new PropertyDescriptor("boolprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.setValue(bProperty, Boolean.valueOf(true));
		assertFalse(sti.isPropertyValueAssignableTo(bProperty, String.class, "SPDX-2.3"));
		assertTrue(sti.isPropertyValueAssignableTo(bProperty, Boolean.class, "SPDX-2.3"));
		assertFalse(sti.isPropertyValueAssignableTo(bProperty, TypedValue.class, "SPDX-2.3"));
		// TypedValue
		PropertyDescriptor tvProperty = new PropertyDescriptor("tvprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.setValue(tvProperty, new TypedValue(TEST_ID2, TEST_TYPE2, "SPDX-2.3"));
		assertFalse(sti.isPropertyValueAssignableTo(tvProperty, String.class, "SPDX-2.3"));
		assertFalse(sti.isPropertyValueAssignableTo(tvProperty, Boolean.class, "SPDX-2.3"));
		assertTrue(sti.isPropertyValueAssignableTo(tvProperty, TypedValue.class, "SPDX-2.3"));
		// Empty
		PropertyDescriptor emptyProperty = new PropertyDescriptor("emptyprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		assertFalse(sti.isPropertyValueAssignableTo(emptyProperty, String.class, "SPDX-2.3"));
	}
	
	public void testIsCollectionProperty() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_OBJECT_URI1, TEST_TYPE1, "SPDX-2.3");
		// String
		PropertyDescriptor sProperty = new PropertyDescriptor("stringprop", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.setValue(sProperty, "String 1");
		PropertyDescriptor listProperty = new PropertyDescriptor("listProp", SpdxConstantsCompatV2.SPDX_NAMESPACE);
		sti.addValueToList(listProperty, "testValue");
		assertTrue(sti.isCollectionProperty(listProperty));
		assertFalse(sti.isCollectionProperty(sProperty));
	}

}
