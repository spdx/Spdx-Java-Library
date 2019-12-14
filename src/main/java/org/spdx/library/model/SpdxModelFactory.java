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

	public static ModelObject createModelObject(IModelStore modelStore, String documentUri, String id,
			String type) throws InvalidSPDXAnalysisException {
		switch (type) {
		case SpdxConstants.CLASS_SPDX_DOCUMENT: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_PACKAGE: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_CREATION_INFO: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_CHECKSUM: throw new RuntimeException("Not implemented"); //TODO: Implement
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
		case SpdxConstants.CLASS_SPDX_FILE: throw new RuntimeException("Not implemented");  //TODO: Implement
		case SpdxConstants.CLASS_SPDX_REVIEW: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_VERIFICATIONCODE: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_ANNOTATION: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_RELATIONSHIP: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_ITEM: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_ELEMENT: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_EXTERNAL_DOC_REF: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_EXTERNAL_REFERENCE: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_REFERENCE_TYPE: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_SPDX_SNIPPET: throw new RuntimeException("Not implemented"); //TODO: Implement
		case SpdxConstants.CLASS_NOASSERTION_LICENSE: return new SpdxNoAssertionLicense(modelStore, documentUri);
		case SpdxConstants.CLASS_NONE_LICENSE: return new SpdxNoneLicense(modelStore, documentUri);
		case GenericModelObject.GENERIC_MODEL_OBJECT_TYPE: return new GenericModelObject(modelStore, documentUri, id, true);
		default: throw new InvalidSPDXAnalysisException("Unknown SPDX type: "+type);
		}
	}
}
