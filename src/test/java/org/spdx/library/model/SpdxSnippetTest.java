/**
 * Copyright (c) 2020 Source Auditor Inc.
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.Version;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.ConjunctiveLicenseSet;
import org.spdx.library.model.license.DisjunctiveLicenseSet;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.SpdxListedLicense;
import org.spdx.library.model.pointer.ByteOffsetPointer;
import org.spdx.library.model.pointer.LineCharPointer;
import org.spdx.library.model.pointer.StartEndPointer;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SpdxSnippetTest extends TestCase {

	static final String[] NONSTD_IDS = new String[] {SpdxConstants.NON_STD_LICENSE_ID_PRENUM+"1",
			SpdxConstants.NON_STD_LICENSE_ID_PRENUM+"2", SpdxConstants.NON_STD_LICENSE_ID_PRENUM+"3",
			SpdxConstants.NON_STD_LICENSE_ID_PRENUM+"4"};
		static final String[] NONSTD_TEXTS = new String[] {"text1", "text2", "text3", "text4"};
		static final String[] STD_IDS = new String[] {"AFL-3.0", "CECILL-B", "EUPL-1.0"};
		static final String[] STD_TEXTS = new String[] {"std text1", "std text2", "std text3"};
		
		static DateFormat DATEFORMAT = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		static String DATE_NOW = DATEFORMAT.format(new Date());
		Annotation ANNOTATION1;
		Annotation ANNOTATION2;
		Annotation ANNOTATION3;
		
		ExtractedLicenseInfo[] NON_STD_LICENSES;
		SpdxListedLicense[] STANDARD_LICENSES;
		DisjunctiveLicenseSet[] DISJUNCTIVE_LICENSES;
		ConjunctiveLicenseSet[] CONJUNCTIVE_LICENSES;
		
		ConjunctiveLicenseSet COMPLEX_LICENSE;
		
		SpdxFile FROM_FILE1;
		SpdxFile FROM_FILE2;
		
		int OFFSET1_1 = 2342;
		ByteOffsetPointer BOP_POINTER1_1;
		int LINE1_1 = 113;
		LineCharPointer LCP_POINTER1_1; 
		int OFFSET2_1 = 444;
		ByteOffsetPointer BOP_POINTER2_1;
		int LINE2_1 = 23422;
		LineCharPointer LCP_POINTER2_1; 
		int OFFSET1_2 = 3542;
		ByteOffsetPointer BOP_POINTER1_2;
		int LINE1_2 = 555;
		LineCharPointer LCP_POINTER1_2; 
		int OFFSET2_2 = 2444;
		ByteOffsetPointer BOP_POINTER2_2;
		int LINE2_2 = 23428;
		LineCharPointer LCP_POINTER2_2; 
		StartEndPointer BYTE_RANGE1;
		StartEndPointer BYTE_RANGE2;
		StartEndPointer LINE_RANGE1;
		StartEndPointer LINE_RANGE2;
		GenericModelObject gmo;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
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
			STANDARD_LICENSES[i] = new SpdxListedLicense("Name "+String.valueOf(i), 
					STD_IDS[i], STD_TEXTS[i], Arrays.asList(new String[] {"URL "+String.valueOf(i)}),
					"Notes "+String.valueOf(i), 
					"LicHeader "+String.valueOf(i), "Template "+String.valueOf(i), true,
					false, null, false, null);
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
		
		FROM_FILE1 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"fromFile1", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE, 
				gmo.createChecksum(ChecksumAlgorithm.SHA1, "1123456789abcdef0123456789abcdef01234567")).build();

		FROM_FILE2 = gmo.createSpdxFile(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()),
				"fromFile2", STANDARD_LICENSES[0], Arrays.asList(STANDARD_LICENSES), SpdxConstants.NOASSERTION_VALUE, 
				gmo.createChecksum(ChecksumAlgorithm.SHA1, "5555556789abcdef0123456789abcdef01234567")).build();
		
		BOP_POINTER1_1 = gmo.createByteOffsetPointer(FROM_FILE1, OFFSET1_1);
		BOP_POINTER1_2 =  gmo.createByteOffsetPointer(FROM_FILE1, OFFSET1_2);
		BYTE_RANGE1 =  gmo.createStartEndPointer(BOP_POINTER1_1, BOP_POINTER1_2);
		LCP_POINTER1_1 =  gmo.createLineCharPointer(FROM_FILE1, LINE1_1);
		LCP_POINTER1_2 =  gmo.createLineCharPointer(FROM_FILE1, LINE1_2);
		LINE_RANGE1 =  gmo.createStartEndPointer(LCP_POINTER1_1, LCP_POINTER1_2);
		BOP_POINTER2_1 =  gmo.createByteOffsetPointer(FROM_FILE2, OFFSET2_1);
		BOP_POINTER2_2 =  gmo.createByteOffsetPointer(FROM_FILE2, OFFSET2_2);
		BYTE_RANGE2 =  gmo.createStartEndPointer(BOP_POINTER2_1, BOP_POINTER2_2);
		LCP_POINTER2_1 =  gmo.createLineCharPointer(FROM_FILE2, LINE2_1);
		LCP_POINTER2_2 =  gmo.createLineCharPointer(FROM_FILE2, LINE2_2);
		LINE_RANGE2 =  gmo.createStartEndPointer(LCP_POINTER2_1, LCP_POINTER2_2);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxSnippet#verify()}.
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		SpdxSnippet snippet = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		
		List<String> result = snippet.verify();
		assertEquals(0, result.size());
		// missing file
		SpdxSnippet snippet2 = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		snippet2.setStrict(false);
		BYTE_RANGE1.setStrict(false);
		LINE_RANGE1.setStrict(false);
		snippet2.setSnippetFromFile(null);

		result = snippet2.verify();
		assertTrue(result.size() > 0);
		// missing byte range
		SpdxSnippet snippet3 = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		snippet3.setStrict(false);
		snippet3.setPropertyValue(SpdxConstants.PROP_SNIPPET_RANGE, null);
		result = snippet3.verify();
		assertEquals(1, result.size());
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		SpdxSnippet snippet = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		SpdxSnippet snippet2 = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertTrue(snippet.equivalent(snippet2));
		assertTrue(snippet2.equivalent(snippet));
		// Different File
		SpdxSnippet snippet3 = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE2, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertFalse(snippet3.equivalent(snippet));
		assertFalse(snippet.equivalent(snippet3));
		// different byte range
		SpdxSnippet snippet4  = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET2_1, OFFSET2_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertFalse(snippet4.equivalent(snippet));
		assertFalse(snippet.equivalent(snippet4));
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxSnippet#setSnippetFromFile(org.spdx.library.model.SpdxFile)}.
	 */
	public void testSetSnippetFromFile() throws InvalidSPDXAnalysisException {
		SpdxSnippet snippet = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertTrue(FROM_FILE1.equivalent(snippet.getSnippetFromFile()));
		SpdxSnippet snCopy = new SpdxSnippet(snippet.getModelStore(), snippet.getDocumentUri(), snippet.getId(), snippet.getCopyManager(), false);
		assertTrue(FROM_FILE1.equivalent(snCopy.getSnippetFromFile()));
		assertTrue(FROM_FILE1.equivalent(snippet.getSnippetFromFile()));
		snCopy.setSnippetFromFile(FROM_FILE2);
		assertTrue(FROM_FILE2.equivalent(snCopy.getSnippetFromFile()));
		assertTrue(FROM_FILE2.equivalent(snippet.getSnippetFromFile()));
		// setting the from file should also set the reference file in the pointers
		assertTrue(FROM_FILE2.equivalent(snCopy.getByteRange().getStartPointer().getReference()));
		assertTrue(FROM_FILE2.equivalent(snCopy.getByteRange().getEndPointer().getReference()));
		assertTrue(FROM_FILE2.equivalent(snCopy.getLineRange().get().getStartPointer().getReference()));
		assertTrue(FROM_FILE2.equivalent(snCopy.getLineRange().get().getEndPointer().getReference()));
		assertTrue(FROM_FILE2.equivalent(snippet.getByteRange().getStartPointer().getReference()));
		assertTrue(FROM_FILE2.equivalent(snippet.getByteRange().getEndPointer().getReference()));
		assertTrue(FROM_FILE2.equivalent(snippet.getLineRange().get().getStartPointer().getReference()));
		assertTrue(FROM_FILE2.equivalent(snippet.getLineRange().get().getEndPointer().getReference()));
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxSnippet#setByteRange(org.spdx.library.model.pointer.StartEndPointer)}.
	 */
	public void testSetByteRange() throws InvalidSPDXAnalysisException {
		SpdxSnippet snippet = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertEquals(OFFSET1_1, ((ByteOffsetPointer)(snippet.getByteRange().getStartPointer())).getOffset());
		assertEquals(OFFSET1_2, ((ByteOffsetPointer)(snippet.getByteRange().getEndPointer())).getOffset());
		SpdxSnippet snCopy = new SpdxSnippet(snippet.getModelStore(), snippet.getDocumentUri(), snippet.getId(), snippet.getCopyManager(), false);
		assertEquals(OFFSET1_1, ((ByteOffsetPointer)(snCopy.getByteRange().getStartPointer())).getOffset());
		assertEquals(OFFSET1_2, ((ByteOffsetPointer)(snCopy.getByteRange().getEndPointer())).getOffset());
		snippet.setByteRange(OFFSET2_1, OFFSET2_2);
		assertEquals(OFFSET2_1, ((ByteOffsetPointer)(snippet.getByteRange().getStartPointer())).getOffset());
		assertEquals(OFFSET2_2, ((ByteOffsetPointer)(snippet.getByteRange().getEndPointer())).getOffset());
		assertEquals(OFFSET2_1, ((ByteOffsetPointer)(snCopy.getByteRange().getStartPointer())).getOffset());
		assertEquals(OFFSET2_2, ((ByteOffsetPointer)(snCopy.getByteRange().getEndPointer())).getOffset());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxSnippet#setLineRange(org.spdx.library.model.pointer.StartEndPointer)}.
	 */
	public void testSetLineRange() throws InvalidSPDXAnalysisException {
		SpdxSnippet snippet = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertEquals(LINE1_1, ((LineCharPointer)snippet.getLineRange().get().getStartPointer()).getLineNumber());
		assertEquals(LINE1_2, ((LineCharPointer)snippet.getLineRange().get().getEndPointer()).getLineNumber());
		SpdxSnippet snCopy = new SpdxSnippet(snippet.getModelStore(), snippet.getDocumentUri(), snippet.getId(), snippet.getCopyManager(), false);
		assertEquals(LINE1_1, ((LineCharPointer)snCopy.getLineRange().get().getStartPointer()).getLineNumber());
		assertEquals(LINE1_2, ((LineCharPointer)snCopy.getLineRange().get().getEndPointer()).getLineNumber());
		snippet.setLineRange(LINE2_1, LINE2_2);
		assertEquals(LINE2_1, ((LineCharPointer)snippet.getLineRange().get().getStartPointer()).getLineNumber());
		assertEquals(LINE2_2, ((LineCharPointer)snippet.getLineRange().get().getEndPointer()).getLineNumber());
		assertEquals(LINE2_1, ((LineCharPointer)snCopy.getLineRange().get().getStartPointer()).getLineNumber());
		assertEquals(LINE2_2, ((LineCharPointer)snCopy.getLineRange().get().getEndPointer()).getLineNumber());
	}

	/**
	 * Test method for {@link org.spdx.library.model.SpdxSnippet#compareTo(org.spdx.library.model.SpdxSnippet)}.
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		SpdxSnippet snippet = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		// same
		SpdxSnippet snippet2 = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertEquals(0, snippet.compareTo(snippet2));
		// different filename
		SpdxSnippet snippet3 = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"AsnippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertTrue(snippet.compareTo(snippet3) > 0);
		// different from file
		SpdxSnippet snippet4 = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE2, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertTrue(snippet.compareTo(snippet4) < 0);
		// different byterange
		SpdxSnippet snippet5 = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", COMPLEX_LICENSE, Arrays.asList(NON_STD_LICENSES), SpdxConstants.NOASSERTION_VALUE,
				FROM_FILE1, OFFSET2_1, OFFSET2_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();
		assertTrue(snippet.compareTo(snippet5) > 0);
	}

	// Test to verify spec versions prior to 2.3 fail verify for missing license or copyright fields
	public void testVerify23Fields() throws InvalidSPDXAnalysisException {
		SpdxSnippet snippet = gmo.createSpdxSnippet(gmo.getModelStore().getNextId(IdType.SpdxId, gmo.getDocumentUri()), 
				"snippetName", null, Arrays.asList(new AnyLicenseInfo[] {}), null,
				FROM_FILE1, OFFSET1_1, OFFSET1_2)
				.setLineRange(LINE1_1, LINE1_2)
				.build();

		assertEquals(0, snippet.verify().size());
		assertTrue(snippet.verify(Version.TWO_POINT_ZERO_VERSION).size() > 0);
	}
}
