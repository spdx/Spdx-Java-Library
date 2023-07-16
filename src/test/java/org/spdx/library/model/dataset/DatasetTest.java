/**
 * Copyright (c) 2023 Source Auditor Inc.
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
 
package org.spdx.library.model.dataset;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.dataset.Dataset.DatasetBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class DatasetTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";

	IModelStore modelStore;
	ModelCopyManager copyManager;

	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static DatasetBuilder builderForDatasetTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		DatasetBuilder retval = new DatasetBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setsensitivePersonalInformation(new PresenceType())
		.setconfidentialityLevel(ConfidentialityLevelType.ENUM)
		.setdatasetAvailability(DatasetAvailabilityType.ENUM)
		.setdatasetSize(57)
		.setintendedUse("A string")
		.setdatasetNoise("A string")
		.setdataCollectionProcess("A string")
		.setdatasetUpdateMechanism("A string")
		.getsensor.add(DictionaryEntry)
		.getanonymizationMethodUsed.add("Test string")
		.getdataPreprocessing.add("Test string")
		.getknownBias.add("Test string")
		.getdatasetType.add(DatasetType.ENUM)
		***************/
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testDataset.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Dataset.Dataset", testDataset.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Dataset: "+TEST_OBJECT_URI, testDataset.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#Element(org.spdx.library.model.dataset.Dataset.DatasetBuilder)}.
	 */
	public void testDatasetDatasetBuilder() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Dataset test2Dataset = builderForDatasetTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testDataset.equivalent(test2Dataset));
		assertTrue(test2Dataset.equivalent(testDataset));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#setSensitivePersonalInformation}.
	 */
	public void testDatasetsetSensitivePersonalInformation() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDataset.getSensitivePersonalInformation());
//		testDataset.setSensitivePersonalInformation(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDataset.getSensitivePersonalInformation());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#setConfidentialityLevel}.
	 */
	public void testDatasetsetConfidentialityLevel() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDataset.getConfidentialityLevel());
//		testDataset.setConfidentialityLevel(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDataset.getConfidentialityLevel());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#setDatasetAvailability}.
	 */
	public void testDatasetsetDatasetAvailability() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDataset.getDatasetAvailability());
//		testDataset.setDatasetAvailability(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDataset.getDatasetAvailability());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#setDatasetSize}.
	 */
	public void testDatasetsetDatasetSize() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDataset.getDatasetSize());
//		testDataset.setDatasetSize(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDataset.getDatasetSize());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#setIntendedUse}.
	 */
	public void testDatasetsetIntendedUse() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDataset.getIntendedUse());
//		testDataset.setIntendedUse(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDataset.getIntendedUse());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#setDatasetNoise}.
	 */
	public void testDatasetsetDatasetNoise() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDataset.getDatasetNoise());
//		testDataset.setDatasetNoise(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDataset.getDatasetNoise());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#setDataCollectionProcess}.
	 */
	public void testDatasetsetDataCollectionProcess() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDataset.getDataCollectionProcess());
//		testDataset.setDataCollectionProcess(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDataset.getDataCollectionProcess());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#setDatasetUpdateMechanism}.
	 */
	public void testDatasetsetDatasetUpdateMechanism() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testDataset.getDatasetUpdateMechanism());
//		testDataset.setDatasetUpdateMechanism(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testDataset.getDatasetUpdateMechanism());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#getSensor}.
	 */
	public void testDatasetsetSensor() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testDataset.getSensor()));
//		testDataset.getSensor().clear();
//		testDataset.getSensor().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testDataset.getSensor()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#getAnonymizationMethodUsed}.
	 */
	public void testDatasetgetAnonymizationMethodUsed() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testDataset.getAnonymizationMethodUsed()));
//		testDataset.getAnonymizationMethodUsed().clear();
//		testDataset.getAnonymizationMethodUsed().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testDataset.getAnonymizationMethodUsed()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#getDataPreprocessing}.
	 */
	public void testDatasetgetDataPreprocessing() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testDataset.getDataPreprocessing()));
//		testDataset.getDataPreprocessing().clear();
//		testDataset.getDataPreprocessing().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testDataset.getDataPreprocessing()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#getKnownBias}.
	 */
	public void testDatasetgetKnownBias() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testDataset.getKnownBias()));
//		testDataset.getKnownBias().clear();
//		testDataset.getKnownBias().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testDataset.getKnownBias()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.dataset.Dataset#getDatasetType}.
	 */
	public void testDatasetgetDatasetType() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testDataset.getDatasetType()));
//		testDataset.getDatasetType().clear();
//		testDataset.getDatasetType().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testDataset.getDatasetType()));
		fail("Not yet implemented");
	}

/*
*/

}