/**
 * Copyright (c) 2022 Source Auditor Inc.
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
package org.spdx.library.model.compat.v2.license;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;

/**
 * ExtractedLicenseInfo which can be represented as a concrete ExtractedLicenseInfo within
 * the same SPDX document with all properties accessible, or as an ExternalExtractedLicenseInfo
 * which represents a license not included within the SPDX document.
 * 
 * @author Gary O'Neall
 *
 */
public abstract class AbstractExtractedLicenseInfo extends SimpleLicensingInfo
implements Comparable<AbstractExtractedLicenseInfo> {

	/**
	 * Create a new ExtractedLicenseInfo using the ID
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public AbstractExtractedLicenseInfo(String id)
			throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * Create a new ExtractedLicenseInfo object
	 * @param modelStore container which includes the license
	 * @param documentUri URI for the SPDX document containing the license
	 * @param id identifier for the license
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	public AbstractExtractedLicenseInfo(IModelStore modelStore,
			String documentUri, String id, ModelCopyManager copyManager,
			boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
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
	public abstract String getExtractedText() throws InvalidSPDXAnalysisException;
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AbstractExtractedLicenseInfo o) {
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
}
