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
package org.spdx.library;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Static class to manage the SPDX versions and the version of the implementation classes
 * @author Gary O'Neall
 *
 */
public class Version {


	public static final String POINT_EIGHT_SPDX_VERSION = "SPDX-0.8";
	public static final String POINT_NINE_SPDX_VERSION = "SPDX-0.9";
	public static final String ONE_DOT_ZERO_SPDX_VERSION = "SPDX-1.0";
	public static final String ONE_DOT_ONE_SPDX_VERSION = "SPDX-1.1";
	public static final String ONE_DOT_TWO_SPDX_VERSION = "SPDX-1.2";
	public static final String TWO_POINT_ZERO_VERSION = "SPDX-2.0";
	public static final String TWO_POINT_ONE_VERSION = "SPDX-2.1";
	public static final String TWO_POINT_TWO_VERSION = "SPDX-2.2";
	public static final String CURRENT_SPDX_VERSION = TWO_POINT_TWO_VERSION;
	
	public static final Set<String> SUPPORTED_SPDX_VERSIONS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]{
			ONE_DOT_ZERO_SPDX_VERSION, ONE_DOT_ONE_SPDX_VERSION, ONE_DOT_TWO_SPDX_VERSION, TWO_POINT_ZERO_VERSION,
			TWO_POINT_ONE_VERSION, TWO_POINT_TWO_VERSION
	})));
	
	public static final String CURRENT_IMPLEMENTATION_VERSION = "1.0.8";
	
	public static String verifySpdxVersion(String spdxVersion) {
		if (!spdxVersion.startsWith("SPDX-")) {
			return "Invalid spdx version - must start with 'SPDX-'";
		}
		Matcher docSpecVersionMatcher = SpdxConstants.SPDX_VERSION_PATTERN.matcher(spdxVersion);
		if (!docSpecVersionMatcher.matches()) {
			return "Invalid spdx version format - must match 'SPDX-M.N'";
		}
		if (!SUPPORTED_SPDX_VERSIONS.contains(spdxVersion)) {
			return "Version "+spdxVersion+" is not supported by this version of the rdf parser";
		}
		return null;	// if we got here, there is no problem
	}
	
	private Version() {
		// static class
	}

}
