package br.com.franciscohansen.chat.server;


import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;
import br.com.franciscohansen.chat.model.enums.ETipoMensagem;
import br.com.franciscohansen.chat.server.interfaces.IServerCallback;
import br.com.franciscohansen.chat.server.interfaces.IThreadCallback;
import br.com.franciscohansen.chat.server.threads.ServerThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServerMain implements ActionListener, IServerCallback {


    private JTextField edtPorta;

    private JLabel lbPorta;
    private JTabbedPane tabOpcoes;
    private JPanel tabUsuarios;
    private JList lstSalas;
    private JList lstUsuarios;
    private JPanel tabAcoes;
    private JLabel lb1;
    private JPanel pnlAcoesSalas;
    private JPanel pnlAcoesUsuarios;
    private JList lstAcoesSalas;
    private JList lstAcoesUsuarios;

    private JPanel pnlServidor;
    private JPanel pnlTopo;

    private static final String AC_INICIAR = "start";
    private JButton btnIniciar;
    private static final String AC_PARAR = "stop";
    private JButton btnParar;
    private static final String AC_CRIASALA = "cria-sala";
    private JButton btnSalaCriar;
    private static final String AC_EXCLUISALA = "exclui-sala";
    private JButton btnSalaExcluir;
    private static final String AC_BANEUSUARIO = "bane-usuario";
    private JButton btnBanir;
    private JPanel pnlAcoesSalasBotoes;
    private JPanel pnlAcoesUsuariosBotoes;
    private JPanel tabLog;
    private JTextArea txLog;
    private ServerSocket socket;
    private final List<Sala> salas = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private ServerThread serverThread;

    private DefaultListModel<Sala> salaDefaultListModelWithTodos = new DefaultListModel<Sala>();
    private DefaultListModel<Sala> salaDefaultListModel = new DefaultListModel<>();
    private DefaultListModel<Usuario> usuarioDefaultListModel = new DefaultListModel<>();
    private int port;

    public ServerMain() {
        init();
        initBotoes();
    }


    private void initBotoes() {
        btnIniciar.setActionCommand(AC_INICIAR);
        btnIniciar.addActionListener(this);
        btnParar.setActionCommand(AC_PARAR);
        btnParar.addActionListener(this);
        btnSalaCriar.setActionCommand(AC_CRIASALA);
        btnSalaCriar.addActionListener(this);
        btnSalaExcluir.setActionCommand(AC_EXCLUISALA);
        btnSalaExcluir.addActionListener(this);
        btnBanir.setActionCommand(AC_BANEUSUARIO);
        btnBanir.addActionListener(this);
    }

    private void init() {
        tabOpcoes.setVisible(false);
        btnParar.setEnabled(false);
        salaDefaultListModelWithTodos.addElement(new Sala("TODOS", this.usuarios));

        lstSalas.setModel(salaDefaultListModelWithTodos);
        lstAcoesSalas.setModel(salaDefaultListModel);
        lstUsuarios.setModel(usuarioDefaultListModel);
        lstAcoesUsuarios.setModel(usuarioDefaultListModel);
    }

    private void populaSalasModel(List<Sala> salas) {
        this.salaDefaultListModel.clear();
        this.salaDefaultListModelWithTodos.clear();
        this.salaDefaultListModelWithTodos.addElement(new Sala("TODOS", usuarios));
        for (Sala sala : salas) {
            salaDefaultListModelWithTodos.addElement(sala);
            salaDefaultListModel.addElement(sala);
        }
    }

    private void populaUserModel(List<Usuario> usuarios) {
        this.usuarioDefaultListModel.clear();
        for (Usuario usuario : usuarios) {
            this.usuarioDefaultListModel.addElement(usuario);
        }
    }

    private void addSala() {

        String nome = "";
        nome = JOptionPane.showInputDialog("Informe o nome da sala:");
        if (nome.equalsIgnoreCase("todos")) {
            JOptionPane.showMessageDialog(pnlServidor, "O Nome \"TODOS\" não pode ser utilizado.");
            return;
        }
        if (nome != null && !nome.isEmpty()) {
            Sala sala = new Sala(nome);
            this.salas.add(sala);
            atualizaSalas();
            this.serverThread.criaSala();
        }
    }

    private void iniciaServer() {
        String strPort = edtPorta.getText();
        if (strPort == null || strPort.isEmpty()) {
            JOptionPane.showMessageDialog(pnlServidor, "Informe a porta para iniciar o servidor");
            return;
        }
        port = Integer.valueOf(strPort);
        boolean bOk;
        try {
            socket = new ServerSocket(port);
            bOk = true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(pnlServidor, "Erro ao conectar na porta " + port, "Erro", JOptionPane.ERROR_MESSAGE);
            bOk = false;
        }
        if (bOk) {
            addLog(this.getClass(), "Servidor iniciado na porta " + port);
            serverThread = new ServerThread(socket, this);
            serverThread.start();
            tabOpcoes.setVisible(true);
            btnIniciar.setEnabled(false);
            btnParar.setEnabled(true);
            edtPorta.setEnabled(false);
        }
    }

    private void stopServer() {
        this.serverThread.interrupt();
        try {
            this.socket.close();
            this.edtPorta.setEnabled(true);
            this.btnIniciar.setEnabled(true);
            this.tabOpcoes.setVisible(false);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }
    }

    private void baneUsuario() {
        Usuario u = (Usuario) lstAcoesUsuarios.getSelectedValue();
        if (u == null) {
            JOptionPane.showMessageDialog(null, "Selecione um usuário para banir");
            return;
        }
        this.serverThread.baneUsuario(u);
    }

    private void excluiSala() {
        //TODO finalizar

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case AC_INICIAR: {
                iniciaServer();
                break;
            }
            case AC_PARAR: {
                stopServer();
                break;
            }
            case AC_CRIASALA: {
                addSala();
                break;
            }
            case AC_EXCLUISALA: {
                excluiSala();
                break;
            }
            case AC_BANEUSUARIO: {
                baneUsuario();
                break;
            }
            default:
                throw new UnsupportedOperationException("Not Supported");
        }

    }


    public static void main(String[] args) {
        JFrame frame = new JFrame(ServerMain.class.getSimpleName());
        frame.setContentPane(new ServerMain().pnlServidor);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }


    @Override
    public void atualizaUsuarios(List<Usuario> usuarios) {
        this.usuarios.clear();
        this.usuarios.addAll(usuarios);
        populaUserModel(usuarios);
    }

    @Override
    public void atualizaSalas() {
        this.populaSalasModel(salas);
    }

    @Override
    public void addLog(Class<?> clz, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String logMsg = sdf.format(Calendar.getInstance().getTime()) + "@" + clz.getSimpleName() + ":\n";
        logMsg += message + "\n";
        txLog.append(logMsg);
    }

    @Override
    public List<Sala> getSalas() {
        return this.salas;
    }
}
