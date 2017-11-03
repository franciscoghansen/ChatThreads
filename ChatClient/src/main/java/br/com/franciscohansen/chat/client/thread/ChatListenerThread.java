package br.com.franciscohansen.chat.client.thread;

import br.com.franciscohansen.chat.client.interfaces.IClientCallback;
import br.com.franciscohansen.chat.client.interfaces.IClientThread;
import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.enums.EAcao;

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
   public void enviaMensagem(Mensagem mensagem){
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
    public void run() {
        while (true) {
            try {
                Acao acao = (Acao) inputStream.readObject();
                for (IClientCallback callback : callbackList) {
                    callback.chamaAcao(acao);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
