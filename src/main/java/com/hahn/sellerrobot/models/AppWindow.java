package com.hahn.sellerrobot.models;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

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
public class AppWindow implements IWindow {
	private static Logger log = LogManager.getLogger(AppWindow.class);
	
	private String window_name;
	private int expected_width, expected_height;
	
	private long last_check;
	private Point top_left, bottom_right;
	
	private Robot robot;
	
	public AppWindow(String window_name, int expected_width, int expected_height) throws AWTException, WindowNotFoundException, GetWindowRectException {		
		this.window_name = window_name;
		this.expected_width = expected_width;
		this.expected_height = expected_height;
		
		this.robot = new Robot();
		
		refreshLocation();
	}
	
	private void refreshLocation() throws WindowNotFoundException, GetWindowRectException {
		if (this.last_check - System.currentTimeMillis() < 100) {
			int[] rect = User32.getRect(window_name);
			this.top_left = new Point(rect[0], rect[1]);
			this.bottom_right = new Point(rect[2], rect[3]);
			
			this.last_check = System.currentTimeMillis();
		}
	}
	
	private Point getTopLeft() throws WindowNotFoundException, GetWindowRectException {
		refreshLocation();
		return this.top_left;
	}
	
	private int getWidth() throws WindowNotFoundException, GetWindowRectException {
		refreshLocation();
		return this.bottom_right.x - this.top_left.x;
	}
	
	private int getHeight() throws WindowNotFoundException, GetWindowRectException {
		refreshLocation();
		return this.bottom_right.y - this.top_left.y;
	}
	
	private void fixSize() throws WindowNotFoundException, GetWindowRectException, ResizeWindowException {
		int width = getWidth();
		int height = getHeight();
		
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
	public boolean click(int x, int y) {
		try {			
			fixSize();
			bringToFront();
			
			log.debug("Clicking window " + this.window_name);
			
			Point loc = getTopLeft();
			robot.mouseMove(x + loc.x, y + loc.y);
			
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			Thread.sleep(1000);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			
			return true;
		} catch (Exception e) {
			log.error(e);
			
			return false;
		}
	}
	
}
