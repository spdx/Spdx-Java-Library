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
package org.spdx.library.conversion;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v3_0_0.core.ExternalMap;
import org.spdx.library.model.v3_0_0.core.Hash;
import org.spdx.library.model.v3_0_0.core.IntegrityMethod;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * @author gary
 *
 */
public class ExternalMapInfoTest {
	
	IModelStore modelStore;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SpdxModelFactory.init();
		modelStore = new InMemSpdxStore();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.ExternalMapInfo#addExternalMap(java.lang.String, org.spdx.storage.IModelStore)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testAddExternalMap() throws InvalidSPDXAnalysisException {
		String docRefId = SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM + "test";
		String externalDocumentUri = "https://external/doc/uri";
		Hash hash = new Hash(modelStore, modelStore.getNextId(IdType.Anonymous), null, true, null);
		Optional<Hash> externalDocumentHash = Optional.of(hash);
		List<ExternalMap> docImports = new ArrayList<>();
		ExternalMapInfo emi = new ExternalMapInfo(docRefId, externalDocumentUri, externalDocumentHash, docImports);
		String externalUri = externalDocumentUri + "#" + SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM + "test";
		assertTrue(docImports.isEmpty());
		emi.addExternalMap(externalUri, modelStore);
		assertEquals(1, docImports.size());
		assertEquals(externalUri, docImports.get(0).getExternalSpdxId());
		assertEquals(externalDocumentUri, docImports.get(0).getLocationHint().get());
		IntegrityMethod[] hashes = docImports.get(0).getVerifiedUsings().toArray(new IntegrityMethod[docImports.get(0).getVerifiedUsings().size()]);
		assertEquals(1, hashes.length);
		assertTrue(hashes[0] instanceof Hash);
		assertTrue(hashes[0].equivalent(hash));
		emi.addExternalMap(externalUri, modelStore);
		// already added
		assertEquals(1, docImports.size());
	}

}
