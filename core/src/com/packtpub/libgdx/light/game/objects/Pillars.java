package com.packtpub.libgdx.light.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.packtpub.libgdx.light.game.Assets;
import com.badlogic.gdx.math.Vector2;

/**
 * This builds the mountains in the game background and determines
 * their size.
 * Author: Jacob Kole
 */
public class Pillars extends AbstractGameObject {
	
	// texture regions for the left and right pieces of the
	// mountain range to be merged together
	private TextureRegion pillars;
	
	// Length of the mountains
	private int length;
	
	/**
	 * Constructor to set the length range of the pillars and initiate them
	 * @param length the length of the pillars
	 */
	public Pillars (int length) {
		this.length = length;
		init();
	}
	
	/**
	 * Sets the dimensions of the pillars and pulls their assets from levelDecoration.
	 */
	private void init () {
		dimension.set(10, 50);
		
		pillars = Assets.instance.levelDecoration.pillars;
		
		// shift mountain and extend length
		origin.x = -dimension.x;
		length += dimension.x;
	}
	
	/**
	 * Draw one set of pillars in the background, stretch them to cover the whole level. Vertically and horizontally.
	 * @param batch the batch of sprites
	 * @param offsetX x offset coord
	 * @param offsetY y offset coord
	 * @param tintColor change the color overlay
	 * @param parallaxSpeedX scroll speed effect
	 */
	private void drawPillars (SpriteBatch batch, float offsetX, float offsetY, float tintColor, float parallaxSpeedX) {
		TextureRegion reg = null;
		batch.setColor(tintColor, tintColor, tintColor, 1);
		float xRel = dimension.x * offsetX;
		float yRel = dimension.y * -offsetY;
		
		// mountains span the whole level
		int mountainLength = 0;
		mountainLength += MathUtils.ceil(length/* / (2 * dimension.x) */* (1 - parallaxSpeedX));
		mountainLength += MathUtils.ceil(0.5f + offsetX);
		for (int i = 0; i < mountainLength; i++) {
			// mountain left
			reg = pillars;
			batch.draw(reg.getTexture(),origin.x + xRel + position.x * parallaxSpeedX,origin.y + yRel + position.y,
					origin.x, origin.y,dimension.x, dimension.y,scale.x, scale.y,rotation,reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(),false, false);
			xRel += dimension.x;
		}
		// reset color to white
		batch.setColor(1, 1, 1, 1);
	}
	
	/**
	 * Draw the pillars once at a set distance.
	 */
	@Override
	public void render (SpriteBatch batch) {
		// 80% distant mountains (dark gray)
		//drawPillars(batch, 0.5f, 0.5f, 0.5f, 0.8f);
		// 50% distant mountains (gray)
		drawPillars(batch, 0.25f, 0.25f, 0.7f, 0.5f);
		// 30% distant mountains (light gray)
		//drawPillars(batch, 0.0f, 0.0f, 0.9f, 0.3f);
	}
	
	/**
	 * Scroll the pillars by at a different speed to add depth.
	 * @param camPosition position of the camera
	 */
	//update the scroll position
	public void updateScrollPosition (Vector2 camPosition) {
		position.set(camPosition.x, position.y);
	}
}
