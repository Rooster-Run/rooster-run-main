package uk.ac.aston.teamproj.game.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.screens.PlayScreen;


public class Rooster extends Sprite {
	
	private final static float MIN_SPEED_SLOW = 0.5f;
	private final static float MIN_SPEED_NORMAL = 2;	
	private final static float MIN_SPEED_FAST = 4;
	
	private final static float LINEAR_IMPULSE_SLOW = 0.25f;
	private final static float LINEAR_IMPULSE_NORMAL = 0.1f;
	private final static float LINEAR_IMPULSE_FAST = 0.2f;
	
	private final static int DEFAULT_POWERUP_DURATION = 5;
	
	//enum variable storing different states
	public enum State {
		FALLING,
		JUMPING,
		STANDING,
		RUNNING,
		DEAD, 
		RUNNING_FAST,
		RUNNING_SLOW,
		WON
	}
	public State currentState;
	public State previousState;
	private float stateTimer; //keeps track of the amount of time we stay in a state.
							 //Decides what frame gets pulled from an animation  
	
	public World world; // The world chicken is going to live in.
	public Body b2body; // box2d body
	
	private TextureRegion roosterStand; //region containing the "idle" rooster
	private TextureRegion roosterDead;
	private Animation roosterRun;
	private Animation roosterJump;
	private boolean runningRight;
	
	private boolean isDead = false;
	private boolean isRunningFast = false;
	private boolean isRunningSlow = false;
	private boolean hasWon = false;
	
	private int lives = 3;
	private int coins = 0;
		
	@SuppressWarnings("unchecked")
	public Rooster(World world, PlayScreen screen) {
		super(screen.getAtlas().findRegion("new_chicken")); //pass the required texture region to the superclass
		this.world = world;
		defineRooster();
		
		//set the rooster to be associated with this texture region
		roosterStand = new TextureRegion(getTexture(), 96, 0, 96, 96);
		setBounds(0, 0, 96/MainGame.PPM, 96/MainGame.PPM);
		setRegion(roosterStand);
		
		//set texture associated with dead state
		roosterDead = roosterStand;
		
		//initialize state
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		runningRight = true;
		
		//initialize run animation
		Array<TextureRegion> frames = new Array<>(); //store textures for this animation in an array
		for (int i = 0; i < 3; i++) {
			TextureRegion tr = new TextureRegion(getTexture(), i*96, 0, 96, 96);
			frames.add(tr);
		}
		roosterRun = new Animation(0.1f, frames); //0.1f = duration of each image frame
		frames.clear();
		
		//initialize jump animation
		{
			frames.add(roosterStand);
			//frames.add(new TextureRegion(getTexture(), 48*7, 6*48, 48, 48));
			//frames.add(new TextureRegion(getTexture(), 48*7, 2*48, 48, 48));
		}
		roosterJump = new Animation(0.1f, frames);
		frames.clear();
	}
	
	public void update (float dt) { //dt = delta time
		// The box2D body coordinate system starts at the center of the fixture. 
		// Need to move the texture region to the bottom left-hand corner.
		setPosition(b2body.getPosition().x - getWidth()/2, //move by half the width of the x axis
				b2body.getPosition().y - getHeight()/2); //move by half the height of the y axis
		
		setRegion(getFrame(dt));
		
		if (!isDead) {
			//check if rooster has fallen
			if (b2body.getPosition().y < -10/MainGame.PPM) {
				isDead = true;
			}
			
			//check if rooster has already been in RUNNING_FAST state for 5 second
			if (currentState == State.RUNNING_FAST && stateTimer >= DEFAULT_POWERUP_DURATION) {
				runFaster(false);
			} else if (currentState == State.RUNNING_SLOW && stateTimer >= DEFAULT_POWERUP_DURATION) {
				runSlower(false);
			}
		}
	}
	
	//returns the appropriate frame that we need to display as the sprite texture region
	public TextureRegion getFrame (float dt) {
		//to return the appropriate frame, check the current state
		currentState = getState();
		
		TextureRegion region;
		switch(currentState) {
			case JUMPING:
				region = (TextureRegion) roosterJump.getKeyFrame(stateTimer); 
				break;
			case RUNNING: 
			case RUNNING_FAST: 
			case RUNNING_SLOW:
				region = (TextureRegion) roosterRun.getKeyFrame(stateTimer, true);
				break;
			case DEAD:
				region = roosterDead;
				break;
			case FALLING:
			case STANDING:					
			default:
				region = roosterStand;
				break;
		}
		
		//if rooster is running left and the region is not facing the left, then FLIP it over.
		if ((b2body.getLinearVelocity().x<0 || !runningRight) && !region.isFlipX()) {
			region.flip(true, false);
			runningRight = false;
		} else if ((b2body.getLinearVelocity().x>0 || runningRight) && region.isFlipX()) {
			region.flip(true, false);
			runningRight = true;
		}
		
		// if current state does not equal the previous state, than we must have transitioned to a new
		// state, so we need to reset the state timer
		stateTimer = (currentState == previousState)? stateTimer + dt : 0;
		previousState = currentState;
		return region;
	}
	
