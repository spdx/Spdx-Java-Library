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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spdx.core.DefaultModelStore;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.InvalidLicenseStringException;
import org.spdx.library.model.v3.ExternalCustomLicense;
import org.spdx.library.model.v3.core.NamespaceMap;
import org.spdx.library.model.v3.core.SpdxDocument;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingConjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingCustomLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingCustomLicenseAddition;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingDisjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicenseException;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingOrLaterOperator;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingWithAdditionOperator;
import org.spdx.library.model.v3.simplelicensing.SimpleLicensingAnyLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
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
	static final String[] NONSTD_ADDITION_IDS = new String[] {"exception-1","exception-2", "exception-3", "exception-4"};
	static final String[] STD_EXCEPTION_IDS = new String[] {"389-exception", "Autoconf-exception-2.0"};
	static final String[] NON_STD_ADDITION_NAMES = new String[] {"exName-1", "exName-2", "exName-3", "exName-4"};
	static final String[] NON_STD_ADDITION_TEXTS = new String[] {"Ex text 1", "Ex text 2", "Ex text 3", "Ex text 4"};
	static final String[] STD_EXCEPTION_NAMES = new String[] {"389 exception", "Autoconf Exception"};
	static final String[] STD_EXCEPTION_TEXTS = new String[] {"389 exception text", "Autoconf Exception text"};
	
	static final String DOC_URI = "https://spdx-doc/thatisunique";
	static final String[] PREFIXES = new String[] {"prefix1", "prefix2", "prefix3"};
	static final String[] NAMESPACES = new String[] {"https://uri1", "https://uri2", "urn:uri3"};
	static final Map<String, String> NAMESPACE_MAP = new HashMap<>();
	static {
		NAMESPACE_MAP.put(PREFIXES[0], NAMESPACES[0]);
		NAMESPACE_MAP.put(PREFIXES[1], NAMESPACES[1]);
		NAMESPACE_MAP.put(PREFIXES[2], NAMESPACES[2]);
	}
	
	static final String[] EXTERNAL_CUSTOM_LICENSE_IDS = new String[] {"lic-ex1", "lic-ex2", "lic-ex3", "lic-ex4"};
	static final String[] EXTERNAL_CUSTOM_LICENSE_TOKENS = new String[] {
			PREFIXES[0] + ":" + EXTERNAL_CUSTOM_LICENSE_IDS[0],
			PREFIXES[1] + ":" + EXTERNAL_CUSTOM_LICENSE_IDS[1],
			PREFIXES[2] + ":" + EXTERNAL_CUSTOM_LICENSE_IDS[2],
			PREFIXES[2] + ":" + EXTERNAL_CUSTOM_LICENSE_IDS[3]
	};
	static final String[] EXTERNAL_CUSTOM_LICENSE_URIS = new String[] {
			NAMESPACES[0] + EXTERNAL_CUSTOM_LICENSE_IDS[0],
			NAMESPACES[1] + EXTERNAL_CUSTOM_LICENSE_IDS[1],
			NAMESPACES[2] + EXTERNAL_CUSTOM_LICENSE_IDS[2],
			NAMESPACES[2] + EXTERNAL_CUSTOM_LICENSE_IDS[3]
	};
	
	static final String[] EXTERNAL_CUSTOM_ADDITIONS_IDS = new String[] {"add-ex1", "add-ex2", "add-ex3", "add-ex4"};
	static final String[] EXTERNAL_CUSTOM_ADDITION_TOKENS = new String[] {
			PREFIXES[0] + ":" + EXTERNAL_CUSTOM_ADDITIONS_IDS[0],
			PREFIXES[1] + ":" + EXTERNAL_CUSTOM_ADDITIONS_IDS[1],
			PREFIXES[2] + ":" + EXTERNAL_CUSTOM_ADDITIONS_IDS[2],
			PREFIXES[2] + ":" + EXTERNAL_CUSTOM_ADDITIONS_IDS[3]
	};
	static final String[] EXTERNAL_CUSTOM_ADDITION_URIS = new String[] {
			NAMESPACES[0] + EXTERNAL_CUSTOM_ADDITIONS_IDS[0],
			NAMESPACES[1] + EXTERNAL_CUSTOM_ADDITIONS_IDS[1],
			NAMESPACES[2] + EXTERNAL_CUSTOM_ADDITIONS_IDS[2],
			NAMESPACES[2] + EXTERNAL_CUSTOM_ADDITIONS_IDS[3]
	};
	
	ExpandedLicensingCustomLicense[] NON_STD_LICENSES;
	ExpandedLicensingListedLicense[] STANDARD_LICENSES;
	ExpandedLicensingListedLicenseException[] STD_LICENSE_EXCEPTIONS;
	ExpandedLicensingCustomLicenseAddition[] NON_STD_LICENSE_ADDITIONS;

	IModelStore modelStore;
	static final String TEST_DOCUMENT_URI = "https://test.doc.uri";
	static final String DEFAULT_PREFIX = TEST_DOCUMENT_URI + "#";
	ModelCopyManager copyManager;
	SpdxDocument doc;
	
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
		STD_LICENSE_EXCEPTIONS = new ExpandedLicensingListedLicenseException[STD_EXCEPTION_IDS.length];
		for (int i = 0; i < STD_EXCEPTION_IDS.length; i++) {
			STD_LICENSE_EXCEPTIONS[i] = new ExpandedLicensingListedLicenseException(
					modelStore, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + STD_EXCEPTION_IDS[i], copyManager, true);
			STD_LICENSE_EXCEPTIONS[i].setName(STD_EXCEPTION_NAMES[i])
					.setExpandedLicensingAdditionText(STD_EXCEPTION_TEXTS[i]);
		}
		NON_STD_LICENSE_ADDITIONS = new ExpandedLicensingCustomLicenseAddition[NONSTD_ADDITION_IDS.length];
		for (int i = 0; i < NONSTD_ADDITION_IDS.length; i++) {
			NON_STD_LICENSE_ADDITIONS[i] = new ExpandedLicensingCustomLicenseAddition(
					modelStore, TEST_DOCUMENT_URI + "#" + NONSTD_ADDITION_IDS[i], copyManager, true);
			NON_STD_LICENSE_ADDITIONS[i].setName(NON_STD_ADDITION_NAMES[i])
					.setExpandedLicensingAdditionText(NON_STD_ADDITION_TEXTS[i]);
		}
		doc = new SpdxDocument(modelStore, DOC_URI, copyManager, true);
		Collection<NamespaceMap> namespaceMap = doc.getNamespaceMaps();
		for (Entry<String, String> entry:NAMESPACE_MAP.entrySet()) {
			namespaceMap.add(doc.createNamespaceMap(modelStore.getNextId(IdType.Anonymous))
								.setPrefix(entry.getKey())
								.setNamespace(entry.getValue())
								.build());
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
				modelStore, DEFAULT_PREFIX,
				copyManager, doc);
		assertTrue(expected.equals(result));
	}


	public void testSingleExtractedLicense() throws InvalidSPDXAnalysisException {
		String parseString = NONSTD_IDS[0];
		SimpleLicensingAnyLicenseInfo expected = NON_STD_LICENSES[0];
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
	}
	
	public void testUninitializedExtractedLicense() throws InvalidSPDXAnalysisException {
		String parseString = "LicenseRef-3242";
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, doc);
		assertEquals(DEFAULT_PREFIX + parseString, result.getObjectUri());
	}


	public void testOrLater() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0]+"+";
		ExpandedLicensingOrLaterOperator expected = new ExpandedLicensingOrLaterOperator(DefaultModelStore.getDefaultDocumentUri());
		expected.setExpandedLicensingSubjectLicense(STANDARD_LICENSES[0]);
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
	}


	public void testWithException() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0]+" WITH " + NONSTD_ADDITION_IDS[0];
		ExpandedLicensingWithAdditionOperator expected = new ExpandedLicensingWithAdditionOperator();
		expected.setExpandedLicensingSubjectAddition(NON_STD_LICENSE_ADDITIONS[0]);
		expected.setExpandedLicensingSubjectExtendableLicense(STANDARD_LICENSES[0]);
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
		parseString = STD_IDS[0]+" WITH " + STD_EXCEPTION_IDS[0];
		expected = new ExpandedLicensingWithAdditionOperator();
		expected.setExpandedLicensingSubjectAddition(STD_LICENSE_EXCEPTIONS[0]);
		expected.setExpandedLicensingSubjectExtendableLicense(STANDARD_LICENSES[0]);
		result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
	}


	public void testSimpleAnd() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0] + " AND " + NONSTD_IDS[0];
		ExpandedLicensingConjunctiveLicenseSet expected = new ExpandedLicensingConjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().add(STANDARD_LICENSES[0]);
		expected.getExpandedLicensingMembers().add(NON_STD_LICENSES[0]);
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
	}


	public void testSimpleOr() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0] + " OR " + NONSTD_IDS[0];
		ExpandedLicensingDisjunctiveLicenseSet expected = new ExpandedLicensingDisjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().add(STANDARD_LICENSES[0]);
		expected.getExpandedLicensingMembers().add(NON_STD_LICENSES[0]);
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
	}


	public void testLargerAnd() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " AND " + NONSTD_IDS[1] + " AND " +
					STD_IDS[2] + " AND " + STD_IDS[3];
		ExpandedLicensingConjunctiveLicenseSet expected = new ExpandedLicensingConjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
	}


	public void testLargerOr() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " OR " + NONSTD_IDS[1] + " OR " +
					STD_IDS[2] + " OR " + STD_IDS[3];
		ExpandedLicensingDisjunctiveLicenseSet expected = new ExpandedLicensingDisjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
	}


	public void testOuterParens() throws InvalidSPDXAnalysisException {
		String parseString = "(" + STD_IDS[1] + " OR " + NONSTD_IDS[1] + " OR " +
					STD_IDS[2] + " OR " + STD_IDS[3] + ")";
		ExpandedLicensingDisjunctiveLicenseSet expected = new ExpandedLicensingDisjunctiveLicenseSet();
		expected.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, doc);
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
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, doc);
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
				modelStore, DEFAULT_PREFIX, copyManager, doc);
		assertTrue(expected.equals(result));
	}
	
	public void testExternalCustomLicense() throws InvalidSPDXAnalysisException {
		String simpleParseString = EXTERNAL_CUSTOM_LICENSE_TOKENS[0];
		SimpleLicensingAnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(simpleParseString, 
				modelStore, DEFAULT_PREFIX, copyManager, doc);
		assertTrue(result instanceof ExternalCustomLicense);
		assertEquals(EXTERNAL_CUSTOM_LICENSE_URIS[0], ((ExternalCustomLicense)result).getObjectUri());
		String complexParseString = EXTERNAL_CUSTOM_LICENSE_TOKENS[0] + " AND " +
				EXTERNAL_CUSTOM_LICENSE_TOKENS[1] + " AND " +
				EXTERNAL_CUSTOM_LICENSE_TOKENS[2] + " AND " +
				EXTERNAL_CUSTOM_LICENSE_TOKENS[3];
		result = LicenseExpressionParser.parseLicenseExpression(complexParseString, 
				modelStore, DEFAULT_PREFIX, copyManager, doc);
		assertTrue(result instanceof ExpandedLicensingConjunctiveLicenseSet);
		Boolean[] found = new Boolean[] {false, false, false, false};
		Collection<SimpleLicensingAnyLicenseInfo> members = ((ExpandedLicensingConjunctiveLicenseSet)result).getExpandedLicensingMembers();
		assertEquals(4, members.size());
		for (SimpleLicensingAnyLicenseInfo member:members) {
			for (int i = 0; i < EXTERNAL_CUSTOM_LICENSE_URIS.length; i++) {
				if (member.getObjectUri().equals(EXTERNAL_CUSTOM_LICENSE_URIS[i])) {
					found[i] = true;
				}
			}
		}
		for (Boolean foundIt:found) {
			assertTrue(foundIt);
		}
	}
	
	public void testExternalLicenseAddition() throws InvalidSPDXAnalysisException {
		fail("Not implemented");
	}

    public void regressionMitWith() throws InvalidSPDXAnalysisException, InvalidLicenseStringException {
    	SimpleLicensingAnyLicenseInfo result = LicenseInfoFactory.parseSPDXLicenseString("MIT WITH Autoconf-exception-2.0");
        assertEquals("MIT WITH Autoconf-exception-2.0",result.toString());
    }
}
