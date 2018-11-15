package com.packtpub.libgdx.light.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.packtpub.libgdx.light.util.CameraHelper;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.light.screens.MenuScreen;
import com.packtpub.libgdx.light.game.Assets;
//import com.packtpub.libgdx.light.util.AudioManager;
import com.packtpub.libgdx.light.LightMain;
import com.packtpub.libgdx.light.game.objects.AbstractGameObject;
import com.packtpub.libgdx.light.game.objects.DarkRock;
import com.packtpub.libgdx.light.game.objects.Shard;
import com.packtpub.libgdx.light.util.Constants;

/**
 * Handles the updates in the world. It KNOWS where objects are and what they're
 * doing at all times and makes sure the renderer can draw objects in their
 * correct positions. Also handles collision detection.
 * 
 * @author Jacob Kole
 */
public class WorldController extends InputAdapter implements Disposable, ContactListener {
	private static final String TAG = WorldController.class.getName();
	public CameraHelper cameraHelper;
	private Game game;

	public Level level;
	public int life;
	public int time;

	public float livesVisual;
	public float timeVisual;
	
	private float timeLeftGameOverDelay;
	
	int shardsCollected = 0; // temporary for now to test for sensors touched
	boolean doneOnce = false;

	// BOX2D STUFF
	public static World world; // contains all the box2d bodies and fixtures
	final static float MAX_VELOCITY = 7f; // move speed outside method to not create it each frame.
	boolean jump = false;
	float stillTime = 0;
	long lastGroundTime = 0;
	AbstractGameObject touchedObject; // object the player contacts
	DarkRock groundedPlatform = null; // ground detection for jumping
	AbstractGameObject toDestroy;
	//Shard shardFound = null;
	
	Timer timer;

	/**
	 * constructor for world controller
	 * 
	 * @param game instance of the game/LightMain
	 */
	public WorldController(Game game) {
		this.game = game;
		init();
	}

	/**
	 * Initialize the world controller. Set lives. Get a CameraHelper.
	 */
	private void init() {
		world = new World(new Vector2(0, -20.0f), true);
		Gdx.input.setInputProcessor(this);
		world.setContactListener(this);
		cameraHelper = new CameraHelper();
		life = Constants.LIVES_START;
		timeLeftGameOverDelay = 0;
		initLevel();
	}

