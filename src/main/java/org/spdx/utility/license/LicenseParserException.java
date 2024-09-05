package org.spdx.utility.license;

import org.spdx.core.InvalidSPDXAnalysisException;

public class LicenseParserException extends InvalidSPDXAnalysisException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg
	 */
	public LicenseParserException(String msg) {
		super(msg);
	}

	public LicenseParserException(String msg, Throwable inner) {
		super(msg, inner);
	}
}
