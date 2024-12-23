/**
 * Copyright (c) 2015 Source Auditor Inc.
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
 *
*/
package org.spdx.utility.compare;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.Annotation;
import org.spdx.library.model.v2.Checksum;
import org.spdx.library.model.v2.ExternalRef;
import org.spdx.library.model.v2.GenericSpdxItem;
import org.spdx.library.model.v2.ReferenceType;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.SpdxElement;
import org.spdx.library.model.v2.SpdxFile;
import org.spdx.library.model.v2.SpdxPackage;
import org.spdx.library.model.v2.SpdxPackageVerificationCode;
import org.spdx.library.model.v2.enumerations.AnnotationType;
import org.spdx.library.model.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.v2.enumerations.FileType;
import org.spdx.library.model.v2.enumerations.ReferenceCategory;
import org.spdx.library.model.v2.enumerations.RelationshipType;
import org.spdx.library.model.v2.license.AnyLicenseInfo;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;


/**
 * @author Gary O'Neall
 */
public class SpdxPackageComparerTest extends TestCase {
	
	private static final String DOC_URI_A = "http://doc/uri/A";
	private static final String DOC_URI_B = "http://doc/uri/B";
	private static final String COMMENTA = "comment A";
	@SuppressWarnings("unused")
	private static final String COMMENTB = "comment B";
	private static final String LICENSE_COMMENTA = "License Comment A";
	@SuppressWarnings("unused")
	private static final String LICENSE_COMMENTB = "License Comment B";
	private static final String COPYRIGHTA = "Copyright A";
	@SuppressWarnings("unused")
	private static final String COPYRIGHTB = "Copyright B";
	private static final String NAMEA = "NameA";
	@SuppressWarnings("unused")
	private static final String NAMEB = "NameB";
	private static final String ORIGINATORA = "Organization: OrgA";
	private static final String ORIGINATORB = "Organization: OrgB";
	private static final String HOMEPAGEA = "http://home.page/a";
	private static final String HOMEPAGEB = "http://home.page/b";
	private static final String DOWNLOADA = "http://download.page/a";
	private static final String DOWNLOADB = "http://download.page/b";
	private static final String DESCRIPTIONA = "Description A";
	private static final String DESCRIPTIONB = "Description B";
	private static final String PACKAGE_FILENAMEA = "packageFileNameA";
	@SuppressWarnings("unused")
	private static final String PACKAGE_FILENAMEB = "packageFileNameB";
	private static final String SOURCEINFOA = "Sourc info A";
	private static final String SOURCEINFOB = "Sourc info B";
	private static final String SUMMARYA = "Summary A";
	private static final String SUMMARYB = "Summary B";
	private static final String VERSIONINFOA = "Version A";
	private static final String VERSIONINFOB = "Version B";
	private static final String SUPPLIERA = "Person: Supplier A";
	private static final String SUPPLIERB = "Person: Supplier B";
	private static final Map<String, String> LICENSE_XLATION_MAPAB = new HashMap<>();
	
	private static final String[] REFERENCE_LOCATORS = new String[] {
		"org.apache.tomcat:tomcat:9.0.0.M4", "Microsoft.AspNet.MVC/5.0.0",
		"cpe:2.3:o:canonical:ubuntu_linux:10.04::lts:*:*:*:*:*"
	};

	private static final String[] COMMENTS = new String[] {
		"comment one", "comment two", ""
	};

	private static final String[] REFERENCE_TYPE_NAMES = new String[] {
		"maven-central", "nuget", "cpe23Type"
	};

	ReferenceCategory[] REFERENCE_CATEGORIES = {ReferenceCategory.PACKAGE_MANAGER,
			ReferenceCategory.PACKAGE_MANAGER,
			ReferenceCategory.SECURITY
	};

	ExternalRef[] TEST_REFERENCES;
	
	static {
		LICENSE_XLATION_MAPAB.put(DOC_URI_A + "#" + "LicenseRef-1", DOC_URI_B + "#" + "LicenseRef-4");
		LICENSE_XLATION_MAPAB.put(DOC_URI_A + "#" + "LicenseRef-2", DOC_URI_B + "#" + "LicenseRef-5");
		LICENSE_XLATION_MAPAB.put(DOC_URI_A + "#" + "LicenseRef-3", DOC_URI_B + "#" + "LicenseRef-6");
	}
	