	public State getState() {
		if (isDead) 
			return State.DEAD;
		else if (hasWon) 
			return State.WON;
		else if (isRunningFast)
			return State.RUNNING_FAST;
		else if (isRunningSlow)
			return State.RUNNING_SLOW;
		else if (b2body.getLinearVelocity().y > 0 //rooster is going up
			|| (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) //rooster was jumping and is now in descent phase 
				return State.JUMPING;
		else if (b2body.getLinearVelocity().y < 0) //rooster is falling
			return State.FALLING;
		else if (b2body.getLinearVelocity().x != 0)
			return State.RUNNING;
		else
			return State.STANDING;
	}
	
	public void setState(State state) {
		currentState = state;
	}
	
	private void defineRooster() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(600 / MainGame.PPM, 300 / MainGame.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
				
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(36 / MainGame.PPM);
		
		//set category bit (what this fixture is) and mask bit (what this fixture can collide with)

		fdef.filter.categoryBits = MainGame.ROOSTER_BIT;
		//
		fdef.filter.maskBits = MainGame.DEFAULT_BIT |
				MainGame.BRICK_BIT | MainGame.BOMB_BIT |
				MainGame.LIGHTNING_BIT | MainGame.MUD_BIT |
				MainGame.BOUNDARY_BIT | MainGame.COIN_BIT | 
				MainGame.PLANE_BIT | MainGame.GROUND_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef);
		
		// sensor beneath rooster
		EdgeShape legsSensor = new EdgeShape(); //edgeshape is basically a line between 2 points
		legsSensor.set(new Vector2( 12 / MainGame.PPM, -36 / MainGame.PPM),
				 	   new Vector2(-12 / MainGame.PPM, -36 / MainGame.PPM));
		fdef.shape = legsSensor;
		fdef.isSensor = true; //it's not a colliding object, but just a sensor
		
		b2body.createFixture(fdef).setUserData("legs"); //uniquely identifies this fixture as "legs"						
	}
	
	public void bombHit() {
		
		if (lives > 1) {
			lives--;
			
		} else {
		isDead = true;

		//redefine what Rooster can collide with (i.e. nothing, he's dead)
		//To do so, for every fixture attached to rooster, reset the masks bits
		//mask bits are what fixtures a fixture can collide with
		Filter filter = new Filter();
		filter.maskBits = MainGame.NOTHING_BIT;
		for (Fixture f: b2body.getFixtureList())
			f.setFilterData(filter);
		
		//make rooster go up
		b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);		
	
		}
	}
	
	public void onFinish() {
		hasWon = true;
	}
	
	public void runFaster(boolean runFaster) {
		isRunningFast = runFaster;
		if (runFaster) 
			isRunningSlow = false;
	}
	
	public void runSlower(boolean runSlower) {
		isRunningSlow  = runSlower;		
		if (runSlower) 
			isRunningFast = false;
	}
	
	public float getPositionX() {
		return b2body.getPosition().x;
	}
	
	public float getPositionY() {
		return b2body.getPosition().y;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public float getStateTimer() {
		return stateTimer;
	}
	
	public float getMinSpeed() {
		switch (currentState) {
			case RUNNING_SLOW:
				return MIN_SPEED_SLOW;
		
			case RUNNING_FAST:
				return MIN_SPEED_FAST;
				
			default: //all remaining states (not just RUNNING)
				return MIN_SPEED_NORMAL;
		}
	}
	
	public float getLinearImpulse() {
		switch (currentState) {
		case RUNNING_SLOW:
			return LINEAR_IMPULSE_SLOW;
	
		case RUNNING_FAST:
			return LINEAR_IMPULSE_FAST;
			
		default: //all remaining states (not just RUNNING)
			return LINEAR_IMPULSE_NORMAL;
		}
	}
	
	public int getLives() {
		return lives;
	}
	
	public int getCoins() {
		return coins;
	}
	
	public void updateCoins(int value) {
		coins += value;
	}

}
