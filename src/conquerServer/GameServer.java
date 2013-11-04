package conquerServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameServer implements Runnable {
	private int port = 5816;
	private ServerSocket server = null;
	private ArrayList<GameServerThread> connections = new ArrayList<GameServerThread>();

	/**
	 * Constructor creates a new Socket
	 */
	public GameServer() {
		try {
			server =  new ServerSocket(port);
			System.out.println("GameServer running on port " + port);
		} catch (IOException e) {
			System.out.println("Port " + port + " is already being used by another process.");
		}	
	}
	
	public synchronized void disconnect(GameServerThread GST) {
		connections.remove(GST);
		System.out.println("Current amount of connections: " + connections.size());
	}

	@Override
	/**
	 * Run function starts a new thread for each incomming connection
	 */
	public void run() {
		while(true){
			System.out.println("Waiting for incomming connection...");
			try {
				Socket client = server.accept();
				GameServerThread GST = new GameServerThread(client, this);
				connections.add(GST);
				Thread thread = new Thread(GST);
				thread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		GameServer GS = new GameServer();
		Thread thread = new Thread(GS);
		thread.start();
	}

}