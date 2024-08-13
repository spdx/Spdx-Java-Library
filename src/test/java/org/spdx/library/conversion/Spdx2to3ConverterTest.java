/**
 * Copyright (c) 2024 Source Auditor Inc.
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
package org.spdx.library.conversion;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v2.SpdxConstantsCompatV2;
import org.spdx.library.model.v2.SpdxCreatorInformation;
import org.spdx.library.model.v2.enumerations.AnnotationType;
import org.spdx.library.model.v3.ModelObjectV3;
import org.spdx.library.model.v3.SpdxConstantsV3;
import org.spdx.library.model.v3.SpdxModelClassFactory;
import org.spdx.library.model.v3.core.Agent;
import org.spdx.library.model.v3.core.Annotation;
import org.spdx.library.model.v3.core.CreationInfo;
import org.spdx.library.model.v3.core.Element;
import org.spdx.library.model.v3.core.ExternalIdentifier;
import org.spdx.library.model.v3.core.ExternalIdentifierType;
import org.spdx.library.model.v3.core.Hash;
import org.spdx.library.model.v3.core.IntegrityMethod;
import org.spdx.library.model.v3.core.NamespaceMap;
import org.spdx.library.model.v3.core.Organization;
import org.spdx.library.model.v3.core.Person;
import org.spdx.library.model.v3.core.Relationship;
import org.spdx.library.model.v3.core.RelationshipType;
import org.spdx.library.model.v3.core.SpdxDocument;
import org.spdx.library.model.v3.core.Tool;
import org.spdx.library.model.v3.expandedlicensing.CustomLicense;
import org.spdx.library.model.v3.simplelicensing.AnyLicenseInfo;
import org.spdx.library.model.v3.software.SpdxFile;
import org.spdx.library.model.v3.software.SpdxPackage;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * @author Gary O'Neall
 *
 */
public class Spdx2to3ConverterTest {
	
	static final String DOCUMENT_URI = "https://my.document.uri";
	static final String DEFAULT_PREFIX = "https://default.prefix/";
	static final String DEFAULT_CREATOR_NAME = "Gary O'Neall";
	
