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
import org.spdx.library.SpdxConstants;
import org.spdx.library.Version;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class ChecksumTest extends TestCase {
	
	static final ChecksumAlgorithm[] ALGORITHMS = new ChecksumAlgorithm[] {
			ChecksumAlgorithm.MD5, ChecksumAlgorithm.SHA1,
			ChecksumAlgorithm.SHA256, ChecksumAlgorithm.SHA3_256, ChecksumAlgorithm.SHA3_384, ChecksumAlgorithm.SHA3_512,
			ChecksumAlgorithm.BLAKE2b_256, ChecksumAlgorithm.BLAKE2b_384, ChecksumAlgorithm.BLAKE2b_512,
			ChecksumAlgorithm.BLAKE3, ChecksumAlgorithm.ADLER32};
	static final String SHA1_VALUE1 = "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12";
	static final String SHA1_VALUE2 = "2222e1c67a2d28fced849ee1bb76e7391b93eb12";
	static final String SHA256_VALUE1 = "CA978112CA1BBDCAFAC231B39A23DC4DA786EFF8147C4E72B9807785AFEE48BB";
	static final String SHA256_VALUE2 = "F7846F55CF23E14EEBEAB5B4E1550CAD5B509E3348FBC4EFA3A1413D393CB650";
	static final String MD5_VALUE1 = "9e107d9d372bb6826bd81d3542a419d6";
	static final String MD5_VALUE2 = "d41d8cd98f00b204e9800998ecf8427e";
	static final String SHA3_256_VALUE1 = "3518a1626d45e0136ab145f4406e7991b983609ef47fda2c0e12b8c07c35bcde";
	static final String SHA3_256_VALUE2 = "ef6b5a41a0e7c3e0699f17aa1a2b03d0d3739163015928dead0136ffcd2d0733";
	static final String SHA3_384_VALUE1 = "8ba59b02f048a31a7ee4cbbd22a6cd02961e9650008037b7f7309dd882f3aaa9bb2a93653c1d524420f25ac62d037667";
	static final String SHA3_384_VALUE2 = "ccfe5458990438984358069f0b647f5cbc248ee41679bca93b4f18c0bb69ec8e6e41f19481eb3dc83dd22a2ad28f2102";
	static final String SHA3_512_VALUE1 = "b410ad04ad92b70b1f77b62165a67c2ac368030ca79d47f95d48f37e9be155423242d4ef0c2af510c99f1c99deb95b990a131189adfe0dc841082833dd5dfc64";
	static final String SHA3_512_VALUE2 = "e32069186e8946b22c0eec91a2978727b16bd6020e2b191f95ddd1e3ffcfa533ac1444dd0c09caf73b003b30001e974859ef1a48996e9b4cf783d764438725d6";
	static final String BLAKE2B_256_VALUE1 = "716f6e863f744b9ac22c97ec7b76ea5f5908bc5b2f67c61510bfc4751384ea7a";
	static final String BLAKE2B_256_VALUE2 = "aaaf6e863f744b9ac22c97ec7b76ea5f5908bc5b2f67c61510bfc4751384ea7a";
	static final String BLAKE2B_384_VALUE1 = "c6cbd89c926ab525c242e6621f2f5fa73aa4afe3d9e24aed727faaadd6af38b620bdb623dd2b4788b1c8086984af8706";
	static final String BLAKE2B_384_VALUE2 = "aaabd89c926ab525c242e6621f2f5fa73aa4afe3d9e24aed727faaadd6af38b620bdb623dd2b4788b1c8086984af8706";
	static final String BLAKE2B_512_VALUE1 = "a8cfbbd73726062df0c6864dda65defe58ef0cc52a5625090fa17601e1eecd1b628e94f396ae402a00acc9eab77b4d4c2e852aaaa25a636d80af3fc7913ef5b8";
	static final String BLAKE2B_512_VALUE2 = "dddfbbd73726062df0c6864dda65defe58ef0cc52a5625090fa17601e1eecd1b628e94f396ae402a00acc9eab77b4d4c2e852aaaa25a636d80af3fc7913ef5b8";
	static final String BLAKE3_VALUE1 = "9d48cdf8fdcd4af64318de560973d16140ea4de3e1f9212770b01211d9eb59fc";
	static final String BLAKE3_VALUE2 = "aaa8cdf8fdcd4af64318de560973d16140ea4de3e1f9212770b01211d9eb59fc";
	static final String ADLER32_VALUE1 = "0eaa033d";
	static final String ADLER32_VALUE2 = "ddaa033d";

	String[] VALUES = new String[] {MD5_VALUE1, SHA1_VALUE1, SHA256_VALUE1, SHA3_256_VALUE1, SHA3_384_VALUE1,
			SHA3_512_VALUE1, BLAKE2B_256_VALUE1, BLAKE2B_384_VALUE1, BLAKE2B_512_VALUE1, BLAKE3_VALUE1, ADLER32_VALUE1};
	String[] VALUES2 = new String[] {MD5_VALUE2, SHA1_VALUE2, SHA256_VALUE2, SHA3_256_VALUE2, SHA3_384_VALUE2,
			SHA3_512_VALUE2, BLAKE2B_256_VALUE2, BLAKE2B_384_VALUE2, BLAKE2B_512_VALUE2, BLAKE3_VALUE2, ADLER32_VALUE2};
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
		
		checksum.setPropertyValue(SpdxConstants.PROP_CHECKSUM_VALUE, "Bad value");
		assertEquals(1, checksum.verify().size());
		checksum.setValue("0123456789abcdef0123456789abcdef01234567");
		assertEquals(0, verify.size());
		checksum.setPropertyValue(SpdxConstants.PROP_CHECKSUM_ALGORITHM, null);
		assertEquals(1, checksum.verify().size());
		for (Checksum cksum : TEST_CHECKSUMS) {
			assertEquals(0, cksum.verify().size());
		}
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
		for (int i = 0;i < newAlgorithms.length; i++) {
			assertEquals(ALGORITHMS[i], TEST_CHECKSUMS[i].getAlgorithm());
			assertEquals(ALGORITHMS[i], checksumReferences[i].getAlgorithm());
			checksumReferences[i].setAlgorithm(newAlgorithms[i]);
			assertEquals(newAlgorithms[i], TEST_CHECKSUMS[i].getAlgorithm());
			assertEquals(newAlgorithms[i], checksumReferences[i].getAlgorithm());
			assertEquals(VALUES[i], TEST_CHECKSUMS[i].getValue());
			assertEquals(VALUES[i], checksumReferences[i].getValue());
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
				MD5_VALUE2, SHA1_VALUE2, SHA256_VALUE2
		};
		for (int i = 0;i < newValues.length; i++) {
			assertEquals(VALUES[i], TEST_CHECKSUMS[i].getValue());
			assertEquals(VALUES[i], checksumReferences[i].getValue());
			checksumReferences[i].setValue(newValues[i]);
			assertEquals(newValues[i], TEST_CHECKSUMS[i].getValue());
			assertEquals(newValues[i], checksumReferences[i].getValue());
			assertEquals(ALGORITHMS[i], TEST_CHECKSUMS[i].getAlgorithm());
			assertEquals(ALGORITHMS[i], checksumReferences[i].getAlgorithm());
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
		checksum.setPropertyValue(SpdxConstants.PROP_CHECKSUM_ALGORITHM, null);
		assertTrue(checksum.toString().contains("EMPTY"));
		checksum.setPropertyValue(SpdxConstants.PROP_CHECKSUM_VALUE, null);
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
	}
	
	public void testPre23FailsVerification() throws InvalidSPDXAnalysisException {
		assertEquals(1, gmo.createChecksum(ChecksumAlgorithm.ADLER32, ADLER32_VALUE1)
				.verify(Version.TWO_POINT_TWO_VERSION).size());
		assertEquals(1, gmo.createChecksum(ChecksumAlgorithm.BLAKE2b_256, BLAKE2B_256_VALUE1)
				.verify(Version.TWO_POINT_TWO_VERSION).size());
		assertEquals(1, gmo.createChecksum(ChecksumAlgorithm.BLAKE2b_384, BLAKE2B_384_VALUE1)
				.verify(Version.TWO_POINT_TWO_VERSION).size());
		assertEquals(1, gmo.createChecksum(ChecksumAlgorithm.BLAKE2b_512, BLAKE2B_512_VALUE1)
				.verify(Version.TWO_POINT_TWO_VERSION).size());
		assertEquals(1, gmo.createChecksum(ChecksumAlgorithm.BLAKE3, BLAKE3_VALUE1)
				.verify(Version.TWO_POINT_TWO_VERSION).size());
		assertEquals(1, gmo.createChecksum(ChecksumAlgorithm.SHA3_256, SHA3_256_VALUE1)
				.verify(Version.TWO_POINT_TWO_VERSION).size());
		assertEquals(1, gmo.createChecksum(ChecksumAlgorithm.SHA3_384, SHA3_384_VALUE1)
				.verify(Version.TWO_POINT_TWO_VERSION).size());
		assertEquals(1, gmo.createChecksum(ChecksumAlgorithm.SHA3_512, SHA3_512_VALUE1)
				.verify(Version.TWO_POINT_TWO_VERSION).size());
	}

}
