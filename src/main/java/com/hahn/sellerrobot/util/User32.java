package com.hahn.sellerrobot.util;

import com.hahn.sellerrobot.util.exceptions.GetWindowRectException;
import com.hahn.sellerrobot.util.exceptions.ResizeWindowException;
import com.hahn.sellerrobot.util.exceptions.WindowNotFoundException;
import com.hahn.sellerrobot.util.exceptions.WindowToForegroundException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface User32 extends StdCallLibrary {
	User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

	/**
	 * Retrieves a handle to the top-level window whose class name and window name match the specified strings. 
	 * @param lpClassName Optional. If null find any window that matches lpWindowName
	 * @param lpWindowName The window name (the window's title). If this parameter is NULL, all window names match.
	 * @return Handle to the window or null on error
	 */
	HWND FindWindow(String lpClassName, String lpWindowName);

	/**
	 * @param handle A handle to the window
	 * @param rect Array to store result in: int[] { left, top, right, bottom }
	 * @return Nonzero on success
	 */
	int GetWindowRect(HWND handle, int[] rect);
	
	/**
	 * 
	 * @param hWnd A handle to the window
		 * @param hWndInsertAfter null
	 * @param x The new position of the left side of the window, in client coordinates.
	 * @param y The new position of the top of the window, in client coordinates.
	 * @param cx The new width of the window, in pixels.
	 * @param cy The new height of the window, in pixels.
	 * @param uFlags The window sizing and positioning flags. (0x4042 recommended - ASYNC, SHOWWINDOW, NOMOVE)
	 * @return Nonzero on success
	 */
	int SetWindowPos(HWND hWnd, HWND hWndInsertAfter, int x, int y, int cx, int cy, int uFlags);
	
	/**
	 * Move window to foreground
	 * @param hWnd A handle to the window
	 * @return Nonzero on success
	 */
	int SetForegroundWindow(HWND hWnd);
	
	/**
	 * Get the rectangle of the window with the given name
	 * @param windowName The name of the window
	 * @return int[] { left, top, right, bottom }
	 * @throws WindowNotFoundException
	 * @throws GetWindowRectException
	 */
	static int[] getRect(String windowName) throws WindowNotFoundException, GetWindowRectException {
		HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
		if (hwnd == null) {
			throw new WindowNotFoundException("", windowName);
		}

		int[] rect = { 0, 0, 0, 0 };
		int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
		if (result == 0) {
			throw new GetWindowRectException(windowName);
		}
		return rect;
	}
	
	static void setSize(String windowName, int width, int height) throws WindowNotFoundException, ResizeWindowException {
		HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
		if (hwnd == null) {
			throw new WindowNotFoundException("", windowName);
		}
		
		int result;		
		result = User32.INSTANCE.SetWindowPos(hwnd, null, 0, 0, width, height, 0x4042);
		if (result == 0) {
			throw new ResizeWindowException(windowName);
		}
	}
	
	static void bringToForeground(String windowName) throws WindowNotFoundException, WindowToForegroundException {
		HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
		if (hwnd == null) {
			throw new WindowNotFoundException("", windowName);
		}
		
		int result = User32.INSTANCE.SetForegroundWindow(hwnd);
		if (result == 0) {
			throw new WindowToForegroundException(windowName);
		}
	}
}