	IModelStore fromModelStore;
	IModelStore toModelStore;
	IModelCopyManager copyManager;
	CreationInfo defaultCreationInfo;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SpdxModelFactory.init();
		fromModelStore = new InMemSpdxStore();
		toModelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
		defaultCreationInfo = SpdxModelClassFactory.createCreationInfo(toModelStore, DEFAULT_PREFIX + "createdBy", DEFAULT_CREATOR_NAME, copyManager);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertCreationInfo(org.spdx.library.model.v2.SpdxCreatorInformation, org.spdx.storage.IModelStore, java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertCreationInfo() throws InvalidSPDXAnalysisException {
		String creatorId = fromModelStore.getNextId(IdType.Anonymous);
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		String toolCreatorName = "Test Tool";
		String toolCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_TOOL + toolCreatorName;
		String organizationCreatorName = "Source Auditor Inc.";
		String organizationCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_ORGANIZATION + organizationCreatorName;
		String created = "2010-01-29T18:30:22Z";
		String licenseListVersion = "3.21";
		SpdxCreatorInformation creatorInfo = new SpdxCreatorInformation(fromModelStore, DOCUMENT_URI, creatorId, copyManager, true);
		creatorInfo.getCreators().add(personCreator);
		creatorInfo.getCreators().add(toolCreator);
		creatorInfo.getCreators().add(organizationCreator);
		creatorInfo.setCreated(created);
		creatorInfo.setLicenseListVersion(licenseListVersion);
		List<String> verify = creatorInfo.verify();
		assertTrue(verify.isEmpty());
		
		CreationInfo result = Spdx2to3Converter.convertCreationInfo(creatorInfo, toModelStore, DEFAULT_PREFIX);
		assertEquals(2, result.getCreatedBys().size());
		Person creatorPerson = null;
		Organization creatorOrganization= null;
		for (Agent creator:result.getCreatedBys()) {
			if (creator instanceof Person) {
				creatorPerson = (Person)creator;
			} else if (creator instanceof Organization) {
				creatorOrganization = (Organization)creator;
			} else {
				fail("Unknown type for creator agent "+creator);
			}
		}
		assertTrue(Objects.nonNull(creatorPerson));
		assertEquals(personCreatorName, creatorPerson.getName().get());
		ExternalIdentifier[] emails = creatorPerson.getExternalIdentifiers().toArray(new ExternalIdentifier[creatorPerson.getExternalIdentifiers().size()]);
		assertEquals(1, emails.length);
		assertEquals(personCreatorEmail, emails[0].getIdentifier());
		assertEquals(ExternalIdentifierType.EMAIL, emails[0].getExternalIdentifierType());
		assertTrue(Objects.nonNull(creatorOrganization));
		assertEquals(organizationCreatorName, creatorOrganization.getName().get());
		assertTrue(creatorOrganization.getExternalIdentifiers().isEmpty());
		Tool[] tools = result.getCreatedUsings().toArray(new Tool[result.getCreatedUsings().size()]);
		assertEquals(1, tools.length);
		assertEquals(toolCreatorName, tools[0].getName().get());
		assertEquals(created, result.getCreated());
		assertEquals(IdType.Anonymous, toModelStore.getIdType(result.getObjectUri()));
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#Spdx2to3Converter(org.spdx.storage.IModelStore, org.spdx.library.ModelCopyManager, org.spdx.library.model.v3.core.CreationInfo, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSpdx2to3Converter() {
		Spdx2to3Converter result = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX);
		assertFalse(result.alreadyCopied(DOCUMENT_URI));
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#alreadyCopied(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testAlreadyCopied() throws InvalidSPDXAnalysisException {
		String licenseId = fromModelStore.getNextId(IdType.LicenseRef);
		org.spdx.library.model.v2.license.ExtractedLicenseInfo licV2 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, licenseId,
						copyManager, true);
		licV2.setExtractedText("Extracted Text");
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX);
		assertFalse(converter.alreadyCopied(DOCUMENT_URI + "#" + licenseId));
		converter.convertAndStore(licV2);
		assertTrue(converter.alreadyCopied(DOCUMENT_URI + "#" + licenseId));
	}
	
	@Test
	public void testDifferentModelStore() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testExternalElement() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testExternalExtractedLicenseInfo() {
		fail("Not yet implemented");
	}
	
	@Test
	public void TestNoneElement() {
		fail("Not implemented");
	}
	
	@Test
	public void TestNoAssertionLicense() {
		fail("Not implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#getExistingObject(java.lang.String, java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testGetExistingObject() throws InvalidSPDXAnalysisException {
		String licenseId = fromModelStore.getNextId(IdType.LicenseRef);
		org.spdx.library.model.v2.license.ExtractedLicenseInfo licV2 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, licenseId,
						copyManager, true);
		licV2.setExtractedText("Extracted Text");
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX);
		assertFalse(converter.getExistingObject(DOCUMENT_URI + "#" + licenseId, SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE).isPresent());
		
		CustomLicense customLicense = converter.convertAndStore(licV2);
		Optional<ModelObjectV3> result = converter.getExistingObject(DOCUMENT_URI + "#" + licenseId, SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE);
		assertTrue(result.isPresent());
		assertTrue(result.get() instanceof CustomLicense);
		assertEquals(customLicense, result.get());
		assertTrue(customLicense.equivalent(result.get()));
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.SpdxDocument)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreSpdxDocument() throws InvalidSPDXAnalysisException {
		
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		
		String created = "2010-01-29T18:30:22Z";
		String dataLicenseStr = "CC0-1.0";
		String docComment = "Doc comment";
		
		org.spdx.library.model.v2.enumerations.AnnotationType annotationType = AnnotationType.REVIEW;
		String annotationComment = "Annotation comment";
		
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgConcludedLicenseStr = "Apache-2.0";
		String pkgLicenseDeclaredStr = "NOASSERTION";
		String pkgDownloadLoctation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		
		String externalDocumentId = SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM + "external";
		String externalDocumentUri = "https://external/document";
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		String externalDocChecksumValue = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3";
		
		String extractedLicComment = "comment";
		String extractedText = "Extracted text";
		String extractedLicName = "name";
		String extractedLicSeeAlso1 = "https://seealso1";
		
		String docName = "DocumentName";
		
		String fileName = "File name";
		String fileConcludedLicenseStr = "MIT";
		String fileSeenLicenseStr = "NONE";
		String fileCopyright = "Copyright my file";
		String fileChecksumValue = "c3cdc01f93cf99c15097c0cdf107a28466b83913";
		
		org.spdx.library.model.v2.enumerations.RelationshipType containsRelationshipType = 
				org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINS;
		String relationshipComment = "Relationship comment";
		
		org.spdx.library.model.v2.SpdxDocument doc = new org.spdx.library.model.v2.SpdxDocument(fromModelStore, DOCUMENT_URI, copyManager, true);
		doc.setCreationInfo(doc.createCreationInfo(Arrays.asList(new String[] {personCreator}), created));
		org.spdx.library.model.v2.license.AnyLicenseInfo dataLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(dataLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		doc.setDataLicense(dataLicense);
		doc.setComment(docComment);
		org.spdx.library.model.v2.Annotation annotation = doc.createAnnotation(personCreator, annotationType, 
				created, annotationComment);
		doc.addAnnotation(annotation);
		String pkgId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgLicenseDeclared = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgLicenseDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.SpdxPackage pkg = doc.createPackage(pkgId, pkgName, pkgConcludedLicense, pkgCopyright, pkgLicenseDeclared)
				.setFilesAnalyzed(false)
				.setDownloadLocation(pkgDownloadLoctation)
				.build();		
		doc.setDocumentDescribes(Arrays.asList(new org.spdx.library.model.v2.SpdxItem[] {pkg}));
		org.spdx.library.model.v2.Checksum externalDocChecksum = doc.createChecksum(sha1Algorithm, externalDocChecksumValue);
		org.spdx.library.model.v2.ExternalDocumentRef externalDocRef = doc.createExternalDocumentRef(externalDocumentId, externalDocumentUri, externalDocChecksum);
		doc.setExternalDocumentRefs(Arrays.asList(new org.spdx.library.model.v2.ExternalDocumentRef[] {externalDocRef}));
		org.spdx.library.model.v2.license.ExtractedLicenseInfo extractedLicense = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		extractedLicense.setComment(extractedLicComment);
		extractedLicense.setExtractedText(extractedText);
		extractedLicense.setName(extractedLicName);
		extractedLicense.getSeeAlso().add(extractedLicSeeAlso1);
		doc.setExtractedLicenseInfos(Arrays.asList(new org.spdx.library.model.v2.license.ExtractedLicenseInfo[] {extractedLicense}));
		doc.setName(docName);
		String fileId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileSeenLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileSeenLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.Checksum fileSha1 = doc.createChecksum(sha1Algorithm, fileChecksumValue);
		org.spdx.library.model.v2.SpdxFile spdxFile = doc.createSpdxFile(fileId, fileName, fileConcludedLicense, 
				Arrays.asList(new org.spdx.library.model.v2.license.AnyLicenseInfo[] {fileSeenLicense}), fileCopyright, fileSha1)
				.build();
		org.spdx.library.model.v2.Relationship containsRelationship = doc.createRelationship(spdxFile, containsRelationshipType, relationshipComment);
		doc.addRelationship(containsRelationship);
		List<String> verify = doc.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX);
		SpdxDocument result = converter.convertAndStore(doc);
		assertEquals(docComment, result.getComment().get());
		assertEquals(dataLicenseStr, result.getDataLicense().get().toString());
		
		List<Relationship> resultRelationships = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		Object[] resultAnnotations = SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_ANNOTATION, DEFAULT_PREFIX, DEFAULT_PREFIX).toArray();
		assertEquals(1, resultAnnotations.length);
		assertTrue(resultAnnotations[0] instanceof Annotation);
		Annotation resultAnnotation = (Annotation)resultAnnotations[0];
		assertEquals(annotationType.toString(), resultAnnotation.getAnnotationType().toString());
		assertEquals(annotationComment, resultAnnotation.getStatement().get());
		
		Element[] rootElements = result.getRootElements().toArray(new Element[result.getRootElements().size()]);
		assertEquals(1, rootElements.length);
		assertTrue(rootElements[0] instanceof SpdxPackage);
		SpdxPackage resultPkg = (SpdxPackage)rootElements[0];
		assertEquals(pkgName, resultPkg.getName().get());
		assertEquals(pkgDownloadLoctation, resultPkg.getDownloadLocation().get());
		assertEquals(pkgCopyright, resultPkg.getCopyrightText().get());
		List<Relationship> pkgConcludedLicRelationships = findRelationship(resultRelationships, RelationshipType.HAS_CONCLUDED_LICENSE, resultPkg.getObjectUri(), null);
		assertEquals(1, pkgConcludedLicRelationships.size());
		AnyLicenseInfo[] pkgConcludedLicResults = pkgConcludedLicRelationships.get(0).getTos().toArray(new AnyLicenseInfo[pkgConcludedLicRelationships.get(0).getTos().size()]);
		assertEquals(1, pkgConcludedLicResults.length);
		assertEquals(pkgConcludedLicenseStr, pkgConcludedLicResults[0].toString());
		List<Relationship> pkgDeclaredLicRelationships = findRelationship(resultRelationships, RelationshipType.HAS_DECLARED_LICENSE, resultPkg.getObjectUri(), null);
		assertEquals(1, pkgDeclaredLicRelationships.size());
		AnyLicenseInfo[] pkgDeclaredLicResults = pkgDeclaredLicRelationships.get(0).getTos().toArray(new AnyLicenseInfo[pkgDeclaredLicRelationships.get(0).getTos().size()]);
		assertEquals(1, pkgDeclaredLicResults.length);
		assertEquals(pkgLicenseDeclaredStr, pkgDeclaredLicResults[0].toString());
		
		NamespaceMap[] namespaceMaps = result.getNamespaceMaps().toArray(new NamespaceMap[result.getNamespaceMaps().size()]);
		assertEquals(1, namespaceMaps.length);
		assertEquals(externalDocumentUri, namespaceMaps[0].getNamespace());
		assertEquals(externalDocumentId, namespaceMaps[0].getPrefix());
		
		List<CustomLicense> customLicenses = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.EXPANDED_LICENSING_CUSTOM_LICENSE, DEFAULT_PREFIX, DEFAULT_PREFIX)
							.forEach(cli -> customLicenses.add((CustomLicense)cli));
		assertEquals(1, customLicenses.size());
		assertEquals(extractedLicComment, customLicenses.get(0).getComment().get());
		assertEquals(extractedText, customLicenses.get(0).getLicenseText());
		assertEquals(extractedLicName, customLicenses.get(0).getName().get());
		String[] seeAlsos = customLicenses.get(0).getSeeAlsos().toArray(new String[customLicenses.get(0).getSeeAlsos().size()]);
		assertEquals(1, seeAlsos.length);
		assertEquals(extractedLicSeeAlso1, seeAlsos[0]);
		
		assertEquals(docName, result.getName().get());
		
		List<Relationship> resultContains = findRelationship(resultRelationships, RelationshipType.CONTAINS, result.getObjectUri(), null);
		assertEquals(1, resultContains.size());
		assertEquals(relationshipComment, resultContains.get(0).getComment().get());
		Element[] containsRelTos = resultContains.get(0).getTos().toArray(new Element[resultContains.get(0).getTos().size()]);
		assertEquals(1, containsRelTos.length);
		assertTrue(containsRelTos[0] instanceof SpdxFile);
		SpdxFile containedFile = (SpdxFile)containsRelTos[0];
		
		assertEquals(fileName, containedFile.getName().get());
		List<Relationship> fileConcludedRel = findRelationship(resultRelationships, RelationshipType.HAS_CONCLUDED_LICENSE, containedFile.getObjectUri(), null);
		assertEquals(1, fileConcludedRel.size());
		AnyLicenseInfo[] fileConcludedLicResults = fileConcludedRel.get(0).getTos().toArray(new AnyLicenseInfo[fileConcludedRel.get(0).getTos().size()]);
		assertEquals(1, fileConcludedLicResults.length);
		assertEquals(fileConcludedLicenseStr, fileConcludedLicResults[0].toString());
		List<Relationship> fileSeenRel = findRelationship(resultRelationships, RelationshipType.HAS_DECLARED_LICENSE, containedFile.getObjectUri(), null);
		assertEquals(1, fileSeenRel.size());
		AnyLicenseInfo[] fileDeclaredLicResults = fileSeenRel.get(0).getTos().toArray(new AnyLicenseInfo[fileSeenRel.get(0).getTos().size()]);
		assertEquals(1, fileDeclaredLicResults.length);
		assertEquals(fileSeenLicenseStr, fileDeclaredLicResults[0].toString());
		assertEquals(fileCopyright, containedFile.getCopyrightText().get());
		IntegrityMethod[] fileIntegrityMethods = containedFile.getVerifiedUsings().toArray(new IntegrityMethod[containedFile.getVerifiedUsings().size()]);
		assertEquals(1, fileIntegrityMethods.length);
		assertTrue(fileIntegrityMethods[0] instanceof Hash);
		Hash containedFileHash = (Hash)fileIntegrityMethods[0];
		assertEquals(sha1Algorithm.toString(), containedFileHash.getAlgorithm().toString());
		assertEquals(fileChecksumValue, containedFileHash.getHashValue());
	}
	
	@Test
	public void testConvertAndStoreExternalDocRef() {
		fail("Unimplemented");
		/*
		 * ExternalMap[] externalMaps = result.getImportss().toArray(new ExternalMap[result.getImportss().size()]);
		assertEquals(1, externalMaps.length);
		externalMaps[0].getDefiningArtifact()
		externalMaps[0].getExternalSpdxId()
		externalMaps[0].getLocationHint()
		externalMaps[0].getVerifiedUsings()
		 */
	}
	
	List<Relationship> findRelationship(List<Relationship> relationships, @Nullable RelationshipType relationshipType,
			@Nullable String fromObjectUri, @Nullable String toObjectUri) throws InvalidSPDXAnalysisException {
		List<Relationship> result = new ArrayList<>();
		for (Relationship relationship:relationships) {
			RelationshipType relType = relationship.getRelationshipType();
			String fromObject = relationship.getFrom().getObjectUri();
			if ((Objects.nonNull(relationshipType) && Objects.equals(relType, relationshipType)) &&
					(Objects.nonNull(fromObjectUri) && Objects.equals(fromObject, fromObjectUri))) {
				if (Objects.isNull(toObjectUri)) {
					result.add(relationship);
				} else {
					for (Element toElement:relationship.getTos()) {
						if (Objects.equals(toElement.getObjectUri(), toObjectUri)) {
							result.add(relationship);
							break;
						}
					}
				}
			}
		}
		return result;
	}
	
	@Test
	public void testConvertAndStoreRelationship() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testConvertAndStoreAnnotation() {
		fail("Not yet implemented");
	}
	
	
	@Test
	public void testConvertAndStoreListedLicenseException() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testConvertAndStoreElement() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testConvertAndStoreHash() {
		fail("Not yet implemented");
	}


	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.ConjunctiveLicenseSet)}.
	 */
	@Test
	public void testConvertAndStoreConjunctiveLicenseSet() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.DisjunctiveLicenseSet)}.
	 */
	@Test
	public void testConvertAndStoreDisjunctiveLicenseSet() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.ExtractedLicenseInfo)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreExtractedLicenseInfo() throws InvalidSPDXAnalysisException {
		String extractedLicComment = "comment";
		String extractedText = "Extracted text";
		String extractedLicName = "name";
		String extractedLicSeeAlso1 = "https://seealso1";
		String extractedLicSeeAlso2 = "https://seealso2";
		org.spdx.library.model.v2.license.ExtractedLicenseInfo licV2 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		licV2.setComment(extractedLicComment);
		licV2.setExtractedText(extractedText);
		licV2.setName(extractedLicName);
		licV2.getSeeAlso().add(extractedLicSeeAlso1);
		licV2.getSeeAlso().add(extractedLicSeeAlso2);
		List<String> verify = licV2.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX);
		CustomLicense result = converter.convertAndStore(licV2);
		assertEquals(extractedLicComment, result.getComment().get());
		assertEquals(extractedText, result.getLicenseText());
		assertEquals(extractedLicName, result.getName().get());
		assertEquals(2, result.getSeeAlsos().size());
		assertTrue(result.getSeeAlsos().contains(extractedLicSeeAlso1));
		assertTrue(result.getSeeAlsos().contains(extractedLicSeeAlso2));
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.OrLaterOperator)}.
	 */
	@Test
	public void testConvertAndStoreOrLaterOperator() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.SpdxListedLicense)}.
	 */
	@Test
	public void testConvertAndStoreSpdxListedLicense() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.WithExceptionOperator)}.
	 */
	@Test
	public void testConvertAndStoreWithExceptionOperator() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.AnyLicenseInfo)}.
	 */
	@Test
	public void testConvertAndStoreAnyLicenseInfo() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.SpdxFile)}.
	 */
	@Test
	public void testConvertAndStoreSpdxFile() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.SpdxPackage)}.
	 */
	@Test
	public void testConvertAndStoreSpdxPackage() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#stringToAgent(java.lang.String, org.spdx.library.model.v3.core.CreationInfo)}.
	 */
	@Test
	public void testStringToAgent() {
		// Tested as part of the testConvertCreationInfo
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.SpdxSnippet)}.
	 */
	@Test
	public void testConvertAndStoreSpdxSnippet() {
		fail("Not yet implemented");
	}

	@Test
	public void testConvertAndStoreLicenseAddition() {
		fail("Not yet implemented");
	}

}
