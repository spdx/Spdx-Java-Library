/**
 * SPDX-FileCopyrightText: Copyright (c) 2019 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.storage.listedlicense;

import java.io.IOException;
import java.io.InputStream;

import org.spdx.core.InvalidSPDXAnalysisException;


/**
 * Model store for listed licenses using the JSON files in the resources/stdlicenses directory.
 * Note the resources/stdlicenses must be on the build path.
 *
 * @author Gary O'Neall
 */
public class SpdxListedLicenseLocalStore extends SpdxListedLicenseModelStore {
	
	static final String LISTED_LICENSE_JSON_LOCAL_DIR = "resources" + "/" + "stdlicenses";
	
	public SpdxListedLicenseLocalStore() throws InvalidSPDXAnalysisException {
		super();
	}

	@Override
	public InputStream getTocInputStream() throws IOException {
		String fileName = LISTED_LICENSE_JSON_LOCAL_DIR + "/" + LICENSE_TOC_FILENAME;
    	InputStream retval = SpdxListedLicenseLocalStore.class.getResourceAsStream("/" + fileName);
    	if (retval == null) {
    		throw new IOException("Unable to open local local license table of contents file");
    	}
    	return retval;
	}

	@Override
	public InputStream getLicenseInputStream(String licenseId) throws IOException {
		
		String fileName = LISTED_LICENSE_JSON_LOCAL_DIR + "/" + licenseId + JSON_SUFFIX;
    	InputStream retval = SpdxListedLicenseLocalStore.class.getResourceAsStream("/" + fileName);
    	if (retval == null) {
    		throw new IOException("Unable to open local local license JSON file for license ID "+licenseId);
    	}
    	return retval;
	}

	@Override
	public InputStream getExceptionTocInputStream() throws IOException {
		String fileName = LISTED_LICENSE_JSON_LOCAL_DIR + "/" + EXCEPTION_TOC_FILENAME;
    	InputStream retval = SpdxListedLicenseLocalStore.class.getResourceAsStream("/" + fileName);
    	if (retval == null) {
    		throw new IOException("Unable to open local local license table of contents file");
    	}
    	return retval;
	}

	@Override
	public InputStream getExceptionInputStream(String exceptionId) throws IOException {
		return getLicenseInputStream(exceptionId);
	}
}
