package com.hahn.sellerrobot.model;

import java.awt.Point;

import com.hahn.sellerrobot.util.exceptions.GetWindowRectException;
import com.hahn.sellerrobot.util.exceptions.ResizeWindowException;
import com.hahn.sellerrobot.util.exceptions.WindowNotFoundException;
import com.hahn.sellerrobot.util.exceptions.WindowToForegroundException;

public interface Window {
	
	/**
	 * Resize the window
	 * @param width The new width
	 * @param height The new height
	 */
	void resize(int width, int height) throws WindowNotFoundException, GetWindowRectException, ResizeWindowException;
	
	/**
	 * Get the top left of the window
	 * @return The absolute top left point of the window 
	 * @throws WindowNotFoundException
	 * @throws GetWindowRectException
	 */
	Point getTopLeft() throws WindowNotFoundException, GetWindowRectException;
	
	/**
	 * Click on a point on the app window
	 * @param x Relative x location
	 * @param y Relative y location
	 */
	void click(int x, int y) throws WindowNotFoundException, GetWindowRectException, ResizeWindowException, WindowToForegroundException;
	
	/**
	 * Use the robot to type the given text
	 * @param text The text to type
	 */
	void type(String text) throws WindowNotFoundException, WindowToForegroundException;
	
	void mouseWheel(int amount) throws WindowNotFoundException, WindowToForegroundException;
	
	int getWidth();
	
	int getHeight();
}
