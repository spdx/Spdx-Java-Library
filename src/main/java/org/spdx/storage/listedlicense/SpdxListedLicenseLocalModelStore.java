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

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

/**
 * @author gary
 *
 */
public class SpdxListedLicenseLocalModelStore implements IListedLicenseStore {
	
	static final String DEFAULT_LICENSE_LIST_VERSION = "3.7";
	
	String licenseListVersion = DEFAULT_LICENSE_LIST_VERSION;
	private static final ReadWriteLock listedLicenseModificationLock = new ReentrantReadWriteLock();
	
	public SpdxListedLicenseLocalModelStore() throws InvalidSPDXAnalysisException {
		loadLicenseIds();
	}

	private void loadLicenseIds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exists(String documentUri, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void create(String documentUri, String id, String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getPropertyValueNames(String documentUri, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPropertyValueListNames(String documentUri, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTypedValue(String documentUri, String id, String propertyName, String valueId, String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPrimitiveValue(String documentUri, String id, String propertyName, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearPropertyValueList(String documentUri, String id, String propertyName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTypedValueToList(String documentUri, String id, String propertyName, String valueId, String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPrimitiveValueToList(String documentUri, String id, String propertyName, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<?> getValueList(String documentUri, String id, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String documentUri, String id, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNextId(IdType idType, String documentUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSpdxListedLicenseIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLicenseListVersion() {
		// TODO Auto-generated method stub
		return null;
	}

}
