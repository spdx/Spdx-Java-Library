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
package org.spdx.library.model.compat.v2.enumerations;

import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.model.compat.v2.IndividualUriValue;

/**
 * File Type is intrinsic to the file, independent of how the file is being used.  
 * A file may have more than one file type assigned to it.
 * 
 * @author Gary O'Neall
 *
 */
public enum FileType implements IndividualUriValue {
	APPLICATION("fileType_application"),
	ARCHIVE("fileType_archive"),
	AUDIO("fileType_audio"),
	BINARY("fileType_binary"),
	DOCUMENTATION("fileType_documentation"),
	IMAGE("fileType_image"),
	OTHER("fileType_other"),
	SOURCE("fileType_source"),
	SPDX("fileType_spdx"),
	TEXT("fileType_text"),
	VIDEO("fileType_video")
	;
	
	private String longName;
	
	private FileType(String longName) {
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
		return SpdxConstantsCompatV2.SPDX_NAMESPACE;
	}

}
