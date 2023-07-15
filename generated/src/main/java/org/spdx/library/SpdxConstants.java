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
	public static final String A_I_NAMESPACE = "https://spdx.org/rdf/AI";
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
	public static final String BUILD_NAMESPACE = "https://spdx.org/rdf/Build";
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
	public static final String CORE_NAMESPACE = "https://spdx.org/rdf/Core";
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
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_ID = new PropertyDescriptor("externalId", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_IDENTIFIER = new PropertyDescriptor("externalIdentifier", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_IDENTIFIER_TYPE = new PropertyDescriptor("externalIdentifierType", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_REFERENCE = new PropertyDescriptor("externalReference", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_EXTERNAL_REFERENCE_TYPE = new PropertyDescriptor("externalReferenceType", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_FROM = new PropertyDescriptor("from", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_HASH_VALUE = new PropertyDescriptor("hashValue", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_IDENTIFIER = new PropertyDescriptor("identifier", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_IDENTIFIER_LOCATOR = new PropertyDescriptor("identifierLocator", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_IMPORTS = new PropertyDescriptor("imports", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_ISSUING_AUTHORITY = new PropertyDescriptor("issuingAuthority", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_KEY = new PropertyDescriptor("key", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_LOCATION_HINT = new PropertyDescriptor("locationHint", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_LOCATOR = new PropertyDescriptor("locator", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_NAMESPACE = new PropertyDescriptor("namespace", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_NAMESPACES = new PropertyDescriptor("namespaces", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_ORIGINATED_BY = new PropertyDescriptor("originatedBy", CORE_NAMESPACE);
	public static final PropertyDescriptor CORE_PROP_PREFIX = new PropertyDescriptor("prefix", CORE_NAMESPACE);
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
	public static final String DATASET_NAMESPACE = "https://spdx.org/rdf/Dataset";
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
	 * Licensing namespace
	 */
	public static final String LICENSING_NAMESPACE = "https://spdx.org/rdf/Licensing";
	public static final PropertyDescriptor LICENSING_PROP_ADDITION_TEXT = new PropertyDescriptor("additionText", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_IS_DEPRECATED_ADDITION_ID = new PropertyDescriptor("isDeprecatedAdditionId", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_IS_DEPRECATED_LICENSE_ID = new PropertyDescriptor("isDeprecatedLicenseId", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_IS_FSF_LIBRE = new PropertyDescriptor("isFsfLibre", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_IS_OSI_APPROVED = new PropertyDescriptor("isOsiApproved", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_LICENSE_EXPRESSION = new PropertyDescriptor("licenseExpression", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_LICENSE_TEXT = new PropertyDescriptor("licenseText", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_STANDARD_ADDITION_TEMPLATE = new PropertyDescriptor("standardAdditionTemplate", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_STANDARD_LICENSE_HEADER = new PropertyDescriptor("standardLicenseHeader", LICENSING_NAMESPACE);
	public static final PropertyDescriptor LICENSING_PROP_STANDARD_LICENSE_TEMPLATE = new PropertyDescriptor("standardLicenseTemplate", LICENSING_NAMESPACE);
	
	/**
	 * Security namespace
	 */
	public static final String SECURITY_NAMESPACE = "https://spdx.org/rdf/Security";
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
	public static final PropertyDescriptor SECURITY_PROP_PROBABILITY = new PropertyDescriptor("probability", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_STATUS_NOTES = new PropertyDescriptor("statusNotes", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_SUPPLIED_BY = new PropertyDescriptor("suppliedBy", SECURITY_NAMESPACE);
	public static final PropertyDescriptor SECURITY_PROP_VEX_VERSION = new PropertyDescriptor("vexVersion", SECURITY_NAMESPACE);
	
	/**
	 * Software namespace
	 */
	public static final String SOFTWARE_NAMESPACE = "https://spdx.org/rdf/Software";
	public static final PropertyDescriptor SOFTWARE_PROP_ADDITIONAL_PURPOSE = new PropertyDescriptor("additionalPurpose", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_ATTRIBUTION_TEXT = new PropertyDescriptor("attributionText", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_BYTE_RANGE = new PropertyDescriptor("byteRange", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_CONCLUDED_LICENSE = new PropertyDescriptor("concludedLicense", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_CONDITIONALITY = new PropertyDescriptor("conditionality", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_CONTENT_IDENTIFIER = new PropertyDescriptor("contentIdentifier", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_CONTENT_TYPE = new PropertyDescriptor("contentType", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_COPYRIGHT_TEXT = new PropertyDescriptor("copyrightText", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_DECLARED_LICENSE = new PropertyDescriptor("declaredLicense", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_DOWNLOAD_LOCATION = new PropertyDescriptor("downloadLocation", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_HOME_PAGE = new PropertyDescriptor("homePage", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_LINE_RANGE = new PropertyDescriptor("lineRange", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_PACKAGE_URL = new PropertyDescriptor("packageUrl", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_PACKAGE_VERSION = new PropertyDescriptor("packageVersion", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_PRIMARY_PURPOSE = new PropertyDescriptor("primaryPurpose", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_SBOM_TYPE = new PropertyDescriptor("sbomType", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_SOFTWARE_LINKAGE = new PropertyDescriptor("softwareLinkage", SOFTWARE_NAMESPACE);
	public static final PropertyDescriptor SOFTWARE_PROP_SOURCE_INFO = new PropertyDescriptor("sourceInfo", SOFTWARE_NAMESPACE);
	
	// class types
	static final String SOFTWARE_SOFTWARE_PURPOSE = "Software.SoftwarePurpose";
	static final String DATASET_DATASET = "Dataset.Dataset";
	static final String CORE_SEM_VER = "Core.SemVer";
	static final String CORE_LIFECYCLE_SCOPE_TYPE = "Core.LifecycleScopeType";
	static final String LICENSING_WITH_ADDITION_OPERATOR = "Licensing.WithAdditionOperator";
	static final String SECURITY_VEX_JUSTIFICATION_TYPE = "Security.VexJustificationType";
	static final String SOFTWARE_SOFTWARE_DEPENDENCY_LINK_TYPE = "Software.SoftwareDependencyLinkType";
	static final String DATASET_CONFIDENTIALITY_LEVEL_TYPE = "Dataset.ConfidentialityLevelType";
	static final String SOFTWARE_SOFTWARE_DEPENDENCY_RELATIONSHIP = "Software.SoftwareDependencyRelationship";
	static final String CORE_HASH_ALGORITHM = "Core.HashAlgorithm";
	static final String CORE_EXTERNAL_REFERENCE_TYPE = "Core.ExternalReferenceType";
	static final String SOFTWARE_SNIPPET = "Software.Snippet";
	static final String SECURITY_SSVC_VULN_ASSESSMENT_RELATIONSHIP = "Security.SsvcVulnAssessmentRelationship";
	static final String SECURITY_VEX_UNDER_INVESTIGATION_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexUnderInvestigationVulnAssessmentRelationship";
	static final String CORE_ANONYMOUS_PAYLOAD = "Core.AnonymousPayload";
	static final String LICENSING_LICENSE_ADDITION = "Licensing.LicenseAddition";
	static final String DATASET_DATASET_TYPE = "Dataset.DatasetType";
	static final String LICENSING_OR_LATER_OPERATOR = "Licensing.OrLaterOperator";
	static final String CORE_POSITIVE_INTEGER_RANGE = "Core.PositiveIntegerRange";
	static final String DATASET_DATASET_AVAILABILITY_TYPE = "Dataset.DatasetAvailabilityType";
	static final String SOFTWARE_S_B_O_M_TYPE = "Software.SBOMType";
	static final String SECURITY_VULN_ASSESSMENT_RELATIONSHIP = "Security.VulnAssessmentRelationship";
	static final String SECURITY_EPSS_VULN_ASSESSMENT_RELATIONSHIP = "Security.EpssVulnAssessmentRelationship";
	static final String EXPANDED_LICENSE_CONJUNCTIVE_LICENSE_SET = "ExpandedLicense.ConjunctiveLicenseSet";
	static final String SECURITY_CVSS_V_2_VULN_ASSESSMENT_RELATIONSHIP = "Security.CvssV2VulnAssessmentRelationship";
	static final String CORE_RELATIONSHIP_COMPLETENESS = "Core.RelationshipCompleteness";
	static final String CORE_PROFILE_IDENTIFIER_TYPE = "Core.ProfileIdentifierType";
	static final String SOFTWARE_SOFTWARE_ARTIFACT = "Software.SoftwareArtifact";
	static final String LICENSING_LISTED_LICENSE = "Licensing.ListedLicense";
	static final String SECURITY_SSVC_DECISION_TYPE = "Security.SsvcDecisionType";
	static final String SOFTWARE_SPDX_FILE = "Software.SpdxFile";
	static final String CORE_ANNOTATION = "Core.Annotation";
	static final String CORE_MEDIA_TYPE = "Core.MediaType";
	static final String CORE_TOOL = "Core.Tool";
	static final String CORE_EXTERNAL_MAP = "Core.ExternalMap";
	static final String CORE_EXTERNAL_IDENTIFIER = "Core.ExternalIdentifier";
	static final String CORE_ANNOTATION_TYPE = "Core.AnnotationType";
	static final String LICENSING_ANY_LICENSE_INFO = "Licensing.AnyLicenseInfo";
	static final String CORE_ELEMENT_COLLECTION = "Core.ElementCollection";
	static final String CORE_HASH = "Core.Hash";
	static final String A_I_SAFETY_RISK_ASSESSMENT_TYPE = "AI.SafetyRiskAssessmentType";
	static final String SECURITY_CVSS_V_3_VULN_ASSESSMENT_RELATIONSHIP = "Security.CvssV3VulnAssessmentRelationship";
	static final String LICENSING_LICENSE_EXPRESSION = "Licensing.LicenseExpression";
	static final String CORE_LIFECYCLE_SCOPED_RELATIONSHIP = "Core.LifecycleScopedRelationship";
	static final String LICENSING_LICENSE = "Licensing.License";
	static final String SOFTWARE_DEPENDENCY_CONDITIONALITY_TYPE = "Software.DependencyConditionalityType";
	static final String CORE_INTEGRITY_METHOD = "Core.IntegrityMethod";
	static final String CORE_BUNDLE = "Core.Bundle";
	static final String SECURITY_EXPLOIT_CATALOG_VULN_ASSESSMENT_RELATIONSHIP = "Security.ExploitCatalogVulnAssessmentRelationship";
	static final String CORE_ARTIFACT = "Core.Artifact";
	static final String LICENSING_CUSTOM_LICENSE = "Licensing.CustomLicense";
	static final String CORE_EXTERNAL_REFERENCE = "Core.ExternalReference";
	static final String CORE_DICTIONARY_ENTRY = "Core.DictionaryEntry";
	static final String EXPANDED_LICENSE_DISJUNCTIVE_LICENSE_SET = "ExpandedLicense.DisjunctiveLicenseSet";
	static final String LICENSING_LISTED_LICENSE_EXCEPTION = "Licensing.ListedLicenseException";
	static final String CORE_EXTERNAL_IDENTIFIER_TYPE = "Core.ExternalIdentifierType";
	static final String SECURITY_VEX_NOT_AFFECTED_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexNotAffectedVulnAssessmentRelationship";
	static final String CORE_ELEMENT = "Core.Element";
	static final String SECURITY_VULNERABILITY = "Security.Vulnerability";
	static final String CORE_NAMESPACE_MAP = "Core.NamespaceMap";
	static final String CORE_PERSON = "Core.Person";
	static final String SECURITY_VEX_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexVulnAssessmentRelationship";
	static final String CORE_ORGANIZATION = "Core.Organization";
	static final String SECURITY_EXPLOIT_CATALOG_TYPE = "Security.ExploitCatalogType";
	static final String CORE_CREATION_INFO = "Core.CreationInfo";
	static final String CORE_RELATIONSHIP = "Core.Relationship";
	static final String SECURITY_VEX_AFFECTED_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexAffectedVulnAssessmentRelationship";
	static final String BUILD_BUILD = "Build.Build";
	static final String SOFTWARE_SPDX_PACKAGE = "Software.SpdxPackage";
	static final String SOFTWARE_SBOM = "Software.Sbom";
	static final String CORE_SPDX_DOCUMENT = "Core.SpdxDocument";
	static final String SECURITY_VEX_FIXED_VULN_ASSESSMENT_RELATIONSHIP = "Security.VexFixedVulnAssessmentRelationship";
	static final String A_I_A_I_PACKAGE = "AI.AIPackage";
	static final String CORE_PAYLOAD = "Core.Payload";
	static final String A_I_PRESENCE_TYPE = "AI.PresenceType";
	static final String LICENSING_CUSTOM_LICENSE_ADDITION = "Licensing.CustomLicenseAddition";
	static final String CORE_BOM = "Core.Bom";
	static final String CORE_SOFTWARE_AGENT = "Core.SoftwareAgent";
	static final String EXPANDED_LICENSE_EXTENDABLE_LICENSE = "ExpandedLicense.ExtendableLicense";
	static final String CORE_AGENT = "Core.Agent";
	static final String CORE_DATE_TIME = "Core.DateTime";
	static final String CORE_RELATIONSHIP_TYPE = "Core.RelationshipType";
	
	static final String[] ALL_SPDX_CLASSES = {SOFTWARE_SOFTWARE_PURPOSE, DATASET_DATASET, CORE_SEM_VER, 
			CORE_LIFECYCLE_SCOPE_TYPE, LICENSING_WITH_ADDITION_OPERATOR, SECURITY_VEX_JUSTIFICATION_TYPE, 
			SOFTWARE_SOFTWARE_DEPENDENCY_LINK_TYPE, DATASET_CONFIDENTIALITY_LEVEL_TYPE, 
			SOFTWARE_SOFTWARE_DEPENDENCY_RELATIONSHIP, CORE_HASH_ALGORITHM, CORE_EXTERNAL_REFERENCE_TYPE, 
			SOFTWARE_SNIPPET, SECURITY_SSVC_VULN_ASSESSMENT_RELATIONSHIP, SECURITY_VEX_UNDER_INVESTIGATION_VULN_ASSESSMENT_RELATIONSHIP, 
			CORE_ANONYMOUS_PAYLOAD, LICENSING_LICENSE_ADDITION, DATASET_DATASET_TYPE, 
			LICENSING_OR_LATER_OPERATOR, CORE_POSITIVE_INTEGER_RANGE, DATASET_DATASET_AVAILABILITY_TYPE, 
			SOFTWARE_S_B_O_M_TYPE, SECURITY_VULN_ASSESSMENT_RELATIONSHIP, SECURITY_EPSS_VULN_ASSESSMENT_RELATIONSHIP, 
			EXPANDED_LICENSE_CONJUNCTIVE_LICENSE_SET, SECURITY_CVSS_V_2_VULN_ASSESSMENT_RELATIONSHIP, 
			CORE_RELATIONSHIP_COMPLETENESS, CORE_PROFILE_IDENTIFIER_TYPE, SOFTWARE_SOFTWARE_ARTIFACT, 
			LICENSING_LISTED_LICENSE, SECURITY_SSVC_DECISION_TYPE, SOFTWARE_SPDX_FILE, 
			CORE_ANNOTATION, CORE_MEDIA_TYPE, CORE_TOOL, CORE_EXTERNAL_MAP, CORE_EXTERNAL_IDENTIFIER, 
			CORE_ANNOTATION_TYPE, LICENSING_ANY_LICENSE_INFO, CORE_ELEMENT_COLLECTION, 
			CORE_HASH, A_I_SAFETY_RISK_ASSESSMENT_TYPE, SECURITY_CVSS_V_3_VULN_ASSESSMENT_RELATIONSHIP, 
			LICENSING_LICENSE_EXPRESSION, CORE_LIFECYCLE_SCOPED_RELATIONSHIP, LICENSING_LICENSE, 
			SOFTWARE_DEPENDENCY_CONDITIONALITY_TYPE, CORE_INTEGRITY_METHOD, CORE_BUNDLE, 
			SECURITY_EXPLOIT_CATALOG_VULN_ASSESSMENT_RELATIONSHIP, CORE_ARTIFACT, LICENSING_CUSTOM_LICENSE, 
			CORE_EXTERNAL_REFERENCE, CORE_DICTIONARY_ENTRY, EXPANDED_LICENSE_DISJUNCTIVE_LICENSE_SET, 
			LICENSING_LISTED_LICENSE_EXCEPTION, CORE_EXTERNAL_IDENTIFIER_TYPE, SECURITY_VEX_NOT_AFFECTED_VULN_ASSESSMENT_RELATIONSHIP, 
			CORE_ELEMENT, SECURITY_VULNERABILITY, CORE_NAMESPACE_MAP, CORE_PERSON, 
			SECURITY_VEX_VULN_ASSESSMENT_RELATIONSHIP, CORE_ORGANIZATION, SECURITY_EXPLOIT_CATALOG_TYPE, 
			CORE_CREATION_INFO, CORE_RELATIONSHIP, SECURITY_VEX_AFFECTED_VULN_ASSESSMENT_RELATIONSHIP, 
			BUILD_BUILD, SOFTWARE_SPDX_PACKAGE, SOFTWARE_SBOM, CORE_SPDX_DOCUMENT, 
			SECURITY_VEX_FIXED_VULN_ASSESSMENT_RELATIONSHIP, A_I_A_I_PACKAGE, CORE_PAYLOAD, 
			A_I_PRESENCE_TYPE, LICENSING_CUSTOM_LICENSE_ADDITION, CORE_BOM, CORE_SOFTWARE_AGENT, 
			EXPANDED_LICENSE_EXTENDABLE_LICENSE, CORE_AGENT, CORE_DATE_TIME, CORE_RELATIONSHIP_TYPE};
}
