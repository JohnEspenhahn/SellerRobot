package com.hahn.sellerrobot.models;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.hahn.sellerrobot.util.exceptions.ProcedureParseException;

public class Procedure {
	List<Event> events;

	public Procedure(String json_in) throws FileNotFoundException, ParseException, ProcedureParseException {
		JSONParser parser = new JSONParser();
		JSONArray mainJSONArray = (JSONArray) parser.parse(json_in);
		
		this.events = new ArrayList<Event>(mainJSONArray.size());

		for (int i = 0; i < mainJSONArray.size(); i++) {
			JSONArray jsonObj = (JSONArray) mainJSONArray.get(i);

			EnumAction action = null;
			String action_name = jsonObj.get(0).toString();
			try {
				action = EnumAction.valueOf(action_name.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new ProcedureParseException("Unknow action " + action_name);
			}
			
			JSONObject jsonParams = (JSONObject) jsonObj.get(1);
			events.add(new Event(action, jsonParams));
		}
	}
	
	public int size() {
		return events.size();
	}
	
	public Event get(int idx) {
		return events.get(idx);
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
		public Event(EnumAction action, Map parameters) {
			this.action = action;
			this.parameters = parameters;
		}

		public EnumAction getAction() {
			return action;
		}

		public String getParameter(String name) {
			return parameters.get(name).toString();
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

	public enum EnumAction {
		CLICK;
	}

}
