package org.spdx.library.model.compat.v2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.Version;
import org.spdx.library.model.compat.v2.Annotation;
import org.spdx.library.model.compat.v2.Checksum;
import org.spdx.library.model.compat.v2.GenericModelObject;
import org.spdx.library.model.compat.v2.Relationship;
import org.spdx.library.model.compat.v2.SpdxFile;
import org.spdx.library.model.compat.v2.enumerations.AnnotationType;
import org.spdx.library.model.compat.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.compat.v2.enumerations.FileType;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.library.model.compat.v2.license.AnyLicenseInfo;
import org.spdx.library.model.compat.v2.license.ConjunctiveLicenseSet;
import org.spdx.library.model.compat.v2.license.DisjunctiveLicenseSet;
import org.spdx.library.model.compat.v2.license.ExtractedLicenseInfo;
import org.spdx.library.model.compat.v2.license.SpdxListedLicense;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

public class SpdxFileTest extends TestCase {
	
	static final String[] NONSTD_IDS = new String[] {SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"1",
			SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"2", SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"3",
			SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM+"4"};
		static final String[] NONSTD_TEXTS = new String[] {"text1", "text2", "text3", "text4"};
		static final String[] STD_IDS = new String[] {"AFL-3.0", "CECILL-B", "EUPL-1.0"};
		static final String[] STD_TEXTS = new String[] {"std text1", "std text2", "std text3"};
		
