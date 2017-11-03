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
}
