/**
 * Copyright (c) 2024 Source Auditor Inc.
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
 
package org.spdx.library.model.v3.dataset;

import org.spdx.library.IndividualUriValue;

/**
 * DO NOT EDIT - this file is generated by the Owl to Java Utility 
 * See: https://github.com/spdx/tools-java 
 * 
 * Describes the possible types of availability of a dataset, indicating whether the 
 * dataset can be directly downloaded, can be assembled using a script for scraping 
 * the data, is only available after a clickthrough or a registration form. 
 */
public enum DatasetAvailabilityType implements IndividualUriValue {

	SCRAPING__SCRIPT("Scraping-Script"),
	DIRECT__DOWNLOAD("Direct-Download"),
	QUERY("Query"),
	CLICKTHROUGH("Clickthrough"),
	REGISTRATION("Registration");
	
	private String longName;
	
	private DatasetAvailabilityType(String longName) {
		this.longName = longName;
	}
	
	@Override
	public String getIndividualURI() {
		return getNameSpace() + "/" + getLongName();
	}
	
	public String getLongName() {
		return longName;
	}
	
	public String getNameSpace() {
		return "https://spdx.org/rdf/v3/Dataset/DatasetAvailabilityType";
	}
}
