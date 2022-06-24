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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SpdxElement;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;

/**
 * A pointing method made up of a unique pointer. This is an abstract single pointer that provides the necessary framework, 
 * but it does not provide any kind of pointer, so more specific subclasses must be used.
 * See http://www.w3.org/2009/pointers and https://www.w3.org/WAI/ER/Pointers/WD-Pointers-in-RDF10-20110427
 * 
 * @author Gary O'Neall
 */
public abstract class SinglePointer extends ModelObject implements Comparable<SinglePointer> {
	
	static final Logger logger = LoggerFactory.getLogger(SinglePointer.class);

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SinglePointer() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SinglePointer(String id) throws InvalidSPDXAnalysisException {
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
	public SinglePointer(IModelStore modelStore, String documentUri, String id, ModelCopyManager copyManager,
			boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/**
	 * @return the reference, null if no reference is stored
	 * @throws InvalidSPDXAnalysisException 
	 */
	public @Nullable SpdxElement getReference() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstants.PROP_POINTER_REFERENCE);
		if (!retval.isPresent()) {
			return null;
		}
		if (!(retval.get() instanceof SpdxElement)) {
			throw new SpdxInvalidTypeException("Invalid type for reference.  Expect SdpxElement, found "+retval.get().getClass().toString());
		}
		return (SpdxElement)retval.get();
	}
	
	/**
	 * @param reference the reference to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setReference(SpdxElement reference) throws InvalidSPDXAnalysisException {
		if (strict) {
			if (Objects.isNull(reference)) {
				throw new InvalidSPDXAnalysisException("Can not set required reference to null");
			}
		}
		setPropertyValue(SpdxConstants.PROP_POINTER_REFERENCE, reference);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(List<String> verifiedIds, String specVersion) {
		ArrayList<String> retval = new ArrayList<String>();
		SpdxElement reference;
		try {
			reference = getReference();
			if (Objects.isNull(reference)) {
				retval.add("Missing required reference field");
			} else {
				retval.addAll(reference.verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting reference: "+e.getMessage());
		}

		return retval;
	}
	
	protected int compareReferences(SinglePointer o) {
		if (o == null) {
			return 1;
		}
		SpdxElement compRef = null;
		try {
			compRef = o.getReference();
			SpdxElement reference = getReference();
			if (reference == null) {
				if (compRef == null) {
					return 0;
				} else {
					return -1;
				}
			} else if (compRef == null) {
				return 1;
			} else {
				String myName = "";
				try {
					Optional<String> name = reference.getName();
					if (name.isPresent()) {
						myName = name.get();
					}
				} catch (InvalidSPDXAnalysisException ex) {
					logger.warn("Error getting reference name",ex);
				}
				String compName = "";
				try {
					Optional<String> cName = compRef.getName();
					if (cName.isPresent()) {
						compName = cName.get();
					}
				} catch (InvalidSPDXAnalysisException ex) {
					logger.warn("Error getting compare name",ex);
				}
				return myName.compareTo(compName);
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.error("Error getting comparison reference element",e);
			return -1;
		}
	}

}
