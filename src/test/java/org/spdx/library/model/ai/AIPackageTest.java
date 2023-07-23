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
 
package org.spdx.library.model.ai;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.ai.AIPackage.AIPackageBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class AIPackageTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String INFORMATION_ABOUT_TRAINING_TEST_VALUE = "test informationAboutTraining";
	static final String LIMITATION_TEST_VALUE = "test limitation";
	static final String ENERGY_CONSUMPTION_TEST_VALUE = "test energyConsumption";
	static final String INFORMATION_ABOUT_APPLICATION_TEST_VALUE = "test informationAboutApplication";
	static final PresenceType SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1 = PresenceType.values()[0];
	static final PresenceType SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2 = PresenceType.values()[1];
	static final SafetyRiskAssessmentType SAFETY_RISK_ASSESSMENT_TEST_VALUE1 = SafetyRiskAssessmentType.values()[0];
	static final SafetyRiskAssessmentType SAFETY_RISK_ASSESSMENT_TEST_VALUE2 = SafetyRiskAssessmentType.values()[1];
	static final PresenceType AUTONOMY_TYPE_TEST_VALUE1 = PresenceType.values()[0];
	static final PresenceType AUTONOMY_TYPE_TEST_VALUE2 = PresenceType.values()[1];
	static final String DOMAIN_TEST_VALUE1 = "test 1 domain";
	static final String DOMAIN_TEST_VALUE2 = "test 2 domain";
	static final String DOMAIN_TEST_VALUE3 = "test 3 domain";
	static final List<String> DOMAIN_TEST_LIST1 = Arrays.asList(new String[] { DOMAIN_TEST_VALUE1, DOMAIN_TEST_VALUE2 });
	static final List<String> DOMAIN_TEST_LIST2 = Arrays.asList(new String[] { DOMAIN_TEST_VALUE3 });
	static final String STANDARD_COMPLIANCE_TEST_VALUE1 = "test 1 standardCompliance";
	static final String STANDARD_COMPLIANCE_TEST_VALUE2 = "test 2 standardCompliance";
	static final String STANDARD_COMPLIANCE_TEST_VALUE3 = "test 3 standardCompliance";
	static final List<String> STANDARD_COMPLIANCE_TEST_LIST1 = Arrays.asList(new String[] { STANDARD_COMPLIANCE_TEST_VALUE1, STANDARD_COMPLIANCE_TEST_VALUE2 });
	static final List<String> STANDARD_COMPLIANCE_TEST_LIST2 = Arrays.asList(new String[] { STANDARD_COMPLIANCE_TEST_VALUE3 });
	static final String MODEL_DATA_PREPROCESSING_TEST_VALUE1 = "test 1 modelDataPreprocessing";
	static final String MODEL_DATA_PREPROCESSING_TEST_VALUE2 = "test 2 modelDataPreprocessing";
	static final String MODEL_DATA_PREPROCESSING_TEST_VALUE3 = "test 3 modelDataPreprocessing";
	static final List<String> MODEL_DATA_PREPROCESSING_TEST_LIST1 = Arrays.asList(new String[] { MODEL_DATA_PREPROCESSING_TEST_VALUE1, MODEL_DATA_PREPROCESSING_TEST_VALUE2 });
	static final List<String> MODEL_DATA_PREPROCESSING_TEST_LIST2 = Arrays.asList(new String[] { MODEL_DATA_PREPROCESSING_TEST_VALUE3 });
	static final String TYPE_OF_MODEL_TEST_VALUE1 = "test 1 typeOfModel";
	static final String TYPE_OF_MODEL_TEST_VALUE2 = "test 2 typeOfModel";
	static final String TYPE_OF_MODEL_TEST_VALUE3 = "test 3 typeOfModel";
	static final List<String> TYPE_OF_MODEL_TEST_LIST1 = Arrays.asList(new String[] { TYPE_OF_MODEL_TEST_VALUE1, TYPE_OF_MODEL_TEST_VALUE2 });
	static final List<String> TYPE_OF_MODEL_TEST_LIST2 = Arrays.asList(new String[] { TYPE_OF_MODEL_TEST_VALUE3 });
	static final String MODEL_EXPLAINABILITY_TEST_VALUE1 = "test 1 modelExplainability";
	static final String MODEL_EXPLAINABILITY_TEST_VALUE2 = "test 2 modelExplainability";
	static final String MODEL_EXPLAINABILITY_TEST_VALUE3 = "test 3 modelExplainability";
	static final List<String> MODEL_EXPLAINABILITY_TEST_LIST1 = Arrays.asList(new String[] { MODEL_EXPLAINABILITY_TEST_VALUE1, MODEL_EXPLAINABILITY_TEST_VALUE2 });
	static final List<String> MODEL_EXPLAINABILITY_TEST_LIST2 = Arrays.asList(new String[] { MODEL_EXPLAINABILITY_TEST_VALUE3 });
	
	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public static AIPackageBuilder builderForAIPackageTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		AIPackageBuilder retval = new AIPackageBuilder(modelStore, objectUri, copyManager)
				.setInformationAboutTraining(INFORMATION_ABOUT_TRAINING_TEST_VALUE)
				.setLimitation(LIMITATION_TEST_VALUE)
				.setEnergyConsumption(ENERGY_CONSUMPTION_TEST_VALUE)
				.setInformationAboutApplication(INFORMATION_ABOUT_APPLICATION_TEST_VALUE)
				.addDomain(DOMAIN_TEST_VALUE1)
				.addDomain(DOMAIN_TEST_VALUE2)
				.addStandardCompliance(STANDARD_COMPLIANCE_TEST_VALUE1)
				.addStandardCompliance(STANDARD_COMPLIANCE_TEST_VALUE2)
				.addModelDataPreprocessing(MODEL_DATA_PREPROCESSING_TEST_VALUE1)
				.addModelDataPreprocessing(MODEL_DATA_PREPROCESSING_TEST_VALUE2)
				.addTypeOfModel(TYPE_OF_MODEL_TEST_VALUE1)
				.addTypeOfModel(TYPE_OF_MODEL_TEST_VALUE2)
				.addModelExplainability(MODEL_EXPLAINABILITY_TEST_VALUE1)
				.addModelExplainability(MODEL_EXPLAINABILITY_TEST_VALUE2)
				.setSensitivePersonalInformation(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1)
				.setSafetyRiskAssessment(SAFETY_RISK_ASSESSMENT_TEST_VALUE1)
				.setAutonomyType(AUTONOMY_TYPE_TEST_VALUE1)
				//TODO: Add in test values
				/********************
				.addMetric(DictionaryEntry)
				.addHyperparameter(DictionaryEntry)
				.addMetricDecisionThreshold(DictionaryEntry)
				***************/
				;
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testAIPackage.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("AI.AIPackage", testAIPackage.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("AIPackage: "+TEST_OBJECT_URI, testAIPackage.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#Element(org.spdx.library.model.ai.AIPackage.AIPackageBuilder)}.
	 */
	public void testAIPackageAIPackageBuilder() throws InvalidSPDXAnalysisException {
		builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		AIPackage test2AIPackage = builderForAIPackageTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testAIPackage.equivalent(test2AIPackage));
		assertTrue(test2AIPackage.equivalent(testAIPackage));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setSensitivePersonalInformation}.
	 */
	public void testAIPackagesetSensitivePersonalInformation() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1), testAIPackage.getSensitivePersonalInformation());
		testAIPackage.setSensitivePersonalInformation(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2);
		assertEquals(Optional.of(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2), testAIPackage.getSensitivePersonalInformation());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setSafetyRiskAssessment}.
	 */
	public void testAIPackagesetSafetyRiskAssessment() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(SAFETY_RISK_ASSESSMENT_TEST_VALUE1), testAIPackage.getSafetyRiskAssessment());
		testAIPackage.setSafetyRiskAssessment(SAFETY_RISK_ASSESSMENT_TEST_VALUE2);
		assertEquals(Optional.of(SAFETY_RISK_ASSESSMENT_TEST_VALUE2), testAIPackage.getSafetyRiskAssessment());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setAutonomyType}.
	 */
	public void testAIPackagesetAutonomyType() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(AUTONOMY_TYPE_TEST_VALUE1), testAIPackage.getAutonomyType());
		testAIPackage.setAutonomyType(AUTONOMY_TYPE_TEST_VALUE2);
		assertEquals(Optional.of(AUTONOMY_TYPE_TEST_VALUE2), testAIPackage.getAutonomyType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setInformationAboutTraining}.
	 */
	public void testAIPackagesetInformationAboutTraining() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(INFORMATION_ABOUT_TRAINING_TEST_VALUE), testAIPackage.getInformationAboutTraining());
		testAIPackage.setInformationAboutTraining("new informationAboutTraining value");
		assertEquals(Optional.of("new informationAboutTraining value"), testAIPackage.getInformationAboutTraining());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setLimitation}.
	 */
	public void testAIPackagesetLimitation() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(LIMITATION_TEST_VALUE), testAIPackage.getLimitation());
		testAIPackage.setLimitation("new limitation value");
		assertEquals(Optional.of("new limitation value"), testAIPackage.getLimitation());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setEnergyConsumption}.
	 */
	public void testAIPackagesetEnergyConsumption() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(ENERGY_CONSUMPTION_TEST_VALUE), testAIPackage.getEnergyConsumption());
		testAIPackage.setEnergyConsumption("new energyConsumption value");
		assertEquals(Optional.of("new energyConsumption value"), testAIPackage.getEnergyConsumption());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setInformationAboutApplication}.
	 */
	public void testAIPackagesetInformationAboutApplication() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(INFORMATION_ABOUT_APPLICATION_TEST_VALUE), testAIPackage.getInformationAboutApplication());
		testAIPackage.setInformationAboutApplication("new informationAboutApplication value");
		assertEquals(Optional.of("new informationAboutApplication value"), testAIPackage.getInformationAboutApplication());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getMetric}.
	 */
	public void testAIPackagegetMetrics() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getMetrics())));
