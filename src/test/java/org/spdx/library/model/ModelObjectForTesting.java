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
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.v3.core.ProfileIdentifierType;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * Concrete subclass of ModelObject for testing purposes
 * 
 * @author Gary O'Neall
 *
 */
public class ModelObjectForTesting extends ModelObject {

	public static final String TYPE = "ModelObjectForTesting";

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelObjectForTesting() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param objectUri
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelObjectForTesting(String objectUri)
			throws InvalidSPDXAnalysisException {
		super(objectUri);
	}

	/**
	 * @param modelStore
	 * @param objectUri
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelObjectForTesting(IModelStore modelStore, String objectUri,
			ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, objectUri, copyManager, create);
	}

	/**
	 * @param builder
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelObjectForTesting(CoreModelObjectBuilder builder)
			throws InvalidSPDXAnalysisException {
		super(builder);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.Set, java.lang.String, java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedElementIds,
			String specVersion, List<ProfileIdentifierType> profiles) {
		return new ArrayList<String>();
	}
	
	public static class ModelObjectForTestingBuilder extends CoreModelObjectBuilder {
		
		/**
		 * Create an ElementBuilder from another model object copying the modelStore and copyManager and using an anonymous ID
		 * @param from model object to copy the model store and copyManager from
		 * @throws InvalidSPDXAnalysisException
		 */
		public ModelObjectForTestingBuilder(ModelObject from) throws InvalidSPDXAnalysisException {
			this(from, from.getModelStore().getNextId(IdType.Anonymous, null));
		}
	
		/**
		 * Create an ElementBuilder from another model object copying the modelStore and copyManager
		 * @param from model object to copy the model store and copyManager from
		 * @param objectUri URI for the object
		 * @param objectUri
		 */
		public ModelObjectForTestingBuilder(ModelObject from, String objectUri) {
			this(from.getModelStore(), objectUri, from.getCopyManager());
			setStrict(from.isStrict());
			setExternalMap(from.externalMap);
		}
		
		/**
		 * Creates a ElementBuilder
		 * @param modelStore model store for the built Element
		 * @param objectUri objectUri for the built Element
		 * @param copyManager optional copyManager for the built Element
		 */
		public ModelObjectForTestingBuilder(IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) {
			super(modelStore, objectUri, copyManager);
		}
	}

}
