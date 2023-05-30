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
package org.spdx.storage;

import java.util.Objects;

/**
 * Holds a description of a property including the property name and property 
 * nameSpace. Includes a helper function to default the namespace. 
 * @author Gary O'Neall
 *
 */
public class PropertyDescriptor {
	
	private String name;
	private String nameSpace;
	
	/**
	 * @param name Property name as defined in the SPDX specification
	 * @param nameSpace Property nameSpace as defined in the SPDX specification
	 */
	public PropertyDescriptor(String name, String nameSpace) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(nameSpace);
		this.name = name;
		this.nameSpace = nameSpace;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Objects.requireNonNull(name, "Can not set name to null");
		this.name = name;
	}

	/**
	 * @return the nameSpace
	 */
	public String getNameSpace() {
		return nameSpace;
	}

	/**
	 * @param nameSpace the nameSpace to set
	 */
	public void setNameSpace(String nameSpace) {
		Objects.requireNonNull(nameSpace, "Can not set nameSpace to null");
		this.nameSpace = nameSpace;
	}
	
	@Override
	public boolean equals(Object compare) {
		return compare instanceof PropertyDescriptor &&
				Objects.equals(this.name, ((PropertyDescriptor)(compare)).name) && 
				Objects.equals(this.nameSpace, ((PropertyDescriptor)(compare)).nameSpace);
	}
	
	@Override
	public int hashCode() {
		return 11 ^ this.name.hashCode() ^ this.nameSpace.hashCode();
	}

	@Override
	public String toString() {
		return this.nameSpace + this.name;
	}
}
