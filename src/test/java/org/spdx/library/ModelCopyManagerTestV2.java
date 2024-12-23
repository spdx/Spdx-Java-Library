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
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.TypedValue;
import org.spdx.library.model.v2.Checksum;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.SpdxFile;
import org.spdx.library.model.v2.SpdxPackage;
import org.spdx.library.model.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.v2.enumerations.RelationshipType;
import org.spdx.library.model.v2.license.AnyLicenseInfo;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.compatv2.CompatibleModelStoreWrapper;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * @author Gary O'Neall
 */
public class ModelCopyManagerTestV2 {
	private static final String ORIGINAL_NAMESPACE = "http://something#";
	private static final String CHECKSUM_OBJECT_URI = ORIGINAL_NAMESPACE + "SPDXRef-15";
	private static final String OBJECT_URI2 = ORIGINAL_NAMESPACE + "SPDXRef-somethigelse";
	private static final String CHECKSUM_TYPE = "Checksum";
	private static final String NAMESPACE = "https://spdx/namespace#";
	private static final ChecksumAlgorithm CHECKSUM_ALGORITHM = ChecksumAlgorithm.MD5;
	private static final String CHECKSUM_VALUE = "912ec803b2ce49e4a541068d495ab570";
	private ModelCopyManager modelCopyManager;
	private InMemSpdxStore fromStore;
	private InMemSpdxStore toStore;
	private Checksum checksum;
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
		checksum = (Checksum)SpdxModelFactory.inflateModelObject(fromStore, CHECKSUM_OBJECT_URI, CHECKSUM_TYPE,
				modelCopyManager, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, true, null);
		checksum.setAlgorithm(CHECKSUM_ALGORITHM);
		checksum.setValue(CHECKSUM_VALUE);
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
		assertTrue(Objects.isNull(modelCopyManager.getCopiedObjectUri(fromStore, CHECKSUM_OBJECT_URI, toStore)));
		assertTrue(Objects.isNull(modelCopyManager.putCopiedId(fromStore, CHECKSUM_OBJECT_URI, toStore, OBJECT_URI2)));
		assertEquals(OBJECT_URI2, (modelCopyManager.getCopiedObjectUri(fromStore, CHECKSUM_OBJECT_URI, toStore)));
	}

	/**
	 * Test method for {@link org.spdx.library.ModelCopyManager#copy(org.spdx.storage.IModelStore, org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testCopyIModelStoreIModelStoreStringStringStringString() throws InvalidSPDXAnalysisException {
		modelCopyManager.copy(toStore, OBJECT_URI2, fromStore, CHECKSUM_OBJECT_URI, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, NAMESPACE);
		assertEquals(OBJECT_URI2, (modelCopyManager.getCopiedObjectUri(fromStore, CHECKSUM_OBJECT_URI, toStore)));
		Checksum copiedChecksum = (Checksum)SpdxModelFactory.inflateModelObject(toStore, OBJECT_URI2,
				CHECKSUM_TYPE, modelCopyManager, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, false, null);
		assertTrue(checksum.equivalent(copiedChecksum));
		assertEquals(CHECKSUM_ALGORITHM, copiedChecksum.getAlgorithm());
		assertEquals(CHECKSUM_VALUE, copiedChecksum.getValue());
	}
	
	@Test
	public void testDeepComplexCopy() throws InvalidSPDXAnalysisException {
		String docUri = "https://spdx/doc";
		SpdxDocument doc = new SpdxDocument(fromStore, docUri, modelCopyManager, true);
		doc.setCreationInfo(doc.createCreationInfo(Arrays.asList(new String[] {"Person: Gary"}), date));
		ExtractedLicenseInfo lic = new ExtractedLicenseInfo(fromStore, docUri, fromStore.getNextId(IdType.LicenseRef),
				modelCopyManager, true);
		lic.setExtractedText("License Text");
		
		SpdxFile spdxFile = doc.createSpdxFile(fromStore.getNextId(IdType.SpdxId), "fileName", 
				lic, Arrays.asList(new AnyLicenseInfo[] {lic}), "copyright text", checksum).build();
		
		SpdxPackage pkg = doc.createPackage(fromStore.getNextId(IdType.SpdxId), "Name", lic, "PkgCopyright", lic)
				.setFilesAnalyzed(false)
				.build();
		Relationship rel = pkg.createRelationship(spdxFile, RelationshipType.CONTAINS, "comment");
		pkg.addRelationship(rel);
		doc.addRelationship(doc.createRelationship(pkg, RelationshipType.DESCRIBES, null));
		TypedValue result = modelCopyManager.copy(toStore, fromStore, doc.getObjectUri(), CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, NAMESPACE);
		
		assertEquals(NAMESPACE + "SPDXRef-DOCUMENT", result.getObjectUri());
		assertEquals(doc.getType(), result.getType());
		assertEquals(CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, result.getSpecVersion());
		SpdxDocument copiedDoc = (SpdxDocument)SpdxModelFactory.inflateModelObject(toStore, result.getObjectUri(), 
				result.getType(), modelCopyManager, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, false, null);
		assertTrue(copiedDoc.equivalent(doc));
		
	}
	
	/**
	 * Test method for {@link org.spdx.library.ModelCopyManager#copy(org.spdx.storage.IModelStore, java.lang.String, org.spdx.storage.IModelStore, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testCopyIModelStoreStringIModelStoreStringStringStringString() throws InvalidSPDXAnalysisException {
		// URI does not exist
		TypedValue result = modelCopyManager.copy(toStore, fromStore, CHECKSUM_OBJECT_URI, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, NAMESPACE);
		String expectedUri = NAMESPACE + CHECKSUM_OBJECT_URI.substring(ORIGINAL_NAMESPACE.length());
		assertEquals(expectedUri, result.getObjectUri());
		assertEquals(CHECKSUM_TYPE, result.getType());
		Checksum copiedChecksum = (Checksum)SpdxModelFactory.inflateModelObject(toStore, expectedUri, CHECKSUM_TYPE, modelCopyManager, 
				CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, false, null);
		assertTrue(checksum.equivalent(copiedChecksum));
		assertEquals(CHECKSUM_ALGORITHM, copiedChecksum.getAlgorithm());
		assertEquals(CHECKSUM_VALUE, copiedChecksum.getValue());
		assertEquals(CHECKSUM_TYPE, result.getType());
		assertEquals(expectedUri, result.getObjectUri());
		assertEquals(CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, result.getSpecVersion());
		// Anonymous fromUri 
		String differentUri = fromStore.getNextId(IdType.Anonymous);
		Checksum differentChecksum = (Checksum)SpdxModelFactory.inflateModelObject(fromStore, differentUri, 
				CHECKSUM_TYPE, modelCopyManager, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, true, null);
		differentChecksum.setAlgorithm(ChecksumAlgorithm.SHA1);
		differentChecksum.setValue("3da541559918a808c2402bba5012f6c60b27661c");
		result = modelCopyManager.copy(toStore, fromStore, differentUri, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, NAMESPACE);
		assertEquals(IdType.Anonymous, toStore.getIdType(result.getObjectUri()));
		assertEquals(CHECKSUM_TYPE, result.getType());
		assertEquals(CHECKSUM_TYPE, result.getType());assertEquals(CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, result.getSpecVersion());
		Checksum differentCopiedChecksum = (Checksum)SpdxModelFactory.inflateModelObject(toStore, 
				result.getObjectUri(), CHECKSUM_TYPE, modelCopyManager, 
				CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, true, null);
		assertTrue(differentChecksum.equivalent(differentCopiedChecksum));
	}
	
	@Test
	public void testCopyToExistingUri() throws InvalidSPDXAnalysisException {
		String differentUri = "http://prefix/something#"+fromStore.getNextId(IdType.SpdxId);
		modelCopyManager.copy(toStore, differentUri, fromStore, CHECKSUM_OBJECT_URI, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, NAMESPACE);
		Checksum differentChecksum = (Checksum)SpdxModelFactory.inflateModelObject(fromStore, differentUri, 
				CHECKSUM_TYPE, modelCopyManager, CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, true, null);
		differentChecksum.setAlgorithm(ChecksumAlgorithm.SHA1);
		differentChecksum.setValue("3da541559918a808c2402bba5012f6c60b27661c");
		TypedValue result = modelCopyManager.copy(toStore, fromStore, differentUri, 
				CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, NAMESPACE);
		assertEquals(IdType.SpdxId, toStore.getIdType(result.getObjectUri()));
		assertTrue(result.getObjectUri().startsWith(NAMESPACE));
		assertEquals(CHECKSUM_TYPE, result.getType());
		assertEquals(CHECKSUM_TYPE, result.getType());assertEquals(CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, result.getSpecVersion());
		Checksum differentCopiedChecksum = (Checksum)SpdxModelFactory.inflateModelObject(toStore, 
				result.getObjectUri(), CHECKSUM_TYPE, modelCopyManager, 
				CompatibleModelStoreWrapper.LATEST_SPDX_2X_VERSION, true, null);
		assertTrue(differentChecksum.equivalent(differentCopiedChecksum));
	}
}
