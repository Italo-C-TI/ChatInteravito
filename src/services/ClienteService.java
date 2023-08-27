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
			this.conexao = new Socket("localhost", 5555);
			this.output = new ObjectOutputStream(conexao.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.conexao;
	}
	
	public void disconnect() {
		try {
			if(!this.conexao.isClosed()) {
				this.conexao.close();
				System.out.println("Desconectado da rede com sucesso");
				return;
			}
			System.out.println("Cliente já encerrou a conexão com a rede");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(ChatMessage message) {
		try {
			//System.out.println(message.getName());
			output.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

