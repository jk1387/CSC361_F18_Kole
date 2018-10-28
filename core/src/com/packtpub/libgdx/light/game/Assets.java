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


	//singleton: prevent instantiation from other classes
	private Assets() {}
	
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
		orb = new AssetOrb(atlas);
		rock = new AssetRock(atlas);
		shard = new AssetShard(atlas);
		ember = new AssetEmber(atlas);
		levelDecoration = new AssetLevelDecoration(atlas);
	}

	// This inner class contains a member variable called "head"
	// and will display the orb head
	public class AssetOrb {
		public final AtlasRegion orb;

		public AssetOrb (TextureAtlas atlas) {
			orb = atlas.findRegion("orb");
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
	public class AssetEmber {
		public final AtlasRegion ember;

		public AssetEmber (TextureAtlas atlas) {
			ember = atlas.findRegion("item_ember");
		}
	}
	public class AssetLevelDecoration{
//		public final AtlasRegion cloud01;
//		public final AtlasRegion cloud02;
//		public final AtlasRegion cloud03;
		public final AtlasRegion pillars;

		public AssetLevelDecoration(TextureAtlas atlas) {
//			cloud01 = atlas.findRegion("cloud01");
//			cloud02 = atlas.findRegion("cloud02");
//			cloud03 = atlas.findRegion("cloud03");
			pillars = atlas.findRegion("pillars");
		}
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}
	public void error(String filename, Class type, Throwable throwable) {
		Gdx.app.error(TAG,  "Couldn't load asset '"+ filename + "'",(Exception)throwable);

	}
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '"+ asset.fileName + "'", (Exception)throwable);

	}
}