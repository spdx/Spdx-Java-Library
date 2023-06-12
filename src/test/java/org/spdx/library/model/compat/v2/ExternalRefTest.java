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
package org.spdx.library.model.compat.v2;

import java.net.URI;
import java.net.URISyntaxException;

import org.spdx.library.DefaultModelStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstantsCompatV2;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.compat.v2.enumerations.ReferenceCategory;
import org.spdx.library.referencetype.compat.v2.ListedReferenceTypes;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;

import junit.framework.TestCase;

/**
 * @author Gary O'Neall
 *
 */
public class ExternalRefTest extends TestCase {
	
	private static final String[] REFERENCE_LOCATORS = new String[] {
			"org.apache.tomcat:tomcat:9.0.0.M4", "Microsoft.AspNet.MVC/5.0.0",
			"cpe:2.3:o:canonical:ubuntu_linux:10.04::lts:*:*:*:*:*"
		};

		private static final String[] COMMENTS = new String[] {
			"comment one", "comment two", ""
		};

		private static final String[] REFERENCE_TYPE_NAMES = new String[] {
			"maven-central", "nuget", "cpe23Type"
		};

		ReferenceCategory[] REFERENCE_CATEGORIES = {ReferenceCategory.PACKAGE_MANAGER,
				ReferenceCategory.PACKAGE_MANAGER,
				ReferenceCategory.SECURITY
		};

