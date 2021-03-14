package uk.ac.aston.teamproj.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.net.MPClient;

/**
 * created by Parmo on 5.11.20 
 * edited by Arthur on 15.12.20
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class Bomb extends InteractiveTileObject {

	private static TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("explosion_texture/explosion.atlas"));
	private static Animation bombExplosion = setupAnimation();

	public Bomb(World world, TiledMap map, Rectangle bounds) {
		super(world, map, bounds);

		fixture.setUserData(this);
		setCategoryFilter(MainGame.BOMB_BIT);
	}

	//define bomb explosion animation
	private static Animation setupAnimation() {
		Array<TextureRegion> frames = new Array<>(); //store textures for this animation in an array
		for (int i = 0; i < 9; i++) {
			TextureRegion tr = new TextureRegion (atlas.findRegion("Webp.net-resizeimage").getTexture(), i*96, 0, 96, 96);
			frames.add(tr);
		}
		Animation a = new Animation(0.1f, frames); //0.1f = duration of each image frame

		return a;
	}

	public Animation getAnimation() {
		return bombExplosion;
	}


	@Override
	public void onHit() {
		//for now, just log the event to the console
	   	Sound sound = Gdx.audio.newSound(Gdx.files.internal("bomb.wav"));
        sound.play(1F);
		Gdx.app.log(String.valueOf(MPClient.clientID), "Bomb Collision");
	
		//set category to destroyed bit
		setCategoryFilter(MainGame.DESTROYED_BIT);
		getCell().setTile(null);
	}


	@Override
	public TiledMapTileLayer.Cell getCell() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
		return layer.getCell((int) (body.getPosition().x * MainGame.PPM/96),
				(int) (body.getPosition().y * MainGame.PPM/96));
	}
}
