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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.net.MPClient;
import uk.ac.aston.teamproj.game.net.packet.CreateGameSession;

public class CreateScreen implements Screen {
		
	private Label lbl_ip, lbl_name;
	private LabelStyle lbl_style;
	private TextField txt_ip, txt_name;
	public static String ip = "Localhost", name = "Player 1"; // change with user input
	private Skin txt_skin;
	private TextButtonStyle btn_style;
	private MainGame game;
	private Viewport viewport;
	private Stage stage;
	private TextureAtlas buttonsAtlas; //the sprite-sheet containing all buttons
	private Skin skin; //skin for buttons
	private ImageButton[] optionButtons;
	
	//level picking tools
	private ImageButton leftBtn;
	private ImageButton rightBtn;
	
	private final static int NUM_MAPS = 2;
	private String[] mapsPaths = new String[NUM_MAPS];
	private Texture[] mapsImages = new Texture[NUM_MAPS];
	private Image mapPreview = new Image();
	private int mapIdx = 0;

	public CreateScreen(MainGame game) {
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
		optionButtons = new ImageButton[3];
		
		initializeButtons();		
		populateTable();
		
		mapsImages[0] = new Texture("easymap.png");
		mapsImages[1] = new Texture("hardmap.png");
		mapsPaths[0] = "map_beginner_fix";
		mapsPaths[1] = "map_hard";
	}
	
	private void initializeButtons() {		
		ImageButtonStyle style;
		
		//Continue Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("start_inactive");  //set default image
		style.over = skin.getDrawable("start_active");  //set image for mouse over
		
		ImageButton startBtn = new ImageButton(style);
		startBtn.addListener(new InputListener() {
	            @Override	            
	            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

	            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	                sound.play(1F);  
	            	
	    			txt_ip.setTextFieldListener(new TextField.TextFieldListener() {
	    	
	    				@Override
	    				public void keyTyped(TextField textField, char c) {
	    					Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	    	                sound.play(1F);				
	    					ip = textField.getText();
	    				}
	    			});
	    			txt_name.setTextFieldListener(new TextField.TextFieldListener() {
	    				
	    				@Override
	    				public void keyTyped(TextField textField, char c) {
	    					Sound sound2 = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	    	                sound2.play(1F);
	    					name = textField.getText();
	    				}
	    			});
	    			
	    			// pass in map data
	    			new MPClient(txt_ip.getText(), txt_name.getText(), game);
	    			dispose();
	    			CreateGameSession packet = new CreateGameSession();
	    			packet.name = getName();
	    			MPClient.client.sendTCP(packet);
	            	return true;
		
		}});
		
		
		
		//Go Back Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("back_inactive");  //set default image
		style.over = skin.getDrawable("back_active");  //set image for mouse over
		
		ImageButton backBtn = new ImageButton(style);
		backBtn.addListener(new InputListener() {
	            @Override
	            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

	            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	                sound.play(1F);
	            	System.out.println("Back");
	            	CreateScreen.this.dispose();
	            	game.setScreen(new MultiplayerMenuScreen(game));
	            	return true;
	            }	       
		});
		
		optionButtons[0] = startBtn;
		optionButtons[1] = backBtn;
		
		// Left Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("left_inactive");  //set default image
		style.over = skin.getDrawable("left_active");  //set image for mouse over
		
		leftBtn = new ImageButton(style);
		leftBtn.addListener(new InputListener() {
	            @Override
	            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	                sound.play(1F);
	            	if (mapIdx > 0) {
	            		mapIdx --;
	            	} else {
	            		mapIdx = NUM_MAPS - 1;
	            	}
	            	return true;
	            }					       
		});
		
		// Right Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("right_inactive");  //set default image
		style.over = skin.getDrawable("right_active");  //set image for mouse over
		
		rightBtn = new ImageButton(style);
		rightBtn.addListener(new InputListener() {
	            @Override
	            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	                sound.play(1F);
	            	mapIdx = (mapIdx + 1) % NUM_MAPS;
	            	return true;
	            }					       
		});
	}
	
	private void populateTable() {
		Table table = new Table();		
		table.top();
		table.setFillParent(true);
		
		//draw the background
		Texture background = new Texture("buttons/multiplayer_menu_bg.jpg");
		table.background(new TextureRegionDrawable(new TextureRegion(background)));
		
		//initialise Label
		lbl_ip = new Label("IP Address:" , lbl_style);
		lbl_name = new Label("Name: " , lbl_style);
		
		//initialise TextField
		txt_ip = new TextField("localhost", txt_skin);
		txt_name = new TextField(name, txt_skin);
		
		
		//add contents to table
		table.add(lbl_ip).right().expandX();
		table.add(txt_ip).width(200).pad(4);
		table.row();
		table.add(lbl_name).right().expandX();
		table.add(txt_name).width(200).pad(4);
		table.row();
		
		
		
		// ************** OPTIONS SUB-TABLE*******************
		Table optionsTable = new Table();
		
		// startBtn
		ImageButton singleBtn = optionButtons[0];
		optionsTable.add(singleBtn).height(22f).width(120).pad(4);
		optionsTable.row();
		
		// backBtn
		ImageButton backBtn = optionButtons[1];
		optionsTable.add(backBtn).height(22f).width(120).pad(4);
		optionsTable.row();		
		
		// ************** LEVELS SUB-TABLE*******************		
		Table levelsTable = new Table();
		
		levelsTable.add(leftBtn).height(22f).width(24.8f).pad(4).padLeft(15f);
		levelsTable.add(mapPreview).height(80).width(120).pad(4);
		levelsTable.add(rightBtn).height(22f).width(24.8f).pad(4);
		
		// Add both tables
		table.add(levelsTable).padTop(15f);
		table.add(optionsTable).padTop(15f);
		table.row();
		
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
		
		mapPreview.setDrawable(new TextureRegionDrawable(new TextureRegion(mapsImages[mapIdx])));
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

	@Override
	public void dispose() {
		buttonsAtlas.dispose();
		skin.dispose();
		txt_skin.dispose();
		stage.dispose();
	}
	
	private String getName() {
		return txt_name.getText();
	}
}
