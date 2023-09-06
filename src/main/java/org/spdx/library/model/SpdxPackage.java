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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.Version;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.Purpose;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.OrLaterOperator;
import org.spdx.library.model.license.SimpleLicensingInfo;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.library.model.license.SpdxNoneLicense;
import org.spdx.library.model.license.WithExceptionOperator;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IModelStoreLock;

/**
 * A Package represents a collection of software files that are
 * delivered as a single functional component.
 * @author Gary O'Neall
 *
 */
public class SpdxPackage extends SpdxItem implements Comparable<SpdxPackage> {
	Collection<SpdxElement> files;

	/**
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage() throws InvalidSPDXAnalysisException {
		super();
		files = new RelatedElementCollection(this, RelationshipType.CONTAINS, SpdxConstants.CLASS_SPDX_FILE);
	}

	/**
	 * @param modelStore
	 * @param documentUri
	 * @param id
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
		files = new RelatedElementCollection(this, RelationshipType.CONTAINS, SpdxConstants.CLASS_SPDX_FILE);
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage(String id) throws InvalidSPDXAnalysisException {
		super(id);
		files = new RelatedElementCollection(this, RelationshipType.CONTAINS, SpdxConstants.CLASS_SPDX_FILE);
	}

	protected SpdxPackage(SpdxPackageBuilder spdxPackageBuilder) throws InvalidSPDXAnalysisException {
		this(spdxPackageBuilder.modelStore, spdxPackageBuilder.documentUri, spdxPackageBuilder.id, 
				spdxPackageBuilder.copyManager, true);
		setFilesAnalyzed(spdxPackageBuilder.filesAnalyzed);	// this must be done first since it impact validation
		setCopyrightText(spdxPackageBuilder.copyrightText);
		setName(spdxPackageBuilder.name);
		setLicenseConcluded(spdxPackageBuilder.concludedLicense);
		setLicenseDeclared(spdxPackageBuilder.licenseDeclared);
		
		// optional parameters - SpdxElement
		getAnnotations().addAll(spdxPackageBuilder.annotations);
		getRelationships().addAll(spdxPackageBuilder.relationships);
		setComment(spdxPackageBuilder.comment);
		
		// optional parameters - SpdxItem
		setLicenseComments(spdxPackageBuilder.licenseComments);
		getLicenseInfoFromFiles().addAll(spdxPackageBuilder.licenseInfosFromFile);
		getAttributionText().addAll(spdxPackageBuilder.attributionText);
		
		// optional parameters - SpdxPackage
		getChecksums().addAll(spdxPackageBuilder.checksums);
		setDescription(spdxPackageBuilder.description);
		setDownloadLocation(spdxPackageBuilder.downloadLocation);
		getExternalRefs().addAll(spdxPackageBuilder.externalRefs);
		getFiles().addAll(spdxPackageBuilder.files);
		setHomepage(spdxPackageBuilder.homepage);
		setOriginator(spdxPackageBuilder.originator);
		setPackageFileName(spdxPackageBuilder.pacakgeFileName);
		setPackageVerificationCode(spdxPackageBuilder.packageVerificationCode);
		setSourceInfo(spdxPackageBuilder.sourceInfo);
		setSummary(spdxPackageBuilder.summary);
		setSupplier(spdxPackageBuilder.supplier);
		setVersionInfo(spdxPackageBuilder.versionInfo);
		setPrimaryPurpose(spdxPackageBuilder.primaryPurpose);
		setBuiltDate(spdxPackageBuilder.builtDate);
		setValidUntilDate(spdxPackageBuilder.validUntilDate);
		setReleaseDate(spdxPackageBuilder.releaseDate);
	}

	@Override
	public String getType() {
		return SpdxConstants.CLASS_SPDX_PACKAGE;
	}
	
	@Override
	protected String getNamePropertyName() {
		return SpdxConstants.PROP_NAME;
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
	 * @return This field provides a place for recording the actual date the package was built.
	 * @throws InvalidSPDXAnalysisException
	 */
	public Optional<String> getBuiltDate() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_BUILT_DATE);
	}
	
	/**
	 * @param builtDate This field provides a place for recording the actual date the package was built.
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setBuiltDate(String builtDate) throws InvalidSPDXAnalysisException {
		if (strict && Objects.nonNull(builtDate) && Objects.nonNull(SpdxVerificationHelper.verifyDate(builtDate))) {
			throw new InvalidSPDXAnalysisException("Invalid built date");
		}
		setPropertyValue(SpdxConstants.PROP_BUILT_DATE, builtDate);
	}
	
	/**
	 * @return This field provides a place for recording the date the package was released.
	 * @throws InvalidSPDXAnalysisException
	 */
	public Optional<String> getReleaseDate() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_RELEASE_DATE);
	}
	
	/**
	 * @param releaseDate This field provides a place for recording the date the package was released.
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setReleaseDate(String releaseDate) throws InvalidSPDXAnalysisException {
		if (strict && Objects.nonNull(releaseDate) && Objects.nonNull(SpdxVerificationHelper.verifyDate(releaseDate))) {
			throw new InvalidSPDXAnalysisException("Invalid release date");
		}
		setPropertyValue(SpdxConstants.PROP_RELEASE_DATE, releaseDate);
	}
	
	/**
	 * @return This field provides a place for recording the end of the support period for a package from the supplier.
	 * @throws InvalidSPDXAnalysisException
	 */
	public Optional<String> getValidUntilDate() throws InvalidSPDXAnalysisException {
		return getStringPropertyValue(SpdxConstants.PROP_VALID_UNTIL_DATE);
	}
	
	/**
	 * @param validUntilDate This field provides a place for recording the end of the support period for a package from the supplier.
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setValidUntilDate(String validUntilDate) throws InvalidSPDXAnalysisException {
		if (strict && Objects.nonNull(validUntilDate) && Objects.nonNull(SpdxVerificationHelper.verifyDate(validUntilDate))) {
			throw new InvalidSPDXAnalysisException("Invalid valid until date");
		}
		setPropertyValue(SpdxConstants.PROP_VALID_UNTIL_DATE, validUntilDate);
	}
	
	
	/**
	 * @return the licenseDeclared or NOASSERTION if no license declared is found
	 * @throws InvalidSPDXAnalysisException
	 */
	public @Nullable AnyLicenseInfo getLicenseDeclared() throws InvalidSPDXAnalysisException {
		Optional<AnyLicenseInfo> retval = getAnyLicenseInfoPropertyValue(SpdxConstants.PROP_PACKAGE_DECLARED_LICENSE);
		if (retval.isPresent()) {
			return retval.get();
		} else {
			logger.warn("No declared license provided - returning NoAssertion");
			return new SpdxNoAssertionLicense();
		}
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
		return (Collection<Checksum>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstants.PROP_PACKAGE_CHECKSUM, Checksum.class);
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
		if (strict && Objects.nonNull(downloadLocation)) {
			String verify = SpdxVerificationHelper.verifyDownloadLocation(downloadLocation);
			if (Objects.nonNull(verify) && !verify.isEmpty()) {
				throw new InvalidSPDXAnalysisException(verify);
			}
		}
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
		if (strict && Objects.nonNull(homepage)) {
			if (!SpdxVerificationHelper.isValidUri(homepage)) {
				throw new InvalidSPDXAnalysisException(homepage + " is not a valid URI");
			}
		}
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
		if (strict && Objects.nonNull(originator)) {
			String verify = SpdxVerificationHelper.verifyOriginator(originator);
			if (Objects.nonNull(verify) && !verify.isEmpty()) {
				throw new InvalidSPDXAnalysisException(verify);
			}
			
		}
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
	 * @return the packageVerificationCode, null if not present
	 * @throws InvalidSPDXAnalysisException
	 */
	public Optional<SpdxPackageVerificationCode> getPackageVerificationCode() throws InvalidSPDXAnalysisException {
		Optional<?> retval = getObjectPropertyValue(SpdxConstants.PROP_PACKAGE_VERIFICATION_CODE);
		if (!retval.isPresent()) {
			return Optional.empty();
		}
		if (!(retval.get() instanceof SpdxPackageVerificationCode)) {
			throw new InvalidSPDXAnalysisException("Invalid type - expecting SpdxVerificationCode, type was "+retval.get().getClass().toString());
		}
		return Optional.of((SpdxPackageVerificationCode)retval.get());
	}
	
	/**
	 * @param verificationCode the package verification code to set
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage setPackageVerificationCode(SpdxPackageVerificationCode verificationCode) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PACKAGE_VERIFICATION_CODE, verificationCode);
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
		if (Objects.nonNull(supplier) && strict) {
			String verify = SpdxVerificationHelper.verifySupplier(supplier);
			if (Objects.nonNull(verify) && !verify.isEmpty()) {
				throw new InvalidSPDXAnalysisException(verify);
			}
		}
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
		return (Collection<ExternalRef>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstants.PROP_EXTERNAL_REF, ExternalRef.class);
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
		return (Collection<SpdxFile>)(Collection<?>)files;
	}
	
	/**
	 * Add a file to the collection of files attached to this package
	 * @param file
	 * @return this to build additional options
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage addFile(SpdxFile file) throws InvalidSPDXAnalysisException {
		if (Objects.isNull(file)) {
			throw new InvalidSPDXAnalysisException("Can not add null file to a package");
		}
		getFiles().add(file);
		return this;
	}
	
	/**
	 * @return provides information about the primary purpose of the package. Package Purpose is intrinsic to how the package is being used rather than the content of the package.
	 * @throws InvalidSPDXAnalysisException
	 */
	@SuppressWarnings("unchecked")
	public Optional<Purpose> getPrimaryPurpose() throws InvalidSPDXAnalysisException {
		Optional<Enum<?>> retval = getEnumPropertyValue(SpdxConstants.PROP_PRIMARY_PACKAGE_PURPOSE);
		if (retval.isPresent() && !(retval.get() instanceof Purpose)) {
			throw new SpdxInvalidTypeException("Invalid enum type for "+retval.get().toString());
		}
		return (Optional<Purpose>)(Optional<?>)retval;
	}
	
	/**
	 * @param purpose provides information about the primary purpose of the package. Package Purpose is intrinsic to how the package is being used rather than the content of the package.
	 * @throws InvalidSPDXAnalysisException
	 */
	public void setPrimaryPurpose(Purpose purpose) throws InvalidSPDXAnalysisException {
		setPropertyValue(SpdxConstants.PROP_PRIMARY_PACKAGE_PURPOSE, purpose);
	}
	
	/* (non-Javadoc)
	 * @see org.spdx.library.model.SpdxItem#_verify(java.util.List)
	 */
	@Override
	protected List<String> _verify(Set<String> verifiedIds, String specVersion) {
		List<String> retval = super._verify(verifiedIds, specVersion);
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
				List<String> checksumVerify = checksum.verify(verifiedIds, specVersion);
				addNameToWarnings(checksumVerify);
				retval.addAll(checksumVerify);
			}
		}  catch (InvalidSPDXAnalysisException e1) {
			retval.add("Unable to get checksums: "+e1.getMessage());
		}

		// sourceinfo - nothing really to check

		// license declared
		try {
			Optional<AnyLicenseInfo> declaredLicense = getAnyLicenseInfoPropertyValue(SpdxConstants.PROP_PACKAGE_DECLARED_LICENSE);
			if (!declaredLicense.isPresent()) {
				if (Version.versionLessThan(specVersion, Version.TWO_POINT_THREE_VERSION)) {
					retval.add("Missing required declared license for package "+pkgName);
				}
			} else {
				List<String> verify = declaredLicense.get().verify(verifiedIds, specVersion);
				addNameToWarnings(verify);
				retval.addAll(verify);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid package declared license: "+e.getMessage());
		}
		try {
		    verifyLicenseInfosInFiles(getLicenseInfoFromFiles(), filesAnalyzed, pkgName, verifiedIds, retval, specVersion);
		} catch (InvalidSPDXAnalysisException e) {
            retval.add("Invalid license infos from file: "+e.getMessage());
        }
		
		// files depends on if the filesAnalyzed flag
		try {
			if (getFiles().size() !=0 && !filesAnalyzed) {
				retval.add("Warning: Found analyzed files for package " + pkgName + " when analyzedFiles is set to false.")
			}
			for (SpdxFile file:getFiles()) {
				List<String> verify = file.verify(verifiedIds, specVersion);
				addNameToWarnings(verify);
				retval.addAll(verify);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid package files: "+e.getMessage());
		}

		// verification code
		try {
			Optional<SpdxPackageVerificationCode> verificationCode = this.getPackageVerificationCode();
			if (verificationCode.isPresent()
					&& !verificationCode.get().getValue().isEmpty()
					&& !filesAnalyzed) {
				retval.add("Verification code must not be included when files not analyzed.");
			} else if (filesAnalyzed && verificationCode.isPresent()) {
				List<String> verify = verificationCode.get().verify(verifiedIds, specVersion);
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
				retval.addAll(externalRef.verify(verifiedIds, specVersion));
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Invalid external refs: " + e.getMessage());
		}
		// built date
		try {
			Optional<String> date = getBuiltDate();
			if (date.isPresent()) {
				String err = SpdxVerificationHelper.verifyDate(date.get());
				if (Objects.nonNull(err)) {
					retval.add("Invalid built date: "+err);
				}
				if (Version.versionLessThan(specVersion, Version.TWO_POINT_THREE_VERSION)) {
					retval.add("Built date is not supported prior to release "+Version.TWO_POINT_THREE_VERSION);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting built date");
		}
		// release date
		try {
			Optional<String> date = getReleaseDate();
			if (date.isPresent()) {
				String err = SpdxVerificationHelper.verifyDate(date.get());
				if (Objects.nonNull(err)) {
					retval.add("Invalid releaes date: "+err);
				}
				if (Version.versionLessThan(specVersion, Version.TWO_POINT_THREE_VERSION)) {
					retval.add("Release date is not supported prior to release "+Version.TWO_POINT_THREE_VERSION);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting release date");
		}
		// valid until date
		try {
			Optional<String> date = getValidUntilDate();
			if (date.isPresent()) {
				String err = SpdxVerificationHelper.verifyDate(date.get());
				if (Objects.nonNull(err)) {
					retval.add("Invalid valid until date: "+err);
				}
				if (Version.versionLessThan(specVersion, Version.TWO_POINT_THREE_VERSION)) {
					retval.add("Valid until date is not supported prior to release "+Version.TWO_POINT_THREE_VERSION);
				}
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting valid until date");
		}
		// Primary purpose
		try {
			Optional<Purpose> purpose = getPrimaryPurpose();
			if (purpose.isPresent() && Version.versionLessThan(specVersion, Version.TWO_POINT_THREE_VERSION)) {
				retval.add("Primary purpose is not supported prior to release "+Version.TWO_POINT_THREE_VERSION);
			}
		} catch (InvalidSPDXAnalysisException e) {
			retval.add("Error getting primary purpose");
		}
		return retval;
	}

	private void verifyLicenseInfosInFiles(Collection<AnyLicenseInfo> licenseInfoFromFiles, 
		boolean filesAnalyzed, String pkgName, Set<String> verifiedIds, List<String> retval, String specVersion) {
		if (licenseInfoFromFiles.size() != 0 && !filesAnalyzed) {
			retval.add("License information from files must not be included when files not analyzed. Package " + pkgName);
		} else {
			boolean foundNonSimpleLic = false;
			for (AnyLicenseInfo lic:licenseInfoFromFiles) {
				List<String> verify = lic.verify(verifiedIds, specVersion);
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
			if (compVersion.isPresent()) {
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
				String value = checksum.getValue();
				if (!value.isEmpty()) {
					return value;
				}
			}
		}
		// No sha1 found, return an empty string
		return "";
	}
	
	public static class SpdxPackageBuilder {
		// required fields - Model Object
		IModelStore modelStore;
		String documentUri;
		String id;
		ModelCopyManager copyManager;
		
		// required fields - SpdxElement
		String name;
		
		AnyLicenseInfo concludedLicense;
		String copyrightText;
		
		// required fields - SpdxPackage
		AnyLicenseInfo licenseDeclared;
		
		// optional fields - SpdxElement
		Collection<Annotation> annotations = new ArrayList<Annotation>();
		Collection<Relationship> relationships = new ArrayList<Relationship>();
		String comment = null;
		
		// optional fields - SpdxItem
		Collection<AnyLicenseInfo> licenseInfosFromFile = new ArrayList<>(); // required if isFilesAnalyzed is true
		String licenseComments = null;
		Collection<String> attributionText = new ArrayList<String>();
		
		// optional fields - SpdxPackage
		Collection<Checksum> checksums = new ArrayList<Checksum>();
		String description = null;
		String downloadLocation = null;
		Collection<ExternalRef> externalRefs = new ArrayList<ExternalRef>();
		Collection<SpdxFile> files = new ArrayList<SpdxFile>(); // required if isFilesAnalyzed is true
		String homepage = null;
		String originator = null;
		String pacakgeFileName = null;
		SpdxPackageVerificationCode packageVerificationCode = null; // required if isFilesAnalyzed is true
		String sourceInfo = null;
		String summary = null;
		String supplier = null;
		String versionInfo = null;
		boolean filesAnalyzed = true;
		Purpose primaryPurpose = null;
		String builtDate = null;
		String releaseDate = null;
		String validUntilDate = null;
				
		/**
		 * Build an SpdxPackage with the required parameters if isFilesAnalyzed is false
		 * - note isFilesAnalyzed must be explicitly set to false
		 * 
		 * @param modelStore       Storage for the model objects
		 * @param documentUri      SPDX Document URI for a document associated with this
		 *                         model
		 * @param id               ID for this object - must be unique within the SPDX
		 *                         document
		 * @param name             File name
		 * @param copyManager
		 * @param concludedLicense
		 * @param copyrightText    Copyright text
		 * @param licenseDeclared  Declared license for the package
		 */
		public SpdxPackageBuilder(IModelStore modelStore, String documentUri, String id, 
				@Nullable ModelCopyManager copyManager, String name,AnyLicenseInfo concludedLicense, 
				String copyrightText, AnyLicenseInfo licenseDeclared) {
			Objects.requireNonNull(modelStore, "Model store can not be null");
			Objects.requireNonNull(documentUri, "Document URI can not be null");
			Objects.requireNonNull(id, "ID can not be null");
			Objects.requireNonNull(name, "Name can not be null");
			this.modelStore = modelStore;
			this.documentUri = documentUri;
			this.id = id;
			this.name = name;
			this.concludedLicense = concludedLicense;
			this.copyrightText = copyrightText;
			this.licenseDeclared = licenseDeclared;
			this.copyManager = copyManager;
		}
		
		/**
		 * @param annotations Annotations
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setAnnotations(Collection<Annotation> annotations) {
			Objects.requireNonNull(annotations, "Annotations can not be null");
			this.annotations = annotations;
			return this;
		}
		
		/**
		 * @param annotation Annotation to add
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addAnnotation(Annotation annotation) {
			Objects.requireNonNull(annotation, "Annotation can not be null");
			this.annotations.add(annotation);
			return this;
		}
		
		/**
		 * @param relationships Relationships
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setRelationships(Collection<Relationship> relationships) {
			Objects.requireNonNull(relationships, "Relationships can not be null");
			this.relationships = relationships;
			return this;
		}
		
		/**
		 * @param relationship Relationship to add
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addRelationship(Relationship relationship) {
			Objects.requireNonNull(relationship, "Relationship can not be null");
			this.relationships.add(relationship);
			return this;
		}
		
		/**
		 * @param comment Comment
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setComment(@Nullable String comment) {
			this.comment = comment;
			return this;
		}
		
		/**
		 * @param licenseInfosFromFile License information from all files in the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setLicenseInfosFromFile(Collection<AnyLicenseInfo> licenseInfosFromFile) {
			this.licenseInfosFromFile = licenseInfosFromFile;
			return this;
		}
		
		/**
		 * @param licenseInfo license info for a file to be added to the collection of license infos
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addLicenseInfosFromFile(AnyLicenseInfo licenseInfo) {
			this.licenseInfosFromFile.add(licenseInfo);
			return this;
		}
		
		/**
		 * @param licenseComments license comments
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setLicenseComments(@Nullable String licenseComments) {
			this.licenseComments = licenseComments;
			return this;
		}
		
		/**
		 * @param checksums Checksum
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setChecksums(Collection<Checksum> checksums) {
			Objects.requireNonNull(checksums, "Checksums can not be null");
			this.checksums = checksums;
			return this;
		}
		
		/**
		 * @param checksum Checksum to add
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addChecksum(Checksum checksum) {
			Objects.requireNonNull(checksum, "Checksum can not be null");
			this.checksums.add(checksum);
			return this;
		}
		
		/**
		 * @param description long description
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setDescription(String description) {
			Objects.requireNonNull(description, "Description can not be null");
			this.description = description;
			return this;
		}
		
		/**
		 * @param externalRefs external references to this package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setExternalRefs(Collection<ExternalRef> externalRefs) {
			Objects.requireNonNull(externalRefs, "External Refs can not be null");
			this.externalRefs = externalRefs;
			return this;
		}
		
		/**
		 * @param externalRef external reference to this package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addExternalRef(ExternalRef externalRef) {
			Objects.requireNonNull(externalRef, "External ref can not be null");
			this.externalRefs.add(externalRef);
			return this;
		}
		
		/**
		 * @param files files contained in the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setFiles(Collection<SpdxFile> files) {
			Objects.requireNonNull(files, "Files can not be null");
			this.files = files;
			return this;
		}
		
		/**
		 * @param file file to be added to the collection of files
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addFile(SpdxFile file) {
			Objects.requireNonNull(file, "File can not be null");
			this.files.add(file);
			return this;
		}

		/**
		 * @param homepage package home page
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setHomepage(String homepage) {
			Objects.requireNonNull(homepage, "Homepage can not be null");
			this.homepage = homepage;
			return this;
		}
		
		/**
		 * @param originator originator of the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setOriginator(String originator) {
			Objects.requireNonNull(originator, "Orinator can not be null");
			this.originator = originator;
			return this;
		}
		
		/**
		 * @param packageFileName file name of the archive containing the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setPackageFileName(String packageFileName) {
			Objects.requireNonNull(packageFileName, "Package file name can not be null");
			this.pacakgeFileName = packageFileName;
			return this;
		}
		
		/**
		 * @param packageVerificationCode Package verification code calculated from the files
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setPackageVerificationCode(SpdxPackageVerificationCode packageVerificationCode) {
			Objects.requireNonNull(packageVerificationCode, "Package verification code can not be null");
			this.packageVerificationCode = packageVerificationCode;
			return this;
		}
		
		/**
		 * @param sourceInfo Information on the source of the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setSourceInfo(String sourceInfo) {
			Objects.requireNonNull(sourceInfo, "Source info can not be null");
			this.sourceInfo = sourceInfo;
			return this;
		}
		
		/**
		 * @param summary Short description
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setSummary(String summary) {
			Objects.requireNonNull(summary, "Summary can not be null");
			this.summary = summary;
			return this;
		}
		
		/**
		 * @param supplier Package supplier
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setSupplier(String supplier) {
			Objects.requireNonNull(supplier, "Supplier can not be null");
			this.supplier = supplier;
			return this;
		}
		
		/**
		 * @param versionInfo Package version
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setVersionInfo(String versionInfo) {
			Objects.requireNonNull(versionInfo, "Version can not be null");
			this.versionInfo = versionInfo;
			return this;
		}
		
		/**
		 * @param filesAnalyzed if true, files were analyzed for this package - add additional required parameters files, verificationCode and licenseInfosFromFile
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setFilesAnalyzed(boolean filesAnalyzed) {
			this.filesAnalyzed = filesAnalyzed;
			return this;
		}
		
		/**
		 * @param downloadLocation download location for the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setDownloadLocation(String downloadLocation) {
			Objects.requireNonNull(downloadLocation, "Download location can not be null");
			this.downloadLocation = downloadLocation;
			return this;
		}
		
		/**
		 * @param attributionText Attribution text for the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setAttributionText(Collection<String> attributionText) {
			Objects.requireNonNull(attributionText, "Attribution text collection can not be null");
			this.attributionText = attributionText;
			return this;
		}
		
		/**
		 * @param attribution attribution to add to the attribution text
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addAttributionText(String attribution) {
			Objects.requireNonNull(attributionText, "Attribution text can not be null");
			this.attributionText.add(attribution);
			return this;
		}
		
		/**
		 * @param purpose Package Purpose is intrinsic to how the package is being used rather than the content of the package.
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setPrimaryPurpose(Purpose purpose) {
			Objects.requireNonNull(purpose, "Purpose can not be null");
			this.primaryPurpose = purpose;
			return this;
		}
		
		/**
		 * @param builtDate This field provides a place for recording the actual date the package was built.
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setBuiltDate(String builtDate) {
			Objects.requireNonNull(builtDate, "Built date can not be null");
			this.builtDate = builtDate;
			return this;
		}
		
		/**
		 * @param validUntilDate This field provides a place for recording the end of the support period for a package from the supplier.
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setValidUntilDate(String validUntilDate) {
			Objects.requireNonNull(validUntilDate, "Valid until date can not be null");
			this.validUntilDate = validUntilDate;
			return this;
		}
		
		/**
		 * @param releaseDate This field provides a place for recording the date the package was released.
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setReleaseDate(String releaseDate) {
			Objects.requireNonNull(releaseDate, "Release date can not be null");
			this.releaseDate = releaseDate;
			return this;
		}
		
		/**
		 * @return the SPDX package
		 * @throws InvalidSPDXAnalysisException
		 */
		public SpdxPackage build() throws InvalidSPDXAnalysisException {
			IModelStoreLock lock = modelStore.enterCriticalSection(documentUri, false);
			try {
				return new SpdxPackage(this);
			} finally {
				modelStore.leaveCriticalSection(lock);
			}

		}
	}

}
