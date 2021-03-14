package uk.ac.aston.teamproj.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

import uk.ac.aston.teamproj.game.MainGame;

/**
 * 
 * created by Parmo on 5.11.20
 *
 */

public class EndPlane extends InteractiveTileObject {
	
	public EndPlane(World world, TiledMap map, Rectangle bounds) {
		super(world, map, bounds);
		
		fixture.setUserData(this);
		setCategoryFilter(MainGame.PLANE_BIT);
	}

	@Override
	public void onHit() {
		//for now, just log the event to the console
		Gdx.app.log("PLANE", "Collision");
		
		Sound sound = Gdx.audio.newSound(Gdx.files.internal("firstplace.wav"));
        sound.play(1F);
		
		//set category to destroyed bit
		//getCell().setTile(null);
	}
		
	@Override
	public TiledMapTileLayer.Cell getCell() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
		return layer.getCell((int) (body.getPosition().x * MainGame.PPM/96), 
				(int) (body.getPosition().y * MainGame.PPM/96));
	}
}
