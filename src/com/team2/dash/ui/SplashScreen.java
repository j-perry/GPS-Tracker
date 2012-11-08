


package com.team2.dash.ui;

import java.awt.Graphics2D;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;

/**
 * Class description goes here
 * @author Jonathan Perry (jp373)
 */
public class SplashScreen extends Activity {
	
	/**
	 * 	Private Members
	 */
	private int SPLASH_DISPLAY_LENGTH;			// timer to display splash screen
	private Graphics2D SPLASH_DISPLAY_IMAGE;	// stores an image loaded from memory
	private String SPLASH_DISPLAY_IMAGE_URL;	// stores image URL
	private boolean isVisible;					// defines whether to display the splash screen or not
	
	/**
	 * 	Default Constructor
	 */
	public SplashScreen() {
		this.SPLASH_DISPLAY_LENGTH = 2000; // milliseconds (2 seconds)
		this.isVisible = true;			   // splash screen is visible
	}
	
	/**
	 * Creates a new menu
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
	}
	
	/**
	 * 
	 */
	public void run() {
		
	}
	
	/**
	 * 
	 */
	public void update() {
		
	}
	
	/**
	 * 	Initialises the defined splash screen size
	 * 	@param	width 
	 * 	@param	height 
	 */
	public void setSplashScreenSize(int width, int height) {
		
	}
	
	/**
	 * 	Returns the assigned splash screen size
	 * 	@return
	 */
	public Display getSplashScreenSize() {
		// initialise a new display object with the present state of the display
		Display screen = getWindowManager().getDefaultDisplay();
		
		// return the screen object for access
		return screen;
	}	
}
