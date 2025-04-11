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
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

import javax.annotation.Nullable;

import org.spdx.core.DefaultModelStore;
import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.ModelObjectV2;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;
import org.spdx.library.model.v2.license.SimpleLicensingInfo;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v2.license.SpdxNoAssertionLicense;
import org.spdx.library.model.v2.license.SpdxNoneLicense;
import org.spdx.library.model.v2.license.WithExceptionOperator;
import org.spdx.library.model.v3_0_1.SpdxConstantsV3;
import org.spdx.library.model.v3_0_1.core.DictionaryEntry;
import org.spdx.library.model.v3_0_1.expandedlicensing.ConjunctiveLicenseSet;
import org.spdx.library.model.v3_0_1.expandedlicensing.CustomLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.CustomLicenseAddition;
import org.spdx.library.model.v3_0_1.expandedlicensing.DisjunctiveLicenseSet;
import org.spdx.library.model.v3_0_1.expandedlicensing.ExtendableLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ExternalCustomLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ExternalCustomLicenseAddition;
import org.spdx.library.model.v3_0_1.expandedlicensing.License;
import org.spdx.library.model.v3_0_1.expandedlicensing.LicenseAddition;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicenseException;
import org.spdx.library.model.v3_0_1.expandedlicensing.NoAssertionLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.NoneLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.OrLaterOperator;
import org.spdx.library.model.v3_0_1.expandedlicensing.WithAdditionOperator;
import org.spdx.library.model.v3_0_1.simplelicensing.AnyLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * A parser for the SPDX License Expressions as documented in the SPDX appendix
 * <p>
 * This is a static help class.  The primary method is parseLicenseExpression which 
 * returns an AnyLicenseInfo.
 * 
 * @author Gary O'Neall
 */
public class LicenseExpressionParser {

	enum Operator {
		OR_LATER, WITH, AND, OR	//NOTE: These must be in precedence order 
	}

    static final String LEFT_PAREN = "(";
	static final String RIGHT_PAREN = ")";
	static final Map<String, Operator> OPERATOR_MAP = new HashMap<>();
	public static final String UNINITIALIZED_LICENSE_TEXT = "[Initialized with license Parser.  The actual license text is not available]";
	
	static {
		OPERATOR_MAP.put("+", Operator.OR_LATER);
		OPERATOR_MAP.put("AND", Operator.AND);
		OPERATOR_MAP.put("OR", Operator.OR);
		OPERATOR_MAP.put("WITH", Operator.WITH);
		OPERATOR_MAP.put("and", Operator.AND);
		OPERATOR_MAP.put("or", Operator.OR);
		OPERATOR_MAP.put("with", Operator.WITH);
	}
	
