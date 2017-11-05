package br.com.franciscohansen.chat.client.interfaces;

import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Mensagem;

import java.io.IOException;

public interface IClientThread {
    void enviaMensagem(Mensagem mensagem);
    void enviaAcao( Acao acao ) throws IOException;
}
