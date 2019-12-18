package org.spdx.library.model;

import java.util.Optional;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

public class ModelStorageClassConverterTest extends TestCase {
	
	GenericModelObject gmo;

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testStoredObjectToModelObject() throws InvalidSPDXAnalysisException {
		// TypedValue
		TypedValue tv = new TypedValue("SPDXRef-10", SpdxConstants.CLASS_ANNOTATION);
		Object result = ModelStorageClassConverter.storedObjectToModelObject(tv, gmo.getDocumentUri(), 
				gmo.getModelStore());
		assertTrue(result instanceof Annotation);
		assertEquals(tv.getId(), ((Annotation)result).getId());
		// Enum
		SimpleUriValue suv = new SimpleUriValue(ChecksumAlgorithm.MD5);
		result = ModelStorageClassConverter.storedObjectToModelObject(suv, gmo.getDocumentUri(), 
				gmo.getModelStore());
		assertEquals(ChecksumAlgorithm.MD5, result);
		// ExternalElementRef
		new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), true);
		String externalDocUri = "http://externalDoc";
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.SHA1, "A94A8FE5CCB19BA61C4C0873D391E987982FBBD3");
		ExternalDocumentRef ref = gmo.createExternalDocumentRef(gmo.getModelStore().getNextId(IdType.DocumentRef, gmo.getDocumentUri()),
				externalDocUri, checksum);
		String externalDocElementId = SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "11";
		String externalRefUri = externalDocUri + "#" + externalDocElementId;
		suv = new SimpleUriValue(externalRefUri);
		result = ModelStorageClassConverter.storedObjectToModelObject(suv, gmo.getDocumentUri(), 
				gmo.getModelStore());
		assertTrue(result instanceof ExternalSpdxElement);
		ExternalSpdxElement external = (ExternalSpdxElement)result;
		assertTrue(external.getId().contains(externalDocElementId));
		// String
		String expected = "expected";
		result = ModelStorageClassConverter.storedObjectToModelObject(expected, gmo.getDocumentUri(), 
				gmo.getModelStore());
		assertEquals(expected, result);
		// Boolean
		Boolean b = true;
		result = ModelStorageClassConverter.storedObjectToModelObject(b, gmo.getDocumentUri(), 
				gmo.getModelStore());
		assertEquals(b, result);
		
	}

	public void testOptionalStoredObjectToModelObject() throws InvalidSPDXAnalysisException {
		// TypedValue
		TypedValue tv = new TypedValue("SPDXRef-10", SpdxConstants.CLASS_ANNOTATION);
		Optional<Object> result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(tv), gmo.getDocumentUri(), gmo.getModelStore());
		assertTrue(result.isPresent());
		assertTrue(result.get() instanceof Annotation);
		assertEquals(tv.getId(), ((Annotation)result.get()).getId());
		// Enum
		SimpleUriValue suv = new SimpleUriValue(ChecksumAlgorithm.MD5);
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(suv), gmo.getDocumentUri(), 
				gmo.getModelStore());
		assertEquals(ChecksumAlgorithm.MD5, result.get());
		// ExternalElementRef
		new SpdxDocument(gmo.getModelStore(), gmo.getDocumentUri(), true);
		String externalDocUri = "http://externalDoc";
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.SHA1, "A94A8FE5CCB19BA61C4C0873D391E987982FBBD3");
		ExternalDocumentRef ref = gmo.createExternalDocumentRef(gmo.getModelStore().getNextId(IdType.DocumentRef, gmo.getDocumentUri()),
				externalDocUri, checksum);
		String externalDocElementId = SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "11";
		String externalRefUri = externalDocUri + "#" + externalDocElementId;
		suv = new SimpleUriValue(externalRefUri);
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(suv), gmo.getDocumentUri(), 
				gmo.getModelStore());
		assertTrue(result.get() instanceof ExternalSpdxElement);
		ExternalSpdxElement external = (ExternalSpdxElement)result.get();
		assertTrue(external.getId().contains(externalDocElementId));
		// String
		String expected = "expected";
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(expected), gmo.getDocumentUri(), gmo.getModelStore());
		assertTrue(result.isPresent());
		assertEquals(expected, result.get());
		// Boolean
		Boolean b = true;
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.of(b), gmo.getDocumentUri(), gmo.getModelStore());
		assertTrue(result.isPresent());
		assertEquals(b, result.get());
		// Empty
		result = ModelStorageClassConverter.optionalStoredObjectToModelObject(Optional.empty(), gmo.getDocumentUri(), gmo.getModelStore());
		assertFalse(result.isPresent());
	}

	public void testModelObjectToStoredObject() throws InvalidSPDXAnalysisException {
		// ModelObject
		Object result = ModelStorageClassConverter.modelObjectToStoredObject(gmo, gmo.getDocumentUri(), gmo.getModelStore(), false);
		assertTrue(result instanceof TypedValue);
		assertEquals(gmo.getId(), ((TypedValue)result).getId());
		assertEquals(gmo.getType(), ((TypedValue)result).getType());
		// Uri value
		result = ModelStorageClassConverter.modelObjectToStoredObject(RelationshipType.BUILD_TOOL_OF, gmo.getDocumentUri(), gmo.getModelStore(), false);
		assertTrue(result instanceof SimpleUriValue);
		assertEquals(RelationshipType.BUILD_TOOL_OF.getIndividualURI(), ((SimpleUriValue)result).getIndividualURI());
		// String
		String expected = "expected";
		result = ModelStorageClassConverter.modelObjectToStoredObject(expected, gmo.getDocumentUri(), gmo.getModelStore(), false);
		assertEquals(expected, result);
		// Boolean
		Boolean b = true;
		result = ModelStorageClassConverter.storedObjectToModelObject(b, gmo.getDocumentUri(), gmo.getModelStore());
		assertEquals(b, result);
	}

	public void testCopyIModelStoreStringIModelStoreStringStringString() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
	}

	public void testCopyIModelStoreStringStringIModelStoreStringStringString() throws InvalidSPDXAnalysisException {
		fail("Not yet implemented");
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
