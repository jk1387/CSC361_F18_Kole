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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
	private static final String TAG = WorldController.class.getName();
	public CameraHelper cameraHelper;

	public Level level;
	public int life;
	public int time;

	public float livesVisual;
	public float scoreVisual;

	// BOX2D STUFF
	final static float MAX_VELOCITY = 7f; // move speed outside method to not create it each frame.
	boolean jump = false;
	public static World world;
	Body player;
	Fixture playerPhysicsFixture;
	Fixture playerSensorFixture;
	OrthographicCamera cam;
	Box2DDebugRenderer renderer;
	DarkRock groundedPlatform = null;
	float stillTime = 0;
	long lastGroundTime = 0;

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
		world = new World(new Vector2(0, -9.81f), true);
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
		// player = createPlayer();
		// level.orb.body = createPlayer();
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
			//body.setTransform(rock.position, 0);
			rock.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = rock.bounds.width / 2.0f;
			origin.y = rock.bounds.height / 2.0f;
			polygonShape.setAsBox(rock.bounds.width / 2.0f, rock.bounds.height / 2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
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
		handleDebugInput(deltaTime);
		handleInputGame(deltaTime);
		//playerMovement();
		
		//world.step(Gdx.graphics.getDeltaTime(), 4, 4);
		//level.orb.body.setAwake(true);
		
		// level.orb.body = player;
		cameraHelper.update(deltaTime);
		level.update(deltaTime);
	}
	
	/**
	 * Handles the input keys for the game. These affect
	 * player movements in-game and change the physics affecting
	 * the player.
	 * @param deltaTime game time
	 */
	private void handleInputGame(float deltaTime) {
		Vector2 velocity = new Vector2(0, 0);
		
		// Horizontal Movement
		if (Gdx.input.isKeyPressed(Keys.D)) {
			velocity.x += level.orb.movementSpeed;
		} else if (Gdx.input.isKeyPressed(Keys.A)) {
			velocity.x -= level.orb.movementSpeed;
		}
		
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			//level.orb.body.setLinearVelocity(velocity.x, 0);
			//level.orb.body.setTransform(pos.x, pos.y + 0.01f, 0);
			//level.orb.body.applyLinearImpulse(0, 30, pos.x, pos.y, true);
			//velocity.y += 30;
			velocity.y += 10;
			level.orb.body.applyLinearImpulse(velocity, level.orb.body.getPosition(), true);
		}
		
		// Set velocity of player
		level.orb.body.setLinearVelocity(velocity);
		System.out.println("Vel: " + level.orb.body.getLinearVelocity());
		System.out.println("Position: " + level.orb.body.getPosition());
	}

	private void playerMovement() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		cameraHelper.position.set(player.getPosition().x, player.getPosition().y);
		//cameraHelper.position.set(level.orb.body.getPosition().x, level.orb.body.getPosition().y);

//		Vector2 vel = player.getLinearVelocity();
//		Vector2 pos = player.getPosition();
		Vector2 vel = level.orb.body.getLinearVelocity();
		Vector2 pos = level.orb.body.getPosition();
		//Vector2 vel = level.orb.velocity;
		//Vector2 pos = level.orb.position;
		System.out.println(vel + " and " + pos);
		boolean grounded = isPlayerGrounded(Gdx.graphics.getDeltaTime());
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
//			player.setLinearVelocity(vel.x, vel.y);
			level.orb.body.setLinearVelocity(vel.x, vel.y);
		}

		// calculate stilltime & damp
		if (!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
			stillTime += Gdx.graphics.getDeltaTime();
//			player.setLinearVelocity(vel.x * 0.9f, vel.y);
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
//			player.applyLinearImpulse(-2f, 0, pos.x, pos.y, true);
			level.orb.body.applyLinearImpulse(-2f, 0, pos.x, pos.y, true);
		}

		// apply right impulse, but only if max velocity is not reached yet
		if (Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
//			player.applyLinearImpulse(2f, 0, pos.x, pos.y, true);
			level.orb.body.applyLinearImpulse(2f, 0, pos.x, pos.y, true);
		}

		// jump, but only when grounded
		if (jump) {
			jump = false;
			if (grounded) {
//				player.setLinearVelocity(vel.x, 0);			
//				player.setTransform(pos.x, pos.y + 0.01f, 0);
//				player.applyLinearImpulse(0, 30, pos.x, pos.y, true);
				level.orb.body.setLinearVelocity(vel.x, 0);
				level.orb.body.setTransform(pos.x, pos.y + 0.01f, 0);
				level.orb.body.applyLinearImpulse(0, 30, pos.x, pos.y, true);
			}
		}

		// le step...
//		world.step(Gdx.graphics.getDeltaTime(), 4, 4);
////		player.setAwake(true);
//		level.orb.body.setAwake(true);
	}

	private boolean isPlayerGrounded(float deltaTime) {
		groundedPlatform = null;
		Array<Contact> contactList = world.getContactList();
		for (int i = 0; i < contactList.size; i++) {
			Contact contact = contactList.get(i);
			if (contact.isTouching()
					&& (contact.getFixtureA() == playerSensorFixture || contact.getFixtureB() == playerSensorFixture)) {

//				Vector2 pos = player.getPosition();
				Vector2 pos = level.orb.body.getPosition();
				WorldManifold manifold = contact.getWorldManifold();
				boolean below = true;
				for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
				}

				if (below) {
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

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.W) {
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
			//backToMenu();
		}
		return false;
	}

	/**
	 * Overridden dispose method destorys b2world if its extant
	 */
	@Override
	public void dispose() {
		if (world != null)
			world.dispose();
	}
}