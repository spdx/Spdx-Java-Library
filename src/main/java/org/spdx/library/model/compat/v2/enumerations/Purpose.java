/**
 * Copyright (c) 2022 Source Auditor Inc.
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

import org.spdx.library.IndividualUriValue;
import org.spdx.library.SpdxConstantsCompatV2;

/**
 * Package Purpose is intrinsic to how the package is being used rather than the content of the package.
 * 
 * @author Gary O'Neall
 *
 */
public enum Purpose implements IndividualUriValue {
	
	APPLICATION("purpose_application"),
	FRAMEWORK("purpose_framework"), 
	LIBRARY("purpose_library"), 
	OPERATING_SYSTEM("purpose_operatingSystem"),
	DEVICE("purpose_device"),
	FIRMWARE("purpose_firmware"),
	SOURCE("purpose_source"),
	FILE("purpose_file"),
	INSTALL("purpose_install"),
	ARCHIVE("purpose_archive"),
	CONTAINER("purpose_container"),
	OTHER("purpose_other");
	
private String longName;
	
	private Purpose(String longName) {
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
