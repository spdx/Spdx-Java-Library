/**
 * Copyright (c) 2020 Source Auditor Inc.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.spdx.licenseTemplate.ILicenseTemplateOutputHandler;
import org.spdx.licenseTemplate.LicenseTemplateRule;
import org.spdx.licenseTemplate.LicenseTextHelper;

/**
 * @deprecated The <code>TemplateRegexMatcher</code> class should be used in place of this class.  This class will be removed in the next major release.
 * 
 * Filter the template output to create a list of strings filtering out optional and/or var text
 * @author Gary O'Neall
 *
 */
@Deprecated
public class FilterTemplateOutputHandler implements ILicenseTemplateOutputHandler {
	
	public static final String REGEX_ESCAPE = "~~~";
	public enum VarTextHandling {
		OMIT,		// Omit the var text all together
		ORIGINAL,	// Include the original text for the regex
		REGEX,		// Include the regex itself included by the REGEX_ESCAPE strings
	}
	
	public enum OptionalTextHandling {
		OMIT,		// Omit the optional text
		ORIGINAL,	// Retain the optional text
		REGEX_USING_TOKENS		// Create a regex for the optional text with the REGEX_ESCAPE string tokenizing the words
	}
	
	private VarTextHandling varTextHandling;
	private OptionalTextHandling optionalTextHandling;
	private List<String> filteredText = new ArrayList<>();
	StringBuilder currentString = new StringBuilder();
	private int optionalDepth = 0;	// depth of optional rules
	private Map<Integer, List<String>> optionalTokens = new HashMap<>(); // map of optional dept to a list of tokens for the optional text

	/**
	 * @param includeVarText if true, include the default variable text
	 */
	@Deprecated
	public FilterTemplateOutputHandler(boolean includeVarText) {
		this(includeVarText ? VarTextHandling.ORIGINAL : VarTextHandling.OMIT, OptionalTextHandling.OMIT);
	}
	
	/**
	 * @param varTextHandling include original, exclude, or include the regex (enclosed with "~~~") for "var" text
	 */
	public FilterTemplateOutputHandler(VarTextHandling varTextHandling) {
		this(varTextHandling, OptionalTextHandling.OMIT);
	}
	
	/**
	 * @param varTextHandling include original, exclude, or include the regex (enclosed with "~~~") for "var" text
	 * @param optionalTextHandling include optional text, exclude, or include a regex for the optional text
	 */
	public FilterTemplateOutputHandler(VarTextHandling varTextHandling, OptionalTextHandling optionalTextHandling) {
		this.varTextHandling = varTextHandling;
		this.optionalTextHandling = optionalTextHandling;
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#text(java.lang.String)
	 */
	@Override
	public void text(String text) {
		if (optionalDepth <= 0 || OptionalTextHandling.ORIGINAL.equals(optionalTextHandling)) {
			currentString.append(text);
		} else if (OptionalTextHandling.REGEX_USING_TOKENS.equals(optionalTextHandling)) {
			optionalTokens.get(optionalDepth).addAll(Arrays.asList(
					LicenseTextHelper.tokenizeLicenseText(text, new HashMap<>())));
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#variableRule(org.spdx.licenseTemplate.LicenseTemplateRule)
	 */
	@Override
	public void variableRule(LicenseTemplateRule rule) {
		if (VarTextHandling.REGEX.equals(varTextHandling) && optionalDepth <= 0) {
			currentString.append(REGEX_ESCAPE);
			currentString.append('(');
			currentString.append(rule.getMatch());
			currentString.append(')');
			currentString.append(REGEX_ESCAPE);
		} else if (VarTextHandling.ORIGINAL.equals(varTextHandling) && optionalDepth <= 0) {
			currentString.append(rule.getOriginal());
		} else if (optionalDepth > 0 && OptionalTextHandling.REGEX_USING_TOKENS.equals(optionalTextHandling)) {
			currentString.append('(');
			currentString.append(rule.getMatch());
			currentString.append(')');
		} else {
			if (currentString.length() > 0) {
				filteredText.add(currentString.toString());
				currentString.setLength(0);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#beginOptional(org.spdx.licenseTemplate.LicenseTemplateRule)
	 */
	@Override
	public void beginOptional(LicenseTemplateRule rule) {
		if (OptionalTextHandling.REGEX_USING_TOKENS.equals(optionalTextHandling)) {
			if (optionalDepth == 0) {
				if (currentString.length() > 0) {
					filteredText.add(currentString.toString());
					currentString.setLength(0);
				}
				currentString.append(REGEX_ESCAPE);
			} else {
				currentString.append(toTokenRegex(optionalTokens.get(optionalDepth)));
				optionalTokens.get(optionalDepth).clear();
			}
			currentString.append('(');
		} else if (currentString.length() > 0) {
			filteredText.add(currentString.toString());
			currentString.setLength(0);
		}
		optionalDepth++;
		optionalTokens.put(optionalDepth, new ArrayList<>());
	}

	/**
	 * @return regular expression with quoted tokens
	 */
	private String toTokenRegex(List<String> tokens) {
		StringBuilder sb = new StringBuilder();
		for (String token:tokens) {
			token = token.trim();
			if (LicenseTextHelper.NORMALIZE_TOKENS.containsKey(token.toLowerCase())) {
				token = LicenseTextHelper.NORMALIZE_TOKENS.get(token.toLowerCase());
			}
			sb.append(Pattern.quote(token));
			sb.append("\\s*");
		}
		return sb.toString();
	}


	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#endOptional(org.spdx.licenseTemplate.LicenseTemplateRule)
	 */
	@Override
	public void endOptional(LicenseTemplateRule rule) {
		if (OptionalTextHandling.REGEX_USING_TOKENS.equals(optionalTextHandling)) {
			currentString.append(toTokenRegex(optionalTokens.get(optionalDepth)));
			currentString.append(")?");
			if (optionalDepth == 1) {
				currentString.append(REGEX_ESCAPE);
				filteredText.add(currentString.toString());
				currentString.setLength(0);
			}
		} else if (currentString.length() > 0) {
			filteredText.add(currentString.toString());
			currentString.setLength(0);
		}
		optionalTokens.remove(optionalDepth);
		optionalDepth--;
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#completeParsing()
	 */
	@Override
	public void completeParsing() {
		if (currentString.length() > 0) {
			filteredText.add(currentString.toString());
			currentString.setLength(0);
		}
	}

	/**
	 * @return the includeVarText
	 */
	public boolean isIncludeVarText() {
		return VarTextHandling.ORIGINAL.equals(varTextHandling);
	}

	/**
	 * @return the varTextHandling
	 */
	public VarTextHandling getVarTextHandling() {
		return varTextHandling;
	}

	/**
	 * @return the filteredText
	 */
	public List<String> getFilteredText() {
		return filteredText;
	}
}
