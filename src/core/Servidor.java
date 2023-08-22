package core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
	private ServerSocket serverSocket;
	private Socket conexao;
	private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
	
	public Servidor() {
	    try {
	    	serverSocket = new ServerSocket(5555);
	        System.out.println("ServidorSocket rodando na porta 5555");
	        while (true) {
	             conexao = serverSocket.accept();
	        }
	    } catch (IOException e) {
	
	        System.out.println("IOException: " + e);
	    }
	}
}
