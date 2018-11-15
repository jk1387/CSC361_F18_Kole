package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.packtpub.libgdx.light.LightMain;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

/**
 * Launches the game. It can be set to launch the game in debug
 * mode as well, which draws grid lines when rendered in order
 * to view how objects are laid out and their collision boxes.
 * @author Jacob Kole
 */
public class DesktopLauncher {
	//private static boolean for atlas and outline
	private static boolean rebuildAtlas = false; // rebuilds map. Switch to true if the way the atlas is drawn is changed
	private static boolean drawDebugOutline = false; // turn on to see pink lines
	
	/**
	 * Runner for the whole game.
	 * @param arg
	 */
	public static void main (String[] arg) {
		// if the Atlas needs to be rebuilt, this needs to
		// set the settings and direct where the image is found
		if (rebuildAtlas) {
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.duplicatePadding = false;
			settings.debug = drawDebugOutline;

			TexturePacker.process(settings, "assets-raw/images", "../core/assets/images", "light.atlas");
			TexturePacker.process(settings, "assets-raw/images", "../core/assets/images-ui","light-ui.atlas");
		}
			
		LwjglApplicationConfiguration cfg = new
		LwjglApplicationConfiguration();
		cfg.title = "Light";
		cfg.width = 800;
		cfg.height = 480;
		new LwjglApplication(new LightMain(), cfg);
	}
}