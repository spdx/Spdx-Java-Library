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
 
package org.spdx.library.model.v3.software;

import org.spdx.library.IndividualUriValue;

/**
 * DO NOT EDIT - this file is generated by the Owl to Java Utility 
 * See: https://github.com/spdx/tools-java 
 * 
 * The set of SBOM types with definitions as defined in [Types of Software Bill of Material 
 * (SBOM) Documents](https://www.cisa.gov/sites/default/files/2023-04/sbom-types-document-508c.pdf), 
 * published on April 21, 2023. An SBOM type describes the most likely type of an SBOM 
 * from the producer perspective, so that consumers can draw conclusions about the 
 * data inside an SBOM. A single SBOM can have multiple SBOM document types associated 
 * with it. 
 */
public enum SbomType implements IndividualUriValue {

	ANALYZED("analyzed"),
	SOURCE("source"),
	DESIGN("design"),
	DEPLOYED("deployed"),
	BUILD("build"),
	RUNTIME("runtime");
	
	private String longName;
	
	private SbomType(String longName) {
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
		return "https://spdx.org/rdf/v3/Software/SbomType";
	}
}
