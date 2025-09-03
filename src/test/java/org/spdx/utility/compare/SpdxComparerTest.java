/*
 * SPDX-FileCopyrightText: Copyright (c) 2013 Source Auditor Inc.
 * SPDX-FileCopyrightText: Copyright (c) 2013 Black Duck Software Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.utility.compare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.spdx.core.DefaultModelStore;
import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.Annotation;
import org.spdx.library.model.v2.Checksum;
import org.spdx.library.model.v2.ExternalDocumentRef;
import org.spdx.library.model.v2.GenericModelObject;
import org.spdx.library.model.v2.GenericSpdxElement;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.SpdxCreatorInformation;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.SpdxElement;
import org.spdx.library.model.v2.SpdxFile;
import org.spdx.library.model.v2.SpdxItem;
import org.spdx.library.model.v2.SpdxPackage;
import org.spdx.library.model.v2.SpdxPackageVerificationCode;
import org.spdx.library.model.v2.SpdxSnippet;
import org.spdx.library.model.v2.enumerations.AnnotationType;
import org.spdx.library.model.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.v2.enumerations.FileType;
import org.spdx.library.model.v2.enumerations.RelationshipType;
import org.spdx.library.model.v2.license.AnyLicenseInfo;
import org.spdx.library.model.v2.license.ConjunctiveLicenseSet;
import org.spdx.library.model.v2.license.DisjunctiveLicenseSet;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;
import org.spdx.library.model.v2.license.InvalidLicenseStringException;
import org.spdx.library.model.v2.license.License;
import org.spdx.library.model.v2.license.LicenseSet;
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v2.license.SpdxNoAssertionLicense;
import org.spdx.library.model.v2.license.SpdxNoneLicense;
import org.spdx.library.model.v2.pointer.ByteOffsetPointer;
import org.spdx.library.model.v2.pointer.LineCharPointer;
import org.spdx.library.model.v2.pointer.StartEndPointer;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;


/**
 * Test SPDX comparer
 *
 * @author Gary O'Neall
 */
public class SpdxComparerTest extends TestCase {
	
	private static final String STD_LIC_ID_CC0 = "CC-BY-1.0";
	private static final String STD_LIC_ID_MPL11 = "MPL-1.1";
	
	private static final String COMMENTA = "comment A";
	private static final String COMMENTB = "comment B";
	private static final String LICENSE_COMMENTA = "License Comment A";
	@SuppressWarnings("unused")
	private static final String LICENSE_COMMENTB = "License Comment B";
	private static final String COPYRIGHTA = "Copyright A";
	@SuppressWarnings("unused")
	private static final String COPYRIGHTB = "Copyright B";
	private static final String NAMEA = "NameA";
	private static final String NAMEB = "NameB";
	private static final String NAMEC = "NameC";
	private static final Map<String, String> LICENSE_XLATION_MAP = new HashMap<>();
	private static final String ORIGINATORA = "Organization: OrgA";
	@SuppressWarnings("unused")
	private static final String ORIGINATORB = "Organization: OrgB";
	private static final String HOMEPAGEA = "http://home.page/a";
	@SuppressWarnings("unused")
	private static final String HOMEPAGEB = "http://home.page/b";
	private static final String DOWNLOADA = "http://download.page/a";
	@SuppressWarnings("unused")
	private static final String DOWNLOADB = "http://download.page/b";
	private static final String DESCRIPTIONA = "Description A";
	@SuppressWarnings("unused")
	private static final String DESCRIPTIONB = "Description B";
	private static final String PACKAGE_FILENAMEA = "packageFileNameA";
	@SuppressWarnings("unused")
	private static final String PACKAGE_FILENAMEB = "packageFileNameB";
	private static final String SOURCEINFOA = "Sourc info A";
	@SuppressWarnings("unused")
	private static final String SOURCEINFOB = "Sourc info B";
	private static final String SUMMARYA = "Summary A";
	@SuppressWarnings("unused")
	private static final String SUMMARYB = "Summary B";
	private static final String VERSIONINFOA = "Version A";
	@SuppressWarnings("unused")
	private static final String VERSIONINFOB = "Version B";
	private static final String SUPPLIERA = "Person: Supplier A";
	@SuppressWarnings("unused")
	private static final String SUPPLIERB = "Person: Supplier B";
	private static final String DOC_URIA = "http://spdx.org/documents/uriA";
	private static final String DOC_URIB = "http://spdx.org/documents/uriB";
	private static final String DOC_URIC = "http://spdx.org/documents/uriC";
	private static final String DOC_NAMEA = "DocumentA";
	private static final String DOC_NAMEB = "DocumentB";
	private static final String DOC_NAMEC = "DocumentC";
	static {
		LICENSE_XLATION_MAP.put("LicenseRef-1", "LicenseRef-4");
		LICENSE_XLATION_MAP.put("LicenseRef-2", "LicenseRef-5");
		LICENSE_XLATION_MAP.put("LicenseRef-3", "LicenseRef-6");
	}
	
	Integer OFFSET1_1 = Integer.valueOf(2342);
	ByteOffsetPointer BOP_POINTER1_1;
	Integer LINE1_1 = Integer.valueOf(113);
	LineCharPointer LCP_POINTER1_1; 
	Integer OFFSET2_1 = Integer.valueOf(444);
	ByteOffsetPointer BOP_POINTER2_1;
	Integer LINE2_1 = Integer.valueOf(23422);
	LineCharPointer LCP_POINTER2_1; 
	Integer OFFSET1_2 = Integer.valueOf(3542);
	ByteOffsetPointer BOP_POINTER1_2;
	Integer LINE1_2 = Integer.valueOf(555);
	LineCharPointer LCP_POINTER1_2; 
	Integer OFFSET2_2 = Integer.valueOf(2444);
	ByteOffsetPointer BOP_POINTER2_2;
	Integer LINE2_2 = Integer.valueOf(23428);
	
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
	private SpdxCreatorInformation CREATION_INFOA;
	private SpdxCreatorInformation CREATION_INFOB;
	private ExtractedLicenseInfo[] EXTRACTED_LICENSESA;
	private ExtractedLicenseInfo[] EXTRACTED_LICENSESB;
	private AnyLicenseInfo DATALICENSE;

	private Annotation ANNOTATION1;
	private Annotation ANNOTATION2;
	private Annotation ANNOTATION3;
	private Annotation ANNOTATION4;
	private Collection<Annotation> ANNOTATIONSA;
	private Collection<Annotation> ANNOTATIONSB;
	
	private Collection<Relationship> RELATIONSHIPSA;
	private Collection<Relationship> RELATIONSHIPSB;
	private SpdxElement RELATED_ELEMENT1;
	private SpdxElement RELATED_ELEMENT2;
	private SpdxElement RELATED_ELEMENT3;
	private SpdxElement RELATED_ELEMENT4;
	private Relationship RELATIONSHIP1;
	private Relationship RELATIONSHIP2;
	private Relationship RELATIONSHIP3;
	private Relationship RELATIONSHIP4;
	private Checksum CHECKSUM1;
	private Checksum CHECKSUM2;
	private Checksum CHECKSUM3;
	private Checksum CHECKSUM4;
	private Collection<Checksum> CHECKSUMSA;
	private Collection<Checksum> CHECKSUMSB;
	private String FILE1_NAME = "file1Name";
	private String FILE2_NAME = "file2Name";
	private String FILE3_NAME = "file3Name";
	private String FILE4_NAME = "file4Name";
	private SpdxFile FILE1A;
	private SpdxFile FILE1B;
	private SpdxFile FILE1B_DIFF_CHECKSUM;
	private SpdxFile FILE2A;
	private SpdxFile FILE3A;
	private SpdxFile FILE2B;
	private SpdxFile FILE3B;
	private SpdxFile FILE4A;
	private Collection<SpdxFile> FILESA;
	private Collection<SpdxFile> FILESB;
	private Collection<SpdxFile> FILESB_SAME;
	private SpdxPackageVerificationCode VERIFICATION_CODEA;
	@SuppressWarnings("unused")
	private SpdxPackageVerificationCode VERIFICATION_CODEB;
	
	LineCharPointer LCP_POINTER2_2; 
	StartEndPointer BYTE_RANGE1;
	StartEndPointer BYTE_RANGE2;
	StartEndPointer LINE_RANGE1;
	StartEndPointer LINE_RANGE2;
	
	SpdxSnippet SNIPPET1;
	SpdxSnippet SNIPPET2;
	
	SpdxPackage pkgA1;
	SpdxPackage pkgA1_1;
	SpdxPackage pkgA1_2;
	SpdxPackage pkgA2;
	SpdxPackage pkgB1;
	SpdxPackage pkgB2;
	
	ExternalDocumentRef ref1;
	ExternalDocumentRef ref2;
	ExternalDocumentRef ref3;
	
	Collection<ExternalDocumentRef> EXTERNAL_DOC_REFS;
    IModelStore modelStore;
    IModelCopyManager copyManager;
    String DEFAULT_DOCUMENT_URI = "http://default/doc";

