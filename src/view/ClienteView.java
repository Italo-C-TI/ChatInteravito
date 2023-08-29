package view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTable;
import core.ChatMessage;
import core.ChatMessage.Action;
import services.ClienteService;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class ClienteView extends JFrame {
	private Socket socket;
	private ChatMessage message;
	private ClienteService service;
	private Thread thread;
	
	private JTextField textNome;
	private JTextArea textEnviarMensagem;
	private JButton btnConectar;

	public ClienteView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 634, 260);
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Conectar / Sair");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblNewLabel.setBounds(22, 0, 143, 35);
		getContentPane().add(lblNewLabel);
		
		textNome = new JTextField();
		textNome.setBounds(20, 54, 130, 26);
		getContentPane().add(textNome);
		textNome.setColumns(10);
		
		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nome= textNome.getText();
				if(!nome.isEmpty()) {
					message = new ChatMessage();
					message.setAction(Action.CONNECT);
					message.setName(nome);
				
					if(socket == null) {
						service = new ClienteService();
						socket = service.connect();
						
						thread = new Thread(new ListenerSocket(socket)); 
						thread.start();
					}
					service.send(message);
				}
			}
		});
		btnConectar.setBounds(162, 55, 94, 27);
		getContentPane().add(btnConectar);
		
		JButton btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        message.setAction(Action.DISCONNECT);
		        service.send(message);        		
		        service.disconnect();
			}
		});
		btnSair.setBounds(549, 4, 79, 28);
		getContentPane().add(btnSair);
		
		textEnviarMensagem = new JTextArea();
		textEnviarMensagem.setBounds(22, 120, 397, 57);
		getContentPane().add(textEnviarMensagem);
		
		JButton btnEnviarMensagem = new JButton("Enviar");
		btnEnviarMensagem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        message.setAction(Action.SEND_ALL);
				String texto= textEnviarMensagem.getText();
				message.setText(texto);
				service.send(message);
				textEnviarMensagem.setText("");

			}
		});
		btnEnviarMensagem.setBounds(302, 199, 117, 29);
		getContentPane().add(btnEnviarMensagem);
		
		JLabel LbNome = new JLabel("Nome:");
		LbNome.setBounds(22, 39, 46, 14);
		getContentPane().add(LbNome);
		
		JLabel lblMensagem = new JLabel("Mensagem:");
		lblMensagem.setBounds(22, 94, 86, 14);
		getContentPane().add(lblMensagem);

	}
	
	private class ListenerSocket implements Runnable {
	    private ObjectInputStream input;

	    public ListenerSocket(Socket socket) {
	        try {
	            this.input = new ObjectInputStream(socket.getInputStream());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    @Override
	    public void run() {
	        ChatMessage message = null;
	        try {
	            while ((message = (ChatMessage) input.readObject()) != null) {
	                Action action = message.getAction();

	                if (action.equals(Action.CONNECT)) {
	                    connect(message);
	                } else if (action.equals(Action.DISCONNECT)) {
	                    disconnect(message);
	                }else if (action.equals(Action.SEND_ALL)) {
	                    updateChatArea(message);
	                }
	            }
	        } catch (Exception e) {
	            System.out.println("Exception: " + e);
	        }
	    }
	}

	private void connect(ChatMessage message) {
	    if (message.getText().equals("NO")) {
	        textNome.setText("");
	        JOptionPane.showMessageDialog(this, "Conexão não realizada");
	        return;
	    }
	    message.setAction(Action.USER_ONLINE);
	    service.send(message);
	}

	private void disconnect(ChatMessage message) {
	    message.setAction(Action.DISCONNECT);
	    service.send(message);
	}


	private void updateChatArea(ChatMessage message) {
		service.send(message);
	}
}
