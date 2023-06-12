package org.spdx.utility.verificationcode;


import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants.SpdxMajorVersion;
import org.spdx.library.model.compat.v2.SpdxFile;
import org.spdx.library.model.compat.v2.SpdxPackageVerificationCode;
import org.spdx.library.model.compat.v2.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.compat.v2.enumerations.FileType;
import org.spdx.library.model.compat.v2.license.SpdxNoAssertionLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;
import org.spdx.storage.simple.InMemSpdxStore;

import junit.framework.TestCase;

public class VerificationCodeGeneratorTest extends TestCase {
	
    static final String SOURCE_PATH = "TestFiles" + File.separator + "spdx-parser-source";

    static final String[] SKIPPED_FILE_NAMES = new String[] {
            "TestFiles" + File.separator + "spdx-parser-source" + File.separator + "org" + File.separator + "spdx" + File.separator + "rdfparser"
                    + File.separator + "DOAPProject.java",
            "TestFiles" + File.separator + "spdx-parser-source" + File.separator + "org" + File.separator + "spdx" + File.separator + "rdfparser"
                    + File.separator + "SPDXFile.java"
    };

    private static final String SHA1_RESULT = "73e91925b5d50b9cc03d4da79f6ad9defcf63de3";

    private static String[] SPDX_FILE_NAMES = new String[] {
            "file/path/abc-not-skipped.java", "file/path/skipped.spdx", "file/path/not-skipped"
    };

    private static String[] SPDX_FILE_SHA1S = new String[] {
            "dddd9215216045864ca5785d1892a00106cf0f6a",
            "bbbb9215216045864ca5785d1892a00106cf0f6a",
            "cccc9215216045864ca5785d1892a00106cf0f6a"
    };

    private SpdxFile[] SPDX_FILES;
    
    private IModelStore modelStore;
    private static String DOCUMENT_URI = "https://TEST/DOCUMENT/URI";

	protected void setUp() throws Exception {
		super.setUp();
		modelStore = new InMemSpdxStore(SpdxMajorVersion.VERSION_2);
        SPDX_FILES = new SpdxFile[SPDX_FILE_NAMES.length];
        for (int i = 0; i < SPDX_FILES.length; i++) {
        	SPDX_FILES[i] = new SpdxFile(modelStore, DOCUMENT_URI, modelStore.getNextId(IdType.Anonymous, DOCUMENT_URI), null, true);
        	SPDX_FILES[i].setName(SPDX_FILE_NAMES[i]);
        	SPDX_FILES[i].getFileTypes().add(FileType.SOURCE);
        	SPDX_FILES[i].getChecksums().add(SPDX_FILES[i].createChecksum(ChecksumAlgorithm.SHA1, SPDX_FILE_SHA1S[i]));
        	SPDX_FILES[i].setLicenseConcluded(new SpdxNoAssertionLicense());
        	SPDX_FILES[i].getLicenseInfoFromFiles().add(new SpdxNoAssertionLicense());
        }
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

    public void testGeneratePackageVerificationCodeFileFileArray() throws NoSuchAlgorithmException, IOException, InvalidSPDXAnalysisException {
        VerificationCodeGenerator vg = new VerificationCodeGenerator(new JavaSha1ChecksumGenerator());
        File sourceDirectory = new File(SOURCE_PATH);
        File[] skippedFiles = new File[SKIPPED_FILE_NAMES.length];
        for (int i = 0; i < skippedFiles.length; i++) {
            skippedFiles[i] = new File(SKIPPED_FILE_NAMES[i]);
        }
        SpdxPackageVerificationCode vc = vg.generatePackageVerificationCode(sourceDirectory, skippedFiles, modelStore, DOCUMENT_URI);
        assertEquals(SHA1_RESULT, vc.getValue());
        compareFileNameArrays(SKIPPED_FILE_NAMES, vc.getExcludedFileNames());
    }
    
    /**
     * @param skippedFileNames
     * @param excludedFileNames
     */
    private void compareFileNameArrays(String[] skippedFileNames,
            Collection<String> excludedFileNames) {
        assertEquals(skippedFileNames.length, excludedFileNames.size());
        for (String skippedFileName : skippedFileNames) {
            boolean found = false;
            String skippedFile = VerificationCodeGenerator.normalizeFilePath(skippedFileName.substring(SOURCE_PATH.length() + 1));
            for (String excludedFileName : excludedFileNames) {
                if (excludedFileName.equals(skippedFile)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail(skippedFile + " not found");
            }
        }
    }
    
    public void testNormalizeFilePath() {
        String s1 = "simple/test.c";
        String ns1 = "./simple/test.c";
        String s2 = "name";
        String ns2 = "./name";
        String s3 = "dos\\file\\name.c";
        String ns3 = "./dos/file/name.c";
        String s4 = "\\leading\\slash";
        String ns4 = "./leading/slash";
        String s5 = "test/./dot/./slash";
        String ns5 = "./test/dot/slash";
        String s6 = "test/parent/../directory/name";
        String ns6 = "./test/directory/name";
        assertEquals(ns1, VerificationCodeGenerator.normalizeFilePath(s1));
        assertEquals(ns2, VerificationCodeGenerator.normalizeFilePath(s2));
        assertEquals(ns3, VerificationCodeGenerator.normalizeFilePath(s3));
        assertEquals(ns4, VerificationCodeGenerator.normalizeFilePath(s4));
        assertEquals(ns5, VerificationCodeGenerator.normalizeFilePath(s5));
        assertEquals(ns6, VerificationCodeGenerator.normalizeFilePath(s6));
    }
}
