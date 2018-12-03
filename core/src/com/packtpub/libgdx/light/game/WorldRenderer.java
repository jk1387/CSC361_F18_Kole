package com.packtpub.libgdx.light.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.packtpub.libgdx.light.game.Assets;
//import com.packtpub.libgdx.light.util.GamePreferences;
import com.packtpub.libgdx.light.util.Constants;
import com.packtpub.libgdx.light.util.GamePreferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * This class renders the world's objects and its GUI.
 * 
 * @author Jacob Kole
 */
public class WorldRenderer implements Disposable {
	private static final boolean DEBUT_DRAW_BOX2D_WORLD = false;
	private OrthographicCamera camera;
	private OrthographicCamera cameraGUI;
	private SpriteBatch batch;
	private WorldController worldController;
	private Box2DDebugRenderer b2debugRenderer;
	private Timer timer;
	//private static int cnt;

	/**
	 * Initializes the world renderer and creates an instance for the world
	 * controller.
	 * 
	 * @param worldController
	 */
	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController; // instance of world controller
		init(); // initialize
	}

	/**
	 * Initializes the world renderer and its sprite batch, cameras, and GUI.
	 */
	private void init() {
		batch = new SpriteBatch(); // create a batch of sprites
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT); // makes the camera
		camera.position.set(0, 0, 0); // set origin position
		camera.update(); // update to new position

		// creates a second camera specifically set up just to render
		// the game's GUI
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT); // makes the camera
		cameraGUI.position.set(0, 0, 0); // set origin position for GUI camera
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update(); // makes sure the camera's updated
		b2debugRenderer = new Box2DDebugRenderer();
		
		// Timer for the clock to tick down in-game
		// still need to link to "time"
		ActionListener actListner = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//cnt += 1;
				if(worldController.time > 0 && worldController.timeVisual > 0 && !worldController.won) {
					worldController.time--;
					worldController.timeVisual--;
					//System.out.println("Counter = "+cnt);
				}
			}
		};
				
		timer = new Timer(800, actListner);
		timer.start();
	}

	/**
	 * Calls renderWorld to draw the game objects of the loaded level.
	 */
	public void render() {
		renderWorld(batch);
		renderGui(batch);
	}

	/**
	 * Render the world and its WorldController and CameraHelper. Render the level.
	 * 
	 * @param batch
	 */
	private void renderWorld(SpriteBatch batch) {
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();
		
		if (DEBUT_DRAW_BOX2D_WORLD) {
			b2debugRenderer.render(WorldController.world, camera.combined);
		}
	}

	/**
	 * Renders all of the GUI elements to be displayed.
	 * @param batch sprite batch
	 */
	private void renderGui(SpriteBatch batch) {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		// draw collected gold coins icon + text
		// (anchored to top left edge)
		renderGuiTime(batch);
		// draw collected feather icon
		// (anchored to top left edge)
		renderGuiEmberPowerup(batch);
		// draw extra lives icon + text
		// (anchored to top right edge)
		renderGuiLife(batch);
		// draw FPS text
		// draw FPS text (anchored to bottom right edge)
		if (GamePreferences.instance.showFpsCounter)
			renderGuiFpsCounter(batch);
		renderGuiGameOverMessage(batch);
		batch.end();
	}
	
	/**
	 * Renders the GUI elements, specifically the overlaying score (with font).
	 * @param batch sprite batch
	 */
	public void renderGuiTime (SpriteBatch batch) {
		float x = -15;
		float y = -15;
		
		float offsetX = 50;
		float offsetY = 50;
		
		if (worldController.timeVisual < worldController.time) {
			long shakeAlpha = System.currentTimeMillis() % 360;
			float shakeDist = 1.5f;
			offsetX += MathUtils.sinDeg(shakeAlpha * 2.2f) * shakeDist;
			offsetY += MathUtils.sinDeg(shakeAlpha * 2.9f) * shakeDist;
		}
//		// draw the gold coin in the top left corner, by the score
//		batch.draw(Assets.instance.shard.shard, x, y, offsetX,
//				offsetY, 100, 100, 0.35f, -0.35f, 0);
		batch.draw(Assets.instance.shard.shard, x, y, offsetX,
				offsetY, 100, 100, 0.35f, -0.35f, 0);
//		batch.draw(Assets.instance.shard.shard, x, y, 50,
//				50, 100, 100, 0.35f, -0.35f, 0);
		// draws the score in the stored font
		Assets.instance.fonts.defaultBig.draw(batch,
				"" + (int)worldController.timeVisual, // cast to int to cut off fraction
				x + 75, y + 37);
		
		Assets.instance.fonts.defaultBig.draw(batch,
				"" + (int)worldController.shardsCollected + " / " + (int)worldController.totalShards, // cast to int to cut off fraction
				x + 165, y + 37);
	}
	
	/**
	 * Renders the GUI's extra life elements, specifically the three
	 * bunny heads to mark the amount of lives the player has
	 * in the top right.
	 * @param batch sprite batch
	 */
	private void renderGuiLife (SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
		float y = -15;
		// keeps track of the lives from the start of the game
		for (int i = 0; i < Constants.LIVES_START; i++) {
			// grays out the bunny heads when one is lost
			if (worldController.life <= i)
				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			// draws the bunny heads
			batch.draw(Assets.instance.orb.orb,
					x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1); // sets a color for the bunny head
		}
		if(worldController.life >= 0
				&&worldController.livesVisual>worldController.life) {
			   int i = worldController.life;
			   float alphaColor = Math.max(0, worldController.livesVisual
					   - worldController.life - 0.5f);
			   float alphaScale = 0.35f*(2+ worldController.life 
					   -worldController.livesVisual) * 2;
			   float alphaRotate = -45 * alphaColor;
			   batch.setColor(1.0f,0.7f,0.7f,alphaColor);
			   batch.draw(Assets.instance.orb.orb,
					   x + i*50, y, 50,50,120,100,alphaScale,-alphaScale,
					   alphaRotate);
			   batch.setColor(1,1,1,1);
		}
		
		// draw a temporary bunny head icon that is changed
		// in its alpha color, scale, and rotation over
		// time to create a life lost animation. The progress
		// of this is controlled by livesVisual in WorldController.
		if (worldController.life >= 0
				&& worldController.livesVisual > worldController.life) {
			int i = worldController.life;
			float alphaColor = Math.max(0,  worldController.livesVisual
					- worldController.life - 0.5f);
			float alphaScale = 0.35f * (2 + worldController.life
					- worldController.livesVisual) *2;
			float alphaRotate = -45 * alphaColor;
			batch.setColor(1.0f, 0.7f, 0.7f, alphaColor);
			batch.draw(Assets.instance.orb.orb,
					x + i * 50, y, 50, 50, 120, 100, alphaScale, -alphaScale,
					alphaRotate);
			batch.setColor(1, 1, 1, 1);
		}
	}
	
	/**
	 * FPS counter for the GUI that changes color 
	 * depending on how low the FPS gets (located in the bottom right)
	 * @param batch sprite batch
	 */
	private void renderGuiFpsCounter (SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		// grab the frames per second
		int fps = Gdx.graphics.getFramesPerSecond();
		// set the font style
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		// check for how many FPS the system is getting
		if (fps >= 45) {
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1); // green
		} else if (fps >= 30) {
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1); // yellow
		} else {
			// less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1); // red
		}
		// draw the FPS display
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
	}
	
	/**
	 * Renders the game over message.
	 * @param batch sprite batch
	 */
	private void renderGuiGameOverMessage (SpriteBatch batch) {
		// cuts the camera GUI's dimensions in half to
		// calculate the center of the camera's viewport
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		
		// checks if there is a game over
		if (worldController.isGameOver()) {
			// grabs the game over message font and sets its color
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1);
			
			// draws the message
			// HAlignment.CENTER: means to draw the font horizontally centered
			// to the given position
			fontGameOver.draw(batch, "GAME OVER", x, y, 1,
					Align.center, false);
			fontGameOver.setColor(1, 1, 1, 1);
		}
	}
	
	/**
	 * Checks whether there is still time left for the feather
	 * power-up effect to end. The icon is drawn in the top-left
	 * corner under the gold coin icon. The small number next to it
	 * displays the rounded time still left until the effect vanishes.
	 * It'll fade back and forth when there's less than four seconds left.
	 * @param batch sprite batch
	 */
	private void renderGuiEmberPowerup (SpriteBatch batch) {
		// where to place the feather power-up display image
		float x = -15;
		float y = 30;
		// checks how much time is left for the power-up
		float timeLeftEmberPowerup = 
				worldController.level.orb.timeLeftEmberPowerup;
		if (timeLeftEmberPowerup > 0) {
			// Start icon fade in/out if the left power-up time
			// is less than 4 seconds. The fade interval is set
			// to 5 changes per second.
			if (timeLeftEmberPowerup < 4) {
				if (((int)(timeLeftEmberPowerup * 5) % 2) != 0) {
					batch.setColor(1, 1, 1, 0.5f);;
				}
			}
			batch.draw(Assets.instance.ember.ember,
					x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
			Assets.instance.fonts.defaultSmall.draw(batch,
					"" + (int)timeLeftEmberPowerup, x + 60, y + 70);
		}
	}

	/**
	 * Resizes the dimension of the world.
	 * 
	 * @param width  the width to resize to
	 * @param height the height to resize to
	 */
	public void resize(int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width; // changes dimensions of camera view
		camera.update(); // updates camera
	}

	/**
	 * Disposes of unused resources that build up in Java and C under-layer.
	 */
	@Override
	public void dispose() {
		batch.dispose();
	}
}
