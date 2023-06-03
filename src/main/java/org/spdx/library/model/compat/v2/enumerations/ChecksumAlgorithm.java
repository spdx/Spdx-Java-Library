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
 * Enum constants for Checksum Algorithms
 * 
 * @author Gary O'Neall
 *
 */
public enum ChecksumAlgorithm implements IndividualUriValue {
	SHA1("checksumAlgorithm_sha1"),
	MD5("checksumAlgorithm_md5"),
	SHA256("checksumAlgorithm_sha256"), 
	MISSING("InvalidMissingChecksum"),
	SHA224("checksumAlgorithm_sha224"),
	SHA384("checksumAlgorithm_sha384"),
	SHA512("checksumAlgorithm_sha512"),
	MD2("checksumAlgorithm_md2"),
	MD4("checksumAlgorithm_md4"),
	MD6("checksumAlgorithm_md6"),
	SHA3_256("checksumAlgorithm_sha3_256"),
	SHA3_384("checksumAlgorithm_sha3_384"),
	SHA3_512("checksumAlgorithm_sha3_512"),
	BLAKE2b_256("checksumAlgorithm_blake2b256"),
	BLAKE2b_384("checksumAlgorithm_blake2b384"),
	BLAKE2b_512("checksumAlgorithm_blake2b512"),
	BLAKE3("checksumAlgorithm_blake3"),
	ADLER32("checksumAlgorithm_adler32"),
	;

	private final String longName;
	
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
		return SpdxConstantsCompatV2.SPDX_NAMESPACE;
	}

}
