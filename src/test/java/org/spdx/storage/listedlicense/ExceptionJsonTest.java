/**
 * SPDX-FileCopyrightText: Copyright (c) 2019 Source Auditor Inc.
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.InvalidSpdxPropertyException;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v3_0_1.SpdxConstantsV3;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicenseException;
import org.spdx.storage.PropertyDescriptor;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import com.google.gson.Gson;

import junit.framework.TestCase;

/**
 * @author Gary O'Neall
 */
public class ExceptionJsonTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */

	static final List<PropertyDescriptor> STRING_PROPERTIES = Arrays.asList(
			SpdxConstantsCompatV2.PROP_LICENSE_EXCEPTION_ID, SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT, 
			SpdxConstantsCompatV2.PROP_STD_LICENSE_NAME, SpdxConstantsCompatV2.RDFS_PROP_COMMENT, 
			SpdxConstantsCompatV2.PROP_EXCEPTION_TEMPLATE, 
			SpdxConstantsCompatV2.PROP_EXAMPLE, SpdxConstantsCompatV2.PROP_LIC_DEPRECATED_VERSION,
			SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT_HTML,
			SpdxConstantsV3.PROP_ADDITION_TEXT,
			SpdxConstantsV3.PROP_NAME, SpdxConstantsV3.PROP_STANDARD_ADDITION_TEMPLATE,
			SpdxConstantsV3.PROP_DEPRECATED_VERSION, SpdxConstantsV3.PROP_COMMENT,
			SpdxConstantsV3.PROP_LICENSE_XML, SpdxConstantsV3.PROP_OBSOLETED_BY,
			SpdxConstantsV3.PROP_LIST_VERSION_ADDED);
	
	static final List<PropertyDescriptor> BOOLEAN_PROPERTIES = Arrays.asList(
			SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED,
			SpdxConstantsV3.PROP_IS_DEPRECATED_ADDITION_ID
			);

	static final List<PropertyDescriptor> LIST_PROPERTIES = Arrays.asList(
			SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, SpdxConstantsV3.PROP_SEE_ALSO);
	static final List<PropertyDescriptor> ALL_PROPERTIES = new ArrayList<>();
	static final Set<String> ALL_PROPERTY_NAMES = new HashSet<>();
	static {
		ALL_PROPERTIES.addAll(STRING_PROPERTIES);
		ALL_PROPERTIES.addAll(BOOLEAN_PROPERTIES);
		ALL_PROPERTIES.addAll(LIST_PROPERTIES);
		for (PropertyDescriptor ps:ALL_PROPERTIES) {
			ALL_PROPERTY_NAMES.add(ExceptionJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(ps));
		}
	}

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

	public void testExceptionJson() {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		assertEquals(exceptionId, ej.licenseExceptionId);
	}

	public void testGetPropertyValueDescriptors() {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		List<PropertyDescriptor> result = ej.getPropertyValueDescriptors();
		int emptySize = result.size();
		ej.comment = "comment";
		ej.deprecatedVersion = "deprecatedVersion";
		ej.example = "example";
		ej.exceptionTextHtml = "exceptionTextHtml";
		ej.isDeprecatedLicenseId = true;
		ej.licenseComments = "licenseComments";
		ej.licenseExceptionId = exceptionId;
		ej.licenseExceptionTemplate = "template";
		ej.licenseExceptionText = "text";
		ej.licenseXml = "licenseXml";
		ej.listVersionAdded = "2.3";
		ej.name = "name";
		ej.obsoletedBy = "obsoletedBy";
		ej.seeAlso = Arrays.asList("see1", "see2");
		ej.licenseExceptionId = exceptionId;
		result = ej.getPropertyValueDescriptors();
		assertTrue(result.size() > emptySize);
		assertTrue(ALL_PROPERTIES.size() <= result.size());
		for (PropertyDescriptor valueName:ALL_PROPERTIES) {
			if (!result.contains(valueName)) {
				fail("Missing "+valueName);
			}
		}
	}

	public void testSetTypedProperty() {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		try {
			ej.setTypedProperty("TestPropertyName", "SpdxId22", SpdxConstantsCompatV2.CLASS_SPDX_ELEMENT);
			fail("This shouldn't work");
		} catch (InvalidSPDXAnalysisException e) {
			// Expected
		}
	}

	public void testGetSetPrimitiveValue() throws InvalidSpdxPropertyException {
		Map<PropertyDescriptor, String> stringValues = new HashMap<>();
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		for (PropertyDescriptor property:STRING_PROPERTIES) {
			stringValues.put(property, "ValueFor"+ExceptionJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property));
			ej.setPrimitiveValue(property, stringValues.get(property));
		}
		Map<PropertyDescriptor, Boolean> booleanValues = new HashMap<>();
		for (PropertyDescriptor property:BOOLEAN_PROPERTIES) {
			booleanValues.put(property, false);
			ej.setPrimitiveValue(property, booleanValues.get(property));
		}
		for (PropertyDescriptor property:STRING_PROPERTIES) {
			assertEquals(stringValues.get(property), ej.getValue(property));
		}
		for (PropertyDescriptor property:BOOLEAN_PROPERTIES) {
			assertEquals(booleanValues.get(property), ej.getValue(property));
		}
	}
	
	public void testRemove() throws InvalidSpdxPropertyException {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		String value = "value";
		ej.setPrimitiveValue(STRING_PROPERTIES.get(0), value);
		assertEquals("value", ej.getValue(STRING_PROPERTIES.get(0)));
		ej.removeProperty(STRING_PROPERTIES.get(0));
		assertTrue(ej.getValue(STRING_PROPERTIES.get(0)) == null);
	}

	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueListV2() throws InvalidSpdxPropertyException {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		List<String> result = (List<String>) ej.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(0, result.size());
		String firstItem = "first";
		String secondItem = "second";
		ej.addPrimitiveValueToList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, firstItem);
		result = (List<String>) ej.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(1, result.size());
		assertEquals(firstItem, result.get(0));
		ej.addPrimitiveValueToList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, secondItem);
		result = (List<String>) ej.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(2, result.size());
		assertEquals(firstItem, result.get(0));
		assertEquals(secondItem, result.get(1));
		ej.clearPropertyValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		result = (List<String>) ej.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueListV3() throws InvalidSpdxPropertyException {
		String exceptionId = "SpdxexceptionId1";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		List<String> result = (List<String>) ej.getValueList(SpdxConstantsV3.PROP_SEE_ALSO);
		assertEquals(0, result.size());
		String firstItem = "first";
		String secondItem = "second";
		ej.addPrimitiveValueToList(SpdxConstantsV3.PROP_SEE_ALSO, firstItem);
		result = (List<String>) ej.getValueList(SpdxConstantsV3.PROP_SEE_ALSO);
		assertEquals(1, result.size());
		assertEquals(firstItem, result.get(0));
		ej.addPrimitiveValueToList(SpdxConstantsV3.PROP_SEE_ALSO, secondItem);
		result = (List<String>) ej.getValueList(SpdxConstantsV3.PROP_SEE_ALSO);
		assertEquals(2, result.size());
		assertEquals(firstItem, result.get(0));
		assertEquals(secondItem, result.get(1));
		ej.clearPropertyValueList(SpdxConstantsV3.PROP_SEE_ALSO);
		result = (List<String>) ej.getValueList(SpdxConstantsV3.PROP_SEE_ALSO);
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testJson() throws Exception {
		StringBuilder json = new StringBuilder("{\n");
		Map<PropertyDescriptor, String> stringValues = new HashMap<>();
		Set<String> addedPropertyNames = new HashSet<>();
		for (PropertyDescriptor property:STRING_PROPERTIES) {
			String propertyName = ExceptionJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property);
			stringValues.put(property, "ValueFor"+propertyName);
			if (!addedPropertyNames.contains(propertyName)) {
				json.append("\t\"");
				json.append(propertyName);
				json.append("\":\"");
				json.append(stringValues.get(property));
				json.append("\",\n");
				addedPropertyNames.add(propertyName);
			}
			
		}
		Map<PropertyDescriptor, Boolean> booleanValues = new HashMap<>();
		for (PropertyDescriptor property:BOOLEAN_PROPERTIES) {
			String propertyName = ExceptionJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property);
			booleanValues.put(property, false);
			if (!addedPropertyNames.contains(propertyName)) {
				json.append("\t\"");
				json.append(propertyName);
				json.append("\":\"");
				json.append(booleanValues.get(property));
				json.append("\",\n");
				addedPropertyNames.add(propertyName);
			}
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
		for (PropertyDescriptor property:STRING_PROPERTIES) {
			assertEquals(stringValues.get(property), ej.getValue(property));
		}
		for (PropertyDescriptor property:BOOLEAN_PROPERTIES) {
			assertEquals(booleanValues.get(property), ej.getValue(property));
		}
		List<String> seeAlsoResult = (List<String>)ej.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
		seeAlsoResult = (List<String>)ej.getValueList(SpdxConstantsV3.PROP_SEE_ALSO);
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testLegacyJson() throws Exception {
		//TODO: In SPDX 3.0 this test should be removed once Spec issue #158 is resolved (https://github.com/spdx/spdx-spec/issues/158)
		StringBuilder json = new StringBuilder("{\n");
		Map<PropertyDescriptor, String> stringValues = new HashMap<>();
		Set<String> addedPropertyNames = new HashSet<>();
		for (PropertyDescriptor property:STRING_PROPERTIES) {
			String propertyName = ExceptionJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property);
			stringValues.put(property, "ValueFor"+propertyName);
			if (!addedPropertyNames.contains(propertyName)) {
				json.append("\t\"");
				if (SpdxConstantsCompatV2.RDFS_PROP_COMMENT.equals(property)) {
					json.append("licenseComments");	// Legacy value
				} else {
					json.append(propertyName);
				}
				json.append("\":\"");
				json.append(stringValues.get(property));
				json.append("\",\n");
				addedPropertyNames.add(propertyName);
			}
		}
		Map<PropertyDescriptor, Boolean> booleanValues = new HashMap<>();
		for (PropertyDescriptor property:BOOLEAN_PROPERTIES) {
			String propertyName = ExceptionJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property);
			booleanValues.put(property, false);
			if (!addedPropertyNames.contains(propertyName)) {
				json.append("\t\"");
				json.append(propertyName);
				json.append("\":\"");
				json.append(booleanValues.get(property));
				json.append("\",\n");
				addedPropertyNames.add(propertyName);
			}
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
		for (PropertyDescriptor property:STRING_PROPERTIES) {
			assertEquals(stringValues.get(property), ej.getValue(property));
		}
		for (PropertyDescriptor property:BOOLEAN_PROPERTIES) {
			assertEquals(booleanValues.get(property), ej.getValue(property));
		}
		List<String> seeAlsoResult = (List<String>)ej.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
		seeAlsoResult = (List<String>)ej.getValueList(SpdxConstantsV3.PROP_SEE_ALSO);
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
	}
	
	public void testIsCollectionMembersAssignableTo() throws Exception {
		String exceptionId = "excId";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		assertTrue(ej.isCollectionMembersAssignableTo(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, String.class));
		assertTrue(ej.isCollectionMembersAssignableTo(SpdxConstantsV3.PROP_SEE_ALSO, String.class));
		assertFalse(ej.isCollectionMembersAssignableTo(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, Boolean.class));
		assertFalse(ej.isCollectionMembersAssignableTo(SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT, String.class));
	}
	
	public void testIsPropertyValueAssignableTo() throws Exception {
		String exceptionId = "excId";
		ExceptionJson ej = new ExceptionJson(exceptionId);
		assertFalse(ej.isPropertyValueAssignableTo(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, String.class));
		assertTrue(ej.isPropertyValueAssignableTo(SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT, String.class));
		assertFalse(ej.isPropertyValueAssignableTo(SpdxConstantsCompatV2.PROP_EXCEPTION_TEXT, Boolean.class));

		assertFalse(ej.isPropertyValueAssignableTo(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, String.class));
		assertTrue(ej.isPropertyValueAssignableTo(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, Boolean.class));
	}
	
	@SuppressWarnings("deprecation")
	public void testCopyFromV2() throws Exception {
		InMemSpdxStore store = new InMemSpdxStore();
		String docUri = "http://temp.uri";
		String exceptionId = "exceptionId";
		String comment = "comment";
		Boolean deprecated = true;
		String deprecatedVersion = "v1";
		String example = "example";
		String exceptionTextHtml = "<h1>html</h1>";
		String text = "text";
		String name = "name";
		String[] seeAlsoArray = new String[]{"http://seealso1", "http://see/also/2"};
		List<String> seeAlso = Arrays.asList(seeAlsoArray);
		String template = "template";
		
		org.spdx.library.model.v2.license.ListedLicenseException exception = new org.spdx.library.model.v2.license.ListedLicenseException(store, docUri, exceptionId, null, true);
		exception.setComment(comment);
		exception.setDeprecated(deprecated);
		exception.setDeprecatedVersion(deprecatedVersion);
		exception.setExample(example);
		exception.setExceptionTextHtml(exceptionTextHtml);
		exception.setLicenseExceptionTemplate(template);
		exception.setLicenseExceptionText(text);
		exception.setName(name);
		exception.setSeeAlso(seeAlso);
		exception.setDeprecated(deprecated);
		ExceptionJson ej = new ExceptionJson();
		ej.copyFrom(exception);
		
		assertEquals(exceptionId, ej.licenseExceptionId);
		//TODO: Remove the comment in the following line for SPDX 3.0
		//assertEquals(comment, ej.comment);
		assertEquals(comment, ej.licenseComments);
		assertEquals(deprecated, ej.isDeprecatedLicenseId);
		assertEquals(deprecatedVersion, ej.deprecatedVersion);
		assertEquals(example, ej.example);
		assertEquals(exceptionTextHtml, ej.exceptionTextHtml);
		assertEquals(template, ej.licenseExceptionTemplate);
		assertEquals(text, ej.licenseExceptionText);
		assertEquals(name, ej.name);
		UnitTestHelper.isListsEqual(seeAlso, ej.seeAlso);
		
	}

	public void testCopyFromV3() throws Exception {
		InMemSpdxStore store = new InMemSpdxStore();
		String exceptionId = "exceptionId";
		String objectUri = "http://spdx.org/licenses/" + exceptionId;
		String comment = "comment";
		Boolean deprecated = true;
		String deprecatedVersion = "v1";
		String text = "text";
		String name = "name";
		String[] seeAlsoArray = new String[]{"http://seealso1", "http://see/also/2"};
		List<String> seeAlso = Arrays.asList(seeAlsoArray);
		String template = "template";
		String licenseXml = "licenseXml";
		String obsoletedBy = "obsoletedBy";
		String listVersionAdded = "2.3.2";
		ListedLicenseException exception = new ListedLicenseException(store,
					 objectUri, null, true, null);
		exception.setComment(comment);
		exception.setIsDeprecatedAdditionId(deprecated);
		exception.setDeprecatedVersion(deprecatedVersion);
		exception.setStandardAdditionTemplate(template);
		exception.setAdditionText(text);
		exception.setName(name);
		exception.getSeeAlsos().addAll(seeAlso);
		exception.setIsDeprecatedAdditionId(deprecated);
		exception.setObsoletedBy(obsoletedBy);
		exception.setComment(comment);
		exception.setLicenseXml(licenseXml);
		exception.setListVersionAdded(listVersionAdded);
		ExceptionJson ej = new ExceptionJson();
		ej.copyFrom(exception);
		
		assertEquals(exceptionId, ej.licenseExceptionId);
		assertEquals(comment, ej.comment);
		assertEquals(deprecated, ej.isDeprecatedLicenseId);
		assertEquals(deprecatedVersion, ej.deprecatedVersion);
		assertEquals(template, ej.licenseExceptionTemplate);
		assertEquals(text, ej.licenseExceptionText);
		assertEquals(name, ej.name);
		UnitTestHelper.isListsEqual(seeAlso, ej.seeAlso);
		assertEquals(obsoletedBy, ej.obsoletedBy);
		assertEquals(comment, ej.comment);
		assertEquals(licenseXml, ej.licenseXml);
		assertEquals(listVersionAdded, ej.listVersionAdded);
	}
}
