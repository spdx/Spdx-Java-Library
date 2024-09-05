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

package org.spdx.storage.listedlicense;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.InvalidSpdxPropertyException;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.CrossRef;
import org.spdx.storage.PropertyDescriptor;

/**
 * JSON Representation of a CrossRef
 * 
 * @author Gary O'Neall
 *
 */
class CrossRefJson {

	public String match;
	public String url;
	public Boolean isValid;
	public Boolean isLive;
	public String timestamp;
	public Boolean isWayBackLink;
	public Integer order;
	private transient String id;	// The ID is always transient and of anonymous type
	
	public CrossRefJson() {
		// empty constructor so GSON will work
	}
	
	/**
	 * @param crossRef cross ref to copy values from
	 * @throws InvalidSPDXAnalysisException 
	 */
	public CrossRefJson(CrossRef crossRef) throws InvalidSPDXAnalysisException {
		this.id = crossRef.getId();
		Optional<String> fromMatch = crossRef.getMatch();
		if (fromMatch.isPresent()) {
			match = fromMatch.get();
		}
		Optional<String> fromUrl = crossRef.getUrl();
		if (fromUrl.isPresent()) {
			url = fromUrl.get();
		}
		Optional<Boolean> fromIsValid = crossRef.getValid();
		if (fromIsValid.isPresent()) {
			isValid = fromIsValid.get();
		}
		Optional<Boolean> fromIsLive = crossRef.getLive();
		if (fromIsLive.isPresent()) {
			isLive = fromIsLive.get();
		}
		Optional<Boolean> fromIsWayBackLink = crossRef.getIsWayBackLink();
		if (fromIsWayBackLink.isPresent()) {
			isWayBackLink = fromIsWayBackLink.get();
		}
		Optional<String> fromTimestamp = crossRef.getTimestamp();
		if (fromTimestamp.isPresent()) {
			timestamp = fromTimestamp.get();
		}
		Optional<Integer> fromOrder = crossRef.getOrder();
		if (fromOrder.isPresent()) {
			order = fromOrder.get();
		}
	}
	
	/**
	 * @return all valid property descriptors
	 */
	public List<PropertyDescriptor> getPropertyValueDescriptors() {
		List<PropertyDescriptor> retval = new ArrayList<PropertyDescriptor>();
		if (Objects.nonNull(match)) {
			retval.add(new PropertyDescriptor("match", SpdxConstantsCompatV2.SPDX_NAMESPACE));
		}
		if (Objects.nonNull(url)) {
			retval.add(new PropertyDescriptor("url", SpdxConstantsCompatV2.SPDX_NAMESPACE));
		}
		if (Objects.nonNull(isValid)) {
			retval.add(new PropertyDescriptor("isValid", SpdxConstantsCompatV2.SPDX_NAMESPACE));
		}
		if (Objects.nonNull(isLive)) {
			retval.add(new PropertyDescriptor("isLive", SpdxConstantsCompatV2.SPDX_NAMESPACE));
		}
		if (Objects.nonNull(timestamp)) {
			retval.add(new PropertyDescriptor("timestamp", SpdxConstantsCompatV2.SPDX_NAMESPACE));
		}
		if (Objects.nonNull(isWayBackLink)) {
			retval.add(new PropertyDescriptor("isWayBackLink", SpdxConstantsCompatV2.SPDX_NAMESPACE));
		}
		if (Objects.nonNull(order)) {
			retval.add(new PropertyDescriptor("order", SpdxConstantsCompatV2.SPDX_NAMESPACE));
		}
		return retval;
	}

