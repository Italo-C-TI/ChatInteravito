package services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import core.ChatMessage;
import core.ChatMessage.Action;;


public class servidorService {
	private ServerSocket server;
	private Socket conexao;
	private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
	
	public servidorService() {
	    try {
	    	server = new ServerSocket(5555);
	        System.out.println("ServidorSocket rodando na porta 5555");
	        while (true) {
	        	conexao = server.accept();
	        	new Thread(new ListenerSocket(conexao)).start();
	        }
	    } catch (IOException e) {
	
	        System.out.println("IOException: " + e);
	    }
	}
	
	private class ListenerSocket implements Runnable {
		private ObjectOutputStream output;
		private ObjectInputStream input;
		
		public ListenerSocket(Socket conexao) {
			try {
				System.out.println("entrei");
				this.output = new ObjectOutputStream(conexao.getOutputStream());
				this.input = new ObjectInputStream(conexao.getInputStream());
				System.out.println(input);
				System.out.println(output);
			} catch (Exception e) {
		        System.out.println("Exception: " + e);
			}

			
		}
		
		@Override
		public void run() {
			ChatMessage message = null;
			try {
				
				while ((message = (ChatMessage)input.readObject()) != null) {
					Action action = message.getAction();
					
					if(action.equals(action.CONNECT)){
						System.out.println("conectou");

					}else if(action.equals(action.DISCONNECT)){
						
					}else if(action.equals(action.SEND_ALL)){
						
					}else if(action.equals(action.USER_ONLINE)){
						
					}
				}
			} catch (Exception e) {
		        System.out.println("Exception: " + e);
			}
			
		}
	}
	private boolean connect(ChatMessage message, ObjectOutputStream output) {
		if(mapOnlines.size() == 0) {
			message.setText("YES");
			sendAll(message, output);
			return true;
		}
		message.setText("NO");
		return false;
	}
	private void sendAll(ChatMessage message, ObjectOutputStream output) {
//		for(Map.Entry<String, ObjectOutputStream>kv:map) {
//			
//		}
		
		try {
			output.writeObject(message);
		} catch (Exception e) {
	        System.out.println("Exception: " + e);
		}

	}
}