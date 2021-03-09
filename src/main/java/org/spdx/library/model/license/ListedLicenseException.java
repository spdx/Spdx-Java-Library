/**
 * Copyright (c) 2020 Source Auditor Inc.
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
package org.spdx.library.model.license;

import java.util.Collection;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ModelObject;
import org.spdx.licenseTemplate.LicenseTemplateRuleException;
import org.spdx.licenseTemplate.SpdxLicenseTemplateHelper;
import org.spdx.storage.IModelStore;

/**
 * Represents a License Exception present on the SPDX License List
 * 
 * @author Gary O'Neall
 *
 */
public class ListedLicenseException extends LicenseException {

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public ListedLicenseException(IModelStore modelStore, String documentUri, String id, ModelCopyManager copyManager,
			boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/**
	 * @param id
	 * @param name
	 * @param text
	 * @param seeAlso
	 * @param comment
	 * @throws InvalidSPDXAnalysisException
	 */
	public ListedLicenseException(String id, String name, String text, Collection<String> seeAlso, String comment)
			throws InvalidSPDXAnalysisException {
		super(id, name, text, seeAlso, comment);
	}

	/**
	 * @param id
	 * @param name
	 * @param text
	 * @param template
	 * @param seeAlso
	 * @param comment
	 * @throws InvalidSPDXAnalysisException
	 */
	public ListedLicenseException(String id, String name, String text, String template, Collection<String> seeAlso,
			String comment) throws InvalidSPDXAnalysisException {
		super(id, name, text, template, seeAlso, comment);
	}

	/**
	 * @param id
	 * @param name
	 * @param text
	 * @throws InvalidSPDXAnalysisException
	 */
	public ListedLicenseException(String id, String name, String text) throws InvalidSPDXAnalysisException {
		super(id, name, text);
	}
	
	/**
	 * @param exceptionTextHtml
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setExceptionTextHtml(String exceptionTextHtml) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_EXCEPTION_TEXT_HTML, exceptionTextHtml);
	}
	
	/**
	 * @return HTML form of the exception text either from a stored property or generated from the template or text
	 * @throws InvalidSPDXAnalysisException
	 */
	public String getExceptionTextHtml() throws InvalidSPDXAnalysisException {
		Optional<String> exceptionTextHtml = getStringPropertyValue(SpdxConstants.PROP_EXCEPTION_TEXT_HTML);
		if (exceptionTextHtml.isPresent()) {
			return exceptionTextHtml.get();
		} else {
			Optional<String> templateText = getStringPropertyValue(SpdxConstants.PROP_EXCEPTION_TEMPLATE);
			if (templateText.isPresent()) {
				try {
					return SpdxLicenseTemplateHelper.templateTextToHtml(templateText.get());
				} catch(LicenseTemplateRuleException ex) {
					throw new InvalidSPDXAnalysisException("Invalid license rule found in exception text for exception "+getName()+":"+ex.getMessage());
				}
			} else {
				Optional<String> exceptionText = getStringPropertyValue(SpdxConstants.PROP_EXCEPTION_TEXT);
				if (exceptionText.isPresent()) {
					return SpdxLicenseTemplateHelper.formatEscapeHTML(exceptionText.get());
				} else {
					return "";
				}
			}
		}
	}
	
	   @Override
	    public boolean equivalent(ModelObject compare, boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
	        if (compare instanceof ListedLicenseException) {
	            return this.getId().equals(((ListedLicenseException)compare).getId()); // for listed license, the license ID is the only thing that matters
	        } else {
	            return super.equivalent(compare, ignoreRelatedElements);
	        }
	    }

}
