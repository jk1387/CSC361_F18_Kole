package com.packtpub.libgdx.light;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.packtpub.libgdx.light.game.WorldController;
import com.packtpub.libgdx.light.game.WorldRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.packtpub.libgdx.light.game.Assets;

public class LightMain extends ApplicationAdapter {
	private static final String TAG =
			LightMain.class.getName();
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	
	private boolean paused;
	
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
		// Initialize controller and renderer
		worldController = new WorldController();
		worldRenderer = new WorldRenderer(worldController);
		// Game world is active on start
		paused = false;
	}

	/**
	 * Render the world's colors.
	 */
	@Override
	public void render () {
		// Do not update game world when paused.
		if(!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		// Sets the clear screen color to: Dark Gray
		Gdx.gl.glClearColor(0x35/255.0f, 0x35/255.0f, 0x35/255.0f,
				0xff/255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Render game world to screen
		worldRenderer.render();
	}
	
	/**
	 * Resize the window for the world to render in.
	 */
	@Override
	public void resize (int width, int height) {
		worldRenderer.resize(width, height);
	}
	
	/**
	 * Sets the game to pause.
	 */
	@Override
	public void pause () {
		paused = true;
	}
	
	/**
	 * Resume the world.
	 */
	@Override
	public void resume () {
		paused = false;
	}
	
	/**
	 * Dispose unused resources.
	 */
	@Override
	public void dispose () {
		worldRenderer.dispose();
		Assets.instance.dispose();
	}
}
