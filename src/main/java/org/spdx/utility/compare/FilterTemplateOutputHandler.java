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
import java.util.List;

import org.spdx.licenseTemplate.ILicenseTemplateOutputHandler;
import org.spdx.licenseTemplate.LicenseTemplateRule;

/**
 * Filter the template output to create a list of strings filtering out optional and/or var text
 * @author Gary O'Neall
 *
 */
public class FilterTemplateOutputHandler implements ILicenseTemplateOutputHandler {
	
	public static final String REGEX_ESCAPE = "~~~";
	public enum VarTextHandling {
		OMIT,		// Omit the var text all together
		ORIGINAL,	// Include the original text for the regex
		REGEX,		// Include the regex itself included by the REGEX_ESCAPE strings
	}
	
	private VarTextHandling varTextHandling;
	private List<String> filteredText = new ArrayList<>();
	StringBuilder currentString = new StringBuilder();
	private int optionalDepth = 0;	// depth of optional rules

	/**
	 * @param includeVarText if true, include the default variable text
	 */
	@Deprecated
	public FilterTemplateOutputHandler(boolean includeVarText) {
		this(includeVarText ? VarTextHandling.ORIGINAL : VarTextHandling.OMIT);
	}
	
	
	/**
	 * @param varTextHandling include original, exclude, or include the regex (enclosed with "~~~") for "var" text
	 */
	public FilterTemplateOutputHandler(VarTextHandling varTextHandling) {
		this.varTextHandling = varTextHandling;
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#text(java.lang.String)
	 */
	@Override
	public void text(String text) {
		if (optionalDepth <= 0) {
			currentString.append(text);
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#variableRule(org.spdx.licenseTemplate.LicenseTemplateRule)
	 */
	@Override
	public void variableRule(LicenseTemplateRule rule) {
		if (VarTextHandling.REGEX.equals(varTextHandling) && optionalDepth <= 0) {
			currentString.append(REGEX_ESCAPE);
			currentString.append(rule.getMatch());
			currentString.append(REGEX_ESCAPE);
		} else if (VarTextHandling.ORIGINAL.equals(varTextHandling) && optionalDepth <= 0) {
			currentString.append(rule.getOriginal());
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
		optionalDepth++;
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#endOptional(org.spdx.licenseTemplate.LicenseTemplateRule)
	 */
	@Override
	public void endOptional(LicenseTemplateRule rule) {
		optionalDepth--;
		if (optionalDepth == 0 && currentString.length() > 0) {
				filteredText.add(currentString.toString());
				currentString.setLength(0);
		}
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
