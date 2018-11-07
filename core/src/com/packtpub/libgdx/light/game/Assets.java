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
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Loads up the assets and images.
 * @author Jacob Kole
 */
public class Assets implements Disposable, AssetErrorListener {

	public static final String TAG = Assets.class.getName(); //get tag name
	public static final Assets instance = new Assets();
	private AssetManager assetManager;
	
	public AssetOrb orb;
	public AssetRock rock;
	public AssetShard shard;
	public AssetEmber ember;
	public AssetLevelDecoration levelDecoration;
	public AssetFonts fonts;


	/**
	 * singleton: prevent instantiation from other classes
	 */
	private Assets() {}
	
	/**
	 * Manages the assets and throws errors if assets aren't loaded.
	 * @param assetManager
	 */
	public void init(AssetManager assetManager) 
	{
		this.assetManager = assetManager;

		//set asset manager error handler
		assetManager.setErrorListener(this);

		//load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		
		//start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size );
		
		for(String a : assetManager.getAssetNames()) 
			Gdx.app.debug(TAG, "asset: "+a);
		
		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
		
		// enable texture filtering for pixel smoothing
		for(Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		// create game resource objects
		fonts = new AssetFonts();
		orb = new AssetOrb(atlas);
		rock = new AssetRock(atlas);
		shard = new AssetShard(atlas);
		ember = new AssetEmber(atlas);
		levelDecoration = new AssetLevelDecoration(atlas);
	}

	/**
	 *  This inner class contains a member variable called "orb" and will display it.
	 */
	public class AssetOrb {
		public final AtlasRegion orb;

		public AssetOrb (TextureAtlas atlas) {
			orb = atlas.findRegion("orb");
		}
	}

	/**
	 * Builds the rock platforms based on its edges around each central piece.
	 */
	public class AssetRock {
		public final AtlasRegion edge;
		public final AtlasRegion middle;
		public final AtlasRegion darkRock;

		public AssetRock (TextureAtlas atlas) {
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
		
		public AssetShard (TextureAtlas atlas) {
			shard = atlas.findRegion("item_shard");
		}
	}

	/**
	 * Builds the assets for embers.
	 */
	public class AssetEmber {
		public final AtlasRegion ember;

		public AssetEmber (TextureAtlas atlas) {
			ember = atlas.findRegion("item_ember");
		}
	}
	
	/**
	 * Builds the assets for pillars.
	 */
	public class AssetLevelDecoration{
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
	 * @param filename name of the file
	 * @param type type of the file class
	 * @param throwable exception
	 */
	public void error(String filename, Class type, Throwable throwable) {
		Gdx.app.error(TAG,  "Couldn't load asset '"+ filename + "'",(Exception)throwable);

	}
	
	/**
	 * Error call for asset file missing.
	 */
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '"+ asset.fileName + "'", (Exception)throwable);

	}
}