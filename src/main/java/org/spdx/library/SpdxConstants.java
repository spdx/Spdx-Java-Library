/**
 * Copyright (c) 2011 Source Auditor Inc.
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
 *
 */
package org.spdx.library;

import java.util.regex.Pattern;


/**
 * Constants which map to the SPDX specifications found at http://spdx.org/rdf/terms
 * @author Gary O'Neall
 *
 */
public class SpdxConstants {

	// Namespaces
	public static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String SPDX_NAMESPACE = "http://spdx.org/rdf/terms#";
	public static final String DOAP_NAMESPACE = "http://usefulinc.com/ns/doap#";
	public static final String OWL_NAMESPACE = "http://www.w3.org/2002/07/owl#";
	public static final String RDF_POINTER_NAMESPACE = "http://www.w3.org/2009/pointers#";
	public static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
	
	// RDF Properties - within the RDF_NAMESPACE
	public static final String RDF_PROP_TYPE = "type";
	public static final String RDF_PROP_RESOURCE = "resource";
	public static final String[] RDF_PROPERTIES = new String[] {RDF_PROP_TYPE, RDF_PROP_RESOURCE};
	
	
	// OWL Properties - within the OWL_NAMESPACE
	public static final String PROP_OWL_SAME_AS = "sameAs";
	public static final String[] OWL_PROPERTIES = new String[] {PROP_OWL_SAME_AS};
	
	// RDFS Properties - within the RDFS_NAMESPACE
	public static final String RDFS_PROP_COMMENT = "comment";
	public static final String RDFS_PROP_LABEL = "label";
	public static final String RDFS_PROP_SEE_ALSO = "seeAlso";
	public static final String[] RDFS_PROPERTIES = new String[] {RDFS_PROP_COMMENT, RDFS_PROP_LABEL, RDFS_PROP_SEE_ALSO};
	
	// DOAP Class Names - within the DOAP_NAMESPACE
	public static final String CLASS_DOAP_PROJECT = "Project";
	public static final String[] DOAP_CLASSES = {CLASS_DOAP_PROJECT};
	
	// DOAP Project Property Names - within the DOAP_NAMESPACE
	public static final String PROP_PROJECT_HOMEPAGE = "homepage";
	public static final String[] DOAP_PROPERTIES = new String[] {PROP_PROJECT_HOMEPAGE};
	
	// Pointer Class Names - with in the RDF_POINTER_NAMESPACE
	public static final String CLASS_POINTER_START_END_POINTER = "StartEndPointer";
	public static final String CLASS_POINTER_BYTE_OFFSET_POINTER = "ByteOffsetPointer";
	public static final String CLASS_POINTER_LINE_CHAR_POINTER = "LineCharPointer";
	public static final String CLASS_POINTER_COMPOUNT_POINTER = "CompoundPointer";
	public static final String CLASS_SINGLE_POINTER = "SinglePointer";
	public static final String[] POINTER_CLASSES = new String[] {
			CLASS_POINTER_START_END_POINTER, CLASS_POINTER_BYTE_OFFSET_POINTER, 
			CLASS_POINTER_LINE_CHAR_POINTER, CLASS_POINTER_COMPOUNT_POINTER, CLASS_SINGLE_POINTER
			};
	
	// Pointer Properties - with in the RDF_POINTER_NAMESPACE
	public static final String PROP_POINTER_START_POINTER = "startPointer";
	public static final String PROP_POINTER_END_POINTER = "endPointer";
	public static final String PROP_POINTER_REFERENCE = "reference";
	public static final String PROP_POINTER_OFFSET = "offset";
	public static final String PROP_POINTER_LINE_NUMBER = "lineNumber";
	public static final String[] POINTER_PROPERTIES = new String[] {
			PROP_POINTER_START_POINTER, PROP_POINTER_END_POINTER, PROP_POINTER_REFERENCE, PROP_POINTER_OFFSET,
			PROP_POINTER_LINE_NUMBER
	};
	
