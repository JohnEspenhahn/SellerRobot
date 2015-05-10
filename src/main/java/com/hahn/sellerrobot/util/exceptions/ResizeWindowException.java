package com.hahn.sellerrobot.util.exceptions;

public class ResizeWindowException extends Exception {
	private static final long serialVersionUID = -7502327460678118981L;

	public ResizeWindowException(String windowName) {
		super("Failed to resize window with name " + windowName);
	}
}