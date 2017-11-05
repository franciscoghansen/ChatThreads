package br.com.franciscohansen.chat.client;

import br.com.franciscohansen.chat.client.interfaces.IAcaoListener;
import br.com.franciscohansen.chat.client.interfaces.IClientCallback;
import br.com.franciscohansen.chat.client.interfaces.IClientThread;
import br.com.franciscohansen.chat.client.interfaces.IListenerCallback;
import br.com.franciscohansen.chat.client.listener.AcaoListener;
import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;
import br.com.franciscohansen.chat.model.enums.EAcao;

import javax.swing.*;
import java.io.IOException;

public class ChatForm extends JFrame implements IClientCallback, IListenerCallback {

    JPanel pnlBackground;
    private JPanel pnlBottom;
    private JTextField edtMsg;
    private JComboBox cbUsuario;
    private JButton btnEnviar;
    private JTextArea txChat;
    private JCheckBox chkPrivate;

    private final Usuario usuario;
    private final Sala sala;
    private final IClientThread thread;
    private final IAcaoListener listener;

    private boolean disconnected;

    public ChatForm(Usuario usuario, Sala sala, IClientThread thread) throws IOException {
        disconnected = false;
        setContentPane(this.pnlBackground);
        setTitle("Chat Threads - Sala: " + sala.getNome());
        this.usuario = usuario;
        this.sala = sala;
        this.thread = thread;
        this.thread.enviaAcao(new Acao(this.usuario, this.sala));
        this.listener = new AcaoListener()
                .setUsuario(this.usuario)
                .setSala(this.sala)
                .setClientThread(this.thread)
                .setComboUsers(this.cbUsuario)
                .setTextArea(this.txChat)
                .setEdtMsg(this.edtMsg)
                .setBtnEnviar(this.btnEnviar)
                .setChkPrivado(this.chkPrivate)
                .setCallback(this)
                .setFrame(this);
        Acao acao = new Acao(EAcao.LISTA_USUARIOS);
        acao.setSala(this.sala);
        thread.enviaAcao(acao);
    }


    @Override
    public Sala getSala() {
        return this.sala;
    }

    @Override
    public void chamaAcao(Acao acao) {
        this.listener.actionPerformed(acao);
    }


    @Override
    public void dispose() {
        if (!disconnected) {
            try {
                this.thread.enviaAcao(new Acao(EAcao.SAI_SALA, this.usuario, this.sala));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.dispose();
    }

    @Override
    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }
}
