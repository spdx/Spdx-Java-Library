/**
 * Copyright (c) 2023 Source Auditor Inc.
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
package org.spdx.library;

import org.spdx.storage.PropertyDescriptor;

/**
 * @author Gary O'Neall
 * 
 * Primarily used for debugging.  Records details when two model objects are compared and are determined to not
 * be equivalent
 *
 */
public class NotEquivalentReason {
	
	public enum NotEquivalent {
		DIFFERENT_CLASS, MISSING_PROPERTY, PROPERTY_NOT_EQUIVALENT, COMPARE_PROPERTY_MISSING};
		
		NotEquivalent reason;
		PropertyDescriptor property = null;
		
		public NotEquivalentReason(NotEquivalent reason) {
			this.reason = reason;
		}
		
		public NotEquivalentReason(NotEquivalent reason, PropertyDescriptor property) {
			this(reason);
			this.property = property;
		}

		/**
		 * @return the reason
		 */
		public NotEquivalent getReason() {
			return reason;
		}

		/**
		 * @param reason the reason to set
		 */
		public void setReason(NotEquivalent reason) {
			this.reason = reason;
		}

		/**
		 * @return the property
		 */
		public PropertyDescriptor getProperty() {
			return property;
		}

		/**
		 * @param property the property to set
		 */
		public void setProperty(PropertyDescriptor property) {
			this.property = property;
		}

}
