package br.feevale;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class MensagemJson implements IMensagemChat {

    private Socket socket;

    public MensagemJson(Socket socket) {
        this.socket = socket;
    }

    private void enviaJson(JSONObject data) {
        try {
            OutputStream os = this.socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream( os );

            dos.writeUTF( data.toString() );
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enviaHandshake(String nomeUsuario) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("tpTransacao", IMensagemChat.TipoMensagem.HANDSHAKE);
        json.put("nome", nomeUsuario);

        this.enviaJson(json);
    }

    @Override
    public void enviaMensagem(String mensagem) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("tpTransacao", IMensagemChat.TipoMensagem.MENSAGEM);
        json.put("mensagem", mensagem);

        this.enviaJson(json);
    }

    @Override
    public void chamaAtencao() {
        // TODO Auto-generated method stub
    }

    @Override
    public void informaDesconexao() {
        // TODO Auto-generated method stub
    }

    @Override
    public void solicitaEnvioArquivo(String nomeArquivo, Integer tamanho) {
        // TODO Auto-generated method stub
    }

    @Override
    public void aceitaEnvioArquivo(Boolean aceita, Integer porta) {
        // TODO Auto-generated method stub
    }

    @Override
    public void solicitaEnvioFoto(String nomeArquivo, Integer tamanho) {
        // TODO Auto-generated method stub
    }

    @Override
    public void aceitaEnvioFoto(Integer porta) {
        // TODO Auto-generated method stub
    }
}
