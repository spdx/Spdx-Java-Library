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
package org.spdx.library.model.v3;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v3.core.Agent;
import org.spdx.library.model.v3.core.CreationInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * @author gary
 *
 */
public class SpdxModelClassFactoryTest {

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
	 * Test method for {@link org.spdx.library.model.v3.SpdxModelClassFactory#createCreationInfo(org.spdx.storage.IModelStore, java.lang.String, java.lang.String, org.spdx.core.IModelCopyManager)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testCreateCreationInfo() throws InvalidSPDXAnalysisException {
		IModelStore modelStore = new InMemSpdxStore();
		IModelCopyManager copyManager = new ModelCopyManager();
		String createdByUri = "urn:some.name";
		String createdByName = "My name";
		CreationInfo result = SpdxModelClassFactory.createCreationInfo(modelStore, createdByUri, createdByName, copyManager);
		assertEquals(IdType.Anonymous, modelStore.getIdType(result.getObjectUri()));
		assertEquals(1, result.getCreatedBys().size());
		assertEquals(0, result.verify().size());
		Agent agent = result.getCreatedBys().toArray(new Agent[1])[0];
		assertEquals(createdByUri, agent.getObjectUri());
		assertEquals(createdByName, agent.getName().get());
		assertTrue(result.equivalent(agent.getCreationInfo()));
		assertEquals(result, agent.getCreationInfo());
	}

}
