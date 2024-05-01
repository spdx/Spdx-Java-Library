/**
 * Copyright (c) 2024 Source Auditor Inc.
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
 
package org.spdx.library.model.v3.security;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.ModelObject;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.IModelStore.IModelStoreLock;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.v3.core.ProfileIdentifierType;

/**
 * DO NOT EDIT - this file is generated by the Owl to Java Utility 
 * See: https://github.com/spdx/tools-java 
 * 
 * VexAffectedVulnAssessmentRelationship connects a vulnerability and a number 
 * of elements. The relationship marks these elements as products affected by the vulnerability. 
 * This relationship corresponds to the VEX affected status. **Constraints** When 
 * linking elements using a VexAffectedVulnAssessmentRelationship, the following 
 * requirements must be observed: - Elements linked with a VulnVexAffectedAssessmentRelationship 
 * are constrained to the affects relationship type. **Syntax** ```json { "@type": 
 * "VexAffectedVulnAssessmentRelationship", "@id": "urn:spdx.dev:vex-affected-1", 
 * "relationshipType": "affects", "from": "urn:spdx.dev:vuln-cve-2020-28498", 
 * "to": ["urn:product-acme-application-1.3"], "assessedElement": "urn:npm-elliptic-6.5.2", 
 * "actionStatement": "Upgrade to version 1.4 of ACME application.", "suppliedBy": 
 * ["urn:spdx.dev:agent-jane-doe"], "publishedTime": "2021-03-09T11:04:53Z" 
 * } ``` 
 */
public class VexAffectedVulnAssessmentRelationship extends VexVulnAssessmentRelationship  {

	Collection<String> actionStatementTimes;
	
