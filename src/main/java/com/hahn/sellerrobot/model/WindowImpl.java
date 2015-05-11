package com.hahn.sellerrobot.model;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hahn.sellerrobot.util.User32;
import com.hahn.sellerrobot.util.exceptions.GetWindowRectException;
import com.hahn.sellerrobot.util.exceptions.ResizeWindowException;
import com.hahn.sellerrobot.util.exceptions.WindowNotFoundException;
import com.hahn.sellerrobot.util.exceptions.WindowToForegroundException;

/**
 * 
 * @author John Espenhahn
 *
 * Uses:
 * 	http://stackoverflow.com/questions/6091531/how-to-get-the-x-and-y-of-a-program-window-in-java
 *
 */
public class WindowImpl implements Window {
	private static Logger log = LogManager.getLogger(WindowImpl.class);
	
	private String window_name;
	private int expected_width, expected_height;
	
	private long last_check;
	private Point top_left, bottom_right;
	
	private Robot robot;
	
	public WindowImpl(String window_name, int expected_width, int expected_height) throws AWTException, WindowNotFoundException, GetWindowRectException {		
		this.window_name = window_name;
		this.expected_width = expected_width;
		this.expected_height = expected_height;
		
		this.robot = new Robot();
	}
	
	@Override
	public int getWidth() {
		return expected_width;
	}
	
	@Override
	public int getHeight() {
		return expected_height;
	}
	
	private void refreshLocation() throws WindowNotFoundException, GetWindowRectException {
		if (System.currentTimeMillis() - this.last_check > 100) {
			this.last_check = System.currentTimeMillis();
			
			// log.debug("Refreshing " + window_name + "'s location");
			int[] rect = User32.getRect(window_name);
			this.top_left = new Point(rect[0], rect[1]);
			this.bottom_right = new Point(rect[2], rect[3]);
		}
	}
	
	private Point getTopLeft() throws WindowNotFoundException, GetWindowRectException {
		refreshLocation();
		return this.top_left;
	}
	
	private int getRealWidth() throws WindowNotFoundException, GetWindowRectException {
		refreshLocation();
		return this.bottom_right.x - this.top_left.x;
	}
	
	private int getRealHeight() throws WindowNotFoundException, GetWindowRectException {
		refreshLocation();
		return this.bottom_right.y - this.top_left.y;
	}
	
	private void fixSize() throws WindowNotFoundException, GetWindowRectException, ResizeWindowException {
		if (this.expected_width < 0 || this.expected_height < 0) return;
		
		int width = getRealWidth();
		int height = getRealHeight();
		
		if (width != this.expected_width || height != this.expected_height) {
			log.debug(String.format("Resizing window %s to %dx%d", this.window_name, this.expected_width, this.expected_height));
			User32.setSize(this.window_name, this.expected_width, this.expected_height);
			
			// The last loaded size is no longer valid
			this.last_check = 0;
			
			// Wait for size to update
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error(e);
			}
			
			// Check the size has been resized properly
			fixSize();
		}
	}
	
	private void bringToFront() throws WindowNotFoundException, WindowToForegroundException {
		User32.bringToForeground(this.window_name);
	}
	
	@Override
	public synchronized void resize(int width, int height) throws WindowNotFoundException, GetWindowRectException, ResizeWindowException {
		this.expected_width = width;
		this.expected_height = height;
		
		fixSize();
	}
	
	@Override
	public synchronized void click(int x, int y) throws WindowNotFoundException, GetWindowRectException, ResizeWindowException, WindowToForegroundException, InterruptedException {
		fixSize();
		bringToFront();
		
		Point loc = getTopLeft();
		log.debug(String.format("Clicking window %s at %dx%d", this.window_name, x, y));
		// log.debug(String.format("Top left at absolute %dx%d", loc.x, loc.y));
		
		robot.mouseMove(x + loc.x, y + loc.y);
		
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		Thread.sleep(1000);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	@Override
	public synchronized void type(String text) throws WindowNotFoundException, WindowToForegroundException {
		log.debug("Typing '" + text + "'");
		bringToFront();
		
		// Type the text
		for (char c: text.toCharArray()) {
			if (Character.isUpperCase(c)) {
	            robot.keyPress(KeyEvent.VK_SHIFT);
	        }
			
			int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);			
	        robot.keyPress(keyCode);
	        robot.keyRelease(keyCode);

	        if (Character.isUpperCase(c)) {
	            robot.keyRelease(KeyEvent.VK_SHIFT);
	        }
		}
	}
	
	@Override
	public synchronized void mouseWheel(int amount) throws WindowNotFoundException, WindowToForegroundException {
		log.debug("Mouse wheeling " + amount);
		bringToFront();
		
		robot.mouseWheel(amount);
	}
}
