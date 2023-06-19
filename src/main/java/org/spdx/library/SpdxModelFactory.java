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
package org.spdx.library;

import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.compat.v2.CompatibleModelStoreWrapper;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class to create ModelObjects based on the type and SPDX Spec Version
 * Types are defined classes in the SpdxConstantsCompatV2 class and map to the standard SPDX model
 * 
 * @author Gary O'Neall
 *
 */
public class SpdxModelFactory {
	
	static final Logger logger = LoggerFactory.getLogger(SpdxModelFactory.class);
	
	public static Map<String, Class<?>> SPDX_TYPE_TO_CLASS_V2;
	public static Map<String, Class<?>> SPDX_TYPE_TO_CLASS_V3;
	public static Map<Class<?>, String> SPDX_CLASS_TO_TYPE;
	static {
		Map<String, Class<?>> typeToClassV2 = new HashMap<>();
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_DOCUMENT, org.spdx.library.model.compat.v2.SpdxDocument.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_PACKAGE, org.spdx.library.model.compat.v2.SpdxPackage.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_CREATION_INFO, org.spdx.library.model.compat.v2.SpdxCreatorInformation.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_CHECKSUM, org.spdx.library.model.compat.v2.Checksum.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_ANY_LICENSE_INFO, org.spdx.library.model.compat.v2.license.AnyLicenseInfo.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_SIMPLE_LICENSE_INFO, org.spdx.library.model.compat.v2.license.SimpleLicensingInfo.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_CONJUNCTIVE_LICENSE_SET, org.spdx.library.model.compat.v2.license.ConjunctiveLicenseSet.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_DISJUNCTIVE_LICENSE_SET, org.spdx.library.model.compat.v2.license.DisjunctiveLicenseSet.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_EXTRACTED_LICENSING_INFO, org.spdx.library.model.compat.v2.license.ExtractedLicenseInfo.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_LICENSE, org.spdx.library.model.compat.v2.license.License.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, org.spdx.library.model.compat.v2.license.SpdxListedLicense.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_LICENSE_EXCEPTION, org.spdx.library.model.compat.v2.license.LicenseException.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, org.spdx.library.model.compat.v2.license.ListedLicenseException.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_OR_LATER_OPERATOR, org.spdx.library.model.compat.v2.license.OrLaterOperator.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_WITH_EXCEPTION_OPERATOR, org.spdx.library.model.compat.v2.license.WithExceptionOperator.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_FILE, org.spdx.library.model.compat.v2.SpdxFile.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_VERIFICATIONCODE, org.spdx.library.model.compat.v2.SpdxPackageVerificationCode.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_ANNOTATION, org.spdx.library.model.compat.v2.Annotation.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_RELATIONSHIP, org.spdx.library.model.compat.v2.Relationship.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_ITEM, org.spdx.library.model.compat.v2.SpdxItem.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_ELEMENT, org.spdx.library.model.compat.v2.SpdxElement.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_NONE_ELEMENT, org.spdx.library.model.compat.v2.SpdxNoneElement.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_NOASSERTION_ELEMENT, org.spdx.library.model.compat.v2.SpdxNoAssertionElement.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_EXTERNAL_DOC_REF, org.spdx.library.model.compat.v2.ExternalDocumentRef.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_EXTERNAL_REFERENCE, org.spdx.library.model.compat.v2.ExternalRef.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_REFERENCE_TYPE, org.spdx.library.model.compat.v2.ReferenceType.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SPDX_SNIPPET, org.spdx.library.model.compat.v2.SpdxSnippet.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_NOASSERTION_LICENSE, org.spdx.library.model.compat.v2.license.SpdxNoAssertionLicense.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_NONE_LICENSE, org.spdx.library.model.compat.v2.license.SpdxNoneLicense.class);
		typeToClassV2.put(org.spdx.library.model.compat.v2.GenericModelObject.GENERIC_MODEL_OBJECT_TYPE, org.spdx.library.model.compat.v2.GenericModelObject.class);
		typeToClassV2.put(org.spdx.library.model.compat.v2.GenericSpdxElement.GENERIC_SPDX_ELEMENT_TYPE, org.spdx.library.model.compat.v2.GenericSpdxElement.class);
		typeToClassV2.put(org.spdx.library.model.compat.v2.GenericSpdxItem.GENERIC_SPDX_ITEM_TYPE, org.spdx.library.model.compat.v2.GenericSpdxItem.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_EXTERNAL_SPDX_ELEMENT, org.spdx.library.model.compat.v2.ExternalSpdxElement.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_POINTER_START_END_POINTER, org.spdx.library.model.compat.v2.pointer.StartEndPointer.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_POINTER_BYTE_OFFSET_POINTER, org.spdx.library.model.compat.v2.pointer.ByteOffsetPointer.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_POINTER_LINE_CHAR_POINTER, org.spdx.library.model.compat.v2.pointer.LineCharPointer.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_POINTER_COMPOUNT_POINTER, org.spdx.library.model.compat.v2.pointer.CompoundPointer.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_SINGLE_POINTER, org.spdx.library.model.compat.v2.pointer.SinglePointer.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_CROSS_REF, org.spdx.library.model.compat.v2.license.CrossRef.class);
		typeToClassV2.put(SpdxConstantsCompatV2.ENUM_FILE_TYPE, org.spdx.library.model.compat.v2.enumerations.FileType.class);
		typeToClassV2.put(SpdxConstantsCompatV2.ENUM_ANNOTATION_TYPE, org.spdx.library.model.compat.v2.enumerations.AnnotationType.class);
		typeToClassV2.put(SpdxConstantsCompatV2.ENUM_CHECKSUM_ALGORITHM_TYPE, org.spdx.library.model.compat.v2.enumerations.ChecksumAlgorithm.class);
		typeToClassV2.put(SpdxConstantsCompatV2.ENUM_REFERENCE_CATEGORY_TYPE, org.spdx.library.model.compat.v2.enumerations.ReferenceCategory.class);
		typeToClassV2.put(SpdxConstantsCompatV2.ENUM_REFERENCE_RELATIONSHIP_TYPE, org.spdx.library.model.compat.v2.enumerations.RelationshipType.class);
		typeToClassV2.put(SpdxConstantsCompatV2.CLASS_EXTERNAL_EXTRACTED_LICENSE, org.spdx.library.model.compat.v2.license.ExternalExtractedLicenseInfo.class);	
		typeToClassV2.put(SpdxConstantsCompatV2.ENUM_PURPOSE, org.spdx.library.model.compat.v2.enumerations.Purpose.class);
		SPDX_TYPE_TO_CLASS_V2 = Collections.unmodifiableMap(typeToClassV2);
		Map<String, Class<?>> typeToClassV3 = new HashMap<>();
		//TODO Add V3 class strings
		SPDX_TYPE_TO_CLASS_V3 = Collections.unmodifiableMap(typeToClassV3);
		Map<Class<?>, String> classToType = new HashMap<>();
		for (Entry<String, Class<?>> entry:typeToClassV2.entrySet()) {
			classToType.put(entry.getValue(), entry.getKey());
		}
		for (Entry<String, Class<?>> entry:typeToClassV3.entrySet()) {
			classToType.put(entry.getValue(), entry.getKey());
		}
		
