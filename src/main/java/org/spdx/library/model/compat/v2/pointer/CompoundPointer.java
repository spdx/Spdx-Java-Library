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
package org.spdx.library.model.compat.v2.pointer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.model.compat.v2.ModelObject;
import org.spdx.library.model.compat.v2.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;

/**
 * A pointing method made up of a pair of pointers that identify a well defined section within a document delimited by a begin and an end.
 * See http://www.w3.org/2009/pointers and https://www.w3.org/WAI/ER/Pointers/WD-Pointers-in-RDF10-20110427
 * This is an abstract class of pointers which must be subclassed
 * @author Gary O'Neall
 */
public abstract class CompoundPointer extends ModelObject {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public CompoundPointer() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public CompoundPointer(String id) throws InvalidSPDXAnalysisException {
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
	public CompoundPointer(IModelStore modelStore, String documentUri, String id, ModelCopyManager copyManager,
			boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}
	
	/**
	 * @return the startPointer, null if not present
	 * @throws InvalidSPDXAnalysisException 
	 */
	public @Nullable SinglePointer getStartPointer() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstantsCompatV2.PROP_POINTER_START_POINTER);
		if (!retval.isPresent()) {
			return null;
		}
		if (!(retval.get() instanceof SinglePointer)) {
			throw new SpdxInvalidTypeException("Incorrect type for getEndPointer - expected SinglePointer, found "+retval.get().getClass().toString());
		}
		return (SinglePointer)retval.get();
	}

	/*
	 * @param startPointer the startPointer to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public CompoundPointer setStartPointer(SinglePointer startPointer) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(startPointer)) {
				throw new InvalidSPDXAnalysisException("Can not set required startPointer to null");
			}
		}
		setPropertyValue(SpdxConstantsCompatV2.PROP_POINTER_START_POINTER, startPointer);
		return this;
	 }

	/* (non-Javadoc)
	 * @see org.spdx.library.model.compat.v2.compat.v2.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		ArrayList<String> retval = new ArrayList<String>();
		try {
			SinglePointer startPointer = getStartPointer();
			if (startPointer == null) {
				retval.add("Missing required start pointer");
			} else {
				retval.addAll(startPointer.verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException ex) {
			retval.add("Error getting start pointer: "+ex.getMessage());
		}
		return retval;
	}
	
	@Override
	public String getType() {
		return SpdxConstantsCompatV2.CLASS_POINTER_COMPOUNT_POINTER;
	}

}
