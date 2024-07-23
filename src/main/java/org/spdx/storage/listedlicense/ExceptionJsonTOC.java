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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.license.ListedLicenseException;


/**
 * Table of Contents for the listed license list as represented as a JSON index file
 * at spdx.org/licenses/licenses.json
 * 
 * @author Gary O'Neall
 *
 */
public class ExceptionJsonTOC {
	static class ExceptionJson {
		private String reference;
		private boolean isDeprecatedLicenseId;
		private String detailsUrl;
		private int referenceNumber;
		private String name;
		private String licenseExceptionId;
		private List<String> seeAlso;
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
		public List<String> getSeeAlso() {
			return seeAlso;
		}
		/**
		 * @param reference the reference to set
		 */
		public void setReference(String reference) {
			this.reference = reference;
		}
		/**
		 * @param isDeprecatedLicenseId the isDeprecatedLicenseId to set
		 */
		public void setDeprecatedLicenseId(boolean isDeprecatedLicenseId) {
			this.isDeprecatedLicenseId = isDeprecatedLicenseId;
		}
		/**
		 * @param detailsUrl the detailsUrl to set
		 */
		public void setDetailsUrl(String detailsUrl) {
			this.detailsUrl = detailsUrl;
		}
		/**
		 * @param referenceNumber the referenceNumber to set
		 */
		public void setReferenceNumber(int referenceNumber) {
			this.referenceNumber = referenceNumber;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @param licenseExceptionId the licenseExceptionId to set
		 */
		public void setLicenseExceptionId(String licenseExceptionId) {
			this.licenseExceptionId = licenseExceptionId;
		}
		/**
		 * @param seeAlso the seeAlso to set
		 */
		public void setSeeAlso(List<String> seeAlso) {
			this.seeAlso = seeAlso;
		}
	}
	

	private String licenseListVersion;
	private List<ExceptionJson> exceptions;
	private String releaseDate;

	public ExceptionJsonTOC(String version, String releaseDate) {
		this.licenseListVersion = version;
		this.releaseDate = releaseDate;
		exceptions = new ArrayList<>();
	}

	public ExceptionJsonTOC() {
		licenseListVersion = null;
		exceptions = new ArrayList<>();
		releaseDate = null;
	}

	/**
	 * @return the licenseListVersion
	 */
	public @Nullable String getLicenseListVersion() {
		return licenseListVersion;
	}

	/**
	 * @return the exceptions
	 */
	public List<ExceptionJson> getExceptions() {
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
	public @Nullable String getReleaseDate() {
		return releaseDate;
	}

	/**
	 * Add a new exception to the list of exceptions
	 * @param exception
	 * @param exceptionHTMLReference
	 * @param exceptionJSONReference
	 * @param deprecated
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void addException(ListedLicenseException exception, String exceptionHTMLReference,
			String exceptionJSONReference, boolean deprecated) throws InvalidSPDXAnalysisException {
		ExceptionJson ej = new ExceptionJson();
		ej.setLicenseExceptionId(exception.getId());
		ej.setDeprecatedLicenseId(deprecated);
		ej.setDetailsUrl(LicenseJsonTOC.toAbsoluteURL(exceptionJSONReference));
		ej.setName(exception.getName());
		ej.setReference(LicenseJsonTOC.toAbsoluteURL(exceptionHTMLReference));
		int referenceNumber = 0;
		for (ExceptionJson existing:this.exceptions) {
			if (existing.getReferenceNumber() > referenceNumber) {
				referenceNumber = existing.getReferenceNumber();
			}
		}
		referenceNumber++;
		ej.setReferenceNumber(referenceNumber);
		List<String> seeAlso = new ArrayList<>();
		for (String sa:exception.getSeeAlso()) {
			seeAlso.add(sa);
		}
		ej.setSeeAlso(seeAlso);
		this.exceptions.add(ej);
	}

	/**
	 * @param licenseListVersion the licenseListVersion to set
	 */
	public void setLicenseListVersion(String licenseListVersion) {
		this.licenseListVersion = licenseListVersion;
	}

	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	
}
