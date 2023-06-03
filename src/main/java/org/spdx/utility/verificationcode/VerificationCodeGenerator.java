/**
 * Copyright (c) 2011 Source Auditor Inc.
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
package org.spdx.utility.verificationcode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.compat.v2.SpdxFile;
import org.spdx.library.model.compat.v2.SpdxPackageVerificationCode;
import org.spdx.storage.IModelStore;
import org.spdx.storage.IModelStore.IdType;


/**
 * Generates a package verification code from a directory of source code or an array of <code>SPDXFile</code>s.
 *
 * A class implementing the IFileChecksumGenerator is supplied as a parameter to the constructor.
 * The method <code>getFileChecksum</code> is called for each file in the directory.  This can
 * be used as a hook to capture all files in the directory and capture the checksum values at
 * a file level.
 *
 * @author Gary O'Neall
 *
 */
public class VerificationCodeGenerator {

	private IFileChecksumGenerator fileChecksumGenerator;

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
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxPackageVerificationCode generatePackageVerificationCode(SpdxFile[] spdxFiles, 
			String[] skippedFilePaths, IModelStore modelStore, String documentUri) throws NoSuchAlgorithmException, InvalidSPDXAnalysisException {
		if (spdxFiles == null) {
			return null;
		}
		Set<String> skippedFilePathSet = new TreeSet<>();
		if (skippedFilePaths != null) {
			for (int i = 0; i < skippedFilePaths.length; i++) {
				if (skippedFilePaths[i] != null) {
					skippedFilePathSet.add(skippedFilePaths[i]);
				}
			}
		}
		List<String> fileChecksums = new ArrayList<>();
		for (int i = 0; i < spdxFiles.length; i++) {
		    if (spdxFiles[i] != null) {
		        Optional<String> name = spdxFiles[i].getName();
	            if (name.isPresent() && !skippedFilePathSet.contains(name.get())) {
	                fileChecksums.add(spdxFiles[i].getSha1());
	            }
		    }
		}
		return generatePackageVerificationCode(fileChecksums, skippedFilePathSet.toArray(new String[skippedFilePathSet.size()]), modelStore, documentUri);
	}


	/**
	 * Generate the SPDX Package Verification Code from a directory of files included in the archive
	 * @param sourceDirectory
	 * @param modelStore where the resultant VerificationCode is store
	 * @param documentUri document URI where the VerificationCode is stored
	 * @return PackageVerificationCode based on the files in the sourceDirectory
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxPackageVerificationCode generatePackageVerificationCode(File sourceDirectory, 
			File[] skippedFiles, IModelStore modelStore, String documentUri) throws NoSuchAlgorithmException, IOException, InvalidSPDXAnalysisException {
		// create a sorted list of file paths
		Set<String> skippedFilesPath = new TreeSet<>();
		String rootOfDirectory = sourceDirectory.getAbsolutePath();
		int rootLen = rootOfDirectory.length()+1;
		for (int i = 0; i < skippedFiles.length; i++) {
			String skippedPath = normalizeFilePath(skippedFiles[i].getAbsolutePath().substring(rootLen));
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
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidSPDXAnalysisException
	 */
	protected SpdxPackageVerificationCode generatePackageVerificationCode(List<String> fileChecksums,
			String[] skippedFilePaths, IModelStore modelStore, String documentUri) throws NoSuchAlgorithmException, InvalidSPDXAnalysisException {
		Collections.sort(fileChecksums);
		MessageDigest verificationCodeDigest = MessageDigest.getInstance("SHA-1");
		for (int i = 0;i < fileChecksums.size(); i++) {
			byte[] hashInput = fileChecksums.get(i).getBytes(Charset.forName("UTF-8"));
			verificationCodeDigest.update(hashInput);
		}
		String value = convertChecksumToString(verificationCodeDigest.digest());
		SpdxPackageVerificationCode retval = new SpdxPackageVerificationCode(modelStore, documentUri, modelStore.getNextId(IdType.Anonymous, documentUri), null, true);
		retval.setValue(value);
		for (String skippedPath:skippedFilePaths) {
			retval.getExcludedFileNames().add(skippedPath);
		}
		return retval;
	}

	/**
	 * Collect the file level checksums and filenames
	 * @param prefixForRelative The portion of the filepath which preceeds the relative file path for the archive
	 * @param sourceDirectory
	 * @param fileNameAndChecksums
	 * @throws IOException
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
		for (int i = 0; i < filesAndDirs.length; i++) {
			if (filesAndDirs[i].isDirectory()) {
				collectFileData(prefixForRelative, filesAndDirs[i], fileNameAndChecksums, skippedFiles);
			} else {
				String filePath = normalizeFilePath(filesAndDirs[i].getAbsolutePath()
						.substring(prefixForRelative.length()+1));
				if (!skippedFiles.contains(filePath)) {
					String checksumValue = this.fileChecksumGenerator.getFileChecksum(filesAndDirs[i]).toLowerCase();
					fileNameAndChecksums.add(checksumValue);
				}
			}
		}
	}

	/**
	 * Normalizes a file path per the SPDX spec
	 * @param nonNormalizedFilePath
	 * @return
	 */
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
	 * @param digest
	 * @return
	 */
	private static String convertChecksumToString(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < digest.length; i++) {
			String hex = Integer.toHexString(0xff & digest[i]);
			if (hex.length() < 2) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
	/**
	 * @param sourceDirectory
	 * @param modelStore where the resultant VerificationCode is store
	 * @param documentUri document URI where the VerificationCode is stored
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidSPDXAnalysisException 
	 */
	public SpdxPackageVerificationCode generatePackageVerificationCode(
			File sourceDirectory, IModelStore modelStore, String documentUri) throws NoSuchAlgorithmException, IOException, InvalidSPDXAnalysisException {
		return generatePackageVerificationCode(sourceDirectory, new File[0], modelStore, documentUri);
	}
}
