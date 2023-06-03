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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstantsCompatV2;

/**
 * @author gary
 *
 */
public class SpdxListedLicenseWebStore extends SpdxListedLicenseModelStore {

	private static final int READ_TIMEOUT = 5000;
	
	static final List<String> WHITE_LIST = Collections.unmodifiableList(Arrays.asList(
			"spdx.org", "spdx.dev", "spdx.com", "spdx.info")); // Allowed host names for the SPDX listed licenses

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxListedLicenseWebStore() throws InvalidSPDXAnalysisException {
		super();
	}
	
	private InputStream getUrlInputStream(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setReadTimeout(READ_TIMEOUT);
		int status = connection.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK && 
			(status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)) {
				// redirect
			String redirectUrlStr = connection.getHeaderField("Location");
			if (Objects.isNull(redirectUrlStr) || redirectUrlStr.isEmpty()) {
				throw new IOException("Empty redirect URL response");
			}
			URL redirectUrl;
			try {
				redirectUrl = new URL(redirectUrlStr);
			} catch(Exception ex) {
				throw new IOException("Invalid redirect URL");
			}
			if (!redirectUrl.getProtocol().toLowerCase().startsWith("http")) {
				throw new IOException("Invalid redirect protocol");
			}
			if (!WHITE_LIST.contains(redirectUrl.getHost())) {
				throw new IOException("Invalid redirect host - not on the allowed 'white list'");
			}
			connection = (HttpURLConnection)redirectUrl.openConnection();
		}
		return connection.getInputStream();
	}

	@Override
	InputStream getTocInputStream() throws IOException {
		return getUrlInputStream(new URL(SpdxConstantsCompatV2.LISTED_LICENSE_URL + LICENSE_TOC_FILENAME));
	}

	@Override
	InputStream getLicenseInputStream(String licenseId) throws IOException {
		return getUrlInputStream(new URL(SpdxConstantsCompatV2.LISTED_LICENSE_URL + licenseId + JSON_SUFFIX));
	}

	@Override
	InputStream getExceptionTocInputStream() throws IOException {
		return getUrlInputStream(new URL(SpdxConstantsCompatV2.LISTED_LICENSE_URL + EXCEPTION_TOC_FILENAME));
	}

	@Override
	InputStream getExceptionInputStream(String exceptionId) throws IOException {
		return getLicenseInputStream(exceptionId);	// Same URL using exception ID rather than license ID
	}
	
	@Override
	public void close() throws Exception {
		// Nothing to do for the either the in-memory or the web store
	}
}
