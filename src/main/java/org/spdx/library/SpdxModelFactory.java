/**
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
package org.spdx.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.core.*;
import org.spdx.library.model.v2.SpdxModelInfoV2_X;
import org.spdx.library.model.v3_0_1.SpdxModelInfoV3_0;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * Main entrypoint for the SPDX Java Library
 * <p>
 * This is a static class used to manage the different versions of the SPDX spec by
 * creating different model classes based on the version of the spec.
 * <p>
 * Since the release of the SPDX spec version 3.0, the Java classes were generated.
 * <p>
 * Each generated set of classes generated for a specific version are in a separate library / Jar file.
 * <p>
 * These generated classes are registered in the Core model registry
 * <p>
 * The <code>inflateModelObject</code> methods will create an initial object based on the name of the type
 * 
 * @author Gary O'Neall
 */
@SuppressWarnings("unused")
public class SpdxModelFactory {

	static final Logger logger = LoggerFactory.getLogger(SpdxModelFactory.class.getName());
	
	static {
		// register the supported spec version models
		ModelRegistry.getModelRegistry().registerModel(new SpdxModelInfoV2_X());
		ModelRegistry.getModelRegistry().registerModel(new SpdxModelInfoV3_0());
	}
	
	public static final String IMPLEMENTATION_VERSION = "2.0.0";

	static final String DEFAULT_DOCUMENT_URI = "https://default/spdx/document";

	private static final Object INIT_LOCK = new Object();

	/**
	 * Static class private constructor
	 */
	private SpdxModelFactory() {
		// Static class
	}
	
	/**
	 * @param version version to check
	 * @return null if valid, otherwise will return a message
	 */
	static String verifyVersion(String version) {
		if (Objects.isNull(version)) {
			return "Null version";
		}
		if (!ModelRegistry.getModelRegistry().containsSpecVersion(version)) {
			return "Spec version is not supported";
		}
		return null;
	}
	
	/**
	 * @return the latest version of the spec supported by the library
	 */
	public static String getLatestSpecVersion() {
		List<String> allVersions = ModelRegistry.getModelRegistry().getSupportedVersions();
		List<String> preVersion2Versions = new ArrayList<>();
		List<String> post2Versions = new ArrayList<>();
		for (String version:allVersions) {
			if (version.startsWith("SPDX-")) {
				preVersion2Versions.add(version);
			} else {
				post2Versions.add(version);
			}
		}
		if (!post2Versions.isEmpty()) {
			Collections.sort(post2Versions);
			return post2Versions.get(post2Versions.size()-1);
		} else if (!preVersion2Versions.isEmpty()) {
			return preVersion2Versions.get(preVersion2Versions.size()-1);
		} else {
			return "";
		}
	}

	/**
	 * This static method is a convenience to load this class and initialize the supported model versions and
	 * initialize the DefaultModelStore with default values
	 * <p>
	 * It should be called before using any other functionality from the library
	 */
	public static void init() {
		synchronized (INIT_LOCK) {
			if (!DefaultModelStore.isInitialized()) {
				DefaultModelStore.initialize(new InMemSpdxStore(), DEFAULT_DOCUMENT_URI, new ModelCopyManager());
			}
		}
	}

