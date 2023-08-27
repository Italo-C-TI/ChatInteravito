package view;

import java.awt.EventQueue;

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
		panel.setBounds(431, 59, 175, 361);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnAtualizarOnlines = new JButton("Atualizar");
		btnAtualizarOnlines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnAtualizarOnlines.setBounds(28, 326, 117, 29);
		panel.add(btnAtualizarOnlines);
		
		
		JList listOnlines = new JList();
		listOnlines.setBounds(6, 30, 163, 284);
		panel.add(listOnlines);
		
		textNome = new JTextField();
		textNome.setBounds(22, 54, 130, 26);
		getContentPane().add(textNome);
		textNome.setColumns(10);
		
		JButton btnConectar = new JButton("Conectar");
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
					textMensagensRecebidas.append("Usuario " + message.getName() + " Se conectou\n");
					service.send(message);
					
				}
			}
		});
		btnConectar.setBounds(173, 53, 117, 29);
		getContentPane().add(btnConectar);
		
		JButton btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        ChatMessage message = new ChatMessage();
		        message.setName(message.getName());
		        message.setAction(Action.DISCONNECT);
		        service.send(message);
				service.disconnect();
			}
		});
		btnSair.setBounds(302, 54, 117, 29);
		getContentPane().add(btnSair);
		
		textMensagensRecebidas = new JTextArea();
		textMensagensRecebidas.setBounds(20, 107, 351, 215);
		getContentPane().add(textMensagensRecebidas);
		
		textEnviarMensagem = new JTextArea();
		textEnviarMensagem.setBounds(22, 334, 349, 57);
		getContentPane().add(textEnviarMensagem);
		
		JButton btnEnviarMensagem = new JButton("Enviar");
		btnEnviarMensagem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String texto= textEnviarMensagem.getText();
				String nome= textNome.getText();
				textMensagensRecebidas.append(nome + ": " + texto);
				textEnviarMensagem.setText("");

			}
		});
		btnEnviarMensagem.setBounds(254, 403, 117, 29);
		getContentPane().add(btnEnviarMensagem);

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
				service = new ClienteService();
				socket = service.connect();
				
				while ((message = (ChatMessage)input.readObject()) != null) {
					Action action = message.getAction();
					
					if(action.equals(Action.CONNECT)){
						connect(message);
					}else if(action.equals(Action.DISCONNECT)){
						disconnect(message);						
					}else if(action.equals(Action.USER_ONLINE)){
						refreshOnlines(message);
					}
				}
			} catch (Exception e) {
		        System.out.println("Exception: " + e);
			}
		}
		
	}
	private void connect (ChatMessage message) {
		if(message.getText().equals("NO")) {
			textNome.setText("");
			JOptionPane.showMessageDialog(this, "Conexao nao realizada");
		}
		textMensagensRecebidas.append(message.getName()+ "\n");

	}
	private void disconnect (ChatMessage message) {
		
	}
	private void refreshOnlines (ChatMessage message) {
		
	}
}
