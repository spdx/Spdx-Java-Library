/**
 * Copyright (c) 2015 Source Auditor Inc.
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
 *
*/
package org.spdx.utility.compare;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SpdxDocument;

/**
 * Helper class for unit tests
 * @author Gary
 *
 */
public class UnitTestHelper {

	/**
	 * @param a1
	 * @param a2
	 */
	public static boolean isArraysEqual(Object[] a1,
			Object[] a2) {
		if (a1 == null) {
			return(a2 == null);
		}
		if (a2 == null) {
			return false;
		}
		if (a1.length != a2.length) {
			return false;
		}
		for (int i = 0; i < a1.length; i++) {
			boolean found = false;
			for (int j = 0; j < a2.length; j++) {
				if (a1[i].equals(a2[j])) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param filePath Path for file
	 * @return Text from the file
	 * @throws IOException 
	 */
	public static String fileToText(String filePath) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String s:Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

	public static boolean isListsEqual(List<? extends Object> expected, List<? extends Object> result) {
		if (Objects.isNull(expected)) {
			return Objects.isNull(result);
		}
		if (Objects.isNull(result)) {
			return false;
		}
		if (expected.size() != result.size()) {
			return false;
		}
		return expected.containsAll(result);
	}
	
	public static boolean isListsEquivalent(List<? extends ModelObject> expected, List<? extends ModelObject> result) throws InvalidSPDXAnalysisException {
		if (Objects.isNull(expected)) {
			return Objects.isNull(result);
		}
		if (Objects.isNull(result)) {
			return false;
		}
		if (expected.size() != result.size()) {
			return false;
		}
		for (ModelObject o1:expected) {
			boolean found = false;
			for (ModelObject o2:result) {
				if (o1.equivalent(o2)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public static void copyObjectsToDoc(SpdxDocument doc, Collection<? extends ModelObject> modelObjects) throws InvalidSPDXAnalysisException {
		for (ModelObject mo:modelObjects) {
			doc.getCopyManager().copy(doc.getModelStore(), doc.getDocumentUri(), mo.getModelStore(), 
					mo.getDocumentUri(), mo.getId(), mo.getType());
		}
	}

}
