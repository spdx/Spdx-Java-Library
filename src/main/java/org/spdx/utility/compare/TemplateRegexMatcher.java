/**
 * Copyright (c) 2023 Source Auditor Inc.
 * <p>
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
package org.spdx.utility.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.licenseTemplate.ILicenseTemplateOutputHandler;
import org.spdx.licenseTemplate.LicenseParserException;
import org.spdx.licenseTemplate.LicenseTemplateRule;
import org.spdx.licenseTemplate.LicenseTemplateRuleException;
import org.spdx.licenseTemplate.LicenseTextHelper;
import org.spdx.licenseTemplate.SpdxLicenseTemplateHelper;

/**
 * Constructs a regular expression from a license or exception template and provide a matching method
 * to see if code matching the template exists within the text provided
 * <p>
 * Note that the regular expression assumes a fully normalized text string to match
 * <p>
 * <code>isTemplateMatchWithinText(String text)</code> will return true if the text matches the template
 * <p>
 * <code>getQuickMatchRegex()</code> will return a regular expression with limited backtracking which can be used for a quick search
 * <code>getCompleteRegex()</code> will return a regular expression for the entire license where
 * <code>getStartRegex(int wordLimit)</code> will return a regular expression to match the beginning of a license
 * and <code>getEndRegex(int wordLimit)</code> will return a regular expression to match the end of a license
 * 
 * @author Gary O'Neall
 *
 */
public class TemplateRegexMatcher implements ILicenseTemplateOutputHandler {
	
	static final Logger logger = LoggerFactory.getLogger(TemplateRegexMatcher.class);
	
	static final int WORD_LIMIT = 25; // number of words to search for in the quick match, beginning and end of the template
	
	static final String REGEX_GLOBAL_MODIFIERS = "(?im)"; // ignore case and multi-line
	
	interface RegexElement {
	}
	
	static class RegexList implements RegexElement {
		private final List<RegexElement> elements = new ArrayList<>();
		
		public void addElement(RegexElement element) {
			elements.add(element);
		}
		
		public List<RegexElement> getElements() {
			return elements;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (RegexElement element:elements) {
				sb.append(element.toString());
			}
			return sb.toString();
		}
	}
	
	/**
	 * Group with the optional modifier
	 */
	static class OptionalRegexGroup extends RegexList {
		
		@Override
		public String toString() {
			return "(" + super.toString() + ")?";
		}
	}
	
	static class RegexToken implements RegexElement {
		private final String token;
		
		public RegexToken(String token) {
			this.token = token.trim();
		}
		
		public String getToken() {
			return token;
		}
		
		@Override
		public String toString() {
			return Pattern.quote(
					LicenseTextHelper.NORMALIZE_TOKENS.getOrDefault(token.toLowerCase(), token)) + 
					"\\s*";
		}
	}
	
	static class RegexPattern implements RegexElement {
		private String pattern;
		
		public RegexPattern(String pattern) {
			this.pattern = pattern;
		}
		
		public String getPattern() {
			return pattern;
		}
		
		@Override
		public String toString() {
			return "(" + pattern + ")" + "\\s*"; // We always treat the pattern as a group
		}

		/**
		 * @param pattern pattern to set
		 */
		public void setPattern(String pattern) {
			this.pattern = pattern;
		}
	}

	private final String template;
	
	/**
	 * Top level regex
	 */
	private final RegexList regexPatternList = new RegexList();
	
	
	private int optionalNestLevel = 0;
	
	private final List<OptionalRegexGroup> optionalGroups = new ArrayList<>();

	/**
	 * Generates regular expressions from a license or exception template
	 * @throws SpdxCompareException on compare errors
	 */
	public TemplateRegexMatcher(String template) throws SpdxCompareException {
		this.template = template;
		parseTemplate();
	}
	
