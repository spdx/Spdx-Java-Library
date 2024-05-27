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

import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.ModelRegistryException;
import org.spdx.core.SpdxInvalidIdException;
import org.spdx.core.SpdxInvalidTypeException;
import org.spdx.core.TypedValue;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.core.CreationInfo;

/**
 * @author gary
 *
 */
public class LicenseCreatorAgentTest {
	
	static final String LICENSE_LIST_VERSION = "3.24.0";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SpdxModelFactory.init();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}


	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreatorAgent#getObjectUri()}.
	 * @throws ModelRegistryException 
	 * @throws SpdxInvalidTypeException 
	 * @throws SpdxInvalidIdException 
	 */
	@Test
	public void testGetObjectUri() throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		LicenseCreatorAgent lca = new LicenseCreatorAgent(LICENSE_LIST_VERSION);
		assertEquals(LicenseCreatorAgent.OBJECT_URI_PREFIX + "3_24_0", lca.getObjectUri());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreatorAgent#getTypedValue()}.
	 * @throws ModelRegistryException 
	 * @throws SpdxInvalidTypeException 
	 * @throws SpdxInvalidIdException 
	 */
	@Test
	public void testGetTypedValue() throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		LicenseCreatorAgent lca = new LicenseCreatorAgent(LICENSE_LIST_VERSION);
		TypedValue result = lca.getTypedValue();
		assertEquals(LicenseCreatorAgent.OBJECT_URI_PREFIX + "3_24_0", result.getObjectUri());
		assertEquals(SpdxConstantsV3.MODEL_SPEC_VERSION, result.getSpecVersion());
		assertEquals(SpdxConstantsV3.CORE_AGENT, result.getType());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreatorAgent#getValueList(org.spdx.storage.PropertyDescriptor)}.
	 */
	@Test
	public void testGetValueList() throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		LicenseCreatorAgent lca = new LicenseCreatorAgent(LICENSE_LIST_VERSION);
		assertTrue(lca.getValueList(SpdxConstantsV3.PROP_CREATED).isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreatorAgent#getValue(org.spdx.storage.PropertyDescriptor)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testGetValue() throws InvalidSPDXAnalysisException {
		LicenseCreatorAgent lca = new LicenseCreatorAgent(LICENSE_LIST_VERSION);
		Object result = lca.getValue(SpdxConstantsV3.PROP_CREATION_INFO);
		assertTrue(result instanceof TypedValue);
		assertEquals(LicenseCreationInfo.CREATION_INFO_URI, ((TypedValue)result).getObjectUri());
		assertEquals(SpdxConstantsV3.CORE_CREATION_INFO, ((TypedValue)result).getType());
		assertEquals(SpdxConstantsV3.MODEL_SPEC_VERSION, ((TypedValue)result).getSpecVersion());
		result = lca.getValue(SpdxConstantsV3.PROP_NAME);
		assertTrue(result instanceof String);
		assertFalse(((String)result).isEmpty());
		result = lca.getValue(SpdxConstantsV3.PROP_DESCRIPTION);
		assertTrue(result instanceof String);
		assertFalse(((String)result).isEmpty());
		result = lca.getValue(SpdxConstantsV3.PROP_A_I_DOMAIN);
		assertTrue(Objects.isNull(result));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreatorAgent#isCollectionMembersAssignableTo(org.spdx.storage.PropertyDescriptor, java.lang.Class)}.
	 */
	@Test
	public void testIsCollectionMembersAssignableTo() throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		LicenseCreatorAgent lca = new LicenseCreatorAgent(LICENSE_LIST_VERSION);
		assertFalse(lca.isCollectionMembersAssignableTo(SpdxConstantsV3.PROP_CREATION_INFO, CreationInfo.class));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreatorAgent#isPropertyValueAssignableTo(org.spdx.storage.PropertyDescriptor, java.lang.Class)}.
	 */
	@Test
	public void testIsPropertyValueAssignableTo() throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		LicenseCreatorAgent lca = new LicenseCreatorAgent(LICENSE_LIST_VERSION);
		assertTrue(lca.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_CREATION_INFO, CreationInfo.class));
		assertTrue(lca.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_CREATION_INFO, LicenseCreationInfo.class));
		assertFalse(lca.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_CREATION_INFO, String.class));
		assertTrue(lca.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_NAME, String.class));
		assertTrue(lca.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_DESCRIPTION, String.class));
		assertFalse(lca.isPropertyValueAssignableTo(SpdxConstantsV3.PROP_COMMENT, String.class));
	}

	/**
	 * Test method for {@link org.spdx.storage.listedlicense.LicenseCreatorAgent#isCollectionProperty(org.spdx.storage.PropertyDescriptor)}.
	 */
	@Test
	public void testIsCollectionProperty() throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		LicenseCreatorAgent lca = new LicenseCreatorAgent(LICENSE_LIST_VERSION);
		assertFalse(lca.isCollectionProperty(SpdxConstantsV3.PROP_NAME));
	}
}
