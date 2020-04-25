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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.DuplicateSpdxIdException;
import org.spdx.library.model.SpdxIdNotFoundException;
import org.spdx.library.model.TypedValue;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.library.model.license.SpdxListedLicenseException;

import com.google.gson.Gson;

/**
 * Read-only model store for the SPDX listed licenses
 * 
 * License and exception ID's can be case insensitive
 * 
 * @author Gary O'Neall
 *
 */
public abstract class SpdxListedLicenseModelStore implements IListedLicenseStore {
	
	static final Logger logger = LoggerFactory.getLogger(SpdxListedLicenseModelStore.class.getName());
	static final String DEFAULT_LICENSE_LIST_VERSION = "3.7";
	static final String LICENSE_TOC_FILENAME = "licenses.json";
	static final String EXCEPTION_TOC_FILENAME = "exceptions.json";
	static final String JSON_SUFFIX = ".json";
	private static final List<String> DOCUMENT_URIS = Collections.unmodifiableList(Arrays.asList(new String[]{SpdxConstants.LISTED_LICENSE_URL}));
	
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
	String licenseListVersion = DEFAULT_LICENSE_LIST_VERSION;
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
	
	public SpdxListedLicenseModelStore() throws InvalidSPDXAnalysisException {
		loadIds();
	}
	
	/**
	 * @return InputStream for the Table of Contents of the licenses formated in JSON SPDX
	 * @throws IOException
	 */
	abstract InputStream getTocInputStream() throws IOException;
	
	/**
	 * @return InputStream for the Table of Contents of the exceptions formated in JSON SPDX
	 * @throws IOException
	 */
	abstract InputStream getExceptionTocInputStream() throws IOException;
	
	/**
	 * @return InputStream for a license formated in SPDX JSON
	 * @throws IOException
	 */
	abstract InputStream getLicenseInputStream(String licenseId) throws IOException;
	
