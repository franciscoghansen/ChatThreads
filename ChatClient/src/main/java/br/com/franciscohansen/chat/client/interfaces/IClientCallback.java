package br.com.franciscohansen.chat.client.interfaces;

import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Sala;

public interface IClientCallback {

    Sala getSala();
    void chamaAcao( Acao acao );
}
