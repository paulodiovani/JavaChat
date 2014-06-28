package br.feevale;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor de chat respons√°vel por ouvir conex√µes e guardar mensagens
 * 
 * @author diovani
 * 
 */
public class ServidorChat extends Thread {

	/**
	 * Instancia singleton da classe
	 */
	private static ServidorChat instance;

	private Integer porta;
	
	private String nomeServidor;

	private Socket cliente;

	private ServerSocket server;

	/**
	 * Construtor privado para garantir o uso como singleton
	 */
	private ServidorChat() {
	}

	/**
	 * Obt√©m a instancia singleton do classe ou a cria
	 * 
	 * @return ServidorChat
	 */
	public static ServidorChat getInstance() {
		if (instance == null) {
			instance = new ServidorChat();
		}

		return instance;
	}

	/**
	 * Inicia a thread para escutar a porta
	 * 
	 * @param int porta
	 * @param String nomeServidor
	 *
	 */
	public void inicia(int porta, String nomeServidor) throws IOException {
		this.porta = porta;
		this.nomeServidor = nomeServidor;

		server = new ServerSocket(this.porta);
		this.start();
	}

	/**
	 * Finaliza a thread a fecha o servidor
	 * 
	 * @throws IOException
	 */
	public void finaliza() throws IOException {
		this.server.close();
		this.interrupt();
		instance = null;
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Espera uma conex„o de socket
				Socket socketCliente = this.server.accept();
				
				//ApÛs receber a conex„o cria uma janela de chat para o servidor
				new TelaChat(socketCliente, TelaChat.TpTela.SERVIDOR,nomeServidor);
				System.out.println("Socket no servidor: " + socketCliente);

			}
		} catch (Exception e) {
			System.out.printf(e.getMessage());
		}
	}

	public Socket getClient() {
		return this.cliente;
	}
}
