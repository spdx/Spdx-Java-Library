/**
 * Copyright (c) 2019 Source Auditor Inc.
 * <p>
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
/**
 * @author Gary O'Neall
 * <p>
 * Storage for SPDX listed licenses.
 * <p>
 * The <code>SpdxListedLicenseModelStore</code> is the default storage which pull the data from JSON files at spdx.org/licenses
 * 
 * The <code>SpdxListedLicenseLocalModelStore</code> uses a local copy of the licenses stored in the resources/licenses directory
 *
 */
package org.spdx.storage.listedlicense;