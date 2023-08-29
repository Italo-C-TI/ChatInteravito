package view;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import core.ChatMessage;
import core.ChatMessage.Action;
import services.ClienteService;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JList;
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
	
	private JTable table;
	private JTextField textNome;
	private JTextArea textMensagensRecebidas;
	private JTextArea textEnviarMensagem;
	private JButton btnConectar;
	private JList<String> listOnlines;

	public ClienteView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 634, 480);
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Conectar");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblNewLabel.setBounds(22, 0, 397, 35);
		getContentPane().add(lblNewLabel);
		
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Onlines", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(429, 39, 179, 391);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnAtualizarOnlines = new JButton("Atualizar");
		btnAtualizarOnlines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			       if (socket != null) {
			            ChatMessage request = new ChatMessage();
			            request.setAction(Action.USER_ONLINE);
			            service.send(request);
			        }
			}
		});
		btnAtualizarOnlines.setBounds(28, 351, 117, 29);
		panel.add(btnAtualizarOnlines);
		
		
		listOnlines = new JList<>();
		listOnlines.setBounds(6, 30, 163, 284);
		panel.add(listOnlines);
		
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
						
						new Thread(new ListenerSocket(socket)).start(); 
					}
					service.send(message);
					textMensagensRecebidas.append("Usuario " + message.getName() + " se conectou\n");
				}
			}
		});
		btnConectar.setBounds(239, 53, 94, 27);
		getContentPane().add(btnConectar);
		
		JButton btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMessage message = new ChatMessage();
		        message.setName(message.getName());
		        message.setAction(Action.DISCONNECT);
		        service.disconnect();
		        service.send(message);
			}
		});
		btnSair.setBounds(340, 52, 79, 28);
		getContentPane().add(btnSair);
		
		textMensagensRecebidas = new JTextArea();
		textMensagensRecebidas.setBounds(20, 91, 399, 231);
		getContentPane().add(textMensagensRecebidas);
		
		textEnviarMensagem = new JTextArea();
		textEnviarMensagem.setBounds(22, 334, 397, 57);
		getContentPane().add(textEnviarMensagem);
		
		JButton btnEnviarMensagem = new JButton("Enviar");
		btnEnviarMensagem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String texto= textEnviarMensagem.getText();
				String nome= textNome.getText();
				textMensagensRecebidas.append(nome + ": " + texto + "\n");
				textEnviarMensagem.setText("");

			}
		});
		btnEnviarMensagem.setBounds(302, 401, 117, 29);
		getContentPane().add(btnEnviarMensagem);
		
		JLabel LbNome = new JLabel("Nome:");
		LbNome.setBounds(22, 39, 46, 14);
		getContentPane().add(LbNome);

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
	                } else if (action.equals(Action.USER_ONLINE)) {
	                    refreshOnlines(message);
	                } else if (action.equals(Action.SEND_ALL)) {
	                    updateChatArea(message.getName() + ": " + message.getText() + "\n");
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
	    textMensagensRecebidas.append("Usuário " + message.getName() + " se conectou\n");
	    message.setAction(Action.USER_ONLINE);
	    service.send(message);
	}

	private void disconnect(ChatMessage message) {
	    textMensagensRecebidas.append("Usuário " + message.getName() + " se desconectou\n");
	    message.setAction(Action.DISCONNECT);
	    service.send(message);
	}

	private void refreshOnlines(ChatMessage message) {
	    String[] users = message.getSetOnlines().toArray(new String[0]);
	    listOnlines.setListData(users);
	}

	private void updateChatArea(String message) {
	    textMensagensRecebidas.append(message);
	}

}