		SPDX_CLASS_TO_TYPE = Collections.unmodifiableMap(classToType);
	}
	
	
	/**
	 * Create an SPDX version 2.X document with default values for creator, created, licenseListVersion, data license and specVersion
	 * @param modelStore Where to store the SPDX Document
	 * @param documentUri unique URI for the SPDX document
	 * @param copyManager if non-null, allows for copying of properties from other model stores or document URI's when referenced
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public static org.spdx.library.model.compat.v2.SpdxDocument createSpdxDocumentV2(IModelStore modelStore, String documentUri, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		org.spdx.library.model.compat.v2.SpdxDocument retval = new org.spdx.library.model.compat.v2.SpdxDocument(modelStore, documentUri, copyManager, true);
		String date = new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT).format(new Date());
		org.spdx.library.model.compat.v2.SpdxCreatorInformation creationInfo = new org.spdx.library.model.compat.v2.SpdxCreatorInformation(
				modelStore, documentUri, modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		creationInfo.getCreators().add("Tool: SPDX Tools");
		creationInfo.setCreated(date);
		creationInfo.setLicenseListVersion(org.spdx.library.model.compat.v2.license.ListedLicenses.getListedLicenses().getLicenseListVersion());
		retval.setCreationInfo(creationInfo);
		retval.setDataLicense(org.spdx.library.model.compat.v2.license.ListedLicenses.getListedLicenses().getListedLicenseById(SpdxConstantsCompatV2.SPDX_DATA_LICENSE_ID));
		retval.setSpecVersion(Version.CURRENT_SPDX_VERSION);
		return retval;
	}
	
	/**
	 * Create an SPDX version 2 model object in a model store given the document URI, ID and type
	 * @param modelStore model store where the object is to be created
	 * @param documentUri document URI for the stored item
	 * @param id for the item
	 * @param type SPDX class or type
	 *  @param copyManager if non-null, allows for copying of properties from other model stores or document URI's when referenced
	 * @return a ModelObject of type type
	 * @throws InvalidSPDXAnalysisException
	 */
	public static org.spdx.library.model.compat.v2.ModelObject createModelObjectV2(IModelStore modelStore, String documentUri, String id,
			String type, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore, "Model store can not be null");
		Objects.requireNonNull(documentUri, "A document URI or namespace must be supplied for all SPDX version 2 model objects");
		Objects.requireNonNull(id, "ID must not be null");
		return getModelObjectV2(modelStore, documentUri, id, type, copyManager, true);
	 }
	
	/**
	 * Create an SPDX spec version 2.X model object in a model store given the document URI, ID and type
	 * @param modelStore model store where the object is to be created
	 * @param documentUri document URI for the stored item
	 * @param id ID for the item
	 * @param type SPDX class or type
	 * @param copyManager if non-null, allows for copying of properties from other model stores or document URI's when referenced
	 * @param create if true, create the model object if it does not already exist
	 * @return a ModelObject of type type
	 * @throws InvalidSPDXAnalysisException
	 */
	public static org.spdx.library.model.compat.v2.ModelObject getModelObjectV2(IModelStore modelStore, String documentUri, String id,
			String type, ModelCopyManager copyManager, boolean create) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore, "Model store can not be null");
		Objects.requireNonNull(documentUri, "A document URI or namespace must be supplied for all SPDX version 2 model objects");
		Objects.requireNonNull(id, "ID must not be null");
		switch (type) {
		case SpdxConstantsCompatV2.CLASS_SPDX_DOCUMENT: return new org.spdx.library.model.compat.v2.SpdxDocument(modelStore, documentUri, copyManager, create); //Note: the ID is ignored
		case SpdxConstantsCompatV2.CLASS_SPDX_PACKAGE: return new org.spdx.library.model.compat.v2.SpdxPackage(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_CREATION_INFO: return new org.spdx.library.model.compat.v2.SpdxCreatorInformation(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_CHECKSUM: return new org.spdx.library.model.compat.v2.Checksum(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_ANY_LICENSE_INFO: throw new InvalidSPDXAnalysisException("Can not create abstract AnyLicensing Info.  Must specify one of the concrete classes");
		case SpdxConstantsCompatV2.CLASS_SPDX_SIMPLE_LICENSE_INFO:  throw new InvalidSPDXAnalysisException("Can not create abstract SimpleLicensingInfo.  Must specify one of the concrete classes");
		case SpdxConstantsCompatV2.CLASS_SPDX_CONJUNCTIVE_LICENSE_SET: return new org.spdx.library.model.compat.v2.license.ConjunctiveLicenseSet(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_DISJUNCTIVE_LICENSE_SET: return new org.spdx.library.model.compat.v2.license.DisjunctiveLicenseSet(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_EXTRACTED_LICENSING_INFO: return new org.spdx.library.model.compat.v2.license.ExtractedLicenseInfo(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_LICENSE: throw new InvalidSPDXAnalysisException("Can not create abstract License.  Must specify one of the concrete classes");
		case SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE: return new org.spdx.library.model.compat.v2.license.SpdxListedLicense(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_LICENSE_EXCEPTION: return new org.spdx.library.model.compat.v2.license.LicenseException(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION: return new org.spdx.library.model.compat.v2.license.ListedLicenseException(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_OR_LATER_OPERATOR: return new org.spdx.library.model.compat.v2.license.OrLaterOperator(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_WITH_EXCEPTION_OPERATOR: return new org.spdx.library.model.compat.v2.license.WithExceptionOperator(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_FILE: return new org.spdx.library.model.compat.v2.SpdxFile(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_REVIEW: throw new RuntimeException("SPDX Review class is no longer supported");
		case SpdxConstantsCompatV2.CLASS_SPDX_VERIFICATIONCODE: return new org.spdx.library.model.compat.v2.SpdxPackageVerificationCode(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_ANNOTATION: return new org.spdx.library.model.compat.v2.Annotation(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_RELATIONSHIP: return new org.spdx.library.model.compat.v2.Relationship(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_ITEM: throw new RuntimeException("SPDX item is an abstract item and can not be created.");
		case SpdxConstantsCompatV2.CLASS_SPDX_ELEMENT: throw new RuntimeException("SPDX element is an abstract item and can not be created.");
		case SpdxConstantsCompatV2.CLASS_SPDX_NONE_ELEMENT: return new org.spdx.library.model.compat.v2.SpdxNoneElement(modelStore, documentUri);
		case SpdxConstantsCompatV2.CLASS_SPDX_NOASSERTION_ELEMENT: return new org.spdx.library.model.compat.v2.SpdxNoAssertionElement(modelStore, documentUri);
		case SpdxConstantsCompatV2.CLASS_EXTERNAL_DOC_REF: return new org.spdx.library.model.compat.v2.ExternalDocumentRef(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_EXTERNAL_REFERENCE: return new org.spdx.library.model.compat.v2.ExternalRef(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_EXTERNAL_EXTRACTED_LICENSE: return new org.spdx.library.model.compat.v2.license.ExternalExtractedLicenseInfo(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_REFERENCE_TYPE:  throw new RuntimeException("Reference type can only be created with a type supplied.");
		case SpdxConstantsCompatV2.CLASS_SPDX_SNIPPET: return new org.spdx.library.model.compat.v2.SpdxSnippet(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_NOASSERTION_LICENSE: return new org.spdx.library.model.compat.v2.license.SpdxNoAssertionLicense(modelStore, documentUri);
		case SpdxConstantsCompatV2.CLASS_NONE_LICENSE: return new org.spdx.library.model.compat.v2.license.SpdxNoneLicense(modelStore, documentUri);
		case org.spdx.library.model.compat.v2.GenericModelObject.GENERIC_MODEL_OBJECT_TYPE: return new org.spdx.library.model.compat.v2.GenericModelObject(modelStore, documentUri, id, copyManager, create);
		case org.spdx.library.model.compat.v2.GenericSpdxElement.GENERIC_SPDX_ELEMENT_TYPE: return new org.spdx.library.model.compat.v2.GenericSpdxElement(modelStore, documentUri, id, copyManager, create);
		case org.spdx.library.model.compat.v2.GenericSpdxItem.GENERIC_SPDX_ITEM_TYPE: return new org.spdx.library.model.compat.v2.GenericSpdxItem(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_EXTERNAL_SPDX_ELEMENT: return new org.spdx.library.model.compat.v2.ExternalSpdxElement(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_POINTER_START_END_POINTER: return new org.spdx.library.model.compat.v2.pointer.StartEndPointer(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_POINTER_BYTE_OFFSET_POINTER: return new org.spdx.library.model.compat.v2.pointer.ByteOffsetPointer(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_POINTER_LINE_CHAR_POINTER: return new org.spdx.library.model.compat.v2.pointer.LineCharPointer(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_CROSS_REF: return new org.spdx.library.model.compat.v2.license.CrossRef(modelStore, documentUri, id, copyManager, create);
		default: throw new InvalidSPDXAnalysisException("Unknown SPDX type: "+type);
		}
	}

	/**
	 * @param type SPDX Type
	 * @param specVersion Version of the SPDX Spec
	 * @return class associated with the type
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Class<? extends Object> typeToClass(String type, SpdxMajorVersion specVersion) throws InvalidSPDXAnalysisException {
		Class<?> retval;
		if (specVersion.compareTo(SpdxMajorVersion.VERSION_3) < 0) {
			retval = SPDX_TYPE_TO_CLASS_V2.get(type);
		} else {
			retval = SPDX_TYPE_TO_CLASS_V3.get(type);
		}
		if (Objects.isNull(retval)) {
			throw new InvalidSPDXAnalysisException("Unknown SPDX type: "+type);
		}
		return retval;
	}
	
	/**
	 * @param type SPDX Type
	 * @return class associated with the type for the latest spec version
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Class<? extends Object> typeToClass(String type) throws InvalidSPDXAnalysisException {
		return typeToClass(type, SpdxMajorVersion.latestVersion());
	}
	
	/**
	 * @param store model store
	 * @param nameSpace optional namespace to filter elements on
	 * @param copyManager optional copy manager
	 * @param spdxClass class to filter elements on
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Stream<?> getElements(IModelStore store, @Nullable String nameSpace, @Nullable ModelCopyManager copyManager, 
			Class<?> spdxClass) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(store, "Store must not be null");
		Objects.requireNonNull(spdxClass, "spdxClass must not be null");
		String type = SPDX_CLASS_TO_TYPE.get(spdxClass);
		if (Objects.isNull(type)) {
			throw new InvalidSPDXAnalysisException("Unknow SPDX class: "+spdxClass.toString());
		}
		return store.getAllItems(nameSpace, type).map(tv -> {
			try {
				return createModelObjectV2(store, nameSpace, tv.getObjectUri(), tv.getType(), copyManager);
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Error creating model object",e);
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * @param classUri URI for the class type
	 * @param specVersion Version of the SPDX Spec
	 * @return class represented by the URI
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Class<?> classUriToClass(String classUri, SpdxMajorVersion specVersion) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(classUri, "Missing required class URI");
		int indexOfPound = classUri.lastIndexOf('#');
		if (indexOfPound < 1) {
			throw new InvalidSPDXAnalysisException("Invalid class URI: "+classUri);
		}
		String type = classUri.substring(indexOfPound+1);
		return typeToClass(type, specVersion);
	}
	
	/**
	 * @param classUri URI for the class type
	 * @return class represented by the URI for the more recent spec version
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Class<?> classUriToClass(String classUri) throws InvalidSPDXAnalysisException {
		return classUriToClass(classUri, SpdxMajorVersion.latestVersion());
	}

	/**
	 * @param modelStore Store for the SPDX Spec version 2 model
	 * @param documentUri Document URI for for the ID
	 * @param copyManager Optional copy manager for copying any properties from other model
	 * @param objectUri ID for the model object
	 * @return SPDX Version 2 compatible ModelObject with the ID in the model store
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Optional<org.spdx.library.model.compat.v2.ModelObject> getModelObjectV2(IModelStore modelStore, String documentUri,
			String id, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore, "Model store can not be null");
		Objects.requireNonNull(documentUri, "A document URI or namespace must be supplied for all SPDX version 2 model objects");
		Objects.requireNonNull(id, "ID must not be null");
		if (id.contains(":")) {
			// External document ref
			try {
				return Optional.of(new org.spdx.library.model.compat.v2.ExternalSpdxElement(modelStore, documentUri, id, copyManager, true));
			} catch(InvalidSPDXAnalysisException ex) {
				logger.warn("Attempting to get a model object for an invalid SPDX ID.  Returning empty");
				return Optional.empty();
			}
		}
		Optional<TypedValue> tv = modelStore.getTypedValue(
				CompatibleModelStoreWrapper.documentUriIdToUri(documentUri, id, modelStore.getIdType(id).equals(IdType.Anonymous)));
		if (tv.isPresent()) {
			String type = tv.get().getType();
			try {
				return Optional.of(getModelObjectV2(modelStore, documentUri, id, type, copyManager, false));
			} catch(SpdxIdNotFoundException ex) {
				return Optional.empty();	// There is a window where the ID disappears between getTypedValue and getModelObject
			}
		} else {
			if (SpdxConstantsCompatV2.NOASSERTION_VALUE.equals(id)) {
				return Optional.of(new org.spdx.library.model.compat.v2.SpdxNoAssertionElement());
			} else if (SpdxConstantsCompatV2.NONE_VALUE.equals(id)) {
				return Optional.of(new org.spdx.library.model.compat.v2.SpdxNoneElement());
			} else {
				return Optional.empty();
			}
		}
	}
}
