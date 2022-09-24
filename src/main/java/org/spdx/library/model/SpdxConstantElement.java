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
package org.spdx.library.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;

/**
 * Type of SpdxElement which is a constant unmodifiable element
 * 
 * @author Gary O'Neall
 *
 */
public abstract class SpdxConstantElement extends SpdxElement implements IndividualUriValue {

	private Collection<Annotation> annotations = Collections.unmodifiableCollection(new ArrayList<>());
	private Collection<Relationship> relationships = Collections.unmodifiableCollection(new ArrayList<>());
	
	public SpdxConstantElement(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}
	
	/**
	 * @param modelStore where the model is stored
	 * @param documentUri Unique document URI
	 * @param id ID for the constant element
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxConstantElement(IModelStore modelStore, String documentUri, String id)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, null, true);
	}

	@Override
	protected List<String> _verify(List<String> verifiedIds, String specVersion) {
		return new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_NONE_ELEMENT;
	}
	
	@Override
	public Collection<Annotation> getAnnotations() throws InvalidSPDXAnalysisException {
		return annotations;
	}
	
	@Override
	public SpdxElement setAnnotations(Collection<Annotation> annotations) throws InvalidSPDXAnalysisException {
		throw new RuntimeException("Can not set annotations for NONE and NOASSERTION SPDX Elements");
	}
	
	@Override
	public boolean addAnnotation(Annotation annotation) throws InvalidSPDXAnalysisException {
		throw new RuntimeException("Can not add annotations for NONE and NOASSERTION SPDX Elements");
	}
	
	@Override
	public boolean removeAnnotation(Annotation annotation) throws InvalidSPDXAnalysisException {
		throw new RuntimeException("Can not remove annotations for NONE and NOASSERTION SPDX Elements");
	}
	
	@Override
	public Collection<Relationship> getRelationships() throws InvalidSPDXAnalysisException {
		return relationships;
	}
	
	@Override
	public SpdxElement setRelationships(Collection<Relationship> relationships) throws InvalidSPDXAnalysisException {
		throw new RuntimeException("Can not set relationships for NONE and NOASSERTION SPDX Elements");
	}
	
	@Override
	public boolean addRelationship(Relationship relationship) throws InvalidSPDXAnalysisException {
		throw new RuntimeException("Can not add relationships for NONE and NOASSERTION SPDX Elements");
	}
	
	@Override
	public boolean removeRelationship(Relationship relationship) throws InvalidSPDXAnalysisException {
		throw new RuntimeException("Can not remove relationships for NONE and NOASSERTION SPDX Elements");
	}
	
	@Override
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		throw new RuntimeException("Can not set comment for NONE and NOASSERTION SPDX Elements");
	}
	
	@Override
	public SpdxElement setName(String name) throws InvalidSPDXAnalysisException {
		throw new RuntimeException("Can not set name for NONE and NOASSERTION SPDX Elements");
	}
	
	@Override
	public boolean equals(Object comp) {
		if (!(comp instanceof IndividualUriValue)) {
			return false;
		}
		return Objects.equals(this.getIndividualURI(), ((IndividualUriValue)comp).getIndividualURI());
	}

	@Override
	public int hashCode() {
		return 11 ^ this.getIndividualURI().hashCode();
	}

}
