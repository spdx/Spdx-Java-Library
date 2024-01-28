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
package org.spdx.library;

import org.spdx.storage.PropertyDescriptor;

/**
 * Constants which map to the SPDX specifications
 * @author Gary O'Neall
 *
 */
public class SpdxConstants {
	
	public enum SpdxMajorVersion {
		VERSION_1,
		VERSION_2,
		VERSION_3;

		public static SpdxMajorVersion latestVersion() {
			return VERSION_3;
		}
	}
	
	/**
	 * AI namespace
	 */
	public static final String A_I_NAMESPACE = "https://spdx.org/rdf/v3/AI";
	public static final PropertyDescriptor A_I_PROP_AUTONOMY_TYPE = new PropertyDescriptor("autonomyType", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_DOMAIN = new PropertyDescriptor("domain", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_ENERGY_CONSUMPTION = new PropertyDescriptor("energyConsumption", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_HYPERPARAMETER = new PropertyDescriptor("hyperparameter", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_INFORMATION_ABOUT_APPLICATION = new PropertyDescriptor("informationAboutApplication", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_INFORMATION_ABOUT_TRAINING = new PropertyDescriptor("informationAboutTraining", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_LIMITATION = new PropertyDescriptor("limitation", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_METRIC = new PropertyDescriptor("metric", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_METRIC_DECISION_THRESHOLD = new PropertyDescriptor("metricDecisionThreshold", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_MODEL_DATA_PREPROCESSING = new PropertyDescriptor("modelDataPreprocessing", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_MODEL_EXPLAINABILITY = new PropertyDescriptor("modelExplainability", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_SAFETY_RISK_ASSESSMENT = new PropertyDescriptor("safetyRiskAssessment", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_SENSITIVE_PERSONAL_INFORMATION = new PropertyDescriptor("sensitivePersonalInformation", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_STANDARD_COMPLIANCE = new PropertyDescriptor("standardCompliance", A_I_NAMESPACE);
	public static final PropertyDescriptor A_I_PROP_TYPE_OF_MODEL = new PropertyDescriptor("typeOfModel", A_I_NAMESPACE);
	
	/**
	 * Build namespace
	 */
	public static final String BUILD_NAMESPACE = "https://spdx.org/rdf/v3/Build";
	public static final PropertyDescriptor BUILD_PROP_BUILD_END_TIME = new PropertyDescriptor("buildEndTime", BUILD_NAMESPACE);
	public static final PropertyDescriptor BUILD_PROP_BUILD_ID = new PropertyDescriptor("buildId", BUILD_NAMESPACE);
	public static final PropertyDescriptor BUILD_PROP_BUILD_START_TIME = new PropertyDescriptor("buildStartTime", BUILD_NAMESPACE);
	public static final PropertyDescriptor BUILD_PROP_BUILD_TYPE = new PropertyDescriptor("buildType", BUILD_NAMESPACE);
	public static final PropertyDescriptor BUILD_PROP_CONFIG_SOURCE_DIGEST = new PropertyDescriptor("configSourceDigest", BUILD_NAMESPACE);
	public static final PropertyDescriptor BUILD_PROP_CONFIG_SOURCE_ENTRYPOINT = new PropertyDescriptor("configSourceEntrypoint", BUILD_NAMESPACE);
	public static final PropertyDescriptor BUILD_PROP_CONFIG_SOURCE_URI = new PropertyDescriptor("configSourceUri", BUILD_NAMESPACE);
	public static final PropertyDescriptor BUILD_PROP_ENVIRONMENT = new PropertyDescriptor("environment", BUILD_NAMESPACE);
	public static final PropertyDescriptor BUILD_PROP_PARAMETERS = new PropertyDescriptor("parameters", BUILD_NAMESPACE);
	
	/**
	 * Core namespace
	 */
	public static final String CORE_NAMESPACE = "https://spdx.org/rdf/v3/Core";
	public static final PropertyDescriptor CORE_PROP_ALGORITHM = new PropertyDescriptor("algorithm", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_ANNOTATION_TYPE = new PropertyDescriptor("annotationType", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_BEGIN = new PropertyDescriptor("begin", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_BUILT_TIME = new PropertyDescriptor("builtTime", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_COMMENT = new PropertyDescriptor("comment", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_COMPLETENESS = new PropertyDescriptor("completeness", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_CONTENT_TYPE = new PropertyDescriptor("contentType", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_CONTEXT = new PropertyDescriptor("context", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_CREATED = new PropertyDescriptor("created", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_CREATED_BY = new PropertyDescriptor("createdBy", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_CREATED_USING = new PropertyDescriptor("createdUsing", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_CREATION_INFO = new PropertyDescriptor("creationInfo", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_DATA_LICENSE = new PropertyDescriptor("dataLicense", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_DEFINING_DOCUMENT = new PropertyDescriptor("definingDocument", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_DESCRIPTION = new PropertyDescriptor("description", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_ELEMENT = new PropertyDescriptor("element", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_END = new PropertyDescriptor("end", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_END_TIME = new PropertyDescriptor("endTime", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTENSION = new PropertyDescriptor("extension", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_ID = new PropertyDescriptor("externalId", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_IDENTIFIER = new PropertyDescriptor("externalIdentifier", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_IDENTIFIER_TYPE = new PropertyDescriptor("externalIdentifierType", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_REF = new PropertyDescriptor("externalRef", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_REF_TYPE = new PropertyDescriptor("externalRefType", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_FROM = new PropertyDescriptor("from", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_HASH_VALUE = new PropertyDescriptor("hashValue", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_IDENTIFIER = new PropertyDescriptor("identifier", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_IDENTIFIER_LOCATOR = new PropertyDescriptor("identifierLocator", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_IMPORTS = new PropertyDescriptor("imports", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_ISSUING_AUTHORITY = new PropertyDescriptor("issuingAuthority", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_KEY = new PropertyDescriptor("key", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_LOCATION_HINT = new PropertyDescriptor("locationHint", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_LOCATOR = new PropertyDescriptor("locator", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_NAME = new PropertyDescriptor("name", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_ORIGINATED_BY = new PropertyDescriptor("originatedBy", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_PROFILE = new PropertyDescriptor("profile", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_RELATIONSHIP_TYPE = new PropertyDescriptor("relationshipType", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_RELEASE_TIME = new PropertyDescriptor("releaseTime", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_ROOT_ELEMENT = new PropertyDescriptor("rootElement", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_SCOPE = new PropertyDescriptor("scope", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_SPEC_VERSION = new PropertyDescriptor("specVersion", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_STANDARD = new PropertyDescriptor("standard", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_START_TIME = new PropertyDescriptor("startTime", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_STATEMENT = new PropertyDescriptor("statement", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_SUBJECT = new PropertyDescriptor("subject", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_SUMMARY = new PropertyDescriptor("summary", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_SUPPLIED_BY = new PropertyDescriptor("suppliedBy", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_TO = new PropertyDescriptor("to", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_VALID_UNTIL_TIME = new PropertyDescriptor("validUntilTime", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_VALUE = new PropertyDescriptor("value", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_VERIFIED_USING = new PropertyDescriptor("verifiedUsing", CORE_NAMESPACE);
	
	/**
	 * Dataset namespace
	 */
	public static final String DATASET_NAMESPACE = "https://spdx.org/rdf/v3/Dataset";
	public static final PropertyDescriptor DATASET_PROP_ANONYMIZATION_METHOD_USED = new PropertyDescriptor("anonymizationMethodUsed", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_CONFIDENTIALITY_LEVEL = new PropertyDescriptor("confidentialityLevel", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_DATA_COLLECTION_PROCESS = new PropertyDescriptor("dataCollectionProcess", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_DATA_PREPROCESSING = new PropertyDescriptor("dataPreprocessing", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_DATASET_AVAILABILITY = new PropertyDescriptor("datasetAvailability", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_DATASET_NOISE = new PropertyDescriptor("datasetNoise", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_DATASET_SIZE = new PropertyDescriptor("datasetSize", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_DATASET_TYPE = new PropertyDescriptor("datasetType", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_DATASET_UPDATE_MECHANISM = new PropertyDescriptor("datasetUpdateMechanism", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_INTENDED_USE = new PropertyDescriptor("intendedUse", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_KNOWN_BIAS = new PropertyDescriptor("knownBias", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_SENSITIVE_PERSONAL_INFORMATION = new PropertyDescriptor("sensitivePersonalInformation", DATASET_NAMESPACE);
	public static final PropertyDescriptor DATASET_PROP_SENSOR = new PropertyDescriptor("sensor", DATASET_NAMESPACE);
	
	/**
	 * ExpandedLicensing namespace
	 */
	public static final String EXPANDED_LICENSING_NAMESPACE = "https://spdx.org/rdf/v3/ExpandedLicensing";
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_ADDITION_TEXT = new PropertyDescriptor("additionText", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_DEPRECATED_VERSION = new PropertyDescriptor("deprecatedVersion", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_IS_DEPRECATED_ADDITION_ID = new PropertyDescriptor("isDeprecatedAdditionId", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_IS_DEPRECATED_LICENSE_ID = new PropertyDescriptor("isDeprecatedLicenseId", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_IS_FSF_LIBRE = new PropertyDescriptor("isFsfLibre", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_IS_OSI_APPROVED = new PropertyDescriptor("isOsiApproved", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_LICENSE_XML = new PropertyDescriptor("licenseXml", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_LIST_VERSION_ADDED = new PropertyDescriptor("listVersionAdded", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_MEMBER = new PropertyDescriptor("member", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_OBSOLETED_BY = new PropertyDescriptor("obsoletedBy", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_STANDARD_ADDITION_TEMPLATE = new PropertyDescriptor("standardAdditionTemplate", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_STANDARD_LICENSE_HEADER = new PropertyDescriptor("standardLicenseHeader", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_STANDARD_LICENSE_TEMPLATE = new PropertyDescriptor("standardLicenseTemplate", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_SUBJECT_ADDITION = new PropertyDescriptor("subjectAddition", EXPANDED_LICENSING_NAMESPACE);
	public static final PropertyDescriptor EXPANDED_LICENSING_PROP_SUBJECT_LICENSE = new PropertyDescriptor("subjectLicense", EXPANDED_LICENSING_NAMESPACE);
	
	/**
	 * Security namespace
	 */
	public static final String SECURITY_NAMESPACE = "https://spdx.org/rdf/v3/Security";
	public static final PropertyDescriptor SECURITY_PROP_ACTION_STATEMENT = new PropertyDescriptor("actionStatement", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_ACTION_STATEMENT_TIME = new PropertyDescriptor("actionStatementTime", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_ASSESSED_ELEMENT = new PropertyDescriptor("assessedElement", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_CATALOG_TYPE = new PropertyDescriptor("catalogType", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_DECISION_TYPE = new PropertyDescriptor("decisionType", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_EXPLOITED = new PropertyDescriptor("exploited", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_IMPACT_STATEMENT = new PropertyDescriptor("impactStatement", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_IMPACT_STATEMENT_TIME = new PropertyDescriptor("impactStatementTime", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_JUSTIFICATION_TYPE = new PropertyDescriptor("justificationType", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_LOCATOR = new PropertyDescriptor("locator", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_MODIFIED_TIME = new PropertyDescriptor("modifiedTime", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_PROBABILITY = new PropertyDescriptor("probability", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_PUBLISHED_TIME = new PropertyDescriptor("publishedTime", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_SCORE = new PropertyDescriptor("score", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_SEVERITY = new PropertyDescriptor("severity", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_STATUS_NOTES = new PropertyDescriptor("statusNotes", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_SUPPLIED_BY = new PropertyDescriptor("suppliedBy", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_VECTOR = new PropertyDescriptor("vector", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_VEX_VERSION = new PropertyDescriptor("vexVersion", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_WITHDRAWN_TIME = new PropertyDescriptor("withdrawnTime", SECURITY_NAMESPACE);
	
	/**
	 * SimpleLicensing namespace
	 */
	public static final String SIMPLE_LICENSING_NAMESPACE = "https://spdx.org/rdf/v3/SimpleLicensing";
	public static final PropertyDescriptor SIMPLE_LICENSING_PROP_CUSTOM_ID_TO_URI = new PropertyDescriptor("customIdToUri", SIMPLE_LICENSING_NAMESPACE);
	public static final PropertyDescriptor SIMPLE_LICENSING_PROP_LICENSE_EXPRESSION = new PropertyDescriptor("licenseExpression", SIMPLE_LICENSING_NAMESPACE);
	public static final PropertyDescriptor SIMPLE_LICENSING_PROP_LICENSE_LIST_VERSION = new PropertyDescriptor("licenseListVersion", SIMPLE_LICENSING_NAMESPACE);
	public static final PropertyDescriptor SIMPLE_LICENSING_PROP_LICENSE_TEXT = new PropertyDescriptor("licenseText", SIMPLE_LICENSING_NAMESPACE);
	
	/**
	 * Software namespace
	 */
	public static final String SOFTWARE_NAMESPACE = "https://spdx.org/rdf/v3/Software";
	public static final PropertyDescriptor SOFTWARE_PROP_ADDITIONAL_PURPOSE = new PropertyDescriptor("additionalPurpose", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_ATTRIBUTION_TEXT = new PropertyDescriptor("attributionText", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_BYTE_RANGE = new PropertyDescriptor("byteRange", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_CONDITIONALITY = new PropertyDescriptor("conditionality", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_CONTENT_IDENTIFIER = new PropertyDescriptor("contentIdentifier", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_CONTENT_TYPE = new PropertyDescriptor("contentType", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_COPYRIGHT_TEXT = new PropertyDescriptor("copyrightText", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_DOWNLOAD_LOCATION = new PropertyDescriptor("downloadLocation", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_HOME_PAGE = new PropertyDescriptor("homePage", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_LINE_RANGE = new PropertyDescriptor("lineRange", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_PACKAGE_URL = new PropertyDescriptor("packageUrl", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_PACKAGE_VERSION = new PropertyDescriptor("packageVersion", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_PRIMARY_PURPOSE = new PropertyDescriptor("primaryPurpose", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_SBOM_TYPE = new PropertyDescriptor("sbomType", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_SNIPPET_FROM_FILE = new PropertyDescriptor("snippetFromFile", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_SOFTWARE_LINKAGE = new PropertyDescriptor("softwareLinkage", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_SOURCE_INFO = new PropertyDescriptor("sourceInfo", SOFTWARE_NAMESPACE);
	
	// class types
	public static final String CORE_DICTIONARY_ENTRY = "Core.DictionaryEntry";
	public static final String SIMPLE_LICENSING_LICENSE_EXPRESSION = "SimpleLicensing.LicenseExpression";
	public static final String DATASET_CONFIDENTIALITY_LEVEL_TYPE = "Dataset.ConfidentialityLevelType";
	public static final String A_I_SAFETY_RISK_ASSESSMENT_TYPE = "AI.SafetyRiskAssessmentType";
	public static final String CORE_BOM = "Core.Bom";
	public static final String EXPANDED_LICENSING_OR_LATER_OPERATOR = "ExpandedLicensing.OrLaterOperator";
	public static final String SIMPLE_LICENSING_SIMPLE_LICENSING_TEXT = "SimpleLicensing.SimpleLicensingText";
	public static final String EXPANDED_LICENSING_LICENSE = "ExpandedLicensing.License";
	public static final String CORE_ANNOTATION = "Core.Annotation";
	public static final String SOFTWARE_SPDX_FILE = "Software.SpdxFile";
	public static final String EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION = "ExpandedLicensing.ListedLicenseException";
	public static final String EXPANDED_LICENSING_LICENSE_ADDITION = "ExpandedLicensing.LicenseAddition";
	public static final String SOFTWARE_SOFTWARE_ARTIFACT = "Software.SoftwareArtifact";
	public static final String SECURITY_VEX_AFFECTED_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexAffectedVulnAssessmentRelationship";
	public static final String DATASET_DATASET = "Dataset.Dataset";
	public static final String SECURITY_SSVC_VULN_ASSESSMENT_RELATIONSHIP = "Security.SsvcVulnAssessmentRelationship";
	public static final String CORE_INTEGRITY_METHOD = "Core.IntegrityMethod";
	public static final String SOFTWARE_SNIPPET = "Software.Snippet";
	public static final String CORE_EXTENSION = "Core.Extension";
	public static final String SECURITY_EPSS_VULN_ASSESSMENT_RELATIONSHIP = "Security.EpssVulnAssessmentRelationship";
	public static final String SOFTWARE_SBOM_TYPE = "Software.SbomType";
	public static final String CORE_TOOL = "Core.Tool";
	public static final String CORE_EXTERNAL_REF = "Core.ExternalRef";
	public static final String CORE_EXTERNAL_IDENTIFIER = "Core.ExternalIdentifier";
	public static final String CORE_ELEMENT_COLLECTION = "Core.ElementCollection";
	public static final String CORE_ANNOTATION_TYPE = "Core.AnnotationType";
	public static final String SOFTWARE_SOFTWARE_DEPENDENCY_RELATIONSHIP = "Software.SoftwareDependencyRelationship";
	public static final String SECURITY_VEX_JUSTIFICATION_TYPE = "Security.VexJustificationType";
	public static final String A_I_A_I_PACKAGE = "AI.AIPackage";
	public static final String EXPANDED_LICENSING_CONJUNCTIVE_LICENSE_SET = "ExpandedLicensing.ConjunctiveLicenseSet";
	public static final String CORE_EXTERNAL_REF_TYPE = "Core.ExternalRefType";
	public static final String SECURITY_EXPLOIT_CATALOG_TYPE = "Security.ExploitCatalogType";
	public static final String SOFTWARE_SOFTWARE_PURPOSE = "Software.SoftwarePurpose";
	public static final String EXPANDED_LICENSING_CUSTOM_LICENSE_ADDITION = "ExpandedLicensing.CustomLicenseAddition";
	public static final String CORE_ELEMENT = "Core.Element";
	public static final String CORE_PERSON = "Core.Person";
	public static final String SOFTWARE_DEPENDENCY_CONDITIONALITY_TYPE = "Software.DependencyConditionalityType";
	public static final String DATASET_DATASET_AVAILABILITY_TYPE = "Dataset.DatasetAvailabilityType";
	public static final String CORE_EXTERNAL_MAP = "Core.ExternalMap";
	public static final String EXPANDED_LICENSING_LISTED_LICENSE = "ExpandedLicensing.ListedLicense";
	public static final String SECURITY_VULN_ASSESSMENT_RELATIONSHIP = "Security.VulnAssessmentRelationship";
	public static final String CORE_AGENT = "Core.Agent";
	public static final String SOFTWARE_SPDX_PACKAGE = "Software.SpdxPackage";
	public static final String CORE_EXTERNAL_IDENTIFIER_TYPE = "Core.ExternalIdentifierType";
	public static final String SOFTWARE_SOFTWARE_DEPENDENCY_LINK_TYPE = "Software.SoftwareDependencyLinkType";
	public static final String CORE_POSITIVE_INTEGER_RANGE = "Core.PositiveIntegerRange";
	public static final String CORE_PRESENCE_TYPE = "Core.PresenceType";
	public static final String EXPANDED_LICENSING_DISJUNCTIVE_LICENSE_SET = "ExpandedLicensing.DisjunctiveLicenseSet";
	public static final String CORE_HASH = "Core.Hash";
	public static final String DATASET_DATASET_TYPE = "Dataset.DatasetType";
	public static final String EXPANDED_LICENSING_CUSTOM_LICENSE = "ExpandedLicensing.CustomLicense";
	public static final String CORE_SPDX_DOCUMENT = "Core.SpdxDocument";
	public static final String SIMPLE_LICENSING_ANY_LICENSE_INFO = "SimpleLicensing.AnyLicenseInfo";
	public static final String SECURITY_VEX_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexVulnAssessmentRelationship";
	public static final String SECURITY_CVSS_V2_VULN_ASSESSMENT_RELATIONSHIP = "Security.CvssV2VulnAssessmentRelationship";
	public static final String EXPANDED_LICENSING_WITH_ADDITION_OPERATOR = "ExpandedLicensing.WithAdditionOperator";
	public static final String CORE_BUNDLE = "Core.Bundle";
	public static final String SOFTWARE_SBOM = "Software.Sbom";
	public static final String CORE_LIFECYCLE_SCOPE_TYPE = "Core.LifecycleScopeType";
	public static final String SECURITY_VEX_UNDER_INVESTIGATION_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexUnderInvestigationVulnAssessmentRelationship";
	public static final String SECURITY_VEX_FIXED_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexFixedVulnAssessmentRelationship";
	public static final String CORE_HASH_ALGORITHM = "Core.HashAlgorithm";
	public static final String SECURITY_EXPLOIT_CATALOG_VULN_ASSESSMENT_RELATIONSHIP = "Security.ExploitCatalogVulnAssessmentRelationship";
	public static final String CORE_SOFTWARE_AGENT = "Core.SoftwareAgent";
	public static final String CORE_CREATION_INFO = "Core.CreationInfo";
	public static final String SECURITY_CVSS_V3_VULN_ASSESSMENT_RELATIONSHIP = "Security.CvssV3VulnAssessmentRelationship";
	public static final String CORE_ORGANIZATION = "Core.Organization";
	public static final String CORE_RELATIONSHIP = "Core.Relationship";
	public static final String CORE_RELATIONSHIP_TYPE = "Core.RelationshipType";
	public static final String CORE_RELATIONSHIP_COMPLETENESS = "Core.RelationshipCompleteness";
	public static final String CORE_ARTIFACT = "Core.Artifact";
	public static final String CORE_LIFECYCLE_SCOPED_RELATIONSHIP = "Core.LifecycleScopedRelationship";
	public static final String SECURITY_VEX_NOT_AFFECTED_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexNotAffectedVulnAssessmentRelationship";
	public static final String CORE_PROFILE_IDENTIFIER_TYPE = "Core.ProfileIdentifierType";
	public static final String BUILD_BUILD = "Build.Build";
	public static final String EXPANDED_LICENSING_EXTENDABLE_LICENSE = "ExpandedLicensing.ExtendableLicense";
	public static final String SECURITY_SSVC_DECISION_TYPE = "Security.SsvcDecisionType";
	public static final String SECURITY_VULNERABILITY = "Security.Vulnerability";
	
	public static final String[] ALL_SPDX_CLASSES = {CORE_DICTIONARY_ENTRY, SIMPLE_LICENSING_LICENSE_EXPRESSION, 
			DATASET_CONFIDENTIALITY_LEVEL_TYPE, A_I_SAFETY_RISK_ASSESSMENT_TYPE, CORE_BOM, 
			EXPANDED_LICENSING_OR_LATER_OPERATOR, SIMPLE_LICENSING_SIMPLE_LICENSING_TEXT, 
			EXPANDED_LICENSING_LICENSE, CORE_ANNOTATION, SOFTWARE_SPDX_FILE, EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION, 
			EXPANDED_LICENSING_LICENSE_ADDITION, SOFTWARE_SOFTWARE_ARTIFACT, SECURITY_VEX_AFFECTED_VULN_ASSESSMENT_RELATIONSHIP, 
			DATASET_DATASET, SECURITY_SSVC_VULN_ASSESSMENT_RELATIONSHIP, CORE_INTEGRITY_METHOD, 
			SOFTWARE_SNIPPET, CORE_EXTENSION, SECURITY_EPSS_VULN_ASSESSMENT_RELATIONSHIP, 
			SOFTWARE_SBOM_TYPE, CORE_TOOL, CORE_EXTERNAL_REF, CORE_EXTERNAL_IDENTIFIER, 
			CORE_ELEMENT_COLLECTION, CORE_ANNOTATION_TYPE, SOFTWARE_SOFTWARE_DEPENDENCY_RELATIONSHIP, 
			SECURITY_VEX_JUSTIFICATION_TYPE, A_I_A_I_PACKAGE, EXPANDED_LICENSING_CONJUNCTIVE_LICENSE_SET, 
			CORE_EXTERNAL_REF_TYPE, SECURITY_EXPLOIT_CATALOG_TYPE, SOFTWARE_SOFTWARE_PURPOSE, 
			EXPANDED_LICENSING_CUSTOM_LICENSE_ADDITION, CORE_ELEMENT, CORE_PERSON, 
			SOFTWARE_DEPENDENCY_CONDITIONALITY_TYPE, DATASET_DATASET_AVAILABILITY_TYPE, 
			CORE_EXTERNAL_MAP, EXPANDED_LICENSING_LISTED_LICENSE, SECURITY_VULN_ASSESSMENT_RELATIONSHIP, 
			CORE_AGENT, SOFTWARE_SPDX_PACKAGE, CORE_EXTERNAL_IDENTIFIER_TYPE, SOFTWARE_SOFTWARE_DEPENDENCY_LINK_TYPE, 
			CORE_POSITIVE_INTEGER_RANGE, CORE_PRESENCE_TYPE, EXPANDED_LICENSING_DISJUNCTIVE_LICENSE_SET, 
			CORE_HASH, DATASET_DATASET_TYPE, EXPANDED_LICENSING_CUSTOM_LICENSE, CORE_SPDX_DOCUMENT, 
			SIMPLE_LICENSING_ANY_LICENSE_INFO, SECURITY_VEX_VULN_ASSESSMENT_RELATIONSHIP, 
			SECURITY_CVSS_V2_VULN_ASSESSMENT_RELATIONSHIP, EXPANDED_LICENSING_WITH_ADDITION_OPERATOR, 
			CORE_BUNDLE, SOFTWARE_SBOM, CORE_LIFECYCLE_SCOPE_TYPE, SECURITY_VEX_UNDER_INVESTIGATION_VULN_ASSESSMENT_RELATIONSHIP, 
			SECURITY_VEX_FIXED_VULN_ASSESSMENT_RELATIONSHIP, CORE_HASH_ALGORITHM, SECURITY_EXPLOIT_CATALOG_VULN_ASSESSMENT_RELATIONSHIP, 
			CORE_SOFTWARE_AGENT, CORE_CREATION_INFO, SECURITY_CVSS_V3_VULN_ASSESSMENT_RELATIONSHIP, 
			CORE_ORGANIZATION, CORE_RELATIONSHIP, CORE_RELATIONSHIP_TYPE, CORE_RELATIONSHIP_COMPLETENESS, 
			CORE_ARTIFACT, CORE_LIFECYCLE_SCOPED_RELATIONSHIP, SECURITY_VEX_NOT_AFFECTED_VULN_ASSESSMENT_RELATIONSHIP, 
			CORE_PROFILE_IDENTIFIER_TYPE, BUILD_BUILD, EXPANDED_LICENSING_EXTENDABLE_LICENSE, 
			SECURITY_SSVC_DECISION_TYPE, SECURITY_VULNERABILITY};
}