	/**
	 * Parses the template generating the regular expression
	 * @throws SpdxCompareException on compare errors
	 */
	private void parseTemplate() throws SpdxCompareException {
		try {
			SpdxLicenseTemplateHelper.parseTemplate(LicenseCompareHelper.removeCommentChars(template), this);
		} catch (LicenseTemplateRuleException e) {
			throw new SpdxCompareException("Invalid template rule found during filter: "+e.getMessage(),e);
		} catch (LicenseParserException e) {
			throw new SpdxCompareException("Invalid template found during filter: "+e.getMessage(),e);
		}
	}
	
	/**
	 * @return the complete regular expression for the template
	 */
	public String getCompleteRegex() {
		return REGEX_GLOBAL_MODIFIERS + regexPatternList;
	}
	
	/**
	 * @param wordLimit maximum number of contiguous words to match
	 * @return a regular expression to match the template with minimum backtracking - avoiding optional and var tags
	 */
	public String getQuickMatchRegex(int wordLimit) {
		RegexList result = new RegexList();
		int index = 0;
		int numWords = 0;
		List<RegexElement> elementList = regexPatternList.getElements();
		int largestContiguousText = 0; // number of contiguous tokens in a regular text
		while (index < elementList.size() && numWords <= wordLimit) {
			RegexElement element = elementList.get(index++);
			result.addElement(element);
			if (element instanceof RegexToken) {
				numWords++;
			} else {
				if (numWords > largestContiguousText) {
					largestContiguousText = numWords;
				}
				result.getElements().clear();
				numWords = 0;
			}
		}
		if (numWords < largestContiguousText) {
			// Need to retry to get as much as we can
            //noinspection ConstantValue
            while (index < elementList.size() && numWords <= largestContiguousText) {
				RegexElement element = elementList.get(index++);
				result.addElement(element);
				if (element instanceof RegexToken) {
					numWords++;
				} else {
					result.getElements().clear();
					numWords = 0;
				}
			}
		}
		return REGEX_GLOBAL_MODIFIERS + result;
	}
	
	/**
	 * @param wordLimit number of non-optional words to include in the pattern
	 * @return a regex to match the start of the license per the template
	 */
	public String getStartRegex(int wordLimit) {
		RegexList result = new RegexList();
		int index = 0;
		int numWords = 0;
		List<RegexElement> elementList = regexPatternList.getElements();
		while (index < elementList.size() && numWords <= wordLimit) {
			RegexElement element = elementList.get(index++);
			result.addElement(element);
			if (element instanceof RegexToken) {
				numWords++;
			}
		}
		// Need to check if the string starts with a greedy regex and change to non-greedy
		if (!result.getElements().isEmpty()) {
			RegexElement firstElement = result.getElements().get(0);
			if (firstElement instanceof RegexPattern) {
				String pattern = ((RegexPattern)firstElement).pattern;
				if (!pattern.startsWith(".?") && pattern.startsWith(".")) {
					((RegexPattern)firstElement).setPattern(".?" + pattern.substring(1));
				}
			}
		}
		return REGEX_GLOBAL_MODIFIERS + result;
	}
	
	/**
	 * @param wordLimit number of non-optional words to include in the pattern
	 * @return a regex to match the end of the license per the template
	 */
	public String getEndRegex(int wordLimit) {
		RegexList result = new RegexList();
		int numWords = 0;
		List<RegexElement> elementList = regexPatternList.getElements();
		int index = elementList.size()-1;
		// find the beginning of the end ...
		while (index > 0 && numWords <= wordLimit) {
			if (elementList.get(index--) instanceof RegexToken) {
				numWords++;
			}
		}
		while (index < elementList.size()) {
			result.addElement(elementList.get(index++));
		}
		return REGEX_GLOBAL_MODIFIERS + result;
	}
	
