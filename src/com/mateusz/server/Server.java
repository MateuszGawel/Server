package com.mateusz.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	private ServerSocket serverSocket;
	private static final int SERVER_PORT = 6066;

	public Server(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Couldn't start server");
		}
	}

	public void run() {
		while (true) {
			try {
				System.out.println("LISTENING...");
				Socket clientSocket = serverSocket.accept();
				System.out.println("GOT CONNECTION");
				
				PlayerThread playerThread = new PlayerThread(clientSocket);
				playerThread.start();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Exception occured on server");
				break;
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Server is down.");
		}
	}

	public static void main(String[] args) {
		Thread t = new Server(SERVER_PORT);
		t.start();
	}
}
