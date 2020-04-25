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

import java.util.HashMap;
import java.util.Map;


/**
 * Table of Contents for the listed license list as represented as a JSON index file
 * at spdx.org/licenses/licenses.json
 * 
 * @author Gary O'Neall
 *
 */
public class ExceptionJsonTOC {
	class ExceptionJson {
		private String reference;
		private boolean isDeprecatedLicenseId;
		private String detailsUrl;
		private int referenceNumber;
		private String name;
		private String licenseExceptionId;
		private String[] seeAlso;
		/**
		 * @return the licenseExceptionId
		 */
		public String getLicenseExceptionId() {
			return licenseExceptionId;
		}
		/**
		 * @return the reference
		 */
		public String getReference() {
			return reference;
		}
		/**
		 * @return the isDeprecatedLicenseId
		 */
		public boolean isDeprecatedLicenseId() {
			return isDeprecatedLicenseId;
		}
		/**
		 * @return the detailsUrl
		 */
		public String getDetailsUrl() {
			return detailsUrl;
		}
		/**
		 * @return the referenceNumber
		 */
		public int getReferenceNumber() {
			return referenceNumber;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the seeAlso
		 */
		public String[] getSeeAlso() {
			return seeAlso;
		}
	}
	

	private String licenseListVersion;
	private ExceptionJson[] exceptions;
	private String releaseDate;

	/**
	 * @return the licenseListVersion
	 */
	public String getLicenseListVersion() {
		return licenseListVersion;
	}

	/**
	 * @return the exceptions
	 */
	public ExceptionJson[] getExceptions() {
		return exceptions;
	}
	
	/**
	 * @return map of lower case to correct case exception IDs
	 */
	public Map<String, String> getExceptionIds() {
		Map<String, String> retval = new HashMap<>();
		if (exceptions == null) {
			return retval;
		}
		for (ExceptionJson licenseException:exceptions) {
			retval.put(licenseException.licenseExceptionId.toLowerCase(), licenseException.licenseExceptionId);
		}
		return retval;
	}

	/**
	 * @return the releaseDate
	 */
	public String getReleaseDate() {
		return releaseDate;
	}
}
