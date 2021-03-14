package uk.ac.aston.teamproj.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.screens.PlayScreen;

public class PlayerProgressBar implements Disposable {
	
	private static final float MAP_SIZE = 500;
	private static final float BAR_WIDTH = 400;
	private static final float BAR_HEIGHT = 32;
	private static final float PLAYER_RADIUS = 30;
	
	private Stage stage;
	private Viewport viewport;
	
	// progress bar
	private Image background;
	private Image player;	
	private float playerProgressPos;	
		
	// coins
	private Image coin;
	private Label coinsLabel;
	private int coinsCollected = 0;
	
	// lives
	private Image[] hearts = new Image[3];
	
	public PlayerProgressBar(SpriteBatch sb) {
		viewport = new FitViewport(MainGame.V_WIDTH / 3, MainGame.V_HEIGHT / 3, new OrthographicCamera());
		stage = new Stage(viewport, sb);
		
		// progress bar
		background = new Image(new Texture("progress_bar/grey_bar.png"));
		player = new Image(new Texture("progress_bar/rooster_new.png"));		
		background.setColor(1f, 1f, 1f, 0.5f);
		background.setBounds(10, 370, BAR_WIDTH, BAR_HEIGHT);
		player.setColor(1f, 1f, 1f, 0.6f);		
		
		// coins
		coin = new Image(new Texture("progress_bar/coin.png"));
		coin.setColor(1f, 1f, 1f, 0.6f);
		coin.setBounds(500, 370, 32, 32);
		coinsLabel = new Label(String.format("\t%02d", coinsCollected), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		coinsLabel.setColor(1f,  1f,  1f,  0.6f);
		coinsLabel.setX(540);
		coinsLabel.setY(376);
		coinsLabel.setFontScale(1.8f);
		
		// lives
		Texture heartTexture = new Texture("progress_bar/heart.png");
		hearts[0] = new Image(heartTexture);
		hearts[1] = new Image(heartTexture);
		hearts[2] = new Image(heartTexture);
		
		for (int i = 0, x = 650; i < hearts.length; i++, x += 40) {
			hearts[i].setColor(1f, 1f, 1f, 0.6f);
			hearts[i].setBounds(x, 374, 32, 25);
		}
	}

	public void draw() {
		player.setBounds(12 + playerProgressPos, 371f, PLAYER_RADIUS, PLAYER_RADIUS);
		
		Group group = new Group();
		group.addActor(background);
		group.addActor(player);
		for (Image life : hearts)
			group.addActor(life);
		group.addActor(coin);
		group.addActor(coinsLabel);
		
		stage.addActor(group);
		stage.draw();
		stage.act();
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
	
	public void updateProgress(float position) {
		float actualPosition = (position * MainGame.PPM) / 100;
		float progress = (actualPosition * 100) / MAP_SIZE;
		
		this.playerProgressPos = (progress * (BAR_WIDTH - PLAYER_RADIUS/2)) / 100;
	}
	
	public void updateCoins(int value) {
		PlayScreen.player.updateCoins(value);
		coinsCollected = PlayScreen.player.getCoins();
		coinsLabel.setText(String.format("%02d", coinsCollected));
	}
	
	public void updateLives() {
		int lives;
		if (PlayScreen.player.isDead())
			lives = 0;
		else
			lives = PlayScreen.player.getLives();
		if (lives < hearts.length)
			hearts[lives].setVisible(false);
	}
}