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
package org.spdx.storage.listedlicense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.DuplicateSpdxIdException;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.SpdxIdNotFoundException;
import org.spdx.core.TypedValue;
import org.spdx.library.model.v2.ModelObjectV2;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.license.SpdxListedLicenseException;
import org.spdx.library.model.v3_0_0.SpdxConstantsV3;
import org.spdx.storage.PropertyDescriptor;

import com.google.gson.Gson;

/**
 * Read-only model store for the SPDX listed licenses
 * 
 * License and exception ID's can be case insensitive
 * 
 * License information is stored in a LicenseJson file with the ID being Listed License ID
 * License Exception information is stored in an ExceptionJson file with the ID being the Listed Exception ID
 * CrossRef information is stored within the LicenseJson file.  Id's are anonymous and generated. 
 * 
 * @author Gary O'Neall
 *
 */
public abstract class SpdxListedLicenseModelStore implements IListedLicenseStore {
	
	static final Logger logger = LoggerFactory.getLogger(SpdxListedLicenseModelStore.class.getName());
	static final String DEFAULT_LICENSE_LIST_VERSION = "3.24";
	static final String LICENSE_TOC_FILENAME = "licenses.json";
	static final String EXCEPTION_TOC_FILENAME = "exceptions.json";
	static final String JSON_SUFFIX = ".json";
	private static final String ANONYMOUS_ID_PREFIX = "SpdxLicenseGeneratedId-";
	public static final String LISTED_LICENSE_NAMESPACE = SpdxConstantsCompatV2.LISTED_LICENSE_NAMESPACE_PREFIX;
	
