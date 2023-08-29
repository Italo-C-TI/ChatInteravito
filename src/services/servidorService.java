package services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
				this.output = new ObjectOutputStream(conexao.getOutputStream());
				this.input = new ObjectInputStream(conexao.getInputStream());
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
					
					if(action.equals(Action.CONNECT)){
						connect(message, output); 
					}else if(action.equals(Action.DISCONNECT)){
						disconnect(message,output);
						
					}else if(action.equals(Action.SEND_ALL)){
						sendAll(message, output);						
					}
				}
			} catch (Exception e) {
				disconnect(message,output);
		        System.out.println("Exception: " + e);
			}
			
		}
	}
	
	private void connect(ChatMessage message, ObjectOutputStream output) {
	    message.setAction(Action.CONNECT);
		System.out.println(message.getName() + " se conectou");
	}

	private void disconnect(ChatMessage message, ObjectOutputStream output) {
	    message.setAction(Action.DISCONNECT);
		System.out.println(message.getName() +" saiu no chat!");


	}

	private void sendAll(ChatMessage message, ObjectOutputStream output) {
	    message.setAction(Action.SEND_ALL);
		System.out.println(message.getName() +": " + message.getText());
	}

}