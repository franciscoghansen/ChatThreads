package br.com.franciscohansen.chat.model;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ConexaoUsuario implements Serializable{
    private Usuario usuario;
    private ObjectOutputStream outputStream;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
