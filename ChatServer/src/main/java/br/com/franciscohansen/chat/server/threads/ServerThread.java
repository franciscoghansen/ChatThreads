package br.com.franciscohansen.chat.server.threads;

import br.com.franciscohansen.chat.model.*;
import br.com.franciscohansen.chat.model.enums.EAcao;
import br.com.franciscohansen.chat.model.enums.ETipoMensagem;
import br.com.franciscohansen.chat.server.interfaces.IChatCallback;
import br.com.franciscohansen.chat.server.interfaces.IServerCallback;
import br.com.franciscohansen.chat.server.interfaces.IThreadCallback;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ServerThread extends Thread implements IThreadCallback, IChatCallback {
    private static final Logger LOG = Logger.getLogger(ServerThread.class.getSimpleName());

    private final ServerSocket serverSocket;

    private final List<ConexaoUsuario> conexoes = new ArrayList<>();
    private final List<Sala> salas = new ArrayList<>();
    private final IServerCallback callback;


    public ServerThread(ServerSocket socket, IServerCallback callback) {
        this.callback = callback;
        this.serverSocket = socket;
    }


    @Override
    public boolean criaSala(Sala sala) {
        callback.addLog(ServerThread.class, "Criou sala " + sala.getNome());
        salas.add(sala);
        for (ConexaoUsuario con : conexoes) {
            ObjectOutputStream out = con.getOutputStream();
            Acao acao = new Acao();
            acao.setTipoAcao(EAcao.CRIA_SALA);
            acao.setObjetoAcao(salas);
            try {
                out.writeObject(acao);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean excluiSala(Sala sala) {
        salas.remove(sala);
        for (ConexaoUsuario con : conexoes) {
            Acao acao = new Acao();
            acao.setObjetoAcao(salas);
            acao.setTipoAcao(EAcao.EXCLUI_SALA);
            try {
                con.getOutputStream().writeObject(acao);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean baneUsuario(Usuario usuario) {
        callback.addLog(ServerThread.class, "Banindo usuário " + usuario.getNick());
        ConexaoUsuario aExcluir = null;
        for (ConexaoUsuario con : conexoes) {
            ObjectOutputStream out = con.getOutputStream();
            Acao acao = new Acao();
            acao.setTipoAcao(EAcao.BANIR);
            acao.setObjetoAcao(usuario);
            try {
                out.writeObject(acao);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (con.getUsuario().equals(usuario)) {
                aExcluir = con;
            }
        }
        if (aExcluir != null) {
            conexoes.remove(aExcluir);
        }
        Acao acao = new Acao();
        acao.setObjetoAcao(getUsuarios());
        acao.setTipoAcao(EAcao.LISTA_USUARIOS);
        for (ConexaoUsuario con : conexoes) {
            try {
                con.getOutputStream().writeObject(acao);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    @Override
    public boolean nickValido(String nick) {
        for (ConexaoUsuario con : conexoes) {
            if (con.getUsuario().getNick().equalsIgnoreCase(nick)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean doLogin(Usuario usuario, ObjectOutputStream outputStream) {
        try {
            ConexaoUsuario con = new ConexaoUsuario();
            con.setOutputStream(outputStream);
            con.setUsuario(usuario);
            this.conexoes.add(con);
            this.callback.addLog(this.getClass(), "Usuário " + usuario.getNick() + " entrou no chat");

            this.callback.atualizaUsuarios(getUsuarios());
            Acao acao = new Acao();
            acao.setTipoAcao(EAcao.LOGIN_OK);
            outputStream.writeObject(acao);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Acao acao = new Acao();
        acao.setObjetoAcao(getUsuarios());
        acao.setTipoAcao(EAcao.LISTA_USUARIOS);
        for (ConexaoUsuario con : conexoes) {
            try {
                con.getOutputStream().writeObject(acao);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        acao.setTipoAcao(EAcao.LISTA_SALAS);
        acao.setObjetoAcao(this.salas);
        for (ConexaoUsuario con : conexoes) {
            try {
                con.getOutputStream().writeObject(acao);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void sendMessage(Mensagem message) {
        Acao acao = new Acao();
        acao.setObjetoAcao(message);
        acao.setTipoAcao(EAcao.MENSAGEM);
        try {
            if (message.getTipoMensagem().equals(ETipoMensagem.USUARIO_PRIVADA)) {
                for (ConexaoUsuario con : conexoes) {
                    if (con.getUsuario().equals(message.getUsuarioPara())) {
                        con.getOutputStream().writeObject(acao);
                    }
                }
            } else {
                for (ConexaoUsuario con : conexoes) {
                    con.getOutputStream().writeObject(acao);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                LOG.info("Aguardando conexão");
                Socket socket = serverSocket.accept();
                LOG.info("Conexão recebida");
                ChatThread chatThread = new ChatThread(this, socket);
                chatThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        for (ConexaoUsuario conexao : conexoes) {
            usuarios.add(conexao.getUsuario());
        }
        return usuarios;
    }

}
