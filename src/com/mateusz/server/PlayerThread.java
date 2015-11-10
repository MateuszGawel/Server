package com.mateusz.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlayerThread extends Thread {
	private static final Logger LOGGER = Logger.getLogger(PlayerThread.class.getName());

	private static List<PlayerThread> players = new ArrayList<PlayerThread>(4);

	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	private String playerName;
	private ObjectMapper objectMapper = new ObjectMapper();

	public PlayerThread(Socket socket) throws IOException {
		try {
			this.socket = socket;
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			LOGGER.log(Level.INFO, "Couldn't read stream");
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				String inputMessage = in.readUTF();
				System.out.println("GOT: " + inputMessage);
				JsonNode jsonNode = objectMapper.readTree(inputMessage);
				String senderName = jsonNode.get("senderName").asText();
				
				if (!players.contains(this)) {
					playerName = senderName;
					players.add(this);
					System.out.println("DODALEM I MAM PLAYEROW:");
					players.forEach((p) -> System.out.println(p));
				}

				for (PlayerThread p : players) {
					p.getOut().writeUTF(inputMessage);
				}

			} catch (IOException e) {
				LOGGER.log(Level.INFO, "Player disconnected: " + playerName);
				players.remove(this);
				break;
			}
		}

		try {
			socket.close();
		} catch (IOException e) {
			LOGGER.log(Level.INFO, "Couldn't close stream");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((playerName == null) ? 0 : playerName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerThread other = (PlayerThread) obj;
		if (playerName == null) {
			if (other.playerName != null)
				return false;
		} else if (!playerName.equals(other.playerName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Player [name=" + playerName + "]";
	}

	public DataInputStream getIn() {
		return in;
	}

	public DataOutputStream getOut() {
		return out;
	}
}
