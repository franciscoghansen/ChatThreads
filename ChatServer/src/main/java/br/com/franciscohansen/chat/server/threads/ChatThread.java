package br.com.franciscohansen.chat.server.threads;

import br.com.franciscohansen.chat.model.*;
import br.com.franciscohansen.chat.model.enums.EAcao;
import br.com.franciscohansen.chat.server.interfaces.IChatCallback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
                try {
                    Acao acao = (Acao) input.readObject();
                    if (acao != null) {
                        switch (acao.getTipoAcao()) {
                            case LOGIN: {
                                Usuario usuario = (Usuario) acao.getObjetoAcao();
                                if (this.callback.nickValido(usuario.getNick())) {
                                    this.callback.doLogin(usuario, output);
                                } else {
                                    Acao erro = new Acao();
                                    erro.setTipoAcao(EAcao.ERRO);
                                    erro.setObjetoAcao("NickName já utilizado!");
                                    envia(output, erro);
                                }
                                break;
                            }
                            case LOGOUT: {
                                callback.doLogout((Usuario) acao.getObjetoAcao());
                                break;
                            }
                            case MENSAGEM: {
                                Mensagem msg = (Mensagem) acao.getObjetoAcao();
                                this.callback.sendMessage(msg);
                                break;
                            }
                            case ENTRA_SALA: {
                                callback.entraSala((Usuario) acao.getObjetoAcao(), acao.getSala());
                                break;
                            }
                            case LISTA_USUARIOS: {
                                Sala s = acao.getSala();
                                if (s == null) {
                                    s = new Sala("TODOS");
                                }
                                callback.listaUsuarios(s);
                                break;
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    continue;
                }
            }

        } catch (IOException e) {
            System.exit(1);
        }
    }

    private boolean envia(ObjectOutputStream out, Object object) {
        try {
            out.writeObject(object);
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
