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
package org.spdx.library;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.spdx.library.model.ChecksumAlgorithm;

import junit.framework.TestCase;

/**
 * @author gary
 *
 */
public class SpdxVerificationHelperTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#verifyNonStdLicenseid(java.lang.String)}.
	 */
	public void testVerifyNonStdLicenseid() {
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyNonStdLicenseid(SpdxConstants.NON_STD_LICENSE_ID_PRENUM+"something")));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyNonStdLicenseid("InvalidID")));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#verifyCreator(java.lang.String)}.
	 */
	public void testVerifyCreator() {
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyCreator("Person:me")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyCreator("Organization:Big Brother")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyCreator("Tool:SPDX Tools")));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyCreator("Just ME")));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#verifyOriginator(java.lang.String)}.
	 */
	public void testVerifyOriginator() {
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyOriginator("Person:me")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyOriginator("Organization:Big Brother")));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyOriginator("Tool:SPDX Tools")));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyOriginator("Just ME")));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#verifySupplier(java.lang.String)}.
	 */
	public void testVerifySupplier() {
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifySupplier("Person:me")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifySupplier("Organization:Big Brother")));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifySupplier("Tool:SPDX Tools")));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifySupplier("Just ME")));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#verifyDate(java.lang.String)}.
	 */
	public void testVerifyDate() {
		DateFormat format = new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT);
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDate(format.format(new Date()))));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyDate(new Date().toString())));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#verifyAnnotator(java.lang.String)}.
	 */
	public void testVerifyAnnotator() {
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyAnnotator("Person:me")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyAnnotator("Organization:Big Brother")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyAnnotator("Tool:SPDX Tools")));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyAnnotator("Just ME")));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#isValidExternalDocRef(java.lang.String)}.
	 */
	public void testIsValidExternalDocRef() {
		assertTrue(SpdxVerificationHelper.isValidExternalDocRef("DocumentRef-SPDXA"));
		assertFalse(SpdxVerificationHelper.isValidExternalDocRef("WrongDocRef-SPDXA"));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#isValidUri(java.lang.String)}.
	 */
	public void testIsValidUri() {
		assertTrue(SpdxVerificationHelper.isValidUri("https://spdx.org/spdx-specification-21-web-version#h.h430e9ypa0j9"));
		assertFalse(SpdxVerificationHelper.isValidUri("bad uri"));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#verifyChecksumString(java.lang.String, org.spdx.library.model.ChecksumAlgorithm)}.
	 */
	public void testVerifyChecksumString() {
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyChecksumString("bc527343c7ffc103111f3a694b004e2f", ChecksumAlgorithm.MD5)));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyChecksumString("da39a3ee5e6b4b0d3255bfef95601890afd80709", ChecksumAlgorithm.MD5)));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyChecksumString("da39a3ee5e6b4b0d3255bfef95601890afd80709", ChecksumAlgorithm.SHA1)));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyChecksumString("bc527343c7ffc103111f3a694b004e2f", ChecksumAlgorithm.SHA1)));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyChecksumString("c01b39c7a35ccc3b081a3e83d2c71fa9a767ebfeb45c69f08e17dfe3ef375a7b", ChecksumAlgorithm.SHA256)));
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyChecksumString("bc527343c7ffc103111f3a694b004e2f", ChecksumAlgorithm.SHA256)));
	}

	/**
	 * Test method for {@link org.spdx.library.SpdxVerificationHelper#verifyDownloadLocation(java.lang.String)}.
	 */
	public void testVerifyDownloadLocation() {
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("NONE")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("NOASSERTION")));
		// http
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("http://ftp.gnu.org/gnu/glibc/glibc-ports-2.15.tar.gz")));
		// git
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git://git.myproject.org/MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+https://git.myproject.org/MyProject.git")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+http://git.myproject.org/MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+ssh://git.myproject.org/MyProject.git")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+git://git.myproject.org/MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+git@git.myproject.org:MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git://git.myproject.org/MyProject#src/somefile.c")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+https://git.myproject.org/MyProject#src/Class.java")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git://git.myproject.org/MyProject.git@master")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+https://git.myproject.org/MyProject.git@v1.0")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git://git.myproject.org/MyProject.git@da39a3ee5e6b4b0d3255bfef95601890afd80709")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+https://git.myproject.org/MyProject.git@master#/src/MyClass.cpp")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("git+https://git.myproject.org/MyProject@da39a3ee5e6b4b0d3255bfef95601890afd80709#lib/variable.rb")));
		// Mercurial
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+http://hg.myproject.org/MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+ssh://hg.myproject.org/MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject#src/somefile.c")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject#src/Class.java")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject@da39a3ee5e6b")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject@2019")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject@v1.0")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject@special_feature")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject@master#/src/MyClass.cpp")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("hg+https://hg.myproject.org/MyProject@da39a3ee5e6b#lib/variable.rb")));
		// Subversion
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn://svn.myproject.org/svn/MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+svn://svn.myproject.org/svn/MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+http://svn.myproject.org/svn/MyProject/trunk")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+https://svn.myproject.org/svn/MyProject/trunk")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+https://svn.myproject.org/MyProject#src/somefile.c")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+https://svn.myproject.org/MyProject#src/Class.java")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+https://svn.myproject.org/MyProject/trunk#src/somefile.c")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+https://svn.myproject.org/MyProject/trunk/src/somefile.c")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+https://svn.myproject.org/svn/MyProject/trunk@2019")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+https://svn.myproject.org/MyProject@123#/src/MyClass.cpp")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("svn+https://svn.myproject.org/MyProject/trunk@1234#lib/variable/variable.rb")));
		// Bazaar
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+https://bzr.myproject.org/MyProject/trunk")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+http://bzr.myproject.org/MyProject/trunk")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+sftp://myproject.org/MyProject/trunk")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+ssh://myproject.org/MyProject/trunk")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+ftp://myproject.org/MyProject/trunk")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+lp:MyProject")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+https://bzr.myproject.org/MyProject/trunk#src/somefile.c")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+https://bzr.myproject.org/MyProject/trunk#src/Class.java")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+https://bzr.myproject.org/MyProject/trunk@2019")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+http://bzr.myproject.org/MyProject/trunk@v1.0")));
		assertTrue(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("bzr+https://bzr.myproject.org/MyProject/trunk@2019#src/somefile.c")));
		
		// invalid
		assertFalse(Objects.isNull(SpdxVerificationHelper.verifyDownloadLocation("notsupported+https://bzr.myproject.org/MyProject/trunk@2019#src/somefile.c")));
	}

}
