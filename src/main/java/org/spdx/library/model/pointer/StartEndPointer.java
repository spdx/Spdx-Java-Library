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
package org.spdx.library.model.pointer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;

/**
 * A compound pointer pointing out parts of a document by means of a range delimited by a pair of single pointers that define the start point and the end point.
 * See http://www.w3.org/2009/pointers and https://www.w3.org/WAI/ER/Pointers/WD-Pointers-in-RDF10-20110427
 * @author Gary O'Neall
 */
public class StartEndPointer extends CompoundPointer implements Comparable<StartEndPointer> {
	
	static final Logger logger = LoggerFactory.getLogger(StartEndPointer.class);

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public StartEndPointer() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public StartEndPointer(String id) throws InvalidSPDXAnalysisException {
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
	public StartEndPointer(IModelStore modelStore, String documentUri, String id, ModelCopyManager copyManager,
			boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	@Override
	public String getType() {
		return SpdxConstants.CLASS_POINTER_START_END_POINTER;
	}
	
	/**
	 * @return the endPointer, null if not present
	 * @throws InvalidSPDXAnalysisException 
	 */
	public @Nullable SinglePointer getEndPointer() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstants.PROP_POINTER_END_POINTER);
		if (!retval.isPresent()) {
			return null;
		}
		if (!(retval.get() instanceof SinglePointer)) {
			logger.error("Incorrect type for getEndPointer - expected SinglePointer, found "+retval.get().getClass().toString());
			throw new SpdxInvalidTypeException("Incorrect type for getEndPointer - expected SinglePointer, found "+retval.get().getClass().toString());
		}
		return (SinglePointer)retval.get();
	}
	
	/**
	 * @param endPointer the endPointer to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setEndPointer(SinglePointer endPointer) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(endPointer)) {
				throw new InvalidSPDXAnalysisException("Can not set required endPointer to null");
			}
			SinglePointer startPointer = getStartPointer();
			if (Objects.nonNull(startPointer) && !startPointer.getClass().isAssignableFrom(endPointer.getClass())) {
				throw new SpdxInvalidTypeException("Incompatable type for endPointer: "+endPointer.getClass().toString() + 
						".  Must be assignable to "+startPointer.getClass().toString()+".");
			}
			//TODO: We could add a check to make sure the endpointer is greater than the startpointer
		}
		setPropertyValue(SpdxConstants.PROP_POINTER_END_POINTER, endPointer);
	}
	
	@Override
	public StartEndPointer setStartPointer(SinglePointer startPointer) throws InvalidSPDXAnalysisException {
		if (strict) {
			SinglePointer endPointer = getEndPointer();
			if (Objects.nonNull(endPointer) && !endPointer.getClass().isAssignableFrom(startPointer.getClass())) {
				throw new SpdxInvalidTypeException("Incompatable type for startPointer: "+startPointer.getClass().toString() + 
						".  Must be assignable to "+endPointer.getClass().toString()+".");
			}
			//TODO: We could add a check to make sure the startPointer is less than the endPointer
		}
		setPropertyValue(SpdxConstants.PROP_POINTER_START_POINTER, startPointer);
		return this;
	}

	@Override
	public List<String> verify() {
		List<String> retval = super.verify();
		SinglePointer endPointer;
		try {
			endPointer = getEndPointer();
			if (endPointer == null) {
				retval.add("Missing required end pointer");
			} else {
				retval.addAll(endPointer.verify());
				try {
					SinglePointer startPointer = getStartPointer();
					if (startPointer != null && startPointer instanceof ByteOffsetPointer && !(endPointer instanceof ByteOffsetPointer)) {
						retval.add("Inconsistent start and end pointer types");
					}
					if (startPointer != null && startPointer instanceof LineCharPointer && !(endPointer instanceof LineCharPointer)) {
						retval.add("Inconsistent start and end pointer types");
					}
					if (startPointer != null && startPointer.compareTo(endPointer) > 0) {
						retval.add("End pointer is less than start pointer");
					}
				}  catch (InvalidSPDXAnalysisException e) {
					retval.add("Error getting startPointer: "+e.getMessage());
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting endPointer: "+e.getMessage());
		}
		return retval;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(StartEndPointer o) {
		if (o == null) {
			return 1;
		}
		try {
			SinglePointer startPointer = getStartPointer();
			SinglePointer compareStartPointer = o.getStartPointer();
			if (startPointer == null) {
				if (compareStartPointer == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (compareStartPointer == null) {
				return 1;
			} else {
				return startPointer.compareTo(compareStartPointer);
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error getting comparison for start end pointer",e);
			return -1;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("From: ");
		SinglePointer startPointer;
		try {
			startPointer = getStartPointer();
			if (startPointer != null) {
				sb.append(startPointer.toString());
			} else {
				sb.append("[UNKNOWN]");
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting start pointer",e);
			sb.append("[ERROR]");
		}

		sb.append(" To: ");
		SinglePointer endPointer;
		try {
			endPointer = getEndPointer();
			if (endPointer != null) {
				sb.append(endPointer.toString());
			} else {
				sb.append("[UNKNOWN]");
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting end pointer",e);
			sb.append("[ERROR]");
		}

		return sb.toString();
	}

}
