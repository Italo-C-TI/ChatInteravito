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
					
					if(action.equals(Action.CONNECT)){
						boolean isConnect = connect(message, output);
						if(isConnect) {
							mapOnlines.put(message.getName(), output);

						}
						connect(message, output); 
						
						System.out.println(message.getName() + " se conectou");
						
					}else if(action.equals(Action.DISCONNECT)){
						disconnect(message,output);
						
					}else if(action.equals(Action.SEND_ALL)){
						sendAll(message, output);
						
					}else if(action.equals(Action.USER_ONLINE)){
						refreshOnlines(message); 
					}
				}
			} catch (Exception e) {
				disconnect(message,output);
		        System.out.println("Exception: " + e);
			}
			
		}
	}
	
	private boolean connect(ChatMessage message, ObjectOutputStream output) {
	    message.setAction(Action.CONNECT);
	    sendAll(message, output);

	    return true;
	}

	private void disconnect(ChatMessage message, ObjectOutputStream output) {
	    mapOnlines.remove(message.getName());
	    sendAll(message, output);
	    message.setAction(Action.DISCONNECT);
	    sendAll(message, output);
	}

	private void sendAll(ChatMessage message, ObjectOutputStream output) {
	    for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
	        try {
	            kv.getValue().writeObject(message);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	private void sendToOne(ChatMessage message, ObjectOutputStream output) {
	    try {
	        output.writeObject(message);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private void refreshOnlines(ChatMessage message) {
	    String requesterName = message.getName();
	    ObjectOutputStream requesterOutput = mapOnlines.get(requesterName);

	    if (requesterOutput != null) {
	        Set<String> users = mapOnlines.keySet();
	        message.setSetOnlines(users);
	        sendToOne(message, requesterOutput);
	    }
	}

}