package uk.ac.aston.teamproj.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.physics.box2d.World;

import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.net.MPClient;

public class Lightning extends InteractiveTileObjectCircular {

	public Lightning(World world, TiledMap map, Ellipse bounds) {
		super(world, map, bounds);
		
		fixture.setUserData(this);
		setCategoryFilter(MainGame.LIGHTNING_BIT);
	}
	
	@Override
	public void onHit() {
		Gdx.app.log(String.valueOf(MPClient.clientID), "Lightning Collision");	
	   	Sound sound = Gdx.audio.newSound(Gdx.files.internal("lightening.mp3"));
        sound.play(1F);
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
