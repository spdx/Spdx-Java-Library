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
package org.spdx.utility.compare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ConjunctiveLicenseSet;
import org.spdx.library.model.license.DisjunctiveLicenseSet;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.License;
import org.spdx.library.model.license.LicenseException;
import org.spdx.library.model.license.LicenseSet;
import org.spdx.library.model.license.ListedLicenseException;
import org.spdx.library.model.license.ListedLicenses;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.licenseTemplate.LicenseParserException;
import org.spdx.licenseTemplate.LicenseTemplateRuleException;
import org.spdx.licenseTemplate.SpdxLicenseTemplateHelper;
import org.spdx.utility.compare.CompareTemplateOutputHandler.DifferenceDescription;
import org.spdx.utility.compare.FilterTemplateOutputHandler.OptionalTextHandling;
import org.spdx.utility.compare.FilterTemplateOutputHandler.VarTextHandling;

/**
 * Primarily a static class of helper functions for comparing two SPDX licenses
 * @author Gary O'Neall
 *
 */
@SuppressWarnings("deprecation")
public class LicenseCompareHelper {
	
	static final Logger logger = LoggerFactory.getLogger(LicenseCompareHelper.class);
	
	protected static final String TOKEN_SPLIT_REGEX = "(^|[^\\s\\.,?'();:\"/\\[\\]]{1,100})((\\s|\\.|,|\\?|'|\"|\\(|\\)|;|:|/|\\[|\\]|$){1,100})";
	protected static final Pattern TOKEN_SPLIT_PATTERN = Pattern.compile(TOKEN_SPLIT_REGEX);

	protected static final Set<String> PUNCTUATION = Collections.unmodifiableSet(new HashSet<String>(
			Arrays.asList(".",",","?","\"","'","(",")",";",":","/","[", "]")));
	
	// most of these are comments for common programming languages (C style, Java, Ruby, Python)
	protected static final Set<String> SKIPPABLE_TOKENS = Collections.unmodifiableSet(new HashSet<String>(
		Arrays.asList("//","/*","*/","/**","#","##","*","**","\"\"\"","/","=begin","=end")));
	
	protected static final Map<String, String> NORMALIZE_TOKENS = new HashMap<>();

	protected static final Integer CROSS_REF_NUM_WORDS_MATCH = 80;
	
	protected static final Pattern REGEX_QUANTIFIER_PATTERN = Pattern.compile(".*\\.\\{(\\d+),(\\d+)\\}$");
	
