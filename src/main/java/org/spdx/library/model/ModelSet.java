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

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.PropertyDescriptor;

/**
 * A ModelCollection implemented as a set where all items in the collection are unique based
 * on equality (not based on equivalence).
 * 
 * @author Gary O'Neall
 *
 */
public class ModelSet<T extends Object> extends ModelCollection<T> {
	
	static final Logger logger = LoggerFactory.getLogger(ModelSet.class);
	
	/**
	 * @param modelStore
	 * @param objectUri
	 * @param propertyDescriptor
	 * @param copyManager
	 * @param type
	 * @throws InvalidSPDXAnalysisException
	 */
	public ModelSet(IModelStore modelStore, String objectUri, PropertyDescriptor propertyDescriptor, 
			@Nullable ModelCopyManager copyManager, @Nullable Class<?> type) throws InvalidSPDXAnalysisException {
		super(modelStore, objectUri, propertyDescriptor, copyManager, type);
	}
	
	@Override
	public boolean add(Object element) {	
		IModelStoreLock lock;
		try {
			lock = this.getModelStore().enterCriticalSection(false);
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
		try {
			if (!super.contains(element)) {
				return super.add(element);
			} else {
				return false;
			}
		} finally {
			this.getModelStore().leaveCriticalSection(lock);
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends Object> c) {
		IModelStoreLock lock;
		try {
			lock = this.getModelStore().enterCriticalSection(false);
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
		try {
			boolean retval = false;
			Iterator<? extends Object> iter = c.iterator();
			while (iter.hasNext()) {
				Object item = iter.next();
				if (!super.contains(item) && super.add(item)) {
					retval = true;
				}
			}
			return retval;
		} finally {
			this.getModelStore().leaveCriticalSection(lock);
		}
	}
	
}
