/**
 * Copyright (c) 2024 Source Auditor Inc.
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
package org.spdx.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.ListedLicenses;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.SpdxCreatorInformation;
import org.spdx.library.model.v3.ExternalElement;
import org.spdx.library.model.v3.ModelObjectV3;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.SpdxModelClassFactory;
import org.spdx.library.model.v3.core.Agent;
import org.spdx.library.model.v3.core.Annotation;
import org.spdx.library.model.v3.core.AnnotationType;
import org.spdx.library.model.v3.core.CreationInfo;
import org.spdx.library.model.v3.core.Element;
import org.spdx.library.model.v3.core.ExternalIdentifierType;
import org.spdx.library.model.v3.core.ExternalRefType;
import org.spdx.library.model.v3.core.Hash;
import org.spdx.library.model.v3.core.HashAlgorithm;
import org.spdx.library.model.v3.core.IntegrityMethod;
import org.spdx.library.model.v3.core.LifecycleScopeType;
import org.spdx.library.model.v3.core.LifecycleScopedRelationship;
import org.spdx.library.model.v3.core.NoAssertionElement;
import org.spdx.library.model.v3.core.NoneElement;
import org.spdx.library.model.v3.core.Organization;
import org.spdx.library.model.v3.core.PackageVerificationCode;
import org.spdx.library.model.v3.core.Person;
import org.spdx.library.model.v3.core.Relationship;
import org.spdx.library.model.v3.core.RelationshipCompleteness;
import org.spdx.library.model.v3.core.RelationshipType;
import org.spdx.library.model.v3.core.SpdxDocument;
import org.spdx.library.model.v3.core.Tool;
import org.spdx.library.model.v3.expandedlicensing.ConjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.CustomLicense;
import org.spdx.library.model.v3.expandedlicensing.CustomLicenseAddition;
import org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExtendableLicense;
import org.spdx.library.model.v3.expandedlicensing.ExternalLicense;
import org.spdx.library.model.v3.expandedlicensing.License;
import org.spdx.library.model.v3.expandedlicensing.LicenseAddition;
import org.spdx.library.model.v3.expandedlicensing.ListedLicense;
import org.spdx.library.model.v3.expandedlicensing.ListedLicenseException;
import org.spdx.library.model.v3.expandedlicensing.NoAssertionLicense;
import org.spdx.library.model.v3.expandedlicensing.NoneLicense;
import org.spdx.library.model.v3.expandedlicensing.OrLaterOperator;
import org.spdx.library.model.v3.expandedlicensing.WithAdditionOperator;
import org.spdx.library.model.v3.simplelicensing.AnyLicenseInfo;
import org.spdx.library.model.v3.software.ContentIdentifierType;
import org.spdx.library.model.v3.software.Snippet;
import org.spdx.library.model.v3.software.SoftwareArtifact;
import org.spdx.library.model.v3.software.SoftwarePurpose;
import org.spdx.library.model.v3.software.SpdxFile;
import org.spdx.library.model.v3.software.SpdxPackage;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.listedlicense.SpdxListedLicenseModelStore;

/**
 * @author Gary O'Neall
 * 
 * Converts SPDX spec version 2.X objects to SPDX spec version 3.X and stores the result in the
 * toModelStore
 *
 */
public class Spdx2to3Converter implements ISpdxConverter {
	
	static final Logger logger = LoggerFactory.getLogger(Spdx2to3Converter.class);
	
	static final Pattern SPDX_2_CREATOR_PATTERN = Pattern.compile("(Person|Organization):\\s*([^(]+)\\s*(\\(.+\\))?");

	private static final Map<org.spdx.library.model.v2.enumerations.RelationshipType, RelationshipType> RELATIONSHIP_TYPE_MAP;
	
	private static final Map<org.spdx.library.model.v2.enumerations.RelationshipType, LifecycleScopeType> LIFECYCLE_SCOPE_MAP;

	private static final Set<org.spdx.library.model.v2.enumerations.RelationshipType> SWAP_TO_FROM_REL_TYPES;
	
	private static final Map<org.spdx.library.model.v2.enumerations.AnnotationType, AnnotationType> ANNOTATION_TYPE_MAP;
	
	private static final Map<org.spdx.library.model.v2.enumerations.ChecksumAlgorithm, HashAlgorithm> HASH_ALGORITH_MAP;
	
	private static final Map<String, ContentIdentifierType> CONTENT_IDENTIFIER_TYPE_MAP;
	
	private static final Map<String, ExternalIdentifierType> EXTERNAL_IDENTIFIER_TYPE_MAP;
	
	private static final Map<String, ExternalRefType> EXTERNAL_REF_TYPE_MAP;
	
	private static final Map<org.spdx.library.model.v2.enumerations.Purpose, SoftwarePurpose> PURPOSE_MAP;
	
