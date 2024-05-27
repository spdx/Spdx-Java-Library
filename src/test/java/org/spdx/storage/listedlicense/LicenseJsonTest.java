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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.InvalidSpdxPropertyException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.CrossRef;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicense;
import org.spdx.storage.PropertyDescriptor;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import com.google.gson.Gson;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class LicenseJsonTest extends TestCase {
	
	static final List<PropertyDescriptor> STRING_PROPERTIES = Arrays.asList(
			SpdxConstantsCompatV2.PROP_LICENSE_ID, SpdxConstantsCompatV2.PROP_LICENSE_TEXT,
			SpdxConstantsCompatV2.PROP_LICENSE_TEXT_HTML, 
			SpdxConstantsCompatV2.PROP_STD_LICENSE_NAME, SpdxConstantsCompatV2.RDFS_PROP_COMMENT,
			SpdxConstantsCompatV2.PROP_STD_LICENSE_NOTICE, SpdxConstantsCompatV2.PROP_STD_LICENSE_HEADER_TEMPLATE,
			SpdxConstantsCompatV2.PROP_LICENSE_HEADER_HTML, SpdxConstantsCompatV2.PROP_STD_LICENSE_TEMPLATE,
			SpdxConstantsCompatV2.PROP_EXAMPLE, SpdxConstantsCompatV2.PROP_LIC_DEPRECATED_VERSION,
			SpdxConstantsV3.PROP_EXPANDED_LICENSING_LICENSE_XML, SpdxConstantsV3.PROP_EXPANDED_LICENSING_OBSOLETED_BY,
			SpdxConstantsV3.PROP_EXPANDED_LICENSING_LIST_VERSION_ADDED, SpdxConstantsV3.PROP_SIMPLE_LICENSING_LICENSE_TEXT,
			SpdxConstantsV3.PROP_NAME, SpdxConstantsV3.PROP_EXPANDED_LICENSING_STANDARD_LICENSE_HEADER,
			SpdxConstantsV3.PROP_EXPANDED_LICENSING_STANDARD_LICENSE_TEMPLATE,
			SpdxConstantsV3.PROP_EXPANDED_LICENSING_DEPRECATED_VERSION, SpdxConstantsV3.PROP_COMMENT
			);
	
	static final List<PropertyDescriptor> BOOLEAN_PROPERTIES = Arrays.asList(
			SpdxConstantsCompatV2.PROP_STD_LICENSE_OSI_APPROVED, SpdxConstantsCompatV2.PROP_STD_LICENSE_FSF_LIBRE,
			SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, SpdxConstantsV3.PROP_EXPANDED_LICENSING_IS_OSI_APPROVED,
			SpdxConstantsV3.PROP_EXPANDED_LICENSING_IS_FSF_LIBRE,
			SpdxConstantsV3.PROP_EXPANDED_LICENSING_IS_DEPRECATED_LICENSE_ID
			);
	
	static final List<PropertyDescriptor> ALL_PROPERTIES = new ArrayList<>();
	static final Set<String> ALL_PROPERTY_NAMES = new HashSet<>();
	static final List<PropertyDescriptor> PROPERTY_VALUE_LIST_NAMES = Arrays.asList(
			SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO, SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO,
			SpdxConstantsCompatV2.PROP_CROSS_REF);
	static {
		ALL_PROPERTIES.addAll(STRING_PROPERTIES);
		ALL_PROPERTIES.addAll(BOOLEAN_PROPERTIES);
		ALL_PROPERTIES.addAll(PROPERTY_VALUE_LIST_NAMES);
		for (PropertyDescriptor pd:ALL_PROPERTIES) {
			ALL_PROPERTY_NAMES.add(LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(pd));
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
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetPropertyValueNames() throws InvalidSPDXAnalysisException {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		for (PropertyDescriptor desc:STRING_PROPERTIES) {
			lj.setPrimativeValue(desc, "s");
		}
		for (PropertyDescriptor desc:BOOLEAN_PROPERTIES) {
			lj.setPrimativeValue(desc, true);
		}
		CrossRefJson firstItem = new CrossRefJson();
		firstItem.url = "http://first";
		lj.addPrimitiveValueToList(SpdxConstantsCompatV2.PROP_CROSS_REF, firstItem);
		String secondItem = "second";
		lj.addPrimitiveValueToList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, secondItem);
		List<PropertyDescriptor> result = lj.getPropertyValueDescriptors();
		assertTrue(ALL_PROPERTIES.size() < result.size());
		for (PropertyDescriptor valueName:ALL_PROPERTIES) {
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
			lj.setTypedProperty("TestPropertyName", "SpdxId22", SpdxConstantsCompatV2.CLASS_SPDX_ELEMENT);
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
		Map<PropertyDescriptor, String> stringValues = new HashMap<>();
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		for (PropertyDescriptor valueName:STRING_PROPERTIES) {
			stringValues.put(valueName, "ValueFor"+LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(valueName));
			lj.setPrimativeValue(valueName, stringValues.get(valueName));
		}
		Map<PropertyDescriptor, Boolean> booleanValues = new HashMap<>();
		for (PropertyDescriptor valueName:BOOLEAN_PROPERTIES) {
			booleanValues.put(valueName, false);
			lj.setPrimativeValue(valueName, booleanValues.get(valueName));
		}
		for (PropertyDescriptor valueName:STRING_PROPERTIES) {
			assertEquals(stringValues.get(valueName), lj.getValue(valueName));
		}
		for (PropertyDescriptor valueName:BOOLEAN_PROPERTIES) {
			assertEquals(booleanValues.get(valueName), lj.getValue(valueName));
		}
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseJson#clearPropertyValueList(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueListSeeAlsoV2() throws InvalidSPDXAnalysisException {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		List<String> result = (List<String>) lj.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(0, result.size());
		String firstItem = "first";
		String secondItem = "second";
		lj.addPrimitiveValueToList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, firstItem);
		result = (List<String>) lj.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(1, result.size());
		assertEquals(firstItem, result.get(0));
		lj.addPrimitiveValueToList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, secondItem);
		result = (List<String>) lj.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(2, result.size());
		assertEquals(firstItem, result.get(0));
		assertEquals(secondItem, result.get(1));
		lj.clearPropertyValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		result = (List<String>) lj.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueListSeeAlsoV3() throws InvalidSPDXAnalysisException {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		List<String> result = (List<String>) lj.getValueList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO);
		assertEquals(0, result.size());
		String firstItem = "first";
		String secondItem = "second";
		lj.addPrimitiveValueToList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO, firstItem);
		result = (List<String>) lj.getValueList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO);
		assertEquals(1, result.size());
		assertEquals(firstItem, result.get(0));
		lj.addPrimitiveValueToList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO, secondItem);
		result = (List<String>) lj.getValueList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO);
		assertEquals(2, result.size());
		assertEquals(firstItem, result.get(0));
		assertEquals(secondItem, result.get(1));
		lj.clearPropertyValueList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO);
		result = (List<String>) lj.getValueList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO);
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueListCrossRef() throws InvalidSPDXAnalysisException {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		List<CrossRef> result = (List<CrossRef>) lj.getValueList(SpdxConstantsCompatV2.PROP_CROSS_REF);
		assertEquals(0, result.size());
		CrossRefJson firstItem = new CrossRefJson();
		firstItem.url = "http://first";
		CrossRefJson secondItem = new CrossRefJson();
		secondItem.url = "http://second";
		lj.addPrimitiveValueToList(SpdxConstantsCompatV2.PROP_CROSS_REF, firstItem);
		result = (List<CrossRef>) lj.getValueList(SpdxConstantsCompatV2.PROP_CROSS_REF);
		assertEquals(1, result.size());
		assertEquals(firstItem, result.get(0));
		lj.addPrimitiveValueToList(SpdxConstantsCompatV2.PROP_CROSS_REF, secondItem);
		result = (List<CrossRef>) lj.getValueList(SpdxConstantsCompatV2.PROP_CROSS_REF);
		assertEquals(2, result.size());
		assertEquals(firstItem, result.get(0));
		assertEquals(secondItem, result.get(1));
		lj.clearPropertyValueList(SpdxConstantsCompatV2.PROP_CROSS_REF);
		result = (List<CrossRef>) lj.getValueList(SpdxConstantsCompatV2.PROP_CROSS_REF);
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testJson() throws Exception {
		StringBuilder json = new StringBuilder("{\n");
		Map<PropertyDescriptor, String> stringValues = new HashMap<>();
		Set<String> addedPropertyNames = new HashSet<>();
		for (PropertyDescriptor properties:STRING_PROPERTIES) {
			String propertyName = LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(properties);
			stringValues.put(properties, "ValueFor"+propertyName);
			if (!addedPropertyNames.contains(propertyName)) {
				json.append("\t\"");
				json.append(propertyName);
				json.append("\":\"");
				json.append(stringValues.get(properties));
				json.append("\",\n");
				addedPropertyNames.add(propertyName);
			}
		}
		Map<PropertyDescriptor, Boolean> booleanValues = new HashMap<>();
		for (PropertyDescriptor properties:BOOLEAN_PROPERTIES) {
			String propertyName = LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(properties);
			booleanValues.put(properties, false);
			if (!addedPropertyNames.contains(propertyName)) {
				json.append("\t\"");
				json.append(LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(properties));
				json.append("\":\"");
				json.append(booleanValues.get(properties));
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
		json.append("\"\n\t],\n");
		CrossRefJson crossRef1 = new CrossRefJson();
		crossRef1.isLive = true;
		crossRef1.isValid = false;
		crossRef1.isWayBackLink = true;
		crossRef1.match = "match1";
		crossRef1.order = 1;
		crossRef1.timestamp = "timestamp1";
		crossRef1.url = "http://url1";
		CrossRefJson crossRef2 = new CrossRefJson();
		crossRef2.isLive = false;
		crossRef2.isValid = true;
		crossRef2.isWayBackLink = false;
		crossRef2.match = "match2";
		crossRef2.order = 2;
		crossRef2.timestamp = "timestamp2";
		crossRef2.url = "http://url2";
		json.append("\t\"crossRef\": [\n\t\t{\n\t\t\t\"isLive\":");
		json.append(crossRef1.isLive);
		json.append(",\n\t\t\t\"isValid\": ");
		json.append(crossRef1.isValid);
		json.append(",\n\t\t\t\"isWayBackLink\": ");
		json.append(crossRef1.isWayBackLink);
		json.append(",\n\t\t\t\"match\": \"");
		json.append(crossRef1.match);
		json.append("\",\n\t\t\t\"url\": \"");
		json.append(crossRef1.url);
		json.append("\",\n\t\t\t\"order\": ");
		json.append(crossRef1.order);
		json.append(",\n\t\t\t\"timestamp\": \"");
		json.append(crossRef1.timestamp);
		json.append("\"\n\t\t},\n\t\t{\n\t\t\t\"isLive\":");
		json.append(crossRef2.isLive);
		json.append(",\n\t\t\t\"isValid\": ");
		json.append(crossRef2.isValid);
		json.append(",\n\t\t\t\"isWayBackLink\": ");
		json.append(crossRef2.isWayBackLink);
		json.append(",\n\t\t\t\"match\": \"");
		json.append(crossRef2.match);
		json.append("\",\n\t\t\t\"url\": \"");
		json.append(crossRef2.url);
		json.append("\",\n\t\t\t\"order\": ");
		json.append(crossRef2.order);
		json.append(",\n\t\t\t\"timestamp\": \"");
		json.append(crossRef2.timestamp);
		json.append("\"\n\t\t}\n\t]\n}");
		Gson gson = new Gson();
		LicenseJson lj = gson.fromJson(json.toString(), LicenseJson.class);
		for (PropertyDescriptor properties:STRING_PROPERTIES) {
			assertEquals(stringValues.get(properties), lj.getValue(properties));
		}
		for (PropertyDescriptor properties:BOOLEAN_PROPERTIES) {
			assertEquals(booleanValues.get(properties), lj.getValue(properties));
		}
		List<String> seeAlsoResult = (List<String>)lj.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
		seeAlsoResult = (List<String>)lj.getValueList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO);
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
		List<CrossRefJson> crossRefResult = (List<CrossRefJson>)lj.getValueList(SpdxConstantsCompatV2.PROP_CROSS_REF);
		assertEquals(2, crossRefResult.size());
		assertEquals(crossRef1.match, crossRefResult.get(0).match);
		assertEquals(crossRef1.timestamp, crossRefResult.get(0).timestamp);
		assertEquals(crossRef1.url, crossRefResult.get(0).url);
		assertEquals(crossRef1.isLive, crossRefResult.get(0).isLive);
		assertEquals(crossRef1.isValid, crossRefResult.get(0).isValid);
		assertEquals(crossRef1.isWayBackLink, crossRefResult.get(0).isWayBackLink);
		assertEquals(crossRef1.order, crossRefResult.get(0).order);
		assertEquals(crossRef2.match, crossRefResult.get(1).match);
		assertEquals(crossRef2.timestamp, crossRefResult.get(1).timestamp);
		assertEquals(crossRef2.url, crossRefResult.get(1).url);
		assertEquals(crossRef2.isLive, crossRefResult.get(1).isLive);
		assertEquals(crossRef2.isValid, crossRefResult.get(1).isValid);
		assertEquals(crossRef2.isWayBackLink, crossRefResult.get(1).isWayBackLink);
		assertEquals(crossRef2.order, crossRefResult.get(1).order);
	}
	
	@SuppressWarnings("unchecked")
	public void testLegacyJson() throws Exception {
		//TODO: In SPDX 3.0 this test should be removed once Spec issue #158 is resolved (https://github.com/spdx/spdx-spec/issues/158)
		StringBuilder json = new StringBuilder("{\n");
		Set<String> addedPropertyNames = new HashSet<>();
		Map<PropertyDescriptor, String> stringValues = new HashMap<>();
		for (PropertyDescriptor property:STRING_PROPERTIES) {
			String propertyName = LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property);
			stringValues.put(property, "ValueFor"+propertyName);
			if (!addedPropertyNames.contains(propertyName)) {
				stringValues.put(property, "ValueFor"+propertyName);
				json.append("\t\"");
				if (SpdxConstantsCompatV2.RDFS_PROP_COMMENT.equals(property)) {
					json.append("licenseComments");	// Legacy value
				} else {
					json.append(LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property));
				}
				json.append("\":\"");
				json.append(stringValues.get(property));
				json.append("\",\n");
				addedPropertyNames.add(propertyName);
			}
			
		}
		Map<PropertyDescriptor, Boolean> booleanValues = new HashMap<>();
		for (PropertyDescriptor property:BOOLEAN_PROPERTIES) {
			String propertyName = LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property);
			booleanValues.put(property, false);
			if (!addedPropertyNames.contains(propertyName)) {
				json.append("\t\"");
				json.append(LicenseJson.PROPERTY_DESCRIPTOR_TO_VALUE_NAME.get(property));
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
		LicenseJson lj = gson.fromJson(json.toString(), LicenseJson.class);
		for (PropertyDescriptor property:STRING_PROPERTIES) {
			assertEquals(stringValues.get(property), lj.getValue(property));
		}
		for (PropertyDescriptor property:BOOLEAN_PROPERTIES) {
			assertEquals(booleanValues.get(property), lj.getValue(property));
		}
		List<String> seeAlsoResult = (List<String>)lj.getValueList(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO);
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
		seeAlsoResult = (List<String>)lj.getValueList(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO);
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
		lj.setPrimativeValue(STRING_PROPERTIES.get(0), value);
		assertEquals("value", lj.getValue(STRING_PROPERTIES.get(0)));
		lj.removeProperty(STRING_PROPERTIES.get(0));
		assertTrue(lj.getValue(STRING_PROPERTIES.get(0)) == null);
	}
	
	public void testIsCollectionMembersAssignableTo() throws Exception {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		assertTrue(lj.isCollectionMembersAssignableTo(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, String.class));
		assertTrue(lj.isCollectionMembersAssignableTo(SpdxConstantsV3.PROP_EXPANDED_LICENSING_SEE_ALSO, String.class));
		assertFalse(lj.isCollectionMembersAssignableTo(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, Boolean.class));
		assertFalse(lj.isCollectionMembersAssignableTo(SpdxConstantsCompatV2.PROP_LICENSE_TEXT, String.class));
		assertTrue(lj.isCollectionMembersAssignableTo(SpdxConstantsCompatV2.PROP_CROSS_REF, CrossRef.class));
	}
	
	public void testIsPropertyValueAssignableTo() throws Exception {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		assertFalse(lj.isPropertyValueAssignableTo(SpdxConstantsCompatV2.RDFS_PROP_SEE_ALSO, String.class));
		assertTrue(lj.isPropertyValueAssignableTo(SpdxConstantsCompatV2.PROP_LICENSE_TEXT, String.class));
		assertFalse(lj.isPropertyValueAssignableTo(SpdxConstantsCompatV2.PROP_LICENSE_TEXT, Boolean.class));

		assertFalse(lj.isPropertyValueAssignableTo(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, String.class));
		assertTrue(lj.isPropertyValueAssignableTo(SpdxConstantsCompatV2.PROP_LIC_ID_DEPRECATED, Boolean.class));
	}
	
	public void testFromListedLicenseV3() throws InvalidSPDXAnalysisException {
		LicenseJson lj = new LicenseJson();
		InMemSpdxStore store = new InMemSpdxStore();
		String objectUri = "http://spdx.org/licenses/test";
		ModelCopyManager copyManager = new ModelCopyManager();
		ExpandedLicensingListedLicense license = new ExpandedLicensingListedLicense(store, objectUri, copyManager, true);
		boolean deprecated = true;
		String comment = "comment";
		String deprecatedVersion = "deprecatedVersion";
		String licenseText = "licenseText";
		String name = "name";
		String standardLicenseHeader = "standardLicenseHeader";
		String standardLicenseTemplate = "standardLicenseTemplate";
		Boolean fsfLibre = true;
		Boolean osiApproved = true;
		List<String> seeAlsoUrl = Arrays.asList(new String[]{"http://url1", "http://url2"});
		String licenseXml = "licenseXml";
		String listVersionAdded = "12.1.1";
		String obsoletedBy = "something";
		
		license.setComment(comment);
		license.setExpandedLicensingDeprecatedVersion(deprecatedVersion);
		license.setSimpleLicensingLicenseText(licenseText);
		license.setName(name);
		license.setExpandedLicensingStandardLicenseHeader(standardLicenseHeader);
		license.setExpandedLicensingStandardLicenseTemplate(standardLicenseTemplate);
		license.setExpandedLicensingIsFsfLibre(fsfLibre);
		license.setExpandedLicensingIsOsiApproved(osiApproved);
		license.getExpandedLicensingSeeAlsos().addAll(seeAlsoUrl);
		license.setExpandedLicensingIsDeprecatedLicenseId(deprecated);
		license.setExpandedLicensingLicenseXml(licenseXml);
		license.setExpandedLicensingListVersionAdded(listVersionAdded);
		license.setExpandedLicensingObsoletedBy(obsoletedBy);
		
		lj.copyFrom(license);
		assertEquals(fsfLibre, lj.isFsfLibre);
		assertEquals(osiApproved, lj.isOsiApproved);
		assertEquals(comment, lj.comment);
		assertEquals(deprecatedVersion, lj.deprecatedVersion);
		assertEquals(licenseText, lj.licenseText);
		assertEquals(name, lj.name);
		assertEquals(standardLicenseHeader, lj.standardLicenseHeader);
		assertEquals(standardLicenseTemplate, lj.standardLicenseTemplate);
		assertTrue(UnitTestHelper.isListsEqual(seeAlsoUrl, lj.seeAlso));
		assertEquals(obsoletedBy, lj.obsoletedBy);
		assertEquals(licenseXml, lj.licenseXml);
		assertEquals(listVersionAdded, lj.listVersionAdded);
	}
	
	public void testCopyFromLicenseV2() throws Exception {
		LicenseJson lj = new LicenseJson();
		InMemSpdxStore store = new InMemSpdxStore();
		String docUri = "http://doc.uri";
		String id = "licenseId";
		boolean deprecated = true;
		String comment = "comment";
		String deprecatedVersion = "deprecatedVersion";
		String licenseText = "licenseText";
		String licenseTextHtml = "licenseTextHtml";
		String name = "name";
		String standardLicenseHeader = "standardLicenseHeader";
		String standardLicenseHeaderHtml = "standardLicenseHeaderHtml";
		String standardLicenseHeaderTemplate = "standardLicenseHeaderTemplate";
		String standardLicenseTemplate = "standardLicenseTemplate";
		Boolean fsfLibre = true;
		Boolean osiApproved = true;
		List<String> seeAlsoUrl = Arrays.asList(new String[]{"http://url1", "http://url2"});
		ModelCopyManager copyManager = new ModelCopyManager();
		
		org.spdx.library.model.v2.license.SpdxListedLicense license = new org.spdx.library.model.v2.license.SpdxListedLicense(store, docUri, id, copyManager, true);
		List<CrossRef> crossRefs = new ArrayList<>();
		List<String> crossRefUrls = Arrays.asList(new String[]{"http://crossref1", "http://crossref2"});
		for (String crossRefUrl:crossRefUrls) {
			crossRefs.add(license.createCrossRef(crossRefUrl).build());
		}
		license.setComment(comment);
		license.setDeprecated(deprecated);
		license.setDeprecatedVersion(deprecatedVersion);
		license.setFsfLibre(fsfLibre);
		license.setLicenseHeaderHtml(standardLicenseHeaderHtml);
		license.setLicenseText(licenseText);
		license.setLicenseTextHtml(licenseTextHtml);
		license.setName(name);
		license.setOsiApproved(osiApproved);
		license.setSeeAlso(seeAlsoUrl);
		license.setStandardLicenseHeader(standardLicenseHeader);
		license.setStandardLicenseHeaderTemplate(standardLicenseHeaderTemplate);
		license.setStandardLicenseTemplate(standardLicenseTemplate);
		license.getCrossRef().addAll(crossRefs);
		
		lj.copyFrom(license);
		assertEquals(fsfLibre, lj.isFsfLibre);
		assertEquals(standardLicenseHeaderHtml, lj.standardLicenseHeaderHtml);
		assertEquals(osiApproved, lj.isOsiApproved);
		assertEquals(comment, lj.comment);
		assertEquals(comment, lj.licenseComments);
		assertEquals(deprecatedVersion, lj.deprecatedVersion);
		assertEquals(id, lj.licenseId);
		assertEquals(licenseText, lj.licenseText);
		assertEquals(licenseTextHtml, lj.licenseTextHtml);
		assertEquals(name, lj.name);
		assertEquals(standardLicenseHeader, lj.standardLicenseHeader);
		assertEquals(standardLicenseHeaderHtml, lj.standardLicenseHeaderHtml);
		assertEquals(standardLicenseHeaderTemplate, lj.standardLicenseHeaderTemplate);
		assertEquals(standardLicenseTemplate, lj.standardLicenseTemplate);
		assertTrue(UnitTestHelper.isListsEqual(seeAlsoUrl, lj.seeAlso));
		List<String> resultCrossRefUrls = new ArrayList<>();
		for (CrossRefJson resultCr:lj.crossRef) {
			resultCrossRefUrls.add(resultCr.url);
		}
		assertTrue(UnitTestHelper.isListsEqual(crossRefUrls, resultCrossRefUrls));
	}
}
