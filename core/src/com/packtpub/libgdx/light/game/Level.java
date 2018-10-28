package com.packtpub.libgdx.light.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
//import com.packtpub.libgdx.light.game.objects.AbstractGameObject;
//import com.packtpub.libgdx.light.game.objects.Clouds;
//import com.packtpub.libgdx.light.game.objects.Mountains;
//import com.packtpub.libgdx.light.game.objects.Rock;
//import com.packtpub.libgdx.light.game.objects.WaterOverlay;
//import com.packtpub.libgdx.light.game.objects.orb;
//import com.packtpub.libgdx.light.game.objects.Feather;
//import com.packtpub.libgdx.light.game.objects.GoldCoin;
//import com.packtpub.libgdx.light.game.objects.Carrot;
//import com.packtpub.libgdx.light.game.objects.Goal;

/**
 * This class compiles all the game objects and builds the level.
 * Author: Jacob Kole
 */
public class Level {
	
	public static final String TAG = Level.class.getName();
	
	// object member variables
	public Orb orb;
	public Array<GoldCoin> goldcoins;
	public Array<Feather> feathers;
	public Array<Carrot> carrots;
	public Goal goal;
	
	/**
	 * Grabs the color from the image's pixel and determines
	 * what kind of AbstractGameObject it needs to place
	 * at that location.
	 * @author Jacob Kole
	 */
	public enum BLOCK_TYPE {
		EMPTY(0, 0, 0), // black
		ROCK(0, 255, 0), // green
		PLAYER_SPAWNPOINT(255, 255, 255), // white
		GOAL(255, 0, 0), // red
		ITEM_EMBER(255, 0, 255), // purple
		ITEM_SHARD(255, 255, 0); // yellow
		
		private int color;
	
		// checks a block type based on color
		private BLOCK_TYPE (int r, int g, int b) {
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}
	
		// checks to see if two colors are exactly the same
		public boolean sameColor (int color) {
			return this.color == color;
		}
	
		// gets the color of the block
		public int getColor () {
			return color;
		}
	}
	
//	// objects
//	public Array<Rock> rocks;
	
	// decoration
//	public Clouds clouds;
//	public Mountains mountains;
//	public WaterOverlay waterOverlay;
	
	/**
	 * initiates the level through a filename
	 * @param filename name of the file for the level
	 */
	public Level (String filename) {
		init(filename);
		orb.position.y = 1.0f;
	}
	
	/**
	 * Initializes the level's objects during the level
	 * loading process and does all the placements of the
	 * needed AbstractGameObjects.
	 * @param filename name of the file for the level
	 */
	private void init (String filename) {
		// player character
		orb = null;
		// objects array for platforming
		rocks = new Array<Rock>();
		// gold coins array
		goldcoins = new Array<GoldCoin>();
		// feathers array
		feathers = new Array<Feather>();
		// carrot array
		carrots = new Array<Carrot>();
		
		// load image file that represents the level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		// scan pixels from top-left to bottom-right
		int lastPixel = -1;
		for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
			for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				// height grows from bottom to top
				float baseHeight = pixmap.getHeight() - pixelY;
				// get color of current pixel as 32-bit RGBA value
				int currentPixel = pixmap.getPixel(pixelX,  pixelY);
				// find matching color value to identify block type at (x,y)
				// point and create the corresponding game object if there is
				// a match
				
				// empty space
				if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
					// do nothing
				}
				// rock
				else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
					if (lastPixel != currentPixel) {
						obj = new Rock();
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y 
								* heightIncreaseFactor + offsetHeight);
						// set the rock then add it to the rocks array
						rocks.add((Rock)obj);
					} else {
						rocks.get(rocks.size - 1).increaseLength(1);
					}
				}
				// player spawn point
				else if
					(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
					obj = new orb();
					offsetHeight = -3.0f;
					obj.position.set(pixelX,baseHeight * obj.dimension.y +
							offsetHeight);
					orb = (orb)obj;
				}
				// feather
				else if
					(BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {
					obj = new Feather();
					offsetHeight = -1.5f;
					obj.position.set(pixelX,baseHeight * obj.dimension.y + 
							offsetHeight);
					// set the feather then add it to the feathers array
					feathers.add((Feather)obj);
				}
				// gold coin
				else if
					(BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel)) {
					obj = new GoldCoin();
					offsetHeight = -1.5f;
					obj.position.set(pixelX,baseHeight * obj.dimension.y + 
							offsetHeight);
					// set the gold coin then add it to the gold coins array
					goldcoins.add((GoldCoin)obj);
				}
				// goal
				else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
					obj = new Goal();
					offsetHeight = -7.0f;
					obj.position.set(pixelX, baseHeight + offsetHeight);
					goal = (Goal)obj;
				}
				// unknown object
				else {
					int r = 0xff & (currentPixel >>> 24); //red color channel
					int g = 0xff & (currentPixel >>> 16); //green color channel
					int b = 0xff & (currentPixel >>> 8); //blue color channel
					int a = 0xff & currentPixel; //alpha channel
					Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<"
							+ pixelY + ">: r<" + r+ "> g<" + g + "> b<" + b + "> a<" + a + ">");
				}
				lastPixel = currentPixel;
				}
			}
		
		// decoration
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0, 2);
		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1, -1);
		waterOverlay = new WaterOverlay(pixmap.getWidth());
		waterOverlay.position.set(0, -3.75f);
		
		// free memory
		pixmap.dispose();
		Gdx.app.debug(TAG,  "level '" + filename + "' loaded");
	}
	
	/**
	 * Renders all the AbstractGameObjects, interactive
	 * and decorative.
	 * @param batch the batch of sprites to render
	 */
	public void render (SpriteBatch batch) {
		// Draw Mountains
		mountains.render(batch);
		
		// Draw Goal
		goal.render(batch);
		
		// Draw Rocks
		for (Rock rock : rocks)
			rock.render(batch);
		
		// Draw Gold Coins
		for (GoldCoin goldCoin : goldcoins)
			goldCoin.render(batch);
		
		// Draw Feathers
		for (Feather feather : feathers)
			feather.render(batch);
		
		// Draw Player Character
		orb.render(batch);
		
		// Draw Water Overlay
		waterOverlay.render(batch);
		
		// Draw Clouds
		clouds.render(batch);
	}
	
	/**
	 * Sends updates to each of the listed AbstractGameObjects.
	 * Those being: orb, Rocks, GoldCoins, Feathers, and Clouds.
	 * @param deltaTime the current time
	 */
	public void update (float deltaTime) {
		// update the bunny head
		// aka: where am I, have I touched anything,
		// what am I doing
		orb.update(deltaTime);
		
		// update the rocks
		// aka: have I been touched, am I moving
		for(Rock rock : rocks)
			rock.update(deltaTime);
		
		// update the gold coins
		// aka: have they been picked up yet
		for(GoldCoin goldCoin : goldcoins)
			goldCoin.update(deltaTime);
		
		// update the feathers
		// aka: have they been picked up yet
		for(Feather feather : feathers)
			feather.update(deltaTime);
		
		// keeps the clouds moving
		clouds.update(deltaTime);
	}
}
