package org.spdx.library.model.compat.v2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SimpleUriValue;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.TypedValue;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.compat.v2.enumerations.AnnotationType;
import org.spdx.library.model.compat.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.compat.v2.enumerations.RelationshipType;
import org.spdx.library.model.compat.v2.license.ExternalExtractedLicenseInfo;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.compat.v2.CompatibleModelStoreWrapper;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class ModelStorageClassConverterTest extends TestCase {
	
	GenericModelObject gmo;

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset(SpdxMajorVersion.VERSION_2);
		gmo = new GenericModelObject();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testStoredObjectToModelObject() throws InvalidSPDXAnalysisException {
		// TypedValue
		TypedValue tv = new TypedValue("SPDXRef-10", SpdxConstantsCompatV2.CLASS_ANNOTATION);
		Object result = ModelStorageClassConverter.storedObjectToModelObject(tv, gmo.getDocumentUri(), 
				gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result instanceof Annotation);
		assertEquals(tv.getObjectUri(), ((Annotation)result).getId());
		// Enum
		SimpleUriValue suv = new SimpleUriValue(ChecksumAlgorithm.MD5);
		result = ModelStorageClassConverter.storedObjectToModelObject(suv, gmo.getDocumentUri(), 
				gmo.getModelStore(), gmo.getCopyManager());
		assertEquals(ChecksumAlgorithm.MD5, result);
		// ExternalElementRef
		new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getCopyManager(), true);
		String externalDocUri = "http://externalDoc";
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.SHA1, "A94A8FE5CCB19BA61C4C0873D391E987982FBBD3");
		gmo.createExternalDocumentRef(gmo.getModelStore().getNextId(IdType.DocumentRef, gmo.getDocumentUri()),
				externalDocUri, checksum);
		String externalDocElementId = SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM + "11";
		String externalRefUri = externalDocUri + "#" + externalDocElementId;
		suv = new SimpleUriValue(externalRefUri);
		result = ModelStorageClassConverter.storedObjectToModelObject(suv, gmo.getDocumentUri(), 
				gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result instanceof ExternalSpdxElement);
		ExternalSpdxElement external = (ExternalSpdxElement)result;
		assertTrue(external.getId().contains(externalDocElementId));
		// ExternalLicenseRef
		String externalLicenseRefId = SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + "525";
		String externalLicenseRefUri = externalDocUri + "#" + externalLicenseRefId;
		suv = new SimpleUriValue(externalLicenseRefUri);
		result = ModelStorageClassConverter.storedObjectToModelObject(suv, gmo.getDocumentUri(), 
				gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result instanceof ExternalExtractedLicenseInfo);
		ExternalExtractedLicenseInfo externalLic = (ExternalExtractedLicenseInfo)result;
		assertTrue(externalLic.getId().contains(externalLicenseRefId));
		// String
		String expected = "expected";
		result = ModelStorageClassConverter.storedObjectToModelObject(expected, gmo.getDocumentUri(), 
				gmo.getModelStore(), gmo.getCopyManager());
		assertEquals(expected, result);
		// Boolean
		Boolean b = true;
		result = ModelStorageClassConverter.storedObjectToModelObject(b, gmo.getDocumentUri(), 
				gmo.getModelStore(), gmo.getCopyManager());
		assertEquals(b, result);
		
	}

	public void testOptionalStoredObjectToModelObject() throws InvalidSPDXAnalysisException {
		// TypedValue
		TypedValue tv = new TypedValue("SPDXRef-10", SpdxConstantsCompatV2.CLASS_ANNOTATION);
		Optional<Object> result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(tv), gmo.getDocumentUri(), gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result.isPresent());
		assertTrue(result.get() instanceof Annotation);
		assertEquals(tv.getObjectUri(), ((Annotation)result.get()).getId());
		// Enum
		SimpleUriValue suv = new SimpleUriValue(ChecksumAlgorithm.MD5);
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(suv), gmo.getDocumentUri(), 
				gmo.getModelStore(), gmo.getCopyManager());
		assertEquals(ChecksumAlgorithm.MD5, result.get());
		// ExternalElementRef
		new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), gmo.getCopyManager(), true);
		String externalDocUri = "http://externalDoc";
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.SHA1, "A94A8FE5CCB19BA61C4C0873D391E987982FBBD3");
		gmo.createExternalDocumentRef(gmo.getModelStore().getNextId(IdType.DocumentRef, gmo.getDocumentUri()),
				externalDocUri, checksum);
		String externalDocElementId = SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM + "11";
		String externalRefUri = externalDocUri + "#" + externalDocElementId;
		suv = new SimpleUriValue(externalRefUri);
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(suv), gmo.getDocumentUri(), 
				gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result.get() instanceof ExternalSpdxElement);
		ExternalSpdxElement external = (ExternalSpdxElement)result.get();
		assertTrue(external.getId().contains(externalDocElementId));
		// String
		String expected = "expected";
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(expected), gmo.getDocumentUri(), gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result.isPresent());
		assertEquals(expected, result.get());
		// Boolean
		Boolean b = true;
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(b), gmo.getDocumentUri(), gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result.isPresent());
		assertEquals(b, result.get());
		// Empty
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.empty(), gmo.getDocumentUri(), gmo.getModelStore(), gmo.getCopyManager());
		assertFalse(result.isPresent());
	}

	public void testModelObjectToStoredObject() throws InvalidSPDXAnalysisException {
		// ModelObject
		Object result = ModelStorageClassConverter.modelObjectToStoredObject(gmo, gmo.getDocumentUri(), gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result instanceof TypedValue);
		assertEquals(gmo.getId(), ((TypedValue)result).getObjectUri());
		assertEquals(gmo.getType(), ((TypedValue)result).getType());
		// Uri value
		result = ModelStorageClassConverter.modelObjectToStoredObject(RelationshipType.BUILD_TOOL_OF, gmo.getDocumentUri(), gmo.getModelStore(), gmo.getCopyManager());
		assertTrue(result instanceof SimpleUriValue);
		assertEquals(RelationshipType.BUILD_TOOL_OF.getIndividualURI(), ((SimpleUriValue)result).getIndividualURI());
		// String
		String expected = "expected";
		result = ModelStorageClassConverter.modelObjectToStoredObject(expected, gmo.getDocumentUri(), gmo.getModelStore(), gmo.getCopyManager());
		assertEquals(expected, result);
		// Boolean
		Boolean b = true;
		result = ModelStorageClassConverter.storedObjectToModelObject(b, gmo.getDocumentUri(), gmo.getModelStore(), gmo.getCopyManager());
		assertEquals(b, result);
	}

	public void testCopyIModelStoreStringIModelStoreStringStringString() throws InvalidSPDXAnalysisException {
		IModelStore store1 = new InMemSpdxStore(SpdxMajorVersion.VERSION_2);
		IModelStore store2 = new InMemSpdxStore(SpdxMajorVersion.VERSION_2);
		ModelCopyManager copyManager = new ModelCopyManager();
		String docUri1 = "http://doc1/uri";
		String docUri2 = "http://doc2/uri";
		new SpdxDocument(store1, docUri1, copyManager, true);
		new SpdxDocument(store2, docUri2, copyManager, true);
		String id1 = "ID1";
		String id2 = "ID2";
		GenericSpdxItem element1 = new GenericSpdxItem(store1, docUri1, id1, copyManager, true);
		DateFormat format = new SimpleDateFormat(SpdxConstantsCompatV2.SPDX_DATE_FORMAT);
		String date = format.format(new Date());
		Annotation annotation = element1.createAnnotation("Person: Annotator", AnnotationType.REVIEW, date, "Annotation Comment");
		element1.addAnnotation(annotation);
		String externalUri = "http://doc3/uri#" + SpdxConstantsCompatV2.SPDX_ELEMENT_REF_PRENUM + "23";
		ExternalSpdxElement externalElement = ExternalSpdxElement.uriToExternalSpdxElement(externalUri, store1, docUri1, copyManager);
		String externalLicenseUri = "http://doc3/uri#" + SpdxConstantsCompatV2.NON_STD_LICENSE_ID_PRENUM + "55";
		Relationship relationship = element1.createRelationship(externalElement, RelationshipType.BUILD_TOOL_OF, "relationshipComment");
		element1.addRelationship(relationship);
		ExternalExtractedLicenseInfo externalLicense = ExternalExtractedLicenseInfo.uriToExternalExtractedLicense(externalLicenseUri, store1, docUri1, copyManager);
		element1.setLicenseConcluded(externalLicense);
		element1.setName("ElementName");
		copyManager.copy(store2, CompatibleModelStoreWrapper.documentUriIdToUri(docUri2, id2, false), store1, 
				CompatibleModelStoreWrapper.documentUriIdToUri(docUri1, id1, false), element1.getType(), 
				docUri2, docUri1, docUri2, docUri1);
		GenericSpdxItem element2 = new GenericSpdxItem(store2, docUri2, id2, copyManager, false);
		assertTrue(element1.equivalent(element2));
		assertTrue(element2.equivalent(element1));
	}

	public void testModelClassToStoredClass() {
		// ModelObject
		assertEquals(TypedValue.class, ModelStorageClassConverter.modelClassToStoredClass(GenericModelObject.class));
		// InvidiualUriValue
		assertEquals(SimpleUriValue.class, ModelStorageClassConverter.modelClassToStoredClass(ChecksumAlgorithm.MD5.getClass()));
		// Other
		assertEquals(String.class, ModelStorageClassConverter.modelClassToStoredClass("This".getClass()));
	}

}
