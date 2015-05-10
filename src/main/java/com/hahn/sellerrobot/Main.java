package com.hahn.sellerrobot;

import java.io.IOException;

import com.hahn.sellerrobot.models.Procedure.Event;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hahn.sellerrobot.models.AppWindow;
import com.hahn.sellerrobot.models.Procedure;

public class Main {
	public static Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		AppWindow app = null;
		Procedure procedure = null;
		try {
			procedure = new Procedure(new Main().getFileWithUtil("procedure.json"));
			log.debug("Procedure: " + procedure.toString());
			
			app = new AppWindow("BlueStacks App Player", 723, 476);
		} catch (Exception e) {
			log.fatal(e.toString());
			System.exit(-1);
		}
		
		for (int i = 0; i < procedure.size(); i++) {
			Event e = procedure.get(i);
			switch (e.getAction()) {
			case CLICK:
				app.click(Integer.parseInt(e.getParameter("x")), Integer.parseInt(e.getParameter("y")));
				break;
			default:
				throw new RuntimeException("Unhandled action " + e.getAction());
			}
		}
	}
	
	private String getFileWithUtil(String fileName) {
		String result = "";

		ClassLoader classLoader = getClass().getClassLoader();
		try {
			result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
