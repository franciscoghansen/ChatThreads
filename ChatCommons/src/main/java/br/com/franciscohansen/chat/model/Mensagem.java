package br.com.franciscohansen.chat.model;

import br.com.franciscohansen.chat.model.enums.ETipoMensagem;

import java.io.Serializable;

public class Mensagem implements Serializable{

    private Sala sala;
    private Usuario usuarioDe;
    private Usuario usuarioPara;
    private String mensagem;
    private ETipoMensagem tipoMensagem;

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Usuario getUsuarioDe() {
        return usuarioDe;
    }

    public void setUsuarioDe(Usuario usuarioDe) {
        this.usuarioDe = usuarioDe;
    }

    public Usuario getUsuarioPara() {
        return usuarioPara;
    }

    public void setUsuarioPara(Usuario usuarioPara) {
        this.usuarioPara = usuarioPara;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public ETipoMensagem getTipoMensagem() {
        return tipoMensagem;
    }

    public void setTipoMensagem(ETipoMensagem tipoMensagem) {
        this.tipoMensagem = tipoMensagem;
    }
}
