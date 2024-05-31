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
import org.spdx.library.model.v2.Checksum;
import org.spdx.library.model.v2.GenericModelObject;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.v2.license.AnyLicenseInfo;
import org.spdx.library.model.v2.license.ConjunctiveLicenseSet;
import org.spdx.library.model.v2.license.DisjunctiveLicenseSet;
import org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;
import org.spdx.library.model.v2.license.InvalidLicenseStringException;
import org.spdx.library.model.v2.license.LicenseException;
import org.spdx.library.model.v2.license.ListedLicenseException;
import org.spdx.library.model.v2.license.OrLaterOperator;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v2.license.WithExceptionOperator;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class LicenseExpressionParserTestV2 extends TestCase {

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
	ExtractedLicenseInfo[] NON_STD_LICENSES;
	SpdxListedLicense[] STANDARD_LICENSES;
	LicenseException[] LICENSE_EXCEPTIONS;
	static final String DOCID = SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM + "DOCID1";
	IModelStore modelStore;
	static final String TEST_DOCUMENT_URI = "https://test.doc.uri";
	GenericModelObject gmo;
	ModelCopyManager copyManager;
	
	protected void setUp() throws Exception {
		SpdxModelFactory.init();
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		DefaultModelStore.initialize(new InMemSpdxStore(), "https://docnamespace", copyManager);
		gmo = new GenericModelObject();
		NON_STD_LICENSES = new ExtractedLicenseInfo[NONSTD_IDS.length];
		for (int i = 0; i < NONSTD_IDS.length; i++) {
			NON_STD_LICENSES[i] = new ExtractedLicenseInfo(NONSTD_IDS[i], NONSTD_TEXTS[i]);
		}

		STANDARD_LICENSES = new SpdxListedLicense[STD_IDS.length];
		for (int i = 0; i < STD_IDS.length; i++) {
			STANDARD_LICENSES[i] = new SpdxListedLicense("Name "+String.valueOf(i),
					STD_IDS[i], STD_TEXTS[i], new ArrayList<String>(Arrays.asList(new String[] {"URL "+String.valueOf(i)})), "Notes "+String.valueOf(i),
					"LicHeader "+String.valueOf(i), "Template "+String.valueOf(i), true, false, "", false, "");
		}
		LICENSE_EXCEPTIONS = new ListedLicenseException[EXCEPTION_IDS.length];
		for (int i = 0; i < EXCEPTION_IDS.length; i++) {
			LICENSE_EXCEPTIONS[i] = new ListedLicenseException(EXCEPTION_IDS[i], EXCEPTION_NAMES[i], EXCEPTION_TEXTS[i]);
		}
		
		SpdxDocument doc = new SpdxDocument(modelStore, TEST_DOCUMENT_URI, null, true);
		Checksum checksum = doc.createChecksum(ChecksumAlgorithm.SHA1, "A94A8FE5CCB19BA61C4C0873D391E987982FBBD3");
		doc.createExternalDocumentRef(DOCID, "http://external.doc", checksum);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		DefaultModelStore.initialize(new InMemSpdxStore(), "https://docnamespace", new ModelCopyManager());
	}
	
	public void testSingleStdLicense() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0];
		AnyLicenseInfo expected = STANDARD_LICENSES[0];
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testSingleExtractedLicense() throws InvalidSPDXAnalysisException {
		String parseString = NONSTD_IDS[0];
		AnyLicenseInfo expected = NON_STD_LICENSES[0];
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}
	
	public void testUninitializedExtractedLicense() throws InvalidSPDXAnalysisException {
		String parseString = "LicenseRef-3242";
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertEquals(parseString, result.getId());
	}


	public void testOrLater() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0]+"+";
		AnyLicenseInfo expected = new OrLaterOperator(STANDARD_LICENSES[0]);
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testWithException() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0]+" WITH " + EXCEPTION_IDS[0];
		AnyLicenseInfo expected = new WithExceptionOperator(STANDARD_LICENSES[0], LICENSE_EXCEPTIONS[0]);
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testSimpleAnd() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0] + " AND " + NONSTD_IDS[0];
		AnyLicenseInfo expected = gmo.createConjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[0], NON_STD_LICENSES[0]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testSimpleOr() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0] + " OR " + NONSTD_IDS[0];
		AnyLicenseInfo expected = gmo.createDisjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[0], NON_STD_LICENSES[0]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testLargerAnd() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " AND " + NONSTD_IDS[1] + " AND " +
					STD_IDS[2] + " AND " + STD_IDS[3];
		AnyLicenseInfo expected = gmo.createConjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testLargerOr() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " OR " + NONSTD_IDS[1] + " OR " +
					STD_IDS[2] + " OR " + STD_IDS[3];
		AnyLicenseInfo expected = gmo.createDisjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testOuterParens() throws InvalidSPDXAnalysisException {
		String parseString = "(" + STD_IDS[1] + " OR " + NONSTD_IDS[1] + " OR " +
					STD_IDS[2] + " OR " + STD_IDS[3] + ")";
		AnyLicenseInfo expected = gmo.createDisjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testInnerParens() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " AND " + NONSTD_IDS[1] + " AND " +
				"(" + STD_IDS[2] + " OR " + STD_IDS[3] + ")";
		DisjunctiveLicenseSet dls = gmo.createDisjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		AnyLicenseInfo expected = gmo.createConjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1] ,
				NON_STD_LICENSES[1], dls})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, DefaultModelStore.getDefaultModelStore(), 
				DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}


	public void testAndOrPrecedence() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " OR " + NONSTD_IDS[1] + " AND " +
				STD_IDS[2] + " OR " + STD_IDS[3];
		ConjunctiveLicenseSet cls = gmo.createConjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {NON_STD_LICENSES[1], STANDARD_LICENSES[2]})));
		AnyLicenseInfo expected = gmo.createDisjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1] ,
				cls, STANDARD_LICENSES[3]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(parseString, 
				DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), DefaultModelStore.getDefaultCopyManager());
		assertTrue(expected.equals(result));
	}
	
	public void testExternalLicenseRef() throws InvalidSPDXAnalysisException {
		String externalExtractedId = DOCID + ":" + SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + "232";
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpressionCompatV2(externalExtractedId, 
				modelStore, TEST_DOCUMENT_URI, null);
		assertTrue(result instanceof ExternalExtractedLicenseInfo);
		assertEquals(externalExtractedId, ((ExternalExtractedLicenseInfo)result).getId());
	}

    public void regressionMitWith() throws InvalidSPDXAnalysisException, InvalidLicenseStringException {
        AnyLicenseInfo result = LicenseInfoFactory.parseSPDXLicenseV2String("MIT WITH Autoconf-exception-2.0");
        assertEquals("MIT WITH Autoconf-exception-2.0",result.toString());
    }
}
