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

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ConjunctiveLicenseSet;
import org.spdx.library.model.license.DisjunctiveLicenseSet;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.library.model.license.ListedLicenses;
import org.spdx.library.model.license.ListedLicenseException;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.library.model.license.SpdxNoneLicense;
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
    static final String MIT_2_SPACES = "TestFiles" + File.separator + "MIT2Spaces.txt";
    static final String MIT_TEMPLATE = "TestFiles" + File.separator + "MIT.template.txt";
    static final String BSD_3_CLAUSE_NL = "TestFiles" + File.separator + "BSD-3-Clause-newline.txt";
    static final String BSD_3_CLAUSE_TEMPLATE = "TestFiles" + File.separator + "BSD-3-Clause.template.txt";
    static final String BSD_2_CLAUSE_NL = "TestFiles" + File.separator + "BSD-2-Clause-nl.txt";
    static final String BSD_2_CLAUSE_TEMPLATE = "TestFiles" + File.separator + "BSD-2-Clause.template.txt";
    static final String EPL_2 = "TestFiles" + File.separator + "EPL-2.0.txt";
    static final String EPL_2_TEMPLATE = "TestFiles" + File.separator + "EPL-2.0.template.txt";
    static final String GPL_2_NL = "TestFiles" + File.separator + "GPL-2.0-NL.txt";
    static final String GPL_2_TEMPLATE = "TestFiles" + File.separator + "GPL-2.0-only.template.txt";
    static final String IMAGE_MAGIK_TEMPLATE = "TestFiles" + File.separator + "ImageMagick.template.txt";

	/**
	 * @throws java.lang.Exception
	 */
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	public void tearDown() throws Exception {
	    DefaultModelStore.reset();
	}

	/**
	 * Test method for {@link org.spdx.utility.compare.LicenseCompareHelper#isLicenseTextEquivalent(java.lang.String, java.lang.String)}.
	 */
	public void testLicensesMatch() {
		// equal strings
		String testA = "Now is the time  for all . good. men/to \\come to the aid of their country.";
		boolean result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testA);
		assertTrue(result);
		// b is longer
		String testB = testA + " A bit longer";
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertFalse(result);
		// first parameter is longer
		result = LicenseCompareHelper.isLicenseTextEquivalent(testB, testA);
		assertFalse(result);
		// white space doesn't matter
		String testPart1 = "Now is the time ";
		String testPart2 = " for all good men";
		String whiteSpace = " \t\n\r";
		testA = testPart1 + testPart2;
		testB = testPart1 + whiteSpace + testPart2;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		// trailing white space
		testB = testA + whiteSpace;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		result = LicenseCompareHelper.isLicenseTextEquivalent(testB, testA);
		assertTrue(result);
		// preceeding white space
		testB = whiteSpace + testA;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		result = LicenseCompareHelper.isLicenseTextEquivalent(testB, testA);
		assertTrue(result);
		// case shouldnt matter
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testA.toUpperCase());
		assertTrue(result);
		// punctuation should matter
		testA = testPart1 + testPart2;
		String punctuation = ",";
		testB = testPart1 + punctuation + testPart2;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertFalse(result);
		// dash variations
		testA = testPart1 + "-" + testPart2;
		testB = testPart1 + "\u2013" + testPart2;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		testB = testPart1 + "\u2014" + testPart2;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		testB = testPart1 + "\u2015" + testPart2;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
	}
	
	public void testLicenseMatchCodeComments() {
		String part1 = " now is the time for all good men\n";
		String part2 = "\tto come to the aid ";
		// c style line comment
		String cCommentLine = "//";
		String testA = part1 + part2;
		String testB = cCommentLine + part1 + cCommentLine + part2 + "\n"+ cCommentLine;
		boolean result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		result = LicenseCompareHelper.isLicenseTextEquivalent(testB, testA);
		assertTrue(result);
		// c style multi line
		String startCMulti = "/*";
		String endCMulti = "*/";
		testB = startCMulti + part1 + part2 + endCMulti;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		result = LicenseCompareHelper.isLicenseTextEquivalent(testB, testA);
		assertTrue(result);
		// javaDocs comments
		String startJavaDocs = "/**";
		testB = startJavaDocs + part1 + part2 + endCMulti;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		result = LicenseCompareHelper.isLicenseTextEquivalent(testB, testA);
		assertTrue(result);
		// script line comment
		String scriptLineComment = "#";
		testB = scriptLineComment + part1 + scriptLineComment + part2 + "\n"+ scriptLineComment;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		result = LicenseCompareHelper.isLicenseTextEquivalent(testB, testA);
		assertTrue(result);
	}
	
	public void testLicenseMatchEquivWords() {
		// per cent -> percent
		String part1 = "now is the time for ";
		String testA = part1 + "per cent";
		String testB = part1 + "percent";
		boolean result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		// copyright holder-> copyright owner
		testA = "Copyright holder "+part1;
		testB = "copyright Owner "+ part1;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
		// "license","licence"
		testA = part1 + " license " + part1;
		testB = part1 + " licence " + part1;
		result = LicenseCompareHelper.isLicenseTextEquivalent(testA, testB);
		assertTrue(result);
	}

	public void testLicenseEqualsStdLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		Map<String, String> xlation = new HashMap<>();;
		String licName = "name";
		String licId = "ID1";
		String licText = "Text";
		Collection<String> sourceUrls = new HashSet<String>(Arrays.asList(new String[] {"http://www.sourceauditor.com/licenses"}));
		String notes = "Notes";
		String stdLicNotice = "Notice";
		String template = "Template";
		boolean osiApproved = false;
		boolean fsfLibre = true;
		String licenseTextHtml = "<h1>Text</h1>";
		boolean isDeprecated = false;
		String deprecatedVersion = "";
		SpdxListedLicense lic1 = 
			new SpdxListedLicense(licName, licId, licText, 
					sourceUrls, notes, stdLicNotice, template, osiApproved, 
					fsfLibre, licenseTextHtml, isDeprecated, deprecatedVersion);
		SpdxListedLicense lic2 = 
			new SpdxListedLicense(licName, licId, licText, 
					sourceUrls, notes, stdLicNotice, template, osiApproved, 
					fsfLibre, licenseTextHtml, isDeprecated, deprecatedVersion);
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlation));
		
		// try just changing the text - should still equal since the ID's are equal
		String text2 = "text2";
		SpdxListedLicense lic3 = 
			new SpdxListedLicense(licName, licId, text2, 
					sourceUrls, notes, stdLicNotice, template, osiApproved
					, 
					fsfLibre, licenseTextHtml, isDeprecated, deprecatedVersion);
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic3, xlation));
		// now try a different ID
		String licId2 = "ID2";
		SpdxListedLicense lic4 = 
			new SpdxListedLicense(licName, licId2, licText, 
					sourceUrls, notes, stdLicNotice, template, osiApproved, 
					fsfLibre, licenseTextHtml, isDeprecated, deprecatedVersion);
		assertFalse(LicenseCompareHelper.isLicenseEqual(lic1, lic4, xlation));
	}
	
	public void testLicenseEqualsNonStdLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		Map<String, String> xlation = new HashMap<>();;
		String licId = "ID1";
		String licText = "Text";

		// same license ID's
		ExtractedLicenseInfo lic1 = 
			new ExtractedLicenseInfo(licId, licText);
		ExtractedLicenseInfo lic2 = 
			new ExtractedLicenseInfo(licId, licText);
		xlation.put(licId, licId);
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlation));
		// different license ID, same license
		xlation.clear();
		String licId2 = "id2";
		lic2 = 
			new ExtractedLicenseInfo(licId2, licText);
		xlation.put(licId, licId2);
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlation));
		// different license ID, different license
		String licId3 = "id3";
		lic2 = 
			new ExtractedLicenseInfo(licId3, licId2);
		assertFalse(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlation));
	}
	
	public void testLicenseEqualsConjunctiveLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		String licText = "Text";
		String licId1 = "id1";
		String licId2 = "id2";
		String licId3 = "id3";
		String licId4 = "id4";
		String licId5 = "id5";
		String licId6 = "id6";
		Map<String, String> xlation = new HashMap<>();;
		ExtractedLicenseInfo lic1 = new ExtractedLicenseInfo(licId1, licText);
		ExtractedLicenseInfo lic2 = new ExtractedLicenseInfo(licId2, licText);
		ExtractedLicenseInfo lic3 = new ExtractedLicenseInfo(licId3, licText);
		ExtractedLicenseInfo lic4 = new ExtractedLicenseInfo(licId4, licText);
		ExtractedLicenseInfo lic5 = new ExtractedLicenseInfo(licId5, licText);
		ExtractedLicenseInfo lic6 = new ExtractedLicenseInfo(licId6, licText);
		xlation.put(licId1, licId4);
		xlation.put(licId2, licId5);
		xlation.put(licId3, licId6);
		Collection<AnyLicenseInfo> set1 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic1, lic2, lic3
		}));
		Collection<AnyLicenseInfo> set2 =  new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic4, lic5, lic6
		}));
		ConjunctiveLicenseSet conj1 = new ConjunctiveLicenseSet();
		conj1.setMembers(set1);
		ConjunctiveLicenseSet conj2 = new ConjunctiveLicenseSet();
		conj2.setMembers(set2);
		
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj1, conj2, xlation));
		// different order
		set2 =  new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic5, lic6, lic4
		}));
		conj2 = new ConjunctiveLicenseSet();
		conj2.setMembers(set2);
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj1, conj2, xlation));

		String licId7 = "id7";
		ExtractedLicenseInfo lic7 = new ExtractedLicenseInfo(licId7, licText);
		Collection<AnyLicenseInfo> set3 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic4, lic5, lic7
		}));
		ConjunctiveLicenseSet conj3 = new ConjunctiveLicenseSet();
		conj3.setMembers(set3);
		assertFalse(LicenseCompareHelper.isLicenseEqual(conj1, conj3, xlation));		
	}		
	
	public void testLicenseEqualsConjunctiveLicenseDifferentOrder() throws InvalidSPDXAnalysisException, SpdxCompareException {
		String licText = "Text";
		String licId1 = "id1";
		String licId2 = "id2";
		String licId3 = "id3";
		String licId4 = "id4";
		String licId5 = "id5";
		String licId6 = "id6";
		Map<String, String> xlation = new HashMap<>();;
		ExtractedLicenseInfo lic1 = new ExtractedLicenseInfo(licId1, licText);
		ExtractedLicenseInfo lic2 = new ExtractedLicenseInfo(licId2, licText);
		ExtractedLicenseInfo lic3 = new ExtractedLicenseInfo(licId3, licText);
		ExtractedLicenseInfo lic4 = new ExtractedLicenseInfo(licId4, licText);
		ExtractedLicenseInfo lic5 = new ExtractedLicenseInfo(licId5, licText);
		ExtractedLicenseInfo lic6 = new ExtractedLicenseInfo(licId6, licText);
		xlation.put(licId1, licId4);
		xlation.put(licId2, licId5);
		xlation.put(licId3, licId6);
		Collection<AnyLicenseInfo> set1 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic1, lic2, lic3
		}));
		Collection<AnyLicenseInfo> set2 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic4, lic6, lic5
		}));
		ConjunctiveLicenseSet conj1 = new ConjunctiveLicenseSet();
		conj1.setMembers(set1);
		ConjunctiveLicenseSet conj2 = new ConjunctiveLicenseSet();
		conj2.setMembers(set2);
		
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj1, conj2, xlation));
	
		// busybox-1.rdf: (LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-3 AND LicenseRef-12 AND LicenseRef-4 AND LicenseRef-13 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND LicenseRef-7 AND LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND GPL-2.0+ AND GPL-2.0 AND LicenseRef-17 AND LicenseRef-16 AND BSD-2-Clause-Clear)
		xlation.clear();
		String licIdRef14 = "LicenseRef-14";
		ExtractedLicenseInfo licref14 = new ExtractedLicenseInfo(licIdRef14, licText);
		xlation.put(licIdRef14, licIdRef14);
		String licIdRef5 = "LicenseRef-5";
		ExtractedLicenseInfo licref5 = new ExtractedLicenseInfo(licIdRef5, licText);
		xlation.put(licIdRef5, licIdRef5);
		String licIdref6 = "LicenseRef-6";
		ExtractedLicenseInfo licref6 = new ExtractedLicenseInfo(licIdref6, licText);
		xlation.put(licIdref6, licIdref6);
		String licIdRef15 = "LicenseRef-15";
		ExtractedLicenseInfo licref15 = new ExtractedLicenseInfo(licIdRef15, licText);
		xlation.put(licIdRef15, licIdRef15);
		String licIdRef3 = "LicenseRef-3";
		ExtractedLicenseInfo licref3 = new ExtractedLicenseInfo(licIdRef3, licText);
		xlation.put(licIdRef3, licIdRef3);
		String licIdRef12 = "LicenseRef-12";
		ExtractedLicenseInfo licref12 = new ExtractedLicenseInfo(licIdRef12, licText);
		xlation.put(licIdRef12, licIdRef12);
		String licIdRef4 = "LicenseRef-4";
		ExtractedLicenseInfo licref4 = new ExtractedLicenseInfo(licIdRef4, licText);
		xlation.put(licIdRef4, licIdRef4);
		String licIdRef13 = "LicenseRef-13";
		ExtractedLicenseInfo licref13 = new ExtractedLicenseInfo(licIdRef13, licText);
		xlation.put(licIdRef13, licIdRef13);
		String licIdref10 = "LicenseRef-10";
		ExtractedLicenseInfo licref10 = new ExtractedLicenseInfo(licIdref10, licText);
		xlation.put(licIdref10, licIdref10);
		String licIdRef9 = "LicenseRef-9";
		ExtractedLicenseInfo licref9 = new ExtractedLicenseInfo(licIdRef9, licText);
		xlation.put(licIdRef9, licIdRef9);
		String licIdRef11 = "LicenseRef-11";
		ExtractedLicenseInfo licref11 = new ExtractedLicenseInfo(licIdRef11, licText);
		xlation.put(licIdRef11, licIdRef11);
		String licIdRef7 = "LicenseRef-7";
		ExtractedLicenseInfo licref7 = new ExtractedLicenseInfo(licIdRef7, licText);
		xlation.put(licIdRef7, licIdRef7);
		String licIdRef8 = "LicenseRef-8";
		ExtractedLicenseInfo licref8 = new ExtractedLicenseInfo(licIdRef8, licText);
		xlation.put(licIdRef8, licIdRef8);
		String licLGPLPlusId = "LGPL-2.1+";
		SpdxListedLicense licLGPLPlus = LicenseInfoFactory.getListedLicenseById(licLGPLPlusId);
		String licRef1 = "LicenseRef-1";
		ExtractedLicenseInfo licref1 = new ExtractedLicenseInfo(licRef1, licText);
		xlation.put(licRef1, licRef1);
		String licRef2 = "LicenseRef-2";
		ExtractedLicenseInfo licref2 = new ExtractedLicenseInfo(licRef2, licText);
		xlation.put(licRef2, licRef2);
		String licRef0 = "LicenseRef-0";
		ExtractedLicenseInfo licref0 = new ExtractedLicenseInfo(licRef0, licText);
		xlation.put(licRef0, licRef0);
		String licGPL20PlusId = "GPL-2.0+";
		SpdxListedLicense licGPL20Plus = LicenseInfoFactory.getListedLicenseById(licGPL20PlusId);
		String licGPL20id = "GPL-2.0";
		SpdxListedLicense licGPL20 = LicenseInfoFactory.getListedLicenseById(licGPL20id);
		String licRef17 = "LicenseRef-17";
		ExtractedLicenseInfo licref17 = new ExtractedLicenseInfo(licRef17, licText);
		xlation.put(licRef17, licRef17);
		String licRef16 = "LicenseRef-16";
		ExtractedLicenseInfo licref16 = new ExtractedLicenseInfo(licRef16, licText);
		xlation.put(licRef16, licRef16);
		String licRefBSD2Clearid = "BSD-2-Clause";
		SpdxListedLicense licRefBSD2Clear = LicenseInfoFactory.getListedLicenseById(licRefBSD2Clearid);
		// busybox-1.rdf: (LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-3 AND 
		//LicenseRef-12 AND LicenseRef-4 AND LicenseRef-13 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND 
		//LicenseRef-7 AND LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND 
		//GPL-2.0+ AND GPL-2.0 AND LicenseRef-17 AND LicenseRef-16 AND BSD-2-Clause-Clear)

		Collection<AnyLicenseInfo> bbset1 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {licref14, licref5, licref6, licref15, licref3, licref12, licref4, 
				licref13,licref10, licref9, licref11, licref7, licref8, licLGPLPlus, licref1, licref2, licref0, licGPL20Plus,
				licGPL20, licref17, licref16, licRefBSD2Clear
		}));
		ConjunctiveLicenseSet bbconj1 = new ConjunctiveLicenseSet();
		bbconj1.setMembers(bbset1);
		// busybox-2.rdf: (LicenseRef-14 AND LicenseRef-5 AND LicenseRef-6 AND LicenseRef-15 AND LicenseRef-12 AND LicenseRef-3
		//AND LicenseRef-13 AND LicenseRef-4 AND LicenseRef-10 AND LicenseRef-9 AND LicenseRef-11 AND LicenseRef-7 AND 
		//LicenseRef-8 AND LGPL-2.1+ AND LicenseRef-1 AND LicenseRef-2 AND LicenseRef-0 AND GPL-2.0+ AND GPL-2.0 AND 
		//LicenseRef-17 AND BSD-2-Clause-Clear AND LicenseRef-16)

		Collection<AnyLicenseInfo> bbset2 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {licref14, licref5, licref6, licref15, licref12, licref3, licref13,
				licref4, licref10, licref9, licref11, licref7, licref8, licLGPLPlus, licref1, licref2, licref0, licGPL20Plus,
				licGPL20, licref17, licRefBSD2Clear, licref16
		}));
		ConjunctiveLicenseSet bbconj2 = new ConjunctiveLicenseSet();
		bbconj2.setMembers(bbset2);
		assertTrue(LicenseCompareHelper.isLicenseEqual(bbconj1, bbconj2, xlation));
		assertTrue(LicenseCompareHelper.isLicenseEqual(bbconj2, bbconj1, xlation));
	}	
	
	public void testLicenseEqualsDisjunctiveLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		String licText = "Text";
		String licId1 = "id1";
		String licId2 = "id2";
		String licId3 = "id3";
		String licId4 = "id4";
		String licId5 = "id5";
		String licId6 = "id6";
		Map<String, String> xlation = new HashMap<>();;
		ExtractedLicenseInfo lic1 = new ExtractedLicenseInfo(licId1, licText);
		ExtractedLicenseInfo lic2 = new ExtractedLicenseInfo(licId2, licText);
		ExtractedLicenseInfo lic3 = new ExtractedLicenseInfo(licId3, licText);
		ExtractedLicenseInfo lic4 = new ExtractedLicenseInfo(licId4, licText);
		ExtractedLicenseInfo lic5 = new ExtractedLicenseInfo(licId5, licText);
		ExtractedLicenseInfo lic6 = new ExtractedLicenseInfo(licId6, licText);
		xlation.put(licId1, licId4);
		xlation.put(licId2, licId5);
		xlation.put(licId3, licId6);
		Collection<AnyLicenseInfo> set1 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic1, lic2, lic3
		}));
		Collection<AnyLicenseInfo> set2 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic4, lic5, lic6
		}));
		DisjunctiveLicenseSet conj1 = new DisjunctiveLicenseSet();
		conj1.setMembers(set1);
		DisjunctiveLicenseSet conj2 = new DisjunctiveLicenseSet();
		conj2.setMembers(set2);
		
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj1, conj2, xlation));
		
		String licId7 = "id7";
		ExtractedLicenseInfo lic7 = new ExtractedLicenseInfo(licId7, licText);
		Collection<AnyLicenseInfo> set3 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic4, lic5, lic7
		}));
		DisjunctiveLicenseSet conj3 = new DisjunctiveLicenseSet();
		conj3.setMembers(set3);
		assertFalse(LicenseCompareHelper.isLicenseEqual(conj1, conj3, xlation));
	}	
	
	public void testLicenseEqualsComplexLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		String licText = "Text";
		String licId1 = "id1";
		String licId2 = "id2";
		String licId3 = "id3";
		String licId4 = "id4";
		String licId5 = "id5";
		String licId6 = "id6";
		Map<String, String> xlation = new HashMap<>();;
		ExtractedLicenseInfo lic1 = new ExtractedLicenseInfo(licId1, licText);
		ExtractedLicenseInfo lic2 = new ExtractedLicenseInfo(licId2, licText);
		ExtractedLicenseInfo lic3 = new ExtractedLicenseInfo(licId3, licText);
		ExtractedLicenseInfo lic4 = new ExtractedLicenseInfo(licId4, licText);
		ExtractedLicenseInfo lic5 = new ExtractedLicenseInfo(licId5, licText);
		ExtractedLicenseInfo lic6 = new ExtractedLicenseInfo(licId6, licText);
		xlation.put(licId1, licId4);
		xlation.put(licId2, licId5);
		xlation.put(licId3, licId6);
		Collection<AnyLicenseInfo> set1 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic1, lic2
		}));
		Collection<AnyLicenseInfo> set2 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic4, lic5
		}));
		DisjunctiveLicenseSet conj1 = new DisjunctiveLicenseSet();
		conj1.setMembers(set1);
		DisjunctiveLicenseSet conj2 = new DisjunctiveLicenseSet();
		conj2.setMembers(set2);
		
		Collection<AnyLicenseInfo> set3 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				conj1, lic3
		}));
		Collection<AnyLicenseInfo> set4 = new HashSet<AnyLicenseInfo>(Arrays.asList(new AnyLicenseInfo[] {
				lic6, conj2
		}));
		ConjunctiveLicenseSet conj3 = new ConjunctiveLicenseSet();
		conj3.setMembers(set3);
		ConjunctiveLicenseSet conj4 = new ConjunctiveLicenseSet();
		conj4.setMembers(set4);
		
		assertTrue(LicenseCompareHelper.isLicenseEqual(conj3, conj4, xlation));
	}	
	
	public void testLicenseEqualsNoAsserLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxNoAssertionLicense lic1 = new SpdxNoAssertionLicense();
		SpdxNoAssertionLicense lic2 = new SpdxNoAssertionLicense();
		SpdxNoneLicense lic3 = new SpdxNoneLicense();
		Map<String, String> xlationMap = new HashMap<>();;
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic1, lic2, xlationMap));
		assertFalse(LicenseCompareHelper.isLicenseEqual(lic1, lic3, xlationMap));
	}	
	
	public void testLicenseEqualsNoneLicense() throws InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxNoAssertionLicense lic2 = new SpdxNoAssertionLicense();
		SpdxNoneLicense lic3 = new SpdxNoneLicense();
		SpdxNoneLicense lic4 = new SpdxNoneLicense();
		Map<String, String> xlationMap = new HashMap<>();;
		assertTrue(LicenseCompareHelper.isLicenseEqual(lic3, lic4, xlationMap));
		assertFalse(LicenseCompareHelper.isLicenseEqual(lic4, lic2, xlationMap));
	}	
	
	public void testTokenizeLicenseText() {
		String test = "Now is the.time,for? \"all\" good men.";
		Map<Integer, LineColumn> tokenToLocation = new HashMap<Integer, LineColumn>();
		String[] result = LicenseCompareHelper.tokenizeLicenseText(test, tokenToLocation);
		assertEquals(14,result.length);
		assertEquals("now",result[0]);
		assertEquals("is",result[1]);
		assertEquals("the",result[2]);
		assertEquals(".",result[3]);
		assertEquals("time",result[4]);
		assertEquals(",",result[5]);
		assertEquals("for",result[6]);
		assertEquals("?",result[7]);
		assertEquals("\"",result[8]);
		assertEquals("all",result[9]);
		assertEquals("\"",result[10]);
		assertEquals("good",result[11]);
		assertEquals("men",result[12]);
		assertEquals(".",result[13]);
		assertEquals(0,tokenToLocation.get(0).getColumn());
		assertEquals(4,tokenToLocation.get(1).getColumn());
		assertEquals(7,tokenToLocation.get(2).getColumn());
		assertEquals(10,tokenToLocation.get(3).getColumn());
		assertEquals(11,tokenToLocation.get(4).getColumn());
		assertEquals(15,tokenToLocation.get(5).getColumn());
		assertEquals(16,tokenToLocation.get(6).getColumn());
		assertEquals(19,tokenToLocation.get(7).getColumn());
		assertEquals(21,tokenToLocation.get(8).getColumn());
		assertEquals(22,tokenToLocation.get(9).getColumn());
		assertEquals(25,tokenToLocation.get(10).getColumn());
		assertEquals(27,tokenToLocation.get(11).getColumn());
		assertEquals(32,tokenToLocation.get(12).getColumn());
		assertEquals(35,tokenToLocation.get(13).getColumn());
	}
	
	public void regressionTokenString() {
		String test = "THIS SOFTWARE IS PROVIDED BY COPYRIGHT HOLDER \"AS IS\" AND";
		Map<Integer, LineColumn> tokenToLocation = new HashMap<Integer, LineColumn>();
		String[] result = LicenseCompareHelper.tokenizeLicenseText(test, tokenToLocation);
		assertEquals(11, result.length);
		assertEquals("this",result[0]);
		assertEquals("software",result[1]);
		assertEquals("is",result[2]);
		assertEquals("provided",result[3]);
		assertEquals("by",result[4]);
		assertEquals("copyright-holder",result[5]);
		assertEquals("\"",result[6]);
		assertEquals("as",result[7]);
		assertEquals("is",result[8]);
		assertEquals("\"",result[9]);
		assertEquals("and",result[10]);
		assertEquals(0,tokenToLocation.get(0).getColumn());
		assertEquals(5,tokenToLocation.get(1).getColumn());
		assertEquals(14,tokenToLocation.get(2).getColumn());
		assertEquals(17,tokenToLocation.get(3).getColumn());
		assertEquals(26,tokenToLocation.get(4).getColumn());
		assertEquals(29,tokenToLocation.get(5).getColumn());
		assertEquals(46,tokenToLocation.get(6).getColumn());
		assertEquals(47,tokenToLocation.get(7).getColumn());
		assertEquals(50,tokenToLocation.get(8).getColumn());
		assertEquals(52,tokenToLocation.get(9).getColumn());
		assertEquals(54,tokenToLocation.get(10).getColumn());
	}
	
	public void testOddChars() {
		String test = "COPYRIGHT   I B M   CORPORATION 2002";
		Map<Integer, LineColumn> tokenToLocation = new HashMap<Integer, LineColumn>();
		String[] result = LicenseCompareHelper.tokenizeLicenseText(test, tokenToLocation);
		assertEquals(6,result.length);
		assertEquals("copyright", result[0]);
		assertEquals("i", result[1]);
		assertEquals("b", result[2]);
		assertEquals("m", result[3]);
		assertEquals("corporation", result[4]);
		assertEquals("2002", result[5]);
		test = "Claims      If";
		result = LicenseCompareHelper.tokenizeLicenseText(test, tokenToLocation);
		assertEquals(2, result.length);
		assertEquals("claims",result[0]);
		assertEquals("if", result[1]);	
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

	
	public void testSpaceNormalization() {
		String t1 = "This is a test of space extra";
		String t2 = "This is\u2060a\u2007test\u202Fof space\u2009extra";
		assertTrue(LicenseCompareHelper.isLicenseTextEquivalent(t1, t2));
	}
	
	   public void testCommaNormalization() {
	        String t1 = "This, is, a,test , of commas";
	        String t2 = "This\uFE10 is\uFF0C a\uFE50test , of commas";
	        assertTrue(LicenseCompareHelper.isLicenseTextEquivalent(t1, t2));
	    }
	
	public void testIsTextStandardLicenseGpl3() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		SpdxListedLicense gpl3 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-3.0");
		String compareText = UnitTestHelper.fileToText(GPL_3_TEXT);
		DifferenceDescription result = LicenseCompareHelper.isTextStandardLicense(gpl3, compareText);
		assertFalse(result.isDifferenceFound());
	}
	
	public void testIsTextStandardLicenseComments() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		SpdxListedLicense bsd = ListedLicenses.getListedLicenses().getListedLicenseById("0BSD");
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new StringReader(bsd.getLicenseText()));
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
			reader = new BufferedReader(new StringReader(bsd.getLicenseText()));
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
		SpdxListedLicense gpl30 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-3.0");
		SpdxListedLicense apache10 = ListedLicenses.getListedLicenses().getListedLicenseById("Apache-1.0");
		SpdxListedLicense apache20 = ListedLicenses.getListedLicenses().getListedLicenseById("Apache-2.0");
		SpdxListedLicense mplLicense = ListedLicenses.getListedLicenses().getListedLicenseById("MPL-2.0");
		String multiLicenseText = gpl30.getLicenseText() + "\n\n----------\n\n" + apache20.getLicenseText();
		String textWithRandomPrefixAndSuffix = "Some random preamble text.\n\n" + apache20.getLicenseText() + "\n\nSome random epilogue text.";

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
		SpdxListedLicense cddl11 = ListedLicenses.getListedLicenses().getListedLicenseById("CDDL-1.1");
		SpdxListedLicense gpl20 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-2.0");
		String javaMailLicense = UnitTestHelper.urlToText("https://raw.githubusercontent.com/javaee/javamail/master/LICENSE.txt");

		assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(javaMailLicense, cddl11));
		assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(javaMailLicense, gpl20));
		assertFalse(LicenseCompareHelper.isStandardLicenseWithinText(javaMailLicense, apache20));
 */
	}
	
	public void testIsStandardLicenseExceptionWithinText() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		ListedLicenseException classpath20 = ListedLicenses.getListedLicenses().getListedExceptionById("Classpath-exception-2.0");
		String gpl20 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-2.0").getLicenseText();
		String classpathException20 = classpath20.getLicenseExceptionText();
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
		String gpl30 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-3.0").getLicenseText();
		String apache20 = ListedLicenses.getListedLicenses().getListedLicenseById("Apache-2.0").getLicenseText();
		String multiLicenseText = gpl30 + "\n\n----------\n\n" + apache20;
		String textWithRandomPrefixAndSuffix = "Some random preamble text.\n\n" + apache20 + "\n\nSome random epilogue text.";
		String aladdin = ListedLicenses.getListedLicenses().getListedLicenseById("Aladdin").getLicenseText();
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
		String gpl20 = ListedLicenses.getListedLicenses().getListedLicenseById("GPL-2.0").getLicenseText();
		String classpathException20 = ListedLicenses.getListedLicenses().getListedExceptionById("Classpath-exception-2.0").getLicenseExceptionText();
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
        SpdxListedLicense bsdp = new SpdxListedLicense(
                new SpdxListedLicense.Builder("BSD-Protection", "BSD Protection", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(bsdp, licText);
        assertFalse(diff.isDifferenceFound());
	}
	
    public void testRegressionEUPL12() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(EUPL_1_2_TEXT);
        String templateText = UnitTestHelper.fileToText(EUPL_1_2_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("EUPL1.2", "EUPL 1.2", licText)
                .setTemplate(templateText));
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
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("MSPL-2.0", "MSPL-2.0", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        assertFalse(diff.isDifferenceFound());
    }
    
    public void testGroffComments() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(GROFF_COMMENTED_VERBATIM_TEXT);
        String templateText = UnitTestHelper.fileToText(VERBATIM_MAN_PAGES_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("Verbatim-man-pages", "Verbatim-man-pages", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        assertFalse(diff.isDifferenceFound());
    }
    
    public void testRegressionPython201() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(PYTHON201_TEXT);
        String templateText = UnitTestHelper.fileToText(PYTHON201_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("PYTHON-2.0.1", "Python 2.0.1", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        assertTrue(diff.isDifferenceFound());
        assertTrue(diff.getDifferenceMessage().contains("dditional text found"));
    }
    
    public void testRegressionSGIB10() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(SGIB_1_0_TEXT);
        String templateText = UnitTestHelper.fileToText(SGIB_1_0_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("SGI-B-1.0", "SGI-B 1.0", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionXDebug() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(XDEBUG_1_03_TEXT);
        String templateText = UnitTestHelper.fileToText(XDEBUG_1_03_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("XDEBUG-1.03", "XDEBUG-1.03", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionFTL() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(FTL_TEXT);
        String templateText = UnitTestHelper.fileToText(FTL_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("FTL", "FTL", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionPloyformNC() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(POLYFORM_NC_TEXT);
        String templateText = UnitTestHelper.fileToText(POLYFORM_NC_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("pfnc", "pfnc", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionParity7() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(PARITY7_TEXT);
        String templateText = UnitTestHelper.fileToText(PARITY7_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("FTL", "FTL", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testRegressionAPL10() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(APL_1_TEXT);
        String templateText = UnitTestHelper.fileToText(APL_1_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("APL-1.0", "APL 1.0", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
   
    public void testNonOptionalTextToStartPattern() throws InvalidSPDXAnalysisException, SpdxCompareException {
    	//TODO: Fix this up for the new return value
    	String expectedMatch = "This is line 1\nThis is line 2";
    	List<String> noRegexes = Arrays.asList(new String[] {"This is line 1", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToPatterns(noRegexes, 100).getLeft().matcher(expectedMatch).matches());
    	
    	List<String> regexMiddle = Arrays.asList(new String[] {"This is~~~.+~~~1", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToPatterns(regexMiddle, 100).getLeft().matcher(expectedMatch).matches());
    	
    	List<String> regexStart = Arrays.asList(new String[] {"~~~.+~~~is line 1", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToPatterns(regexStart, 100).getLeft().matcher(expectedMatch).matches());
    	
    	List<String> regexEnd = Arrays.asList(new String[] {"This is line~~~.+~~~", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToPatterns(regexEnd, 100).getLeft().matcher(expectedMatch).matches());
    	
    	List<String> multipleRegex = Arrays.asList(new String[] {"~~~.+~~~is line~~~.+~~~", "This is line 2"});
    	assertTrue(LicenseCompareHelper.nonOptionalTextToPatterns(multipleRegex, 100).getLeft().matcher(expectedMatch).matches());
    }
    
    public void test2Spaces() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(MIT_2_SPACES);
        String templateText = UnitTestHelper.fileToText(MIT_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("MIT", "MIT", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }

    public void testBsdNewLine() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(BSD_3_CLAUSE_NL);
        String templateText = UnitTestHelper.fileToText(BSD_3_CLAUSE_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("BSD-3-Clause", "BSD-3-Clause", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
    }
    
    public void testConsistentMatch() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(BSD_2_CLAUSE_NL);
        String templateText = UnitTestHelper.fileToText(BSD_2_CLAUSE_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("BSD-2-Clause", "BSD-2-Clause", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
        assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(licText, lic));
    }
    
    public void testEpl20ConsistentMatch() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(EPL_2);
        String templateText = UnitTestHelper.fileToText(EPL_2_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("EPL-2.0", "EPL-2.0", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
        assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(licText, lic));
    }
    
    public void testGpl20ConsistentMatch() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(GPL_2_TEXT);
        String templateText = UnitTestHelper.fileToText(GPL_2_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("GPL-2.0", "GPL-2.0", licText)
                .setTemplate(templateText));
        DifferenceDescription diff = LicenseCompareHelper.isTextStandardLicense(lic, licText);
        if (diff.isDifferenceFound()) {
        	fail(diff.getDifferenceMessage());
        }
        assertTrue(LicenseCompareHelper.isStandardLicenseWithinText(licText, lic));
    }
    
    public void testImageMagikTextWithin() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
        String licText = UnitTestHelper.fileToText(MPL_2_FROM_MOZILLA_FILE);
        String templateText = UnitTestHelper.fileToText(IMAGE_MAGIK_TEMPLATE);
        SpdxListedLicense lic = new SpdxListedLicense(
                new SpdxListedLicense.Builder("imageMagik", "imageMagik", licText)
                .setTemplate(templateText));
        assertFalse(LicenseCompareHelper.isStandardLicenseWithinText(licText, lic));
    }
    
}
