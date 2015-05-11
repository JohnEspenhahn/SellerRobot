package com.hahn.sellerrobot.model;

import java.util.HashMap;
import java.util.Map;

public class ParamFactory {
	private static Map<String, Param> PARAMS = new HashMap<String, Param>();
	
	public static class Param {
		private Class<?> clazz;
		private String name;
		
		private Param(Class<?> clazz, String name) {
			this.clazz = clazz;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public Class<?> getClazz() {
			return clazz;
		}
		
		public boolean isInstance(Object o) {
			return getClazz().isInstance(o);
		}
		
		private String getKey() {
			return getKey(getClazz(), getName());
		}
		
		@Override
		public String toString() {
			return getClazz().getName() + " " + getName();
		}
		
		private static String getKey(Class<?> clazz, String name) {
			return clazz.getCanonicalName() + ":" + name;
		}
	}
	
	public static Param get(Class<?> clazz, String name) {
		Param p = ParamFactory.PARAMS.get(Param.getKey(clazz, name));
		if (p == null) {
			p = new Param(clazz, name);
			ParamFactory.PARAMS.put(p.getKey(), p);
		}
		
		return p;
	}
}