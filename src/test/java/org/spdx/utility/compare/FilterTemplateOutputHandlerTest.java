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
package org.spdx.utility.compare;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.licenseTemplate.LicenseParserException;
import org.spdx.licenseTemplate.LicenseTemplateRuleException;
import org.spdx.licenseTemplate.SpdxLicenseTemplateHelper;
import org.spdx.utility.compare.FilterTemplateOutputHandler.OptionalTextHandling;
import org.spdx.utility.compare.FilterTemplateOutputHandler.VarTextHandling;

/**
 * @author Gary O'Neall
 */
public class FilterTemplateOutputHandlerTest {
	
	static final String LINE1 = " line 1";
	static final String LINE2 = "line 2 ";
	static final String LINE3 = "line3";
	static final String LINE4 = " line 4 ";
	static final String VAR_ORIGINAL = "var original line 5";
	static final String LINE6 = "line 6";
	static final String LAST_LINE = "last line";
	static final String FIRST_OPTIONAL_TOKEN_REGEX = "~~~(\\Qline\\E\\s*\\Q1\\E\\s*(\\Qline\\E\\s*\\Q2\\E\\s*)?)?~~~";
	static final String LAST_OPTIONAL_TOKEN_REGEX = "~~~(\\Qlast\\E\\s*\\Qline\\E\\s*)?~~~";
	static final String VAR_REGEX = "~~~(,|)~~~";
	static final String TEST_TEMPLATE = "<<beginOptional>>"+LINE1+"\n"
			+"<<beginOptional>>" + LINE2 + "<<endOptional>>\n"
			+ "<<endOptional>>\n"
			+ LINE3 + "\n"
			+ LINE4 + "\n"
			+ "<<var;name=\"incComma\";original=\""+VAR_ORIGINAL+"\";match=\",|\">>  \n"
			+ LINE6 + "\n"
			+ "<<beginOptional>>"+LAST_LINE+"<<endOptional>>";
	static final String GPL_TEMPLATE_SNIPPET_FILE = "TestFiles" + File.separator + "GPL-optional-template.txt";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOriginal() throws LicenseParserException, LicenseTemplateRuleException {
		FilterTemplateOutputHandler filteredOutput = new FilterTemplateOutputHandler(VarTextHandling.ORIGINAL, 
				OptionalTextHandling.ORIGINAL);
		SpdxLicenseTemplateHelper.parseTemplate(TEST_TEMPLATE, filteredOutput);
		List<String> result = filteredOutput.getFilteredText();
		assertEquals(4, result.size());
		assertEquals(LINE1 + "\n", result.get(0));
		assertEquals(LINE2, result.get(1));
		assertEquals("\n" + LINE3 + "\n" +  LINE4 + "\n" + VAR_ORIGINAL + "  \n" + LINE6 + "\n", result.get(2));
		assertEquals(LAST_LINE, result.get(3));
	}
	
	@Test
	public void testOptionalRegex() throws LicenseParserException, LicenseTemplateRuleException {
		FilterTemplateOutputHandler filteredOutput = new FilterTemplateOutputHandler(VarTextHandling.ORIGINAL, 
				OptionalTextHandling.REGEX_USING_TOKENS);
		SpdxLicenseTemplateHelper.parseTemplate(TEST_TEMPLATE, filteredOutput);
		List<String> result = filteredOutput.getFilteredText();
		assertEquals(3, result.size());
		assertEquals(FIRST_OPTIONAL_TOKEN_REGEX, result.get(0));
		assertEquals("\n" + LINE3 + "\n" +  LINE4 + "\n" + VAR_ORIGINAL + "  \n" + LINE6 + "\n", result.get(1));
		assertEquals(LAST_OPTIONAL_TOKEN_REGEX, result.get(2));
	}
	
	@Test
	public void testNoVar() throws LicenseParserException, LicenseTemplateRuleException {
		FilterTemplateOutputHandler filteredOutput = new FilterTemplateOutputHandler(VarTextHandling.OMIT, 
				OptionalTextHandling.ORIGINAL);
		SpdxLicenseTemplateHelper.parseTemplate(TEST_TEMPLATE, filteredOutput);
		List<String> result = filteredOutput.getFilteredText();
		assertEquals(5, result.size());
		assertEquals(LINE1 + "\n", result.get(0));
		assertEquals(LINE2, result.get(1));
		assertEquals("\n" + LINE3 + "\n" +  LINE4 + "\n", result.get(2));
		assertEquals("  \n" + LINE6 + "\n", result.get(3));
		assertEquals(LAST_LINE, result.get(4));
	}
	
	@Test
	public void testNoVarRegex() throws LicenseParserException, LicenseTemplateRuleException {
		FilterTemplateOutputHandler filteredOutput = new FilterTemplateOutputHandler(VarTextHandling.REGEX, 
				OptionalTextHandling.ORIGINAL);
		SpdxLicenseTemplateHelper.parseTemplate(TEST_TEMPLATE, filteredOutput);
		List<String> result = filteredOutput.getFilteredText();
		assertEquals(4, result.size());
		assertEquals(LINE1 + "\n", result.get(0));
		assertEquals(LINE2, result.get(1));
		assertEquals("\n" + LINE3 + "\n" +  LINE4 + "\n" + VAR_REGEX + "  \n" + LINE6 + "\n", result.get(2));
		assertEquals(LAST_LINE, result.get(3));
	}
	
	@Test
	public void testNoOptional() throws LicenseParserException, LicenseTemplateRuleException {
		FilterTemplateOutputHandler filteredOutput = new FilterTemplateOutputHandler(VarTextHandling.ORIGINAL, 
				OptionalTextHandling.OMIT);
		SpdxLicenseTemplateHelper.parseTemplate(TEST_TEMPLATE, filteredOutput);
		List<String> result = filteredOutput.getFilteredText();
		assertEquals(1, result.size());
		assertEquals("\n" + LINE3 + "\n" +  LINE4 + "\n" + VAR_ORIGINAL + "  \n" + LINE6 + "\n", result.get(0));
	}
	
	@Test
	public void testGplRegression() throws LicenseParserException, LicenseTemplateRuleException, IOException {
		FilterTemplateOutputHandler filteredOutput = new FilterTemplateOutputHandler(VarTextHandling.REGEX, 
				OptionalTextHandling.REGEX_USING_TOKENS);
		SpdxLicenseTemplateHelper.parseTemplate(UnitTestHelper.fileToText(GPL_TEMPLATE_SNIPPET_FILE), filteredOutput);
		List<String> result = filteredOutput.getFilteredText();
		assertEquals(2, result.size());
	}

}
