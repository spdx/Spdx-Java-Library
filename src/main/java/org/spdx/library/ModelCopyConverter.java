/**
 * Copyright (c) 2024 Source Auditor Inc.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.spdx.core.IndividualUriValue;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.SimpleUriValue;
import org.spdx.core.TypedValue;
import org.spdx.storage.IModelStore;
import org.spdx.storage.PropertyDescriptor;

/**
 * @author Gary O'Neall
 * 
 * Static class which manages the conversion between different versions of the SPDX specification
 *
 */
public class ModelCopyConverter {
	
	static Map<String, Map<String, ModelSpecConverter>> SPEC_CONVERTER_MAP = new HashMap<>();
	
	private ModelCopyConverter() {
		// static class
	}

	/**
	 * @param fromTv typed value to convert from
	 * @param toSpecVersion for the return converted byp
	 * @return the type to be used for the toSpecVersion of the <code>fromTv.getType()</code>
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static String convertType(TypedValue fromTv,
			String toSpecVersion) throws InvalidSPDXAnalysisException {
		if (versionsCompatible(fromTv.getSpecVersion(), toSpecVersion)) {
			return fromTv.getType();
		} else {
			ModelSpecConverter specConverter = getSpecConverter(fromTv.getSpecVersion(), toSpecVersion);
			return specConverter.convertType(fromTv.getType());
		}
	}

	/**
	 * @param fromSpecVersion from spec version
	 * @param toSpecVersion to spec version
	 * @return true if no conversion is needed from the fromSpecVersion to the toSpecVersion
	 */
	public static boolean versionsCompatible(String fromSpecVersion,
			String toSpecVersion) {
		Objects.requireNonNull(fromSpecVersion);
		Objects.requireNonNull(toSpecVersion);
		return fromSpecVersion.startsWith("SPDX-2") && toSpecVersion.startsWith("SPDX-2") ||
				fromSpecVersion.startsWith("3.0.") && toSpecVersion.startsWith("3.0.");
	}

	/**
	 * @param fromSpecVersion from spec version
	 * @param toSpecVersion to spec version
	 * @return the model spec converter from the fromSpecVersion to the toSpecVersion
	 * @throws InvalidSPDXAnalysisException if a converter is not found
	 */
	private static ModelSpecConverter getSpecConverter(String fromSpecVersion,
			String toSpecVersion) throws InvalidSPDXAnalysisException {
		Map<String, ModelSpecConverter> conversionMap = SPEC_CONVERTER_MAP.get(fromSpecVersion);
		if (Objects.isNull(conversionMap)) {
			throw new InvalidSPDXAnalysisException("No conversions for spec version "+fromSpecVersion);
		}
		ModelSpecConverter specConverter = conversionMap.get(toSpecVersion);
		if (Objects.isNull(specConverter)) {
			throw new InvalidSPDXAnalysisException("No conversion from spec version "+fromSpecVersion+" to "+toSpecVersion);
		}
		return specConverter;
	}

	/**
	 * Converts (if needed) the value then store the result in the toStore
     * @param toStore Model Store to copy to
     * @param toTV to typedValue to copy the property to
     * @param fromStore Model Store containing the source item
     * @param fromTV typedValue to copy the property from
     * @param fromPropDescriptor Descriptor for the property to be copied from
	 * @param value value from the fromStore
	 * @param toNamespace Namespace to use if an ID needs to be generated for the to object
	 * @param modelCopyManager copyManager to use for any typedValue results
	 * @throws InvalidSPDXAnalysisException on any issues converting or storing the value
	 */
	public static void copyConvertedPropertyValue(
			IModelStore toStore, TypedValue toTv, IModelStore fromStore,
			TypedValue fromTv, PropertyDescriptor fromPropDescriptor,
			Object value, String toNamespace,
			ModelCopyManager modelCopyManager) throws InvalidSPDXAnalysisException {
		if (versionsCompatible(fromTv.getSpecVersion(), toTv.getSpecVersion())) {
			
			if (value instanceof IndividualUriValue) {
	            toStore.setValue(toTv.getObjectUri(), fromPropDescriptor, new SimpleUriValue((IndividualUriValue)value));
	        } else if (value instanceof TypedValue) {
	            TypedValue valueTv = (TypedValue)value;
	            if (fromStore.equals(toStore)) {
	                toStore.setValue(toTv.getObjectUri(), fromPropDescriptor, valueTv);
	            } else {
	                toStore.setValue(toTv.getObjectUri(), fromPropDescriptor, 
	                		modelCopyManager.copy(toStore, fromStore, valueTv.getObjectUri(), 
	                        		toTv.getSpecVersion(), toNamespace));
	            }
	        } else {
	            toStore.setValue(toTv.getObjectUri(), fromPropDescriptor, value);
	        }
		} else {
			throw new InvalidSPDXAnalysisException("Unimplemented");
		}
	}
	
	/**
	 * Converts (if needed) the value then adds the result to a collection in the toStore
     * @param toStore Model Store to copy to
     * @param toTV to typedValue to copy the property to
     * @param fromStore Model Store containing the source item
     * @param fromTV typedValue to copy the property from
     * @param fromPropDescriptor Descriptor for the property to be copied from
	 * @param value value from the fromStore
	 * @param toNamespace Namespace to use if an ID needs to be generated for the to object
	 * @param modelCopyManager copyManager to use for any typedValue results
	 * @throws InvalidSPDXAnalysisException on any issues converting or storing the value
	 */
	public static void addConvertedPropertyValue(
			IModelStore toStore, TypedValue toTv, IModelStore fromStore,
			TypedValue fromTv, PropertyDescriptor fromPropDescriptor,
			Object value, String toNamespace,
			ModelCopyManager modelCopyManager) throws InvalidSPDXAnalysisException {
		if (versionsCompatible(fromTv.getSpecVersion(), toTv.getSpecVersion())) {
			if (value instanceof IndividualUriValue) {
	            toStore.addValueToCollection(toTv.getObjectUri(), fromPropDescriptor, new SimpleUriValue((IndividualUriValue)value));
	        } else if (value instanceof TypedValue) {
	            TypedValue valueTv = (TypedValue)value;
	            if (fromStore.equals(toStore)) {
	                toStore.addValueToCollection(toTv.getObjectUri(), fromPropDescriptor, valueTv);
	            } else {
	                toStore.addValueToCollection(toTv.getObjectUri(), fromPropDescriptor, 
	                		modelCopyManager.copy(toStore, fromStore, valueTv.getObjectUri(), 
	                        		toTv.getSpecVersion(), toNamespace));
	            }
	        } else {
	            toStore.addValueToCollection(toTv.getObjectUri(), fromPropDescriptor, value);
	        }
		} else {
			throw new InvalidSPDXAnalysisException("Unimplemented");
		}
	}

}
