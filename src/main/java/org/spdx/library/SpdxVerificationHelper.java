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
package org.spdx.library;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import org.spdx.library.model.ChecksumAlgorithm;

/**
 * Holds static methods used for verify various property values
 * @author Gary O'Neall
 *
 */
public class SpdxVerificationHelper {
	
	static final String[] VALID_CREATOR_PREFIXES = new String[] {SpdxConstants.CREATOR_PREFIX_PERSON,
		SpdxConstants.CREATOR_PREFIX_ORGANIZATION, SpdxConstants.CREATOR_PREFIX_TOOL};
	static final String[] VALID_ORIGINATOR_SUPPLIER_PREFIXES = new String[] {SpdxConstants.NOASSERTION_VALUE, "Person:", "Organization:"};
	
	public static String verifyNonStdLicenseid(String licenseId) {
		if (SpdxConstants.LICENSE_ID_PATTERN.matcher(licenseId).matches()) {
			return null;
		} else {
			return "Invalid license id '"+licenseId+"'.  Must start with 'LicenseRef-' " +
					"and made up of the characters from the set 'a'-'z', 'A'-'Z', '0'-'9', '+', '_', '.', and '-'.";
		}
	}
	
	/**
	 * Verifies a creator string value
	 * @param creator
	 * @return
	 */
	public static String verifyCreator(String creator) {
		boolean ok = false;
		for (int i = 0; i < VALID_CREATOR_PREFIXES.length; i++) {
			if (creator.startsWith(VALID_CREATOR_PREFIXES[i])) {
				ok = true;
				break;
			}
		}
		if (!ok) {
			StringBuilder sb = new StringBuilder("Creator does not start with one of ");
			sb.append(VALID_CREATOR_PREFIXES[0]);
			for (int i = 1; i < VALID_CREATOR_PREFIXES.length; i++) {
				sb.append(", ");
				sb.append(VALID_CREATOR_PREFIXES[i]);
			}
			return sb.toString();
		} else {
			return null;
		}
	}
	
	/**
	 * Verifies the originator string
	 * @param originator
	 * @return
	 */
	public static String verifyOriginator(String originator) {
		return verifyOriginatorOrSupplier(originator);
	}
	
	/**
	 * Verifies the supplier String
	 * @param supplier
	 * @return
	 */
	public static String verifySupplier(String supplier) {
		return verifyOriginatorOrSupplier(supplier);
	}

	/**
	 * Verifies a the originator or supplier
	 * @param creator
	 * @return
	 */
	private static String verifyOriginatorOrSupplier(String originatorOrSupplier) {
		boolean ok = false;
		for (int i = 0; i < VALID_ORIGINATOR_SUPPLIER_PREFIXES.length; i++) {
			if (originatorOrSupplier.startsWith(VALID_ORIGINATOR_SUPPLIER_PREFIXES[i])) {
				ok = true;
				break;
			}
		}
		if (!ok) {
			StringBuilder sb = new StringBuilder("Value must start with one of ");
			sb.append(VALID_ORIGINATOR_SUPPLIER_PREFIXES[0]);
			for (int i = 1; i < VALID_ORIGINATOR_SUPPLIER_PREFIXES.length; i++) {
				sb.append(", ");
				sb.append(VALID_ORIGINATOR_SUPPLIER_PREFIXES[i]);
			}
			return sb.toString();
		} else {
			return null;
		}
	}
	
	/**
	 * @param creationDate
	 * @return
	 */
	public static String verifyDate(String creationDate) {
		SimpleDateFormat format = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		try {
			format.parse(creationDate);
		} catch (ParseException e) {
			return("Invalid date format: "+e.getMessage());
		}
		return null;
	}

	/**
	 * @param reviewer
	 * @return
	 */
	public static String verifyReviewer(String reviewer) {
		if (!reviewer.startsWith("Person:") && !reviewer.startsWith("Tool:") &&
				!reviewer.startsWith("Organization:")) {
			return "Reviewer does not start with Person:, Organization:, or Tool:";
		} else {
			return null;
		}
	}
	
	/**
	 * Returns true if s1 equals s2 taking into account the possibility of null values
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean equalsWithNull(Object s1, Object s2) {
		if (s1 == null) {
			return (s2 == null);
		}
		if (s2 == null) {
			return false;
		}
		return s1.equals(s2);
	}
	
	/**
	 * Returns true if the array s1 contains the same objects as s2 independent of order
	 * and allowing for null values
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean equivalentArray(Object[] s1, Object[] s2) {
		if (s1 == null) {
			return (s2 == null);
		}
		if (s2 == null) {
			return false;
		}
		if (s1.length != s2.length) {
			return false;
		}
		for (int i = 0; i < s1.length; i++) {
			boolean found = false;
			for (int j = 0; j < s2.length; j++) {
				if (equalsWithNull(s1[i], s2[j])) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param annotator
	 * @return
	 */
	public static String verifyAnnotator(String annotator) {
		if (!annotator.startsWith("Person:") && !annotator.startsWith("Tool:") &&
				!annotator.startsWith("Organization:")) {
			return "Annotator does not start with Person:, Organization:, or Tool";
		} else {
			return null;
		}
	}

	/**
	 * @param externalDocumentId
	 * @return
	 */
	public static boolean isValidExternalDocRef(String externalDocumentId) {
		return SpdxConstants.EXTERNAL_DOC_REF_PATTERN.matcher(externalDocumentId).matches();
	}

	public static boolean isValidUri(String uri) {
		try {
			URI.create(uri);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String verifyChecksumString(String checksum, ChecksumAlgorithm algorithm) {		
		for (int i = 0; i < checksum.length(); i++) {
			char c = checksum.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
				return "Invalid checksum string character at position "+String.valueOf(i);
			}
		}
		
		if (ChecksumAlgorithm.SHA1.equals(algorithm) && checksum.length() != 40) {
			return "Invalid number of characters for checksum";
		}
	
		if (ChecksumAlgorithm.SHA256.equals(algorithm) && checksum.length() != 64) {
			return "Invalid number of characters for checksum";
		}
		if (ChecksumAlgorithm.MD5.equals(algorithm) && checksum.length() != 32) {
			return "Invalid number of characters for checksum";
		}
		return null;	// if we got here, all OK
	}

	/**
	 * Verify a download location per section 3.7.5 of the spec
	 * @param downloadLocation
	 * @return null if a valid string otherwise a description of the error
	 */
	public static String verifyDownloadLocation(String downloadLocation) {
		if (Objects.isNull(downloadLocation)) {
			return "Download location is null";
		} else if (SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(downloadLocation).matches()) {
			return null;
		} else {
			return "Invalid download location pattern "+downloadLocation+".  Must match the pattern "+
					SpdxConstants.DOWNLOAD_LOCATION_PATTERN.pattern();
		}
	}
}
