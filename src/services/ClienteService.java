package services;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import core.ChatMessage;

public class ClienteService {
	private Socket conexao;
	private ObjectOutputStream output;
	
	public Socket connect() {
		try {
			this.conexao = new Socket("127.0.0.1", 5555);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.conexao;
	}
	
	public void send(ChatMessage message) {
		try {
			output.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

