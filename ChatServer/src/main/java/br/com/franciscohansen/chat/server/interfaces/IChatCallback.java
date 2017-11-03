package br.com.franciscohansen.chat.server.interfaces;

import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Usuario;

import java.io.ObjectOutputStream;

public interface IChatCallback {
    boolean nickValido(String nick);

    boolean doLogin(Usuario usuario, ObjectOutputStream outputStream);

    void sendMessage(Mensagem message);

}
