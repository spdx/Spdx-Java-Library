package org.spdx.utility.compare;

/**
 * @author Gary O'Neall
 *
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

