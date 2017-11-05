package br.com.franciscohansen.chat.server.interfaces;

import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;

public interface IThreadCallback {

    boolean criaSala();

    boolean excluiSala(Sala sala);

    boolean baneUsuario(Usuario usuario);

}
