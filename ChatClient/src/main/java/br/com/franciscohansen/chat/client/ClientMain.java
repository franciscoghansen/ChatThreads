package br.com.franciscohansen.chat.client;

import br.com.franciscohansen.chat.client.interfaces.IChatScreen;
import br.com.franciscohansen.chat.client.interfaces.IClientCallback;
import br.com.franciscohansen.chat.client.thread.ChatListenerThread;
import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;
import br.com.franciscohansen.chat.model.enums.EAcao;
import br.com.franciscohansen.chat.model.enums.ETipoMensagem;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.swing.*;
import javax.swing.plaf.SliderUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ClientMain implements ActionListener, IClientCallback, IChatScreen {

    private static final String AC_ENTRASALA = "entra-sala";
    private static final String AC_LOGIN = "login";
    private static final String AC_MSG = "envia-mensagem";

    private JPanel pnlBackground;
    private JPanel pnlTopo;
    private JPanel pnlSideBar;
    private JPanel pnlChatGlobal;
    private JPanel pnlBase;

    private JLabel lbNick;

    private JTextField edtNick;
    private JTextField edtMsg;

    private JButton btnLogin;
    private JButton btnEntraSala;
    private JButton btnEnviar;

    private JList lstSalas;

    private JTextArea txChatGlobal;

    private JComboBox cbUsuarios;
    private JCheckBox chkPrivate;
    private JLabel lbMsg;
    private JLabel lbPorta;
    private JSpinner edtPorta;

    private Usuario usuario;
    private Socket socket;
    private int port;
    private ChatListenerThread thread;
    private final DefaultComboBoxModel<Usuario> comboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultListModel<Sala> listModel = new DefaultListModel<>();

    public ClientMain() {
        disableAll();
        initBotoes();
        this.lstSalas.setModel(this.listModel);
        this.cbUsuarios.setModel(comboBoxModel);
    }

    private void enableOrDisable(boolean enabled) {
        lstSalas.setEnabled(enabled);
        btnEntraSala.setEnabled(enabled);
        btnEnviar.setEnabled(enabled);
        edtMsg.setEnabled(enabled);
        cbUsuarios.setEnabled(enabled);
        chkPrivate.setEnabled(enabled);
    }

    private void disableAll() {
        enableOrDisable(false);
    }

    private void enableAll() {
        enableOrDisable(true);
    }

    private void initBotoes() {
        btnEntraSala.setActionCommand(AC_ENTRASALA);
        btnLogin.setActionCommand(AC_LOGIN);
        btnEnviar.setActionCommand(AC_MSG);
        btnLogin.addActionListener(this);
        btnEntraSala.addActionListener(this);
        btnEnviar.addActionListener(this);
    }


    private void fazLogin() {
        String nick = edtNick.getText();
        if (nick == null || nick.isEmpty()) {
            JOptionPane.showMessageDialog(pnlBackground, "Informe o nick para continuar");
            return;
        }
        if (Integer.valueOf(edtPorta.getValue().toString()) == 0) {
            JOptionPane.showMessageDialog(pnlBackground, "A porta não pode ser 0");
            return;
        }
        port = Integer.valueOf(edtPorta.getValue().toString());

        Usuario usuario = new Usuario();
        usuario.setNick(nick);
        try {
            this.socket = new Socket("localhost", port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Acao acao = new Acao();
            acao.setTipoAcao(EAcao.LOGIN);
            acao.setObjetoAcao(usuario);
            outputStream.writeObject(acao);
            Acao retorno = (Acao) inputStream.readObject();
            if (acao.getTipoAcao().equals(EAcao.ERRO)) {
                JOptionPane.showMessageDialog(pnlBackground, acao.getObjetoAcao().toString(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                this.usuario = new Usuario();
                this.usuario.setNick(nick);
            }
            thread = new ChatListenerThread(socket, inputStream, outputStream);
            thread.addCallback(this);
            thread.start();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        txChatGlobal.append("Seja bem vindo " + this.usuario.getNick() + "!\n");
        enableAll();
        this.edtNick.setEnabled(false);
        this.edtPorta.setEnabled(false);
        this.btnLogin.setEnabled(false);
    }

    private void enviaMsgGlobal() {
        if (edtMsg.getText() == null || edtMsg.getText().isEmpty()) {
            return;
        }

        Mensagem msg = new Mensagem();
        msg.setMensagem(edtMsg.getText());
        msg.setUsuarioDe(this.usuario);
        msg.setSala(getSala());
        Usuario usuario = (Usuario) cbUsuarios.getSelectedItem();
        if (usuario != null && !usuario.getNick().equalsIgnoreCase("TODOS")) {
            msg.setUsuarioPara(usuario);
            msg.setTipoMensagem(ETipoMensagem.USUARIO);
        } else {
            msg.setTipoMensagem(ETipoMensagem.TODOS);
        }
        if (chkPrivate.isSelected() && msg.getTipoMensagem().equals(ETipoMensagem.USUARIO)) {
            msg.setTipoMensagem(ETipoMensagem.USUARIO_PRIVADA);
        }
        thread.enviaMensagem(msg);

        edtMsg.setText("");
    }

    private void entraSala() {
        Sala sala = (Sala) lstSalas.getSelectedValue();
        if( sala == null ){
            JOptionPane.showMessageDialog(pnlBackground, "Selecione uma sala para continuar");
            return;
        }
        JFrame frameSala = new JFrame(ChatForm.class.getSimpleName());
        frameSala.setContentPane(new ChatForm(this.usuario, sala, thread).getPnlBackground());
        frameSala.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameSala.pack();
        frameSala.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case AC_ENTRASALA: {
                entraSala();
                break;
            }
            case AC_LOGIN: {
                fazLogin();
                break;
            }
            case AC_MSG: {
                enviaMsgGlobal();
                break;
            }
            default:
                throw new UnsupportedOperationException("Operation Not Supported");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(ClientMain.class.getSimpleName());
        frame.setContentPane(new ClientMain().pnlBackground);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public Sala getSala() {
        return new Sala("TODOS", null);
    }

    @Override
    public void chamaAcao(Acao acao) {
        switch (acao.getTipoAcao()) {
            case BANIR: {
                Usuario u = (Usuario) acao.getObjetoAcao();
                if (u == this.usuario) {
                    JOptionPane.showMessageDialog(pnlBackground, "Você foi banido pelo administrador!");
                    System.exit(0);
                } else {
                    txChatGlobal.append("O usuário " + u.getNick() + " foi banido!");
                }
                break;
            }
            case MENSAGEM:
                Mensagem msg = (Mensagem) acao.getObjetoAcao();
                if (msg.getSala().getNome().equalsIgnoreCase(this.getSala().getNome())) {
                    addMessage(msg);
                }
                break;
            case CRIA_SALA:
            case EXCLUI_SALA:
            case LISTA_SALAS: {
                List<Sala> salas = (List<Sala>) acao.getObjetoAcao();
                atualizaSalas(salas);
                break;
            }
            case ERRO:
                break;
            case LISTA_USUARIOS: {
                List<Usuario> usuarios = (List<Usuario>) acao.getObjetoAcao();
                atualizaUsuarios(usuarios);
                break;
            }
        }
    }

    @Override
    public void addMessage(Mensagem msg) {
        if (msg.getTipoMensagem().equals(ETipoMensagem.USUARIO_PRIVADA) &&
                !msg.getUsuarioPara().equals(this.usuario) &&
                !msg.getUsuarioDe().equals(this.usuario)) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String line = sdf.format(Calendar.getInstance().getTime()) + " - " + msg.getUsuarioDe().getNick() + " diz";
        if (msg.getTipoMensagem().equals(ETipoMensagem.USUARIO_PRIVADA)) {
            line += " em privado";
        }
        if (!msg.getTipoMensagem().equals(ETipoMensagem.TODOS) &&
                msg.getUsuarioPara() != null &&
                msg.getUsuarioPara().getNick() != null &&
                !msg.getUsuarioPara().getNick().isEmpty()) {
            line += " para " + msg.getUsuarioPara().getNick();
        }
        line += ":\n" + msg.getMensagem() + "\n";
        txChatGlobal.append(line);
    }

    @Override
    public void atualizaUsuarios(List<Usuario> usuarios) {
        usuarios.remove(this.usuario);
        comboBoxModel.removeAllElements();
        comboBoxModel.addElement(new Usuario("TODOS"));
        for (Usuario u : usuarios) {
            comboBoxModel.addElement(u);
        }
    }

    @Override
    public void atualizaSalas(List<Sala> salas) {
        listModel.clear();
        for (Sala sala : salas) {
            listModel.addElement(sala);
        }
    }
}
