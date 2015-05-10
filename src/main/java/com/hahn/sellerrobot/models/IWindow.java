package com.hahn.sellerrobot.models;

public interface IWindow {

	/**
	 * Click on a point on the app window
	 * @param x Relative x location
	 * @param y Relative y location
	 * @return True on successful click
	 */
	boolean click(int x, int y);
	
}
