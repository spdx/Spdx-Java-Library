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

import org.spdx.library.SpdxConstants;

/**
 * Enum constants for Checksum Algorithms
 * 
 * @author Gary O'Neall
 *
 */
public enum ChecksumAlgorithm implements IndividualValue {
	SHA1("checksumAlgorithm_sha1"),
	MD5("checksumAlgorithm_md5"),
	SHA256("checksumAlgorithm_sha256")
	;

	private String longName;
	
	private ChecksumAlgorithm(String longName) {
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
