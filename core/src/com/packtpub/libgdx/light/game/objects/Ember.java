package com.packtpub.libgdx.light.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packtpub.libgdx.light.game.Assets;

/**
 * The feather object allows the bunnyhead to jump infinitely (fly)
 * when collected
 * @author Jacob Kole
 */
public class Ember extends AbstractGameObject {
	
	// texture region for the feather
	private TextureRegion regEmber;
	
	public boolean emberCollected;
	
	/**
	 * Initialize the feather.
	 */
	public Ember() {
		init();
	}
	
	/**
	 * Initialize the dimension and image for the feather.
	 * Make sure its bound box is set for when the bunnyhead
	 * collides with the feather.
	 */
	private void init () {
		// sets the size of the feather
		dimension.set(0.5f, 0.5f);
		// grabs the image for the feather
		regEmber = Assets.instance.ember.ember;
		
		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		
		// flag for item collection
		emberCollected = false;
	}
	
	/**
	 * Renders the feather into the game world.
	 */
	public void render (SpriteBatch batch) {
		// if it's collected, don't render
		if (emberCollected) return;
		
		// reset the texture region
		TextureRegion reg = null;
		reg = regEmber;
		batch.draw(reg.getTexture(), position.x, position.y,
				origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
				rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	/**
	 * Gets the score for collecting an ember.
	 * @return
	 */
	public int getScore() {
		return 0;
	}
}