	// SPDX Class Names
	public static final String CLASS_SPDX_DOCUMENT = "SpdxDocument";
	public static final String CLASS_SPDX_PACKAGE = "Package";
	public static final String CLASS_SPDX_CREATION_INFO = "CreationInfo";
	public static final String CLASS_SPDX_CHECKSUM = "Checksum";
	public static final String CLASS_SPDX_ANY_LICENSE_INFO = "AnyLicenseInfo";
	public static final String CLASS_SPDX_SIMPLE_LICENSE_INFO = "SimpleLicensingInfo";
	public static final String CLASS_SPDX_CONJUNCTIVE_LICENSE_SET = "ConjunctiveLicenseSet";
	public static final String CLASS_SPDX_DISJUNCTIVE_LICENSE_SET = "DisjunctiveLicenseSet";
	public static final String CLASS_SPDX_EXTRACTED_LICENSING_INFO = "ExtractedLicensingInfo";
	public static final String CLASS_SPDX_LICENSE = "License";
	public static final String CLASS_SPDX_LISTED_LICENSE = "ListedLicense";
	public static final String CLASS_SPDX_LICENSE_EXCEPTION = "LicenseException";
	public static final String CLASS_SPDX_LISTED_LICENSE_EXCEPTION = "ListedLicenseException";
	public static final String CLASS_OR_LATER_OPERATOR = "OrLaterOperator";
	public static final String CLASS_WITH_EXCEPTION_OPERATOR = "WithExceptionOperator";
	public static final String CLASS_SPDX_FILE = "File";
	public static final String CLASS_SPDX_REVIEW = "Review";
	public static final String CLASS_SPDX_VERIFICATIONCODE = "PackageVerificationCode";
	public static final String CLASS_ANNOTATION = "Annotation";
	public static final String CLASS_RELATIONSHIP = "Relationship";
	public static final String CLASS_SPDX_ITEM = "SpdxItem";
	public static final String CLASS_SPDX_ELEMENT = "SpdxElement";
	public static final String CLASS_SPDX_NONE_ELEMENT = "SpdxNoneElement";
	public static final String CLASS_SPDX_NOASSERTION_ELEMENT = "SpdxNoAssertionElement";
	public static final String CLASS_EXTERNAL_DOC_REF = "ExternalDocumentRef";
	public static final String CLASS_SPDX_EXTERNAL_REFERENCE = "ExternalRef";
	public static final String CLASS_SPDX_REFERENCE_TYPE = "ReferenceType";
	public static final String CLASS_SPDX_SNIPPET = "Snippet";
	public static final String CLASS_NONE_LICENSE = "SpdxNoneLicense";
	public static final String CLASS_NOASSERTION_LICENSE = "SpdxNoAssertionLicense";
	public static final String CLASS_EXTERNAL_SPDX_ELEMENT = "ExternalSpdxElement";
	public static final String CLASS_EXTERNAL_EXTRACTED_LICENSE = "ExternalExtractedLicenseInfo";
	public static final String CLASS_CROSS_REF = "CrossRef";
	
	// all classes used including classes in non-SPDX namespaces
	public static final String[] ALL_SPDX_CLASSES = {CLASS_SPDX_DOCUMENT, CLASS_SPDX_PACKAGE, 
			CLASS_SPDX_CREATION_INFO, CLASS_SPDX_CHECKSUM, CLASS_SPDX_ANY_LICENSE_INFO, 
			CLASS_SPDX_SIMPLE_LICENSE_INFO, CLASS_SPDX_CONJUNCTIVE_LICENSE_SET, CLASS_SPDX_DISJUNCTIVE_LICENSE_SET, 
			CLASS_SPDX_EXTRACTED_LICENSING_INFO, CLASS_SPDX_LICENSE, CLASS_SPDX_LISTED_LICENSE, 
			CLASS_SPDX_LICENSE_EXCEPTION, CLASS_SPDX_LISTED_LICENSE_EXCEPTION, CLASS_OR_LATER_OPERATOR, CLASS_WITH_EXCEPTION_OPERATOR,
			CLASS_SPDX_FILE, CLASS_SPDX_REVIEW, CLASS_SPDX_VERIFICATIONCODE, CLASS_ANNOTATION,
			CLASS_RELATIONSHIP, CLASS_SPDX_ITEM, CLASS_SPDX_ELEMENT, 
			CLASS_SPDX_NONE_ELEMENT, CLASS_SPDX_NOASSERTION_ELEMENT, CLASS_EXTERNAL_DOC_REF,
			CLASS_SPDX_EXTERNAL_REFERENCE, CLASS_SPDX_REFERENCE_TYPE, CLASS_SPDX_SNIPPET,
			CLASS_NONE_LICENSE, CLASS_NOASSERTION_LICENSE, CLASS_EXTERNAL_SPDX_ELEMENT,
			CLASS_EXTERNAL_EXTRACTED_LICENSE, CLASS_CROSS_REF,
			// DOAP Namespace
			CLASS_DOAP_PROJECT,
			// RDF Pointer Namespace
			CLASS_POINTER_START_END_POINTER, CLASS_POINTER_BYTE_OFFSET_POINTER, 
			CLASS_POINTER_COMPOUNT_POINTER, CLASS_POINTER_LINE_CHAR_POINTER, CLASS_SINGLE_POINTER};
	
