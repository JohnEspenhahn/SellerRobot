package com.hahn.sellerrobot.controller;

import java.awt.AWTException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hahn.sellerrobot.Main;
import com.hahn.sellerrobot.model.FilesCollection;
import com.hahn.sellerrobot.model.JSONObjectHandler;
import com.hahn.sellerrobot.model.Procedure;
import com.hahn.sellerrobot.model.Procedure.Action;
import com.hahn.sellerrobot.model.Procedure.EnumAction;
import com.hahn.sellerrobot.model.RunDeterminant;
import com.hahn.sellerrobot.model.Window;
import com.hahn.sellerrobot.util.exceptions.GetWindowRectException;
import com.hahn.sellerrobot.util.exceptions.MissingArgumentException;
import com.hahn.sellerrobot.util.exceptions.ProcedureParseException;
import com.hahn.sellerrobot.util.exceptions.ResizeWindowException;
import com.hahn.sellerrobot.util.exceptions.WindowNotFoundException;
import com.hahn.sellerrobot.util.exceptions.WindowToForegroundException;

public class ProcedureController {
	private static JSONObjectHandler jsonObjectHandler;
	private static Logger log = LogManager.getLogger(ProcedureController.class);
	
	private RunDeterminant determinant;
	private FilesCollection files;
	private Procedure procedure;
	
	private String focused_window; 
	private Map<String, Window> windows;

	public ProcedureController(Main main, String json_filename) throws ProcedureParseException, IOException, AWTException, WindowNotFoundException, GetWindowRectException, MissingArgumentException, ResizeWindowException, WindowToForegroundException {		
		if (jsonObjectHandler == null) jsonObjectHandler = new JSONObjectHandler(main.getFilesCollection());
		
		windows = new HashMap<String, Window>();
		files = main.getFilesCollection();
		
		procedure = Main.MAPPER.readValue((String) files.parse(json_filename), Procedure.class);
		
		// Run setup
		for (Action a: procedure.getSetup()) {
			handleAction(a);
		}
	}
	
	public void execute() throws IOException, AWTException, WindowNotFoundException, GetWindowRectException, MissingArgumentException, ResizeWindowException, WindowToForegroundException {
		determinant.reset();
		do {
			for (Action a: procedure.getProcedure()) {
				handleAction(a);
			}
			
			determinant.next();
		} while (determinant.hasNext());
	}
	
	public void handleAction(Action a) throws IOException, AWTException, WindowNotFoundException, GetWindowRectException, MissingArgumentException, ResizeWindowException, WindowToForegroundException {
		if (a == null) throw new IllegalArgumentException();
		
		switch (a.getAction()) {
		case read:
			files.parse((String) a.get("filename"), (String) a.get("type"));
			break;
		case determinant:
			determinant = jsonObjectHandler.toDeterminant(a);
			break;
		case click:
			getFocusedWindow().click((int) a.get("x"), (int) a.get("y"));
			break;
		case focus:
			focusWindow(a);
			break;
		case sleep:
			sleep((int) a.get("ms"));
			break;
		case type:
			getFocusedWindow().type(jsonObjectHandler.toString(a.get("text")));
			break;
		case wheel:
			getFocusedWindow().mouseWheel((int) a.get("amount"));
			break;
		}
	}
	
	public Window getFocusedWindow() {
		if (focused_window == null) throw new RuntimeException("Tried to click a window before focusing on one");
		return windows.get(focused_window);
	}
	
	/**
	 * Focus on a window defined by the action. If it is the first time focusing on the given window the target width and height must be specified.
	 * @param a The action defining which window to target
	 * @throws AWTException
	 * @throws WindowNotFoundException
	 * @throws GetWindowRectException
	 * @throws MissingArgumentException
	 * @throws ResizeWindowException
	 */
	public void focusWindow(Action a) throws AWTException, WindowNotFoundException, GetWindowRectException, MissingArgumentException, ResizeWindowException {
		if (a.getAction() != EnumAction.focus) throw new IllegalArgumentException();
		
		focused_window = (String) a.get("name");
		if (!windows.containsKey(focused_window)) {
			log.debug(String.format("Focusing on new window %s", focused_window));
			
			int width, height;
			if (a.has("width")) width = (int) a.get("width");
			else throw new MissingArgumentException("Must specify width when focusing on a window for the first time");
			
			if (a.has("height")) height = (int) a.get("height");
			else throw new MissingArgumentException("Must specify height when focusing on a window for the first time");
			
			windows.put(focused_window, new WindowImpl(focused_window, width, height));
		} else if (a.has("width") || a.has("height")) {
			log.debug(String.format("Switching focus to window %s", focused_window));
			
			Window window = windows.get(focused_window);
			
			int width, height;
			if (a.has("width")) width = (int) a.get("width");
			else width = window.getWidth();
			
			if (a.has("height")) height = (int) a.get("height");
			else height = window.getHeight();
			
			window.resize(width, height);
		}
	}
	
	public void sleep(int ms) {
		try {
			log.debug("Sleeping for " + ms + "ms");
			Thread.sleep(ms);
		} catch (InterruptedException e1) {
			log.error("An error occured while trying to sleep: \n" + e1.toString());
		}
	}
	
}
