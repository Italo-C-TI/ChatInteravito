package core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
	private ServerSocket server;
	private Socket conexao;
	private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
	
	public Servidor() {
	    try {
	    	server = new ServerSocket(5555);
	        System.out.println("ServidorSocket rodando na porta 5555");
	        while (true) {
	        	conexao = server.accept();
	        }
	    } catch (IOException e) {
	
	        System.out.println("IOException: " + e);
	    }
	}
}
