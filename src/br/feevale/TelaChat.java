package br.feevale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.JSONException;
import org.json.JSONObject;

public class TelaChat extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum TpTela {
		SERVIDOR, CLIENTE
	}

	/*
	 * Atributos de tela
	 */
	private JTextArea log;
	private JTextField mensagem;
	private JButton arquivo;
	private JButton atencao;
	/*
	 * Outros atributos
	 */

	public JFileChooser fc;
	public String file;
	private Socket socket;
	private String meuNome;
	private String outroNome;

	/**
	 * @param socket
	 * @param tipo
	 * @param meuNome
	 * @throws JSONException
	 */
	public TelaChat(Socket socket, TpTela tipo, String meuNome)
			throws JSONException {

		this.socket = socket;
		this.meuNome = meuNome;

		if (tipo == TpTela.SERVIDOR) {
			setBounds(10, 80, 500, 450);
		} else {
			setBounds(515, 80, 500, 450);
		}
		setTitle(meuNome);
		setLayout(null);

		log = new JTextArea();
		log.setLineWrap(true);
		log.setEditable(false);

		JScrollPane sp = new JScrollPane(log);
		sp.setBounds(10, 10, 470, 300);
		getContentPane().add(sp);

		mensagem = new JTextField();
		mensagem.setBounds(10, 320, 470, 23);
		getContentPane().add(mensagem);

		mensagem.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					enviaMensagem();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		/*
		 * bot�o para enviar arquivo
		 */
		arquivo = new JButton("Enviar arquivo");
		arquivo.setBounds(10, 345, 150, 20);
		getContentPane().add(arquivo);
		arquivo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				enviaArquivo();
			}
		});

		/*
		 * bot�o para chamar a aten��o
		 */

		atencao = new JButton("Chamar aten��o");
		atencao.setBounds(170, 345, 150, 20);
		getContentPane().add(atencao);
		atencao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JOptionPane
						.showMessageDialog(null, "Chamar aten��o do usu�rio");
			}
		});

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

		System.out.println("Iniciando...");
		new LeitorDeSocket().start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
		}

		enviaHandshake();
	}

	private void enviaHandshake() throws JSONException {

		JSONObject obj = new JSONObject();
		obj.put("tpTransacao", 1);
		obj.put("meuNome", meuNome);

		enviaTransacao(obj);
	}

	private void enviaTransacao(JSONObject obj) {

		try {
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);

			dos.writeUTF(obj.toString());
			os.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void enviaMensagem() {

		String mensagemAEnviar = mensagem.getText();

		mensagem.setText(null);
		mensagem.requestFocusInWindow();

		try {
			JSONObject obj = new JSONObject();
			obj.put("tpTransacao", 2);
			obj.put("msg", mensagemAEnviar);

			enviaTransacao(obj);

			escreveNoLog(meuNome + ": " + mensagemAEnviar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enviaArquivo() {

		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(TelaChat.this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			File file = fc.getSelectedFile();
			
			mensagem.setText(file.getName());

			int confirma = JOptionPane.showConfirmDialog(null,
					"Deseja enviar o arquivo:" + file.getName(),
					"Aten��o", JOptionPane.YES_NO_OPTION);

			if (confirma == JOptionPane.YES_OPTION) {
				
				System.out.println(file);
				
				try {
					JSONObject obj = new JSONObject();
					obj.put("tpTransacao", 3);
					obj.put("arquivo", file);

					enviaTransacao(obj);

					escreveNoLog(meuNome + ": " + file.getName());

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			} else {
				
				mensagem.setText(null);
				mensagem.requestFocus();
			}
		}
	}

	private class LeitorDeSocket extends Thread {

		@Override
		public void run() {
			try {
				InputStream is = socket.getInputStream();

				DataInputStream dis = new DataInputStream(is);

				while (isVisible()) {

					String mensagem = dis.readUTF();

					JSONObject obj = new JSONObject(mensagem);

					int tpTransacao = obj.getInt("tpTransacao");

					switch (tpTransacao) {

					case 1:
						trataHandshake(obj);
						break;
					case 2:
						trataMensagem(obj);
						break;
					case 3:
						trataArquivo(obj);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void escreveNoLog(String msg) {
		log.append(msg);
		log.append("\n");
	}

	public void trataHandshake(JSONObject obj) throws JSONException {
		outroNome = obj.getString("meuNome");
	}

	public void trataMensagem(JSONObject obj) throws JSONException {

		String mensagem = obj.getString("msg");
		escreveNoLog(outroNome + ": " + mensagem);
	}

	/*
	 * Trata arquivo a receber
	 */
	public void trataArquivo(JSONObject obj) throws JSONException {
		
		File file = (File) obj.get("Arquivo");
				
		int confirma = JOptionPane.showConfirmDialog(null,
				"Deseja receber o arquivo" + file.getName(), "Aten��o",
				JOptionPane.YES_NO_OPTION);
		if (confirma == JOptionPane.YES_OPTION) {

			int returnVal = fc.showSaveDialog(TelaChat.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				//File file = fc.getSelectedFile();

				escreveNoLog(outroNome + ": " + file.getName() + "." + "/n");

			} else {
				escreveNoLog(outroNome + ": " + "Cancelou recebimento");
			}
		}

		String mensagem = obj.getString("msg");

		escreveNoLog(outroNome + ": " + mensagem);
	}

}