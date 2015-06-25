package com.hahn.sellerrobot;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hahn.sellerrobot.controller.ProcedureController;
import com.hahn.sellerrobot.model.ClickPoints.ClickPoint;
import com.hahn.sellerrobot.model.FilesCollection;

public class Main {
	public static ObjectMapper MAPPER = new ObjectMapper();
	
	private static final String VERSION = "2.0.0";
	private static Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		MAPPER.configure(Feature.ALLOW_COMMENTS, true);
		MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
		
		log.debug("Running Mercari Robot v" + VERSION + " by John Espenhahn");
		
		Main main = new Main();
		if (args.length >= 1 && args[0].equals("--map-points")) {
			main.mapPoints();
		} else {
			while (true) {
				main.execute();
			}
		}
	}
	
	private ProcedureController procedure;
	private FilesCollection files;
	
	public Main() {
		files = new FilesCollection();
		
		procedure = null;
		try {
			procedure = new ProcedureController(this, "procedure.json", "points.json");
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
	
	public void mapPoints()  {
		log.debug("Mapping points...");
		
		Scanner scanner = new Scanner(System.in);
		
		try {
			String id = "";
			while (true) {
				System.out.print("Next point's id or ':q' to quit> ");
				id = scanner.nextLine();
				
				if (id.equals(":q")) {
					break;
				} else {
					Point window = procedure.getFocusedWindow().getTopLeft();
					Point mouse = MouseInfo.getPointerInfo().getLocation();
					ClickPoint point = new ClickPoint((int) (mouse.getX() - window.getX()), (int) (mouse.getY() - window.getY()));
					
					procedure.getPoints().setPoint(id, point);
					System.out.println("Added Point(" + id + ") at " + point);
				}
			}
		} catch (Exception e) {
			log.error("An error occured while trying to input a point");
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		
		try {
			MAPPER.writeValue(new File("points.json"), procedure.getPoints());
		} catch (IOException e) {
			log.error("Failed to export points.json");
			e.printStackTrace();
		}
		
		log.info("Done mapping points");
	}
	
	public FilesCollection getFilesCollection() {
		return files;
	}

}
