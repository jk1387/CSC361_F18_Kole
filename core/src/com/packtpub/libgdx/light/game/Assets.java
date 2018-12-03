package com.packtpub.libgdx.light.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.packtpub.libgdx.light.util.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Loads up the assets and images.
 * 
 * @author Jacob Kole
 */
public class Assets implements Disposable, AssetErrorListener {

	public static final String TAG = Assets.class.getName(); // get tag name
	public static final Assets instance = new Assets();
	private AssetManager assetManager;

	public AssetOrb orb;
	public AssetRock rock;
	public AssetShard shard;
	public AssetEmber ember;
	public AssetLevelDecoration levelDecoration;
	public AssetFonts fonts;
	public AssetSounds sounds;
	public AssetMusic music;

	private static String allScore = "";
	private static String allNames = "";

	/**
	 * singleton: prevent instantiation from other classes
	 */
	private Assets() {
	}

	/**
	 * Manages the assets and throws errors if assets aren't loaded.
	 * 
	 * @param assetManager
	 */
	public void init(AssetManager assetManager) {
		// Constants.prefs.putInteger("highScore", 0);
		this.assetManager = assetManager;

		// set asset manager error handler
		assetManager.setErrorListener(this);

		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);

		// load sounds
		assetManager.load("sounds/jump.wav", Sound.class);
		assetManager.load("sounds/pickup_shard.wav", Sound.class);
		assetManager.load("sounds/pickup_ember.wav", Sound.class);
		assetManager.load("sounds/live_lost.wav", Sound.class);
		assetManager.load("sounds/win.wav", Sound.class);

		// load music
		//assetManager.load("music/keith303_-_brand_new_highscore.mp3", Music.class);
		assetManager.load("music/kf2_krampus.mp3", Music.class);

		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);

		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);

		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}

		// create game resource objects
		fonts = new AssetFonts();
		orb = new AssetOrb(atlas);
		rock = new AssetRock(atlas);
		shard = new AssetShard(atlas);
		ember = new AssetEmber(atlas);
		levelDecoration = new AssetLevelDecoration(atlas);
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
	}

	/**
	 * This inner class contains a member variable called "orb" and will display it.
	 */
	public class AssetOrb {
		public final AtlasRegion orb;
		public final Animation<TextureRegion> animNormal;

		public AssetOrb(TextureAtlas atlas) {
			orb = atlas.findRegion("orb");

			Array<AtlasRegion> regions = null;
			AtlasRegion region = null;

			// Animation: Bunny Normal
			regions = atlas.findRegions("orb");
			animNormal = new Animation<TextureRegion>(1.0f / 10.0f, regions, Animation.PlayMode.LOOP);
		}
	}

	/**
	 * Builds the rock platforms based on its edges around each central piece.
	 */
	public class AssetRock {
		public final AtlasRegion edge;
		public final AtlasRegion middle;
		public final AtlasRegion darkRock;

		public AssetRock(TextureAtlas atlas) {
			edge = atlas.findRegion("rock_edge");
			middle = atlas.findRegion("rock_middle");
			darkRock = atlas.findRegion("dark_rock");
		}
	}

	/**
	 * Builds the assets for the shards.
	 */
	public class AssetShard {
		public final AtlasRegion shard;

		public AssetShard(TextureAtlas atlas) {
			shard = atlas.findRegion("item_shard");
		}
	}

	/**
	 * Builds the assets for embers.
	 */
	public class AssetEmber {
		public final AtlasRegion ember;

		public AssetEmber(TextureAtlas atlas) {
			ember = atlas.findRegion("item_ember");
		}
	}

	/**
	 * Builds the assets for pillars.
	 */
	public class AssetLevelDecoration {
		public final AtlasRegion pillars;

		public AssetLevelDecoration(TextureAtlas atlas) {
			pillars = atlas.findRegion("pillars");
		}
	}

	/**
	 * Builds the fonts.
	 */
	public class AssetFonts {
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;

		public AssetFonts() {
			// create three fonts using Libgdx's 15px bitmap font
			defaultSmall = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);

			// set font sizes
			defaultSmall.getData().setScale(0.75f);
			defaultNormal.getData().setScale(1.0f);
			defaultBig.getData().setScale(2.0f);

			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	/**
	 * Assets for sounds.
	 * 
	 * @author Jacob Kole
	 */
	public class AssetSounds {
		public final Sound jump;
		public final Sound pickupShard;
		public final Sound pickupEmber;
		public final Sound liveLost;
		public final Sound win;

		public AssetSounds(AssetManager am) {
			jump = am.get("sounds/jump.wav", Sound.class);
			pickupShard = am.get("sounds/pickup_shard.wav", Sound.class);
			pickupEmber = am.get("sounds/pickup_ember.wav", Sound.class);
			liveLost = am.get("sounds/live_lost.wav", Sound.class);
			win = am.get("sounds/win.wav", Sound.class);
		}
	}

	/**
	 * Assets for music.
	 * 
	 * @author Jacob Kole
	 */
	public class AssetMusic {
		public final Music song01;

		public AssetMusic(AssetManager am) {
			//song01 = am.get("music/keith303_-_brand_new_highscore.mp3", Music.class);
			song01 = am.get("music/kf2_krampus.mp3", Music.class);
		}
	}

	/**
	 * Adds high scores to the list.
	 * 
	 * @param val the value of the new score
	 */
	public static void setHighScore(int val, String name) {
		allScore = allScore + " " + val;
		allNames = allNames + "}" + name;

		Constants.prefs.putString("highScore", allScore);
		Constants.prefs.putString("allNames", allNames);
		// Constants.prefs.putInteger("highScore", val);
		Constants.prefs.flush();
	}

	/**
	 * Gets the entire high score list in the form of an array.
	 * 
	 * @return scores the high score list
	 */
	public static int[] getHighScore() {
		allScore = Constants.prefs.getString("highScore");

		String[] strings = allScore.split(" ");
		int[] scores = new int[strings.length];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = Integer.parseInt(strings[i]);
		}

		return scores;

		// return Constants.prefs.getInteger("highScore");
	}

	/**
	 * Gets the entire list of names for the high score list.
	 * 
	 * @return the string array of names
	 */
	public static String[] getScoreNames() {
		allNames = Constants.prefs.getString("allNames");
		return allNames.split("}");
	}

	/**
	 * Resets the high score list.
	 */
	public static void resetHighScore() {
		allScore = "";
		allNames = "";
		Constants.prefs.putInteger("highScore", 0);
		Constants.prefs.putInteger("allNames", 0);
//		Constants.prefs.putString("highScore", allScore);
		Constants.prefs.flush();
	}

	/**
	 * Disposes unused assets from the asset manager.
	 */
	@Override
	public void dispose() {
		assetManager.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}

	/**
	 * Error call for a file missing its assets.
	 * 
	 * @param filename  name of the file
	 * @param type      type of the file class
	 * @param throwable exception
	 */
	public void error(String filename, Class type, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + filename + "'", (Exception) throwable);

	}

	/**
	 * Error call for asset file missing.
	 */
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", (Exception) throwable);

	}
}