	/**
	 * Sets the value to the property name
	 * @param propertyDescriptor
	 * @param value
	 * @throws InvalidSpdxPropertyException
	 */
	public void setPrimativeValue(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		switch (propertyDescriptor.getName()) {
			case "match": if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				this.match = (String)value;
			break;
			case "url": if (!(value instanceof String)) {
						throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
					}
				this.url = (String)value;
				break;
			case "timestamp": if (!(value instanceof String)) {
					throw new InvalidSpdxPropertyException("Expected string type for "+propertyDescriptor);
				}
				this.timestamp = (String)value;
				break;
			case "isValid": if (!(value instanceof Boolean)) {
					throw new InvalidSpdxPropertyException("Expected boolean type for "+propertyDescriptor);
				}
				this.isValid = (Boolean)value;
				break;
			case "isLive": if (!(value instanceof Boolean)) {
					throw new InvalidSpdxPropertyException("Expected boolean type for "+propertyDescriptor);
				}
				this.isLive = (Boolean)value;
				break;
			case "isWayBackLink": if (!(value instanceof Boolean)) {
					throw new InvalidSpdxPropertyException("Expected boolean type for "+propertyDescriptor);
				}
				this.isWayBackLink = (Boolean)value;
				break;
			case "order": if (!(value instanceof Integer)) {
					throw new InvalidSpdxPropertyException("Expected integer type for "+propertyDescriptor);
				}
				this.order = (Integer)value;
				break;
			default: throw new InvalidSpdxPropertyException("Invalid property for CrossRef:"+propertyDescriptor);
		}
	}

	public void clearPropertyValueList(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException(propertyDescriptor + " is not a list type.");
	}

	public boolean addPrimitiveValueToList(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException(propertyDescriptor + " is not a list type.");
	}

	public boolean removePrimitiveValueToList(PropertyDescriptor propertyDescriptor, Object value) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException(propertyDescriptor + " is not a list type.");
	}

	public @Nullable String getId() {
		return this.id;
	}

	public void setId(String crossRefId) {
		this.id = crossRefId;
	}

	/**
	 * @param propertyDescriptor
	 * @return the list associated with the property
	 * @throws InvalidSpdxPropertyException
	 */
	public List<?> getValueList(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		throw new InvalidSpdxPropertyException(propertyDescriptor + " is not a list type.");
	}

	/**
	 * @param propertyDescriptor
	 * @return the value associated with the property - null if not assigned or not present
	 * @throws InvalidSpdxPropertyException
	 */
	public @Nullable Object getValue(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		switch (propertyDescriptor.getName()) {
			case "match": return this.match;
			case "url": return this.url;
			case "isValid": return this.isValid;
			case "isLive": return this.isLive;
			case "timestamp": return this.timestamp;
			case "isWayBackLink": return this.isWayBackLink;
			case "order": return this.order;
			default: throw new InvalidSpdxPropertyException("Invalid property for CrossRef:"+propertyDescriptor);
		}
	}

	/**
	 * sets the property to null (no way to remove in this store)
	 * @param propertyDescriptor
	 * @throws InvalidSpdxPropertyException
	 */
	public void removeProperty(PropertyDescriptor propertyDescriptor) throws InvalidSpdxPropertyException {
		switch (propertyDescriptor.getName()) {
			case "match": this.match = null; break;
			case "url": this.url = null; break;
			case "isValid": this.isValid = null; break;
			case "isLive": this.isLive = null; break;
			case "timestamp": this.timestamp = null; break;
			case "isWayBackLink": this.isWayBackLink = null; break;
			case "order": this.order = null; break;
			default: throw new InvalidSpdxPropertyException("Invalid property for CrossRef:"+propertyDescriptor);
		}
	}

	/**
	 * @param propertyDescriptor
	 * @param clazz
	 * @return true if the members can be assigned from clazz
	 */
	public boolean isCollectionMembersAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) {
		return false;
	}

	/**
	 * @param propertyDescriptor
	 * @param clazz
	 * @return true if the property can be assigned from clazz
	 * @throws InvalidSpdxPropertyException
	 */
	public boolean isPropertyValueAssignableTo(PropertyDescriptor propertyDescriptor, Class<?> clazz) throws InvalidSpdxPropertyException {
		switch (propertyDescriptor.getName()) {
			case "match":
			case "url":
			case "timestamp": return String.class.isAssignableFrom(clazz);
			case "isValid":
			case "isLive":
			case "isWayBackLink": return Boolean.class.isAssignableFrom(clazz);
			case "order": return Integer.class.isAssignableFrom(clazz);
			default: throw new InvalidSpdxPropertyException("Invalid property for CrossRef:"+propertyDescriptor);
	}

	}

	/**
	 * @param propertyName
	 * @return if the property is a colloection
	 */
	public boolean isCollectionProperty(String propertyName) {
		return false;
	}
}