	/**
	 * If the object exists in the model store, it will be "inflated" back to the Java object.
	 * If the object does not exist AND the create parameter is true, a new object will be created and
	 * its inflated form will be returned
	 * @param modelStore store to use for the inflated object
	 * @param objectUri URI of the element or SPDX object
	 * @param type Type of the object to create
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 *@param specVersion version of the SPDX spec the object complies with
	 * @param create if true, create the model object ONLY if it does not already exist
	 * @param idPrefix optional prefix used for any new object URI's created in support of this model object
	 * @return model object of type type
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static CoreModelObject inflateModelObject(IModelStore modelStore, String objectUri, 
			String type, IModelCopyManager copyManager,
			String specVersion, boolean create, @Nullable String idPrefix) throws InvalidSPDXAnalysisException {
		return ModelRegistry.getModelRegistry().inflateModelObject(modelStore, objectUri, type, 
				copyManager, specVersion, create, idPrefix);
	}
	
	/**
	 * For the most recent spec version supported:
	 * If the object exists in the model store, it will be "inflated" back to the Java object.
	 * If the object does not exist AND the create parameter is true, a new object will be created and
	 * its inflated form will be returned
	 * @param modelStore store to use for the inflated object
	 * @param objectUri URI of the external element
	 * @param type Type of the object to create
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 * @param create if true, create the model object ONLY if it does not already exist
	 * @param idPrefix optional prefix used for any new object URI's created in support of this model object
	 * @return model object of type type
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static CoreModelObject inflateModelObject(IModelStore modelStore, String objectUri, 
			String type, IModelCopyManager copyManager, boolean create, @Nullable String idPrefix) throws InvalidSPDXAnalysisException {
		return inflateModelObject(modelStore, objectUri, type, copyManager, getLatestSpecVersion(), create, idPrefix);
	}
	
	/**
	 * @param store store to use for the inflated object
	 * @param uri URI of the external element
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 * @param type type hint for creating the correct external element
	 * @param specVersion version of the SPDX spec the object complies with
	 * @return a java object representing an SPDX element external to model store, collection or document
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static Object getExternalElement(IModelStore store, String uri,
			@Nullable IModelCopyManager copyManager, Class<?> type,
			String specVersion) throws InvalidSPDXAnalysisException {
		return ModelRegistry.getModelRegistry().getExternalElement(store, uri, copyManager, type, specVersion);
	}
	
	/**
	 * @param store store to use for the inflated object
	 * @param uri URI of the external element
	 * @param copyManager if non-null, implicitly copy any referenced properties from other model stores
	 * @param type type hint for creating the correct external element
	 * @return a java object representing an SPDX element external to model store, collection or document for the most recent version of the spec supported
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static Object getExternalElement(IModelStore store, String uri,
			@Nullable IModelCopyManager copyManager, Class<?> type) throws InvalidSPDXAnalysisException {
		return getExternalElement(store, uri, copyManager, type, getLatestSpecVersion());
	}
	
	/**
	 * Converts a URI to enum
	 * @param uri URI for the Enum individual
	 * @param specVersion Version of the spec the enum belongs to
	 * @return the Enum represented by the individualURI if it exists within the spec model
	 * @throws ModelRegistryException if the spec version does not exist
	 */
	public static @Nullable Enum<?> uriToEnum(String uri, String specVersion) throws ModelRegistryException {
		return ModelRegistry.getModelRegistry().uriToEnum(uri, specVersion);
	}
	
	/**
	 * Converts a URI to enum for the latest supported version of the spec
	 * @param uri URI for the Enum individual
	 * @return the Enum represented by the individualURI if it exists within the spec model
	 * @throws ModelRegistryException if the spec version does not exist
	 */
	public static @Nullable Enum<?> uriToEnum(String uri) throws ModelRegistryException {
		return uriToEnum(uri, getLatestSpecVersion());
	}
	
	/**
	 * @param store model store
	 * @param copyManager optional copy manager
	 * @param typeFilter type to filter on
	 * @param objectUriPrefixFilter only return objects with URI's starting with this string
	 * @param idPrefix optional prefix used for any new object URI's created in support of this model object
	 * @return stream of objects stored in the model store - an object being any non-primitive type
	 * @throws InvalidSPDXAnalysisException on SPDX parsing errors
	 */
	public static Stream<?> getSpdxObjects(IModelStore store, @Nullable IModelCopyManager copyManager, 
			@Nullable String typeFilter, @Nullable String objectUriPrefixFilter, @Nullable String idPrefix) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(store, "Store must not be null");
		return store.getAllItems(objectUriPrefixFilter, typeFilter).map(tv -> {
			//TODO: Change this a null namespace and filtering on anonymous or startswith document URI - this will catch the anon. types
			try {
				return inflateModelObject(store, tv.getObjectUri(), tv.getType(), copyManager, tv.getSpecVersion(), false, idPrefix);
			} catch (InvalidSPDXAnalysisException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
