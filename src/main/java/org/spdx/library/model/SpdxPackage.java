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
package org.spdx.library.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.OrLaterOperator;
import org.spdx.library.model.license.SimpleLicensingInfo;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.library.model.license.SpdxNoneLicense;
import org.spdx.library.model.license.WithExceptionOperator;
import org.spdx.storage.IModelStore;

/**
 * A Package represents a collection of software files that are
 * delivered as a single functional component.
 * @author Gary O'Neall
 *
 */
public class SpdxPackage extends SpdxItem implements Comparable<SpdxPackage> {

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage() throws InvalidSPDXAnalysisException {
		super();
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage(IModelStore modelStore, String documentUri, String id, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_PACKAGE;
	}
	
	@Override
	protected String getNamePropertyName() {
		return SpdxConstants.PROP_PROJECT_NAME;
	}
	
	/**
	 * @return true unless the filesAnalyzed property is present and set to false (default is true)
	 * @throws InvalidSPDXAnalysisException
	 */
	public boolean isFilesAnalyzed() throws InvalidSPDXAnalysisException {
		Optional<Boolean> filesAnalyzed = getBooleanPropertyValue(SpdxConstants.PROP_PACKAGE_FILES_ANALYZED);
		if (!filesAnalyzed.isPresent()) {
			return true;
		} else {
			return filesAnalyzed.get();
		}
	}
	
	/**
	 * Set files Analyzed for the package
	 * @param filesAnalyzed
	 * @return this to build additional options
	 */
	public SpdxPackage setFilesAnalyzed(@Nullable Boolean filesAnalyzed) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_FILES_ANALYZED, filesAnalyzed);
		return this;
	}
	
	/**
	 * @return the licenseDeclared
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public Optional<AnyLicenseInfo> getLicenseDeclared() throws InvalidSPDXAnalysisException {
		return (Optional<AnyLicenseInfo>)(Optional<?>)getObjectPropertyValue(SpdxConstants.PROP_PACKAGE_DECLARED_LICENSE);
	}
	
	/**
	 * Set the licenseDeclared
	 * @param licenseDeclared
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setLicenseDeclared(@Nullable AnyLicenseInfo licenseDeclared) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_DECLARED_LICENSE, licenseDeclared);
		return this;
	}
	
	/**
	 * @return collection of checksums
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Checksum> getChecksums() throws InvalidSPDXAnalysisException {
		return (Collection<Checksum>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_PACKAGE_CHECKSUM);
	}
	
	/**
	 * Add a checksum to the collection of checksums for this package
	 * @param checksum
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage addChecksum(Checksum checksum) throws InvalidSPDXAnalysisException {
		getChecksums().add(checksum);
		return this;
	}

	/**
	 * @return the description
	 */
	public Optional<String> getDescription() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PACKAGE_DESCRIPTION);
	}
	
	/**
	 * @param description
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setDescription(String description) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_DESCRIPTION, description);
		return this;
	}
	
	/**
	 * @return the downloadLocation
	 */
	public Optional<String> getDownloadLocation() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PACKAGE_DOWNLOAD_URL);
	}
	
	/**
	 * @param downloadLocation the download location
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setDownloadLocation(String downloadLocation) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_DOWNLOAD_URL, downloadLocation);
		return this;
	}
	
	/**
	 * @return the homepage
	 */
	public Optional<String> getHomepage() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PROJECT_HOMEPAGE);
	}
	
	/**
	 * @param homepage the package homepage
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setHomepage(String homepage) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PROJECT_HOMEPAGE, homepage);
		return this;
	}
	
	/**
	 * @return the originator
	 */
	public Optional<String> getOriginator() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PACKAGE_ORIGINATOR);
	}
	
	/**
	 * @param originator the package originator
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setOriginator(String originator) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_ORIGINATOR, originator);
		return this;
	}
	
	/**
	 * @return the packageFileName
	 */
	public Optional<String> getPackageFileName() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PACKAGE_FILE_NAME);
	}
	
	/**
	 * @param packageFileName the package filename to set
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setPackageFileName(String packageFileName) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_FILE_NAME, packageFileName);
		return this;
	}
	
	/**
	 * @return the packageVerificationCode
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public Optional<SpdxPackageVerificationCode> getPackageVerificationCode() throws InvalidSPDXAnalysisException {
		return (Optional<SpdxPackageVerificationCode>) (Optional<?>)getObjectPropertyValue(SpdxConstants.PROP_PACKAGE_VERIFICATION_CODE);
	}
	
	/**
	 * @param verificationCode the package verification code to set
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setPackageVerificationCode(SpdxPackageVerificationCode verificationCode) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_FILE_NAME, verificationCode);
		return this;
	}
	
	/**
	 * @return the sourceInfo
	 */
	public Optional<String> getSourceInfo() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PACKAGE_SOURCE_INFO);
	}
	
	/**
	 * @param sourceInfo package source info
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setSourceInfo(String sourceInfo) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_SOURCE_INFO, sourceInfo);
		return this;
	}
	
	/**
	 * @return the summary
	 */
	public Optional<String> getSummary() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PACKAGE_SHORT_DESC);
	}
	
	/**
	 * @param summary package summary
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setSummary(String summary) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_SHORT_DESC, summary);
		return this;
	}
	
	/**
	 * @return the supplier
	 */
	public Optional<String> getSupplier() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PACKAGE_SUPPLIER);
	}
	
	/**
	 * @param supplier the package supplier to set
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setSupplier(String supplier) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_SUPPLIER, supplier);
		return this;
	}
	
	/**
	 * @return the versionInfo
	 */
	public Optional<String> getVersionInfo() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_PACKAGE_VERSION_INFO);
	}
	
	/**
	 * @param versionInfo
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setVersionInfo(String versionInfo) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_VERSION_INFO, versionInfo);
		return this;
	}
	
	/**
	 * @return the externalRefs
	 * @throws InvalidSPDXAnalysisException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<ExternalRef> getExternalRefs() throws InvalidSPDXAnalysisException {
		return (Collection<ExternalRef>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_EXTERNAL_REF);
	}
	
	/**
	 * @param externalRef
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxPackage addExternalRef(ExternalRef externalRef) throws InvalidSPDXAnalysisException {
		getExternalRefs().add(externalRef);
		return this;
	}
	
	/**
	 * @return the files
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SpdxFile> getFiles() throws InvalidSPDXAnalysisException {
		return (Collection<SpdxFile>)(Collection<?>)this.getObjectPropertyValueCollection(SpdxConstants.PROP_PACKAGE_FILE);
	}
	
	/**
	 * Add a file to the collection of files attached to this package
	 * @param file
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage addFile(SpdxFile file) throws InvalidSPDXAnalysisException {
		getFiles().add(file);
		return this;
	}
	
	@Override
	public List<String> verify() {
		List<String> retval = super.verify();
		String pkgName = "UNKNOWN PACKAGE";
		try {
			Optional<String> name = getName();
			if (name.isPresent()) {
				pkgName = name.get();
			} else {
				pkgName = "UNKNOWN PACKAGE";
			}
		} catch (InvalidSPDXAnalysisException e1) {
			retval.add("Unable to get package name: "+e1.getMessage());
		}

		boolean filesAnalyzed = true;
		try {
			filesAnalyzed = isFilesAnalyzed();
		} catch (InvalidSPDXAnalysisException e1) {
			retval.add("Unable to get filesAnalyzed: "+e1.getMessage());
		}
		
		// summary - nothing really to check

		// description - nothing really to check

		// download location
		try {
			Optional<String> downloadLocation = this.getDownloadLocation();
			if (!downloadLocation.isPresent() || downloadLocation.get().isEmpty()) {
				retval.add("Missing required download location for package "+pkgName);
			} else {
				String warning = SpdxVerificationHelper.verifyDownloadLocation(downloadLocation.get());
				if (Objects.nonNull(warning)) {
					retval.add(warning);
				}
			}
		} catch (InvalidSPDXAnalysisException e1) {
			retval.add("Unable to get download location: "+e1.getMessage());
		}

		// checksum
		try {
			for (Checksum checksum:getChecksums()) {
				List<String> checksumVerify = checksum.verify();
				addNameToWarnings(checksumVerify);
				retval.addAll(checksumVerify);
			}
		}  catch (InvalidSPDXAnalysisException e1) {
			retval.add("Unable to get checksums: "+e1.getMessage());
		}

		// sourceinfo - nothing really to check

		// license declared - mandatory - 1 (need to change return values)
		try {
			Optional<AnyLicenseInfo> declaredLicense = this.getLicenseDeclared();
			if (!declaredLicense.isPresent()) {
				retval.add("Missing required declared license for package "+pkgName);
			} else {
				List<String> verify = declaredLicense.get().verify();
				addNameToWarnings(verify);
				retval.addAll(verify);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid package declared license: "+e.getMessage());
		}
		try {
			if (getLicenseInfoFromFiles().size() == 0 && filesAnalyzed) {
				retval.add("Missing required license information from files for "+pkgName);
			} else {
				boolean foundNonSimpleLic = false;
				for (AnyLicenseInfo lic:getLicenseInfoFromFiles()) {
					List<String> verify = lic.verify();
					addNameToWarnings(verify);
					retval.addAll(verify);
					if (!(lic instanceof SimpleLicensingInfo ||
							lic instanceof SpdxNoAssertionLicense ||
							lic instanceof SpdxNoneLicense ||
							lic instanceof OrLaterOperator ||
							lic instanceof WithExceptionOperator)) {
						foundNonSimpleLic = true;
					}
				}
				if (foundNonSimpleLic) {
					retval.add("license info from files contains complex licenses for "+pkgName);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid license infos from file: "+e.getMessage());
		}
		// files depends on if the filesAnalyzed flag
		try {
			if (getFiles().size() == 0) {
				if (filesAnalyzed) {
					retval.add("Missing required package files for "+pkgName);
				}
			} else {
				if (!filesAnalyzed) {
					retval.add("Warning: Found analyzed files for package "+pkgName+" when analyzedFiles is set to false.");
				}
				for (SpdxFile file:getFiles()) {
					List<String> verify = file.verify();
					addNameToWarnings(verify);
					retval.addAll(verify);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid package files: "+e.getMessage());
		}

		// verification code
		try {
			Optional<SpdxPackageVerificationCode> verificationCode = this.getPackageVerificationCode();
			if (!verificationCode.isPresent() && filesAnalyzed) {
				retval.add("Missing required package verification code for package " + pkgName);
			} else if (verificationCode.isPresent() && verificationCode.get().getValue().isPresent() && !filesAnalyzed) {
				retval.add("Verification code must not be included when files not analyzed.");
			} else if (filesAnalyzed) {
				List<String> verify = verificationCode.get().verify();
				addNameToWarnings(verify);
				retval.addAll(verify);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid package verification code: " + e.getMessage());
		}

		// supplier
		try {
			Optional<String> supplier = this.getSupplier();
			if (supplier.isPresent() && !supplier.get().isEmpty()) {
				String error = SpdxVerificationHelper.verifySupplier(supplier.get());
				if (error != null && !error.isEmpty()) {
					retval.add("Supplier error - "+error+ " for package "+pkgName);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid supplier: " + e.getMessage());
		}
		// originator
		try {
			Optional<String> originator = this.getOriginator();
			if (originator.isPresent() && !originator.get().isEmpty()) {
				String error = SpdxVerificationHelper.verifyOriginator(originator.get());
				if (error != null && !error.isEmpty()) {
					retval.add("Originator error - "+error+ " for package "+pkgName);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid originator: " + e.getMessage());
		}
		// External refs
		try {
			for (ExternalRef externalRef:getExternalRefs()) {
				retval.addAll(externalRef.verify());
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid external refs: " + e.getMessage());
		}
		return retval;
	}

	@Override
	public int compareTo(SpdxPackage pkg) {
		// sort order is determined by the name and the version		
		
		String myNameVersion = "";
		String compNameVersion = "";
		try {
			Optional<String> myName = this.getName();
			if (myName.isPresent()) {
				myNameVersion = myName.get();
			} else {
				myNameVersion = "";
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting my name",e);
		}
		try {
			Optional<String> compName = pkg.getName();
			if (compName.isPresent()) {
				compNameVersion = compName.get();
			} else {
				compNameVersion = "";
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare name",e);
		}
		try {
			Optional<String> myVersion = this.getVersionInfo();
			if (myVersion.isPresent()) {
				myNameVersion = myNameVersion + myVersion.get();
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting my version",e);
		}
		try {
			Optional<String> compVersion = pkg.getVersionInfo();
			if (compVersion != null) {
				compNameVersion = compNameVersion + compVersion.get();
			}
		} catch (InvalidSPDXAnalysisException e) {
			logger.warn("Error getting compare version",e);
		}
		return myNameVersion.compareToIgnoreCase(compNameVersion);
	}

	/**
	 * @return the Sha1 checksum value for this package, or a blank string if no
	 * sha1 checksum has been set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public String getSha1() throws InvalidSPDXAnalysisException {
		for (Checksum checksum:getChecksums()) {
			if (checksum.getAlgorithm().equals(ChecksumAlgorithm.SHA1)) {
				Optional<String> value = checksum.getValue();
				if (value.isPresent()) {
					return value.get();
				}
			}
		}
		// No sha1 found, return an empty string
		return "";
	}

}