	// classes that use the listed license URI for their namespace
	public static final String[] LISTED_LICENSE_URI_CLASSES = {CLASS_SPDX_LISTED_LICENSE, CLASS_SPDX_LICENSE_EXCEPTION};
	
	// Enumeration class names
	public static final String ENUM_FILE_TYPE = "FileType";
	public static final String ENUM_ANNOTATION_TYPE = "AnnotationType";
	public static final String ENUM_CHECKSUM_ALGORITHM_TYPE = "ChecksumAlgorithm";
	public static final String ENUM_REFERENCE_CATEGORY_TYPE = "ReferenceCategory";
	public static final String ENUM_REFERENCE_RELATIONSHIP_TYPE = "RelationshipType";
	// General SPDX Properties
	public static final String PROP_VALUE_NONE = "none";
	public static final String URI_VALUE_NONE = SPDX_NAMESPACE  + PROP_VALUE_NONE;
	public static final String PROP_VALUE_NOASSERTION = "noassertion";
	public static final String URI_VALUE_NOASSERTION = SPDX_NAMESPACE + PROP_VALUE_NOASSERTION;
	public static final String SPDX_IDENTIFIER = "SPDXID";
	public static final String EXTERNAL_DOCUMENT_REF_IDENTIFIER = "externalDocumentId";
	
	// SPDX Document Properties
	// The comment property is the RDFS_PROP_COMMENT property in the rdfs namespace
	public static final String PROP_SPDX_REVIEWED_BY = "reviewed";
	public static final String PROP_SPDX_EXTRACTED_LICENSES = "hasExtractedLicensingInfo";
	public static final String PROP_SPDX_VERSION = "specVersion"; // TODO: Migrate this to PROP_SPDX_SPEC_VERSION in 3.0.  See issue 
	public static final String PROP_SPDX_SPEC_VERSION = "spdxVersion";
	public static final String PROP_SPDX_CREATION_INFO = "creationInfo";
	public static final String PROP_SPDX_PACKAGE = "describesPackage";
	@Deprecated		// since 2.0  Planned to be removed in next major spec revision
	public static final String PROP_SPDX_FILE_REFERENCE = "referencesFile";
	public static final String PROP_SPDX_DATA_LICENSE = "dataLicense";
	public static final String PROP_SPDX_EXTERNAL_DOC_REF = "externalDocumentRef";
	public static final String SPDX_DOCUMENT_ID = "SPDXRef-DOCUMENT";
	public static final String PROP_DOCUMENT_NAMESPACE = "documentNamespace";
	
	// SPDX Document properties for JSON and YAML files
	public static final String PROP_DOCUMENT_DESCRIBES = "documentDescribes"; //TODO: This is not yet approved in the spec - see issue #
	public static final String PROP_DOCUMENT_FILES = "files"; //TODO: This is not yet approved in the spec - see issue #
	public static final String PROP_DOCUMENT_PACKAGES = "packages"; //TODO: This is not yet approved in the spec - see issue #
	public static final String PROP_DOCUMENT_SNIPPETS = "snippets"; //TODO: This is not yet approved in the spec - see issue #
	public static final String PROP_DOCUMENT_RELATIONSHIPS = "relationships"; //TODO: This is not yet approved in the spec - see issue #
	
	// SPDX CreationInfo Properties
	// The comment property is the RDFS_PROP_COMMENT property in the rdfs namespace
	public static final String PROP_CREATION_CREATOR = "creator";
	public static final String PROP_CREATION_CREATED = "created"; // creation timestamp
	public static final String PROP_LICENSE_LIST_VERSION = "licenseListVersion";
	public static final String CREATOR_PREFIX_PERSON = "Person:";
	public static final String CREATOR_PREFIX_ORGANIZATION = "Organization:";
	public static final String CREATOR_PREFIX_TOOL = "Tool:";
	
