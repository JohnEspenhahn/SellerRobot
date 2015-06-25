package com.hahn.sellerrobot.model;

import java.util.HashMap;
import java.util.Map;

import com.hahn.sellerrobot.controller.ActionVerifier;
import com.hahn.sellerrobot.util.exceptions.ConstantNotFoundException;

public class ClickPoints {
	private Map<String, ClickPoint> constants;
	
	public void setConstants(Map<String, ClickPoint> constants) {
		this.constants = constants;
	}
	
	public Map<String, ClickPoint> getConstants() {
		return constants;
	}
	
	public ClickPoint getPoint(String id) throws ConstantNotFoundException {
		ClickPoint p = constants.get(id);
		if (p == null) throw new ConstantNotFoundException(id);
		
		ActionVerifier.verifyParams("Point(" + id + ")", p, ParamFactory.get(Integer.class, "x"), ParamFactory.get(Integer.class, "y"));
		
		return p;
	}
	
	public void setPoint(String id, ClickPoint point) {
		constants.put(id, point);
	}
	
	public static class ClickPoint extends HashMap<String, Integer> {
		private static final long serialVersionUID = -8575632251683782405L;

		public ClickPoint() { }
		
		public ClickPoint(int x, int y) {
			setX(x);
			setY(y);
		}
		
		public Integer getX() {
			return this.get("x");
		}
		
		public Integer getY() {
			return this.get("y");
		}
		
		public void setX(int x) {
			this.put("x", x);
		}
		
		public void setY(int y) {
			this.put("y", y);
		}
		
		@Override
		public String toString() {
			return String.format("(%d,%d)", getX(), getY());
		}
	}
}