	private static final Map<String, String> LICENSE_XLATION_MAPBA = new HashMap<>();
	
	static {
		LICENSE_XLATION_MAPBA.put(DOC_URI_B + "#" + "LicenseRef-4", DOC_URI_A + "#" + "LicenseRef-1");
		LICENSE_XLATION_MAPBA.put(DOC_URI_B + "#" + "LicenseRef-5", DOC_URI_A + "#" + "LicenseRef-2");
		LICENSE_XLATION_MAPBA.put(DOC_URI_B + "#" + "LicenseRef-6", DOC_URI_A + "#" + "LicenseRef-3");
	}
	
	private final Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> LICENSE_XLATION_MAP = new HashMap<>();

	private ExtractedLicenseInfo LICENSEA1;
	private ExtractedLicenseInfo LICENSEA2;
	private ExtractedLicenseInfo LICENSEA3;
	private ExtractedLicenseInfo LICENSEB1;
	private ExtractedLicenseInfo LICENSEB2;
	private ExtractedLicenseInfo LICENSEB3;
	private Collection<AnyLicenseInfo> LICENSE_INFO_FROM_FILESA;
	private Collection<AnyLicenseInfo> LICENSE_INFO_FROM_FILESB;
	private AnyLicenseInfo LICENSE_CONCLUDEDA;
	private AnyLicenseInfo LICENSE_CONCLUDEDB;
	private AnyLicenseInfo LICENSE_DECLAREDA;
	private AnyLicenseInfo LICENSE_DECLAREDB;

	private SpdxDocument DOCA;
	private SpdxDocument DOCB;
	private Annotation ANNOTATIONA1;
	private Annotation ANNOTATIONA2;
	private Annotation ANNOTATIONA3;
	private Annotation ANNOTATIONA4;
	private Annotation ANNOTATIONB1;
	private Annotation ANNOTATIONB2;
	private Annotation ANNOTATIONB3;
	private Annotation ANNOTATIONB4;
	private Collection<Annotation> ANNOTATIONSA1;
	private Collection<Annotation> ANNOTATIONSB1;
	@SuppressWarnings("unused")
	private Collection<Annotation> ANNOTATIONSA2;
	@SuppressWarnings("unused")
	private Collection<Annotation> ANNOTATIONSB2;
	
	private Collection<Relationship> RELATIONSHIPSA1;
	@SuppressWarnings("unused")
	private Collection<Relationship> RELATIONSHIPSA2;
	private Collection<Relationship> RELATIONSHIPSB1;
	@SuppressWarnings("unused")
	private Collection<Relationship> RELATIONSHIPSB2;
	private SpdxElement RELATED_ELEMENTA1;
	private SpdxElement RELATED_ELEMENTA2;
	private SpdxElement RELATED_ELEMENTA3;
	private SpdxElement RELATED_ELEMENTA4;
	private SpdxElement RELATED_ELEMENTB1;
	private SpdxElement RELATED_ELEMENTB2;
	private SpdxElement RELATED_ELEMENTB3;
	private SpdxElement RELATED_ELEMENTB4;
	private Relationship RELATIONSHIPA1;
	private Relationship RELATIONSHIPA2;
	private Relationship RELATIONSHIPA3;
	private Relationship RELATIONSHIPA4;
	private Relationship RELATIONSHIPB1;
	private Relationship RELATIONSHIPB2;
	private Relationship RELATIONSHIPB3;
	private Relationship RELATIONSHIPB4;
	private Checksum CHECKSUM1;
	private Checksum CHECKSUM2;
	private Checksum CHECKSUM3;
	private Checksum CHECKSUM4;
	private Collection<Checksum> CHECKSUMSA;
	private Collection<Checksum> CHECKSUMSB;
	private String FILE1_NAME = "file1Name";
	private String FILE2_NAME = "file2Name";
	private String FILE3_NAME = "file3Name";
	private SpdxFile FILE1A;
	private SpdxFile FILE1B;
	private SpdxFile FILE1B_DIFF_CHECKSUM;
	private SpdxFile FILE2A;
	private SpdxFile FILE3A;
	private SpdxFile FILE2B;
	private SpdxFile FILE3B;
	private Collection<SpdxFile> FILESA;
	private Collection<SpdxFile> FILESB;
	private Collection<SpdxFile> FILESB_SAME;
	private SpdxPackageVerificationCode VERIFICATION_CODEA;
	private SpdxPackageVerificationCode VERIFICATION_CODEB;
	
