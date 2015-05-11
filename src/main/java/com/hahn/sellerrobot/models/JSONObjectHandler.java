package com.hahn.sellerrobot.models;

import org.json.simple.JSONObject;

import com.hahn.sellerrobot.util.RunDeterminant;

public class JSONObjectHandler {
	
	private FilesCollection files;
	
	public JSONObjectHandler(FilesCollection files) {
		this.files = files;
	}

	public String toString(JSONObject obj) {
		EnumJSONObjectType type = getType(obj);
		
		switch (type) {
		case CSV:
			String file = (String) obj.get("file");
			int column = (int) (long) obj.get("column");
			return files.getCSV(file).get(column);
		default:
			throw new IllegalArgumentException("Unknow JSONObject type " + type);
		}
	}

	public RunDeterminant toDeterminant(JSONObject obj) {		
		EnumJSONObjectType type = getType(obj);
		
		switch (type) {
		case CSV:
			String file = (String) obj.get("file");
			return files.getCSV(file);
		default:
			throw new IllegalArgumentException("Unknow JSONObject type " + type);
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
		CSV;
	}
}