	// SPDX Checksum Properties
	public static final String PROP_CHECKSUM_ALGORITHM = "algorithm";
	public static final String PROP_CHECKSUM_VALUE = "checksumValue";
	public static final String ALGORITHM_SHA1 = "SHA1";
	public static final String PROP_CHECKSUM_ALGORITHM_SHA1 = "checksumAlgorithm_sha1";
	
	// SPDX PackageVerificationCode Properties
	public static final String PROP_VERIFICATIONCODE_IGNORED_FILES = "packageVerificationCodeExcludedFile";
	public static final String PROP_VERIFICATIONCODE_VALUE = "packageVerificationCodeValue";

	// SPDX Element Properties 
	public static final String PROP_ANNOTATION = "annotation";
	public static final String PROP_RELATIONSHIP = "relationship";
	
	// SPDX Item Properties 
	public static final String PROP_LICENSE_CONCLUDED = "licenseConcluded";
	public static final String PROP_COPYRIGHT_TEXT = "copyrightText";	
	public static final String PROP_LIC_COMMENTS = "licenseComments";
	public static final String PROP_LICENSE_DECLARED = "licenseDeclared";
	public static final String PROP_ATTRIBUTION_TEXT = "attributionText";
	
	// SPDX Package Properties
	public static final String PROP_PACKAGE_DECLARED_NAME = "name";
	public static final String PROP_PACKAGE_FILE_NAME = "packageFileName";
	public static final String PROP_PACKAGE_CHECKSUM = "checksum";
	public static final String PROP_PACKAGE_DOWNLOAD_URL = "downloadLocation";
	public static final String PROP_PACKAGE_SOURCE_INFO = "sourceInfo";
	public static final String PROP_PACKAGE_DECLARED_LICENSE = "licenseDeclared";
	public static final String PROP_PACKAGE_CONCLUDED_LICENSE = PROP_LICENSE_CONCLUDED;
	public static final String PROP_PACKAGE_DECLARED_COPYRIGHT = PROP_COPYRIGHT_TEXT;
	public static final String PROP_PACKAGE_SHORT_DESC = "summary";
	public static final String PROP_PACKAGE_DESCRIPTION = "description";
	public static final String PROP_PACKAGE_FILE = "hasFile";
	public static final String PROP_PACKAGE_VERIFICATION_CODE = "packageVerificationCode";
	public static final String PROP_PACKAGE_LICENSE_INFO_FROM_FILES = "licenseInfoFromFiles";
	public static final String PROP_PACKAGE_LICENSE_COMMENT = "licenseComments";
	public static final String PROP_PACKAGE_VERSION_INFO = "versionInfo";
	public static final String PROP_PACKAGE_ORIGINATOR = "originator";
	public static final String PROP_PACKAGE_SUPPLIER = "supplier";
	public static final String PROP_PACKAGE_FILES_ANALYZED = "filesAnalyzed";
	public static final String PROP_EXTERNAL_REF = "externalRef";
	
