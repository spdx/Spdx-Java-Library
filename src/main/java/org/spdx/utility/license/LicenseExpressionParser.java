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

import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.ModelObjectV2;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.AnyLicenseInfo;
import org.spdx.library.model.v2.license.ConjunctiveLicenseSet;
import org.spdx.library.model.v2.license.DisjunctiveLicenseSet;
import org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;
import org.spdx.library.model.v2.license.ListedLicenseException;
import org.spdx.library.model.v2.license.OrLaterOperator;
import org.spdx.library.model.v2.license.SimpleLicensingInfo;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v2.license.SpdxNoAssertionLicense;
import org.spdx.library.model.v2.license.SpdxNoneLicense;
import org.spdx.library.model.v2.license.WithExceptionOperator;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingConjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingCustomLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingDisjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingExtendableLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingListedLicenseException;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingNoAssertionLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingNoneLicense;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingOrLaterOperator;
import org.spdx.library.model.v3.expandedlicensing.ExpandedLicensingWithAdditionOperator;
import org.spdx.library.model.v3.simplelicensing.SimpleLicensingAnyLicenseInfo;
import org.spdx.storage.CompatibleModelStoreWrapper;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * A parser for the SPDX License Expressions as documented in the SPDX appendix.
 * 
 * This is a static help class.  The primary method is parseLicenseExpression which 
 * returns an AnyLicenseInfo.
 * @author Gary O'Neall
 *
 */
public class LicenseExpressionParser {

	enum Operator {
		OR_LATER, WITH, AND, OR	//NOTE: These must be in precedence order 
	};
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
	 * Parses a license expression into an license for use in the Model using the SPDX Version 3.X model
	 * @param expression Expression to be parsed
	 * @param store Store containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model store will be used.
	 * @param documentUri Document URI for the document containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model document URI will be used.
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @return the parsed license expression
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static SimpleLicensingAnyLicenseInfo parseLicenseExpression(String expression, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (expression == null || expression.trim().isEmpty()) {
			throw new LicenseParserException("Empty license expression");
		}
		Objects.requireNonNull(store, "Model store can not be null");
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		String[] tokens  = tokenizeExpression(expression);
		if (tokens.length == 1 && tokens[0].equals(SpdxConstantsCompatV2.NOASSERTION_VALUE)) {
			return new ExpandedLicensingNoAssertionLicense();
		} else if (tokens.length == 1 && tokens[0].equals(SpdxConstantsCompatV2.NONE_VALUE)) {
			return new ExpandedLicensingNoneLicense();
		} else {
			try {
				return parseLicenseExpression(tokens, store, documentUri, copyManager);
			} catch (LicenseParserException ex) {
				// Add the expression to the error message to provide additional information to the user
				throw new LicenseParserException(ex.getMessage()+" License expression: '"+expression+"'", ex);
			} catch (EmptyStackException ex) {
				throw new LicenseParserException("Invalid license expression: '"+expression+"' - check that every operator (e.g. AND and OR) has operators and that parenthesis are matched");
			}
		}
	}
	
	/**
	 * Parses a license expression into an license for use in the Model using the SPDX Version 2.X model
	 * @param expression Expression to be parsed
	 * @param store Store containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model store will be used.
	 * @param documentUri Document URI for the document containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model document URI will be used.
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @return the parsed license expression
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static AnyLicenseInfo parseLicenseExpressionCompatV2(String expression, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (expression == null || expression.trim().isEmpty()) {
			throw new LicenseParserException("Empty license expression");
		}
		Objects.requireNonNull(store, "Model store can not be null");
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		String[] tokens  = tokenizeExpression(expression);
		if (tokens.length == 1 && tokens[0].equals(SpdxConstantsCompatV2.NOASSERTION_VALUE)) {
			return new SpdxNoAssertionLicense();
		} else if (tokens.length == 1 && tokens[0].equals(SpdxConstantsCompatV2.NONE_VALUE)) {
			return new SpdxNoneLicense();
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
	 * A custom tokenizer since there is not white space between parents and pluses
	 * @param expression
	 * @return
	 */
	private static String[] tokenizeExpression(String expression) {
		String[] startTokens = expression.split("\\s");
		List<String> endTokens = new ArrayList<>();
		for (String token : startTokens) {
			processPreToken(token, endTokens);
		}
		return endTokens.toArray(new String[endTokens.size()]);
	}

