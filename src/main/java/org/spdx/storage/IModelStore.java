/**
 * Copyright (c) 2019 Source Auditor Inc.
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
package org.spdx.storage;

import java.util.List;

/**
 * Interface for storing and retrieving SPDX properties for SPDX documents.
 * 
 * The interface uses the SPDX document URI and an ID to identify specific objects stored.
 * 
 * Each object can have property values and property value lists associated with them.  
 * 
 * A property value is an object of a primitive type (e.g. String or Boolean) or is another
 * object which includes it's own ID and must also have a type described in the SPDX model.
 * 
 * A property list is just a list of values.
 * 
 * @author Gary O'Neall
 *
 */
public interface IModelStore {
	
	/**
	 * Different types of ID's
	 */
	public enum IdType {
		LicenseRef, 		// ID's that start with LicenseRef-
		DocumentRef, 		// ID's that start with DocumentRef-
		SpdxId, 			// ID's that start with SpdxRef-
		ListedLicense, 		// ID's associated with listed licenses
		Literal,			// ID's for pre-defined literals (such as NONE, NOASSERTION)
		Anonomous};			// ID's for object only referenced internally

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @return true if the id already exists for the document
	 */
	boolean exists(String documentUri, String id);

	/**
	 * Create a new object with ID
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param type SPDX model type as defined in the CLASS constants in SpdxConstants
	 */
	void create(String documentUri, String id, String type);

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @return Property names for all properties having a value for a given id within a document
	 */
	List<String> getPropertyValueNames(String documentUri, String id);

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @return Property names for all properties have a value list for a given id within a document
	 */
	List<String> getPropertyValueListNames(String documentUri, String id);

	/**
	 * Sets the value for a property to a Model Object with a valueId and type
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @param valueId The ID for the value
	 * @param type the SPDX class name for the type
	 */
	void setTypedValue(String documentUri, String id, String propertyName, String valueId, String type);

	/**
	 * Sets a property value for a String or Boolean type of value
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @param value value to set
	 */
	void setPrimitiveValue(String documentUri, String id, String propertyName, Object value);

	/**
	 * Sets the value list for the property to an empty list
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 */
	void clearPropertyValueList(String documentUri, String id, String propertyName);

	/**
	 * Adds a value to a value list for a property to a Model Object with a valueId and type
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @param valueId The ID for the value
	 * @param type the SPDX class name for the type
	 */
	void addTypedValueToList(String documentUri, String id, String propertyName, String valueId, String type);

	/**
	 * Adds a value to a property list for a String or Boolean type of value
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @param value value to set
	 */
	void addPrimitiveValueToList(String documentUri, String id, String propertyName, Object value);

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @return List of values associated with the id, propertyName and document
	 */
	List<?> getValueList(String documentUri, String id, String propertyName);

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @return the single value associated with the id, propertyName and document
	 */
	Object getValue(String documentUri, String id, String propertyName);

	/**
	 * Generate a unique ID for use within the document
	 * @param idType Type of ID
	 * @param documentUri the SPDX Document URI
	 * @return
	 */
	String getNextId(IdType idType, String documentUri);
}
