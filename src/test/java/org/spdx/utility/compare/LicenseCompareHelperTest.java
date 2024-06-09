/**
 * Copyright (c) 2013 Source Auditor Inc.
 * Copyright (c) 2013 Black Duck Software Inc.
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
 *
*/
package org.spdx.utility.compare;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.spdx.core.DefaultModelStore;
import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.ListedLicenses;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingConjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingCustomLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingDisjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicenseException;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingNoAssertionLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingNoneLicense;
import org.spdx.library.model.v3.simplelicensing.SimpleLicensingAnyLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.CompareTemplateOutputHandler.DifferenceDescription;
import org.spdx.utility.compare.FilterTemplateOutputHandler.VarTextHandling;

import junit.framework.TestCase;

/**
 * @author Gary O'Neall
 *
 */
public class LicenseCompareHelperTest extends TestCase {
	
	static final String GPL_2_TEXT = "TestFiles" + File.separator + "GPL-2.0.txt";
	static final String ZPL_2_1_TEXT = "TestFiles" + File.separator + "ZPL-2.1.txt";
	static final String GPL_3_TEXT = "TestFiles" + File.separator + "GPL-3.0-test.txt";
    static final String BSD_PROTECTION_TEXT = "TestFiles" + File.separator + "BSD-Protection.txt";
    static final String BSD_PROTECTION_TEMPLATE = "TestFiles" + File.separator + "BSD-Protection.template.txt";
    static final String EUPL_1_2_TEXT = "TestFiles" + File.separator + "EUPL-1.2.txt";
    static final String EUPL_1_2_TEMPLATE = "TestFiles" + File.separator + "EUPL-1.2.template.txt";
    static final String GD_TEMPLATE = "TestFiles" + File.separator + "GD.template.txt";
    static final String MULAN_PSL_2_TEMPLATE = "TestFiles" + File.separator + "MulanPSL-2.0.template.txt";
    static final String MULAN_PSL_2_COMMA_TEXT = "TestFiles" + File.separator + "MulanPSL-2.0-comma.txt";
    private static final String GROFF_COMMENTED_VERBATIM_TEXT = "TestFiles" + File.separator + "verbatim-man-page.txt";
    private static final String VERBATIM_MAN_PAGES_TEMPLATE = "TestFiles" + File.separator + "Verbatim-man-pages.template.txt";
    static final String PYTHON201_TEXT = "TestFiles" + File.separator + "Python-2.0.1.txt";
    static final String PYTHON201_TEMPLATE = "TestFiles" + File.separator + "Python-2.0.1.template.txt";
    static final String SGIB_1_0_TEXT = "TestFiles" + File.separator + "SGI-B-1.0.txt";
    static final String SGIB_1_0_TEMPLATE = "TestFiles" + File.separator + "SGI-B-1.0.template.txt";
    static final String APACHE_1_0_TEXT = "TestFiles" + File.separator + "Apache-1.0.txt";
    static final String ATLASSAIN_BSD_FILE = "TestFiles" + File.separator + "atlassain-bsd";
    static final String MPL_2_FROM_MOZILLA_FILE = "TestFiles" + File.separator + "mpl_2_from_mozilla.txt";
    static final String XDEBUG_1_03_TEXT = "TestFiles" + File.separator + "Xdebug-1.03.txt";
    static final String XDEBUG_1_03_TEMPLATE = "TestFiles" + File.separator + "Xdebug-1.03.template.txt";
    static final String FTL_TEXT = "TestFiles" + File.separator + "FTL.txt";
    static final String FTL_TEMPLATE = "TestFiles" + File.separator + "FTL.template.txt";
    static final String PARITY7_TEXT = "TestFiles" + File.separator + "Parity-7.0.0.txt";
    static final String PARITY7_TEMPLATE = "TestFiles" + File.separator + "Parity-7.0.0.template.txt";
    static final String POLYFORM_NC_TEXT = "TestFiles" + File.separator + "PolyForm-Noncommercial-1.0.0.txt";
    static final String POLYFORM_NC_TEMPLATE = "TestFiles" + File.separator + "PolyForm-Noncommercial-1.0.0.template.txt";
    static final String APL_1_TEXT = "TestFiles" + File.separator + "APL-1.0.txt";
    static final String APL_1_TEMPLATE = "TestFiles" + File.separator + "APL-1.0.template.txt";
    
    IModelStore modelStore;
    IModelCopyManager copyManager;
    String DEFAULT_DOCUMENT_URI = "http://default/doc";
    

	/**
	 * @throws java.lang.Exception
	 */
	public void setUp() throws Exception {
		SpdxModelFactory.init();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		DefaultModelStore.initialize(modelStore, DEFAULT_DOCUMENT_URI, copyManager);
	}

	/**
	 * @throws java.lang.Exception
	 */
	public void tearDown() throws Exception {
		DefaultModelStore.initialize(new InMemSpdxStore(), DEFAULT_DOCUMENT_URI, new ModelCopyManager());
	}


