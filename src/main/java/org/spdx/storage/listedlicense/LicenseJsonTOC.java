/**
 * Copyright (c) 2018 Source Auditor Inc.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.SpdxListedLicense;



/**
 * Table of Contents for the listed license list as represented as a JSON index file
 * at spdx.org/licenses/licenses.json
 * 
 * @author Gary O'Neall
 *
 */
public class LicenseJsonTOC {
	
	@SuppressWarnings("unused")
    public static class LicenseJson {
		private String reference;
		private boolean isDeprecatedLicenseId;
		private String detailsUrl;
		private int referenceNumber;
		private String name;
		private String licenseId;
		private List<String> seeAlso;
		private boolean isOsiApproved;
		private Boolean isFsfLibre;
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
		 * @return the licenseId
		 */
		public String getLicenseId() {
			return licenseId;
		}
		/**
		 * @return the seeAlso
		 */
		public List<String> getSeeAlso() {
			return seeAlso;
		}
		/**
		 * @return the isOsiApproved
		 */
		public boolean isOsiApproved() {
			return isOsiApproved;
		}
		/**
		 * @return fsfLibre
		 */
		public @Nullable Boolean getFsfLibre() {
		    return this.isFsfLibre;
		}
		/**
		 * @param fsfLibre the fsfLibre flag to set
		 */
		public void setFsfLibre(@Nullable Boolean fsfLibre) {
		    this.isFsfLibre = fsfLibre;
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
		 * @param licenseId the licenseId to set
		 */
		public void setLicenseId(String licenseId) {
			this.licenseId = licenseId;
		}
		/**
		 * @param seeAlso the seeAlso to set
		 */
		public void setSeeAlso(List<String> seeAlso) {
			this.seeAlso = seeAlso;
		}
		/**
		 * @param isOsiApproved the isOsiApproved to set
		 */
		public void setOsiApproved(boolean isOsiApproved) {
			this.isOsiApproved = isOsiApproved;
		}
	}
	

	private String licenseListVersion;
	private final List<LicenseJson> licenses;
	private String releaseDate;

	public LicenseJsonTOC(String version, String releaseDate) {
		this.licenseListVersion = version;
		this.releaseDate = releaseDate;
		this.licenses = new ArrayList<>();
	}
	
	public LicenseJsonTOC() {
		this.licenseListVersion = null;
		this.releaseDate = null;
		this.licenses = new ArrayList<>();
	}

	/**
	 * @return the licenseListVersion
	 */
	public String getLicenseListVersion() {
		return licenseListVersion;
	}

	/**
	 * @return the licenses
	 */
	public List<LicenseJson> getLicenses() {
		return licenses;
	}
	
	/**
	 * @return map of lower case to correct case license IDs
	 */
	public Map<String, String> getLicenseIds() {
		Map<String, String> retval = new HashMap<>();
		for (LicenseJson license:licenses) {
			retval.put(license.licenseId.toLowerCase(), license.licenseId);
		}
		return retval;
	}

	/**
	 * @return the releaseDate
	 */
	public String getReleaseDate() {
		return releaseDate;
	}

	/**
	 * Add summary information about a specific license to the licenses list
	 * @param license license to add
	 * @param licHTMLReference HTML URL reference for the license
	 * @param licJSONReference URL reference to the JSON file
	 * @param deprecated if true, license ID is deprecated
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	public void addLicense(SpdxListedLicense license, String licHTMLReference, String licJSONReference,
			boolean deprecated) throws InvalidSPDXAnalysisException {
		LicenseJson lj = new LicenseJson();
		lj.setDeprecatedLicenseId(deprecated);
		lj.setDetailsUrl(toAbsoluteURL(licJSONReference));
		lj.setLicenseId(license.getId());
		lj.setName(license.getName());
		lj.setOsiApproved(license.isOsiApproved());
		lj.setFsfLibre(license.getFsfLibre());
		lj.setReference(toAbsoluteURL(licHTMLReference));
		int referenceNumber = -1;
		for (LicenseJson existing:this.licenses) {
			if (existing.getReferenceNumber() > referenceNumber) {
				referenceNumber = existing.getReferenceNumber();
			}
		}
		referenceNumber++;
		lj.setReferenceNumber(referenceNumber);
		lj.setSeeAlso(new ArrayList<>(license.getSeeAlso()));
		this.licenses.add(lj);
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

	protected static String toAbsoluteURL(String relURL) {
		String retval = relURL.startsWith("./") ? relURL.substring(2) : relURL;
		return SpdxConstantsCompatV2.LISTED_LICENSE_URL + retval;
	}
}
