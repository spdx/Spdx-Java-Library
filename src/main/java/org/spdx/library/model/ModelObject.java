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
package org.spdx.library.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.license.ListedLicenses;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * @author Gary O'Neall
 * 
 * Superclass for all SPDX model objects
 * 
 * Provides the primary interface to the storage class that access and stores the data for 
 * the model objects.
 *
 */
public abstract class ModelObject implements SpdxConstants {

	private IModelStore modelStore;
	private String documentUri;
	private String id;
	/**
	 * Map of ID's copied from other model stores for efficiency
	 */
	private Map<IModelStore, Map<String, String>> idMap = new HashMap<>();

	/**
	 * @param modelStore Storage for the model objects
	 * @param documentUri SPDX Document URI for the document associated with this model
	 * @param id ID for this object - must be unique within the SPDX document
	 * @param create - if true, the object will be created in the store if it is not already present
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelObject(IModelStore modelStore, String documentUri, String id, boolean create) throws InvalidSPDXAnalysisException {
		this.modelStore = modelStore;
		this.documentUri = documentUri;
		this.id = id;
		if (modelStore == null) {
			throw new InvalidSPDXAnalysisException("Missing required model store") ;
		}
		if (!modelStore.exists(documentUri, id)) {
			if (create) {
				modelStore.create(documentUri, id, getType());
			} else {
				throw new SpdxIdNotFoundException(id+" does not exist in document "+documentUri);
			}
		}
	}
	
	// Abstract methods that must be implemented in the subclasses
	/**
	 * @return The class name for this object.  Class names are defined in the constants file
	 */
	public abstract String getType();
	
	/**
	 * @return Any verification errors or warnings associated with this object
	 */
	public abstract List<String> verify();
	
	/**
	 * @return the Document URI for this object
	 */
	public String getDocumentUri() {
		return this.documentUri;
	}
	
	/**
	 * @return ID for the object
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the model store for this object
	 */
	public IModelStore getModelStore() {
		return this.modelStore;
	}
	
	//The following methods are to manage the properties associated with the model object
	/**
	 * @return all names of property values currently associated with this object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public List<String> getPropertyValueNames() throws InvalidSPDXAnalysisException {
		return modelStore.getPropertyValueNames(documentUri, id);
	}
	
	/**
	 * @return all names of property lists currently associated with this object
	 * @throws InvalidSPDXAnalysisException 
	 */
	public List<String> getPropertyValueListNames() throws InvalidSPDXAnalysisException {
		return modelStore.getPropertyValueListNames(documentUri, id);
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return value associated with a property
	 */
	public Object getObjectPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		return modelStore.getValue(documentUri, id, propertyName);
	}

	/**
	 * @param propertyName Name of the proprety associated with this object
	 * @param value Value to associate with the property
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setPropertyValue(String propertyName, Object value) throws InvalidSPDXAnalysisException {
		//TODO: Add nullable annotation, make sure the storage class handles null values
		if (value instanceof ModelObject) {
			modelStore.setTypedValue(documentUri, id, propertyName, 
					modelObjectToId((ModelObject)value), ((ModelObject)value).getType());
		} else {
			modelStore.setPrimitiveValue(documentUri, id, propertyName, value);
		}	
	}
	
	/**
	 * @param propertyName Name of a property
	 * @return the String value associated with a property
	 * @throws SpdxInvalidTypeException
	 */
	public String getStringPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Object ovalue = getObjectPropertyValue(propertyName);
		if (ovalue == null) {
			return null;
		}
		if (!(ovalue instanceof String)) {
			throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type String");
		}
		return (String)ovalue;
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return the Boolean value for a property
	 * @throws SpdxInvalidTypeException
	 */
	public Boolean getBooleanPropertyValue(String propertyName) throws InvalidSPDXAnalysisException {
		Object ovalue = getObjectPropertyValue(propertyName);
		if (ovalue == null) {
			return null;	// Note that some of the properties such as FsfLibre explicitly look for null values
		}
		if (!(ovalue instanceof Boolean)) {
			throw new SpdxInvalidTypeException("Property "+propertyName+" is not of type Boolean");
		}
		return (Boolean)ovalue;
	}
	
	// The following methods manage lists of values associated with a property
	/**
	 * Clears a list of values associated with a property
	 * @param propertyName Name of the property
	 */
	public void clearPropertyValueList(String propertyName) throws InvalidSPDXAnalysisException {
		modelStore.clearPropertyValueList(documentUri, id, propertyName);
	}
	
	/**
	 * Add a value to a list of values associated with a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param propertyName  Name of the property
	 * @param value to add
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void addPropertyValueToList(String propertyName, Object value) throws InvalidSPDXAnalysisException {
		if (value instanceof ModelObject) {
			modelStore.addTypedValueToList(documentUri, id, propertyName, 
					modelObjectToId((ModelObject)value), ((ModelObject)value).getType());
		} else {
			modelStore.addPrimitiveValueToList(documentUri, id, propertyName, value);
		}
	}
	
	/**
	 * Replace the entire value list for a property.  If a value is a ModelObject and does not
	 * belong to the document, it will be copied into the object store
	 * @param propertyName name of the property
	 * @param values list of new properties
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void replacePropertyValueList(String propertyName, List<?> values) throws InvalidSPDXAnalysisException {
		clearPropertyValueList(propertyName);
		for (Object value:values) {
			addPropertyValueToList(propertyName, value);
		}
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return List of values associated with a property
	 */
	public List<?> getObjectPropertyValueList(String propertyName) throws InvalidSPDXAnalysisException {
		return modelStore.getValueList(documentUri, id, propertyName);
	}
	