	public void testLicenseEqualsStdLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		Map<String, String> xlation = new HashMap<>();;
		String licName = "name";
		String licId = "ID1";
		String licText = "Text";
		Collection<String> sourceUrls = new HashSet<String>(Arrays.asList(new String[] {"http://www.sourceauditor.com/licenses"}));
		String notes = "Notes";
		String template = "Template";
		boolean osiApproved = false;
		boolean fsfLibre = true;
		boolean isDeprecated = false;
		String deprecatedVersion = "";
		ExpandedLicensingListedLicense lic1 = 
			new ExpandedLicensingListedLicense(modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + licId,
					copyManager, true, null)
				.setName(licName)
				.setSimpleLicensingLicenseText(licText)
				.setExpandedLicensingStandardLicenseTemplate(template)
				.setExpandedLicensingIsOsiApproved(osiApproved)
				.setExpandedLicensingIsFsfLibre(fsfLibre)
				.setExpandedLicensingIsDeprecatedLicenseId(isDeprecated)
				.setExpandedLicensingDeprecatedVersion(deprecatedVersion)
				.setComment(notes);
		lic1.getExpandedLicensingSeeAlsos().addAll(sourceUrls);
		ExpandedLicensingListedLicense lic2 = 
				new ExpandedLicensingListedLicense(modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + licId,
						copyManager, true, null)
					.setName(licName)
					.setSimpleLicensingLicenseText(licText)
					.setExpandedLicensingStandardLicenseTemplate(template)
					.setExpandedLicensingIsOsiApproved(osiApproved)
					.setExpandedLicensingIsFsfLibre(fsfLibre)
					.setExpandedLicensingIsDeprecatedLicenseId(isDeprecated)
					.setExpandedLicensingDeprecatedVersion(deprecatedVersion)
					.setComment(notes);
			lic2.getExpandedLicensingSeeAlsos().addAll(sourceUrls);
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlation));
		
		// try just changing the text - should still equal since the ID's are equal
		String text2 = "text2";
		ExpandedLicensingListedLicense lic3 = 
				new ExpandedLicensingListedLicense(modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + licId,
						copyManager, true, null)
					.setName(licName)
					.setSimpleLicensingLicenseText(text2)
					.setExpandedLicensingStandardLicenseTemplate(template)
					.setExpandedLicensingIsOsiApproved(osiApproved)
					.setExpandedLicensingIsFsfLibre(fsfLibre)
					.setExpandedLicensingIsDeprecatedLicenseId(isDeprecated)
					.setExpandedLicensingDeprecatedVersion(deprecatedVersion)
					.setComment(notes);
			lic2.getExpandedLicensingSeeAlsos().addAll(sourceUrls);
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic3, xlation));
		// now try a different ID
		String licId2 = "ID2";
		ExpandedLicensingListedLicense lic4 = 
				new ExpandedLicensingListedLicense(modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + licId2,
						copyManager, true, null)
					.setName(licName)
					.setSimpleLicensingLicenseText(licText)
					.setExpandedLicensingStandardLicenseTemplate(template)
					.setExpandedLicensingIsOsiApproved(osiApproved)
					.setExpandedLicensingIsFsfLibre(fsfLibre)
					.setExpandedLicensingIsDeprecatedLicenseId(isDeprecated)
					.setExpandedLicensingDeprecatedVersion(deprecatedVersion)
					.setComment(notes);
			lic4.getExpandedLicensingSeeAlsos().addAll(sourceUrls);
		assertFalse(LicenseCompareHelper.isLicenseEqual(lic1, lic4, xlation));
	}
	
	public void testLicenseEqualsNonStdLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		Map<String, String> xlation = new HashMap<>();;
		String licId = "ID1";
		String licText = "Text";

		// same license ID's
		ExpandedLicensingCustomLicense lic1 = 
			new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#LicenseRef-" + licId,
					copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic2 = 
			new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#LicenseRef-" + licId,
					copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#LicenseRef-" + licId, DEFAULT_DOCUMENT_URI + "#LicenseRef-" + licId);
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlation));
		// different license ID, same license
		xlation.clear();
		String licId2 = "id2";
		lic2 = 
			new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#LicenseRef-" + licId2,
					copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#LicenseRef-" + licId, DEFAULT_DOCUMENT_URI + "#LicenseRef-" + licId2);
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlation));
		// different license ID, different license
		String licId3 = "id3";
		lic2 = 
			new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#LicenseRef-" + licId3,
					copyManager, true, null).setSimpleLicensingLicenseText(licId2);
		assertFalse(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlation));
	}
	
	public void testLicenseEqualsConjunctiveLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		String licText = "Text";
		String licId1 = "LicenseRef-id1";
		String licId2 = "LicenseRef-id2";
		String licId3 = "LicenseRef-id3";
		String licId4 = "LicenseRef-id4";
		String licId5 = "LicenseRef-id5";
		String licId6 = "LicenseRef-id6";
		Map<String, String> xlation = new HashMap<>();;
		ExpandedLicensingCustomLicense lic1 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId1,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic2 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId2,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic3 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId3,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic4 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId4,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic5 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId5,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic6 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId6,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId1, DEFAULT_DOCUMENT_URI + "#" + licId4);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId2, DEFAULT_DOCUMENT_URI + "#" + licId5);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId3, DEFAULT_DOCUMENT_URI + "#" + licId6);
		Collection<SimpleLicensingAnyLicenseInfo> set1 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic1, lic2, lic3
		}));
		Collection<SimpleLicensingAnyLicenseInfo> set2 =  new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic4, lic5, lic6
		}));
		ExpandedLicensingConjunctiveLicenseSet conj1 = new ExpandedLicensingConjunctiveLicenseSet(modelStore, modelStore.getNextId(IdType.Anonymous), copyManager, true, null);
		conj1.getExpandedLicensingMembers().addAll(set1);
		ExpandedLicensingConjunctiveLicenseSet conj2 = new ExpandedLicensingConjunctiveLicenseSet();
		conj2.getExpandedLicensingMembers().addAll(set2);
		
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj1, conj2, xlation));
		// different order
		set2 =  new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic5, lic6, lic4
		}));
		conj2 = new ExpandedLicensingConjunctiveLicenseSet();
		conj2.getExpandedLicensingMembers().addAll(set2);
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj1, conj2, xlation));

		String licId7 = "LicenseRef-id7";
		ExpandedLicensingCustomLicense lic7 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId7,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		Collection<SimpleLicensingAnyLicenseInfo> set3 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic4, lic5, lic7
		}));
		ExpandedLicensingConjunctiveLicenseSet conj3 = new ExpandedLicensingConjunctiveLicenseSet();
		conj3.getExpandedLicensingMembers().addAll(set3);
		assertFalse(LicenseCompareHelper.isLicenseEqual(conj1, conj3, xlation));		
	}		
	
	public void testLicenseEqualsConjunctiveLicenseDifferentOrder() throws InvalidSPDXAnalysisException, SpdxCompareException {
		String licText = "Text";
		String licId1 = "LicenseRef-id1";
		String licId2 = "LicenseRef-id2";
		String licId3 = "LicenseRef-id3";
		String licId4 = "LicenseRef-id4";
		String licId5 = "LicenseRef-id5";
		String licId6 = "LicenseRef-id6";
		Map<String, String> xlation = new HashMap<>();;
		ExpandedLicensingCustomLicense lic1 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId1,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic2 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId2,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic3 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId3,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic4 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId4,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic5 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId5,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic6 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId6,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId1, DEFAULT_DOCUMENT_URI + "#" + licId4);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId2, DEFAULT_DOCUMENT_URI + "#" + licId5);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId3, DEFAULT_DOCUMENT_URI + "#" + licId6);
		Collection<SimpleLicensingAnyLicenseInfo> set1 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic1, lic2, lic3
		}));
		Collection<SimpleLicensingAnyLicenseInfo> set2 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic4, lic6, lic5
		}));
		ExpandedLicensingConjunctiveLicenseSet conj1 = new ExpandedLicensingConjunctiveLicenseSet();
		conj1.getExpandedLicensingMembers().addAll(set1);
		ExpandedLicensingConjunctiveLicenseSet conj2 = new ExpandedLicensingConjunctiveLicenseSet();
		conj2.getExpandedLicensingMembers().addAll(set2);
		
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj1, conj2, xlation));
	
		// busybox-1.rdf: (LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-3 AND LicenseRef-12 AND LicenseRef-4 AND LicenseRef-13 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND LicenseRef-7 AND LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND GPL-2.0+ AND GPL-2.0 AND LicenseRef-17 AND LicenseRef-16 AND BSD-2-Clause-Clear)
		xlation.clear();
		String licIdRef14 = "LicenseRef-14";
		ExpandedLicensingCustomLicense licref14 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef14,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef14, DEFAULT_DOCUMENT_URI + "#" + licIdRef14);
		String licIdRef5 = "LicenseRef-5";
		ExpandedLicensingCustomLicense licref5 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef5,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef5, DEFAULT_DOCUMENT_URI + "#" + licIdRef5);
		String licIdref6 = "LicenseRef-6";
		ExpandedLicensingCustomLicense licref6 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdref6,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdref6, DEFAULT_DOCUMENT_URI + "#" + licIdref6);
		String licIdRef15 = "LicenseRef-15";
		ExpandedLicensingCustomLicense licref15 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef15,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef15, DEFAULT_DOCUMENT_URI + "#" + licIdRef15);
		String licIdRef3 = "LicenseRef-3";
		ExpandedLicensingCustomLicense licref3 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef3,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef3, DEFAULT_DOCUMENT_URI + "#" + licIdRef3);
		String licIdRef12 = "LicenseRef-12";
		ExpandedLicensingCustomLicense licref12 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef12,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef12, DEFAULT_DOCUMENT_URI + "#" + licIdRef12);
		String licIdRef4 = "LicenseRef-4";
		ExpandedLicensingCustomLicense licref4 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef4,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef4, DEFAULT_DOCUMENT_URI + "#" + licIdRef4);
		String licIdRef13 = "LicenseRef-13";
		ExpandedLicensingCustomLicense licref13 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef13,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef13, DEFAULT_DOCUMENT_URI + "#" + licIdRef13);
		String licIdref10 = "LicenseRef-10";
		ExpandedLicensingCustomLicense licref10 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdref10,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdref10, DEFAULT_DOCUMENT_URI + "#" + licIdref10);
		String licIdRef9 = "LicenseRef-9";
		ExpandedLicensingCustomLicense licref9 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef9,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef9, DEFAULT_DOCUMENT_URI + "#" + licIdRef9);
		String licIdRef11 = "LicenseRef-11";
		ExpandedLicensingCustomLicense licref11 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef11,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef11, DEFAULT_DOCUMENT_URI + "#" + licIdRef11);
		String licIdRef7 = "LicenseRef-7";
		ExpandedLicensingCustomLicense licref7 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef7,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef7, DEFAULT_DOCUMENT_URI + "#" + licIdRef7);
		String licIdRef8 = "LicenseRef-8";
		ExpandedLicensingCustomLicense licref8 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licIdRef8,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licIdRef8, DEFAULT_DOCUMENT_URI + "#" + licIdRef8);
		String licLGPLPlusId = "LGPL-2.1+";
		ExpandedLicensingListedLicense licLGPLPlus = LicenseInfoFactory.getListedLicenseById(licLGPLPlusId);
		String licRef1 = "LicenseRef-1";
		ExpandedLicensingCustomLicense licref1 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licRef1,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licRef1, DEFAULT_DOCUMENT_URI + "#" + licRef1);
		String licRef2 = "LicenseRef-2";
		ExpandedLicensingCustomLicense licref2 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licRef2,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licRef2, DEFAULT_DOCUMENT_URI + "#" + licRef2);
		String licRef0 = "LicenseRef-0";
		ExpandedLicensingCustomLicense licref0 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licRef0,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licRef0, DEFAULT_DOCUMENT_URI + "#" + licRef0);
		String licGPL20PlusId = "GPL-2.0+";
		ExpandedLicensingListedLicense licGPL20Plus = LicenseInfoFactory.getListedLicenseById(licGPL20PlusId);
		String licGPL20id = "GPL-2.0";
		ExpandedLicensingListedLicense licGPL20 = LicenseInfoFactory.getListedLicenseById(licGPL20id);
		String licRef17 = "LicenseRef-17";
		ExpandedLicensingCustomLicense licref17 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licRef17,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licRef17, DEFAULT_DOCUMENT_URI + "#" + licRef17);
		String licRef16 = "LicenseRef-16";
		ExpandedLicensingCustomLicense licref16 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licRef16,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licRef16, DEFAULT_DOCUMENT_URI + "#" + licRef16);
		String licRefBSD2Clearid = "BSD-2-Clause";
		ExpandedLicensingListedLicense licRefBSD2Clear = LicenseInfoFactory.getListedLicenseById(licRefBSD2Clearid);
		// busybox-1.rdf: (LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-3 AND 
		//LicenseRef-12 AND LicenseRef-4 AND LicenseRef-13 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND 
		//LicenseRef-7 AND LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND 
		//GPL-2.0+ AND GPL-2.0 AND LicenseRef-17 AND LicenseRef-16 AND BSD-2-Clause-Clear)

		Collection<SimpleLicensingAnyLicenseInfo> bbset1 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {licref14, licref5, licref6, licref15, licref3, licref12, licref4, 
				licref13,licref10, licref9, licref11, licref7, licref8, licLGPLPlus, licref1, licref2, licref0, licGPL20Plus,
				licGPL20, licref17, licref16, licRefBSD2Clear
		}));
		ExpandedLicensingConjunctiveLicenseSet bbconj1 = new ExpandedLicensingConjunctiveLicenseSet();
		bbconj1.getExpandedLicensingMembers().addAll(bbset1);
		// busybox-2.rdf: (LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-12 AND LicenseRef-3
		//AND LicenseRef-13 AND LicenseRef-4 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND LicenseRef-7 AND 
		//LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND GPL-2.0+ AND GPL-2.0 AND 
		//LicenseRef-17 AND BSD-2-Clause-Clear AND LicenseRef-16)

		Collection<SimpleLicensingAnyLicenseInfo> bbset2 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {licref14, licref5, licref6, licref15, licref12, licref3, licref13,
				licref4, licref10, licref9, licref11, licref7, licref8, licLGPLPlus, licref1, licref2, licref0, licGPL20Plus,
				licGPL20, licref17, licRefBSD2Clear, licref16
		}));
		ExpandedLicensingConjunctiveLicenseSet bbconj2 = new ExpandedLicensingConjunctiveLicenseSet();
		bbconj2.getExpandedLicensingMembers().addAll(bbset2);
		assertTrue(LicenseCompareHelper.isLicenseEqual(bbconj1, bbconj2, xlation));
		assertTrue(LicenseCompareHelper.isLicenseEqual(bbconj2, bbconj1, xlation));
	}	
	
	public void testLicenseEqualsDisjunctiveLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		String licText = "Text";
		String licId1 = "LicenseRef-id1";
		String licId2 = "LicenseRef-id2";
		String licId3 = "LicenseRef-id3";
		String licId4 = "LicenseRef-id4";
		String licId5 = "LicenseRef-id5";
		String licId6 = "LicenseRef-id6";
		Map<String, String> xlation = new HashMap<>();;
		ExpandedLicensingCustomLicense lic1 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId1,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic2 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId2,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic3 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId3,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic4 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId4,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic5 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId5,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic6 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId6,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId1, DEFAULT_DOCUMENT_URI + "#" + licId4);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId2, DEFAULT_DOCUMENT_URI + "#" + licId5);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId3, DEFAULT_DOCUMENT_URI + "#" + licId6);
		Collection<SimpleLicensingAnyLicenseInfo> set1 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic1, lic2, lic3
		}));
		Collection<SimpleLicensingAnyLicenseInfo> set2 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic4, lic5, lic6
		}));
		ExpandedLicensingDisjunctiveLicenseSet conj1 = new ExpandedLicensingDisjunctiveLicenseSet();
		conj1.getExpandedLicensingMembers().addAll(set1);
		ExpandedLicensingDisjunctiveLicenseSet conj2 = new ExpandedLicensingDisjunctiveLicenseSet();
		conj2.getExpandedLicensingMembers().addAll(set2);
		
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj1, conj2, xlation));
		
		String licId7 = "LicenseRef-id7";
		ExpandedLicensingCustomLicense lic7 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId7,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		Collection<SimpleLicensingAnyLicenseInfo> set3 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic4, lic5, lic7
		}));
		ExpandedLicensingDisjunctiveLicenseSet conj3 = new ExpandedLicensingDisjunctiveLicenseSet();
		conj3.getExpandedLicensingMembers().addAll(set3);
		assertFalse(LicenseCompareHelper.isLicenseEqual(conj1, conj3, xlation));
	}	
	
	public void testLicenseEqualsComplexLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		String licText = "Text";
		String licId1 = "LicenseRef-id1";
		String licId2 = "LicenseRef-id2";
		String licId3 = "LicenseRef-id3";
		String licId4 = "LicenseRef-id4";
		String licId5 = "LicenseRef-id5";
		String licId6 = "LicenseRef-id6";
		Map<String, String> xlation = new HashMap<>();;
		ExpandedLicensingCustomLicense lic1 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId1,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic2 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId2,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic3 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId3,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic4 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId4,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic5 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId5,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		ExpandedLicensingCustomLicense lic6 = new ExpandedLicensingCustomLicense(modelStore, DEFAULT_DOCUMENT_URI + "#" + licId6,
				copyManager, true, null).setSimpleLicensingLicenseText(licText);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId1, DEFAULT_DOCUMENT_URI + "#" + licId4);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId2, DEFAULT_DOCUMENT_URI + "#" + licId5);
		xlation.put(DEFAULT_DOCUMENT_URI + "#" + licId3, DEFAULT_DOCUMENT_URI + "#" + licId6);
		Collection<SimpleLicensingAnyLicenseInfo> set1 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic1, lic2
		}));
		Collection<SimpleLicensingAnyLicenseInfo> set2 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic4, lic5
		}));
		ExpandedLicensingDisjunctiveLicenseSet conj1 = new ExpandedLicensingDisjunctiveLicenseSet();
		conj1.getExpandedLicensingMembers().addAll(set1);
		ExpandedLicensingDisjunctiveLicenseSet conj2 = new ExpandedLicensingDisjunctiveLicenseSet();
		conj2.getExpandedLicensingMembers().addAll(set2);
		
		Collection<SimpleLicensingAnyLicenseInfo> set3 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				conj1, lic3
		}));
		Collection<SimpleLicensingAnyLicenseInfo> set4 = new HashSet<SimpleLicensingAnyLicenseInfo>(Arrays.asList(new SimpleLicensingAnyLicenseInfo[] {
				lic6, conj2
		}));
		ExpandedLicensingConjunctiveLicenseSet conj3 = new ExpandedLicensingConjunctiveLicenseSet();
		conj3.getExpandedLicensingMembers().addAll(set3);
		ExpandedLicensingConjunctiveLicenseSet conj4 = new ExpandedLicensingConjunctiveLicenseSet();
		conj4.getExpandedLicensingMembers().addAll(set4);
		
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj3, conj4, xlation));
	}	
	
	public void testLicenseEqualsNoAsserLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		ExpandedLicensingNoAssertionLicense lic1 = new ExpandedLicensingNoAssertionLicense();
		ExpandedLicensingNoAssertionLicense lic2 = new ExpandedLicensingNoAssertionLicense();
		ExpandedLicensingNoneLicense lic3 = new ExpandedLicensingNoneLicense();
		Map<String, String> xlationMap = new HashMap<>();;
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlationMap));
		assertFalse(LicenseCompareHelper.isLicenseEqual(lic1, lic3, xlationMap));
	}	
	
	public void testLicenseEqualsNoneLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		ExpandedLicensingNoAssertionLicense lic2 = new ExpandedLicensingNoAssertionLicense();
		ExpandedLicensingNoneLicense lic3 = new ExpandedLicensingNoneLicense();
		ExpandedLicensingNoneLicense lic4 = new ExpandedLicensingNoneLicense();
		Map<String, String> xlationMap = new HashMap<>();;
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic3, lic4, xlationMap));
		assertFalse(LicenseCompareHelper.isLicenseEqual(lic4, lic2, xlationMap));
	}	
	
	
	public void testisSingleTokenString() {
		assertTrue(LicenseCompareHelper.isSingleTokenString(" token "));
		assertTrue(LicenseCompareHelper.isSingleTokenString("'"));
		assertTrue(LicenseCompareHelper.isSingleTokenString(" '"));
		assertTrue(LicenseCompareHelper.isSingleTokenString("' "));
		assertFalse(LicenseCompareHelper.isSingleTokenString("a and"));
		assertFalse(LicenseCompareHelper.isSingleTokenString("a\nand"));
	}
	
	public void regressionTestMatchingGpl20Only() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException {
		String compareText = UnitTestHelper.fileToText(GPL_2_TEXT);
		DifferenceDescription result = LicenseCompareHelper.isTextStandardLicense(LicenseInfoFactory.getListedLicenseById("GPL-2.0-only"), compareText);
		assertFalse(result.isDifferenceFound());
	}
	
	public void testMatchingStandardLicenseIds() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException {
		if (UnitTestHelper.runSlowTests()) {
			String compareText = UnitTestHelper.fileToText(GPL_2_TEXT);
			String[] result = LicenseCompareHelper.matchingStandardLicenseIds(compareText);
			assertEquals(4,result.length);
			assertTrue(result[0].startsWith("GPL-2"));
			assertTrue(result[1].startsWith("GPL-2"));
			assertTrue(result[2].startsWith("GPL-2"));
			assertTrue(result[3].startsWith("GPL-2"));
		}
	}
	
	public void testFirstLicenseToken() {
		assertEquals("first", LicenseCompareHelper.getFirstLicenseToken("   first,token that is needed\nnext"));
	}
	
	@SuppressWarnings("unused")
	private String stringCharToUnicode(String s, int location) {
		return "\\u" + Integer.toHexString(s.charAt(location) | 0x10000).substring(1);
	}
	
	public void regressionTestZpl21() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException {
		String compareText = UnitTestHelper.fileToText(ZPL_2_1_TEXT);
		DifferenceDescription result = LicenseCompareHelper.isTextStandardLicense(LicenseInfoFactory.getListedLicenseById("ZPL-2.1"), compareText);
		assertFalse(result.isDifferenceFound());
	}
	
	public void testIsTextStandardLicenseGpl3() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		ExpandedLicensingListedLicense gpl3 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-3.0");
		String compareText = UnitTestHelper.fileToText(GPL_3_TEXT);
		DifferenceDescription result = LicenseCompareHelper.isTextStandardLicense(gpl3, compareText);
		assertFalse(result.isDifferenceFound());
	}
	
	public void testIsTextStandardLicenseComments() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		ExpandedLicensingListedLicense bsd = ListedLicenses.getListedLicenses().getListedLicenseById("0BSD");
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new StringReader(bsd.getSimpleLicensingLicenseText()));
			String line = reader.readLine();
			sb.append("/* \n");
			while (line != null) {
				sb.append("  * ");
				sb.append(line);
				sb.append("\n");
				line = reader.readLine();
			}
			sb.append("*/\n");
			DifferenceDescription result = LicenseCompareHelper.isTextStandardLicense(bsd, sb.toString());
			assertFalse(result.isDifferenceFound());
			reader.close();
			reader = new BufferedReader(new StringReader(bsd.getSimpleLicensingLicenseText()));
			sb.setLength(0);
			line = reader.readLine();
			while (line != null) {
				sb.append("  REM ");
				sb.append(line);
				sb.append("\n");
				line = reader.readLine();
			}
			result = LicenseCompareHelper.isTextStandardLicense(bsd, sb.toString());
			assertFalse(result.isDifferenceFound());
		} finally {
		    if (Objects.nonNull(reader)) {
		        reader.close();
		    }
		}
	}

	public void testIsStandardLicenseWithinText() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		ExpandedLicensingListedLicense gpl30 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-3.0");
		ExpandedLicensingListedLicense apache10 = ListedLicenses.getListedLicenses().getListedLicenseById("Apache-1.0");
		ExpandedLicensingListedLicense apache20 = ListedLicenses.getListedLicenses().getListedLicenseById("Apache-2.0");
		ExpandedLicensingListedLicense mplLicense = ListedLicenses.getListedLicenses().getListedLicenseById("MPL-2.0");
		String multiLicenseText = gpl30.getSimpleLicensingLicenseText() + "\n\n----------\n\n" + apache20.getSimpleLicensingLicenseText();
		String textWithRandomPrefixAndSuffix = "Some random preamble text.\n\n" + apache20.getSimpleLicensingLicenseText() + "\n\nSome random epilogue text.";

		assertFalse(LicenseCompareHelper.isStandardLicenseWithinText(null, gpl30));
		assertFalse(LicenseCompareHelper.isStandardLicenseWithinText("", gpl30));
		assertFalse(LicenseCompareHelper.isStandardLicenseWithinText("Some random text that isn't GPL-3.0", gpl30));

		assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(multiLicenseText, gpl30));
		assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(multiLicenseText, apache20));
		assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(textWithRandomPrefixAndSuffix, apache20));
		assertFalse(LicenseCompareHelper.isStandardLicenseWithinText(multiLicenseText, apache10));
		String mplText = UnitTestHelper.fileToText(MPL_2_FROM_MOZILLA_FILE);
		assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(mplText, mplLicense));
		DifferenceDescription mplDiff = LicenseCompareHelper.isTextStandardLicense(mplLicense, mplText);
		assertFalse(mplDiff.isDifferenceFound());

