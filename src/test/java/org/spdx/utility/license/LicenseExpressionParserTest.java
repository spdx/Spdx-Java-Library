/**
 * SPDX-FileCopyrightText: Copyright (c) 2019 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spdx.core.DefaultModelStore;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.InvalidLicenseStringException;
import org.spdx.library.model.v3_0_1.core.DictionaryEntry;
import org.spdx.library.model.v3_0_1.expandedlicensing.ConjunctiveLicenseSet;
import org.spdx.library.model.v3_0_1.expandedlicensing.CustomLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.CustomLicenseAddition;
import org.spdx.library.model.v3_0_1.expandedlicensing.DisjunctiveLicenseSet;
import org.spdx.library.model.v3_0_1.expandedlicensing.ExtendableLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ExternalCustomLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ExternalExtendableLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicenseException;
import org.spdx.library.model.v3_0_1.expandedlicensing.OrLaterOperator;
import org.spdx.library.model.v3_0_1.expandedlicensing.WithAdditionOperator;
import org.spdx.library.model.v3_0_1.simplelicensing.AnyLicenseInfo;
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
	static final String[] NAMESPACES = new String[] {"https://uri1/", "https://uri2#", "urn:uri3:"};
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
	
	CustomLicense[] NON_STD_LICENSES;
	ListedLicense[] STANDARD_LICENSES;
	ListedLicenseException[] STD_LICENSE_EXCEPTIONS;
	CustomLicenseAddition[] NON_STD_LICENSE_ADDITIONS;

	IModelStore modelStore;
	static final String TEST_DOCUMENT_URI = "https://test.doc.uri";
	static final String DEFAULT_PREFIX = TEST_DOCUMENT_URI + "#";
	ModelCopyManager copyManager;
	List<DictionaryEntry> idMap;
	
	protected void setUp() throws Exception {
		super.setUp();
		SpdxModelFactory.init();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		DefaultModelStore.initialize(new InMemSpdxStore(), TEST_DOCUMENT_URI, copyManager);
		NON_STD_LICENSES = new CustomLicense[NONSTD_IDS.length];
		for (int i = 0; i < NONSTD_IDS.length; i++) {
			NON_STD_LICENSES[i] = new CustomLicense(modelStore, TEST_DOCUMENT_URI + "#" + 
					NONSTD_IDS[i], copyManager, true, null);
			NON_STD_LICENSES[i].setLicenseText(NONSTD_TEXTS[i]);
		}

		STANDARD_LICENSES = new ListedLicense[STD_IDS.length];
		for (int i = 0; i < STD_IDS.length; i++) {
			STANDARD_LICENSES[i] = new ListedLicense(modelStore, 
					SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + STD_IDS[i], copyManager, true, null);
			STANDARD_LICENSES[i].setName("Name "+String.valueOf(i))
				.setLicenseText(STD_TEXTS[i])
				.getSeeAlsos().addAll(Arrays.asList(new String[] {"URL "+String.valueOf(i)}));
			STANDARD_LICENSES[i].setDescription("Notes "+String.valueOf(i))
			.setStandardLicenseHeader("LicHeader "+String.valueOf(i))
			.setStandardLicenseTemplate("Template "+String.valueOf(i));
		}
		STD_LICENSE_EXCEPTIONS = new ListedLicenseException[STD_EXCEPTION_IDS.length];
		for (int i = 0; i < STD_EXCEPTION_IDS.length; i++) {
			STD_LICENSE_EXCEPTIONS[i] = new ListedLicenseException(
					modelStore, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + STD_EXCEPTION_IDS[i], 
					copyManager, true, null);
			STD_LICENSE_EXCEPTIONS[i].setName(STD_EXCEPTION_NAMES[i])
					.setAdditionText(STD_EXCEPTION_TEXTS[i]);
		}
		NON_STD_LICENSE_ADDITIONS = new CustomLicenseAddition[NONSTD_ADDITION_IDS.length];
		for (int i = 0; i < NONSTD_ADDITION_IDS.length; i++) {
			NON_STD_LICENSE_ADDITIONS[i] = new CustomLicenseAddition(
					modelStore, TEST_DOCUMENT_URI + "#" + NONSTD_ADDITION_IDS[i], copyManager, true, null);
			NON_STD_LICENSE_ADDITIONS[i].setName(NON_STD_ADDITION_NAMES[i])
					.setAdditionText(NON_STD_ADDITION_TEXTS[i]);
		}
		idMap = new ArrayList<>();
		for (Entry<String, String> entry:NAMESPACE_MAP.entrySet()) {
			DictionaryEntry de = new DictionaryEntry(modelStore, modelStore.getNextId(IdType.Anonymous),
					copyManager, true, SpdxModelFactory.getLatestSpecVersion());
			de.setIdPrefix(entry.getKey());
			de.setValue(entry.getValue());
			idMap.add(de);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		DefaultModelStore.initialize(new InMemSpdxStore(), "https://docnamespace", new ModelCopyManager());
	}
	
	public void testSingleStdLicense() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0];
		AnyLicenseInfo expected = STANDARD_LICENSES[0];
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX,
				copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testSingleExtractedLicense() throws InvalidSPDXAnalysisException {
		String parseString = NONSTD_IDS[0];
		AnyLicenseInfo expected = NON_STD_LICENSES[0];
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}
	
	public void testUninitializedExtractedLicense() throws InvalidSPDXAnalysisException {
		String parseString = "LicenseRef-3242";
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, idMap);
		assertEquals(DEFAULT_PREFIX + parseString, result.getObjectUri());
	}


	public void testOrLater() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0]+"+";
		OrLaterOperator expected = new OrLaterOperator(DefaultModelStore.getDefaultDocumentUri());
		expected.setSubjectLicense(STANDARD_LICENSES[0]);
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testWithException() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0]+" WITH " + NONSTD_ADDITION_IDS[0];
		WithAdditionOperator expected = new WithAdditionOperator();
		expected.setSubjectAddition(NON_STD_LICENSE_ADDITIONS[0]);
		expected.setSubjectExtendableLicense(STANDARD_LICENSES[0]);
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
		parseString = STD_IDS[0]+" WITH " + STD_EXCEPTION_IDS[0];
		expected = new WithAdditionOperator();
		expected.setSubjectAddition(STD_LICENSE_EXCEPTIONS[0]);
		expected.setSubjectExtendableLicense(STANDARD_LICENSES[0]);
		result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testSimpleAnd() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0] + " AND " + NONSTD_IDS[0];
		ConjunctiveLicenseSet expected = new ConjunctiveLicenseSet();
		expected.getMembers().add(STANDARD_LICENSES[0]);
		expected.getMembers().add(NON_STD_LICENSES[0]);
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testSimpleOr() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[0] + " OR " + NONSTD_IDS[0];
		DisjunctiveLicenseSet expected = new DisjunctiveLicenseSet();
		expected.getMembers().add(STANDARD_LICENSES[0]);
		expected.getMembers().add(NON_STD_LICENSES[0]);
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testLargerAnd() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " AND " + NONSTD_IDS[1] + " AND " +
					STD_IDS[2] + " AND " + STD_IDS[3];
		ConjunctiveLicenseSet expected = new ConjunctiveLicenseSet();
		expected.getMembers().addAll(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testLargerOr() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " OR " + NONSTD_IDS[1] + " OR " +
					STD_IDS[2] + " OR " + STD_IDS[3];
		DisjunctiveLicenseSet expected = new DisjunctiveLicenseSet();
		expected.getMembers().addAll(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testOuterParens() throws InvalidSPDXAnalysisException {
		String parseString = "(" + STD_IDS[1] + " OR " + NONSTD_IDS[1] + " OR " +
					STD_IDS[2] + " OR " + STD_IDS[3] + ")";
		DisjunctiveLicenseSet expected = new DisjunctiveLicenseSet();
		expected.getMembers().addAll(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1],
				NON_STD_LICENSES[1], STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testInnerParens() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " AND " + NONSTD_IDS[1] + " AND " +
				"(" + STD_IDS[2] + " OR " + STD_IDS[3] + ")";
		DisjunctiveLicenseSet dls = new DisjunctiveLicenseSet();
		dls.getMembers().addAll(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[2], STANDARD_LICENSES[3]})));
		ConjunctiveLicenseSet expected = new ConjunctiveLicenseSet();
		expected.getMembers().addAll(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1] ,
				NON_STD_LICENSES[1], dls})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, modelStore, 
				DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}


	public void testAndOrPrecedence() throws InvalidSPDXAnalysisException {
		String parseString = STD_IDS[1] + " OR " + NONSTD_IDS[1] + " AND " +
				STD_IDS[2] + " OR " + STD_IDS[3];
		ConjunctiveLicenseSet cls = new ConjunctiveLicenseSet();
		cls.getMembers().addAll(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {NON_STD_LICENSES[1], STANDARD_LICENSES[2]})));
		
		DisjunctiveLicenseSet expected = new DisjunctiveLicenseSet();
		expected.getMembers().addAll(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[1] ,
				cls, STANDARD_LICENSES[3]})));
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(parseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(expected.equals(result));
	}
	
	public void testExternalCustomLicense() throws InvalidSPDXAnalysisException {
		String simpleParseString = EXTERNAL_CUSTOM_LICENSE_TOKENS[0];
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(simpleParseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(result instanceof ExternalCustomLicense);
		assertEquals(EXTERNAL_CUSTOM_LICENSE_URIS[0], ((ExternalCustomLicense)result).getObjectUri());
		String licenseWithAddition = EXTERNAL_CUSTOM_LICENSE_TOKENS[0] + " WITH 	389-exception";
		result = LicenseExpressionParser.parseLicenseExpression(licenseWithAddition, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(result instanceof WithAdditionOperator);
		ExtendableLicense subject = ((WithAdditionOperator)result).getSubjectExtendableLicense();
		assertTrue(subject instanceof ExternalExtendableLicense);
		assertEquals(EXTERNAL_CUSTOM_LICENSE_URIS[0], subject.getObjectUri());
		String complexParseString = EXTERNAL_CUSTOM_LICENSE_TOKENS[0] + " AND " +
				EXTERNAL_CUSTOM_LICENSE_TOKENS[1] + " AND " +
				EXTERNAL_CUSTOM_LICENSE_TOKENS[2] + " AND " +
				EXTERNAL_CUSTOM_LICENSE_TOKENS[3];
		result = LicenseExpressionParser.parseLicenseExpression(complexParseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(result instanceof ConjunctiveLicenseSet);
		Boolean[] found = new Boolean[] {false, false, false, false};
		Collection<AnyLicenseInfo> members = ((ConjunctiveLicenseSet)result).getMembers();
		assertEquals(4, members.size());
		for (AnyLicenseInfo member:members) {
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
		String simpleParseString = NONSTD_IDS[0] + " WITH " + EXTERNAL_CUSTOM_ADDITION_TOKENS[0];
		AnyLicenseInfo result = LicenseExpressionParser.parseLicenseExpression(simpleParseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(result instanceof WithAdditionOperator);
		
		assertEquals(EXTERNAL_CUSTOM_ADDITION_URIS[0], ((WithAdditionOperator)result)
						.getSubjectAddition().getObjectUri());
		String complexParseString = STD_IDS[0] + " WITH " + EXTERNAL_CUSTOM_ADDITION_TOKENS[0] + " AND " +
				STD_IDS[0] + " WITH " + EXTERNAL_CUSTOM_ADDITION_TOKENS[1] + " AND " +
				STD_IDS[1] + " WITH " + EXTERNAL_CUSTOM_ADDITION_TOKENS[2] + " AND " +
				STD_IDS[2] + " WITH " + EXTERNAL_CUSTOM_ADDITION_TOKENS[3];
		result = LicenseExpressionParser.parseLicenseExpression(complexParseString, 
				modelStore, DEFAULT_PREFIX, copyManager, idMap);
		assertTrue(result instanceof ConjunctiveLicenseSet);
		Boolean[] found = new Boolean[] {false, false, false, false};
		Collection<AnyLicenseInfo> members = ((ConjunctiveLicenseSet)result).getMembers();
		assertEquals(4, members.size());
		for (AnyLicenseInfo member:members) {
			for (int i = 0; i < EXTERNAL_CUSTOM_ADDITION_URIS.length; i++) {
				assertTrue(member instanceof WithAdditionOperator);
				if (((WithAdditionOperator)member).getSubjectAddition().getObjectUri().equals(EXTERNAL_CUSTOM_ADDITION_URIS[i])) {
					found[i] = true;
				}
			}
		}
		for (Boolean foundIt:found) {
			assertTrue(foundIt);
		}
	}

    public void regressionMitWith() throws InvalidSPDXAnalysisException, InvalidLicenseStringException {
    	AnyLicenseInfo result = LicenseInfoFactory.parseSPDXLicenseString("MIT WITH Autoconf-exception-2.0");
        assertEquals("MIT WITH Autoconf-exception-2.0",result.toString());
    }
}