	static {
		//TODO: These should be moved to a property file
		NORMALIZE_TOKENS.put("&","and");
		NORMALIZE_TOKENS.put("acknowledgment","acknowledgement");   
		NORMALIZE_TOKENS.put("analogue","analog");   
		NORMALIZE_TOKENS.put("analyse","analyze");   
		NORMALIZE_TOKENS.put("artefact","artifact");   
		NORMALIZE_TOKENS.put("authorisation","authorization");   
		NORMALIZE_TOKENS.put("authorised","authorized");   
		NORMALIZE_TOKENS.put("calibre","caliber");   
		NORMALIZE_TOKENS.put("cancelled","canceled");   
		NORMALIZE_TOKENS.put("apitalisations","apitalizations");   
		NORMALIZE_TOKENS.put("catalogue","catalog");   
		NORMALIZE_TOKENS.put("categorise","categorize");   
		NORMALIZE_TOKENS.put("centre","center");   
		NORMALIZE_TOKENS.put("emphasised","emphasized");   
		NORMALIZE_TOKENS.put("favour","favor");   
		NORMALIZE_TOKENS.put("favourite","favorite");   
		NORMALIZE_TOKENS.put("fulfil","fulfill");   
		NORMALIZE_TOKENS.put("fulfilment","fulfillment");   
		NORMALIZE_TOKENS.put("initialise","initialize");   
		NORMALIZE_TOKENS.put("judgment","judgement");   
		NORMALIZE_TOKENS.put("labelling","labeling");   
		NORMALIZE_TOKENS.put("labour","labor");   
		NORMALIZE_TOKENS.put("licence","license");   
		NORMALIZE_TOKENS.put("maximise","maximize");   
		NORMALIZE_TOKENS.put("modelled","modeled");   
		NORMALIZE_TOKENS.put("modelling","modeling");   
		NORMALIZE_TOKENS.put("offence","offense");   
		NORMALIZE_TOKENS.put("optimise","optimize");   
		NORMALIZE_TOKENS.put("organisation","organization");   
		NORMALIZE_TOKENS.put("organise","organize");   
		NORMALIZE_TOKENS.put("practise","practice");   
		NORMALIZE_TOKENS.put("programme","program");   
		NORMALIZE_TOKENS.put("realise","realize");   
		NORMALIZE_TOKENS.put("recognise","recognize");   
		NORMALIZE_TOKENS.put("signalling","signaling");   
		NORMALIZE_TOKENS.put("utilisation","utilization");   
		NORMALIZE_TOKENS.put("whilst","while");   
		NORMALIZE_TOKENS.put("wilful","wilfull");   
		NORMALIZE_TOKENS.put("non-commercial","noncommercial");    
		NORMALIZE_TOKENS.put("copyright-owner", "copyright-holder");
		NORMALIZE_TOKENS.put("sublicense", "sub-license");
		NORMALIZE_TOKENS.put("non-infringement", "noninfringement");
		NORMALIZE_TOKENS.put("(c)", "-c-");
		NORMALIZE_TOKENS.put("©", "-c-");
		NORMALIZE_TOKENS.put("copyright", "-c-");
		NORMALIZE_TOKENS.put("\"", "'");
		NORMALIZE_TOKENS.put("merchantability", "merchantability");
	}
	
	
	static final String DASHES_REGEX = "[\\u2012\\u2013\\u2014\\u2015]";
	static final Pattern SPACE_PATTERN = Pattern.compile("[\\u202F\\u2007\\u2060\\u2009]");
	static final Pattern COMMA_PATTERN = Pattern.compile("[\\uFF0C\\uFE10\\uFE50]");
	static final Pattern PER_CENT_PATTERN = Pattern.compile("per cent", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_HOLDER_PATTERN = Pattern.compile("copyright holder", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_HOLDERS_PATTERN = Pattern.compile("copyright holders", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_OWNERS_PATTERN = Pattern.compile("copyright owners", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_OWNER_PATTERN = Pattern.compile("copyright owner", Pattern.CASE_INSENSITIVE);
	static final Pattern PER_CENT_PATTERN_LF = Pattern.compile("per\\s{0,100}\\n{1,10}\\s{0,100}cent", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_HOLDERS_PATTERN_LF = Pattern.compile("copyright\\s{0,100}\\n{1,10}\\s{0,100}holders", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_HOLDER_PATTERN_LF = Pattern.compile("copyright\\s{0,100}\\n{1,10}\\s{0,100}holder", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_OWNERS_PATTERN_LF = Pattern.compile("copyright\\s{0,100}\\n{1,10}\\s{0,100}owners", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_OWNER_PATTERN_LF = Pattern.compile("copyright\\s{0,100}\\n{1,10}\\s{0,100}owner", Pattern.CASE_INSENSITIVE);
	static final Pattern COPYRIGHT_SYMBOL_PATTERN = Pattern.compile("\\(c\\)", Pattern.CASE_INSENSITIVE);
	static final String START_COMMENT_CHAR_PATTERN = "(//|/\\*|\\*|#|' |REM |<!--|--|;|\\(\\*|\\{-)|\\.\\\\\"";
	
	/**
	 * Returns true if two sets of license text is considered a match per
	 * the SPDX License matching guidelines documented at spdx.org (currently http://spdx.org/wiki/spdx-license-list-match-guidelines)
	 * There are 2 unimplemented features - bullets/numbering is not considered and comments with no whitespace between text is not skipped
	 * @param licenseTextA
	 * @param licenseTextB
	 * @return
	 */
	public static boolean isLicenseTextEquivalent(String licenseTextA, String licenseTextB) {
		//TODO: Handle comment characters without white space before text
		//TODO: Handle bullets and numbering
		// Need to take care of multi-word equivalent words - convert to single words with hypens
		
		// tokenize each of the strings
		if (licenseTextA == null) {
			return (licenseTextB == null || licenseTextB.isEmpty());
		}
		if (licenseTextB == null) {
			return licenseTextA.isEmpty();
		}
		if (licenseTextA.equals(licenseTextB)) {
			return true;
		}
		Map<Integer, LineColumn> tokenToLocationA = new HashMap<Integer, LineColumn>();
		Map<Integer, LineColumn> tokenToLocationB = new HashMap<Integer, LineColumn>();
		String[] licenseATokens = tokenizeLicenseText(licenseTextA,tokenToLocationA);
		String[] licenseBTokens = tokenizeLicenseText(licenseTextB,tokenToLocationB);
		int bTokenCounter = 0;
		int aTokenCounter = 0;
		String nextAToken = getTokenAt(licenseATokens, aTokenCounter++);
		String nextBToken = getTokenAt(licenseBTokens, bTokenCounter++);
		while (nextAToken != null) {
			if (nextBToken == null) {
				// end of b stream
				while (nextAToken != null && canSkip(nextAToken)) {
					nextAToken = getTokenAt(licenseATokens, aTokenCounter++);
				}
				if (nextAToken != null) {
					return false;	// there is more stuff in the license text B, so not equal
				}
			} else if (tokensEquivalent(nextAToken, nextBToken)) { 
				// just move onto the next set of tokens
				nextAToken = getTokenAt(licenseATokens, aTokenCounter++);
				nextBToken = getTokenAt(licenseBTokens, bTokenCounter++);
			} else {
				// see if we can skip through some B tokens to find a match
				while (nextBToken != null && canSkip(nextBToken)) {
					nextBToken = getTokenAt(licenseBTokens, bTokenCounter++);
				}
				// just to be sure, skip forward on the A license
				while (nextAToken != null && canSkip(nextAToken)) {
					nextAToken = getTokenAt(licenseATokens, aTokenCounter++);
				}
				if (!tokensEquivalent(nextAToken, nextBToken)) {
					return false;
				} else {
					nextAToken = getTokenAt(licenseATokens, aTokenCounter++);
					nextBToken = getTokenAt(licenseBTokens, bTokenCounter++);
				}
			}
		}
		// need to make sure B is at the end
		while (nextBToken != null && canSkip(nextBToken)) {
			nextBToken = getTokenAt(licenseBTokens, bTokenCounter++);
		}
		return (nextBToken == null);
	}
	
	/**
	 * @param s Input string
	 * @return s without any line separators (---, ***, ===)
	 */
	public static String removeLineSeparators(String s) {
		return s.replaceAll("(-|=|\\*){3,}\\s*$", "");  // Remove ----, ***,  and ====
	}
	
	/**
	 * Remove common comment characters from either a template or license text strings
	 * @param s
	 * @return
	 */
	public static String removeCommentChars(String s) {
	       StringBuilder sb = new StringBuilder();
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new StringReader(s));
	            String line = reader.readLine();
	            while (line != null) {
	            	line = line.replaceAll("(\\*/|-->|-\\}|\\*\\)|\\s\\*)\\s*$", "");  // remove end of line comments
	                line = line.replaceAll("^\\s*" + START_COMMENT_CHAR_PATTERN, "");  // remove start of line comments
                    line = line.replaceAll("^\\s*<<beginOptional>>\\s*" + START_COMMENT_CHAR_PATTERN, "<<beginOptional>>");
                    sb.append(line);
	                sb.append("\n");
	                line = reader.readLine();
	            }
	            return sb.toString();
	        } catch (IOException e) {
	            logger.warn("IO error reading strings?!?", e);
	            return s;
	        } finally {
                if (Objects.nonNull(reader)) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        logger.warn("IO error closing a string reader?!?", e);
                    }
                }
	        }
	}
	/**
	 * Normalize quotes and no-break spaces
	 * @param s String to normalize
	 * @return String normalized for comparison
	 */
	public static String normalizeText(String s) {
		// First normalize single quotes, then normalize two single quotes to a double quote, normalize double quotes 
		// then normalize non-breaking spaces to spaces
		return s.replaceAll("‘|’|‛|‚|`", "'")	// Take care of single quotes first
				.replaceAll("http://", "https://") // Normalize the http protocol scheme
 				.replaceAll("''","\"")			// This way, we can change doulbe single quotes to a single double cquote
				.replaceAll("“|”|‟|„", "\"")	// Now we can normalize the double quotes
				.replaceAll("\\u00A0", " ")		// replace non-breaking spaces with spaces since Java does not handle the former well
				.replaceAll("—|–","-")			// replace em dash, en dash with simple dash
				.replaceAll("\\u2028", "\n");	// replace line separator with newline since Java does not handle the former well
	}
	
	/**
	 * Locate the original text starting with the start token and ending with the end token
	 * @param fullLicenseText
	 * @param startToken
	 * @param endToken
	 * @param tokenToLocation
	 * @return
	 */
	public static String locateOriginalText(String fullLicenseText, int startToken, int endToken,  
			Map<Integer, LineColumn> tokenToLocation, String[] tokens) {
		if (startToken > endToken) {
			return "";
		}
		LineColumn start = tokenToLocation.get(startToken);
		if (start == null) {
			return "";
		}
		LineColumn end = tokenToLocation.get(endToken);
		// If end == null, then we read to the end
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new StringReader(fullLicenseText));
			int currentLine = 1;
			String line = reader.readLine();
			while (line != null && currentLine < start.getLine()) {
				currentLine++;
				line = reader.readLine();
			}
			if (line == null) {
				return "";
			}
			if (end == null) {
				// read until the end of the stream
				StringBuilder sb = new StringBuilder(line.substring(start.getColumn(), line.length()));
				currentLine++;
				line = reader.readLine();
				while (line != null) {
					sb.append(line);
					currentLine++;
					line = reader.readLine();
				}
				return sb.toString();
			} else if (end.getLine() == currentLine) {
				return line.substring(start.getColumn(), end.getColumn()+end.getLen());
			} else {
				StringBuilder sb = new StringBuilder(line.substring(start.getColumn(), line.length()));
				currentLine++;
				line = reader.readLine();
				while (line != null && currentLine < end.getLine()) {
					sb.append("\n");
					sb.append(line);
					currentLine++;
					line = reader.readLine();
				}
				if (line != null && end.getColumn()+end.getLen() > 0) {
					sb.append("\n");
					sb.append(line.substring(0, end.getColumn()+end.getLen()));
				}
				return sb.toString();
			}			
		} catch (IOException e) {
			// just build with spaces - not ideal, but close enough most of the time
			StringBuilder sb = new StringBuilder(tokens[startToken]);
			for (int i = startToken+1; i <= endToken; i++) {
				sb.append(' ');
				sb.append(tokens[i]);
			}
			return sb.toString();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	/**
	 * Tokenizes the license text, normalizes quotes, lowercases and converts
	 * multi-words for better equiv. comparisons
	 * 
	 * @param tokenToLocation location for all of the tokens by line and column
	 * @param licenseText
	 * @return tokens
	 */
	public static String[] tokenizeLicenseText(String licenseText, Map<Integer, LineColumn> tokenToLocation) {
		String textToTokenize = normalizeText(replaceMultWord(replaceSpaceComma(licenseText))).toLowerCase();
		List<String> tokens = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new StringReader(textToTokenize));
			int currentLine = 1;
			int currentToken = 0;
			String line = reader.readLine();
			while (line != null) {
				line = removeLineSeparators(line);
				Matcher lineMatcher = TOKEN_SPLIT_PATTERN.matcher(line);
				while (lineMatcher.find()) {
					String token = lineMatcher.group(1).trim();
					if (!token.isEmpty()) {
						tokens.add(token);
						tokenToLocation.put(currentToken, new LineColumn(currentLine, lineMatcher.start(), token.length()));
						currentToken++;
					}
					String fullMatch = lineMatcher.group(0);
					for (int i = lineMatcher.group(1).length(); i < fullMatch.length(); i++) {
						String possiblePunctuation = fullMatch.substring(i, i+1);
						if (PUNCTUATION.contains(possiblePunctuation)) {
							tokens.add(possiblePunctuation);
							tokenToLocation.put(currentToken, new LineColumn(currentLine, lineMatcher.start()+i, 1));
							currentToken++;
						}
					}
				}
				currentLine++;
				line = reader.readLine();
			}
		} catch (IOException e) {
			// Don't fill in the lines, take a simpler approach
			Matcher m = TOKEN_SPLIT_PATTERN.matcher(textToTokenize);
			while (m.find()) {
				String word = m.group(1).trim();
				String seperator = m.group(2).trim();
				tokens.add(word);
				if (PUNCTUATION.contains(seperator)) {
					tokens.add(seperator);
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	/**
	 * @param text
	 * @return the first token in the license text
	 */
	public static String getFirstLicenseToken(String text) {
		String textToTokenize = normalizeText(replaceMultWord(replaceSpaceComma(removeLineSeparators(removeCommentChars(text))))).toLowerCase();
		Matcher m = TOKEN_SPLIT_PATTERN.matcher(textToTokenize);
		while (m.find()) {
			if (!m.group(1).trim().isEmpty()) {
				return m.group(1).trim();
			}
		}
		return null;
	}
	
	/**
	 * @param text
	 * @return true if the text contains a single token
	 */
	public static boolean isSingleTokenString(String text) {
		if (text.contains("\n")) {
			return false;
		}
		Matcher m = TOKEN_SPLIT_PATTERN.matcher(text);
		boolean found = false;
		while (m.find()) {
			if (!m.group(1).trim().isEmpty()) {
				if (found) {
					return false;
				} else {
					found = true;
				}
			}
		}
		return true;
	}
	
	/**
	 * Replace different forms of space with a normalized space and different forms of commas with a normalized comma
	 * @param s
	 * @return
	 */
	static String replaceSpaceComma(String s) {
		Matcher spaceMatcher = SPACE_PATTERN.matcher(s);
		Matcher commaMatcher = COMMA_PATTERN.matcher(spaceMatcher.replaceAll(" "));
		return commaMatcher.replaceAll(",");
	}

	/**
	 * replaces all mult-words with a single token using a dash to separate
	 * @param s
	 * @return
	 */
	static String replaceMultWord(String s) {
		//TODO: There is certainly some room for optimization - perhaps a single regex in a find loop
		Matcher m = COPYRIGHT_HOLDERS_PATTERN.matcher(s);
		String retval = m.replaceAll("copyright-holders");
		m = COPYRIGHT_HOLDERS_PATTERN_LF.matcher(retval);
		retval = m.replaceAll("copyright-holders\n");
		m = COPYRIGHT_OWNERS_PATTERN.matcher(retval);
		retval = m.replaceAll("copyright-owners");
		m = COPYRIGHT_OWNERS_PATTERN_LF.matcher(retval);
		retval = m.replaceAll("copyright-owners\n");
		m = COPYRIGHT_HOLDER_PATTERN.matcher(retval);
		retval = m.replaceAll("copyright-holder");
		m = COPYRIGHT_HOLDER_PATTERN_LF.matcher(retval);
		retval = m.replaceAll("copyright-holder\n");
		m = COPYRIGHT_OWNER_PATTERN.matcher(retval);
		retval = m.replaceAll("copyright-owner");
		m = COPYRIGHT_OWNER_PATTERN_LF.matcher(retval);
		retval = m.replaceAll("copyright-owner\n");
		m = PER_CENT_PATTERN.matcher(retval);
		retval = m.replaceAll("percent");
		m = PER_CENT_PATTERN.matcher(retval);
		retval = m.replaceAll("percent\n");
		m = COPYRIGHT_SYMBOL_PATTERN.matcher(retval);
		retval = m.replaceAll("-c-");	// replace the parenthesis with a dash so that it results in a single token rather than 3
		return retval;
	}
	
	/**
	 * Just fetches the string at the index checking for range.  Returns null if index is out of range.
	 * @param tokens
	 * @param tokenIndex
	 * @return
	 */
	static String getTokenAt(String[] tokens, int tokenIndex) {
		if (tokenIndex >= tokens.length) {
			return null;
		} else {
			return tokens[tokenIndex];
		}
	}
	/**
	 * Returns true if the two tokens can be considered equlivalent per the SPDX license matching rules
	 * @param tokenA
	 * @param tokenB
	 * @return
	 */
	static boolean tokensEquivalent(String tokenA, String tokenB) {
		if (tokenA == null) {
			if (tokenB == null) {
				return true;
			} else {
				return false;
			}
		} else if (tokenB == null) {
			return false;
		} else {
			String s1 = tokenA.trim().toLowerCase().replaceAll(DASHES_REGEX, "-");
			String s2 = tokenB.trim().toLowerCase().replaceAll(DASHES_REGEX, "-");
			if (s1.equals(s2)) {
				return true;
			} else {
				// check for equivalent tokens by normalizing the tokens
				String ns1 = NORMALIZE_TOKENS.get(s1);
				if (ns1 == null) {
					ns1 = s1;
				}
				String ns2 = NORMALIZE_TOKENS.get(s2);
				if (ns2 == null) {
					ns2 = s2;
				}
				return ns1.equals(ns2);
			}
		}
	}
	/**
	 * Returns true if the token can be ignored per the rules
	 * @param token
	 * @return
	 */
	static boolean canSkip(String token) {
		if (token == null) {
			return false;
		}
		if (token.trim().isEmpty()) {
			return true;
		}
		return SKIPPABLE_TOKENS.contains(token.trim().toLowerCase());
	}

	/**
	 * Compares two licenses from potentially two different documents which may have
	 * different license ID's for the same license
	 * @param license1
	 * @param license2
	 * @param xlationMap Mapping the license ID's from license 1 to license 2
	 * @return
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static boolean isLicenseEqual(AnyLicenseInfo license1,
			AnyLicenseInfo license2, Map<String, String> xlationMap) throws SpdxCompareException, InvalidSPDXAnalysisException {
		if (license1 instanceof ConjunctiveLicenseSet) {
			if (!(license2 instanceof ConjunctiveLicenseSet)) {
				return false;
			} else {
				return isLicenseSetsEqual((ConjunctiveLicenseSet)license1,
						(ConjunctiveLicenseSet)license2, xlationMap);
			}
		} else if (license1 instanceof DisjunctiveLicenseSet) {
			if (!(license2 instanceof DisjunctiveLicenseSet)) {
				return false;
			} else {
				return isLicenseSetsEqual((DisjunctiveLicenseSet)license1,
						(DisjunctiveLicenseSet)license2, xlationMap);
			}
		} else if (license1 instanceof ExtractedLicenseInfo) {
			if (!(license2 instanceof ExtractedLicenseInfo)) {
				return false;
			} else {
				String licenseid1 = ((ExtractedLicenseInfo)license1).getLicenseId();
				String licenseid2 = ((ExtractedLicenseInfo)license2).getLicenseId();
				String xlatedLicenseId = xlationMap.get(licenseid1);
				if (xlatedLicenseId == null) {
					return false;	// no equivalent license was found
				}
				return xlatedLicenseId.equals(licenseid2);
			}
		} else {
            return license1.equals(license2);
        }
	}

	/**
	 * Compares two license sets using the xlationMap for the non-standard license IDs
	 * @param license1
	 * @param license2
	 * @return
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static boolean isLicenseSetsEqual(LicenseSet license1, LicenseSet license2, Map<String, String> xlationMap) throws SpdxCompareException, InvalidSPDXAnalysisException {
		// note - order does not matter
		Collection<AnyLicenseInfo> licenseInfos1 = license1.getMembers();
		Collection<AnyLicenseInfo> licenseInfos2 = license2.getMembers();
		if (licenseInfos1 == null) {
			return licenseInfos2 == null;
		}
		if (licenseInfos2 == null) {
			return false;
		}
		if (licenseInfos1.size() != licenseInfos2.size()) {
			return false;
		}
		for (AnyLicenseInfo ali1:licenseInfos1) {
			boolean found = false;
			for (AnyLicenseInfo ali2:licenseInfos2) {
				if (isLicenseEqual(ali1, ali2, xlationMap)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @deprecated The <code>TemplateRegexMatcher</code> class should be used in place of this method. This method will be removed in the next major release.
	 * Get the text of a license minus any optional text - note: this include the default variable text
	 * @param licenseTemplate license template containing optional and var tags
	 * @param includeVarText if true, include the default variable text; if false remove the variable text
	 * @return list of strings for all non-optional license text.  
	 * @throws SpdxCompareException
	 */
	@Deprecated
	public static List<String> getNonOptionalLicenseText(String licenseTemplate, boolean includeVarText) throws SpdxCompareException {
		return getNonOptionalLicenseText(licenseTemplate,
				includeVarText ? VarTextHandling.ORIGINAL : VarTextHandling.OMIT,
						OptionalTextHandling.OMIT);
	}
	
	/**
	 * Get the text of a license minus any optional text
	 * @param licenseTemplate license template containing optional and var tags
	 * @param varTextHandling include original, exclude, or include the regex (enclosed with "~~~") for "var" text
	 * @return list of strings for all non-optional license text.  
	 * @throws SpdxCompareException
	 */
	public static List<String> getNonOptionalLicenseText(String licenseTemplate, 
			VarTextHandling varTextHandling) throws SpdxCompareException {
		return getNonOptionalLicenseText(licenseTemplate, varTextHandling, OptionalTextHandling.OMIT);
	}
	
	/**
	 * Get the text of a license converting variable and optional text according to the options
	 * @param licenseTemplate license template containing optional and var tags
	 * @param varTextHandling include original, exclude, or include the regex (enclosed with "~~~") for "var" text
	 * @param optionalTextHandling include optional text, exclude, or include a regex for the optional text
	 * @return list of strings for all non-optional license text.  
	 * @throws SpdxCompareException
	 */
	public static List<String> getNonOptionalLicenseText(String licenseTemplate, 
			VarTextHandling varTextHandling, OptionalTextHandling optionalTextHandling) throws SpdxCompareException {
		FilterTemplateOutputHandler filteredOutput = new FilterTemplateOutputHandler(varTextHandling, optionalTextHandling);
		try {
			SpdxLicenseTemplateHelper.parseTemplate(licenseTemplate, filteredOutput);
		} catch (LicenseTemplateRuleException e) {
			throw new SpdxCompareException("Invalid template rule found during filter: "+e.getMessage(),e);
		} catch (LicenseParserException e) {
			throw new SpdxCompareException("Invalid template found during filter: "+e.getMessage(),e);
		}
		return filteredOutput.getFilteredText();
	}
	
	/**
	 * @deprecated The <code>TemplateRegexMatcher</code> class should be used in place of this method. This method will be removed in the next major release.
	 * Creates a regular expression pattern to match the start of a license text
	 * This method should be replaced by the <code>TemplateRegexMatcher</code> class and methods
	 * @param nonOptionalText List of strings of non-optional text from the license template (see {@literal List<String> getNonOptionalLicenseText})
	 * @param numberOfWords Number of words to use in the match
	 * @return A pair of Patterns the first of which will match the start of the license text the second of which will match the end of the license
	 */
	@Deprecated
	public static Pair<Pattern, Pattern> nonOptionalTextToPatterns(List<String> nonOptionalText, int numberOfWords) {
		if (Objects.isNull(nonOptionalText) || nonOptionalText.size() == 0 || numberOfWords < 1) {
			return new ImmutablePair<>(Pattern.compile(""), Pattern.compile(""));
		}
		int startWordCount = 0;
		int startTextIndex = 0;
		int wordsInLastLine = 0;	// keep track of the number of words processed in the last start line to make sure we don't overlap words in the end lines
		StringBuilder startPatternBuilder = new StringBuilder();
		String regexLimit = "," + Integer.toString(numberOfWords * 10) + "}";
		String lastRegex = "";
		while (startWordCount < numberOfWords && startTextIndex < nonOptionalText.size()) {
			String line = nonOptionalText.get(startTextIndex++);
			String[] regexSplits = line.trim().split(FilterTemplateOutputHandler.REGEX_ESCAPE);
			boolean inRegex = false; // if it starts with a regex, it will start with a blank line
			for (String regexSplit:regexSplits) {
				if (inRegex && startWordCount < numberOfWords) {
					String regexToAppend;
					if (regexSplit.endsWith(".+")) {
						regexToAppend = regexSplit.substring(0, regexSplit.length()-1) +"{1" + regexLimit;
					} else if (regexSplit.endsWith(".*")) {
						regexToAppend = regexSplit.substring(0, regexSplit.length()-1) +"{0" + regexLimit;
					} else {
						regexToAppend = regexSplit;
					}
					if (startPatternBuilder.toString().endsWith("}") && regexToAppend.endsWith("}")) {
						// collapse consecutive match anything
						Matcher lastRegexMatch = REGEX_QUANTIFIER_PATTERN.matcher(lastRegex);
						Matcher regexToAppendMatch = REGEX_QUANTIFIER_PATTERN.matcher(regexToAppend);
						if (lastRegexMatch.matches() && regexToAppendMatch.matches()) {
							int lastRegexMax = Integer.parseInt(lastRegexMatch.group(2));
							int thisRegexMax = Integer.parseInt(regexToAppendMatch.group(2));
							if (lastRegexMax >= thisRegexMax) {
								regexToAppend = ""; // already covered by previous regex
							} else {
								// remove the last max
								startPatternBuilder.setLength(startPatternBuilder.length()-(lastRegexMatch.group(2).length()+1));
								regexToAppend = regexToAppend.substring(regexToAppend.indexOf(',')+1);
							}
						}
					}
					startPatternBuilder.append(regexToAppend);
					lastRegex = regexToAppend;
					startWordCount++;
					inRegex = false;
				} else {
					String[] tokens = normalizeText(regexSplit.trim()).split("\\s");
					int tokenIndex = 0;
					wordsInLastLine = 0;
					while (tokenIndex < tokens.length && startWordCount < numberOfWords) {
						String token = tokens[tokenIndex++].trim();
						if (token.length() > 0) {
							if (NORMALIZE_TOKENS.containsKey(token.toLowerCase())) {
								token = NORMALIZE_TOKENS.get(token.toLowerCase());
							}
							startPatternBuilder.append(Pattern.quote(token));
							startPatternBuilder.append("\\s*");
							startWordCount++;
							wordsInLastLine++;
						}
					}
					inRegex = true;
				}
			}
		}
		
		// End words
		StringBuilder endPatternBuilder = new StringBuilder();
		List<String> endTextReversePattern = new ArrayList<>();
		int endTextIndex = nonOptionalText.size()-1;
		int endWordCount = 0;
		int lastProcessedStartLine = startTextIndex - 1;
		while (endWordCount < numberOfWords && 
				(endTextIndex > lastProcessedStartLine || 
						(endTextIndex == lastProcessedStartLine && (numberOfWords - endWordCount) < (nonOptionalText.get(endTextIndex).length() - wordsInLastLine)))) {	// Check to make sure we're not overlapping the start words
			List<String> nonEmptyTokens = new ArrayList<>();
			String line = nonOptionalText.get(endTextIndex);
			String[] regexSplits = line.trim().split(FilterTemplateOutputHandler.REGEX_ESCAPE);
			boolean inRegex = false;
			for (String regexSplit:regexSplits) {
				if (inRegex) {
					if (!regexSplit.isEmpty()) {
						nonEmptyTokens.add(FilterTemplateOutputHandler.REGEX_ESCAPE + regexSplit);
					}
					inRegex = false;
				} else {
					String[] tokens = normalizeText(regexSplit.trim()).split("\\s");
					
					for (String token:tokens) {
						String trimmedToken = token.trim();
						if (!trimmedToken.isEmpty()) {
							nonEmptyTokens.add(trimmedToken);
						}
					}
					inRegex = true;
				}
			}
			int remainingTokens = (endTextIndex == lastProcessedStartLine && nonEmptyTokens.size() - wordsInLastLine > numberOfWords - endWordCount) ? 
										numberOfWords - endWordCount : nonEmptyTokens.size() - wordsInLastLine;
			endTextIndex--;
			int tokenIndex = nonEmptyTokens.size() - 1;
			while (tokenIndex >= 0 && remainingTokens > 0) {
				String token = nonEmptyTokens.get(tokenIndex--);
				if (token.startsWith(FilterTemplateOutputHandler.REGEX_ESCAPE)) {
					endTextReversePattern.add(token.substring(FilterTemplateOutputHandler.REGEX_ESCAPE.length()));
				} else {
					endTextReversePattern.add("\\s*");
					if (NORMALIZE_TOKENS.containsKey(token.toLowerCase())) {
						token = NORMALIZE_TOKENS.get(token.toLowerCase());
					}
					endTextReversePattern.add(Pattern.quote(token));
				}
				remainingTokens--;
				endWordCount++;
			}
		}
		
		int revPatternIndex = endTextReversePattern.size()-1;
		while (revPatternIndex >= 0) {
			endPatternBuilder.append(endTextReversePattern.get(revPatternIndex--));
		}
		return new ImmutablePair<>(
				Pattern.compile(startPatternBuilder.toString(), Pattern.DOTALL|Pattern.CASE_INSENSITIVE),
				Pattern.compile(endPatternBuilder.toString(), Pattern.DOTALL|Pattern.CASE_INSENSITIVE));
	}
	
	/**
	 * @param template Template in the standard template format used for comparison
	 * @param compareText Text to compare using the template
	 * @return any differences found
	 * @throws SpdxCompareException
	 * @throws InvalidSPDXAnalysisException
	 */
	public static DifferenceDescription isTextMatchingTemplate(String template, String compareText) throws SpdxCompareException, InvalidSPDXAnalysisException {
		CompareTemplateOutputHandler compareTemplateOutputHandler = null;
		try {
			compareTemplateOutputHandler = new CompareTemplateOutputHandler(removeLineSeparators(removeCommentChars(compareText)));
		} catch (IOException e1) {
			throw new SpdxCompareException("IO Error reading the compare text: "+e1.getMessage(),e1);
		}
		try {
		    //TODO: The remove comment chars will not be removed for lines beginning with a template << or ending with >>
			SpdxLicenseTemplateHelper.parseTemplate(removeCommentChars(template), compareTemplateOutputHandler);
		} catch (LicenseTemplateRuleException e) {
			throw new SpdxCompareException("Invalid template rule found during compare: "+e.getMessage(),e);
		} catch (LicenseParserException e) {
			throw new SpdxCompareException("Invalid template found during compare: "+e.getMessage(),e);
		}
		return compareTemplateOutputHandler.getDifferences();
	}
		
		
	
	/**
	 * Compares license text to the license text of an SPDX Standard License
	 * @param license SPDX Standard License to compare
	 * @param compareText Text to compare to the standard license
	 * @return any differences found
	 * @throws SpdxCompareException
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static DifferenceDescription isTextStandardLicense(License license, String compareText) throws SpdxCompareException, InvalidSPDXAnalysisException {
		String licenseTemplate = license.getStandardLicenseTemplate();
		if (licenseTemplate == null || licenseTemplate.trim().isEmpty()) {
			licenseTemplate = license.getLicenseText();
		}
		return isTextMatchingTemplate(licenseTemplate, compareText);
	}
	
	/**
	 * Compares exception text to the exception text of an SPDX Standard exception
	 * @param exception SPDX Standard exception to compare
	 * @param compareText Text to compare to the standard exceptions
	 * @return any differences found
	 * @throws SpdxCompareException
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static DifferenceDescription isTextStandardException(LicenseException exception, String compareText) throws SpdxCompareException, InvalidSPDXAnalysisException {
		String exceptionTemplate = exception.getLicenseExceptionTemplate();
		if (exceptionTemplate == null || exceptionTemplate.trim().isEmpty()) {
			exceptionTemplate = exception.getLicenseExceptionText();
		}
		return isTextMatchingTemplate(exceptionTemplate, compareText);
	}

	/**
	 * Detect if a text contains the standard license (perhaps along with other text before and/or after)
	 * @param text    The text to search within (should not be null)
	 * @param license The standard SPDX license to search for (should not be null)
	 * @return True if the license is found within the text, false otherwise (or if either argument is null)
	 */
	public static boolean isStandardLicenseWithinText(String text, SpdxListedLicense license) {
		try {
			return new TemplateRegexMatcher(license.getStandardLicenseTemplate()).isTemplateMatchWithinText(text);
		} catch (SpdxCompareException e) {
			logger.warn("Error getting optional text for license ID " + license.getLicenseId(), e);
			return false;
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting optional text for license ID " + license.getLicenseId(), e);
			return false;
		}
	}


	/**
	 * Detect if a text contains the standard license exception (perhaps along with other text before and/or after)
	 * @param text    The text to search within (should not be null)
	 * @param exception The standard SPDX license exception to search for (should not be null)
	 * @return True if the license exception is found within the text, false otherwise (or if either argument is null)
	 */
	public static boolean isStandardLicenseExceptionWithinText(String text, ListedLicenseException exception) {
		boolean result = false;
		if (text == null || text.isEmpty() || exception == null) {
			return false;
		}
		try {
			return new TemplateRegexMatcher(exception.getLicenseExceptionTemplate()).isTemplateMatchWithinText(text);
		} catch (SpdxCompareException e) {
			logger.warn("Error getting optional text for license exception ID " + exception.getLicenseExceptionId(), e);
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting optional text for license exception ID " + exception.getLicenseExceptionId(), e);
		}
		return result;
	}


	/**
	 * Returns a list of SPDX Standard License ID's that match the text provided using
	 * the SPDX matching guidelines.
	 * @param licenseText Text to compare to the standard license texts
	 * @return Array of SPDX standard license IDs that match
	 * @throws InvalidSPDXAnalysisException If an error occurs accessing the standard licenses
	 * @throws SpdxCompareException If an error occurs in the comparison
	 */
	public static String[] matchingStandardLicenseIds(String licenseText) throws InvalidSPDXAnalysisException, SpdxCompareException {
		List<String> stdLicenseIds = ListedLicenses.getListedLicenses().getSpdxListedLicenseIds();
		List<String> matchingIds  = new ArrayList<>();
		for (String stdLicId : stdLicenseIds) {
			SpdxListedLicense license = ListedLicenses.getListedLicenses().getListedLicenseById(stdLicId);
			if (!isTextStandardLicense(license, licenseText).isDifferenceFound()) {
				matchingIds.add(license.getLicenseId());
			}
		}
		return matchingIds.toArray(new String[matchingIds.size()]);
	}


	/**
	 * Returns a list of SPDX Standard License ID's from the provided list that were found within the text, using
	 * the SPDX matching guidelines.
	 * @param text Text to compare to
	 * @param licenseIds License ids to compare against
	 * @return List of SPDX standard license IDs from licenseIds that match
	 * @throws InvalidSPDXAnalysisException If an error occurs accessing the standard licenses
	 * @throws SpdxCompareException If an error occurs in the comparison
	 */
	public static List<String> matchingStandardLicenseIdsWithinText(String text, List<String> licenseIds) throws InvalidSPDXAnalysisException, SpdxCompareException {
		List<String> result = new ArrayList<>();

		if (text != null && !text.isEmpty() && licenseIds != null && !licenseIds.isEmpty()) {
			for (String stdLicId : licenseIds) {
				SpdxListedLicense license = ListedLicenses.getListedLicenses().getListedLicenseById(stdLicId);
				if (isStandardLicenseWithinText(text, license)) {
					result.add(license.getLicenseId());
				}
			}
		}

		return result;
	}


	/**
	 * Returns a list of SPDX Standard License ID's that were found within the text, using
	 * the SPDX matching guidelines.
	 * @param text Text to compare to all of the standard licenses
	 * @return List of SPDX standard license IDs that match
	 * @throws InvalidSPDXAnalysisException If an error occurs accessing the standard licenses
	 * @throws SpdxCompareException If an error occurs in the comparison
	 */
	public static List<String> matchingStandardLicenseIdsWithinText(String text) throws InvalidSPDXAnalysisException, SpdxCompareException {
		return matchingStandardLicenseIdsWithinText(text, ListedLicenses.getListedLicenses().getSpdxListedLicenseIds());
	}


	/**
	 * Returns a list of SPDX Standard License Exception ID's from the provided list that were found within the text, using
	 * the SPDX matching guidelines.
	 * @param text Text to compare to
	 * @param licenseExceptionIds License Exceptions Ids to compare against
	 * @return Array of SPDX standard license exception IDs from licenseExceptionIds that match
	 * @throws InvalidSPDXAnalysisException If an error occurs accessing the standard license exceptions
	 * @throws SpdxCompareException If an error occurs in the comparison
	 */
	public static List<String> matchingStandardLicenseExceptionIdsWithinText(String text, List<String> licenseExceptionIds) throws InvalidSPDXAnalysisException, SpdxCompareException {
		List<String> result = new ArrayList<>();

		if (text != null && !text.isEmpty() && licenseExceptionIds != null && !licenseExceptionIds.isEmpty()) {
			for (String stdLicExcId : licenseExceptionIds) {
				ListedLicenseException licenseException = ListedLicenses.getListedLicenses().getListedExceptionById(stdLicExcId);
				if (isStandardLicenseExceptionWithinText(text, licenseException)) {
					result.add(licenseException.getLicenseExceptionId());
				}
			}
		}

		return result;
	}


	/**
	 * Returns a list of SPDX Standard License Exception ID's that were found within the text, using
	 * the SPDX matching guidelines.
	 * @param text Text to compare to all of the standard license exceptions
	 * @return Array of SPDX standard license exception IDs that match
	 * @throws InvalidSPDXAnalysisException If an error occurs accessing the standard license exceptions
	 * @throws SpdxCompareException If an error occurs in the comparison
	 */
	public static List<String> matchingStandardLicenseExceptionIdsWithinText(String text) throws InvalidSPDXAnalysisException, SpdxCompareException {
		return matchingStandardLicenseExceptionIdsWithinText(text, ListedLicenses.getListedLicenses().getSpdxListedExceptionIds());
	}


	private static <T> boolean contains(
			T[] array,
			T value
	) {
		for (T t : array) {
			if (Objects.equals(t, value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if a license pass black lists
	 * @param license license
	 * @param blackList license black list
	 * @return if the license pass black lists
	 * @throws InvalidSPDXAnalysisException
	 */
	public static boolean isLicensePassBlackList(
			AnyLicenseInfo license,
			String... blackList
	) throws InvalidSPDXAnalysisException {
		if (license == null) {
			return true;
		}
		if (blackList == null || blackList.length == 0) {
			return true;
		}
		if (license instanceof ConjunctiveLicenseSet) {
			for (AnyLicenseInfo member : ((ConjunctiveLicenseSet) license).getMembers()) {
				if (!isLicensePassBlackList(member, blackList)) {
					return false;
				}
			}
			return true;
		} else if (license instanceof DisjunctiveLicenseSet) {
			for (AnyLicenseInfo member : ((DisjunctiveLicenseSet) license).getMembers()) {
				if (isLicensePassBlackList(member, blackList)) {
					return true;
				}
			}
			return false;
		} else {
			return !contains(blackList, license.toString());
		}
	}

	/**
	 * Detect if a license pass white lists
	 * @param license license
	 * @param whiteList license white list
	 * @return if the license pass white lists
	 * @throws InvalidSPDXAnalysisException
	 */
	public static boolean isLicensePassWhiteList(
			AnyLicenseInfo license,
			String... whiteList
	) throws InvalidSPDXAnalysisException {
		if (license == null) {
			return false;
		}
		if (whiteList == null || whiteList.length == 0) {
			return false;
		}
		if (license instanceof ConjunctiveLicenseSet) {
			for (AnyLicenseInfo member : ((ConjunctiveLicenseSet) license).getMembers()) {
				if (!isLicensePassWhiteList(member, whiteList)) {
					return false;
				}
			}
			return true;
		} else if (license instanceof DisjunctiveLicenseSet) {
			for (AnyLicenseInfo member : ((DisjunctiveLicenseSet) license).getMembers()) {
				if (isLicensePassWhiteList(member, whiteList)) {
					return true;
				}
			}
			return false;
		} else {
			return contains(whiteList, license.toString());
		}
	}

}