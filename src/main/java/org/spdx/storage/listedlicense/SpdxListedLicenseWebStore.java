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
package org.spdx.storage.listedlicense;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;

/**
 * @author gary
 *
 */
public class SpdxListedLicenseWebStore extends SpdxListedLicenseModelStore {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxListedLicenseWebStore() throws InvalidSPDXAnalysisException {
		super();
	}

	@Override
	InputStream getTocInputStream() throws IOException {
		URL tocUrl = new URL(SpdxConstants.LISTED_LICENSE_DOCUMENT_URI + LICENSE_TOC_FILENAME);
		return tocUrl.openStream();
	}

	@Override
	InputStream getLicenseInputStream(String licenseId) throws IOException {
		URL tocUrl = new URL(SpdxConstants.LISTED_LICENSE_DOCUMENT_URI + licenseId + JSON_SUFFIX);
		return tocUrl.openStream();
	}

	@Override
	InputStream getExceptionTocInputStream() throws IOException {
		URL tocUrl = new URL(SpdxConstants.LISTED_LICENSE_DOCUMENT_URI + EXCEPTION_TOC_FILENAME);
		return tocUrl.openStream();
	}

	@Override
	InputStream getExceptionInputStream(String exceptionId) throws IOException {
		return getLicenseInputStream(exceptionId);	// Same URL using exception ID rather than license ID
	}
}
