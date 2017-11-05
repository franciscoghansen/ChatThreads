package br.com.franciscohansen.chat.client;

import br.com.franciscohansen.chat.client.interfaces.IAcaoListener;
import br.com.franciscohansen.chat.client.interfaces.IChatScreen;
import br.com.franciscohansen.chat.client.interfaces.IClientCallback;
import br.com.franciscohansen.chat.client.listener.AcaoListener;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ClientMain extends WindowAdapter implements ActionListener, IClientCallback {

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
    private final IAcaoListener listener;

    public ClientMain() {
        disableAll();
        initBotoes();
        this.listener = new AcaoListener()
                .setListSalas(lstSalas)
                .setComboUsers(cbUsuarios)
                .setTextArea(txChatGlobal)
                .setSala(new Sala("TODOS"))
                .setBtnEnviar(btnEnviar)
                .setChkPrivado(chkPrivate)
                .setEdtMsg(edtMsg);

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
            if (retorno.getTipoAcao().equals(EAcao.ERRO)) {
                JOptionPane.showMessageDialog(pnlBackground, acao.getObjetoAcao().toString(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                this.usuario = new Usuario();
                this.usuario.setNick(nick);
            }
            thread = new ChatListenerThread(socket, inputStream, outputStream);
            thread.addCallback(this);
            thread.start();
            this.listener.setClientThread(thread).setUsuario(this.usuario);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        txChatGlobal.append("Seja bem vindo " + this.usuario.getNick() + "!\n");
        enableAll();
        this.edtNick.setEnabled(false);
        this.edtPorta.setEnabled(false);
        this.btnLogin.setEnabled(false);
    }


    private void entraSala() {
        Sala sala = (Sala) lstSalas.getSelectedValue();
        if (sala == null) {
            JOptionPane.showMessageDialog(pnlBackground, "Selecione uma sala para continuar");
            return;
        }
        try {
            ChatForm form = new ChatForm(this.usuario, sala, thread);
            JFrame frameSala = new JFrame(ChatForm.class.getSimpleName());
            frameSala.setContentPane(form.getPnlBackground());
            frameSala.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frameSala.pack();
            frameSala.setVisible(true);
            thread.addCallback(form);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            default:
                throw new UnsupportedOperationException("Operation Not Supported");
        }
    }


    @Override
    public Sala getSala() {
        return new Sala("TODOS", null);
    }

    @Override
    public void chamaAcao(Acao acao) {
        this.listener.actionPerformed(acao);
    }


    @Override
    public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        if (this.usuario != null) {
            Acao acao = new Acao();
            acao.setTipoAcao(EAcao.LOGOUT);
            acao.setObjetoAcao(this.usuario);
            try {
                thread.enviaAcao(acao);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ClientMain mainInstance = new ClientMain();
        JFrame frame = new JFrame(ClientMain.class.getSimpleName());
        frame.setContentPane(mainInstance.pnlBackground);
        frame.addWindowListener(mainInstance);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }
}
