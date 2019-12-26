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
package org.spdx.library.model;

import org.spdx.storage.IModelStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.license.ConjunctiveLicenseSet;
import org.spdx.library.model.license.DisjunctiveLicenseSet;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.LicenseException;
import org.spdx.library.model.license.OrLaterOperator;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.library.model.license.SpdxNoneLicense;
import org.spdx.library.model.license.WithExceptionOperator;

/**
 * Factory class to create ModelObjects based on the type
 * Types are defined classes in the SpdxConstants class and map to the standard SPDX model
 * 
 * @author Gary O'Neall
 *
 */
public class SpdxModelFactory {
	
	/**
	 * Create a model object in a model store given the document URI, ID and type
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param type
	 * @return a ModelObject of type type
	 * @throws InvalidSPDXAnalysisException
	 */
	public static ModelObject createModelObject(IModelStore modelStore, String documentUri, String id,
			String type) throws InvalidSPDXAnalysisException {
		switch (type) {
		case SpdxConstants.CLASS_SPDX_DOCUMENT: return new SpdxDocument(modelStore, documentUri, true); //Note: the ID is ignored
		case SpdxConstants.CLASS_SPDX_PACKAGE: return new SpdxPackage(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_CREATION_INFO: return new SpdxCreatorInformation(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_CHECKSUM: return new Checksum(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_ANY_LICENSE_INFO: throw new InvalidSPDXAnalysisException("Can not create abstract AnyLicensing Info.  Must specify one of the concrete classes");
		case SpdxConstants.CLASS_SPDX_SIMPLE_LICENSE_INFO:  throw new InvalidSPDXAnalysisException("Can not create abstract SimpleLicensingInfo.  Must specify one of the concrete classes");
		case SpdxConstants.CLASS_SPDX_CONJUNCTIVE_LICENSE_SET: return new ConjunctiveLicenseSet(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_DISJUNCTIVE_LICENSE_SET: return new DisjunctiveLicenseSet(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO: return new ExtractedLicenseInfo(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_LICENSE: throw new InvalidSPDXAnalysisException("Can not create abstract License.  Must specify one of the concrete classes");
		case SpdxConstants.CLASS_SPDX_LISTED_LICENSE: return new SpdxListedLicense(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION: return new LicenseException(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_OR_LATER_OPERATOR: return new OrLaterOperator(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_WITH_EXCEPTION_OPERATOR: return new WithExceptionOperator(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_FILE: return new SpdxFile(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_REVIEW: throw new RuntimeException("SPDX Review class is no longer supported");
		case SpdxConstants.CLASS_SPDX_VERIFICATIONCODE: return new SpdxPackageVerificationCode(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_ANNOTATION: return new Annotation(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_RELATIONSHIP: return new Relationship(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_ITEM: throw new RuntimeException("SPDX item is an abstract item and can not be created.");
		case SpdxConstants.CLASS_SPDX_ELEMENT: throw new RuntimeException("SPDX element is an abstract item and can not be created.");
		case SpdxConstants.CLASS_EXTERNAL_DOC_REF: return new ExternalDocumentRef(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_EXTERNAL_REFERENCE: return new ExternalRef(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_SPDX_REFERENCE_TYPE:  throw new RuntimeException("Reference type can only be created with a type supplied.");
		case SpdxConstants.CLASS_SPDX_SNIPPET: return new SpdxSnippet(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_NOASSERTION_LICENSE: return new SpdxNoAssertionLicense(modelStore, documentUri);
		case SpdxConstants.CLASS_NONE_LICENSE: return new SpdxNoneLicense(modelStore, documentUri);
		case GenericModelObject.GENERIC_MODEL_OBJECT_TYPE: return new GenericModelObject(modelStore, documentUri, id, true);
		case GenericSpdxElement.GENERIC_SPDX_ELEMENT_TYPE: return new GenericSpdxElement(modelStore, documentUri, id, true);
		case SpdxConstants.CLASS_EXTERNAL_SPDX_ELEMENT: return new ExternalSpdxElement(modelStore, documentUri, id, true);
		default: throw new InvalidSPDXAnalysisException("Unknown SPDX type: "+type);
		}
	}

	/**
	 * @param type SPDX Type
	 * @return class associated with the type
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Class<? extends Object> typeToClass(String type) throws InvalidSPDXAnalysisException {
		switch (type) {
		case SpdxConstants.CLASS_SPDX_DOCUMENT: return SpdxDocument.class;
		case SpdxConstants.CLASS_SPDX_PACKAGE: return SpdxPackage.class;
		case SpdxConstants.CLASS_SPDX_CREATION_INFO: return SpdxCreatorInformation.class;
		case SpdxConstants.CLASS_SPDX_CHECKSUM: return Checksum.class;
		case SpdxConstants.CLASS_SPDX_ANY_LICENSE_INFO: throw new InvalidSPDXAnalysisException("Can not create abstract AnyLicensing Info.  Must specify one of the concrete classes");
		case SpdxConstants.CLASS_SPDX_SIMPLE_LICENSE_INFO:  throw new InvalidSPDXAnalysisException("Can not create abstract SimpleLicensingInfo.  Must specify one of the concrete classes");
		case SpdxConstants.CLASS_SPDX_CONJUNCTIVE_LICENSE_SET: return ConjunctiveLicenseSet.class;
		case SpdxConstants.CLASS_SPDX_DISJUNCTIVE_LICENSE_SET: return DisjunctiveLicenseSet.class;
		case SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO: return ExtractedLicenseInfo.class;
		case SpdxConstants.CLASS_SPDX_LICENSE: throw new InvalidSPDXAnalysisException("Can not create abstract License.  Must specify one of the concrete classes");
		case SpdxConstants.CLASS_SPDX_LISTED_LICENSE: return SpdxListedLicense.class;
		case SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION: return LicenseException.class;
		case SpdxConstants.CLASS_OR_LATER_OPERATOR: return OrLaterOperator.class;
		case SpdxConstants.CLASS_WITH_EXCEPTION_OPERATOR: return WithExceptionOperator.class;
		case SpdxConstants.CLASS_SPDX_FILE: return SpdxFile.class;
		case SpdxConstants.CLASS_SPDX_REVIEW: throw new RuntimeException("SPDX Review class is no longer supported");
		case SpdxConstants.CLASS_SPDX_VERIFICATIONCODE: return SpdxPackageVerificationCode.class;
		case SpdxConstants.CLASS_ANNOTATION: return Annotation.class;
		case SpdxConstants.CLASS_RELATIONSHIP: return Relationship.class;
		case SpdxConstants.CLASS_SPDX_ITEM: throw new RuntimeException("SPDX item is an abstract item and can not be created.");
		case SpdxConstants.CLASS_SPDX_ELEMENT: throw new RuntimeException("SPDX element is an abstract item and can not be created.");
		case SpdxConstants.CLASS_EXTERNAL_DOC_REF: return ExternalDocumentRef.class;
		case SpdxConstants.CLASS_SPDX_EXTERNAL_REFERENCE: return ExternalRef.class;
		case SpdxConstants.CLASS_SPDX_REFERENCE_TYPE:  throw new RuntimeException("Reference type can only be created with a type supplied.");
		case SpdxConstants.CLASS_SPDX_SNIPPET: return SpdxSnippet.class;
		case SpdxConstants.CLASS_NOASSERTION_LICENSE: return SpdxNoAssertionLicense.class;
		case SpdxConstants.CLASS_NONE_LICENSE: return SpdxNoneLicense.class;
		case GenericModelObject.GENERIC_MODEL_OBJECT_TYPE: return GenericModelObject.class;
		case GenericSpdxElement.GENERIC_SPDX_ELEMENT_TYPE: return GenericSpdxElement.class;
		case SpdxConstants.CLASS_EXTERNAL_SPDX_ELEMENT: return ExternalSpdxElement.class;
		default: throw new InvalidSPDXAnalysisException("Unknown SPDX type: "+type);
		}
	}
}
