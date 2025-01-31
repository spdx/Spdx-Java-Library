/**
 * SPDX-FileCopyrightText: Copyright (c) 2020 Source Auditor Inc.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.Annotation;
import org.spdx.library.model.v2.GenericSpdxElement;
import org.spdx.library.model.v2.GenericSpdxItem;
import org.spdx.library.model.v2.Relationship;
import org.spdx.library.model.v2.SpdxDocument;
import org.spdx.library.model.v2.SpdxElement;
import org.spdx.library.model.v2.SpdxItem;
import org.spdx.library.model.v2.enumerations.AnnotationType;
import org.spdx.library.model.v2.enumerations.RelationshipType;
import org.spdx.library.model.v2.license.AnyLicenseInfo;
import org.spdx.library.model.v2.license.ExtractedLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

/**
 * Test SPDX item comparer
 *
 * @author Gary O'Neall
 */
public class SpdxItemComparerTest extends TestCase {
	
	private static final String COMMENTA = "comment A";
	private static final String COMMENTB = "comment B";
	private static final String LICENSE_COMMENTA = "License Comment A";
	private static final String LICENSE_COMMENTB = "License Comment B";
	private static final String COPYRIGHTA = "Copyright A";
	private static final String COPYRIGHTB = "Copyright B";
	private static final String NAMEA = "NameA";
	private static final String ATT_TEXTA = "Attribution A";
	private static final String ATT_TEXTB = "Attribution B";
	private static final String[] ATTRIBUTION_TEXTA = new String[] {ATT_TEXTA};
	private static final String[] ATTRIBUTION_TEXTB = new String[] {ATT_TEXTB};
	
	private final Map<SpdxDocument, Map<SpdxDocument, Map<String, String>>> LICENSE_XLATION_MAP = new HashMap<>();

	private SpdxDocument DOCA;
	private SpdxDocument DOCB;
	
	private ExtractedLicenseInfo LICENSEA1;
	private ExtractedLicenseInfo LICENSEA2;
	private ExtractedLicenseInfo LICENSEA3;
	private ExtractedLicenseInfo LICENSEB1;
	private ExtractedLicenseInfo LICENSEB2;
	private ExtractedLicenseInfo LICENSEB3;
	private AnyLicenseInfo[] LICENSE_INFO_FROM_FILESA;
	private AnyLicenseInfo[] LICENSE_INFO_FROM_FILESB;
	private AnyLicenseInfo LICENSE_CONCLUDEDA;
	private AnyLicenseInfo LICENSE_CONCLUDEDB;
	
	private Annotation ANNOTATION1;
	private Annotation ANNOTATION2;
	private Annotation ANNOTATION3;
	private Annotation ANNOTATION4;
	private Annotation[] ANNOTATIONSA;
	@SuppressWarnings("unused")
	private Annotation[] ANNOTATIONSB;
	
	private Relationship[] RELATIONSHIPSA;
	@SuppressWarnings("unused")
	private Relationship[] RELATIONSHIPSB;
	private SpdxElement RELATED_ELEMENT1;
	private SpdxElement RELATED_ELEMENT2;
	private SpdxElement RELATED_ELEMENT3;
	private SpdxElement RELATED_ELEMENT4;
	private Relationship RELATIONSHIP1;
	private Relationship RELATIONSHIP2;
	private Relationship RELATIONSHIP3;
	private Relationship RELATIONSHIP4;
	
	IModelStore modelStoreA;
	IModelStore modelStoreB;
    IModelCopyManager copyManager;
    String DEFAULT_DOCUMENT_URI = "http://default/doc";
	

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		SpdxModelFactory.init();
		modelStoreA = new InMemSpdxStore();
		modelStoreB = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		String uri1 = "http://doc/uri1";
		DOCA = new SpdxDocument(modelStoreA, uri1, copyManager, true);
		String uri2 = "http://doc/uri2";
		DOCB = new SpdxDocument(modelStoreB, uri2, copyManager, true);
		LICENSEA1 = new ExtractedLicenseInfo(DOCA.getModelStore(), DOCA.getDocumentUri(), "LicenseRef-1", copyManager, true);
		LICENSEA1.setExtractedText("License1");
		LICENSEA2 = new ExtractedLicenseInfo(DOCA.getModelStore(), DOCA.getDocumentUri(), "LicenseRef-2", copyManager, true);
		LICENSEA1.setExtractedText("License2");
		LICENSEA3 = new ExtractedLicenseInfo(DOCA.getModelStore(), DOCA.getDocumentUri(), "LicenseRef-3", copyManager, true);
		LICENSEA1.setExtractedText("License3");
		LICENSEB1 = new ExtractedLicenseInfo(DOCB.getModelStore(), DOCB.getDocumentUri(), "LicenseRef-4", copyManager, true);
		LICENSEA1.setExtractedText("License1");
		LICENSEB2 = new ExtractedLicenseInfo(DOCB.getModelStore(), DOCB.getDocumentUri(), "LicenseRef-5", copyManager, true);
		LICENSEA1.setExtractedText("License2");
		LICENSEB3 = new ExtractedLicenseInfo(DOCB.getModelStore(), DOCB.getDocumentUri(), "LicenseRef-6", copyManager, true);
		LICENSEA1.setExtractedText("License3");
		LICENSE_INFO_FROM_FILESA = new AnyLicenseInfo[] {LICENSEA1, LICENSEA2, LICENSEA3};
		LICENSE_INFO_FROM_FILESB = new AnyLicenseInfo[] {LICENSEB1, LICENSEB2, LICENSEB3};

