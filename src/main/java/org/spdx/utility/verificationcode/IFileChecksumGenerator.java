/**
 * Copyright (c) 2011 Source Auditor Inc.
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
 *
*/
package org.spdx.utility.verificationcode;

import java.io.File;
import java.io.IOException;

/**
 * Interface for implementations of generators of file checksums
 * @author Gary O'Neall
 *
 */
public interface IFileChecksumGenerator {
	/**
	 * @param file File to generate the checksum for
	 * @return the checksum for the file
	 * @throws IOException on errors reading the file
	 */
	public String getFileChecksum(File file) throws IOException;
}
