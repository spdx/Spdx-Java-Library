package org.spdx.library.model.compat.v2.license;

import org.spdx.library.InvalidSPDXAnalysisException;

public class InvalidLicenseStringException extends InvalidSPDXAnalysisException {
	private static final long serialVersionUID = -1688466911486933160L;
	public InvalidLicenseStringException(String message) {
		super(message);
	}
	public InvalidLicenseStringException(String message, Throwable inner) {
		super(message, inner);
	}
}
