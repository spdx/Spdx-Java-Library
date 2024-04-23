package org.spdx.utility.compare;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.library.model.license.SpdxListedLicense;

public class CompareConsistencyHelper {

    /**
     * Tests for consistency across the various comparison methods in LicenseCompareHelper, and returns either null
     * (no inconsistencies found), or a String describing all of the detected inconsistencies.
     *
     * Note: assumes that `text` contains just a single license text, with no extraneous prefix or suffix text.
     *
     * @param licenseId The SPDX license identifier that's expected to be detected within text.
     * @param text      The license text being tested.
     * @return Whether consistency across the APIs under test was found or not.
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
        final SpdxListedLicense listedLicense = LicenseInfoFactory.getListedLicenseById(licenseId);

        if (listedLicense == null) {
            throw new IllegalArgumentException("could not find listed license for identifier" + licenseId);
        }

        final boolean      differenceFound                    = LicenseCompareHelper.isTextStandardLicense(listedLicense, text).isDifferenceFound();
        final boolean      standardLicenseWithinText          = LicenseCompareHelper.isStandardLicenseWithinText(text, listedLicense);
        final List<String> standardLicensesWithinText         = Arrays.asList(LicenseCompareHelper.matchingStandardLicenseIds(text));
        final List<String> matchingStandardLicensesWithinText = LicenseCompareHelper.matchingStandardLicenseIdsWithinText(text);

        // Note: we sort the elements because we don't care about different orderings within each list - just that they contain the same elements (in any order)
        Collections.sort(standardLicensesWithinText);
        Collections.sort(matchingStandardLicensesWithinText);

        if (differenceFound == standardLicenseWithinText) {  // Note: this condition seems backwards, but only because one variable indicates whether there was a difference, while the other indicates whether the license was found (they're logically opposite)
            result.append("  * LicenseCompareHelper.isTextStandardLicense() and LicenseCompareHelper.isStandardLicenseWithinText()\n");
        }

        if (standardLicenseWithinText && (standardLicensesWithinText == null || standardLicensesWithinText.isEmpty())) {
            result.append("  * LicenseCompareHelper.isStandardLicenseWithinText() and LicenseCompareHelper.matchingStandardLicenseIds()\n");
        }

        if (standardLicenseWithinText && (matchingStandardLicensesWithinText == null || matchingStandardLicensesWithinText.isEmpty())) {
            result.append("  * LicenseCompareHelper.isStandardLicenseWithinText() and LicenseCompareHelper.matchingStandardLicensesWithinText()\n");
        }

        if (!Objects.equals(standardLicensesWithinText, matchingStandardLicensesWithinText)) {
            result.append("  * LicenseCompareHelper.matchingStandardLicenseIds() and LicenseCompareHelper.matchingStandardLicenseIdsWithinText()\n");
        }

        if (result.toString().trim().isEmpty()) {
            return null;
        } else {
            return(("While testing API consistency with a text known to be " + licenseId + ", inconsistencies were found between:\n" + result.toString()).trim());
        }
    }

}
