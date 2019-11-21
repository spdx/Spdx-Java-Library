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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.InvalidSpdxPropertyException;

import com.google.gson.Gson;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ExceptionJsonTest extends TestCase implements SpdxConstants {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */

	static final List<String> STRING_PROPERTY_VALUE_NAMES = Arrays.asList(
			PROP_LICENSE_EXCEPTION_ID, PROP_EXCEPTION_TEXT, 
			PROP_STD_LICENSE_NAME, RDFS_PROP_COMMENT, PROP_EXCEPTION_TEMPLATE, 
			PROP_EXAMPLE, PROP_LIC_DEPRECATED_VERSION
			);
	
	static final List<String> BOOLEAN_PROPERTY_VALUE_NAMES = Arrays.asList(
			PROP_LIC_ID_DEPRECATED
			);
	
	static final List<String> PROPERTY_VALUE_NAMES = new ArrayList<>();
	static {
		PROPERTY_VALUE_NAMES.addAll(STRING_PROPERTY_VALUE_NAMES);
		PROPERTY_VALUE_NAMES.addAll(BOOLEAN_PROPERTY_VALUE_NAMES);
	}
	static final List<String> PROPERTY_VALUE_LIST_NAMES = Arrays.asList(RDFS_PROP_SEE_ALSO);

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

	public void testExceptionJson() {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		assertEquals(exceptionId, ej.licenseExceptionId);
	}

	public void testGetPropertyValueNames() {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		List<String> result = ej.getPropertyValueNames();
		assertEquals(PROPERTY_VALUE_NAMES.size(), result.size());
		for (String valueName:PROPERTY_VALUE_NAMES) {
			if (!result.contains(valueName)) {
				fail("Missing "+valueName);
			}
		}
	}

	public void testGetPropertyValueListNames() {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		List<String> result = ej.getPropertyValueListNames();
		assertEquals(PROPERTY_VALUE_LIST_NAMES.size(), result.size());
		for (String valueName:PROPERTY_VALUE_LIST_NAMES) {
			assertTrue(result.contains(valueName));
		}
	}

	public void testSetTypedProperty() {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		try {
			ej.setTypedProperty("TestPropertyName", "SpdxId22", CLASS_SPDX_ELEMENT);
			fail("This shouldn't work");
		} catch (InvalidSPDXAnalysisException e) {
			// Expected
		}
	}

	public void testGetSetPrimativeValue() throws InvalidSpdxPropertyException {
		Map<String, String> stringValues = new HashMap<>();
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			ej.setPrimativeValue(valueName, stringValues.get(valueName));
		}
		Map<String, Boolean> booleanValues = new HashMap<>();
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			booleanValues.put(valueName, false);
			ej.setPrimativeValue(valueName, booleanValues.get(valueName));
		}
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), ej.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), ej.getValue(valueName));
		}
	}

	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueList() throws InvalidSpdxPropertyException {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		List<String> result = (List<String>) ej.getValueList("seeAlso");
		assertEquals(0, result.size());
		String firstItem = "first";
		String secondItem = "second";
		ej.addPrimitiveValueToList("seeAlso", firstItem);
		result = (List<String>) ej.getValueList("seeAlso");
		assertEquals(1, result.size());
		assertEquals(firstItem, result.get(0));
		ej.addPrimitiveValueToList("seeAlso", secondItem);
		result = (List<String>) ej.getValueList("seeAlso");
		assertEquals(2, result.size());
		assertEquals(firstItem, result.get(0));
		assertEquals(secondItem, result.get(1));
		ej.clearPropertyValueList("seeAlso");
		result = (List<String>) ej.getValueList("seeAlso");
		assertEquals(0, result.size());
	}
	
	public void testJson() throws Exception {
		StringBuilder json = new StringBuilder("{\n");
		Map<String, String> stringValues = new HashMap<>();
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			json.append("\t\"");
			json.append(valueName);
			json.append("\":\"");
			json.append(stringValues.get(valueName));
			json.append("\",\n");
		}
		Map<String, Boolean> booleanValues = new HashMap<>();
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			booleanValues.put(valueName, false);
			json.append("\t\"");
			json.append(valueName);
			json.append("\":\"");
			json.append(booleanValues.get(valueName));
			json.append("\",\n");
		}
		List<String> seeAlsoValues = Arrays.asList("seeAlso1", "seeAlso2");
		json.append("\t\"seeAlso\": [\n\t\t\"");
		json.append(seeAlsoValues.get(0));
		for (int i = 1; i < seeAlsoValues.size(); i++) {
			json.append("\",\n\t\t\"");
			json.append(seeAlsoValues.get(i));
		}
		json.append("\"\n\t]\n}");
		Gson gson = new Gson();
		ExceptionJson ej = gson.fromJson(json.toString(), ExceptionJson.class);
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), ej.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), ej.getValue(valueName));
		}
		@SuppressWarnings("unchecked")
		List<String> seeAlsoResult = (List<String>)ej.getValueList("seeAlso");
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
	}
	
	public void testLegacyJson() throws Exception {
		//TODO: In SPDX 3.0 this test should be removed once Spec issue #158 is resolved (https://github.com/spdx/spdx-spec/issues/158)
		StringBuilder json = new StringBuilder("{\n");
		Map<String, String> stringValues = new HashMap<>();
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			json.append("\t\"");
			if (RDFS_PROP_COMMENT.equals(valueName)) {
				json.append("licenseComments");	// Legacy value
			} else {
				json.append(valueName);
			}
			json.append("\":\"");
			json.append(stringValues.get(valueName));
			json.append("\",\n");
		}
		Map<String, Boolean> booleanValues = new HashMap<>();
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			booleanValues.put(valueName, false);
			json.append("\t\"");
			json.append(valueName);
			json.append("\":\"");
			json.append(booleanValues.get(valueName));
			json.append("\",\n");
		}
		List<String> seeAlsoValues = Arrays.asList("seeAlso1", "seeAlso2");
		json.append("\t\"seeAlso\": [\n\t\t\"");
		json.append(seeAlsoValues.get(0));
		for (int i = 1; i < seeAlsoValues.size(); i++) {
			json.append("\",\n\t\t\"");
			json.append(seeAlsoValues.get(i));
		}
		json.append("\"\n\t]\n}");
		Gson gson = new Gson();
		ExceptionJson ej = gson.fromJson(json.toString(), ExceptionJson.class);
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), ej.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), ej.getValue(valueName));
		}
		@SuppressWarnings("unchecked")
		List<String> seeAlsoResult = (List<String>)ej.getValueList("seeAlso");
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
	}
}
