package org.spdx.library.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class SpdxModelFactoryTest extends TestCase {
	
	static final String DOCUMENT_URI = "http://www.spdx.org/documents";
	static final String ID1 = SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "1";
	static final String ID2 = SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "2";
	
	IModelStore modelStore;
	ModelCopyManager copyManager;
	

	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore();
		copyManager = new ModelCopyManager();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateSpdxDocument() throws InvalidSPDXAnalysisException {
		SpdxDocument result = SpdxModelFactory.createSpdxDocument(modelStore, DOCUMENT_URI, copyManager);
		assertEquals(SpdxConstants.SPDX_DOCUMENT_ID, result.getId());
	}

	public void testCreateModelObject() throws InvalidSPDXAnalysisException {
		ModelObject result = SpdxModelFactory.createModelObject(modelStore, DOCUMENT_URI, ID1, 
				SpdxConstants.CLASS_SPDX_CHECKSUM, copyManager);
		assertTrue(result instanceof Checksum);
		assertEquals(ID1, result.getId());
	}

	public void testGetModelObjectIModelStoreStringStringStringModelCopyManagerBoolean() throws InvalidSPDXAnalysisException {
		ModelObject result = SpdxModelFactory.getModelObject(modelStore, DOCUMENT_URI, ID1, 
				SpdxConstants.CLASS_SPDX_CHECKSUM, copyManager, true);
		assertTrue(result instanceof Checksum);
		assertEquals(ID1, result.getId());
		ModelObject result2 = SpdxModelFactory.getModelObject(modelStore, DOCUMENT_URI, ID1, 
				SpdxConstants.CLASS_SPDX_CHECKSUM, copyManager, false);
		assertTrue(result2 instanceof Checksum);
		assertEquals(ID1, result2.getId());
		try {
			result = SpdxModelFactory.getModelObject(modelStore, DOCUMENT_URI, ID2, 
					SpdxConstants.CLASS_SPDX_CHECKSUM, copyManager, false);
			fail("Expected id not found exception");
		} catch(SpdxIdNotFoundException ex) {
			// expected
		}
	}

	public void testTypeToClass() throws InvalidSPDXAnalysisException {
		assertEquals(Checksum.class, SpdxModelFactory.typeToClass(SpdxConstants.CLASS_SPDX_CHECKSUM));
		assertEquals(SpdxFile.class, SpdxModelFactory.typeToClass(SpdxConstants.CLASS_SPDX_FILE));
	}

	@SuppressWarnings("unchecked")
	public void testGetElements() throws InvalidSPDXAnalysisException {
		ModelObject file1 = SpdxModelFactory.createModelObject(modelStore, DOCUMENT_URI, ID1, 
				SpdxConstants.CLASS_SPDX_FILE, copyManager);
		ModelObject file2 = SpdxModelFactory.createModelObject(modelStore, DOCUMENT_URI, ID2, 
				SpdxConstants.CLASS_SPDX_FILE, copyManager);
		for (SpdxElement element:(List<SpdxElement>)(SpdxModelFactory.getElements(modelStore, DOCUMENT_URI, copyManager, SpdxFile.class).collect(Collectors.toList()))) {
			
			assertTrue(element instanceof SpdxFile);
			SpdxFile result = (SpdxFile)element;
			if (result.getId().equals(ID1)) {
				try {
					assertTrue(file1.equivalent(result));
				} catch (InvalidSPDXAnalysisException e) {
					fail("Error: "+e.getMessage());
				}
			} else {
				try {
					assertTrue(file2.equivalent(result));
				} catch (InvalidSPDXAnalysisException e) {
					fail("Error: "+e.getMessage());
				}
			}
		}
	}

	public void testClassUriToClass() throws InvalidSPDXAnalysisException {
		assertEquals(Annotation.class, 
				SpdxModelFactory.classUriToClass(SpdxConstants.SPDX_NAMESPACE + SpdxConstants.CLASS_ANNOTATION));
	}

	public void testGetModelObjectIModelStoreStringStringModelCopyManager() throws InvalidSPDXAnalysisException {
		ModelObject result = SpdxModelFactory.getModelObject(modelStore, DOCUMENT_URI, ID1, 
				SpdxConstants.CLASS_SPDX_CHECKSUM, copyManager, true);
		assertTrue(result instanceof Checksum);
		assertEquals(ID1, result.getId());
		Optional<ModelObject> result2 = SpdxModelFactory.getModelObject(modelStore, DOCUMENT_URI, ID1, copyManager);
		assertTrue(result2.isPresent());
		assertTrue(result2.get() instanceof Checksum);
		assertEquals(ID1, result2.get().getId());
		result2 = SpdxModelFactory.getModelObject(modelStore, DOCUMENT_URI, ID2, copyManager);
		assertFalse(result2.isPresent());
	}

}
