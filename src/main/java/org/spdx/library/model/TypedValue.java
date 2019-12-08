package org.spdx.library.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.spdx.library.SpdxConstants;
import org.spdx.library.SpdxInvalidIdException;


/**
 * Value which is a stored typed item
 */
public class TypedValue {
	
	static Set<String> SPDX_CLASSES = new HashSet<>(Arrays.asList(SpdxConstants.ALL_SPDX_CLASSES));
	
	String id;
	String type;
	
	public TypedValue(String id, String type) throws SpdxInvalidIdException, SpdxInvalidTypeException {
		if (id == null) {
			throw new SpdxInvalidIdException("Null value Id");
		}
		// TODO: can add some additional checks for different string formats based on the type
		if (type == null) {
			throw new SpdxInvalidTypeException("Null type");
		}
		if (!SPDX_CLASSES.contains(type) && !GenericModelObject.GENERIC_MODEL_OBJECT_TYPE.equals(type)) {
			throw new SpdxInvalidTypeException(type + " is not a valid SPDX class");
		}
		this.id = id;
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
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
		return tv.getId().equals(this.id) && tv.getType().equals(this.type);
	}
	
	@Override
	public int hashCode() {
		return 181 ^ this.id.hashCode() ^ this.type.hashCode();
	}
}