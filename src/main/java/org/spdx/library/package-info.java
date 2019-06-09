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
/**
 * Package containing useful library functions for reading, writing, and manipulating 
 * SPDX documents.
 * 
 * The <code>org.spdx.library.model</code> package represents the SPDX model as Java objects
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
 *
 */
package org.spdx.library;