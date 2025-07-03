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
package org.spdx.library;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.DefaultModelStore;
import org.spdx.core.DefaultStoreNotInitializedException;
import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.license.InvalidLicenseStringException;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v3_0_1.core.CreationInfo;
import org.spdx.library.model.v3_0_1.core.DictionaryEntry;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicenseException;
import org.spdx.library.model.v3_0_1.simplelicensing.AnyLicenseInfo;
import org.spdx.library.model.v3_0_1.simplelicensing.InvalidLicenseExpression;
import org.spdx.storage.IModelStore;
import org.spdx.utility.license.LicenseExpressionParser;
import org.spdx.utility.license.LicenseParserException;

/**
 * Factory for creating SPDXLicenseInfo objects from a Jena model
 * 
 * @author Gary O'Neall
 */
@SuppressWarnings("unused")
public class LicenseInfoFactory {
	
	@SuppressWarnings("unused")
    static final Logger logger = LoggerFactory.getLogger(LicenseInfoFactory.class.getName());
	
	public static final String NOASSERTION_LICENSE_NAME = "NOASSERTION";
	public static final String NONE_LICENSE_NAME = "NONE";
	
	/**
	 * @param licenseId SPDX Listed License ID
	 * @return SPDX listed license or null if the ID is not in the SPDX license list
	 * @throws InvalidSPDXAnalysisException on errors getting license data
	 */
	public static ListedLicense getListedLicenseById(String licenseId)throws InvalidSPDXAnalysisException {
		return ListedLicenses.getListedLicenses().getListedLicenseById(licenseId);
	}
	
	/**
	 * @param licenseId SPDX Listed License ID
	 * @return SPDX listed license in SPDX spec version 2.X format or null if the ID is not in the SPDX license list
	 * @throws InvalidSPDXAnalysisException on errors getting license data
	 */
	public static SpdxListedLicense getListedLicenseByIdCompatV2(String licenseId)throws InvalidSPDXAnalysisException {
		return ListedLicenses.getListedLicenses().getListedLicenseByIdCompatV2(licenseId);
	}

