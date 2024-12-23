/**
 * Copyright (c) 2011 Source Auditor Inc.
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
 *
*/
package org.spdx.utility.verificationcode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.model.v2.SpdxFile;
import org.spdx.library.model.v2.SpdxPackageVerificationCode;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;


/**
 * Generates a package verification code from a directory of source code or an array of <code>SPDXFile</code>s.
 * <p>
 * A class implementing the IFileChecksumGenerator is supplied as a parameter to the constructor.
 * The method <code>getFileChecksum</code> is called for each file in the directory.  This can
 * be used as a hook to capture all files in the directory and capture the checksum values at
 * a file level.
 *
 * @author Gary O'Neall
 */
public class VerificationCodeGenerator {

	private final IFileChecksumGenerator fileChecksumGenerator;

	public VerificationCodeGenerator(IFileChecksumGenerator fileChecksumGenerator) {
		this.fileChecksumGenerator = fileChecksumGenerator;
	}

	/**
	 * Generate the SPDX Package Verification Code from an array of SPDXFiles
	 * @param spdxFiles Files to generate the VerificationCode from
	 * @param skippedFilePaths File path names to not include in the VerificationCode
	 * @param modelStore where the resultant VerificationCode is store
	 * @param documentUri document URI where the VerificationCode is stored
	 * @return VerificationCode based on all files in spdxFiles minus the skippedFilePaths
	 * @throws NoSuchAlgorithmException unexpected checksum calculation error
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	public SpdxPackageVerificationCode generatePackageVerificationCode(SpdxFile[] spdxFiles, 
			String[] skippedFilePaths, IModelStore modelStore, String documentUri) throws NoSuchAlgorithmException, InvalidSPDXAnalysisException {
		if (spdxFiles == null) {
			return null;
		}
		Set<String> skippedFilePathSet = new TreeSet<>();
		if (skippedFilePaths != null) {
            for (String skippedFilePath : skippedFilePaths) {
                if (skippedFilePath != null) {
                    skippedFilePathSet.add(skippedFilePath);
                }
            }
		}
		List<String> fileChecksums = new ArrayList<>();
        for (SpdxFile spdxFile : spdxFiles) {
            if (spdxFile != null) {
                Optional<String> name = spdxFile.getName();
                if (name.isPresent() && !skippedFilePathSet.contains(name.get())) {
                    fileChecksums.add(spdxFile.getSha1());
                }
            }
        }
		return generatePackageVerificationCode(fileChecksums, skippedFilePathSet.toArray(new String[0]), modelStore, documentUri);
	}


	/**
	 * Generate the SPDX Package Verification Code from a directory of files included in the archive
	 * @param sourceDirectory source directory for the package verification code
	 * @param modelStore where the resultant VerificationCode is store
	 * @param documentUri document URI where the VerificationCode is stored
	 * @return PackageVerificationCode based on the files in the sourceDirectory
	 * @throws NoSuchAlgorithmException unexpected error creating checksums
	 * @throws IOException on file or directory read errors
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	public SpdxPackageVerificationCode generatePackageVerificationCode(File sourceDirectory, 
			File[] skippedFiles, IModelStore modelStore, String documentUri) throws NoSuchAlgorithmException, IOException, InvalidSPDXAnalysisException {
		// create a sorted list of file paths
		Set<String> skippedFilesPath = new TreeSet<>();
		String rootOfDirectory = sourceDirectory.getAbsolutePath();
		int rootLen = rootOfDirectory.length()+1;
        for (File skippedFile : skippedFiles) {
            String skippedPath = normalizeFilePath(skippedFile.getAbsolutePath().substring(rootLen));
            skippedFilesPath.add(skippedPath);
        }
		List<String> fileChecksums = new ArrayList<>();
		collectFileData(rootOfDirectory, sourceDirectory, fileChecksums, skippedFilesPath);
		String[] skippedFileNames = new String[skippedFilesPath.size()];
		Iterator<String> iter = skippedFilesPath.iterator();
		int i = 0;
		while (iter.hasNext()) {
			skippedFileNames[i++] = iter.next();
		}
		return generatePackageVerificationCode(fileChecksums, skippedFileNames, modelStore, documentUri);
	}

	/**
	 * @param fileChecksums used to create the verification code value
	 * @param skippedFilePaths list of files skipped when calculating the verification code
	 * @param modelStore where the resultant VerificationCode is store
	 * @param documentUri document URI where the VerificationCode is stored
	 * @return a PackageVerificationCode with the value created from the fileChecksums
	 * @throws NoSuchAlgorithmException unexpected error creating checksums
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	protected SpdxPackageVerificationCode generatePackageVerificationCode(List<String> fileChecksums,
			String[] skippedFilePaths, IModelStore modelStore, String documentUri) throws NoSuchAlgorithmException, InvalidSPDXAnalysisException {
		Collections.sort(fileChecksums);
		MessageDigest verificationCodeDigest = MessageDigest.getInstance("SHA-1");
        for (String fileChecksum : fileChecksums) {
            byte[] hashInput = fileChecksum.getBytes(StandardCharsets.UTF_8);
            verificationCodeDigest.update(hashInput);
        }
		String value = convertChecksumToString(verificationCodeDigest.digest());
		SpdxPackageVerificationCode retval = new SpdxPackageVerificationCode(modelStore, documentUri, modelStore.getNextId(IdType.Anonymous), null, true);
		retval.setValue(value);
		for (String skippedPath:skippedFilePaths) {
			retval.getExcludedFileNames().add(skippedPath);
		}
		return retval;
	}

	/**
	 * Collect the file level checksums and filenames
	 * @param prefixForRelative The portion of the filepath which precedes the relative file path for the archive
	 * @param sourceDirectory directory to collectd the file data from
	 * @param fileNameAndChecksums resultant list of file names and checksums - added to in this method
	 * @param skippedFiles files to be ignored in the package verification result
	 * @throws IOException on IO error reading the directory of file
	 */
	private void collectFileData(String prefixForRelative, File sourceDirectory,
			List<String> fileNameAndChecksums, Set<String> skippedFiles) throws IOException {
		if (!sourceDirectory.isDirectory()) {
			return;
		}
		File[] filesAndDirs = sourceDirectory.listFiles();
		if (filesAndDirs == null) {
			return;
		}
        for (File filesAndDir : filesAndDirs) {
            if (filesAndDir.isDirectory()) {
                collectFileData(prefixForRelative, filesAndDir, fileNameAndChecksums, skippedFiles);
            } else {
                String filePath = normalizeFilePath(filesAndDir.getAbsolutePath()
                        .substring(prefixForRelative.length() + 1));
                if (!skippedFiles.contains(filePath)) {
                    String checksumValue = this.fileChecksumGenerator.getFileChecksum(filesAndDir).toLowerCase();
                    fileNameAndChecksums.add(checksumValue);
                }
            }
        }
	}