	/**
	 * Parses a license expression into n license for use in the Model using the SPDX Version 3.X model
	 * @param expression Expression to be parsed
	 * @param store Store containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model store will be used.
	 * @param customLicenseUriPrefix Prefix for Object URI's created when appending custom license ID's or custom license additions. If any custom licenses or additions already exist, they will be used.
	 * If none exist for an ID, they will be added.  If null, the default model document URI will be used.
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param customIdToUri Mapping of the id prefixes used in the license expression to the namespace preceding the external ID
	 * @return the parsed license expression
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static AnyLicenseInfo parseLicenseExpression(String expression, IModelStore store, 
			String customLicenseUriPrefix, @Nullable IModelCopyManager copyManager, 
			@Nullable List<DictionaryEntry> customIdToUri) throws InvalidSPDXAnalysisException {
		if (expression == null || expression.trim().isEmpty()) {
			throw new LicenseParserException("Empty license expression");
		}
		Objects.requireNonNull(store, "Model store can not be null");
		if (Objects.isNull(customLicenseUriPrefix)) {
			customLicenseUriPrefix = DefaultModelStore.getDefaultDocumentUri() + "#";
		}
		String[] tokens  = tokenizeExpression(expression);
		if (tokens.length == 1 && tokens[0].equals(SpdxConstantsCompatV2.NOASSERTION_VALUE)) {
			return new NoAssertionLicense();
		} else if (tokens.length == 1 && tokens[0].equals(SpdxConstantsCompatV2.NONE_VALUE)) {
			return new NoneLicense();
		} else {
			try {
				return parseLicenseExpression(tokens, store, customLicenseUriPrefix, copyManager, customIdToUri);
			} catch (LicenseParserException ex) {
				// Add the expression to the error message to provide additional information to the user
				throw new LicenseParserException(ex.getMessage()+" License expression: '"+expression+"'", ex);
			} catch (EmptyStackException ex) {
				throw new LicenseParserException("Invalid license expression: '"+expression+"' - check that every operator (e.g. AND and OR) has operators and that parenthesis are matched");
			}
		}
	}
	
	/**
	 * Parses a license expression into n license for use in the Model using the SPDX Version 2.X model
	 * @param expression Expression to be parsed
	 * @param store Store containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model store will be used.
	 * @param documentUri Document URI for the document containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model document URI will be used.
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @return the parsed license expression
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static org.spdx.library.model.v2.license.AnyLicenseInfo parseLicenseExpressionCompatV2(String expression, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (expression == null || expression.trim().isEmpty()) {
			throw new LicenseParserException("Empty license expression");
		}
		Objects.requireNonNull(store, "Model store can not be null");
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		String[] tokens  = tokenizeExpression(expression);
		if (tokens.length == 1 && tokens[0].equals(SpdxConstantsCompatV2.NOASSERTION_VALUE)) {
			return new SpdxNoAssertionLicense(store, documentUri);
		} else if (tokens.length == 1 && tokens[0].equals(SpdxConstantsCompatV2.NONE_VALUE)) {
			return new SpdxNoneLicense(store, documentUri);
		} else {
			try {
				return parseLicenseExpressionCompatV2(tokens, store, documentUri, copyManager);
			} catch (LicenseParserException ex) {
				// Add the expression to the error message to provide additional information to the user
				throw new LicenseParserException(ex.getMessage()+" License expression: '"+expression+"'", ex);
			} catch (EmptyStackException ex) {
				throw new LicenseParserException("Invalid license expression: '"+expression+"' - check that every operator (e.g. AND and OR) has operators and that parenthesis are matched");
			}
		}
	}

	/**
	 * A custom tokenizer since there is not a white space between parents and pluses
	 * @param expression license expression string
	 * @return array of string tokens
	 */
	private static String[] tokenizeExpression(String expression) {
		String[] startTokens = expression.split("\\s");
		List<String> endTokens = new ArrayList<>();
		for (String token : startTokens) {
			processPreToken(token, endTokens);
		}
		return endTokens.toArray(new String[0]);
	}

	/**
	 * @param preToken previous token
	 * @param tokenList resultant list of tokens - modified by this methods
	 */
	private static void processPreToken(String preToken,
			List<String> tokenList) {
		if (preToken.isEmpty()) {
            //noinspection UnnecessaryReturnStatement
            return;
		} else if (preToken.startsWith("(")) {
			tokenList.add("(");
			processPreToken(preToken.substring(1), tokenList);
		} else if (preToken.endsWith(")")) {
			processPreToken(preToken.substring(0, preToken.length()-1), tokenList);
			tokenList.add(")");
		} else if (preToken.endsWith("+")) {
			processPreToken(preToken.substring(0, preToken.length()-1), tokenList);
			tokenList.add("+");
		} else {
			tokenList.add(preToken);
		}
	}

