package com.hahn.sellerrobot.util.exceptions;

public class WindowToForegroundException extends Exception {
	private static final long serialVersionUID = -7502327460678118981L;

	public WindowToForegroundException(String windowName) {
		super("Failed to bring window with name " + windowName + " to foreground");
	}
}