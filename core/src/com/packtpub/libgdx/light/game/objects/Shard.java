package com.packtpub.libgdx.light.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packtpub.libgdx.light.game.Assets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Shards give additional time to your clock, ultimately
 * becoming your score.
 * @author Jacob Kole
 */
public class Shard extends AbstractGameObject {
	private TextureRegion regShard;
	//public boolean collected;
	
	//public Fixture fixture;
	
	/**
	 * Constructor to initialize the shard.
	 */
    public Shard(){
    	init();
    }
    
    /**
     * Initialization method for gold coin, set dimensions and select assets.
     */
    private void init(){
    	dimension.set(0.5f, 0.5f);
    	//setAnimation(Assets.instance.goldCoin.animGoldCoin);
    	//stateTime = MathUtils.random(0.0f,1.0f);
    	origin.set(dimension.x / 2, dimension.y / 2);
		bounds.set(0, 0, dimension.x, dimension.y);
    	
    	regShard = Assets.instance.shard.shard;
    	 // Set bounding box for collision detection
    	//bounds.set(0,0,dimension.x, dimension.y);
    	collected = false;
    	//body.setUserData(this);
    }
    
    /**
     * Render method for the gold coin object.
     */
    public void render (SpriteBatch batch){
    	if(collected) return;
    	
    	TextureRegion reg = null; //init reg var
    	reg = regShard;
    	//reg = (TextureRegion) animation.getKeyFrame(stateTime,true); //set reg var
    	batch.draw(reg.getTexture(), position.x, position.y,
    			origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
    			rotation, reg.getRegionX(), reg.getRegionY(),
    			reg.getRegionWidth(), reg.getRegionHeight(), false, false);
    }
    
    /**
     * Get score method, returns score.
     * @return the score from collecting a shard.
     */
    public int getScore(){
    	return 15;
    }
}
