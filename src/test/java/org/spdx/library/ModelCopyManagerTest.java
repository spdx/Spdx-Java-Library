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
package org.spdx.library;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.TypedValue;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v3_0_1.core.Agent;
import org.spdx.library.model.v3_0_1.core.CreationInfo;
import org.spdx.library.model.v3_0_1.core.Hash;
import org.spdx.library.model.v3_0_1.core.HashAlgorithm;
import org.spdx.library.model.v3_0_1.core.Relationship;
import org.spdx.library.model.v3_0_1.core.RelationshipType;
import org.spdx.library.model.v3_0_1.core.SpdxDocument;
import org.spdx.library.model.v3_0_1.software.SpdxFile;
import org.spdx.library.model.v3_0_1.software.SpdxPackage;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * @author Gary O'Neall
 */
public class ModelCopyManagerTest {
	
	private static final String HASH_OBJECT_URI = "http://something#SPDXRef-15";
	private static final String OBJECT_URI2 = "urn:object.uri.two";
	private static final String HASH_TYPE = "Core.Hash";
	private static final String NAMESPACE = "urn:namespace.";
	private static final HashAlgorithm HASH_ALGORITHM = HashAlgorithm.MD5;
	private static final String HASH_VALUE = "asfd1231";
	private ModelCopyManager modelCopyManager;
	private InMemSpdxStore fromStore;
	private InMemSpdxStore toStore;
	private Hash hash;
	String date;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SpdxModelFactory.init();
		modelCopyManager = new ModelCopyManager();
		fromStore = new InMemSpdxStore();
		toStore = new InMemSpdxStore();
		hash = (Hash)SpdxModelFactory.inflateModelObject(fromStore, HASH_OBJECT_URI, HASH_TYPE, modelCopyManager, "3.0.1", true, null);
		hash.setAlgorithm(HASH_ALGORITHM);
		hash.setHashValue(HASH_VALUE);
		DateFormat format = new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT);
		date = format.format(new Date());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.spdx.library.ModelCopyManager#getCopiedObjectUri(org.spdx.storage.IModelStore, java.lang.String, org.spdx.storage.IModelStore)}.
	 */
	@Test
	public void testGetCopiedObjectUri() {
		assertTrue(Objects.isNull(modelCopyManager.getCopiedObjectUri(fromStore, HASH_OBJECT_URI, toStore)));
		assertTrue(Objects.isNull(modelCopyManager.putCopiedId(fromStore, HASH_OBJECT_URI, toStore, OBJECT_URI2)));
		assertEquals(OBJECT_URI2, (modelCopyManager.getCopiedObjectUri(fromStore, HASH_OBJECT_URI, toStore)));
	}

	/**
	 * Test method for {@link org.spdx.library.ModelCopyManager#copy(org.spdx.storage.IModelStore, org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testCopyIModelStoreIModelStoreStringStringStringString() throws InvalidSPDXAnalysisException {
		modelCopyManager.copy(toStore, OBJECT_URI2, fromStore, HASH_OBJECT_URI, "3.0.1", NAMESPACE);
		assertEquals(OBJECT_URI2, (modelCopyManager.getCopiedObjectUri(fromStore, HASH_OBJECT_URI, toStore)));
		Hash copiedHash = (Hash)SpdxModelFactory.inflateModelObject(toStore, OBJECT_URI2, HASH_TYPE, modelCopyManager, "3.0.1", false, null);
		assertTrue(hash.equivalent(copiedHash));
		assertEquals(HASH_ALGORITHM, copiedHash.getAlgorithm());
		assertEquals(HASH_VALUE, copiedHash.getHashValue());
	}
	
	@Test
	public void testDeepComplexCopy() throws InvalidSPDXAnalysisException {
		CreationInfo creationInfo = hash.createCreationInfo(fromStore.getNextId(IdType.Anonymous))
				.setCreated(date)
				.setSpecVersion("3.0.1")
				.build();
		Agent agent = hash.createAgent(fromStore.getNextId(IdType.SpdxId))
				.setCreationInfo(creationInfo)
				.setName("Name")
				.build();
		creationInfo.getCreatedBys().add(agent);
		agent.setCreationInfo(creationInfo);
		SpdxFile spdxFile = agent.createSpdxFile(fromStore.getNextId(IdType.SpdxId))
				.setName("fileName")
				.setCopyrightText("copyrightText")
				.addVerifiedUsing(hash)
				.build();
		SpdxPackage pkg = spdxFile.createSpdxPackage(fromStore.getNextId(IdType.SpdxId))
				.setName("packageName")
				.setBuiltTime(date)
				.build();
		Relationship rel = pkg.createRelationship(fromStore.getNextId(IdType.SpdxId))
				.setComment("relationship comment")
				.setFrom(pkg)
				.addTo(spdxFile)
				.setRelationshipType(RelationshipType.CONTAINS)
				.build();
		SpdxDocument doc = pkg.createSpdxDocument(fromStore.getNextId(IdType.SpdxId))
				.setName("SPDX Document")
				.addElement(agent)
				.addElement(spdxFile)
				.addElement(pkg)
				.addElement(rel)
				.build();
		TypedValue result = modelCopyManager.copy(toStore, fromStore, doc.getObjectUri(), "3.0.1", NAMESPACE);
		assertEquals(doc.getObjectUri(), result.getObjectUri());
		assertEquals(doc.getType(), result.getType());
		assertEquals("3.0.1", result.getSpecVersion());
		SpdxDocument copiedDoc = (SpdxDocument)SpdxModelFactory.inflateModelObject(toStore, result.getObjectUri(), result.getType(), modelCopyManager, 
				"3.0.1", false, null);
		assertTrue(copiedDoc.equivalent(doc));
		
	}
	
	/**
	 * Test method for {@link org.spdx.library.ModelCopyManager#copy(org.spdx.storage.IModelStore, java.lang.String, org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testCopyIModelStoreStringIModelStoreStringStringStringString() throws InvalidSPDXAnalysisException {
		// URI does not exist
		TypedValue result = modelCopyManager.copy(toStore, fromStore, HASH_OBJECT_URI, "3.0.1", NAMESPACE);
		Hash copiedHash = (Hash)SpdxModelFactory.inflateModelObject(toStore, HASH_OBJECT_URI, HASH_TYPE, modelCopyManager, 
				"3.0.1", false, null);
		assertTrue(hash.equivalent(copiedHash));
		assertEquals(HASH_ALGORITHM, copiedHash.getAlgorithm());
		assertEquals(HASH_VALUE, copiedHash.getHashValue());
		assertEquals(HASH_TYPE, result.getType());
		assertEquals(HASH_OBJECT_URI, result.getObjectUri());
		assertEquals("3.0.1", result.getSpecVersion());
		// Anonymous fromUri 
		String differentUri = fromStore.getNextId(IdType.Anonymous);
		Hash differentHash = (Hash)SpdxModelFactory.inflateModelObject(fromStore, differentUri, HASH_TYPE, modelCopyManager, 
				"3.0.1", true, null);
		differentHash.setAlgorithm(HashAlgorithm.BLAKE2B256);
		differentHash.setHashValue("asasdf1309u93u");
		result = modelCopyManager.copy(toStore, fromStore, differentUri, "3.0.1", NAMESPACE);
		assertEquals(IdType.Anonymous, toStore.getIdType(result.getObjectUri()));
		assertEquals(HASH_TYPE, result.getType());
		assertEquals(HASH_TYPE, result.getType());assertEquals("3.0.1", result.getSpecVersion());
		Hash differentCopiedHash = (Hash)SpdxModelFactory.inflateModelObject(toStore, result.getObjectUri(), HASH_TYPE, 
				modelCopyManager, "3.0.1", true, null);
		assertTrue(differentHash.equivalent(differentCopiedHash));
	}
	
	@Test
	public void testCopyToExistingUri() throws InvalidSPDXAnalysisException {
		String differentUri = "http://prefix/something#"+fromStore.getNextId(IdType.SpdxId);
		modelCopyManager.copy(toStore, differentUri, fromStore, HASH_OBJECT_URI, "3.0.1", NAMESPACE);
		Hash differentHash = (Hash)SpdxModelFactory.inflateModelObject(fromStore, differentUri, HASH_TYPE, modelCopyManager, 
				"3.0.1", true, null);
		differentHash.setAlgorithm(HashAlgorithm.BLAKE2B256);
		differentHash.setHashValue("asasdf1309u93u");
		TypedValue result = modelCopyManager.copy(toStore, fromStore, differentUri, "3.0.1", NAMESPACE);
		assertEquals(IdType.SpdxId, toStore.getIdType(result.getObjectUri()));
		assertTrue(result.getObjectUri().startsWith(NAMESPACE));
		assertEquals(HASH_TYPE, result.getType());
		assertEquals(HASH_TYPE, result.getType());assertEquals("3.0.1", result.getSpecVersion());
		Hash differentCopiedHash = (Hash)SpdxModelFactory.inflateModelObject(toStore, result.getObjectUri(), HASH_TYPE, 
				modelCopyManager, "3.0.1", true, null);
		assertTrue(differentHash.equivalent(differentCopiedHash));
	}
}
