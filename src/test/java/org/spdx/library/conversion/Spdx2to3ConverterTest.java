/**
 * SPDX-FileCopyrightText: Copyright (c) 2024 Source Auditor Inc.
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
package org.spdx.library.conversion;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.spdx.library.model.v2.license.SpdxListedLicense;
import org.spdx.library.model.v3_0_1.ModelObjectV3;
import org.spdx.library.model.v3_0_1.SpdxConstantsV3;
import org.spdx.library.model.v3_0_1.SpdxModelClassFactoryV3;
import org.spdx.library.model.v3_0_1.core.Agent;
import org.spdx.library.model.v3_0_1.core.Annotation;
import org.spdx.library.model.v3_0_1.core.CreationInfo;
import org.spdx.library.model.v3_0_1.core.DictionaryEntry;
import org.spdx.library.model.v3_0_1.core.Element;
import org.spdx.library.model.v3_0_1.core.ExternalElement;
import org.spdx.library.model.v3_0_1.core.ExternalIdentifier;
import org.spdx.library.model.v3_0_1.core.ExternalIdentifierType;
import org.spdx.library.model.v3_0_1.core.ExternalMap;
import org.spdx.library.model.v3_0_1.core.Hash;
import org.spdx.library.model.v3_0_1.core.HashAlgorithm;
import org.spdx.library.model.v3_0_1.core.IntegrityMethod;
import org.spdx.library.model.v3_0_1.core.LifecycleScopeType;
import org.spdx.library.model.v3_0_1.core.LifecycleScopedRelationship;
import org.spdx.library.model.v3_0_1.core.NamespaceMap;
import org.spdx.library.model.v3_0_1.core.NoAssertionElement;
import org.spdx.library.model.v3_0_1.core.NoneElement;
import org.spdx.library.model.v3_0_1.core.Organization;
import org.spdx.library.model.v3_0_1.core.PackageVerificationCode;
import org.spdx.library.model.v3_0_1.core.Person;
import org.spdx.library.model.v3_0_1.core.Relationship;
import org.spdx.library.model.v3_0_1.core.RelationshipCompleteness;
import org.spdx.library.model.v3_0_1.core.RelationshipType;
import org.spdx.library.model.v3_0_1.core.SpdxDocument;
import org.spdx.library.model.v3_0_1.core.Tool;
import org.spdx.library.model.v3_0_1.expandedlicensing.ConjunctiveLicenseSet;
import org.spdx.library.model.v3_0_1.expandedlicensing.CustomLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.DisjunctiveLicenseSet;
import org.spdx.library.model.v3_0_1.expandedlicensing.ExternalCustomLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.LicenseAddition;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicenseException;
import org.spdx.library.model.v3_0_1.expandedlicensing.NoAssertionLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.NoneLicense;
import org.spdx.library.model.v3_0_1.expandedlicensing.OrLaterOperator;
import org.spdx.library.model.v3_0_1.expandedlicensing.WithAdditionOperator;
import org.spdx.library.model.v3_0_1.simplelicensing.AnyLicenseInfo;
import org.spdx.library.model.v3_0_1.simplelicensing.LicenseExpression;
import org.spdx.library.model.v3_0_1.software.Snippet;
import org.spdx.library.model.v3_0_1.software.SpdxFile;
import org.spdx.library.model.v3_0_1.software.SpdxPackage;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * @author Gary O'Neall
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
		defaultCreationInfo = SpdxModelClassFactoryV3.createCreationInfo(toModelStore, DEFAULT_PREFIX + "createdBy", DEFAULT_CREATOR_NAME, copyManager);
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
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#Spdx2to3Converter(org.spdx.storage.IModelStore, org.spdx.library.ModelCopyManager, org.spdx.library.model.v3_0_1.core.CreationInfo, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSpdx2to3Converter() {
		Spdx2to3Converter result = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
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
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		assertFalse(converter.alreadyCopied(DOCUMENT_URI + "#" + licenseId));
		converter.convertAndStore(licV2);
		assertTrue(converter.alreadyCopied(DOCUMENT_URI + "#" + licenseId));
	}
	
	@Test
	public void testExternalElement() throws InvalidSPDXAnalysisException {
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		String created = "2010-01-29T18:30:22Z";
		String dataLicenseStr = "CC0-1.0";
		
		String externalDocumentId = SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM + "external";
		String externalDocumentUri = "https://external/document";
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		String externalDocChecksumValue = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3";
		String externalId = SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM + "external";
		
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgConcludedLicenseStr = "Apache-2.0";
		String pkgLicenseDeclaredStr = "NOASSERTION";
		String pkgDownloadLocation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		
		org.spdx.library.model.v2.SpdxDocument doc = new org.spdx.library.model.v2.SpdxDocument(fromModelStore, DOCUMENT_URI, copyManager, true);
		doc.setCreationInfo(doc.createCreationInfo(Arrays.asList(new String[] {personCreator}), created));
		org.spdx.library.model.v2.license.AnyLicenseInfo dataLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(dataLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		doc.setDataLicense(dataLicense);
		doc.setName("Document Name");
		
		String pkgId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgLicenseDeclared = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgLicenseDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.SpdxPackage pkg = doc.createPackage(pkgId, pkgName, pkgConcludedLicense, pkgCopyright, pkgLicenseDeclared)
				.setFilesAnalyzed(false)
				.setDownloadLocation(pkgDownloadLocation)
				.build();
		
		doc.setDocumentDescribes(Arrays.asList(new org.spdx.library.model.v2.SpdxItem[] {pkg}));
		org.spdx.library.model.v2.Checksum externalDocChecksum = doc.createChecksum(sha1Algorithm, externalDocChecksumValue);
		org.spdx.library.model.v2.ExternalDocumentRef externalDocRef = doc.createExternalDocumentRef(externalDocumentId, externalDocumentUri, externalDocChecksum);
		doc.setExternalDocumentRefs(Arrays.asList(new org.spdx.library.model.v2.ExternalDocumentRef[] {externalDocRef}));
		
		org.spdx.library.model.v2.ExternalSpdxElement externalElement = 
				new org.spdx.library.model.v2.ExternalSpdxElement(fromModelStore, externalDocumentUri, externalId, copyManager, true);
		
		org.spdx.library.model.v2.Relationship dependsOnRelationship = doc.createRelationship(externalElement,
				org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDS_ON, "");
		pkg.addRelationship(dependsOnRelationship);
		List<String> verify = doc.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		SpdxDocument result = converter.convertAndStore(doc);
		
		List<Relationship> resultRelationships = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		Element[] rootElements = result.getRootElements().toArray(new Element[result.getRootElements().size()]);
		assertEquals(1, rootElements.length);
		assertTrue(rootElements[0] instanceof SpdxPackage);
		SpdxPackage resultPkg = (SpdxPackage)rootElements[0];
		List<Relationship> resultDependsOnRelationships = findRelationship(resultRelationships, 
				RelationshipType.DEPENDS_ON, resultPkg.getObjectUri(), null);
		assertEquals(1, resultDependsOnRelationships.size());
		assertEquals(RelationshipType.DEPENDS_ON, resultDependsOnRelationships.get(0).getRelationshipType());
		Element[] dependOnRelTos = resultDependsOnRelationships.get(0).getTos().toArray(new Element[resultDependsOnRelationships.get(0).getTos().size()]);
		assertEquals(1, dependOnRelTos.length);
		assertTrue(dependOnRelTos[0] instanceof ExternalElement);
		assertEquals(externalDocumentUri + "#" + externalId, dependOnRelTos[0].getObjectUri());
		ExternalMap[] externalMaps = result.getSpdxImports().toArray(new ExternalMap[result.getSpdxImports().size()]);
		assertEquals(1, externalMaps.length);
		assertEquals(externalDocumentUri + "#" + externalId,externalMaps[0].getExternalSpdxId());
		IntegrityMethod[] integrityMethods = externalMaps[0].getVerifiedUsings().toArray(new IntegrityMethod[externalMaps[0].getVerifiedUsings().size()]);
		assertEquals(1, integrityMethods.length);
		assertTrue(integrityMethods[0] instanceof Hash);
		Hash hash = (Hash)integrityMethods[0];
		assertEquals(HashAlgorithm.SHA1, hash.getAlgorithm());
		assertEquals(externalDocChecksumValue, hash.getHashValue());
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}
	
	@Test
	public void testExternalExtractedLicenseInfo() throws InvalidSPDXAnalysisException {
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		String created = "2010-01-29T18:30:22Z";
		String dataLicenseStr = "CC0-1.0";
		
		String externalDocumentId = SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM + "external";
		String externalDocumentUri = "https://external/document";
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		String externalDocChecksumValue = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3";
		String externalLicenseId = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + "external";
		
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgLicenseDeclaredStr = "NOASSERTION";
		String pkgDownloadLocation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		
		org.spdx.library.model.v2.SpdxDocument doc = new org.spdx.library.model.v2.SpdxDocument(fromModelStore, DOCUMENT_URI, copyManager, true);
		doc.setCreationInfo(doc.createCreationInfo(Arrays.asList(new String[] {personCreator}), created));
		org.spdx.library.model.v2.license.AnyLicenseInfo dataLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(dataLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		doc.setDataLicense(dataLicense);
		doc.setName("Document Name");
		
		org.spdx.library.model.v2.Checksum externalDocChecksum = doc.createChecksum(sha1Algorithm, externalDocChecksumValue);
		org.spdx.library.model.v2.ExternalDocumentRef externalDocRef = doc.createExternalDocumentRef(externalDocumentId, externalDocumentUri, externalDocChecksum);
		doc.setExternalDocumentRefs(Arrays.asList(new org.spdx.library.model.v2.ExternalDocumentRef[] {externalDocRef}));
		
		String pkgId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgLicenseDeclared = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgLicenseDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo externalLic = 
				new org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo(fromModelStore, externalDocumentUri, externalLicenseId, copyManager, true);
		org.spdx.library.model.v2.SpdxPackage pkg = doc.createPackage(pkgId, pkgName, externalLic, pkgCopyright, pkgLicenseDeclared)
				.setFilesAnalyzed(false)
				.setDownloadLocation(pkgDownloadLocation)
				.build();
		
		doc.setDocumentDescribes(Arrays.asList(new org.spdx.library.model.v2.SpdxItem[] {pkg}));
		
		List<String> verify = doc.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		SpdxDocument result = converter.convertAndStore(doc);
		
		List<Relationship> resultRelationships = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		Element[] rootElements = result.getRootElements().toArray(new Element[result.getRootElements().size()]);
		assertEquals(1, rootElements.length);
		assertTrue(rootElements[0] instanceof SpdxPackage);
		SpdxPackage resultPkg = (SpdxPackage)rootElements[0];
		List<Relationship> pkgConcludedLicRelationships = findRelationship(resultRelationships, RelationshipType.HAS_CONCLUDED_LICENSE, resultPkg.getObjectUri(), null);
		assertEquals(1, pkgConcludedLicRelationships.size());
		Element[] tos = pkgConcludedLicRelationships.get(0).getTos().toArray(new Element[pkgConcludedLicRelationships.get(0).getTos().size()]);
		assertEquals(1, tos.length);
		assertTrue(tos[0] instanceof ExternalElement);
		assertEquals(externalDocumentUri + "#" + externalLicenseId, ((ExternalElement)tos[0]).getIndividualURI());
		
		ExternalMap[] externalMaps = result.getSpdxImports().toArray(new ExternalMap[result.getSpdxImports().size()]);
		assertEquals(1, externalMaps.length);
		assertEquals(externalDocumentUri + "#" + externalLicenseId,externalMaps[0].getExternalSpdxId());
		IntegrityMethod[] integrityMethods = externalMaps[0].getVerifiedUsings().toArray(new IntegrityMethod[externalMaps[0].getVerifiedUsings().size()]);
		assertEquals(1, integrityMethods.length);
		assertTrue(integrityMethods[0] instanceof Hash);
		Hash hash = (Hash)integrityMethods[0];
		assertEquals(HashAlgorithm.SHA1, hash.getAlgorithm());
		assertEquals(externalDocChecksumValue, hash.getHashValue());
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}
	
	@Test
	public void TestNoneElement() throws InvalidSPDXAnalysisException {
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		String created = "2010-01-29T18:30:22Z";
		String dataLicenseStr = "CC0-1.0";
		
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgConcludedLicenseStr = "Apache-2.0";
		String pkgLicenseDeclaredStr = "NOASSERTION";
		String pkgDownloadLocation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		
		org.spdx.library.model.v2.SpdxDocument doc = new org.spdx.library.model.v2.SpdxDocument(fromModelStore, DOCUMENT_URI, copyManager, true);
		doc.setCreationInfo(doc.createCreationInfo(Arrays.asList(new String[] {personCreator}), created));
		org.spdx.library.model.v2.license.AnyLicenseInfo dataLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(dataLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		doc.setDataLicense(dataLicense);
		doc.setName("Document Name");
		
		String pkgId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgLicenseDeclared = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgLicenseDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.SpdxPackage pkg = doc.createPackage(pkgId, pkgName, pkgConcludedLicense, pkgCopyright, pkgLicenseDeclared)
				.setFilesAnalyzed(false)
				.setDownloadLocation(pkgDownloadLocation)
				.build();
		
		doc.setDocumentDescribes(Arrays.asList(new org.spdx.library.model.v2.SpdxItem[] {pkg}));
		
		org.spdx.library.model.v2.Relationship dependsOnRelationship = 
				doc.createRelationship(new org.spdx.library.model.v2.SpdxNoneElement(),
				org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDS_ON, "");
		pkg.addRelationship(dependsOnRelationship);
		List<String> verify = doc.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		SpdxDocument result = converter.convertAndStore(doc);
		
		List<Relationship> resultRelationships = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		Element[] rootElements = result.getRootElements().toArray(new Element[result.getRootElements().size()]);
		assertEquals(1, rootElements.length);
		assertTrue(rootElements[0] instanceof SpdxPackage);
		SpdxPackage resultPkg = (SpdxPackage)rootElements[0];
		List<Relationship> resultDependsOnRelationships = findRelationship(resultRelationships, 
				RelationshipType.DEPENDS_ON, resultPkg.getObjectUri(), null);
		assertEquals(1, resultDependsOnRelationships.size());
		assertEquals(RelationshipType.DEPENDS_ON, resultDependsOnRelationships.get(0).getRelationshipType());
		assertTrue(resultDependsOnRelationships.get(0).getTos().isEmpty());
		assertEquals(RelationshipCompleteness.COMPLETE, resultDependsOnRelationships.get(0).getCompleteness().get());
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}
	
	@Test
	public void TestNoAssertionLicense() throws InvalidSPDXAnalysisException {
		String extractedText = "Extracted text";
		String extractedLicName = "name";
		org.spdx.library.model.v2.license.ExtractedLicenseInfo lic1 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		lic1.setName(extractedLicName);
		lic1.setExtractedText(extractedText);
		org.spdx.library.model.v2.license.AnyLicenseInfo lic2 = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2("NOASSERTION", fromModelStore, DOCUMENT_URI, copyManager);
		
		org.spdx.library.model.v2.license.DisjunctiveLicenseSet ors = 
				new org.spdx.library.model.v2.license.DisjunctiveLicenseSet(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.Anonymous),
						copyManager, true);
		ors.addMember(lic1);
		ors.addMember(lic2);
		List<String> verify = ors.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		DisjunctiveLicenseSet result = converter.convertAndStore(ors);
		AnyLicenseInfo[] members = result.getMembers().toArray(new AnyLicenseInfo[result.getMembers().size()]);
		assertEquals(2, members.length);
		CustomLicense customLicenseResult = null;
		NoAssertionLicense noAssertionLicense = null;
		for (AnyLicenseInfo member:members) {
			if (member instanceof CustomLicense) {
				customLicenseResult = (CustomLicense)member;
			}
			if (member instanceof NoAssertionLicense) {
				noAssertionLicense = (NoAssertionLicense)member;
			}
		}
		assertTrue(Objects.nonNull(noAssertionLicense));
		assertTrue(Objects.nonNull(customLicenseResult));
		assertEquals(extractedLicName, customLicenseResult.getName().get());
		assertEquals(extractedText, customLicenseResult.getLicenseText());
		verify = result.verify();
		assertTrue(verify.isEmpty());
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
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
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
		
		org.spdx.library.model.v2.enumerations.AnnotationType annotationType = 
				org.spdx.library.model.v2.enumerations.AnnotationType.REVIEW;
		String annotationComment = "Annotation comment";
		
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgConcludedLicenseStr = "Apache-2.0";
		String pkgLicenseDeclaredStr = "NOASSERTION";
		String pkgDownloadLocation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		
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
				.setDownloadLocation(pkgDownloadLocation)
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
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		SpdxDocument result = converter.convertAndStore(doc);
		assertEquals(docComment, result.getComment().get());
		assertEquals(dataLicenseStr, result.getDataLicense().get().toString());
		
		List<Relationship> resultRelationships = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
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
		assertEquals(pkgDownloadLocation, resultPkg.getDownloadLocation().get());
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
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}
	
	@Test
	public void testConvertAndStoreExternalDocRef() throws InvalidSPDXAnalysisException {
		String externalDocumentId = SpdxConstantsCompatV2.EXTERNAL_DOC_REF_PRENUM + "external";
		String externalDocumentUri = "https://external/document";

		org.spdx.library.model.v2.ExternalDocumentRef externalDocRef =
				new org.spdx.library.model.v2.ExternalDocumentRef(fromModelStore, DOCUMENT_URI, externalDocumentId, copyManager, true);
		externalDocRef.setSpdxDocumentNamespace(externalDocumentUri);
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		Collection<ExternalMap> docImports = new ArrayList<>();
		NamespaceMap result = converter.convertAndStore(externalDocRef, docImports);
		assertEquals(externalDocumentId, result.getPrefix());
		assertEquals(externalDocumentUri, result.getNamespace());
	}
	
	List<Relationship> findRelationship(List<Relationship> relationships, @Nullable RelationshipType relationshipType,
			@Nullable String fromObjectUri, @Nullable String toObjectUri) throws InvalidSPDXAnalysisException {
		List<Relationship> result = new ArrayList<>();
		for (Relationship relationship:relationships) {
			RelationshipType relType = relationship.getRelationshipType();
			String fromObject = relationship.getFrom().getObjectUri();
			if ((Objects.isNull(relationshipType) || Objects.equals(relType, relationshipType)) &&
					(Objects.isNull(fromObjectUri) || Objects.equals(fromObject, fromObjectUri))) {
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
	public void testConvertAndStoreRelationship() throws InvalidSPDXAnalysisException {
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgDownloadLocation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		
		String fileName = "File name";
		String fileCopyright = "Copyright my file";
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		String fileChecksumValue = "c3cdc01f93cf99c15097c0cdf107a28466b83913";
		
		String relationshipComment = "relationshipComment";
		
		org.spdx.library.model.v2.license.SpdxNoAssertionLicense noAssertionLicense = 
				new org.spdx.library.model.v2.license.SpdxNoAssertionLicense();
		org.spdx.library.model.v2.SpdxPackage pkg = 
				new org.spdx.library.model.v2.SpdxPackage(fromModelStore, DOCUMENT_URI, 
						fromModelStore.getNextId(IdType.SpdxId), copyManager, true);
		pkg.setName(pkgName);
		pkg.setCopyrightText(pkgCopyright);
		pkg.setLicenseConcluded(noAssertionLicense);
		pkg.setLicenseDeclared(noAssertionLicense);
		pkg.setDownloadLocation(pkgDownloadLocation);
		pkg.setFilesAnalyzed(false);
		
		org.spdx.library.model.v2.Checksum fileSha1 = pkg.createChecksum(sha1Algorithm, fileChecksumValue);
		org.spdx.library.model.v2.SpdxFile spdxFile = pkg.createSpdxFile(fromModelStore.getNextId(IdType.SpdxId),
				fileName, noAssertionLicense, 
				Arrays.asList(new org.spdx.library.model.v2.license.AnyLicenseInfo[] {noAssertionLicense}),
				fileCopyright, fileSha1)
				.build();
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		
		SpdxPackage packageElement = converter.convertAndStore(pkg);
		List<String> verify = packageElement.verify();
		assertTrue(verify.isEmpty());
		
		// Standard relationship
		org.spdx.library.model.v2.Relationship stdRelationship = 
				pkg.createRelationship(spdxFile, org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINS, relationshipComment);
		verify = stdRelationship.verify();
		assertTrue(verify.isEmpty());
		Relationship result = converter.convertAndStore(stdRelationship, packageElement);
		assertEquals(relationshipComment, result.getComment().get());
		assertEquals(RelationshipType.CONTAINS, result.getRelationshipType());
		assertEquals(packageElement, result.getFrom());
		Element[] tos = result.getTos().toArray(new Element[result.getTos().size()]);
		assertEquals(1, tos.length);
		assertTrue(tos[0] instanceof SpdxFile);
		assertEquals(fileName, ((SpdxFile)tos[0]).getName().get());
		assertEquals(RelationshipCompleteness.NO_ASSERTION, result.getCompleteness().get());
		verify = result.verify();
		assertTrue(verify.isEmpty());
		
		// Reverse relationship
		org.spdx.library.model.v2.Relationship reverseRelationship = 
				pkg.createRelationship(spdxFile, org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINED_BY, relationshipComment);
		verify = reverseRelationship.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore(reverseRelationship, packageElement);
		assertEquals(relationshipComment, result.getComment().get());
		assertEquals(RelationshipType.CONTAINS, result.getRelationshipType());
		tos = result.getTos().toArray(new Element[result.getTos().size()]);
		assertEquals(1, tos.length);
		assertEquals(packageElement, tos[0]);
		assertTrue(result.getFrom() instanceof SpdxFile);
		assertEquals(fileName, ((SpdxFile)result.getFrom()).getName().get());
		assertEquals(RelationshipCompleteness.NO_ASSERTION, result.getCompleteness().get());
		verify = result.verify();
		assertTrue(verify.isEmpty());
		
		// Relationship with NoAssertion
		org.spdx.library.model.v2.SpdxNoAssertionElement noAssertionElement = 
				new org.spdx.library.model.v2.SpdxNoAssertionElement();
		org.spdx.library.model.v2.Relationship noAssertionRelationship = 
				pkg.createRelationship(noAssertionElement, org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINS, relationshipComment);
		verify = noAssertionRelationship.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore(noAssertionRelationship, packageElement);
		assertEquals(relationshipComment, result.getComment().get());
		assertEquals(RelationshipType.CONTAINS, result.getRelationshipType());
		assertEquals(packageElement, result.getFrom());
		tos = result.getTos().toArray(new Element[result.getTos().size()]);
		assertEquals(0, tos.length);
		assertEquals(RelationshipCompleteness.NO_ASSERTION, result.getCompleteness().get());
		verify = result.verify();
		assertTrue(verify.isEmpty());
		
		// Relationship with None
		org.spdx.library.model.v2.SpdxNoneElement noneElement = 
				new org.spdx.library.model.v2.SpdxNoneElement();
		org.spdx.library.model.v2.Relationship noneRelationship = 
				pkg.createRelationship(noneElement, org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINS, relationshipComment);
		verify = noneRelationship.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore(noneRelationship, packageElement);
		assertEquals(relationshipComment, result.getComment().get());
		assertEquals(RelationshipType.CONTAINS, result.getRelationshipType());
		assertEquals(packageElement, result.getFrom());
		tos = result.getTos().toArray(new Element[result.getTos().size()]);
		assertEquals(0, tos.length);
		assertEquals(RelationshipCompleteness.COMPLETE, result.getCompleteness().get());
		verify = result.verify();
		assertTrue(verify.isEmpty());
		
		// Relationship with build scope
		org.spdx.library.model.v2.Relationship buildRelationship = 
				pkg.createRelationship(spdxFile, org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDS_ON, relationshipComment);
		verify = buildRelationship.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore(buildRelationship, packageElement);
		assertEquals(relationshipComment, result.getComment().get());
		assertEquals(RelationshipType.DEPENDS_ON, result.getRelationshipType());
		assertEquals(packageElement, result.getFrom());
		tos = result.getTos().toArray(new Element[result.getTos().size()]);
		assertEquals(1, tos.length);
		assertTrue(tos[0] instanceof SpdxFile);
		assertEquals(fileName, ((SpdxFile)tos[0]).getName().get());
		assertEquals(RelationshipCompleteness.NO_ASSERTION, result.getCompleteness().get());
		assertTrue(result instanceof LifecycleScopedRelationship);
		assertEquals(LifecycleScopeType.BUILD, ((LifecycleScopedRelationship)result).getScope().get());
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}
	
	@Test
	public void testConvertAndStoreAnnotation() throws InvalidSPDXAnalysisException {
		String personCreatorName = "Gary Lee O'Neall";
		String personCreatorEmail = "garysourceauditor@gmail.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		
		String created = "2015-01-29T18:30:22Z";
		
		String fileName = "File name";
		String fileConcludedLicenseStr = "MIT";
		String fileSeenLicenseStr = "NONE";
		String fileCopyright = "Copyright my file";
		String fileChecksumValue = "c3cdc01f93cf99c15097c0cdf107a28466b83913";
		String licenseComments = "licenseComments";
		String noticeText = "noticeText";
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		
		org.spdx.library.model.v2.enumerations.AnnotationType annotationType = 
				org.spdx.library.model.v2.enumerations.AnnotationType.REVIEW;
		String annotationComment = "Annotation comment";
		org.spdx.library.model.v2.Annotation annotation =
				new org.spdx.library.model.v2.Annotation(fromModelStore, DOCUMENT_URI, 
						fromModelStore.getNextId(IdType.Anonymous), copyManager, true);
		annotation.setAnnotationDate(created);
		annotation.setAnnotationType(annotationType);
		annotation.setComment(annotationComment);
		annotation.setAnnotator(personCreator);
		List<String> verify = annotation.verify();
		assertTrue(verify.isEmpty());
		
		String fileId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.SpdxFile spdxFile = 
				new org.spdx.library.model.v2.SpdxFile(fromModelStore, DOCUMENT_URI, fileId, copyManager, true);
		
		org.spdx.library.model.v2.license.AnyLicenseInfo fileConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileSeenLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileSeenLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		
		org.spdx.library.model.v2.Checksum pkgSha1 = spdxFile.createChecksum(sha1Algorithm, fileChecksumValue);
		spdxFile.getChecksums().add(pkgSha1);
		spdxFile.setCopyrightText(fileCopyright);
		spdxFile.setLicenseComments(licenseComments);
		spdxFile.setLicenseConcluded(fileConcludedLicense);
		spdxFile.getLicenseInfoFromFiles().add(fileSeenLicense);
		spdxFile.setName(fileName);
		spdxFile.setNoticeText(noticeText);
		verify = spdxFile.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		SpdxFile file = converter.convertAndStore(spdxFile);
		Annotation result = converter.convertAndStore(annotation, file);
		
		Object[] resultAnnotations = SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_ANNOTATION, DEFAULT_PREFIX, DEFAULT_PREFIX).toArray();
		assertEquals(1, resultAnnotations.length);
		assertTrue(resultAnnotations[0] instanceof Annotation);
		Annotation resultAnnotation = (Annotation)resultAnnotations[0];
		assertEquals(annotationType.toString(), resultAnnotation.getAnnotationType().toString());
		assertEquals(annotationComment, resultAnnotation.getStatement().get());
		assertEquals(annotationType.toString(), result.getAnnotationType().toString());
		assertEquals(annotationComment, result.getStatement().get());
		assertEquals(created, result.getCreationInfo().getCreated());
		Agent[] createdBys = result.getCreationInfo().getCreatedBys().toArray(new Agent[result.getCreationInfo().getCreatedBys().size()]);
		assertEquals(1, createdBys.length);
		assertEquals(personCreatorName, createdBys[0].getName().get());
		ExternalIdentifier[] identifiers = createdBys[0].getExternalIdentifiers().toArray(
				new ExternalIdentifier[createdBys[0].getExternalIdentifiers().size()]);
		assertEquals(1, identifiers.length);
		assertEquals(ExternalIdentifierType.EMAIL, identifiers[0].getExternalIdentifierType());
		assertEquals(personCreatorEmail, identifiers[0].getIdentifier());
	}
	
	
	@Test
	public void testConvertAndStoreListedLicenseException() throws InvalidSPDXAnalysisException {
		String exceptionId = "ExceptionId";
		String exceptionComment = "exceptionComment";
		Boolean exceptionDeprecated = true;
		String exceptionDeprecatedVersion = "v3.24.0";
		String exceptionTextHtml = "exceptionTextHtml";
		String exceptionTemplate = "exceptionTemplate";
		String exceptionName = "exceptionName";
		String seeAlso1 = "https://over/here";
		String seeAlso2 = "https://over/here2";
		List<String> exceptionSeeAlsos = Arrays.asList(new String[] {seeAlso1, seeAlso2});
		String exceptionText = "exceptionText";
		
		org.spdx.library.model.v2.license.ListedLicenseException licException = 
				new org.spdx.library.model.v2.license.ListedLicenseException(fromModelStore, DOCUMENT_URI,
						exceptionId, copyManager, true);
		licException.setComment(exceptionComment);
		licException.setDeprecated(exceptionDeprecated);
		licException.setDeprecatedVersion(exceptionDeprecatedVersion);
		licException.setExceptionTextHtml(exceptionTextHtml);
		licException.setLicenseExceptionTemplate(exceptionTemplate);
		licException.setLicenseExceptionText(exceptionText);
		licException.setName(exceptionName);
		licException.setSeeAlso(exceptionSeeAlsos);
		List<String> verify = licException.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		LicenseAddition result = converter.convertAndStore(licException);
		assertTrue(result instanceof ListedLicenseException);
		assertEquals(exceptionComment, result.getComment().get());
		assertEquals(exceptionDeprecated, ((ListedLicenseException)result).getIsDeprecatedAdditionId().get());
		assertEquals(exceptionDeprecatedVersion, ((ListedLicenseException)result).getDeprecatedVersion().get());
		assertEquals(exceptionTemplate, ((ListedLicenseException)result).getStandardAdditionTemplate().get());
		assertEquals(exceptionText, result.getAdditionText());
		assertEquals(exceptionName, result.getName().get());
		assertEquals(exceptionSeeAlsos.size(), result.getSeeAlsos().size());
		assertTrue(result.getSeeAlsos().containsAll(exceptionSeeAlsos));
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}
	
	@Test
	public void testConvertAndStoreElement() throws InvalidSPDXAnalysisException {
		// org.spdx.library.model.v2.ExternalSpdxElement;
		String externalDocumentUri = "https://external/document";
		String externalId = SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM + "external";
		org.spdx.library.model.v2.ExternalSpdxElement externalElement = 
				new org.spdx.library.model.v2.ExternalSpdxElement(fromModelStore, externalDocumentUri, externalId, copyManager, true);
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		Element result = converter.convertAndStore((org.spdx.library.model.v2.SpdxElement)externalElement);
		assertTrue(result instanceof ExternalElement);
		List<String> verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.SpdxNoneElement;
		org.spdx.library.model.v2.SpdxNoneElement noneElement = new org.spdx.library.model.v2.SpdxNoneElement();
		result = converter.convertAndStore((org.spdx.library.model.v2.SpdxElement)noneElement);
		assertTrue(result instanceof NoneElement);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.SpdxNoAssertionElement;
		org.spdx.library.model.v2.SpdxNoAssertionElement noAssertionElement = new org.spdx.library.model.v2.SpdxNoAssertionElement();
		result = converter.convertAndStore((org.spdx.library.model.v2.SpdxElement)noAssertionElement);
		assertTrue(result instanceof NoAssertionElement);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.SpdxDocument;
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		
		String created = "2010-01-29T18:30:22Z";
		String dataLicenseStr = "CC0-1.0";
		
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgConcludedLicenseStr = "Apache-2.0";
		String pkgLicenseDeclaredStr = "NOASSERTION";
		String pkgDownloadLocation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		
		String docName = "DocumentName";
		
		String fileName = "File name";
		String fileConcludedLicenseStr = "MIT";
		String fileSeenLicenseStr = "NONE";
		String fileCopyright = "Copyright my file";
		String fileChecksumValue = "c3cdc01f93cf99c15097c0cdf107a28466b83913";
		
		org.spdx.library.model.v2.SpdxDocument doc = new org.spdx.library.model.v2.SpdxDocument(fromModelStore, DOCUMENT_URI, copyManager, true);
		doc.setCreationInfo(doc.createCreationInfo(Arrays.asList(new String[] {personCreator}), created));
		org.spdx.library.model.v2.license.AnyLicenseInfo dataLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(dataLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		doc.setDataLicense(dataLicense);
		String pkgId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgLicenseDeclared = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgLicenseDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.SpdxPackage pkg = doc.createPackage(pkgId, pkgName, pkgConcludedLicense, pkgCopyright, pkgLicenseDeclared)
				.setFilesAnalyzed(false)
				.setDownloadLocation(pkgDownloadLocation)
				.build();		
		doc.setDocumentDescribes(Arrays.asList(new org.spdx.library.model.v2.SpdxItem[] {pkg}));
		doc.setName(docName);
		
		verify = doc.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore((org.spdx.library.model.v2.SpdxElement)doc);
		assertTrue(result instanceof SpdxDocument);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		
		// org.spdx.library.model.v2.SpdxFile;
		String fileId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileSeenLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileSeenLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.Checksum fileSha1 = doc.createChecksum(sha1Algorithm, fileChecksumValue);
		org.spdx.library.model.v2.SpdxFile spdxFile = doc.createSpdxFile(fileId, fileName, fileConcludedLicense, 
				Arrays.asList(new org.spdx.library.model.v2.license.AnyLicenseInfo[] {fileSeenLicense}), fileCopyright, fileSha1)
				.build();
		verify = spdxFile.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore((org.spdx.library.model.v2.SpdxElement)spdxFile);
		assertTrue(result instanceof SpdxFile);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.SpdxPackage;
		result = converter.convertAndStore((org.spdx.library.model.v2.SpdxElement)pkg);
		assertTrue(result instanceof SpdxPackage);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.SpdxSnippet;
		String snippetConcludedStr = "MIT";
		String snippetDeclaredStr = "Apache-2.0";
		Integer fromByte = 1212;
		Integer toByte = 5050;
		String snippetId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.SpdxSnippet snippet = new 
				org.spdx.library.model.v2.SpdxSnippet(fromModelStore, DOCUMENT_URI,
						snippetId, copyManager, true);
		org.spdx.library.model.v2.license.AnyLicenseInfo licenseConcluded =
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(snippetConcludedStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo licenseDeclared =
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(snippetDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		snippet.getLicenseInfoFromFiles().add(licenseDeclared);
		snippet.setLicenseConcluded(licenseConcluded);
		snippet.setSnippetFromFile(spdxFile);
		snippet.setByteRange(fromByte, toByte);
		verify = snippet.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore((org.spdx.library.model.v2.SpdxElement)snippet);
		assertTrue(result instanceof Snippet);
		verify = result.verify();
	}
	
	@Test
	public void testConvertAndStoreHash() throws InvalidSPDXAnalysisException {
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm checksumAlgorithm =
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.MD5;
		String value = "912ec803b2ce49e4a541068d495ab570";
		org.spdx.library.model.v2.Checksum checksum =
				new org.spdx.library.model.v2.Checksum(fromModelStore, DOCUMENT_URI,
						fromModelStore.getNextId(IdType.Anonymous), copyManager, true);
		checksum.setAlgorithm(checksumAlgorithm);
		checksum.setValue(value);
		List<String> verify = checksum.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		Hash result = converter.convertAndStore(checksum);
		assertEquals(checksumAlgorithm.toString(), result.getAlgorithm().toString());
		assertEquals(value, result.getHashValue());
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}


	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.ConjunctiveLicenseSet)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreConjunctiveLicenseSet() throws InvalidSPDXAnalysisException {
		String extractedText = "Extracted text";
		String extractedLicName = "name";
		org.spdx.library.model.v2.license.ExtractedLicenseInfo lic1 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		lic1.setName(extractedLicName);
		lic1.setExtractedText(extractedText);
		org.spdx.library.model.v2.license.AnyLicenseInfo lic2 = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2("Apache-2.0", fromModelStore, DOCUMENT_URI, copyManager);
		
		org.spdx.library.model.v2.license.ConjunctiveLicenseSet ands = 
				new org.spdx.library.model.v2.license.ConjunctiveLicenseSet(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.Anonymous),
						copyManager, true);
		ands.addMember(lic1);
		ands.addMember(lic2);
		List<String> verify = ands.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		ConjunctiveLicenseSet result = converter.convertAndStore(ands);
		AnyLicenseInfo[] members = result.getMembers().toArray(new AnyLicenseInfo[result.getMembers().size()]);
		assertEquals(2, members.length);
		CustomLicense customLicenseResult = null;
		ListedLicense listedLicenseResult = null;
		for (AnyLicenseInfo member:members) {
			if (member instanceof CustomLicense) {
				customLicenseResult = (CustomLicense)member;
			}
			if (member instanceof ListedLicense) {
				listedLicenseResult = (ListedLicense)member;
			}
		}
		assertTrue(Objects.nonNull(listedLicenseResult));
		assertTrue(listedLicenseResult.getObjectUri().endsWith("Apache-2.0"));
		assertTrue(Objects.nonNull(customLicenseResult));
		assertEquals(extractedLicName, customLicenseResult.getName().get());
		assertEquals(extractedText, customLicenseResult.getLicenseText());
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.DisjunctiveLicenseSet)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreDisjunctiveLicenseSet() throws InvalidSPDXAnalysisException {
		String extractedText = "Extracted text";
		String extractedLicName = "name";
		org.spdx.library.model.v2.license.ExtractedLicenseInfo lic1 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		lic1.setName(extractedLicName);
		lic1.setExtractedText(extractedText);
		org.spdx.library.model.v2.license.AnyLicenseInfo lic2 = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2("Apache-2.0", fromModelStore, DOCUMENT_URI, copyManager);
		
		org.spdx.library.model.v2.license.DisjunctiveLicenseSet ors = 
				new org.spdx.library.model.v2.license.DisjunctiveLicenseSet(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.Anonymous),
						copyManager, true);
		ors.addMember(lic1);
		ors.addMember(lic2);
		List<String> verify = ors.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		DisjunctiveLicenseSet result = converter.convertAndStore(ors);
		AnyLicenseInfo[] members = result.getMembers().toArray(new AnyLicenseInfo[result.getMembers().size()]);
		assertEquals(2, members.length);
		CustomLicense customLicenseResult = null;
		ListedLicense listedLicenseResult = null;
		for (AnyLicenseInfo member:members) {
			if (member instanceof CustomLicense) {
				customLicenseResult = (CustomLicense)member;
			}
			if (member instanceof ListedLicense) {
				listedLicenseResult = (ListedLicense)member;
			}
		}
		assertTrue(Objects.nonNull(listedLicenseResult));
		assertTrue(listedLicenseResult.getObjectUri().endsWith("Apache-2.0"));
		assertTrue(Objects.nonNull(customLicenseResult));
		assertEquals(extractedLicName, customLicenseResult.getName().get());
		assertEquals(extractedText, customLicenseResult.getLicenseText());
		verify = result.verify();
		assertTrue(verify.isEmpty());
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
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
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
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreOrLaterOperator() throws InvalidSPDXAnalysisException {
		String extractedText = "Extracted text";
		String extractedLicName = "name";
		org.spdx.library.model.v2.license.ExtractedLicenseInfo lic1 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		lic1.setName(extractedLicName);
		lic1.setExtractedText(extractedText);
		org.spdx.library.model.v2.license.OrLaterOperator orLater = 
				new org.spdx.library.model.v2.license.OrLaterOperator(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		orLater.setLicense(lic1);
		List<String> verify = orLater.verify();
		assertTrue(verify.isEmpty());
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		OrLaterOperator result = converter.convertAndStore(orLater);
		assertEquals(extractedText, result.getSubjectLicense().getLicenseText());
		assertEquals(extractedLicName, result.getSubjectLicense().getName().get());
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.SpdxListedLicense)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreSpdxListedLicense() throws InvalidSPDXAnalysisException {
		String licenseId = "licenseId";
		String licenseComment = "licenseComment";
		Boolean deprecated = true;
		String deprecatedVersion = "3.14.2";
		Boolean fsfLibre = true;
		String licenseHeaderHtml = "licenseHeaderHtml";
		String licenseText = "licenseText";
		String licenseTextHtml = "licenseTextHtml";
		String licenseName = "licenseName";
		Boolean osiApproved = true;
		String seeAlso1 = "http://see/one";
		String seeAlso2 = "http://see/two";
		List<String> seeAlsos = Arrays.asList(new String[] {seeAlso1, seeAlso2});
		String standardLicenseHeader = "standardLicenseHeader";
		
		org.spdx.library.model.v2.license.SpdxListedLicense listedLicense = 
				new org.spdx.library.model.v2.license.SpdxListedLicense(fromModelStore, DOCUMENT_URI, 
						licenseId, copyManager, true);
		listedLicense.setComment(licenseComment);
		listedLicense.setDeprecated(deprecated);
		listedLicense.setDeprecatedVersion(deprecatedVersion);
		listedLicense.setFsfLibre(fsfLibre);
		listedLicense.setLicenseHeaderHtml(licenseHeaderHtml);
		listedLicense.setLicenseText(licenseText);
		listedLicense.setLicenseTextHtml(licenseTextHtml);
		listedLicense.setName(licenseName);
		listedLicense.setOsiApproved(osiApproved);
		listedLicense.setSeeAlso(seeAlsos);
		listedLicense.setStandardLicenseHeader(standardLicenseHeader);
		List<String> verify = listedLicense.verify();
		assertEquals(1, verify.size()); // deprecated ID causes a warning
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		ListedLicense result = converter.convertAndStore(listedLicense);
		assertEquals(licenseComment, result.getComment().get());
		assertEquals(deprecated, result.getIsDeprecatedLicenseId().get());
		assertEquals(deprecatedVersion, result.getDeprecatedVersion().get());
		assertEquals(fsfLibre, result.getIsFsfLibre().get());
		assertEquals(licenseText, result.getLicenseText());
		assertEquals(licenseName, result.getName().get());
		assertEquals(osiApproved, result.getIsOsiApproved().get());
		assertEquals(seeAlsos.size(), result.getSeeAlsos().size());
		assertTrue(result.getSeeAlsos().containsAll(seeAlsos));
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.WithExceptionOperator)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreWithExceptionOperator() throws InvalidSPDXAnalysisException {
		String exceptionId = "ExceptionId";
		String exceptionName = "exceptionName";
		String exceptionText = "exceptionText";
		
		org.spdx.library.model.v2.license.ListedLicenseException licException = 
				new org.spdx.library.model.v2.license.ListedLicenseException(fromModelStore, DOCUMENT_URI,
						exceptionId, copyManager, true);
		licException.setLicenseExceptionText(exceptionText);
		licException.setName(exceptionName);
		org.spdx.library.model.v2.license.WithExceptionOperator withException = 
				new org.spdx.library.model.v2.license.WithExceptionOperator(fromModelStore, DOCUMENT_URI,
						fromModelStore.getNextId(IdType.Anonymous), copyManager, true);
		withException.setException(licException);
		org.spdx.library.model.v2.license.SpdxListedLicense listedLicense = 
				(SpdxListedLicense) LicenseInfoFactory.parseSPDXLicenseStringCompatV2("Apache-2.0", fromModelStore, DOCUMENT_URI, copyManager);
		withException.setLicense(listedLicense);
		List<String> verify = withException.verify();
		assertTrue(verify.isEmpty());
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		WithAdditionOperator result = converter.convertAndStore(withException);
		assertTrue(result.getSubjectExtendableLicense().getObjectUri().endsWith("Apache-2.0"));
		assertEquals(exceptionText, result.getSubjectAddition().getAdditionText());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.license.AnyLicenseInfo)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreAnyLicenseInfo() throws InvalidSPDXAnalysisException {	
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		
		// org.spdx.library.model.v2.license.ExtractedLicenseInfo;
		String extractedText = "Extracted text";
		org.spdx.library.model.v2.license.ExtractedLicenseInfo licV2 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		licV2.setExtractedText(extractedText);
		List<String> verify = licV2.verify();
		assertTrue(verify.isEmpty());
		AnyLicenseInfo result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)licV2);
		assertTrue(result instanceof CustomLicense);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.license.ConjunctiveLicenseSet;
		org.spdx.library.model.v2.license.ConjunctiveLicenseSet ands = 
				new org.spdx.library.model.v2.license.ConjunctiveLicenseSet(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.Anonymous),
						copyManager, true);
		ands.addMember(licV2);
		verify = ands.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)ands);
		assertTrue(result instanceof ConjunctiveLicenseSet);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.license.DisjunctiveLicenseSet;
		org.spdx.library.model.v2.license.DisjunctiveLicenseSet ors = 
				new org.spdx.library.model.v2.license.DisjunctiveLicenseSet(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.Anonymous),
						copyManager, true);
		ors.addMember(licV2);
		verify = ors.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)ors);
		assertTrue(result instanceof DisjunctiveLicenseSet);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.license.OrLaterOperator;
		org.spdx.library.model.v2.license.OrLaterOperator orLater = 
				new org.spdx.library.model.v2.license.OrLaterOperator(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.LicenseRef),
						copyManager, true);
		orLater.setLicense(licV2);
		verify = orLater.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)orLater);
		assertTrue(result instanceof OrLaterOperator);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo;
		String externalDocumentUri = "https://external/document";
		String externalLicenseId = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + "external";
		org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo externalExtracted = 
				new org.spdx.library.model.v2.license.ExternalExtractedLicenseInfo(fromModelStore, externalDocumentUri, externalLicenseId, copyManager, true);
		result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)externalExtracted);
		assertTrue(result instanceof ExternalCustomLicense);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.license.SpdxListedLicense;
		org.spdx.library.model.v2.license.SpdxListedLicense listedLicense = 
				(SpdxListedLicense) LicenseInfoFactory.parseSPDXLicenseStringCompatV2("Apache-2.0", fromModelStore, DOCUMENT_URI, copyManager);
		result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)listedLicense);
		assertTrue(result instanceof ListedLicense);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.license.SpdxNoAssertionLicense;
		org.spdx.library.model.v2.license.SpdxNoAssertionLicense noAssertionLicense = 
				new org.spdx.library.model.v2.license.SpdxNoAssertionLicense();
		result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)noAssertionLicense);
		assertTrue(result instanceof NoAssertionLicense);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// org.spdx.library.model.v2.license.SpdxNoneLicense;
		org.spdx.library.model.v2.license.SpdxNoneLicense noneLicense = 
				new org.spdx.library.model.v2.license.SpdxNoneLicense();
		result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)noneLicense);
		assertTrue(result instanceof NoneLicense);
		verify = result.verify();
		assertTrue(verify.isEmpty());
		// WithExceptionOperator
		String exceptionId = "ExceptionId";
		String exceptionName = "exceptionName";
		String exceptionText = "exceptionText";
		
		org.spdx.library.model.v2.license.ListedLicenseException licException = 
				new org.spdx.library.model.v2.license.ListedLicenseException(fromModelStore, DOCUMENT_URI,
						exceptionId, copyManager, true);
		licException.setLicenseExceptionText(exceptionText);
		licException.setName(exceptionName);
		org.spdx.library.model.v2.license.WithExceptionOperator withException = 
				new org.spdx.library.model.v2.license.WithExceptionOperator(fromModelStore, DOCUMENT_URI,
						fromModelStore.getNextId(IdType.Anonymous), copyManager, true);
		withException.setException(licException);
		withException.setLicense(listedLicense);
		verify = withException.verify();
		assertTrue(verify.isEmpty());
		result = converter.convertAndStore((org.spdx.library.model.v2.license.AnyLicenseInfo)withException);
		assertTrue(result instanceof WithAdditionOperator);
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.SpdxFile)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreSpdxFile() throws InvalidSPDXAnalysisException {
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		
		String created = "2010-01-29T18:30:22Z";
		
		org.spdx.library.model.v2.enumerations.AnnotationType annotationType = 
				org.spdx.library.model.v2.enumerations.AnnotationType.REVIEW;
		String annotationComment = "Annotation comment";
		
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgConcludedLicenseStr = "Apache-2.0";
		String pkgLicenseDeclaredStr = "NOASSERTION";
		String pkgDownloadLocation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		
		String fileName = "File name";
		String fileConcludedLicenseStr = "MIT";
		String fileSeenLicenseStr = "NONE";
		String fileCopyright = "Copyright my file";
		String fileChecksumValue = "c3cdc01f93cf99c15097c0cdf107a28466b83913";
		String fileComment = "fileComment";
		String licenseComments = "licenseComments";
		String noticeText = "noticeText";
		
		org.spdx.library.model.v2.enumerations.RelationshipType containsRelationshipType = 
				org.spdx.library.model.v2.enumerations.RelationshipType.CONTAINS;
		String relationshipComment = "Relationship comment";
		
		String fileId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.SpdxFile spdxFile = 
				new org.spdx.library.model.v2.SpdxFile(fromModelStore, DOCUMENT_URI, fileId, copyManager, true);
		
		org.spdx.library.model.v2.license.AnyLicenseInfo fileConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileSeenLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileSeenLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		
		org.spdx.library.model.v2.Checksum pkgSha1 = spdxFile.createChecksum(sha1Algorithm, fileChecksumValue);
		spdxFile.getChecksums().add(pkgSha1);
		org.spdx.library.model.v2.Annotation annotation = spdxFile.createAnnotation(personCreator, annotationType, 
				created, annotationComment);
		spdxFile.addAnnotation(annotation);
		spdxFile.setComment(fileComment);
		spdxFile.setCopyrightText(fileCopyright);
		spdxFile.setLicenseComments(licenseComments);
		spdxFile.setLicenseConcluded(fileConcludedLicense);
		spdxFile.getLicenseInfoFromFiles().add(fileSeenLicense);
		spdxFile.setName(fileName);
		spdxFile.setNoticeText(noticeText);
		String pkgId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgLicenseDeclared = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgLicenseDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.SpdxPackage pkg = spdxFile.createPackage(pkgId, pkgName, pkgConcludedLicense, pkgCopyright, pkgLicenseDeclared)
				.setFilesAnalyzed(false)
				.setDownloadLocation(pkgDownloadLocation)
				.build();
		
		
		org.spdx.library.model.v2.Relationship containsRelationship = spdxFile.createRelationship(pkg, containsRelationshipType, relationshipComment);
		spdxFile.addRelationship(containsRelationship);
		List<String> verify = spdxFile.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		SpdxFile result = converter.convertAndStore(spdxFile);
		List<Relationship> resultRelationships = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		Object[] resultAnnotations = SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_ANNOTATION, DEFAULT_PREFIX, DEFAULT_PREFIX).toArray();
		assertEquals(1, resultAnnotations.length);
		assertTrue(resultAnnotations[0] instanceof Annotation);
		Annotation resultAnnotation = (Annotation)resultAnnotations[0];
		assertEquals(annotationType.toString(), resultAnnotation.getAnnotationType().toString());
		assertEquals(annotationComment, resultAnnotation.getStatement().get());
		assertEquals(result.getObjectUri(), resultAnnotation.getSubject().getObjectUri());
		assertEquals(fileComment + ";" + licenseComments, result.getComment().get());
		assertEquals(fileCopyright, result.getCopyrightText().get());
		List<Relationship> fileDeclaredLicRelationships = findRelationship(resultRelationships, RelationshipType.HAS_DECLARED_LICENSE, result.getObjectUri(), null);
		assertEquals(1, fileDeclaredLicRelationships.size());
		AnyLicenseInfo[] fileDeclaredLicResults = fileDeclaredLicRelationships.get(0).getTos().toArray(new AnyLicenseInfo[fileDeclaredLicRelationships.get(0).getTos().size()]);
		assertEquals(1, fileDeclaredLicResults.length);
		assertEquals(fileSeenLicenseStr, fileDeclaredLicResults[0].toString());
		List<Relationship> fileConcludedLicRelationships = findRelationship(resultRelationships, RelationshipType.HAS_CONCLUDED_LICENSE, result.getObjectUri(), null);
		assertEquals(1, fileConcludedLicRelationships.size());
		AnyLicenseInfo[] fileConcludedLicResults = fileConcludedLicRelationships.get(0).getTos().toArray(new AnyLicenseInfo[fileConcludedLicRelationships.get(0).getTos().size()]);
		assertEquals(1, fileConcludedLicResults.length);
		assertEquals(fileConcludedLicenseStr, fileConcludedLicResults[0].toString());
		assertEquals(fileName, result.getName().get());
		String[] attributionTexts = result.getAttributionTexts().toArray(new String[result.getAttributionTexts().size()]);
		assertEquals(1, attributionTexts.length);
		assertEquals(noticeText, attributionTexts[0]);
		List<Relationship> allFileRelationships = findRelationship(resultRelationships, null, result.getObjectUri(), null);
		assertEquals(3, allFileRelationships.size());
		Relationship containsnRelationship = null;
		for (Relationship relationship:allFileRelationships) {
			if (relationship.getRelationshipType().equals(RelationshipType.CONTAINS)) {
				containsnRelationship = relationship;
			}
		}
		assertTrue(Objects.nonNull(containsnRelationship));
		Element[] containsElements = containsnRelationship.getTos().toArray(new Element[containsnRelationship.getTos().size()]);
		assertEquals(1, containsElements.length);
		assertEquals(pkgName, containsElements[0].getName().get());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.SpdxPackage)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreSpdxPackage() throws InvalidSPDXAnalysisException {
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		
		String created = "2010-01-29T18:30:22Z";
		
		org.spdx.library.model.v2.enumerations.AnnotationType annotationType = 
				org.spdx.library.model.v2.enumerations.AnnotationType.REVIEW;
		String annotationComment = "Annotation comment";
		
		String pkgName = "my package";
		String pkgCopyright = "Package copyright";
		String pkgConcludedLicenseStr = "Apache-2.0";
		String pkgLicenseDeclaredStr = "NOASSERTION";
		String pkgDownloadLocation = "https://github.com/spdx/tools-java/releases/tag/v1.1.8";
		String builtDate = "2023-01-29T18:30:22Z";
		String releaseDate = "2022-01-29T18:30:22Z";
		String validUntil = "2025-01-29T18:30:22Z";
		String pkgComment = "pkgComment";
		String pkgDescription = "pkgDescription";
		String pkgHomePage = "https://pkg/home/page";
		String pkgLicenseComments = "pkgLicenseComments";
		String organizationCreatorName = "Source Auditor Inc.";
		String organizationCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_ORGANIZATION + organizationCreatorName;
		String pkgFileName = "/root/some/file";
		org.spdx.library.model.v2.enumerations.Purpose primaryPurpose = 
				org.spdx.library.model.v2.enumerations.Purpose.APPLICATION;
		String pkgSourceInfo = "pkgSourceInfo";
		String summary = "summary";
		String pkgVersion = "1.2.3";
		String pkgSha1 = "dedec01f93cf99c15097c0cdf107a28466b83913";
		
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		String verificationCodeValue = "ddcdc01f93cf99c15097c0cdf107a28466b83913";
		String skippedFile = "./some/skipped/file";
		String fileName = "File name";
		String fileConcludedLicenseStr = "MIT";
		String fileSeenLicenseStr = "NONE";
		String fileCopyright = "Copyright my file";
		String fileChecksumValue = "c3cdc01f93cf99c15097c0cdf107a28466b83913";
		
		org.spdx.library.model.v2.enumerations.RelationshipType dependsRelationshipType = 
				org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDS_ON;
		String relationshipComment = "Relationship comment";
		
		org.spdx.library.model.v2.SpdxPackage pkg = 
				new org.spdx.library.model.v2.SpdxPackage(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.SpdxId),
						copyManager, true);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo pkgLicenseDeclared = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(pkgLicenseDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		pkg.setLicenseConcluded(pkgConcludedLicense);
		pkg.setName(pkgName);
		pkg.setCopyrightText(pkgCopyright);
		pkg.setLicenseDeclared(pkgLicenseDeclared);
		org.spdx.library.model.v2.Annotation annotation = pkg.createAnnotation(personCreator, annotationType, 
				created, annotationComment);
		pkg.addAnnotation(annotation);
		pkg.setBuiltDate(builtDate);
		pkg.setComment(pkgComment);
		pkg.setDescription(pkgDescription);
		pkg.setDownloadLocation(pkgDownloadLocation);
		pkg.setFilesAnalyzed(true);
		pkg.setHomepage(pkgHomePage);
		pkg.setLicenseComments(pkgLicenseComments);
		pkg.setOriginator(organizationCreator);
		pkg.setPackageFileName(pkgFileName);
		org.spdx.library.model.v2.SpdxPackageVerificationCode pkgVerificationCode = 
				pkg.createPackageVerificationCode(verificationCodeValue, Arrays.asList(new String[] {skippedFile}));
		pkg.setPackageVerificationCode(pkgVerificationCode);
		pkg.setPrimaryPurpose(primaryPurpose);
		pkg.setReleaseDate(releaseDate);
		pkg.setSourceInfo(pkgSourceInfo);
		pkg.setSummary(summary);
		pkg.setSupplier(personCreator);
		pkg.setValidUntilDate(validUntil);
		pkg.setVersionInfo(pkgVersion);
		org.spdx.library.model.v2.Checksum pkgChecksum = pkg.createChecksum(sha1Algorithm, pkgSha1);
		pkg.getChecksums().add(pkgChecksum);
		
		String fileId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileSeenLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileSeenLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.Checksum fileSha1 = pkg.createChecksum(sha1Algorithm, fileChecksumValue);
		org.spdx.library.model.v2.SpdxFile spdxFile = pkg.createSpdxFile(fileId, fileName, fileConcludedLicense, 
				Arrays.asList(new org.spdx.library.model.v2.license.AnyLicenseInfo[] {fileSeenLicense}), fileCopyright, fileSha1)
				.build();
		org.spdx.library.model.v2.Relationship dependsRelationship = pkg.createRelationship(spdxFile, dependsRelationshipType, relationshipComment);
		pkg.addRelationship(dependsRelationship);
		pkg.getFiles().add(spdxFile);
		List<String> verify = pkg.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		SpdxPackage result = converter.convertAndStore(pkg);
		List<Relationship> resultRelationships = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		Object[] resultAnnotations = SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_ANNOTATION, DEFAULT_PREFIX, DEFAULT_PREFIX).toArray();
		assertEquals(1, resultAnnotations.length);
		assertTrue(resultAnnotations[0] instanceof Annotation);
		Annotation resultAnnotation = (Annotation)resultAnnotations[0];
		assertEquals(annotationType.toString(), resultAnnotation.getAnnotationType().toString());
		assertEquals(annotationComment, resultAnnotation.getStatement().get());
		assertEquals(result.getObjectUri(), resultAnnotation.getSubject().getObjectUri());
		
		assertEquals(pkgComment + ";" + pkgLicenseComments, result.getComment().get());
		List<Relationship> pkgDeclaredLicRelationships = findRelationship(resultRelationships, RelationshipType.HAS_DECLARED_LICENSE, result.getObjectUri(), null);
		assertEquals(1, pkgDeclaredLicRelationships.size());
		AnyLicenseInfo[] pkgDeclaredLicResults = pkgDeclaredLicRelationships.get(0).getTos().toArray(new AnyLicenseInfo[pkgDeclaredLicRelationships.get(0).getTos().size()]);
		assertEquals(1, pkgDeclaredLicResults.length);
		assertEquals(pkgLicenseDeclaredStr, pkgDeclaredLicResults[0].toString());
		List<Relationship> pkgConcludedLicenseRelationships = findRelationship(resultRelationships, RelationshipType.HAS_CONCLUDED_LICENSE, result.getObjectUri(), null);
		assertEquals(1, pkgConcludedLicenseRelationships.size());
		AnyLicenseInfo[] pkgConcludedLicResults = pkgConcludedLicenseRelationships.get(0).getTos().toArray(new AnyLicenseInfo[pkgConcludedLicenseRelationships.get(0).getTos().size()]);
		assertEquals(1, pkgConcludedLicResults.length);
		assertEquals(pkgConcludedLicenseStr, pkgConcludedLicResults[0].toString());
		assertEquals(pkgName, result.getName().get());
		assertEquals(pkgCopyright, result.getCopyrightText().get());
		assertEquals(builtDate, result.getBuiltTime().get());
		assertEquals(pkgDescription, result.getDescription().get());
		assertEquals(pkgDownloadLocation, result.getDownloadLocation().get());
		assertEquals(pkgHomePage, result.getHomePage().get());
		Agent[] originators = result.getOriginatedBys().toArray(new Agent[result.getOriginatedBys().size()]);
		assertEquals(1, originators.length);
		assertEquals(organizationCreatorName, originators[0].getName().get());
		List<Relationship> pkgFileNameRels = findRelationship(resultRelationships, RelationshipType.HAS_DISTRIBUTION_ARTIFACT, result.getObjectUri(), null);
		assertEquals(1, pkgFileNameRels.size());
		SpdxFile[] resultPkgFileNames = pkgFileNameRels.get(0).getTos().toArray(new SpdxFile[pkgFileNameRels.get(0).getTos().size()]);
		assertEquals(1, resultPkgFileNames.length);
		assertEquals(pkgFileName, resultPkgFileNames[0].getName().get());
		IntegrityMethod[] fileVerifiedUsing = resultPkgFileNames[0].getVerifiedUsings().toArray(
				new IntegrityMethod[resultPkgFileNames[0].getVerifiedUsings().size()]);
		assertEquals(1, fileVerifiedUsing.length);
		assertTrue(fileVerifiedUsing[0] instanceof Hash);
		assertEquals(HashAlgorithm.SHA1, ((Hash)fileVerifiedUsing[0]).getAlgorithm());
		assertEquals(pkgSha1, ((Hash)fileVerifiedUsing[0]).getHashValue());
		IntegrityMethod[] resultVerificationCodes = result.getVerifiedUsings().toArray(new IntegrityMethod[result.getVerifiedUsings().size()]);
		assertEquals(1, resultVerificationCodes.length);
		assertTrue(resultVerificationCodes[0] instanceof PackageVerificationCode);
		assertEquals(verificationCodeValue, ((PackageVerificationCode)resultVerificationCodes[0]).getHashValue());
		assertEquals(HashAlgorithm.SHA1, ((PackageVerificationCode)resultVerificationCodes[0]).getAlgorithm());
		String[] resultExcluded = ((PackageVerificationCode)resultVerificationCodes[0]).getPackageVerificationCodeExcludedFiles().toArray(
				new String[((PackageVerificationCode)resultVerificationCodes[0]).getPackageVerificationCodeExcludedFiles().size()]);
		assertEquals(1, resultExcluded.length);
		assertEquals(skippedFile, resultExcluded[0]);
		assertEquals(primaryPurpose.toString(), result.getPrimaryPurpose().get().toString());
		assertEquals(releaseDate, result.getReleaseTime().get());
		assertEquals(pkgSourceInfo, result.getSourceInfo().get());
		assertEquals(summary, result.getSummary().get());
		assertEquals(personCreatorName, result.getSuppliedBy().get().getName().get());
		assertEquals(validUntil, result.getValidUntilTime().get());
		assertEquals(pkgVersion, result.getPackageVersion().get());
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#stringToAgent(java.lang.String, org.spdx.library.model.v3_0_1.core.CreationInfo)}.
	 */
	@Test
	public void testStringToAgent() {
		// Tested as part of the testConvertCreationInfo
	}

	/**
	 * Test method for {@link org.spdx.library.conversion.Spdx2to3Converter#convertAndStore(org.spdx.library.model.v2.SpdxSnippet)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	@Test
	public void testConvertAndStoreSpdxSnippet() throws InvalidSPDXAnalysisException {
		String fileName = "File name";
		String fileConcludedLicenseStr = "MIT";
		String fileSeenLicenseStr = "NONE";
		String fileCopyright = "Copyright my file";
		String fileChecksumValue = "c3cdc01f93cf99c15097c0cdf107a28466b83913";
		org.spdx.library.model.v2.enumerations.ChecksumAlgorithm sha1Algorithm = 
				org.spdx.library.model.v2.enumerations.ChecksumAlgorithm.SHA1;
		
		org.spdx.library.model.v2.enumerations.AnnotationType annotationType = 
				org.spdx.library.model.v2.enumerations.AnnotationType.REVIEW;
		String annotationComment = "Annotation comment";
		String personCreatorName = "Gary O'Neall";
		String personCreatorEmail = "gary@sourceauditor.com";
		String personCreator = SpdxConstantsCompatV2.CREATOR_PREFIX_PERSON + personCreatorName + " (" + personCreatorEmail + ")";
		String created = "2010-01-29T18:30:22Z";
		
		Integer fromByte = 1212;
		Integer toByte = 5050;
		String snippetComment = "snippetComment";
		String snippetCopyright = "snippetCopyright";
		String licenseComments = "licenseComments";
		String snippetConcludedStr = "MIT";
		String snippetDeclaredStr = "Apache-2.0";
		Integer startLine = 5;
		Integer endLine = 33;
		String snippetName = "snippetName";
		org.spdx.library.model.v2.enumerations.RelationshipType snippetRelationshipType = 
				org.spdx.library.model.v2.enumerations.RelationshipType.DEPENDS_ON;
		
		String snippetId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.SpdxSnippet snippet = new 
				org.spdx.library.model.v2.SpdxSnippet(fromModelStore, DOCUMENT_URI,
						snippetId, copyManager, true);
		String fileId = fromModelStore.getNextId(IdType.SpdxId);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileConcludedLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileConcludedLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo fileSeenLicense = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(fileSeenLicenseStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.Checksum fileSha1 = snippet.createChecksum(sha1Algorithm, fileChecksumValue);
		org.spdx.library.model.v2.SpdxFile spdxFile = snippet.createSpdxFile(fileId, fileName, fileConcludedLicense, 
				Arrays.asList(new org.spdx.library.model.v2.license.AnyLicenseInfo[] {fileSeenLicense}), fileCopyright, fileSha1)
				.build();
		org.spdx.library.model.v2.Annotation annotation = snippet.createAnnotation(personCreator, annotationType, 
				created, annotationComment);
		org.spdx.library.model.v2.license.AnyLicenseInfo licenseConcluded =
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(snippetConcludedStr, fromModelStore, DOCUMENT_URI, copyManager);
		org.spdx.library.model.v2.license.AnyLicenseInfo licenseDeclared =
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2(snippetDeclaredStr, fromModelStore, DOCUMENT_URI, copyManager);
		snippet.getLicenseInfoFromFiles().add(licenseDeclared);
		snippet.setLicenseConcluded(licenseConcluded);
		snippet.setSnippetFromFile(spdxFile);
		snippet.setLineRange(startLine, endLine);
		snippet.setName(snippetName);
		org.spdx.library.model.v2.Relationship snippetRelationship = snippet.createRelationship(spdxFile, snippetRelationshipType, null);
		snippet.addRelationship(snippetRelationship);

		snippet.addAnnotation(annotation);
		snippet.setByteRange(fromByte, toByte);
		snippet.setComment(snippetComment);
		snippet.setCopyrightText(snippetCopyright);
		snippet.setLicenseComments(licenseComments);
		List<String> verify = snippet.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		Snippet result = converter.convertAndStore(snippet);
		
		List<Relationship> resultRelationships = new ArrayList<>();
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_LIFECYCLE_SCOPED_RELATIONSHIP, DEFAULT_PREFIX, DEFAULT_PREFIX).forEach(rel -> resultRelationships.add((Relationship)rel));
		Object[] resultAnnotations = SpdxModelFactory.getSpdxObjects(toModelStore, copyManager, SpdxConstantsV3.CORE_ANNOTATION, DEFAULT_PREFIX, DEFAULT_PREFIX).toArray();
		assertEquals(1, resultAnnotations.length);
		assertTrue(resultAnnotations[0] instanceof Annotation);
		Annotation resultAnnotation = (Annotation)resultAnnotations[0];
		assertEquals(annotationType.toString(), resultAnnotation.getAnnotationType().toString());
		assertEquals(annotationComment, resultAnnotation.getStatement().get());
		
		assertEquals(fromByte, result.getByteRange().get().getBeginIntegerRange());
		assertEquals(toByte, result.getByteRange().get().getEndIntegerRange());
		String resultComment = result.getComment().get();
		assertEquals(snippetComment + ";" + licenseComments, resultComment);
		assertEquals(snippetCopyright, result.getCopyrightText().get());
		List<Relationship> snippetConcludedRel = findRelationship(resultRelationships, RelationshipType.HAS_CONCLUDED_LICENSE, result.getObjectUri(), null);
		assertEquals(1, snippetConcludedRel.size());
		AnyLicenseInfo[] snippetConcludedLicResults = snippetConcludedRel.get(0).getTos().toArray(new AnyLicenseInfo[snippetConcludedRel.get(0).getTos().size()]);
		assertEquals(1, snippetConcludedLicResults.length);
		assertEquals(snippetConcludedStr, snippetConcludedLicResults[0].toString());
		List<Relationship> snippetDeclaredRel = findRelationship(resultRelationships, RelationshipType.HAS_DECLARED_LICENSE, result.getObjectUri(), null);
		assertEquals(1, snippetDeclaredRel.size());
		AnyLicenseInfo[] snippetDeclaredLicResults = snippetDeclaredRel.get(0).getTos().toArray(new AnyLicenseInfo[snippetDeclaredRel.get(0).getTos().size()]);
		assertEquals(1, snippetDeclaredLicResults.length);
		assertEquals(snippetDeclaredStr, snippetDeclaredLicResults[0].toString());
		assertEquals(startLine, result.getLineRange().get().getBeginIntegerRange());
		assertEquals(endLine, result.getLineRange().get().getEndIntegerRange());
		assertEquals(snippetName, result.getName().get());
		List<Relationship> allSnippetRelationships = findRelationship(resultRelationships, null, result.getObjectUri(), null);
		SpdxFile resultFromFile = result.getSnippetFromFile();
		assertEquals(fileName, resultFromFile.getName().get());
		assertEquals(3, allSnippetRelationships.size());
		Relationship dependsOnRelationship = null;
		for (Relationship relationship:allSnippetRelationships) {
			if (relationship.getRelationshipType().equals(RelationshipType.DEPENDS_ON)) {
				dependsOnRelationship = relationship;
			}
		}
		assertTrue(Objects.nonNull(dependsOnRelationship));
		Element[] documentationTos = dependsOnRelationship.getTos().toArray(new Element[dependsOnRelationship.getTos().size()]);
		assertEquals(1, documentationTos.length);
		assertEquals(resultFromFile, documentationTos[0]);
		verify = snippet.verify();
		assertTrue(verify.isEmpty());
	}

	@Test
	public void testConvertAndStoreLicenseAddition() throws InvalidSPDXAnalysisException {
		String exceptionId = "ExceptionId";
		String exceptionComment = "exceptionComment";
		Boolean exceptionDeprecated = true;
		String exceptionDeprecatedVersion = "v3.24.0";
		String exceptionTextHtml = "exceptionTextHtml";
		String exceptionTemplate = "exceptionTemplate";
		String exceptionName = "exceptionName";
		String seeAlso1 = "https://over/here";
		String seeAlso2 = "https://over/here2";
		List<String> exceptionSeeAlsos = Arrays.asList(new String[] {seeAlso1, seeAlso2});
		String exceptionText = "exceptionText";
		
		org.spdx.library.model.v2.license.ListedLicenseException licException = 
				new org.spdx.library.model.v2.license.ListedLicenseException(fromModelStore, DOCUMENT_URI,
						exceptionId, copyManager, true);
		licException.setComment(exceptionComment);
		licException.setDeprecated(exceptionDeprecated);
		licException.setDeprecatedVersion(exceptionDeprecatedVersion);
		licException.setExceptionTextHtml(exceptionTextHtml);
		licException.setLicenseExceptionTemplate(exceptionTemplate);
		licException.setLicenseExceptionText(exceptionText);
		licException.setName(exceptionName);
		licException.setSeeAlso(exceptionSeeAlsos);
		List<String> verify = licException.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		LicenseAddition result = converter.convertAndStore((org.spdx.library.model.v2.license.LicenseException)licException);
		assertTrue(result instanceof ListedLicenseException);
		assertEquals(exceptionComment, result.getComment().get());
		assertEquals(exceptionDeprecated, ((ListedLicenseException)result).getIsDeprecatedAdditionId().get());
		assertEquals(exceptionDeprecatedVersion, ((ListedLicenseException)result).getDeprecatedVersion().get());
		assertEquals(exceptionTemplate, ((ListedLicenseException)result).getStandardAdditionTemplate().get());
		assertEquals(exceptionText, result.getAdditionText());
		assertEquals(exceptionName, result.getName().get());
		assertEquals(exceptionSeeAlsos.size(), result.getSeeAlsos().size());
		assertTrue(result.getSeeAlsos().containsAll(exceptionSeeAlsos));
		verify = result.verify();
		assertTrue(verify.isEmpty());
	}
	
	@Test
	public void testConvertToLicenseExpression() throws InvalidSPDXAnalysisException {
		String extractedText = "Extracted text1";
		String extractedLicName = "name";
		String extractedText2 = "Extracted text2";
		String extractedLicName2 = "name2";
		String licId1 = fromModelStore.getNextId(IdType.LicenseRef);
		String licId2 = fromModelStore.getNextId(IdType.LicenseRef);
		org.spdx.library.model.v2.license.ExtractedLicenseInfo lic1 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, licId1,
						copyManager, true);
		lic1.setName(extractedLicName);
		lic1.setExtractedText(extractedText);
		org.spdx.library.model.v2.license.AnyLicenseInfo lic2 = 
				LicenseInfoFactory.parseSPDXLicenseStringCompatV2("Apache-2.0", fromModelStore, DOCUMENT_URI, copyManager);
		
		org.spdx.library.model.v2.license.ConjunctiveLicenseSet ands = 
				new org.spdx.library.model.v2.license.ConjunctiveLicenseSet(fromModelStore, DOCUMENT_URI, fromModelStore.getNextId(IdType.Anonymous),
						copyManager, true);
		org.spdx.library.model.v2.license.ExtractedLicenseInfo lic3 = 
				new org.spdx.library.model.v2.license.ExtractedLicenseInfo(fromModelStore, DOCUMENT_URI, licId2,
						copyManager, true);
		lic3.setName(extractedLicName2);
		lic3.setExtractedText(extractedText2);
		ands.addMember(lic1);
		ands.addMember(lic2);
		ands.addMember(lic3);
		List<String> verify = ands.verify();
		assertTrue(verify.isEmpty());
		
		Spdx2to3Converter converter = new Spdx2to3Converter(toModelStore, copyManager, defaultCreationInfo, 
				SpdxModelFactory.getLatestSpecVersion(), DEFAULT_PREFIX, true);
		
		LicenseExpression result = converter.convertToLicenseExpression(ands);
		Map<String, String> expected = new HashMap<>();
		expected.put(licId1, DEFAULT_PREFIX + licId1);
		expected.put(licId2, DEFAULT_PREFIX + licId2);
		for (DictionaryEntry entry:result.getCustomIdToUris()) {
			String id = entry.getKey();
			String uri = entry.getValue().get();
			assertTrue(expected.containsKey(id));
			assertEquals(expected.get(id), uri);
			expected.remove(id);
		}
		assertTrue(expected.isEmpty());
		assertEquals(ands.toString(), result.getLicenseExpression());
	}

}