	/**
	 * Parses a license string and converts it into a SPDXLicenseInfo object
	 * Syntax - A license set must start and end with a parenthesis "("
	 * 			A conjunctive license set will have and AND after the first
	 *				licenseInfo term
	 * 			A disjunctive license set will have an OR after the first 
	 *				licenseInfo term
	 *			If there is no And or Or, then it is converted to a simple
	 *				license type
	 *			A space or tab must be used between license ID's and the 
	 *				keywords AND and OR
	 *			A licenseID must NOT be "AND" or "OR"
	 * @param licenseString String conforming to the syntax
	 * @param store Store containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model store will be used.
	 * @param documentUri Document URI for the document containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model document URI will be used.
	 * @param copyManager allows for copying of any properties set which use other model stores or document URI's.  If null, the default will be used.
	 * @return an SPDXLicenseInfo created from the string.  If the license expression is not parseable, a <code>InvalidLicenseExpression</code> is returned.
	 * @throws DefaultStoreNotInitializedException if the default model store is not initialized
	 */
	public static org.spdx.library.model.v2.license.AnyLicenseInfo parseSPDXLicenseStringCompatV2(String licenseString, @Nullable IModelStore store, 
			@Nullable String documentUri, @Nullable IModelCopyManager copyManager) throws InvalidLicenseStringException, DefaultStoreNotInitializedException {
		if (Objects.isNull(store)) {
			store = DefaultModelStore.getDefaultModelStore();
		}
		if (Objects.isNull(documentUri)) {
			documentUri = DefaultModelStore.getDefaultDocumentUri();
		}
		if (Objects.isNull(copyManager)) {
			copyManager = DefaultModelStore.getDefaultCopyManager();
		}
		try {
			return LicenseExpressionParser.parseLicenseExpressionCompatV2(licenseString, store, documentUri, 
					copyManager);
		} catch (LicenseParserException e) {
            try {
                return new org.spdx.library.model.v2.license.InvalidLicenseExpression(store, documentUri,
                        store.getNextId(IModelStore.IdType.Anonymous), copyManager, e.getMessage(), licenseString);
            } catch (InvalidSPDXAnalysisException ex) {
                throw new RuntimeException(ex);
            }
        } catch (InvalidSPDXAnalysisException e) {
			try {
				return new org.spdx.library.model.v2.license.InvalidLicenseExpression(store, documentUri,
						store.getNextId(IModelStore.IdType.Anonymous), copyManager,
						String.format("Unexpected SPDX error parsing license string: %s", e.getMessage()), licenseString);
			} catch (InvalidSPDXAnalysisException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	/**
	 * Parses a license string and converts it into a SPDXLicenseInfo object
	 * Syntax - A license set must start and end with a parenthesis "("
	 * 			A conjunctive license set will have and AND after the first
	 *				licenseInfo term
	 * 			A disjunctive license set will have an OR after the first 
	 *				licenseInfo term
	 *			If there is no And or Or, then it is converted to a simple
	 *				license type
	 *			A space or tab must be used between license ID's and the 
	 *				keywords AND and OR
	 *			A licenseID must NOT be "AND" or "OR"
	 * @param licenseString String conforming to the syntax
	 * @param store Store containing any extractedLicenseInfos - if any extractedLicenseInfos by ID already exist, they will be used.  If
	 * none exist for an ID, they will be added.  If null, the default model store will be used.
	 * @param customLicensePrefix Prefix to use for any custom licenses or addition IDs found in the string.  If the resultant object URI does not exist
	 * for an ID, they will be added.  If null, the default model document URI + "#" will be used.
	 * @param copyManager allows for copying of any properties set which use other model stores or document URI's.  If null, the default will be used.
	 * @param customIdToUri Mapping of the id prefixes used in the license expression to the namespace preceding the external ID
	 * @return an SPDXLicenseInfo created from the string.   If the license expression is not parseable, a <code>InvalidLicenseExpression</code> is returned.
	 * @throws DefaultStoreNotInitializedException if the default model store is not initialized
	 */
	public static AnyLicenseInfo parseSPDXLicenseString(String licenseString, @Nullable IModelStore store, 
			@Nullable String customLicensePrefix, @Nullable IModelCopyManager copyManager, 
			@Nullable List<DictionaryEntry> customIdToUri) throws InvalidLicenseStringException, DefaultStoreNotInitializedException {
		if (Objects.isNull(store)) {
			store = DefaultModelStore.getDefaultModelStore();
		}
		if (Objects.isNull(customLicensePrefix)) {
			customLicensePrefix = DefaultModelStore.getDefaultDocumentUri() + "#";
		}
		if (Objects.isNull(copyManager)) {
			copyManager = DefaultModelStore.getDefaultCopyManager();
		}
		try {
			return LicenseExpressionParser.parseLicenseExpression(licenseString, store, customLicensePrefix,
					copyManager, customIdToUri);
		} catch (LicenseParserException e) {
			try {
				InvalidLicenseExpression retval = new InvalidLicenseExpression(store, store.getNextId(IModelStore.IdType.Anonymous),
						copyManager, true, customLicensePrefix);
				retval.setMessage(e.getMessage());
				return retval;
			} catch (InvalidSPDXAnalysisException e1) {
				throw new RuntimeException(e1);
			}
		} catch (InvalidSPDXAnalysisException e) {
			try {
				InvalidLicenseExpression retval = new InvalidLicenseExpression(store, store.getNextId(IModelStore.IdType.Anonymous),
						copyManager, true, customLicensePrefix);
				retval.setMessage(String.format("Unexpected SPDX error parsing license string: %s", e.getMessage()));
				return retval;
			} catch (InvalidSPDXAnalysisException e1) {
				throw new RuntimeException(e1);
			}
		}

	}

	/**
	 * Parses a license string and converts it into a SPDXLicenseInfo object
	 * Syntax - A license set must start and end with a parenthesis "("
	 * 			A conjunctive license set will have and AND after the first
	 *				licenseInfo term
	 * 			A disjunctive license set will have an OR after the first 
	 *				licenseInfo term
	 *			If there is no And or Or, then it is converted to a simple
	 *				license type
	 *			A space or tab must be used between license ID's and the 
	 *				keywords AND and OR
	 *			A licenseID must NOT be "AND" or "OR"
	 * @param licenseString String conforming to the syntax
	 * @return an SPDXLicenseInfo created from the string
	 * @throws InvalidLicenseStringException if the license string is not valid
	 * @throws DefaultStoreNotInitializedException if the default model store is not initialized
	 */
	public static AnyLicenseInfo parseSPDXLicenseString(String licenseString) throws InvalidLicenseStringException, DefaultStoreNotInitializedException {
		return parseSPDXLicenseString(licenseString, null, null, null, null);
	}
	
	/**
	 * Parses a license string and converts it into a SPDXLicenseInfo object
	 * Syntax - A license set must start and end with a parenthesis "("
	 * 			A conjunctive license set will have and AND after the first
	 *				licenseInfo term
	 * 			A disjunctive license set will have an OR after the first 
	 *				licenseInfo term
	 *			If there is no And or Or, then it is converted to a simple
	 *				license type
	 *			A space or tab must be used between license ID's and the 
	 *				keywords AND and OR
	 *			A licenseID must NOT be "AND" or "OR"
	 * @param licenseString String conforming to the syntax
	 * @return an SPDXLicenseInfo created from the string
	 * @throws InvalidLicenseStringException On invalid license expression
	 * @throws DefaultStoreNotInitializedException On the model store not being initialized - see DefaultModelStore in SPDX core package
	 */
	public static org.spdx.library.model.v2.license.AnyLicenseInfo parseSPDXLicenseStringCompatV2(String licenseString) throws InvalidLicenseStringException, DefaultStoreNotInitializedException {
		return parseSPDXLicenseStringCompatV2(licenseString, null, null, null);
	}



	/**
	 * @param licenseID case insensitive
	 * @return true if the licenseID belongs to an SPDX listed license
	 */
	public static boolean isSpdxListedLicenseId(String licenseID)  {
		return ListedLicenses.getListedLicenses().isSpdxListedLicenseId(licenseID);
	}
	
	/**
	 * @return List of all SPDX listed license IDs
	 */
	public static List<String> getSpdxListedLicenseIds() {
		return ListedLicenses.getListedLicenses().getSpdxListedLicenseIds();
	}
	
	/**
	 * @return Version of the license list being used by the SPDXLicenseInfoFactory
	 */
	public static String getLicenseListVersion() {
		return ListedLicenses.getListedLicenses().getLicenseListVersion();
	}

	/**
	 * @param id exception ID
	 * @return true if the exception ID is a supported SPDX listed exception
	 */
	public static boolean isSpdxListedExceptionId(String id) {
		return ListedLicenses.getListedLicenses().isSpdxListedExceptionId(id);
	}

	/**
	 * @param id ID for the listed exception
	 * @return the standard SPDX license exception or null if the ID is not in the SPDX license list
	 * @throws InvalidSPDXAnalysisException On SPDX parsing errors
	 */
	public static ListedLicenseException getListedExceptionById(String id) throws InvalidSPDXAnalysisException {
		return ListedLicenses.getListedLicenses().getListedExceptionById(id);
	}

	/**
	 * @param id ID for the listed exception
	 * @return the standard SPDX license exception in SPDX Spec V2.X format or null if the ID is not in the SPDX license list
	 * @throws InvalidSPDXAnalysisException On SPDX parsing errors
	 */
	public static org.spdx.library.model.v2.license.ListedLicenseException getListedExceptionV2ById(String id) throws InvalidSPDXAnalysisException {
		return ListedLicenses.getListedLicenses().getListedExceptionByIdCompatV2(id);
	}
	
	/**
	 * @param licenseId case insensitive license ID
	 * @return the case sensitive license ID
	 */
	public static Optional<String> listedLicenseIdCaseSensitive(String licenseId) {
		return ListedLicenses.getListedLicenses().listedLicenseIdCaseSensitive(licenseId);
	}

	/**
	 * @param exceptionId case insensitive exception ID
	 * @return case sensitive ID
	 */
	public static Optional<String> listedExceptionIdCaseSensitive(String exceptionId) {
		return ListedLicenses.getListedLicenses().listedExceptionIdCaseSensitive(exceptionId);
	}
	
	/**
	 * @return the CreationInfo used for all SPDX listed licenses and listed exceptions
	 * @throws InvalidSPDXAnalysisException on error inflating the creation info
	 */
	public static CreationInfo getListedLicenseCreationInfo() throws InvalidSPDXAnalysisException {
		return ListedLicenses.getListedLicenses().getListedLicenseCreationInfo();
	}

}
