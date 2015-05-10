package com.hahn.sellerrobot.util.exceptions;

public class ProcedureParseException extends Exception {
	private static final long serialVersionUID = 840169056061172239L;
	
	public ProcedureParseException(String error) {
		super("Failed to parse procedure. " + error);
	}

}
