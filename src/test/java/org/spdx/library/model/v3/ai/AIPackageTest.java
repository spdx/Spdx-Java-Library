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
 
package org.spdx.library.model.v3.ai;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.ModelRegistry;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.SpdxModelInfoV3_0;
import org.spdx.library.model.v3.ai.AIAIPackage.AIAIPackageBuilder;
import org.spdx.library.model.v3.core.Agent.AgentBuilder;
import org.spdx.library.model.v3.core.CreationInfo;
import org.spdx.library.model.v3.core.PresenceType;
import org.spdx.library.model.v3.core.ProfileIdentifierType;
import org.spdx.library.model.v3.core.CreationInfo.CreationInfoBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class AIPackageTest extends TestCase {

	static final String TEST_OBJECT_URI = "https://test.uri/testuri";
	

	IModelStore modelStore;
	ModelCopyManager copyManager;

	static final String LIMITATION_TEST_VALUE = "test limitation";
	static final String INFORMATION_ABOUT_APPLICATION_TEST_VALUE = "test informationAboutApplication";
	static final String INFORMATION_ABOUT_TRAINING_TEST_VALUE = "test informationAboutTraining";
	static final String ENERGY_CONSUMPTION_TEST_VALUE = "test energyConsumption";
	static final AISafetyRiskAssessmentType SAFETY_RISK_ASSESSMENT_TEST_VALUE1 = AISafetyRiskAssessmentType.values()[0];
	static final AISafetyRiskAssessmentType SAFETY_RISK_ASSESSMENT_TEST_VALUE2 = AISafetyRiskAssessmentType.values()[1];
	static final PresenceType AUTONOMY_TYPE_TEST_VALUE1 = PresenceType.values()[0];
	static final PresenceType AUTONOMY_TYPE_TEST_VALUE2 = PresenceType.values()[1];
	static final PresenceType SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1 = PresenceType.values()[0];
	static final PresenceType SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2 = PresenceType.values()[1];
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
	static final String MODEL_EXPLAINABILITY_TEST_VALUE1 = "test 1 modelExplainability";
	static final String MODEL_EXPLAINABILITY_TEST_VALUE2 = "test 2 modelExplainability";
	static final String MODEL_EXPLAINABILITY_TEST_VALUE3 = "test 3 modelExplainability";
	static final List<String> MODEL_EXPLAINABILITY_TEST_LIST1 = Arrays.asList(new String[] { MODEL_EXPLAINABILITY_TEST_VALUE1, MODEL_EXPLAINABILITY_TEST_VALUE2 });
	static final List<String> MODEL_EXPLAINABILITY_TEST_LIST2 = Arrays.asList(new String[] { MODEL_EXPLAINABILITY_TEST_VALUE3 });
	static final String TYPE_OF_MODEL_TEST_VALUE1 = "test 1 typeOfModel";
	static final String TYPE_OF_MODEL_TEST_VALUE2 = "test 2 typeOfModel";
	static final String TYPE_OF_MODEL_TEST_VALUE3 = "test 3 typeOfModel";
	static final List<String> TYPE_OF_MODEL_TEST_LIST1 = Arrays.asList(new String[] { TYPE_OF_MODEL_TEST_VALUE1, TYPE_OF_MODEL_TEST_VALUE2 });
	static final List<String> TYPE_OF_MODEL_TEST_LIST2 = Arrays.asList(new String[] { TYPE_OF_MODEL_TEST_VALUE3 });
	static final String MODEL_DATA_PREPROCESSING_TEST_VALUE1 = "test 1 modelDataPreprocessing";
	static final String MODEL_DATA_PREPROCESSING_TEST_VALUE2 = "test 2 modelDataPreprocessing";
	static final String MODEL_DATA_PREPROCESSING_TEST_VALUE3 = "test 3 modelDataPreprocessing";
	static final List<String> MODEL_DATA_PREPROCESSING_TEST_LIST1 = Arrays.asList(new String[] { MODEL_DATA_PREPROCESSING_TEST_VALUE1, MODEL_DATA_PREPROCESSING_TEST_VALUE2 });
	static final List<String> MODEL_DATA_PREPROCESSING_TEST_LIST2 = Arrays.asList(new String[] { MODEL_DATA_PREPROCESSING_TEST_VALUE3 });
	
	private CreationInfo creationInfo;
	
	protected void setUp() throws Exception {
		super.setUp();
		ModelRegistry.getModelRegistry().registerModel(new SpdxModelInfoV3_0());
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		creationInfo = new CreationInfoBuilder(modelStore, modelStore.getNextId(IdType.Anonymous), copyManager)
							.setCreated("2010-01-29T18:30:22Z")
							.setSpecVersion("3.0.0").build();
		AgentBuilder agentBuilder = new AgentBuilder(modelStore, "https://unique/id" + modelStore.getNextId(IdType.SpdxId), copyManager);
		agentBuilder.setCreationInfo(creationInfo);
		agentBuilder.setName("Creator Name");
		creationInfo.getCreatedBys().add(agentBuilder.build());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public AIAIPackageBuilder builderForAIPackageTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		AIAIPackageBuilder retval = new AIAIPackageBuilder(modelStore, objectUri, copyManager)
				.setAILimitation(LIMITATION_TEST_VALUE)
				.setAIInformationAboutApplication(INFORMATION_ABOUT_APPLICATION_TEST_VALUE)
				.setAIInformationAboutTraining(INFORMATION_ABOUT_TRAINING_TEST_VALUE)
				.addAIDomain(DOMAIN_TEST_VALUE1)
				.addAIDomain(DOMAIN_TEST_VALUE2)
				.addAIStandardCompliance(STANDARD_COMPLIANCE_TEST_VALUE1)
				.addAIStandardCompliance(STANDARD_COMPLIANCE_TEST_VALUE2)
				.addAIModelExplainability(MODEL_EXPLAINABILITY_TEST_VALUE1)
				.addAIModelExplainability(MODEL_EXPLAINABILITY_TEST_VALUE2)
				.addAITypeOfModel(TYPE_OF_MODEL_TEST_VALUE1)
				.addAITypeOfModel(TYPE_OF_MODEL_TEST_VALUE2)
				.addAIModelDataPreprocessing(MODEL_DATA_PREPROCESSING_TEST_VALUE1)
				.addAIModelDataPreprocessing(MODEL_DATA_PREPROCESSING_TEST_VALUE2)
				.setAISafetyRiskAssessment(SAFETY_RISK_ASSESSMENT_TEST_VALUE1)
				.setAIAutonomyType(AUTONOMY_TYPE_TEST_VALUE1)
				//TODO: Add in test values
				/********************
				.addMetric(DictionaryEntry)
				.addMetricDecisionThreshold(DictionaryEntry)
				.addHyperparameter(DictionaryEntry)
				.setEnergyConsumption(ENERGY_CONSUMPTION_TEST_VALUE)
				.setSensitivePersonalInformation(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1)
				***************/
				;
		retval.setCreationInfo(creationInfo);
		return retval;
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#verify()}.
	 * @throws InvalidSPDXAnalysisException on errors
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		List<String> result = testAIPackage.verify();
		assertTrue(result.isEmpty());
		// TODO - add negative tests
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getType()}.
	 */
	public void testGetType() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("AI.AIAIPackage", testAIPackage.getType());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#toString()}.
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals("AIAIPackage: "+TEST_OBJECT_URI, testAIPackage.toString());
	}

	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#Element(org.spdx.library.model.v3.ai.AIAIPackage.AIAIPackageBuilder)}.
	 */
	public void testAIPackageAIPackageBuilder() throws InvalidSPDXAnalysisException {
		builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		AIAIPackage test2AIPackage = builderForAIPackageTests(new InMemSpdxStore(), "https://testObject2", copyManager).build();
		assertTrue(testAIPackage.equivalent(test2AIPackage));
		assertTrue(test2AIPackage.equivalent(testAIPackage));
		// TODO change some parameters for negative tests
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#setSafetyRiskAssessment}.
	 */
	public void testAIPackagesetSafetyRiskAssessment() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(SAFETY_RISK_ASSESSMENT_TEST_VALUE1), testAIPackage.getAISafetyRiskAssessment());
		testAIPackage.setAISafetyRiskAssessment(SAFETY_RISK_ASSESSMENT_TEST_VALUE2);
		assertEquals(Optional.of(SAFETY_RISK_ASSESSMENT_TEST_VALUE2), testAIPackage.getAISafetyRiskAssessment());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#setAutonomyType}.
	 */
	public void testAIPackagesetAutonomyType() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(AUTONOMY_TYPE_TEST_VALUE1), testAIPackage.getAIAutonomyType());
		testAIPackage.setAIAutonomyType(AUTONOMY_TYPE_TEST_VALUE2);
		assertEquals(Optional.of(AUTONOMY_TYPE_TEST_VALUE2), testAIPackage.getAIAutonomyType());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#setSensitivePersonalInformation}.
	 */
	public void testAIPackagesetSensitivePersonalInformation() throws InvalidSPDXAnalysisException {
		fail("unimplemented");
//		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(Optional.of(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE1), testAIPackage.getAIUseSensitivePersonalInformation());
//		testAIPackage.setAIUseSensitivePersonalInformation(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2);
//		assertEquals(Optional.of(SENSITIVE_PERSONAL_INFORMATION_TEST_VALUE2), testAIPackage.getAIUseSensitivePersonalInformation());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#setLimitation}.
	 */
	public void testAIPackagesetLimitation() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(LIMITATION_TEST_VALUE), testAIPackage.getAILimitation());
		testAIPackage.setAILimitation("new limitation value");
		assertEquals(Optional.of("new limitation value"), testAIPackage.getAILimitation());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#setInformationAboutApplication}.
	 */
	public void testAIPackagesetInformationAboutApplication() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(INFORMATION_ABOUT_APPLICATION_TEST_VALUE), testAIPackage.getAIInformationAboutApplication());
		testAIPackage.setAIInformationAboutApplication("new informationAboutApplication value");
		assertEquals(Optional.of("new informationAboutApplication value"), testAIPackage.getAIInformationAboutApplication());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#setInformationAboutTraining}.
	 */
	public void testAIPackagesetInformationAboutTraining() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertEquals(Optional.of(INFORMATION_ABOUT_TRAINING_TEST_VALUE), testAIPackage.getAIInformationAboutTraining());
		testAIPackage.setAIInformationAboutTraining("new informationAboutTraining value");
		assertEquals(Optional.of("new informationAboutTraining value"), testAIPackage.getAIInformationAboutTraining());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#setEnergyConsumption}.
	 */
	public void testAIPackagesetEnergyConsumption() throws InvalidSPDXAnalysisException {
//		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(Optional.of(ENERGY_CONSUMPTION_TEST_VALUE), testAIPackage.getAIEnergyConsumption());
//		testAIPackage.setAIEnergyConsumption("new energyConsumption value");
//		assertEquals(Optional.of("new energyConsumption value"), testAIPackage.getAIEnergyConsumption());
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getMetric}.
	 */
	public void testAIPackagegetMetrics() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getMetrics())));