	// SPDX License Properties
	// The comment property is the RDFS_PROP_COMMENT property in the rdfs namespace
	// the seeAlso property is in the RDFS_PROP_SEE_ALSO property in the rdfs namespace
	public static final String PROP_LICENSE_ID = "licenseId";
	public static final String PROP_LICENSE_TEXT = "licenseText";
	public static final String PROP_LICENSE_TEXT_HTML = "licenseTextHtml";
	public static final String PROP_EXTRACTED_TEXT = "extractedText";
	public static final String PROP_LICENSE_NAME = "licenseName";
	public static final String PROP_STD_LICENSE_NAME_VERSION_1 = "licenseName";	// old property name (pre 1.1 spec)
	public static final String PROP_STD_LICENSE_NAME = "name";
	public static final String PROP_STD_LICENSE_URL_VERSION_1 = "licenseSourceUrl";	// This has been replaced with the rdfs:seeAlso property
	public static final String PROP_STD_LICENSE_NOTES_VERSION_1 = "licenseNotes";	// old property name (pre 1.1 spec)
	public static final String PROP_STD_LICENSE_HEADER_VERSION_1 = "licenseHeader";	// old property name (pre 1.1 spec)
	public static final String PROP_STD_LICENSE_NOTICE = "standardLicenseHeader";
	public static final String PROP_STD_LICENSE_HEADER_TEMPLATE = "standardLicenseHeaderTemplate";
	public static final String PROP_LICENSE_HEADER_HTML = "standardLicenseHeaderHtml";
	public static final String PROP_STD_LICENSE_TEMPLATE_VERSION_1 = "licenseTemplate";		// old property name (pre 1.2 spec)
	public static final String PROP_STD_LICENSE_TEMPLATE = "standardLicenseTemplate";
	public static final String PROP_STD_LICENSE_OSI_APPROVED = "isOsiApproved";
	public static final String PROP_STD_LICENSE_FSF_LIBRE = "isFsfLibre";
	public static final String PROP_STD_LICENSE_OSI_APPROVED_VERSION_1 = "licenseOsiApproved";	// old property name (pre 1.1 spec)
	public static final String PROP_LICENSE_SET_MEMEBER = "member";
	public static final String TERM_LICENSE_NOASSERTION = PROP_VALUE_NOASSERTION;
	public static final String TERM_LICENSE_NONE = PROP_VALUE_NONE;
	public static final String PROP_LICENSE_EXCEPTION_ID = "licenseExceptionId";
	public static final String PROP_EXAMPLE = "example";
	public static final String PROP_EXCEPTION_TEXT = "licenseExceptionText";
	public static final String PROP_EXCEPTION_TEXT_HTML = "exceptionTextHtml";
	public static final String PROP_EXCEPTION_TEMPLATE = "licenseExceptionTemplate";
	public static final String PROP_LICENSE_EXCEPTION = "licenseException";
	public static final String PROP_LIC_ID_DEPRECATED = "isDeprecatedLicenseId";
	public static final String PROP_LIC_DEPRECATED_VERSION = "deprecatedVersion";
	public static final String PROP_CROSS_REF = "crossRef";
	
	// SPDX Listed License constants
	public static final String LISTED_LICENSE_URL = "https://spdx.org/licenses/";
	// http rather than https since RDF depends on the exact string, 
	// we were not able to update the namespace variable to match the URL's.
	public static final String LISTED_LICENSE_NAMESPACE_PREFIX = "http://spdx.org/licenses/";
	
	// crossrefs details (crossRef) properties
	public static final String PROP_CROSS_REF_IS_VALID = "isValid";
	public static final String PROP_CROSS_REF_WAYBACK_LINK = "isWayBackLink";
	public static final String PROP_CROSS_REF_MATCH = "match";
	public static final String PROP_CROSS_REF_URL = "url";
	public static final String PROP_CROSS_REF_IS_LIVE = "isLive";
	public static final String PROP_CROSS_REF_TIMESTAMP = "timestamp";
	public static final String PROP_CROSS_REF_ORDER = "order";
	
	// SpdxElement Properties
	public static final String PROP_NAME = "name";
	
	// SPDX File Properties
	// The comment property is the RDFS_PROP_COMMENT property in the rdfs namespace
	public static final String PROP_FILE_NAME = "fileName";
	public static final String PROP_FILE_TYPE = "fileType";
	public static final String PROP_FILE_LICENSE = PROP_LICENSE_CONCLUDED;
	public static final String PROP_FILE_COPYRIGHT = PROP_COPYRIGHT_TEXT;
	public static final String PROP_FILE_CHECKSUM = "checksum";
	public static final String PROP_FILE_SEEN_LICENSE = "licenseInfoInFile";	
	public static final String PROP_FILE_LIC_COMMENTS = PROP_LIC_COMMENTS;
	public static final String PROP_FILE_ARTIFACTOF = "artifactOf";
	public static final String PROP_FILE_FILE_DEPENDENCY = "fileDependency"; 
	public static final String PROP_FILE_CONTRIBUTOR = "fileContributor";
	public static final String PROP_FILE_NOTICE = "noticeText";
	
	// SPDX Snippet Properties
	public static final String PROP_SNIPPET_FROM_FILE = "snippetFromFile";
	public static final String PROP_SNIPPET_RANGE = "range";
	public static final String PROP_LICENSE_INFO_FROM_SNIPPETS = "licenseInfoInSnippet";
	
	// SPDX File Type Properties
	public static final String PROP_FILE_TYPE_SOURCE = "fileType_source";
	public static final String PROP_FILE_TYPE_ARCHIVE = "fileType_archive";
	public static final String PROP_FILE_TYPE_BINARY = "fileType_binary";
	public static final String PROP_FILE_TYPE_OTHER = "fileType_other";
	
