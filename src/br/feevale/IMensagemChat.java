package br.feevale;

import org.json.JSONException;

/**
 * Interface que define as possíveis mensagens a serem enviadas entre servidor e cliente
 * @author diovani
 *
 */
public interface IMensagemChat {

    /**
     * (pseudo)Enum para indicar o tipo de mensagem
     *
     */
    public static class TipoMensagem {
        public static final int HANDSHAKE            = 1;
        public static final int MENSAGEM             = 2;
        public static final int ATENCAO              = 3;
        public static final int DESCONEXAO           = 4;
        public static final int ENVIOARQUIVO         = 5;
        public static final int RESPOSTAENVIOARQUIVO = 6;
        public static final int ENVIOFOTO            = 7;
        public static final int RESPOSTAENVIOFOTO    = 8;
    }

    public void enviaHandshake(String nomeUsuario) throws JSONException;
    public void enviaMensagem(String mensagem) throws JSONException;
    public void chamaAtencao();

    public void informaDesconexao();

    public void solicitaEnvioArquivo(String nomeArquivo, Integer tamanho);
    public void aceitaEnvioArquivo(Boolean aceita, Integer porta);

    public void solicitaEnvioFoto(String nomeArquivo, Integer tamanho);
    public void aceitaEnvioFoto(Integer porta);
}
