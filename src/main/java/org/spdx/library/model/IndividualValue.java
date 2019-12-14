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

/**
 * Classes which implement the IndividuallValue interface will be stored as a single value.  Theses classes
 * must NOT implement any properties themselves.  Any such properties will be lost during storage and retrival.
 * 
 * @author Gary O'Neall
 *
 */
public interface IndividualValue {
	
	/**
	 * @return a unique identifier for this value.  Typically the namespace + the long name
	 */
	public String getIndividualURI();
	
	/**
	 * @return the short name for the value - typically used in the SPDX Tag/Value format
	 */
	public String getShortName();
	
	/**
	 * @return the unique long name for the value - typically the URI without the namespace
	 */
	public String getLongName();
	
	/**
	 * @return The namespace used to create the individual URI
	 */
	public String getNameSpace();

}
