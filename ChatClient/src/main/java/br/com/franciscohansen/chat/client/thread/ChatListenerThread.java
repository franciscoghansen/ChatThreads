package br.com.franciscohansen.chat.client.thread;

import br.com.franciscohansen.chat.client.interfaces.IClientCallback;
import br.com.franciscohansen.chat.client.interfaces.IClientThread;
import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.enums.EAcao;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatListenerThread extends Thread implements IClientThread {

    private final List<IClientCallback> callbackList = new ArrayList<>();
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public ChatListenerThread(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void addCallback(IClientCallback callback) {
        this.callbackList.add(callback);
    }

    public void removeCallback(IClientCallback callback) {
        this.callbackList.remove(callback);
    }

    @Override
    public void enviaMensagem(Mensagem mensagem) {
        Acao acao = new Acao();
        acao.setTipoAcao(EAcao.MENSAGEM);
        acao.setObjetoAcao(mensagem);
        try {
            this.outputStream.writeObject(acao);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enviaAcao(Acao acao) throws IOException {
        this.outputStream.writeObject(acao);
    }

    @Override
    public void run() {
        try {
            while (true) {

                Acao acao = (Acao) inputStream.readObject();
                for (IClientCallback callback : callbackList) {
                    if (callback != null) {
                        callback.chamaAcao(acao);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Erro de conexão ao servidor. \nO Chat será encerrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
