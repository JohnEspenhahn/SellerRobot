package com.hahn.sellerrobot.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hahn.sellerrobot.controller.ActionVerifier;
import com.hahn.sellerrobot.util.MapUtils;
import com.hahn.sellerrobot.util.exceptions.MissingArgumentException;

public class Procedure {	
	private List<Action> setup;
	private List<Action> procedure;
	private List<Action> end;
	
	/**
	 * Required to be called to load the points from the points.json
	 * @param p The loaded points object
	 * @throws MissingArgumentException 
	 * @throws IllegalArgumentException 
	 */
	public void loadPoints(ClickPoints p) throws IllegalArgumentException, MissingArgumentException {
		for (Action a: setup) a.loadPoints(p);
		for (Action a: procedure) a.loadPoints(p);
		for (Action a: end) a.loadPoints(p);
	}
	
	public void setSetup(List<Action> setup) {
		this.setup = setup;
	}
	
	public List<Action> getSetup() {
		return this.setup;
	}
	
	public void setProcedure(List<Action> procedure) {
		this.procedure = procedure;
	}
	
	public List<Action> getProcedure() {
		return this.procedure;
	}
	
	public void setEnd(List<Action> end) {
		this.end = end;
	}
	
	public List<Action> getEnd() {
		return this.end;
	}
	
	@Override
	public String toString() {
		return "Setup:\n" + StringUtils.join(setup, ",") + "\nProcedure:\n" + StringUtils.join(procedure, ",");
	}
	
	public enum EnumAction {
		read, determinant,
		ifequ, click, type, focus, sleep, wheel, log
	}
	
	public static class Action {
		private EnumAction action;
		private Object params;
		
		public Action() { }
		
		public Action(Map<String, Object> def, ClickPoints p) {
			if (!def.containsKey("action")) throw new MissingArgumentException("Invalid action definition. Missing [action]");
			else if (!def.containsKey("params")) throw new MissingArgumentException("Invalid action definition. Missing [params]");
			
			action = EnumAction.valueOf(def.get("action").toString());
			params = def.get("params");
			
			loadPoints(p);
		}
		
		/**
		 * If needed loads parameters from the points object, then verify the parameters
		 * @param p The points object
		 */
		public void loadPoints(ClickPoints p) {
			if (params instanceof String) {
				params = (Map<String, Integer>) p.getPoint((String) params);
			} else if (!(params instanceof Map)) {
				throw new IllegalArgumentException("Expected [params] in action definition to be a String point id constant or Map, but got " + params.getClass());
			}
			
			ActionVerifier.verify(this);
		}
		
		public EnumAction getAction() { 
			return action; 
		}
		
		public void setAction(EnumAction action) throws IllegalArgumentException, MissingArgumentException {
			this.action = action;
		}
		
		@SuppressWarnings("unchecked")
		public Map<String, Object> getParams() {
			return (Map<String, Object>) params;
		}
		
		public void setParams(Object params) throws IllegalArgumentException, MissingArgumentException {
			this.params = params;
		}
		
		public boolean has(String name) {
			return getParams() != null && getParams().containsKey(name);
		}
		
		public Object get(String name) {
			if (getParams() != null) return getParams().get(name);
			else return null;
		}
		
		@Override
		public String toString() {
			return String.format("%s(%s)", getAction(), MapUtils.map2str(getParams()));
		}
	}
}
