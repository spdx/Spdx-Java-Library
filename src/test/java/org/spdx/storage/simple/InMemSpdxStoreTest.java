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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.TypedValue;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.IModelStore.ModelTransaction;
import org.spdx.storage.IModelStore.ReadWrite;

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
	static final String[] TEST_VALUE_PROPERTIES = new String[] {"valueProp1", "valueProp2", "valueProp3", "valueProp4"};
	static final Object[] TEST_VALUE_PROPERTY_VALUES = new Object[] {"value1", true, "value2", null};
	static final String[] TEST_LIST_PROPERTIES = new String[] {"listProp1", "listProp2", "listProp3"};

	protected static final int MAX_RETRIES = 10;
	TypedValue[] TEST_TYPED_PROP_VALUES;
	List<?>[] TEST_LIST_PROPERTY_VALUES;
	
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
		TEST_LIST_PROPERTY_VALUES = new List<?>[] {Arrays.asList("ListItem1", "listItem2", "listItem3"), 
			Arrays.asList(true, false, true),
			Arrays.asList(new TypedValue(TEST_DOCUMENT_URI1, "typeId1", TEST_TYPE1), new TypedValue(TEST_DOCUMENT_URI1, "typeId2", TEST_TYPE2))};
			TEST_VALUE_PROPERTY_VALUES[3] = new TypedValue(TEST_DOCUMENT_URI1, "typeId3", TEST_TYPE1);
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
		assertEquals("LicenseRef-0", nextId);
		store.create(TEST_DOCUMENT_URI1, "LicenseRef-33", SpdxConstants.CLASS_SPDX_EXTRACTED_LICENSING_INFO);
		nextId = store.getNextId(IdType.LicenseRef, TEST_DOCUMENT_URI1);
		assertEquals("LicenseRef-34", nextId);
		
		// SPDX ID's
		nextId = store.getNextId(IdType.SpdxId, TEST_DOCUMENT_URI1);
		assertEquals("SPDXRef-0", nextId);
		store.create(TEST_DOCUMENT_URI1, "SPDXRef-33", SpdxConstants.CLASS_SPDX_FILE);
		nextId = store.getNextId(IdType.SpdxId, TEST_DOCUMENT_URI1);
		assertEquals("SPDXRef-34", nextId);
		
		// Anonomous ID's
		nextId = store.getNextId(IdType.Anonomous, TEST_DOCUMENT_URI1);
		assertEquals(InMemSpdxStore.ANON_PREFIX + "0", nextId);
		store.create(TEST_DOCUMENT_URI1, InMemSpdxStore.ANON_PREFIX + "33", SpdxConstants.CLASS_SPDX_CHECKSUM);
		nextId = store.getNextId(IdType.Anonomous, TEST_DOCUMENT_URI1);
		assertEquals(InMemSpdxStore.ANON_PREFIX + "34", nextId);
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
		assertEquals(0, store.getPropertyValueNames(TEST_DOCUMENT_URI1, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueNames(TEST_DOCUMENT_URI2, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueNames(TEST_DOCUMENT_URI1, TEST_ID2).size());
		for (int i = 0; i < TEST_VALUE_PROPERTIES.length; i++) {
			store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[i], TEST_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[i], value);
			}
		}
		List<String> result = store.getPropertyValueNames(TEST_DOCUMENT_URI1, TEST_ID1);
		assertEquals(TEST_VALUE_PROPERTIES.length, result.size());
		for (String prop:TEST_VALUE_PROPERTIES) {
			assertTrue(result.contains(prop));
		}
		assertEquals(0, store.getPropertyValueNames(TEST_DOCUMENT_URI2, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueNames(TEST_DOCUMENT_URI1, TEST_ID2).size());		
	}
	
	public void testGetPropertyValueListNames() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertEquals(0, store.getPropertyValueListNames(TEST_DOCUMENT_URI1, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueListNames(TEST_DOCUMENT_URI2, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueListNames(TEST_DOCUMENT_URI1, TEST_ID2).size());
		for (int i = 0; i < TEST_VALUE_PROPERTIES.length; i++) {
			store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[i], TEST_VALUE_PROPERTY_VALUES[i]);
		}
		for (int i = 0; i < TEST_LIST_PROPERTIES.length; i++) {
			for (Object value:TEST_LIST_PROPERTY_VALUES[i]) {
				store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[i], value);
			}
		}
		List<String> result = store.getPropertyValueListNames(TEST_DOCUMENT_URI1, TEST_ID1);
		assertEquals(TEST_LIST_PROPERTIES.length, result.size());
		for (String prop:TEST_LIST_PROPERTIES) {
			assertTrue(result.contains(prop));
		}
		assertEquals(0, store.getPropertyValueListNames(TEST_DOCUMENT_URI2, TEST_ID1).size());
		assertEquals(0, store.getPropertyValueListNames(TEST_DOCUMENT_URI1, TEST_ID2).size());
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
		store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		assertEquals(2, store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).size());
		assertTrue(store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value1));
		assertTrue(store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value2));
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
	}
	
	public void testGetNextId() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		// License ID's
		String nextId = store.getNextId(IdType.LicenseRef, TEST_DOCUMENT_URI1);
		assertEquals("LicenseRef-0", nextId);
		nextId = store.getNextId(IdType.LicenseRef, TEST_DOCUMENT_URI1);
		assertEquals("LicenseRef-1", nextId);
		
		// SPDX ID's
		nextId = store.getNextId(IdType.SpdxId, TEST_DOCUMENT_URI1);
		assertEquals("SPDXRef-0", nextId);
		nextId = store.getNextId(IdType.SpdxId, TEST_DOCUMENT_URI1);
		assertEquals("SPDXRef-1", nextId);
		
		// Anonomous ID's
		nextId = store.getNextId(IdType.Anonomous, TEST_DOCUMENT_URI1);
		assertEquals(InMemSpdxStore.ANON_PREFIX + "0", nextId);
		nextId = store.getNextId(IdType.Anonomous, TEST_DOCUMENT_URI1);
		assertEquals(InMemSpdxStore.ANON_PREFIX + "1", nextId);
	}
	
	public void testRemoveProperty() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI2, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		store.create(TEST_DOCUMENT_URI1, TEST_ID2, SpdxConstants.CLASS_ANNOTATION);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], TEST_LIST_PROPERTY_VALUES[0]);
		store.setValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0], TEST_LIST_PROPERTY_VALUES[0]);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0], TEST_LIST_PROPERTY_VALUES[0]);
		assertEquals(TEST_LIST_PROPERTY_VALUES[0], store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).get());
		assertEquals(TEST_LIST_PROPERTY_VALUES[0], store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).get());
		assertEquals(TEST_LIST_PROPERTY_VALUES[0], store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).get());
		store.removeProperty(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]);
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		store.setValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0], TEST_LIST_PROPERTY_VALUES[0]);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0], TEST_LIST_PROPERTY_VALUES[0]);
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
		store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		assertEquals(2, store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).size());
		assertTrue(store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value1));
		assertTrue(store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value2));
		assertFalse(store.getValue(TEST_DOCUMENT_URI2, TEST_ID1, TEST_LIST_PROPERTIES[0]).isPresent());
		assertFalse(store.getValue(TEST_DOCUMENT_URI1, TEST_ID2, TEST_LIST_PROPERTIES[0]).isPresent());
		store.clearPropertyValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]);
		assertEquals(0, store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).size());
	}
	
	public void copyFrom() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		String value1 = "value1";
		String value2 = "value2";
		store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0], TEST_VALUE_PROPERTY_VALUES[0]);
		InMemSpdxStore store2 = new InMemSpdxStore();
		store2.copyFrom(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION, store);
		assertEquals(TEST_VALUE_PROPERTY_VALUES[0], store2.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]));
		assertEquals(2, store2.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).size());
		assertTrue(store2.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value1));
		assertTrue(store2.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value2));
	}
	
	public void testRemoveListItem() throws InvalidSPDXAnalysisException {
		InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		String value1 = "value1";
		String value2 = "value2";
		store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		store.addValueToList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value2);
		assertEquals(2, store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).size());
		assertTrue(store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value1));
		assertTrue(store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value2));
		store.removePropertyValueFromList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0], value1);
		assertEquals(1, store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).size());
		assertFalse(store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value1));
		assertTrue(store.getValueList(TEST_DOCUMENT_URI1, TEST_ID1, TEST_LIST_PROPERTIES[0]).contains(value2));
	}
	
	public void testTransaction() throws InvalidSPDXAnalysisException, IOException, InterruptedException, TimeoutException {
		final InMemSpdxStore store = new InMemSpdxStore();
		store.create(TEST_DOCUMENT_URI1, TEST_ID1, SpdxConstants.CLASS_ANNOTATION);
		String value1 = "value1";
		String value2 = "value2";
		ModelTransaction transaction = store.beginTransaction(ReadWrite.WRITE);
		store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0], value1);
		transaction.commit();
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
					ModelTransaction threadTransaction = store.beginTransaction(ReadWrite.READ);
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
					threadTransaction.commit();
					threadTransaction.close();
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
					assertEquals("step3", getTestState());
					ModelTransaction threadTransaction = store.beginTransaction(ReadWrite.WRITE);	// this should block waiting for thread1 transaction to complete
					waiter.assertEquals(value1, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
					store.setValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0], value2);
					waiter.assertEquals(value2, store.getValue(TEST_DOCUMENT_URI1, TEST_ID1, TEST_VALUE_PROPERTIES[0]).get());
					threadTransaction.commit();
					threadTransaction.close();
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
	}
}