	static {
		Map<org.spdx.library.model.v2.enumerations.RelationshipType, RelationshipType> relationshipTypeMap = new HashMap<>();
		Map<org.spdx.library.model.v2.enumerations.RelationshipType, LifecycleScopeType> lifecycleScopeMap = new HashMap<>();
		Set<org.spdx.library.model.v2.enumerations.RelationshipType> swapToFromRelTypes = new HashSet<>();
		Map<org.spdx.library.model.v2.enumerations.ChecksumAlgorithm, HashAlgorithm>  hashAlgorithmMap = new HashMap<>();
		
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DESCRIBES, RelationshipType.DESCRIBES);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DESCRIBED_BY, RelationshipType.DESCRIBES);
		swapToFromRelTypes.add(org.spdx.library.model.v2.enumerations.RelationshipType.DESCRIBED_BY);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.ANCESTOR_OF, RelationshipType.ANCESTOR_OF);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.BUILD_TOOL_OF, RelationshipType.USES_TOOL);
		lifecycleScopeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.BUILD_TOOL_OF, LifecycleScopeType.BUILD);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINED_BY, RelationshipType.CONTAINS);
		swapToFromRelTypes.add(org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINED_BY);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINS, RelationshipType.CONTAINS);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.COPY_OF, RelationshipType.COPIED_TO);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DATA_FILE_OF, RelationshipType.HAS_DATA_FILE);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DESCENDANT_OF, RelationshipType.DESCENDANT_OF);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DISTRIBUTION_ARTIFACT, RelationshipType.HAS_DISTRIBUTION_ARTIFACT);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DOCUMENTATION_OF, RelationshipType.HAS_DOCUMENTATION);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DYNAMIC_LINK, RelationshipType.HAS_DYNAMIC_LINK);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.EXPANDED_FROM_ARCHIVE, RelationshipType.EXPANDS_TO);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.FILE_ADDED, RelationshipType.HAS_ADDED_FILE);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.FILE_DELETED, RelationshipType.HAS_DELETED_FILE);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.FILE_MODIFIED, RelationshipType.MODIFIED_BY);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.GENERATED_FROM, RelationshipType.GENERATES);
		swapToFromRelTypes.add(org.spdx.library.model.v2.enumerations.RelationshipType.GENERATED_FROM);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.GENERATES, RelationshipType.GENERATES);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.METAFILE_OF, RelationshipType.HAS_METADATA);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.OPTIONAL_COMPONENT_OF, RelationshipType.HAS_OPTIONAL_COMPONENT);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.OTHER, RelationshipType.OTHER);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.PACKAGE_OF, RelationshipType.PACKAGED_BY);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.PATCH_APPLIED, RelationshipType.PATCHED_BY);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.PATCH_FOR, RelationshipType.PATCHED_BY);
		swapToFromRelTypes.add(org.spdx.library.model.v2.enumerations.RelationshipType.PATCH_FOR);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.AMENDS, RelationshipType.AMENDED_BY);
		swapToFromRelTypes.add(org.spdx.library.model.v2.enumerations.RelationshipType.AMENDS);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.STATIC_LINK, RelationshipType.HAS_STATIC_LINK);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.TEST_CASE_OF, RelationshipType.HAS_TEST_CASE);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.PREREQUISITE_FOR, RelationshipType.HAS_PREREQUSITE);
		swapToFromRelTypes.add(org.spdx.library.model.v2.enumerations.RelationshipType.PREREQUISITE_FOR);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.HAS_PREREQUISITE, RelationshipType.HAS_PREREQUSITE);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.VARIANT_OF, RelationshipType.HAS_VARIANT);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.BUILD_DEPENDENCY_OF, RelationshipType.DEPENDS_ON);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDENCY_MANIFEST_OF, RelationshipType.HAS_DEPENDENCY_MANIFEST);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDENCY_OF, RelationshipType.DEPENDS_ON);
		swapToFromRelTypes.add(org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDENCY_OF);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDS_ON, RelationshipType.DEPENDS_ON);
		lifecycleScopeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDS_ON, LifecycleScopeType.BUILD);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DEV_DEPENDENCY_OF, RelationshipType.DEPENDS_ON);
		lifecycleScopeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DEV_DEPENDENCY_OF, LifecycleScopeType.DEVELOPMENT);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DEV_TOOL_OF, RelationshipType.USES_TOOL);
		lifecycleScopeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.DEV_TOOL_OF, LifecycleScopeType.DEVELOPMENT);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.EXAMPLE_OF, RelationshipType.HAS_EXAMPLE);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.OPTIONAL_DEPENDENCY_OF, RelationshipType.HAS_OPTIONAL_DEPENDENCY);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.PROVIDED_DEPENDENCY_OF, RelationshipType.HAS_PROVIDED_DEPENDENCY);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.RUNTIME_DEPENDENCY_OF, RelationshipType.DEPENDS_ON);
		lifecycleScopeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.RUNTIME_DEPENDENCY_OF, LifecycleScopeType.RUNTIME);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.TEST_DEPENDENCY_OF, RelationshipType.DEPENDS_ON);
		lifecycleScopeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.TEST_DEPENDENCY_OF, LifecycleScopeType.TEST);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.TEST_OF, RelationshipType.HAS_TEST);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.TEST_TOOL_OF, RelationshipType.USES_TOOL);
		lifecycleScopeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.TEST_TOOL_OF, LifecycleScopeType.TEST);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.REQUIREMENT_DESCRIPTION_FOR, RelationshipType.HAS_REQUIREMENT);
		relationshipTypeMap.put(org.spdx.library.model.v2.enumerations.RelationshipType.SPECIFICATION_FOR, RelationshipType.HAS_SPECIFICATION);
		
		RELATIONSHIP_TYPE_MAP = Collections.unmodifiableMap(relationshipTypeMap);
		SWAP_TO_FROM_REL_TYPES = Collections.unmodifiableSet(swapToFromRelTypes);
		LIFECYCLE_SCOPE_MAP = Collections.unmodifiableMap(lifecycleScopeMap);
		
		Map<org.spdx.library.model.v2.enumerations.AnnotationType, AnnotationType> annotationTypeMap = new HashMap<>();
		annotationTypeMap.put(org.spdx.library.model.v2.enumerations.AnnotationType.OTHER, AnnotationType.OTHER);
		annotationTypeMap.put(org.spdx.library.model.v2.enumerations.AnnotationType.REVIEW, AnnotationType.REVIEW);
		ANNOTATION_TYPE_MAP = Collections.unmodifiableMap(annotationTypeMap);
		
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.ADLER32, HashAlgorithm.OTHER);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.BLAKE2b_256, HashAlgorithm.BLAKE2B256);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.BLAKE2b_384, HashAlgorithm.BLAKE2B384);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.BLAKE2b_512, HashAlgorithm.BLAKE2B512);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.BLAKE3, HashAlgorithm.BLAKE3);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.MD2, HashAlgorithm.MD2);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.MD4, HashAlgorithm.MD4);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.MD5, HashAlgorithm.MD5);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.MD6, HashAlgorithm.MD6);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1, HashAlgorithm.SHA1);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA224, HashAlgorithm.SHA224);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA256, HashAlgorithm.SHA256);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA384, HashAlgorithm.SHA384);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA3_256, HashAlgorithm.SHA3_256);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA3_384, HashAlgorithm.SHA3_384);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA3_512, HashAlgorithm.SHA3_512);
		hashAlgorithmMap.put(org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA512, HashAlgorithm.SHA512);
		
		HASH_ALGORITH_MAP = Collections.unmodifiableMap(hashAlgorithmMap);
		
		Map<String, ContentIdentifierType> contentIdentifierTypeMap = new HashMap<>();
		contentIdentifierTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "gitoid", ContentIdentifierType.GITOID);
		contentIdentifierTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "swh", ContentIdentifierType.SWHID);
		
		CONTENT_IDENTIFIER_TYPE_MAP = Collections.unmodifiableMap(contentIdentifierTypeMap);
		
		Map<String, ExternalIdentifierType> externalIdentifierTypeMap = new HashMap<>();
		externalIdentifierTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "cpe22Type", ExternalIdentifierType.CPE22);
		externalIdentifierTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "cpe23Type", ExternalIdentifierType.CPE23);
		externalIdentifierTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "swid", ExternalIdentifierType.SWID);
		externalIdentifierTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "purl", ExternalIdentifierType.PACKAGE_URL);
		
		EXTERNAL_IDENTIFIER_TYPE_MAP = Collections.unmodifiableMap(externalIdentifierTypeMap);
		
		Map<String, ExternalRefType> externalRefTypeMap = new HashMap<>();
		externalRefTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "maven-central", ExternalRefType.MAVEN_CENTRAL);
		externalRefTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "npm", ExternalRefType.NPM);
		externalRefTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "nuget", ExternalRefType.NUGET);
		externalRefTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "bower", ExternalRefType.BOWER);
		externalRefTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "advisory", ExternalRefType.SECURITY_ADVISORY);
		externalRefTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "fix", ExternalRefType.SECURITY_FIX);
		externalRefTypeMap.put(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "url", ExternalRefType.SECURITY_OTHER);
		
		EXTERNAL_REF_TYPE_MAP = Collections.unmodifiableMap(externalRefTypeMap);
		
		Map<org.spdx.library.model.v2.enumerations.Purpose, SoftwarePurpose> purposeMap = new HashMap<>();
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.APPLICATION, SoftwarePurpose.APPLICATION);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.ARCHIVE, SoftwarePurpose.ARCHIVE);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.CONTAINER, SoftwarePurpose.CONTAINER);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.DEVICE, SoftwarePurpose.DEVICE);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.FILE, SoftwarePurpose.FILE);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.FIRMWARE, SoftwarePurpose.FIRMWARE);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.FRAMEWORK, SoftwarePurpose.FRAMEWORK);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.INSTALL, SoftwarePurpose.INSTALL);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.LIBRARY, SoftwarePurpose.LIBRARY);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.OPERATING_SYSTEM, SoftwarePurpose.OPERATING_SYSTEM);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.OTHER, SoftwarePurpose.OTHER);
		purposeMap.put(org.spdx.library.model.v2.enumerations.Purpose.SOURCE, SoftwarePurpose.SOURCE);
		
		PURPOSE_MAP = Collections.unmodifiableMap(purposeMap);
	}
	
	String toSpecVersion;
	IModelStore toModelStore;
	Map<String, String> alreadyConverted = Collections.synchronizedMap(new HashMap<>());
	CreationInfo defaultCreationInfo;
	String defaultUriPrefix;

	private ModelCopyManager copyManager;

	private int documentIndex = 0;

	/**
	 * @param creationInfoV2 SPDX Spec version 2 creation info
	 * @param modelStore modelStore to store the CreationInfo
	 * @param uriPrefix Prefix to use for the created Agents and Tools
	 * @return SPDX spec version 3 CreationInfo
	 * @throws InvalidSPDXAnalysisException on error converting
	 */
	public static CreationInfo convertCreationInfo(
			SpdxCreatorInformation creationInfoV2,
			IModelStore modelStore,
			String uriPrefix
			) throws InvalidSPDXAnalysisException {
		List<String> toolCreators = new ArrayList<>();
		List<String> agentCreators = new ArrayList<>();
		for (String docCreator:creationInfoV2.getCreators()) {
			if (docCreator.startsWith(SpdxConstantsCompatV2.CREATOR_PREFIX_TOOL)) {
				toolCreators.add(docCreator.substring(SpdxConstantsCompatV2.CREATOR_PREFIX_TOOL.length()).trim());
			} else if (docCreator.startsWith(SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON)) {
				agentCreators.add(docCreator.substring(SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON.length()).trim());
			} else if (docCreator.startsWith(SpdxConstantsCompatV2.CREATOR_PREFIX_ORGANIZATION)) {
				agentCreators.add(docCreator.substring(SpdxConstantsCompatV2.CREATOR_PREFIX_ORGANIZATION.length()).trim());
			} else {
				logger.warn("Invalid creator string in from document: "+docCreator);
				agentCreators.add(docCreator.trim());
			}
		}
		
		if (agentCreators.isEmpty()) {
			logger.warn("Missing person or organization creator from SPDX 2.X version");
		}
		CreationInfo retval = SpdxModelClassFactory.createCreationInfo(modelStore, 
						uriPrefix + "createdBy",
						agentCreators.isEmpty() ? "[MISSING SPDX V2 CREATOR]" : agentCreators.get(0),
						null);
		for (int i = 1; i < agentCreators.size(); i++) {
			Agent agent = (Agent) SpdxModelClassFactory.getModelObject(modelStore, 
					uriPrefix + "additionalCreator" + i,
					SpdxConstantsV3.CORE_AGENT, null, true, uriPrefix);
			agent.setCreationInfo(retval)
				.setName(agentCreators.get(i))
				.setIdPrefix(uriPrefix);
			retval.getCreatedBys().add(agent);
		}
		int toolIndex = 0;
		for (String toolName : toolCreators) {
			Tool tool = (Tool)SpdxModelClassFactory.getModelObject(modelStore, 
					uriPrefix + "additionalTool" + toolIndex++,
					SpdxConstantsV3.CORE_TOOL, null, true, uriPrefix);
			tool.setCreationInfo(retval)
				.setName(toolName)
				.setIdPrefix(uriPrefix);
			retval.getCreatedUsings().add(tool);
		}
		return retval;
	}
	
	/**
	 * @param toModelStore modelStore to store any converted elements to
	 * @param copyManager Copy manager to use for the conversion
	 * @param defaultCreationInfo creationInfo to use for created SPDX elements
	 * @param toSpecVersion specific spec version to convert to
	 * @param defaultUriPrefix URI prefix to use when creating new elements
	 */
	public Spdx2to3Converter(IModelStore toModelStore, ModelCopyManager copyManager, CreationInfo defaultCreationInfo,
			String toSpecVersion, String defaultUriPrefix) {
		this.toModelStore = toModelStore;
		this.defaultCreationInfo = defaultCreationInfo;
		this.toSpecVersion = toSpecVersion;
		this.defaultUriPrefix = defaultUriPrefix;
		this.copyManager = copyManager;
	}
	
	/**
	 * @param fromObjectUri object URI of the SPDX object copied from
	 * @return true if the SPDX object has already been copied
	 */
	public boolean alreadyCopied(String fromObjectUri) {
		return this.alreadyConverted.containsKey(fromObjectUri);
	}
	
	/**
	 * Coy all element properties from the SPDX spec version 2 element to the SPDX version 3 element
	 * @param fromElement SPDX spec version 2 SpdxElement
	 * @param toElement SPDX spec version 3 element
	 * @throws InvalidSPDXAnalysisException on any errors converting element properties
	 */
	public void convertElementProperties(org.spdx.library.model.v2.SpdxElement fromElement, Element toElement) throws InvalidSPDXAnalysisException {
		toElement.setCreationInfo(defaultCreationInfo);
		for (org.spdx.library.model.v2.Annotation fromAnnotation:fromElement.getAnnotations()) {
			convertAndStore(fromAnnotation, toElement);
		}
		toElement.setComment(fromElement.getComment().orElse(null));
		toElement.setName(fromElement.getName().orElse(null));
		for (org.spdx.library.model.v2.Relationship fromRelationship:fromElement.getRelationships()) {
			convertAndStore(fromRelationship, toElement);
		}
	}

	/**
	 * @param fromObjectUri Object URI of the SPDX spec version 2 object being converted from
	 * @param toType SPDX spec version 3 type
	 * @return optional of the existing object - if it exists
	 * @throws InvalidSPDXAnalysisException if there is an error creating the existing model object
	 */
	Optional<ModelObjectV3> getExistingObject(String fromObjectUri, String toType) throws InvalidSPDXAnalysisException {
		String toObjectUri = alreadyConverted.get(fromObjectUri);
		if (Objects.isNull(toObjectUri)) {
			return Optional.empty();
		} else {
			return Optional.of(SpdxModelClassFactory.getModelObject(toModelStore, 
					toObjectUri, toType, copyManager, false, defaultUriPrefix));
		}
	}
	
	/**
	 * Converts an SPDX spec version 2 relationship to an SPDX spec version 3 relationship
	 * @param fromRelationship relationship to convert from
	 * @param containingElement Element which contains the property referring to the fromRelationship
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private Relationship convertAndStore(org.spdx.library.model.v2.Relationship fromRelationship,
			Element containingElement) throws InvalidSPDXAnalysisException {
		org.spdx.library.model.v2.enumerations.RelationshipType fromRelationshipType = fromRelationship.getRelationshipType();
		LifecycleScopeType scope = LIFECYCLE_SCOPE_MAP.get(fromRelationshipType);
		String fromUri = fromRelationship.getObjectUri();
		Optional<ModelObjectV3> existing = getExistingObject(fromUri, 
				Objects.isNull(scope) ? SpdxConstantsV3.CORE_RELATIONSHIP : SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP);
		if (existing.isPresent()) {
			return (Relationship)existing.get();
		}
		String toObjectUri = defaultUriPrefix = toModelStore.getNextId(IdType.SpdxId);
		String exitingUri = alreadyConverted.putIfAbsent(fromUri, toObjectUri);
		if (Objects.nonNull(exitingUri)) {
			return (Relationship)getExistingObject(fromUri, 
					Objects.isNull(scope) ? SpdxConstantsV3.CORE_RELATIONSHIP : SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP).get();
		}
		Relationship toRelationship;
		if (Objects.isNull(scope)) {
			toRelationship = (Relationship)SpdxModelClassFactory.getModelObject(toModelStore, 
					toObjectUri, SpdxConstantsV3.CORE_RELATIONSHIP, copyManager, true, defaultUriPrefix);
		} else {
			toRelationship = (LifecycleScopedRelationship)SpdxModelClassFactory.getModelObject(toModelStore, 
					toObjectUri, SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP, copyManager, true, defaultUriPrefix);
		}
		toRelationship.setCreationInfo(defaultCreationInfo);
		toRelationship.setRelationshipType(RELATIONSHIP_TYPE_MAP.get(fromRelationshipType));
		if (SWAP_TO_FROM_REL_TYPES.contains(fromRelationshipType)) {
			toRelationship.getTos().add(containingElement);
		} else {
			toRelationship.setFrom(containingElement);
		}
		toRelationship.setFrom(containingElement);
		toRelationship.setComment(fromRelationship.getComment().orElse(null));
		Optional<org.spdx.library.model.v2.SpdxElement> relatedSpdxElement = fromRelationship.getRelatedSpdxElement();
		RelationshipCompleteness completeness = RelationshipCompleteness.NO_ASSERTION;
		if (relatedSpdxElement.isPresent() && relatedSpdxElement.get() instanceof org.spdx.library.model.v2.SpdxNoneElement) {
			completeness = RelationshipCompleteness.COMPLETE;
		}
		toRelationship.setCompleteness(completeness);
		if (relatedSpdxElement.isPresent() && 
				!(relatedSpdxElement.get() instanceof org.spdx.library.model.v2.SpdxNoneElement)) {
			if (SWAP_TO_FROM_REL_TYPES.contains(fromRelationshipType)) {
			toRelationship.setFrom(convertAndStore(relatedSpdxElement.get()));
			} else {
				toRelationship.getTos().add(convertAndStore(relatedSpdxElement.get()));
			}
		}
		if (Objects.nonNull(scope)) {
			((LifecycleScopedRelationship)toRelationship).setScope(scope);
		}
		toRelationship.setCreationInfo(defaultCreationInfo);
		return toRelationship;
	}

	/**
	 * Converts an SPDX spec version 2 annotation to an SPDX spec version 3 annotation
	 * @param fromAnnotation annotation to convert from
	 * @param containingElement Element which contains the property referring to the fromAnnotation
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private Annotation convertAndStore(org.spdx.library.model.v2.Annotation fromAnnotation, Element toElement) throws InvalidSPDXAnalysisException {
		String fromUri = fromAnnotation.getObjectUri();
		Optional<ModelObjectV3> existing = getExistingObject(fromUri, SpdxConstantsV3.CORE_ANNOTATION);
		if (existing.isPresent()) {
			return (Annotation)existing.get();
		}
		String toObjectUri = defaultUriPrefix = toModelStore.getNextId(IdType.SpdxId);
		String exitingUri = alreadyConverted.putIfAbsent(fromUri, toObjectUri);
		if (Objects.nonNull(exitingUri)) {
			return (Annotation)getExistingObject(fromUri, SpdxConstantsV3.CORE_ANNOTATION).get();
		}
		Annotation toAnnotation = (Annotation)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.CORE_ANNOTATION, copyManager, true, defaultUriPrefix);
		
		toAnnotation.setAnnotationType(ANNOTATION_TYPE_MAP.get(fromAnnotation.getAnnotationType()));
		toAnnotation.setStatement(fromAnnotation.getComment());
		toAnnotation.setSubject(toElement);
		String fromAnnotationDate = fromAnnotation.getAnnotationDate();
		CreationInfo creationInfo = defaultCreationInfo;
		if (Objects.nonNull(fromAnnotationDate) &&
				!Objects.equals(fromAnnotationDate, defaultCreationInfo.getCreated())) {
			// Create a new creation info with the annotation date
			creationInfo = toAnnotation.createCreationInfo(toModelStore.getNextId(IdType.Anonymous))
					.setCreated(fromAnnotationDate)
					.addAllCreatedBy(defaultCreationInfo.getCreatedBys())
					.addAllCreatedUsing(defaultCreationInfo.getCreatedUsings())
					.build();
		}
		toAnnotation.setCreationInfo(creationInfo);
		return toAnnotation;
	}

	/**
	 * Converts an SPDX spec version 2 SPDX document to an SPDX spec version 3 SPDX document and store the result
	 * in the toStore
	 * @param fromDoc SPDX spec version 2 document to convert from
	 * @return SPDX spec version 3 document converted from the version 2 document
	 * @throws InvalidSPDXAnalysisException on any errors converting the SPDX document
	 */
	public SpdxDocument convertAndStore(org.spdx.library.model.v2.SpdxDocument fromDoc) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromDoc.getObjectUri(), SpdxConstantsV3.CORE_SPDX_DOCUMENT);
		if (existing.isPresent()) {
			return (SpdxDocument)existing.get();
		}
		String toObjectUri = defaultUriPrefix + "document" + documentIndex++;
		String existingUri = this.alreadyConverted.putIfAbsent(fromDoc.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (SpdxDocument)getExistingObject(fromDoc.getObjectUri(), SpdxConstantsV3.CORE_SPDX_DOCUMENT).get();
		} 
		SpdxDocument toDoc = (SpdxDocument)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.CORE_SPDX_DOCUMENT, copyManager, true, defaultUriPrefix);
		convertElementProperties(fromDoc, toDoc);
		toDoc.setCreationInfo(convertCreationInfo(fromDoc.getCreationInfo(), this.toModelStore, this.defaultUriPrefix));
		toDoc.setDataLicense(convertAndStore(fromDoc.getDataLicense()));
		toDoc.getRootElements().addAll(fromDoc.getDocumentDescribes().stream().map(spdxElement -> {
			try {
				return convertAndStore(spdxElement);
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Error converting SPDX elements from spec version 2 to spec version 3", e);
				throw new RuntimeException(e);
			}
		}
						).collect(Collectors.toList()));
		//NOTE: We're not converting the getExternalDocumentRefs since they reference SPDX version 2 objects
		//TODO: Look into converting to the namespace map and external maps
		return toDoc;
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX ConjunctiveLicenseSet to an SPDX spec version 3 SPDX ConjunctiveLicenseSet and store the result
	 * in the toStore
	 * @param fromConjunctiveLicenseSet an SPDX spec version 2 ConjunctiveLicenseSet to convert from
	 * @return an SPDX spec version 3 ConjunctiveLicenseSet
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public ConjunctiveLicenseSet convertAndStore(org.spdx.library.model.v2.license.ConjunctiveLicenseSet fromConjunctiveLicenseSet) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromConjunctiveLicenseSet.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_CONJUNCTIVE_LICENSE_SET);
		if (existing.isPresent()) {
			return (ConjunctiveLicenseSet)existing.get();
		}
		String toObjectUri = toModelStore.getNextId(IdType.Anonymous);
		String existingUri = this.alreadyConverted.putIfAbsent(fromConjunctiveLicenseSet.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (ConjunctiveLicenseSet)getExistingObject(fromConjunctiveLicenseSet.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_CONJUNCTIVE_LICENSE_SET).get();
		} 
		ConjunctiveLicenseSet toConjunctiveLicenseSet = (ConjunctiveLicenseSet)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.EXPANDED_LICENSING_CONJUNCTIVE_LICENSE_SET, copyManager, true, defaultUriPrefix);
		for (org.spdx.library.model.v2.license.AnyLicenseInfo fromMember:fromConjunctiveLicenseSet.getMembers()) {
			toConjunctiveLicenseSet.getMembers().add(convertAndStore(fromMember));
		}
		toConjunctiveLicenseSet.setCreationInfo(defaultCreationInfo);
		return toConjunctiveLicenseSet;
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX DisjunctiveLicenseSet to an SPDX spec version 3 SPDX DisjunctiveLicenseSet and store the result
	 * in the toStore
	 * @param fromDisjunctiveLicenseSet an SPDX spec version 2 DisjunctiveLicenseSet to convert from
	 * @return an SPDX spec version 3 DisjunctiveLicenseSet
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public DisjunctiveLicenseSet convertAndStore(org.spdx.library.model.v2.license.DisjunctiveLicenseSet fromDisjunctiveLicenseSet) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromDisjunctiveLicenseSet.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_DISJUNCTIVE_LICENSE_SET);
		if (existing.isPresent()) {
			return (DisjunctiveLicenseSet)existing.get();
		}
		String toObjectUri = toModelStore.getNextId(IdType.Anonymous);
		String existingUri = this.alreadyConverted.putIfAbsent(fromDisjunctiveLicenseSet.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (DisjunctiveLicenseSet)getExistingObject(fromDisjunctiveLicenseSet.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_DISJUNCTIVE_LICENSE_SET).get();
		} 
		DisjunctiveLicenseSet toDisjunctiveLicenseSet = (DisjunctiveLicenseSet)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.EXPANDED_LICENSING_DISJUNCTIVE_LICENSE_SET, copyManager, true, defaultUriPrefix);
		for (org.spdx.library.model.v2.license.AnyLicenseInfo fromMember:fromDisjunctiveLicenseSet.getMembers()) {
			toDisjunctiveLicenseSet.getMembers().add(convertAndStore(fromMember));
		}
		toDisjunctiveLicenseSet.setCreationInfo(defaultCreationInfo);
		return toDisjunctiveLicenseSet;
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX ExtractedLicenseInfo to an SPDX spec version 3 SPDX CustomLicense and store the result
	 * in the toStore
	 * @param fromExtractedLicenseInfo an SPDX spec version 2 ExtractedLicenseInfo to convert from
	 * @return an SPDX spec version 3 CustomLicense
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public CustomLicense convertAndStore(org.spdx.library.model.v2.license.ExtractedLicenseInfo fromExtractedLicenseInfo) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromExtractedLicenseInfo.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE);
		if (existing.isPresent()) {
			return (CustomLicense)existing.get();
		}
		String toObjectUri = defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId);
		String existingUri = this.alreadyConverted.putIfAbsent(fromExtractedLicenseInfo.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (CustomLicense)getExistingObject(fromExtractedLicenseInfo.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE).get();
		} 
		CustomLicense toCustomLicense = (CustomLicense)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE, copyManager, true, defaultUriPrefix);
		toCustomLicense.setCreationInfo(defaultCreationInfo);
		toCustomLicense.setLicenseText(fromExtractedLicenseInfo.getExtractedText());
		toCustomLicense.setName(fromExtractedLicenseInfo.getName());
		toCustomLicense.getSeeAlsos().addAll(fromExtractedLicenseInfo.getSeeAlso());
		return toCustomLicense;
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX OrLaterOperator to an SPDX spec version 3 SPDX OrLaterOperator and store the result
	 * in the toStore
	 * @param fromOrLaterOperator an SPDX spec version 2 OrLaterOperator to convert from
	 * @return an SPDX spec version 3 OrLaterOperator
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public OrLaterOperator convertAndStore(org.spdx.library.model.v2.license.OrLaterOperator fromOrLaterOperator) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromOrLaterOperator.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_OR_LATER_OPERATOR);
		if (existing.isPresent()) {
			return (OrLaterOperator)existing.get();
		}
		String toObjectUri = toModelStore.getNextId(IdType.Anonymous);
		String existingUri = this.alreadyConverted.putIfAbsent(fromOrLaterOperator.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (OrLaterOperator)getExistingObject(fromOrLaterOperator.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_OR_LATER_OPERATOR).get();
		} 
		OrLaterOperator toOrLaterOperator = (OrLaterOperator)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.EXPANDED_LICENSING_OR_LATER_OPERATOR, copyManager, true, defaultUriPrefix);
		toOrLaterOperator.setCreationInfo(defaultCreationInfo);
		toOrLaterOperator.setSubjectLicense((License)convertAndStore(fromOrLaterOperator.getLicense()));
		return toOrLaterOperator;
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX SpdxListedLicense to an SPDX spec version 3 SPDX ListedLicense and store the result
	 * in the toStore
	 * @param fromSpdxListedLicense an SPDX spec version 2 SpdxListedLicense to convert from
	 * @return an SPDX spec version 3 ListedLicense
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public ListedLicense convertAndStore(org.spdx.library.model.v2.license.SpdxListedLicense fromSpdxListedLicense) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromSpdxListedLicense.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE);
		if (existing.isPresent()) {
			return (ListedLicense)existing.get();
		}
		String existingUri = this.alreadyConverted.putIfAbsent(fromSpdxListedLicense.getObjectUri(), fromSpdxListedLicense.getObjectUri());
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (ListedLicense)getExistingObject(fromSpdxListedLicense.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE).get();
		}
		String licenseId = SpdxListedLicenseModelStore.objectUriToLicenseOrExceptionId(fromSpdxListedLicense.getObjectUri());
		if (ListedLicenses.getListedLicenses().isSpdxListedLicenseId(licenseId)) {
			ListedLicense retval = ListedLicenses.getListedLicenses().getListedLicenseById(licenseId);
			copyManager.copy(toModelStore, fromSpdxListedLicense.getObjectUri(), retval.getModelStore(),
					fromSpdxListedLicense.getObjectUri(), toSpecVersion, null);
			return retval;
		}
		ListedLicense toListedLicense = (ListedLicense)SpdxModelClassFactory.getModelObject(toModelStore, 
				fromSpdxListedLicense.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE, copyManager, true, defaultUriPrefix);
		toListedLicense.setCreationInfo(defaultCreationInfo);
		toListedLicense.setComment(fromSpdxListedLicense.getComment());
		// fromSpdxListedLicense.getCrossRef()) - no equivalent in SPDX version 3.X
		toListedLicense.setDeprecatedVersion(fromSpdxListedLicense.getDeprecatedVersion());
		toListedLicense.setIsFsfLibre(fromSpdxListedLicense.getFsfLibre());
		// fromSpdxListedLicense.getLicenseHeaderHtml();  - no equivalent in SPDX version 3.X
		toListedLicense.setLicenseText(fromSpdxListedLicense.getLicenseText());
		// fromSpdxListedLicense.getLicenseTextHtml();   - no equivalent in SPDX version 3.X
		toListedLicense.setName(fromSpdxListedLicense.getName());
		toListedLicense.getSeeAlsos().addAll(fromSpdxListedLicense.getSeeAlso());
		toListedLicense.setStandardLicenseHeader(fromSpdxListedLicense.getStandardLicenseHeader());
		// fromSpdxListedLicense.getStandardLicenseHeaderTemplate(); - no equivalent in SPDX version 3.X
		toListedLicense.setStandardLicenseTemplate(fromSpdxListedLicense.getStandardLicenseTemplate());
		return toListedLicense;
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX WithExceptionOperator to an SPDX spec version 3 SPDX WithAdditionOperator and store the result
	 * in the toStore
	 * @param fromWithExceptionOperator an SPDX spec version 2 WithExceptionOperator to convert from
	 * @return an SPDX spec version 3 WithAdditionOperator
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public WithAdditionOperator convertAndStore(org.spdx.library.model.v2.license.WithExceptionOperator fromWithExceptionOperator) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromWithExceptionOperator.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_WITH_ADDITION_OPERATOR);
		if (existing.isPresent()) {
			return (WithAdditionOperator)existing.get();
		}
		String toObjectUri = toModelStore.getNextId(IdType.Anonymous);
		String existingUri = this.alreadyConverted.putIfAbsent(fromWithExceptionOperator.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (WithAdditionOperator)getExistingObject(fromWithExceptionOperator.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_WITH_ADDITION_OPERATOR).get();
		} 
		WithAdditionOperator toWithAdditionOperator = (WithAdditionOperator)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.EXPANDED_LICENSING_WITH_ADDITION_OPERATOR, copyManager, true, defaultUriPrefix);
		toWithAdditionOperator.setCreationInfo(defaultCreationInfo);
		toWithAdditionOperator.setSubjectAddition(convertAndStore(fromWithExceptionOperator.getException()));
		toWithAdditionOperator.setSubjectExtendableLicense((ExtendableLicense)convertAndStore(fromWithExceptionOperator.getLicense()));
		return toWithAdditionOperator;
	}
	

	/**
	 * Converts an SPDX spec version 2 SPDX LicenseException to an SPDX spec version 3 LicenseAddition  and store the result
	 * in the toStore
	 * @param fromException an SPDX spec version 2 LicenseException to convert from
	 * @return an SPDX spec version 3 LicenseAddition
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	private LicenseAddition convertAndStore(org.spdx.library.model.v2.license.LicenseException fromException) throws InvalidSPDXAnalysisException {
		if (fromException instanceof org.spdx.library.model.v2.license.ListedLicenseException) {
			return convertAndStore((org.spdx.library.model.v2.license.ListedLicenseException)fromException);
		}
		Optional<ModelObjectV3> existing = getExistingObject(fromException.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE_ADDITION);
		if (existing.isPresent()) {
			return (CustomLicenseAddition)existing.get();
		}
		String toObjectUri = toModelStore.getNextId(IdType.Anonymous);
		String existingUri = this.alreadyConverted.putIfAbsent(fromException.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (CustomLicenseAddition)getExistingObject(fromException.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE_ADDITION).get();
		} 
		CustomLicenseAddition toCustomAddition = (CustomLicenseAddition)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE_ADDITION, copyManager, true, defaultUriPrefix);
		convertLicenseAdditionProperties(fromException, toCustomAddition);
		return toCustomAddition;
	}
	
	/**
	 * Convert and add properties from the fromException to the toAddition
	 * @param fromException SPDX spec verion 2 LicenseException to copy properties from
	 * @param toAddtion SPDX spec version 3 LicenseAddition to copy properties to
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	private void convertLicenseAdditionProperties(
			org.spdx.library.model.v2.license.LicenseException fromException,
			LicenseAddition toAddition) throws InvalidSPDXAnalysisException {
		toAddition.setCreationInfo(defaultCreationInfo);
		toAddition.setAdditionText(fromException.getLicenseExceptionText());
		toAddition.setComment(fromException.getComment());
		toAddition.setName(fromException.getName());
		toAddition.setStandardAdditionTemplate(fromException.getLicenseExceptionTemplate());
		toAddition.getSeeAlsos().addAll(fromException.getSeeAlso());
	}

	/**
	 * Converts an SPDX spec version 2 SPDX ListedLicenseException to an SPDX spec version 3 ListedLicenseAddition  and store the result
	 * in the toStore
	 * @param fromException an SPDX spec version 2 ListedLicenseException to convert from
	 * @return an SPDX spec version 3 ListedLicenseException
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	private ListedLicenseException convertAndStore(org.spdx.library.model.v2.license.ListedLicenseException fromException) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromException.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION);
		if (existing.isPresent()) {
			return (ListedLicenseException)existing.get();
		}
		String existingUri = this.alreadyConverted.putIfAbsent(fromException.getObjectUri(), fromException.getObjectUri());
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (ListedLicenseException)getExistingObject(fromException.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION).get();
		}
		String exceptionId = SpdxListedLicenseModelStore.objectUriToLicenseOrExceptionId(fromException.getObjectUri());
		if (ListedLicenses.getListedLicenses().isSpdxListedExceptionId(exceptionId)) {
			ListedLicenseException retval = ListedLicenses.getListedLicenses().getListedExceptionById(exceptionId);
			copyManager.copy(toModelStore, fromException.getObjectUri(), retval.getModelStore(),
					fromException.getObjectUri(), toSpecVersion, null);
			return retval;
		}
		ListedLicenseException toListedException = (ListedLicenseException)SpdxModelClassFactory.getModelObject(toModelStore, 
				fromException.getObjectUri(), SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION, copyManager, true, defaultUriPrefix);
		convertLicenseAdditionProperties(fromException, toListedException);
		toListedException.setDeprecatedVersion(fromException.getDeprecatedVersion());		
		// fromException.getExample(); - no SPDX spec version 3 equivalent
		// fromException.getExceptionTextHtml(); - no SPDX spec version 3 equivalent
		return toListedException;
	}

	/**
	 * Converts an SPDX spec version 2 SPDX AnyLicenseIfno to an SPDX spec version 3 SPDX AnyLicenseIfno and store the result
	 * @param fromLicense an SPDX spec version 2 AnyLicenseIfno
	 * @return an SPDX spec version 3 AnyLicenseIfno
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public AnyLicenseInfo convertAndStore(org.spdx.library.model.v2.license.AnyLicenseInfo fromLicense) throws InvalidSPDXAnalysisException {
		if (fromLicense instanceof org.spdx.library.model.v2.license.ConjunctiveLicenseSet) {
			return convertAndStore((org.spdx.library.model.v2.license.ConjunctiveLicenseSet)fromLicense);
		} else if (fromLicense instanceof org.spdx.library.model.v2.license.DisjunctiveLicenseSet) {
			return convertAndStore((org.spdx.library.model.v2.license.DisjunctiveLicenseSet)fromLicense);
		} else if (fromLicense instanceof org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo) {
			String externalUri = ((org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo)fromLicense).getIndividualURI();
			logger.warn("Referencing an external SPDX 2 element with URI " + externalUri +
					" while converting from SPDX 2 to 3");
			return new ExternalLicense(externalUri);
		} else if (fromLicense instanceof org.spdx.library.model.v2.license.ExtractedLicenseInfo) {
			return convertAndStore((org.spdx.library.model.v2.license.ExtractedLicenseInfo)fromLicense);
		} else if (fromLicense instanceof org.spdx.library.model.v2.license.OrLaterOperator) {
			return convertAndStore((org.spdx.library.model.v2.license.OrLaterOperator)fromLicense);
		} else if (fromLicense instanceof org.spdx.library.model.v2.license.SpdxListedLicense) {
			return convertAndStore((org.spdx.library.model.v2.license.SpdxListedLicense)fromLicense);
		} else if (fromLicense instanceof org.spdx.library.model.v2.license.SpdxNoneLicense) {
			return new NoneLicense();
		} else if (fromLicense instanceof org.spdx.library.model.v2.license.SpdxNoAssertionLicense) {
			return new NoAssertionLicense();
		} else if (fromLicense instanceof org.spdx.library.model.v2.license.WithExceptionOperator) {
			return convertAndStore((org.spdx.library.model.v2.license.WithExceptionOperator)fromLicense);
		} else {
			throw new InvalidSPDXAnalysisException("Can not convert the from AnyLicenseInfo type "+fromLicense.getType());
		}
	}

	/**
	 * Converts the Element and stores all properties in the toStore
	 * @param fromElement element to convert from
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private Element convertAndStore(org.spdx.library.model.v2.SpdxElement fromElement) throws InvalidSPDXAnalysisException {
		if (fromElement instanceof org.spdx.library.model.v2.SpdxFile) {
			return convertAndStore((org.spdx.library.model.v2.SpdxFile)fromElement);
		} else if (fromElement instanceof org.spdx.library.model.v2.SpdxPackage) {
			return convertAndStore((org.spdx.library.model.v2.SpdxPackage)fromElement);
		} else if (fromElement instanceof org.spdx.library.model.v2.SpdxSnippet) {
			return convertAndStore((org.spdx.library.model.v2.SpdxSnippet)fromElement);
		} else if (fromElement instanceof org.spdx.library.model.v2.SpdxNoAssertionElement) {
			return new NoAssertionElement();
		} else if (fromElement instanceof org.spdx.library.model.v2.SpdxNoneElement) {
			return new NoneElement();
		} else if (fromElement instanceof org.spdx.library.model.v2.ExternalSpdxElement) {
			String externalUri = ((org.spdx.library.model.v2.ExternalSpdxElement)fromElement).getIndividualURI();
			logger.warn("Referencing an external SPDX 2 element with URI " + externalUri +
					" while converting from SPDX 2 to 3");
			return new ExternalElement(toModelStore, externalUri, copyManager);
		} else if (fromElement instanceof org.spdx.library.model.v2.SpdxDocument) {
			return convertAndStore((org.spdx.library.model.v2.SpdxDocument)fromElement);
		} else {
			throw new InvalidSPDXAnalysisException("Conversion of SPDX 2 type" + fromElement.getType()+" is not currently supported");
		}
	}

	/**
	 * Converts the SPDX 2 SpdxFile to an SPDX 3 SpdxFile and returns the converted file
	 * @param spdxFile SPDX file to convert from
	 * @return SPDX 3 SpdxFile
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	public SpdxFile convertAndStore(org.spdx.library.model.v2.SpdxFile spdxFile) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(spdxFile.getObjectUri(), SpdxConstantsV3.SOFTWARE_SPDX_FILE);
		if (existing.isPresent()) {
			return (SpdxFile)existing.get();
		}
		String toObjectUri = defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId);
		String existingUri = this.alreadyConverted.putIfAbsent(spdxFile.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (SpdxFile)getExistingObject(spdxFile.getObjectUri(), SpdxConstantsV3.SOFTWARE_SPDX_FILE).get();
		} 
		SpdxFile toFile = (SpdxFile)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.SOFTWARE_SPDX_FILE, copyManager, true, defaultUriPrefix);
		convertItemProperties(spdxFile, toFile);
		
		for (org.spdx.library.model.v2.Checksum checksum:spdxFile.getChecksums()) {
			toFile.getVerifiedUsings().add(convertAndStore(checksum));
		}
		// spdxFile.getFileContributors(); - No equivalent SPDX 3 property
		// spdxFile.getFileDependency(); - deprecated - skipping
		
		for (org.spdx.library.model.v2.enumerations.FileType fileType : spdxFile.getFileTypes()) {
			convertAndAddFileType(fileType, toFile);
		}
		toFile.setCopyrightText(spdxFile.getNoticeText().orElseGet(null));
		String sha1 = spdxFile.getSha1();
		if (Objects.nonNull(sha1)) {
			Hash hash = toFile.createHash(toModelStore.getNextId(IdType.Anonymous))
					.setAlgorithm(HashAlgorithm.SHA1)
					.setHashValue(sha1)
					.build();
			toFile.getVerifiedUsings().add(hash);
		}
		return toFile;
	}
	
	/**
	 * Converts the SPDX spec version 2 Checksum to an SPDX spec version 3 Hash and store the result
	 * @param checksum SPDX spec version 2 Checksum
	 * @return SPDX spec version 3 Hash
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private Hash convertAndStore(org.spdx.library.model.v2.Checksum checksum) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(checksum.getObjectUri(), SpdxConstantsV3.CORE_HASH);
		if (existing.isPresent()) {
			return (Hash)existing.get();
		}
		String toObjectUri = toModelStore.getNextId(IdType.Anonymous);
		String existingUri = this.alreadyConverted.putIfAbsent(checksum.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (Hash)getExistingObject(checksum.getObjectUri(), SpdxConstantsV3.CORE_HASH).get();
		} 
		Hash toHash = (Hash)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.CORE_HASH, copyManager, true, defaultUriPrefix);
		toHash.setAlgorithm(HASH_ALGORITH_MAP.get(checksum.getAlgorithm()));
		toHash.setHashValue(checksum.getValue());
		return toHash;
	}

	/**
	 * Converts an SPDX spec version 2 FileType to the corresponding SPDX model 3 software purpose and/or content type
	 * and adds that information to the file
	 * @param fileType SPDX spec version 2 FileType to convert and add
	 * @param file SPDX spec version 3 SpdxFile to add the software purpose or content type to
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private void convertAndAddFileType(org.spdx.library.model.v2.enumerations.FileType fileType, SpdxFile file) throws InvalidSPDXAnalysisException {
		switch (fileType) {
			case ARCHIVE: addSoftwarePurpose(SoftwarePurpose.ARCHIVE, file); break;
			case BINARY: file.setSoftwareContentType("application/octet-stream"); break;
			case SOURCE: addSoftwarePurpose(SoftwarePurpose.SOURCE, file); break;
			case TEXT: file.setSoftwareContentType("text/plain"); break;
			case APPLICATION: addSoftwarePurpose(SoftwarePurpose.APPLICATION, file); break;
			case AUDIO: file.setSoftwareContentType("audio/*"); break;
			case IMAGE: file.setSoftwareContentType("image/*"); break;
			case VIDEO: file.setSoftwareContentType("video/*"); break;
			case DOCUMENTATION: addSoftwarePurpose(SoftwarePurpose.DOCUMENTATION, file); break;
			case SPDX: file.setSoftwareContentType("text/spdx"); break;
			case OTHER: addSoftwarePurpose(SoftwarePurpose.OTHER, file); break;
			
			default: throw new InvalidSPDXAnalysisException("Unknown file type "+fileType);
		}
	}

	/**
	 * Adds a Sotfware Purpose to a SoftwareArtifact.  If the primaryPurpose is already used, add as an additionalPurpose
	 * @param purpose purpose to add
	 * @param artifact artifact to add the purpose to
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private void addSoftwarePurpose(SoftwarePurpose purpose,
			SoftwareArtifact artifact) throws InvalidSPDXAnalysisException {
		if (artifact.getPrimaryPurpose().isPresent()) {
			artifact.getAdditionalPurposes().add(purpose);
		} else {
			artifact.setPrimaryPurpose(purpose);
		}
	}

	/**
	 * Converts and copies properties from the fromItem to the toArtifact
	 * @param fromItem SPDX spec version 2 Item to copy properties from
	 * @param toArtifact SPDX spec version 3 SoftwareArtifact to copy properties to
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private void convertItemProperties(org.spdx.library.model.v2.SpdxItem fromItem, SoftwareArtifact toArtifact) throws InvalidSPDXAnalysisException {
		convertElementProperties(fromItem, toArtifact);
		toArtifact.getAttributionTexts().addAll(fromItem.getAttributionText());
		toArtifact.setCopyrightText(fromItem.getCopyrightText());
		Optional<String> licenseComments = fromItem.getLicenseComments();
		if (licenseComments.isPresent()) {
			Optional<String> existingComment = toArtifact.getComment();
			toArtifact.setComment(existingComment.isPresent() ? existingComment.get() + ";" + licenseComments.get() : licenseComments.get());
		}
		org.spdx.library.model.v2.license.AnyLicenseInfo concludedLicense = fromItem.getLicenseConcluded();
		if (Objects.nonNull(concludedLicense)) {
			Relationship concludedRelationship = (Relationship)SpdxModelClassFactory.getModelObject(toModelStore, 
					defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId), SpdxConstantsV3.CORE_RELATIONSHIP, copyManager, true, defaultUriPrefix);
			concludedRelationship.setCreationInfo(defaultCreationInfo);
			concludedRelationship.setFrom(toArtifact);
			concludedRelationship.getTos().add(convertAndStore(concludedLicense));
			concludedRelationship.setRelationshipType(RelationshipType.HAS_CONCLUDED_LICENSE);
		}
		if (!(fromItem instanceof org.spdx.library.model.v2.SpdxPackage)) {
			// we use the license concluded for the SPDX package
			for (org.spdx.library.model.v2.license.AnyLicenseInfo declaredLicense:fromItem.getLicenseInfoFromFiles()) {
				Relationship declaredRelationship = (Relationship)SpdxModelClassFactory.getModelObject(toModelStore, 
						defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId), SpdxConstantsV3.CORE_RELATIONSHIP, copyManager, true, defaultUriPrefix);
				declaredRelationship.setCreationInfo(defaultCreationInfo);
				declaredRelationship.setFrom(toArtifact);
				declaredRelationship.getTos().add(convertAndStore(declaredLicense));
				declaredRelationship.setRelationshipType(RelationshipType.HAS_DECLARED_LICENSE);
			}
		}
	}

	/**
	 * Converts the SPDX 2 SpdxPackage to an SPDX 3 SpdxPackage and returns the converted package
	 * @param spdxPackage SPDX package to convert from
	 * @return SPDX 3 SpdxPackage
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	public SpdxPackage convertAndStore(org.spdx.library.model.v2.SpdxPackage spdxPackage) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(spdxPackage.getObjectUri(), SpdxConstantsV3.SOFTWARE_SPDX_PACKAGE);
		if (existing.isPresent()) {
			return (SpdxPackage)existing.get();
		}
		String toObjectUri = defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId);
		String existingUri = this.alreadyConverted.putIfAbsent(spdxPackage.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (SpdxPackage)getExistingObject(spdxPackage.getObjectUri(), SpdxConstantsV3.SOFTWARE_SPDX_PACKAGE).get();
		} 
		SpdxPackage toPackage = (SpdxPackage)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.SOFTWARE_SPDX_PACKAGE, copyManager, true, defaultUriPrefix);
		convertItemProperties(spdxPackage, toPackage);
		toPackage.setBuiltTime(spdxPackage.getBuiltDate().orElse(null));
		toPackage.setDescription(spdxPackage.getDescription().orElse(null));
		toPackage.setDownloadLocation(spdxPackage.getDownloadLocation().orElse(null));
		for (org.spdx.library.model.v2.ExternalRef externalRef:spdxPackage.getExternalRefs()) {
			addExternalRefToArtifact(externalRef, toPackage);
		}
		// spdxPackage.getFiles() - these should be captured in relationships
		toPackage.setHomePage(spdxPackage.getHomepage().orElse(null));
		Optional<String> originator = spdxPackage.getOriginator();
		if (originator.isPresent()) {
			toPackage.getOriginatedBys().add(stringToAgent(originator.get(), toPackage.getCreationInfo()));
		}
		Optional<String> packageFileName = spdxPackage.getPackageFileName();
		if (packageFileName.isPresent()) {
			addPackageFileNameToPackage(packageFileName.get(), toPackage, spdxPackage.getChecksums());
		}
		Optional<org.spdx.library.model.v2.SpdxPackageVerificationCode> pkgVerificationCode = spdxPackage.getPackageVerificationCode();
		if (pkgVerificationCode.isPresent()) {
			toPackage.getVerifiedUsings().add(convertAndStore(pkgVerificationCode.get()));
		}
		Optional<org.spdx.library.model.v2.enumerations.Purpose> primaryPurpose = spdxPackage.getPrimaryPurpose();
		if (primaryPurpose.isPresent()) {
			if (toPackage.getPrimaryPurpose().isPresent()) {
				toPackage.getAdditionalPurposes().add(toPackage.getPrimaryPurpose().get());
			}
			toPackage.setPrimaryPurpose(PURPOSE_MAP.get(primaryPurpose.get()));
		}
		toPackage.setReleaseTime(spdxPackage.getReleaseDate().orElse(null));
		String sha1 = spdxPackage.getSha1();
		if (Objects.nonNull(sha1)) {
			toPackage.getVerifiedUsings().add(toPackage.createHash(toModelStore.getNextId(IdType.Anonymous))
					.setAlgorithm(HashAlgorithm.SHA1)
					.setHashValue(sha1)
					.build());
		}
		toPackage.setSourceInfo(spdxPackage.getSourceInfo().orElse(null));
		toPackage.setSummary(spdxPackage.getSummary().orElse(null));
		Optional<String> supplier = spdxPackage.getSupplier();
		if (supplier.isPresent()) {
			toPackage.setSuppliedBy(stringToAgent(supplier.get(), toPackage.getCreationInfo()));
		}
		toPackage.setValidUntilTime(spdxPackage.getValidUntilDate().orElse(null));
		toPackage.setPackageVersion(spdxPackage.getVersionInfo().orElse(null));
		
		org.spdx.library.model.v2.license.AnyLicenseInfo declaredLicense = spdxPackage.getLicenseDeclared();
		if (Objects.nonNull(declaredLicense)) {
			Relationship declaredRelationship = (Relationship)SpdxModelClassFactory.getModelObject(toModelStore, 
					defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId), SpdxConstantsV3.CORE_RELATIONSHIP, copyManager, true, defaultUriPrefix);
			declaredRelationship.setCreationInfo(defaultCreationInfo);
			declaredRelationship.setFrom(toPackage);
			declaredRelationship.getTos().add(convertAndStore(declaredLicense));
			declaredRelationship.setRelationshipType(RelationshipType.HAS_DECLARED_LICENSE);
		}
		return toPackage;
	}

	/**
	 * Converts the spdxPackageVerificationCode to an IntegrityMethod and store the result in the toModelStore
	 * @param spdxPackageVerificationCode SPDX Spec version 2 package verification code
	 * @return the package verification code integrity method
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private IntegrityMethod convertAndStore(
			org.spdx.library.model.v2.SpdxPackageVerificationCode spdxPackageVerificationCode) throws InvalidSPDXAnalysisException {
		PackageVerificationCode pkgVerificationCode = (PackageVerificationCode)SpdxModelClassFactory.getModelObject(toModelStore, 
				toModelStore.getNextId(IdType.Anonymous), SpdxConstantsV3.CORE_PACKAGE_VERIFICATION_CODE,
				copyManager, true, defaultUriPrefix);
		
		pkgVerificationCode.setAlgorithm(HashAlgorithm.SHA1);
		pkgVerificationCode.setHashValue(spdxPackageVerificationCode.getValue());
		pkgVerificationCode.getPackageVerificationCodeExcludedFiles().addAll(spdxPackageVerificationCode.getExcludedFileNames());
		return pkgVerificationCode;
	}

	/**
	 * Create a File artifact and add that to toPackage as a relationship
	 * @param fileName Name of the File artifact
	 * @param toPackage package to add the file to
	 * @param fileChecksums checksums for the file
	 * @throws InvalidSPDXAnalysisException 
	 */
	private void addPackageFileNameToPackage(String fileName,
			SpdxPackage toPackage, Collection<org.spdx.library.model.v2.Checksum> fileChecksums) throws InvalidSPDXAnalysisException {
		SpdxFile file = toPackage.createSpdxFile(defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId))
				.setName(fileName)
				.build();
		for (org.spdx.library.model.v2.Checksum checksum : fileChecksums) {
			file.getVerifiedUsings().add(convertAndStore(checksum));
		}
		toPackage.createRelationship(defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId))
				.setRelationshipType(RelationshipType.HAS_DISTRIBUTION_ARTIFACT)
				.setFrom(toPackage)
				.addTo(file)
				.setCompleteness(RelationshipCompleteness.COMPLETE)
				.build();
	}

	/**
	 * @param spdx2personOrgString String formatted in the SPDX 2 typical format for person/org/tool
	 * @param creationInfo creationInfo to add to the Agent
	 * @return Agent based on parsing the spdx2personOrgString - if NONE or NOASSERTION is the string value, the null is returned
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	public @Nullable Agent stringToAgent(String spdx2personOrgString, CreationInfo creationInfo) throws InvalidSPDXAnalysisException {
		Matcher matcher = SPDX_2_CREATOR_PATTERN.matcher(spdx2personOrgString);
		if (!matcher.matches()) {
			// return a generic Agent
			Agent agent = (Agent)SpdxModelClassFactory.getModelObject(toModelStore, 
					defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId),
					SpdxConstantsV3.CORE_AGENT, copyManager, true, defaultUriPrefix);
			agent.setCreationInfo(creationInfo);
			agent.setName(spdx2personOrgString);
			return agent;
		} else if (matcher.group(1).trim().equals("Person")) {
			Person person = (Person)SpdxModelClassFactory.getModelObject(toModelStore, 
					defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId),
					SpdxConstantsV3.CORE_PERSON, copyManager, true, defaultUriPrefix);
			person.setCreationInfo(creationInfo);
			person.setName(matcher.group(2).trim());
			String email = matcher.group(4);
			if (Objects.nonNull(email)) {
				person.getExternalIdentifiers().add(person.createExternalIdentifier(toModelStore.getNextId(IdType.Anonymous))
						.setExternalIdentifierType(ExternalIdentifierType.EMAIL)
						.setIdentifier(email)
						.build());
			}
			return person;
		} else {
			Organization organization = (Organization)SpdxModelClassFactory.getModelObject(toModelStore, 
					defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId),
					SpdxConstantsV3.CORE_ORGANIZATION, copyManager, true, defaultUriPrefix);
			organization.setCreationInfo(creationInfo);
			organization.setName(matcher.group(2).trim());
			String email = matcher.group(4);
			if (Objects.nonNull(email)) {
				organization.getExternalIdentifiers().add(organization.createExternalIdentifier(toModelStore.getNextId(IdType.Anonymous))
						.setExternalIdentifierType(ExternalIdentifierType.EMAIL)
						.setIdentifier(email)
						.build());
			}
			return organization;
		}
	}

	/**
	 * @param externalRef SPDX Spec version 2 External Ref to add to the package
	 * @param artifact SPDX Spec version 3 Artifact to add either an ExternalRef or ExternalId depending on the externalRef type
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	private void addExternalRefToArtifact(org.spdx.library.model.v2.ExternalRef externalRef,
			SoftwareArtifact artifact) throws InvalidSPDXAnalysisException {
		org.spdx.library.model.v2.ReferenceType referenceType = externalRef.getReferenceType();
		Objects.requireNonNull(referenceType);
		switch (referenceType.getIndividualURI()) {
			case SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "cpe22Type":
			case SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "cpe23Type":
			case SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "swid":
				artifact.getExternalIdentifiers().add(artifact.createExternalIdentifier(toModelStore.getNextId(IdType.Anonymous))
						.setExternalIdentifierType(EXTERNAL_IDENTIFIER_TYPE_MAP.get(referenceType.getIndividualURI()))
						.setIdentifier(externalRef.getReferenceLocator())
						.build()); break;
			case SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "purl": {
				if (artifact instanceof SpdxPackage) {
					((SpdxPackage)artifact).setPackageUrl(externalRef.getReferenceLocator());
				} else {
					artifact.getExternalIdentifiers().add(artifact.createExternalIdentifier(toModelStore.getNextId(IdType.Anonymous))
							.setExternalIdentifierType(EXTERNAL_IDENTIFIER_TYPE_MAP.get(referenceType.getIndividualURI()))
							.setIdentifier(externalRef.getReferenceLocator())
							.build()); break;
				}
			} break;
			case SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "swh":
			case SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "gitoid":
				artifact.getContentIdentifiers().add(artifact.createContentIdentifier(toModelStore.getNextId(IdType.Anonymous))
						.setContentIdentifierType(CONTENT_IDENTIFIER_TYPE_MAP.get(referenceType.getIndividualURI()))
						.setContentIdentifierValue(externalRef.getReferenceLocator())
						.build()); break;
			default: {
				ExternalRefType externalRefType = EXTERNAL_REF_TYPE_MAP.get(referenceType.getIndividualURI());
				if (Objects.isNull(externalRefType)) {
					switch (externalRef.getReferenceCategory()) {
						case PACKAGE_MANAGER: externalRefType = ExternalRefType.BUILD_SYSTEM; break;
						case SECURITY: externalRefType = ExternalRefType.SECURITY_OTHER; break;
						default: externalRefType = ExternalRefType.OTHER;
					}
				}
				artifact.getExternalRefs().add(artifact.createExternalRef(toModelStore.getNextId(IdType.Anonymous))
						.setExternalRefType(externalRefType)
						.addLocator(externalRef.getReferenceLocator())
						.build());
			}
		}
	}

	/**
	 * Converts the SPDX 2 SpdxSnippet to an SPDX 3 Snippet and returns the converted snippet
	 * @param fromSnippet SPDX 2 snippet to convert from
	 * @return SPDX 3 Snippet
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	public Snippet convertAndStore(org.spdx.library.model.v2.SpdxSnippet fromSnippet) throws InvalidSPDXAnalysisException {
		Optional<ModelObjectV3> existing = getExistingObject(fromSnippet.getObjectUri(), SpdxConstantsV3.SOFTWARE_SNIPPET);
		if (existing.isPresent()) {
			return (Snippet)existing.get();
		}
		String toObjectUri = defaultUriPrefix + toModelStore.getNextId(IdType.SpdxId);
		String existingUri = this.alreadyConverted.putIfAbsent(fromSnippet.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occurred since the last check already converted
			return (Snippet)getExistingObject(fromSnippet.getObjectUri(), SpdxConstantsV3.SOFTWARE_SNIPPET).get();
		} 
		Snippet toSnippet = (Snippet)SpdxModelClassFactory.getModelObject(toModelStore, 
				toObjectUri, SpdxConstantsV3.SOFTWARE_SNIPPET, copyManager, true, defaultUriPrefix);
		convertItemProperties(fromSnippet, toSnippet);
		org.spdx.library.model.v2.pointer.StartEndPointer fromByteRange = fromSnippet.getByteRange();
		if (Objects.nonNull(fromByteRange)) {
			toSnippet.setByteRange(toSnippet.createPositiveIntegerRange(toModelStore.getNextId(IdType.Anonymous))
					.setBeginIntegerRange(((org.spdx.library.model.v2.pointer.ByteOffsetPointer)fromByteRange.getStartPointer()).getOffset())
					.setEndIntegerRange(((org.spdx.library.model.v2.pointer.ByteOffsetPointer)fromByteRange.getEndPointer()).getOffset())
					.build());
		}
		Optional<org.spdx.library.model.v2.pointer.StartEndPointer> fromLineRange = fromSnippet.getLineRange();
		if (fromLineRange.isPresent()) {
			toSnippet.setLineRange(toSnippet.createPositiveIntegerRange(toModelStore.getNextId(IdType.Anonymous))
					.setBeginIntegerRange(((org.spdx.library.model.v2.pointer.LineCharPointer)fromLineRange.get().getStartPointer()).getLineNumber())
					.setEndIntegerRange(((org.spdx.library.model.v2.pointer.LineCharPointer)fromLineRange.get().getEndPointer()).getLineNumber())
					.build());
		}
		return toSnippet;
	}
}
