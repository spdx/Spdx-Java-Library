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
package org.spdx.library;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spdx.core.DefaultModelStore;
import org.spdx.core.DefaultStoreNotInitialized;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.InvalidLicenseStringException;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingConjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingCustomLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingDisjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingNoAssertionLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingNoneLicense;
import org.spdx.library.model.v3.simplelicensing.SimpleLicensingAnyLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class LicenseInfoFactoryTest extends TestCase {
	
	static final String[] NONSTD_IDS = new String[] {SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"1",
			SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"2", SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"3",
			SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"4"};
		static final String[] NONSTD_TEXTS = new String[] {"text1", "text2", "text3", "text4"};
		static final String[] STD_IDS = new String[] {"AFL-3.0", "CECILL-B", "EUPL-1.0"};
		static final String[] STD_TEXTS = new String[] {"Academic Free License (", "CONTRAT DE LICENCE DE LOGICIEL LIBRE CeCILL-B",
			"European Union Public Licence"};

		ExpandedLicensingCustomLicense[] NON_STD_LICENSES;
		ExpandedLicensingListedLicense[] STANDARD_LICENSES;
		ExpandedLicensingDisjunctiveLicenseSet[] DISJUNCTIVE_LICENSES;
		ExpandedLicensingConjunctiveLicenseSet[] CONJUNCTIVE_LICENSES;
		
		ExpandedLicensingConjunctiveLicenseSet COMPLEX_LICENSE;
		
		ModelCopyManager copyManager;
		IModelStore modelStore;
		static final String TEST_DOCUMENT_URI = "https://test.doc.uri";

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		SpdxModelFactory.init();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		DefaultModelStore.initialize(new InMemSpdxStore(), TEST_DOCUMENT_URI, copyManager);
		NON_STD_LICENSES = new ExpandedLicensingCustomLicense[NONSTD_IDS.length];
		for (int i = 0; i < NONSTD_IDS.length; i++) {
			NON_STD_LICENSES[i] = new ExpandedLicensingCustomLicense(modelStore, 
					TEST_DOCUMENT_URI + "#" + NONSTD_IDS[i], copyManager, true, null);
			NON_STD_LICENSES[i].setSimpleLicensingLicenseText(NONSTD_TEXTS[i]);
		}
		
		STANDARD_LICENSES = new ExpandedLicensingListedLicense[STD_IDS.length];
		for (int i = 0; i < STD_IDS.length; i++) {
			STANDARD_LICENSES[i] = new ExpandedLicensingListedLicense(modelStore, 
					SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + STD_IDS[i], copyManager, true, null);
			STANDARD_LICENSES[i].setName("Name "+String.valueOf(i));
			STANDARD_LICENSES[i].setSimpleLicensingLicenseText(STD_TEXTS[i]);
			STANDARD_LICENSES[i].getExpandedLicensingSeeAlsos().add("URL "+String.valueOf(i));
			STANDARD_LICENSES[i].setComment("Notes "+String.valueOf(i));
			STANDARD_LICENSES[i].setExpandedLicensingStandardLicenseHeader("LicHeader "+String.valueOf(i));
			STANDARD_LICENSES[i].setExpandedLicensingStandardLicenseTemplate("Template "+String.valueOf(i));
		}
		
		DISJUNCTIVE_LICENSES = new ExpandedLicensingDisjunctiveLicenseSet[3];
		CONJUNCTIVE_LICENSES = new ExpandedLicensingConjunctiveLicenseSet[2];
		
		DISJUNCTIVE_LICENSES[0] = new ExpandedLicensingDisjunctiveLicenseSet(modelStore, 
				modelStore.getNextId(IdType.Anonymous), copyManager, true, null);
		DISJUNCTIVE_LICENSES[0].getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				NON_STD_LICENSES[0], NON_STD_LICENSES[1], STANDARD_LICENSES[1]
		})));
		CONJUNCTIVE_LICENSES[0] = new ExpandedLicensingConjunctiveLicenseSet(modelStore, 
				modelStore.getNextId(IdType.Anonymous), copyManager, true, null);
		CONJUNCTIVE_LICENSES[0].getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				STANDARD_LICENSES[0], NON_STD_LICENSES[0], STANDARD_LICENSES[1]
		})));
		CONJUNCTIVE_LICENSES[1] = new ExpandedLicensingConjunctiveLicenseSet(modelStore, 
				modelStore.getNextId(IdType.Anonymous), copyManager, true, null);
		CONJUNCTIVE_LICENSES[1].getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[0], NON_STD_LICENSES[2]
		})));
		DISJUNCTIVE_LICENSES[1] = new ExpandedLicensingDisjunctiveLicenseSet(modelStore, 
				modelStore.getNextId(IdType.Anonymous), copyManager, true, null);
		DISJUNCTIVE_LICENSES[1].getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				CONJUNCTIVE_LICENSES[1], NON_STD_LICENSES[0], STANDARD_LICENSES[0]
		})));
		DISJUNCTIVE_LICENSES[2] = new ExpandedLicensingDisjunctiveLicenseSet(modelStore, 
				modelStore.getNextId(IdType.Anonymous), copyManager, true, null);
		DISJUNCTIVE_LICENSES[2].getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[1], CONJUNCTIVE_LICENSES[0], STANDARD_LICENSES[2]
		})));
		COMPLEX_LICENSE = new ExpandedLicensingConjunctiveLicenseSet(modelStore, 
				modelStore.getNextId(IdType.Anonymous), copyManager, true, null);
		COMPLEX_LICENSE.getExpandedLicensingMembers().addAll(new ArrayList<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[2], NON_STD_LICENSES[2], CONJUNCTIVE_LICENSES[1]
		})));
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		DefaultModelStore.initialize(new InMemSpdxStore(), "https://default/prefix", new ModelCopyManager());
	}
	
	public void testParseSPDXLicenseString() throws InvalidLicenseStringException, DefaultStoreNotInitialized {
		String parseString = COMPLEX_LICENSE.toString();
		SimpleLicensingAnyLicenseInfo li = LicenseInfoFactory.parseSPDXLicenseString(parseString);
		if (!li.equals(COMPLEX_LICENSE)) {
			fail("Parsed license does not equal");
		}
	}
	
	
	public void testSpecialLicenses() throws InvalidLicenseStringException, InvalidSPDXAnalysisException {
		// NONE
		SimpleLicensingAnyLicenseInfo none = LicenseInfoFactory.parseSPDXLicenseString(LicenseInfoFactory.NONE_LICENSE_NAME);
		SimpleLicensingAnyLicenseInfo comp = new ExpandedLicensingNoneLicense();
		assertEquals(none, comp);
		List<String> verify = comp.verify();
		assertEquals(0, verify.size());
		// NOASSERTION_NAME
		SimpleLicensingAnyLicenseInfo noAssertion = LicenseInfoFactory.parseSPDXLicenseString(LicenseInfoFactory.NOASSERTION_LICENSE_NAME);
		comp = new ExpandedLicensingNoAssertionLicense();
		assertEquals(noAssertion, comp);
		verify = comp.verify();
		assertEquals(0, verify.size());
	}
	
	
	public void testDifferentLicenseOrder() throws InvalidSPDXAnalysisException {
		SimpleLicensingAnyLicenseInfo order1 = LicenseInfoFactory.parseSPDXLicenseString("(LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-3 AND LicenseRef-12 AND LicenseRef-4 AND LicenseRef-13 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND LicenseRef-7 AND LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND GPL-2.0+ AND GPL-2.0 AND LicenseRef-17 AND LicenseRef-16 AND BSD-3-Clause-Clear)");
		SimpleLicensingAnyLicenseInfo order2 = LicenseInfoFactory.parseSPDXLicenseString("(LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-12 AND LicenseRef-3 AND LicenseRef-13 AND LicenseRef-4 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND LicenseRef-7 AND LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND GPL-2.0+ AND GPL-2.0 AND LicenseRef-17 AND BSD-3-Clause-Clear AND LicenseRef-16)");
		assertTrue(order1.equals(order2));
		assertTrue(order1.equivalent(order2));
	}

	public void testParseSPDXLicenseStringMixedCase() throws InvalidSPDXAnalysisException {
		String parseString = COMPLEX_LICENSE.toString();
		String lowerCaseCecil = parseString.replace("CECILL-B", "CECILL-B".toLowerCase());
		SimpleLicensingAnyLicenseInfo result = LicenseInfoFactory.parseSPDXLicenseString(lowerCaseCecil);
		assertEquals(COMPLEX_LICENSE, result);
	}
}