/* Currently doesn't work - see https://github.com/spdx/Spdx-Java-Library/issues/141 for details
		// JavaMail license is "CDDL-1.1 OR GPL-2.0 WITH Classpath-exception-2.0"
		ExpandedLicensingListedLicense cddl11 = ListedLicenses.getListedLicenses().getListedLicenseById("CDDL-1.1");
		ExpandedLicensingListedLicense gpl20 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-2.0");
		String javaMailLicense = UnitTestHelper.urlToText("https://raw.githubusercontent.com/javaee/javamail/master/LICENSE.txt");

		assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(javaMailLicense, cddl11));
		assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(javaMailLicense, gpl20));
		assertFalse(LicenseCompareHelper.isStandardLicenseWithinText(javaMailLicense, apache20));
 */
	}
	
	public void testIsStandardLicenseExceptionWithinText() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		ExpandedLicensingListedLicenseException classpath20 = ListedLicenses.getListedLicenses().getListedExceptionById("Classpath-exception-2.0");
		String gpl20 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-2.0").getSimpleLicensingLicenseText();
		String classpathException20 = classpath20.getExpandedLicensingAdditionText();
		String gpl20WithClasspathException20 = gpl20 + "\n\n" + classpathException20;
		String textWithRandomPrefixAndSuffix = "Some random preamble text.\n\n" + classpathException20 + "\n\nSome random epilogue text.";

		assertFalse(LicenseCompareHelper.isStandardLicenseExceptionWithinText(null, classpath20));
		assertFalse(LicenseCompareHelper.isStandardLicenseExceptionWithinText("", classpath20));
		assertFalse(LicenseCompareHelper.isStandardLicenseExceptionWithinText("Some random text that isn't Classpath-exception-2.0", classpath20));

		assertTrue(LicenseCompareHelper.isStandardLicenseExceptionWithinText(classpathException20, classpath20));
		assertTrue(LicenseCompareHelper.isStandardLicenseExceptionWithinText(gpl20WithClasspathException20, classpath20));
		assertTrue(LicenseCompareHelper.isStandardLicenseExceptionWithinText(textWithRandomPrefixAndSuffix, classpath20));
	}

	// Note: comparing lists directly in JUnit 4.x doesn't work as one might expect, so we use this helper
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void assertListsEqual(List expected, List actual) {
		if (expected == null) {
			if (actual != null) {
				fail("Expected: <null> but was: " + Arrays.toString(actual.toArray()));
			}
		}
		else if (actual == null) {
			fail("Expected: " + Arrays.toString(expected.toArray()) + " but was: <null>");
		}
		else if (!(actual.containsAll(expected) && expected.containsAll(actual))) {
			fail("Expected: " + Arrays.toString(expected.toArray()) + " but was: " + Arrays.toString(actual.toArray()));
		}
	}

	public void testMatchingStandardLicenseIdsWithinText() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		String gpl30 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-3.0").getSimpleLicensingLicenseText();
		String apache20 = ListedLicenses.getListedLicenses().getListedLicenseById("Apache-2.0").getSimpleLicensingLicenseText();
		String multiLicenseText = gpl30 + "\n\n----------\n\n" + apache20;
		String textWithRandomPrefixAndSuffix = "Some random preamble text.\n\n" + apache20 + "\n\nSome random epilogue text.";
		String aladdin = ListedLicenses.getListedLicenses().getListedLicenseById("Aladdin").getSimpleLicensingLicenseText();
		List<String> expectedResultEmpty = Arrays.asList();
		List<String> expectedResultApache20 = Arrays.asList("Apache-2.0");
		List<String> expectedResultGpl30 = Arrays.asList("GPL-3.0");
		List<String> expectedResultGpl30Apache20 = Arrays.asList("GPL-3.0-only", "GPL-3.0", "Apache-2.0", "GPL-3.0-or-later", "GPL-3.0+");
		List<String> expectedBsd2Clause = Arrays.asList("BSD-2-Clause");
		List<String> expectedAladdin = Arrays.asList("Aladdin");
		List<String> expectedSmlnj = Arrays.asList("SMLNJ");
		// Note: be cautious about adding too many assertions to this test, as LicenseCompareHelper.matchingStandardLicenseIdsWithinText can have lengthy runtimes
		assertListsEqual(expectedResultEmpty, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(null));
		assertListsEqual(expectedResultEmpty, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(""));
		if (UnitTestHelper.runSlowTests()) {
			assertListsEqual(expectedResultEmpty, LicenseCompareHelper.matchingStandardLicenseIdsWithinText("Some random text that isn't a standard license"));
		}

		// Tests for the 2-arg version of matchingStandardLicenseIdsWithinText (which is faster than the 1-arg version)
		assertListsEqual(expectedResultApache20, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(multiLicenseText, Arrays.asList("Apache-2.0")));
		if (UnitTestHelper.runSlowTests()) {
			assertListsEqual(expectedResultGpl30, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(multiLicenseText, Arrays.asList("GPL-3.0")));
		}
		// Test for all license IDs
		if (UnitTestHelper.runSlowTests()) {
			assertEquals(expectedAladdin, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(aladdin, expectedAladdin));
			assertEquals(expectedResultEmpty, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(multiLicenseText, expectedAladdin));
			assertEquals(expectedResultEmpty, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(multiLicenseText, expectedSmlnj));
			assertListsEqual(expectedResultApache20, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(apache20));
			assertListsEqual(expectedResultGpl30Apache20, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(multiLicenseText));
			assertListsEqual(expectedResultApache20, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(textWithRandomPrefixAndSuffix));
			assertListsEqual(expectedBsd2Clause, LicenseCompareHelper.matchingStandardLicenseIdsWithinText(UnitTestHelper.fileToText(ATLASSAIN_BSD_FILE), Arrays.asList("BSD-2-Clause")));
		}
	}

	public void testMatchingStandardLicenseExceptionIdsWithinText() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		String gpl20 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-2.0").getSimpleLicensingLicenseText();
		String classpathException20 = ListedLicenses.getListedLicenses().getListedExceptionByIdCompatV2("Classpath-exception-2.0").getLicenseExceptionText();
		String gpl20WithClasspathException20 = gpl20 + "\n\n" + classpathException20;
		String textWithRandomPrefixAndSuffix = "Some random preamble text.\n\n" + classpathException20 + "\n\nSome random epilogue text.";

		List<String> expectedResultEmpty = Arrays.asList();
		List<String> expectedResultClasspathException20 = Arrays.asList("Classpath-exception-2.0");

		// Note: be cautious about adding too many assertions to this test, as LicenseCompareHelper.matchingStandardLicenseExceptionIdsWithinText can have lengthy runtimes
		assertListsEqual(expectedResultEmpty, LicenseCompareHelper.matchingStandardLicenseExceptionIdsWithinText(null));
		assertListsEqual(expectedResultEmpty, LicenseCompareHelper.matchingStandardLicenseExceptionIdsWithinText(""));
		assertListsEqual(expectedResultEmpty, LicenseCompareHelper.matchingStandardLicenseExceptionIdsWithinText("Some random text that isn't a standard license exception"));

		// Tests for the 2-arg version of matchingStandardLicenseExceptionIdsWithinText (which is faster than the 1-arg version)
		assertListsEqual(expectedResultClasspathException20, LicenseCompareHelper.matchingStandardLicenseExceptionIdsWithinText(gpl20WithClasspathException20, Arrays.asList("Classpath-exception-2.0")));

		if (UnitTestHelper.runSlowTests()) {
			assertListsEqual(expectedResultClasspathException20, LicenseCompareHelper.matchingStandardLicenseExceptionIdsWithinText(classpathException20));
			assertListsEqual(expectedResultClasspathException20, LicenseCompareHelper.matchingStandardLicenseExceptionIdsWithinText(gpl20WithClasspathException20));
			assertListsEqual(expectedResultClasspathException20, LicenseCompareHelper.matchingStandardLicenseExceptionIdsWithinText(textWithRandomPrefixAndSuffix));
		}
	}

	public void testRegressionBSDProtection() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(BSD_PROTECTION_TEXT);
        String templateText = UnitTestHelper.fileToText(BSD_PROTECTION_TEMPLATE);
        ExpandedLicensingListedLicense bsdp = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "BSD-Protection", copyManager, true, null)
        		.setName("BSD Protection")
        		.setSimpleLicensingLicenseText(licText)
        		.setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(bsdp, licText);
        assertFalse(diff.isDifferenceFound());
	}
	
    public void testRegressionEUPL12() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(EUPL_1_2_TEXT);
        String templateText = UnitTestHelper.fileToText(EUPL_1_2_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "EUPL1.2", copyManager, true, null)
        		.setName("EUPL 1.2")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        assertFalse(diff.isDifferenceFound());
    }
    
    public void testRegressionGD() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String templateText = UnitTestHelper.fileToText(GD_TEMPLATE);
        List<String> result = LicenseCompareHelper.getNonOptionalLicenseText(templateText, VarTextHandling.ORIGINAL);
        assertEquals(1, result.size());
    }
    
    public void testReplaceComma() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(MULAN_PSL_2_COMMA_TEXT);
        String templateText = UnitTestHelper.fileToText(MULAN_PSL_2_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "MSPL-2.0", copyManager, true, null)
        		.setName("MSPL-2.0")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        assertFalse(diff.isDifferenceFound());
    }
    
    public void testGroffComments() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(GROFF_COMMENTED_VERBATIM_TEXT);
        String templateText = UnitTestHelper.fileToText(VERBATIM_MAN_PAGES_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "Verbatim-man-pages", copyManager, true, null)
        		.setName("Verbatim-man-pages")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        assertFalse(diff.isDifferenceFound());
    }
    
    public void testRegressionPython201() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(PYTHON201_TEXT);
        String templateText = UnitTestHelper.fileToText(PYTHON201_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "PYTHON-2.0.1", copyManager, true, null)
        		.setName("Python 2.0.1")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        assertTrue(diff.isDifferenceFound());
        assertTrue(diff.getDifferenceMessage().contains("dditional text found"));
    }
    
    public void testRegressionSGIB10() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(SGIB_1_0_TEXT);
        String templateText = UnitTestHelper.fileToText(SGIB_1_0_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "SGI-B-1.0", copyManager, true, null)
        		.setName("SGI-B 1.0")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionXDebug() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(XDEBUG_1_03_TEXT);
        String templateText = UnitTestHelper.fileToText(XDEBUG_1_03_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "XDEBUG-1.03", copyManager, true, null)
        		.setName("XDEBUG-1.03")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionFTL() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(FTL_TEXT);
        String templateText = UnitTestHelper.fileToText(FTL_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "FTL", copyManager, true, null)
        		.setName("FTL")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionPloyformNC() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(POLYFORM_NC_TEXT);
        String templateText = UnitTestHelper.fileToText(POLYFORM_NC_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "pfnc", copyManager, true, null)
        		.setName("pfnc")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionParity7() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(PARITY7_TEXT);
        String templateText = UnitTestHelper.fileToText(PARITY7_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
        		modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "FTL", copyManager, true, null)
        		.setName("FTL")
        		.setSimpleLicensingLicenseText(licText)
                .setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionAPL10() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(APL_1_TEXT);
        String templateText = UnitTestHelper.fileToText(APL_1_TEMPLATE);
        ExpandedLicensingListedLicense lic = new ExpandedLicensingListedLicense(
                modelStore, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE + "APL-1.0", copyManager, true, null)
                		.setName("APL 1.0")
                		.setSimpleLicensingLicenseText(licText)
                		.setExpandedLicensingStandardLicenseTemplate(templateText);
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
   
    public void testNonOptionalTextToStartPattern() throws InvalidSPDXAnalysisException, SpdxCompareException {
    	String expectedMatch = "This is line 1\nThis is line 2";
    	List<String> noRegexes = Arrays.asList(new String[] {"This is line 1", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToStartPattern(noRegexes, 100).matcher(expectedMatch).matches());
    	
    	List<String> regexMiddle = Arrays.asList(new String[] {"This is~~~.+~~~1", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToStartPattern(regexMiddle, 100).matcher(expectedMatch).matches());
    	
    	List<String> regexStart = Arrays.asList(new String[] {"~~~.+~~~is line 1", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToStartPattern(regexStart, 100).matcher(expectedMatch).matches());
    	
    	List<String> regexEnd = Arrays.asList(new String[] {"This is line~~~.+~~~", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToStartPattern(regexEnd, 100).matcher(expectedMatch).matches());
    	
    	List<String> multipleRegex = Arrays.asList(new String[] {"~~~.+~~~is line~~~.+~~~", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToStartPattern(multipleRegex, 100).matcher(expectedMatch).matches());
    }
}
