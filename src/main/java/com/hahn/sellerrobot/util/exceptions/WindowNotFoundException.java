package com.hahn.sellerrobot.util.exceptions;

public class WindowNotFoundException extends Exception {
	private static final long serialVersionUID = -2561676546570640003L;

	public WindowNotFoundException(String className, String windowName) {
		super(String.format("Window null for className: %s; windowName: %s", className, windowName));
	}
	
}