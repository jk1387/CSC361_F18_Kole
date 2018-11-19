package com.packtpub.libgdx.light.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.packtpub.libgdx.light.game.Assets;

/**
 * Abstract class to create methods for the game screen to use. 
 * @author Jacob Kole
 */
public abstract class AbstractGameScreen implements Screen {
	protected Game game;

	/**
	 * Constructor for the abstract game screen. Has a game
	 * instance.
	 * @param game
	 */
	public AbstractGameScreen(Game game) {
		this.game = game;
	}

	/**
	 * Render the game screen.
	 */
	public abstract void render(float deltaTime);

	/**
	 * Resize the game window.
	 */
	public abstract void resize(int width, int height);

	/**
	 * Show the game window.
	 */
	public abstract void show();

	/**
	 * Hide the game window.
	 */
	public abstract void hide();

	/**
	 * Pause the game.
	 */
	public abstract void pause();

	/**
	 * Resume and dispose methods for the asset manager.
	 */
	public void resume() {
		Assets.instance.init(new AssetManager());
	}

	/**
	 * Dispose of unused resources.
	 */
	public void dispose() {
		Assets.instance.dispose();
	}
}