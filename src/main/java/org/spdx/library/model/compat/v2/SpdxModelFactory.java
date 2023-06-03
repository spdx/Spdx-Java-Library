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
package org.spdx.library.model.compat.v2;

import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

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
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.Version;
import org.spdx.library.model.compat.v2.enumerations.AnnotationType;
import org.spdx.library.model.compat.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.compat.v2.enumerations.FileType;
import org.spdx.library.model.compat.v2.enumerations.Purpose;
import org.spdx.library.model.compat.v2.enumerations.ReferenceCategory;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.library.model.compat.v2.license.AnyLicenseInfo;
import org.spdx.library.model.compat.v2.license.ConjunctiveLicenseSet;
import org.spdx.library.model.compat.v2.license.CrossRef;
import org.spdx.library.model.compat.v2.license.DisjunctiveLicenseSet;
import org.spdx.library.model.compat.v2.license.ExternalExtractedLicenseInfo;
import org.spdx.library.model.compat.v2.license.ExtractedLicenseInfo;
import org.spdx.library.model.compat.v2.license.License;
import org.spdx.library.model.compat.v2.license.LicenseException;
import org.spdx.library.model.compat.v2.license.ListedLicenseException;
import org.spdx.library.model.compat.v2.license.ListedLicenses;
import org.spdx.library.model.compat.v2.license.OrLaterOperator;
import org.spdx.library.model.compat.v2.license.SimpleLicensingInfo;
import org.spdx.library.model.compat.v2.license.SpdxListedLicense;
import org.spdx.library.model.compat.v2.license.SpdxNoAssertionLicense;
import org.spdx.library.model.compat.v2.license.SpdxNoneLicense;
import org.spdx.library.model.compat.v2.license.WithExceptionOperator;
import org.spdx.library.model.compat.v2.pointer.ByteOffsetPointer;
import org.spdx.library.model.compat.v2.pointer.CompoundPointer;
import org.spdx.library.model.compat.v2.pointer.LineCharPointer;
import org.spdx.library.model.compat.v2.pointer.SinglePointer;
import org.spdx.library.model.compat.v2.pointer.StartEndPointer;

/**
 * Factory class to create ModelObjects based on the type
 * Types are defined classes in the SpdxConstantsCompatV2 class and map to the standard SPDX model
 * 
 * @author Gary O'Neall
 *
 */
public class SpdxModelFactory {
	
	static final Logger logger = LoggerFactory.getLogger(SpdxModelFactory.class);
	
