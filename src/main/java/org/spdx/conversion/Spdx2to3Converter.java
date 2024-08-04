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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.InvalidSPDXAnalysisException;
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
import org.spdx.library.model.v3.core.LifecycleScopeType;
import org.spdx.library.model.v3.core.LifecycleScopedRelationship;
import org.spdx.library.model.v3.core.NoAssertionElement;
import org.spdx.library.model.v3.core.NoneElement;
import org.spdx.library.model.v3.core.Relationship;
import org.spdx.library.model.v3.core.RelationshipCompleteness;
import org.spdx.library.model.v3.core.RelationshipType;
import org.spdx.library.model.v3.core.SpdxDocument;
import org.spdx.library.model.v3.core.Tool;
import org.spdx.library.model.v3.expandedlicensing.ConjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.CustomLicense;
import org.spdx.library.model.v3.expandedlicensing.DisjunctiveLicenseSet;
import org.spdx.library.model.v3.expandedlicensing.ExternalLicense;
import org.spdx.library.model.v3.expandedlicensing.ListedLicense;
import org.spdx.library.model.v3.expandedlicensing.NoAssertionLicense;
import org.spdx.library.model.v3.expandedlicensing.NoneLicense;
import org.spdx.library.model.v3.expandedlicensing.OrLaterOperator;
import org.spdx.library.model.v3.expandedlicensing.WithAdditionOperator;
import org.spdx.library.model.v3.simplelicensing.AnyLicenseInfo;
import org.spdx.library.model.v3.software.Snippet;
import org.spdx.library.model.v3.software.SpdxFile;
import org.spdx.library.model.v3.software.SpdxPackage;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * @author Gary O'Neall
 * 
 * Converts SPDX spec version 2.X objects to SPDX spec version 3.X and stores the result in the
 * toModelStore
 *
 */
public class Spdx2to3Converter implements ISpdxConverter {
	
	 static final Logger logger = LoggerFactory.getLogger(Spdx2to3Converter.class);

	private static final Map<org.spdx.library.model.v2.enumerations.RelationshipType, RelationshipType> RELATIONSHIP_TYPE_MAP;
	
	private static final Map<org.spdx.library.model.v2.enumerations.RelationshipType, LifecycleScopeType> LIFECYCLE_SCOPE_MAP;

	private static final Set<org.spdx.library.model.v2.enumerations.RelationshipType> SWAP_TO_FROM_REL_TYPES;
	
	private static final Map<org.spdx.library.model.v2.enumerations.AnnotationType, AnnotationType> ANNOTATION_TYPE_MAP;
	
	static {
		Map<org.spdx.library.model.v2.enumerations.RelationshipType, RelationshipType> relationshipTypeMap = new HashMap<>();
		Map<org.spdx.library.model.v2.enumerations.RelationshipType, LifecycleScopeType> lifecycleScopeMap = new HashMap<>();
		Set<org.spdx.library.model.v2.enumerations.RelationshipType> swapToFromRelTypes = new HashSet<>();
		
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
		Optional<ModelObjectV3> existing = getExistingObject(fromDoc.getDocumentUri(), SpdxConstantsV3.CORE_SPDX_DOCUMENT);
		if (existing.isPresent()) {
			return (SpdxDocument)existing.get();
		}
		String toObjectUri = defaultUriPrefix + "document" + documentIndex++;
		String existingUri = this.alreadyConverted.putIfAbsent(fromDoc.getObjectUri(), toObjectUri);
		if (Objects.nonNull(existingUri)) {
			// small window if conversion occured since the last check already converted
			return (SpdxDocument)getExistingObject(fromDoc.getDocumentUri(), SpdxConstantsV3.CORE_SPDX_DOCUMENT).get();
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
		//TODO: Implement
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX DisjunctiveLicenseSet to an SPDX spec version 3 SPDX DisjunctiveLicenseSet and store the result
	 * in the toStore
	 * @param fromDisjunctiveLicenseSet an SPDX spec version 2 DisjunctiveLicenseSet to convert from
	 * @return an SPDX spec version 3 DisjunctiveLicenseSet
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public DisjunctiveLicenseSet convertAndStore(org.spdx.library.model.v2.license.DisjunctiveLicenseSet fromDisjunctiveLicenseSet) throws InvalidSPDXAnalysisException {
		//TODO: Implement
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX ExtractedLicenseInfo to an SPDX spec version 3 SPDX CustomLicense and store the result
	 * in the toStore
	 * @param fromExtractedLicenseInfo an SPDX spec version 2 ExtractedLicenseInfo to convert from
	 * @return an SPDX spec version 3 CustomLicense
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public CustomLicense convertAndStore(org.spdx.library.model.v2.license.ExtractedLicenseInfo fromExtractedLicenseInfo) throws InvalidSPDXAnalysisException {
		//TODO: Implement
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX OrLaterOperator to an SPDX spec version 3 SPDX OrLaterOperator and store the result
	 * in the toStore
	 * @param fromOrLaterOperator an SPDX spec version 2 OrLaterOperator to convert from
	 * @return an SPDX spec version 3 OrLaterOperator
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public OrLaterOperator convertAndStore(org.spdx.library.model.v2.license.OrLaterOperator fromOrLaterOperator) throws InvalidSPDXAnalysisException {
		//TODO: Implement
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX SpdxListedLicense to an SPDX spec version 3 SPDX ListedLicense and store the result
	 * in the toStore
	 * @param fromSpdxListedLicense an SPDX spec version 2 SpdxListedLicense to convert from
	 * @return an SPDX spec version 3 ListedLicense
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public ListedLicense convertAndStore(org.spdx.library.model.v2.license.SpdxListedLicense fromSpdxListedLicense) throws InvalidSPDXAnalysisException {
		//TODO: Implement
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}
	
	/**
	 * Converts an SPDX spec version 2 SPDX WithExceptionOperator to an SPDX spec version 3 SPDX WithAdditionOperator and store the result
	 * in the toStore
	 * @param fromWithExceptionOperator an SPDX spec version 2 WithExceptionOperator to convert from
	 * @return an SPDX spec version 3 WithAdditionOperator
	 * @throws InvalidSPDXAnalysisException on any errors converting
	 */
	public WithAdditionOperator convertAndStore(org.spdx.library.model.v2.license.WithExceptionOperator fromWithExceptionOperator) throws InvalidSPDXAnalysisException {
		//TODO: Implement
		throw new InvalidSPDXAnalysisException("Unimplemented");
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
		// TODO Auto-generated method stub
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}

	/**
	 * Converts the SPDX 2 SpdxPackage to an SPDX 3 SpdxPackage and returns the converted package
	 * @param spdxPackage SPDX package to convert from
	 * @return SPDX 3 SpdxPackage
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	public SpdxPackage convertAndStore(org.spdx.library.model.v2.SpdxPackage spdxPackage) throws InvalidSPDXAnalysisException {
		// TODO Auto-generated method stub
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}

	/**
	 * Converts the SPDX 2 snippet to an SPDX 3 snippet and returns the converted snippet
	 * @param spdxSnippet SPDX 2 snippet to convert from
	 * @return SPDX 3 snippet
	 * @throws InvalidSPDXAnalysisException on any error in conversion
	 */
	public Snippet convertAndStore(org.spdx.library.model.v2.SpdxSnippet f) throws InvalidSPDXAnalysisException {
		// TODO Auto-generated method stub
		throw new InvalidSPDXAnalysisException("Unimplemented");
	}

}
