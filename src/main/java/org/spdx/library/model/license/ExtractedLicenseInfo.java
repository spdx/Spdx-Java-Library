/**
 * Copyright (c) 2011, 2019 Source Auditor Inc.
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
 *
*/
package org.spdx.library.model.license;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.utility.compare.LicenseCompareHelper;

/**
 * An ExtractedLicensingInfo represents a license or licensing notice that was found in the package. 
 * Any license text that is recognized as a license may be represented as a License 
 * rather than an ExtractedLicensingInfo.
 * @author Gary O'Neall
 *
 */
public class ExtractedLicenseInfo extends SimpleLicensingInfo implements Comparable<ExtractedLicenseInfo> {
	
	public ExtractedLicenseInfo() throws InvalidSPDXAnalysisException {
		super(DefaultModelStore.getDefaultModelStore().getNextId(IdType.LicenseRef, DefaultModelStore.getDefaultDocumentUri()));
	}
	
	public ExtractedLicenseInfo(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * Create a new SimpleLicensingInfo object
	 * @param modelStore container which includes the license
	 * @param documentUri URI for the SPDX document containing the license
	 * @param id identifier for the license
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ExtractedLicenseInfo(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}
	
	/**
	 * Create a new ExtractedLicenseInfo using the ID and text
	 * @param id
	 * @param text
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ExtractedLicenseInfo(String id, String text) throws InvalidSPDXAnalysisException {
		super(id);
		this.setExtractedText(text);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO;
	}


	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#toString()
	 */
	@Override
	public String toString() {
		// must be only the ID if we are to use this to create 
		// parseable license strings
		return this.getLicenseId();
	}
	
	/**
	 * @return the text
	 * @throws SpdxInvalidTypeException 
	 */
	public String getExtractedText() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(SpdxConstants.PROP_EXTRACTED_TEXT);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}

	/**
	 * @param text the text to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setExtractedText(String text) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_EXTRACTED_TEXT, text);
	}

	/**
	 * @return
	 */
	@Override
    public List<String> verify() {
		List<String> retval = new ArrayList<>();
		String id = this.getLicenseId();
		if (id == null || id.isEmpty()) {
			retval.add("Missing required license ID");
		} else {
			String idError = SpdxVerificationHelper.verifyNonStdLicenseid(id);
			if (idError != null && !idError.isEmpty()) {
				retval.add(idError);
			}
		}
		try {
		String licenseText = this.getExtractedText();
			if (licenseText == null || licenseText.isEmpty()) {
				retval.add("Missing required license text for " + id);
			}
		} catch (InvalidSPDXAnalysisException ex) {
			retval.add("Unable to fetch license text: "+ex.getMessage());
		}

		return retval;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ExtractedLicenseInfo o) {
		try {
			if (this.getLicenseId() == null) {
				if (o.getLicenseId() == null) {
					if (this.getExtractedText() == null) {
						if (o.getExtractedText() == null) {
							return 0;
						} else {
							return 1;
						}
					}else if (o.getExtractedText() == null) {
						return -1;
					} else {
						return this.getExtractedText().compareToIgnoreCase(o.getExtractedText());
					}
				} else {
					return 1;
				}
			} else {
				if (o.getLicenseId() == null) {
					return -1;
				} else {
					return this.getLicenseId().compareToIgnoreCase(o.getLicenseId());
				}
			}
		} catch (InvalidSPDXAnalysisException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public boolean equivalent(ModelObject compare) throws InvalidSPDXAnalysisException {
		if (compare instanceof ExtractedLicenseInfo) {
			// Only test for the text - other fields do not need to equal to be considered equivalent
			return LicenseCompareHelper.isLicenseTextEquivalent(this.getExtractedText(), ((ExtractedLicenseInfo)compare).getExtractedText());
		} else {
			return super.equivalent(compare);
		}
		
	}
}