	/**
	 * @param propertyName Name of property
	 * @return List of Strings associated with the property
	 * @throws SpdxInvalidTypeException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getStringPropertyValueList(String propertyName) throws InvalidSPDXAnalysisException {
		List<?> oList = getObjectPropertyValueList(propertyName);
		if (oList == null) {
			return null;
		}
		if (oList.size() > 0 && (!(oList.get(0) instanceof String))) {
			throw new SpdxInvalidTypeException("Property "+propertyName+" does not contain a list of Strings");
		}
		return (List<String>)oList;
	}
	
	/**
	 * @param compare
	 * @return true if all the properties have the same or equivalent values
	 */
	public boolean equivalent(ModelObject compare) throws InvalidSPDXAnalysisException {
		if (!this.getClass().equals(compare.getClass())) {
			return false;
		}
		List<String> propertyValueNames = getPropertyValueNames();
		List<String> comparePropertyValueNames = new ArrayList<String>(compare.getPropertyValueNames());	// create a copy since we're going to modify it
		for (String propertyName:propertyValueNames) {
			if (comparePropertyValueNames.contains(propertyName)) {
				if (!Objects.equals(this.getObjectPropertyValue(propertyName), compare.getObjectPropertyValue(propertyName))) {
					return false;
				}
				comparePropertyValueNames.remove(propertyName);
			} else {
				// No property value
				if (this.getObjectPropertyValue(propertyName) != null) {
					return false;
				}
			}
		}
		for (String propertyName:comparePropertyValueNames) {
			if (compare.getObjectPropertyValue(propertyName) != null) {
				return false;
			}
		}
		List<String> propertyValueListNames = getPropertyValueListNames();
		List<String> comparePropertyValueListNames = new ArrayList<String>(compare.getPropertyValueListNames());	// create a copy since we're going to modify it
		for (String propertyName:propertyValueListNames) {
			if (comparePropertyValueListNames.contains(propertyName)) {
				List<?> myList = getObjectPropertyValueList(propertyName);
				List<?> compList = compare.getObjectPropertyValueList(propertyName);
				for (Object item:myList) {
					if (!compList.contains(item)) {
						return false;
					}
				}
				comparePropertyValueNames.remove(propertyName);
			} else {
				// No property value
				if (!this.getObjectPropertyValueList(propertyName).isEmpty()) {
					return false;
				}
			}
		}
		for (String propertyName:comparePropertyValueListNames) {
			if (!compare.getObjectPropertyValueList(propertyName).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (this.id != null) {
			return this.id.toLowerCase().hashCode() ^ this.documentUri.hashCode();
		} else {
			return 0;
		}
	}
	/* (non-Javadoc)
	 * @see org.spdx.rdfparser.license.AnyLicenseInfo#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ModelObject)) {
			// covers o == null, as null is not an instance of anything
			return false;
		}
		ModelObject comp = (ModelObject)o;
		return Objects.equals(id, comp.getId()) && Objects.equals(documentUri, comp.getDocumentUri());
	}
	

	
	public Object clone() {
		//TODO Implement - I'm not sure this is even needed in the new design since the objects are stateless
		throw new RuntimeException("Unimplemented funtion");
	}
	
	/**
	 * Copy all the properties from the source object
	 * @param source
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void copyFrom(ModelObject source) throws InvalidSPDXAnalysisException {
		List<String> propertyValueNames = source.getPropertyValueNames();
		for (String propertyName:propertyValueNames) {
			setPropertyValue(propertyName, source.getObjectPropertyValue(propertyName));
		}
		List<String> propertyValueListNames = source.getPropertyValueListNames();
		for (String propertyName:propertyValueListNames) {
			replacePropertyValueList(propertyName, source.getObjectPropertyValueList(propertyName));
		}
	}
	
	/**
	 * Translates a model object into an ID taking into account the modelObject may be from
	 * a different model store and needs to be made referenceable within the model store
	 * associated with this model object
	 * @param modelObject
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private String modelObjectToId(ModelObject modelObject) throws InvalidSPDXAnalysisException {
		//TODO: Make threadsafe
		if (this.getModelStore().equals(modelObject.getModelStore()) && this.getDocumentUri().equals(modelObject.getDocumentUri())) {
			return modelObject.getId();
		} else {
			// Need to find or create a duplicate stored in this model store
			// We keep track of any ID's we created in the idMap
			Map<String, String> mapForModelStore = this.idMap.get(modelObject.getModelStore());
			if (mapForModelStore == null) {
				mapForModelStore = new HashMap<>();
			}
			String retval = mapForModelStore.get(modelObject.getId());
			if (retval != null) {
				return retval;
			} else {
				retval = modelStore.getNextId(idToIdType(modelObject.getId()), documentUri);
				SpdxModelFactory.createModelObject(modelStore, documentUri, retval, modelObject.getType()).copyFrom(modelObject);
			}
			mapForModelStore.put(modelObject.getId(), retval);
			return retval;
		}
	}
	
	/**
	 * @param id String for the object
	 * @return type of the ID
	 */
	IdType idToIdType(String id) {
		if (id.startsWith(NON_STD_LICENSE_ID_PRENUM)) {
			return IdType.LicenseRef;
		} else if (id.startsWith(SPDX_ELEMENT_REF_PRENUM)) {
			return IdType.SpdxId;
		} else if (id.startsWith(EXTERNAL_DOC_REF_PRENUM)) {
			return IdType.DocumentRef;
		} else if (ListedLicenses.getListedLicenses().isSpdxListedLicenseId(id)) {
			return IdType.ListedLicense;
		} else if ("none".equals(id) || "noassertion".equals(id)) {
			return IdType.Literal;
		} else {
			return IdType.Anonomous;
		}
	}
}
