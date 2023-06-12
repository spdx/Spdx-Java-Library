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
package org.spdx.library.model.compat.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.Version;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.compat.v2.enumerations.AnnotationType;
import org.spdx.library.model.compat.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.compat.v2.enumerations.FileType;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.library.model.compat.v2.license.AnyLicenseInfo;
import org.spdx.library.model.compat.v2.license.ExtractedLicenseInfo;
import org.spdx.library.model.compat.v2.license.LicenseInfoFactory;
import org.spdx.library.model.compat.v2.license.SimpleLicensingInfo;
import org.spdx.library.model.compat.v2.license.SpdxListedLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.compat.v2.CompatibleModelStoreWrapper;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SpdxDocumentTest extends TestCase {
	
	private static final String ANNOTATOR1 = "Person: Annotator1";
	private static final String ANNOTATOR2 = "Person: Annotator2";
	private static final AnnotationType ANNOTATION_TYPE1 = AnnotationType.REVIEW;
	private static final AnnotationType ANNOTATION_TYPE2 = AnnotationType.OTHER;
	private static final String ANNOTATION_COMMENT1 = "Comment 1 for annotation";
	private static final String ANNOTATION_COMMENT2 = "Comment 2 for annotation";
	private static final String DATE1 = "2010-01-29T18:30:22Z";
	private static final String DATE2 = "2015-01-29T18:30:22Z";
	private static final String[] CREATORS1 = new String[] {"Tool: SPDX tool", "Person: the person"};
	private static final String[] CREATORS2 = new String[] {"Tool: Teesst"};
	private static final String CREATOR_COMMENT1 = "Creator comment1";
	private static final String CREATOR_COMMENT2 = "Creator comment2";
	private static final String LICENSE_LISTV1 = "1.18";
	private static final String LICENSE_LISTV2 = "1.22";
	static final String SHA1_VALUE1 = "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12";
	static final String SHA1_VALUE2 = "2222e1c67a2d28fced849ee1bb76e7391b93eb12";
	private static final String DOC_COMMENT1 = "Doc Comment1";
	private static final String REFERENCED_DOC_URI1 = "http://referenced.document/uri1";
	private static final String REFERENCED_DOC_URI2 = "http://referenced.document/uri2";
	ExtractedLicenseInfo LICENSE1;
	ExtractedLicenseInfo LICENSE2;
	ExtractedLicenseInfo LICENSE3;
	private static final String DOC_NAME1 = "DocName1";
	private static final String DOCID1 = "DocumentRef-1";
	private static final String DOCID2 = "DocumentRef-2";
	
	AnyLicenseInfo CCO_DATALICENSE;
	
	private Annotation ANNOTATION1;
	private Annotation ANNOTATION2;
	private SpdxCreatorInformation CREATIONINFO1;
	private SpdxCreatorInformation CREATIONINFO2;
	private Checksum CHECKSUM1;
	private Checksum CHECKSUM2;
	SpdxElement RELATED_ELEMENT1;
	SpdxElement RELATED_ELEMENT2;
	Relationship RELATIONSHIP1;
	Relationship RELATIONSHIP2;
	SpdxFile FILE1;
	SpdxFile FILE2;
	SpdxFile FILE3;
	SpdxPackage PACKAGE1;
	SpdxPackage PACKAGE2;
	SpdxPackage PACKAGE3;
	GenericModelObject gmo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset(SpdxMajorVersion.VERSION_2);
		gmo = new GenericModelObject();
		CCO_DATALICENSE = LicenseInfoFactory.getListedLicenseById("CC0-1.0");
		LICENSE1 = new ExtractedLicenseInfo("LicenseRef-1", "License Text 1");
		LICENSE2 = new ExtractedLicenseInfo("LicenseRef-2", "License Text 2");
		LICENSE3 = new ExtractedLicenseInfo("LicenseRef-3", "License Text 3");
		ANNOTATION1 = gmo.createAnnotation(ANNOTATOR1, ANNOTATION_TYPE1, DATE1, ANNOTATION_COMMENT1);
		ANNOTATION2 = gmo.createAnnotation(ANNOTATOR2, ANNOTATION_TYPE2, DATE2, ANNOTATION_COMMENT2);
		CREATIONINFO1 = gmo.createCreationInfo(Arrays.asList(CREATORS1), DATE1)
				.setComment(CREATOR_COMMENT1)
				.setLicenseListVersion(LICENSE_LISTV1);
		CREATIONINFO2 = gmo.createCreationInfo(Arrays.asList(CREATORS2), DATE2)
				.setComment(CREATOR_COMMENT2)
				.setLicenseListVersion(LICENSE_LISTV2);
		CHECKSUM1 = gmo.createChecksum(ChecksumAlgorithm.SHA1, SHA1_VALUE1);
		CHECKSUM2 = gmo.createChecksum(ChecksumAlgorithm.SHA1, SHA1_VALUE2);
		RELATED_ELEMENT1 = new GenericSpdxElement();
		RELATED_ELEMENT1.setName("relatedElementName1");
		RELATED_ELEMENT2 = new GenericSpdxElement();
		RELATED_ELEMENT2.setName("relatedElementName2");
		RELATIONSHIP1 = gmo.createRelationship(RELATED_ELEMENT1, 
				RelationshipType.CONTAINS, "Relationship Comment1");
		RELATIONSHIP2 = gmo.createRelationship(RELATED_ELEMENT2, 
				RelationshipType.DYNAMIC_LINK, "Relationship Comment2");
		FILE1 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"FileName1", LICENSE1, Arrays.asList(new ExtractedLicenseInfo[] {LICENSE2}), 
				"File Copyright1", CHECKSUM1)
				.setComment("FileComment 1")
				.setLicenseComments("License Comment1")
				.setFileTypes(Arrays.asList(new FileType[] {FileType.ARCHIVE}))
				.setFileContributors(Arrays.asList(new String[] {"File Contrib1"}))
				.setNoticeText("File Notice1")
				.build();

		FILE2 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"FileName2", LICENSE2, Arrays.asList(new ExtractedLicenseInfo[] {LICENSE3}), 
				"File Copyright2", CHECKSUM2)
				.setComment("FileComment 2")
				.setLicenseComments("License Comment2")
				.setFileTypes(Arrays.asList(new FileType[] {FileType.SOURCE}))
				.setFileContributors(Arrays.asList(new String[] {"File Contrib2"}))
				.setNoticeText("File Notice2")
				.build();

		FILE3 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"FileName3", LICENSE3, Arrays.asList(new ExtractedLicenseInfo[] {LICENSE1}), 
				"File Copyright2", CHECKSUM1)
				.setComment("FileComment 3")
				.setLicenseComments("License Comment3")
				.setFileTypes(Arrays.asList(new FileType[] {FileType.TEXT}))
				.setFileContributors(Arrays.asList(new String[] {"File Contrib3"}))
				.setNoticeText("File Notice3")
				.build();

		PACKAGE1 = gmo.createPackage(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"Package 1", LICENSE1, "Pkg Copyright1", LICENSE2)
				.addChecksum(CHECKSUM1)
				.setLicenseInfosFromFile(Arrays.asList(new SimpleLicensingInfo[] {LICENSE2}))
				.setComment("Package Comments1")
				.setDescription("Pkg Description 1")
				.setDownloadLocation("hg+https://hg.myproject.org/MyProject#src/somefile.c")
				.setLicenseComments("Pkg License Comment 1")
				.setFiles(Arrays.asList( new SpdxFile[] {FILE1}))
				.setHomepage("http://home.page/one")
				.setOriginator("Person: originator1")
				.setPackageFileName("packagename1")
				.setPackageVerificationCode(gmo.createPackageVerificationCode("0000e1c67a2d28fced849ee1bb76e7391b93eb12",
						Arrays.asList(new String[] {"excludedfile1", "excluedfiles2"})))
				.setSourceInfo("sourceinfo1")
				.setSummary("summary1")
				.setSupplier("Person: supplier1")
				.setVersionInfo("version1")
				.build();

		PACKAGE2 = gmo.createPackage(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"Package 2", LICENSE2, "Pkg Copyright2", LICENSE3)
				.addChecksum(CHECKSUM2)
				.setLicenseInfosFromFile(Arrays.asList(new SimpleLicensingInfo[] {LICENSE2}))
				.setComment("Package Comments2")
				.setDescription("Pkg Description 2")
				.setDownloadLocation("hg+https://hg.myproject.org/MyProject#src/someotherfile.c")
				.setLicenseComments("Pkg License Comment 2")
				.setFiles(Arrays.asList(new SpdxFile[] {FILE2, FILE3}))
				.setHomepage("http://home.page/two")
				.setOriginator("Person: originator2")
				.setPackageFileName("packagename2")
				.setPackageVerificationCode(gmo.createPackageVerificationCode("2222e1c67a2d28fced849ee1bb76e7391b93eb12",
						Arrays.asList(new String[] {"excludedfile3", "excluedfiles4"})))
				.setSourceInfo("sourceinfo2")
				.setSummary("summary2")
				.setSupplier("Person: supplier2")
				.setVersionInfo("version2")
				.build();

		PACKAGE3 = gmo.createPackage(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"Package 3", LICENSE1, "Pkg Copyright3", LICENSE3)
				.addChecksum(CHECKSUM1)
				.setLicenseInfosFromFile(Arrays.asList(new SimpleLicensingInfo[] {LICENSE2}))
				.setComment("Package Comments3")
				.setDescription("Pkg Description 3")
				.setDownloadLocation("hg+https://hg.myotherproject.org/MyProject#src/someotherfile.c")
				.setLicenseComments("Pkg License Comment 3")
				.setFiles(Arrays.asList(new SpdxFile[] {FILE3}))
				.setHomepage("http://home.page/three")
				.setOriginator("Person: originator3")
				.setPackageFileName("packagename3")
				.setPackageVerificationCode(gmo.createPackageVerificationCode("3333e1c67a2d28fced849ee1bb76e7391b93eb12",
						Arrays.asList(new String[] {"excludedfile4", "excluedfiles5"})))
				.setSourceInfo("sourceinfo3")
				.setSummary("summary3")
				.setSupplier("Person: supplier3")
				.setVersionInfo("version3")
				.build();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
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
	 * Per 1309, if no creation info is available in the model, we'll assign a creation date, as one is mandatory,
	 * and the License List Version (because we know what version we have).
	 */
	public void testDefaultCreationInfo() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = SpdxModelFactory.createSpdxDocumentV2(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getCopyManager());
		assertNotNull(doc.getCreationInfo());
		assertTrue("Mandatory creation date missing from new SPDX Document.", !doc.getCreationInfo().getCreated().isEmpty());
		Optional<String> licenseListVersion = doc.getCreationInfo().getLicenseListVersion();
		assertTrue(licenseListVersion.isPresent() && StringUtils.isNotBlank(licenseListVersion.get()));
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = SpdxModelFactory.createSpdxDocumentV2(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getCopyManager());
		doc.setStrict(false);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});

		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				PACKAGE1, FILE1, PACKAGE2, FILE2
			});
		doc.getAnnotations().addAll(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);

		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		List<Relationship> relationships = new ArrayList<>();
		relationships.add(RELATIONSHIP1);
		relationships.add(RELATIONSHIP2);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		assertTrue(collectionsSame(annotations, doc.getAnnotations()));
		assertEquals(DOC_COMMENT1, doc.getComment().get());
		assertEquals(CREATIONINFO1, doc.getCreationInfo());
		assertEquals(CCO_DATALICENSE, doc.getDataLicense());
		assertTrue(collectionsSame(externalDocumentRefs, doc.getExternalDocumentRefs()));
		assertTrue(collectionsSame(extractedLicenseInfos, doc.getExtractedLicenseInfos()));
		assertEquals(DOC_NAME1, doc.getName().get());
		// assertTrue(collectionsSame(relationships, doc.getRelationships())); - gets messed up by adding the document describes
		assertTrue(collectionsSame(items, doc.getDocumentDescribes()));
		
		assertTrue(doc.equivalent(doc));
		
		String doc2Uri = "http://spdx.org/spdx/2ndoc/2342";
		IModelStore model2 = new InMemSpdxStore();
		SpdxDocument doc2 = SpdxModelFactory.createSpdxDocumentV2(model2, doc2Uri, gmo.getCopyManager());
		doc2.setStrict(false);
		doc2.setAnnotations(annotations);
		doc2.setComment(DOC_COMMENT1);
		doc2.setCreationInfo(CREATIONINFO1);
		doc2.setDataLicense(CCO_DATALICENSE);
		doc2.setExtractedLicenseInfos(extractedLicenseInfos);
		doc2.setExternalDocumentRefs(externalDocumentRefs);
		doc2.setName(DOC_NAME1);
		doc2.setRelationships(relationships);
		doc2.setDocumentDescribes(items);
		assertTrue(doc.equivalent(doc2));
		// CreationInfo
		doc2.setCreationInfo(CREATIONINFO2);
		assertFalse(doc.equivalent(doc2));
		doc2.setCreationInfo(CREATIONINFO1);
		assertTrue(doc.equivalent(doc2));
		// DataLicense
		doc2.setDataLicense(LicenseInfoFactory.getListedLicenseById("APAFML"));
		assertFalse(doc.equivalent(doc2));
		doc2.setDataLicense(CCO_DATALICENSE);
		assertTrue(doc.equivalent(doc2));
		// ExternalDocumentRefs
		doc2.setExternalDocumentRefs(Arrays.asList(new ExternalDocumentRef[] {externalDocRef1}));
		assertFalse(doc.equivalent(doc2));
		doc2.setExternalDocumentRefs(externalDocumentRefs);
		assertTrue(doc.equivalent(doc2));
		// ExtracteLicenseInfos
		doc2.setExtractedLicenseInfos(Arrays.asList(new ExtractedLicenseInfo[] {LICENSE2}));
		assertFalse(doc.equivalent(doc2));
		doc2.setExtractedLicenseInfos(extractedLicenseInfos);
		assertTrue(doc.equivalent(doc2));
		// Items
		doc2.addRelationship(doc2.createRelationship(FILE3, 
				RelationshipType.DESCRIBES, ""));
		doc2.addRelationship(doc2.createRelationship(PACKAGE3, 
				RelationshipType.DESCRIBES, ""));
		assertFalse(doc.equivalent(doc2));
	}
	
	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxDocument#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), gmo.getCopyManager(), true);
		doc.setStrict(false);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});
		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2, PACKAGE1, PACKAGE2
			});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {
					RELATIONSHIP1, RELATIONSHIP2
			});
		doc.setAnnotations(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);
		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		doc.setSpecVersion(Version.CURRENT_SPDX_VERSION);
		List<String> result = doc.verify();
		assertEquals(0, result.size());
		// data license
		doc.setDataLicense(LicenseInfoFactory.getListedLicenseById("AFL-3.0"));
		result = doc.verify();
		assertEquals(1, result.size());
		// Name
		doc.setName(null);
		result = doc.verify();
		assertEquals(2, result.size());
		// SpecVersion
		doc.setSpecVersion(null);
		result = doc.verify();
		assertEquals(3, result.size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxDocument#getDocumentDescribes()}.
	 */
	public void testGetDocumentDescribes() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), gmo.getCopyManager(), true);
		doc.setStrict(false);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});
		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2
			});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {
					RELATIONSHIP1, RELATIONSHIP2
			});
		doc.setAnnotations(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);
		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		doc.setSpecVersion(Version.CURRENT_SPDX_VERSION);
		
		assertTrue(collectionsSame(items, doc.getDocumentDescribes()));
		List<SpdxItem> expected = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2, PACKAGE1, PACKAGE2
			});
		Relationship describes = doc.createRelationship(PACKAGE1, RelationshipType.DESCRIBES, "added relationship");
		doc.addRelationship(describes);
		doc.getDocumentDescribes().add(PACKAGE2);
		assertTrue(collectionsSame(expected, doc.getDocumentDescribes()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxDocument#setCreationInfo(org.spdx.library.model.compat.v2.compat.v2.SpdxCreatorInformation)}.
	 */
	public void testSetCreationInfo() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), gmo.getCopyManager(), true);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});
		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2, PACKAGE1, PACKAGE2
			});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {
					RELATIONSHIP1, RELATIONSHIP2
			});
		doc.setAnnotations(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);
		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		assertEquals(CREATIONINFO1, doc.getCreationInfo());
		doc.setCreationInfo(CREATIONINFO2);
		assertEquals(CREATIONINFO2, doc.getCreationInfo());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxDocument#setDataLicense(org.spdx.library.model.compat.v2.compat.v2.license.AnyLicenseInfo)}.
	 */
	public void testSetDataLicense() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), gmo.getCopyManager(), true);
		doc.setStrict(false);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});
		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2, PACKAGE1, PACKAGE2
			});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {
					RELATIONSHIP1, RELATIONSHIP2
			});
		doc.setAnnotations(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);
		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		assertEquals(CCO_DATALICENSE, doc.getDataLicense());
		SpdxListedLicense lic = LicenseInfoFactory.getListedLicenseById("Apache-2.0");
		doc.setDataLicense(lic);
		assertEquals(lic, doc.getDataLicense());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxDocument#getExternalDocumentRefs()}.
	 */
	public void testGetExternalDocumentRefs() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), gmo.getCopyManager(), true);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});
		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2, PACKAGE1, PACKAGE2
			});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {
					RELATIONSHIP1, RELATIONSHIP2
			});
		doc.setAnnotations(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);
		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		assertTrue(collectionsSame(externalDocumentRefs, doc.getExternalDocumentRefs()));
		Collection<ExternalDocumentRef> ref2 = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef2
		});
		doc.setExternalDocumentRefs(ref2);
		assertTrue(collectionsSame(ref2, doc.getExternalDocumentRefs()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxDocument#getExtractedLicenseInfos()}.
	 */
	public void testGetExtractedLicenseInfos() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), gmo.getCopyManager(), true);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});
		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2, PACKAGE1, PACKAGE2
			});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {
					RELATIONSHIP1, RELATIONSHIP2
			});
		doc.setAnnotations(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);
		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		assertTrue(collectionsSame(extractedLicenseInfos, doc.getExtractedLicenseInfos()));
		List<ExtractedLicenseInfo> infos2 = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE2, LICENSE3
		});
		doc.setExtractedLicenseInfos(infos2);
		assertTrue(collectionsSame(infos2, doc.getExtractedLicenseInfos()));
	}
	
	public void testAddExtractedLicenseInfos() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), gmo.getCopyManager(), true);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});
		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2, PACKAGE1, PACKAGE2
			});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {
					RELATIONSHIP1, RELATIONSHIP2
			});
		doc.setAnnotations(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);
		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		assertTrue(collectionsSame(extractedLicenseInfos, doc.getExtractedLicenseInfos()));

		doc.addExtractedLicenseInfos(LICENSE2);
		assertEquals(2, doc.getExtractedLicenseInfos().size());
		doc.addExtractedLicenseInfos(LICENSE3);
		assertEquals(3, doc.getExtractedLicenseInfos().size());
		List<ExtractedLicenseInfo> expected = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2, LICENSE3
		});
		assertTrue(collectionsSame(expected, doc.getExtractedLicenseInfos()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.SpdxDocument#setSpecVersion(java.lang.String)}.
	 */
	public void testSetSpecVersion() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), gmo.getCopyManager(), true);
		List<Annotation> annotations = Arrays.asList(new Annotation[] {
				ANNOTATION1, ANNOTATION2	
			});
		ExternalDocumentRef externalDocRef1 = gmo.createExternalDocumentRef(DOCID1, REFERENCED_DOC_URI1, CHECKSUM1);
		ExternalDocumentRef externalDocRef2 = gmo.createExternalDocumentRef(DOCID2, REFERENCED_DOC_URI2, CHECKSUM2);
		List<ExternalDocumentRef> externalDocumentRefs = Arrays.asList(new ExternalDocumentRef[] {
				externalDocRef1, externalDocRef2
			});
		List<ExtractedLicenseInfo> extractedLicenseInfos = Arrays.asList(new ExtractedLicenseInfo[] {
				LICENSE1, LICENSE2
			});
		List<SpdxItem> items = Arrays.asList(new SpdxItem[] {
				FILE1, FILE2, PACKAGE1, PACKAGE2
			});
		List<Relationship> relationships = Arrays.asList(new Relationship[] {
					RELATIONSHIP1, RELATIONSHIP2
			});
		doc.setAnnotations(annotations);
		doc.setComment(DOC_COMMENT1);
		doc.setCreationInfo(CREATIONINFO1);
		doc.setDataLicense(CCO_DATALICENSE);
		doc.setExternalDocumentRefs(externalDocumentRefs);
		doc.setExtractedLicenseInfos(extractedLicenseInfos);
		doc.setName(DOC_NAME1);
		doc.setRelationships(relationships);
		doc.setDocumentDescribes(items);
		assertTrue(doc.getSpecVersion().isEmpty());
		String ver = "SPDX-2.1";
		doc.setSpecVersion(ver);
		assertEquals(ver, doc.getSpecVersion());
	}
	
	// Test for issue 126 - removing a documentDescribes not properly decrementing use counts
	public void testRemoveDescribes() throws InvalidSPDXAnalysisException {
		IModelStore modelStore = new InMemSpdxStore();
		String docUri = "https://some.doc.uri";
		ModelCopyManager copyManager = new ModelCopyManager();
		SpdxDocument doc = new SpdxDocument(modelStore, docUri, copyManager, true);
		String describedElementId = "describedElement";
		SpdxElement describedElement = new GenericSpdxElement(modelStore, docUri, describedElementId, copyManager, true);
		assertEquals(0, doc.getDocumentDescribes().size());
		assertEquals(0, doc.getRelationships().size());
		doc.getDocumentDescribes().add(describedElement);
		assertEquals(1, doc.getDocumentDescribes().size());
		assertEquals(1, doc.getRelationships().size());
		Relationship rel = doc.getRelationships().toArray(new Relationship[1])[0];
		assertEquals(describedElement, rel.getRelatedSpdxElement().get());
		doc.getDocumentDescribes().remove(describedElement);
		modelStore.delete(CompatibleModelStoreWrapper.documentUriIdToUri(docUri, describedElementId, false));
	}

}