	public static final String FILE_TYPE_SOURCE = "SOURCE";
	public static final String FILE_TYPE_ARCHIVE = "ARCHIVE";
	public static final String FILE_TYPE_BINARY = "BINARY";
	public static final String FILE_TYPE_OTHER = "OTHER";
	
	// SPDX Annotation Properties
	public static final String PROP_ANNOTATOR = "annotator";
	public static final String PROP_ANNOTATION_DATE = "annotationDate";
	public static final String PROP_ANNOTATION_TYPE = "annotationType";
	
	// SPDX Relationship Properties
	public static final String PROP_RELATED_SPDX_ELEMENT = "relatedSpdxElement";
	public static final String PROP_RELATIONSHIP_TYPE = "relationshipType";
	public static final String PROP_SPDX_ELEMENTID = "spdxElementId";
	
	// ExternalDocumentRef properties
	public static final String PROP_EXTERNAL_DOC_CHECKSUM = "checksum";
	public static final String PROP_EXTERNAL_SPDX_DOCUMENT = "spdxDocument";
	public static final String PROP_EXTERNAL_DOCUMENT_ID = "externalDocumentId";
	
	// External Reference properties
	public static final String PROP_REFERENCE_CATEGORY = "referenceCategory";
	public static final String PROP_REFERENCE_TYPE = "referenceType";
	public static final String PROP_REFERENCE_LOCATOR = "referenceLocator";
	
	// SPDX Review Properties
	// NOTE: These have all been deprecated as of SPDX 2.0
	// The comment property is the RDFS_PROP_COMMENT property in the rdfs namespace
	@Deprecated
	public static final String PROP_REVIEW_REVIEWER = "reviewer";
	@Deprecated
	public static final String PROP_REVIEW_DATE = "reviewDate";
	
	// Date format - NOTE: This format does not handle milliseconds.  Use Instant.parse for full ISO 8601 parsing
	public static final String SPDX_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	// license ID format
	public static String NON_STD_LICENSE_ID_PRENUM = "LicenseRef-";
	public static Pattern LICENSE_ID_PATTERN_NUMERIC = 
			Pattern.compile(NON_STD_LICENSE_ID_PRENUM+"(\\d+)$");	// Pattern for numeric only license IDs
	public static Pattern LICENSE_ID_PATTERN = Pattern.compile(NON_STD_LICENSE_ID_PRENUM+"([0-9a-zA-Z\\.\\-\\_]+)\\+?$");
	
	// SPDX Element Reference format
	public static String SPDX_ELEMENT_REF_PRENUM = "SPDXRef-";
	public static Pattern SPDX_ELEMENT_REF_PATTERN = Pattern.compile(SPDX_ELEMENT_REF_PRENUM+"([0-9a-zA-Z\\.\\-\\+]+)$");
	
	// External Document ID format
	public static String EXTERNAL_DOC_REF_PRENUM = "DocumentRef-";
	public static Pattern EXTERNAL_DOC_REF_PATTERN = Pattern.compile(EXTERNAL_DOC_REF_PRENUM+"([0-9a-zA-Z\\.\\-\\+]+)$");
	public static Pattern EXTERNAL_ELEMENT_REF_PATTERN = Pattern.compile("(.+[0-9a-zA-Z\\.\\-\\+]+):("+SPDX_ELEMENT_REF_PRENUM+"[0-9a-zA-Z\\.\\-\\+]+)$");	
	public static Pattern EXTERNAL_SPDX_ELEMENT_URI_PATTERN = Pattern.compile("(.+)#("+SPDX_ELEMENT_REF_PRENUM+"[0-9a-zA-Z\\.\\-\\+]+)$");
	public static Pattern EXTERNAL_EXTRACTED_LICENSE_URI_PATTERN = Pattern.compile("(.+)#("+NON_STD_LICENSE_ID_PRENUM+"[0-9a-zA-Z\\.\\-\\+]+)$");
	public static Pattern EXTERNAL_EXTRACTED_LICENSE_PATTERN = Pattern.compile("(.+[0-9a-zA-Z\\.\\-\\+]+):("+NON_STD_LICENSE_ID_PRENUM+"[0-9a-zA-Z\\.\\-\\+]+)$");	
	
