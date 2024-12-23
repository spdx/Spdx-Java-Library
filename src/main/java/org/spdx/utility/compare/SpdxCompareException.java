package org.spdx.utility.compare;

/**
 * Exception thrown when there is an error during the comparison of SPDX
 * documents
 * 
 * @author Gary O'Neall
 */
public class SpdxCompareException extends Exception {

	private static final long serialVersionUID = 1L;

	public SpdxCompareException(String message) {
		super(message);
	}

	public SpdxCompareException(Throwable cause) {
		super(cause);
	}

	public SpdxCompareException(String message, Throwable cause) {
		super(message, cause);
	}

}

