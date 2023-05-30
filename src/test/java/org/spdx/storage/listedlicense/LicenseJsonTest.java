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
import org.spdx.library.model.license.CrossRef;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import com.google.gson.Gson;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class LicenseJsonTest extends TestCase {
	
	static final List<String> STRING_PROPERTY_VALUE_NAMES = Arrays.asList(
			SpdxConstants.PROP_LICENSE_ID.getName(), SpdxConstants.PROP_LICENSE_TEXT.getName(),
			SpdxConstants.PROP_LICENSE_TEXT_HTML.getName(), 
			SpdxConstants.PROP_STD_LICENSE_NAME.getName(), SpdxConstants.RDFS_PROP_COMMENT.getName(),
			SpdxConstants.PROP_STD_LICENSE_NOTICE.getName(), SpdxConstants.PROP_STD_LICENSE_HEADER_TEMPLATE.getName(),
			SpdxConstants.PROP_LICENSE_HEADER_HTML.getName(), SpdxConstants.PROP_STD_LICENSE_TEMPLATE.getName(),
			SpdxConstants.PROP_EXAMPLE.getName(), SpdxConstants.PROP_LIC_DEPRECATED_VERSION.getName()
			);
	
	static final List<String> BOOLEAN_PROPERTY_VALUE_NAMES = Arrays.asList(
			SpdxConstants.PROP_STD_LICENSE_OSI_APPROVED.getName(), SpdxConstants.PROP_STD_LICENSE_FSF_LIBRE.getName(),
			SpdxConstants.PROP_LIC_ID_DEPRECATED.getName()
			);
	
	static final List<String> PROPERTY_VALUE_NAMES = new ArrayList<>();
	static final List<String> PROPERTY_VALUE_LIST_NAMES = Arrays.asList(SpdxConstants.RDFS_PROP_SEE_ALSO.getName(),
			SpdxConstants.PROP_CROSS_REF.getName());
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
			lj.setTypedProperty("TestPropertyName", "SpdxId22", SpdxConstants.CLASS_SPDX_ELEMENT);
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
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueListSeeAlso() throws InvalidSPDXAnalysisException {
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
	
	@SuppressWarnings("unchecked")
	public void testAddClearGetPropertyValueListCrossRef() throws InvalidSPDXAnalysisException {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		List<CrossRef> result = (List<CrossRef>) lj.getValueList("crossRef");
		assertEquals(0, result.size());
		CrossRefJson firstItem = new CrossRefJson();
		firstItem.url = "http://first";
		CrossRefJson secondItem = new CrossRefJson();
		secondItem.url = "http://second";
		lj.addPrimitiveValueToList("crossRef", firstItem);
		result = (List<CrossRef>) lj.getValueList("crossRef");
		assertEquals(1, result.size());
		assertEquals(firstItem, result.get(0));
		lj.addPrimitiveValueToList("crossRef", secondItem);
		result = (List<CrossRef>) lj.getValueList("crossRef");
		assertEquals(2, result.size());
		assertEquals(firstItem, result.get(0));
		assertEquals(secondItem, result.get(1));
		lj.clearPropertyValueList("crossRef");
		result = (List<CrossRef>) lj.getValueList("crossRef");
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("unchecked")
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
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), lj.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), lj.getValue(valueName));
		}
		List<String> seeAlsoResult = (List<String>)lj.getValueList("seeAlso");
		assertEquals(seeAlsoValues.size(), seeAlsoResult.size());
		for (String seeAlsoValue:seeAlsoValues) {
			if (!seeAlsoResult.contains(seeAlsoValue)) {
				fail("Missing "+seeAlsoValue);
			}
		}
		List<CrossRefJson> crossRefResult = (List<CrossRefJson>)lj.getValueList("crossRef");
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
		Map<String, String> stringValues = new HashMap<>();
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			json.append("\t\"");
			if (SpdxConstants.RDFS_PROP_COMMENT.equals(valueName)) {
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
	
	public void testIsCollectionMembersAssignableTo() throws Exception {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		assertTrue(lj.isCollectionMembersAssignableTo(SpdxConstants.RDFS_PROP_SEE_ALSO.getName(), String.class));
		assertFalse(lj.isCollectionMembersAssignableTo(SpdxConstants.RDFS_PROP_SEE_ALSO.getName(), Boolean.class));
		assertFalse(lj.isCollectionMembersAssignableTo(SpdxConstants.PROP_LICENSE_TEXT.getName(), String.class));
		assertTrue(lj.isCollectionMembersAssignableTo(SpdxConstants.PROP_CROSS_REF.getName(), CrossRef.class));
	}
	
	public void testIsPropertyValueAssignableTo() throws Exception {
		String licenseId = "SpdxLicenseId1";
		LicenseJson lj = new LicenseJson(licenseId);
		assertFalse(lj.isPropertyValueAssignableTo(SpdxConstants.RDFS_PROP_SEE_ALSO.getName(), String.class));
		assertTrue(lj.isPropertyValueAssignableTo(SpdxConstants.PROP_LICENSE_TEXT.getName(), String.class));
		assertFalse(lj.isPropertyValueAssignableTo(SpdxConstants.PROP_LICENSE_TEXT.getName(), Boolean.class));

		assertFalse(lj.isPropertyValueAssignableTo(SpdxConstants.PROP_LIC_ID_DEPRECATED.getName(), String.class));
		assertTrue(lj.isPropertyValueAssignableTo(SpdxConstants.PROP_LIC_ID_DEPRECATED.getName(), Boolean.class));
	}
	
	public void testCopyFromLicense() throws Exception {
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
		
		SpdxListedLicense license = new SpdxListedLicense(store, docUri, id, null, true);
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
		//TODO: Uncomment out the following line in SPDX 3.0
		//assertEquals(comment, lj.comment);
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
