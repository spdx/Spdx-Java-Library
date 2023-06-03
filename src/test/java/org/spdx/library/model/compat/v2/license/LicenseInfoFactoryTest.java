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
package org.spdx.library.model.compat.v2.license;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.model.compat.v2.GenericModelObject;

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

		ExtractedLicenseInfo[] NON_STD_LICENSES;
		SpdxListedLicense[] STANDARD_LICENSES;
		DisjunctiveLicenseSet[] DISJUNCTIVE_LICENSES;
		ConjunctiveLicenseSet[] CONJUNCTIVE_LICENSES;
		
		ConjunctiveLicenseSet COMPLEX_LICENSE;
		
		GenericModelObject gmo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		NON_STD_LICENSES = new ExtractedLicenseInfo[NONSTD_IDS.length];
		for (int i = 0; i < NONSTD_IDS.length; i++) {
			NON_STD_LICENSES[i] = new ExtractedLicenseInfo(NONSTD_IDS[i], NONSTD_TEXTS[i]);
		}
		
		STANDARD_LICENSES = new SpdxListedLicense[STD_IDS.length];
		for (int i = 0; i < STD_IDS.length; i++) {
			STANDARD_LICENSES[i] = new SpdxListedLicense("Name "+String.valueOf(i), 
					STD_IDS[i], STD_TEXTS[i], new ArrayList<String>(Arrays.asList(new String[] {"URL "+String.valueOf(i)})),
					"Notes "+String.valueOf(i), 
					"LicHeader "+String.valueOf(i), "Template "+String.valueOf(i), true, false, "", false, "");
		}
		
		DISJUNCTIVE_LICENSES = new DisjunctiveLicenseSet[3];
		CONJUNCTIVE_LICENSES = new ConjunctiveLicenseSet[2];
		
		DISJUNCTIVE_LICENSES[0] = gmo.createDisjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				NON_STD_LICENSES[0], NON_STD_LICENSES[1], STANDARD_LICENSES[1]
		})));
		CONJUNCTIVE_LICENSES[0] = gmo.createConjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				STANDARD_LICENSES[0], NON_STD_LICENSES[0], STANDARD_LICENSES[1]
		})));
		CONJUNCTIVE_LICENSES[1] = gmo.createConjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[0], NON_STD_LICENSES[2]
		})));
		DISJUNCTIVE_LICENSES[1] = gmo.createDisjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				CONJUNCTIVE_LICENSES[1], NON_STD_LICENSES[0], STANDARD_LICENSES[0]
		})));
		DISJUNCTIVE_LICENSES[2] = gmo.createDisjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[1], CONJUNCTIVE_LICENSES[0], STANDARD_LICENSES[2]
		})));
		COMPLEX_LICENSE = gmo.createConjunctiveLicenseSet(new ArrayList<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[2], NON_STD_LICENSES[2], CONJUNCTIVE_LICENSES[1]
		})));
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testParseSPDXLicenseString() throws InvalidLicenseStringException {
		String parseString = COMPLEX_LICENSE.toString();
		AnyLicenseInfo li = LicenseInfoFactory.parseSPDXLicenseString(parseString);
		if (!li.equals(COMPLEX_LICENSE)) {
			fail("Parsed license does not equal");
		}
	}
	
	
	public void testSpecialLicenses() throws InvalidLicenseStringException, InvalidSPDXAnalysisException {
		// NONE
		AnyLicenseInfo none = LicenseInfoFactory.parseSPDXLicenseString(LicenseInfoFactory.NONE_LICENSE_NAME);
		AnyLicenseInfo comp = new SpdxNoneLicense(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri());
		assertEquals(none, comp);
		List<String> verify = comp.verify();
		assertEquals(0, verify.size());
		// NOASSERTION_NAME
		AnyLicenseInfo noAssertion = LicenseInfoFactory.parseSPDXLicenseString(LicenseInfoFactory.NOASSERTION_LICENSE_NAME);
		comp = new SpdxNoAssertionLicense(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri());
		assertEquals(noAssertion, comp);
		verify = comp.verify();
		assertEquals(0, verify.size());
	}
	
	
	public void testDifferentLicenseOrder() throws InvalidSPDXAnalysisException {
		AnyLicenseInfo order1 = LicenseInfoFactory.parseSPDXLicenseString("(LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-3 AND LicenseRef-12 AND LicenseRef-4 AND LicenseRef-13 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND LicenseRef-7 AND LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND GPL-2.0+ AND GPL-2.0 AND LicenseRef-17 AND LicenseRef-16 AND BSD-2-Clause-Clear)");
		AnyLicenseInfo order2 = LicenseInfoFactory.parseSPDXLicenseString("(LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-12 AND LicenseRef-3 AND LicenseRef-13 AND LicenseRef-4 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND LicenseRef-7 AND LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND GPL-2.0+ AND GPL-2.0 AND LicenseRef-17 AND BSD-2-Clause-Clear AND LicenseRef-16)");
		assertTrue(order1.equals(order2));
		assertTrue(order1.equivalent(order2));
	}

	public void testParseSPDXLicenseStringMixedCase() throws InvalidSPDXAnalysisException {
		String parseString = COMPLEX_LICENSE.toString();
		// Local license ID's 
		String lowerCaseLicenseRef = parseString.replace("LicenseRef-", "licenseref-");
		AnyLicenseInfo result = LicenseInfoFactory.parseSPDXLicenseString(lowerCaseLicenseRef);
		assertEquals(COMPLEX_LICENSE, result);
		String lowerCaseCecil = parseString.replace("CECILL-B", "CECILL-B".toLowerCase());
		result = LicenseInfoFactory.parseSPDXLicenseString(lowerCaseCecil);
		assertEquals(COMPLEX_LICENSE, result);
	}
}
