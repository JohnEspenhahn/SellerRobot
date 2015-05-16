package com.hahn.sellerrobot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hahn.sellerrobot.controller.ProcedureController;
import com.hahn.sellerrobot.model.FilesCollection;

public class Main {
	public static ObjectMapper MAPPER = new ObjectMapper();
	
	private static final String VERSION = "1.1.1";
	private static Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		MAPPER.configure(Feature.ALLOW_COMMENTS, true);
		MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		
		log.debug("Running Robot v" + VERSION + " by John Espenhahn");
		
		Main main = new Main();
		while (true) {
			main.execute();
		}
	}
	
	private ProcedureController procedure;
	private FilesCollection files;
	
	public Main() {
		files = new FilesCollection();
		
		procedure = null;
		try {
			procedure = new ProcedureController(this, "procedure.json");
		} catch (Exception e) {
			log.fatal("A fatal error occured while setting up the procedure!", e);
			System.exit(-1);
		}
	}
	
	public void execute() {
		try {
			procedure.execute();
		} catch (Exception e) {
			log.fatal("A fatal error occured while running the procedure!", e);
		}
		
		try {
			procedure.end();
		} catch (Exception e) {
			log.fatal("A fatal error occured while running end!", e);
		}
	}
	
	public FilesCollection getFilesCollection() {
		return files;
	}

}
