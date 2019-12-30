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
package org.spdx.library.model;

import java.util.List;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ChecksumTest extends TestCase {
	
	static final ChecksumAlgorithm[] ALGORITHMS = new ChecksumAlgorithm[] {
			ChecksumAlgorithm.MD5, ChecksumAlgorithm.SHA1,
			ChecksumAlgorithm.SHA256};
	static final String SHA1_VALUE1 = "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12";
	static final String SHA1_VALUE2 = "2222e1c67a2d28fced849ee1bb76e7391b93eb12";
	static final String SHA256_VALUE1 = "CA978112CA1BBDCAFAC231B39A23DC4DA786EFF8147C4E72B9807785AFEE48BB";
	static final String SHA256_VALUE2 = "F7846F55CF23E14EEBEAB5B4E1550CAD5B509E3348FBC4EFA3A1413D393CB650";
	static final String MD5_VALUE1 = "9e107d9d372bb6826bd81d3542a419d6";
	static final String MD5_VALUE2 = "d41d8cd98f00b204e9800998ecf8427e";
	String[] VALUES = new String[] {MD5_VALUE1, SHA1_VALUE1, SHA256_VALUE1};
	Checksum[] TEST_CHECKSUMS;
	GenericModelObject gmo;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset();
		gmo = new GenericModelObject();
		TEST_CHECKSUMS = new Checksum[ALGORITHMS.length];
		for (int i = 0; i < ALGORITHMS.length; i++) {
			TEST_CHECKSUMS[i] = gmo.createChecksum(ALGORITHMS[i], VALUES[i]);
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEquivalent() throws InvalidSPDXAnalysisException {
		Checksum c1 = gmo.createChecksum(ChecksumAlgorithm.SHA256, SHA256_VALUE1);
		Checksum c2 = gmo.createChecksum(ChecksumAlgorithm.SHA256, SHA256_VALUE1);
		assertTrue(c1.equivalent(c2));
		c2.setAlgorithm(ChecksumAlgorithm.SHA1);
		assertFalse(c1.equals(c2));
		c2.setAlgorithm(ChecksumAlgorithm.SHA256);
		assertTrue(c1.equivalent(c2));
		c2.setValue(SHA256_VALUE2);
		assertFalse(c1.equals(c2));
		c2.setValue(SHA256_VALUE1);
		assertTrue(c1.equivalent(c2));
	}

	/**
	 * Test method for {@link org.spdx.library.model.Checksum#verify()}.
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.SHA1, "0123456789abcdef0123456789abcdef01234567");
		List<String> verify = checksum.verify();
		assertEquals(0, verify.size());
		checksum.setValue("BadValue");
		assertEquals(1, checksum.verify().size());
		checksum.setAlgorithm(null);
		assertEquals(1, checksum.verify().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.Checksum#setAlgorithm(org.spdx.library.model.enumerations.ChecksumAlgorithm)}.
	 */
	public void testSetAlgorithm() throws InvalidSPDXAnalysisException {
		Checksum[] checksumReferences = new Checksum[TEST_CHECKSUMS.length];
		for (int i = 0; i < checksumReferences.length; i++) {
			checksumReferences[i] = new Checksum(TEST_CHECKSUMS[i].getModelStore(), TEST_CHECKSUMS[i].getDocumentUri(),
					TEST_CHECKSUMS[i].getId(), TEST_CHECKSUMS[i].getCopyManager(), false);
		}
		ChecksumAlgorithm[] newAlgorithms = new ChecksumAlgorithm[] {
				ALGORITHMS[2], ALGORITHMS[0], ALGORITHMS[1]
		};
		for (int i = 0;i < checksumReferences.length; i++) {
			assertEquals(ALGORITHMS[i], TEST_CHECKSUMS[i].getAlgorithm().get());
			assertEquals(ALGORITHMS[i], checksumReferences[i].getAlgorithm().get());
			checksumReferences[i].setAlgorithm(newAlgorithms[i]);
			assertEquals(newAlgorithms[i], TEST_CHECKSUMS[i].getAlgorithm().get());
			assertEquals(newAlgorithms[i], checksumReferences[i].getAlgorithm().get());
			assertEquals(VALUES[i], TEST_CHECKSUMS[i].getValue().get());
			assertEquals(VALUES[i], checksumReferences[i].getValue().get());
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.Checksum#setValue(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testSetValue() throws InvalidSPDXAnalysisException {
		Checksum[] checksumReferences = new Checksum[TEST_CHECKSUMS.length];
		for (int i = 0; i < checksumReferences.length; i++) {
			checksumReferences[i] = new Checksum(TEST_CHECKSUMS[i].getModelStore(), TEST_CHECKSUMS[i].getDocumentUri(),
					TEST_CHECKSUMS[i].getId(), TEST_CHECKSUMS[i].getCopyManager(), false);
		}
		String[] newValues = new String[] {
				VALUES[2], VALUES[0], VALUES[1]
		};
		for (int i = 0;i < checksumReferences.length; i++) {
			assertEquals(VALUES[i], TEST_CHECKSUMS[i].getValue().get());
			assertEquals(VALUES[i], checksumReferences[i].getValue().get());
			checksumReferences[i].setValue(newValues[i]);
			assertEquals(newValues[i], TEST_CHECKSUMS[i].getValue().get());
			assertEquals(newValues[i], checksumReferences[i].getValue().get());
			assertEquals(ALGORITHMS[i], TEST_CHECKSUMS[i].getAlgorithm().get());
			assertEquals(ALGORITHMS[i], checksumReferences[i].getAlgorithm().get());
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.Checksum#toString()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testToString() throws InvalidSPDXAnalysisException {
		for (int i = 0; i < TEST_CHECKSUMS.length; i++) {
			assertTrue(TEST_CHECKSUMS[i].toString().contains(ALGORITHMS[i].toString()));
			assertTrue(TEST_CHECKSUMS[i].toString().contains(VALUES[i]));
		}
		Checksum checksum = gmo.createChecksum(ALGORITHMS[0], VALUES[0]);
		checksum.setAlgorithm(null);
		assertTrue(checksum.toString().contains("EMPTY"));
		checksum.setValue(null);
		assertTrue(checksum.toString().contains("EMPTY"));
		checksum.setAlgorithm(ALGORITHMS[0]);
		assertTrue(checksum.toString().contains("EMPTY"));
	}

	/**
	 * Test method for {@link org.spdx.library.model.Checksum#compareTo(org.spdx.library.model.Checksum)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		Checksum checksum = gmo.createChecksum(ChecksumAlgorithm.MD5, MD5_VALUE1);
		Checksum checksum2 = gmo.createChecksum(ChecksumAlgorithm.MD5, MD5_VALUE1);
		assertEquals(0, checksum.compareTo(checksum2));
		assertEquals(0, checksum2.compareTo(checksum));
		checksum2.setValue(MD5_VALUE2);
		assertTrue(checksum.compareTo(checksum2) < 0);
		assertTrue(checksum2.compareTo(checksum) > 0);
		checksum.setAlgorithm(ChecksumAlgorithm.SHA1);
		assertTrue(checksum.compareTo(checksum2) > 0);
		assertTrue(checksum2.compareTo(checksum) < 0);
		checksum.setValue(null);
		assertTrue(checksum.compareTo(checksum2) > 0);
		assertTrue(checksum2.compareTo(checksum) < 0);
		checksum2.setAlgorithm(null);
		assertTrue(checksum.compareTo(checksum2) < 0);
		assertTrue(checksum2.compareTo(checksum) > 0);
		checksum.setAlgorithm(null);
		checksum2.setValue(null);
		assertEquals(0, checksum.compareTo(checksum2));
		assertEquals(0, checksum2.compareTo(checksum));
	}

}
