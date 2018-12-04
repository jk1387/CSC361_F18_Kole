package com.packtpub.libgdx.light;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.packtpub.libgdx.light.screens.MenuScreen;
import com.packtpub.libgdx.light.util.AudioManager;
//import com.packtpub.libgdx.light.util.AudioManager;
import com.packtpub.libgdx.light.util.GamePreferences;
import com.packtpub.libgdx.light.game.Assets;

/**
 * Runner for the game. This creates, disposes, renders,
 * resizes, pauses, and resumes. It also creates the instances
 * of WorldController and WorldRenderer.
 * @author Jacob Kole
 */
public class LightMain extends Game {
	private static final String TAG =
			LightMain.class.getName();
	
	/**
	 * Create the instances for WorldController and WorldRenderer
	 * and initialize the assets. Set paused to false.
	 */
	@Override
	public void create () {
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		
		// Load preferences for audio settings and start playing music
		GamePreferences.instance.load();
		//AudioManager.instance.play(Assets.instance.music.song01);
		
		// Start game at menu screen
		setScreen(new MenuScreen(this));
	}
}
