package br.feevale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Porta utilizada: 6789
 * 
 * @author nicolasvinicius
 * 
 */
public class Main extends JFrame implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField nome;
	private JButton btn_conecta;

	public Main() {

		super("Chat");
		setBounds(100, 100, 300, 200);
		setLayout(null);

		nome = new JTextField();
		nome.setBounds(30, 40, 200, 23);
		getContentPane().add(nome);

		JButton btn = new JButton("Iniciar Servidor");
		btn.setBounds(30, 80, 150, 25);
		getContentPane().add(btn);

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					controlaServidor((JButton) ev.getSource(), btn_conecta);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btn_conecta = new JButton("Conectar");
		btn_conecta.setBounds(30, 110, 150, 25);
		getContentPane().add(btn_conecta);

		btn_conecta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				conectar();
			}
		});

		addWindowListener(this);

		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/*
	 * Fim do construtor, início dos métodos para conexão entre outros.
	 */
	protected void conectar() {

		try {
			Socket socket = new Socket("localhost", 1710);
			new TelaChat(socket, TelaChat.TpTela.CLIENTE, nome.getText());
			System.out.println("Meu socket: " + socket);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Não foi possível conectar -> "
					+ e.getMessage());
		}
	}

	public void controlaServidor(JButton btn, JButton btn_de_conexao)
			throws IOException {

		if (nome.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(this,
					"Para inicializar, digite seu nome");
			nome.requestFocusInWindow();
			return;
		}

		ServidorChat servidor = ServidorChat.getInstance();

		if (!servidor.isAlive()) {
			try {
				servidor.inicia(1710, nome.getText());
				btn.setText("Finalizar Servidor");
				btn_de_conexao.setEnabled(false);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,
						"Não foi possível iniciar o chat -> " + e.getMessage());
			}
		} else {
			btn.setText("Iniciar Servidor");
			btn_de_conexao.setEnabled(true);
			servidor.finaliza();
		}
	}

	/*
	 * Método MAIN - início do programa
	 */
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException{

		System.out.println("Início");
		new Main();
	}

	@Override
	public void windowClosed(WindowEvent e) {

		ServidorChat servidor = ServidorChat.getInstance();

		if (servidor.isAlive()) {
			try {
				servidor.finaliza();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}