		ExternalRef[] TEST_REFERENCES;
		IModelStore store;
		String docUri;
		ModelCopyManager copyManager;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DefaultModelStore.reset(SpdxMajorVersion.VERSION_2);
		store = DefaultModelStore.getDefaultModelStore();
		docUri = DefaultModelStore.getDefaultDocumentUri();
		copyManager = DefaultModelStore.getDefaultCopyManager();
		TEST_REFERENCES = new ExternalRef[REFERENCE_CATEGORIES.length];
		for (int i = 0; i < REFERENCE_CATEGORIES.length; i++) {
			TEST_REFERENCES[i] = new ExternalRef(store, docUri, store.getNextId(IdType.Anonymous, docUri), copyManager, true);
			TEST_REFERENCES[i].setReferenceCategory(REFERENCE_CATEGORIES[i]);
			TEST_REFERENCES[i].setReferenceType(new ReferenceType(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + REFERENCE_TYPE_NAMES[i]));
			TEST_REFERENCES[i].setReferenceLocator(REFERENCE_LOCATORS[i]);
			TEST_REFERENCES[i].setComment(COMMENTS[i]);
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.ExternalRef#verify()}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testVerify() throws InvalidSPDXAnalysisException {
		for (int i = 0; i < TEST_REFERENCES.length; i++) {
			assertEquals(0, TEST_REFERENCES[i].verify().size());
		}
		ExternalRef noCategory = new ExternalRef(store, docUri, store.getNextId(IdType.Anonymous, docUri),copyManager, true);
		noCategory.setReferenceType(new ReferenceType(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + REFERENCE_TYPE_NAMES[0]));
		noCategory.setReferenceLocator(REFERENCE_LOCATORS[0]);
		noCategory.setComment(COMMENTS[0]);
		assertEquals(1, noCategory.verify().size());
		ExternalRef noReferenceType = new ExternalRef(store, docUri, store.getNextId(IdType.Anonymous, docUri),copyManager, true);
		noReferenceType.setReferenceCategory(REFERENCE_CATEGORIES[0]);
		noReferenceType.setReferenceLocator(REFERENCE_LOCATORS[0]);
		noReferenceType.setComment(COMMENTS[0]);
		assertEquals(1, noReferenceType.verify().size());
		ExternalRef noRferenceLocator = new ExternalRef(store, docUri, store.getNextId(IdType.Anonymous, docUri),copyManager, true);
		noRferenceLocator.setReferenceCategory(REFERENCE_CATEGORIES[0]);
		noRferenceLocator.setReferenceType(new ReferenceType(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + REFERENCE_TYPE_NAMES[0]));
		noRferenceLocator.setComment(COMMENTS[0]);
		assertEquals(1, noRferenceLocator.verify().size());
		ExternalRef noComment =new ExternalRef(store, docUri, store.getNextId(IdType.Anonymous, docUri),copyManager, true);
		noComment.setReferenceCategory(REFERENCE_CATEGORIES[0]);
		noComment.setReferenceType(new ReferenceType(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + REFERENCE_TYPE_NAMES[0]));
		noComment.setReferenceLocator(REFERENCE_LOCATORS[0]);
		assertEquals(0, noComment.verify().size());
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.ExternalRef#compareTo(org.spdx.library.model.compat.v2.compat.v2.ExternalRef)}.
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void testCompareTo() throws InvalidSPDXAnalysisException {
		ExternalRef copy = new ExternalRef(store, docUri, store.getNextId(IdType.Anonymous, docUri),copyManager, true);
		copy.setComment(TEST_REFERENCES[2].getComment().get());
		copy.setReferenceCategory(TEST_REFERENCES[2].getReferenceCategory());
		copy.setReferenceLocator(TEST_REFERENCES[2].getReferenceLocator());
		copy.setReferenceType(TEST_REFERENCES[2].getReferenceType());
	
		assertEquals(0, copy.compareTo(TEST_REFERENCES[2]));
		assertTrue(TEST_REFERENCES[0].compareTo(TEST_REFERENCES[1]) < 0);
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.ExternalRef#setComment(java.lang.String)}.
	 */
	public void testSetComment() throws InvalidSPDXAnalysisException {
		String[] changedComments = new String[] {
			"changed1", "changed2", "changed3"
		};
		for (int i = 0; i < TEST_REFERENCES.length; i++) {
			assertEquals(COMMENTS[i], TEST_REFERENCES[i].getComment().get());
			TEST_REFERENCES[i].setComment(changedComments[i]);
			assertEquals(changedComments[i], TEST_REFERENCES[i].getComment().get());
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.ExternalRef#setReferenceCategory(org.spdx.library.model.compat.v2.compat.v2.enumerations.ReferenceCategory)}.
	 */
	public void testSetReferenceCategory() throws InvalidSPDXAnalysisException {
		ExternalRef er = new ExternalRef(store, docUri, store.getNextId(IdType.Anonymous, docUri),copyManager,  true);
		er.setReferenceCategory(ReferenceCategory.PACKAGE_MANAGER);
		ReferenceCategory retval = er.getReferenceCategory();
		assertEquals(ReferenceCategory.PACKAGE_MANAGER, retval);
		ReferenceCategory[] changedCategories = new ReferenceCategory[] {
			REFERENCE_CATEGORIES[1], REFERENCE_CATEGORIES[2], REFERENCE_CATEGORIES[0]
		};
		for (int i = 0; i < TEST_REFERENCES.length; i++) {
			assertEquals(REFERENCE_CATEGORIES[i], TEST_REFERENCES[i].getReferenceCategory());
			TEST_REFERENCES[i].setReferenceCategory(changedCategories[i]);
			assertEquals(changedCategories[i], TEST_REFERENCES[i].getReferenceCategory());
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.ExternalRef#setReferenceType(org.spdx.library.model.compat.v2.compat.v2.ReferenceType)}.
	 * @throws URISyntaxException 
	 */
	public void testSetReferenceType() throws InvalidSPDXAnalysisException, URISyntaxException {
		ReferenceType[] changedTypes = new ReferenceType[] {
			new ReferenceType(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + REFERENCE_TYPE_NAMES[1]),
			new ReferenceType(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + REFERENCE_TYPE_NAMES[2]),
			new ReferenceType(SpdxConstantsCompatV2.SPDX_LISTED_REFERENCE_TYPES_PREFIX + REFERENCE_TYPE_NAMES[0])
		};
		for (int i = 0; i < TEST_REFERENCES.length; i++) {
			assertEquals(REFERENCE_TYPE_NAMES[i], ListedReferenceTypes.getListedReferenceTypes().getListedReferenceName(new URI(TEST_REFERENCES[i].getReferenceType().getIndividualURI())));
			TEST_REFERENCES[i].setReferenceType(changedTypes[i]);
			ReferenceType refType = TEST_REFERENCES[i].getReferenceType();
			assertEquals(changedTypes[i].getIndividualURI(), refType.getIndividualURI());
		}
	}

	/**
	 * Test method for {@link org.spdx.library.model.compat.v2.compat.v2.ExternalRef#setReferenceLocator(java.lang.String)}.
	 */
	public void testSetReferenceLocator() throws InvalidSPDXAnalysisException {
		String[] changedLocators = new String[] {
			"changed1", "changed2", "changed3"
		};
		for (int i = 0; i < TEST_REFERENCES.length; i++) {
			assertEquals(REFERENCE_LOCATORS[i], TEST_REFERENCES[i].getReferenceLocator());
			TEST_REFERENCES[i].setReferenceLocator(changedLocators[i]);
			assertEquals(changedLocators[i], TEST_REFERENCES[i].getReferenceLocator());
		}
	}

}
