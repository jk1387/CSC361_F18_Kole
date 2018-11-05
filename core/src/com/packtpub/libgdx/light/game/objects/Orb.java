//Christian Crouthamel

package com.packtpub.libgdx.light.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packtpub.libgdx.light.game.Assets;
import com.packtpub.libgdx.light.util.Constants;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * The player object. This has the most interactions across all other objects in
 * the game.
 * 
 * @author Christian, Drake, Jacob
 */
public class Orb extends AbstractGameObject {
	public static final String TAG = Orb.class.getName();
	
	public enum VIEW_DIRECTION { LEFT, RIGHT }

	private TextureRegion regOrb;
	public VIEW_DIRECTION viewDirection;

	public Orb() {
		init();
	}

	public void init() {

		// chapter 6
		dimension.set(1, 1);
		regOrb = Assets.instance.orb.orb;
		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
	};

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}

	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;
		reg = regOrb;

		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x,
				dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT, false);
		// Reset color to white
		batch.setColor(1, 1, 1, 1);
	}
}