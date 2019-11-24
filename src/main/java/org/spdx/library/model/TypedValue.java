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
	String documentUri;
	
	public TypedValue(String documentUri, String id, String type) throws SpdxInvalidIdException, SpdxInvalidTypeException {
		if (id == null) {
			throw new SpdxInvalidIdException("Null value Id");
		}
		// TODO: can add some additional checks for different string formats based on the type
		if (type == null) {
			throw new SpdxInvalidTypeException("Null type");
		}
		if (!SPDX_CLASSES.contains(type)) {
			throw new SpdxInvalidTypeException(type + " is not a valid SPDX class");
		}
		if (documentUri == null) {
			throw new SpdxInvalidIdException("Null value for document URI");
		}
		this.id = id;
		this.type = type;
		this.documentUri = documentUri;
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

	
	/**
	 * @return the documentUri
	 */
	public String getDocumentUri() {
		return documentUri;
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
		return tv.getDocumentUri().equals(this.documentUri) && tv.getId().equals(this.id);
	}
	
	@Override
	public int hashCode() {
		return 181 ^ this.id.hashCode() ^ this.documentUri.hashCode();
	}
}