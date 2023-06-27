package org.spdx.library.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.storage.PropertyDescriptor;

import junit.framework.TestCase;

public class ModelCollectionTest extends TestCase {
	
	static final PropertyDescriptor PROPERTY_NAME = new PropertyDescriptor("property", "namespace");
	static final String[] ELEMENTS = new String[] {"e1", "e2", "e3", "e4"};
	
	//TODO: Change this to a version 3 GMO
	GenericModelObject gmo;

	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset(SpdxMajorVersion.VERSION_3);
		gmo = new GenericModelObject();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSize() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), null);
		for (String element:ELEMENTS) {
			mc.add(element);
		}
		assertEquals(ELEMENTS.length, mc.size());
	}

	public void testIsEmpty() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		assertTrue(mc.isEmpty());
		for (String element:ELEMENTS) {
			mc.add(element);
		}
		assertFalse(mc.isEmpty());
	}

	public void testContains() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		for (String element:ELEMENTS) {
			mc.add(element);
		}
		for (String element:ELEMENTS) {
			assertTrue(mc.contains(element));
		}
		assertFalse(mc.contains("not there"));
	}

	public void testToImmutableList() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		for (String element:ELEMENTS) {
			mc.add(element);
		}
		List<Object> result = mc.toImmutableList();
		assertEquals(ELEMENTS.length, result.size());
		for (String element:ELEMENTS) {
			assertTrue(result.contains(element));
		}
	}

	public void testAdd() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		assertEquals(0, mc.size());
		mc.add(ELEMENTS[0]);
		assertEquals(1, mc.size());
		assertTrue(mc.contains(ELEMENTS[0]));
		mc.add(ELEMENTS[1]);
		assertEquals(2, mc.size());
		assertTrue(mc.contains(ELEMENTS[0]));
		assertTrue(mc.contains(ELEMENTS[1]));
	}

	public void testRemove() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		for (String element:ELEMENTS) {
			mc.add(element);
		}
		assertEquals(ELEMENTS.length, mc.size());
		assertTrue(mc.contains(ELEMENTS[0]));
		mc.remove(ELEMENTS[0]);
		assertEquals(ELEMENTS.length-1, mc.size());
		assertFalse(mc.contains(ELEMENTS[0]));
	}

	public void testContainsAll() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		for (String element:ELEMENTS) {
			mc.add(element);
		}
		List<String> compare = new ArrayList<String>(Arrays.asList(ELEMENTS));
		assertTrue(mc.containsAll(compare));
		compare.add("Another");
		assertFalse(mc.containsAll(compare));
	}

	public void testAddAll() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		List<String> compare = new ArrayList<String>(Arrays.asList(ELEMENTS));
		mc.addAll(compare);
		
		assertEquals(ELEMENTS.length, mc.size());
		for (String element:ELEMENTS) {
			assertTrue(mc.contains(element));
		}
	}

	public void testRemoveAll() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		List<String> list1 = Arrays.asList(ELEMENTS);
		List<String> list2 = new ArrayList<String>(list1);
		String addedElement = "added";
		list2.add(addedElement);
		mc.addAll(list2);
		mc.removeAll(list1);
		assertEquals(1, mc.size());
		assertTrue(mc.contains(addedElement));
	}

	public void testRetainAll() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		List<String> list1 = Arrays.asList(ELEMENTS);
		List<String> list2 = new ArrayList<String>(list1);
		String addedElement = "added";
		list2.add(addedElement);
		mc.addAll(list2);
		assertEquals(list2.size(), mc.size());
		mc.retainAll(list1);
		assertEquals(list1.size(), mc.size());
		for (String s:list1) {
			assertTrue(mc.contains(s));
		}
	}

	public void testClear() throws InvalidSPDXAnalysisException {
		ModelCollection<String> mc = new ModelCollection<String>(gmo.getModelStore(), gmo.getObjectUri(), PROPERTY_NAME, gmo.getCopyManager(), String.class);
		for (String element:ELEMENTS) {
			mc.add(element);
		}
		assertEquals(ELEMENTS.length, mc.size());
		mc.clear();
		assertEquals(0, mc.size());
	}

}