		LICENSE_CONCLUDEDA = LICENSEA1;
		LICENSE_CONCLUDEDB = LICENSEB1;
		ANNOTATION1 = DOCA.createAnnotation("Person:Annotator1", AnnotationType.OTHER, 
				"2010-01-29T18:30:22Z", "AnnotationComment1");
		ANNOTATION2 = DOCA.createAnnotation("Person:Annotator2", AnnotationType.REVIEW, 
				"2011-01-29T18:30:22Z", "AnnotationComment2");
		ANNOTATION3 = DOCA.createAnnotation("Person:Annotator3", AnnotationType.OTHER, 
				"2012-01-29T18:30:22Z", "AnnotationComment3");
		ANNOTATION4 = DOCA.createAnnotation("Person:Annotator4", AnnotationType.REVIEW, 
				"2013-01-29T18:30:22Z", "AnnotationComment4");
		ANNOTATIONSA = new Annotation[] {ANNOTATION1, ANNOTATION2};
		ANNOTATIONSB = new Annotation[] {ANNOTATION3, ANNOTATION4};
		RELATED_ELEMENT1 = new GenericSpdxElement(DOCA.getModelStore(), DOCA.getDocumentUri(), 
				DOCA.getModelStore().getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENT1.setName("relatedElementName1");
		RELATED_ELEMENT1.setComment("related element comment 1");
		RELATED_ELEMENT2 = new GenericSpdxElement(DOCA.getModelStore(), DOCA.getDocumentUri(), 
				DOCA.getModelStore().getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENT2.setName("relatedElementName2");
		RELATED_ELEMENT2.setComment("related element comment 2");
		RELATED_ELEMENT3 = new GenericSpdxElement(DOCB.getModelStore(), DOCB.getDocumentUri(), 
				DOCB.getModelStore().getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENT3.setName("relatedElementName3");
		RELATED_ELEMENT3.setComment("related element comment 3");
		RELATED_ELEMENT4 = new GenericSpdxElement(DOCB.getModelStore(), DOCB.getDocumentUri(), 
				DOCB.getModelStore().getNextId(IdType.SpdxId), copyManager, true);
		RELATED_ELEMENT4.setName("relatedElementName4");
		RELATED_ELEMENT4.setComment("related element comment 4");
		RELATIONSHIP1 = DOCA.createRelationship(RELATED_ELEMENT1, 
				RelationshipType.CONTAINS, "Relationship Comment1");
		RELATIONSHIP2 = DOCA.createRelationship(RELATED_ELEMENT2, 
				RelationshipType.DYNAMIC_LINK, "Relationship Comment2");
		RELATIONSHIP3 = DOCB.createRelationship(RELATED_ELEMENT3, 
				RelationshipType.DATA_FILE_OF, "Relationship Comment3");
		RELATIONSHIP4 = DOCB.createRelationship(RELATED_ELEMENT4, 
				RelationshipType.DISTRIBUTION_ARTIFACT, "Relationship Comment4");
		RELATIONSHIPSA = new Relationship[] {RELATIONSHIP1, RELATIONSHIP2};
		RELATIONSHIPSB = new Relationship[] {RELATIONSHIP3, RELATIONSHIP4};
		
		Map<SpdxDocument, Map<String, String>> bmap = new HashMap<>();
		Map<String, String> mapAB = new HashMap<>();
		mapAB.put(DOCA.getDocumentUri() + "#" + "LicenseRef-1", DOCB.getDocumentUri() + "#" + "LicenseRef-4");
		mapAB.put(DOCA.getDocumentUri() + "#" + "LicenseRef-2", DOCB.getDocumentUri() + "#" + "LicenseRef-5");
		mapAB.put(DOCA.getDocumentUri() + "#" + "LicenseRef-3", DOCB.getDocumentUri() + "#" + "LicenseRef-6");
		bmap.put(DOCB, mapAB);
		LICENSE_XLATION_MAP.put(DOCA, bmap);
		Map<SpdxDocument, Map<String, String>> amap = new HashMap<>();
		Map<String, String> mapBA = new HashMap<>();
		mapBA.put(DOCB.getDocumentUri() + "#" + "LicenseRef-4", DOCA.getDocumentUri() + "#" + "LicenseRef-1");
		mapBA.put(DOCB.getDocumentUri() + "#" + "LicenseRef-5", DOCA.getDocumentUri() + "#" + "LicenseRef-2");
		mapBA.put(DOCB.getDocumentUri() + "#" + "LicenseRef-6", DOCA.getDocumentUri() + "#" + "LicenseRef-3");
		amap.put(DOCA, mapBA);
		LICENSE_XLATION_MAP.put(DOCB, amap);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCompare() throws SpdxCompareException, InvalidSPDXAnalysisException {
		
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA, 
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertFalse(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
		assertTrue(comparer.isAttributionTextEquals());
	}

	private SpdxItem createGenericItem(SpdxDocument doc, String name, String comment, 
			Annotation[] annotations, Relationship[] relationships, AnyLicenseInfo licenseConcluded,
			AnyLicenseInfo[] licenseInfosFromFiles, String copyright, String licenseComments,
			String[] attributionText) throws InvalidSPDXAnalysisException {
		
		GenericSpdxItem retval = new GenericSpdxItem(doc.getModelStore(), doc.getDocumentUri(),
				doc.getModelStore().getNextId(IdType.Anonymous), doc.getCopyManager(), true);
		retval.setName(name);
		retval.setComment(comment);
		for (Annotation annotation:annotations) {
			retval.getAnnotations().add(annotation);
		}
		for (Relationship relationship:relationships) {
			retval.getRelationships().add(relationship);
		}
		retval.setLicenseConcluded(licenseConcluded);
		for (AnyLicenseInfo licFromFile:licenseInfosFromFiles) {
			retval.getLicenseInfoFromFiles().add(licFromFile);
		}
		retval.setCopyrightText(copyright);
		retval.setLicenseComments(licenseComments);
		for (String att:attributionText) {
			retval.getAttributionText().add(att);
		}
		return retval;
	}

	public void testIsConcludedLicenseEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSEB2, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertFalse(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
	}

	public void testIsSeenLicenseEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, new AnyLicenseInfo[] {LICENSEB1}, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertFalse(comparer.isSeenLicenseEquals());
	}

	public void testGetUniqueSeenLicensesB() throws SpdxCompareException, InvalidSPDXAnalysisException {
		AnyLicenseInfo[] s1 = new AnyLicenseInfo[] {LICENSEA1};
		AnyLicenseInfo[] s2 = new AnyLicenseInfo[] {LICENSEB1, LICENSEB2};
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, s1, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, s2, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertFalse(comparer.isSeenLicenseEquals());
		List<AnyLicenseInfo> result = comparer.getUniqueSeenLicenses(DOCB, DOCA);
		assertEquals(1, result.size());
		assertTrue(LICENSEB2.equivalent(result.get(0)));
		
		itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertEquals(0, comparer.getUniqueSeenLicenses(DOCB, DOCA).size());

	}

	public void testGetUniqueSeenLicensesA() throws SpdxCompareException, InvalidSPDXAnalysisException {
		AnyLicenseInfo[] s1 = new AnyLicenseInfo[] {LICENSEA1, LICENSEA2};
		AnyLicenseInfo[] s2 = new AnyLicenseInfo[] {LICENSEB1};
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, s1, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB =createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, s2, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertFalse(comparer.isSeenLicenseEquals());
		List<AnyLicenseInfo> result = comparer.getUniqueSeenLicenses(DOCA, DOCB);
		assertEquals(1, result.size());
		assertTrue(LICENSEA2.equivalent(result.get(0)));
		
		itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertEquals(0, comparer.getUniqueSeenLicenses(DOCA, DOCB).size());
	}

	public void testIsCommentsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTB, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertFalse(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
	}

	public void testIsCopyrightsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTB, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertFalse(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
	}
	
	public void testIsAttributionTextEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTB);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
		assertFalse(comparer.isAttributionTextEquals());
	}

	public void testIsLicenseCommentsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTB,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertFalse(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
	}

	public void testGetItem() throws SpdxCompareException, InvalidSPDXAnalysisException {
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTB, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertEquals(itemA, comparer.getItem(DOCA));
		assertEquals(itemB, comparer.getItem(DOCB));
	}

	public void testIsRelationshipsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Relationship[] r1 = new Relationship[] {RELATIONSHIP1, RELATIONSHIP2};
		Relationship[] r2 = new Relationship[] {RELATIONSHIP2, RELATIONSHIP3};
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, r1,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, r2,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertFalse(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
	}

	public void testGetUniqueRelationshipA() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Relationship[] r1 = new Relationship[] {RELATIONSHIP1, RELATIONSHIP2};
		Relationship[] r2 = new Relationship[] {RELATIONSHIP2, RELATIONSHIP3};
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, r1,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, r2,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertFalse(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
		List<Relationship> result = comparer.getUniqueRelationship(DOCA, DOCB);
		assertEquals(1, result.size());
		assertTrue(RELATIONSHIP1.equivalent(result.get(0)));
		itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertEquals(0, comparer.getUniqueRelationship(DOCA, DOCB).size());
	}


	public void testGetUniqueRelationshipB() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Relationship[] r1 = new Relationship[] {RELATIONSHIP1, RELATIONSHIP2};
		Relationship[] r2 = new Relationship[] {RELATIONSHIP2, RELATIONSHIP3};
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, r1,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, r2,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);;
		assertTrue(comparer.isDifferenceFound());
		assertTrue(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertFalse(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
		List<Relationship> result = comparer.getUniqueRelationship(DOCB, DOCA);
		assertEquals(1, result.size());
		assertTrue(RELATIONSHIP3.equivalent(result.get(0)));
		itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertEquals(0, comparer.getUniqueRelationship(DOCB, DOCA).size());
	}


	public void testIsAnnotationsEquals() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Annotation[] a1 = new Annotation[] {ANNOTATION1, ANNOTATION2};
		Annotation[] a2 = new Annotation[] {ANNOTATION2, ANNOTATION3};
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, a1, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, a2, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
	}

