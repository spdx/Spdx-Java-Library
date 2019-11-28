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
public class LicenseJsonTest extends TestCase implements SpdxConstants {
	
	static final List<String> STRING_PROPERTY_VALUE_NAMES = Arrays.asList(
			PROP_LICENSE_ID, PROP_LICENSE_TEXT, PROP_LICENSE_TEXT_HTML, 
			PROP_STD_LICENSE_NAME, RDFS_PROP_COMMENT, PROP_STD_LICENSE_NOTICE,PROP_STD_LICENSE_HEADER_TEMPLATE,
			PROP_LICENSE_HEADER_HTML, PROP_STD_LICENSE_TEMPLATE, PROP_EXAMPLE, PROP_LIC_DEPRECATED_VERSION
			);
	
	static final List<String> BOOLEAN_PROPERTY_VALUE_NAMES = Arrays.asList(
			PROP_STD_LICENSE_OSI_APPROVED, PROP_STD_LICENSE_FSF_LIBRE, PROP_LIC_ID_DEPRECATED
			);
	
	static final List<String> PROPERTY_VALUE_NAMES = new ArrayList<>();
	static final List<String> PROPERTY_VALUE_LIST_NAMES = Arrays.asList(RDFS_PROP_SEE_ALSO);
	static {
		PROPERTY_VALUE_NAMES.addAll(STRING_PROPERTY_VALUE_NAMES);
		PROPERTY_VALUE_NAMES.addAll(BOOLEAN_PROPERTY_VALUE_NAMES);
		PROPERTY_VALUE_NAMES.addAll(PROPERTY_VALUE_LIST_NAMES);
	}
	

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
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJson#LicenseJson(java.lang.String)}.
	 */
	public void testLicenseJson() {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		assertEquals(licenseId, lj.licenseId);
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJson#getPropertyValueNames()}.
	 */
	public void testGetPropertyValueNames() {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		List<String> result = lj.getPropertyValueNames();
		assertEquals(PROPERTY_VALUE_NAMES.size(), result.size());
		for (String valueName:PROPERTY_VALUE_NAMES) {
			if (!result.contains(valueName)) {
				fail("Missing "+valueName);
			}
		}
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJson#setTypedProperty(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testSetTypedProperty() {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		try {
			lj.setTypedProperty("TestPropertyName", "SpdxId22", CLASS_SPDX_ELEMENT);
			fail("This shouldn't work");
		} catch (InvalidSPDXAnalysisException e) {
			// Expected
		}
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJson#setPrimativeValue(java.lang.String, java.lang.Object)}.
	 * @throws InvalidSpdxPropertyException 
	 */
	public void testGetSetPrimativeValue() throws InvalidSpdxPropertyException {
		Map<String, String> stringValues = new HashMap<>();
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			lj.setPrimativeValue(valueName, stringValues.get(valueName));
		}
		Map<String, Boolean> booleanValues = new HashMap<>();
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			booleanValues.put(valueName, false);
			lj.setPrimativeValue(valueName, booleanValues.get(valueName));
		}
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), lj.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), lj.getValue(valueName));
		}
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJson#clearPropertyValueList(java.lang.String)}.
	 * @throws InvalidSpdxPropertyException 
	 */
	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueList() throws InvalidSpdxPropertyException {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		List<String> result = (List<String>) lj.getValueList("seeAlso");
		assertEquals(0, result.size());
		String firstItem = "first";
		String secondItem = "second";
		lj.addPrimitiveValueToList("seeAlso", firstItem);
		result = (List<String>) lj.getValueList("seeAlso");
		assertEquals(1, result.size());
		assertEquals(firstItem, result.get(0));
		lj.addPrimitiveValueToList("seeAlso", secondItem);
		result = (List<String>) lj.getValueList("seeAlso");
		assertEquals(2, result.size());
		assertEquals(firstItem, result.get(0));
		assertEquals(secondItem, result.get(1));
		lj.clearPropertyValueList("seeAlso");
		result = (List<String>) lj.getValueList("seeAlso");
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
		LicenseJson lj = gson.fromJson(json.toString(), LicenseJson.class);
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), lj.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), lj.getValue(valueName));
		}
		@SuppressWarnings("unchecked")
		List<String> seeAlsoResult = (List<String>)lj.getValueList("seeAlso");
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
		LicenseJson lj = gson.fromJson(json.toString(), LicenseJson.class);
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), lj.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), lj.getValue(valueName));
		}
		@SuppressWarnings("unchecked")
		List<String> seeAlsoResult = (List<String>)lj.getValueList("seeAlso");
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
	}
	
	public void testRemoveProperty() throws InvalidSpdxPropertyException {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		String value = "value";
		lj.setPrimativeValue(STRING_PROPERTY_VALUE_NAMES.get(0), value);
		assertEquals("value", lj.getValue(STRING_PROPERTY_VALUE_NAMES.get(0)));
		lj.removeProperty(STRING_PROPERTY_VALUE_NAMES.get(0));
		assertTrue(lj.getValue(STRING_PROPERTY_VALUE_NAMES.get(0)) == null);
	}

}
