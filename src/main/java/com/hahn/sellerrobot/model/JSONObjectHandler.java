package com.hahn.sellerrobot.model;

import org.json.simple.JSONObject;

import com.hahn.sellerrobot.util.RunDeterminant;

public class JSONObjectHandler {
	
	private FilesCollection files;
	
	public JSONObjectHandler(FilesCollection files) {
		this.files = files;
	}

	public String toString(JSONObject obj) {
		EnumJSONObjectType type = getType(obj);
		String filename = (String) obj.get("filename");
		
		switch (type) {
		case CSV:
			int column = (int) (long) obj.get("column");
			return ((CSVFile) files.get(filename, "csv")).get(column);
		case JSON:
			Object idx = obj.get("index");
			return ((JSONObject) files.get(filename, "json")).get(idx).toString();
		default:
			throw new IllegalArgumentException("Unknown JSONObject type " + type);
		}
	}

	public RunDeterminant toDeterminant(JSONObject obj) {		
		EnumJSONObjectType type = getType(obj);
		String filename = (String) obj.get("filename");
		
		switch (type) {
		case CSV:
			return (CSVFile) files.get(filename, "csv");
		default:
			throw new IllegalArgumentException("Unhandled JSONObject determinant type " + type);
		}
	}
	
	private EnumJSONObjectType getType(JSONObject obj) {
		if (!obj.containsKey("type")) throw new IllegalArgumentException("Cannot hanlde JSONObject without a type field: \n" + obj.toJSONString());
		
		String action_name = obj.get("type").toString();
		try {
			return EnumJSONObjectType.valueOf(action_name.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknow JSONObject type " + action_name);
		}
	}
	
	enum EnumJSONObjectType {
		CSV, JSON;
	}
}
