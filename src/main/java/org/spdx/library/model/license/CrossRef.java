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
package org.spdx.library.model.license;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ModelObject;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;

/**
 * Cross reference details for the a URL reference
 * 
 * @author Gary O'Neall
 *
 */
public class CrossRef extends ModelObject {
	
	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public CrossRef() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public CrossRef(String id) throws InvalidSPDXAnalysisException {
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
	public CrossRef(IModelStore modelStore, String documentUri, String id, ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}
	
	/**
	 * @param builder Builder to build the file
	 * @throws InvalidSPDXAnalysisException
	 */
	protected CrossRef(CrossRefBuilder builder) throws InvalidSPDXAnalysisException {
		this(builder.modelStore, builder.documentUri, builder.id, builder.copyManager, true);
		setLive(builder.live);
		setMatch(builder.match);
		setOrder(builder.order);
		setTimestamp(builder.timestamp);
		setUrl(builder.url);
		setValid(builder.valid);
		setIsWayBackLink(builder.wayBackLink);
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_CROSS_REF;
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = new ArrayList<>();
		Optional<String> url;
		try {
			url = getStringPropertyValue(SpdxConstants.PROP_CROSS_REF_URL);
			if (!url.isPresent()) {
				retval.add("Missing required URL");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting URL property value: "+e.getMessage());
		}
		return retval;
	}

	/**
	 * @return the match
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<String> getMatch() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_CROSS_REF_MATCH);
	}

	/**
	 * @param match the match to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setMatch(@Nullable String match) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_CROSS_REF_MATCH, match);
	}

	/**
	 * @return the url
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<String> getUrl() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_CROSS_REF_URL);
	}

	/**
	 * @param url the url to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setUrl(String url) throws InvalidSPDXAnalysisException {
		if (strict) {
			Objects.requireNonNull(url, "URL must not be null");
		}
		setPropertyValue(SpdxConstants.PROP_CROSS_REF_URL, url);
	}

	/**
	 * @return the isValid
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<Boolean> getValid() throws InvalidSPDXAnalysisException {
		return getBooleanPropertyValue(SpdxConstants.PROP_CROSS_REF_IS_VALID);
	}

	/**
	 * @param isValid the isValid to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setValid(@Nullable Boolean isValid) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_CROSS_REF_IS_VALID, isValid);
	}

	/**
	 * @return the isLive
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<Boolean> getLive() throws InvalidSPDXAnalysisException {
		return getBooleanPropertyValue(SpdxConstants.PROP_CROSS_REF_IS_LIVE);
	}

	/**
	 * @param isLive the isLive to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setLive(Boolean isLive) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_CROSS_REF_IS_LIVE, isLive);
	}

	/**
	 * @return the timestamp
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<String> getTimestamp() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_CROSS_REF_TIMESTAMP);
	}

	/**
	 * @param timestamp the timestamp to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setTimestamp(String timestamp) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_CROSS_REF_TIMESTAMP, timestamp);
	}

	/**
	 * @return the isWayBackLink
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<Boolean> getIsWayBackLink() throws InvalidSPDXAnalysisException {
		return getBooleanPropertyValue(SpdxConstants.PROP_CROSS_REF_WAYBACK_LINK);
	}

	/**
	 * @param isWayBackLink the isWayBackLink to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setIsWayBackLink(Boolean isWayBackLink) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_CROSS_REF_WAYBACK_LINK, isWayBackLink);
	}

	/**
	 * @return the order
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<Integer> getOrder() throws InvalidSPDXAnalysisException {
		return getIntegerPropertyValue(SpdxConstants.PROP_CROSS_REF_ORDER);
	}

	/**
	 * @param order the order to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setOrder(Integer order) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_CROSS_REF_ORDER, order);
	}
	
	/**
	 * Convenience method for setting details related to the URL checking
	 * @param isValid
	 * @param isLive
	 * @param isWayBackLink
	 * @param match
	 * @param timestamp
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setDetails(@Nullable Boolean isValid, @Nullable Boolean isLive, @Nullable Boolean isWayBackLink, 
			@Nullable String match, @Nullable String timestamp) throws InvalidSPDXAnalysisException {
		setValid(isValid);
		setLive(isLive);
		setIsWayBackLink(isWayBackLink);
		setMatch(match);
		setTimestamp(timestamp);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String url;
		try {
			Optional<String> oUrl = getStringPropertyValue(SpdxConstants.PROP_CROSS_REF_URL);
			if (oUrl.isPresent()) {
				url = oUrl.get();
			} else {
				url = "N/A";
			}
		} catch (InvalidSPDXAnalysisException e) {
			url = "N/A";
		}

		String isValid;
		try {
			Optional<Boolean> oIsValid = getBooleanPropertyValue(SpdxConstants.PROP_CROSS_REF_IS_VALID);
			if (oIsValid.isPresent()) {
				isValid = oIsValid.get().toString();
			} else {
				isValid = "N/A";
			}
		} catch (InvalidSPDXAnalysisException e) {
			isValid = "N/A";
		}
		String isLive;
		try {
			Optional<Boolean> oIsLive = getBooleanPropertyValue(SpdxConstants.PROP_CROSS_REF_IS_LIVE);
	
			if (oIsLive.isPresent()) {
				isLive = oIsLive.get().toString();
			} else {
				isLive = "N/A";
			}
		} catch (InvalidSPDXAnalysisException e) {
			isLive = "N/A";
		}
		String isWayBackLink;
		try {
			Optional<Boolean> oWayback = getBooleanPropertyValue(SpdxConstants.PROP_CROSS_REF_WAYBACK_LINK);
			if (oWayback.isPresent()) {
				isWayBackLink = oWayback.get().toString();
			} else {
				isWayBackLink = "N/A";
			}
		} catch (InvalidSPDXAnalysisException e) {
			isWayBackLink = "N/A";
		}
		String match;
		try {
			Optional<String> oMatch = getStringPropertyValue(SpdxConstants.PROP_CROSS_REF_MATCH);
	
			if (oMatch.isPresent()) {
				match = oMatch.get();
			} else {
				match = "N/A";
			}
		} catch (InvalidSPDXAnalysisException e) {
			match = "N/A";
		}
		String timestamp;
		try {
			Optional<String> oTimestamp = getStringPropertyValue(SpdxConstants.PROP_CROSS_REF_TIMESTAMP);
			if (oTimestamp.isPresent()) {
				timestamp = oTimestamp.get();
			} else {
				timestamp = "N/A";
			}
		} catch (InvalidSPDXAnalysisException e) {
			timestamp = "N/A";
		}
		String crossRefDetails = String.format("{%s: %s,%s: %s,%s: %s,%s: %s,%s: %s,%s: %s}",
				SpdxConstants.PROP_CROSS_REF_URL, url,
				SpdxConstants.PROP_CROSS_REF_IS_VALID, isValid,
				SpdxConstants.PROP_CROSS_REF_IS_LIVE, isLive,
				SpdxConstants.PROP_CROSS_REF_WAYBACK_LINK, isWayBackLink,
				SpdxConstants.PROP_CROSS_REF_MATCH, match,
				SpdxConstants.PROP_CROSS_REF_TIMESTAMP, timestamp);
		return crossRefDetails;
	}
	
	public static class CrossRefBuilder {
		// required fields - Model Object
		IModelStore modelStore;
		String documentUri;
		String id;
		ModelCopyManager copyManager;
		
		// required fields
		String url;
		
		// optional fields
		String match;
		Boolean valid;
		Boolean live;
		String timestamp;
		Boolean wayBackLink;
		Integer order;
		
		/**
		 * Create a CrossRef with the required parameters
		 * @param modelStore Storage for the model objects
		 * @param documentUri SPDX Document URI for a document associated with this model
		 * @param id ID for this object - must be unique within the SPDX document
		 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
		 * @param url URL for the CrossRef
		 */
		public CrossRefBuilder(IModelStore modelStore, String documentUri, String id, 
				@Nullable ModelCopyManager copyManager, String url) {
			Objects.requireNonNull(modelStore, "Model store can not be null");
			Objects.requireNonNull(documentUri, "Document URI can not be null");
			Objects.requireNonNull(id, "ID can not be null");
			Objects.requireNonNull(url, "URL can not be null");
			this.modelStore = modelStore;
			this.documentUri = documentUri;
			this.id = id;
			this.url = url;
			this.copyManager = copyManager;
		}
		
		public CrossRefBuilder setMatch(@Nullable String match) {
			this.match = match;
			return this;
		}

		/**
		 * @param url the url to set
		 */
		public CrossRefBuilder setUrl(String url) {
			Objects.requireNonNull(url, "URL must not be null");
			this.url = url;
			return this;
		}

		/**
		 * @param valid the valid to set
		 */
		public CrossRefBuilder setValid(@Nullable Boolean valid) {
			this.valid = valid;
			return this;
		}

		/**
		 * @param live the live to set
		 */
		public CrossRefBuilder setLive(@Nullable Boolean live) {
			this.live = live;
			return this;
		}

		/**
		 * @param timestamp the timestamp to set
		 */
		public CrossRefBuilder setTimestamp(@Nullable String timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		/**
		 * @param wayBackLink the wayBackLink to set
		 */
		public CrossRefBuilder setWayBackLink(@Nullable Boolean wayBackLink) {
			this.wayBackLink = wayBackLink;
			return this;
		}

		/**
		 * @param order the order to set
		 */
		public CrossRefBuilder setOrder(@Nullable Integer order) {
			this.order = order;
			return this;
		}
		
		/**
		 * @return CrossRef built from the supplied parameters
		 * @throws InvalidSPDXAnalysisException
		 */
		public CrossRef build() throws InvalidSPDXAnalysisException {
			IModelStoreLock lock = modelStore.enterCriticalSection(documentUri, false);
			try {
				return new CrossRef(this);
			} finally {
				modelStore.leaveCriticalSection(lock);
			}
		}
	}
}
