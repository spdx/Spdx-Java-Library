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
package org.spdx.library.model.license;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.IndividualUriValue;
import org.spdx.library.model.SpdxInvalidTypeException;
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
	 * Open or create a model object with the default store and default document URI
	 * @param id ID for this object - must be unique within the SPDX document
	 * @throws InvalidSPDXAnalysisException 
	 */
	SimpleLicensingInfo(String id) throws InvalidSPDXAnalysisException {
		super(id);
        if (!(this instanceof IndividualUriValue)) {
            setPropertyValue(SpdxConstants.PROP_LICENSE_ID, id);  // Needs to be set as a property per spec
        }
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
	SimpleLicensingInfo(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
		if (!(this instanceof IndividualUriValue)) {
		    setPropertyValue(SpdxConstants.PROP_LICENSE_ID, id);  // Needs to be set as a property per spec
		}
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
	public String getName() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(SpdxConstants.PROP_STD_LICENSE_NAME);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * @param name the name to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setName(String name) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_STD_LICENSE_NAME, name);
	}
	
	/**
	 * @return the comments
	 * @throws SpdxInvalidTypeException 
	 */
	public String getComment() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(SpdxConstants.RDFS_PROP_COMMENT);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * @param comment the comment to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.RDFS_PROP_COMMENT, comment);
	}
	
	/**
	 * @return the urls which reference the same license information
	 * @throws SpdxInvalidTypeException 
	 */
	public Collection<String> getSeeAlso() throws InvalidSPDXAnalysisException {
		return getStringCollection(SpdxConstants.RDFS_PROP_SEE_ALSO);
	}
	/**
	 * @param seeAlsoUrl the urls which are references to the same license to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setSeeAlso(Collection<String> seeAlsoUrl) throws InvalidSPDXAnalysisException {
		if (seeAlsoUrl == null) {
			clearValueCollection(SpdxConstants.RDFS_PROP_SEE_ALSO);
		} else {
			setPropertyValue(SpdxConstants.RDFS_PROP_SEE_ALSO, seeAlsoUrl);
		}
	}
}
