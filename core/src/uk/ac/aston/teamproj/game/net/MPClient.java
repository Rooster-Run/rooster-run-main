package uk.ac.aston.teamproj.game.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.net.packet.CreateGameSession;
import uk.ac.aston.teamproj.game.net.packet.JoinGameSession;
import uk.ac.aston.teamproj.game.net.packet.Login;
import uk.ac.aston.teamproj.game.screens.LoadingScreen;

public class MPClient {
	
	private String ip = "localhost";

	public static Client client;
	public static int clientID;
	public int sessionID;
	public int hostID;
	
	public MainGame game;
	private String name;
	private String mapPath;

	
	public MPClient(String ip, String name, MainGame game) {
		this.name = name;
		this.game = game;
		
		client = new Client();
		client.start();
		
		Network.register(client);
		
		try {
			client.connect(60000, ip, Network.TCP_PORT, Network.UDP_PORT);
			requestLogin();
//			game.setScreen(new LoadingScreen(game, mapPath));
		} catch (Exception e) {
			System.err.println("Error. Cannot reach the server.");
		}
		
		client.addListener(new ThreadedListener(new Listener() {
			
			public void connected(Connection connection) {
				// get text here
			}
			
			public void received(Connection connection, Object object) {
				
				if(object instanceof Login) {
					Login packet = (Login) object;
					clientID = packet.id;
					System.out.println("I have successfully connected to the server and my clientID is: " + clientID);
				}
				
				if(object instanceof CreateGameSession) {
					CreateGameSession packet = (CreateGameSession) object;
					System.out.println("The lobby has been created. You can invite players with the following code: " + packet.token);
				}
				
				if(object instanceof JoinGameSession) {
					JoinGameSession packet = (JoinGameSession) object;
					// start the game
					hostID = packet.host;
					System.out.println("my connection id is: "  + connection.getID() + "\nand the host is: " + packet.host);
				}

			}

		}));
		
	}

	public void requestLogin() {
		Login login = new Login();
		login.name = name;
		client.sendTCP(login);
	}

}
