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

import java.util.Arrays;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;

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
	private ExternalDocumentRef EXTERNAL_REF1;
	private ExternalDocumentRef EXTERNAL_REF2;
	SpdxElement RELATED_ELEMENT1;
	SpdxElement RELATED_ELEMENT2;
	Relationship RELATIONSHIP1;
	Relationship RELATIONSHIP2;
//	SpdxFile FILE1;
//	SpdxFile FILE2;
//	SpdxFile FILE3;
	SpdxPackage PACKAGE1;
	SpdxPackage PACKAGE2;
	SpdxPackage PACKAGE3;
	GenericModelObject gmo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
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
		EXTERNAL_REF1 = gmo.createExternalDocumentRef(REFERENCED_DOC_URI1, DOCID1, CHECKSUM1);
		EXTERNAL_REF2 = gmo.createExternalDocumentRef(REFERENCED_DOC_URI2, DOCID2, CHECKSUM2);
		RELATED_ELEMENT1 = new GenericSpdxElement("relatedElementName1");
		RELATED_ELEMENT2 = new GenericSpdxElement("relatedElementName2");
//		RELATIONSHIP1 = new Relationship(RELATED_ELEMENT1, 
//				RelationshipType.CONTAINS, "Relationship Comment1");
//		RELATIONSHIP2 = new Relationship(RELATED_ELEMENT2, 
//				RelationshipType.DYNAMIC_LINK, "Relationship Comment2");
//		FILE1 = new SpdxFile("FileName1", "FileComment 1", 
//				null, null,LICENSE1, new ExtractedLicenseInfo[] {LICENSE2}, 
//				"File Copyright1", "License Comment1", new FileType[] {FileType.fileType_archive}, 
//				new Checksum[] {CHECKSUM1},
//				new String[] {"File Contrib1"}, "File Notice1", 
//				new DoapProject[] {new DoapProject("Project1", "http://project.home.page/one")});
//		FILE2 = new SpdxFile("FileName2", "FileComment 2", 
//				null, null,LICENSE2, new ExtractedLicenseInfo[] {LICENSE3}, 
//				"File Copyright2", "License Comment2", new FileType[] {FileType.fileType_source}, 
//				new Checksum[] {CHECKSUM2},
//				new String[] {"File Contrib2"}, "File Notice2", 
//				new DoapProject[] {new DoapProject("Project2", "http://project.home.page/two")});
//		FILE3 = new SpdxFile("FileName3", "FileComment 3", 
//				null, null,LICENSE3, new ExtractedLicenseInfo[] {LICENSE1}, 
//				"File Copyright3", "License Comment3", new FileType[] {FileType.fileType_text}, 
//				new Checksum[] {CHECKSUM1},
//				new String[] {"File Contrib3"}, "File Notice3", 
//				new DoapProject[] {new DoapProject("Project3", "http://project.home.page/three")});
//		PACKAGE1 = gmo.createSpdxPackage("Package 1", "Package Comments1", 
//				null, null,LICENSE1, Arrays.asList(new SimpleLicensingInfo[] {LICENSE2}), 
//				"Pkg Copyright1", "Pkg License Comment 1", LICENSE2, new Checksum[] {CHECKSUM1},
//				"Pkg Description 1", "Downlodlocation1", new SpdxFile[] {FILE1}, 
//				"http://home.page/one", "Person: originator1", "packagename1", 
//				new SpdxPackageVerificationCode("0000e1c67a2d28fced849ee1bb76e7391b93eb12", new String[] {"excludedfile1", "excluedfiles2"}),
//				"sourceinfo1", "summary1", "Person: supplier1", "version1");
//		PACKAGE1 = new SpdxPackage();
//		PACKAGE2 = new SpdxPackage("Package 2", "Package Comments2", 
//				null, null,LICENSE2, new SimpleLicensingInfo[] { LICENSE3}, 
//				"Pkg Copyright2", "Pkg License Comment 2", LICENSE3, new Checksum[] {CHECKSUM2},
//				"Pkg Description 2", "Downlodlocation2", new SpdxFile[] {FILE2, FILE3}, 
//				"http://home.page/two", "Person: originator2", "packagename2", 
//				new SpdxPackageVerificationCode("2222e1c67a2d28fced849ee1bb76e7391b93eb12", new String[] {"excludedfile3", "excluedfiles4"}),
//				"sourceinfo2", "summary2", "Person: supplier2", "version2");
//		PACKAGE3 = new SpdxPackage("Package 3", "Package Comments3", 
//				null, null,LICENSE1, new SimpleLicensingInfo[] { LICENSE2}, 
//				"Pkg Copyright3", "Pkg License Comment 3", LICENSE3, new Checksum[] {CHECKSUM1},
//				"Pkg Description 3", "Downlodlocation3", new SpdxFile[] {FILE3}, 
//				"http://home.page/three", "Person: originator3", "packagename3", 
//				new SpdxPackageVerificationCode("3333e1c67a2d28fced849ee1bb76e7391b93eb12", new String[] {"excludedfile4", "excluedfiles5"}),
//				"sourceinfo3", "summary3", "Person: supplier3", "version3");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxDocument#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SpdxDocument doc = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), DefaultModelStore.getDefaultDocumentUri(), true);
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxDocument#getDocumentDescribes()}.
	 */
	public void testGetDocumentDescribes() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxDocument#setCreationInfo(org.spdx.library.model.SpdxCreatorInformation)}.
	 */
	public void testSetCreationInfo() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxDocument#setDataLicense(org.spdx.library.model.license.AnyLicenseInfo)}.
	 */
	public void testSetDataLicense() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxDocument#getExternalDocumentRefs()}.
	 */
	public void testGetExternalDocumentRefs() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxDocument#getExtractedLicenseInfos()}.
	 */
	public void testGetExtractedLicenseInfos() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxDocument#setSpecVersion(java.lang.String)}.
	 */
	public void testSetSpecVersion() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

}
