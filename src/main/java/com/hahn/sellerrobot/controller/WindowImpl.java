package com.hahn.sellerrobot.controller;

import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_1;
import static java.awt.event.KeyEvent.VK_2;
import static java.awt.event.KeyEvent.VK_3;
import static java.awt.event.KeyEvent.VK_4;
import static java.awt.event.KeyEvent.VK_5;
import static java.awt.event.KeyEvent.VK_6;
import static java.awt.event.KeyEvent.VK_7;
import static java.awt.event.KeyEvent.VK_8;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_AMPERSAND;
import static java.awt.event.KeyEvent.VK_ASTERISK;
import static java.awt.event.KeyEvent.VK_AT;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_BACK_QUOTE;
import static java.awt.event.KeyEvent.VK_BACK_SLASH;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_CIRCUMFLEX;
import static java.awt.event.KeyEvent.VK_CLOSE_BRACKET;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOLLAR;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_EQUALS;
import static java.awt.event.KeyEvent.VK_EXCLAMATION_MARK;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_G;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_K;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_NUMBER_SIGN;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_OPEN_BRACKET;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_PERIOD;
import static java.awt.event.KeyEvent.VK_PLUS;
import static java.awt.event.KeyEvent.VK_Q;
import static java.awt.event.KeyEvent.VK_QUOTE;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SEMICOLON;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_SLASH;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_T;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_U;
import static java.awt.event.KeyEvent.VK_UNDERSCORE;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hahn.sellerrobot.model.Window;
import com.hahn.sellerrobot.util.User32;
import com.hahn.sellerrobot.util.exceptions.GetWindowRectException;
import com.hahn.sellerrobot.util.exceptions.ResizeWindowException;
import com.hahn.sellerrobot.util.exceptions.WindowNotFoundException;
import com.hahn.sellerrobot.util.exceptions.WindowToForegroundException;

/**
 * 
 * @author John Espenhahn
 *
 * References:
 * 	http://stackoverflow.com/questions/6091531/how-to-get-the-x-and-y-of-a-program-window-in-java
 *  http://stackoverflow.com/questions/1248510/convert-string-to-keyevents
 *
 */
public class WindowImpl implements Window {
	private static Logger log = LogManager.getLogger(WindowImpl.class);
	
	private String window_name;
	private int expected_width, expected_height;
	
	private long last_check;
	private Point top_left, bottom_right;
	
	private Robot robot;
	
