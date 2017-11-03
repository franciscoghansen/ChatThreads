package br.com.franciscohansen.chat.client.interfaces;

import br.com.franciscohansen.chat.model.Mensagem;

public interface IClientThread {
    void enviaMensagem(Mensagem mensagem);
}
