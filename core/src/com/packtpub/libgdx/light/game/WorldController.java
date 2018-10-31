package com.packtpub.libgdx.light.game;

import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.packtpub.libgdx.light.util.CameraHelper;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.light.game.objects.DarkRock;
import com.packtpub.libgdx.light.util.Constants;

/**
 * Handles the updates in the world. It KNOWS where objects are and what they're
 * doing at all times and makes sure the renderer can draw objects in their
 * correct positions. Also handles collision detection.
 * 
 * @author Jacob Kole
 */
public class WorldController extends InputAdapter implements Disposable {
	private static final String TAG = 
			WorldController.class.getName();
	public CameraHelper cameraHelper;
	
	public Level level;
	public int life;
	public int time;
	
	public float livesVisual;
	public float scoreVisual;

	/**
	 * constructor for world controller
	 * 
	 * @param game instance of the game/BunnyMain
	 */
	public WorldController() {
		init();
	}

	/**
	 * Initialize the world controller. Set lives. Get a CameraHelper.
	 */
	private void init() {
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		life = Constants.LIVES_START;
		initLevel();
	}
	
	/**
	 * Initialize the level with 100 seconds.
	 */
	private void initLevel() {
		time = 100;
		level = new Level(Constants.LEVEL_01);
	}

	/**
	 * Build a pixmap
	 * 
	 * @param width  width of the procedural pixmap
	 * @param height height of the procedural pixmap
	 * @return the built pixmap
	 */
	private Pixmap createProceduralPixmap(int width, int height) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		// fill square w/ red color at 50% opacity
		pixmap.setColor(1, 0, 0, 0.5f);
		pixmap.fill();
		// Draw a yellow colored x shape on square
		pixmap.setColor(1, 1, 0, 1);
		pixmap.drawLine(0, 0, width, height);
		pixmap.drawLine(width, 0, 0, height);
		// draw a cyan colored border around square
		pixmap.setColor(0, 1, 1, 1);
		pixmap.drawRectangle(0, 0, width, height);
		return pixmap;
	}

	/**
	 * Updates the level (and its objects), update the camera, handle game inputs,
	 * handle collisions.
	 * 
	 * @param deltaTime the game time
	 */
	public void update(float deltaTime) {
		handleDebugInput(deltaTime);
		cameraHelper.update(deltaTime);
	}
	
	/**
	 * Handles all the debug inputs.
	 * @param deltaTime time between updates
	 */
	private void handleDebugInput(float deltaTime) {
		if(Gdx.app.getType() != ApplicationType.Desktop) return;

		// Camera Controls(move)
		float camMoveSpeed = 5 * deltaTime;
		float camMoveSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camMoveSpeed *= camMoveSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			moveCamera(-camMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			moveCamera(camMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.UP))
			moveCamera(0, camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			moveCamera(0, -camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
			cameraHelper.setPosition(0, 0);

		// Camera controls(zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD))
			cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH))
			cameraHelper.setZoom(1);
	}

	/**
	 * Controls the x and y movements of the camera.
	 * 
	 * @param x horizontal position
	 * @param y vertical position
	 */
	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	/**
	 * Checks for special key inputs that restart the world and change how the
	 * camera follows the player. Also handles escaping back to the menu screen.
	 */
	@Override
	public boolean keyUp(int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world restarted");
		}
//		// Toggle camera follow
//		else if (keycode == Keys.ENTER) {
//			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.orb);
//			Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
//		}
//		// Back to Menu
//		else if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
//			//backToMenu();
//		}
		return false;
	}

	/**
	 * Overridden dispose method destorys b2world if its extant
	 */
	@Override
	public void dispose() {
//		if (b2world != null)
//			b2world.dispose();

	}
}