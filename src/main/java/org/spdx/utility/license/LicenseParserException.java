package org.spdx.utility.license;

import org.spdx.core.InvalidSPDXAnalysisException;

/**
 * Exception thrown when there is an error during the parsing of SPDX license
 * information
 * 
 * @author Gary O'Neall
 */
public class LicenseParserException extends InvalidSPDXAnalysisException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param msg exception message
	 */
	public LicenseParserException(String msg) {
		super(msg);
	}

	public LicenseParserException(String msg, Throwable inner) {
		super(msg, inner);
	}
}
