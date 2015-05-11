package com.hahn.sellerrobot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hahn.sellerrobot.controller.ProcedureController;
import com.hahn.sellerrobot.model.FilesCollection;

public class Main {
	public static ObjectMapper MAPPER = new ObjectMapper();
	private static Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		// new Console();
		
		Main main = new Main();
		while (true) {
			main.execute();
			
			try {
				Thread.sleep(1000 * 60 * 30);
			} catch (InterruptedException e) {
				log.debug(e);
			}
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
			log.fatal("A fatal error has occured!", e);
			System.exit(-1);
		}
	}
	
	public void execute() {
		try {
			procedure.execute();
		} catch (Exception e) {
			log.fatal("A fatal error has occured!", e);
		}
	}
	
	public FilesCollection getFilesCollection() {
		return files;
	}

}