	/**
	 * Initialize the level with 100 seconds.
	 */
	private void initLevel() {
		time = 30;
		timeVisual = time;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.orb);
		initPhysics();
	}

	/**
	 * Initialize the physics inside of the world using box2d assets dispose of
	 * excess to free memory
	 */
	private void initPhysics() {
//		if (world != null)
//			world.dispose(); // destroy if already init
//		world = new World(new Vector2(0, -20f), true);

		// Rocks
		Vector2 origin = new Vector2();
		for (DarkRock rock : level.darkRocks) { // for each rock
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.StaticBody;
			bodyDef.position.set(rock.position);
			Body body = world.createBody(bodyDef);
			rock.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = rock.bounds.width / 2.0f;
			origin.y = rock.bounds.height / 2.0f;
			polygonShape.setAsBox(rock.bounds.width / 2.0f, rock.bounds.height / 2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			rock.body.createFixture(fixtureDef);
			polygonShape.dispose();
		}
		
		// Shards
		for (Shard shard : level.shards) {
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.StaticBody;
			bodyDef.position.set(shard.position);
			Body body = world.createBody(bodyDef);
			shard.body = body;
			CircleShape circle = new CircleShape();
			origin.x = shard.bounds.width / 2.0f;
			origin.y = shard.bounds.height / 2.0f;
			circle.setRadius(0.18f);
			//circle.setRadius(0.5f);
			circle.setPosition(origin);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef = new FixtureDef();
			fixtureDef.shape = circle;
			fixtureDef.isSensor = true;
			shard.body.createFixture(fixtureDef);
			//shard.body.createFixture(circle, 0);
			circle.dispose();
			shard.body.setUserData(shard);
		}
	}

	/**
	 * Build a pixmap
	 * 
	 * @param width  width of the procedural pixmap
	 * @param height height of the procedural pixmap
	 * @return the built pixmap
	 */
	@SuppressWarnings("unused")
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
		world.step(Gdx.graphics.getDeltaTime(), 4, 4);
		if(toDestroy != null) { 
			world.destroyBody(toDestroy.body); 
			toDestroy = null; 
			System.out.println("DESTROYED");
		}
		handleDebugInput(deltaTime);
		if (isGameOver()/*|| goalReached*/) {
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0) {
				backToMenu();
				return;
			}
		} else {
			playerMovement();
		}
		//handleInputGame(deltaTime);
		//playerMovement();
		
		//world.step(Gdx.graphics.getDeltaTime(), 4, 4);
		//level.orb.body.setAwake(true);
		
		// level.orb.body = player;
		cameraHelper.update(deltaTime);
		level.update(deltaTime);
		
		if (!isGameOver() && isTimeGone()) {
			//AudioManager.instance.play(Assets.instance.sounds.liveLost);
			life--;
			if (isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
		
		level.pillars.updateScrollPosition(cameraHelper.getPosition());
		
		// score gain animation goes as scoreVisual catches up to new score
		if (timeVisual < time)
			timeVisual = Math.min(time,  timeVisual 
					+ 250 * deltaTime);
	}
	
	/**
	 * Boolean checker method for if the game has ended
	 * @return true if life is 0
	 */
	public boolean isGameOver() {
		return life <= 0;
	}
	
	/**
	 * Boolean checker method for if the player is out of time
	 * @return true if time is at 0
	 */
	public boolean isTimeGone() {
		return time <= 0;
	}
	
//	/**
//	 * Handles the input keys for the game. These affect
//	 * player movements in-game and change the physics affecting
//	 * the player.
//	 * @param deltaTime game time
//	 */
//	private void handleInputGame(float deltaTime) {
//		Vector2 velocity = new Vector2(0, 0);
//		
//		// Horizontal Movement
//		if (Gdx.input.isKeyPressed(Keys.D)) {
//			velocity.x += level.orb.movementSpeed;
//		} else if (Gdx.input.isKeyPressed(Keys.A)) {
//			velocity.x -= level.orb.movementSpeed;
//		}
//		
//		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
//			velocity.y += 10;
//			level.orb.body.applyLinearImpulse(velocity, level.orb.body.getPosition(), true);
//		}
//		
//		// Set velocity of player
//		level.orb.body.setLinearVelocity(velocity);
//	}

	/**
	 * Handles the input keys for the game. These affect
	 * player movements in-game and change the physics affecting
	 * the player.
	 * @param deltaTime game time
	 */
	private void playerMovement() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Vector2 vel = level.orb.body.getLinearVelocity();
		Vector2 pos = level.orb.body.getPosition();
		//System.out.println(level.orb.body.getLinearVelocity());//--------------------------------------------
		boolean grounded = isPlayerGrounded(Gdx.graphics.getDeltaTime());
		//boolean touching = isShard(Gdx.graphics.getDeltaTime());
		//isShard(Gdx.graphics.getDeltaTime());
		//System.out.println(grounded);//-----------------------------------------------------------------------
		if (grounded) {
			lastGroundTime = System.nanoTime();
		} else {
			if (System.nanoTime() - lastGroundTime < 100000000) {
				grounded = true;
			}
		}

		// cap max velocity on x
		if (Math.abs(vel.x) > MAX_VELOCITY) {
			vel.x = Math.signum(vel.x) * MAX_VELOCITY;
			level.orb.body.setLinearVelocity(vel.x, vel.y);
		}

		// calculate stilltime & damp
		if (!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
			stillTime += Gdx.graphics.getDeltaTime();
			level.orb.body.setLinearVelocity(vel.x * 0.9f, vel.y);
		} else {
			stillTime = 0;
		}

		// disable friction while jumping
		if (!grounded) {
			level.orb.playerPhysicsFixture.setFriction(0f);
			level.orb.playerSensorFixture.setFriction(0f);
		} else {
			if (!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && stillTime > 0.2) {
				level.orb.playerPhysicsFixture.setFriction(100f);
				level.orb.playerSensorFixture.setFriction(100f);
			} else {
				level.orb.playerPhysicsFixture.setFriction(0.2f);
				level.orb.playerSensorFixture.setFriction(0.2f);
			}

//			if(groundedPlatform != null && groundedPlatform.dist == 0) {
//				player.applyLinearImpulse(0, -24, pos.x, pos.y, true);				
//			}
		}

		// apply left impulse, but only if max velocity is not reached yet
		if (Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
			level.orb.body.applyLinearImpulse(-2f, 0, pos.x, pos.y, true);
		}

		// apply right impulse, but only if max velocity is not reached yet
		if (Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
			level.orb.body.applyLinearImpulse(2f, 0, pos.x, pos.y, true);
		}

		// jump, but only when grounded
		if (jump) {
			jump = false;
			if (grounded) {
				level.orb.body.setLinearVelocity(vel.x, 0);
				level.orb.body.setTransform(pos.x, pos.y + 0.01f, 0);
				level.orb.body.applyLinearImpulse(0, 10, pos.x, pos.y, true);
			}
		}
	}

	/**
	 * Detects when the player is grounded on some kind of rock. This also counts when
	 * the player is against a wall so they can wall-jump.
	 * @param deltaTime time passed between updates
	 * @return true if player is on the ground or a wall
	 */
	private boolean isPlayerGrounded(float deltaTime) {
		groundedPlatform = null;
		Array<Contact> contactList = world.getContactList();
		for (int i = 0; i < contactList.size; i++) {
			Contact contact = contactList.get(i);
			if (contact.isTouching()
					&& (contact.getFixtureA() == level.orb.playerSensorFixture || contact.getFixtureB() == level.orb.playerSensorFixture)) {

//				Vector2 pos = player.getPosition();
				Vector2 pos = level.orb.body.getPosition();
				WorldManifold manifold = contact.getWorldManifold();
				boolean below = true;
				//System.out.println("Player HAS TOUCHED DOWN");
				for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below &= (manifold.getPoints()[j].y > pos.y - 1.5f);
				}

				if (below) {
					//System.out.println("BELOW");
					if (contact.getFixtureA().getUserData() != null
							&& contact.getFixtureA().getUserData().equals("p")) {
						groundedPlatform = (DarkRock) contact.getFixtureA().getBody().getUserData();
					}

					if (contact.getFixtureB().getUserData() != null
							&& contact.getFixtureB().getUserData().equals("p")) {
						groundedPlatform = (DarkRock) contact.getFixtureB().getBody().getUserData();
					}
					return true;
				}

				return false;
			}
		}
		return false;
	}
	