	/**
	 * @return InputStream for an exception formated in SPDX JSON
	 * @throws IOException
	 */
	abstract InputStream getExceptionInputStream(String exceptionId) throws IOException;

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
                reader = new BufferedReader(new InputStreamReader(tocStream));
                StringBuilder tocJsonStr = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                	tocJsonStr.append(line);
                }
                LicenseJsonTOC jsonToc = gson.fromJson(tocJsonStr.toString(), LicenseJsonTOC.class);
                licenseIds = jsonToc.getLicenseIds();
                this.licenseListVersion = jsonToc.getLicenseListVersion();
                
                // read the exception ID's
                tocStream = getExceptionTocInputStream();
                reader = new BufferedReader(new InputStreamReader(tocStream));
                tocJsonStr  = new StringBuilder();
                while((line = reader.readLine()) != null) {
                	tocJsonStr.append(line);
                }
                ExceptionJsonTOC exceptionToc = gson.fromJson(tocJsonStr.toString(), ExceptionJsonTOC.class);
                exceptionIds = exceptionToc.getExceptionIds();
            } catch (MalformedURLException e) {
				logger.error("Json TOC URL invalid, using local TOC file");
				throw(new SpdxListedLicenseException("License TOC URL invalid"));
			} catch (IOException e) {
				logger.error("I/O error opening Json TOC URL");
				throw(new SpdxListedLicenseException("I/O error reading license TOC"));
			} finally {
            	if (reader != null) {
            		try {
						reader.close();
					} catch (IOException e) {
						logger.warn("Unable to close JSON TOC reader");
					}
            	} else if (tocStream != null) {
            		try {
						tocStream.close();
					} catch (IOException e) {
						logger.warn("Unable to close JSON TOC input stream");
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
	public boolean exists(String documentUri, String id) {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			return false;
		}
		return (this.licenseIds.containsKey(id.toLowerCase()) || this.exceptionIds.containsKey(id.toLowerCase()));
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#create(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void create(String documentUri, String id, String type) throws InvalidSPDXAnalysisException {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		listedLicenseModificationLock.writeLock().lock();
		try {
			if (this.licenseIds.containsKey(id.toLowerCase()) || this.exceptionIds.containsKey(id.toLowerCase())) {
				logger.error("Duplicate SPDX ID on create: "+id);;
				throw new DuplicateSpdxIdException("ID "+id+" already exists.");
			}
			if (SpdxConstants.CLASS_SPDX_LISTED_LICENSE.equals(type)) {
				this.licenseIds.put(id.toLowerCase(), id);
				this.listedLicenseCache.put(id, new LicenseJson(id));
			} else if (SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION.equals(type)) {
				this.exceptionIds.put(id.toLowerCase(), id);
				this.listedExceptionCache.put(id,  new ExceptionJson(id));
			}
		} finally {
			listedLicenseModificationLock.writeLock().unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getPropertyValueNames(java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> getPropertyValueNames(String documentUri, String id) throws InvalidSPDXAnalysisException  {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(licenseIds.get(id.toLowerCase()));
			return license.getPropertyValueNames();
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(exceptionIds.get(id.toLowerCase()));
			return exc.getPropertyValueNames();
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
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
					logger.error("Json license invalid for ID "+id);
					throw(new SpdxListedLicenseException("JSON license URL invalid for ID "+id));
				} catch (IOException e) {
					logger.error("I/O error opening Json license URL");
					throw(new SpdxListedLicenseException("I/O Error reading license data for ID "+id));
				} finally {
	            	if (reader != null) {
	            		try {
							reader.close();
						} catch (IOException e) {
							logger.warn("Unable to close JSON TOC reader");
						}
	            	} else if (jsonStream != null) {
	            		try {
							jsonStream.close();
						} catch (IOException e) {
							logger.warn("Unable to close JSON TOC input stream");
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
					throw(new SpdxListedLicenseException("JSON license URL invalid for ID "+id));
				} catch (IOException e) {
					logger.error("I/O error opening Json license URL");
					throw(new SpdxListedLicenseException("I/O Error reading license data for ID "+id));
				} finally {
	            	if (reader != null) {
	            		try {
							reader.close();
						} catch (IOException e) {
							logger.warn("Unable to close JSON TOC reader");
						}
	            	} else if (jsonStream != null) {
	            		try {
							jsonStream.close();
						} catch (IOException e) {
							logger.warn("Unable to close JSON TOC input stream");
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
	public void setValue(String documentUri, String id, String propertyName, Object value)  throws InvalidSPDXAnalysisException  {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			license.setPrimativeValue(propertyName, value);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			exc.setPrimativeValue(propertyName, value);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#clearPropertyValueList(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void clearValueCollection(String documentUri, String id, String propertyName)  throws InvalidSPDXAnalysisException  {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			license.clearPropertyValueList(propertyName);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			exc.clearPropertyValueList(propertyName);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#addPrimitiveValueToList(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean addValueToCollection(String documentUri, String id, String propertyName, Object value)  throws InvalidSPDXAnalysisException  {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return license.addPrimitiveValueToList(propertyName, value);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.addPrimitiveValueToList(propertyName, value);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}

	@Override
	public boolean removeValueFromCollection(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return license.removePrimitiveValueToList(propertyName, value);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.removePrimitiveValueToList(propertyName, value);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}
	
	/**
	 * @param documentUri
	 * @param id
	 * @param propertyName
	 * @return List of values for the propertyName for id within documentUri
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getValueList(String documentUri, String id, String propertyName)  throws InvalidSPDXAnalysisException  {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return (List<Object>)(List<?>)license.getValueList(propertyName);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return (List<Object>)(List<?>)exc.getValueList(propertyName);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getValueList(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Iterator<Object> listValues(String documentUri, String id, String propertyName)  throws InvalidSPDXAnalysisException  {
		return getValueList(documentUri, id, propertyName).iterator();
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<Object> getValue(String documentUri, String id, String propertyName)  throws InvalidSPDXAnalysisException  {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return Optional.ofNullable(license.getValue(propertyName));
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return Optional.ofNullable(exc.getValue(propertyName));
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.storage.IModelStore#getNextId(org.spdx.storage.IModelStore.IdType, java.lang.String)
	 */
	@Override
	public String getNextId(IdType idType, String documentUri)  throws InvalidSPDXAnalysisException  {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		this.listedLicenseModificationLock.writeLock().lock();
		try {
			return "SpdxLicenseGeneratedId-"+String.valueOf(this.nextId++);
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
	public void removeProperty(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			license.removeProperty(propertyName);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			exc.removeProperty(propertyName);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}
		
	@Override
	public List<String> getDocumentUris() {
		return DOCUMENT_URIS;
	}

	@Override
	public Stream<TypedValue> getAllItems(String documentUri, @Nullable String typeFilter)
			throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(typeFilter, "Type filter can not be null");
		listedLicenseModificationLock.readLock().lock();
		try {
			List<TypedValue> allItems = new ArrayList<TypedValue>();
			if (Objects.isNull(typeFilter) || SpdxConstants.CLASS_SPDX_LISTED_LICENSE.equals(typeFilter)) {
				for (String licenseId:this.licenseIds.values()) {
					allItems.add(new TypedValue(licenseId, SpdxConstants.CLASS_SPDX_LISTED_LICENSE));
				}
			}
			if (Objects.isNull(typeFilter) || SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION.equals(typeFilter)) {
				for (String exceptionId:this.exceptionIds.values()) {
					allItems.add(new TypedValue(exceptionId, SpdxConstants.CLASS_SPDX_LICENSE_EXCEPTION));
				}
			}
			return Collections.unmodifiableList(allItems).stream();
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
	}
	
	
	
	@Override
	public int collectionSize(String documentUri, String id, String propertyName) throws InvalidSPDXAnalysisException {
		return getValueList(documentUri, id, propertyName).size();
	}

	@Override
	public boolean collectionContains(String documentUri, String id, String propertyName, Object value)
			throws InvalidSPDXAnalysisException {
		return getValueList(documentUri, id, propertyName).contains(value);
	}

	@Override
	public boolean isCollectionMembersAssignableTo(String documentUri, String id, String propertyName, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return license.isCollectionMembersAssignableTo(propertyName, clazz);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.isCollectionMembersAssignableTo(propertyName, clazz);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}

	@Override
	public boolean isPropertyValueAssignableTo(String documentUri, String id, String propertyName, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return license.isPropertyValueAssignableTo(propertyName, clazz);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.isPropertyValueAssignableTo(propertyName, clazz);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}
	
	@Override
	public boolean isCollectionProperty(String documentUri, String id, String propertyName)
			throws InvalidSPDXAnalysisException {
		if (!SpdxConstants.LISTED_LICENSE_URL.equals(documentUri)) {
			logger.error("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
			throw new SpdxIdNotFoundException("Document URI for SPDX listed licenses is expected to be "+
					SpdxConstants.LISTED_LICENSE_URL + ".  Supplied document URI was "+documentUri);
		}
		boolean isLicenseId = false;
		boolean isExceptionId = false;
		listedLicenseModificationLock.readLock().lock();
		try {
			if (licenseIds.containsKey(id.toLowerCase())) {
				isLicenseId = true;
			} else if (exceptionIds.containsKey(id.toLowerCase())) {
				isExceptionId = true;
			}
		} finally {
			listedLicenseModificationLock.readLock().unlock();
		}
		if (isLicenseId) {
			LicenseJson license = fetchLicenseJson(id);
			return license.isCollectionProperty(propertyName);
		} else if (isExceptionId) {
			ExceptionJson exc = fetchExceptionJson(id);
			return exc.isCollectionProperty(propertyName);
		} else {
			logger.error("ID "+id+" is not a listed license ID nor a listed exception ID");
			throw new SpdxIdNotFoundException("ID "+id+" is not a listed license ID nor a listed exception ID");
		}
	}
	
	@Override
	public IdType getIdType(String id) {
		if (LicenseInfoFactory.isSpdxListedLicenseId(id) || LicenseInfoFactory.isSpdxListedExceptionId(id)) {
			return IdType.ListedLicense;
		} else {
			return IdType.Unkown;
		}
	}
	

	@Override
	public IModelStoreLock enterCriticalSection(String documentUri, boolean readLockRequested) {
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
}
