package com.packtpub.libgdx.light.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.packtpub.libgdx.light.util.Constants;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.packtpub.libgdx.light.util.GamePreferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

/**
 * This class renders the world's objects and its GUI.
 * @author Jacob Kole
 */
public class WorldRenderer implements Disposable {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private WorldController worldController;
	
	/**
	 * Initializes the world renderer and creates an
	 * instance for the world controller.
	 * @param worldController
	 */
	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController; // instance of world controller
		init(); // initialize
	}
	
	/**
	 * Initializes the world renderer and its sprite batch,
	 * cameras, and GUI.
	 */
	private void init () {
		batch = new SpriteBatch(); // create a batch of sprites
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT); // makes the camera
		camera.position.set(0, 0, 0); // set origin position
		camera.update(); // update to new position
	}
	
	/**
	 * Calls renderWorld to draw the game objects of the loaded level.
	 */
	public void render () {

	}
	
	/**
	 * Resizes the dimension of the world.
	 * @param width the width to resize to
	 * @param height the height to resize to
	 */
	public void resize (int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) *
				width; // changes dimensions of camera view
		camera.update(); // updates camera
	}
	
	/**
	 * Disposes of unused resources that build up in Java
	 * and C under-layer.
	 */
	@Override public void dispose () {
		batch.dispose();
	}
}

