/**
 * Copyright (c) 2023 Source Auditor Inc.
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
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.IndividualUriValue;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.core.CreationInfo;
import org.spdx.library.model.v3.core.Element;
import org.spdx.library.model.v3.core.ExternalIdentifier;
import org.spdx.library.model.v3.core.ExternalMap;
import org.spdx.library.model.v3.core.ExternalReference;
import org.spdx.library.model.v3.core.ProfileIdentifierType;
import org.spdx.storage.IModelStore;

/**
 * This class represents an SPDX element which is not present in the model store
 * 
 * The external property provides optional information on where the external element may be located and verified
 * 
 * @author Gary O'Neall
 *
 */
public class ExternalElement extends Element implements IndividualUriValue {
	
	ExternalMap external;

	/**
	 * @param store store to use for the inflated object
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 * @param objectUri URI or anonymous ID for the Element
	 * @param external Information about the external map
	 * @throws InvalidSPDXAnalysisException 
	 */
	public ExternalElement(IModelStore store, String objectUri, ModelCopyManager copyManager,
			ExternalMap external) throws InvalidSPDXAnalysisException {
		super(store, objectUri, copyManager, true);
		this.external = external;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.IndividualUriValue#getIndividualURI()
	 */
	@Override
	public String getIndividualURI() {
		return getObjectUri();
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return "Core.ExternalElement";
	}
	
	// Getters and Setters

	/**
	 * @return the externalMap
	 */
	public ExternalMap getExternal() {
		return this.external;
	}

	

		/**
	 * @return the creationInfo
	 */
	 @Override
	public CreationInfo getCreationInfo() throws InvalidSPDXAnalysisException {
		 throw new InvalidSPDXAnalysisException(getObjectUri() + " is external to this object store.");
	}
	
	@Override
	public Collection<ExternalReference> getExternalReferences() {
		throw new RuntimeException(new InvalidSPDXAnalysisException(getObjectUri() + " is external to this object store."));
	}
	@Override
	public Collection<ExternalIdentifier> getExternalIdentifiers() {
		throw new RuntimeException(new InvalidSPDXAnalysisException(getObjectUri() + " is external to this object store."));
	}
	

		/**
	 * @return the description
	 */
	public Optional<String> getDescription() throws InvalidSPDXAnalysisException {
		throw new InvalidSPDXAnalysisException(getObjectUri() + " is external to this object store.");
	}
	/**
	 * @param description the description to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Override
	public Element setDescription(@Nullable String description) throws InvalidSPDXAnalysisException {
		throw new InvalidSPDXAnalysisException(getObjectUri() + " is external to this object store.");
	}

	/**
	 * @return the summary
	 */
	@Override
	public Optional<String> getSummary() throws InvalidSPDXAnalysisException {
		throw new InvalidSPDXAnalysisException(getObjectUri() + " is external to this object store.");
	}
	/**
	 * @param summary the summary to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Override
	public Element setSummary(@Nullable String summary) throws InvalidSPDXAnalysisException {
		throw new InvalidSPDXAnalysisException(getObjectUri() + " is external to this object store.");
	}
	
	
	@Override
	public String toString() {
		return "External Element: "+getObjectUri();
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion, List<ProfileIdentifierType> profiles) {
		return new ArrayList<>();
	}

}
