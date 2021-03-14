package uk.ac.aston.teamproj.game.net;

import java.util.ArrayList;

public class GameSession {
	
	private String token;
	private int hostID;
	private boolean active;
	private ArrayList<Integer> players;
	private ArrayList<String> names;
	
	public GameSession(String token) {
		this.token = token;
		players = new ArrayList<>();
		names = new ArrayList<>();
	}
	
	public void addPlayer(int id, String name) {
		players.add(id);
		names.add(name);
	}
	
	public void setHost(int id) {
		this.hostID = id;
	}
	
	public ArrayList<Integer> getPlayers() {
		return players;
	}
	
	public ArrayList<String> getPlayerNames() {
		return names;
	}
	
	public String getToken() {
		return token;
	}
	
	public int getHost() {
		return hostID;
	}
}