//		testAIPackage.getMetrics().clear();
//		testAIPackage.getMetrics().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getMetrics())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getHyperparameter}.
	 */
	public void testAIPackagegetHyperparameters() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getHyperparameters())));
//		testAIPackage.getHyperparameters().clear();
//		testAIPackage.getHyperparameters().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getHyperparameters())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getMetricDecisionThreshold}.
	 */
	public void testAIPackagegetMetricDecisionThresholds() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getMetricDecisionThresholds())));
//		testAIPackage.getMetricDecisionThresholds().clear();
//		testAIPackage.getMetricDecisionThresholds().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getMetricDecisionThresholds())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getDomains}.
	 */
	public void testAIPackagegetDomains() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(DOMAIN_TEST_LIST1, new ArrayList<>(testAIPackage.getDomains())));
		testAIPackage.getDomains().clear();
		testAIPackage.getDomains().addAll(DOMAIN_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(DOMAIN_TEST_LIST2, new ArrayList<>(testAIPackage.getDomains())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getStandardCompliances}.
	 */
	public void testAIPackagegetStandardCompliances() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(STANDARD_COMPLIANCE_TEST_LIST1, new ArrayList<>(testAIPackage.getStandardCompliances())));
		testAIPackage.getStandardCompliances().clear();
		testAIPackage.getStandardCompliances().addAll(STANDARD_COMPLIANCE_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(STANDARD_COMPLIANCE_TEST_LIST2, new ArrayList<>(testAIPackage.getStandardCompliances())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getModelDataPreprocessings}.
	 */
	public void testAIPackagegetModelDataPreprocessings() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(MODEL_DATA_PREPROCESSING_TEST_LIST1, new ArrayList<>(testAIPackage.getModelDataPreprocessings())));
		testAIPackage.getModelDataPreprocessings().clear();
		testAIPackage.getModelDataPreprocessings().addAll(MODEL_DATA_PREPROCESSING_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(MODEL_DATA_PREPROCESSING_TEST_LIST2, new ArrayList<>(testAIPackage.getModelDataPreprocessings())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getTypeOfModels}.
	 */
	public void testAIPackagegetTypeOfModels() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(TYPE_OF_MODEL_TEST_LIST1, new ArrayList<>(testAIPackage.getTypeOfModels())));
		testAIPackage.getTypeOfModels().clear();
		testAIPackage.getTypeOfModels().addAll(TYPE_OF_MODEL_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(TYPE_OF_MODEL_TEST_LIST2, new ArrayList<>(testAIPackage.getTypeOfModels())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getModelExplainabilitys}.
	 */
	public void testAIPackagegetModelExplainabilitys() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(MODEL_EXPLAINABILITY_TEST_LIST1, new ArrayList<>(testAIPackage.getModelExplainabilitys())));
		testAIPackage.getModelExplainabilitys().clear();
		testAIPackage.getModelExplainabilitys().addAll(MODEL_EXPLAINABILITY_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(MODEL_EXPLAINABILITY_TEST_LIST2, new ArrayList<>(testAIPackage.getModelExplainabilitys())));
	}
}