	public static Map<String, Class<?>> SPDX_TYPE_TO_CLASS;
	public static Map<Class<?>, String> SPDX_CLASS_TO_TYPE;
	static {
		Map<String, Class<?>> typeToClass = new HashMap<>();
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_DOCUMENT, SpdxDocument.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_PACKAGE, SpdxPackage.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_CREATION_INFO, SpdxCreatorInformation.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_CHECKSUM, Checksum.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_ANY_LICENSE_INFO, AnyLicenseInfo.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_SIMPLE_LICENSE_INFO, SimpleLicensingInfo.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_CONJUNCTIVE_LICENSE_SET, ConjunctiveLicenseSet.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_DISJUNCTIVE_LICENSE_SET, DisjunctiveLicenseSet.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_EXTRACTED_LICENSING_INFO, ExtractedLicenseInfo.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_LICENSE, License.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, SpdxListedLicense.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_LICENSE_EXCEPTION, LicenseException.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, ListedLicenseException.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_OR_LATER_OPERATOR, OrLaterOperator.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_WITH_EXCEPTION_OPERATOR, WithExceptionOperator.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_FILE, SpdxFile.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_VERIFICATIONCODE, SpdxPackageVerificationCode.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_ANNOTATION, Annotation.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_RELATIONSHIP, Relationship.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_ITEM, SpdxItem.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_ELEMENT, SpdxElement.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_NONE_ELEMENT, SpdxNoneElement.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_NOASSERTION_ELEMENT, SpdxNoAssertionElement.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_EXTERNAL_DOC_REF, ExternalDocumentRef.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_EXTERNAL_REFERENCE, ExternalRef.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_REFERENCE_TYPE, ReferenceType.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SPDX_SNIPPET, SpdxSnippet.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_NOASSERTION_LICENSE, SpdxNoAssertionLicense.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_NONE_LICENSE, SpdxNoneLicense.class);
		typeToClass.put(GenericModelObject.GENERIC_MODEL_OBJECT_TYPE, GenericModelObject.class);
		typeToClass.put(GenericSpdxElement.GENERIC_SPDX_ELEMENT_TYPE, GenericSpdxElement.class);
		typeToClass.put(GenericSpdxItem.GENERIC_SPDX_ITEM_TYPE, GenericSpdxItem.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_EXTERNAL_SPDX_ELEMENT, ExternalSpdxElement.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_POINTER_START_END_POINTER, StartEndPointer.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_POINTER_BYTE_OFFSET_POINTER, ByteOffsetPointer.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_POINTER_LINE_CHAR_POINTER, LineCharPointer.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_POINTER_COMPOUNT_POINTER, CompoundPointer.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_SINGLE_POINTER, SinglePointer.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_CROSS_REF, CrossRef.class);
		typeToClass.put(SpdxConstantsCompatV2.ENUM_FILE_TYPE, FileType.class);
		typeToClass.put(SpdxConstantsCompatV2.ENUM_ANNOTATION_TYPE, AnnotationType.class);
		typeToClass.put(SpdxConstantsCompatV2.ENUM_CHECKSUM_ALGORITHM_TYPE, ChecksumAlgorithm.class);
		typeToClass.put(SpdxConstantsCompatV2.ENUM_REFERENCE_CATEGORY_TYPE, ReferenceCategory.class);
		typeToClass.put(SpdxConstantsCompatV2.ENUM_REFERENCE_RELATIONSHIP_TYPE, RelationshipType.class);
		typeToClass.put(SpdxConstantsCompatV2.CLASS_EXTERNAL_EXTRACTED_LICENSE, ExternalExtractedLicenseInfo.class);	
		typeToClass.put(SpdxConstantsCompatV2.ENUM_PURPOSE, Purpose.class);
		SPDX_TYPE_TO_CLASS = Collections.unmodifiableMap(typeToClass);
		
		Map<Class<?>, String> classToType = new HashMap<>();
		for (Entry<String, Class<?>> entry:typeToClass.entrySet()) {
			classToType.put(entry.getValue(), entry.getKey());
		}
		
