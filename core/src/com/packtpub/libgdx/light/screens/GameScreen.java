package com.packtpub.libgdx.light.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.packtpub.libgdx.light.util.GamePreferences;
import com.packtpub.libgdx.light.game.Assets;
import com.packtpub.libgdx.light.game.WorldController;
import com.packtpub.libgdx.light.game.WorldRenderer;

/**
 * Recreation of the CanyonBunnyMain method calls, only this one
 * accommodates the Screen interface. This class now houses the
 * core functionality of running the game. Create, destroy, render,
 * resize, pause, and resume.
 * @author Jacob Kole
 */
public class GameScreen extends AbstractGameScreen {
	private static final String TAG = GameScreen.class.getName();
	
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	
	private boolean paused;
	
	/**
	 * Constructor for the game screen.
	 * @param game
	 */
	public GameScreen (Game game) {
		super(game);
	}
	
	/**
	 * Don't render if the game is paused. This renders the world
	 * using worldRenderer, and uses deltaTime in tandem with the
	 * worldController's way of updating the game in order to
	 * know when to render.
	 * @param deltaTime
	 */
	@Override
	public void render (float deltaTime) {
		// Do not update game world when paused.
		if(!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		// Sets the clear screen color to: Dark Gray
//		Gdx.gl.glClearColor(0x35/255.0f, 0x35/255.0f, 0x35/255.0f,
//				0xff/255.0f);
		Gdx.gl.glClearColor(0x10/255.0f, 0x10/255.0f, 0x10/255.0f,
				0xff/255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Render game world to screen
		worldRenderer.render();
	}
	
	/**
	 * Resets the dimensions of the game for what the
	 * world renders.
	 * @param width the horizontal width of the world
	 * @param height the vertical height of the world
	 */
	@Override
	public void resize (int width, int height) {
		worldRenderer.resize(width, height);
	}
	
	/**
	 * Acts as the create() method, keeping all the functionality
	 * of initiating the necessary processes in order to run
	 * the game.
	 */
	@Override
	public void show () {
		GamePreferences.instance.load();
		worldController = new WorldController(game);
		worldRenderer = new WorldRenderer(worldController);
		Gdx.input.setCatchBackKey(true);
	}
	
	/**
	 * Acts as the dispose() method, making sure the unused resources
	 * are being properly flushed out after being used.
	 */
	@Override
	public void hide () {
		worldRenderer.dispose();
		worldController.dispose();
		Gdx.input.setCatchBackKey(false);
	}
	
	/**
	 * Freezes the updates in the game.
	 */
	@Override
	public void pause () {
		paused = true;
	}
	
	/**
	 * Allows the updates in the game to properly
	 * update again.
	 */
	@Override
	public void resume () {
		super.resume();
		// Only called on Android!
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
