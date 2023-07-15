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
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.ai.AIPackage.AIPackageBuilder;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.UnitTestHelper;

import junit.framework.TestCase;

public class AIPackageTest extends TestCase {

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
	
	public static AIPackageBuilder builderForAIPackageTests(
					IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		AIPackageBuilder retval = new AIPackageBuilder(modelStore, objectUri, copyManager);
		//TODO: Add in test values
		/********************
		.setsensitivePersonalInformation(PresenceType.ENUM)
		.setsafetyRiskAssessment(SafetyRiskAssessmentType.ENUM)
		.setautonomyType(PresenceType.ENUM)
		.setinformationAboutTraining("A string")
		.setlimitation("A string")
		.setenergyConsumption("A string")
		.setinformationAboutApplication("A string")
		.getmetric.add(DictionaryEntry)
		.gethyperparameter.add(DictionaryEntry)
		.getmetricDecisionThreshold.add(DictionaryEntry)
		.getdomain.add("Test string")
		.getstandardCompliance.add("Test string")
		.getmodelDataPreprocessing.add("Test string")
		.gettypeOfModel.add("Test string")
		.getmodelExplainability.add("Test string")
		***************/
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
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
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
//		assertEquals(TEST_VALUE, testAIPackage.getSensitivePersonalInformation());
//		testAIPackage.setSensitivePersonalInformation(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testAIPackage.getSensitivePersonalInformation());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setSafetyRiskAssessment}.
	 */
	public void testAIPackagesetSafetyRiskAssessment() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testAIPackage.getSafetyRiskAssessment());
//		testAIPackage.setSafetyRiskAssessment(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testAIPackage.getSafetyRiskAssessment());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setAutonomyType}.
	 */
	public void testAIPackagesetAutonomyType() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testAIPackage.getAutonomyType());
//		testAIPackage.setAutonomyType(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testAIPackage.getAutonomyType());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setInformationAboutTraining}.
	 */
	public void testAIPackagesetInformationAboutTraining() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testAIPackage.getInformationAboutTraining());
//		testAIPackage.setInformationAboutTraining(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testAIPackage.getInformationAboutTraining());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setLimitation}.
	 */
	public void testAIPackagesetLimitation() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testAIPackage.getLimitation());
//		testAIPackage.setLimitation(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testAIPackage.getLimitation());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setEnergyConsumption}.
	 */
	public void testAIPackagesetEnergyConsumption() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testAIPackage.getEnergyConsumption());
//		testAIPackage.setEnergyConsumption(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testAIPackage.getEnergyConsumption());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#setInformationAboutApplication}.
	 */
	public void testAIPackagesetInformationAboutApplication() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertEquals(TEST_VALUE, testAIPackage.getInformationAboutApplication());
//		testAIPackage.setInformationAboutApplication(NEW_TEST_VALUE);
//		assertEquals(NEW_TEST_VALUE, testAIPackage.getInformationAboutApplication());
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getMetric}.
	 */
	public void testAIPackagesetMetric() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getMetric()));
//		testAIPackage.getMetric().clear();
//		testAIPackage.getMetric().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getMetric()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getHyperparameter}.
	 */
	public void testAIPackagesetHyperparameter() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getHyperparameter()));
//		testAIPackage.getHyperparameter().clear();
//		testAIPackage.getHyperparameter().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getHyperparameter()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getMetricDecisionThreshold}.
	 */
	public void testAIPackagesetMetricDecisionThreshold() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEquivalent(TEST_VALUE, new ArrayList<>(testAIPackage.getMetricDecisionThreshold()));
//		testAIPackage.getMetricDecisionThreshold().clear();
//		testAIPackage.getMetricDecisionThreshold().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEquivalent(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getMetricDecisionThreshold()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getDomain}.
	 */
	public void testAIPackagegetDomain() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testAIPackage.getDomain()));
//		testAIPackage.getDomain().clear();
//		testAIPackage.getDomain().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getDomain()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getStandardCompliance}.
	 */
	public void testAIPackagegetStandardCompliance() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testAIPackage.getStandardCompliance()));
//		testAIPackage.getStandardCompliance().clear();
//		testAIPackage.getStandardCompliance().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getStandardCompliance()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getModelDataPreprocessing}.
	 */
	public void testAIPackagegetModelDataPreprocessing() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testAIPackage.getModelDataPreprocessing()));
//		testAIPackage.getModelDataPreprocessing().clear();
//		testAIPackage.getModelDataPreprocessing().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getModelDataPreprocessing()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getTypeOfModel}.
	 */
	public void testAIPackagegetTypeOfModel() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testAIPackage.getTypeOfModel()));
//		testAIPackage.getTypeOfModel().clear();
//		testAIPackage.getTypeOfModel().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getTypeOfModel()));
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.ai.AIPackage#getModelExplainability}.
	 */
	public void testAIPackagegetModelExplainability() throws InvalidSPDXAnalysisException {
		AIPackage testAIPackage = builderForAIPackageTests(modelStore, TEST_OBJECT_URI, copyManager).build();
//		assertTrue(UnitTestHelper.isListsEqual(TEST_VALUE, new ArrayList<>(testAIPackage.getModelExplainability()));
//		testAIPackage.getModelExplainability().clear();
//		testAIPackage.getModelExplainability().addAll(NEW_TEST_VALUE);
//		assertTrue(UnitTestHelper.isListsEqual(NEW_TEST_VALUE, new ArrayList<>(testAIPackage.getModelExplainability()));
		fail("Not yet implemented");
	}

/*
*/

}