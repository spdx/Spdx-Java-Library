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
package org.spdx.library.model.compat.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxInvalidTypeException;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.Version;
import org.spdx.library.model.compat.v2.enumerations.ChecksumAlgorithm;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

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
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public Checksum(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}
	
	/**
	 * Create a checksum with an anonymous ID
	 * @param modelStore
	 * @param documentUri
	 * @param algorithm
	 * @param value
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static Checksum create(IModelStore modelStore, String documentUri, 
			ChecksumAlgorithm algorithm, String value) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(modelStore, "Missing required model store");
		Objects.requireNonNull(documentUri, "Missing required document URI");
		Objects.requireNonNull(algorithm, "Missing required algorithm");
		Objects.requireNonNull(value, "Missing required value");
		Checksum retval = new Checksum(modelStore, documentUri, 
				modelStore.getNextId(IdType.Anonymous, documentUri), null, true);
		retval.setAlgorithm(algorithm);
		retval.setValue(value);
		return retval;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstantsCompatV2.CLASS_SPDX_CHECKSUM;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = new ArrayList<>();
		ChecksumAlgorithm algorithm;
		try {
			algorithm = getAlgorithm();
			if (ChecksumAlgorithm.MISSING.equals(algorithm)) {
				retval.add("Missing required algorithm");
			} else {
				try {
					String checksumValue = getValue();
					if (checksumValue.isEmpty()) {
						retval.add("Missing required checksum value");
					} else {
						String verify = SpdxVerificationHelper.verifyChecksumString(checksumValue, 
								algorithm, specVersion);
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

	/**
	 * @return the ChecksumAlgorithm  MISSING denotes that there was no algorithm stored
	 * @throws InvalidSPDXAnalysisException
	 */
	public ChecksumAlgorithm getAlgorithm() throws InvalidSPDXAnalysisException {
		Optional<?> retval = getEnumPropertyValue(SpdxConstantsCompatV2.PROP_CHECKSUM_ALGORITHM);
		if (retval.isPresent()) {
			if (!(retval.get() instanceof ChecksumAlgorithm)) {
				logger.error("Invalid type for checksum algorithm: "+retval.get().getClass().toString());
				throw new SpdxInvalidTypeException("Invalid type for checksum algorithm: "+retval.get().getClass().toString());
			}
			return (ChecksumAlgorithm)retval.get();
		} else {
			return ChecksumAlgorithm.MISSING;
		}
	}


	/**
	 * Set the checksum algorithm.  This should only be called by factory classes since they should not be
	 * modified once created and stored.
	 * @param algorithm
	 * @throws InvalidSPDXAnalysisException
	 */
	protected void setAlgorithm(ChecksumAlgorithm algorithm) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(algorithm)) {
				throw new InvalidSPDXAnalysisException("Can not set required checksum algorithm to null");
			}
		}
		if (ChecksumAlgorithm.MISSING.equals(algorithm)) {
			throw new InvalidSPDXAnalysisException("Can not set required checksum algorithm to MISSING.  This is only used when no algorithm value was found.");
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_CHECKSUM_ALGORITHM, algorithm);
	}

	/**
	 * @return the checksum algorithm or an empty string if no algorithm value was stored
	 * @throws InvalidSPDXAnalysisException
	 */
	public String getValue() throws InvalidSPDXAnalysisException {
		Optional<String> retval = getStringPropertyValue(SpdxConstantsCompatV2.PROP_CHECKSUM_VALUE);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			return "";
		}
	}
	
	/**
	 * Set the value - this should only be called by factory methods
	 * @param value
	 * @throws InvalidSPDXAnalysisException
	 */
	protected void setValue(String value) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(value)) {
				throw new InvalidSPDXAnalysisException("Can not set required checksum value to null");
			}
			ChecksumAlgorithm algorithm = getAlgorithm();
			if (!ChecksumAlgorithm.MISSING.equals(algorithm)) {
				String verify = SpdxVerificationHelper.verifyChecksumString(value, algorithm, Version.CURRENT_SPDX_VERSION);
				if (verify != null && !verify.isEmpty()) {
					throw new InvalidSPDXAnalysisException(verify);
				}
			}
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_CHECKSUM_VALUE, value);
	}
	
	@Override
	public String toString() {
		ChecksumAlgorithm algorithm;
		try {
			algorithm = getAlgorithm();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting algorithm",e);
			algorithm = ChecksumAlgorithm.MISSING;
		}
		String checksumValue;
		try {
			checksumValue = getValue();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting value",e);
			checksumValue = "";
		}
		if (ChecksumAlgorithm.MISSING.equals(algorithm) || checksumValue.isEmpty()) {
			return "[EMPTY-CHECKSUM]";
		} else {
			return (algorithm.toString()+" "+checksumValue);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Checksum compare) {
		ChecksumAlgorithm algorithm;
		try {
			algorithm = getAlgorithm();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting algorithm",e);
			algorithm = ChecksumAlgorithm.MISSING;
		}
		String checksumValue;
		try {
			checksumValue = getValue();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting value",e);
			checksumValue = "";
		}
		ChecksumAlgorithm compareAlgorithm;
		try {
			compareAlgorithm = compare.getAlgorithm();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare algorithm",e);
			compareAlgorithm = ChecksumAlgorithm.MISSING;
		}
		String compareChecksumValue;
		try {
			compareChecksumValue = compare.getValue();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare value",e);
			compareChecksumValue = "";
		}
		int retval = algorithm.toString().compareTo(compareAlgorithm.toString());
		if (retval == 0) {
			retval = checksumValue.compareTo(compareChecksumValue);
		}
		return retval;
	}	
}
