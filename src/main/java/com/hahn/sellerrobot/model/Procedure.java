package com.hahn.sellerrobot.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hahn.sellerrobot.util.MapUtils;
import com.hahn.sellerrobot.util.exceptions.MissingArgumentException;

public class Procedure {
	private List<Action> setup;
	private List<Action> procedure;
	
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
	
	@Override
	public String toString() {
		return "Setup:\n" + StringUtils.join(setup, ",") + "\nProcedure:\n" + StringUtils.join(procedure, ",");
	}
	
	public enum EnumAction {
		read, determinant,
		click, type, focus, sleep, wheel
	}
	
	public static class Action {
		private EnumAction action;
		private Map<String, Object> params;
		
		public Action() { }
		
		public EnumAction getAction() { 
			return action; 
		}
		
		public void setAction(EnumAction action) throws IllegalArgumentException, MissingArgumentException {
			this.action = action;
			
			if (getParams() != null) ActionVerifier.verify(this);
		}
		
		protected Map<String, Object> getParams() {
			return params;
		}
		
		public void setParams(Map<String, Object> params) throws IllegalArgumentException, MissingArgumentException {
			this.params = params;
			
			if (getAction() != null) ActionVerifier.verify(this);
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
