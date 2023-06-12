package org.spdx.storage.listedlicense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.InvalidSpdxPropertyException;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.model.compat.v2.license.CrossRef;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class CrossRefJsonTest extends TestCase {
	
	static final List<String> STRING_PROPERTY_VALUE_NAMES = Arrays.asList(
			SpdxConstantsCompatV2.PROP_CROSS_REF_MATCH.getName(),
			SpdxConstantsCompatV2.PROP_CROSS_REF_TIMESTAMP.getName(),
			SpdxConstantsCompatV2.PROP_CROSS_REF_URL.getName());
	
	static final List<String> BOOLEAN_PROPERTY_VALUE_NAMES = Arrays.asList(
			SpdxConstantsCompatV2.PROP_CROSS_REF_IS_LIVE.getName(),
			SpdxConstantsCompatV2.PROP_CROSS_REF_IS_VALID.getName(),
			SpdxConstantsCompatV2.PROP_CROSS_REF_WAYBACK_LINK.getName()
			);
	
	static final List<String> INTEGER_PROPERTY_VALUE_NAMES = Arrays.asList(SpdxConstantsCompatV2.PROP_CROSS_REF_ORDER.getName());
	
	static final List<String> PROPERTY_VALUE_NAMES = new ArrayList<>();
	
	static {
		PROPERTY_VALUE_NAMES.addAll(STRING_PROPERTY_VALUE_NAMES);
		PROPERTY_VALUE_NAMES.addAll(BOOLEAN_PROPERTY_VALUE_NAMES);
		PROPERTY_VALUE_NAMES.addAll(INTEGER_PROPERTY_VALUE_NAMES);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCrossRefJsonCrossRef() throws InvalidSPDXAnalysisException {
		IModelStore modelStore = new InMemSpdxStore();
		String docUri = "http://doc/uri";
		String id = "tempid";
		String url = "http://url";
		Boolean isWayBackLink = true;
		Boolean isLive = false;
		String match = "match";
		Integer order = 12;
		String timestamp = "time and date";
		Boolean isValid = true;
		CrossRef inputCrossRef = new CrossRef(modelStore, docUri, id, null, true);
		inputCrossRef.setUrl(url);
		inputCrossRef.setIsWayBackLink(isWayBackLink);
		inputCrossRef.setLive(isLive);
		inputCrossRef.setMatch(match);
		inputCrossRef.setOrder(order);
		inputCrossRef.setTimestamp(timestamp);
		inputCrossRef.setValid(isValid);
		CrossRefJson result = new CrossRefJson(inputCrossRef);
		assertEquals(id, result.getId());
		assertEquals(url, result.url);
		assertEquals(isWayBackLink, result.isWayBackLink);
		assertEquals(isLive, result.isLive);
		assertEquals(match, result.match);
		assertEquals(order, result.order);
		assertEquals(timestamp, result.timestamp);
		assertEquals(isValid, result.isValid);
	}

	public void testGetPropertyValueNames() throws InvalidSpdxPropertyException {
		CrossRefJson crj = new CrossRefJson();
		List<String> result = crj.getPropertyValueNames();
		assertEquals(0, result.size());
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			crj.setPrimativeValue(valueName, "ValueFor"+valueName);
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			crj.setPrimativeValue(valueName, false);
		}
		int i = 1;
		for (String valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			crj.setPrimativeValue(valueName, i++);
		}
		result = crj.getPropertyValueNames();
		assertEquals(PROPERTY_VALUE_NAMES.size(), result.size());
		for (String valueName:PROPERTY_VALUE_NAMES) {
			if (!result.contains(valueName)) {
				fail("Missing "+valueName);
			}
		}
	}

	public void testSetPrimativeValue() throws InvalidSpdxPropertyException {
		Map<String, String> stringValues = new HashMap<>();
		CrossRefJson crj = new CrossRefJson();
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			crj.setPrimativeValue(valueName, stringValues.get(valueName));
		}
		Map<String, Boolean> booleanValues = new HashMap<>();
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			booleanValues.put(valueName, false);
			crj.setPrimativeValue(valueName, booleanValues.get(valueName));
		}
		Map<String, Integer> integerValues = new HashMap<>();
		int i = 1;
		for (String valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			integerValues.put(valueName, i++);
			crj.setPrimativeValue(valueName, integerValues.get(valueName));
		}
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), crj.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), crj.getValue(valueName));
		}
		for (String valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			assertEquals(integerValues.get(valueName), crj.getValue(valueName));
		}
	}

	public void testGetId() {
		CrossRefJson crj = new CrossRefJson();
		assertTrue(Objects.isNull(crj.getId()));
		String id = "objectUri";
		crj.setId(id);
		assertEquals(id, crj.getId());
	}

	public void testRemoveProperty() throws InvalidSpdxPropertyException {
		Map<String, String> stringValues = new HashMap<>();
		CrossRefJson crj = new CrossRefJson();
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			stringValues.put(valueName, "ValueFor"+valueName);
			crj.setPrimativeValue(valueName, stringValues.get(valueName));
		}
		Map<String, Boolean> booleanValues = new HashMap<>();
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			booleanValues.put(valueName, false);
			crj.setPrimativeValue(valueName, booleanValues.get(valueName));
		}
		Map<String, Integer> integerValues = new HashMap<>();
		int i = 1;
		for (String valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			integerValues.put(valueName, i++);
			crj.setPrimativeValue(valueName, integerValues.get(valueName));
		}
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertEquals(stringValues.get(valueName), crj.getValue(valueName));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertEquals(booleanValues.get(valueName), crj.getValue(valueName));
		}
		for (String valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			assertEquals(integerValues.get(valueName), crj.getValue(valueName));
		}
		
		for (String valueName:PROPERTY_VALUE_NAMES) {
			crj.removeProperty(valueName);
		}
		
		for (String valueName:PROPERTY_VALUE_NAMES) {
			assertTrue(Objects.isNull(crj.getValue(valueName)));
		}
	}

	public void testIsPropertyValueAssignableTo() throws InvalidSpdxPropertyException {
		CrossRefJson crj = new CrossRefJson();
		for (String valueName:STRING_PROPERTY_VALUE_NAMES) {
			assertTrue(crj.isPropertyValueAssignableTo(valueName, String.class));
			assertFalse(crj.isPropertyValueAssignableTo(valueName, Boolean.class));
			assertFalse(crj.isPropertyValueAssignableTo(valueName, Integer.class));
		}
		for (String valueName:BOOLEAN_PROPERTY_VALUE_NAMES) {
			assertFalse(crj.isPropertyValueAssignableTo(valueName, String.class));
			assertTrue(crj.isPropertyValueAssignableTo(valueName, Boolean.class));
			assertFalse(crj.isPropertyValueAssignableTo(valueName, Integer.class));
		}
		for (String valueName:INTEGER_PROPERTY_VALUE_NAMES) {
			assertFalse(crj.isPropertyValueAssignableTo(valueName, String.class));
			assertFalse(crj.isPropertyValueAssignableTo(valueName, Boolean.class));
			assertTrue(crj.isPropertyValueAssignableTo(valueName, Integer.class));
		}
	}

}
