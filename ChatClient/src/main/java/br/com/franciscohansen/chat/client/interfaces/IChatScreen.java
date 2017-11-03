package br.com.franciscohansen.chat.client.interfaces;

import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;

import java.util.List;

public interface IChatScreen {
    void addMessage(Mensagem msg);

    void atualizaUsuarios(List<Usuario> usuarios);

    void atualizaSalas(List<Sala> salas);
}
