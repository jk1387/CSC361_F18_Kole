package com.packtpub.libgdx.light.game;
/*
 * Author: Drake Conaway
 */
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.packtpub.libgdx.orb.util.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Assets implements Disposable, AssetErrorListener {

	public static final String TAG = Assets.class.getName(); //get tag name
	public static final Assets instance = new Assets();
	public AssetOrb orb;
	public AssetRock rock;
	public AssetShard Shard;
	public Assetember ember;
	public AssetLevelDecoration levelDecoration;
	public AssetFonts fonts;
	public AssetSounds sounds;
	public AssetMusic music;
	private AssetManager assetManager;

	//singleton: prevent instantiation from other classes
	private Assets() {

	}
	


	public void init(AssetManager assetManager) 
	{
		this.assetManager = assetManager;

		//set asset manager error handler
		assetManager.setErrorListener(this);

		//load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);

		// load sounds
		assetManager.load("sounds/jump.wav", Sound.class);
		assetManager.load("sounds/jump_with_ember.wav", Sound.class);
		assetManager.load("sounds/pickup_shard.wav", Sound.class);
		assetManager.load("sounds/pickup_ember.wav", Sound.class);
		assetManager.load("sounds/live_lost.wav", Sound.class);
		
		// load music
		assetManager.load("music/keith303_-_brand_new_highscore.mp3",
		Music.class);
		
		//start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size );
		
		for(String a : assetManager.getAssetNames()) 
			Gdx.app.debug(TAG, "asset: "+a);
		
		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
		
		//enable texture filtering for pixel smoothing
		for(Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		//create game resource objects
		fonts = new AssetFonts();
		orb = new AssetOrb(atlas);
		rock = new AssetRock(atlas);
		Shard = new AssetShard(atlas);
		ember = new Assetember(atlas);
		levelDecoration = new AssetLevelDecoration(atlas);
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
	}

	// This inner class contains a member variable called "head"
	// and will display the orb head
	public class AssetOrb {
		public final AtlasRegion head;

		public AssetOrb (TextureAtlas atlas) {
			head = atlas.findRegion("orb_head");
		}
	}

	// Builds the rock platforms based on its edges around each central piece
	public class AssetRock {
		public final AtlasRegion edge;
		public final AtlasRegion middle;

		public AssetRock (TextureAtlas atlas) {
			edge = atlas.findRegion("rock_edge");
			middle = atlas.findRegion("rock_middle");
		}
	}

	// Builds the assets for gold shards
	public class AssetShard {
		public final AtlasRegion shard;
		
		public AssetShard (TextureAtlas atlas) {
			shard = atlas.findRegion("item_shard");
		}
	}

	// Builds the assets for embers
	public class Assetember {
		public final AtlasRegion ember;

		public Assetember (TextureAtlas atlas) {
			ember = atlas.findRegion("item_ember");
		}
	}
	public class AssetLevelDecoration{
		public final AtlasRegion cloud01;
		public final AtlasRegion cloud02;
		public final AtlasRegion cloud03;
		public final AtlasRegion mountainLeft;
		public final AtlasRegion mountainRight;
		public final AtlasRegion waterOverlay;
		public final AtlasRegion carrot;
		public final AtlasRegion goal;

		public AssetLevelDecoration(TextureAtlas atlas) {
			cloud01 = atlas.findRegion("cloud01");
			cloud02 = atlas.findRegion("cloud02");
			cloud03 = atlas.findRegion("cloud03");
			mountainLeft = atlas.findRegion("mountain_left");
			mountainRight = atlas.findRegion("mountain_right");
			waterOverlay = atlas.findRegion("water_overlay");
			carrot = atlas.findRegion("carrot");
			goal = atlas.findRegion("goal");
		}
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}
	public void error(String filename, Class type, Throwable throwable) {
		Gdx.app.error(TAG,  "Couldn't load asset '"+ filename + "'",(Exception)throwable);

	}
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '"+ asset.fileName + "'", (Exception)throwable);

	}

	public class AssetFonts {
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;
		
		public AssetFonts () {
			// create three fonts using Libgdx's 15px bitmap font
			defaultSmall = new BitmapFont(
			Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(
			Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(
			Gdx.files.internal("images/arial-15.fnt"), true);
			
			// set font sizes
			defaultSmall.getData().setScale(0.75f);
			defaultNormal.getData().setScale(1.0f);
			defaultBig.getData().setScale(2.0f);
			
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture().setFilter(
			TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(
			TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(
			TextureFilter.Linear, TextureFilter.Linear);
		}
	}
	
	/*
	 * asset sound method
	 */
	public class AssetSounds {
		public final Sound jump;
		public final Sound jumpWithEmber;
		public final Sound pickupshard;
		public final Sound pickupEmber;
		public final Sound liveLost;
		public AssetSounds (AssetManager am) {
		jump = am.get("sounds/jump.wav", Sound.class);
		jumpWithEmber = am.get("sounds/jump_with_ember.wav",
		Sound.class);
		pickupshard = am.get("sounds/pickup_shard.wav", Sound.class);
		pickupEmber = am.get("sounds/pickup_ember.wav",
		Sound.class);
		liveLost = am.get("sounds/live_lost.wav", Sound.class);
		}
	}
	
	/*
	 * asset music method
	 * */
	public class AssetMusic {
		public final Music song01;
		public AssetMusic (AssetManager am) {
		song01 = am.get("music/keith303_-_brand_new_highscore.mp3",
		Music.class);
		}
	}
}