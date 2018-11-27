package com.packtpub.libgdx.light.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * Implemented as a singleton so we can call its load()
 * and save() methods from virtually anywhere inside our
 * project. The settings will be loaded from and saved to
 * a preferences file defined in Constants.PREFERENCES.
 * @author Jacob Kole
 */
public class GamePreferences {
	
	public static final String TAG = GamePreferences.class.getName();
	
	public static final GamePreferences instance =
			new GamePreferences();
	
	// variables to control sound, music, their volume,
	// charSkins, and whether or not the FPS counter
	// is visible
	public boolean sound;
	public boolean music;
	public float volSound;
	public float volMusic;
	public int charSkin;
	public boolean showFpsCounter;
	public String highScore;
	
	// local variable for preferences
	private Preferences prefs;
	
	// singleton: prevent instantiation from other classes
	private GamePreferences () {
		//prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
		prefs = Constants.prefs;
	}
	
	/**
	 * This method always tries its best to find a suitable a
	 * valid value. It supplies default values first for the getter
	 * methods just in case as it searches for the actual values
	 * in PREFERENCES. clamp() ensures the value is within
	 * the allowed range of values.
	 */
	public void load () {
		// gets the boolean values for sound and music
		sound = prefs.getBoolean("sound", true);
		music = prefs.getBoolean("music", true);
		
		// uses the clamp() range to make sure the actual
		// values found are within the set range. It will use
		// default values otherwise
		volSound = MathUtils.clamp(prefs.getFloat("volSound", 0.5f),
				0.0f, 1.0f);
		volMusic = MathUtils.clamp(prefs.getFloat("volMusic", 0.5f),
				0.0f, 1.0f);
		charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0),
				0, 2);
		
		// gets the FPS boolean value
		showFpsCounter = prefs.getBoolean("showFpsCounter", false);
		
		// gets the previous high score
		highScore = prefs.getString("highScore");
		System.out.println("GamePreferences LOAD: " + highScore);
	}
	
	/**
	 * Takes the current values of its public variables
	 * and puts them into the map of the preferences file.
	 * flush() actually writes the changed values into the file.
	 */
	public void save () {
		// changes the public variables and 
		// puts them into the preferences file map
		prefs.putBoolean("sound", sound);
		prefs.putBoolean("music", music);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putInteger("charSkin",  charSkin);
		prefs.putBoolean("showFpsCounter",  showFpsCounter);
		//prefs.putString("highScore", highScore);
		//System.out.println("GamePreferences SAVE: " + highScore);
		
		// write the changed values into the preferences file
		prefs.flush();
	}
}