	/**
	 * @throws java.lang.Exception
	 */
	public void setUp() throws Exception {
		super.setUp();
		SpdxModelFactory.init();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		DefaultModelStore.initialize(modelStore, DEFAULT_DOCUMENT_URI, copyManager);
		GenericModelObject gmoA = new GenericModelObject(modelStore, DOC_URIA, modelStore.getNextId(IdType.Anonymous), copyManager, true);
		GenericModelObject gmoB = new GenericModelObject(modelStore, DOC_URIB, modelStore.getNextId(IdType.Anonymous), copyManager, true);
		DATALICENSE = LicenseInfoFactory.parseSPDXLicenseStringCompatV2(SpdxConstantsCompatV2.SPDX_DATA_LICENSE_ID);
		LICENSEA1 = new ExtractedLicenseInfo(modelStore, DOC_URIA, "LicenseRef-1", copyManager, true);
		LICENSEA1.setExtractedText("License1");
		LICENSEA2 = new ExtractedLicenseInfo(modelStore, DOC_URIA, "LicenseRef-2", copyManager, true);
		LICENSEA2.setExtractedText("License2");
		LICENSEA3 = new ExtractedLicenseInfo(modelStore, DOC_URIA, "LicenseRef-3", copyManager, true);
		LICENSEA3.setExtractedText("License3");
		LICENSEB1 = new ExtractedLicenseInfo(modelStore, DOC_URIB, "LicenseRef-4", copyManager, true);
		LICENSEB1.setExtractedText("License1");
		LICENSEB2 = new ExtractedLicenseInfo(modelStore, DOC_URIB, "LicenseRef-5", copyManager, true);
		LICENSEB2.setExtractedText("License2");
		LICENSEB3 =  new ExtractedLicenseInfo(modelStore, DOC_URIB, "LicenseRef-6", copyManager, true);
		LICENSEB3.setExtractedText("License3");
		LICENSE_INFO_FROM_FILESA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2, LICENSEA3}));
		LICENSE_INFO_FROM_FILESB = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2, LICENSEB3}));
		LICENSE_CONCLUDEDA = LICENSEA1;
		LICENSE_CONCLUDEDB = LICENSEB1;
		LICENSE_DECLAREDA = LICENSEA2;
		LICENSE_DECLAREDB = LICENSEB2;
		CREATION_INFOA = gmoA.createCreationInfo(Arrays.asList(new String[] {"Person: CreatorA"}),
				"2010-01-29T18:30:22Z");
		CREATION_INFOA.setComment("Creator CommentA");
		CREATION_INFOA.setLicenseListVersion("1.15");
		CREATION_INFOB = gmoB.createCreationInfo(Arrays.asList(new String[] {"Person: CreatorB"}), 
				"2012-01-29T18:30:22Z");
		CREATION_INFOB.setComment("Creator CommentB");
		CREATION_INFOB.setLicenseListVersion("1.17");
		EXTRACTED_LICENSESA = new ExtractedLicenseInfo[]{
				LICENSEA1, LICENSEA2, LICENSEA3
			};
		EXTRACTED_LICENSESB = new ExtractedLicenseInfo[]{
				LICENSEB1, LICENSEB2, LICENSEB3
			};
		
		ANNOTATION1 = gmoA.createAnnotation("Person: Annotator1", AnnotationType.OTHER, 
				"2010-01-29T18:30:22Z", "AnnotationComment1");
		ANNOTATION2 = gmoA.createAnnotation("Person: Annotator2", AnnotationType.REVIEW, 
				"2011-01-29T18:30:22Z", "AnnotationComment2");
		ANNOTATION3 = gmoB.createAnnotation("Person: Annotator3", AnnotationType.OTHER, 
				"2012-01-29T18:30:22Z", "AnnotationComment3");
		ANNOTATION4 = gmoB.createAnnotation("Person: Annotator4", AnnotationType.REVIEW, 
				"2013-01-29T18:30:22Z", "Person: AnnotationComment4");
		ANNOTATIONSA = new HashSet<>(Arrays.asList(new Annotation[] {ANNOTATION1, ANNOTATION2}));
		ANNOTATIONSB =  new HashSet<>(Arrays.asList(new Annotation[] {ANNOTATION3, ANNOTATION4}));
		RELATED_ELEMENT1 = new GenericSpdxElement(modelStore, DOC_URIA, modelStore.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENT1.setName("relatedElementName1");
		RELATED_ELEMENT1.setComment("related element comment 1");
		RELATED_ELEMENT2 = new GenericSpdxElement(modelStore, DOC_URIA, modelStore.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENT2.setName("relatedElementName2");
		RELATED_ELEMENT2.setComment("related element comment 2");
		RELATED_ELEMENT3 = new GenericSpdxElement(modelStore, DOC_URIB, modelStore.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENT3.setName("relatedElementName3");
		RELATED_ELEMENT3.setComment("related element comment 3");
		RELATED_ELEMENT4 = new GenericSpdxElement(modelStore, DOC_URIB, modelStore.getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENT4.setName("relatedElementName4");
		RELATED_ELEMENT4.setComment("related element comment 4");
		RELATIONSHIP1 = gmoA.createRelationship(RELATED_ELEMENT1, 
				RelationshipType.CONTAINS, "Relationship Comment1");
		RELATIONSHIP2 = gmoA.createRelationship(RELATED_ELEMENT2, 
				RelationshipType.DYNAMIC_LINK, "Relationship Comment2");
		RELATIONSHIP3 = gmoB.createRelationship(RELATED_ELEMENT3, 
				RelationshipType.DATA_FILE_OF, "Relationship Comment3");
		RELATIONSHIP4 = gmoB.createRelationship(RELATED_ELEMENT4, 
				RelationshipType.DISTRIBUTION_ARTIFACT, "Relationship Comment4");
		RELATIONSHIPSA = new HashSet<>(Arrays.asList(new Relationship[] {RELATIONSHIP1, RELATIONSHIP2}));
		RELATIONSHIPSB = new HashSet<>(Arrays.asList(new Relationship[] {RELATIONSHIP3, RELATIONSHIP4}));
		CHECKSUM1 = gmoA.createChecksum(ChecksumAlgorithm.SHA1, 
				"111bf72bf99b7e471f1a27989667a903658652bb");
		CHECKSUM2 = gmoA.createChecksum(ChecksumAlgorithm.SHA1, 
				"222bf72bf99b7e471f1a27989667a903658652bb");
		CHECKSUM3 = gmoB.createChecksum(ChecksumAlgorithm.SHA1, 
				"333bf72bf99b7e471f1a27989667a903658652bb");
		CHECKSUM4 = gmoB.createChecksum(ChecksumAlgorithm.SHA1, 
				"444bf72bf99b7e471f1a27989667a903658652bb");
		CHECKSUMSA = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM1, CHECKSUM2}));
		CHECKSUMSB = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM3, CHECKSUM4}));
		
		FILE1A = gmoA.createSpdxFile("SPDXRef-FILE1A", FILE1_NAME, LICENSE_CONCLUDEDA, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();

		FILE1B = gmoB.createSpdxFile("SPDXRef-FILE1B", FILE1_NAME, LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();

		FILE1B_DIFF_CHECKSUM = gmoB.createSpdxFile("SPDXRef-DIFFCHKSUM", FILE1_NAME, LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), 
				COPYRIGHTA, CHECKSUM3)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSB)
				.build();

		FILE2A = gmoA.createSpdxFile("SPDXRef-FILE2A", FILE2_NAME, LICENSE_CONCLUDEDA, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();
		
		FILE3A = gmoA.createSpdxFile("SPDXRef-FILE3A", FILE3_NAME, LICENSE_CONCLUDEDA, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();
		
		FILE4A = gmoA.createSpdxFile("SPDXRef-FILE4A", FILE4_NAME, LICENSE_CONCLUDEDA, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();

		FILE2B = gmoB.createSpdxFile("SPDXRef-FILE2B", FILE2_NAME, LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();
		
		FILE3B = gmoB.createSpdxFile("SPDXRef-FILE3B", FILE3_NAME, LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();

		FILESA = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1A, FILE2A}));
		FILESB_SAME = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1B, FILE2B}));
		FILESB = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1B_DIFF_CHECKSUM, FILE3B}));
		VERIFICATION_CODEA = gmoA.createPackageVerificationCode("aaabf72bf99b7e471f1a27989667a903658652bb",
				new HashSet<>(Arrays.asList(new String[] {"file2"})));
		VERIFICATION_CODEB = gmoB.createPackageVerificationCode("bbbbf72bf99b7e471f1a27989667a903658652bb",
				new HashSet<>(Arrays.asList(new String[] {"file3"})));
		BOP_POINTER1_1 = gmoA.createByteOffsetPointer(FILE1A, OFFSET1_1);
		BOP_POINTER1_2 = gmoA.createByteOffsetPointer(FILE1A, OFFSET1_2);
		BYTE_RANGE1 = gmoA.createStartEndPointer(BOP_POINTER1_1, BOP_POINTER1_2);
		LCP_POINTER1_1 = gmoA.createLineCharPointer(FILE1A, LINE1_1);
		LCP_POINTER1_2 = gmoA.createLineCharPointer(FILE1A, LINE1_2);
		LINE_RANGE1 = gmoA.createStartEndPointer(LCP_POINTER1_1, LCP_POINTER1_2);
		BOP_POINTER2_1 = gmoB.createByteOffsetPointer(FILE2A, OFFSET2_1);
		BOP_POINTER2_2 = gmoB.createByteOffsetPointer(FILE2A, OFFSET2_2);
		BYTE_RANGE2 = gmoB.createStartEndPointer(BOP_POINTER2_1, BOP_POINTER2_2);
		LCP_POINTER2_1 = gmoB.createLineCharPointer(FILE2A, LINE2_1);
		LCP_POINTER2_2 = gmoB.createLineCharPointer(FILE2A, LINE2_2);
		LINE_RANGE2 = gmoB.createStartEndPointer(LCP_POINTER2_1, LCP_POINTER2_2);
		SNIPPET1 = gmoA.createSpdxSnippet("SPDXRef-SnippetName1", "SnippetName1", LICENSE_CONCLUDEDA, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEA1, LICENSEA2})), "Copyright1", 
						FILE1A, OFFSET1_1, OFFSET1_2)
				.setComment("SnippetCOmment1")
				.setAnnotations(ANNOTATIONSA)
				.setRelationship(RELATIONSHIPSA)
				.setLicenseComments("LicenseComment1")
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		
		SNIPPET2 = gmoB.createSpdxSnippet("SPDXRef-SnippetName2", "SnippetName2", LICENSE_CONCLUDEDB, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {LICENSEB1, LICENSEB2})), "Copyright2", 
				FILE2A, OFFSET2_1, OFFSET2_2)
				.setComment("SnippetCOmment2")
				.setAnnotations(ANNOTATIONSB)
				.setRelationship(RELATIONSHIPSB)
				.setLicenseComments("LicenseComment2")
				.setLineRange(LINE2_1, LINE2_2)
				.build();
		pkgA1 = gmoA.createPackage("SPDXRef-pkgA1", NAMEA, LICENSE_CONCLUDEDA, COPYRIGHTA, LICENSE_DECLAREDA)
				.addChecksum(CHECKSUM1)
				.setComment(COMMENTA)
				.setAnnotations(ANNOTATIONSA)
				.setRelationships(RELATIONSHIPSA)
				.setLicenseInfosFromFile(LICENSE_INFO_FROM_FILESA)
				.setLicenseComments(LICENSE_COMMENTA)
				.setChecksums(CHECKSUMSA)
				.setDescription(DESCRIPTIONA)
				.setDownloadLocation(DOWNLOADA)
				.setFiles(FILESA)
				.setHomepage(HOMEPAGEA)
				.setOriginator(ORIGINATORA)
				.setPackageFileName(PACKAGE_FILENAMEA)
				.setPackageVerificationCode(VERIFICATION_CODEA)
				.setSourceInfo(SOURCEINFOA)
				.setSummary(SUMMARYA)
				.setSupplier(SUPPLIERA)
				.setVersionInfo(VERSIONINFOA)
				.build();
		pkgA2 = gmoA.createPackage("SPDXRef-pkgA2", NAMEB, LICENSE_CONCLUDEDA, COPYRIGHTA, LICENSE_DECLAREDA)
				.addChecksum(CHECKSUM1)
				.setComment(COMMENTA)
				.setAnnotations(ANNOTATIONSA)
				.setRelationships(RELATIONSHIPSA)
				.setLicenseInfosFromFile(LICENSE_INFO_FROM_FILESA)
				.setLicenseComments(LICENSE_COMMENTA)
				.setChecksums(CHECKSUMSA)
				.setDescription(DESCRIPTIONA)
				.setDownloadLocation(DOWNLOADA)
				.setFiles(FILESA)
				.setHomepage(HOMEPAGEA)
				.setOriginator(ORIGINATORA)
				.setPackageFileName(PACKAGE_FILENAMEA)
				.setPackageVerificationCode(VERIFICATION_CODEA)
				.setSourceInfo(SOURCEINFOA)
				.setSummary(SUMMARYA)
				.setSupplier(SUPPLIERA)
				.setVersionInfo(VERSIONINFOA)
				.build();
		
		pkgA1_1 = gmoA.createPackage("SPDXRef-pkgA11", NAMEA, LICENSE_CONCLUDEDA, COPYRIGHTA, LICENSE_DECLAREDA)
				.addChecksum(CHECKSUM1)
				.setComment(COMMENTA)
				.setAnnotations(ANNOTATIONSA)
				.setRelationships(RELATIONSHIPSA)
				.setLicenseInfosFromFile(LICENSE_INFO_FROM_FILESA)
				.setLicenseComments(LICENSE_COMMENTA)
				.setChecksums(CHECKSUMSA)
				.setDescription(DESCRIPTIONA)
				.setDownloadLocation(DOWNLOADA)
				.setFiles(FILESA)
				.setHomepage(HOMEPAGEA)
				.setOriginator(ORIGINATORA)
				.setPackageFileName(PACKAGE_FILENAMEA)
				.setPackageVerificationCode(VERIFICATION_CODEA)
				.setSourceInfo(SOURCEINFOA)
				.setSummary(SUMMARYA)
				.setSupplier(SUPPLIERA)
				.setVersionInfo(VERSIONINFOA)
				.build();
		
		pkgA1_2 = gmoA.createPackage("SPDXRef-pkgA12", NAMEA, LICENSE_CONCLUDEDA, COPYRIGHTA, LICENSE_DECLAREDA)
				.addChecksum(CHECKSUM1)
				.setComment(COMMENTA)
				.setAnnotations(ANNOTATIONSA)
				.setRelationships(RELATIONSHIPSA)
				.setLicenseInfosFromFile(LICENSE_INFO_FROM_FILESA)
				.setLicenseComments(LICENSE_COMMENTA)
				.setChecksums(CHECKSUMSA)
				.setDescription(DESCRIPTIONA)
				.setDownloadLocation(DOWNLOADA)
				.setFiles(FILESA)
				.setHomepage(HOMEPAGEA)
				.setOriginator(ORIGINATORA)
				.setPackageFileName(PACKAGE_FILENAMEA)
				.setPackageVerificationCode(VERIFICATION_CODEA)
				.setSourceInfo(SOURCEINFOA)
				.setSummary(SUMMARYA)
				.setSupplier(SUPPLIERA)
				.setVersionInfo(VERSIONINFOA)
				.build();

		pkgB1 = gmoB.createPackage("SPDXRef-pkgB1", NAMEA, LICENSE_CONCLUDEDB, COPYRIGHTA, LICENSE_DECLAREDB)
				.addChecksum(CHECKSUM1)
				.setComment(COMMENTA)
				.setAnnotations(ANNOTATIONSA)
				.setRelationships(RELATIONSHIPSA)
				.setLicenseInfosFromFile(LICENSE_INFO_FROM_FILESB)
				.setLicenseComments(LICENSE_COMMENTA)
				.setChecksums(CHECKSUMSA)
				.setDescription(DESCRIPTIONA)
				.setDownloadLocation(DOWNLOADA)
				.setFiles(FILESB_SAME)
				.setHomepage(HOMEPAGEA)
				.setOriginator(ORIGINATORA)
				.setPackageFileName(PACKAGE_FILENAMEA)
				.setPackageVerificationCode(VERIFICATION_CODEA)
				.setSourceInfo(SOURCEINFOA)
				.setSummary(SUMMARYA)
				.setSupplier(SUPPLIERA)
				.setVersionInfo(VERSIONINFOA)
				.build();

		pkgB2 = gmoB.createPackage("SPDXRef-pkgB2", NAMEB, LICENSE_CONCLUDEDB, COPYRIGHTA, LICENSE_DECLAREDB)
				.addChecksum(CHECKSUM1)
				.setComment(COMMENTB)
				.setAnnotations(ANNOTATIONSA)
				.setRelationships(RELATIONSHIPSA)
				.setLicenseInfosFromFile(LICENSE_INFO_FROM_FILESB)
				.setLicenseComments(LICENSE_COMMENTA)
				.setChecksums(CHECKSUMSA)
				.setDescription(DESCRIPTIONA)
				.setDownloadLocation(DOWNLOADA)
				.setFiles(FILESB)
				.setHomepage(HOMEPAGEA)
				.setOriginator(ORIGINATORA)
				.setPackageFileName(PACKAGE_FILENAMEA)
				.setPackageVerificationCode(VERIFICATION_CODEA)
				.setSourceInfo(SOURCEINFOA)
				.setSummary(SUMMARYA)
				.setSupplier(SUPPLIERA)
				.setVersionInfo(VERSIONINFOA)
				.build();
		
		new SpdxDocument(DOC_URIA);	// necessary for creating the external document refs
		ref1 = gmoA.createExternalDocumentRef("DocumentRef-1", "http://namespace/one", CHECKSUM1);
		ref2 = gmoA.createExternalDocumentRef("DocumentRef-2", "http://namespace/two", CHECKSUM2);
		ref3 = gmoA.createExternalDocumentRef("DocumentRef-3", "http://namespace/three", CHECKSUM3);
		EXTERNAL_DOC_REFS = new HashSet<>(Arrays.asList(ref1, ref2, ref3));
	}

	/**
	 * @throws java.lang.Exception
	 */
	public void tearDown() throws Exception {
		super.tearDown();
		DefaultModelStore.initialize(new InMemSpdxStore(), DEFAULT_DOCUMENT_URI, new ModelCopyManager());
	}
	
	private SpdxDocument createTestSpdxDoc(String docUri) throws InvalidSPDXAnalysisException {
		IModelStore newStore = new InMemSpdxStore();
		SpdxDocument retval = new SpdxDocument(newStore, docUri, DefaultModelStore.getDefaultCopyManager(), true);
		ExtractedLicenseInfo licenseConcluded = new ExtractedLicenseInfo(newStore, docUri, "LicenseRef-1", copyManager, true);
		licenseConcluded.setExtractedText("License1");
		ExtractedLicenseInfo licenseDeclared = new ExtractedLicenseInfo(newStore, docUri, "LicenseRef-2", copyManager, true);
		licenseDeclared.setExtractedText("License2");
		GenericSpdxElement relatedElement1 = new GenericSpdxElement(newStore, docUri, newStore.getNextId(IdType.SpdxId), copyManager, true);
		relatedElement1.setName("relatedElementName1");
		relatedElement1.setComment("related element comment 1");
		GenericSpdxElement relatedElement2 = new GenericSpdxElement(newStore, docUri, newStore.getNextId(IdType.SpdxId), copyManager, true);
		relatedElement2.setName("relatedElementName2");
		relatedElement2.setComment("related element comment 2");
		Relationship relationship1 = retval.createRelationship(relatedElement1, 
				RelationshipType.CONTAINS, "Relationship Comment1");
		Relationship relationship2= retval.createRelationship(relatedElement2, 
				RelationshipType.DYNAMIC_LINK, "Relationship Comment2");
		Set<Relationship> relationships = new HashSet<>(Arrays.asList(new Relationship[] {relationship1, relationship2}));
		ExtractedLicenseInfo license1 = new ExtractedLicenseInfo(newStore, docUri, "LicenseRef-1", copyManager, true);
		license1.setExtractedText("License1");
		ExtractedLicenseInfo license2 = new ExtractedLicenseInfo(newStore, docUri, "LicenseRef-2", copyManager, true);
		license2.setExtractedText("License2");
		ExtractedLicenseInfo license3 = new ExtractedLicenseInfo(newStore, docUri, "LicenseRef-3", copyManager, true);
		license3.setExtractedText("License3");
		Set<AnyLicenseInfo> licenseInfosFromFile = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {license1, license2, license3}));
		SpdxFile file1 = retval.createSpdxFile("SPDXRef-FILE1A", FILE1_NAME, licenseConcluded, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {license1, license2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();

		SpdxFile file2 = retval.createSpdxFile("SPDXRef-FILE2A", FILE2_NAME, licenseConcluded, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {license1, license2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();

		Set<SpdxFile> files = new HashSet<>(Arrays.asList(new SpdxFile[] {file1, file2}));
		SpdxPackage pkg = retval.createPackage("SPDXRef-pkg", NAMEA, licenseConcluded, COPYRIGHTA, licenseDeclared)
				.addChecksum(CHECKSUM1)
				.setComment(COMMENTA)
				.setAnnotations(ANNOTATIONSA)
				.setRelationships(relationships)
				.setLicenseInfosFromFile(licenseInfosFromFile)
				.setLicenseComments(LICENSE_COMMENTA)
				.setChecksums(CHECKSUMSA)
				.setDescription(DESCRIPTIONA)
				.setDownloadLocation(DOWNLOADA)
				.setFiles(files)
				.setHomepage(HOMEPAGEA)
				.setOriginator(ORIGINATORA)
				.setPackageFileName(PACKAGE_FILENAMEA)
				.setPackageVerificationCode(VERIFICATION_CODEA)
				.setSourceInfo(SOURCEINFOA)
				.setSummary(SUMMARYA)
				.setSupplier(SUPPLIERA)
				.setVersionInfo(VERSIONINFOA)
				.build();
		SpdxFile file = retval.createSpdxFile("SPDXRef-FILE", FILE4_NAME, licenseConcluded, 
				new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {license1, license2})), 
				COPYRIGHTA, CHECKSUM1)
				.setFileTypes(new HashSet<>(Arrays.asList(new FileType[] {FileType.DOCUMENTATION, FileType.TEXT})))
				.setChecksums(CHECKSUMSA)
				.build();
		Relationship docDescribesPackage = retval.createRelationship(pkg, 
				RelationshipType.DESCRIBES, "Package1 describes");
		retval.addRelationship(docDescribesPackage);
		Relationship docDescribesFile = retval.createRelationship(file, 
				RelationshipType.DESCRIBES, "File describes");
		retval.addRelationship(docDescribesFile);
		retval.setAnnotations(ANNOTATIONSA);
		retval.setComment(COMMENTA);
		retval.setCreationInfo(CREATION_INFOA);
		retval.setDataLicense(DATALICENSE);
		
		ExternalDocumentRef r1 = retval.createExternalDocumentRef("DocumentRef-1", "http://namespace/one", CHECKSUM1);
		ExternalDocumentRef r2 = retval.createExternalDocumentRef("DocumentRef-2", "http://namespace/two", CHECKSUM2);
		ExternalDocumentRef r3 = retval.createExternalDocumentRef("DocumentRef-3", "http://namespace/three", CHECKSUM3);
		Set<ExternalDocumentRef> externalDocRefs = new HashSet<>(Arrays.asList(r1, r2, r3));
		
		retval.setExternalDocumentRefs(externalDocRefs);
		retval.setExtractedLicenseInfos(Arrays.asList(new ExtractedLicenseInfo[]{license1, license2, license3}));
		retval.setName("DocumentA");
		retval.setSpecVersion("SPDX-2.1");
		return retval;
	}

	public void testCompare() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		try {
			comparer.isCreatorInformationEqual();	// should fail
			fail("Not checking for comparer being complete");
		} catch (SpdxCompareException ex) {
			// we expect an error
		}
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isDifferenceFound());
		assertEquals(doc1.hashCode(), comparer.getSpdxDoc(0).hashCode());
		assertEquals(doc2.hashCode(), comparer.getSpdxDoc(1).hashCode());
	}

	public void testCompareLicense() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException, InvalidLicenseStringException {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isDifferenceFound());
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isDifferenceFound());
		ExtractedLicenseInfo[] extractedInfos1 = doc1.getExtractedLicenseInfos().toArray(new ExtractedLicenseInfo[doc1.getExtractedLicenseInfos().size()]);
		Arrays.sort(extractedInfos1);
		ExtractedLicenseInfo[] extractedInfos2 = doc2.getExtractedLicenseInfos().toArray(new ExtractedLicenseInfo[doc2.getExtractedLicenseInfos().size()]);
		Arrays.sort(extractedInfos2);
		Map<Integer, Integer> xlateDoc1ToDoc2LicId = createLicIdXlation(extractedInfos1, extractedInfos2);
		
		//Standard License
		SpdxListedLicense lic1 = LicenseInfoFactory.getListedLicenseByIdCompatV2(STD_LIC_ID_CC0);
		SpdxListedLicense lic1_1 = LicenseInfoFactory.getListedLicenseByIdCompatV2(STD_LIC_ID_CC0);
		SpdxListedLicense lic2 = LicenseInfoFactory.getListedLicenseByIdCompatV2(STD_LIC_ID_MPL11);
		assertTrue(comparer.compareLicense(0, lic1, 1, lic1_1));
		assertFalse(comparer.compareLicense(0, lic1, 1, lic2));
		//Extracted License
		assertTrue(comparer.compareLicense(0, extractedInfos1[0], 1, extractedInfos2[xlateDoc1ToDoc2LicId.get(0)]));
		int nonEqual = 0;
		if (xlateDoc1ToDoc2LicId.get(0) == nonEqual) {
			nonEqual = 1;
		}
		assertFalse(comparer.compareLicense(0, extractedInfos1[0], 1, extractedInfos2[nonEqual]));
		try {
			assertFalse(comparer.compareLicense(0, extractedInfos1[0], 1, extractedInfos1[0]));
		} catch(SpdxCompareException ex) {
			// we expect a mappint exception
		}
		//Conjunctive License
		StringBuilder sb = new StringBuilder("(");
		sb.append(STD_LIC_ID_CC0);
		sb.append(" AND ");
		sb.append(extractedInfos1[0].getLicenseId());
		sb.append(" AND ");
		sb.append(STD_LIC_ID_MPL11);
		sb.append(" AND ");
		sb.append(extractedInfos1[1].getLicenseId());
		sb.append(")");
		AnyLicenseInfo conj1 = LicenseInfoFactory.parseSPDXLicenseStringCompatV2(sb.toString(), doc1.getModelStore(), doc1.getDocumentUri(), doc1.getCopyManager());

		sb = new StringBuilder("(");
		sb.append(STD_LIC_ID_MPL11);
		sb.append(" AND ");
		sb.append(extractedInfos2[xlateDoc1ToDoc2LicId.get(1)].getLicenseId());
		sb.append(" AND ");
		sb.append(STD_LIC_ID_CC0);
		sb.append(" AND ");
		sb.append(extractedInfos2[xlateDoc1ToDoc2LicId.get(0)].getLicenseId());
		sb.append(")");
		AnyLicenseInfo conj2 = LicenseInfoFactory.parseSPDXLicenseStringCompatV2(sb.toString(), doc2.getModelStore(), doc2.getDocumentUri(), doc2.getCopyManager());
		
		sb = new StringBuilder("(");
		sb.append(STD_LIC_ID_MPL11);
		sb.append(" AND ");
		sb.append(extractedInfos2[xlateDoc1ToDoc2LicId.get(1)].getLicenseId());
		sb.append(" AND ");
		sb.append(STD_LIC_ID_CC0);
		sb.append(")");
		AnyLicenseInfo conj3 = LicenseInfoFactory.parseSPDXLicenseStringCompatV2(sb.toString(), doc2.getModelStore(), doc2.getDocumentUri(), doc2.getCopyManager());
		
		assertTrue(comparer.compareLicense(0, conj1, 1, conj2));
		assertFalse(comparer.compareLicense(0, conj1, 1, conj3));
		try {
			assertFalse(comparer.compareLicense(0, conj2, 1, conj2));
		} catch(SpdxCompareException ex) {
			// we expect a mappint exception
		}
		//Disjunctive License
		sb = new StringBuilder("(");
		sb.append(STD_LIC_ID_CC0);
		sb.append(" OR ");
		sb.append(extractedInfos1[0].getLicenseId());
		sb.append(" OR ");
		sb.append(STD_LIC_ID_MPL11);
		sb.append(" OR ");
		sb.append(extractedInfos1[1].getLicenseId());
		sb.append(")");
		AnyLicenseInfo dis1 = LicenseInfoFactory.parseSPDXLicenseStringCompatV2(sb.toString(), doc1.getModelStore(), doc1.getDocumentUri(), doc1.getCopyManager());

		sb = new StringBuilder("(");
		sb.append(STD_LIC_ID_MPL11);
		sb.append(" OR ");
		sb.append(extractedInfos2[xlateDoc1ToDoc2LicId.get(1)].getLicenseId());
		sb.append(" OR ");
		sb.append(extractedInfos2[xlateDoc1ToDoc2LicId.get(0)].getLicenseId());
		sb.append(" OR ");
		sb.append(STD_LIC_ID_CC0);
		sb.append(")");
		AnyLicenseInfo dis2 = LicenseInfoFactory.parseSPDXLicenseStringCompatV2(sb.toString(), doc2.getModelStore(), doc2.getDocumentUri(), doc2.getCopyManager());
		
		sb = new StringBuilder("(");
		sb.append(STD_LIC_ID_MPL11);
		sb.append(" OR ");
		sb.append(extractedInfos2[xlateDoc1ToDoc2LicId.get(1)].getLicenseId());
		sb.append(" OR ");
		sb.append(STD_LIC_ID_CC0);
		sb.append(")");
		AnyLicenseInfo dis3 = LicenseInfoFactory.parseSPDXLicenseStringCompatV2(sb.toString(), doc2.getModelStore(), doc2.getDocumentUri(), doc2.getCopyManager());
		
		assertTrue(comparer.compareLicense(0, dis1, 1, dis2));
		assertFalse(comparer.compareLicense(0, dis1, 1, dis3));
		try {
			assertFalse(comparer.compareLicense(0, dis2, 1, dis2));
		} catch(SpdxCompareException ex) {
			// we expect a mappint exception
		}
		//Complex License
		DisjunctiveLicenseSet subcomplex1 = new DisjunctiveLicenseSet(doc1.getModelStore(), 
				doc1.getDocumentUri(), doc1.getModelStore().getNextId(IdType.Anonymous),doc1.getCopyManager(), true);
		subcomplex1.setMembers(new HashSet<>(Arrays.asList(
					new AnyLicenseInfo[] {lic1, conj1})));
		ConjunctiveLicenseSet complex1 = new ConjunctiveLicenseSet(doc1.getModelStore(), 
				doc1.getDocumentUri(), doc1.getModelStore().getNextId(IdType.Anonymous),doc1.getCopyManager(), true);
		complex1.setMembers(new HashSet<>(Arrays.asList(
				new AnyLicenseInfo[] {subcomplex1, dis1, extractedInfos1[0]})));
		DisjunctiveLicenseSet subcomplex2 = new DisjunctiveLicenseSet(doc2.getModelStore(), 
				doc2.getDocumentUri(), doc2.getModelStore().getNextId(IdType.Anonymous),doc2.getCopyManager(), true);
		subcomplex2.setMembers(new HashSet<>(Arrays.asList(
				new AnyLicenseInfo[] {conj2, lic1_1})));
		ConjunctiveLicenseSet complex2 = new ConjunctiveLicenseSet(doc2.getModelStore(), 
				doc2.getDocumentUri(), doc2.getModelStore().getNextId(IdType.Anonymous),doc2.getCopyManager(), true);
		complex2.setMembers(new HashSet<>(Arrays.asList(
			new AnyLicenseInfo[] {dis2, subcomplex2, extractedInfos2[xlateDoc1ToDoc2LicId.get(0)]})));
		
		DisjunctiveLicenseSet subcomplex3 = new DisjunctiveLicenseSet(doc2.getModelStore(), 
				doc2.getDocumentUri(), doc2.getModelStore().getNextId(IdType.Anonymous),doc2.getCopyManager(), true);
		subcomplex3.setMembers(new HashSet<>(Arrays.asList(
				new AnyLicenseInfo[] {conj3, lic1_1})));
		ConjunctiveLicenseSet complex3 = new ConjunctiveLicenseSet(doc2.getModelStore(), 
				doc2.getDocumentUri(), doc2.getModelStore().getNextId(IdType.Anonymous),doc2.getCopyManager(), true);
		complex3.setMembers(new HashSet<>(Arrays.asList(
			new AnyLicenseInfo[] {dis2, subcomplex3, extractedInfos2[xlateDoc1ToDoc2LicId.get(0)]})));
		assertTrue(comparer.compareLicense(0, complex1, 1, complex2));
		assertFalse(comparer.compareLicense(0, complex1, 1, complex3));
		//NONE
		SpdxNoneLicense noneLic1 = new SpdxNoneLicense();
		SpdxNoneLicense noneLic2 = new SpdxNoneLicense();
		SpdxNoAssertionLicense noAssertLic1 = new SpdxNoAssertionLicense();
		SpdxNoAssertionLicense noAssertLic2 = new SpdxNoAssertionLicense();
		assertTrue (comparer.compareLicense(0, noneLic1, 1, noneLic2));
		assertFalse (comparer.compareLicense(0, complex1, 1, noneLic2));
		//NOASSERTION
		assertTrue(comparer.compareLicense(0, noAssertLic1, 1, noAssertLic2));
		assertFalse(comparer.compareLicense(0, noAssertLic2, 1, lic1));
		assertFalse(comparer.compareLicense(0, noneLic2, 1, noAssertLic1));
	}

	/**
	 * Create a license ID mapping table between licInfos1 and licInfos2 based on EXACT matches of text
	 * @param licInfos1
	 * @param licInfos2
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private Map<Integer, Integer> createLicIdXlation(
			ExtractedLicenseInfo[] licInfos1,
			ExtractedLicenseInfo[] licInfos2) throws InvalidSPDXAnalysisException {
		Map<Integer, Integer> retval = new HashMap<>();
		for (int i = 0;i < licInfos1.length; i++) {
			boolean found = false;
			for (int j = 0; j < licInfos2.length; j++) {
				if (licInfos1[i].getExtractedText().equals(licInfos2[j].getExtractedText())) {
					if (found) {
						fail("Two licenses found with the same text: "+licInfos1[i].getExtractedText());
					}
					retval.put(i, j);
				}
			}
		}
		return retval;
	}

	/**
	 * Modifies the extracted license info license ID's by adding a digit to
	 * each of the ID's
	 * @param doc
	 * @return 
	 * @throws InvalidSPDXAnalysisException 
	 */
	private void alterExtractedLicenseInfoIds(SpdxDocument doc, int digit) throws InvalidSPDXAnalysisException {
		List<ExtractedLicenseInfo> newExtractedLicenseInfos = new ArrayList<>();
		Map<String, ExtractedLicenseInfo> oldToNewLicIds = new HashMap<>();
		Collection<ExtractedLicenseInfo> extracted = doc.getExtractedLicenseInfos();
		for (ExtractedLicenseInfo lic:extracted) {
			String oldId = lic.getLicenseId();
			String newId = oldId + String.valueOf(digit);
			ExtractedLicenseInfo newLic = new ExtractedLicenseInfo(doc.getModelStore(), doc.getDocumentUri(), newId, doc.getCopyManager(), true);
			newLic.setComment(lic.getComment());
			newLic.setExtractedText(lic.getExtractedText());
			newLic.setName(lic.getName());
			newLic.setSeeAlso(lic.getSeeAlso());
			List<String>ver = newLic.verify(); 
			assertEquals(0, ver.size());
			newExtractedLicenseInfos.add(newLic);
			oldToNewLicIds.put(oldId, newLic);
		}
		doc.setExtractedLicenseInfos(newExtractedLicenseInfos);
		// fix up all references to the old licenses
		// files
		try(@SuppressWarnings("unchecked")
        Stream<SpdxFile> fileStream = (Stream<SpdxFile>)SpdxModelFactory.getSpdxObjects(doc.getModelStore(), 
        		doc.getCopyManager(), SpdxConstantsCompatV2.CLASS_SPDX_FILE, doc.getDocumentUri(), null)) {
		    fileStream.forEach(file -> {
		        fixExtractedLicenseId(file, oldToNewLicIds);
		    });
		}
		// packages
	      try(@SuppressWarnings("unchecked")
	        Stream<SpdxPackage> packageStream = (Stream<SpdxPackage>)SpdxModelFactory.getSpdxObjects(doc.getModelStore(), 
	        		doc.getCopyManager(), SpdxConstantsCompatV2.CLASS_SPDX_PACKAGE, doc.getDocumentUri(), null)) {
	          packageStream.forEach(pkg -> {
	               fixExtractedLicenseIdPackage(pkg, oldToNewLicIds);
	            });
	        }

		// snippets
        try(@SuppressWarnings("unchecked")
          Stream<SpdxSnippet> snippetStream = (Stream<SpdxSnippet>)SpdxModelFactory.getSpdxObjects(doc.getModelStore(), 
        		  doc.getCopyManager(), SpdxConstantsCompatV2.CLASS_SPDX_SNIPPET, doc.getDocumentUri(), null)) {
            snippetStream.forEach(snippet -> {
                fixExtractedLicenseId(snippet, oldToNewLicIds);
            });
        }
		// NOTE - we're ignoring document data license
	}

	private void fixExtractedLicenseIdPackage(SpdxPackage element, Map<String, ExtractedLicenseInfo> oldToNewLicIds) {
		fixExtractedLicenseId(element, oldToNewLicIds);
		try {
			element.setLicenseDeclared(updateToNewExtractedLicenseId(element.getLicenseDeclared(), oldToNewLicIds));
		} catch (InvalidSPDXAnalysisException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private void fixExtractedLicenseId(SpdxItem element, Map<String, ExtractedLicenseInfo> oldToNewLicIds) {
		try {
			element.setLicenseConcluded(updateToNewExtractedLicenseId(element.getLicenseConcluded(), oldToNewLicIds));
			Collection<AnyLicenseInfo> newLicenseInfosFromFile = new HashSet<>();
			for (AnyLicenseInfo lic:element.getLicenseInfoFromFiles()) {
				newLicenseInfosFromFile.add(updateToNewExtractedLicenseId(lic, oldToNewLicIds));
			}
			element.getLicenseInfoFromFiles().clear();
			element.getLicenseInfoFromFiles().addAll(newLicenseInfosFromFile);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private AnyLicenseInfo updateToNewExtractedLicenseId(AnyLicenseInfo license, Map<String, ExtractedLicenseInfo> oldToNewLicIds) throws InvalidSPDXAnalysisException {
		if (license instanceof LicenseSet) {
			Collection<AnyLicenseInfo> newMembers = new HashSet<>();
			for (AnyLicenseInfo member:((LicenseSet)license).getMembers()) {
				newMembers.add(updateToNewExtractedLicenseId(member, oldToNewLicIds));
			}
			((LicenseSet)license).getMembers().clear();
			((LicenseSet)license).getMembers().addAll(newMembers);
			return license;
		} else if (license instanceof ExtractedLicenseInfo) {
			return oldToNewLicIds.get(license.getId());
		} else {
			return license;
		}
	}

	public void testIsDifferenceFound() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isDifferenceFound());
		doc2.setComment("a new doc comment");
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isDifferenceFound());
		// Note - we will test the isDifferenceFound in each of the specific
		// differences unit tests below

	}

	public void testIsSpdxVersionEqual() throws InvalidSPDXAnalysisException, SpdxCompareException, IOException {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isDifferenceFound());
		assertTrue(comparer.isSpdxVersionEqual());
		doc2.setSpecVersion("SPDX-2.0");
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isSpdxVersionEqual());
		doc1.setSpecVersion("SPDX-2.0");
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isDifferenceFound());
	}

	public void testGetSpdxDoc() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		String DOC_2_COMMENT = "doc2";
		String DOC_1_COMMENT = "doc1";
		doc1.setComment(DOC_1_COMMENT);
		doc2.setComment(DOC_2_COMMENT);
		comparer.compare(doc1, doc2);
		assertEquals(DOC_1_COMMENT, comparer.getSpdxDoc(0).getComment().get());
		assertEquals(DOC_2_COMMENT, comparer.getSpdxDoc(1).getComment().get());

	}

	public void testIsDataLicenseEqual() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxComparer comparer = new SpdxComparer();		
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		doc1.setStrict(false);
		doc2.setStrict(false);
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isDifferenceFound());
		assertTrue(comparer.isDataLicenseEqual());
		doc2.setDataLicense(LicenseInfoFactory.getListedLicenseByIdCompatV2(SpdxConstantsCompatV2.SPDX_DATA_LICENSE_ID_VERSION_1_0));
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isDataLicenseEqual());
	}

	public void testIsDocumentCommentsEqual()throws IOException, InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isDocumentCommentsEqual());
		assertFalse(comparer.isDifferenceFound());
		doc2.setComment("a new doc comment");
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isDocumentCommentsEqual());
		doc1.setComment("a new doc comment");
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isDifferenceFound());
		assertTrue(comparer.isDocumentCommentsEqual());
	}
	public void testIsExtractedLicensingInfosEqual() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException  {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		Collection<ExtractedLicenseInfo> orig1 = Collections.unmodifiableCollection(new HashSet<>(doc1.getExtractedLicenseInfos()));
		Collection<ExtractedLicenseInfo> orig2 = Collections.unmodifiableCollection(new HashSet<>(doc2.getExtractedLicenseInfos()));
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isExtractedLicensingInfosEqual());
		assertFalse(comparer.isDifferenceFound());

		int doc1id = 100;
		int doc2id = 200;
		String id1_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text1 = "License text 1";
		String name1 = "licname1";
		Collection<String> crossReff1 = new HashSet<>(Arrays.asList(new String[] {"http://cross.ref.one"}));
		String comment1 = "comment1";
		ExtractedLicenseInfo lic1_1 = new ExtractedLicenseInfo(doc1.getModelStore(), doc1.getDocumentUri(), id1_1, doc1.getCopyManager(), true);
		lic1_1.setExtractedText(text1);
		lic1_1.setName(name1);
		lic1_1.setSeeAlso(crossReff1);
		lic1_1.setComment(comment1);
		String id1_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic1_2 = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id1_2, doc2.getCopyManager(), true);
		lic1_2.setExtractedText(text1);
		lic1_2.setName(name1);
		lic1_2.setSeeAlso(crossReff1);
		lic1_2.setComment(comment1);
		
		String id2_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text2 = "License text 2";
		String name2 = "licname2";
		Collection<String> crossReff2 = new HashSet<>(Arrays.asList(new String[] {"http://cross.ref.one", "http://cross.ref.two"}));
		String comment2 = "comment2";
		ExtractedLicenseInfo lic2_1 = new ExtractedLicenseInfo(doc1.getModelStore(), doc1.getDocumentUri(), id2_1, doc1.getCopyManager(), true);
		lic2_1.setExtractedText(text2);
		lic2_1.setName(name2);
		lic2_1.setSeeAlso(crossReff2);
		lic2_1.setComment(comment2);
		String id2_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic2_2 = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id2_2, doc2.getCopyManager(), true);
		lic2_2.setExtractedText(text2);
		lic2_2.setName(name2);
		lic2_2.setSeeAlso(crossReff2);
		lic2_2.setComment(comment2);
		
		String id3_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text3 = "License text 3";
		String name3 = "";
		Collection<String> crossReff3 = new HashSet<>();
		String comment3 = "comment3";
		ExtractedLicenseInfo lic3_1 = new ExtractedLicenseInfo(doc1.getModelStore(), doc1.getDocumentUri(), id3_1, doc1.getCopyManager(), true);
		lic3_1.setExtractedText(text3);
		lic3_1.setName(name3);
		lic3_1.setSeeAlso(crossReff3);
		lic3_1.setComment(comment3);
		
		String id3_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic3_2 = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id3_2, doc2.getCopyManager(), true);
		lic3_2.setExtractedText(text3);
		lic3_2.setName(name3);
		lic3_2.setSeeAlso(crossReff3);
		lic3_2.setComment(comment3);
		
		String id4_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text4 = "License text 4";
		String name4 = "";
		Collection<String> crossReff4 = new HashSet<>();
		String comment4 = "";
		ExtractedLicenseInfo lic4_1 = new ExtractedLicenseInfo(doc1.getModelStore(), doc1.getDocumentUri(), id4_1, doc1.getCopyManager(), true);
		lic4_1.setExtractedText(text4);
		lic4_1.setName(name4);
		lic4_1.setSeeAlso(crossReff4);
		lic4_1.setComment(comment4);
		
		String id4_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic4_2 = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id4_2, doc2.getCopyManager(), true);
		lic4_2.setExtractedText(text4);
		lic4_2.setName(name4);
		lic4_2.setSeeAlso(crossReff4);
		lic4_2.setComment(comment4);
		
		// same licenses, different order
		List<ExtractedLicenseInfo> exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		List<ExtractedLicenseInfo> exLicenses2 = new ArrayList<>(orig2);
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2);
		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isExtractedLicensingInfosEqual());
		List<SpdxLicenseDifference> result = comparer.getExtractedLicenseDifferences(0, 1);
		assertEquals(0, result.size());
		result = comparer.getExtractedLicenseDifferences(1, 0);
		assertEquals(0, result.size());

		// More licenses in doc1
		exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		exLicenses2 = new ArrayList<>(orig2);
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2);

		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isExtractedLicensingInfosEqual());
		assertTrue(comparer.isDifferenceFound());
		
		// more licenses in doc2
		exLicenses1 = new ArrayList<>(orig1);

		exLicenses2 = new ArrayList<>(orig2);
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2);
		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isExtractedLicensingInfosEqual());
		assertTrue(comparer.isDifferenceFound());

		// license text different
		exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		exLicenses2 = new ArrayList<>(orig2);
		
		ExtractedLicenseInfo lic1_2_diff_Text = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id1_2, doc2.getCopyManager(), true);
		lic1_2_diff_Text.setExtractedText("Different Text");
		lic1_2_diff_Text.setName(name1);
		lic1_2_diff_Text.setSeeAlso(crossReff1);
		lic1_2_diff_Text.setComment(comment1);
		
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2_diff_Text);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2);

		doc1.setExtractedLicenseInfos(exLicenses1);