		static DateFormat DATEFORMAT = new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT);
		static String DATE_NOW = DATEFORMAT.format(new Date());
		Annotation ANNOTATION1;
		Annotation ANNOTATION2;
		Annotation ANNOTATION3;
		
		ExtractedLicenseInfo[] NON_STD_LICENSES;
		SpdxListedLicense[] STANDARD_LICENSES;
		DisjunctiveLicenseSet[] DISJUNCTIVE_LICENSES;
		ConjunctiveLicenseSet[] CONJUNCTIVE_LICENSES;
		
		ConjunctiveLicenseSet COMPLEX_LICENSE;
		
		Checksum SHA1;
		
		GenericModelObject gmo;
		

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		SHA1 = gmo.createChecksum(ChecksumAlgorithm.SHA1, "1123456789abcdef0123456789abcdef01234567");
		ANNOTATION1 = gmo.createAnnotation("Organization: Annotator1", 
				AnnotationType.OTHER, DATE_NOW, "Comment 1");
		ANNOTATION2 = gmo.createAnnotation("Tool: Annotator2", 
				AnnotationType.REVIEW, DATE_NOW, "Comment 2");
		ANNOTATION3 = gmo.createAnnotation("Person: Annotator3", 
				AnnotationType.OTHER, DATE_NOW, "Comment 3");
		NON_STD_LICENSES = new ExtractedLicenseInfo[NONSTD_IDS.length];
		for (int i = 0; i < NONSTD_IDS.length; i++) {
			NON_STD_LICENSES[i] = new ExtractedLicenseInfo(NONSTD_IDS[i], NONSTD_TEXTS[i]);
		}
		
		STANDARD_LICENSES = new SpdxListedLicense[STD_IDS.length];
		for (int i = 0; i < STD_IDS.length; i++) {
			STANDARD_LICENSES[i] = new SpdxListedLicense(STD_IDS[i]);
			STANDARD_LICENSES[i].setName("Name "+String.valueOf(i));
			STANDARD_LICENSES[i].setLicenseText(STD_TEXTS[i]);
			STANDARD_LICENSES[i].setSeeAlso(Arrays.asList(new String[] {"URL "+String.valueOf(i)}));
			STANDARD_LICENSES[i].setComment("Notes "+String.valueOf(i));
			STANDARD_LICENSES[i].setStandardLicenseHeader("LicHeader "+String.valueOf(i));
			STANDARD_LICENSES[i].setStandardLicenseTemplate( "Template "+String.valueOf(i));
			STANDARD_LICENSES[i].setOsiApproved(true);
		}
		
		DISJUNCTIVE_LICENSES = new DisjunctiveLicenseSet[3];
		CONJUNCTIVE_LICENSES = new ConjunctiveLicenseSet[2];
		
		DISJUNCTIVE_LICENSES[0] = gmo.createDisjunctiveLicenseSet(Arrays.asList(new AnyLicenseInfo[] {
				NON_STD_LICENSES[0], NON_STD_LICENSES[1], STANDARD_LICENSES[1]
		}));
		CONJUNCTIVE_LICENSES[0] = gmo.createConjunctiveLicenseSet(Arrays.asList(new AnyLicenseInfo[] {
				STANDARD_LICENSES[0], NON_STD_LICENSES[0], STANDARD_LICENSES[1]
		}));
		CONJUNCTIVE_LICENSES[1] = gmo.createConjunctiveLicenseSet(Arrays.asList(new AnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[0], NON_STD_LICENSES[2]
		}));
		DISJUNCTIVE_LICENSES[1] = gmo.createDisjunctiveLicenseSet(Arrays.asList(new AnyLicenseInfo[] {
				CONJUNCTIVE_LICENSES[1], NON_STD_LICENSES[0], STANDARD_LICENSES[0]
		}));
		DISJUNCTIVE_LICENSES[2] = gmo.createDisjunctiveLicenseSet(Arrays.asList(new AnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[1], CONJUNCTIVE_LICENSES[0], STANDARD_LICENSES[2]
		}));
		COMPLEX_LICENSE = gmo.createConjunctiveLicenseSet(Arrays.asList(new AnyLicenseInfo[] {
				DISJUNCTIVE_LICENSES[2], NON_STD_LICENSES[2], CONJUNCTIVE_LICENSES[1]
		}));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVerify() throws InvalidSPDXAnalysisException {
		List<AnyLicenseInfo> seenLic = Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[0]});
		List<String> contributors = Arrays.asList(new String[] {"Contrib1", "Contrib2"});
		
		SpdxFile fileDep1 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"fileDep1", COMPLEX_LICENSE, seenLic, "Copyright1", SHA1)
				.setLicenseComments("License Comments1")
				.setComment("Comment1")
				.setNoticeText("Notice Text")
				.addFileType(FileType.SOURCE)
				.setFileContributors(contributors)
				.setAnnotations(Arrays.asList(new Annotation[] {ANNOTATION1, ANNOTATION2}))
				.build();
		List<String> verify = fileDep1.verify();
		assertEquals(0, verify.size());
	
		SpdxFile fileDep2 =gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"fileDep2", COMPLEX_LICENSE, seenLic, "Copyright2", SHA1)
				.setComment("Comment2")
				.addAnnotation(ANNOTATION3)
				.setLicenseComments("License Comments2")
				.addFileType(FileType.BINARY)
				.build();
		
		String fileNotice = "File Notice";
		String name = "fileName";
		String comment = "file comments";
		String copyright = "Copyrights";
		String licenseComment = "License comments";
		
		
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				name, COMPLEX_LICENSE, seenLic,copyright, SHA1)
				.addAnnotation(ANNOTATION3)
				.setFileContributors(contributors)
				.setNoticeText(fileNotice)
				.setLicenseComments(licenseComment)
				.setComment(comment)
				.build();
		file.addRelationship(file.createRelationship(fileDep1, RelationshipType.CONTAINS, "Relationship 1 comment"));
		file.addRelationship(file.createRelationship(fileDep2, RelationshipType.DOCUMENTATION_OF, "Relationship 2 comment"));
		assertEquals(0, file.verify().size());
	}
	
	// Test to verify spec versions prior to 2.3 fail verify for missing license or copyright fields
	public void testVerify23Fields() throws InvalidSPDXAnalysisException {
		
		
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"name", null, Arrays.asList(new AnyLicenseInfo[] {}), null, SHA1)
				.build();
		assertEquals(0, file.verify().size());
		assertTrue(file.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
	}

	public void testGetSha1() throws InvalidSPDXAnalysisException {
		List<AnyLicenseInfo> seenLic = Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[0]});
		List<String> contributors = Arrays.asList(new String[] {"Contrib1", "Contrib2"});
		String fileNotice = "File Notice";
		String name = "fileName";
		String comment = "file comments";
		String copyright = "Copyrights";
		String licenseComment = "License comments";
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				name, COMPLEX_LICENSE, seenLic,copyright, SHA1)
				.addAnnotation(ANNOTATION3)
				.setFileContributors(contributors)
				.setNoticeText(fileNotice)
				.setComment(comment)
				.setLicenseComments(licenseComment)
				.build();
		assertEquals(SHA1.getValue(), file.getSha1());
		String sha2 = "2123456789abcdef0123456789abcdef01234567";
		file.getChecksums().clear();
		file.addChecksum(gmo.createChecksum(ChecksumAlgorithm.SHA1, sha2));
		assertEquals(sha2, file.getSha1());
	}

	public void testAddFileType() throws InvalidSPDXAnalysisException {
		List<FileType> fileTypes = Arrays.asList(new FileType[] {FileType.ARCHIVE, 
				FileType.SPDX, FileType.OTHER, FileType.TEXT});
		FileType initialFileType = FileType.IMAGE;
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"filename", COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES),
				SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.addFileType(initialFileType)
				.build();
		assertCollectionsSame(Arrays.asList(new FileType[]{initialFileType}), file.getFileTypes());
		file.getFileTypes().clear();
		for (FileType ft:fileTypes) {
			file.addFileType(ft);
		}
		assertCollectionsSame(fileTypes, file.getFileTypes());
	}
	
	private void assertCollectionsSame(Collection<? extends Object> c1, Collection<? extends Object> c2) {
		assertEquals(c1.size(), c2.size());
		for (Object c:c1) {
			assertTrue(c2.contains(c));
		}
	}

	public void testEquivalent() throws InvalidSPDXAnalysisException {
		List<AnyLicenseInfo> seenLic = Arrays.asList(new AnyLicenseInfo[] {STANDARD_LICENSES[0]});
		List<String> contributors = Arrays.asList(new String[] {"Contrib1", "Contrib2"});
		
		SpdxFile fileDep1 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"fileDep1", COMPLEX_LICENSE, seenLic, "Copyright1", SHA1)
				.setComment("Comment1")
				.setAnnotations(Arrays.asList(new Annotation[] {ANNOTATION1, ANNOTATION2}))
				.setLicenseComments("License Comments1")
				.addFileType(FileType.SOURCE)
				.setFileContributors(contributors)
				.setNoticeText("Notice Text")
				.build();
		SpdxFile fileDep2 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"fileDep2", COMPLEX_LICENSE, seenLic, "Copyright1", SHA1)
				.addFileType(FileType.BINARY)
				.addAnnotation(ANNOTATION3)
				.setLicenseComments("License Comments2")
				.setFileContributors(contributors)
				.setNoticeText("Notice Text2")
				.build();
		String fileNotice = "File Notice";
		String name = "fileName";
		String comment = "file comments";
		String copyright = "Copyrights";
		String licenseComment = "License comments";
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.SHA1, "0123456789abcdef0123456789abcdef01234567");
		
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				name, COMPLEX_LICENSE, seenLic, copyright, checksum)
				.setComment(comment)
				.addAnnotation(ANNOTATION3)
				.setLicenseComments(licenseComment)
				.addFileType(FileType.SOURCE)
				.setFileContributors(contributors)
				.setNoticeText(fileNotice)
				.build();
		file.addRelationship(file.createRelationship(fileDep1, RelationshipType.CONTAINS, "Relationship 1 comment"));
		file.addRelationship(file.createRelationship(fileDep2, RelationshipType.DOCUMENTATION_OF, "Relationship 2 comment"));

		SpdxFile file2 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				name, COMPLEX_LICENSE, seenLic, copyright, checksum)
				.setComment(comment)
				.addAnnotation(ANNOTATION3)
				.setLicenseComments(licenseComment)
				.addFileType(FileType.SOURCE)
				.setFileContributors(contributors)
				.setNoticeText(fileNotice)
				.build();
		Relationship rel1 = file.createRelationship(fileDep1, RelationshipType.CONTAINS, "Relationship 1 comment");
		file2.addRelationship(rel1);
		Relationship rel2 = file.createRelationship(fileDep2, RelationshipType.DOCUMENTATION_OF, "Relationship 2 comment");
		file2.addRelationship(rel2);
		assertTrue(file.equivalent(file2));
		// name
		file2.setName("NewName");
		assertFalse(file.equivalent(file2));
		file2.setName(name);
		assertTrue(file.equivalent(file2));
		// comment
		file2.setComment("New Comment");
		assertFalse(file.equivalent(file2));
		file2.setComment(comment);
		assertTrue(file.equivalent(file2));
		// annotations
		file2.getAnnotations().clear();
		file2.addAnnotation(ANNOTATION1);
		file2.addAnnotation(ANNOTATION2);
		assertFalse(file.equivalent(file2));
		file2.getAnnotations().clear();
		file2.addAnnotation(ANNOTATION3);
		assertTrue(file.equivalent(file2));
		// relationships
		file2.removeRelationship(rel2);
		assertFalse(file.equivalent(file2));
		file2.addRelationship(rel2);
		assertTrue(file.equivalent(file2));
		// licenseConcluded
		file2.setLicenseConcluded(NON_STD_LICENSES[0]);
		assertFalse(file.equivalent(file2));
		file2.setLicenseConcluded(COMPLEX_LICENSE);
		assertTrue(file.equivalent(file2));
		// seen licenses
		file2.getLicenseInfoFromFiles().clear();
		file2.getLicenseInfoFromFiles().addAll(Arrays.asList(NON_STD_LICENSES));
		assertFalse(file.equivalent(file2));
		file2.getLicenseInfoFromFiles().clear();
		file2.getLicenseInfoFromFiles().addAll(seenLic);
		assertTrue(file.equivalent(file2));
		// copyrights
		file2.setCopyrightText("new copyright");
		assertFalse(file.equivalent(file2));
		file2.setCopyrightText(copyright);
		assertTrue(file.equivalent(file2));
		// license comments
		file2.setLicenseComments("New license comment");
		assertFalse(file.equivalent(file2));
		file2.setLicenseComments(licenseComment);
		assertTrue(file.equivalent(file2));
		// file types
		file2.addFileType(FileType.ARCHIVE);
		assertFalse(file.equivalent(file2));
		file2.getFileTypes().remove(FileType.ARCHIVE);
		assertTrue(file.equivalent(file2));
		// checksum
		Checksum added = gmo.createChecksum(ChecksumAlgorithm.SHA256, "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
		file2.addChecksum(added);
		assertFalse(file.equivalent(file2));
		file2.getChecksums().remove(added);
		assertTrue(file.equivalent(file2));
		// contributors
		file2.getFileContributors().clear();
		file2.getFileContributors().addAll(Arrays.asList(new String[] {"new 1", "new2"}));
		assertFalse(file.equivalent(file2));
		file2.getFileContributors().clear();
		file2.getFileContributors().addAll(contributors);
		assertTrue(file.equivalent(file2));
		// file notice
		file2.setNoticeText("New file notice");
		assertFalse(file.equivalent(file2));
		file2.setNoticeText(fileNotice);
		assertTrue(file.equivalent(file2));
	}

	public void testGetChecksums() throws InvalidSPDXAnalysisException {
		String SHA1_VALUE1 = "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12";
		String SHA1_VALUE2 = "2222e1c67a2d28fced849ee1bb76e7391b93eb12";
		String SHA256_VALUE1 = "CA978112CA1BBDCAFAC231B39A23DC4DA786EFF8147C4E72B9807785AFEE48BB";
		String SHA256_VALUE2 = "F7846F55CF23E14EEBEAB5B4E1550CAD5B509E3348FBC4EFA3A1413D393CB650";
		String MD5_VALUE1 = "9e107d9d372bb6826bd81d3542a419d6";
		String MD5_VALUE2 = "d41d8cd98f00b204e9800998ecf8427e";
		Checksum checksum1 = gmo.createChecksum(ChecksumAlgorithm.SHA1, SHA1_VALUE1);
		Checksum checksum2 = gmo.createChecksum(ChecksumAlgorithm.SHA1, SHA1_VALUE2);
		Checksum checksum3 = gmo.createChecksum(ChecksumAlgorithm.SHA256, SHA256_VALUE1);
		Checksum checksum4 = gmo.createChecksum(ChecksumAlgorithm.SHA256, SHA256_VALUE2);
		Checksum checksum5 = gmo.createChecksum(ChecksumAlgorithm.MD5, MD5_VALUE1);
		Checksum checksum6 = gmo.createChecksum(ChecksumAlgorithm.MD5, MD5_VALUE2);
		
		List<Checksum> checksums1 = Arrays.asList(new Checksum[] {checksum1, checksum3, checksum5});
		List<Checksum> checksums2 = Arrays.asList(new Checksum[] {checksum2, checksum4, checksum6});
		List<Checksum> checksumSingle = Arrays.asList(new Checksum[] {gmo.createChecksum(ChecksumAlgorithm.SHA1,
				"2fd4e1c67a2d28fced849ee1bb76e7391b93eb12")});
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"filename", COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, checksum1)
				.setChecksums(checksums1)
				.build();
		Collection<Checksum> result = file.getChecksums();
		assertCollectionsSame(checksums1, result);
		SpdxFile file2 = new SpdxFile(file.getModelStore(), file.getDocumentUri(), file.getId(), gmo.getCopyManager(), false);
		result = file2.getChecksums();
		assertCollectionsSame(checksums1, result);
		file.getChecksums().clear();
		file.getChecksums().addAll(checksums2);
		result = file.getChecksums();
		assertCollectionsSame(checksums2, result);
		result = file2.getChecksums();
		assertCollectionsSame(checksums2, result);
		file2.getChecksums().clear();
		file2.addChecksum(checksumSingle.get(0));
		result = file2.getChecksums();
		assertCollectionsSame(checksumSingle, result);
		result = file.getChecksums();
		assertCollectionsSame(checksumSingle, result);
	}
	
	public void testGetFileContributors() throws InvalidSPDXAnalysisException {
		String CONTRIBUTOR1 = "Contributor 1";
		String CONTRIBUTOR2 = "Contributor 2";
		String CONTRIBUTOR3 = "Contributor 3";
		List<String> contributors = Arrays.asList(new String[] {CONTRIBUTOR1, CONTRIBUTOR2, CONTRIBUTOR3});
		
		String CONTRIBUTOR4 = "Contributor 4";
		List<String> oneContributor = Arrays.asList(new String[] {CONTRIBUTOR4});
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"filename", COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.setFileContributors(contributors)
				.build();
		assertEquals(contributors.size(), file.getFileContributors().size());
		Collection<String> result = file.getFileContributors();
		result = file.getFileContributors();
		assertCollectionsSame(contributors, result);
		SpdxFile file2 = new SpdxFile(file.getModelStore(), file.getDocumentUri(), file.getId(), file.getCopyManager(), false);
		result = file2.getFileContributors();
		assertCollectionsSame(contributors, result);
		file2.getFileContributors().clear();
		assertEquals(0, file2.getFileContributors().size());
		assertEquals(0, file.getFileContributors().size());
		file2.addFileContributor(CONTRIBUTOR4);
		assertCollectionsSame(oneContributor, file2.getFileContributors());
		assertCollectionsSame(oneContributor, file.getFileContributors());
	}

	public void testGetNoticeText() throws InvalidSPDXAnalysisException {
		String fileNotice = "This is a file notice";
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"filename", COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.build();
		if (file.getNoticeText().isPresent() && !file.getNoticeText().get().isEmpty()) {
			fail("nto null notice text");
		}
		file.setNoticeText(fileNotice);
		String result  = file.getNoticeText().get();
		assertEquals(fileNotice, result);
		SpdxFile file2 = new SpdxFile(file.getModelStore(), file.getDocumentUri(), file.getId(), file.getCopyManager(), false);
		result = file2.getNoticeText().get();
		assertEquals(fileNotice, file2.getNoticeText().get());
		file2.setNoticeText(null);
		if (file2.getNoticeText().isPresent() && !file2.getNoticeText().get().isEmpty()) {
			fail("nto null notice text");
		}
	}

	public void testCompareTo() throws InvalidSPDXAnalysisException {
		String fileName1 = "afile";
		String fileName2 = "bfile";
		
		SpdxFile file1 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				fileName1, COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.build();
		SpdxFile file2 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				fileName2, COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.build();
		SpdxFile file3 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				fileName1, COMPLEX_LICENSE,Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.build();
		
		assertEquals(-1, file1.compareTo(file2));
		assertEquals(1, file2.compareTo(file1));
		assertEquals(0, file1.compareTo(file3));
	}
	
	public void testSetComment() throws InvalidSPDXAnalysisException {
		String COMMENT1 = "comment1";
		String COMMENT2 = "comment2";
		String COMMENT3 = "comment3";
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"filename", COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.setComment(COMMENT1)
				.build();
		assertEquals(file.getComment().get(), COMMENT1);
		file.setLicenseComments("see if this works");
		file.setComment(COMMENT2);
		SpdxFile file2 = new SpdxFile(file.getModelStore(), file.getDocumentUri(), file.getId(), file.getCopyManager(), false);
		assertEquals(file2.getComment().get(), COMMENT2);
		file2.setComment(COMMENT3);
		assertEquals(file2.getComment().get(), COMMENT3);
	}
	
	public void testSetAttributionText() throws InvalidSPDXAnalysisException {
		String ATT1 = "attribution 1";
		String ATT2 = "attribution 2";
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"filename", COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.addAttributionText(ATT1)
				.build();
		assertEquals(1, file.getAttributionText().size());
		assertTrue(file.getAttributionText().contains(ATT1));
		file.getAttributionText().add(ATT2);
		SpdxFile file2 = new SpdxFile(file.getModelStore(), file.getDocumentUri(), file.getId(), file.getCopyManager(), false);
		assertEquals(2, file2.getAttributionText().size());
		assertTrue(file2.getAttributionText().contains(ATT1));
		assertTrue(file2.getAttributionText().contains(ATT2));
		file2.getAttributionText().clear();
		assertEquals(0, file2.getAttributionText().size());
	}
	
	@SuppressWarnings("deprecation")
	public void testDependency() throws InvalidSPDXAnalysisException {
		SpdxFile file = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"filename", COMPLEX_LICENSE, Arrays.asList(CONJUNCTIVE_LICENSES), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.build();
		SpdxFile dep = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"dependency", STANDARD_LICENSES[0], Arrays.asList(STANDARD_LICENSES[0]), SpdxConstantsCompatV2.NOASSERTION_VALUE, SHA1)
				.build();
		Collection<SpdxFile> result = file.getFileDependency();
		assertEquals(0, result.size());
		result.add(dep);
		result = file.getFileDependency();
		assertEquals(1, result.size());
		assertTrue(result.contains(dep));
	}
}
