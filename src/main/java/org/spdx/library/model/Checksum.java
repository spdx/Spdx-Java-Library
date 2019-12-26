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
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.storage.IModelStore;

/**
 * A Checksum is value that allows the contents of a file to be authenticated. 
 * Even small changes to the content of the file will change its checksum. 
 * This class allows the results of a variety of checksum and cryptographic 
 * message digest algorithms to be represented.
 * 
 * @author Gary O'Neall
 */
public class Checksum extends ModelObject implements Comparable<Checksum>  {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public Checksum() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public Checksum(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public Checksum(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_CHECKSUM;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#verify()
	 */
	@Override
	public List<String> verify() {
		List<String> retval = new ArrayList<>();
		Optional<ChecksumAlgorithm> algorithm;
		try {
			algorithm = getAlgorithm();
			if (!algorithm.isPresent()) {
				retval.add("Missing required algorithm");
			} else {
				try {
					Optional<String> checksumValue = getValue();
					if (!checksumValue.isPresent() || checksumValue.get().isEmpty()) {
						retval.add("Missing required checksum value");
					} else {
						String verify = SpdxVerificationHelper.verifyChecksumString(checksumValue.get(), algorithm.get());
						if (verify != null) {
							retval.add(verify);
						}
					}
				} catch (InvalidSPDXAnalysisException e) {
					logger.error("Error getting checksum value",e);
					retval.add("Error getting checksum value: "+e.getMessage());
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error getting algorithm",e);
			retval.add("Error getting checksum algorithm: "+e.getMessage());
		}
		return retval;
	}

	@SuppressWarnings("unchecked")
	public Optional<ChecksumAlgorithm> getAlgorithm() throws InvalidSPDXAnalysisException {
		Optional<?> retval = getEnumPropertyValue(SpdxConstants.PROP_CHECKSUM_ALGORITHM);
		if (retval.isPresent() && !(retval.get() instanceof ChecksumAlgorithm)) {
			logger.error("Invalid type for checksum algorithm: "+retval.get().getClass().toString());
			throw new SpdxInvalidTypeException("Invalid type for checksum algorithm: "+retval.get().getClass().toString());
		}
		return (Optional<ChecksumAlgorithm>)retval;
	}

	public Optional<String> getValue() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_CHECKSUM_VALUE);
	}

	public void setAlgorithm(ChecksumAlgorithm algorithm) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_CHECKSUM_ALGORITHM, algorithm);
	}

	public void setValue(String value) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_CHECKSUM_VALUE, value);
	}
	
	@Override
	public String toString() {
		Optional<ChecksumAlgorithm> algorithm;
		try {
			algorithm = getAlgorithm();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting algorithm",e);
			algorithm = Optional.empty();
		}
		Optional<String> checksumValue;
		try {
			checksumValue = getValue();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting value",e);
			checksumValue = Optional.empty();
		}
		if (!algorithm.isPresent() || !checksumValue.isPresent()) {
			return "[EMPTY-CHECKSUM]";
		} else {
			return (algorithm.get().toString()+" "+checksumValue.get());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Checksum compare) {
		Optional<ChecksumAlgorithm> algorithm;
		try {
			algorithm = getAlgorithm();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting algorithm",e);
			algorithm = Optional.empty();
		}
		Optional<String> checksumValue;
		try {
			checksumValue = getValue();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting value",e);
			checksumValue = Optional.empty();
		}
		Optional<ChecksumAlgorithm> compareAlgorithm;
		try {
			compareAlgorithm = compare.getAlgorithm();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare algorithm",e);
			compareAlgorithm = Optional.empty();
		}
		Optional<String> compareChecksumValue;
		try {
			compareChecksumValue = compare.getValue();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare value",e);
			compareChecksumValue = Optional.empty();
		}
		int retval = 0;
		if (!algorithm.isPresent()) {
			if (compareAlgorithm.isPresent()) {
				retval = 1;
			} else {
				retval = 0;
			}
		} else {
			if (!compareAlgorithm.isPresent()) {
				retval = -1;
			} else {
				retval = algorithm.get().toString().compareTo(compareAlgorithm.get().toString());
			}
			
		} 
		if (retval == 0) {
			if (!checksumValue.isPresent()) {
				if (compareChecksumValue.isPresent()) {
					retval = 1;
				} else {
					retval = 0;
				}
			} else {
				if (!compareChecksumValue.isPresent()) {
					retval = -1;
				} else {
					retval = checksumValue.get().compareTo(compareChecksumValue.get());
				}
			}
		}
		return retval;
	}

}
