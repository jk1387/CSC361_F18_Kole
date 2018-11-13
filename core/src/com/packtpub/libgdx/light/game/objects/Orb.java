//Christian Crouthamel

package com.packtpub.libgdx.light.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packtpub.libgdx.light.game.Assets;
import com.packtpub.libgdx.light.game.WorldController;
import com.packtpub.libgdx.light.util.Constants;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
	
	public Fixture playerPhysicsFixture;
	public Fixture playerSensorFixture;
	
	public float movementSpeed = 5.0f;
	
//	public Body body;

	public Orb() {
		super();
		init();
		//System.out.println("----------------->" + position + " , " + body.getPosition());
	}

	public void init() {
		//body.getWorld().destroyBody(body);
		// chapter 6
		dimension.set(1, 1);
		regOrb = Assets.instance.orb.orb;
		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
		bounds.set(0, 0, dimension.x, dimension.y);
		
		//bodyDef.position.set(position);
		
		// Set physics values
//		terminalVelocity.set(3.0f, 4.0f);
//		friction.set(12.0f, 0.0f);
//		acceleration.set(0.0f, -25.0f);
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		//bodyDef.type = BodyType.KinematicBody;
		Body box = WorldController.world.createBody(bodyDef);

//		PolygonShape poly = new PolygonShape();
//		poly.setAsBox(0.5f, 0.5f);
//		playerPhysicsFixture = box.createFixture(poly, 1);
//		poly.dispose();

		CircleShape circle = new CircleShape();
//		circle.setRadius(0.5f);
//		circle.setPosition(new Vector2(0, -1.4f));
		circle.setRadius(0.18f);
		circle.setPosition(origin);
		playerSensorFixture = box.createFixture(circle, 0);
		circle.dispose();
		
		body = box;
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