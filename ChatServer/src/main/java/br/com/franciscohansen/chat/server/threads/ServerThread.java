package br.com.franciscohansen.chat.server.threads;

import br.com.franciscohansen.chat.model.*;
import br.com.franciscohansen.chat.model.enums.EAcao;
import br.com.franciscohansen.chat.model.enums.ETipoMensagem;
import br.com.franciscohansen.chat.server.interfaces.IChatCallback;
import br.com.franciscohansen.chat.server.interfaces.IServerCallback;
import br.com.franciscohansen.chat.server.interfaces.IThreadCallback;

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
    public boolean criaSala() {
        enviaSalas();
        return true;
    }

    @Override
    public boolean excluiSala(Sala sala) {
        callback.getSalas().remove(sala);
        enviaSalas();
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
            envia(con, acao);
            if (con.getUsuario().equals(usuario)) {
                aExcluir = con;
            }
        }
        if (aExcluir != null) {
            removeConexao(aExcluir);
        }
        enviaUsuarios();
        enviaSalas();
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
        ConexaoUsuario con = new ConexaoUsuario();
        con.setOutputStream(outputStream);
        con.setUsuario(usuario);
        this.conexoes.add(con);
        this.callback.addLog(this.getClass(), "Usuário " + usuario.getNick() + " entrou no chat");

        this.callback.atualizaUsuarios(getUsuarios());
        callback.atualizaSalas();

        Acao acao = new Acao();
        acao.setTipoAcao(EAcao.LOGIN_OK);
        envia(con, acao);

        enviaUsuarios();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        enviaSalas();
        return true;
    }

    @Override
    public boolean entraSala(Usuario usuario, Sala sala) {
        for (Sala s : callback.getSalas()) {
            if (s.getNome().equals(sala.getNome())) {
                s.getUsuarioList().add(usuario);
            }
        }
        enviaSalas();
        return true;
    }

    @Override
    public void doLogout(Usuario usuario) {
        Acao acao = new Acao();
        acao.setObjetoAcao(usuario);
        acao.setTipoAcao(EAcao.LOGOUT);
        for (ConexaoUsuario con : conexoes) {
            if (!con.getUsuario().equals(usuario)) {
                envia(con, acao);
            } else {
                removeConexao(con);
            }
        }
    }

    private void removeConexao(ConexaoUsuario con) {
        Usuario u = con.getUsuario();
        conexoes.remove(con);
        for (Sala s : salas) {
            s.getUsuarioList().remove(u);
        }
        this.callback.atualizaUsuarios(getUsuarios());
        this.callback.atualizaSalas();
        enviaUsuarios();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        enviaSalas();
    }

    @Override
    public void sendMessage(Mensagem message) {
        Acao acao = new Acao();
        acao.setObjetoAcao(message);
        acao.setTipoAcao(EAcao.MENSAGEM);
        if (message.getTipoMensagem().equals(ETipoMensagem.USUARIO_PRIVADA)) {
            for (ConexaoUsuario con : conexoes) {
                if (con.getUsuario().equals(message.getUsuarioPara()) ||
                        con.getUsuario().equals(message.getUsuarioDe())) {
                    envia(con, acao);
                }
            }
        } else {
            for (ConexaoUsuario con : conexoes) {
                envia(con, acao);

            }
        }
    }

    @Override
    public void listaUsuarios(Sala sala) {
        Acao acao = new Acao(EAcao.LISTA_USUARIOS);
        for (Sala s : this.callback.getSalas()) {
            if (s.equals(sala)) {
                acao.setObjetoAcao(sala.getUsuarioList());
                acao.setSala(s);
                break;
            }
        }
        for (ConexaoUsuario con : conexoes) {
            envia(con, acao);
        }
    }


    private List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        for (ConexaoUsuario conexao : conexoes) {
            usuarios.add(conexao.getUsuario());
        }
        return usuarios;
    }

    private void enviaSalas() {
        List<Sala> lst = new ArrayList<>();
        lst.addAll(this.callback.getSalas());
        Acao acao = new Acao(EAcao.LISTA_SALAS, lst);
        for (ConexaoUsuario con : conexoes) {
            envia(con, acao);
        }
    }

    private void enviaUsuarios() {
        Acao acao = new Acao(EAcao.LISTA_USUARIOS, getUsuarios(), new Sala("TODOS"));
        for (ConexaoUsuario con : conexoes) {
            envia(con, acao);
        }
    }

    private boolean envia(ConexaoUsuario con, Object object) {

        try {
            Thread.sleep(500);
            con.getOutputStream().writeObject(object);
            con.getOutputStream().flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
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


}
