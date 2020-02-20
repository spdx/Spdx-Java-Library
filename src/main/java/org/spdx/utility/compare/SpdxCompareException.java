package org.spdx.utility.compare;

/**
 * @author Gary O'Neall
 *
 */
public class SpdxCompareException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public SpdxCompareException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SpdxCompareException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SpdxCompareException(String message, Throwable cause) {
		super(message, cause);
	}

}

