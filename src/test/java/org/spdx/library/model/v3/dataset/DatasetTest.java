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
 
package org.spdx.library.model.v3.dataset;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.core.PresenceType;
import org.spdx.library.model.v3.dataset.Dataset.DatasetBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class DatasetTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final Integer DATASET_SIZE_TEST_VALUE = 55;
	static final String DATASET_UPDATE_MECHANISM_TEST_VALUE = "test datasetUpdateMechanism";
	static final String DATA_COLLECTION_PROCESS_TEST_VALUE = "test dataCollectionProcess";
	static final String INTENDED_USE_TEST_VALUE = "test intendedUse";
	static final String DATASET_NOISE_TEST_VALUE = "test datasetNoise";
	static final PresenceType SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1 = PresenceType.values()[0];
	static final PresenceType SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2 = PresenceType.values()[1];
	static final DatasetAvailabilityType DATASET_AVAILABILITY_TEST_VALUE1 = DatasetAvailabilityType.values()[0];
	static final DatasetAvailabilityType DATASET_AVAILABILITY_TEST_VALUE2 = DatasetAvailabilityType.values()[1];
	static final ConfidentialityLevelType CONFIDENTIALITY_LEVEL_TEST_VALUE1 = ConfidentialityLevelType.values()[0];
	static final ConfidentialityLevelType CONFIDENTIALITY_LEVEL_TEST_VALUE2 = ConfidentialityLevelType.values()[1];
	static final String ANONYMIZATION_METHOD_USED_TEST_VALUE1 = "test 1 anonymizationMethodUsed";
	static final String ANONYMIZATION_METHOD_USED_TEST_VALUE2 = "test 2 anonymizationMethodUsed";
	static final String ANONYMIZATION_METHOD_USED_TEST_VALUE3 = "test 3 anonymizationMethodUsed";
	static final List<String> ANONYMIZATION_METHOD_USED_TEST_LIST1 = Arrays.asList(new String[] { ANONYMIZATION_METHOD_USED_TEST_VALUE1, ANONYMIZATION_METHOD_USED_TEST_VALUE2 });
	static final List<String> ANONYMIZATION_METHOD_USED_TEST_LIST2 = Arrays.asList(new String[] { ANONYMIZATION_METHOD_USED_TEST_VALUE3 });
	static final String KNOWN_BIAS_TEST_VALUE1 = "test 1 knownBias";
	static final String KNOWN_BIAS_TEST_VALUE2 = "test 2 knownBias";
	static final String KNOWN_BIAS_TEST_VALUE3 = "test 3 knownBias";
	static final List<String> KNOWN_BIAS_TEST_LIST1 = Arrays.asList(new String[] { KNOWN_BIAS_TEST_VALUE1, KNOWN_BIAS_TEST_VALUE2 });
	static final List<String> KNOWN_BIAS_TEST_LIST2 = Arrays.asList(new String[] { KNOWN_BIAS_TEST_VALUE3 });
	static final String DATA_PREPROCESSING_TEST_VALUE1 = "test 1 dataPreprocessing";
	static final String DATA_PREPROCESSING_TEST_VALUE2 = "test 2 dataPreprocessing";
	static final String DATA_PREPROCESSING_TEST_VALUE3 = "test 3 dataPreprocessing";
	static final List<String> DATA_PREPROCESSING_TEST_LIST1 = Arrays.asList(new String[] { DATA_PREPROCESSING_TEST_VALUE1, DATA_PREPROCESSING_TEST_VALUE2 });
	static final List<String> DATA_PREPROCESSING_TEST_LIST2 = Arrays.asList(new String[] { DATA_PREPROCESSING_TEST_VALUE3 });
	static final DatasetType DATASET_TYPE_TEST_VALUE1 = DatasetType.values()[0];
	static final DatasetType DATASET_TYPE_TEST_VALUE2 = DatasetType.values()[1];
	static final List<DatasetType> DATASET_TYPE_TEST_LIST1 = Arrays.asList(new DatasetType[] { DATASET_TYPE_TEST_VALUE1, DATASET_TYPE_TEST_VALUE2 });
	static final List<DatasetType> DATASET_TYPE_TEST_LIST2 = Arrays.asList(new DatasetType[] { DATASET_TYPE_TEST_VALUE1 });
	
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
		DatasetBuilder retval = new DatasetBuilder(modelStore, objectUri, copyManager)
				.setDatasetSize(DATASET_SIZE_TEST_VALUE)
				.setDatasetUpdateMechanism(DATASET_UPDATE_MECHANISM_TEST_VALUE)
				.setDataCollectionProcess(DATA_COLLECTION_PROCESS_TEST_VALUE)
				.setIntendedUse(INTENDED_USE_TEST_VALUE)
				.setDatasetNoise(DATASET_NOISE_TEST_VALUE)
				.addAnonymizationMethodUsed(ANONYMIZATION_METHOD_USED_TEST_VALUE1)
				.addAnonymizationMethodUsed(ANONYMIZATION_METHOD_USED_TEST_VALUE2)
				.addKnownBias(KNOWN_BIAS_TEST_VALUE1)
				.addKnownBias(KNOWN_BIAS_TEST_VALUE2)
				.addDataPreprocessing(DATA_PREPROCESSING_TEST_VALUE1)
				.addDataPreprocessing(DATA_PREPROCESSING_TEST_VALUE2)
				.setSensitivePersonalInformation(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1)
				.setDatasetAvailability(DATASET_AVAILABILITY_TEST_VALUE1)
				.setConfidentialityLevel(CONFIDENTIALITY_LEVEL_TEST_VALUE1)
				.addDatasetType(DATASET_TYPE_TEST_VALUE1)
				.addDatasetType(DATASET_TYPE_TEST_VALUE2)
				//TODO: Add in test values
				/********************
				.addSensor(DictionaryEntry)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testDataset.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Dataset.Dataset", testDataset.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("Dataset: "+TEST_OBJECT_URI, testDataset.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#Element(org.spdx.library.model.v3.dataset.Dataset.DatasetBuilder)}.
	 */
	public void testDatasetDatasetBuilder() throws InvalidSPDXAnalysisException {
		builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		Dataset test2Dataset = builderForDatasetTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testDataset.equivalent(test2Dataset));
		assertTrue(test2Dataset.equivalent(testDataset));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#setSensitivePersonalInformation}.
	 */
	public void testDatasetsetSensitivePersonalInformation() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1), testDataset.getSensitivePersonalInformation());
		testDataset.setSensitivePersonalInformation(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2);
		assertEquals(Optional.of(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2), testDataset.getSensitivePersonalInformation());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#setDatasetAvailability}.
	 */
	public void testDatasetsetDatasetAvailability() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(DATASET_AVAILABILITY_TEST_VALUE1), testDataset.getDatasetAvailability());
		testDataset.setDatasetAvailability(DATASET_AVAILABILITY_TEST_VALUE2);
		assertEquals(Optional.of(DATASET_AVAILABILITY_TEST_VALUE2), testDataset.getDatasetAvailability());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#setConfidentialityLevel}.
	 */
	public void testDatasetsetConfidentialityLevel() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(CONFIDENTIALITY_LEVEL_TEST_VALUE1), testDataset.getConfidentialityLevel());
		testDataset.setConfidentialityLevel(CONFIDENTIALITY_LEVEL_TEST_VALUE2);
		assertEquals(Optional.of(CONFIDENTIALITY_LEVEL_TEST_VALUE2), testDataset.getConfidentialityLevel());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#setDatasetSize}.
	 */
	public void testDatasetsetDatasetSize() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(DATASET_SIZE_TEST_VALUE), testDataset.getDatasetSize());
		testDataset.setDatasetSize(new Integer(653));
		assertEquals(Optional.of(new Integer(653)), testDataset.getDatasetSize());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#setDatasetUpdateMechanism}.
	 */
	public void testDatasetsetDatasetUpdateMechanism() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(DATASET_UPDATE_MECHANISM_TEST_VALUE), testDataset.getDatasetUpdateMechanism());
		testDataset.setDatasetUpdateMechanism("new datasetUpdateMechanism value");
		assertEquals(Optional.of("new datasetUpdateMechanism value"), testDataset.getDatasetUpdateMechanism());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#setDataCollectionProcess}.
	 */
	public void testDatasetsetDataCollectionProcess() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(DATA_COLLECTION_PROCESS_TEST_VALUE), testDataset.getDataCollectionProcess());
		testDataset.setDataCollectionProcess("new dataCollectionProcess value");
		assertEquals(Optional.of("new dataCollectionProcess value"), testDataset.getDataCollectionProcess());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#setIntendedUse}.
	 */
	public void testDatasetsetIntendedUse() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(INTENDED_USE_TEST_VALUE), testDataset.getIntendedUse());
		testDataset.setIntendedUse("new intendedUse value");
		assertEquals(Optional.of("new intendedUse value"), testDataset.getIntendedUse());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#setDatasetNoise}.
	 */
	public void testDatasetsetDatasetNoise() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(DATASET_NOISE_TEST_VALUE), testDataset.getDatasetNoise());
		testDataset.setDatasetNoise("new datasetNoise value");
		assertEquals(Optional.of("new datasetNoise value"), testDataset.getDatasetNoise());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#getSensor}.
	 */
	public void testDatasetgetSensors() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testDataset.getSensors())));
