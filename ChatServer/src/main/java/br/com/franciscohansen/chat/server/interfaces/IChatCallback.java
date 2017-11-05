package br.com.franciscohansen.chat.server.interfaces;

import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;

import java.io.ObjectOutputStream;

public interface IChatCallback {
    boolean nickValido(String nick);

    boolean doLogin(Usuario usuario, ObjectOutputStream outputStream);

    boolean entraSala( Usuario usuario, Sala sala );

    void doLogout( Usuario usuario );

    void sendMessage(Mensagem message);

    void listaUsuarios( Sala sala );

}
