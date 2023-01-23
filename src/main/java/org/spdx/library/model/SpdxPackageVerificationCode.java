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
package org.spdx.library.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.storage.IModelStore;

/**
 * @author gary
 *
 */
public class SpdxPackageVerificationCode extends ModelObject {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackageVerificationCode() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackageVerificationCode(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackageVerificationCode(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_VERIFICATIONCODE;
	}


	/**
	 * @return the value of the verification code
	 * @throws InvalidSPDXAnalysisException
	 */
	public String getValue() throws InvalidSPDXAnalysisException {
		Optional<String> retval = getStringPropertyValue(SpdxConstants.PROP_VERIFICATIONCODE_VALUE);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			logger.warn("No value found - returning empty string");
			return "";
		}
	}
	
	/**
	 * Set the value for the verification code
	 * @param value verification code value
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setValue(String value) throws InvalidSPDXAnalysisException {
		if (strict && (Objects.isNull(value) || value.isEmpty())) {
			throw new InvalidSPDXAnalysisException("Can not set required verification code value to null or empty string");
		}
		setPropertyValue(SpdxConstants.PROP_VERIFICATIONCODE_VALUE, value);
	}
	
	/**
	 * @return Collection containing files which have been excluded from the verification code calculation
	 * @throws InvalidSPDXAnalysisException
	 */
	public Collection<String> getExcludedFileNames() throws InvalidSPDXAnalysisException {
		return this.getStringCollection(SpdxConstants.PROP_VERIFICATIONCODE_IGNORED_FILES);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = new ArrayList<>();
		try {
			String value = this.getValue();
			if (value.isEmpty()) {
				retval.add("Missing required verification code value");
			} else {
				String verify = SpdxVerificationHelper.verifyChecksumString(value, ChecksumAlgorithm.SHA1, specVersion);
				if (verify != null) {
					retval.add(verify);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting verification code value: "+e.getMessage());
		}
		return retval;
	}
}
