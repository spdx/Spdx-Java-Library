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

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.TypedValue;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class StoredTypedItemTest extends TestCase {

	static final String TEST_ID1 = "TestID1";
	static final String TEST_ID2 = "TestID2";
	static final String TEST_TYPE1 = SpdxConstants.CLASS_ANNOTATION;
	static final String TEST_TYPE2 = SpdxConstants.CLASS_RELATIONSHIP;
	static final String TEST_DOCUMENTURI1 = "https://test.doc.uri1";
	static final String TEST_DOCUMENTURI2 = "https://test.doc.uri2";
	static final String[] TEST_VALUE_PROPERTIES = new String[] {"valueProp1", "valueProp2", "valueProp3", "valueProp4"};
	static final Object[] TEST_VALUE_PROPERTY_VALUES = new Object[] {"value1", true, "value2", null};
	static final String[] TEST_LIST_PROPERTIES = new String[] {"listProp1", "listProp2", "listProp3"};
	TypedValue[] TEST_TYPED_PROP_VALUES;
	List<?>[] TEST_LIST_PROPERTY_VALUES;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		TEST_LIST_PROPERTY_VALUES = new List<?>[] {Arrays.asList("ListItem1", "listItem2", "listItem3"), 
			Arrays.asList(true, false, true),
			Arrays.asList(new TypedValue(TEST_DOCUMENTURI1, "typeId1", TEST_TYPE1), new TypedValue(TEST_DOCUMENTURI1, "typeId2", TEST_TYPE2))};
			TEST_VALUE_PROPERTY_VALUES[3] = new TypedValue(TEST_DOCUMENTURI1, "typeId3", TEST_TYPE1);
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
		StoredTypedItem sti = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID1, TEST_TYPE1);
		StoredTypedItem sti2 = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID1, TEST_TYPE1);
		assertTrue(sti.equals(sti2));
		assertTrue(sti2.equals(sti2));
		StoredTypedItem sti3 = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID2, TEST_TYPE1);
		assertFalse(sti.equals(sti3));
		assertFalse(sti3.equals(sti));
		StoredTypedItem sti4 = new StoredTypedItem(TEST_DOCUMENTURI2, TEST_ID1, TEST_TYPE2);
		assertFalse(sti.equals(sti4));
		assertFalse(sti4.equals(sti));
	}

	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#getPropertyValueNames(java.lang.String, java.lang.String)}.
	 */
	public void testGetSetPropertyValueNames() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID1, TEST_TYPE1);
		assertEquals(0, sti.getPropertyValueNames().size());
		for (int i = 0; i < TEST_VALUE_PROPERTIES.length; i++) {
			sti.setValue(TEST_VALUE_PROPERTIES[i], TEST_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		List<String> result = sti.getPropertyValueNames();
		assertEquals(TEST_VALUE_PROPERTIES.length + TEST_LIST_PROPERTIES.length, result.size());
		for (String prop:TEST_VALUE_PROPERTIES) {
			assertTrue(result.contains(prop));
		}
		for (String prop:TEST_LIST_PROPERTIES) {
			assertTrue(result.contains(prop));
		}
	}

	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#setValue(java.lang.String, java.lang.Object)}.
	 */
	public void testGetSetValue() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID1, TEST_TYPE1);
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
		StoredTypedItem sti = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID1, TEST_TYPE1);
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		assertEquals(TEST_LIST_PROPERTY_VALUES[0].size(), sti.getValueList(TEST_LIST_PROPERTIES[0]).size());
		sti.clearPropertyValueList(TEST_LIST_PROPERTIES[0]);
		assertEquals(0, sti.getValueList(TEST_LIST_PROPERTIES[0]).size());
	}


	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#addValueToList(java.lang.String, java.lang.Object)}.
	 */
	public void testAddValueToList() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID1, TEST_TYPE1);
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		assertEquals(TEST_LIST_PROPERTY_VALUES[0].size(), sti.getValueList(TEST_LIST_PROPERTIES[0]).size());
		String newValue = "new Value";
		sti.addValueToList(TEST_LIST_PROPERTIES[0], newValue);
		assertEquals(TEST_LIST_PROPERTY_VALUES[0].size()+1, sti.getValueList(TEST_LIST_PROPERTIES[0]).size());
		assertTrue(sti.getValueList(TEST_LIST_PROPERTIES[0]).contains(newValue));
	}

	/**
	 * Test method for {@link org.spdx.storage.simple.StoredTypedItem#getValueList(java.lang.String)}.
	 */
	public void testGetValueList() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID1, TEST_TYPE1);
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		assertEquals(TEST_LIST_PROPERTY_VALUES[0].size(), sti.getValueList(TEST_LIST_PROPERTIES[0]).size());
		for (Object val:TEST_LIST_PROPERTY_VALUES[0]) {
			assertTrue(sti.getValueList(TEST_LIST_PROPERTIES[0]).contains(val));
		}
	}
	
	public void testRemove() throws InvalidSPDXAnalysisException {
		StoredTypedItem sti = new StoredTypedItem(TEST_DOCUMENTURI1, TEST_ID1, TEST_TYPE1);
		assertEquals(0, sti.getPropertyValueNames().size());
		for (int i = 0; i < TEST_VALUE_PROPERTIES.length; i++) {
			sti.setValue(TEST_VALUE_PROPERTIES[i], TEST_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				sti.addValueToList(TEST_LIST_PROPERTIES[i], value);
			}
		}
		assertEquals(TEST_VALUE_PROPERTIES.length + TEST_LIST_PROPERTIES.length, sti.getPropertyValueNames().size());
		sti.removeProperty(TEST_VALUE_PROPERTIES[0]);
		sti.removeProperty(TEST_LIST_PROPERTIES[0]);
		assertEquals(TEST_VALUE_PROPERTIES.length-1+TEST_LIST_PROPERTIES.length-1, sti.getPropertyValueNames().size());
		assertTrue(sti.getValue(TEST_VALUE_PROPERTIES[0]) == null);
		assertTrue(sti.getValue(TEST_LIST_PROPERTIES[0]) == null);
	}

}