		SPDX_CLASS_TO_TYPE = Collections.unmodifiableMap(classToType);
	}
	
	
	/**
	 * Create an SPDX document with default values for creator, created, licenseListVersion, data license and specVersion
	 * @param modelStore Where to store the SPDX Document
	 * @param documentUri unique URI for the SPDX document
	 * @param copyManager if non-null, allows for copying of properties from other model stores or document URI's when referenced
	 * @return
	 * @throws InvalidSPDXAnalysisException
	 */
	public static SpdxDocument createSpdxDocument(IModelStore modelStore, String documentUri, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		SpdxDocument retval = new SpdxDocument(modelStore, documentUri, copyManager, true);
		String date = new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT).format(new Date());
		SpdxCreatorInformation creationInfo = new SpdxCreatorInformation(
				modelStore, documentUri, modelStore.getNextId(IdType.Anonymous, documentUri), copyManager, true);
		creationInfo.getCreators().add("Tool: SPDX Tools");
		creationInfo.setCreated(date);
		creationInfo.setLicenseListVersion(ListedLicenses.getListedLicenses().getLicenseListVersion());
		retval.setCreationInfo(creationInfo);
		retval.setDataLicense(ListedLicenses.getListedLicenses().getListedLicenseById(SpdxConstantsCompatV2.SPDX_DATA_LICENSE_ID));
		retval.setSpecVersion(Version.CURRENT_SPDX_VERSION);
		return retval;
	}
	
	/**
	 * Create a model object in a model store given the document URI, ID and type
	 * @param modelStore model store where the object is to be created
	 * @param documentUri document URI for the stored item
	 * @param id ID for the item
	 * @param type SPDX class or type
	 *  @param copyManager if non-null, allows for copying of properties from other model stores or document URI's when referenced
	 * @return a ModelObject of type type
	 * @throws InvalidSPDXAnalysisException
	 */
	public static ModelObject createModelObject(IModelStore modelStore, String documentUri, String id,
			String type, ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		return getModelObject(modelStore, documentUri, id, type, copyManager, true);
	 }
	
	/**
	 * Create a model object in a model store given the document URI, ID and type
	 * @param modelStore model store where the object is to be created
	 * @param documentUri document URI for the stored item
	 * @param id ID for the item
	 * @param type SPDX class or type
	 * @param copyManager if non-null, allows for copying of properties from other model stores or document URI's when referenced
	 * @param create if true, create the model object if it does not already exist
	 * @return a ModelObject of type type
	 * @throws InvalidSPDXAnalysisException
	 */
	public static ModelObject getModelObject(IModelStore modelStore, String documentUri, String id,
			String type, ModelCopyManager copyManager, boolean create) throws InvalidSPDXAnalysisException {
		switch (type) {
		case SpdxConstantsCompatV2.CLASS_SPDX_DOCUMENT: return new SpdxDocument(modelStore, documentUri, copyManager, create); //Note: the ID is ignored
		case SpdxConstantsCompatV2.CLASS_SPDX_PACKAGE: return new SpdxPackage(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_CREATION_INFO: return new SpdxCreatorInformation(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_CHECKSUM: return new Checksum(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_ANY_LICENSE_INFO: throw new InvalidSPDXAnalysisException("Can not create abstract AnyLicensing Info.  Must specify one of the concrete classes");
		case SpdxConstantsCompatV2.CLASS_SPDX_SIMPLE_LICENSE_INFO:  throw new InvalidSPDXAnalysisException("Can not create abstract SimpleLicensingInfo.  Must specify one of the concrete classes");
		case SpdxConstantsCompatV2.CLASS_SPDX_CONJUNCTIVE_LICENSE_SET: return new ConjunctiveLicenseSet(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_DISJUNCTIVE_LICENSE_SET: return new DisjunctiveLicenseSet(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_EXTRACTED_LICENSING_INFO: return new ExtractedLicenseInfo(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_LICENSE: throw new InvalidSPDXAnalysisException("Can not create abstract License.  Must specify one of the concrete classes");
		case SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE: return new SpdxListedLicense(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_LICENSE_EXCEPTION: return new LicenseException(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION: return new ListedLicenseException(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_OR_LATER_OPERATOR: return new OrLaterOperator(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_WITH_EXCEPTION_OPERATOR: return new WithExceptionOperator(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_FILE: return new SpdxFile(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_REVIEW: throw new RuntimeException("SPDX Review class is no longer supported");
		case SpdxConstantsCompatV2.CLASS_SPDX_VERIFICATIONCODE: return new SpdxPackageVerificationCode(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_ANNOTATION: return new Annotation(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_RELATIONSHIP: return new Relationship(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_ITEM: throw new RuntimeException("SPDX item is an abstract item and can not be created.");
		case SpdxConstantsCompatV2.CLASS_SPDX_ELEMENT: throw new RuntimeException("SPDX element is an abstract item and can not be created.");
		case SpdxConstantsCompatV2.CLASS_SPDX_NONE_ELEMENT: return new SpdxNoneElement(modelStore, documentUri);
		case SpdxConstantsCompatV2.CLASS_SPDX_NOASSERTION_ELEMENT: return new SpdxNoAssertionElement(modelStore, documentUri);
		case SpdxConstantsCompatV2.CLASS_EXTERNAL_DOC_REF: return new ExternalDocumentRef(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_EXTERNAL_REFERENCE: return new ExternalRef(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_EXTERNAL_EXTRACTED_LICENSE: return new ExternalExtractedLicenseInfo(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_SPDX_REFERENCE_TYPE:  throw new RuntimeException("Reference type can only be created with a type supplied.");
		case SpdxConstantsCompatV2.CLASS_SPDX_SNIPPET: return new SpdxSnippet(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_NOASSERTION_LICENSE: return new SpdxNoAssertionLicense(modelStore, documentUri);
		case SpdxConstantsCompatV2.CLASS_NONE_LICENSE: return new SpdxNoneLicense(modelStore, documentUri);
		case GenericModelObject.GENERIC_MODEL_OBJECT_TYPE: return new GenericModelObject(modelStore, documentUri, id, copyManager, create);
		case GenericSpdxElement.GENERIC_SPDX_ELEMENT_TYPE: return new GenericSpdxElement(modelStore, documentUri, id, copyManager, create);
		case GenericSpdxItem.GENERIC_SPDX_ITEM_TYPE: return new GenericSpdxItem(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_EXTERNAL_SPDX_ELEMENT: return new ExternalSpdxElement(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_POINTER_START_END_POINTER: return new StartEndPointer(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_POINTER_BYTE_OFFSET_POINTER: return new ByteOffsetPointer(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_POINTER_LINE_CHAR_POINTER: return new LineCharPointer(modelStore, documentUri, id, copyManager, create);
		case SpdxConstantsCompatV2.CLASS_CROSS_REF: return new CrossRef(modelStore, documentUri, id, copyManager, create);
		default: throw new InvalidSPDXAnalysisException("Unknown SPDX type: "+type);
		}
	}

	/**
	 * @param type SPDX Type
	 * @return class associated with the type
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Class<? extends Object> typeToClass(String type) throws InvalidSPDXAnalysisException {
		Class<?> retval = SPDX_TYPE_TO_CLASS.get(type);
		if (Objects.isNull(retval)) {
			throw new InvalidSPDXAnalysisException("Unknown SPDX type: "+type);
		}
		return retval;
	}
	
	public static Stream<?> getElements(IModelStore store, String documentUri, ModelCopyManager copyManager, 
			Class<?> spdxClass) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(store, "Store must not be null");
		Objects.requireNonNull(documentUri, "documentUri must not be null");
		Objects.requireNonNull(spdxClass, "spdxClass must not be null");
		String type = SPDX_CLASS_TO_TYPE.get(spdxClass);
		if (Objects.isNull(type)) {
			throw new InvalidSPDXAnalysisException("Unknow SPDX class: "+spdxClass.toString());
		}
		return store.getAllItems(documentUri, type).map(tv -> {
			try {
				return createModelObject(store, documentUri, tv.getId(), tv.getType(), copyManager);
			} catch (InvalidSPDXAnalysisException e) {
				logger.error("Error creating model object",e);
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * @param classUri URI for the class type
	 * @return class represented by the URI
	 * @throws InvalidSPDXAnalysisException
	 */
	public static Class<?> classUriToClass(String classUri) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(classUri, "Missing required class URI");
		int indexOfPound = classUri.lastIndexOf('#');
		if (indexOfPound < 1) {
			throw new InvalidSPDXAnalysisException("Invalid class URI: "+classUri);
		}
		String type = classUri.substring(indexOfPound+1);
		return typeToClass(type);
	}

	/**
	 * @param modelStore Store for the model
	 * @param documentUri Document URI for for the ID
	 * @param copyManager Optional copy manager for copying any properties from other model
	 * @param id ID for the model object
	 * @return ModelObject with the ID in the model store
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Optional<ModelObject> getModelObject(IModelStore modelStore, String documentUri,
			String id, @Nullable ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
		if (id.contains(":")) {
			// External document ref
			try {
				return Optional.of(new ExternalSpdxElement(modelStore, documentUri, id, copyManager, true));
			} catch(InvalidSPDXAnalysisException ex) {
				logger.warn("Attempting to get a model object for an invalid SPDX ID.  Returning empty");
				return Optional.empty();
			}
		}
		Optional<TypedValue> tv = modelStore.getTypedValue(documentUri, id);
		if (tv.isPresent()) {
			String type = tv.get().getType();
			try {
				return Optional.of(getModelObject(modelStore, documentUri, id, type, copyManager, false));
			} catch(SpdxIdNotFoundException ex) {
				return Optional.empty();	// There is a window where the ID disappears between getTypedValue and getModelObject
			}
		} else {
			if (SpdxConstantsCompatV2.NOASSERTION_VALUE.equals(id)) {
				return Optional.of(new SpdxNoAssertionElement());
			} else if (SpdxConstantsCompatV2.NONE_VALUE.equals(id)) {
				return Optional.of(new SpdxNoneElement());
			} else {
				return Optional.empty();
			}
		}
	}
}
