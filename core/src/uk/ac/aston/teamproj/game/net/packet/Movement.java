package uk.ac.aston.teamproj.game.net.packet;

public class Movement {
	public int clientID;
	
	/*
	 * 0 is left
	 * 1 is up
	 * 2 is right
	 */
	public int direction; 
	public float impulse;
}
