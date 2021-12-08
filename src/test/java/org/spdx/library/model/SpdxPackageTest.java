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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.Version;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.FileType;
import org.spdx.library.model.enumerations.Purpose;
import org.spdx.library.model.enumerations.ReferenceCategory;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

/**
 * @author Gary O'Neall
 *
 */
public class SpdxPackageTest extends TestCase {
	
	static final String DOCUMENT_NAMESPACE = "http://doc/name/space#";
	static final String PKG_NAME1 = "PackageName1";
	static final String PKG_NAME2 = "PackageName2";
	static final String PKG_COMMENT1 = "Comment1";
	static final String PKG_COMMENT2 = "Comment2";
	static final String DESCRIPTION1 = "Description 1";
	static final String DESCRIPTION2 = "Description 2";
	static final String DOWNLOAD_LOCATION1 = "git+git://git.myproject.org/MyProject";
	static final String DOWNLOAD_LOCATION2 = "hg+https://hg.myproject.org/MyProject#src/somefile.c";
	static final String HOMEPAGE1 = "http://home.page.one/one";
	static final String HOMEPAGE2 = "http://home.page.two/two2";
	static final String ORIGINATOR1 = "Organization: Originator1";
	static final String ORIGINATOR2 = "Organization: Originator2";
	static final String PACKAGEFILENAME1 = "PkgFileName1";
	static final String PACKAGEFILENAME2 = "PkgFileName2";
	static final String SOURCEINFO1 = "SourceInfo1";
	static final String SOURCEINFO2 = "SourceInfo2";
	static final String SUMMARY1 = "Summary 1";
	static final String SUMMARY2 = "Summary 2";
	static final String SUPPLIER1 = "Person: supplier1";
	static final String SUPPLIER2 = "Person: supplier2";
	static final String VERSION1 = "V1";
	static final String VERSION2 = "V2";
	
