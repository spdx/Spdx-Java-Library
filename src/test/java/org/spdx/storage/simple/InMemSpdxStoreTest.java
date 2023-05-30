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
package org.spdx.storage.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxIdInUseException;
import org.spdx.library.model.TypedValue;
import org.spdx.storage.PropertyDescriptor;
import org.spdx.storage.IModelStore.IModelStoreLock;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;
import net.jodah.concurrentunit.Waiter;

/**
 * @author Gary O'Neall
 *
 */
public class InMemSpdxStoreTest extends TestCase {
	
	static final Logger logger = LoggerFactory.getLogger(InMemSpdxStoreTest.class);
	
	static final long TIMEOUT = 10000;
	
	static final String TEST_DOCUMENT_URI1 = "http://test.document.uri/1";
	static final String TEST_DOCUMENT_URI2 = "http://test.document.uri/2";
	
	static final String TEST_ID1 = "id1";
	static final String TEST_ID2 = "id2";

	static final String TEST_TYPE1 = SpdxConstants.CLASS_ANNOTATION;
	static final String TEST_TYPE2 = SpdxConstants.CLASS_RELATIONSHIP;
	static final PropertyDescriptor[] TEST_VALUE_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("valueProp1", SpdxConstants.SPDX_NAMESPACE), 
			new PropertyDescriptor("valueProp2", SpdxConstants.SPDX_NAMESPACE), 
			new PropertyDescriptor("valueProp3", SpdxConstants.SPDX_NAMESPACE), 
			new PropertyDescriptor("valueProp4", SpdxConstants.SPDX_NAMESPACE)};
	static final Object[] TEST_VALUE_PROPERTY_VALUES = new Object[] {"value1", true, "value2", null};
	static final PropertyDescriptor[] TEST_LIST_PROPERTIES = new PropertyDescriptor[] {
			new PropertyDescriptor("listProp1", SpdxConstants.SPDX_NAMESPACE), 
			new PropertyDescriptor("listProp2", SpdxConstants.SPDX_NAMESPACE), 
			new PropertyDescriptor("listProp3", SpdxConstants.SPDX_NAMESPACE)};

	protected static final int MAX_RETRIES = 10;
	TypedValue[] TEST_TYPED_PROP_VALUES;
	ArrayList<?>[] TEST_LIST_PROPERTY_VALUES;
	
	String state;	// used to track state in the asynch tests (e.g. testTransaction)
	
	private synchronized String setTestState(String newState) {
		logger.info("Setting state to "+state);
		String retval = state;
		state = newState;
		return retval;
	}
	
	private synchronized String getTestState() {
		return state;
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		TEST_LIST_PROPERTY_VALUES = new ArrayList<?>[] {new ArrayList<>(Arrays.asList("ListItem1", "listItem2", "listItem3")), 
			new ArrayList<>(Arrays.asList(true, false, true)),
			new ArrayList<>(Arrays.asList(new TypedValue("typeId1", TEST_TYPE1), new TypedValue("typeId2", TEST_TYPE2)))};
			TEST_VALUE_PROPERTY_VALUES[3] = new TypedValue("typeId3", TEST_TYPE1);

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testUpdateNextIds() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		// License ID's
		String nextId = store.getNextId(IdType.LicenseRef, TEST_DOCUMENT_URI1);
		assertEquals("LicenseRef-gnrtd0", nextId);
		store.create(TEST_DOCUMENT_URI1, "LicenseRef-gnrtd33", SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		nextId = store.getNextId(IdType.LicenseRef, TEST_DOCUMENT_URI1);
		assertEquals("LicenseRef-gnrtd34", nextId);
		
		// SPDX ID's
		nextId = store.getNextId(IdType.SpdxId, TEST_DOCUMENT_URI1);
		assertEquals("SPDXRef-gnrtd0", nextId);
		store.create(TEST_DOCUMENT_URI1, "SPDXRef-gnrtd33", SpdxConstants.CLASS_SPDX_FILE);
		nextId = store.getNextId(IdType.SpdxId, TEST_DOCUMENT_URI1);
		assertEquals("SPDXRef-gnrtd34", nextId);
		
		// Anonymous ID's
		nextId = store.getNextId(IdType.Anonymous, TEST_DOCUMENT_URI1);
		assertEquals(InMemSpdxStore.ANON_PREFIX + "gnrtd0", nextId);
		store.create(TEST_DOCUMENT_URI1, InMemSpdxStore.ANON_PREFIX + "gnrtd33", SpdxConstants.CLASS_SPDX_CHECKSUM);
		nextId = store.getNextId(IdType.Anonymous, TEST_DOCUMENT_URI1);
		assertEquals(InMemSpdxStore.ANON_PREFIX + "gnrtd34", nextId);
		
		// Document ID's
		nextId = store.getNextId(IdType.DocumentRef, TEST_DOCUMENT_URI1);
		assertEquals(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "gnrtd0", nextId);
		store.create(TEST_DOCUMENT_URI1, SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "gnrtd33", SpdxConstants.CLASS_EXTERNAL_DOC_REF);
		nextId = store.getNextId(IdType.DocumentRef, TEST_DOCUMENT_URI1);
		assertEquals(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "gnrtd34", nextId);
	}
	
	public void testCreateExists() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		String id1 = "TestId1";
		String id2 = "testId2";
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id2));
		assertFalse(store.exists(TEST_DOCUMENT_URI2, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI2, id2));
		store.create(TEST_DOCUMENT_URI1, id1, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		assertTrue(store.exists(TEST_DOCUMENT_URI1, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id2));
		assertFalse(store.exists(TEST_DOCUMENT_URI2, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI2, id2));
		store.create(TEST_DOCUMENT_URI2, id2, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		assertTrue(store.exists(TEST_DOCUMENT_URI1, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id2));
		assertFalse(store.exists(TEST_DOCUMENT_URI2, id1));
		assertTrue(store.exists(TEST_DOCUMENT_URI2, id2));
		store.create(TEST_DOCUMENT_URI2, id1, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		assertTrue(store.exists(TEST_DOCUMENT_URI1, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id2));
		assertTrue(store.exists(TEST_DOCUMENT_URI2, id1));
		assertTrue(store.exists(TEST_DOCUMENT_URI2, id2));
	}

	public void testGetPropertyValueNames() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertEquals(0, store.getPropertyValueDescriptors(TEST_DOCUMENT_URI1, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueDescriptors(TEST_DOCUMENT_URI2, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueDescriptors(TEST_DOCUMENT_URI1, TEST_ID2).size());
		for (int i = 0; i < TEST_VALUE_PROPERTIES.length; i++) {
		    if (TEST_VALUE_PROPERTY_VALUES[i] instanceof TypedValue) {
		        TypedValue tv = (TypedValue)TEST_VALUE_PROPERTY_VALUES[i];
		        store.create(TEST_DOCUMENT_URI1, tv.getId(), tv.getType());
		    }
			store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[i], TEST_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
			    if (value instanceof TypedValue) {
			        TypedValue tv = (TypedValue)value;
			        store.create(TEST_DOCUMENT_URI1, tv.getId(), tv.getType());
			    }
				store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[i], value);
			}
		}
		List<PropertyDescriptor> result = store.getPropertyValueDescriptors(TEST_DOCUMENT_URI1, TEST_ID1);
		assertEquals(TEST_VALUE_PROPERTIES.length + TEST_LIST_PROPERTIES.length, result.size());
		for (PropertyDescriptor prop:TEST_VALUE_PROPERTIES) {
			assertTrue(result.contains(prop));
		}
		for (PropertyDescriptor prop:TEST_LIST_PROPERTIES) {
			assertTrue(result.contains(prop));
		}
		assertEquals(0, store.getPropertyValueDescriptors(TEST_DOCUMENT_URI2, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueDescriptors(TEST_DOCUMENT_URI1, TEST_ID2).size());		
	}
	
	
	public void testGetSetValue() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_VALUE_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_VALUE_PROPERTIES[0]).isPresent());
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0], TEST_VALUE_PROPERTY_VALUES[0]);
		assertEquals(TEST_VALUE_PROPERTY_VALUES[0], store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0], TEST_VALUE_PROPERTY_VALUES[1]);
		assertEquals(TEST_VALUE_PROPERTY_VALUES[1], store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_VALUE_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_VALUE_PROPERTIES[0]).isPresent());
	}
	
	public void testGetAddValueList() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
		String value1 = "value1";
		String value2 = "value2";
		assertTrue(store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1));
		assertTrue(store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2));
		assertEquals(2, toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).size());
		assertTrue(toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value1));
		assertTrue(toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value2));
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
	}
	
	static List<?> toImmutableList(Iterator<Object> listValues) {
		return (List<Object>) Collections.unmodifiableList(StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(listValues, Spliterator.ORDERED), false)
				.collect(Collectors.toList()));
	}

	public void testGetNextId() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		// License ID's
		String nextId = store.getNextId(IdType.LicenseRef, TEST_DOCUMENT_URI1);
		assertEquals("LicenseRef-gnrtd0", nextId);
		nextId = store.getNextId(IdType.LicenseRef, TEST_DOCUMENT_URI1);
		assertEquals("LicenseRef-gnrtd1", nextId);
		
		// SPDX ID's
		nextId = store.getNextId(IdType.SpdxId, TEST_DOCUMENT_URI1);
		assertEquals("SPDXRef-gnrtd0", nextId);
		nextId = store.getNextId(IdType.SpdxId, TEST_DOCUMENT_URI1);
		assertEquals("SPDXRef-gnrtd1", nextId);
		
		// Anonymous ID's
		nextId = store.getNextId(IdType.Anonymous, TEST_DOCUMENT_URI1);
		assertEquals(InMemSpdxStore.ANON_PREFIX + "gnrtd0", nextId);
		nextId = store.getNextId(IdType.Anonymous, TEST_DOCUMENT_URI1);
		assertEquals(InMemSpdxStore.ANON_PREFIX + "gnrtd1", nextId);
		
		// Document ID's
		nextId = store.getNextId(IdType.DocumentRef, TEST_DOCUMENT_URI1);
		assertEquals(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "gnrtd0", nextId);
		nextId = store.getNextId(IdType.DocumentRef, TEST_DOCUMENT_URI1);
		assertEquals(SpdxConstants.EXTERNAL_DOC_REF_PRENUM + "gnrtd1", nextId);
	}
	
	public void testRemoveProperty() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
		for (Object e:TEST_LIST_PROPERTY_VALUES[0]) {
			try {
				store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], e);
			} catch (InvalidSPDXAnalysisException e1) {
				fail(e1.getMessage());
			}
		}
		for (Object e:TEST_LIST_PROPERTY_VALUES[0]) {
			try {
				store.addValueToCollection(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0], e);
			} catch (InvalidSPDXAnalysisException e1) {
				fail(e1.getMessage());
			}
		}
		for (Object e:TEST_LIST_PROPERTY_VALUES[0]) {
			try {
				store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0], e);
			} catch (InvalidSPDXAnalysisException e1) {
				fail(e1.getMessage());
			}
		}

		assertCollectionsEquals(TEST_LIST_PROPERTY_VALUES[0], store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).get());
		assertCollectionsEquals(TEST_LIST_PROPERTY_VALUES[0], store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).get());
		assertCollectionsEquals(TEST_LIST_PROPERTY_VALUES[0], store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).get());
		store.removeProperty(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		for (Object e:TEST_LIST_PROPERTY_VALUES[0]) {
			try {
				store.addValueToCollection(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0], e);
			} catch (InvalidSPDXAnalysisException e1) {
				fail(e1.getMessage());
			}
		}
		for (Object e:TEST_LIST_PROPERTY_VALUES[0]) {
			try {
				store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0], e);
			} catch (InvalidSPDXAnalysisException e1) {
				fail(e1.getMessage());
			}
		}
	}
	
	private void assertCollectionsEquals(Object c1, Object c2) {
		if (!(c1 instanceof Collection)) {
			fail("c1 is not a collection");
		}
		if (!(c2 instanceof Collection)) {
			fail("c2 is not a collection");
		}
		Collection<?> col1 = (Collection<?>)c1;
		Collection<?> col2 = (Collection<?>)c2;
		assertEquals(col1.size(), col2.size());
		for (Object item:col1) {
			assertTrue(col2.contains(item));
		}
	}

	public void testClearList() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
		String value1 = "value1";
		String value2 = "value2";
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		assertEquals(2, toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).size());
		assertTrue(toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value1));
		assertTrue(toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value2));
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
		store.clearValueCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]);
		assertEquals(0, toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).size());
	}
	
	public void copyFrom() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		String value1 = "value1";
		String value2 = "value2";
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0], TEST_VALUE_PROPERTY_VALUES[0]);
		InMemSpdxStore store2 = new InMemSpdxStore();
		ModelCopyManager copyManager = new ModelCopyManager();
		copyManager.copy(store2, TEST_DOCUMENT_URI2, store, TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		assertEquals(TEST_VALUE_PROPERTY_VALUES[0], store2.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_VALUE_PROPERTIES[0]));
		assertEquals(2, toImmutableList(store2.listValues(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0])).size());
		assertTrue(toImmutableList(store2.listValues(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value1));
		assertTrue(toImmutableList(store2.listValues(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value2));
	}
	
	public void testRemoveListItem() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		String value1 = "value1";
		String value2 = "value2";
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		assertEquals(2, toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).size());
		assertTrue(toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value1));
		assertTrue(toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value2));
		assertTrue(store.removeValueFromCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1));
		assertEquals(1, toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).size());
		assertFalse(toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value1));
		assertTrue(toImmutableList(store.listValues(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0])).contains(value2));
		assertFalse("Already removed - should return false",store.removeValueFromCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1));
	}
	
	//TODO: Fix the following test - it is flakey.  Times out about 1 out of 5 times.  Test problem, not a problem with the code under test
	public void testLock() throws InvalidSPDXAnalysisException, IOException, InterruptedException, TimeoutException {
		final InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		String value1 = "value1";
		String value2 = "value2";
		IModelStoreLock lock = store.enterCriticalSection(TEST_DOCUMENT_URI1, false);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0], value1);
		lock.unlock();
		assertEquals(value1, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
		/* Expected program flow
		 * Step1: thread1 run verifies that the property is still value1
		 * thread1 starts a read transaction
		 * thread1 waits for step2
		 * thread2 run verifies that the property is still value1
		 * Step2: thread2 wakes up thread 1
		 * Step3: thread1 wakes up thread 2
		 * thread2 starts a write transaction
		 * thread2 is expected to block on the write transaction until thread1 finishes the read/verify
		 * step4: thread2 completes the write transaction, both threads verify value updated 
		 */
		final Waiter waiter = new Waiter();
		@SuppressWarnings("unused")
		final Thread thread1 = new Thread(null, null, "Thread1") {
			@Override
			public void run() {
				try {
					logger.info("thread1 started");
					logger.info("Waking up main thread");
					waiter.assertEquals("step0", setTestState("step1"));
					waiter.resume();	// release the main thread
					logger.info("Woke up main thread");
					waiter.assertEquals(value1, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
					IModelStoreLock transactionLock = store.enterCriticalSection(TEST_DOCUMENT_URI1, true);
					waiter.assertEquals(value1, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
					int retries = 0;
					while (!getTestState().equals("step2") && retries < MAX_RETRIES) {
						logger.info("Thread 1 waiting for thread 2");
						waiter.await(TIMEOUT);	// wait for thread 2
					}
					if (retries >= MAX_RETRIES) {
						waiter.fail("State never changed");
					}
					logger.info("Thread1 awoke from thread 2");
					waiter.assertEquals("step2", setTestState("step3"));
					logger.info("Waking up thread2");
					waiter.resume(); // wake thread 2 back up
					logger.info("Woke up thread2");
					waiter.assertEquals(value1, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
					transactionLock.unlock();
					retries = 0;
					while (!getTestState().equals("step4") && retries < MAX_RETRIES) {
						logger.info("Thread 1 waiting for thread 2");
						waiter.await(TIMEOUT);	// wait for thread 2 to commit
					}
					if (retries >= MAX_RETRIES) {
						waiter.fail("State never changed");
					}
					logger.info("Thread1 awoke from thread 2");
					waiter.assertEquals("step4", getTestState());
					waiter.assertEquals(value2, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
				} catch (Exception e) {
					waiter.fail("Unexpected exception: "+e.getMessage());
				} 
			}
		};
		
		@SuppressWarnings("unused")
		final Thread thread2 = new Thread(null ,null, "thread2") {
			@Override
			public void run() {
				try {
					waiter.assertEquals("step1", getTestState());
					waiter.assertEquals(value1, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
					waiter.assertEquals("step1",setTestState("step2"));
					logger.info("Waking up Thread1");
					waiter.resume();	// wakeup thread1
					logger.info("Woke up Thread1");
					int retries = 0;
					while (!getTestState().equals("step3") && retries < MAX_RETRIES) {
						logger.info("Thread 2 waiting for thread 1");
						waiter.await(TIMEOUT);	// wait for thread 1
					}
					if (retries >= MAX_RETRIES) {
						waiter.fail("State never changed");
					}
					logger.info("Thread2 awoke from thread 1");
					waiter.assertEquals("step3", getTestState());
					IModelStoreLock transactionLock2 = store.enterCriticalSection(TEST_DOCUMENT_URI1, false);	// this should block waiting for thread1 transaction to complete
					waiter.assertEquals(value1, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
					store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0], value2);
					waiter.assertEquals(value2, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
					transactionLock2.unlock();
					waiter.assertEquals("step3",setTestState("step4"));
					logger.info("Waking up Thread1");
					waiter.resume();	// wakeup thread1
					logger.info("Woke up Thread1");
					waiter.assertEquals(value2, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
				} catch (Exception e) {
					waiter.fail("Unexpected exception: "+e.getMessage());
				} 
			}
		};
		/*
		setTestState("step0");
		logger.info("Starting Thread1");
		thread1.start();
		int retries = 0;
		while (getTestState().equals("step0")) {
			logger.info("Waiting for Thread1");
			waiter.await(TIMEOUT);	// wait for thread 1 to get going
		}
		if (retries >= MAX_RETRIES) {
			waiter.fail("State never changed");
		}
		logger.info("Starting Thread2");
		thread2.start();
		logger.info("Joining thread1");
		thread1.join();
		logger.info("Joining thread2");
		thread2.join();
		assertEquals("step4", getTestState());
		*/
	}
	
	public void testCollectionSize() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
		String value1 = "value1";
		String value2 = "value2";
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		assertEquals(2, store.collectionSize(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]));
		assertEquals(0, store.collectionSize(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[1]));
	}
	
	public void testCollectionContains() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
		String value1 = "value1";
		String value2 = "value2";
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		assertTrue(store.collectionContains(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0],value1));
		assertTrue(store.collectionContains(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0],value2));
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
	}
	
	public void testIsPropertyValueAssignableTo() throws InvalidSPDXAnalysisException {

		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		// String
		PropertyDescriptor sProperty = new PropertyDescriptor("stringprop", SpdxConstants.SPDX_NAMESPACE);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, "String 1");
		assertTrue(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, String.class));
		assertFalse(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, Boolean.class));
		assertFalse(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, TypedValue.class));
		// Boolean
		PropertyDescriptor bProperty = new PropertyDescriptor("boolprop", SpdxConstants.SPDX_NAMESPACE);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, Boolean.valueOf(true));
		assertFalse(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, String.class));
		assertTrue(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, Boolean.class));
		assertFalse(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, TypedValue.class));
		// TypedValue
		PropertyDescriptor tvProperty = new PropertyDescriptor("tvprop", SpdxConstants.SPDX_NAMESPACE);
		TypedValue tv = new TypedValue(TEST_ID2, TEST_TYPE2);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, TEST_TYPE2);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, tvProperty, tv);
		assertFalse(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, tvProperty, String.class));
		assertFalse(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, tvProperty, Boolean.class));
		assertTrue(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, tvProperty, TypedValue.class));
		// Empty
		PropertyDescriptor emptyProperty = new PropertyDescriptor("emptyprop", SpdxConstants.SPDX_NAMESPACE);
		assertFalse(store.isPropertyValueAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, emptyProperty, String.class));
	}
	
	public void testCollectionMembersAssignableTo() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		// String
		PropertyDescriptor sProperty = new PropertyDescriptor("stringprop", SpdxConstants.SPDX_NAMESPACE);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, "String 1");
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, "String 2");
		assertTrue(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, String.class));
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, Boolean.class));
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, TypedValue.class));
		// Boolean
		PropertyDescriptor bProperty = new PropertyDescriptor("boolprop", SpdxConstants.SPDX_NAMESPACE);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, Boolean.valueOf(true));
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, Boolean.valueOf(false));
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, String.class));
		assertTrue(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, Boolean.class));
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, bProperty, TypedValue.class));
		// TypedValue
		PropertyDescriptor tvProperty  = new PropertyDescriptor("tvprop", SpdxConstants.SPDX_NAMESPACE);
	    TypedValue tv = new TypedValue(TEST_ID2, TEST_TYPE2);
	    store.create(TEST_DOCUMENT_URI1, TEST_ID2, TEST_TYPE2);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, tvProperty, tv);
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, tvProperty, String.class));
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, tvProperty, Boolean.class));
		assertTrue(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, tvProperty, TypedValue.class));
		// Mixed
		PropertyDescriptor mixedProperty = new PropertyDescriptor("mixedprop", SpdxConstants.SPDX_NAMESPACE);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, mixedProperty, Boolean.valueOf(true));
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, mixedProperty, "mixed value");
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, mixedProperty, String.class));
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, mixedProperty, Boolean.class));
		assertFalse(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, mixedProperty, TypedValue.class));
		// Empty
		PropertyDescriptor emptyProperty = new PropertyDescriptor("emptyprop", SpdxConstants.SPDX_NAMESPACE);
		assertTrue(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, emptyProperty, String.class));
		assertTrue(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, emptyProperty, Boolean.class));
		assertTrue(store.isCollectionMembersAssignableTo(TEST_DOCUMENT_URI1, TEST_ID1, emptyProperty, TypedValue.class));
	}
	
	public void testIsCollectionProperty() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		// String
		PropertyDescriptor sProperty = new PropertyDescriptor("stringprop", SpdxConstants.SPDX_NAMESPACE);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, sProperty, "String 1");
		PropertyDescriptor listProperty = new PropertyDescriptor("listProp", SpdxConstants.SPDX_NAMESPACE);
		store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID1, listProperty, "testValue");
		assertTrue(store.isCollectionProperty(TEST_DOCUMENT_URI1, TEST_ID1, listProperty));
		assertFalse(store.isCollectionProperty(TEST_DOCUMENT_URI1, TEST_ID1, sProperty));
	}
	
	public void testIdType() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		assertEquals(IdType.Anonymous, store.getIdType(InMemSpdxStore.ANON_PREFIX+"gnrtd23"));
		assertEquals(IdType.DocumentRef, store.getIdType(SpdxConstants.EXTERNAL_DOC_REF_PRENUM+"gnrtd23"));
		assertEquals(IdType.LicenseRef, store.getIdType(SpdxConstants.NON_STD_LICENSE_ID_PRENUM+"gnrtd23"));
		assertEquals(IdType.ListedLicense, store.getIdType("Apache-2.0"));
		assertEquals(IdType.ListedLicense, store.getIdType("LLVM-exception"));
		assertEquals(IdType.Literal, store.getIdType("NONE"));
		assertEquals(IdType.Literal, store.getIdType("NOASSERTION"));
		assertEquals(IdType.SpdxId, store.getIdType(SpdxConstants.SPDX_ELEMENT_REF_PRENUM+"gnrtd123"));
	}
	
	public void testGetCaseSensisitiveId() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		String expected = "TestIdOne";
		String lower = expected.toLowerCase();
		store.create(TEST_DOCUMENT_URI1, expected, SpdxConstants.CLASS_ANNOTATION);
		assertEquals(expected, store.getCaseSensisitiveId(TEST_DOCUMENT_URI1, lower).get());
		assertFalse(store.getCaseSensisitiveId(TEST_DOCUMENT_URI1, "somethingNotThere").isPresent());
		try {
			store.create(TEST_DOCUMENT_URI1, lower, SpdxConstants.CLASS_ANNOTATION);
			fail("This should be a duplicate ID failure");
		} catch (InvalidSPDXAnalysisException e) {
			// expected
		}
	}
	
	public void testGetTypedValue() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		assertEquals(SpdxConstants.CLASS_ANNOTATION, store.getTypedValue(TEST_DOCUMENT_URI1, TEST_ID1).get().getType());
		assertFalse(store.getTypedValue(TEST_DOCUMENT_URI1, TEST_ID2).isPresent());
		assertFalse(store.getTypedValue(TEST_DOCUMENT_URI2, TEST_ID1).isPresent());
	}
	
	public void testDelete() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		String id1 = "TestId1";
		String id2 = "testId2";
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id2));
		assertFalse(store.exists(TEST_DOCUMENT_URI2, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI2, id2));
		store.create(TEST_DOCUMENT_URI1, id1, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		store.create(TEST_DOCUMENT_URI2, id2, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		store.create(TEST_DOCUMENT_URI2, id1, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		assertTrue(store.exists(TEST_DOCUMENT_URI1, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id2));
		assertTrue(store.exists(TEST_DOCUMENT_URI2, id1));
		assertTrue(store.exists(TEST_DOCUMENT_URI2, id2));
		
		store.delete(TEST_DOCUMENT_URI1, id1);
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id1));
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id2));
		assertTrue(store.exists(TEST_DOCUMENT_URI2, id1));
		assertTrue(store.exists(TEST_DOCUMENT_URI2, id2));
	}
	
	public void testDeleteInUse() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		String id1 = "TestId1";
		String id2 = "testId2";
		String id3 = "testId3";
		String id4 = "testId4";
		store.create(TEST_DOCUMENT_URI1, id1, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		store.create(TEST_DOCUMENT_URI1, id2, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		store.create(TEST_DOCUMENT_URI1, id3, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		store.create(TEST_DOCUMENT_URI1, id4, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		TypedValue tv3 = new TypedValue(id3, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		store.addValueToCollection(TEST_DOCUMENT_URI1, id2, 
				new PropertyDescriptor("listProperty", SpdxConstants.SPDX_NAMESPACE), tv3);
		TypedValue tv4 = new TypedValue(id4, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		store.addValueToCollection(TEST_DOCUMENT_URI1, id2, 
				new PropertyDescriptor("listProperty", SpdxConstants.SPDX_NAMESPACE), tv4);
		store.setValue(TEST_DOCUMENT_URI1, id3, 
				new PropertyDescriptor("property", SpdxConstants.SPDX_NAMESPACE), 
				new TypedValue(id1, SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO));
		
		try {
			store.delete(TEST_DOCUMENT_URI1, id3);
			fail("id3 is in the listProperty for id2");
		} catch (SpdxIdInUseException ex) {
			// expected - id3 is in the listProperty for id2
		}
		try {
			store.delete(TEST_DOCUMENT_URI1, id4);
			fail("id4 is in the listProperty for id2");
		} catch (SpdxIdInUseException ex) {
			// expected - id4 is in the listProperty for id2
		}
		try {
			store.delete(TEST_DOCUMENT_URI1, id1);
			fail("id1 is in the property for id3");
		} catch (SpdxIdInUseException ex) {
			// expected - id1 is in the property for id3
		}
		store.removeValueFromCollection(TEST_DOCUMENT_URI1, id2, 
				new PropertyDescriptor("listProperty", SpdxConstants.SPDX_NAMESPACE), tv4);
		store.delete(TEST_DOCUMENT_URI1, id4);
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id4));
		try {
			store.delete(TEST_DOCUMENT_URI1, id3);
			fail("id3 is in the listProperty for id2");
		} catch (SpdxIdInUseException ex) {
			// expected - id3 is in the listProperty for id2
		}
		store.removeProperty(TEST_DOCUMENT_URI1, id3, 
				new PropertyDescriptor("property", SpdxConstants.SPDX_NAMESPACE));
		store.delete(TEST_DOCUMENT_URI1, id1);
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id1));
		store.delete(TEST_DOCUMENT_URI1, id2);
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id2));
		store.delete(TEST_DOCUMENT_URI1, id3);
		assertFalse(store.exists(TEST_DOCUMENT_URI1, id3));
	}
	
	public void testReferenceCounts() throws Exception {
	    InMemSpdxStore store = new InMemSpdxStore();
        store.create(TEST_DOCUMENT_URI1, TEST_ID1, TEST_TYPE1);
        store.create(TEST_DOCUMENT_URI1, TEST_ID2, TEST_TYPE2);
        StoredTypedItem item = store.getItem(TEST_DOCUMENT_URI1, TEST_ID1);
        assertEquals(0, item.getReferenceCount());
        TypedValue tv = new TypedValue(TEST_ID1, TEST_TYPE1);
        store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID2, 
        		new PropertyDescriptor("prop1", SpdxConstants.SPDX_NAMESPACE), tv);
        assertEquals(1, item.getReferenceCount());
        store.setValue(TEST_DOCUMENT_URI1, TEST_ID2, 
        		new PropertyDescriptor("prop2", SpdxConstants.SPDX_NAMESPACE), tv);
        assertEquals(2, item.getReferenceCount());
        store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID2, 
        		new PropertyDescriptor("prop3", SpdxConstants.SPDX_NAMESPACE), tv);
        assertEquals(3, item.getReferenceCount());
        store.removeProperty(TEST_DOCUMENT_URI1, TEST_ID2, 
        		new PropertyDescriptor("prop2", SpdxConstants.SPDX_NAMESPACE));
        assertEquals(2, item.getReferenceCount());
        store.removeValueFromCollection(TEST_DOCUMENT_URI1, TEST_ID2, 
        		new PropertyDescriptor("prop2", SpdxConstants.SPDX_NAMESPACE), tv);
        assertEquals(1, item.getReferenceCount());
        store.clearValueCollection(TEST_DOCUMENT_URI1, TEST_ID2, 
        		new PropertyDescriptor("prop1", SpdxConstants.SPDX_NAMESPACE));
        assertEquals(0, item.getReferenceCount());
        store.delete(TEST_DOCUMENT_URI1, TEST_ID1);
	}
	
	   public void testReferenceCountsDelete() throws Exception {
	        InMemSpdxStore store = new InMemSpdxStore();
	        store.create(TEST_DOCUMENT_URI1, TEST_ID1, TEST_TYPE1);
	        store.create(TEST_DOCUMENT_URI1, TEST_ID2, TEST_TYPE2);
	        StoredTypedItem item = store.getItem(TEST_DOCUMENT_URI1, TEST_ID1);
	        assertEquals(0, item.getReferenceCount());
	        TypedValue tv = new TypedValue(TEST_ID1, TEST_TYPE1);
	        store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID2, 
	        		new PropertyDescriptor("prop1", SpdxConstants.SPDX_NAMESPACE), tv);
	        assertEquals(1, item.getReferenceCount());
	        store.setValue(TEST_DOCUMENT_URI1, TEST_ID2, 
	        		new PropertyDescriptor("prop2", SpdxConstants.SPDX_NAMESPACE), tv);
	        assertEquals(2, item.getReferenceCount());
	        store.addValueToCollection(TEST_DOCUMENT_URI1, TEST_ID2, 
	        		new PropertyDescriptor("prop3", SpdxConstants.SPDX_NAMESPACE), tv);
	        assertEquals(3, item.getReferenceCount());
	        store.delete(TEST_DOCUMENT_URI1, TEST_ID2);
	        assertEquals(0, item.getReferenceCount());
	        store.delete(TEST_DOCUMENT_URI1, TEST_ID1);
	    }
}