	private IModelStore modelStoreA;
	private IModelStore modelStoreB;
	private ModelCopyManager copyManager;
	
	/**
	 * @throws java.lang.Exception
	 */
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	public void setUp() throws Exception {
		super.setUp();
		SpdxModelFactory.init();
		modelStoreA = new InMemSpdxStore();
		modelStoreB = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		DOCA = new SpdxDocument(modelStoreA, DOC_URI_A, copyManager, true);
		DOCB = new SpdxDocument(modelStoreB, DOC_URI_B, copyManager, true);
		LICENSEA1  = new ExtractedLicenseInfo(modelStoreA, DOC_URI_A, "LicenseRef-1", copyManager, true);
		LICENSEA1.setExtractedText("License1");
		LICENSEA2 = new ExtractedLicenseInfo(modelStoreA, DOC_URI_A, "LicenseRef-2", copyManager, true);
		LICENSEA2.setExtractedText("License2");
		LICENSEA3 = new ExtractedLicenseInfo(modelStoreA, DOC_URI_A, "LicenseRef-3", copyManager, true);
		LICENSEA3.setExtractedText("License3");
		LICENSEB1 = new ExtractedLicenseInfo(modelStoreB, DOC_URI_B, "LicenseRef-4", copyManager, true);
		LICENSEB1.setExtractedText("License1");
		LICENSEB2 = new ExtractedLicenseInfo(modelStoreB, DOC_URI_B, "LicenseRef-5", copyManager, true);
		LICENSEB2.setExtractedText("License2");
		LICENSEB3 = new ExtractedLicenseInfo(modelStoreB, DOC_URI_B, "LicenseRef-6", copyManager, true);
		LICENSEB3.setExtractedText("License3");
		LICENSE_INFO_FROM_FILESA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2, LICENSEA3}));
		LICENSE_INFO_FROM_FILESB = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2, LICENSEB3}));
		LICENSE_CONCLUDEDA = LICENSEA1;
		LICENSE_CONCLUDEDB = LICENSEB1;
		LICENSE_DECLAREDA = LICENSEA2;
		LICENSE_DECLAREDB = LICENSEB2;
		ANNOTATIONA1 = DOCA.createAnnotation("Person: Annotator1", AnnotationType.OTHER, 
				"2010-01-29T18:30:22Z", "AnnotationComment1");
		ANNOTATIONA2 = DOCA.createAnnotation("Person: Annotator2", AnnotationType.REVIEW, 
				"2011-01-29T18:30:22Z", "AnnotationComment2");
		ANNOTATIONA3 = DOCA.createAnnotation("Person: Annotator3", AnnotationType.OTHER, 
				"2012-01-29T18:30:22Z", "AnnotationComment3");
		ANNOTATIONA4 = DOCA.createAnnotation("Person: Annotator4", AnnotationType.REVIEW, 
				"2013-01-29T18:30:22Z", "AnnotationComment4");
		ANNOTATIONB1 = DOCB.createAnnotation("Person: Annotator1", AnnotationType.OTHER, 
				"2010-01-29T18:30:22Z", "AnnotationComment1");
		ANNOTATIONB2 = DOCB.createAnnotation("Person: Annotator2", AnnotationType.REVIEW, 
				"2011-01-29T18:30:22Z", "AnnotationComment2");
		ANNOTATIONB3 = DOCB.createAnnotation("Person: Annotator3", AnnotationType.OTHER, 
				"2012-01-29T18:30:22Z", "AnnotationComment3");
		ANNOTATIONB4 = DOCB.createAnnotation("Person: Annotator4", AnnotationType.REVIEW, 
				"2013-01-29T18:30:22Z", "AnnotationComment4");
		ANNOTATIONSA1 = new HashSet<>(Arrays.asList(new Annotation[] {ANNOTATIONA1, ANNOTATIONA2}));
		ANNOTATIONSB1 = new HashSet<>(Arrays.asList(new Annotation[] {ANNOTATIONB1, ANNOTATIONB2}));
		ANNOTATIONSA2 = new HashSet<>(Arrays.asList(new Annotation[] {ANNOTATIONA3, ANNOTATIONA4}));
		ANNOTATIONSB2 = new HashSet<>(Arrays.asList(new Annotation[] {ANNOTATIONB3, ANNOTATIONB4}));
		RELATED_ELEMENTA1 = new GenericSpdxItem(modelStoreA, DOC_URI_A, modelStoreA.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENTA1.setName("relatedElementName1");
		RELATED_ELEMENTA1.setComment("related element comment 1");
		RELATED_ELEMENTA2 = new GenericSpdxItem(modelStoreA, DOC_URI_A, modelStoreA.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENTA2.setName("relatedElementName2");
		RELATED_ELEMENTA2.setComment("related element comment 2");
		RELATED_ELEMENTA3 = new GenericSpdxItem(modelStoreA, DOC_URI_A, modelStoreA.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENTA3.setName("relatedElementName3");
		RELATED_ELEMENTA3.setComment("related element comment 3");
		RELATED_ELEMENTA4 = new GenericSpdxItem(modelStoreA, DOC_URI_A, modelStoreA.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENTA4.setName("relatedElementName4");
		RELATED_ELEMENTA4.setComment("related element comment 4");
		RELATED_ELEMENTB1 = new GenericSpdxItem(modelStoreB, DOC_URI_B, modelStoreA.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENTB1.setName("relatedElementName1");
		RELATED_ELEMENTB1.setComment("related element comment 1");
		RELATED_ELEMENTB2 = new GenericSpdxItem(modelStoreB, DOC_URI_B, modelStoreA.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENTB2.setName("relatedElementName2");
		RELATED_ELEMENTB2.setComment("related element comment 2");
		RELATED_ELEMENTB3 = new GenericSpdxItem(modelStoreB, DOC_URI_B, modelStoreB.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENTB3.setName("relatedElementName3");
		RELATED_ELEMENTB3.setComment("related element comment 3");
		RELATED_ELEMENTB4 = new GenericSpdxItem(modelStoreB, DOC_URI_B, modelStoreB.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENTB4.setName("relatedElementName4");
		RELATED_ELEMENTB4.setComment("related element comment 4");
		RELATIONSHIPA1 = DOCA.createRelationship(RELATED_ELEMENTA1, 
				RelationshipType.CONTAINS, "Relationship Comment1");
		RELATIONSHIPA2 = DOCA.createRelationship(RELATED_ELEMENTA2, 
				RelationshipType.DYNAMIC_LINK, "Relationship Comment2");
		RELATIONSHIPA3 = DOCA.createRelationship(RELATED_ELEMENTA3, 
				RelationshipType.DATA_FILE_OF, "Relationship Comment3");
		RELATIONSHIPA4 = DOCA.createRelationship(RELATED_ELEMENTA4, 
				RelationshipType.DISTRIBUTION_ARTIFACT, "Relationship Comment4");
		RELATIONSHIPB1 = DOCB.createRelationship(RELATED_ELEMENTB1, 
				RelationshipType.CONTAINS, "Relationship Comment1");
		RELATIONSHIPB2 = DOCB.createRelationship(RELATED_ELEMENTB2, 
				RelationshipType.DYNAMIC_LINK, "Relationship Comment2");
		RELATIONSHIPB3 = DOCB.createRelationship(RELATED_ELEMENTB3, 
				RelationshipType.DATA_FILE_OF, "Relationship Comment3");
		RELATIONSHIPB4 = DOCB.createRelationship(RELATED_ELEMENTB4, 
				RelationshipType.DISTRIBUTION_ARTIFACT, "Relationship Comment4");
		RELATIONSHIPSA1 = new HashSet<>(Arrays.asList(new Relationship[] {RELATIONSHIPA1, RELATIONSHIPA2}));
		RELATIONSHIPSA2 = new HashSet<>(Arrays.asList(new Relationship[] {RELATIONSHIPA3, RELATIONSHIPA4}));
		RELATIONSHIPSB1 = new HashSet<>(Arrays.asList(new Relationship[] {RELATIONSHIPB1, RELATIONSHIPB2}));
		RELATIONSHIPSB2 = new HashSet<>(Arrays.asList(new Relationship[] {RELATIONSHIPB3, RELATIONSHIPB4}));
		CHECKSUM1 = DOCA.createChecksum(ChecksumAlgorithm.SHA1, 
				"111bf72bf99b7e471f1a27989667a903658652bb");
		CHECKSUM2 = DOCA.createChecksum(ChecksumAlgorithm.SHA1, 
				"222bf72bf99b7e471f1a27989667a903658652bb");
		CHECKSUM3 = DOCB.createChecksum(ChecksumAlgorithm.SHA1, 
				"333bf72bf99b7e471f1a27989667a903658652bb");
		CHECKSUM4 = DOCB.createChecksum(ChecksumAlgorithm.SHA1, 
				"444bf72bf99b7e471f1a27989667a903658652bb");
		CHECKSUMSA = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM1, CHECKSUM2}));
		CHECKSUMSB = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM3, CHECKSUM4}));
		
		FILE1A = DOCA.createSpdxFile("SPDXRef-FILEA1A", FILE1_NAME, LICENSE_CONCLUDEDA, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2})), 
				"copyright", CHECKSUM1)
				.setChecksums(CHECKSUMSA)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.build();
		FILE1B = DOCB.createSpdxFile("SPDXRef-FILEA1B", FILE1_NAME, LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), 
				"copyright", CHECKSUM1)
				.setChecksums(CHECKSUMSA)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.build();
		FILE1B_DIFF_CHECKSUM = DOCB.createSpdxFile("SPDXRef-FILEA1BDIFF", FILE1_NAME, LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), 
				"copyright", CHECKSUM3)
				.setChecksums(CHECKSUMSB)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.build();

		FILE2A = DOCA.createSpdxFile("SPDXRef-FILE2A", FILE2_NAME, LICENSE_CONCLUDEDA, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2})), 
				"copyright", CHECKSUM1)
				.setChecksums(CHECKSUMSA)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.build();

		FILE3A = DOCA.createSpdxFile("SPDXRef-FILE3A", FILE3_NAME, LICENSE_CONCLUDEDA, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2})), 
				"copyright", CHECKSUM1)
				.setChecksums(CHECKSUMSA)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.build();

		FILE2B = DOCB.createSpdxFile("SPDXRef-FILE2B", FILE2_NAME, LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), 
				"copyright", CHECKSUM1)
				.setChecksums(CHECKSUMSA)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.build();

		FILE3B = DOCB.createSpdxFile("SPDXRef-FILE3B", FILE3_NAME, LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), 
				"copyright", CHECKSUM1)
				.setChecksums(CHECKSUMSA)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.build();

		FILESA = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1A, FILE2A}));
		FILESB_SAME = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1B, FILE2B}));
		FILESB = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1B_DIFF_CHECKSUM, FILE3B}));
		VERIFICATION_CODEA = DOCA.createPackageVerificationCode("aaabf72bf99b7e471f1a27989667a903658652bb",
				new HashSet<>(Arrays.asList(new String[] {"file2"})));
		VERIFICATION_CODEB = DOCB.createPackageVerificationCode("bbbbf72bf99b7e471f1a27989667a903658652bb",
				new HashSet<>(Arrays.asList(new String[] {"file3"})));
		Map<SpdxDocument, Map<String, String>> bmap = new HashMap<>();
		bmap.put(DOCB, LICENSE_XLATION_MAPAB);
		LICENSE_XLATION_MAP.put(DOCA, bmap);
		Map<SpdxDocument, Map<String, String>> amap = new HashMap<>();
		amap.put(DOCA, LICENSE_XLATION_MAPBA);
		LICENSE_XLATION_MAP.put(DOCB, amap);
		
		TEST_REFERENCES = new ExternalRef[REFERENCE_CATEGORIES.length];
		for (int i = 0; i < REFERENCE_CATEGORIES.length; i++) {
			TEST_REFERENCES[i] = DOCA.createExternalRef(REFERENCE_CATEGORIES[i], 
					new ReferenceType(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + REFERENCE_TYPE_NAMES[i]), 
					REFERENCE_LOCATORS[i], COMMENTS[i]);
		}
		Arrays.sort(TEST_REFERENCES);
	}

	/**
	 * @throws java.lang.Exception
	 */
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#compare(org.spdx.rdfparser.model.SpdxPackage, org.spdx.rdfparser.model.SpdxPackage, java.util.HashMap)}.
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testCompareSpdxPackageSpdxPackageHashMapOfStringString() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
		assertFalse(pc.isDifferenceFound());
	}
	
	private SpdxPackage createPackage(SpdxDocument doc, String name, String comment,
			Collection<Annotation> annotations, Collection<Relationship> relationships,
			AnyLicenseInfo licenseConcluded, Collection<AnyLicenseInfo> licenseInfoFromFiles, String copyright,
			String licenseComment, AnyLicenseInfo licenseDeclared, Collection<Checksum> checksums,
			String description, String download, Collection<SpdxFile> files, String homepage,
			String originator, String packageFilename, SpdxPackageVerificationCode verificationCode,
			String sourceinfo, String summary, String supplier, String versioninfo, Checksum sha1) throws InvalidSPDXAnalysisException {
		
		return createPackage(doc, name, comment, annotations, relationships,
				licenseConcluded, licenseInfoFromFiles, copyright,
				licenseComment, licenseDeclared, checksums,
				description, download, files, homepage,
				originator, packageFilename, verificationCode,
				sourceinfo, summary, supplier, versioninfo, true, new HashSet<ExternalRef>(), sha1);
	}

	private SpdxPackage createPackage(SpdxDocument doc, String name, String comment,
			Collection<Annotation> annotations, Collection<Relationship> relationships,
			AnyLicenseInfo licenseConcluded, Collection<AnyLicenseInfo> licenseInfoFromFiles, String copyright,
			String licenseComment, AnyLicenseInfo licenseDeclared, Collection<Checksum> checksums,
			String description, String download, Collection<SpdxFile> files, String homepage,
			String originator, String packageFilename, SpdxPackageVerificationCode verificationCode,
			String sourceinfo, String summary, String supplier, String versioninfo, 
			boolean filesAnalyzed, Collection<ExternalRef> externalRefs, Checksum sha1) throws InvalidSPDXAnalysisException {
		return doc.createPackage("SPDXRef-"+name, name, licenseConcluded, copyright,  licenseDeclared)
				.addChecksum(sha1)
				.setComment(comment)
				.setAnnotations(annotations)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseInfoFromFiles)
				.setLicenseComments(licenseComment)
				.setChecksums(checksums)
				.setDescription(description)
				.setDownloadLocation(download)
				.setFiles(files)
				.setHomepage(homepage)
				.setOriginator(originator)
				.setPackageFileName(packageFilename)
				.setPackageVerificationCode(verificationCode)
				.setSourceInfo(sourceinfo)
				.setSummary(summary)
				.setSupplier(supplier)
				.setVersionInfo(versioninfo)
				.setFilesAnalyzed(filesAnalyzed)
				.setExternalRefs(externalRefs)
				.build();
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageVersionsEquals()}.
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsPackageVersionsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB,NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOB, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertFalse(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageSuppliersEquals()}.
	 * @throws SpdxCompareException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsPackageSuppliersEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERB, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertFalse(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageDownloadLocationsEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageDownloadLocationsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADB, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertFalse(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageVerificationCodesEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageVerificationCodesEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEB, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertFalse(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageChecksumsEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageChecksumsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSB,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM3);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertFalse(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(2, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(2, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageSourceInfosEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageSourceInfosEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSB1, 
				RELATIONSHIPSB1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOB,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertFalse(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isDeclaredLicensesEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testisDeclaredLicensesEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSEB1, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertFalse(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
		
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageSummaryEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageSummaryEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYB, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertFalse(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageDescriptionsEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageDescriptionsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONB, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertFalse(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageOriginatorsEqual()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageOriginatorsEqual() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORB, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertFalse(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageHomePagesEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageHomePagesEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEB, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertTrue(pc.isPackageFilesEquals());
		assertFalse(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isRelationshipsEquals());
		assertTrue(pc.isSeenLicenseEquals());	
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#getPkgA()}.
	 * @throws SpdxCompareException 
	 */
	public void testGetPkg() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEB, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertEquals(pkgA, pc.getDocPackage(DOCA));
		assertEquals(pkgB, pc.getDocPackage(DOCB));
	}
	

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#getUniqueChecksumsA()}.
	 * @throws SpdxCompareException 
	 */
	public void testGetUniqueChecksumsA() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Collection<Checksum> checksumsA = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM1, CHECKSUM2}));
		Collection<Checksum> checksumsB = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3}));
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, checksumsA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, checksumsB,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM2);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertFalse(pc.isPackageChecksumsEquals());
		assertEquals(1, pc.getUniqueChecksums(DOCB, DOCA).size());
		List<Checksum> result = pc.getUniqueChecksums(DOCA, DOCB);
		assertEquals(1, result.size());
		assertEquals(CHECKSUM1.getValue(), result.get(0).getValue());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#getUniqueChecksumsB()}.
	 * @throws SpdxCompareException 
	 */
	public void testGetUniqueChecksumsB() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Collection<Checksum> checksumsA = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM1, CHECKSUM2}));
		Collection<Checksum> checksumsB = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3}));
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, checksumsA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, checksumsB,
				DESCRIPTIONA, DOWNLOADA, FILESB_SAME, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM2);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertFalse(pc.isPackageChecksumsEquals());
		assertEquals(1, pc.getUniqueChecksums(DOCA, DOCB).size());
		List<Checksum> result = pc.getUniqueChecksums(DOCB, DOCA);
		assertEquals(1, result.size());
		assertEquals(CHECKSUM3.getValue(), result.get(0).getValue());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#isPackageFilesEquals()}.
	 * @throws SpdxCompareException 
	 */
	public void testIsPackageFilesEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESB, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertTrue(pc.isAnnotationsEquals());
		assertTrue(pc.isCommentsEquals());
		assertTrue(pc.isConcludedLicenseEquals());
		assertTrue(pc.isCopyrightsEquals());
		assertTrue(pc.isDeclaredLicensesEquals());
		assertTrue(pc.isLicenseCommmentsEquals());
		assertTrue(pc.isPackageChecksumsEquals());
		assertTrue(pc.isPackageDescriptionsEquals());
		assertTrue(pc.isPackageDownloadLocationsEquals());
		assertTrue(pc.isPackageFilenamesEquals());
		assertFalse(pc.isPackageFilesEquals());
		assertTrue(pc.isPackageHomePagesEquals());
		assertTrue(pc.isPackageOriginatorsEqual());
		assertTrue(pc.isPackageSourceInfosEquals());
		assertTrue(pc.isPackageSummaryEquals());
		assertTrue(pc.isPackageSuppliersEquals());
		assertTrue(pc.isPackageVerificationCodesEquals());
		assertTrue(pc.isPackageVersionsEquals());
		assertTrue(pc.isSeenLicenseEquals());
		assertEquals(0, pc.getUniqueChecksums(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueChecksums(DOCB, DOCA).size());
		assertEquals(1, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(1, pc.getUniqueFiles(DOCB, DOCA).size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#getFileDifferences()}.
	 * @throws SpdxCompareException 
	 */
	public void testGetFileDifferences() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Collection<SpdxFile> filesA = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1A, FILE2A, FILE3A}));
		Collection<SpdxFile> filesB = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1B_DIFF_CHECKSUM, FILE2B, FILE3B}));
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, filesA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, filesB, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertFalse(pc.isPackageFilesEquals());
		assertEquals(0, pc.getUniqueFiles(DOCA, DOCB).size());
		assertEquals(0, pc.getUniqueFiles(DOCB, DOCA).size());
		List<SpdxFileDifference> result = pc.getFileDifferences(DOCA, DOCB);
		assertEquals(1, result.size());
		assertFalse(result.get(0).isChecksumsEquals());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#getUniqueFilesA()}.
	 * @throws SpdxCompareException 
	 */
	public void testGetUniqueFilesA() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Collection<SpdxFile> filesA = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1A, FILE2A}));
		Collection<SpdxFile> filesB = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE2B, FILE3B}));
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, filesA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, filesB, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertFalse(pc.isPackageFilesEquals());
		assertEquals(0, pc.getFileDifferences(DOCA, DOCB).size());
		assertEquals(1, pc.getUniqueFiles(DOCB, DOCA).size());
		List<SpdxFile> result = pc.getUniqueFiles(DOCA, DOCB);
		assertEquals(1, result.size());
		assertTrue(FILE1A.equivalent(result.get(0)));
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxPackageComparer#getUniqueFilesB()}.
	 * @throws SpdxCompareException 
	 */
	public void testGetUniqueFilesB() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Collection<SpdxFile> filesA = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1A, FILE2A}));
		Collection<SpdxFile> filesB = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE2B, FILE3B}));
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, filesA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, filesB, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertFalse(pc.isPackageFilesEquals());
		assertEquals(0, pc.getFileDifferences(DOCA, DOCB).size());
		assertEquals(1, pc.getUniqueFiles(DOCA, DOCB).size());
		List<SpdxFile> result = pc.getUniqueFiles(DOCB, DOCA);
		assertEquals(1, result.size());
		assertTrue(FILE3B.equivalent(result.get(0)));
	}
	
	public void testIsFilesAnalyzedEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, true, new HashSet<ExternalRef>(), CHECKSUM1);
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, false, new HashSet<ExternalRef>(), CHECKSUM1);
		assertEquals(false, pkgB.isFilesAnalyzed());
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertFalse(pc.isFilesAnalyzedEquals());
	}

	public void testGetExternalRefDifferences() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, true, 
				new HashSet<ExternalRef>(Arrays.asList(TEST_REFERENCES)), CHECKSUM1);
		ExternalRef[] externalRefB = new ExternalRef[TEST_REFERENCES.length];
		for (int i = 0; i < TEST_REFERENCES.length; i++) {
			externalRefB[i] = DOCB.createExternalRef(TEST_REFERENCES[i].getReferenceCategory(), TEST_REFERENCES[i].getReferenceType(), 
					TEST_REFERENCES[i].getReferenceLocator(), TEST_REFERENCES[i].getComment().get());
		}
		externalRefB[0].setComment("Different comment");
		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, true, 
				new HashSet<ExternalRef>(Arrays.asList(externalRefB)), CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertFalse(pc.isExternalRefsEquals());
		List<SpdxExternalRefDifference> result = pc.getExternalRefDifferences(DOCA, DOCB);
		assertEquals(1, result.size());
		assertEquals(result.get(0).getReferenceLocator(), TEST_REFERENCES[0].getReferenceLocator());
		assertEquals(TEST_REFERENCES[0].getReferenceType().getIndividualURI(), result.get(0).getReferenceType().getIndividualURI());
		assertFalse(result.get(0).isCommentsEqual());
		assertEquals(TEST_REFERENCES[0].getComment().get(), result.get(0).getCommentB());
		assertEquals(externalRefB[0].getComment().get(), result.get(0).getCommentA());		
		result = pc.getExternalRefDifferences(DOCB, DOCA);
		assertEquals(1, result.size());
		assertEquals(result.get(0).getReferenceLocator(), TEST_REFERENCES[0].getReferenceLocator());
		assertEquals(TEST_REFERENCES[0].getReferenceType().getIndividualURI(), result.get(0).getReferenceType().getIndividualURI());
		assertFalse(result.get(0).isCommentsEqual());
		assertEquals(TEST_REFERENCES[0].getComment().get(), result.get(0).getCommentB());
		assertEquals(externalRefB[0].getComment().get(), result.get(0).getCommentA());	
	}
	
	public void testGetUniqueExternalRefs() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxPackage pkgA = createPackage(DOCA, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDA, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, true, 
				new HashSet<ExternalRef>(Arrays.asList(TEST_REFERENCES)), CHECKSUM1);
		ExternalRef[] externalRefB = new ExternalRef[TEST_REFERENCES.length-1];
		
		for (int i = 0; i < TEST_REFERENCES.length-1; i++) {
			externalRefB[i] = TEST_REFERENCES[i];
		}

		SpdxPackage pkgB = createPackage(DOCB, NAMEA, COMMENTA, ANNOTATIONSA1, 
				RELATIONSHIPSA1, LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB,
				COPYRIGHTA, LICENSE_COMMENTA, LICENSE_DECLAREDB, CHECKSUMSA,
				DESCRIPTIONA, DOWNLOADA, FILESA, HOMEPAGEA, ORIGINATORA, 
				PACKAGE_FILENAMEA, VERIFICATION_CODEA, SOURCEINFOA,
				SUMMARYA, SUPPLIERA, VERSIONINFOA, true, 
				new HashSet<ExternalRef>(Arrays.asList(externalRefB)), CHECKSUM1);
		SpdxPackageComparer pc = new SpdxPackageComparer(LICENSE_XLATION_MAP);
		pc.addDocumentPackage(DOCA, pkgA);
		pc.addDocumentPackage(DOCB, pkgB);
		assertTrue(pc.isDifferenceFound());
		assertFalse(pc.isExternalRefsEquals());
		List<ExternalRef> result = pc.getUniqueExternalRefs(DOCA, DOCB);
		assertEquals(1, result.size());
		assertTrue(TEST_REFERENCES[TEST_REFERENCES.length-1].equivalent(result.get(0)));
		result = pc.getUniqueExternalRefs(DOCB, DOCA);
		assertEquals(0, result.size());
		
	}
}