	public void testGetUniqueAnnotationsA() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Annotation[] a1 = new Annotation[] {ANNOTATION1, ANNOTATION2};
		Annotation[] a2 = new Annotation[] {ANNOTATION2, ANNOTATION3};
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, a1, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, a2, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
		List<Annotation> result = comparer.getUniqueAnnotations(DOCA, DOCB);
		assertEquals(1, result.size());
		assertTrue(ANNOTATION1.equivalent(result.get(0)));
		itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertEquals(0, comparer.getUniqueAnnotations(DOCA, DOCB).size());
	}


	public void testGetUniqueAnnotationsB() throws SpdxCompareException, InvalidSPDXAnalysisException {
		Annotation[] a1 = new Annotation[] {ANNOTATION1, ANNOTATION2};
		Annotation[] a2 = new Annotation[] {ANNOTATION2, ANNOTATION3};
		SpdxItem itemA = createGenericItem(DOCA, NAMEA, COMMENTA, a1, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItem itemB = createGenericItem(DOCB, NAMEA, COMMENTA, a2, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		SpdxItemComparer comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertTrue(comparer.isDifferenceFound());
		assertFalse(comparer.isAnnotationsEquals());
		assertTrue(comparer.isCommentsEquals());
		assertTrue(comparer.isConcludedLicenseEquals());
		assertTrue(comparer.isCopyrightsEquals());
		assertTrue(comparer.isLicenseCommentsEquals());
		assertTrue(comparer.isRelationshipsEquals());
		assertTrue(comparer.isSeenLicenseEquals());
		List<Annotation> result = comparer.getUniqueAnnotations(DOCB, DOCA);
		assertEquals(1, result.size());
		assertTrue(ANNOTATION3.equivalent(result.get(0)));
		itemA = createGenericItem(DOCA, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDA, LICENSE_INFO_FROM_FILESA, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		itemB = createGenericItem(DOCB, NAMEA, COMMENTA, ANNOTATIONSA, RELATIONSHIPSA,
				LICENSE_CONCLUDEDB, LICENSE_INFO_FROM_FILESB, COPYRIGHTA, LICENSE_COMMENTA,
				ATTRIBUTION_TEXTA);
		comparer = new SpdxItemComparer(LICENSE_XLATION_MAP);
		comparer.addDocumentItem(DOCA, itemA);
		comparer.addDocumentItem(DOCB, itemB);
		assertEquals(0, comparer.getUniqueAnnotations(DOCB, DOCA).size());
	}
}
