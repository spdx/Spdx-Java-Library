/**
 * SPDX-FileCopyrightText: Copyright (c) 2019 Source Auditor Inc.
 * SPDX-FileType: SOURCE
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
 * Functions for reading, writing, and manipulating SPDX documents
 * <p>
 * The <code>org.spdx.library.model.compat.v2.compat.v2</code> package represents the SPDX model as Java objects
 * with getters and setters to support manipulating the SPDX documents.
 * 
 * The <code>org.spdx.storage</code> package implements an interface to a storage framework
 * for backing the SPDX document.  An implementation of the storage interface is needed for
 * using this library.
 * 
 * The <code>SpdxDocumentFactory</code> and <code>SpdxLicenseInfoFactory</code> are the entrypoints
 * for creating SPDX documents and license information resp.
 * 
 * @author Gary O'Neall
 */
package org.spdx.library;