	/**
	 * 
	 * @param window_name The name of the window
	 * @param expected_width The target/expected width of the window
	 * @param expected_height The target/expected height of the window
	 * @throws AWTException If failed to create Robot
	 */
	public WindowImpl(String window_name, int expected_width, int expected_height) throws AWTException {		
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
	
	public Point getTopLeft() throws WindowNotFoundException, GetWindowRectException {
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
	public synchronized void click(int x, int y) throws WindowNotFoundException, GetWindowRectException, ResizeWindowException, WindowToForegroundException {
		fixSize();
		bringToFront();
		
		Point loc = getTopLeft();
		log.debug(loc);
		log.debug(String.format("Clicking window %s at %dx%d", this.window_name, x, y));
		// log.debug(String.format("Top left at absolute %dx%d", loc.x, loc.y));
		
		robot.mouseMove(x + loc.x, y + loc.y);
		
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.debug(e);
		}
		
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	@Override
	public synchronized void type(String text) throws WindowNotFoundException, WindowToForegroundException {
		log.debug("Typing '" + text + "'");
		bringToFront();
		
		for (char c: text.toCharArray()) {
			type(c);
			
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	public void type(char character) {
        switch (character) {
        case 'a': doType(VK_A); break;
        case 'b': doType(VK_B); break;
        case 'c': doType(VK_C); break;
        case 'd': doType(VK_D); break;
        case 'e': doType(VK_E); break;
        case 'f': doType(VK_F); break;
        case 'g': doType(VK_G); break;
        case 'h': doType(VK_H); break;
        case 'i': doType(VK_I); break;
        case 'j': doType(VK_J); break;
        case 'k': doType(VK_K); break;
        case 'l': doType(VK_L); break;
        case 'm': doType(VK_M); break;
        case 'n': doType(VK_N); break;
        case 'o': doType(VK_O); break;
        case 'p': doType(VK_P); break;
        case 'q': doType(VK_Q); break;
        case 'r': doType(VK_R); break;
        case 's': doType(VK_S); break;
        case 't': doType(VK_T); break;
        case 'u': doType(VK_U); break;
        case 'v': doType(VK_V); break;
        case 'w': doType(VK_W); break;
        case 'x': doType(VK_X); break;
        case 'y': doType(VK_Y); break;
        case 'z': doType(VK_Z); break;
        case 'A': doType(VK_SHIFT, VK_A); break;
        case 'B': doType(VK_SHIFT, VK_B); break;
        case 'C': doType(VK_SHIFT, VK_C); break;
        case 'D': doType(VK_SHIFT, VK_D); break;
        case 'E': doType(VK_SHIFT, VK_E); break;
        case 'F': doType(VK_SHIFT, VK_F); break;
        case 'G': doType(VK_SHIFT, VK_G); break;
        case 'H': doType(VK_SHIFT, VK_H); break;
        case 'I': doType(VK_SHIFT, VK_I); break;
        case 'J': doType(VK_SHIFT, VK_J); break;
        case 'K': doType(VK_SHIFT, VK_K); break;
        case 'L': doType(VK_SHIFT, VK_L); break;
        case 'M': doType(VK_SHIFT, VK_M); break;
        case 'N': doType(VK_SHIFT, VK_N); break;
        case 'O': doType(VK_SHIFT, VK_O); break;
        case 'P': doType(VK_SHIFT, VK_P); break;
        case 'Q': doType(VK_SHIFT, VK_Q); break;
        case 'R': doType(VK_SHIFT, VK_R); break;
        case 'S': doType(VK_SHIFT, VK_S); break;
        case 'T': doType(VK_SHIFT, VK_T); break;
        case 'U': doType(VK_SHIFT, VK_U); break;
        case 'V': doType(VK_SHIFT, VK_V); break;
        case 'W': doType(VK_SHIFT, VK_W); break;
        case 'X': doType(VK_SHIFT, VK_X); break;
        case 'Y': doType(VK_SHIFT, VK_Y); break;
        case 'Z': doType(VK_SHIFT, VK_Z); break;
        case '`': doType(VK_BACK_QUOTE); break;
        case '0': doType(VK_0); break;
        case '1': doType(VK_1); break;
        case '2': doType(VK_2); break;
        case '3': doType(VK_3); break;
        case '4': doType(VK_4); break;
        case '5': doType(VK_5); break;
        case '6': doType(VK_6); break;
        case '7': doType(VK_7); break;
        case '8': doType(VK_8); break;
        case '9': doType(VK_9); break;
        case '-': doType(VK_MINUS); break;
        case '=': doType(VK_EQUALS); break;
        case '~': doType(VK_SHIFT, VK_BACK_QUOTE); break;
        case '!': doType(VK_EXCLAMATION_MARK); break;
        case '@': doType(VK_AT); break;
        case '#': doType(VK_NUMBER_SIGN); break;
        case '$': doType(VK_DOLLAR); break;
        case '%': doType(VK_SHIFT, VK_5); break;
        case '^': doType(VK_CIRCUMFLEX); break;
        case '&': doType(VK_AMPERSAND); break;
        case '*': doType(VK_ASTERISK); break;
        case '(': doType(VK_SHIFT, VK_9); break;
        case ')': doType(VK_SHIFT, VK_0); break;
        case '_': doType(VK_UNDERSCORE); break;
        case '+': doType(VK_PLUS); break;
        case '\t': doType(VK_TAB); break;
        case '\n': doType(VK_ENTER); break;
        case '[': doType(VK_OPEN_BRACKET); break;
        case ']': doType(VK_CLOSE_BRACKET); break;
        case '\\': doType(VK_BACK_SLASH); break;
        case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); break;
        case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET); break;
        case '|': doType(VK_SHIFT, VK_BACK_SLASH); break;
        case ';': doType(VK_SEMICOLON); break;
        case ':': doType(VK_SHIFT, VK_SEMICOLON); break;
        case '\'': doType(VK_QUOTE); break;
        case '"': doType(VK_SHIFT, VK_QUOTE); break;
        case ',': doType(VK_COMMA); break;
        case '<': doType(VK_SHIFT, VK_COMMA); break;
        case '.': doType(VK_PERIOD); break;
        case '>': doType(VK_SHIFT, VK_PERIOD); break;
        case '/': doType(VK_SLASH); break;
        case '?': doType(VK_SHIFT, VK_SLASH); break;
        case ' ': doType(VK_SPACE); break;
        default:
            throw new IllegalArgumentException("Cannot type character " + character);
        }
    }
	
	private void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }

        robot.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        robot.keyRelease(keyCodes[offset]);
    }
	
	@Override
	public synchronized void mouseWheel(int amount) throws WindowNotFoundException, WindowToForegroundException {
		log.debug("Mouse wheeling " + amount);
		bringToFront();
		
		robot.mouseWheel(amount);
	}
}
