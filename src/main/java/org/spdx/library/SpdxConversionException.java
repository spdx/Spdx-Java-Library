/**
 * Copyright (c) 2024 Source Auditor Inc.
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
package org.spdx.library;

import org.spdx.core.InvalidSPDXAnalysisException;

/**
 * @author gary
 *
 */
@SuppressWarnings("unused")
public class SpdxConversionException extends InvalidSPDXAnalysisException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg message
	 */
	public SpdxConversionException(String msg) {
		super(msg);
	}

	/**
	 * @param cause inner cause of the exception
	 */
	public SpdxConversionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param msg message
	 * @param cause inner cause of the exception
	 */
	public SpdxConversionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