	/**
	 * Parses a tokenized license expression into a license for use in the RDF Parser
	 * @param tokens array of tokens
	 * @param store model store for non-listed licenses
	 * @param customLicenseUriPrefix Prefix for Object URI's created when appending custom license ID's or custom license additions. If any custom licenses or additions already exist, they will be used.
	 * If none exist for an ID, they will be added.  If null, the default model document URI will be used.
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param customIdToUri Mapping of the id prefixes used in the license expression to the namespace preceding the external ID - required for any external additions or licenses
	 * @return a license info representing the fully parsed list of tokens
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static AnyLicenseInfo parseLicenseExpression(String[] tokens, IModelStore store, 
			String customLicenseUriPrefix, @Nullable IModelCopyManager copyManager,
			@Nullable List<DictionaryEntry> customIdToUri) throws InvalidSPDXAnalysisException {
		if (tokens == null || tokens.length == 0) {
			throw new LicenseParserException("Expected license expression");
		}
		Stack<AnyLicenseInfo> operandStack = new Stack<>();
		Stack<Operator> operatorStack = new Stack<>();
		int tokenIndex = 0;
		String token;
		while (tokenIndex < tokens.length) {
			token = tokens[tokenIndex++];
			// left operand
			if (LEFT_PAREN.equals(token)) {
				int rightParenIndex = findMatchingParen(tokens, tokenIndex);
				if (rightParenIndex < 0) {
					throw new LicenseParserException("Missing right parenthesis");
				}
				String[] nestedTokens = Arrays.copyOfRange(tokens, tokenIndex, rightParenIndex);
				operandStack.push(parseLicenseExpression(nestedTokens, store, customLicenseUriPrefix, 
						copyManager, customIdToUri));
				tokenIndex = rightParenIndex + 1;		
			} else if (OPERATOR_MAP.get(token) == null) {	// assumed to be a simple licensing type
				operandStack.push(parseSimpleLicenseToken(token, store, customLicenseUriPrefix, copyManager, customIdToUri));
			} else {
				Operator operator = OPERATOR_MAP.get(token);
				if (operator == Operator.WITH) {
					// special processing here since With must be with an exception, not a licenseInfo
					if (!operatorStack.isEmpty() && Operator.OR_LATER.equals(operatorStack.peek())) {
						Operator tosOperator = operatorStack.pop();
						evaluateExpression(tosOperator, operandStack, store, customLicenseUriPrefix, copyManager);
					}
					if (tokenIndex >= tokens.length) {
						throw new LicenseParserException("Missing exception clause");
					}
					token = tokens[tokenIndex++];
					LicenseAddition licenseAddition = parseSimpleLicenseAdditionToken(token, 
							store, customLicenseUriPrefix, copyManager, customIdToUri);
					AnyLicenseInfo operand = operandStack.pop();
					if (operand == null) {
						throw new LicenseParserException("Missing license for with clause");
					}
					if (!(operand instanceof ExtendableLicense)) {
						throw new LicenseParserException("License with exception is not of type License or OrLaterOperator");
					}
					WithAdditionOperator weo = new WithAdditionOperator(store,
								store.getNextId(IdType.Anonymous), copyManager, true, customLicenseUriPrefix);
					weo.setSubjectExtendableLicense((ExtendableLicense)operand);
					weo.setSubjectAddition(licenseAddition);
					operandStack.push(weo);			
				} else {
					// process in order of precedence using the shunting yard algorithm
					while (!operatorStack.isEmpty() && 
							operatorStack.peek().ordinal() <= operator.ordinal()) {
						Operator tosOperator = operatorStack.pop();
						evaluateExpression(tosOperator, operandStack, store, customLicenseUriPrefix, copyManager);
					}
					operatorStack.push(operator);
				}
			}
		}
		// go through the rest of the stack
		while (!operatorStack.isEmpty()) {
			Operator tosOperator = operatorStack.pop();
			evaluateExpression(tosOperator, operandStack, store, customLicenseUriPrefix, copyManager);
		}
		AnyLicenseInfo retval = operandStack.pop();
		if (!operandStack.isEmpty()) {
			throw new LicenseParserException("Invalid license expression.  Expecting more operands.");
		}
		return retval;
	}
	
	/**
	 * Parses a tokenized license expression into a license
	 * @param tokens array of tokens
	 * @param store model store for non-listed licenses
	 * @param documentUri document URI for non-listed licenses
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @return a license represented by the license expression
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static org.spdx.library.model.v2.license.AnyLicenseInfo parseLicenseExpressionCompatV2(String[] tokens, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (tokens == null || tokens.length == 0) {
			throw new LicenseParserException("Expected license expression");
		}
		Stack<org.spdx.library.model.v2.license.AnyLicenseInfo> operandStack = new Stack<>();
		Stack<Operator> operatorStack = new Stack<>();
		int tokenIndex = 0;
		String token;
		while (tokenIndex < tokens.length) {
			token = tokens[tokenIndex++];
			// left operand
			if (LEFT_PAREN.equals(token)) {
				int rightParenIndex = findMatchingParen(tokens, tokenIndex);
				if (rightParenIndex < 0) {
					throw new LicenseParserException("Missing right parenthesis");
				}
				String[] nestedTokens = Arrays.copyOfRange(tokens, tokenIndex, rightParenIndex);
				operandStack.push(parseLicenseExpressionCompatV2(nestedTokens, store, documentUri, copyManager));
				tokenIndex = rightParenIndex + 1;		
			} else if (OPERATOR_MAP.get(token) == null) {	// assumed to be a simple licensing type
				operandStack.push(parseSimpleLicenseTokenCompatV2(token, store, documentUri, copyManager));
			} else {
				Operator operator = OPERATOR_MAP.get(token);
				if (operator == Operator.WITH) {
					// special processing here since With must be with an exception, not a licenseInfo
					if (!operatorStack.isEmpty() && Operator.OR_LATER.equals(operatorStack.peek())) {
						Operator tosOperator = operatorStack.pop();
						evaluateExpressionCompatV2(tosOperator, operandStack, store, documentUri, copyManager);
					}
					if (tokenIndex >= tokens.length) {
						throw new LicenseParserException("Missing exception clause");
					}
					token = tokens[tokenIndex++];
					org.spdx.library.model.v2.license.ListedLicenseException licenseException;
					Optional<String> exceptionId = Optional.empty();
					if (LicenseInfoFactory.isSpdxListedExceptionId(token)) {
						exceptionId = LicenseInfoFactory.listedExceptionIdCaseSensitive(token);
					}
					if (exceptionId.isPresent()) {
						licenseException = LicenseInfoFactory.getListedExceptionV2ById(exceptionId.get());
					} else if (token.startsWith(SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM)) {
						throw new LicenseParserException("WITH must be followed by a license exception. "+token+" is a Listed License type.");
					} else {
						licenseException = (org.spdx.library.model.v2.license.ListedLicenseException) org.spdx.library.model.v2.SpdxModelFactoryCompatV2.createModelObjectV2(store, 
								documentUri, token, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, copyManager);
					}
					org.spdx.library.model.v2.license.AnyLicenseInfo operand = operandStack.pop();
					if (operand == null) {
						throw new LicenseParserException("Missing license for with clause");
					}
					if (!((operand instanceof SimpleLicensingInfo) || (operand instanceof org.spdx.library.model.v2.license.OrLaterOperator))) {
						throw new LicenseParserException("License with exception is not of type SimpleLicensingInfo or OrLaterOperator");
					}
					WithExceptionOperator weo = new WithExceptionOperator(store, documentUri, store.getNextId(IdType.Anonymous), copyManager, true);
					weo.setLicense(operand);
					weo.setException(licenseException);
					operandStack.push(weo);			
				} else {
					// process in order of precedence using the shunting yard algorithm
					while (!operatorStack.isEmpty() && 
							operatorStack.peek().ordinal() <= operator.ordinal()) {
						Operator tosOperator = operatorStack.pop();
						evaluateExpressionCompatV2(tosOperator, operandStack, store, documentUri, copyManager);
					}
					operatorStack.push(operator);
				}
			}
		}
		// go through the rest of the stack
		while (!operatorStack.isEmpty()) {
			Operator tosOperator = operatorStack.pop();
			evaluateExpressionCompatV2(tosOperator, operandStack, store, documentUri, copyManager);
		}
		org.spdx.library.model.v2.license.AnyLicenseInfo retval = operandStack.pop();
		if (!operandStack.isEmpty()) {
			throw new LicenseParserException("Invalid license expression.  Expecting more operands.");
		}
		return retval;
	}

	/**
	 * Returns the index of the rightmost parenthesis or -1 if not found
	 * @param tokens array of tokens
	 * @param startToken index of the token to start the search
	 * @return index of the matching end parenthesis
	 */
	private static int findMatchingParen(String[] tokens, int startToken) {
		if (tokens == null) {
			return -1;
		}
		int nestCount = 0;
		for (int i = startToken; i < tokens.length; i++) {
			if (LEFT_PAREN.equals(tokens[i])) {
				nestCount++;
			} else if (RIGHT_PAREN.equals(tokens[i])) {
				if (nestCount == 0) {
					return i;
				} else {
					nestCount--;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Converts a string token into its equivalent license addition
	 * checking for a listed license
	 * @param token Token to translate to the equivalent license addition
	 * @param store Store for the licenses
	 * @param customLicenseUriPrefix Prefix to use for any created local licenses or additions
	 * @param copyManager to use when copying from the listed license store
	 * @param customIdToUri Mapping of the id prefixes used in the license expression to the namespace preceding the external ID - required for any external additions or licenses
	 * @return a CustomLicenseAddition, ListedLicense, ListedLicenseException or CustomLicense depending on what is in the store
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static LicenseAddition parseSimpleLicenseAdditionToken(String token, IModelStore store, String customLicenseUriPrefix,
			@Nullable IModelCopyManager copyManager, @Nullable List<DictionaryEntry> customIdToUri) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(token, "Token can not be null");
		Objects.requireNonNull(store, "Model store can not be null");
		Objects.requireNonNull(customLicenseUriPrefix, "URI Prefix can not be null");
		if (token.contains(":")) {
			// External License Ref
			return new ExternalCustomLicenseAddition(convertToExternalObjectUri(token, customIdToUri));
		}
		Optional<String> exceptionId = Optional.empty();
		if (LicenseInfoFactory.isSpdxListedExceptionId(token)) {	
			// listed exception
			exceptionId = LicenseInfoFactory.listedExceptionIdCaseSensitive(token);
		}
		if (exceptionId.isPresent()) {
			ListedLicenseException listedException = LicenseInfoFactory.getListedExceptionById(exceptionId.get());
			if (!store.exists(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + exceptionId.get())) {
				if (Objects.nonNull(copyManager)) {
					// copy to the local store
					copyManager.copy(store, listedException.getObjectUri(), listedException.getModelStore(), 
							listedException.getObjectUri(), SpdxModelFactory.getLatestSpecVersion(), null);
					// copy to the local store
				}
			}
			return new ListedLicenseException(store, listedException.getObjectUri(), copyManager,
					true, customLicenseUriPrefix);
		} else {
			// custom addition
			String objectUri = customLicenseUriPrefix + token;
			CustomLicenseAddition localAddition;
			if (store.exists(objectUri)) {
				localAddition = new CustomLicenseAddition(store, objectUri, copyManager, false, customLicenseUriPrefix);
			} else {
				localAddition = new CustomLicenseAddition(store, objectUri, copyManager, true, customLicenseUriPrefix);
				localAddition.setAdditionText(UNINITIALIZED_LICENSE_TEXT);
			}
			return localAddition;
		}
	}

	/**
	 * Converts a string token into its equivalent license
	 * checking for a listed license
	 * @param token Token to translate to the equivalent license
	 * @param store Store for the licenses
	 * @param customLicenseUriPrefix Prefix to use for any created local licenses or additions
	 * @param copyManager to use when copying from the listed license store
	 * @param customIdToUri Mapping of the id prefixes used in the license expression to the namespace preceding the external ID
	 * @return a CustomLicenseAddition, ListedLicense, ListedLicenseException or CustomLicense depending on what is in the store
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static AnyLicenseInfo parseSimpleLicenseToken(String token, IModelStore store, String customLicenseUriPrefix,
			@Nullable IModelCopyManager copyManager, @Nullable List<DictionaryEntry> customIdToUri) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(token, "Token can not be null");
		Objects.requireNonNull(store, "Model store can not be null");
		Objects.requireNonNull(customLicenseUriPrefix, "URI Prefix can not be null");
		if (token.contains(":")) {
			// External Custom License
			return new ExternalCustomLicense(convertToExternalObjectUri(token, customIdToUri));
		}
		Optional<String> licenseId = Optional.empty();
		if (LicenseInfoFactory.isSpdxListedLicenseId(token)) {	
			// listed license
			licenseId = LicenseInfoFactory.listedLicenseIdCaseSensitive(token);
		}
		if (licenseId.isPresent()) {
			ListedLicense listedLicense = LicenseInfoFactory.getListedLicenseById(licenseId.get());
			if (!store.exists(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + licenseId.get())) {
				if (Objects.nonNull(copyManager)) {
					// copy to the local store
					copyManager.copy(store, listedLicense.getObjectUri(), listedLicense.getModelStore(), 
							listedLicense.getObjectUri(), SpdxModelFactory.getLatestSpecVersion(), null);
					// copy to the local store
				}
			}
			return new ListedLicense(store, listedLicense.getObjectUri(), copyManager, true, SpdxConstantsV3.SPDX_LISTED_LICENSE_NAMESPACE);
		} else if (SpdxConstantsCompatV2.NOASSERTION_VALUE.equals(token)) {
			return new NoAssertionLicense();
		} else if (SpdxConstantsCompatV2.NONE_VALUE.equals(token)) {
			return new NoneLicense();
		}else {
			// LicenseRef
			String objectUri = customLicenseUriPrefix + token;
			CustomLicense localLicense;
			if (store.exists(objectUri)) {
				localLicense = new CustomLicense(store, objectUri, copyManager, false, customLicenseUriPrefix);
			} else {
				localLicense = new CustomLicense(store, objectUri, copyManager, true, customLicenseUriPrefix);
				localLicense.setLicenseText(UNINITIALIZED_LICENSE_TEXT);
			}
			return localLicense;
		}
	}
	
	/**
	 * @param externalReference String of the form [prefix]:[id] where [prefix] is a prefix in the customIdToUri and ID is the suffix of the object URI
	 * @param customIdToUri Mapping of the id prefixes used in the license expression to the namespace preceding the external ID
	 * @return the full object URI with the [prefix] replaced by the associated namespace
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static String convertToExternalObjectUri(String externalReference, @Nullable List<DictionaryEntry> customIdToUri) throws InvalidSPDXAnalysisException {
		if (Objects.isNull(customIdToUri)) {
			throw new LicenseParserException("References to external custom additions or external custom licenses must include the customIdToUri parameter");
		}
		String[] refParts = externalReference.split(":");
		if (refParts.length != 2 || refParts[0].isEmpty() || refParts[1].isEmpty()) {
			throw new LicenseParserException("Invalid external ID: "+externalReference);
		}
		String namespace = null;
		for (DictionaryEntry entry:customIdToUri) {
			if (refParts[0].equals(entry.getIdPrefix())) {
				Optional<String> entryValue = entry.getValue();
				if (!entryValue.isPresent()) {
					throw new LicenseParserException("No associated namespace for license ID prefix "+entry.getIdPrefix());
				}
				namespace = entryValue.get();
			}
		}
		if (Objects.isNull(namespace)) {
			throw new LicenseParserException("No ID Prefix "+refParts[0]+" found in the customIdToUri map");
		}
		return namespace + refParts[1];
	}

	/**
	 * Converts a string token into its equivalent license
	 * checking for a listed license
	 * @param token license ID token
	 * @param store model store for non-listed licenses
	 * @param documentUri document URI for non-listed licenses
	 * @param copyManager copy manager to copy listed licenses to local store
	 * @return license equivalent to the token
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static org.spdx.library.model.v2.license.AnyLicenseInfo parseSimpleLicenseTokenCompatV2(String token,
																									IModelStore store,
																									String documentUri,
																									IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(token, "Token can not be null");
		Objects.requireNonNull(store, "Model store can not be null");
		Objects.requireNonNull(documentUri, "URI prefix can not be null");
		if (token.contains(":")) {
			// External License Ref
			return new ExternalExtractedLicenseInfo(store, documentUri, token, copyManager);
		} 
		Optional<String> licenseId = Optional.empty();
		if (LicenseInfoFactory.isSpdxListedLicenseId(token)) {	
			// listed license
			licenseId = LicenseInfoFactory.listedLicenseIdCaseSensitive(token);
		}
		if (licenseId.isPresent()) {
			if (!store.exists(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + licenseId.get())) {
				SpdxListedLicense listedLicense = LicenseInfoFactory.getListedLicenseByIdCompatV2(licenseId.get());
				if (Objects.nonNull(copyManager)) {
					// copy to the local store
					copyManager.copy(store, listedLicense.getObjectUri(), listedLicense.getModelStore(), 
							listedLicense.getObjectUri(), ModelObjectV2.LATEST_SPDX_2_VERSION, listedLicense.getDocumentUri());
				}
			}
			return (org.spdx.library.model.v2.license.AnyLicenseInfo) org.spdx.library.model.v2.SpdxModelFactoryCompatV2.getModelObjectV2(store, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, 
					licenseId.get(), SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, copyManager, true);
		} else if (SpdxConstantsCompatV2.NOASSERTION_VALUE.equals(token)) {
			return new SpdxNoAssertionLicense();
		} else if (SpdxConstantsCompatV2.NONE_VALUE.equals(token)) {
			return new SpdxNoneLicense();
		} else {
			// LicenseRef
			Optional<String> caseSensitiveId = store.getCaseSensitiveId(documentUri, token);
			ExtractedLicenseInfo localLicense;
			if (caseSensitiveId.isPresent()) {
				localLicense = new ExtractedLicenseInfo(store, documentUri, caseSensitiveId.get(), copyManager, false);
				
			} else {
				localLicense = (ExtractedLicenseInfo) org.spdx.library.model.v2.SpdxModelFactoryCompatV2.createModelObjectV2(
						store, documentUri, token, SpdxConstantsCompatV2.CLASS_SPDX_EXTRACTED_LICENSING_INFO, copyManager);
				localLicense.setExtractedText(UNINITIALIZED_LICENSE_TEXT);
			}
			return localLicense;
		}
	}
	
	/**
	 * Evaluate the given operator using parameters in the parameter stack
	 * @param operator operator
	 * @param operandStack operands for the operator
	 * @param copyManager copy manager to use when copying listed licenses to local store
	 * @param store model store to store non-listed licenses
	 * @param customLicenseUriPrefix prefix to use for non-listed licenses
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static void evaluateExpression(Operator operator,
			Stack<AnyLicenseInfo> operandStack, IModelStore store, 
			String customLicenseUriPrefix, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (operator == Operator.OR_LATER) {
			// unary operator
			AnyLicenseInfo license = operandStack.pop();
			if (!(license instanceof License)) {
				throw new LicenseParserException("Missing license for the '+' or later operator");
			}
			OrLaterOperator olo = new OrLaterOperator(store, store.getNextId(IdType.Anonymous), copyManager, true, customLicenseUriPrefix);
			olo.setSubjectLicense((License)license);
			operandStack.push(olo);
		} else {
			// binary operator
			AnyLicenseInfo operand2 = operandStack.pop();
			AnyLicenseInfo operand1 = operandStack.pop();
			if (operand1 == null || operand2 == null) {
				throw new LicenseParserException("Missing operands for the "+operator.toString()+" operator");
			}
			operandStack.push(evaluateBinary(operator, operand1, operand2, store, customLicenseUriPrefix, copyManager));
		}		
	}

	/**
	 * Evaluate the given operator using parameters in the parameter stack
	 * @param operator operator
	 * @param operandStack operands for the operator
	 * @param copyManager copy manager to use when copying listed licenses to local store
	 * @param store model store to store non-listed licenses
	 * @param documentUri prefix to use for non-listed licenses
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static void evaluateExpressionCompatV2(Operator operator,
			Stack<org.spdx.library.model.v2.license.AnyLicenseInfo> operandStack, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (operator == Operator.OR_LATER) {
			// unary operator
			org.spdx.library.model.v2.license.AnyLicenseInfo license = operandStack.pop();
			if (!(license instanceof SimpleLicensingInfo)) {
				throw new LicenseParserException("Missing license for the '+' or later operator");
			}
			org.spdx.library.model.v2.license.OrLaterOperator olo = new org.spdx.library.model.v2.license.OrLaterOperator(store, documentUri, store.getNextId(IdType.Anonymous), copyManager, true);
			olo.setLicense((SimpleLicensingInfo)license);
			operandStack.push(olo);
		} else {
			// binary operator
			org.spdx.library.model.v2.license.AnyLicenseInfo operand2 = operandStack.pop();
			org.spdx.library.model.v2.license.AnyLicenseInfo operand1 = operandStack.pop();
			if (operand1 == null || operand2 == null) {
				throw new LicenseParserException("Missing operands for the "+operator.toString()+" operator");
			}
			operandStack.push(evaluateBinaryCompatV2(operator, operand1, operand2, store, documentUri, copyManager));
		}		
	}

	/**
	 * Evaluates a binary expression and merges conjunctive and disjunctive licenses
	 * @param tosOperator binary operator
	 * @param operand1 first operand
	 * @param operand2 second operand
	 * @param copyManager copy manager to use when copying listed licenses to local store
	 * @param store model store to store non-listed licenses
	 * @param customLicenseUriPrefix prefix to use for non-listed licenses
	 * @return resultant license representing the binary operation
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static AnyLicenseInfo evaluateBinary(Operator tosOperator,
			AnyLicenseInfo operand1, AnyLicenseInfo operand2, IModelStore store, 
			String customLicenseUriPrefix, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (tosOperator == Operator.AND) {
			if (operand1 instanceof ConjunctiveLicenseSet) {
				// just merge into operand1
				((ConjunctiveLicenseSet) operand1).getMembers().add(operand2);
				return operand1;
			} else {
				ConjunctiveLicenseSet retval = new ConjunctiveLicenseSet(store,
						store.getNextId(IdType.Anonymous), copyManager, true, customLicenseUriPrefix);
				retval.getMembers().add(operand1);
				retval.getMembers().add(operand2);
				return retval;
			}
		} else if (tosOperator == Operator.OR) {
			if (operand1 instanceof DisjunctiveLicenseSet) {
				// just merge into operand1
				((DisjunctiveLicenseSet) operand1).getMembers().add(operand2);
				return operand1;
			} else {
				DisjunctiveLicenseSet retval = new DisjunctiveLicenseSet(store, 
						store.getNextId(IdType.Anonymous), copyManager, true, customLicenseUriPrefix);
				retval.getMembers().add(operand1);
				retval.getMembers().add(operand2);
				return retval;
			}
		} else {
			throw new LicenseParserException("Unknown operator "+tosOperator.toString());
		}
	}
	
	/**
	 * Evaluates a binary expression and merges conjunctive and disjunctive licenses
	 * @param tosOperator binary operator
	 * @param operand1 first operand
	 * @param operand2 second operand
	 * @param copyManager copy manager to use when copying listed licenses to local store
	 * @param store model store to store non-listed licenses
	 * @param documentUri prefix to use for non-listed licenses
	 * @return resultant license representing the binary operation
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	private static org.spdx.library.model.v2.license.AnyLicenseInfo evaluateBinaryCompatV2(Operator tosOperator,
			org.spdx.library.model.v2.license.AnyLicenseInfo operand1, org.spdx.library.model.v2.license.AnyLicenseInfo operand2, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (tosOperator == Operator.AND) {
			if (operand1 instanceof org.spdx.library.model.v2.license.ConjunctiveLicenseSet) {
				// just merge into operand1
				((org.spdx.library.model.v2.license.ConjunctiveLicenseSet) operand1).addMember(operand2);
				return operand1;
			} else {
				org.spdx.library.model.v2.license.ConjunctiveLicenseSet retval = new org.spdx.library.model.v2.license.ConjunctiveLicenseSet(store, documentUri, 
						store.getNextId(IdType.Anonymous), copyManager, true);
				retval.addMember(operand1);
				retval.addMember(operand2);
				return retval;
			}
		} else if (tosOperator == Operator.OR) {
			if (operand1 instanceof org.spdx.library.model.v2.license.DisjunctiveLicenseSet) {
				// just merge into operand1
				((org.spdx.library.model.v2.license.DisjunctiveLicenseSet) operand1).addMember(operand2);
				return operand1;
			} else {
				org.spdx.library.model.v2.license.DisjunctiveLicenseSet retval = new org.spdx.library.model.v2.license.DisjunctiveLicenseSet(store, documentUri, 
						store.getNextId(IdType.Anonymous), copyManager, true);
				retval.addMember(operand1);
				retval.addMember(operand2);
				return retval;
			}
		} else {
			throw new LicenseParserException("Unknown operator "+tosOperator.toString());
		}
	}
}