	/**
	 * Normalizes a file path per the SPDX spec
	 * @param nonNormalizedFilePath original file path - may be unix or DOS format
	 * @return file path normalized per SPDX spec
	 */
	@SuppressWarnings("StatementWithEmptyBody")
    public static String normalizeFilePath(String nonNormalizedFilePath) {
		String filePath = nonNormalizedFilePath.replace('\\', '/').trim();
		if (filePath.contains("../")) {
			// need to remove these references
			String[] filePathParts = filePath.split("/");
			StringBuilder normalizedFilePath = new StringBuilder();
			for (int j = 0; j < filePathParts.length; j++) {
				if (j+1 < filePathParts.length && filePathParts[j+1].equals("..")) {
					// skip this directory
				} else if (filePathParts[j].equals("..")) {
					// remove these from the filePath
				} else {
					if (j > 0) {
						normalizedFilePath.append('/');
					}
					normalizedFilePath.append(filePathParts[j]);
				}
			}
			filePath = normalizedFilePath.toString();
		}
		filePath = filePath.replace("./", "");
		if (!filePath.isEmpty() && filePath.charAt(0) == '/') {
			filePath = "." + filePath;
		} else {
			filePath = "./" + filePath;
		}
		return filePath;
	}
	/**
	 * Convert a byte array SHA-1 digest into a 40 character hex string
	 * @param digest message digest from checksum calculation
	 * @return string representation of the checksum
	 */
	private static String convertChecksumToString(byte[] digest) {
		StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() < 2) {
                sb.append('0');
            }
            sb.append(hex);
        }
		return sb.toString();
	}
	/**
	 * @param sourceDirectory directory to create the verification code for
	 * @param modelStore where the resultant VerificationCode is store
	 * @param documentUri document URI where the VerificationCode is stored
	 * @return SPDX package verification code
	 * @throws NoSuchAlgorithmException unexpected error creating checksums
	 * @throws IOException on IO error reading files or directories
	 * @throws InvalidSPDXAnalysisException on SPDX parsing error
	 */
	public SpdxPackageVerificationCode generatePackageVerificationCode(
			File sourceDirectory, IModelStore modelStore, String documentUri) throws NoSuchAlgorithmException, IOException, InvalidSPDXAnalysisException {
		return generatePackageVerificationCode(sourceDirectory, new File[0], modelStore, documentUri);
	}
}
