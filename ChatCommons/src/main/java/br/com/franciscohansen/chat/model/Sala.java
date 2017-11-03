package br.com.franciscohansen.chat.model;

import java.io.Serializable;
import java.util.List;

public class Sala implements Serializable{

    private String nome;
    private List<Usuario> usuarioList;

    public Sala() {
    }

    public Sala(String nome, List<Usuario> usuarioList) {
        this.nome = nome;
        this.usuarioList = usuarioList;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Usuario> getUsuarioList() {
        return usuarioList;
    }

    public void setUsuarioList(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    @Override
    public String toString() {
        return nome;
    }
}
