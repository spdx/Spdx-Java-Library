package org.spdx.library;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.spdx.library.model.compat.v2.GenericModelObject;
import org.spdx.library.model.compat.v2.GenericSpdxElement;
import org.spdx.library.model.compat.v2.GenericSpdxItem;


/**
 * Value which is a stored typed item
 */
public class TypedValue {
	
	static Set<String> SPDX_CLASSES;
	
	static {
		Set<String> spdxClasses = new HashSet<>(Arrays.asList(SpdxConstantsCompatV2.ALL_SPDX_CLASSES));
		spdxClasses.addAll(Arrays.asList(SpdxConstants.ALL_SPDX_CLASSES));
		SPDX_CLASSES = Collections.unmodifiableSet(spdxClasses);
	}
	
	
	String objectUri;
	String type;
	
	public TypedValue(String objectUri, String type) throws SpdxInvalidIdException, SpdxInvalidTypeException {
		if (objectUri == null) {
			throw new SpdxInvalidIdException("Null value Id");
		}
		// TODO: can add some additional checks for different string formats based on the type
		if (type == null) {
			throw new SpdxInvalidTypeException("Null type");
		}
		if (!SPDX_CLASSES.contains(type) && !GenericModelObject.GENERIC_MODEL_OBJECT_TYPE.equals(type)
				&&!"ModelObjectForTesting".equals(type)
				&&!"ElementForTest".equals(type)
				&&!GenericSpdxElement.GENERIC_SPDX_ELEMENT_TYPE.equals(type)
				&&!GenericSpdxItem.GENERIC_SPDX_ITEM_TYPE.equals(type)) {
			throw new SpdxInvalidTypeException(type + " is not a valid SPDX class");
		}
		this.objectUri = objectUri;
		this.type = type;
	}

	/**
	 * @return the objectUri
	 */
	public String getObjectUri() {
		return objectUri;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof TypedValue)) {
			return false;
		}
		TypedValue tv = (TypedValue)o;
		return tv.getObjectUri().equals(this.objectUri) && tv.getType().equals(this.type);
	}
	
	@Override
	public int hashCode() {
		return 181 ^ this.objectUri.hashCode() ^ this.type.hashCode();
	}
}