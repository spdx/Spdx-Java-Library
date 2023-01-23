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
import java.util.Set;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;

/**
 * @author Gary O'Neall
 *
 */
public class LineCharPointer extends SinglePointer {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public LineCharPointer() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public LineCharPointer(String id) throws InvalidSPDXAnalysisException {
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
	public LineCharPointer(IModelStore modelStore, String documentUri, String id, ModelCopyManager copyManager,
			boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}
	
	/**
	 * @return the lineNumber, -1 if no lineNumber is stored
	 */
	public int getLineNumber() throws InvalidSPDXAnalysisException {
		Optional<Integer> retval = getIntegerPropertyValue(SpdxConstants.PROP_POINTER_LINE_NUMBER);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			logger.warn("Missing line number");
			return -1;
		}
	}
	
	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(Integer lineNumber) throws InvalidSPDXAnalysisException  {
		if (strict) {
			if (Objects.isNull(lineNumber)) {
				throw new InvalidSPDXAnalysisException("Can not set required lineNumber to null");
			}
		}
		setPropertyValue(SpdxConstants.PROP_POINTER_LINE_NUMBER, lineNumber);
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = super._verify(verifiedIds, specVersion);
		int lineNumber;
		try {
			lineNumber = getLineNumber();
			if (lineNumber == -1) {
				retval.add("Missing line number value");
			} else if (lineNumber < 0) {
				retval.add("Line number most not be negative for a line offset pointer: "+Integer.toString(lineNumber));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting line number: "+e.getMessage());
		}
		return retval;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SinglePointer o) {
		if (o == null) {
			return 1;
		}
		int retval = compareReferences(o);
		if (retval != 0) {
			return retval;
		}
		if (!(o instanceof LineCharPointer)) {
			return 1;
		}
		int compLine;
		try {
			compLine = ((LineCharPointer)o).getLineNumber();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting comp line",e);
			compLine = -1;
		}
		int line;
		try {
			line = getLineNumber();
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting line",e);
			line = -1;
		}
		return Integer.compare(line, compLine);
	}

	@Override
	public String toString() {
		int lineNumber;
		try {
			lineNumber = getLineNumber();
			if (lineNumber != -1) {
				return "line number " + Integer.toString(lineNumber);
			} else {
				return "Unknown line number";
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting line number",e);
			return "[ERROR]";
		}

	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_POINTER_LINE_CHAR_POINTER;
	}

}
