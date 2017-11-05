package br.com.franciscohansen.chat.model;

import br.com.franciscohansen.chat.model.enums.EAcao;

import java.io.Serializable;

public class Acao implements Serializable {

    EAcao tipoAcao;
    Object objetoAcao;
    Sala sala;

    public Acao(EAcao tipoAcao) {
        this.tipoAcao = tipoAcao;
    }

    public Acao() {
    }

    public Acao(EAcao tipoAcao, Object objetoAcao) {
        this.tipoAcao = tipoAcao;
        this.objetoAcao = objetoAcao;
    }

    public Acao(EAcao tipoAcao, Object objetoAcao, Sala sala) {
        this.tipoAcao = tipoAcao;
        this.objetoAcao = objetoAcao;
        this.sala = sala;
    }

    public Acao(Object objetoAcao, Sala sala) {
        this.tipoAcao = EAcao.ENTRA_SALA;
        this.objetoAcao = objetoAcao;
        this.sala = sala;
    }

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

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }
}