	/**
	 * @param preToken
	 * @param tokenList
	 */
	private static void processPreToken(String preToken,
			List<String> tokenList) {
		if (preToken.isEmpty()) {
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
	 * @param tokens
	 * @param store
	 * @param documentUri
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static SimpleLicensingAnyLicenseInfo parseLicenseExpression(String[] tokens, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (tokens == null || tokens.length == 0) {
			throw new LicenseParserException("Expected license expression");
		}
		Stack<SimpleLicensingAnyLicenseInfo> operandStack = new Stack<SimpleLicensingAnyLicenseInfo>();
		Stack<Operator> operatorStack = new Stack<Operator>(); 
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
				operandStack.push(parseLicenseExpression(nestedTokens, store, documentUri, copyManager));
				tokenIndex = rightParenIndex + 1;		
			} else if (OPERATOR_MAP.get(token) == null) {	// assumed to be a simple licensing type
				operandStack.push(parseSimpleLicenseToken(token, store, documentUri, copyManager));
			} else {
				Operator operator = OPERATOR_MAP.get(token);
				if (operator == Operator.WITH) {
					// special processing here since With must be with an exception, not a licenseInfo
					if (!operatorStack.isEmpty() && Operator.OR_LATER.equals(operatorStack.peek())) {
						Operator tosOperator = operatorStack.pop();
						evaluateExpression(tosOperator, operandStack, store, documentUri, copyManager);
					}
					if (tokenIndex >= tokens.length) {
						throw new LicenseParserException("Missing exception clause");
					}
					token = tokens[tokenIndex++];
					//TODO Update this to handle custom additions
					ExpandedLicensingListedLicenseException licenseException = null;
					Optional<String> exceptionId = Optional.empty();
					if (LicenseInfoFactory.isSpdxListedExceptionId(token)) {
						exceptionId = LicenseInfoFactory.listedExceptionIdCaseSensitive(token);
					}
					if (exceptionId.isPresent()) {
						licenseException = LicenseInfoFactory.getListedExceptionById(exceptionId.get());
					} else if (token.startsWith(SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM)) {
						throw new LicenseParserException("WITH must be followed by a license exception. "+token+" is a Listed License type.");
					} else {
						licenseException = new ExpandedLicensingListedLicenseException(store, 
								CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, token, false),
								copyManager, true);
					}
					SimpleLicensingAnyLicenseInfo operand = operandStack.pop();
					if (operand == null) {
						throw new LicenseParserException("Missing license for with clause");
					}
					if (!(operand instanceof ExpandedLicensingExtendableLicense)) {
						throw new LicenseParserException("License with exception is not of type License or OrLaterOperator");
					}
					ExpandedLicensingWithAdditionOperator weo = new ExpandedLicensingWithAdditionOperator(store,
								store.getNextId(IdType.Anonymous), copyManager, true);
					weo.setExpandedLicensingSubjectExtendableLicense((ExpandedLicensingExtendableLicense)operand);
					weo.setExpandedLicensingSubjectAddition(licenseException);
					operandStack.push(weo);			
				} else {
					// process in order of precedence using the shunting yard algorithm
					while (!operatorStack.isEmpty() && 
							operatorStack.peek().ordinal() <= operator.ordinal()) {
						Operator tosOperator = operatorStack.pop();
						evaluateExpression(tosOperator, operandStack, store, documentUri, copyManager);
					}
					operatorStack.push(operator);
				}
			}
		}
		// go through the rest of the stack
		while (!operatorStack.isEmpty()) {
			Operator tosOperator = operatorStack.pop();
			evaluateExpression(tosOperator, operandStack, store, documentUri, copyManager);
		}
		SimpleLicensingAnyLicenseInfo retval = operandStack.pop();
		if (!operandStack.isEmpty()) {
			throw new LicenseParserException("Invalid license expression.  Expecting more operands.");
		}
		return retval;
	}
	
	/**
	 * Parses a tokenized license expression into a license for use in the RDF Parser
	 * @param tokens
	 * @param store
	 * @param documentUri
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static AnyLicenseInfo parseLicenseExpressionCompatV2(String[] tokens, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (tokens == null || tokens.length == 0) {
			throw new LicenseParserException("Expected license expression");
		}
		Stack<AnyLicenseInfo> operandStack = new Stack<AnyLicenseInfo>();
		Stack<Operator> operatorStack = new Stack<Operator>(); 
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
					ListedLicenseException licenseException = null;
					Optional<String> exceptionId = Optional.empty();
					if (LicenseInfoFactory.isSpdxListedExceptionId(token)) {
						exceptionId = LicenseInfoFactory.listedExceptionIdCaseSensitive(token);
					}
					if (exceptionId.isPresent()) {
						licenseException = LicenseInfoFactory.getListedExceptionV2ById(exceptionId.get());
					} else if (token.startsWith(SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM)) {
						throw new LicenseParserException("WITH must be followed by a license exception. "+token+" is a Listed License type.");
					} else {
						licenseException = (ListedLicenseException) org.spdx.library.model.v2.SpdxModelFactory.createModelObjectV2(store, 
								documentUri, token, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, copyManager);
					}
					AnyLicenseInfo operand = operandStack.pop();
					if (operand == null) {
						throw new LicenseParserException("Missing license for with clause");
					}
					if (!((operand instanceof SimpleLicensingInfo) || (operand instanceof OrLaterOperator))) {
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
		AnyLicenseInfo retval = operandStack.pop();
		if (!operandStack.isEmpty()) {
			throw new LicenseParserException("Invalid license expression.  Expecting more operands.");
		}
		return retval;
	}

	/**
	 * Returns the index of the rightmost parenthesis or -1 if not found
	 * @param tokens
	 * @return
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
	 * Converts a string token into its equivalent license
	 * checking for a listed license
	 * @param token
	 * @param baseStore
	 * @param documentUri 
	 * @param copyManager
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static SimpleLicensingAnyLicenseInfo parseSimpleLicenseToken(String token, IModelStore store, String documentUri,
			IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(token, "Token can not be null");
		Objects.requireNonNull(store, "Model store can not be null");
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		if (token.contains(":")) {
			// External License Ref
			return SpdxModelFactory.getExternalAnyLicenseInfo(store, documentUri + token, copyManager, SpdxModelFactory.getLatestSpecVersion());
		}
		Optional<String> licenseId = Optional.empty();
		if (LicenseInfoFactory.isSpdxListedLicenseId(token)) {	
			// listed license
			licenseId = LicenseInfoFactory.listedLicenseIdCaseSensitive(token);
		}
		ExpandedLicensingListedLicense listedLicense = LicenseInfoFactory.getListedLicenseById(licenseId.get());
		if (licenseId.isPresent()) {
			if (!store.exists(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + licenseId.get())) {
				if (Objects.nonNull(copyManager)) {
					// copy to the local store
					copyManager.copy(store, listedLicense.getObjectUri(), listedLicense.getModelStore(), 
							listedLicense.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_EXPANDED_LICENSING_LISTED_LICENSE,
							SpdxModelFactory.getLatestSpecVersion(), null);
					// copy to the local store
				}
			}
			return new ExpandedLicensingListedLicense(store, listedLicense.getObjectUri(), copyManager, true);
		} else {
			// LicenseRef
			Optional<String> caseSensitiveId = store.getCaseSensisitiveId(documentUri, token);
			ExpandedLicensingCustomLicense localLicense = null;
			if (caseSensitiveId.isPresent()) {
				localLicense = new ExpandedLicensingCustomLicense(store, documentUri + caseSensitiveId.get(), copyManager, false);
				
			} else {
				localLicense = new ExpandedLicensingCustomLicense(store, documentUri + caseSensitiveId.get(), copyManager, true);
				localLicense.setSimpleLicensingLicenseText(UNINITIALIZED_LICENSE_TEXT);
			}
			return localLicense;
		}
	}
	
	/**
	 * Converts a string token into its equivalent license
	 * checking for a listed license
	 * @param token
	 * @param baseStore
	 * @param documentUri 
	 * @param copyManager
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static AnyLicenseInfo parseSimpleLicenseTokenCompatV2(String token, IModelStore store, String documentUri,
			IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(token, "Token can not be null");
		Objects.requireNonNull(store, "Model store can not be null");
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		if (token.contains(":")) {
			// External License Ref
			return new ExternalExtractedLicenseInfo(store, documentUri, token, copyManager, true);
		} 
		Optional<String> licenseId = Optional.empty();
		if (LicenseInfoFactory.isSpdxListedLicenseId(token)) {	
			// listed license
			licenseId = LicenseInfoFactory.listedLicenseIdCaseSensitive(token);
		}
		if (licenseId.isPresent()) {
			if (!store.exists(SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX + licenseId.get())) {
				SpdxListedLicense listedLicense = LicenseInfoFactory.getListedLicenseV2ByIdCompat(licenseId.get());
				if (Objects.nonNull(copyManager)) {
					// copy to the local store
					copyManager.copy(store, listedLicense.getObjectUri(), listedLicense.getModelStore(), 
							listedLicense.getObjectUri(), SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, 
							ModelObjectV2.LATEST_SPDX_2_VERSION, listedLicense.getDocumentUri());
				}
			}
			return (AnyLicenseInfo) org.spdx.library.model.v2.SpdxModelFactory.getModelObjectV2(store, SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX, 
					licenseId.get(), SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, copyManager, true);
		} else {
			// LicenseRef
			Optional<String> caseSensitiveId = store.getCaseSensisitiveId(documentUri, token);
			ExtractedLicenseInfo localLicense = null;
			if (caseSensitiveId.isPresent()) {
				localLicense = new ExtractedLicenseInfo(store, documentUri, caseSensitiveId.get(), copyManager, false);
				
			} else {
				localLicense = (ExtractedLicenseInfo) org.spdx.library.model.v2.SpdxModelFactory.createModelObjectV2(
						store, documentUri, token, SpdxConstantsCompatV2.CLASS_SPDX_EXTRACTED_LICENSING_INFO, copyManager);
				localLicense.setExtractedText(UNINITIALIZED_LICENSE_TEXT);
			}
			return localLicense;
		}
	}
	
	/**
	 * Evaluate the given operator using paramaeters in the parameter stack
	 * @param operator
	 * @param operandStack
	 * @param copyManager
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static void evaluateExpression(Operator operator,
			Stack<SimpleLicensingAnyLicenseInfo> operandStack, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (operator == Operator.OR_LATER) {
			// unary operator
			SimpleLicensingAnyLicenseInfo license = operandStack.pop();
			if (!(license instanceof ExpandedLicensingLicense)) {
				throw new LicenseParserException("Missing license for the '+' or later operator");
			}
			ExpandedLicensingOrLaterOperator olo = new ExpandedLicensingOrLaterOperator(store, store.getNextId(IdType.Anonymous), copyManager, true);
			olo.setExpandedLicensingSubjectLicense((ExpandedLicensingLicense)license);
			operandStack.push(olo);
		} else {
			// binary operator
			SimpleLicensingAnyLicenseInfo operand2 = operandStack.pop();
			SimpleLicensingAnyLicenseInfo operand1 = operandStack.pop();
			if (operand1 == null || operand2 == null) {
				throw new LicenseParserException("Missing operands for the "+operator.toString()+" operator");
			}
			operandStack.push(evaluateBinary(operator, operand1, operand2, store, documentUri, copyManager));
		}		
	}

	/**
	 * Evaluate the given operator using paramaeters in the parameter stack
	 * @param operator
	 * @param operandStack
	 * @param copyManager
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static void evaluateExpressionCompatV2(Operator operator,
			Stack<AnyLicenseInfo> operandStack, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (operator == Operator.OR_LATER) {
			// unary operator
			AnyLicenseInfo license = operandStack.pop();
			if (!(license instanceof SimpleLicensingInfo)) {
				throw new LicenseParserException("Missing license for the '+' or later operator");
			}
			OrLaterOperator olo = new OrLaterOperator(store, documentUri, store.getNextId(IdType.Anonymous), copyManager, true);
			olo.setLicense((SimpleLicensingInfo)license);
			operandStack.push(olo);
		} else {
			// binary operator
			AnyLicenseInfo operand2 = operandStack.pop();
			AnyLicenseInfo operand1 = operandStack.pop();
			if (operand1 == null || operand2 == null) {
				throw new LicenseParserException("Missing operands for the "+operator.toString()+" operator");
			}
			operandStack.push(evaluateBinaryCompatV2(operator, operand1, operand2, store, documentUri, copyManager));
		}		
	}

	/**
	 * Evaluates a binary expression and merges conjuctive and disjunctive licenses
	 * @param tosOperator
	 * @param operand1
	 * @param operand2
	 * @param copyManager
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static SimpleLicensingAnyLicenseInfo evaluateBinary(Operator tosOperator,
			SimpleLicensingAnyLicenseInfo operand1, SimpleLicensingAnyLicenseInfo operand2, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (tosOperator == Operator.AND) {
			if (operand1 instanceof ExpandedLicensingConjunctiveLicenseSet) {
				// just merge into operand1
				((ExpandedLicensingConjunctiveLicenseSet) operand1).getExpandedLicensingMembers().add(operand2);
				return operand1;
			} else {
				ExpandedLicensingConjunctiveLicenseSet retval = new ExpandedLicensingConjunctiveLicenseSet(store,
						store.getNextId(IdType.Anonymous), copyManager, true);
				retval.getExpandedLicensingMembers().add(operand1);
				retval.getExpandedLicensingMembers().add(operand2);
				return retval;
			}
		} else if (tosOperator == Operator.OR) {
			if (operand1 instanceof ExpandedLicensingDisjunctiveLicenseSet) {
				// just merge into operand1
				((ExpandedLicensingDisjunctiveLicenseSet) operand1).getExpandedLicensingMembers().add(operand2);
				return operand1;
			} else {
				ExpandedLicensingDisjunctiveLicenseSet retval = new ExpandedLicensingDisjunctiveLicenseSet(store, 
						store.getNextId(IdType.Anonymous), copyManager, true);
				retval.getExpandedLicensingMembers().add(operand1);
				retval.getExpandedLicensingMembers().add(operand2);
				return retval;
			}
		} else {
			throw new LicenseParserException("Unknown operator "+tosOperator.toString());
		}
	}
	
	/**
	 * Evaluates a binary expression and merges conjuctive and disjunctive licenses
	 * @param tosOperator
	 * @param operand1
	 * @param operand2
	 * @param copyManager
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private static AnyLicenseInfo evaluateBinaryCompatV2(Operator tosOperator,
			AnyLicenseInfo operand1, AnyLicenseInfo operand2, IModelStore store, 
			String documentUri, IModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (tosOperator == Operator.AND) {
			if (operand1 instanceof ConjunctiveLicenseSet) {
				// just merge into operand1
				((ConjunctiveLicenseSet) operand1).addMember(operand2);
				return operand1;
			} else {
				ConjunctiveLicenseSet retval = new ConjunctiveLicenseSet(store, documentUri, 
						store.getNextId(IdType.Anonymous), copyManager, true);
				retval.addMember(operand1);
				retval.addMember(operand2);
				return retval;
			}
		} else if (tosOperator == Operator.OR) {
			if (operand1 instanceof DisjunctiveLicenseSet) {
				// just merge into operand1
				((DisjunctiveLicenseSet) operand1).addMember(operand2);
				return operand1;
			} else {
				DisjunctiveLicenseSet retval = new DisjunctiveLicenseSet(store, documentUri, 
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
