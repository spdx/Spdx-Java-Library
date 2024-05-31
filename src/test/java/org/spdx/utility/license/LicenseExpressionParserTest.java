/**
 * Copyright (c) 2019 Source Auditor Inc.
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
package org.spdx.utility.license;


import java.util.ArrayList;
import java.util.Arrays;

import org.spdx.core.DefaultModelStore;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.InvalidLicenseStringException;
import org.spdx.library.model.v3.ExternalCustomLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingConjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingCustomLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingDisjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicenseException;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingOrLaterOperator;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingWithAdditionOperator;
import org.spdx.library.model.v3.simplelicensing.SimpleLicensingAnyLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class LicenseExpressionParserTest extends TestCase {

	static final String[] STD_IDS = new String[] {"AFL-3.0", "CECILL-B", "EUPL-1.0", "Afmparse"};
	static final String[] NONSTD_IDS = new String[] {SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"1",
		SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"2", SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"3",
		SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"4"};
	static final String[] STD_TEXTS = new String[] {"Academic Free License (", "CONTRAT DE LICENCE DE LOGICIEL LIBRE CeCILL-B",
	"European Union Public Licence", "Afmparse License"};
	static final String[] NONSTD_TEXTS = new String[] {"text1", "text2", "text3", "text4"};
	static final String[] EXCEPTION_IDS = new String[] {"exception-1","exception-2", "exception-3", "exception-4"};
	static final String[] EXCEPTION_NAMES = new String[] {"exName-1", "exName-2", "exName-3", "exName-4"};
	static final String[] EXCEPTION_TEXTS = new String[] {"Ex text 1", "Ex text 2", "Ex text 3", "Ex text 4"};
	ExpandedLicensingCustomLicense[] NON_STD_LICENSES;
	ExpandedLicensingListedLicense[] STANDARD_LICENSES;
	ExpandedLicensingListedLicenseException[] LICENSE_EXCEPTIONS;
	static final String DOCID = SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM + "DOCID1";
	IModelStore modelStore;
	static final String TEST_DOCUMENT_URI = "https://test.doc.uri";
	ModelCopyManager copyManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		SpdxModelFactory.init();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		DefaultModelStore.initialize(new InMemSpdxStore(), TEST_DOCUMENT_URI, copyManager);
		NON_STD_LICENSES = new ExpandedLicensingCustomLicense[NONSTD_IDS.length];
		for (int i = 0; i < NONSTD_IDS.length; i++) {
			NON_STD_LICENSES[i] = new ExpandedLicensingCustomLicense(modelStore, TEST_DOCUMENT_URI + "#" + 
					NONSTD_IDS[i], copyManager, true);
			NON_STD_LICENSES[i].setSimpleLicensingLicenseText(NONSTD_TEXTS[i]);
		}

		STANDARD_LICENSES = new ExpandedLicensingListedLicense[STD_IDS.length];
		for (int i = 0; i < STD_IDS.length; i++) {
			STANDARD_LICENSES[i] = new ExpandedLicensingListedLicense(modelStore, 
					SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + STD_IDS[i], copyManager, true);
			STANDARD_LICENSES[i].setName("Name "+String.valueOf(i))
				.setSimpleLicensingLicenseText(STD_TEXTS[i])
				.getExpandedLicensingSeeAlsos().addAll(Arrays.asList(new String[] {"URL "+String.valueOf(i)}));
			STANDARD_LICENSES[i].setDescription("Notes "+String.valueOf(i))
			.setExpandedLicensingStandardLicenseHeader("LicHeader "+String.valueOf(i))
			.setExpandedLicensingStandardLicenseTemplate("Template "+String.valueOf(i));
		}
		LICENSE_EXCEPTIONS = new ExpandedLicensingListedLicenseException[EXCEPTION_IDS.length];
		for (int i = 0; i < EXCEPTION_IDS.length; i++) {
			LICENSE_EXCEPTIONS[i] = new ExpandedLicensingListedLicenseException(
					modelStore, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + EXCEPTION_IDS[i], copyManager, true);
			LICENSE_EXCEPTIONS[i].setName(EXCEPTION_NAMES[i])
			.setExpandedLicensingAdditionText(EXCEPTION_TEXTS[i]);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		DefaultModelStore.initialize(new InMemSpdxStore(), "https://docnamespace", new ModelCopyManager());
	}
	
	public void testSingleStdLicense() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0];
		SimpleLicensingAnyLicenseInfo expected = STANDARD_LICENSES[0];
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testSingleExtractedLicense() throws InvalidSPDXAnalysisException {
		String parseString = NONSTD_IDS[0];
		SimpleLicensingAnyLicenseInfo expected = NON_STD_LICENSES[0];
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}
	
	public void testUninitializedExtractedLicense() throws InvalidSPDXAnalysisException {
		String parseString = "LicenseRef-3242";
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertEquals(DefaultModelStore.getDefaultDocumentUri() + "#" + parseString, result.getObjectUri());
	}


	public void testOrLater() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0]+"+";
		ExpandedLicensingOrLaterOperator expected = new ExpandedLicensingOrLaterOperator(DefaultModelStore.getDefaultDocumentUri());
		expected.setExpandedLicensingSubjectLicense(STANDARD_LICENSES[0]);
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testWithException() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0]+" WITH " + EXCEPTION_IDS[0];
		ExpandedLicensingWithAdditionOperator expected = new ExpandedLicensingWithAdditionOperator();
		expected.setExpandedLicensingSubjectAddition(LICENSE_EXCEPTIONS[0]);
		expected.setExpandedLicensingSubjectExtendableLicense(STANDARD_LICENSES[0]);
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testSimpleAnd() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0] + " AND " + NONSTD_IDS[0];
		ExpandedLicensingConjunctiveLicenseSet expected = new ExpandedLicensingConjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().add(STANDARD_LICENSES[0]);
		expected.getExpandedLicensingMembers().add(NON_STD_LICENSES[0]);
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testSimpleOr() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0] + " OR " + NONSTD_IDS[0];
		ExpandedLicensingDisjunctiveLicenseSet expected = new ExpandedLicensingDisjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().add(STANDARD_LICENSES[0]);
		expected.getExpandedLicensingMembers().add(NON_STD_LICENSES[0]);
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testLargerAnd() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " AND " + NONSTD_IDS[1] + " AND " +
					STD_IDS[2] + " AND " + STD_IDS[3];
		ExpandedLicensingConjunctiveLicenseSet expected = new ExpandedLicensingConjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testLargerOr() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " OR " + NONSTD_IDS[1] + " OR " +
					STD_IDS[2] + " OR " + STD_IDS[3];
		ExpandedLicensingDisjunctiveLicenseSet expected = new ExpandedLicensingDisjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testOuterParens() throws InvalidSPDXAnalysisException {
		String parseString = "(" + STD_IDS[1] + " OR " + NONSTD_IDS[1] + " OR " +
					STD_IDS[2] + " OR " + STD_IDS[3] + ")";
		ExpandedLicensingDisjunctiveLicenseSet expected = new ExpandedLicensingDisjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testInnerParens() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " AND " + NONSTD_IDS[1] + " AND " +
				"(" + STD_IDS[2] + " OR " + STD_IDS[3] + ")";
		ExpandedLicensingDisjunctiveLicenseSet dls = new ExpandedLicensingDisjunctiveLicenseSet();
		dls.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		ExpandedLicensingConjunctiveLicenseSet expected = new ExpandedLicensingConjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[1] ,
				NON_STD_LICENSES[1], dls})));
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testAndOrPrecedence() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " OR " + NONSTD_IDS[1] + " AND " +
				STD_IDS[2] + " OR " + STD_IDS[3];
		ExpandedLicensingConjunctiveLicenseSet cls = new ExpandedLicensingConjunctiveLicenseSet();
		cls.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {NON_STD_LICENSES[1], STANDARD_LICENSES[2]})));
		
		ExpandedLicensingDisjunctiveLicenseSet expected = new ExpandedLicensingDisjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[1] ,
				cls, STANDARD_LICENSES[3]})));
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}
	
	public void testExternalCustomLicense() throws InvalidSPDXAnalysisException {
		String externalExtractedId = DOCID + ":" + SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + "232";
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(externalExtractedId, 
				modelStore, TEST_DOCUMENT_URI, null);
		assertTrue(result instanceof ExternalCustomLicense);
		assertEquals(externalExtractedId, ((ExternalCustomLicense)result).getObjectUri());
	}
	
	public void testExternalLicenseAddition() throws InvalidSPDXAnalysisException {
		fail("Not implemented");
	}

    public void regressionMitWith() throws InvalidSPDXAnalysisException, InvalidLicenseStringException {
    	SimpleLicensingAnyLicenseInfo result = LicenseInfoFactory.parseSPDXLicenseString("MIT WITH Autoconf-exception-2.0");
        assertEquals("MIT WITH Autoconf-exception-2.0",result.toString());
    }
}
