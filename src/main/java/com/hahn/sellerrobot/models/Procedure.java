package com.hahn.sellerrobot.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.hahn.sellerrobot.util.RunDeterminant;
import com.hahn.sellerrobot.util.exceptions.ProcedureParseException;

public class Procedure {
	private static JSONObjectHandler jsonObjectHandler;
	
	private RunDeterminant determinant;
	private List<Event> events;

	public Procedure(String json_in, FilesCollection files) throws ParseException, ProcedureParseException, IOException {
		if (jsonObjectHandler == null) jsonObjectHandler = new JSONObjectHandler(files);
		
		JSONObject main = (JSONObject) files.parseJSON(json_in);
		
		// Parse setup
		JSONArray setup_array = (JSONArray) main.get("setup");
		for (int i = 0; i < setup_array.size(); i++) {
			JSONArray action_arr = (JSONArray) setup_array.get(i);
			
			EnumSetupAction action = null;
			String action_name = action_arr.get(0).toString();
			try {
				action = EnumSetupAction.valueOf(action_name.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new ProcedureParseException("Unknow setup action " + action_name);
			}
			
			JSONObject action_params = (JSONObject) action_arr.get(1);			
			switch (action) {
			case READ:
				String fileName = (String) action_params.get("file");
				files.readFile(fileName);
				break;
			case DETERMINANT:
				determinant = jsonObjectHandler.toDeterminant(action_params);
				break;
			}
		}
		
		
		// Parse procedure
		JSONArray procedure_array = (JSONArray) main.get("procedure");
		this.events = new ArrayList<Event>(procedure_array.size());

		for (int i = 0; i < procedure_array.size(); i++) {
			JSONArray action_arr = (JSONArray) procedure_array.get(i);

			EnumAction action = null;
			String action_name = action_arr.get(0).toString();
			try {
				action = EnumAction.valueOf(action_name.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new ProcedureParseException("Unknow action " + action_name);
			}
			
			JSONObject jsonParams = (JSONObject) action_arr.get(1);
			events.add(new Event(action, jsonParams));
		}
	}
	
	public int size() {
		return events.size();
	}
	
	public Event get(int idx) {
		return events.get(idx);
	}
	
	public RunDeterminant getRunDeterminant() {
		return determinant;
	}

	@Override
	public String toString() {
		String str = "";
		for (Event e: events) {
			str += e.toString() + "; ";
		}
		
		return str;
	}

	public static class Event {
		private EnumAction action;

		@SuppressWarnings("rawtypes")
		private Map parameters;

		@SuppressWarnings("rawtypes")
		private Event(EnumAction action, Map parameters) {
			this.action = action;
			this.parameters = parameters;
		}

		public EnumAction getAction() {
			return action;
		}

		public String getString(String name) {
			Object obj = parameters.get(name);
			if (obj == null) return "null";
			else if (obj instanceof String) return (String) obj;
			else if (obj instanceof JSONObject) return jsonObjectHandler.toString((JSONObject) obj);
			else return obj.toString();
		}
		
		public int getInt(String name) {
			return Integer.parseInt(getString(name));
		}
		
		public boolean has(String name) {
			return parameters.containsKey(name);
		}

		@Override
		public String toString() {
			// Build parameters to a string
			String paramStrs = "";
			paramStrs += "{";
			
			boolean first = true;
			for (Object key : parameters.keySet()) {
				if (first) first = false;
				else paramStrs += ", ";
				
				paramStrs += String.format("%s: %s", key.toString(), parameters.get(key).toString());
			}
			paramStrs += "}";

			return String.format("%s(%s)", getAction(), paramStrs);
		}
	}

	public enum EnumSetupAction {
		/** Read a file { String file } */
		READ,
		/** Determines how many times the procedure will be executed { String type, String file } */
		DETERMINANT;
	}
	
	public enum EnumAction {
		/** { int x, int y } */
		CLICK, 
		/** { int ms } */
		SLEEP,
		/** { Object name, optional int width, optional int height } */
		FOCUS,
		/** { Object text } */
		TYPE,
		/** { int amount } */
		WHEEL;
	}
	
}
