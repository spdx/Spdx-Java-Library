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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.ModelObject;

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
	
	@FunctionalInterface
	public static interface ModelUpdate {
		void apply() throws InvalidSPDXAnalysisException;
	}
	
	public enum ReadWrite {
		READ,
		WRITE,
		READ_WRITE
	}
	
	/**
	 * Transaction to provide ACID properties around modifications made between begin and commit
	 *
	 */
	public interface ModelTransaction extends AutoCloseable {
		/**
		 * @param readWrite
		 * @throws IOException
		 */
		void begin(ReadWrite readWrite) throws IOException;
		void commit() throws IOException;
		void close() throws IOException;
	}
	
	/**
	 * Different types of ID's
	 */
	public enum IdType {
		LicenseRef, 		// ID's that start with LicenseRef-
		DocumentRef, 		// ID's that start with DocumentRef-
		SpdxId, 			// ID's that start with SpdxRef-
		ListedLicense, 		// ID's associated with listed licenses
		Literal,			// ID's for pre-defined literals (such as NONE, NOASSERTION)
		Anonomous, 			// ID's for object only referenced internally
		Unkown};			// ID's that just don't fit any pattern

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @return true if the id already exists for the document
	 */
	public boolean exists(String documentUri, String id);

	/**
	 * Create a new object with ID
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param type SPDX model type as defined in the CLASS constants in SpdxConstants
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void create(String documentUri, String id, String type) throws InvalidSPDXAnalysisException;

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @return Property names for all properties having a value for a given id within a document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public List<String> getPropertyValueNames(String documentUri, String id) throws InvalidSPDXAnalysisException;

	/**
	 * Sets a property value for a String or Boolean type of value creating the propertyName if it does not exist
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @param value value to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setValue(String documentUri, String id, String propertyName, Object value) throws InvalidSPDXAnalysisException;

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @return the single value associated with the id, propertyName and document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public Optional<Object> getValue(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException;

	/**
	 * Generate a unique ID for use within the document
	 * @param idType Type of ID
	 * @param documentUri the SPDX Document URI
	 * @return next available unique ID for the specific idType
	 * @throws InvalidSPDXAnalysisException 
	 */
	public String getNextId(IdType idType, String documentUri) throws InvalidSPDXAnalysisException;
	
	/**
	 * Removes a property from the document for the given ID if the property exists.  Does not raise any exception if the propertyName does not exist
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @throws InvalidSPDXAnalysisException
	 */
	public void removeProperty(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException;

	/**
	 * @return a list of all Document URI's stored in the model store
	 */
	public List<String> getDocumentUris();

	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for a document associated with this model
	 * @param typeFilter Optional parameter to specify the type of objects to be retrieved
	 * @return Stream of all items store within the document
	 * @throws InvalidSPDXAnalysisException
	 */
	public Stream<? extends ModelObject> getAllItems(String documentUri, String typeFilter) throws InvalidSPDXAnalysisException;

	/**
	 * Initiates a transaction
	 * @param readWrite signal if any writes or updates is expected
	 * @return transaction object to call commit and close when the transaction is complete
	 * @throws IOException 
	 */
	public ModelTransaction beginTransaction(ReadWrite readWrite) throws IOException;

	/**
	 * Removes a value from a collection of values associated with a property
	 * @param documentUri Unique document URI
	 * @param id ID of the item to associate the property with
	 * @param propertyName name of the property
	 * @param value Value to be removed
	 * @return 
	 */
	public boolean removeValueFromCollection(String documentUri, String id, String propertyName, Object value) throws InvalidSPDXAnalysisException;

	/**
	 * @param documentUri Unique document URI
	 * @param id ID of the item to associate the property with
	 * @param propertyName name of the property
	 * @return size of a collection associated with a property.  0 if the property does not exist.
	 */
	public int collectionSize(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException;

	/**
	 * @param documentUri Unique document URI
	 * @param id ID of the item to associate the property with
	 * @param propertyName name of the property
	 * @param value
	 * @return true if the collection associated with a property contains the value
	 */
	public boolean collectionContains(String documentUri, String id, String propertyName, Object value) throws InvalidSPDXAnalysisException;
	
	/**
	 * Sets the value collection for the property to an empty collection creating the propertyName if it does not exist
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @throws InvalidSPDXAnalysisException 
	 */
	void clearValueCollection(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException;

	/**
	 * Adds a value to a property collection creating the propertyName if it does not exist
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @param value value to add
	 * @return true if the collection was modified
	 * @throws InvalidSPDXAnalysisException 
	 */
	public boolean addValueToCollection(String documentUri, String id, String propertyName, Object value) throws InvalidSPDXAnalysisException;

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @return List of values associated with the id, propertyName and document
	 * @throws InvalidSPDXAnalysisException 
	 */
	public List<Object> getValueList(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException;

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @param clazz Class to test compatibility with
	 * @return true if all members of a collection associated with the id and propertyName can be assigned to the clazz
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean isCollectionMembersAssignableTo(String documentUri, String id, String propertyName, Class<?> clazz) throws InvalidSPDXAnalysisException;
	
	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @param clazz Class to test compatibility with
	 * @return true if the value associated with the id and propertyName can be assigned to the clazz
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean isPropertyValueAssignableTo(String documentUri, String id, String propertyName, Class<?> clazz) throws InvalidSPDXAnalysisException;

	/**
	 * @param documentUri the SPDX Document URI
	 * @param id unique ID within the SPDX document
	 * @param propertyName Name of the property
	 * @return true if the propertyName represents multiple values
	 */
	public boolean isCollectionProperty(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException;

	/**
	 * @param id
	 * @return The type of ID based on the string format
	 */
	public IdType getIdType(String id);
}