	/**
	 * @param text text to search for a match
	 * @return true if a match is found, otherwise false
	 */
	public boolean isTemplateMatchWithinText(String text) throws SpdxCompareException, InvalidSPDXAnalysisException {
		if (text == null || text.isEmpty()) {
			return false;
		}
		String completeText = findTemplateWithinText(text);
		if (completeText != null) {
			return !LicenseCompareHelper.isTextMatchingTemplate(template, completeText).isDifferenceFound();
		} else {
			return false;
		}
	}

	/**
	 * @param text text to search for
	 * @return the text matching the beginning and end regular expressions for the template.  Null if there is no match.
     */
	@SuppressWarnings("UnusedAssignment")
    private @Nullable String findTemplateWithinText(String text) {
		// Get match status
		String result = null;
		@SuppressWarnings("UnusedAssignment") int startIndex = -1;
		int endIndex = -1;

		if (text == null || text.isEmpty() || template == null) {
			return null;
		}
		
		StringBuilder normalizedText = new StringBuilder();
		
		for (String token:LicenseTextHelper.tokenizeLicenseText(LicenseTextHelper.removeLineSeparators(
				LicenseCompareHelper.removeCommentChars(text)), new HashMap<>())) {
			normalizedText.append(
					LicenseTextHelper.NORMALIZE_TOKENS.getOrDefault(token.toLowerCase(), token.toLowerCase()));
			normalizedText.append(' ');
		}
		
		String compareText = normalizedText.toString();

		Pattern quickPattern = Pattern.compile(getQuickMatchRegex(WORD_LIMIT));
		if (quickPattern.matcher(compareText).find()) {
			Pattern startPattern = Pattern.compile(getStartRegex(WORD_LIMIT));
			Matcher startMatcher = startPattern.matcher(compareText);
			if(startMatcher.find()) {
				startIndex = startMatcher.start();
				Pattern endPattern = Pattern.compile(getEndRegex(WORD_LIMIT));
				Matcher endMatcher = endPattern.matcher(compareText);
				if (endMatcher.find()) {
					endIndex = endMatcher.end();
					result = compareText.substring(startIndex, endIndex);
				}
			}
		}
		return result;
	}
	
	private RegexList getCurrentList() {
		return optionalNestLevel == 0 ? regexPatternList : optionalGroups.get(optionalNestLevel - 1);
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#text(java.lang.String)
	 */
	@Override
	public void text(String text) {
		RegexList currentList = getCurrentList();
		for (String token:LicenseTextHelper.tokenizeLicenseText(text, new HashMap<>())) {
			currentList.addElement(new RegexToken(
					LicenseTextHelper.NORMALIZE_TOKENS.getOrDefault(token.toLowerCase(), token.toLowerCase())));
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#variableRule(org.spdx.licenseTemplate.LicenseTemplateRule)
	 */
	@Override
	public void variableRule(LicenseTemplateRule rule) {
		getCurrentList().addElement(new RegexPattern(rule.getMatch()));
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#beginOptional(org.spdx.licenseTemplate.LicenseTemplateRule)
	 */
	@Override
	public void beginOptional(LicenseTemplateRule rule) {
		optionalNestLevel++;
		if (optionalGroups.size() == optionalNestLevel) {
			logger.warn("Optional groups size does not match the nest level");
			optionalGroups.set(optionalNestLevel-1, new OptionalRegexGroup());
		} else {
			optionalGroups.add(new OptionalRegexGroup());
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#endOptional(org.spdx.licenseTemplate.LicenseTemplateRule)
	 */
	@Override
	public void endOptional(LicenseTemplateRule rule) {
		OptionalRegexGroup optionalGroup = optionalGroups.get(optionalNestLevel - 1);
		optionalGroups.remove(optionalNestLevel - 1);
		optionalNestLevel--;
		getCurrentList().addElement(optionalGroup);
	}

	/* (non-Javadoc)
	 * @see org.spdx.licenseTemplate.ILicenseTemplateOutputHandler#completeParsing()
	 */
	@Override
	public void completeParsing() {
		// Nothing to do here
	}
}
