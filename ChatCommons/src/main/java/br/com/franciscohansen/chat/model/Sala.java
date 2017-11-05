package br.com.franciscohansen.chat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sala implements Serializable {

    private String nome;
    private List<Usuario> usuarioList;

    public Sala() {
    }

    public Sala(String nome) {
        this.nome = nome;
        this.usuarioList = new ArrayList<>();
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
        String s = nome + " (";
        if (this.usuarioList != null) {
            s += this.usuarioList.size();
        } else {
            s += 0;
        }
        s += " usuários)";
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sala sala = (Sala) o;

        return nome != null ? nome.equals(sala.nome) : sala.nome == null;
    }

    @Override
    public int hashCode() {
        return nome != null ? nome.hashCode() : 0;
    }
}
