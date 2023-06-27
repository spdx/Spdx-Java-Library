/**
 * Copyright (c) 2013 Source Auditor Inc.
 * Copyright (c) 2013 Black Duck Software Inc.
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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.compat.v2.Checksum;
import org.spdx.library.model.compat.v2.SpdxDocument;
import org.spdx.library.model.compat.v2.SpdxFile;
import org.spdx.library.model.compat.v2.license.AnyLicenseInfo;
import org.spdx.library.model.compat.v2.license.InvalidLicenseStringException;
import org.spdx.library.model.compat.v2.license.License;
import org.spdx.library.model.compat.v2.license.LicenseInfoFactory;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.FileType;

import junit.framework.TestCase;


/**
 * @author Gary O'Neall
 *
 */
public class SpdxFileComparerTest extends TestCase {

	static final String TEST_RDF_FILE_PATH = "TestFiles"+File.separator+"SPDXRdfExample.rdf";
	private static final String STD_LIC_ID_CC0 = "CC-BY-1.0";
	private static final String STD_LIC_ID_MPL11 = "MPL-1.1";
	Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> LICENSE_XLATION = new HashMap<>();
	File testRDFFile;
	SpdxDocument DOCA;
	SpdxDocument DOCB;
	
	/**
	 * @throws java.lang.Exception
	 */
	public void setUp() throws Exception {
		 DefaultModelStore.reset(SpdxMajorVersion.VERSION_2);
		this.testRDFFile = new File(TEST_RDF_FILE_PATH); 
		String uri1 = "http://doc/uri1";
		DOCA = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), uri1, DefaultModelStore.getDefaultCopyManager(), true);
		String uri2 = "http://doc/uri2";
		DOCB = new SpdxDocument(DefaultModelStore.getDefaultModelStore(), uri2, DefaultModelStore.getDefaultCopyManager(), true);
		Map<SpdxDocument, Map<String, String>> bmap = new HashMap<>();
		bmap.put(DOCB, new HashMap<>());
		LICENSE_XLATION.put(DOCA, bmap);
		Map<SpdxDocument, Map<String, String>> amap = new HashMap<>();
		amap.put(DOCA, new HashMap<>());
		LICENSE_XLATION.put(DOCB, amap);
	}
	/**
	 * @throws java.lang.Exception
	 */
	public void tearDown() throws Exception {
		super.tearDown();
		 DefaultModelStore.reset(SpdxMajorVersion.VERSION_3);
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#SpdxFileComparer()}.
	 * @throws InvalidLicenseStringException 
	 * @throws SpdxCompareException 
	 */
	public void testSpdxFileComparer() throws InvalidLicenseStringException, SpdxCompareException {
		try {
            new SpdxFileComparer(LICENSE_XLATION);
		} catch(Exception ex) {
		    fail("Exception creating new file comparer "+ex.getMessage());
		}
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#compare(org.spdx.rdfparser.SpdxFile, org.spdx.rdfparser.SpdxFile, java.util.HashMap)}.
	 * @throws SpdxCompareException 
	 * @throws InvalidLicenseStringException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testCompare() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isConcludedLicenseEquals()}.
	 * @throws SpdxCompareException 
	 * @throws InvalidLicenseStringException 
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsConcludedLicenseEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11);
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isConcludedLicenseEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isConcludedLicenseEquals());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isSeenLicenseEquals()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsSeenLicenseEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11),
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0)}));
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isSeenLicenseEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isSeenLicensesEquals());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#getUniqueSeenLicensesB()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetUniqueSeenLicensesB() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11),
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0)}));
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isSeenLicenseEquals());
		List<AnyLicenseInfo> unique = fc.getUniqueSeenLicenses(DOCB, DOCA);
		assertEquals(1, unique.size());
		assertEquals(STD_LIC_ID_CC0, ((License)unique.get(0)).getLicenseId());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isSeenLicensesEquals());
		List<AnyLicenseInfo> diffUnique = diff.getUniqueSeenLicensesB();
		assertEquals(1, diffUnique.size());
		assertEquals(STD_LIC_ID_CC0, ((License)diffUnique.get(0)).getLicenseId());

		fileA.getLicenseInfoFromFiles().clear();
		fileA.getLicenseInfoFromFiles().addAll(seenLicenseB);
		fileB.getLicenseInfoFromFiles().clear();
		fileB.getLicenseInfoFromFiles().addAll(seenLicenseA);
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isSeenLicenseEquals());
		unique = fc.getUniqueSeenLicenses(DOCB, DOCA);
		assertEquals(0, unique.size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#getUniqueSeenLicensesA()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetUniqueSeenLicensesA() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11),
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0)}));
		Collection<AnyLicenseInfo> seenLicenseB = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isSeenLicenseEquals());
		List<AnyLicenseInfo> unique = fc.getUniqueSeenLicenses(DOCA, DOCB);
		assertEquals(1, unique.size());
		assertEquals(STD_LIC_ID_CC0, ((License)unique.get(0)).getLicenseId());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isSeenLicensesEquals());
		List<AnyLicenseInfo> diffUnique = diff.getUniqueSeenLicensesA();
		assertEquals(1, diffUnique.size());
		assertEquals(STD_LIC_ID_CC0, ((License)diffUnique.get(0)).getLicenseId());
		
		fileA.getLicenseInfoFromFiles().clear();
		fileA.getLicenseInfoFromFiles().addAll(seenLicenseB);
		fileB.getLicenseInfoFromFiles().clear();
		fileB.getLicenseInfoFromFiles().addAll(seenLicenseA);
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isSeenLicenseEquals());
		unique = fc.getUniqueSeenLicenses(DOCA, DOCB);
		assertEquals(0, unique.size());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isCommentsEquals()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsLicenseCommentsEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = "B license comments";
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isLicenseCommmentsEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isLicenseCommentsEqual());
		
		fileB.setLicenseComments(licenseCommentsA);
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
		assertTrue(fc.isLicenseCommmentsEquals());		
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isCopyrightsEquals()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsCopyrightsEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = "B Copyright";
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isCopyrightsEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isCopyrightsEqual());
		
		fileB.setCopyrightText(copyrightA);
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
		assertTrue(fc.isCopyrightsEquals());	
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isLicenseCommmentsEquals()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsCommmentsEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = "file B comment";
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isCommentsEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isCommentsEquals());

		fileB.setComment(fileCommentA);
	    fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
		assertTrue(fc.isCommentsEquals());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isChecksumsEquals()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsChecksumsEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = "cccbf72bf99b7e471f1a27989667a903658652bb";
		String sha1C = "dddbf72bf99b7e471f1a27989667a903658652bb";
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		String fileANotice = "noticeA";
		String fileBNotice = fileANotice;
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.setNoticeText(fileANotice)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.setNoticeText(fileBNotice)
				.build();
		
		fileA.getChecksums().add(fileA.createChecksum(ChecksumAlgorithm.SHA1, sha1B));
		fileB.getChecksums().add(fileB.createChecksum(ChecksumAlgorithm.SHA1, sha1C));
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isChecksumsEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isChecksumsEquals());
		List<Checksum> result = diff.getUniqueChecksumsA();
		assertEquals(1, result.size());
		assertEquals(sha1A, result.get(0).getValue());
		result = diff.getUniqueChecksumsB();
		assertEquals(1, result.size());
		assertEquals(sha1C, result.get(0).getValue());

		fileA.getChecksums().clear();
		fileA.getChecksums().add(fileA.createChecksum(ChecksumAlgorithm.SHA1, sha1C));
		fileA.getChecksums().add(fileA.createChecksum(ChecksumAlgorithm.SHA1, sha1B));
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
		assertTrue(fc.isChecksumsEquals());
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isTypesEquals()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsTypesEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE, FileType.BINARY}));
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isTypesEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isTypeEqual());
		fileA.getFileTypes().clear();
		fileA.getFileTypes().addAll(fileTypeB);
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
		assertTrue(fc.isTypesEquals());
	}
	
	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isContributorsEquals()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsContributorsEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		Collection<String> fileAContributors = new HashSet<>(Arrays.asList(new String[] {"ContributorA", "ContributorB"}));
		String fileANotice = "File A Notice";
		Collection<String> fileBContributors = new HashSet<>(Arrays.asList(new String[] {"Different", "Contributors", "Entirely"}));
		String fileBNotice = fileANotice;
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.setFileContributors(fileAContributors)
				.setNoticeText(fileANotice)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.setFileContributors(fileBContributors)
				.setNoticeText(fileBNotice)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isContributorsEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isContributorsEqual());
		fileA.getFileContributors().clear();
		fileA.getFileContributors().addAll(fileBContributors);
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
		assertTrue(fc.isContributorsEquals());
	}
	
	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isNoticeTextEqual()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsNoticeTextEquals() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		String fileANotice = "File A Notice";
		String fileBNotice = "File B Notice";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.setNoticeText(fileANotice)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.setNoticeText(fileBNotice)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		assertFalse(fc.isNoticeTextEquals());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isNoticeTextsEqual());
		fileA.setNoticeText(fileBNotice);
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
		assertTrue(fc.isNoticeTextEquals());
	}
	
	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#isDifferenceFound()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testIsDifferenceFound() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[] {FileType.SOURCE}));
		Collection<FileType> fileTypeB = fileTypeA;
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertFalse(fc.isDifferenceFound());
		fileA.setComment("Different");
		fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		// Note - all of the other fields are tested in the individual test cases
	}

	/**
	 * Test method for {@link org.spdx.compare.SpdxFileComparer#getFileDifference()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testGetFileDifference() throws SpdxCompareException, InvalidLicenseStringException, InvalidSPDXAnalysisException {
		String fileNameA = "a/b/c/name.txt";
		String fileNameB = fileNameA;
		Collection<FileType> fileTypeA = new HashSet<>(Arrays.asList(new FileType[]{FileType.SOURCE}));
		Collection<FileType> fileTypeB = new HashSet<>(Arrays.asList(new FileType[]{FileType.BINARY}));
		String sha1A = "027bf72bf99b7e471f1a27989667a903658652bb";
		String sha1B = sha1A;
		AnyLicenseInfo concludedLicenseA = LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_CC0);
		AnyLicenseInfo concludedLicenseB = concludedLicenseA;
		Collection<AnyLicenseInfo> seenLicenseA = new HashSet<>(Arrays.asList(new AnyLicenseInfo[] {
				LicenseInfoFactory.parseSPDXLicenseString(STD_LIC_ID_MPL11)
				}));
		Collection<AnyLicenseInfo> seenLicenseB = seenLicenseA;
		String licenseCommentsA = "License Comments";
		String licenseCommentsB = licenseCommentsA;
		String copyrightA = "Copyright";
		String copyrightB = copyrightA;
		String fileCommentA = "file comment";
		String fileCommentB = fileCommentA;
		String idA = "SPDXRef-A";
		String idB = "SPDXRef-B";
		
		SpdxFile fileA = DOCA.createSpdxFile(idA, fileNameA, concludedLicenseA, seenLicenseA, copyrightA, 
				DOCA.createChecksum(ChecksumAlgorithm.SHA1, sha1A))
				.setFileTypes(fileTypeA)
				.setLicenseComments(licenseCommentsA)
				.setComment(fileCommentA)
				.build();
		
		SpdxFile fileB = DOCB.createSpdxFile(idB, fileNameB, concludedLicenseB, seenLicenseB, copyrightB, 
				DOCB.createChecksum(ChecksumAlgorithm.SHA1, sha1B))
				.setFileTypes(fileTypeB)
				.setLicenseComments(licenseCommentsB)
				.setComment(fileCommentB)
				.build();
		
		SpdxFileComparer fc = new SpdxFileComparer(LICENSE_XLATION);
		fc.addDocumentFile(DOCA, fileA);
		fc.addDocumentFile(DOCB, fileB);
		assertTrue(fc.isDifferenceFound());
		SpdxFileDifference diff = fc.getFileDifference(DOCA, DOCB);
		assertFalse(diff.isTypeEqual());
		//Note - each of the individual fields is tested in their respecive unit tests
	}

}
