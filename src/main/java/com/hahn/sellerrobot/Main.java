package com.hahn.sellerrobot;

import java.awt.AWTException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hahn.sellerrobot.model.FilesCollection;
import com.hahn.sellerrobot.model.Procedure;
import com.hahn.sellerrobot.model.Window;
import com.hahn.sellerrobot.model.WindowImpl;
import com.hahn.sellerrobot.model.Procedure.Event;
import com.hahn.sellerrobot.util.RunDeterminant;
import com.hahn.sellerrobot.util.exceptions.GetWindowRectException;
import com.hahn.sellerrobot.util.exceptions.MissingArgumentException;
import com.hahn.sellerrobot.util.exceptions.ResizeWindowException;
import com.hahn.sellerrobot.util.exceptions.WindowNotFoundException;
import com.hahn.sellerrobot.util.exceptions.WindowToForegroundException;

public class Main {
	private static Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		// new Console();
		
		Main main = new Main();
		while (true) {
			try {
				main.execute();
			} catch (Exception e) {
				log.fatal("A fatal error has occured!", e);
			}
			
			try {
				Thread.sleep(1000 * 60 * 30);
			} catch (InterruptedException e) {
				log.debug("Unable to sleep!");
			}
		}
	}
	
	private String focused_window; 
	private Map<String, Window> windows;
	
	private Procedure procedure;
	private FilesCollection files;
	
	public Main() {
		windows = new HashMap<String, Window>();
		files = new FilesCollection();
		
		procedure = null;
		try {
			procedure = new Procedure("procedure.json", files);
			log.debug("Procedure: " + procedure.toString());
		} catch (Exception e) {
			log.fatal("A fatal error has occured!", e);
			System.exit(-1);
		}
	}
	
	public Window getFocusedWindow() {
		if (focused_window == null) throw new RuntimeException("Tried to click a window before focusing on one");
		return windows.get(focused_window);
	}
	
	public void execute() throws AWTException, WindowNotFoundException, GetWindowRectException, MissingArgumentException, ResizeWindowException, WindowToForegroundException, InterruptedException {
		RunDeterminant determinant = procedure.getRunDeterminant();
		determinant.reset();
		while (determinant.hasNext()) {
			for (int i = 0; i < procedure.size(); i++) {
				Event e = procedure.get(i);
				switch (e.getAction()) {
				case CLICK:
					getFocusedWindow().click(e.getInt("x"), e.getInt("y"));
					break;
				case FOCUS:
					focusWindow(e);
					break;
				case SLEEP:
					sleep(e.getInt("ms"));
					break;
				case TYPE:
					getFocusedWindow().type(e.getString("text"));
					break;
				case WHEEL:
					getFocusedWindow().mouseWheel(e.getInt("amount"));
					break;
				}
			}
			
			determinant.next();
		}
	}
	
	public void focusWindow(Event e) throws AWTException, WindowNotFoundException, GetWindowRectException, MissingArgumentException, ResizeWindowException {
		focused_window = e.getString("name");
		if (!windows.containsKey(focused_window)) {
			log.debug(String.format("Focusing on new window %s", focused_window));
			
			int width, height;
			if (e.has("width")) width = e.getInt("width");
			else throw new MissingArgumentException("Must specify width when focusing on a window for the first time");
			
			if (e.has("height")) height = e.getInt("height");
			else throw new MissingArgumentException("Must specify height when focusing on a window for the first time");
			
			windows.put(focused_window, new WindowImpl(focused_window, width, height));
		} else if (e.has("width") || e.has("height")) {
			log.debug(String.format("Switching focus to window %s", focused_window));
			
			Window window = windows.get(focused_window);
			
			int width, height;
			if (e.has("width")) width = e.getInt("width");
			else width = window.getWidth();
			
			if (e.has("height")) height = e.getInt("height");
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
	
	public FilesCollection getFilesCollection() {
		return files;
	}

}
