/*
 * SPDX-FileCopyrightText: Copyright (c) 2020 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.storage.listedlicense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.InvalidSpdxPropertyException;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.CrossRef;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class CrossRefJsonV2Test extends TestCase {
	
	static final List<PropertyDescriptor> STRING_PROPERTY_VALUE_NAMES = Arrays.asList(
			SpdxConstantsCompatV2.PROP_CROSS_REF_MATCH,
			SpdxConstantsCompatV2.PROP_CROSS_REF_TIMESTAMP,
			SpdxConstantsCompatV2.PROP_CROSS_REF_URL);
	
	static final List<PropertyDescriptor> BOOLEAN_PROPERTY_VALUE_NAMES = Arrays.asList(
			SpdxConstantsCompatV2.PROP_CROSS_REF_IS_LIVE,
			SpdxConstantsCompatV2.PROP_CROSS_REF_IS_VALID,
			SpdxConstantsCompatV2.PROP_CROSS_REF_WAYBACK_LINK
			);
	
	static final List<PropertyDescriptor> INTEGER_PROPERTY_VALUE_NAMES = Arrays.asList(SpdxConstantsCompatV2.PROP_CROSS_REF_ORDER);
	
	static final List<PropertyDescriptor> PROPERTY_VALUE_NAMES = new ArrayList<>();
	
	static {
		PROPERTY_VALUE_NAMES.addAll(STRING_PROPERTY_VALUE_NAMES);
		PROPERTY_VALUE_NAMES.addAll(BOOLEAN_PROPERTY_VALUE_NAMES);
		PROPERTY_VALUE_NAMES.addAll(INTEGER_PROPERTY_VALUE_NAMES);
	}

	protected void setUp() throws Exception {
		super.setUp();
		SpdxModelFactory.init();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCrossRefJsonCrossRef() throws InvalidSPDXAnalysisException {
		IModelStore modelStore = new InMemSpdxStore();
		String docUri = "http://doc/uri";
		String id = "tempid";
		String url = "http://url";
		Boolean isWayBackLink = true;
		Boolean isLive = false;
		String match = "match";
		Integer order = 12;
		String timestamp = "time and date";
		Boolean isValid = true;
		CrossRef inputCrossRef = new CrossRef(modelStore, docUri, id, null, true);
		inputCrossRef.setUrl(url);
		inputCrossRef.setIsWayBackLink(isWayBackLink);
		inputCrossRef.setLive(isLive);
		inputCrossRef.setMatch(match);
		inputCrossRef.setOrder(order);
		inputCrossRef.setTimestamp(timestamp);
		inputCrossRef.setValid(isValid);
		CrossRefJson result = new CrossRefJson(inputCrossRef);
		assertEquals(id, result.getId());
		assertEquals(url, result.url);
		assertEquals(isWayBackLink, result.isWayBackLink);
		assertEquals(isLive, result.isLive);
		assertEquals(match, result.match);
		assertEquals(order, result.order);
		assertEquals(timestamp, result.timestamp);
		assertEquals(isValid, result.isValid);
	}

	public void testGetPropertyValueNames() throws InvalidSpdxPropertyException {
		CrossRefJson crj = new CrossRefJson();
		List<PropertyDescriptor> result = crj.getPropertyValueDescriptors();
		assertEquals(0, result.size());
		for (PropertyDescriptor valueName:STRING_PROPERTY_VALUE_NAMES) {
			crj.setPrimitiveValue(valueName, "ValueFor"+valueName);
		}
		for (PropertyDescriptor valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			crj.setPrimitiveValue(valueName, false);
		}
		int i = 1;
		for (PropertyDescriptor valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			crj.setPrimitiveValue(valueName, i++);
		}
		result = crj.getPropertyValueDescriptors();
		assertEquals(PROPERTY_VALUE_NAMES.size(), result.size());
		for (PropertyDescriptor valueName:PROPERTY_VALUE_NAMES) {
			if (!result.contains(valueName)) {
				fail("Missing "+valueName);
			}
		}
	}

	public void testSetPrimitiveValue() throws InvalidSpdxPropertyException {
		Map<PropertyDescriptor, String> stringValues = new HashMap<>();
		CrossRefJson crj = new CrossRefJson();
		for (PropertyDescriptor valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			crj.setPrimitiveValue(valueName, stringValues.get(valueName));
		}
		Map<PropertyDescriptor, Boolean> booleanValues = new HashMap<>();
		for (PropertyDescriptor valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			booleanValues.put(valueName, false);
			crj.setPrimitiveValue(valueName, booleanValues.get(valueName));
		}
		Map<PropertyDescriptor, Integer> integerValues = new HashMap<>();
		int i = 1;
		for (PropertyDescriptor valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			integerValues.put(valueName, i++);
			crj.setPrimitiveValue(valueName, integerValues.get(valueName));
		}
		for (PropertyDescriptor valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), crj.getValue(valueName));
		}
		for (PropertyDescriptor valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), crj.getValue(valueName));
		}
		for (PropertyDescriptor valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			assertEquals(integerValues.get(valueName), crj.getValue(valueName));
		}
	}

	public void testGetId() {
		CrossRefJson crj = new CrossRefJson();
		assertTrue(Objects.isNull(crj.getId()));
		String id = "objectUri";
		crj.setId(id);
		assertEquals(id, crj.getId());
	}

	public void testRemoveProperty() throws InvalidSpdxPropertyException {
		Map<PropertyDescriptor, String> stringValues = new HashMap<>();
		CrossRefJson crj = new CrossRefJson();
		for (PropertyDescriptor valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			crj.setPrimitiveValue(valueName, stringValues.get(valueName));
		}
		Map<PropertyDescriptor, Boolean> booleanValues = new HashMap<>();
		for (PropertyDescriptor valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			booleanValues.put(valueName, false);
			crj.setPrimitiveValue(valueName, booleanValues.get(valueName));
		}
		Map<PropertyDescriptor, Integer> integerValues = new HashMap<>();
		int i = 1;
		for (PropertyDescriptor valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			integerValues.put(valueName, i++);
			crj.setPrimitiveValue(valueName, integerValues.get(valueName));
		}
		for (PropertyDescriptor valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), crj.getValue(valueName));
		}
		for (PropertyDescriptor valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), crj.getValue(valueName));
		}
		for (PropertyDescriptor valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			assertEquals(integerValues.get(valueName), crj.getValue(valueName));
		}
		
		for (PropertyDescriptor valueName:PROPERTY_VALUE_NAMES) {
			crj.removeProperty(valueName);
		}
		
		for (PropertyDescriptor valueName:PROPERTY_VALUE_NAMES) {
			assertTrue(Objects.isNull(crj.getValue(valueName)));
		}
	}

	public void testIsPropertyValueAssignableTo() throws InvalidSpdxPropertyException {
		CrossRefJson crj = new CrossRefJson();
		for (PropertyDescriptor valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertTrue(crj.isPropertyValueAssignableTo(valueName, String.class));
			assertFalse(crj.isPropertyValueAssignableTo(valueName, Boolean.class));
			assertFalse(crj.isPropertyValueAssignableTo(valueName, Integer.class));
		}
		for (PropertyDescriptor valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertFalse(crj.isPropertyValueAssignableTo(valueName, String.class));
			assertTrue(crj.isPropertyValueAssignableTo(valueName, Boolean.class));
			assertFalse(crj.isPropertyValueAssignableTo(valueName, Integer.class));
		}
		for (PropertyDescriptor valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			assertFalse(crj.isPropertyValueAssignableTo(valueName, String.class));
			assertFalse(crj.isPropertyValueAssignableTo(valueName, Boolean.class));
			assertTrue(crj.isPropertyValueAssignableTo(valueName, Integer.class));
		}
	}

}
