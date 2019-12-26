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
package org.spdx.library.model.enumerations;

import org.spdx.library.SpdxConstants;
import org.spdx.library.model.IndividualUriValue;

/**
 * Relationship types
 * 
 * @author Gary O'Neall
 *
 */
public enum RelationshipType implements IndividualUriValue {

	DESCRIBES("relationshipType_describes"),
	DESCRIBED_BY("relationshipType_describedBy"),
	ANCESTOR_OF("relationshipType_ancestorOf"),
	BUILD_TOOL_OF("relationshipType_buildToolOf"),
	CONTAINED_BY("relationshipType_containedBy"),
	CONTAINS("relationshipType_contains"),
	COPY_OF("relationshipType_copyOf"),
	DATA_FILE_OF("relationshipType_dataFile"),
	DESCENDANT_OF("relationshipType_descendantOf"),
	DISTRIBUTION_ARTIFACT("relationshipType_distributionArtifact"),
	DOCUMENTATION_OF("relationshipType_documentation"),
	DYNAMIC_LINK("relationshipType_dynamicLink"),
	EXPANDED_FROM_ARCHIVE("relationshipType_expandedFromArchive"),
	FILE_ADDED("relationshipType_fileAdded"),
	FILE_DELETED("relationshipType_fileDeleted"),
	FILE_MODIFIED("relationshipType_fileModified"),
	GENERATED_FROM("relationshipType_generatedFrom"),
	GENERATES("relationshipType_generates"),
	METAFILE_OF("relationshipType_metafileOf"),
	OPTIONAL_COMPONENT_OF("relationshipType_optionalComponentOf"),
	OTHER("relationshipType_other"),
	PACKAGE_OF("relationshipType_packageOf"),
	PATCH_APPLIED("relationshipType_patchApplied"),
	PATCH_FOR("relationshipType_patchFor"),
	AMENDS("relationshipType_amends"),
	STATIC_LINK("relationshipType_staticLink"),
	TEST_CASE_OF("relationshipType_testcaseOf"),
	PREREQUISITE_FOR("relationshipType_prerequisiteFor"),
	HAS_PREREQUISITE("relationshipType_hasPrerequisite");
	
	private String longName;
	
	private RelationshipType(String longName) {
		this.longName = longName;
	}
	@Override
	public String getIndividualURI() {
		return getNameSpace() + getLongName();
	}


	public String getLongName() {
		return longName;
	}

	public String getNameSpace() {
		return SpdxConstants.SPDX_NAMESPACE;
	}
}
