package com.packtpub.libgdx.light.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.packtpub.libgdx.light.util.Constants;
import com.badlogic.gdx.graphics.g2d.Sprite;

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
		renderTestObjects();
	}
	
	/**
	 * Renders the test objects for testing.
	 */
	private void renderTestObjects() {
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Sprite sprite : worldController.testSprites) {
			sprite.draw(batch);
		}
		batch.end();
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

