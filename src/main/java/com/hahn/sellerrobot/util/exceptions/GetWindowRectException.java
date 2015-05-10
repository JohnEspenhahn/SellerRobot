package com.hahn.sellerrobot.util.exceptions;

public class GetWindowRectException extends Exception {
	private static final long serialVersionUID = -7502327460678118981L;

	public GetWindowRectException(String windowName) {
		super("Window Rect not found for " + windowName);
	}
}