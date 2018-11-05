package com.packtpub.libgdx.light.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.light.game.Level.BLOCK_TYPE;
import com.packtpub.libgdx.light.game.objects.AbstractGameObject;
import com.packtpub.libgdx.light.game.objects.Pillars;
import com.packtpub.libgdx.light.game.objects.Shard;
import com.packtpub.libgdx.light.game.objects.DarkRock;
import com.packtpub.libgdx.light.game.objects.Ember;
import com.packtpub.libgdx.light.game.objects.Orb;

/**
 * This class compiles all the game objects and builds the level. Author: Jacob
 * Kole
 */
public class Level {

	public static final String TAG = Level.class.getName();

	/**
	 * Grabs the color from the image's pixel and determines what kind of
	 * AbstractGameObject it needs to place at that location.
	 * 
	 * @author Jacob Kole
	 */
	public enum BLOCK_TYPE {
		EMPTY(0, 0, 0), // black
		DARKROCK(0, 255, 0), // green
		PLAYER_SPAWNPOINT(255, 255, 255), // white
		ITEM_EMBER(255, 0, 255), // purple
		ITEM_SHARD(255, 255, 0); // yellow

		private int color;

		// checks a block type based on color
		private BLOCK_TYPE(int r, int g, int b) {
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}

		// checks to see if two colors are exactly the same
		public boolean sameColor(int color) {
			return this.color == color;
		}

		// gets the color of the block
		public int getColor() {
			return color;
		}
	}

	// player
	public Orb orb;

	// objects
	public Array<DarkRock> darkRocks;
	public Array<Shard> shards;
	public Array<Ember> embers;

	// decoration
	public Pillars pillars;

	/**
	 * initiates the level through a filename
	 * 
	 * @param filename name of the file for the level
	 */
	public Level(String filename) {
		init(filename);
	}

	/**
	 * Initializes the level's objects during the level loading process and does all
	 * the placements of the needed AbstractGameObjects.
	 * 
	 * @param filename name of the file for the level
	 */
	private void init(String filename) {
		// player character
		orb = null;
		// objects array for platforming
		darkRocks = new Array<DarkRock>();
		// shards array
		shards = new Array<Shard>();
		// embers array
		embers = new Array<Ember>();

		// load image file that represents the level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		// scan pixels from top-left to bottom-right
		int lastPixel = -1;
		for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
			for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
				AbstractGameObject obj = null;
				AbstractGameObject tempObj = null;
				float offsetHeight = 0;
				// height grows from bottom to top
				float baseHeight = pixmap.getHeight() - pixelY;
				// get color of current pixel as 32-bit RGBA value
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				// find matching color value to identify block type at (x,y)
				// point and create the corresponding game object if there is
				// a match

				// empty space
				if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
					// do nothing
				}
				// rock
				else if (BLOCK_TYPE.DARKROCK.sameColor(currentPixel)) {
					if (lastPixel != currentPixel) {
						obj = new DarkRock();
						// float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						// obj.position.set(pixelX, baseHeight * obj.dimension.y
						// * heightIncreaseFactor + offsetHeight);
						obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
						// set the rock then add it to the rocks array
						darkRocks.add((DarkRock) obj);
					} else {
						darkRocks.get(darkRocks.size - 1).increaseLength(1);
					}
				}
				// player spawn point
				else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
					obj = new Orb();
					//offsetHeight = -3.0f;
					offsetHeight = -2.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					orb = (Orb) obj;
				}
				// embers
				else if (BLOCK_TYPE.ITEM_EMBER.sameColor(currentPixel)) {
					obj = new Ember();
					tempObj = new DarkRock();
					offsetHeight = -2.25f;
					obj.position.set(pixelX, baseHeight * tempObj.dimension.y + offsetHeight);
					// set the ember then add it to the embers array
					embers.add((Ember) obj);
				}
				// shards
				else if (BLOCK_TYPE.ITEM_SHARD.sameColor(currentPixel)) {
					obj = new Shard();
					tempObj = new DarkRock();
					offsetHeight = -2.25f;
					obj.position.set(pixelX, baseHeight * tempObj.dimension.y + offsetHeight);
					// set the shard then add it to the shards array
					shards.add((Shard) obj);
				}
				// unknown object
				else {
					int r = 0xff & (currentPixel >>> 24); // red color channel
					int g = 0xff & (currentPixel >>> 16); // green color channel
					int b = 0xff & (currentPixel >>> 8); // blue color channel
					int a = 0xff & currentPixel; // alpha channel
					Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelY + ">: r<" + r + "> g<" + g
							+ "> b<" + b + "> a<" + a + ">");
				}
				lastPixel = currentPixel;
			}
		}

		// decoration
		pillars = new Pillars(pixmap.getWidth());
		pillars.position.set(-1, -1);

		// free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "level '" + filename + "' loaded");
	}

	/**
	 * Renders all the AbstractGameObjects, interactive and decorative.
	 * 
	 * @param batch the batch of sprites to render
	 */
	public void render(SpriteBatch batch) {
		// Draw Mountains
		pillars.render(batch);

		// Draw Rocks
		for (DarkRock darkRock : darkRocks)
			darkRock.render(batch);

		// Draw Shards
		for (Shard shard : shards)
			shard.render(batch);

		// Draw Ember
		for (Ember ember : embers)
			ember.render(batch);

		// Draw Player Character
		orb.render(batch);
	}

	/**
	 * Sends updates to each of the listed AbstractGameObjects. Those being:
	 * BunnyHead, Rocks, GoldCoins, Feathers, and Clouds.
	 * 
	 * @param deltaTime the current time
	 */
	public void update(float deltaTime) {
		// update the bunny head
		// aka: where am I, have I touched anything,
		// what am I doing
		orb.update(deltaTime);

		// update the rocks
		// aka: have I been touched, am I moving
		for (DarkRock darkRock : darkRocks)
			darkRock.update(deltaTime);

		// update the shards
		// aka: have they been picked up yet
		for (Shard shard : shards)
			shard.update(deltaTime);

		// update the embers
		// aka: have they been picked up yet
		for (Ember ember : embers)
			ember.update(deltaTime);
	}
}
