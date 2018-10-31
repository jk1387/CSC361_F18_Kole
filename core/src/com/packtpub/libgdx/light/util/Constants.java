package com.packtpub.libgdx.light.util;

//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Preferences;

/**
 * Sets all the constants for the game. These include the level/camera
 * dimensions, texture atlas dirctory path for objects, level png,
 * and number of starting lives.
 * @author Jacob Kole
 */
public class Constants {
	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;
	
	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	
	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
	
	// Location of description file for texture atlas
	public static final String TEXTURE_ATLAS_OBJECTS = "images/light.atlas";
	
	// Location of image file for level 01
	public static final String LEVEL_01 = "levels/level-01.png";
	
	// Amount of extra lives at level start
	public static final int LIVES_START = 1;
	
	// Preferences file
	public static final String PREFERENCES = "settings.prefs";
}