package com.hahn.sellerrobot.models;

public interface Window {

	/**
	 * Click on a point on the app window
	 * @param x Relative x location
	 * @param y Relative y location
	 * @return True on successful click
	 */
	boolean click(int x, int y);
	
	/**
	 * Resize the window
	 * @param width The new width
	 * @param height The new height
	 * @return True on successful resize
	 */
	boolean resize(int width, int height);
	
	int getWidth();
	
	int getHeight();
}
