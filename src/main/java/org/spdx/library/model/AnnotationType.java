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

import org.spdx.library.SpdxConstants;

/**
 * Annotation types for the Annotation Class
 * 
 * @author Gary O'Neall
 *
 */
public enum AnnotationType implements IndividualValue {
	
	OTHER("annotationType_other"),
	REVIEW("annotationType_review");
	
	private String longName;
	
	private AnnotationType(String longName) {
		this.longName = longName;
	}
	@Override
	public String getIndividualURI() {
		return getNameSpace() + getLongName();
	}

	@Override
	public String getShortName() {
		return toString();
	}

	@Override
	public String getLongName() {
		return longName;
	}

	@Override
	public String getNameSpace() {
		return SpdxConstants.SPDX_NAMESPACE;
	}

}
