/**
 * Copyright (c) 2024 Source Auditor Inc.
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

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.TypedValue;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v3_0_0.SpdxConstantsV3;
import org.spdx.library.model.v3_0_0.core.Agent;

/**
 * @author gary
 *
 */
public class LicenseCreationInfoTest {
	
	static final String LICENSE_LIST_VERSION = "3.24.0";
	
	LicenseCreatorAgent licenseCreatorAgent;
	String licenseListReleaseDate = new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT).format(new Date());

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SpdxModelFactory.init();
		licenseCreatorAgent = new LicenseCreatorAgent(LICENSE_LIST_VERSION);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreationInfo#getTypedValue()}.
	 * @throws InvalidSPDXAnalysisException
	 */
	@Test
	public void testGetTypedValue() throws InvalidSPDXAnalysisException {
		LicenseCreationInfo lci = new LicenseCreationInfo(licenseCreatorAgent, licenseListReleaseDate);
		TypedValue result = lci.getTypedValue();
		assertEquals(LicenseCreationInfo.CREATION_INFO_URI, result.getObjectUri());
		assertEquals(SpdxConstantsV3.CORE_CREATION_INFO, result.getType());
		assertEquals(SpdxConstantsV3.MODEL_SPEC_VERSION, result.getSpecVersion());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreationInfo#isCollectionProperty(org.spdx.storage.PropertyDescriptor)}.
	 */
	@Test
	public void testIsCollectionProperty() throws InvalidSPDXAnalysisException {
		LicenseCreationInfo lci = new LicenseCreationInfo(licenseCreatorAgent, licenseListReleaseDate);
		assertTrue(lci.isCollectionProperty(SpdxConstantsV3.PROP_CREATED_BY));
		assertFalse(lci.isCollectionProperty(SpdxConstantsV3.PROP_CREATED));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreationInfo#getValueList(org.spdx.storage.PropertyDescriptor)}.
	 */
	@Test
	public void testGetValueList() throws InvalidSPDXAnalysisException {
		LicenseCreationInfo lci = new LicenseCreationInfo(licenseCreatorAgent, licenseListReleaseDate);
		List<?> result = lci.getValueList(SpdxConstantsV3.PROP_CREATED_BY);
		assertEquals(1, result.size());
		Object value = result.toArray()[0];
		assertTrue(value instanceof TypedValue);
		assertEquals(licenseCreatorAgent.getObjectUri(), ((TypedValue)value).getObjectUri());
		assertEquals(SpdxConstantsV3.CORE_AGENT, ((TypedValue)value).getType());
		assertEquals(SpdxConstantsV3.MODEL_SPEC_VERSION, ((TypedValue)value).getSpecVersion());
		assertTrue(lci.getValueList(SpdxConstantsV3.PROP_CREATED).isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreationInfo#getValue(org.spdx.storage.PropertyDescriptor)}.
	 */
	@Test
	public void testGetValue() throws InvalidSPDXAnalysisException {
		LicenseCreationInfo lci = new LicenseCreationInfo(licenseCreatorAgent, licenseListReleaseDate);
		Object result = lci.getValue(SpdxConstantsV3.PROP_COMMENT);
		assertTrue(result instanceof String);
		assertFalse(((String)result).isEmpty());
		result = lci.getValue(SpdxConstantsV3.PROP_CREATED);
		assertTrue(result instanceof String);
		assertEquals(licenseListReleaseDate, result);
		result = lci.getValue(SpdxConstantsV3.PROP_CREATED_BY);
		assertTrue(result instanceof List);
		assertFalse(((List<?>)result).isEmpty());
		result = lci.getValue(SpdxConstantsV3.PROP_SPEC_VERSION);
		assertTrue(result instanceof String);
		assertEquals(SpdxConstantsV3.MODEL_SPEC_VERSION, result);
		assertTrue(Objects.isNull(lci.getValue(SpdxConstantsV3.PROP_DOMAIN)));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreationInfo#isCollectionMembersAssignableTo(org.spdx.storage.PropertyDescriptor, java.lang.Class)}.
	 */
	@Test
	public void testIsCollectionMembersAssignableTo() throws InvalidSPDXAnalysisException {
		LicenseCreationInfo lci = new LicenseCreationInfo(licenseCreatorAgent, licenseListReleaseDate);
		assertTrue(lci.isCollectionMembersAssignableTo(SpdxConstantsV3.PROP_CREATED_BY, Agent.class));
		assertTrue(lci.isCollectionMembersAssignableTo(SpdxConstantsV3.PROP_CREATED_BY, LicenseCreatorAgent.class));
		assertFalse(lci.isCollectionMembersAssignableTo(SpdxConstantsV3.PROP_CREATED_BY, String.class));
		assertFalse(lci.isCollectionMembersAssignableTo(SpdxConstantsV3.PROP_CREATED, String.class));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreationInfo#isPropertyValueAssignableTo(org.spdx.storage.PropertyDescriptor, java.lang.Class)}.
	 */
	@Test
	public void testIsPropertyValueAssignableTo() throws InvalidSPDXAnalysisException {
		LicenseCreationInfo lci = new LicenseCreationInfo(licenseCreatorAgent, licenseListReleaseDate);
		assertTrue(lci.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_COMMENT, String.class));
		assertTrue(lci.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_CREATED, String.class));
		assertTrue(lci.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_SPEC_VERSION, String.class));
		assertFalse(lci.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_CREATED_BY, String.class));
		assertFalse(lci.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_DOWNLOAD_LOCATION, String.class));
	}

}
