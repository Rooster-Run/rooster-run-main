package uk.ac.aston.teamproj.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.net.MPClient;
import uk.ac.aston.teamproj.game.net.packet.JoinGameSession;

public class JoinScreen implements Screen {
		
	private Label lbl_token, lbl_name;
	private LabelStyle lbl_style;
	private TextField txt_token, txt_name;
	public	String name = "Guest"; // change with user input
	public 	String token;
	private Skin txt_skin;
	private TextButtonStyle btn_style;
	private MainGame game;
	private Viewport viewport;
	private Stage stage;
	private TextureAtlas buttonsAtlas; //the sprite-sheet containing all buttons
	private Skin skin; //skin for buttons
	private ImageButton[] buttons;

	public JoinScreen(MainGame game) {
		this.game = game;
		viewport = new FitViewport(MainGame.V_WIDTH/6, MainGame.V_HEIGHT/6, new OrthographicCamera());
		stage = new Stage(viewport, ((MainGame) game).batch);
		
		//
		lbl_style = new Label.LabelStyle();
		lbl_style.font = new BitmapFont();
		
		txt_skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		btn_style = new TextButton.TextButtonStyle();
		btn_style.font = new BitmapFont();
		
		
		buttonsAtlas = new TextureAtlas("buttons/new_buttons.pack");
		skin = new Skin(buttonsAtlas);
		buttons = new ImageButton[3];
		
		initializeButtons();		
		populateTable();		
	}


	private void initializeButtons() {		
		ImageButtonStyle style;
		
		//Continue Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("start_inactive");  //set default image
		style.over = skin.getDrawable("start_active");  //set image for mouse over
		
		ImageButton continueBtn = new ImageButton(style);
		continueBtn.addListener(new InputListener() {
			
				@Override
				
	            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					
					//plays button sounds	         
	            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	                sound.play(1F);
	                
					txt_token.setTextFieldListener(new TextField.TextFieldListener() {
				    	
	    				@Override
	    				public void keyTyped(TextField textField, char c) {

	    					 //plays button pop sound
	    					Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	    	                sound.play(1F);

	    					token = textField.getText();	    					
	    				}
	    			});
	    			txt_name.setTextFieldListener(new TextField.TextFieldListener() {
	    				
	    				@Override
	    				public void keyTyped(TextField textField, char c) {
	    					//plays button pop sound
	    					Sound sound2 = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	    	                sound2.play(1F);

	    					name = textField.getText();
	    				}
	    			});
	    			
	    			new MPClient("localhost", name, game);
	    			JoinGameSession packet = new JoinGameSession();
	    			packet.token = getToken();
	    			packet.name = getName();
	    			MPClient.client.sendTCP(packet);
	    			
	    			dispose();
	    			// create add chicken command in playscreen
	            	return true;
				}
		});
		
		
		
		//Go Back Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("back_inactive");  //set default image
		style.over = skin.getDrawable("back_active");  //set image for mouse over
		
		ImageButton backBtn = new ImageButton(style);
		backBtn.addListener(new InputListener() {
	            @Override
	            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	            	//Sets to playScreen

	            	 //plays button pop sound

	            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	                sound.play(1F);
	            	System.out.println("Back");
	            	JoinScreen.this.dispose();
	            	game.setScreen(new MainMenuScreen(game));
	            	return true;
	            }	       
		});
		
		buttons[0] = continueBtn;
		buttons[1] = backBtn;
	}
	
	private void populateTable() {
		Table table = new Table();		
		table.top();
		table.setFillParent(true);
		
		//draw the background
		Texture background = new Texture("buttons/multiplayer_menu_bg.jpg");
		table.background(new TextureRegionDrawable(new TextureRegion(background)));
		
		//initialise Label
		lbl_token = new Label("Token:" , lbl_style);
		lbl_name = new Label("Name: " , lbl_style);
		
		//initialise TextField
		txt_token = new TextField("", txt_skin);
		txt_name = new TextField(name, txt_skin);
		
		
		//add contents to table
		table.add(lbl_token).expandX();
		table.add(txt_token).width(200).pad(4);
		table.row();
		table.add(lbl_name).expandX();
		table.add(txt_name).width(200).pad(4);
		table.row();
		
		
		
		//draw all buttons
		table.add(buttons[0]).height(22f).width(120).pad(4).padLeft(200).padTop(50);
		table.row();
		for (int i = 1; i < buttons.length; i++) {
			ImageButton button = buttons[i];
			table.add(button).height(22f).width(120).pad(4).padLeft(200);
			table.row();
		} 
		
		stage.addActor(table);		
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
    public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,  0,  0 , 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.draw();
		stage.act(delta);	
	}


	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
	public String getToken() {
		return txt_token.getText();
	}
	
	private String getName() {
		return txt_name.getText();
	}

	@Override
	public void dispose() {
		buttonsAtlas.dispose();
		skin.dispose();
		txt_skin.dispose();
		stage.dispose();
	}
}