//		testDataset.getSensors().clear();
//		testDataset.getSensors().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testDataset.getSensors())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#getAnonymizationMethodUseds}.
	 */
	public void testDatasetgetAnonymizationMethodUseds() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(ANONYMIZATION_METHOD_USED_TEST_LIST1, new ArrayList<>(testDataset.getAnonymizationMethodUseds())));
		testDataset.getAnonymizationMethodUseds().clear();
		testDataset.getAnonymizationMethodUseds().addAll(ANONYMIZATION_METHOD_USED_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(ANONYMIZATION_METHOD_USED_TEST_LIST2, new ArrayList<>(testDataset.getAnonymizationMethodUseds())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#getKnownBiass}.
	 */
	public void testDatasetgetKnownBiass() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(KNOWN_BIAS_TEST_LIST1, new ArrayList<>(testDataset.getKnownBiass())));
		testDataset.getKnownBiass().clear();
		testDataset.getKnownBiass().addAll(KNOWN_BIAS_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(KNOWN_BIAS_TEST_LIST2, new ArrayList<>(testDataset.getKnownBiass())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#getDataPreprocessings}.
	 */
	public void testDatasetgetDataPreprocessings() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(DATA_PREPROCESSING_TEST_LIST1, new ArrayList<>(testDataset.getDataPreprocessings())));
		testDataset.getDataPreprocessings().clear();
		testDataset.getDataPreprocessings().addAll(DATA_PREPROCESSING_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(DATA_PREPROCESSING_TEST_LIST2, new ArrayList<>(testDataset.getDataPreprocessings())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.dataset.Dataset#getDatasetType}.
	 */
	public void testDatasetgetDatasetTypes() throws InvalidSPDXAnalysisException {
		Dataset testDataset = builderForDatasetTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(DATASET_TYPE_TEST_LIST1, new ArrayList<>(testDataset.getDatasetTypes())));
		testDataset.getDatasetTypes().clear();
		testDataset.getDatasetTypes().addAll(DATASET_TYPE_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(DATASET_TYPE_TEST_LIST2, new ArrayList<>(testDataset.getDatasetTypes())));
	}
}