	// SPDX version format
	public static Pattern SPDX_VERSION_PATTERN = Pattern.compile("^SPDX-(\\d+)\\.(\\d+)$");
	
	// Download Location Format
	private static final String SUPPORTED_DOWNLOAD_REPOS = "(git|hg|svn|bzr)";
	private static final String URL_PATTERN = "(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/|ssh:\\/\\/|git:\\/\\/|svn:\\/\\/|sftp:\\/\\/|ftp:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+){0,100}\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?";
	private static final String GIT_PATTERN = "(git\\+git@[a-zA-Z0-9\\.]+:[a-zA-Z0-9/\\\\.@]+)";
	private static final String BAZAAR_PATTERN = "(bzr\\+lp:[a-zA-Z0-9\\.]+)";
	public static final Pattern DOWNLOAD_LOCATION_PATTERN = Pattern.compile("^(NONE|NOASSERTION|(("+SUPPORTED_DOWNLOAD_REPOS+"\\+)?"+URL_PATTERN+")|"+GIT_PATTERN+"|"+BAZAAR_PATTERN+")$", Pattern.CASE_INSENSITIVE);

	// License list version Format

    public static final Pattern LICENSE_LIST_VERSION_PATTERN = Pattern.compile("^[a-zA-Z0-9]+\\.[a-zA-Z0-9]+");
	// Standard value strings
	public static String NONE_VALUE = "NONE";
	public static String NOASSERTION_VALUE = "NOASSERTION";
	public static final String[] LITERAL_VALUES = new String[]{NONE_VALUE, NOASSERTION_VALUE};
	
	// data license ID
	public static final String SPDX_DATA_LICENSE_ID_VERSION_1_0 = "PDDL-1.0";
	public static final String SPDX_DATA_LICENSE_ID = "CC0-1.0";
	
	public static final String SPDX_LISTED_REFERENCE_TYPES_PREFIX = "http://spdx.org/rdf/references/";
	
	// License XML constants
	public static final String LICENSEXML_URI = "http://www.spdx.org/license";
	public static final String LICENSEXML_ELEMENT_LICENSE_COLLECTION = "SPDXLicenseCollection";
	public static final String LICENSEXML_ELEMENT_LICENSE = "license";
	public static final String LICENSEXML_ELEMENT_EXCEPTION = "exception";
	public static final String LICENSEXML_ATTRIBUTE_ID = "licenseId";
	public static final String LICENSEXML_ATTRIBUTE_DEPRECATED = "isDeprecated";
	public static final String LICENSEXML_ATTRIBUTE_DEPRECATED_VERSION = "deprecatedVersion";
	public static final String LICENSEXML_ATTRIBUTE_OSI_APPROVED = "isOsiApproved";
	public static final String LICENSEXML_ATTRIBUTE_FSF_LIBRE = "isFsfLibre";
	public static final String LICENSEXML_ATTRIBUTE_NAME = "name";
	public static final String LICENSEXML_ATTRIBUTE_LIST_VERSION_ADDED = "listVersionAdded";
	public static final String LICENSEXML_ELEMENT_CROSS_REFS = "crossRefs";
	public static final String LICENSEXML_ELEMENT_CROSS_REF = "crossRef";
	public static final String LICENSEXML_ELEMENT_NOTES = "notes";
	public static final String LICENSEXML_ELEMENT_STANDARD_LICENSE_HEADER = "standardLicenseHeader";
	public static final String LICENSEXML_ELEMENT_TITLE_TEXT = "titleText";
	public static final String LICENSEXML_ELEMENT_COPYRIGHT_TEXT = "copyrightText";
	public static final String LICENSEXML_ELEMENT_BULLET = "bullet";
	public static final String LICENSEXML_ELEMENT_LIST = "list";
	public static final String LICENSEXML_ELEMENT_ITEM = "item";
	public static final String LICENSEXML_ELEMENT_PARAGRAPH = "p";
	public static final String LICENSEXML_ELEMENT_OPTIONAL = "optional";
	public static final String LICENSEXML_ELEMENT_ALT = "alt";
	public static final String LICENSEXML_ATTRIBUTE_ALT_NAME = "name";
	public static final String LICENSEXML_ATTRIBUTE_ALT_MATCH = "match";
	public static final String LICENSEXML_ELEMENT_BREAK = "br";
	public static final String LICENSEXML_ELEMENT_TEXT = "text";
}