//	/**
//	 * Detects when the player collides with a Shard.
//	 * @param deltaTime the time passed between updates
//	 */
//	private void isShard(float deltaTime) {
//		shardFound = null;
//		Array<Contact> contactList = world.getContactList();
//		for (int i = 0; i < contactList.size; i++) {
//			Contact contact = contactList.get(i);
//			if (contact.isTouching()
//					//&& (contact.getFixtureA() == level.orb.playerSensorFixture || contact.getFixtureB() == level.orb.playerSensorFixture)) {
//					&& (contact.getFixtureA() == level.orb.playerSensorFixture || contact.getFixtureB() == level.shards.get(i).fixture)) {
//				
//				//Vector2 pos = player.getPosition();
//				Vector2 pos = level.orb.body.getPosition();
//				WorldManifold manifold = contact.getWorldManifold();
//				boolean below = true;
//				//System.out.println("Player HAS TOUCHED DOWN");
//				for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
//					below &= (manifold.getPoints()[j].y > pos.y - 1.5f);
//				}
//
//				if (below) {
//					//System.out.println("SHARD");
//					if (contact.getFixtureA().getUserData() != null
//							&& contact.getFixtureA().getUserData().equals("p")) {
//						shardFound = (Shard) contact.getFixtureA().getBody().getUserData();
//						System.out.println("Destroyed1");
//						//shardFound.body.destroyFixture(shardFound.fixture);
//					}
//
//					if (contact.getFixtureB().getUserData() != null
//							&& contact.getFixtureB().getUserData().equals("p")) {
//						shardFound = (Shard) contact.getFixtureB().getBody().getUserData();
//						System.out.println("Destroyed2");
//						//shardFound.body.destroyFixture(shardFound.fixture);
//					}
////					return true;
//				}
//
////				return false;
//			}
//		}
////		return false;
//	}

	/**
	 * Handles all the debug inputs.
	 * 
	 * @param deltaTime time between updates
	 */
	private void handleDebugInput(float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop)
			return;

		// Camera Controls(move)
		if (!cameraHelper.hasTarget(level.orb)) {
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
	 * Detects when the space bar is pressed down, thus
	 * activating the jump boolean.
	 */
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.SPACE) {
			jump = true;
		}
		return false;
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
		if (keycode == Keys.W) {
			jump = false;
		}
		// Toggle camera follow
		else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.orb);
			Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
		}
		// Back to Menu
		else if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			backToMenu();
		}
		return false;
	}
	
	/**
	 * save a reference to the game instance
	 */
	private void backToMenu() {
		// switch to menu screen
		game.setScreen(new MenuScreen(game));
	}

	/**
	 * Overridden dispose method destorys b2world if its extant
	 */
	@Override
	public void dispose() {
		if (world != null)
			world.dispose();
	}
	
	/**
	 * Activates when the player begins contact with a sensor.
	 */
	@Override
	public void beginContact(Contact contact) {
		//System.out.println("CONTACT");
		if(contact.getFixtureA().getBody().getUserData() == level.orb && contact.getFixtureB().getBody().getUserData().getClass() == Shard.class 
				&& contact.getFixtureB().isSensor()) { // player and is a sensor
			if(!doneOnce) {
				touchedObject = (AbstractGameObject) contact.getFixtureB().getBody().getUserData();
				System.out.println("Touched a Shard\n-------" + shardsCollected++);
				touchedObject.collected = true;
				toDestroy = touchedObject;
				doneOnce = true;
				
				//AudioManager.instance.play(Assets.instance.sounds.pickupCoin);
				time += ((Shard)touchedObject).getScore();
				Gdx.app.log(TAG, "Shard collected");
			}
		} else if(contact.getFixtureB().getBody().getUserData() == level.orb && contact.getFixtureA().isSensor()) {
			touchedObject = (AbstractGameObject) contact.getFixtureA().getBody().getUserData();
			System.out.println("B touched A");
		}
	}
	
	/**
	 * Resolves the contact after the contact is initiated.
	 */
	@Override
	public void endContact(Contact contact) {
		if(contact.getFixtureA().getBody().getUserData() == touchedObject) {
			doneOnce = false;
			touchedObject = null;
		} else if(contact.getFixtureB().getBody().getUserData() == touchedObject) {
			touchedObject = null;
		}
	}

	/**
	 * What to resolve before contact?
	 */
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
	}

	/**
	 * What to resolve after contact?
	 */
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
	}
}