package br.com.franciscohansen.chat.model;

import java.io.Serializable;

public class Usuario implements Serializable {


    public Usuario() {
    }

    public Usuario(String nick) {
        this.nick = nick;
    }

    private String nick;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public String toString() {
        return nick;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        return nick != null ? nick.equals(usuario.nick) : usuario.nick == null;
    }

    @Override
    public int hashCode() {
        return nick != null ? nick.hashCode() : 0;
    }
}