//		testAIPackage.getMetrics().clear();
//		testAIPackage.getMetrics().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getMetrics())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getMetricDecisionThreshold}.
	 */
	public void testAIPackagegetMetricDecisionThresholds() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getMetricDecisionThresholds())));
//		testAIPackage.getMetricDecisionThresholds().clear();
//		testAIPackage.getMetricDecisionThresholds().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getMetricDecisionThresholds())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getHyperparameter}.
	 */
	public void testAIPackagegetHyperparameters() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getHyperparameters())));
//		testAIPackage.getHyperparameters().clear();
//		testAIPackage.getHyperparameters().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getHyperparameters())));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getDomains}.
	 */
	public void testAIPackagegetDomains() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(DOMAIN_TEST_LIST1, new ArrayList<>(testAIPackage.getAIDomains())));
		testAIPackage.getAIDomains().clear();
		testAIPackage.getAIDomains().addAll(DOMAIN_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(DOMAIN_TEST_LIST2, new ArrayList<>(testAIPackage.getAIDomains())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getStandardCompliances}.
	 */
	public void testAIPackagegetStandardCompliances() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(STANDARD_COMPLIANCE_TEST_LIST1, new ArrayList<>(testAIPackage.getAIStandardCompliances())));
		testAIPackage.getAIStandardCompliances().clear();
		testAIPackage.getAIStandardCompliances().addAll(STANDARD_COMPLIANCE_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(STANDARD_COMPLIANCE_TEST_LIST2, new ArrayList<>(testAIPackage.getAIStandardCompliances())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getModelExplainabilitys}.
	 */
	public void testAIPackagegetModelExplainabilitys() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(MODEL_EXPLAINABILITY_TEST_LIST1, new ArrayList<>(testAIPackage.getAIModelExplainabilitys())));
		testAIPackage.getAIModelExplainabilitys().clear();
		testAIPackage.getAIModelExplainabilitys().addAll(MODEL_EXPLAINABILITY_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(MODEL_EXPLAINABILITY_TEST_LIST2, new ArrayList<>(testAIPackage.getAIModelExplainabilitys())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getTypeOfModels}.
	 */
	public void testAIPackagegetTypeOfModels() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(TYPE_OF_MODEL_TEST_LIST1, new ArrayList<>(testAIPackage.getAITypeOfModels())));
		testAIPackage.getAITypeOfModels().clear();
		testAIPackage.getAITypeOfModels().addAll(TYPE_OF_MODEL_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(TYPE_OF_MODEL_TEST_LIST2, new ArrayList<>(testAIPackage.getAITypeOfModels())));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.v3.ai.AIPackage#getModelDataPreprocessings}.
	 */
	public void testAIPackagegetModelDataPreprocessings() throws InvalidSPDXAnalysisException {
		AIAIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
		assertTrue(UnitTestHelper.isListsEqual(MODEL_DATA_PREPROCESSING_TEST_LIST1, new ArrayList<>(testAIPackage.getAIModelDataPreprocessings())));
		testAIPackage.getAIModelDataPreprocessings().clear();
		testAIPackage.getAIModelDataPreprocessings().addAll(MODEL_DATA_PREPROCESSING_TEST_LIST2);
		assertTrue(UnitTestHelper.isListsEqual(MODEL_DATA_PREPROCESSING_TEST_LIST2, new ArrayList<>(testAIPackage.getAIModelDataPreprocessings())));
	}
}