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

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.utility.DownloadCache;

/**
 * @author gary   Original code
 * @author pmonks Optional caching of downloaded files
 *
 */
public class SpdxListedLicenseWebStore extends SpdxListedLicenseModelStore {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxListedLicenseWebStore() throws InvalidSPDXAnalysisException {
		super();
	}

	private InputStream getUrlInputStream(final URL url) throws IOException {
		return DownloadCache.getInstance().getUrlInputStream(url);
	}

	@Override
	public InputStream getTocInputStream() throws IOException {
		return getUrlInputStream(new URL(SpdxConstants.LISTED_LICENSE_URL + LICENSE_TOC_FILENAME));
	}

	@Override
	public InputStream getLicenseInputStream(String licenseId) throws IOException {
		return getUrlInputStream(new URL(SpdxConstants.LISTED_LICENSE_URL + licenseId + JSON_SUFFIX));
	}

	@Override
	public InputStream getExceptionTocInputStream() throws IOException {
		return getUrlInputStream(new URL(SpdxConstants.LISTED_LICENSE_URL + EXCEPTION_TOC_FILENAME));
	}

	@Override
	public InputStream getExceptionInputStream(String exceptionId) throws IOException {
		return getLicenseInputStream(exceptionId);	// Same URL using exception ID rather than license ID
	}
	
	@Override
	public void close() throws Exception {
		// Nothing to do for the either the in-memory or the web store
	}
}