	static final String DATE_NOW = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT).format(new Date());
	static final String DATE_THEN = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT).format(new GregorianCalendar(2021, Calendar.JANUARY, 11).getTime());
	
	SpdxElement RELATED_ELEMENT1;
	SpdxElement RELATED_ELEMENT2;
	Relationship RELATIONSHIP1;
	Relationship RELATIONSHIP2;
	ExtractedLicenseInfo LICENSE1;
	ExtractedLicenseInfo LICENSE2;
	ExtractedLicenseInfo LICENSE3;
	static final String COPYRIGHT_TEXT1 = "copyright text 1";
	static final String COPYRIGHT_TEXT2 = "copyright text 2";
	static final String LICENSE_COMMENT1 = "License Comment 1";
	static final String LICENSE_COMMENT2 = "License comment 2";
	
	FileType FILE_TYPE1 = FileType.IMAGE;
	FileType FILE_TYPE2 = FileType.AUDIO;
	
	Checksum CHECKSUM1;
	Checksum CHECKSUM2;
	Checksum CHECKSUM3;
	Checksum CHECKSUM4;
	
	SpdxFile FILE1;
	SpdxFile FILE2;
	
	
	SpdxPackageVerificationCode VERIFICATION_CODE1;
	SpdxPackageVerificationCode VERIFICATION_CODE2;
	
	ReferenceType REF_TYPE1;
	ExternalRef EXTERNAL_REF1;
	ReferenceType REF_TYPE2;
	ExternalRef EXTERNAL_REF2;
	
	Annotation ANNOTATION1;
	Annotation ANNOTATION2;
	
	GenericModelObject gmo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		CHECKSUM1 = gmo.createChecksum(ChecksumAlgorithm.SHA1, 
				"2fd4e1c67a2d28fced849ee1bb76e7391b93eb12");
		CHECKSUM2 = gmo.createChecksum(ChecksumAlgorithm.MD5, 
				"098f6bcd4621d373cade4e832627b4f6");
		CHECKSUM3 = gmo.createChecksum(ChecksumAlgorithm.SHA256, 
				"9F86D081884C7D659A2FEAA0C55AD015A3BF4F1B2B0B822CD15D6C15B0F00A08".toLowerCase());
		CHECKSUM4 = gmo.createChecksum(ChecksumAlgorithm.SHA1, 
				"dddde1c67a2d28fced849ee1bb76e7391b93eb12");
		VERIFICATION_CODE1 = gmo.createPackageVerificationCode(
				"2222e1c67a2d28fced849ee1bb76e7391b93eb12", 
				Arrays.asList(new String[] {"Excluded1", "Excluded2"}));
		VERIFICATION_CODE2 = gmo.createPackageVerificationCode(
				"3333e1c67a2d28fced849ee1bb76e7391b93eb12", 
				Arrays.asList(new String[] {"Excluded3"}));
		ANNOTATION1 = gmo.createAnnotation("Person: Annotator1", 
				AnnotationType.OTHER, DATE_NOW, "Comment1");
		ANNOTATION2 = gmo.createAnnotation("Tool: Annotator2", 
				AnnotationType.REVIEW, DATE_NOW, "Comment2");
		LICENSE1 = new ExtractedLicenseInfo("LicenseRef-1", "License Text 1");
		LICENSE2 = new ExtractedLicenseInfo("LicenseRef-2", "License Text 2");
		LICENSE3 = new ExtractedLicenseInfo("LicenseRef-3", "License Text 3");
		
		FILE1 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"FileName1", LICENSE1, Arrays.asList(new AnyLicenseInfo[] {LICENSE2}), 
				COPYRIGHT_TEXT1, CHECKSUM1)
				.setComment("File Comment1")
				.setLicenseComments(LICENSE_COMMENT1)
				.setFileTypes(Arrays.asList(new FileType[] {FILE_TYPE1}))
				.setFileContributors(Arrays.asList(new String[] {"Contrib 1", "Contrib2"}))
				.setNoticeText("NoticeText1")
				.build();
				
		FILE2 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"FileName2", LICENSE2, Arrays.asList(new AnyLicenseInfo[] {LICENSE1, LICENSE2}), 
				COPYRIGHT_TEXT2, CHECKSUM4)
				.setComment("File Comment2")
				.setLicenseComments(LICENSE_COMMENT2)
				.setFileTypes(Arrays.asList(new FileType[] {FILE_TYPE2}))
				.setFileContributors(Arrays.asList(new String[] {"Contrib 3"}))
				.setNoticeText("NoticeTExt2")
				.build();
		
		RELATED_ELEMENT1 = new GenericSpdxElement();
		RELATED_ELEMENT1.setName("relatedElementName1")
			.setComment("related element comment 1");
		RELATED_ELEMENT2 = new GenericSpdxElement();
		RELATED_ELEMENT2.setName("relatedElementName2")
			.setComment("related element comment 2");
		RELATIONSHIP1 = gmo.createRelationship(RELATED_ELEMENT1, 
				RelationshipType.CONTAINS, "Relationship Comment1");
		RELATIONSHIP2 = gmo.createRelationship(RELATED_ELEMENT2, 
				RelationshipType.DYNAMIC_LINK, "Relationship Comment2");
		
		REF_TYPE1 = new ReferenceType(SpdxConstants.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "npm");
		REF_TYPE2 = new ReferenceType(SpdxConstants.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "cpe23Type");
		EXTERNAL_REF1 = gmo.createExternalRef(ReferenceCategory.PACKAGE_MANAGER,
				REF_TYPE1, "locator1", "comment1");
		EXTERNAL_REF2 = gmo.createExternalRef(ReferenceCategory.SECURITY, REF_TYPE2, "locator1", "comment2");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		pkg.setStrict(false);
		List<String> result = pkg.verify();
		assertEquals(0, result.size());
		// verification code
		pkg.setPackageVerificationCode(null);
		assertEquals(1, pkg.verify().size());
		
		// Make sure no files are allowed when filesAnalyzed is false
		pkg.setFilesAnalyzed(false);
		assertEquals(1, pkg.verify().size());
		
		//Make sure we're valid with no files and no verification code when filesAnalyzed = false.
		pkg.getFiles().clear();
		assertEquals(0, pkg.verify().size());	
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id1 = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		String id2 = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id1, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setSourceInfo(SOURCEINFO1)
				.setVersionInfo(VERSION1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertTrue(pkg.equivalent(pkg));
		SpdxPackage pkg2 = gmo.createPackage(id2, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setSourceInfo(SOURCEINFO1)
				.setVersionInfo(VERSION1)
				.setExternalRefs(externalRefs)
				.build();
		assertTrue(pkg.equivalent(pkg2));
		// Checksums
		pkg2.getChecksums().clear();
		pkg2.getChecksums().add(CHECKSUM2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.getChecksums().clear();
		pkg2.getChecksums().add(CHECKSUM1);
		pkg2.getChecksums().addAll(checksums);
		assertTrue(pkg.equivalent(pkg2));
		// Description
		pkg2.setDescription(DESCRIPTION2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setDescription(DESCRIPTION1);
		assertTrue(pkg.equivalent(pkg2));
		// download location
		pkg2.setDownloadLocation(DOWNLOAD_LOCATION2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setDownloadLocation(DOWNLOAD_LOCATION1);
		assertTrue(pkg.equivalent(pkg2));
		// files
		pkg2.getFiles().clear();
		pkg2.getFiles().add(FILE1);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.getFiles().clear();
		pkg2.getFiles().addAll(files);
		assertTrue(pkg.equivalent(pkg2));
		// homepage
		pkg2.setHomepage(HOMEPAGE2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setHomepage(HOMEPAGE1);
		assertTrue(pkg.equivalent(pkg2));
		// originator
		pkg2.setOriginator(ORIGINATOR2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setOriginator(ORIGINATOR1);
		assertTrue(pkg.equivalent(pkg2));
		// packagefilename
		pkg2.setPackageFileName(PACKAGEFILENAME2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setPackageFileName(PACKAGEFILENAME1);
		assertTrue(pkg.equivalent(pkg2));
		// verification code
		pkg2.setPackageVerificationCode(VERIFICATION_CODE2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setPackageVerificationCode(VERIFICATION_CODE1);
		assertTrue(pkg.equivalent(pkg2));
		// soruceinfo
		pkg2.setSourceInfo(SOURCEINFO2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setSourceInfo(SOURCEINFO1);
		assertTrue(pkg.equivalent(pkg2));
		// summary
		pkg2.setSummary(SUMMARY2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setSummary(SUMMARY1);
		assertTrue(pkg.equivalent(pkg2));
		// supplier
		pkg2.setSupplier(SUPPLIER2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setSupplier(SUPPLIER1);
		assertTrue(pkg.equivalent(pkg2));
		// version
		pkg2.setVersionInfo(VERSION2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setVersionInfo(VERSION1);
		assertTrue(pkg.equivalent(pkg2));
		// ExternalRefs
		pkg2.getExternalRefs().clear();
		pkg2.getExternalRefs().addAll(Arrays.asList(new ExternalRef[] {EXTERNAL_REF1, EXTERNAL_REF2}));
		assertFalse(pkg.equivalent(pkg2));
		pkg2.getExternalRefs().clear();
		pkg2.getExternalRefs().addAll(externalRefs);
		assertTrue(pkg.equivalent(pkg2));
		// Files Analyzed
		pkg2.setFilesAnalyzed(false);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setFilesAnalyzed(true);
		assertTrue(pkg.equivalent(pkg2));
		
		// Source info
		pkg2.setSourceInfo(SOURCEINFO2);
		assertFalse(pkg.equivalent(pkg2));
		pkg2.setSourceInfo(SOURCEINFO1);
		assertTrue(pkg.equivalent(pkg2));
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setFilesAnalyzed(java.lang.Boolean)}.
	 */
	public void testSetFilesAnalyzed() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertTrue(pkg.isFilesAnalyzed());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertTrue(pkg2.isFilesAnalyzed());
		pkg.setFilesAnalyzed(false);
		assertFalse(pkg2.isFilesAnalyzed());
		assertFalse(pkg.isFilesAnalyzed());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setLicenseDeclared(org.spdx.library.model.license.AnyLicenseInfo)}.
	 */
	public void testSetLicenseDeclared() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setSourceInfo(SOURCEINFO1)
				.setVersionInfo(VERSION1)
				.setExternalRefs(externalRefs)
				.build();
		
		
		assertEquals(LICENSE3, pkg.getLicenseDeclared());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(LICENSE3, pkg2.getLicenseDeclared());
		pkg.setLicenseDeclared(LICENSE1);
		assertEquals(LICENSE1, pkg.getLicenseDeclared());
		assertEquals(LICENSE1, pkg2.getLicenseDeclared());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#addChecksum(org.spdx.library.model.Checksum)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testAddChecksum() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.addChecksum(CHECKSUM1)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSourceInfo(SOURCEINFO1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setExternalRefs(externalRefs)
				.build();
		
		List<Checksum> expected = Arrays.asList(new Checksum[] {CHECKSUM1, CHECKSUM2});
		assertEquals(1, pkg.getChecksums().size());
		pkg.addChecksum(CHECKSUM2);
		assertTrue(collectionsSame(expected, pkg.getChecksums()));
	}
	
	private boolean collectionsSame(Collection<? extends Object> c1, Collection<? extends Object> c2) {
		if (c1.size() != c2.size()) {
			return false;
		}
		for (Object c:c1) {
			if (!c2.contains(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setDescription(java.lang.String)}.
	 */
	public void testSetDescription() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		
		assertEquals(DESCRIPTION1, pkg.getDescription().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(DESCRIPTION1, pkg2.getDescription().get());
		pkg.setDescription(DESCRIPTION2);
		assertEquals(DESCRIPTION2, pkg2.getDescription().get());
		assertEquals(DESCRIPTION2, pkg2.getDescription().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setDownloadLocation(java.lang.String)}.
	 */
	public void testSetDownloadLocation() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		
		assertEquals(DOWNLOAD_LOCATION1, pkg.getDownloadLocation().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(DOWNLOAD_LOCATION1, pkg2.getDownloadLocation().get());
		pkg.setDownloadLocation(DOWNLOAD_LOCATION2);
		assertEquals(DOWNLOAD_LOCATION2, pkg.getDownloadLocation().get());
		assertEquals(DOWNLOAD_LOCATION2, pkg2.getDownloadLocation().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setHomepage(java.lang.String)}.
	 */
	public void testSetHomepage() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		
		assertEquals(HOMEPAGE1, pkg.getHomepage().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(HOMEPAGE1, pkg2.getHomepage().get());
		pkg.setHomepage(HOMEPAGE2);
		assertEquals(HOMEPAGE2, pkg2.getHomepage().get());
		assertEquals(HOMEPAGE2, pkg.getHomepage().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setOriginator(java.lang.String)}.
	 */
	public void testSetOriginator() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		
		assertEquals(ORIGINATOR1, pkg.getOriginator().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(ORIGINATOR1, pkg2.getOriginator().get());
		pkg.setOriginator(ORIGINATOR2);
		assertEquals(ORIGINATOR2, pkg2.getOriginator().get());
		assertEquals(ORIGINATOR2, pkg.getOriginator().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setPackageFileName(java.lang.String)}.
	 */
	public void testSetPackageFileName() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		assertEquals(PACKAGEFILENAME1, pkg.getPackageFileName().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(PACKAGEFILENAME1, pkg2.getPackageFileName().get());
		pkg.setPackageFileName(PACKAGEFILENAME2);
		assertEquals(PACKAGEFILENAME2, pkg.getPackageFileName().get());
		assertEquals(PACKAGEFILENAME2, pkg2.getPackageFileName().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setPackageVerificationCode(org.spdx.library.model.SpdxPackageVerificationCode)}.
	 */
	public void testSetPackageVerificationCode() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		assertTrue(VERIFICATION_CODE1.equivalent(pkg.getPackageVerificationCode().get()));
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);;
		assertTrue(VERIFICATION_CODE1.equivalent(pkg2.getPackageVerificationCode().get()));
		pkg.setPackageVerificationCode(VERIFICATION_CODE2);
		assertTrue(VERIFICATION_CODE2.equivalent(pkg2.getPackageVerificationCode().get()));
		assertTrue(VERIFICATION_CODE2.equivalent(pkg.getPackageVerificationCode().get()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setSourceInfo(java.lang.String)}.
	 */
	public void testSetSourceInfo() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setExternalRefs(externalRefs)
				.setSourceInfo(SOURCEINFO1)
				.build();

		assertEquals(SOURCEINFO1, pkg.getSourceInfo().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(SOURCEINFO1, pkg2.getSourceInfo().get());
		pkg.setSourceInfo(SOURCEINFO2);
		assertEquals(SOURCEINFO2, pkg.getSourceInfo().get());
		assertEquals(SOURCEINFO2, pkg2.getSourceInfo().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setSummary(java.lang.String)}.
	 */
	public void testSetSummary() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		assertEquals(SUMMARY1, pkg.getSummary().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(SUMMARY1, pkg2.getSummary().get());
		pkg.setSummary(SUMMARY2);
		assertEquals(SUMMARY2, pkg.getSummary().get());
		assertEquals(SUMMARY2, pkg2.getSummary().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setSupplier(java.lang.String)}.
	 */
	public void testSetSupplier() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		assertEquals(SUPPLIER1, pkg.getSupplier().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(SUPPLIER1, pkg2.getSupplier().get());
		pkg.setSupplier(SUPPLIER2);
		assertEquals(SUPPLIER2, pkg.getSupplier().get());
		assertEquals(SUPPLIER2, pkg2.getSupplier().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#setVersionInfo(java.lang.String)}.
	 */
	public void testSetVersionInfo() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		assertEquals(VERSION1, pkg.getVersionInfo().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(VERSION1, pkg2.getVersionInfo().get());
		pkg.setVersionInfo(VERSION2);
		assertEquals(VERSION2, pkg2.getVersionInfo().get());
		assertEquals(VERSION2, pkg.getVersionInfo().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#addExternalRef(org.spdx.library.model.ExternalRef)}.
	 */
	public void testAddExternalRef() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs1 = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		List<ExternalRef> externalRefs2 = Arrays.asList(new ExternalRef[] {EXTERNAL_REF2, EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs1)
				.build();

		assertTrue(collectionsSame(externalRefs1, pkg.getExternalRefs()));
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertTrue(collectionsSame(externalRefs1, pkg2.getExternalRefs()));
		pkg.addExternalRef(EXTERNAL_REF2);
		assertTrue(collectionsSame(externalRefs2, pkg.getExternalRefs()));
		assertTrue(collectionsSame(externalRefs2, pkg2.getExternalRefs()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#addFile(org.spdx.library.model.SpdxFile)}.
	 */
	public void testAddFile() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();

		Collection<SpdxFile> result = pkg.getFiles();
		assertEquals(1, result.size());
		assertTrue(result.contains(FILE1));
		pkg.addFile(FILE2);
		assertTrue(collectionsSame(Arrays.asList(new SpdxFile[] {FILE1, FILE2}), pkg.getFiles()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#compareTo(org.spdx.library.model.SpdxPackage)}.
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		SpdxPackage sameName = gmo.createPackage(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		SpdxPackage pkg3 = gmo.createPackage(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				PKG_NAME2, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertEquals(0, pkg.compareTo(sameName));
		assertEquals(0, sameName.compareTo(pkg));
		assertTrue(pkg.compareTo(pkg3) < 0);
		assertTrue(pkg3.compareTo(pkg) > 0);
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxPackage#getSha1()}.
	 */
	public void testGetSha1() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertEquals(CHECKSUM1.getValue(), pkg.getSha1());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(CHECKSUM1.getValue(), pkg2.getSha1());
		pkg2.getChecksums().clear();
		String sha1Value = "5222e1c67a2d28fced849ee1bb76e7391b93eb12";
		pkg2.getChecksums().add(gmo.createChecksum(ChecksumAlgorithm.SHA1, sha1Value));
		assertEquals(sha1Value, pkg2.getSha1());
		assertEquals(sha1Value, pkg.getSha1());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxItem#setLicenseConcluded(org.spdx.library.model.license.AnyLicenseInfo)}.
	 */
	public void testSetLicenseConcluded() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		assertEquals(LICENSE1, pkg.getLicenseConcluded());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(LICENSE1, pkg2.getLicenseConcluded());
		pkg2.setLicenseConcluded(LICENSE2);
		assertEquals(LICENSE2, pkg2.getLicenseConcluded());
		assertEquals(LICENSE2, pkg.getLicenseConcluded());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxItem#setCopyrightText(java.lang.String)}.
	 */
	public void testSetCopyrightText() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertEquals(COPYRIGHT_TEXT1, pkg.getCopyrightText());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(COPYRIGHT_TEXT1, pkg2.getCopyrightText());
		pkg.setCopyrightText(COPYRIGHT_TEXT2);
		assertEquals(COPYRIGHT_TEXT2, pkg2.getCopyrightText());
		assertEquals(COPYRIGHT_TEXT2, pkg.getCopyrightText());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxItem#setLicenseComments(java.lang.String)}.
	 */
	public void testSetLicenseComments()  throws InvalidSPDXAnalysisException{
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertEquals(LICENSE_COMMENT1, pkg.getLicenseComments().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(LICENSE_COMMENT1, pkg2.getLicenseComments().get());
		pkg2.setLicenseComments(LICENSE_COMMENT2);
		assertEquals(LICENSE_COMMENT2, pkg2.getLicenseComments().get());
		assertEquals(LICENSE_COMMENT2, pkg.getLicenseComments().get());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxElement#addAnnotation(org.spdx.library.model.Annotation)}.
	 */
	public void testAddAnnotation() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations1 = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Annotation> annotations2 = Arrays.asList(new Annotation[] {ANNOTATION1, ANNOTATION2});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations1)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertTrue(collectionsSame(annotations1, pkg.getAnnotations()));
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertTrue(collectionsSame(annotations1, pkg2.getAnnotations()));
		pkg2.addAnnotation(ANNOTATION2);
		assertTrue(collectionsSame(annotations2, pkg2.getAnnotations()));
		assertTrue(collectionsSame(annotations2, pkg.getAnnotations()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxElement#addRelationship(org.spdx.library.model.Relationship)}.
	 */
	public void testAddRelationship() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships1 = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Relationship> relationships2 = Arrays.asList(new Relationship[] {RELATIONSHIP1, RELATIONSHIP2});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships1)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		assertTrue(collectionsSame(relationships1, pkg.getRelationships()));
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertTrue(collectionsSame(relationships1, pkg2.getRelationships()));
		pkg2.addRelationship(RELATIONSHIP2);
		assertTrue(collectionsSame(relationships2, pkg2.getRelationships()));
		assertTrue(collectionsSame(relationships2, pkg.getRelationships()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxElement#setComment(java.lang.String)}.
	 */
	public void testSetComment() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertEquals(PKG_COMMENT1, pkg.getComment().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(PKG_COMMENT1, pkg2.getComment().get());
		pkg2.setComment(PKG_COMMENT2);
		assertEquals(PKG_COMMENT2, pkg2.getComment().get());
		assertEquals(PKG_COMMENT2, pkg.getComment().get());
	}
	
	public void testSetAttributionText() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		String ATT1 = "attribution 1";
		String ATT2 = "attribution 2";

		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.addAttributionText(ATT1)
				.build();
		
		assertEquals(1, pkg.getAttributionText().size());
		assertTrue(pkg.getAttributionText().contains(ATT1));
		pkg.getAttributionText().add(ATT2);
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(2, pkg2.getAttributionText().size());
		assertTrue(pkg2.getAttributionText().contains(ATT1));
		assertTrue(pkg2.getAttributionText().contains(ATT2));
		pkg2.getAttributionText().clear();
		assertEquals(0, pkg2.getAttributionText().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxElement#setName(java.lang.String)}.
	 */
	public void testSetNameString() throws InvalidSPDXAnalysisException {
		List<Annotation> annotations = Arrays.asList(new Annotation[] {ANNOTATION1});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {RELATIONSHIP1});
		List<Checksum> checksums = Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM1});
		List<SpdxFile> files = Arrays.asList(new SpdxFile[] {FILE1, FILE2});
		List<AnyLicenseInfo> licenseFromFiles = Arrays.asList(new AnyLicenseInfo[] {LICENSE2});
		String id = gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri());
		List<ExternalRef> externalRefs = Arrays.asList(new ExternalRef[] {EXTERNAL_REF1});
		
		SpdxPackage pkg = gmo.createPackage(id, PKG_NAME1, LICENSE1, COPYRIGHT_TEXT1, LICENSE3)
				.setChecksums(checksums)
				.setComment(PKG_COMMENT1)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseFromFiles)
				.setLicenseComments(LICENSE_COMMENT1)
				.setDescription(DESCRIPTION1)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFiles(files)
				.setHomepage(HOMEPAGE1)
				.setOriginator(ORIGINATOR1)
				.setPackageFileName(PACKAGEFILENAME1)
				.setPackageVerificationCode(VERIFICATION_CODE1)
				.setSummary(SUMMARY1)
				.setSupplier(SUPPLIER1)
				.setVersionInfo(VERSION1)
				.setSourceInfo(SOURCEINFO1)
				.setExternalRefs(externalRefs)
				.build();
		
		assertEquals(PKG_NAME1, pkg.getName().get());
		SpdxPackage pkg2 = new SpdxPackage(pkg.getModelStore(), pkg.getDocumentUri(), pkg.getId(), pkg.getCopyManager(), false);
		assertEquals(PKG_NAME1, pkg2.getName().get());
		pkg2.setName(PKG_NAME2);
		assertEquals(PKG_NAME2, pkg2.getName().get());
		assertEquals(PKG_NAME2, pkg.getName().get());
	}
	
	public void testDownloadPattern() {
	    assertTrue(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
	            "git://git.myproject.org/MyProject.git@master").matches());
       assertTrue(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
               "git://git.myproject.org/MyOrg/MyProject.git@master").matches());
        assertTrue(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
                "git+git@git.myproject.org:MyProject").matches());
        assertTrue(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
                "git+git@git.myproject.org:MyOrg/MyProject").matches());
        assertTrue(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
                "git+git@git.myproject.org:MyProject.git").matches());
        assertTrue(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
                "git+git@git.myproject.org:MyOrg/MyProject.git").matches());
        assertTrue(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
                "git+git@git.myproject.org:MyProject@main").matches());
        assertTrue(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
                "git+git@git.myproject.org:MyProject@6338c7a2525e055a05bae1580e4dd189c2feff7b").matches());
        assertFalse(SpdxConstants.DOWNLOAD_LOCATION_PATTERN.matcher(
                "something@git.myproject.org:MyProject@6338c7a2525e055a05bae1580e4dd189c2feff7b").matches());
	}
	
	// Test to verify spec versions prior to 2.3 fail verify for missing license or copyright fields
	public void testVerify23Fields() throws InvalidSPDXAnalysisException {
		// previously required fields
		SpdxPackage pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, null, null, null)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.build();


		assertEquals(0, pkg.verify().size());
		assertTrue(pkg.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
		
		// BuiltDate
		pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, LICENSE1, "copyright", LICENSE2)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.setBuiltDate(DATE_NOW)
				.build();
		assertEquals(0, pkg.verify().size());
		assertTrue(pkg.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
		
		// Release Date
		pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, LICENSE1, "copyright", LICENSE2)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.setReleaseDate(DATE_NOW)
				.build();
		assertEquals(0, pkg.verify().size());
		assertTrue(pkg.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
		
		// Valid Until Date
		pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, LICENSE1, "copyright", LICENSE2)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.setValidUntilDate(DATE_NOW)
				.build();
		assertEquals(0, pkg.verify().size());
		assertTrue(pkg.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
		
		// Primary Purpose
		pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, LICENSE1, "copyright", LICENSE2)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.setPrimaryPurpose(Purpose.APPLICATION)
				.build();
		assertEquals(0, pkg.verify().size());
		assertTrue(pkg.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
		
		// Relationship Type REQUIREMENT_DESCRIPTION_FOR
		Relationship rel = gmo.createRelationship(FILE1, RelationshipType.REQUIREMENT_DESCRIPTION_FOR, "");
		pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, LICENSE1, "copyright", LICENSE2)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.addRelationship(rel)
				.build();
		assertEquals(0, pkg.verify().size());
		assertTrue(pkg.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
		
		// Relationship Type SPECIFICATION_FOR
		rel = gmo.createRelationship(FILE1, RelationshipType.SPECIFICATION_FOR, "");
		pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, LICENSE1, "copyright", LICENSE2)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.addRelationship(rel)
				.build();
		assertEquals(0, pkg.verify().size());
		assertTrue(pkg.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
				
	}

	public void testSetPrimaryPurpose() throws InvalidSPDXAnalysisException {
		SpdxPackage pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, null, null, null)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.setPrimaryPurpose(Purpose.APPLICATION)
				.build();
		
		assertEquals(pkg.getPrimaryPurpose().get(), Purpose.APPLICATION);
		pkg.setPrimaryPurpose(Purpose.FRAMEWORK);
		assertEquals(pkg.getPrimaryPurpose().get(), Purpose.FRAMEWORK);
		pkg.setPrimaryPurpose(null);
		assertFalse(pkg.getPrimaryPurpose().isPresent());
	}
	
	public void testSetBuiltDate() throws InvalidSPDXAnalysisException {
		SpdxPackage pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, null, null, null)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.setBuiltDate(DATE_NOW)
				.build();
		assertEquals(pkg.getBuiltDate().get(), DATE_NOW);
		pkg.setBuiltDate(DATE_THEN);
		assertEquals(pkg.getBuiltDate().get(), DATE_THEN);
		pkg.setBuiltDate(null);
		assertFalse(pkg.getBuiltDate().isPresent());
	}
	
	public void testSetValidUntilDate() throws InvalidSPDXAnalysisException {
		SpdxPackage pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, null, null, null)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.setValidUntilDate(DATE_NOW)
				.build();
		assertEquals(pkg.getValidUntilDate().get(), DATE_NOW);
		pkg.setValidUntilDate(DATE_THEN);
		assertEquals(pkg.getValidUntilDate().get(), DATE_THEN);
		pkg.setValidUntilDate(null);
		assertFalse(pkg.getValidUntilDate().isPresent());
	}
	
	public void testSetReleaseDate() throws InvalidSPDXAnalysisException {
		SpdxPackage pkg = gmo.createPackage(gmo.getModelStore()
				.getNextId(IdType.SpdxId, gmo.getDocumentUri()), PKG_NAME1, null, null, null)
				.setDownloadLocation(DOWNLOAD_LOCATION1)
				.setFilesAnalyzed(false)
				.setReleaseDate(DATE_NOW)
				.build();
		assertEquals(pkg.getReleaseDate().get(), DATE_NOW);
		pkg.setReleaseDate(DATE_THEN);
		assertEquals(pkg.getReleaseDate().get(), DATE_THEN);
		pkg.setReleaseDate(null);
		assertFalse(pkg.getReleaseDate().isPresent());
	}
}
