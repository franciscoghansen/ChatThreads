package br.com.franciscohansen.chat.model;

import br.com.franciscohansen.chat.model.enums.EAcao;

import java.io.Serializable;

public class Acao implements Serializable {

    EAcao tipoAcao;
    Object objetoAcao;

    public EAcao getTipoAcao() {
        return tipoAcao;
    }

    public void setTipoAcao(EAcao tipoAcao) {
        this.tipoAcao = tipoAcao;
    }

    public Object getObjetoAcao() {
        return objetoAcao;
    }

    public void setObjetoAcao(Object objetoAcao) {
        this.objetoAcao = objetoAcao;
    }
}
