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
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.core.ExternalMap;
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
	 * @param modelStore Storage for the model collection
	 * @param objectUri Object URI or anonymous ID
	 * @param propertyDescriptor descriptor for the property use for the model collections
	 * @param copyManager if non-null, use this to copy properties when referenced outside this model store
	 * @param type The class of the elements to be stored in the collection if none, null if not known
	 * @param externalMap Map of URI's of elements referenced but not present in the store
	 * @throws InvalidSPDXAnalysisException on parsing or store errors
	 */
	public ModelSet(IModelStore modelStore, String objectUri, PropertyDescriptor propertyDescriptor, 
			@Nullable ModelCopyManager copyManager, @Nullable Class<?> type,
			Map<String, ExternalMap> externalMap) throws InvalidSPDXAnalysisException {
		super(modelStore, objectUri, propertyDescriptor, copyManager, type, externalMap);
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