	/**
	 * Create the VexAffectedVulnAssessmentRelationship with default model store and generated anonymous ID
	 * @throws InvalidSPDXAnalysisException when unable to create the VexAffectedVulnAssessmentRelationship
	 */
	public VexAffectedVulnAssessmentRelationship() throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore().getNextId(IdType.Anonymous, null));
	}

	/**
	 * @param objectUri URI or anonymous ID for the VexAffectedVulnAssessmentRelationship
	 * @throws InvalidSPDXAnalysisException when unable to create the VexAffectedVulnAssessmentRelationship
	 */
	public VexAffectedVulnAssessmentRelationship(String objectUri) throws InvalidSPDXAnalysisException {
		this(DefaultModelStore.getDefaultModelStore(), objectUri, DefaultModelStore.getDefaultCopyManager(), true);
	}

	/**
	 * @param modelStore Model store where the VexAffectedVulnAssessmentRelationship is to be stored
	 * @param objectUri URI or anonymous ID for the VexAffectedVulnAssessmentRelationship
	 * @param copyManager Copy manager for the VexAffectedVulnAssessmentRelationship - can be null if copying is not required
	 * @param create true if VexAffectedVulnAssessmentRelationship is to be created
	 * @throws InvalidSPDXAnalysisException when unable to create the VexAffectedVulnAssessmentRelationship
	 */
	 @SuppressWarnings("unchecked")
	public VexAffectedVulnAssessmentRelationship(IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager,
			boolean create)	throws InvalidSPDXAnalysisException {
		super(modelStore, objectUri, copyManager, create);
		actionStatementTimes = (Collection<String>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.SECURITY_PROP_ACTION_STATEMENT_TIME, String.class);
	}

	/**
	 * Create the VexAffectedVulnAssessmentRelationship from the builder - used in the builder class
	 * @param builder Builder to create the VexAffectedVulnAssessmentRelationship from
	 * @throws InvalidSPDXAnalysisException when unable to create the VexAffectedVulnAssessmentRelationship
	 */
	 @SuppressWarnings("unchecked")
	protected VexAffectedVulnAssessmentRelationship(VexAffectedVulnAssessmentRelationshipBuilder builder) throws InvalidSPDXAnalysisException {
		super(builder);
		actionStatementTimes = (Collection<String>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.SECURITY_PROP_ACTION_STATEMENT_TIME, String.class);
		getActionStatementTimes().addAll(builder.actionStatementTimes);
		setActionStatement(builder.actionStatement);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return "Security.VexAffectedVulnAssessmentRelationship";
	}
	
	// Getters and Setters
	public Collection<String> getActionStatementTimes() {
		return actionStatementTimes;
	}
	

		/**
	 * @return the actionStatement
	 */
	public Optional<String> getActionStatement() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.SECURITY_PROP_ACTION_STATEMENT);
	}
	/**
	 * @param actionStatement the actionStatement to set
	 * @return this to chain setters
	 * @throws InvalidSPDXAnalysisException 
	 */
	public VexAffectedVulnAssessmentRelationship setActionStatement(@Nullable String actionStatement) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.SECURITY_PROP_ACTION_STATEMENT, actionStatement);
		return this;
	}
	
	
	@Override
	public String toString() {
		return "VexAffectedVulnAssessmentRelationship: "+getObjectUri();
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.List)
	 */
	@Override
	public List<String> _verify(Set<String> verifiedIds, String specVersionForVerify, List<ProfileIdentifierType> profiles) {
		List<String> retval = new ArrayList<>();
		retval.addAll(super._verify(verifiedIds, specVersionForVerify, profiles));
		try {
			@SuppressWarnings("unused")
			Optional<String> actionStatement = getActionStatement();
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting actionStatement for VexAffectedVulnAssessmentRelationship: "+e.getMessage());
		}
		return retval;
	}
	
	public static class VexAffectedVulnAssessmentRelationshipBuilder extends VexVulnAssessmentRelationshipBuilder {
	
		/**
		 * Create an VexAffectedVulnAssessmentRelationshipBuilder from another model object copying the modelStore and copyManager and using an anonymous ID
		 * @param from model object to copy the model store and copyManager from
		 * @throws InvalidSPDXAnalysisException
		 */
		public VexAffectedVulnAssessmentRelationshipBuilder(ModelObject from) throws InvalidSPDXAnalysisException {
			this(from, from.getModelStore().getNextId(IdType.Anonymous, null));
		}
	
		/**
		 * Create an VexAffectedVulnAssessmentRelationshipBuilder from another model object copying the modelStore and copyManager
		 * @param from model object to copy the model store and copyManager from
		 * @param objectUri URI for the object
		 * @param objectUri
		 */
		public VexAffectedVulnAssessmentRelationshipBuilder(ModelObject from, String objectUri) {
			this(from.getModelStore(), objectUri, from.getCopyManager());
			setStrict(from.isStrict());
		}
		
		/**
		 * Creates a VexAffectedVulnAssessmentRelationshipBuilder
		 * @param modelStore model store for the built VexAffectedVulnAssessmentRelationship
		 * @param objectUri objectUri for the built VexAffectedVulnAssessmentRelationship
		 * @param copyManager optional copyManager for the built VexAffectedVulnAssessmentRelationship
		 */
		public VexAffectedVulnAssessmentRelationshipBuilder(IModelStore modelStore, String objectUri, @Nullable ModelCopyManager copyManager) {
			super(modelStore, objectUri, copyManager);
		}
		
		Collection<String> actionStatementTimes = new ArrayList<>();
		String actionStatement = null;
		
		
		/**
		 * Adds a actionStatementTime to the initial collection
		 * @parameter actionStatementTime actionStatementTime to add
		 * @return this for chaining
		**/
		public VexAffectedVulnAssessmentRelationshipBuilder addActionStatementTime(String actionStatementTime) {
			if (Objects.nonNull(actionStatementTime)) {
				actionStatementTimes.add(actionStatementTime);
			}
			return this;
		}
		
		/**
		 * Adds all elements from a collection to the initial actionStatementTime collection
		 * @parameter actionStatementTimeCollection collection to initialize the actionStatementTime
		 * @return this for chaining
		**/
		public VexAffectedVulnAssessmentRelationshipBuilder addAllActionStatementTime(Collection<String> actionStatementTimeCollection) {
			if (Objects.nonNull(actionStatementTimeCollection)) {
				actionStatementTimes.addAll(actionStatementTimeCollection);
			}
			return this;
		}
		
		/**
		 * Sets the initial value of actionStatement
		 * @parameter actionStatement value to set
		 * @return this for chaining
		**/
		public VexAffectedVulnAssessmentRelationshipBuilder setActionStatement(String actionStatement) {
			this.actionStatement = actionStatement;
			return this;
		}
	
		
		/**
		 * @return the VexAffectedVulnAssessmentRelationship
		 * @throws InvalidSPDXAnalysisException on any errors during build
		 */
		public VexAffectedVulnAssessmentRelationship build() throws InvalidSPDXAnalysisException {
			IModelStoreLock lock = modelStore.enterCriticalSection(false);
			try {
				return new VexAffectedVulnAssessmentRelationship(this);
			} finally {
				modelStore.leaveCriticalSection(lock);
			}
		}
	}
}