package com.hahn.sellerrobot.util.exceptions;

public class ConstantNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -2155280072407119637L;

	public ConstantNotFoundException(String id) {
		super("The constant with the id '" + id + "' could not be found!");
	}
}