	/**
	 * Map of lower case to correct case license IDs
	 */
	Map<String, String> licenseIds = new HashMap<>();
	/**
	 * Map of lower case to correct case exception IDs
	 */
	Map<String, String> exceptionIds = new HashMap<>();
	Map<String, LicenseJson> listedLicenseCache = null;
	Map<String, ExceptionJson> listedExceptionCache = null;
	Map<String, CrossRefJson> crossRefs = new HashMap<>();
	String licenseListVersion = DEFAULT_LICENSE_LIST_VERSION;
	String licenseListReleaseDate = new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT).format(new Date());
	LicenseCreationInfo licenseCreationInfo;
	LicenseCreatorAgent licenseCreator;
	
	private int nextId=0;
	private final ReadWriteLock listedLicenseModificationLock = new ReentrantReadWriteLock();
	
	private final IModelStoreLock readLock = new IModelStoreLock() {

		@Override
		public void unlock() {
			listedLicenseModificationLock.readLock().unlock();
		}
		
	};
	
	private final IModelStoreLock writeLock = new IModelStoreLock() {

		@Override
		public void unlock() {
			listedLicenseModificationLock.writeLock().unlock();
		}
		
	};

	Gson gson = new Gson();	// we should be able to reuse since all access is within write locks
	
	public static String objectUriToLicenseOrExceptionId(String objectUri) {
		return objectUri.substring(LISTED_LICENSE_NAMESPACE.length()+1);
	}
	
	public static String licenseOrExceptionIdToObjectUri(String id) {
		return LISTED_LICENSE_NAMESPACE + id;
	}
	
	/**
	 * @param majorSpecVersion Major spec version - the store will work with either SPDX 3 or SPDX 2 major version of the spec
	 * @throws InvalidSPDXAnalysisException on error loading ids
	 */
	public SpdxListedLicenseModelStore() throws InvalidSPDXAnalysisException {
		loadIds();
		licenseCreator = new LicenseCreatorAgent(this.getLicenseListVersion());
		licenseCreationInfo = new LicenseCreationInfo(licenseCreator, this.getLicenseListReleaseDate());
	}
	
	/**
	 * @return InputStream for the Table of Contents of the licenses formated in JSON SPDX
	 * @throws IOException
	 */
	public abstract InputStream getTocInputStream() throws IOException;
	
	/**
	 * @return InputStream for the Table of Contents of the exceptions formated in JSON SPDX
	 * @throws IOException
	 */
	public abstract InputStream getExceptionTocInputStream() throws IOException;
	
	/**
	 * @return InputStream for a license formated in SPDX JSON
	 * @throws IOException
	 */
	public abstract InputStream getLicenseInputStream(String licenseId) throws IOException;
	
	/**
	 * @return InputStream for an exception formated in SPDX JSON
	 * @throws IOException
	 */
	public abstract InputStream getExceptionInputStream(String exceptionId) throws IOException;

	/**
	 * Loads all license and exception ID's from the appropriate JSON files
	 * @throws InvalidSPDXAnalysisException
	 */
	private void loadIds() throws InvalidSPDXAnalysisException {
        listedLicenseModificationLock.writeLock().lock();
        try {
            listedLicenseCache = new HashMap<>(); // clear the cache
            listedExceptionCache = new HashMap<>();
            licenseIds = new HashMap<>(); //Clear the listed license IDs to avoid stale licenses.
             //NOTE: This includes deprecated licenses - should this be changed to only return non-deprecated licenses?
            InputStream tocStream = null;
            BufferedReader reader = null;
            try {
            	// read the license IDs
            	tocStream = getTocInputStream();
                reader = new BufferedReader(new InputStreamReader(tocStream, "UTF-8"));
                StringBuilder tocJsonStr = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                	tocJsonStr.append(line);
                }
                LicenseJsonTOC jsonToc = gson.fromJson(tocJsonStr.toString(), LicenseJsonTOC.class);
                licenseIds = jsonToc.getLicenseIds();
                this.licenseListVersion = jsonToc.getLicenseListVersion();
                this.licenseListReleaseDate = jsonToc.getReleaseDate();
                
                // read the exception ID's
                tocStream = getExceptionTocInputStream();
                reader = new BufferedReader(new InputStreamReader(tocStream, "UTF-8"));
                tocJsonStr  = new StringBuilder();
                while((line = reader.readLine()) != null) {
                	tocJsonStr.append(line);
                }
                ExceptionJsonTOC exceptionToc = gson.fromJson(tocJsonStr.toString(), ExceptionJsonTOC.class);
                exceptionIds = exceptionToc.getExceptionIds();
            } catch (MalformedURLException e) {
				throw new SpdxListedLicenseException("License TOC URL invalid", e) ;
			} catch (IOException e) {
				throw new SpdxListedLicenseException("I/O error reading license TOC", e);
			} finally {
            	if (reader != null) {
            		try {
						reader.close();
					} catch (IOException e) {
						logger.warn("Unable to close JSON TOC reader", e);
					}
            	}
            }
        } finally {
            listedLicenseModificationLock.writeLock().unlock();
        }
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#exists(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean exists(String objectUri) {
		Objects.requireNonNull(objectUri, "Object URI can not be null");
		String id;
		if (objectUri.startsWith(LISTED_LICENSE_NAMESPACE)) {
			id = objectUri.substring(LISTED_LICENSE_NAMESPACE.length());
		} else if (getIdType(objectUri) == IdType.Anonymous ||
				LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri) || 
				this.licenseCreator.getObjectUri().equals(objectUri)) {
			id = objectUri;
		} else {
			return false;
		}
		listedLicenseModificationLock.readLock().lock();
		try {
			return this.licenseIds.containsKey(id.toLowerCase()) || 
					this.exceptionIds.containsKey(id.toLowerCase()) ||
					this.crossRefs.containsKey(id) ||
					LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri) ||
					this.licenseCreator.getObjectUri().equals(objectUri);
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	private String objectUriToId(String objectUri) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(objectUri, "Object URI can not be null");
		String id;
		if (objectUri.startsWith(LISTED_LICENSE_NAMESPACE)) {
			id = objectUri.substring(LISTED_LICENSE_NAMESPACE.length());
		} else if (objectUri.startsWith(SpdxConstantsCompatV2.LISTED_LICENSE_URL)) {
			logger.warn("SPDX listed license URL was used instead of the required namespace ('https:' rather than 'http:'");
			id = objectUri.substring(SpdxConstantsCompatV2.LISTED_LICENSE_URL.length());
		} else if (getIdType(objectUri) == IdType.Anonymous || 
				licenseCreator.getObjectUri().equals(objectUri) ||
				LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			id = objectUri;
		} else {
			logger.error("Namespace for SPDX listed licenses is expected to be "+
					LISTED_LICENSE_NAMESPACE + ".  Supplied URI was "+objectUri);
			throw new SpdxIdNotFoundException("Namespace for SPDX listed licenses is expected to be "+
					LISTED_LICENSE_NAMESPACE + ".  Supplied URI was "+objectUri);
		}
		return id;
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#create(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void create(TypedValue typedValue) throws InvalidSPDXAnalysisException {
		boolean isSpdx3 = typedValue.getSpecVersion().startsWith("3.");
		String id = objectUriToId(typedValue.getObjectUri());
		listedLicenseModificationLock.writeLock().lock();
		try {
			if (SpdxConstantsCompatV2.CLASS_CROSS_REF.equals(typedValue.getType())) {
				CrossRefJson crossRef = new CrossRefJson();
				crossRef.setId(id);
				this.crossRefs.put(id, crossRef);
			} else if ((isSpdx3 && SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE.equals(typedValue.getType())) || 
					(!isSpdx3 && SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE.equals(typedValue.getType()))) {
				if (this.licenseIds.containsKey(id.toLowerCase()) || this.exceptionIds.containsKey(id.toLowerCase())) {
					logger.error("Duplicate SPDX ID on create: "+id);
					throw new DuplicateSpdxIdException("ID "+id+" already exists.");
				}
				this.licenseIds.put(id.toLowerCase(), id);
				this.listedLicenseCache.put(id, new LicenseJson(id));
			} else if ((isSpdx3 && SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION.equals(typedValue.getType())) || 
					(!isSpdx3 && SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION.equals(typedValue.getType()))) {
				if (this.licenseIds.containsKey(id.toLowerCase()) || this.exceptionIds.containsKey(id.toLowerCase())) {
					logger.error("Duplicate SPDX ID on create: "+id);
					throw new DuplicateSpdxIdException("ID "+id+" already exists.");
				}
				this.exceptionIds.put(id.toLowerCase(), id);
				this.listedExceptionCache.put(id,  new ExceptionJson(id));
			} else if (this.licenseCreationInfo.getTypedValue().equals(typedValue)) {
				logger.warn("Ignoring the creation of a creationInfo for the listed license store");
			} else if (this.licenseCreator.getTypedValue().equals(typedValue)) {
				logger.warn("Ignoring the creation of the creator for the listed license store");
			}
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getPropertyValueNames(java.lang.String, java.lang.String)
	 */
	@Override
	public List<PropertyDescriptor> getPropertyValueDescriptors(String objectUri) throws InvalidSPDXAnalysisException  {
		String id = objectUriToId(objectUri);
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				LicenseJson license = fetchLicenseJson(licenseIds.get(id.toLowerCase()));
				return license.getPropertyValueDescriptors();
				// NOTE: we're returning both version 2 and version 3 property value descriptors
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				ExceptionJson exc = fetchExceptionJson(exceptionIds.get(id.toLowerCase()));
				return exc.getPropertyValueDescriptors();
			} else if (crossRefs.containsKey(id)) {
				return crossRefs.get(id).getPropertyValueDescriptors();
				// Currently, there is no SPDX 3 support for cross refs
			} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
				return LicenseCreationInfo.ALL_PROPERTY_DESCRIPTORS;
			} else if (licenseCreator.getObjectUri().equals(objectUri)) {
				return LicenseCreatorAgent.ALL_PROPERTY_DESCRIPTORS;
			} else {
				logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
				throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID. crossRef ID nor a listed exception ID");
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
	}

	/**
	 * @param idCaseInsensitive License ID case insensitive
	 * @return License JSON for the ID - reading from the input stream if needed
	 * @throws InvalidSPDXAnalysisException
	 */
	private LicenseJson fetchLicenseJson(String idCaseInsensitive) throws InvalidSPDXAnalysisException {
		String idLowerCase = idCaseInsensitive.toLowerCase();
		String id = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			id = this.licenseIds.get(idLowerCase);
			if (Objects.isNull(id)) {
				logger.error("Attemting to get property values on non-existent ID "+idCaseInsensitive);
				throw new SpdxIdNotFoundException("ID "+idCaseInsensitive+" not found.");
			}
			if (this.listedLicenseCache.containsKey(id)) {
				return this.listedLicenseCache.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		// If we got here, it wasn't in the cache
		listedLicenseModificationLock.writeLock().lock();
		try {
			// have to retest since we were unlocked
			id = this.licenseIds.get(idLowerCase);
			if (Objects.isNull(id)) {
				logger.error("Attemting to get property values on non-existent ID "+idCaseInsensitive);
				throw new SpdxIdNotFoundException("ID "+idCaseInsensitive+" not found.");
			}
			if (!this.listedLicenseCache.containsKey(id)) {
	            InputStream jsonStream = null;
	            BufferedReader reader = null;
	            try {
	            	jsonStream = getLicenseInputStream(id);
	                reader = new BufferedReader(new InputStreamReader(jsonStream, "UTF-8"));
	                StringBuilder licenseJsonStr = new StringBuilder();
	                String line;
	                while((line = reader.readLine()) != null) {
	                	licenseJsonStr.append(line);
	                }
	                LicenseJson license = gson.fromJson(licenseJsonStr.toString(), LicenseJson.class);
	                this.listedLicenseCache.put(id, license);
	            } catch (MalformedURLException e) {
					logger.error("Json license invalid for ID "+id, e);
					throw new SpdxListedLicenseException("JSON license URL invalid for ID "+id, e);
				} catch (IOException e) {
					logger.error("I/O error opening Json license URL", e);
					throw new SpdxListedLicenseException("I/O Error reading license data for ID "+id, e);
				} finally {
	            	if (reader != null) {
	            		try {
							reader.close();
						} catch (IOException e) {
							logger.warn("Unable to close JSON TOC reader", e);
						}
	            	} else if (jsonStream != null) {
	            		try {
							jsonStream.close();
						} catch (IOException e) {
							logger.warn("Unable to close JSON TOC input stream", e);
						}
	            	}
				}
			}
			return this.listedLicenseCache.get(id);
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}
	
	/**
	 * @param idCaseInsensitive Exception ID case insensitive
	 * @return Exception JSON for the ID - reading from the input stream if needed
	 * @throws InvalidSPDXAnalysisException
	 */
	private ExceptionJson fetchExceptionJson(String idCaseInsensitive) throws InvalidSPDXAnalysisException {
		String idLower = idCaseInsensitive.toLowerCase();
		String id = null; // case sensitive ID
		listedLicenseModificationLock.readLock().lock();
		try {
			id = this.exceptionIds.get(idLower);
			if (Objects.isNull(id)) {
				logger.error("Attemting to get property values on non-existent ID "+idCaseInsensitive);
				throw new SpdxIdNotFoundException("ID "+idCaseInsensitive+" not found.");
			}
			if (this.listedExceptionCache.containsKey(id)) {
				return this.listedExceptionCache.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		// If we got here, it wasn't in the cache
		listedLicenseModificationLock.writeLock().lock();
		try {
			// have to retest since we were unlocked
			id = this.exceptionIds.get(idLower);
			if (Objects.isNull(id)) {
				logger.error("Attemting to get property values on non-existent ID "+idCaseInsensitive);
				throw new SpdxIdNotFoundException("ID "+idCaseInsensitive+" not found.");
			}
			if (!this.listedExceptionCache.containsKey(id)) {
	            InputStream jsonStream = null;
	            BufferedReader reader = null;
	            try {
	            	jsonStream = getExceptionInputStream(id);
	                reader = new BufferedReader(new InputStreamReader(jsonStream, "UTF-8"));
	                StringBuilder exceptionJsonStr = new StringBuilder();
	                String line;
	                while((line = reader.readLine()) != null) {
	                	exceptionJsonStr.append(line);
	                }
	                ExceptionJson exc = gson.fromJson(exceptionJsonStr.toString(), ExceptionJson.class);
	                this.listedExceptionCache.put(id, exc);
	            } catch (MalformedURLException e) {
					logger.error("Json license invalid for ID "+id);
					throw new SpdxListedLicenseException("JSON license URL invalid for ID "+id, e);
				} catch (IOException e) {
					logger.error("I/O error opening Json license URL");
					throw new SpdxListedLicenseException("I/O Error reading license data for ID "+id, e);
				} finally {
	            	if (reader != null) {
	            		try {
							reader.close();
						} catch (IOException e) {
							logger.warn("Unable to close JSON TOC reader", e);
						}
	            	} else if (jsonStream != null) {
	            		try {
							jsonStream.close();
						} catch (IOException e) {
							logger.warn("Unable to close JSON TOC input stream", e);
						}
	            	}
				}
			}
			return this.listedExceptionCache.get(id);
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}


	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#setPrimitiveValue(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setValue(String objectUri, PropertyDescriptor propertyDescriptor, Object value)  throws InvalidSPDXAnalysisException  {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			license.setPrimativeValue(propertyDescriptor, value);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			exc.setPrimativeValue(propertyDescriptor, value);
		} else if (Objects.nonNull(crossRef)) {
			crossRef.setPrimativeValue(propertyDescriptor, value);
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			logger.warn("Ignoring the setting of "+propertyDescriptor.getName()+" for license list creation info");
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			logger.warn("Ignoring the setting of "+propertyDescriptor.getName()+" for license list creator info");
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#clearPropertyValueList(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void clearValueCollection(String objectUri, PropertyDescriptor propertyDescriptor)  throws InvalidSPDXAnalysisException  {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			license.clearPropertyValueList(propertyDescriptor);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			exc.clearPropertyValueList(propertyDescriptor);
		} else if (Objects.nonNull(crossRef)) {
			crossRef.clearPropertyValueList(propertyDescriptor);
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			logger.warn("Ignoring the clearing of collection for license list creation info");
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			logger.warn("Ignoring the clearing of collection for license list creator");
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#addPrimitiveValueToList(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean addValueToCollection(String objectUri, PropertyDescriptor propertyDescriptor, Object value)  throws InvalidSPDXAnalysisException  {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			if (SpdxConstantsCompatV2.PROP_CROSS_REF.equals(propertyDescriptor)) {
				if (!(value instanceof TypedValue)) {
					logger.error("Invalid class for CrossRef - expected TypedValue, was supplied type "+value.getClass().toString());
					throw new InvalidSPDXAnalysisException("Invalid type for CrossRef - expected TypedValue, was supplied type "+value.getClass().toString());
				}
				TypedValue tv = (TypedValue)value;
				if (!SpdxConstantsCompatV2.CLASS_CROSS_REF.equals(tv.getType())) {
					logger.error("Invalid type for CrossRef - expected"+SpdxConstantsCompatV2.CLASS_CROSS_REF+", was supplied type "+value.getClass().toString());
					throw new InvalidSPDXAnalysisException("Invalid type for CrossRef - expected"+SpdxConstantsCompatV2.CLASS_CROSS_REF+", was supplied type "+value.getClass().toString());	
				}
				CrossRefJson crj = crossRefs.get(tv.getObjectUri());
				if (Objects.isNull(crj)) {
					logger.error("CrossRef with ID "+tv.getObjectUri()+" does not exist in the store.");
					throw new InvalidSPDXAnalysisException("CrossRef with ID "+tv.getObjectUri()+" does not exist in the store.");	
				}
				return license.addCrossRefValueToList(propertyDescriptor, crj);
			} else {
				return license.addPrimitiveValueToList(propertyDescriptor, value);
			}
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.addPrimitiveValueToList(propertyDescriptor, value);
		} else if (Objects.nonNull(crossRef)) {
			return crossRef.addPrimitiveValueToList(propertyDescriptor, value);
		}  else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			logger.warn("Ignoring the adding to collection for license list creation info");
			return false;
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			logger.warn("Ignoring the adding to collection for license list creator");
			return false;
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}

	@Override
	public boolean removeValueFromCollection(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			if (SpdxConstantsCompatV2.PROP_CROSS_REF.equals(propertyDescriptor)) {
				if (!(value instanceof TypedValue)) {
					logger.error("Invalid class for CrossRef - expected TypedValue, was supplied type "+value.getClass().toString());
					throw new InvalidSPDXAnalysisException("Invalid type for CrossRef - expected TypedValue, was supplied type "+value.getClass().toString());
				}
				TypedValue tv = (TypedValue)value;
				if (!SpdxConstantsCompatV2.CLASS_CROSS_REF.equals(tv.getType())) {
					logger.error("Invalid type for CrossRef - expected"+SpdxConstantsCompatV2.CLASS_CROSS_REF+", was supplied type "+value.getClass().toString());
					throw new InvalidSPDXAnalysisException("Invalid type for CrossRef - expected"+SpdxConstantsCompatV2.CLASS_CROSS_REF+", was supplied type "+value.getClass().toString());	
				}
				CrossRefJson crj = crossRefs.get(tv.getObjectUri());
				if (Objects.isNull(crj)) {
					logger.error("CrossRef with ID "+tv.getObjectUri()+" does not exist in the store.");
					throw new InvalidSPDXAnalysisException("CrossRef with ID "+tv.getObjectUri()+" does not exist in the store.");	
				}
				return license.removePrimitiveValueToList(propertyDescriptor, crj);
			} else {
				return license.removePrimitiveValueToList(propertyDescriptor, value);
			}
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.removePrimitiveValueToList(propertyDescriptor, value);
		} else if (Objects.nonNull(crossRef)) {
			return crossRef.removePrimitiveValueToList(propertyDescriptor, value);
		}  else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			logger.warn("Ignoring the removing from collection for license list creation info");
			return false;
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			logger.warn("Ignoring the removing from collection for license list creator");
			return false;
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getValueList(java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Object> listValues(String objectUri, PropertyDescriptor propertyDescriptor)  throws InvalidSPDXAnalysisException  {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			List<Object> valueList = (List<Object>)(List<?>)license.getValueList(propertyDescriptor);
			if (SpdxConstantsCompatV2.PROP_CROSS_REF.equals(propertyDescriptor)) {
				final Iterator<Object> crossRefJsonIter = valueList.iterator();
				return new Iterator<Object>() {

					@Override
					public boolean hasNext() {
						return crossRefJsonIter.hasNext();
					}

					@Override
					public Object next() {
						Object nextVal = crossRefJsonIter.next();
						if (Objects.isNull(nextVal)) {
							return null;
						}
						if (!(nextVal instanceof CrossRefJson)) {
							throw new RuntimeException(new InvalidSPDXAnalysisException("Invalid type for "+propertyDescriptor+".  Must be of type CrossRefJson"));
						}
						CrossRefJson nextCrossRef = (CrossRefJson)nextVal;
						String crossRefId = nextCrossRef.getId();
						listedLicenseModificationLock.writeLock().lock();
						try {
							if (Objects.isNull(crossRefId)) {
								// Need to create an ID and store it in the cache
								try {
									crossRefId = getNextId(IdType.Anonymous);
								} catch (InvalidSPDXAnalysisException e) {
									logger.error("Error getting next Anonymous ID",e);
									throw new RuntimeException(e);
								}
								nextCrossRef.setId(crossRefId);
								crossRefs.put(crossRefId, nextCrossRef);
							}
						} finally {
							listedLicenseModificationLock.writeLock().unlock();
						}
						try {
							return new TypedValue(crossRefId, SpdxConstantsCompatV2.CLASS_CROSS_REF, ModelObjectV2.LATEST_SPDX_2_VERSION);
						} catch (InvalidSPDXAnalysisException e) {
							logger.error("Error creating TypedValue for CrossRef",e);
							throw new RuntimeException(e);
						}
					}
				};
			} else {
				return valueList.iterator();
			}			
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return ((List<Object>)(List<?>)exc.getValueList(propertyDescriptor)).iterator();
		} else if (Objects.nonNull(crossRef)) {
			return ((List<Object>)(List<?>)crossRef.getValueList(propertyDescriptor)).iterator();
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			return ((List<Object>)(List<?>)licenseCreationInfo.getValueList(propertyDescriptor)).iterator();
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			return ((List<Object>)(List<?>)licenseCreator.getValueList(propertyDescriptor)).iterator();
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<Object> getValue(String objectUri, PropertyDescriptor propertyDescriptor)  throws InvalidSPDXAnalysisException  {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (SpdxConstantsV3.PROP_CREATION_INFO.equals(propertyDescriptor) && (isLicenseId || isExceptionId)) {
			return Optional.of(licenseCreationInfo.getTypedValue());
		} else if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return Optional.ofNullable(license.getValue(propertyDescriptor));
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return Optional.ofNullable(exc.getValue(propertyDescriptor));
		} else if (Objects.nonNull(crossRef)) {
			return Optional.ofNullable(crossRef.getValue(propertyDescriptor));
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			return Optional.ofNullable(licenseCreationInfo.getValue(propertyDescriptor));
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			return Optional.ofNullable(licenseCreator.getValue(propertyDescriptor));
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)
	 */
	@Override
	public String getNextId(IdType idType)  throws InvalidSPDXAnalysisException  {
		this.listedLicenseModificationLock.writeLock().lock();
		try {
			if (IdType.Anonymous.equals(idType)) {
				return ANONYMOUS_ID_PREFIX + String.valueOf(this.nextId++);
			} else {
				return LISTED_LICENSE_NAMESPACE + "/" + "listedLicenseId_" + String.valueOf(this.nextId++);
			}
		} finally {
			this.listedLicenseModificationLock.writeLock().unlock();
		}
	}

	@Override
	public List<String> getSpdxListedLicenseIds() {
		this.listedLicenseModificationLock.readLock().lock();
		try {
			List<String> retval = new ArrayList<>();
			retval.addAll(this.licenseIds.values());
			return retval;
		} finally {
			this.listedLicenseModificationLock.readLock().unlock();
		}
	}

	@Override
	public String getLicenseListVersion() {
		this.listedLicenseModificationLock.readLock().lock();
		try {
			return this.licenseListVersion;
		} finally {
			this.listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	/**
	 * @return the release date for the license list
	 */
	public String getLicenseListReleaseDate() {
		this.listedLicenseModificationLock.readLock().lock();
		try {
			return this.licenseListReleaseDate;
		} finally {
			this.listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	@Override
	public List<String> getSpdxListedExceptionIds() {
		this.listedLicenseModificationLock.readLock().lock();
		try {
			List<String> retval = new ArrayList<>();
			retval.addAll(this.exceptionIds.values());
			return retval;
		} finally {
			this.listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	/**
	 * @param listedLicenseDocumentUri
	 * @param licenseId
	 * @return true if the licenseId belongs to an SPDX listed license
	 */
	public boolean isSpdxListedLicenseId(String listedLicenseDocumentUri, String licenseId) {
		this.listedLicenseModificationLock.readLock().lock();
		try {
			return this.licenseIds.containsKey(licenseId.toLowerCase());
		} finally {
			this.listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	/**
	 * @param listedLicenseDocumentUri
	 * @param exceptionId
	 * @return true if the exceptionId belongs to an SPDX listed exception
	 */
	public boolean isSpdxListedExceptionId(String listedLicenseDocumentUri, String exceptionId) {
		this.listedLicenseModificationLock.readLock().lock();
		try {
			return this.exceptionIds.containsKey(exceptionId.toLowerCase());
		} finally {
			this.listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	@Override
	public Optional<TypedValue> getTypedValue(String objectUri) throws InvalidSPDXAnalysisException {
		//NOTE: We only return the SPDX 3.0 version of the typed value, SPDX 2.X versions are also supported
		// but there is no API to specify the version of the typedValue
		String id = objectUriToId(objectUri);
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				return Optional.of(new TypedValue(objectUri, SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE, SpdxConstantsV3.MODEL_SPEC_VERSION));
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				return Optional.of(new TypedValue(objectUri, SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION, SpdxConstantsV3.MODEL_SPEC_VERSION));
			} else if (crossRefs.containsKey(id)) {
				// Cross refs are only supported in SPDX version 2.X
				return Optional.of(new TypedValue(objectUri, SpdxConstantsCompatV2.CLASS_CROSS_REF, ModelObjectV2.LATEST_SPDX_2_VERSION));
			} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
				return Optional.of(licenseCreationInfo.getTypedValue());
			} else if (licenseCreator.getObjectUri().equals(objectUri)) {
				return Optional.of(licenseCreator.getTypedValue());
			} else {
				return Optional.empty();
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	@Override
	public void removeProperty(String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			license.removeProperty(propertyDescriptor);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			exc.removeProperty(propertyDescriptor);
		} else if (Objects.nonNull(crossRef)) {
			crossRef.removeProperty(propertyDescriptor);
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			logger.warn("Ignoring remove property "+propertyDescriptor.getName()+" for license list creation info");
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			logger.warn("Ignoring remove property "+propertyDescriptor.getName()+" for license list creator");
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}
	
	@Override
	public Stream<TypedValue> getAllItems(String documentUri, @Nullable String typeFilter)
			throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(typeFilter, "Type filter can not be null");
		listedLicenseModificationLock.readLock().lock();
		try {
			List<TypedValue> allItems = new ArrayList<TypedValue>();
			if (SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE.equals(typeFilter)) {
				for (String licenseId:this.licenseIds.values()) {
					allItems.add(new TypedValue(licenseId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE, ModelObjectV2.LATEST_SPDX_2_VERSION));
				}
			}
			if (Objects.isNull(typeFilter) || SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE.equals(typeFilter)) {
				for (String licenseId:this.licenseIds.values()) {
					allItems.add(new TypedValue(licenseId, SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE, SpdxConstantsV3.MODEL_SPEC_VERSION));
				}
			}
			if (SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION.equals(typeFilter)) {
				for (String exceptionId:this.exceptionIds.values()) {
					allItems.add(new TypedValue(exceptionId, SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION, ModelObjectV2.LATEST_SPDX_2_VERSION));
				}
			}
			if (Objects.isNull(typeFilter) || SpdxConstantsCompatV2.CLASS_SPDX_LISTED_LICENSE_EXCEPTION.equals(typeFilter)) {
				for (String exceptionId:this.exceptionIds.values()) {
					allItems.add(new TypedValue(exceptionId, SpdxConstantsV3.EXPANDED_LICENSING_LISTED_LICENSE_EXCEPTION, SpdxConstantsV3.MODEL_SPEC_VERSION));
				}
			}
			if (Objects.isNull(typeFilter) || SpdxConstantsCompatV2.CLASS_CROSS_REF.equals(typeFilter)) {
				for (String crossRefId:crossRefs.keySet()) {
					allItems.add(new TypedValue(crossRefId, SpdxConstantsCompatV2.CLASS_CROSS_REF, ModelObjectV2.LATEST_SPDX_2_VERSION));
				}
			}
			if (Objects.isNull(typeFilter) || SpdxConstantsV3.CORE_CREATION_INFO.equals(typeFilter)) {
				allItems.add(licenseCreationInfo.typedValue);
			}
			if (Objects.isNull(typeFilter) || SpdxConstantsV3.CORE_AGENT.equals(typeFilter)) {
				allItems.add(licenseCreator.getTypedValue());
			}
			return Collections.unmodifiableList(allItems).stream();
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public int collectionSize(String objectUri, PropertyDescriptor propertyDescriptor) throws InvalidSPDXAnalysisException {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return ((List<Object>)(List<?>)license.getValueList(propertyDescriptor)).size();		
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return ((List<Object>)(List<?>)exc.getValueList(propertyDescriptor)).size();
		} else if (Objects.nonNull(crossRef)) {
			return ((List<Object>)(List<?>)crossRef.getValueList(propertyDescriptor)).size();
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			return ((List<Object>)(List<?>)licenseCreationInfo.getValueList(propertyDescriptor)).size();
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			return ((List<Object>)(List<?>)licenseCreator.getValueList(propertyDescriptor)).size();
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean collectionContains(String objectUri, PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			List<Object> valueList = (List<Object>)(List<?>)license.getValueList(propertyDescriptor);
			if (value instanceof TypedValue && SpdxConstantsCompatV2.CLASS_CROSS_REF.equals(((TypedValue)value).getType())) {
				CrossRefJson compareValue = crossRefs.get(((TypedValue)value).getObjectUri());
				if (Objects.isNull(compareValue)) {
					return false;
				} else {
					return valueList.contains(compareValue);
				}
			} else {
				return valueList.contains(value);
			}			
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return ((List<Object>)(List<?>)exc.getValueList(propertyDescriptor)).contains(value);
		} else if (Objects.nonNull(crossRef)) {
			return ((List<Object>)(List<?>)crossRef.getValueList(propertyDescriptor)).contains(value);
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			return ((List<Object>)(List<?>)licenseCreationInfo.getValueList(propertyDescriptor)).contains(value);
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			return ((List<Object>)(List<?>)licenseCreator.getValueList(propertyDescriptor)).contains(value);
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}

	@Override
	public boolean isCollectionMembersAssignableTo(String objectUri, PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return license.isCollectionMembersAssignableTo(propertyDescriptor, clazz);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.isCollectionMembersAssignableTo(propertyDescriptor, clazz);
		} else if (Objects.nonNull(crossRef)) {
			return crossRef.isCollectionMembersAssignableTo(propertyDescriptor, clazz);
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			return licenseCreationInfo.isCollectionMembersAssignableTo(propertyDescriptor, clazz);
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			return licenseCreator.isCollectionMembersAssignableTo(propertyDescriptor, clazz);
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}

	@Override
	public boolean isPropertyValueAssignableTo(String objectUri, PropertyDescriptor propertyDescriptor, 
			Class<?> clazz, String specVersion)
			throws InvalidSPDXAnalysisException {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return license.isPropertyValueAssignableTo(propertyDescriptor, clazz);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.isPropertyValueAssignableTo(propertyDescriptor, clazz);
		} else if (Objects.nonNull(crossRef)) {
			return crossRef.isPropertyValueAssignableTo(propertyDescriptor, clazz);
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			return licenseCreationInfo.isPropertyValueAssignableTo(propertyDescriptor, clazz);
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			return licenseCreator.isPropertyValueAssignableTo(propertyDescriptor, clazz);
		} else {
			logger.error("ID "+id+" is not a listed license ID, CrossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, CrossRef ID nor a listed exception ID");
		}
	}
	
	@Override
	public boolean isCollectionProperty(String objectUri, PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		String id = objectUriToId(objectUri);
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		CrossRefJson crossRef = null;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			} else if (crossRefs.containsKey(id)) {
				crossRef = crossRefs.get(id);
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return license.isCollectionProperty(propertyDescriptor);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.isCollectionProperty(propertyDescriptor);
		} else if (Objects.nonNull(crossRef)) {
			return crossRef.isCollectionProperty(propertyDescriptor.getName());
		} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			return this.licenseCreationInfo.isCollectionProperty(propertyDescriptor);
		} else if (licenseCreator.getObjectUri().equals(objectUri)) {
			return licenseCreator.isCollectionProperty(propertyDescriptor);
		} else {
			logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
		}
	}
	
	@Override
	public IdType getIdType(String objectUri) {
		Objects.requireNonNull(objectUri, "Object URI must not be null");
		if (objectUri.startsWith(LISTED_LICENSE_NAMESPACE)) {
			return IdType.ListedLicense;
		} else if (objectUri.startsWith(ANONYMOUS_ID_PREFIX) || LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
			return IdType.Anonymous;
		} else if (objectUri.startsWith(LicenseCreatorAgent.OBJECT_URI_PREFIX)) {
			return IdType.SpdxId;
		} else {
			return IdType.Unkown;
		}
	}
	
	@Override
	public boolean isAnon(String objectUri) {
		return objectUri.startsWith(ANONYMOUS_ID_PREFIX);
	}
	

	@Override
	public IModelStoreLock enterCriticalSection(boolean readLockRequested) {
		if (readLockRequested) {
			this.listedLicenseModificationLock.readLock().lock();
			return readLock;
		} else {
			this.listedLicenseModificationLock.writeLock().lock();
			return writeLock;
		}
	}

	@Override
	public void leaveCriticalSection(IModelStoreLock lock) {
		lock.unlock();
	}
	
	@Override
	public Optional<String> listedLicenseIdCaseSensitive(String licenseId) {
		listedLicenseModificationLock.readLock().lock();
		try {
			return Optional.ofNullable(this.licenseIds.get(licenseId.toLowerCase()));
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	
	@Override
	public Optional<String> listedExceptionIdCaseSensitive(String exceptionId) {
		listedLicenseModificationLock.readLock().lock();
		try {
			return Optional.ofNullable(this.exceptionIds.get(exceptionId.toLowerCase()));
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	@Override
	public Optional<String> getCaseSensisitiveId(String documentUri, String caseInsensisitiveId) {
		Optional<String> retval = listedLicenseIdCaseSensitive(caseInsensisitiveId);
		if (retval.isPresent()) {
			return retval;
		}
		return listedExceptionIdCaseSensitive(caseInsensisitiveId);
	}
	
	@Override
	public void delete(String objectUri) throws InvalidSPDXAnalysisException {
		String id = objectUriToId(objectUri);
		listedLicenseModificationLock.writeLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				this.listedLicenseCache.remove(id);
				this.licenseIds.remove(id.toLowerCase());
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				this.listedExceptionCache.remove(id);
				this.exceptionIds.remove(id.toLowerCase());
			} else if (crossRefs.containsKey(id)) {
				this.crossRefs.remove(id);
			} else if (LicenseCreationInfo.CREATION_INFO_URI.equals(objectUri)) {
				logger.warn("Ignoring the removal of the creation info for the license list");
			} else if (licenseCreator.getObjectUri().equals(objectUri)) {
				logger.warn("Ignoring the removal of the creator for the license list");
			} else {
				logger.error("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
				throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID, crossRef ID nor a listed exception ID");
			}
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}
	
	@Override
	public void close() throws Exception {
		// Nothing to do for the either the in-memory or the web store
	}
}
