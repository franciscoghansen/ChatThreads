package br.com.franciscohansen.chat.server.threads;

import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Usuario;
import br.com.franciscohansen.chat.model.enums.EAcao;
import br.com.franciscohansen.chat.server.interfaces.IChatCallback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatThread extends Thread {

    private final IChatCallback callback;
    private final Socket socket;


    public ChatThread(IChatCallback callback, Socket socket) {
        this.callback = callback;
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                Acao acao = (Acao) input.readObject();
                if (acao != null) {
                    switch (acao.getTipoAcao()) {
                        case LOGIN: {
                            Usuario usuario = (Usuario) acao.getObjetoAcao();
                            if (this.callback.nickValido(usuario.getNick())) {
                                this.callback.doLogin(usuario, output);
                            } else {
                                Acao erro = new Acao();
                                acao.setTipoAcao(EAcao.ERRO);
                                acao.setObjetoAcao("NickName já utilizado!");
                                output.writeObject(acao);
                            }
                            break;
                        }
                        case LOGOUT: {
                            break;
                        }
                        case MENSAGEM: {
                            Mensagem msg = (Mensagem) acao.getObjetoAcao();
                            this.callback.sendMessage(msg);
                            break;
                        }
                        case ENTRA_SALA: {
                            break;
                        }
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
