/**
 * Copyright (c) 2015, 2019 Source Auditor Inc.
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
package org.spdx.library.model;

import java.util.List;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.storage.IModelStore;


/**
 * The SimpleLicenseInfo class includes all resources that represent 
 * simple, atomic, licensing information.
 * 
 * @author Gary O'Neall
 *
 */
public abstract class SimpleLicensingInfo extends AnyLicenseInfo {

	/**
	 * Create a new SimpleLicensingInfo object
	 * @param modelStore container which includes the license
	 * @param documentUri URI for the SPDX document containing the license
	 * @param id identifier for the license
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	SimpleLicensingInfo(IModelStore modelStore, String documentUri, String id, boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}
	
	/**
	 * @return the id
	 */
	public String getLicenseId() {
		return this.getId();
	}

	/**
	 * @return the name
	 * @throws SpdxInvalidTypeException 
	 */
	public String getName() throws SpdxInvalidTypeException {
		return getStringPropertyValue(PROP_STD_LICENSE_NAME);
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		setPropertyValue(PROP_STD_LICENSE_NAME, name);
	}
	/**
	 * @return the comments
	 * @throws SpdxInvalidTypeException 
	 */
	public String getComment() throws SpdxInvalidTypeException {
		return getStringPropertyValue(RDFS_PROP_COMMENT);
	}
	
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		setPropertyValue(RDFS_PROP_COMMENT, comment);
	}
	
	/**
	 * @return the urls which reference the same license information
	 * @throws SpdxInvalidTypeException 
	 */
	public List<String> getSeeAlso() throws SpdxInvalidTypeException {
		return getStringPropertyValueList(RDFS_PROP_SEE_ALSO);
	}
	/**
	 * @param seeAlsoUrl the urls which are references to the same license to set
	 */
	public void setSeeAlso(List<String> seeAlsoUrl) {
		if (seeAlsoUrl == null) {
			clearPropertyValueList(RDFS_PROP_SEE_ALSO);
		} else {
			replacePropertyValueList(RDFS_PROP_SEE_ALSO, seeAlsoUrl);
		}
	}
}
