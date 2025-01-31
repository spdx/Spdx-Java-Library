/*
 * SPDX-FileCopyrightText: Copyright (c) 2024 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * <p>
 *       http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.utility.compare;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.LicenseInfoFactory;
import org.spdx.library.model.v3_0_1.expandedlicensing.ListedLicense;


/**
 * Compare consistency helper
 */
public class CompareConsistencyHelper {

    /**
     * Tests for consistency across the various comparison methods in LicenseCompareHelper.
     *
     * Note: assumes that `text` contains just a single license text, with no extraneous prefix or suffix text.
     *
     * @param licenseId The SPDX license identifier that's expected to be detected within `text`.
     * @param text      The license text being used to test API consistency.
     * @return null if no inconsistencies were found, or a String describing the detected inconsistencies.
     */
    public static String explainCompareInconsistencies(final String licenseId, final String text) throws InvalidSPDXAnalysisException, SpdxCompareException {
        StringBuilder result = new StringBuilder();

        // PRECONDITIONS
        if (licenseId == null || licenseId.trim().length() == 0) {
            throw new IllegalArgumentException("licenseId was null or blank");
        }

        if (text == null || text.trim().length() == 0) {
            throw new IllegalArgumentException("text was null or blank");
        }

        // Body
        final ListedLicense listedLicense = LicenseInfoFactory.getListedLicenseById(licenseId);

        if (listedLicense == null) {
            throw new IllegalArgumentException("Could not find listed license for identifier " + licenseId);
        }

        final boolean      isDifferenceFound                    = LicenseCompareHelper.isTextStandardLicense(listedLicense, text).isDifferenceFound();
        final boolean      isStandardLicenseWithinText          = LicenseCompareHelper.isStandardLicenseWithinText(text, listedLicense);
        final List<String> matchingStandardLicenseIds           = Arrays.asList(LicenseCompareHelper.matchingStandardLicenseIds(text));
        final List<String> matchingStandardLicenseIdsWithinText = LicenseCompareHelper.matchingStandardLicenseIdsWithinText(text);

        // Note: we sort these lists because we don't care about different orderings within them - just that they contain the same elements (in any order)
        Collections.sort(matchingStandardLicenseIds);
        Collections.sort(matchingStandardLicenseIdsWithinText);

        if (isDifferenceFound == isStandardLicenseWithinText) {  // Note: this condition may seem backwards, but only because one variable indicates whether there was a difference, while the other indicates whether the license was found (they're logically opposite)
            result.append("  * .isTextStandardLicense() and .isStandardLicenseWithinText()\n");
        }

        if (!isDifferenceFound && !matchingStandardLicenseIds.contains(licenseId)) {
            result.append("  * .isTextStandardLicense() and .matchingStandardLicenseIds()\n");
        }

        if (!isDifferenceFound && !matchingStandardLicenseIdsWithinText.contains(licenseId)) {
            result.append("  * .isTextStandardLicense() and .matchingStandardLicensesWithinText()\n");
        }

        if (isStandardLicenseWithinText && !matchingStandardLicenseIds.contains(licenseId)) {
            result.append("  * .isStandardLicenseWithinText() and .matchingStandardLicenseIds()\n");
        }

        if (isStandardLicenseWithinText && !matchingStandardLicenseIdsWithinText.contains(licenseId)) {
            result.append("  * .isStandardLicenseWithinText() and .matchingStandardLicensesWithinText()\n");
        }

        if (!Objects.equals(matchingStandardLicenseIds, matchingStandardLicenseIdsWithinText)) {
            result.append("  * .matchingStandardLicenseIds() and .matchingStandardLicenseIdsWithinText()\n");
        }

        if (result.toString().trim().isEmpty()) {
            return null;
        } else {
            return(("While testing API consistency with a " + licenseId + " text, inconsistencies were found between LicenseCompareHelper APIs:\n" + result.toString()).trim());
        }
    }

}