//		boolean caughtDupException = false;
//		try {
//			doc2.setExtractedLicenseInfos(exLicenses2);
//		} catch (DuplicateSpdxIdException e) {
//			caughtDupException = true;
//		}
//		assertTrue(caughtDupException);		
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isExtractedLicensingInfosEqual());
		assertTrue(comparer.isDifferenceFound());
		
		// license comments differ	
		exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		exLicenses2 = new ArrayList<>(orig2);
		
		ExtractedLicenseInfo lic1_2_diff_Comment = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id1_2, doc2.getCopyManager(), true );
		lic1_2_diff_Comment.setExtractedText(text1);
		lic1_2_diff_Comment.setName(name1);
		lic1_2_diff_Comment.setSeeAlso(crossReff1);
		lic1_2_diff_Comment.setComment("different comment");
		
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2_diff_Comment);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2);

		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isExtractedLicensingInfosEqual());
		assertTrue(comparer.isDifferenceFound());
		
		// license reference URLs differ
		
		exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		exLicenses2 = new ArrayList<>(orig2);
		
		ExtractedLicenseInfo lic1_2_diff_licenref = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id1_2, doc2.getCopyManager(), true );
		lic1_2_diff_licenref.setExtractedText(text1);
		lic1_2_diff_licenref.setName(name1);
		lic1_2_diff_licenref.setSeeAlso(crossReff2);
		lic1_2_diff_licenref.setComment(comment1);
		
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2_diff_licenref);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2);
		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isExtractedLicensingInfosEqual());
		assertTrue(comparer.isDifferenceFound());		
	}

	public void testGetUniqueExtractedLicenses() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException  {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		Collection<ExtractedLicenseInfo> orig1 = Collections.unmodifiableCollection(new HashSet<>(doc1.getExtractedLicenseInfos()));
		Collection<ExtractedLicenseInfo> orig2 = Collections.unmodifiableCollection(new HashSet<>(doc2.getExtractedLicenseInfos()));
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isExtractedLicensingInfosEqual());
		assertFalse(comparer.isDifferenceFound());

		int doc1id = 100;
		int doc2id = 200;
		String id1_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text1 = "License text 1";
		String name1 = "licname1";
		Collection<String> crossReff1 = new HashSet<>(Arrays.asList(new String[] {"http://cross.ref.one"}));
		String comment1 = "comment1";
		ExtractedLicenseInfo lic1_1 = new ExtractedLicenseInfo(id1_1, text1);
		lic1_1.setName(name1);
		lic1_1.setSeeAlso(crossReff1);
		lic1_1.setComment(comment1);
		String id1_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic1_2 = new ExtractedLicenseInfo(id1_2, text1);
		lic1_1.setName(name1);
		lic1_1.setSeeAlso(crossReff1);
		lic1_1.setComment(comment1);
		
		String id2_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text2 = "License text 2";
		String name2 = "licname2";
		Collection<String> crossReff2 = new HashSet<>(Arrays.asList(new String[] {"http://cross.ref.one", "http://cross.ref.two"}));
		String comment2 = "comment2";
		ExtractedLicenseInfo lic2_1 = new ExtractedLicenseInfo(id2_1, text2);
		lic1_1.setName(name2);
		lic1_1.setSeeAlso(crossReff2);
		lic1_1.setComment(comment2);
		String id2_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic2_2 = new ExtractedLicenseInfo(id2_2, text2);
		lic1_1.setName(name2);
		lic1_1.setSeeAlso(crossReff2);
		lic1_1.setComment(comment2);
		
		String id3_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text3 = "License text 3";
		String name3 = "";
		Collection<String> crossReff3 = new HashSet<>();
		String comment3 = "comment3";
		ExtractedLicenseInfo lic3_1 = new ExtractedLicenseInfo(id3_1, text3);
		lic1_1.setName(name3);
		lic1_1.setSeeAlso(crossReff3);
		lic1_1.setComment(comment3);
		
		String id3_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic3_2 = new ExtractedLicenseInfo(id3_2, text3);
		lic1_1.setName(name3);
		lic1_1.setSeeAlso(crossReff3);
		lic1_1.setComment(comment3);
		
		String id4_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text4 = "License text 4";
		String name4 = "";
		Collection<String> crossReff4 = new HashSet<>();
		String comment4 = "";
		ExtractedLicenseInfo lic4_1 = new ExtractedLicenseInfo(id4_1, text4);
		lic1_1.setName(name4);
		lic1_1.setSeeAlso(crossReff4);
		lic1_1.setComment(comment4);
		
		String id4_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic4_2 = new ExtractedLicenseInfo(id4_2, text4);
		lic1_1.setName(name4);
		lic1_1.setSeeAlso(crossReff4);
		lic1_1.setComment(comment4);
		
		// same licenses, different order
		List<ExtractedLicenseInfo> exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		List<ExtractedLicenseInfo> exLicenses2 = new ArrayList<>(orig2);
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2);
		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		List<ExtractedLicenseInfo> result = comparer.getUniqueExtractedLicenses(0, 1);
		assertEquals(0, result.size());
		result = comparer.getUniqueExtractedLicenses(1, 0);
		assertEquals(0, result.size());

		// More licenses in doc1
		exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		exLicenses2 = new ArrayList<>(orig2);
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2);
		
		Set<String> uniqueLicIds = new HashSet<>();
		uniqueLicIds.add(lic2_1.getLicenseId());
		uniqueLicIds.add(lic4_1.getLicenseId());

		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		result = comparer.getUniqueExtractedLicenses(0, 1);
		assertEquals(2, result.size());
		assertTrue(uniqueLicIds.contains(result.get(0).getLicenseId()));
		assertTrue(uniqueLicIds.contains(result.get(1).getLicenseId()));
		result = comparer.getUniqueExtractedLicenses(1, 0);
		assertEquals(0, result.size());
		
		// more licenses in doc2
		exLicenses1 = new ArrayList<>(orig1);

		exLicenses2 = new ArrayList<>(orig2);
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2);
		uniqueLicIds.clear();
		uniqueLicIds.add(lic3_2.getLicenseId());
		uniqueLicIds.add(lic1_2.getLicenseId());		
		uniqueLicIds.add(lic4_2.getLicenseId());
		uniqueLicIds.add(lic2_2.getLicenseId());
		
		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		result = comparer.getUniqueExtractedLicenses(0, 1);
		assertEquals(0, result.size());
		result = comparer.getUniqueExtractedLicenses(1, 0);
		assertEquals(4, result.size());
		assertTrue(uniqueLicIds.contains(result.get(0).getLicenseId()));
		assertTrue(uniqueLicIds.contains(result.get(1).getLicenseId()));
		assertTrue(uniqueLicIds.contains(result.get(2).getLicenseId()));
		assertTrue(uniqueLicIds.contains(result.get(3).getLicenseId()));
	}

	public void testGetExtractedLicenseDifferences() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException  {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		Collection<ExtractedLicenseInfo> orig1 = Collections.unmodifiableCollection(new HashSet<>(doc1.getExtractedLicenseInfos()));
		Collection<ExtractedLicenseInfo> orig2 = Collections.unmodifiableCollection(new HashSet<>(doc2.getExtractedLicenseInfos()));
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isExtractedLicensingInfosEqual());
		assertFalse(comparer.isDifferenceFound());

		int doc1id = 100;
		int doc2id = 200;
		String id1_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text1 = "License text 1";
		String name1 = "licname1";
		Collection<String> crossReff1 = new HashSet<>(Arrays.asList(new String[] {"http://cross.ref.one"}));
		String comment1 = "comment1";
		ExtractedLicenseInfo lic1_1 = new ExtractedLicenseInfo(doc1.getModelStore(), doc1.getDocumentUri(), id1_1, doc1.getCopyManager(), true);
		lic1_1.setExtractedText(text1);
		lic1_1.setName(name1);
		lic1_1.setSeeAlso(crossReff1);
		lic1_1.setComment(comment1);
		String id1_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic1_2 = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id1_2, doc2.getCopyManager(), true);
		lic1_2.setExtractedText(text1);
		lic1_2.setName(name1);
		lic1_2.setSeeAlso(crossReff1);
		lic1_2.setComment(comment1);
		
		String id2_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text2 = "License text 2";
		String name2 = "licname2";
		Collection<String> crossReff2 = new HashSet<>(Arrays.asList(new String[] {"http://cross.ref.one", "http://cross.ref.two"}));
		String comment2 = "comment2";
		ExtractedLicenseInfo lic2_1 = new ExtractedLicenseInfo(doc1.getModelStore(), doc1.getDocumentUri(), id2_1, doc1.getCopyManager(), true);
		lic2_1.setExtractedText(text2);
		lic2_1.setName(name2);
		lic2_1.setSeeAlso(crossReff2);
		lic2_1.setComment(comment2);
		String id2_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic2_2 = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id2_2, doc2.getCopyManager(), true);
		lic2_2.setExtractedText(text2);
		lic2_2.setName(name2);
		lic2_2.setSeeAlso(crossReff2);
		lic2_2.setComment(comment2);
		
		String id3_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text3 = "License text 3";
		String name3 = "";
		Collection<String> crossReff3 = new HashSet<>();
		String comment3 = "comment3";
		ExtractedLicenseInfo lic3_1 = new ExtractedLicenseInfo(doc1.getModelStore(), doc1.getDocumentUri(), id3_1, doc1.getCopyManager(), true);
		lic3_1.setExtractedText(text3);
		lic3_1.setName(name3);
		lic3_1.setSeeAlso(crossReff3);
		lic3_1.setComment(comment3);
		
		String id3_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic3_2 = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id3_2, doc2.getCopyManager(), true);
		lic3_2.setExtractedText(text3);
		lic3_2.setName(name3);
		lic3_2.setSeeAlso(crossReff3);
		lic3_2.setComment(comment3);
		
		String id4_1 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc1id++);
		String text4 = "License text 4";
		String name4 = "";
		Collection<String> crossReff4 = new HashSet<>();
		String comment4 = "";
		ExtractedLicenseInfo lic4_1 = new ExtractedLicenseInfo(doc1.getModelStore(), doc1.getDocumentUri(), id4_1, doc1.getCopyManager(), true);
		lic4_1.setExtractedText(text4);
		lic4_1.setName(name4);
		lic4_1.setSeeAlso(crossReff4);
		lic4_1.setComment(comment4);
		
		String id4_2 = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + Integer.toString(doc2id++);
		ExtractedLicenseInfo lic4_2 = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id4_2, doc2.getCopyManager(), true);
		lic4_2.setExtractedText(text4);
		lic4_2.setName(name4);
		lic4_2.setSeeAlso(crossReff4);
		lic4_2.setComment(comment4);
		
		// same licenses, different order
		List<ExtractedLicenseInfo> exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		List<ExtractedLicenseInfo> exLicenses2 = new ArrayList<>(orig2);
		exLicenses2.add(lic3_2);
		exLicenses2.add(lic1_2);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2);
		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isExtractedLicensingInfosEqual());
		List<SpdxLicenseDifference> result = comparer.getExtractedLicenseDifferences(0, 1);
		assertEquals(0, result.size());
		result = comparer.getExtractedLicenseDifferences(1, 0);
		assertEquals(0, result.size());
		
		// differences
		exLicenses1 = new ArrayList<>(orig1);
		exLicenses1.add(lic1_1);
		exLicenses1.add(lic2_1);
		exLicenses1.add(lic3_1);
		exLicenses1.add(lic4_1);
		exLicenses2 = new ArrayList<>(orig2);
		
		String differentName = "differentLicenseName";
		ExtractedLicenseInfo lic1_2_diff_name = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id1_2, doc2.getCopyManager(), true);
		lic1_2_diff_name.setExtractedText(text1);
		lic1_2_diff_name.setName(differentName);
		lic1_2_diff_name.setSeeAlso(crossReff1);
		lic1_2_diff_name.setComment(comment1);
		
		String differentComment = "different comment";
		ExtractedLicenseInfo lic2_2_diff_Comment = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id2_2, doc2.getCopyManager(), true);
		lic2_2_diff_Comment.setExtractedText(text2);
		lic2_2_diff_Comment.setName(name2);
		lic2_2_diff_Comment.setSeeAlso(crossReff2);
		lic2_2_diff_Comment.setComment(differentComment);
		
		ExtractedLicenseInfo lic3_2_diff_licenref = new ExtractedLicenseInfo(doc2.getModelStore(), doc2.getDocumentUri(), id3_2, doc2.getCopyManager(), true);
		lic3_2_diff_licenref.setExtractedText(text3);
		lic3_2_diff_licenref.setName(name3);
		lic3_2_diff_licenref.setSeeAlso(crossReff2);
		lic3_2_diff_licenref.setComment(comment3);
		
		exLicenses2.add(lic3_2_diff_licenref);
		exLicenses2.add(lic1_2_diff_name);
		exLicenses2.add(lic4_2);
		exLicenses2.add(lic2_2_diff_Comment);

		doc1.setExtractedLicenseInfos(exLicenses1);
		doc2.setExtractedLicenseInfos(exLicenses2);
		comparer.compare(doc1, doc2);
		
		result = comparer.getExtractedLicenseDifferences(0,1);
		assertEquals(3, result.size());
		boolean lic1Found = false;
		boolean lic2Found = false;
		boolean lic3Found = false;
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).getIdA().equals(lic2_1.getLicenseId())) {
				lic2Found = true;
				assertEquals(lic2_2.getLicenseId(), result.get(i).getIdB());
				assertEquals(comment2, result.get(i).getCommentA());
				assertEquals(differentComment, result.get(i).getCommentB());
				assertEquals(name2, result.get(i).getLicenseNameA());
				assertEquals(name2, result.get(i).getLicenseNameB());
				assertEquals(text2, result.get(i).getLicenseText());
				assertTrue(stringsSame(crossReff2, result.get(i).getSourceUrlsA()));
				assertTrue(stringsSame(crossReff2, result.get(i).getSourceUrlsB()));
			} else if (result.get(i).getIdA().equals(lic1_1.getLicenseId())) {
				lic1Found = true;
				assertEquals(lic1_2.getLicenseId(), result.get(i).getIdB());
				assertEquals(comment1, result.get(i).getCommentA());
				assertEquals(comment1, result.get(i).getCommentB());
				assertEquals(name1, result.get(i).getLicenseNameA());
				assertEquals(differentName, result.get(i).getLicenseNameB());
				assertEquals(text1, result.get(i).getLicenseText());
				assertTrue(stringsSame(crossReff1, result.get(i).getSourceUrlsA()));
				assertTrue(stringsSame(crossReff1, result.get(i).getSourceUrlsB()));
			}else if (result.get(i).getIdA().equals(lic3_1.getLicenseId())) {
				lic3Found = true;
				assertEquals(lic3_2.getLicenseId(), result.get(i).getIdB());
				assertEquals(comment3, result.get(i).getCommentA());
				assertEquals(comment3, result.get(i).getCommentB());
				assertEquals(name3, result.get(i).getLicenseNameA());
				assertEquals(name3, result.get(i).getLicenseNameB());
				assertEquals(text3, result.get(i).getLicenseText());
				assertTrue(stringsSame(crossReff3, result.get(i).getSourceUrlsA()));
				assertTrue(stringsSame(crossReff2, result.get(i).getSourceUrlsB()));
			}
		}
		assertTrue(lic1Found);
		assertTrue(lic2Found);
		assertTrue(lic3Found);
		
		result = comparer.getExtractedLicenseDifferences(1, 0);
		assertEquals(3, result.size());
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).getIdB().equals(lic2_2.getLicenseId())) {
				lic2Found = true;
				assertEquals(lic2_1.getLicenseId(), result.get(i).getIdB());
				assertEquals(differentComment, result.get(i).getCommentA());
				assertEquals(comment2, result.get(i).getCommentB());
				assertEquals(name2, result.get(i).getLicenseNameA());
				assertEquals(name2, result.get(i).getLicenseNameB());
				assertEquals(text2, result.get(i).getLicenseText());
				assertTrue(stringsSame(crossReff2, result.get(i).getSourceUrlsA()));
				assertTrue(stringsSame(crossReff2, result.get(i).getSourceUrlsB()));
			} else if (result.get(i).getIdA().equals(lic1_2.getLicenseId())) {
				lic1Found = true;
				assertEquals(lic1_1.getLicenseId(), result.get(i).getIdB());
				assertEquals(comment1, result.get(i).getCommentA());
				assertEquals(comment1, result.get(i).getCommentB());
				assertEquals(differentName, result.get(i).getLicenseNameA());
				assertEquals(name1, result.get(i).getLicenseNameB());
				assertEquals(text1, result.get(i).getLicenseText());
				assertTrue(stringsSame(crossReff1, result.get(i).getSourceUrlsA()));
				assertTrue(stringsSame(crossReff1, result.get(i).getSourceUrlsB()));
			}else if (result.get(i).getIdA().equals(lic3_2.getLicenseId())) {
				lic3Found = true;
				assertEquals(lic3_1.getLicenseId(), result.get(i).getIdB());
				assertEquals(comment3, result.get(i).getCommentA());
				assertEquals(comment3, result.get(i).getCommentB());
				assertEquals(name3, result.get(i).getLicenseNameA());
				assertEquals(name3, result.get(i).getLicenseNameB());
				assertEquals(text3, result.get(i).getLicenseText());
				assertTrue(stringsSame(crossReff2, result.get(i).getSourceUrlsA()));
				assertTrue(stringsSame(crossReff3, result.get(i).getSourceUrlsB()));
			}
		}
		assertTrue(lic1Found);
		assertTrue(lic2Found);
		assertTrue(lic3Found);
	}

	/**
	 * Compare two arrays or strings
	 * @param crossReff1
	 * @param collection
	 * @return true if arrays contain the same strings independant of order
	 */
	private boolean stringsSame(Collection<String> crossReff1, Collection<String> collection) {
		if (crossReff1 == null) {
			return (collection == null);
		}
		if (collection == null) {
			return false;
		}
		if (crossReff1.size() != collection.size()) {
			return false;
		}
		return crossReff1.containsAll(collection);
	}

	public void testIsPackageEqual() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException  {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isPackagesEquals());
		assertFalse(comparer.isDifferenceFound());
		getDescribedPackage(doc1).setDescription("Different Description");
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isPackagesEquals());
		assertTrue(comparer.isDifferenceFound());
		// note - other test cases will test to make sure isPackageEquals is set for all changes where it should be false
	}

	public void testIsCreatorInformationEqual() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException  {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isCreatorInformationEqual());
		assertFalse(comparer.isDifferenceFound());

		// one more creator
		SpdxCreatorInformation origCreators = doc1.getCreationInfo();
		origCreators.getCreators().add("Person: One More Person");
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isCreatorInformationEqual());
		assertTrue(comparer.isDifferenceFound());
		
		// different creator
		SpdxCreatorInformation updatedCreators = doc1.createCreationInfo(Arrays.asList(new String[] {"Person: different"}),
				CREATION_INFOA.getCreated());
		updatedCreators.setComment(CREATION_INFOA.getComment().get());
		updatedCreators.setLicenseListVersion(CREATION_INFOA.getLicenseListVersion().get());
		doc1.setCreationInfo(updatedCreators);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isCreatorInformationEqual());
		assertTrue(comparer.isDifferenceFound());
		
		// Different creation date
		updatedCreators = doc1.createCreationInfo(Arrays.asList(new String[] {"Person: CreatorA"}),
				"2020-11-29T18:30:22Z");
		updatedCreators.setComment(CREATION_INFOA.getComment().get());
		updatedCreators.setLicenseListVersion(CREATION_INFOA.getLicenseListVersion().get());
		doc1.setCreationInfo(updatedCreators);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isCreatorInformationEqual());
		assertTrue(comparer.isDifferenceFound());
		
		// different comment
		updatedCreators = doc1.createCreationInfo(Arrays.asList(new String[] {"Person: CreatorA"}),
				CREATION_INFOA.getCreated());
		updatedCreators.setComment("Different comment");
		updatedCreators.setLicenseListVersion(CREATION_INFOA.getLicenseListVersion().get());
		doc1.setCreationInfo(updatedCreators);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isCreatorInformationEqual());
		assertTrue(comparer.isDifferenceFound());
		
		// different license list version
		updatedCreators = doc1.createCreationInfo(Arrays.asList(new String[] {"Person: CreatorA"}),
				CREATION_INFOA.getCreated());
		updatedCreators.setComment(CREATION_INFOA.getComment().get());
		updatedCreators.setLicenseListVersion("1.25");
		doc1.setCreationInfo(updatedCreators);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isCreatorInformationEqual());
		assertTrue(comparer.isDifferenceFound());
	}

	public void testGetUniqueCreators() throws IOException, InvalidSPDXAnalysisException, SpdxCompareException  {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isCreatorInformationEqual());
		assertFalse(comparer.isDifferenceFound());
		List<String> result = comparer.getUniqueCreators(0, 1);
		assertEquals(0, result.size());
		result = comparer.getUniqueCreators(1, 0);
		assertEquals(0, result.size());		
		
		// different order of creators
		String creator1 = "Person: Creator1";
		String creator2 = "Organization: Creator2";
		String creator3 = "Tool: Creator3";
		String createdDate = "2013-02-03T00:00:00Z";
		String creatorComment = "Creator comments";
		String licenseListVersion = "1.19";
		List<String> creators1 = Arrays.asList(new String[] {creator1, creator2, creator3});
		List<String> creators2 = Arrays.asList(new String[] {creator3, creator2, creator1});
		
		SpdxCreatorInformation creationInfo1 = doc1.createCreationInfo(
				creators1, createdDate)
				.setComment(creatorComment)
				.setLicenseListVersion(licenseListVersion);
		SpdxCreatorInformation creationInfo2 = doc2.createCreationInfo(
				creators2, createdDate)
				.setComment(creatorComment)
				.setLicenseListVersion(licenseListVersion);
		doc1.setCreationInfo(creationInfo1);
		doc2.setCreationInfo(creationInfo2);
		comparer.compare(doc1, doc2);
		assertTrue(comparer.isCreatorInformationEqual());
		assertFalse(comparer.isDifferenceFound());
		result = comparer.getUniqueCreators(0, 1);
		assertEquals(0, result.size());
		result = comparer.getUniqueCreators(1, 0);
		assertEquals(0, result.size());		
	
		// more results in the first
		creators1 = Arrays.asList(new String[] {creator1, creator2, creator3});
		creators2 = Arrays.asList(new String[] {creator1});
		creationInfo1 = doc1.createCreationInfo(
				creators1, createdDate)
				.setComment(creatorComment)
				.setLicenseListVersion(licenseListVersion);
		creationInfo2 = doc2.createCreationInfo(
				creators2, createdDate)
				.setComment(creatorComment)
				.setLicenseListVersion(licenseListVersion);
		doc1.setCreationInfo(creationInfo1);
		doc2.setCreationInfo(creationInfo2);
		Set<String> additionalCreators = new HashSet<>();
		additionalCreators.add(creator2);
		additionalCreators.add(creator3);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isCreatorInformationEqual());
		assertTrue(comparer.isDifferenceFound());
		result = comparer.getUniqueCreators(0, 1);
		assertEquals(additionalCreators.size(), result.size());
		for (int i = 0; i < result.size(); i++) {
			assertTrue(additionalCreators.contains(result.get(i)));
		}
		result = comparer.getUniqueCreators(1, 0);
		assertEquals(0, result.size());	
		
		// more results in the second
		creators1 = Arrays.asList(new String[] {creator2, creator3});
		creators2 = Arrays.asList(new String[] {creator1, creator2, creator3});
		creationInfo1 = doc1.createCreationInfo(
				creators1, createdDate)
				.setComment(creatorComment)
				.setLicenseListVersion(licenseListVersion);
		creationInfo2 = doc2.createCreationInfo(
				creators2, createdDate)
				.setComment(creatorComment)
				.setLicenseListVersion(licenseListVersion);
		doc1.setCreationInfo(creationInfo1);
		doc2.setCreationInfo(creationInfo2);
		additionalCreators.clear();
		additionalCreators.add(creator1);
		comparer.compare(doc1, doc2);
		assertFalse(comparer.isCreatorInformationEqual());
		assertTrue(comparer.isDifferenceFound());
		result = comparer.getUniqueCreators(0, 1);
		assertEquals(0, result.size());	
		result = comparer.getUniqueCreators(1, 0);
		assertEquals(additionalCreators.size(), result.size());
		for (int i = 0; i < result.size(); i++) {
			assertTrue(additionalCreators.contains(result.get(i)));
		}
	}

	public void testGetFileDifferences() throws InvalidSPDXAnalysisException, IOException, SpdxCompareException, InvalidLicenseStringException {
		SpdxComparer comparer = new SpdxComparer();
		SpdxDocument doc1 = createTestSpdxDoc(DOC_URIA);
		SpdxDocument doc2 = createTestSpdxDoc(DOC_URIB);
		// need to remove the file dependencies to prevent values from getting over-written
		
		alterExtractedLicenseInfoIds(doc2, 1);
		comparer.compare(doc1, doc2);
		List<SpdxFileDifference> differences = comparer.getFileDifferences(0, 1);
		assertEquals(0, differences.size());
		Collection<SpdxFile> describedPackageFiles = this.getDescribedPackage(doc2).getFiles();
		SpdxFile[] files = describedPackageFiles.toArray(new SpdxFile[describedPackageFiles.size()]);

		String file0Name = files[0].getName().get();
		files[0].setComment("a new and unique comment");

		String file1Name = files[1].getName().get();
		files[1].setLicenseConcluded(LicenseInfoFactory.parseSPDXLicenseStringCompatV2(STD_LIC_ID_CC0));
		comparer.compare(doc1, doc2);
		differences = comparer.getFileDifferences(0, 1);
		assertEquals(2, differences.size());
		if (differences.get(0).getFileName().equals(file0Name)) {
			assertFalse(differences.get(0).isCommentsEquals());
			assertEquals(differences.get(0).getCommentB(),files[0].getComment().get());
		} else if (differences.get(0).getFileName().equals(file1Name)) {
			assertFalse(differences.get(0).isConcludedLicenseEquals());
			assertEquals(((License)(files[1].getLicenseConcluded())).getLicenseId(), 
					(differences.get(0).getConcludedLicenseB()));
		} else {
			fail("invalid file name");
		}
		if (differences.get(1).getFileName().equals(file0Name)) {
			assertFalse(differences.get(1).isCommentsEquals());
			assertEquals(differences.get(1).getCommentB(),files[0].getComment().get());
		} else if (differences.get(1).getFileName().equals(file1Name)) {
			assertFalse(differences.get(1).isConcludedLicenseEquals());
			assertEquals(((License)(files[1].getLicenseConcluded())).getLicenseId(), differences.get(1).getConcludedLicenseB());
		} else {
			fail ("Invalid file name");
		}
		
		comparer.compare(doc2, doc1);
		differences = comparer.getFileDifferences(0, 1);
		assertEquals(2, differences.size());
		if (differences.get(0).getFileName().equals(file0Name)) {
			assertFalse(differences.get(0).isCommentsEquals());
			assertEquals(differences.get(0).getCommentA(),files[0].getComment().get());
		} else if (differences.get(0).getFileName().equals(file1Name)) {
			assertFalse(differences.get(0).isConcludedLicenseEquals());
			assertEquals(((License)(files[1].getLicenseConcluded())).getLicenseId(), differences.get(0).getConcludedLicenseA());
		} else {
			fail("invalid file name");
		}
		if (differences.get(1).getFileName().equals(file0Name)) {
			assertFalse(differences.get(1).isCommentsEquals());
			assertEquals(differences.get(1).getCommentA(),files[0].getComment().get());
		} else if (differences.get(1).getFileName().equals(file1Name)) {
			assertFalse(differences.get(1).isConcludedLicenseEquals());
			assertEquals(((License)(files[1].getLicenseConcluded())).getLicenseId(), differences.get(1).getConcludedLicenseA());
		} else {
			fail ("Invalid file name");
		}
	}
	
	/**
	 * Get the describe packages
	 * @param doc
	 * @return
	 * @throws InvalidSPDXAnalysisException 
	 */
	private SpdxPackage getDescribedPackage(SpdxDocument doc) throws InvalidSPDXAnalysisException {
		Collection<SpdxElement> describedItems = doc.getDocumentDescribes();
		for (SpdxElement item:describedItems) {
			if (item instanceof SpdxPackage) {
				return (SpdxPackage)item;
			}
		}
		return null;
	}

	public void testGetUniqueFiles() throws InvalidSPDXAnalysisException, IOException, SpdxCompareException {
		
		String uri1 = "http://uri1";
		String uri2 = "http://uri2";
		
		SpdxDocument docA = new SpdxDocument(uri1);
		SpdxDocument docB = new SpdxDocument(uri2);
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOB);
		UnitTestHelper.copyObjectsToDoc(docA, Arrays.asList(EXTRACTED_LICENSESA));
		UnitTestHelper.copyObjectsToDoc(docB, Arrays.asList(EXTRACTED_LICENSESB));
		Collection<SpdxFile> pkgAFiles = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1A}));
		UnitTestHelper.copyObjectsToDoc(docA, pkgAFiles);
		Collection<SpdxFile> pkgBFiles = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE3B}));
		UnitTestHelper.copyObjectsToDoc(docB, pkgBFiles);
		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		List<SpdxFile> result = comparer.getUniqueFiles(0, 1);
		assertEquals(1, result.size());
		assertTrue(FILE1A.equivalent(result.get(0)));
		result = comparer.getUniqueFiles(1, 0);
		assertEquals(1, result.size());
		assertTrue(FILE3B.equivalent(result.get(0)));
	}
	
	public void testGetFileDifferences2()throws InvalidSPDXAnalysisException, IOException, SpdxCompareException {
		SpdxDocument docA = new SpdxDocument(DOC_URIA);
		SpdxDocument docB = new SpdxDocument(DOC_URIB);
		docA.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESA));
		docB.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESB));
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOB);

		Collection<SpdxFile> pkgAFiles = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1A, FILE2A, FILE3A}));
		UnitTestHelper.copyObjectsToDoc(docA, pkgAFiles);
				
		Collection<SpdxFile> pkgBFiles = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1B_DIFF_CHECKSUM, FILE2B, FILE3B}));
		UnitTestHelper.copyObjectsToDoc(docB, pkgBFiles);
		
		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		List<SpdxFileDifference> result = comparer.getFileDifferences(0, 1);
		assertEquals(1, result.size());
		assertEquals(FILE1A.getName().get(), result.get(0).getName());
		assertFalse(result.get(0).isChecksumsEquals());
		result = comparer.getFileDifferences(1, 0);
		assertEquals(1, result.size());
		assertEquals(FILE1A.getName().get(), result.get(0).getName());
		assertFalse(result.get(0).isChecksumsEquals());
	}
	
	public void testGetPackageDifferences()throws InvalidSPDXAnalysisException, IOException, SpdxCompareException {
		SpdxDocument docA = new SpdxDocument(DOC_URIA);
		SpdxDocument docB = new SpdxDocument(DOC_URIB);
		docA.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESA));
		docB.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESB));
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOB);
		SpdxItem[] itemsA = new SpdxItem[] {pkgA1, pkgA2};
		SpdxItem[] itemsB = new SpdxItem[] {pkgB1, pkgB2};
		for (int i = 0; i < itemsA.length; i++) {
			Relationship rel = docA.createRelationship(itemsA[i], 
					RelationshipType.DESCRIBES, "");
			docA.addRelationship(rel);
		}
		for (int i = 0; i < itemsB.length; i++) {
			Relationship rel = docB.createRelationship(itemsB[i], 
					RelationshipType.DESCRIBES, "");
			docB.addRelationship(rel);
		}

		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		List<SpdxPackageComparer> result = comparer.getPackageDifferences();
		assertEquals(1, result.size());
		assertEquals(NAMEB, result.get(0).getDocPackage(docA).getName().get());
		assertFalse(result.get(0).isCommentsEquals());
		result = comparer.getPackageDifferences();
		assertEquals(1, result.size());
		assertEquals(NAMEB, result.get(0).getDocPackage(docA).getName().get());
		assertFalse(result.get(0).isCommentsEquals());
	}
	
	public void testGetUniquePackages()throws InvalidSPDXAnalysisException, IOException, SpdxCompareException {
		String uri1 = "http://uri1";
		String uri2 = "http://uri2";
		SpdxDocument docA = new SpdxDocument(uri1);
		SpdxDocument docB = new SpdxDocument(uri2);
		UnitTestHelper.copyObjectsToDoc(docA, Arrays.asList(EXTRACTED_LICENSESA));
		UnitTestHelper.copyObjectsToDoc(docB, Arrays.asList(EXTRACTED_LICENSESB));
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOB);
		
		SpdxItem[] itemsA = new SpdxItem[] {pkgA1, pkgA2};
		SpdxItem[] itemsB = new SpdxItem[] {pkgB1, pkgB2};
		pkgB2.setName(NAMEC);
		UnitTestHelper.copyObjectsToDoc(docA, Arrays.asList(itemsA));
		UnitTestHelper.copyObjectsToDoc(docB, Arrays.asList(itemsB));

		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		List<SpdxPackage> result = comparer.getUniquePackages(0, 1);
		assertEquals(1, result.size());
		assertTrue(pkgA2.equivalent(result.get(0)));
		result = comparer.getUniquePackages(1, 0);
		assertEquals(1, result.size());
		assertTrue(pkgB2.equivalent(result.get(0)));
		
	}
	
	public void testElementEquivalent() throws InvalidSPDXAnalysisException {

		String nameA = "A";
		String nameB = "B";
		GenericSpdxElement e1a = new GenericSpdxElement();
		e1a.setName(nameA);
		Optional<SpdxElement> element1A = Optional.of(e1a);
		GenericSpdxElement e2a = new GenericSpdxElement();
		e2a.setName(nameA);
		Optional<SpdxElement> element2A = Optional.of(e2a);
		GenericSpdxElement e1b = new GenericSpdxElement();
		e1b.setName(nameB);
		Optional<SpdxElement> element1B = Optional.of(e1b);
		Optional<SpdxElement> none1 = Optional.empty();
		Optional<SpdxElement> none2 = Optional.empty();
		assertTrue(SpdxComparer.elementsEquivalent(element1A, element2A, new HashMap<>()));
		assertFalse(SpdxComparer.elementsEquivalent(element1A, element1B, new HashMap<>()));
		assertFalse(SpdxComparer.elementsEquivalent(element1A, none1, new HashMap<>()));
		assertTrue(SpdxComparer.elementsEquivalent(none1, none2, new HashMap<>()));
	}

	public void testFindUniqueChecksums() throws InvalidSPDXAnalysisException {
		Collection<Checksum> checksumsA = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM1, CHECKSUM2, CHECKSUM3}));
		Collection<Checksum> checksumsB = new HashSet<>(Arrays.asList(new Checksum[] {CHECKSUM2, CHECKSUM3, CHECKSUM4}));
		List<Checksum> result = SpdxComparer.findUniqueChecksums(checksumsA, checksumsB);
		assertEquals(1, result.size());
		assertEquals(CHECKSUM1, result.get(0));
	}

	public void testCollectAllFiles() throws InvalidSPDXAnalysisException {
		SpdxDocument docA = new SpdxDocument(DOC_URIA);
		docA.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESA));
		docA.setName(DOC_NAMEA);
		docA.setCreationInfo(CREATION_INFOA);
		Relationship docDescribesPackage = docA.createRelationship(pkgA1, 
				RelationshipType.DESCRIBES, "PackageA describes");
		docA.addRelationship(docDescribesPackage);
		
		Relationship docDescribesPackage2 = docA.createRelationship(pkgA2, 
				RelationshipType.DESCRIBES, "PackageA2 describes");
		docA.addRelationship(docDescribesPackage2);
		SpdxElement[] docDescribes = docA.getDocumentDescribes().toArray(new SpdxElement[2]);
		SpdxPackage[] pkgs = new SpdxPackage[2];
		pkgs[0] = (SpdxPackage)docDescribes[0];
		pkgs[1] = (SpdxPackage)docDescribes[1];

		Collection<SpdxFile> pkgAFiles = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE1A, FILE2A}));
		pkgs[0].getFiles().clear();
		pkgs[0].getFiles().addAll(pkgAFiles);
		
		Collection<SpdxFile> pkgAFiles2 = new HashSet<>(Arrays.asList(new SpdxFile[] {FILE3B}));
		pkgs[1].getFiles().clear();
		pkgs[1].getFiles().addAll(pkgAFiles2);
		Relationship docDescribesFile = docA.createRelationship(FILE4A, 
				RelationshipType.DESCRIBES, "PackageA describes");
		docA.addRelationship(docDescribesFile);
		List<SpdxFile> expected = Arrays.asList(new SpdxFile[] {FILE1A, FILE2A, FILE3B, FILE4A});
		
		SpdxComparer comparer = new SpdxComparer();
		List<SpdxFile> result = comparer.collectAllFiles(docA);
		assertTrue(UnitTestHelper.isListsEquivalent(expected, result));
	}
	
	public void testExternalDocumentRefsEqual() throws InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxItem[] itemsA = new SpdxItem[] {pkgA1};
		SpdxItem[] itemsB = new SpdxItem[] {pkgB1};
		SpdxDocument docA = new SpdxDocument(DOC_URIA);
		SpdxDocument docB = new SpdxDocument(DOC_URIB);
		docA.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESA));
		docB.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESB));
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOB);
		for (int i = 0; i < itemsA.length; i++) {
			Relationship rel = docA.createRelationship(itemsA[i], 
					RelationshipType.DESCRIBES, "");
			docA.addRelationship(rel);
		}
		for (int i = 0; i < itemsB.length; i++) {
			Relationship rel = docB.createRelationship(itemsB[i], 
					RelationshipType.DESCRIBES, "");
			docB.addRelationship(rel);
		}

		docA.setExternalDocumentRefs(new HashSet<>(Arrays.asList(new ExternalDocumentRef[] {ref1, ref2} )));
		docB.setExternalDocumentRefs(new HashSet<>(Arrays.asList(new ExternalDocumentRef[] { ref2, ref3 })));
		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isExternalDocumentRefsEquals());
		List<ExternalDocumentRef> result = comparer.getUniqueExternalDocumentRefs(0, 1);
		assertEquals(1, result.size());
		assertTrue(ref1.equivalent(result.get(0)));
		result = comparer.getUniqueExternalDocumentRefs(1, 0);
		assertEquals(1, result.size());
		assertTrue(ref3.equivalent(result.get(0)));
	}
	
	public void testDocumentAnnotationsEquals() throws InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxItem[] itemsA = new SpdxItem[] {pkgA1};
		SpdxItem[] itemsB = new SpdxItem[] {pkgB1};
		SpdxDocument docA = new SpdxDocument(DOC_URIA);
		SpdxDocument docB = new SpdxDocument(DOC_URIB);
		docA.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESA));
		docB.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESB));
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOB);
		for (int i = 0; i < itemsA.length; i++) {
			Relationship rel = docA.createRelationship(itemsA[i], 
					RelationshipType.DESCRIBES, "");
			docA.addRelationship(rel);
		}
		for (int i = 0; i < itemsB.length; i++) {
			Relationship rel = docB.createRelationship(itemsB[i], 
					RelationshipType.DESCRIBES, "");
			docB.addRelationship(rel);
		}

		docA.setAnnotations(new HashSet<>(Arrays.asList(new Annotation[] {ANNOTATION1, ANNOTATION2})));
		docB.setAnnotations(new HashSet<>(Arrays.asList(new Annotation[] {ANNOTATION2, ANNOTATION3})));
		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isDocumentAnnotationsEquals());
		List<Annotation> result = comparer.getUniqueDocumentAnnotations(0, 1);
		assertEquals(1, result.size());
		assertTrue(ANNOTATION1.equivalent(result.get(0)));
		result = comparer.getUniqueDocumentAnnotations(1, 0);
		assertEquals(1, result.size());
		assertTrue(ANNOTATION3.equivalent(result.get(0)));
	}


	public void testDocumentRelationshipsEquals() throws InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxItem[] itemsA = new SpdxItem[] {pkgA1};
		SpdxItem[] itemsB = new SpdxItem[] {pkgB1};
		SpdxDocument docA = new SpdxDocument(DOC_URIA);
		SpdxDocument docB = new SpdxDocument(DOC_URIB);
		docA.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESA));
		docB.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESB));
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOB);
		for (int i = 0; i < itemsA.length; i++) {
			Relationship rel = docA.createRelationship(itemsA[i], 
					RelationshipType.DESCRIBES, "");
			docA.addRelationship(rel);
		}
		for (int i = 0; i < itemsB.length; i++) {
			Relationship rel = docB.createRelationship(itemsB[i], 
					RelationshipType.DESCRIBES, "");
			docB.addRelationship(rel);
		}

		docA.setRelationships(new HashSet<>(Arrays.asList(new Relationship[] {RELATIONSHIP1, RELATIONSHIP2})));
		
		String relationship2elementName = RELATIONSHIP2.getRelatedSpdxElement().get().getName().get();
		Relationship[] setRelationships = docA.getRelationships().toArray(new Relationship[2]);
		List<String> setRelatedElementNames = new ArrayList<>();
		for (Relationship rel:setRelationships) {
			setRelatedElementNames.add(rel.getRelatedSpdxElement().get().getName().get());
		}
		assertTrue(setRelatedElementNames.contains(relationship2elementName));
		docB.setRelationships(new HashSet<>(Arrays.asList(new Relationship[] {RELATIONSHIP2, RELATIONSHIP3})));
		docA.getRelationships().toArray(new Relationship[2]);
		setRelatedElementNames.clear();
		for (Relationship rel:setRelationships) {
			setRelatedElementNames.add(rel.getRelatedSpdxElement().get().getName().get());
		}
		assertTrue(setRelatedElementNames.contains(relationship2elementName));
		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		docA.getRelationships().toArray(new Relationship[2]);
		setRelatedElementNames.clear();
		for (Relationship rel:setRelationships) {
			setRelatedElementNames.add(rel.getRelatedSpdxElement().get().getName().get());
		}
		assertTrue(setRelatedElementNames.contains(relationship2elementName));
		String related3ElementName = RELATIONSHIP3.getRelatedSpdxElement().get().getName().get();
		List<String> set2names = new ArrayList<>();
		for (Relationship rel:docB.getRelationships()) {
			set2names.add(rel.getRelatedSpdxElement().get().getName().get());
		}
		assertTrue(set2names.contains(related3ElementName));
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isDocumentRelationshipsEquals());
		List<Relationship> result= comparer.getUniqueDocumentRelationship(0, 1);
		assertEquals(1, result.size());
		assertTrue(RELATIONSHIP1.equivalent(result.get(0)));
		result = comparer.getUniqueDocumentRelationship(1, 0);
		assertEquals(1, result.size());
		assertTrue(RELATIONSHIP3.equivalent(result.get(0)));
	}
	
	public void testSpdxDocumentContentsEquals() throws InvalidSPDXAnalysisException, SpdxCompareException {
		SpdxItem[] itemsA = new SpdxItem[] {pkgA1};
		SpdxItem[] itemsB = new SpdxItem[] {pkgB1};
		SpdxDocument docA = new SpdxDocument(DOC_URIA);
		SpdxDocument docB = new SpdxDocument(DOC_URIB);
		docA.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESA));
		docB.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESB));
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOA);
		for (int i = 0; i < itemsA.length; i++) {
			Relationship rel = docA.createRelationship(itemsA[i], 
					RelationshipType.DESCRIBES, "");
			docA.addRelationship(rel);
		}
		for (int i = 0; i < itemsB.length; i++) {
			Relationship rel = docB.createRelationship(itemsB[i], 
					RelationshipType.DESCRIBES, "");
			docB.addRelationship(rel);
		}

		assertTrue(pkgA1.equivalent(pkgB1));
		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		assertTrue(comparer.isDocumentContentsEquals());

		assertFalse(pkgA1.equivalent(pkgB2));
		SpdxDocument docC = new SpdxDocument(DOC_URIC);
		docC.setExtractedLicenseInfos(Arrays.asList(EXTRACTED_LICENSESB));
		docC.setName(DOC_NAMEA);
		docC.setCreationInfo(CREATION_INFOA);
		docC.addRelationship(docC.createRelationship(pkgB2, RelationshipType.DESCRIBES, ""));
		comparer = new SpdxComparer();
		comparer.compare(Arrays.asList(new SpdxDocument[] {docA, docB, docC}));
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isDocumentContentsEquals());
	}
	
	public void testCompareSnippets() throws InvalidSPDXAnalysisException, SpdxCompareException {

		IModelStore newModelStore = new InMemSpdxStore();
		String uri1 = "http://uri1";
		String uri2 = "http://uri2";
		String uri3 = "http://uri3";
		SpdxDocument docA = new SpdxDocument(newModelStore, uri1, copyManager, true);
		SpdxDocument docB = new SpdxDocument(newModelStore, uri2, copyManager, true);
		SpdxDocument docC = new SpdxDocument(newModelStore, uri3, copyManager, true);
		docA.setName(DOC_NAMEA);
		docB.setName(DOC_NAMEB);
		docC.setName(DOC_NAMEC);
		UnitTestHelper.copyObjectsToDoc(docA, Arrays.asList(EXTRACTED_LICENSESA));
		UnitTestHelper.copyObjectsToDoc(docB, Arrays.asList(EXTRACTED_LICENSESA));
		UnitTestHelper.copyObjectsToDoc(docC, Arrays.asList(EXTRACTED_LICENSESA));
		docA.setCreationInfo(CREATION_INFOA);
		docB.setCreationInfo(CREATION_INFOA);
		docC.setCreationInfo(CREATION_INFOA);
		
		AnyLicenseInfo concludedLicense = LicenseInfoFactory.getListedLicenseByIdCompatV2("Apache-2.0");
		SpdxFile fromFileA = docA.createSpdxFile(newModelStore.getNextId(IdType.SpdxId), "fileName", 
				concludedLicense, Arrays.asList(new AnyLicenseInfo[] {concludedLicense}), "fileCopyright", CHECKSUM1).build();
		SpdxSnippet snippetA = docA.createSpdxSnippet(newModelStore.getNextId(IdType.SpdxId),
				"name", concludedLicense, Arrays.asList(new AnyLicenseInfo[] {concludedLicense}), "Copyright", fromFileA, 0, 10).build();
		SpdxFile fromFileB = docB.createSpdxFile(newModelStore.getNextId(IdType.SpdxId), "fileName", 
				concludedLicense, Arrays.asList(new AnyLicenseInfo[] {concludedLicense}), "fileCopyright", CHECKSUM1).build();
		SpdxSnippet snippetB = docB.createSpdxSnippet(newModelStore.getNextId(IdType.SpdxId),
				"name", concludedLicense, Arrays.asList(new AnyLicenseInfo[] {concludedLicense}), "Copyright", fromFileB, 0, 10).build();
		SpdxFile fromFileC = docC.createSpdxFile(newModelStore.getNextId(IdType.SpdxId), "fileName", 
				concludedLicense, Arrays.asList(new AnyLicenseInfo[] {concludedLicense}), "fileCopyright", CHECKSUM1).build();
		SpdxSnippet snippetC = docC.createSpdxSnippet(newModelStore.getNextId(IdType.SpdxId),
				"Different Name", concludedLicense, Arrays.asList(new AnyLicenseInfo[] {concludedLicense}), "Copyright", fromFileC, 0, 10).build();
		
		assertTrue(snippetA.equivalent(snippetB));
		assertFalse(snippetA.equivalent(snippetC));
		SpdxComparer comparer = new SpdxComparer();
		comparer.compare(docA, docB);
		assertFalse(comparer.isDifferenceFound());
		assertTrue(comparer.isSnippetsEqual());
		comparer.compare(Arrays.asList(new SpdxDocument[] {docA, docB, docC}));
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isSnippetsEqual());
	}
}
