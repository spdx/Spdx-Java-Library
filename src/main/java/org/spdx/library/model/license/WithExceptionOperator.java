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
package org.spdx.library.model.license;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;

/**
 * A license that has a With exception operator (e.g. GPL-2.0 WITH Autoconf-exception-2.0)
 * @author Gary O'Neall
 */
public class WithExceptionOperator extends AnyLicenseInfo {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public WithExceptionOperator() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public WithExceptionOperator(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param copyManager if non-null, allows for copying of any properties set which use other model stores or document URI's
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public WithExceptionOperator(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	public WithExceptionOperator(AnyLicenseInfo license, LicenseException exception) throws InvalidSPDXAnalysisException {
		super();
		setLicense(license);
		setException(exception);
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#getType()
	 */
	@Override
	public String getType() {
		return SpdxConstants.CLASS_WITH_EXCEPTION_OPERATOR;
	}
	
	/**
	 * @return the license
	 * @throws InvalidSPDXAnalysisException 
	 */
	public AnyLicenseInfo getLicense() throws InvalidSPDXAnalysisException {
		Optional<AnyLicenseInfo> retval = getAnyLicenseInfoPropertyValue(SpdxConstants.PROP_LICENSE_SET_MEMEBER);
		if (!retval.isPresent()) {
			throw new InvalidSPDXAnalysisException("Required license for exception is missing");
		}
		return retval.get();
	}
	
	/**
	 * @param license the license to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setLicense(AnyLicenseInfo license) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LICENSE_SET_MEMEBER, license);
	}
	
	/**
	 * @return the exception
	 */
	public LicenseException getException() throws InvalidSPDXAnalysisException {
		Optional<Object> retval = getObjectPropertyValue(SpdxConstants.PROP_LICENSE_EXCEPTION);
		if (!retval.isPresent()) {
			throw new InvalidSPDXAnalysisException("Required exception is missing for with exception operator");
		}
		if (!(retval.get() instanceof LicenseException)) {
			throw new SpdxInvalidTypeException("Exception is not of type LicenseException");
		}
		return (LicenseException)(retval.get());
	}
	
	/**
	 * @param exception the exception to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setException(LicenseException exception) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_LICENSE_EXCEPTION, exception);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			Optional<Object> license = getObjectPropertyValue(SpdxConstants.PROP_LICENSE_SET_MEMEBER);
			Optional<Object> exception = getObjectPropertyValue(SpdxConstants.PROP_LICENSE_EXCEPTION);
			if (!license.isPresent() || !exception.isPresent()) {
				return "UNDEFINED WITH EXCEPTION";
			}
			return license.get().toString() +" WITH "+exception.get().toString();
		} catch (Exception ex) {
			return "UNDEFINED WITH EXCEPTION";
		}
	}

	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(List<String> verifiedIds) {
		List<String> retval = new ArrayList<>();
		try {
			Optional<Object> license = getObjectPropertyValue(SpdxConstants.PROP_LICENSE_SET_MEMEBER);
			if (license.isPresent()) {
				if (license.get() instanceof AnyLicenseInfo) {
					retval.addAll(((AnyLicenseInfo)(license.get())).verify(verifiedIds));
				} else {
					retval.add("Invalid type for With Operator license");
				}
			} else {
				retval.add("Missing required license for With Operator");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting license property of a With operator: "+e.getMessage());
		}
		try {
			Optional<Object> exception = getObjectPropertyValue(SpdxConstants.PROP_LICENSE_EXCEPTION);
			if (exception.isPresent()) {
				if (exception.get() instanceof LicenseException) {
					retval.addAll(((LicenseException)(exception.get())).verify(verifiedIds));
				} else {
					retval.add("Invalid type for With Operator exception");
				}
			} else {
				retval.add("Missing required exception for With Operator");
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting exception property of a With operator: "+e.getMessage());
		}
		return retval;
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof WithExceptionOperator)) {
			return false;
		}
		WithExceptionOperator comp = (WithExceptionOperator)o;
		AnyLicenseInfo myLicense = null;
		AnyLicenseInfo compLicense = null;
		LicenseException myException = null;
		LicenseException compException = null;
		try {
			myLicense = this.getLicense();
		} catch (InvalidSPDXAnalysisException e) {
			// Likely caused by missing required field - leave the value as null
		}
		try {
			compLicense = comp.getLicense();
		} catch (InvalidSPDXAnalysisException e) {
			// Likely caused by missing required field - leave the value as null
		}
		try {
			myException = this.getException();
		} catch (InvalidSPDXAnalysisException e) {
			// Likely caused by missing required field - leave the value as null
		}
		try {
			compException = comp.getException();
		} catch (InvalidSPDXAnalysisException e) {
			// Likely caused by missing required field - leave the value as null
		}
		if (!Objects.equals(myLicense, compLicense)) {
			return false;
		}
		if (!Objects.equals(myException, compException)) {
			return false;
		}
		return true;	
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.ModelObject#hashCode()
	 */
	@Override
	public int hashCode() {
		int licHashCode = 0;
		int exceptionHashCode = 0;
		try {
			licHashCode = this.getLicense().hashCode();
		} catch (InvalidSPDXAnalysisException e) {
			// Likely caused by missing required field - leave the value as 0
		}
		try {
			exceptionHashCode = this.getException().hashCode();
		} catch (InvalidSPDXAnalysisException e) {
			// Likely caused by missing required field - leave the value as 0
		}
		return 977 ^ licHashCode ^ exceptionHashCode;
	}
}
