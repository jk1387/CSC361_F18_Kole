package com.packtpub.libgdx.light.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.packtpub.libgdx.light.game.WorldController;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Parent for all game objects.
 * @author Jacob Kole
 */
public abstract class AbstractGameObject {

	//variables
	public Vector2 position;
	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float rotation;
	public Rectangle bounds;
	
	public Vector2 velocity;
	public Vector2 terminalVelocity;
	public Vector2 friction;
	public Vector2 acceleration;
	
	public Body body;
	public BodyDef bodyDef;
	public PolygonShape poly;
	
	/**
	 * Constructer to initialize the vectors for the object
	 * position, dimension, origin, scale, and rotation.
	 */
	public AbstractGameObject () {
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;
		bounds = new Rectangle();
		
		velocity = new Vector2();
		terminalVelocity = new Vector2(1, 1);
		friction = new Vector2();
		acceleration = new Vector2();
		bounds = new Rectangle();
		
		//body.setUserData(this);
		
//		BodyDef def = new BodyDef();
//		def.type = BodyType.StaticBody;
//		bodyDef.position.set(new Vector2(0, 0));
//		
//		body = WorldController.world.createBody(def);
//
//		PolygonShape poly = new PolygonShape();
//		poly.setAsBox(0.5f, 0.5f);
//		
//		body.createFixture(poly, 0.0f);
//		
//		poly.dispose();
	}
	
	/**
	 * Update method for the abstract game object class.
	 * @param deltaTime time between updates
	 */
	public void update (float deltaTime) {
		if(body != null)
			position.set(body.getPosition());
	}
	
	/**
	 * Render method for abstract game object class.
	 * @param batch the sprite batch
	 */
	public abstract void render (SpriteBatch batch);
}