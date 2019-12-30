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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxVerificationHelper;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.OrLaterOperator;
import org.spdx.library.model.license.SimpleLicensingInfo;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.library.model.license.SpdxNoneLicense;
import org.spdx.library.model.license.WithExceptionOperator;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.ModelTransaction;
import org.spdx.storage.IModelStore.ReadWrite;

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
	 * @param copyManager
	 * @param create
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage(IModelStore modelStore, String documentUri, String id, 
			@Nullable ModelCopyManager copyManager, boolean create)
			throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, copyManager, create);
	}

	/**
	 * @param id
	 * @throws InvalidSPDXAnalysisException
	 */
	public SpdxPackage(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	protected SpdxPackage(SpdxPackageBuilder spdxPackageBuilder) throws InvalidSPDXAnalysisException {
		this(spdxPackageBuilder.modelStore, spdxPackageBuilder.documentUri, spdxPackageBuilder.id, 
				spdxPackageBuilder.copyManager, true);
		setCopyrightText(spdxPackageBuilder.copyrightText);
		setName(spdxPackageBuilder.name);
		setLicenseConcluded(spdxPackageBuilder.concludedLicense);
		if (!spdxPackageBuilder.sha1.getAlgorithm().isPresent() || 
				!spdxPackageBuilder.sha1.getAlgorithm().get().equals(ChecksumAlgorithm.SHA1)) {
			throw new InvalidSPDXAnalysisException("Invalid SHA1 checksum algorithm for SpdxPackage.  Must be SHA1");
		}
		addChecksum(spdxPackageBuilder.sha1);
		setLicenseDeclared(spdxPackageBuilder.licenseDeclared);
		
		// optional parameters - SpdxElement
		getAnnotations().addAll(spdxPackageBuilder.annotations);
		getRelationships().addAll(spdxPackageBuilder.relationships);
		setComment(spdxPackageBuilder.comment);
		
		// optional parameters - SpdxItem
		setLicenseComments(spdxPackageBuilder.licenseComments);
		getLicenseInfoFromFiles().addAll(spdxPackageBuilder.licenseInfosFromFile);
		
		// optional parameters - SpdxPackage
		Iterator<Checksum> iter = spdxPackageBuilder.checksums.iterator();
		while (iter.hasNext()) {
			Checksum cksum = iter.next();
			if (!cksum.equals(spdxPackageBuilder.sha1)) {
				getChecksums().add(cksum);
			}
		}

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
		setFilesAnalyzed(spdxPackageBuilder.filesAnalyzed);
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
	public Optional<AnyLicenseInfo> getLicenseDeclared() throws InvalidSPDXAnalysisException {
		return getAnyLicenseInfoPropertyValue(SpdxConstants.PROP_PACKAGE_DECLARED_LICENSE);
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
		return (Collection<SpdxFile>)(Collection<?>)this.getObjectPropertyValueSet(SpdxConstants.PROP_PACKAGE_FILE, SpdxFile.class);
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
			if (checksum.getAlgorithm().get().equals(ChecksumAlgorithm.SHA1)) {
				Optional<String> value = checksum.getValue();
				if (value.isPresent()) {
					return value.get();
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
		
		// required fields - SpdxItem
		AnyLicenseInfo concludedLicense;
		String copyrightText;
		
		// required fields - SpdxPackage
		AnyLicenseInfo licenseDeclared;
		Checksum sha1;
		
		// optional fields - SpdxElement
		Collection<Annotation> annotations = new ArrayList<Annotation>();
		Collection<Relationship> relationships = new ArrayList<Relationship>();
		String comment = null;
		
		// optional fields - SpdxItem
		Collection<AnyLicenseInfo> licenseInfosFromFile; // required if isFilesAnalyzed is true
		String licenseComments = null;
		
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
				
		/**
		 * Build an SpdxPackage with the required parameters if isFilesAnalyzed is false - note isFilesAnalyzed must be explicitly set to false
		 * @param modelStore Storage for the model objects
		 * @param documentUri SPDX Document URI for a document associated with this model
		 * @param id ID for this object - must be unique within the SPDX document
		 * @param name - File name
		 * @param concludedLicense license concluded
		 * @param licenseInfosFromFile collection of seen licenses
		 * @param copyrightText Copyright text
		 * @param sha1 - Sha1 checksum value
		 * @param licenseDeclared - Declared license for the package
		 */
		public SpdxPackageBuilder(IModelStore modelStore, String documentUri, String id, 
				@Nullable ModelCopyManager copyManager, String name,AnyLicenseInfo concludedLicense, 
				String copyrightText, Checksum sha1, AnyLicenseInfo licenseDeclared) {
			Objects.requireNonNull(modelStore);
			Objects.requireNonNull(documentUri);
			Objects.requireNonNull(id);
			Objects.requireNonNull(name);
			Objects.requireNonNull(concludedLicense);
			Objects.requireNonNull(copyrightText);
			Objects.requireNonNull(licenseDeclared);
			Objects.requireNonNull(sha1);
			this.modelStore = modelStore;
			this.documentUri = documentUri;
			this.id = id;
			this.name = name;
			this.concludedLicense = concludedLicense;
			this.copyrightText = copyrightText;
			this.sha1 = sha1;
			this.licenseDeclared = licenseDeclared;
			this.copyManager = copyManager;
		}
		
		/**
		 * @param annotations Annotations
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setAnnotations(Collection<Annotation> annotations) {
			Objects.requireNonNull(annotations);
			this.annotations = annotations;
			return this;
		}
		
		/**
		 * @param annotation Annotation to add
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addAnnotation(Annotation annotation) {
			Objects.requireNonNull(annotation);
			this.annotations.add(annotation);
			return this;
		}
		
		/**
		 * @param relationships Relationships
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setRelationships(Collection<Relationship> relationships) {
			Objects.requireNonNull(relationships);
			this.relationships = relationships;
			return this;
		}
		
		/**
		 * @param relationship Relationship to add
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addRelationship(Relationship relationship) {
			Objects.requireNonNull(relationship);
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
			Objects.requireNonNull(checksums);
			this.checksums = checksums;
			return this;
		}
		
		/**
		 * @param checksum Checksum to add
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addChecksum(Checksum checksum) {
			Objects.requireNonNull(checksum);
			this.checksums.add(checksum);
			return this;
		}
		
		/**
		 * @param description long description
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setDescription(String description) {
			Objects.requireNonNull(description);
			this.description = description;
			return this;
		}
		
		/**
		 * @param externalRefs external references to this package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setExternalRefs(Collection<ExternalRef> externalRefs) {
			Objects.requireNonNull(externalRefs);
			this.externalRefs = externalRefs;
			return this;
		}
		
		/**
		 * @param externalRef external reference to this package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addExternalRef(ExternalRef externalRef) {
			Objects.requireNonNull(externalRef);
			this.externalRefs.add(externalRef);
			return this;
		}
		
		/**
		 * @param files files contained in the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setFiles(Collection<SpdxFile> files) {
			Objects.requireNonNull(files);
			this.files = files;
			return this;
		}
		
		/**
		 * @param file file to be added to the collection of files
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder addFile(SpdxFile file) {
			Objects.requireNonNull(file);
			this.files.add(file);
			return this;
		}

		/**
		 * @param homepage package home page
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setHomepage(String homepage) {
			Objects.requireNonNull(homepage);
			this.homepage = homepage;
			return this;
		}
		
		/**
		 * @param originator originator of the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setOriginator(String originator) {
			Objects.requireNonNull(originator);
			this.originator = originator;
			return this;
		}
		
		/**
		 * @param packageFileName file name of the archive containing the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setPackageFileName(String packageFileName) {
			Objects.requireNonNull(packageFileName);
			this.pacakgeFileName = packageFileName;
			return this;
		}
		
		/**
		 * @param packageVerificationCode Package verification code calculated from the files
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setPackageVerificationCode(SpdxPackageVerificationCode packageVerificationCode) {
			Objects.requireNonNull(packageVerificationCode);
			this.packageVerificationCode = packageVerificationCode;
			return this;
		}
		
		/**
		 * @param sourceInfo Information on the source of the package
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setSourceInfo(String sourceInfo) {
			Objects.requireNonNull(sourceInfo);
			this.sourceInfo = sourceInfo;
			return this;
		}
		
		/**
		 * @param summary Short description
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setSummary(String summary) {
			Objects.requireNonNull(summary);
			this.summary = summary;
			return this;
		}
		
		/**
		 * @param supplier Package supplier
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setSupplier(String supplier) {
			Objects.requireNonNull(supplier);
			this.supplier = supplier;
			return this;
		}
		
		/**
		 * @param versionInfo Package version
		 * @return this to continue the build
		 */
		public SpdxPackageBuilder setVersionInfo(String versionInfo) {
			Objects.requireNonNull(versionInfo);
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
			Objects.requireNonNull(downloadLocation);
			this.downloadLocation = downloadLocation;
			return this;
		}
		
		public SpdxPackage build() throws InvalidSPDXAnalysisException {
			ModelTransaction transaction = null;
			try {
				transaction = modelStore.beginTransaction(documentUri, ReadWrite.WRITE);
				try {
					return new SpdxPackage(this);
				} finally {
					transaction.commit();
					transaction.close();
				}
			} catch (IOException e) {
				logger.error("IO Exception in transaction for SPDX package build",e);
				throw new InvalidSPDXAnalysisException("IO Exception in transaction for SPDX package build",e);
			}
		}
	}

}
