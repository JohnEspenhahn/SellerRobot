package com.hahn.sellerrobot.model;

import java.util.Map;

import com.hahn.sellerrobot.model.Procedure.Action;
import com.hahn.sellerrobot.model.Procedure.EnumAction;
import com.hahn.sellerrobot.util.exceptions.MissingArgumentException;

public class JSONObjectHandler {
	
	private FilesCollection files;
	
	public JSONObjectHandler(FilesCollection files) {
		this.files = files;
	}

	/**
	 * Convert the given object to a string. Handles null, generic objects, and file definition json objects (type, filename, [additional params...])
	 * @param obj The object to convert
	 * @return The string conversion of the object
	 * @throws MissingArgumentException If the file definition json object is missing a required parameter
	 */
	public String toString(Object obj) throws MissingArgumentException {
		if (obj == null) {
			return "null";
		} else if (obj instanceof Map) {
			Map<?,?> map = (Map<?,?>) obj;
			
			ActionVerifier.verifyParams("Object", map, ParamFactory.get(String.class, "type"), ParamFactory.get(String.class, "filename"));
			
			String filename = (String) map.get("filename");
			EnumJSONObjectType type = getType((String) map.get("type"));
			switch (type) {
			case CSV:
				ActionVerifier.verifyParams("Object", map, ParamFactory.get(Integer.class, "column"));
				return ((CSVFile) files.get(filename, "csv")).get((int) map.get("column"));
			default:
				throw new IllegalArgumentException("Unhandled JSONObject type " + type);
			}
		} else {
			return obj.toString();
		}
	}

	/**
	 * Handle the determinant action
	 * @param a The determinant action
	 * @return The RunDeterminant defined by the given action
	 * @throws IllegalArgumentException If the defined RunDeterminant is invalid
	 */
	public RunDeterminant toDeterminant(Action a) {
		if (a.getAction() != EnumAction.determinant) throw new IllegalArgumentException();
		
		switch (getType(a)) {
		case CSV:
			return (CSVFile) files.get((String) a.get("filename"), "csv");
		default:
			throw new IllegalArgumentException("Unhandled JSONObject determinant type " + getType(a));
		}
	}
	
	private EnumJSONObjectType getType(Action a) {
		if (!a.has("type")) throw new IllegalArgumentException("Cannot hanlde JSONObject without a type field: \n" + a);
		
		return getType((String) a.get("type"));
	}
	
	private EnumJSONObjectType getType(String typename) {
		try {
			return EnumJSONObjectType.valueOf(typename.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknow JSONObject type " + typename);
		}
	}
	
	enum EnumJSONObjectType {
		CSV;
	}
}
