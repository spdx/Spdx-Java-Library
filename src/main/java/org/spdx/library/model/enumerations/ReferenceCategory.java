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

import org.spdx.library.IndividualUriValue;
import org.spdx.library.SpdxConstantsCompatV2;

/**
 * Reference category for external refs
 * 
 * @author Gary O'Neall
 *
 */
public enum ReferenceCategory implements IndividualUriValue {
	PACKAGE_MANAGER("referenceCategory_packageManager"),
	SECURITY("referenceCategory_security"),
	OTHER("referenceCategory_other"),
	PERSISTENT_ID("referenceCategory_persistentId"),
	MISSING("invalid_missing")
	;

private String longName;
	
	private ReferenceCategory(String longName) {
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
