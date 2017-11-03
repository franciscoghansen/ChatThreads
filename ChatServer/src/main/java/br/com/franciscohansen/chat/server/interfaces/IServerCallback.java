package br.com.franciscohansen.chat.server.interfaces;

import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;

import java.util.List;

public interface IServerCallback {
    void atualizaUsuarios( List<Usuario> usuarios );
    void atuaizaSalas( List<Sala> salas );
    void addLog( Class<?> clz, String message );
}
