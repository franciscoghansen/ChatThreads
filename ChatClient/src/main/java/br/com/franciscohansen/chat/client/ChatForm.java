package br.com.franciscohansen.chat.client;

import br.com.franciscohansen.chat.client.interfaces.IAcaoListener;
import br.com.franciscohansen.chat.client.interfaces.IChatScreen;
import br.com.franciscohansen.chat.client.interfaces.IClientCallback;
import br.com.franciscohansen.chat.client.interfaces.IClientThread;
import br.com.franciscohansen.chat.client.listener.AcaoListener;
import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;
import br.com.franciscohansen.chat.model.enums.EAcao;
import br.com.franciscohansen.chat.model.enums.ETipoMensagem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatForm implements IClientCallback {

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
    private final DefaultComboBoxModel<Usuario> comboBoxModel = new DefaultComboBoxModel<>();
    private final IAcaoListener listener;

    public ChatForm(Usuario usuario, Sala sala, IClientThread thread) throws IOException {
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
                .setChkPrivado(this.chkPrivate);
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

    public JPanel getPnlBackground() {
        return pnlBackground;
    }

    public void setPnlBackground(JPanel pnlBackground) {
        this.pnlBackground = pnlBackground;
    }

    public JPanel getPnlBottom() {
        return pnlBottom;
    }

    public void setPnlBottom(JPanel pnlBottom) {
        this.pnlBottom = pnlBottom;
    }

    public JTextField getEdtMsg() {
        return edtMsg;
    }

    public void setEdtMsg(JTextField edtMsg) {
        this.edtMsg = edtMsg;
    }

    public JComboBox getCbUsuario() {
        return cbUsuario;
    }

    public void setCbUsuario(JComboBox cbUsuario) {
        this.cbUsuario = cbUsuario;
    }

    public JButton getBtnEnviar() {
        return btnEnviar;
    }

    public void setBtnEnviar(JButton btnEnviar) {
        this.btnEnviar = btnEnviar;
    }

    public JTextArea getTxChat() {
        return txChat;
    }

    public void setTxChat(JTextArea txChat) {
        this.txChat = txChat;
    }

    public JCheckBox getChkPrivate() {
        return chkPrivate;
    }

    public void setChkPrivate(JCheckBox chkPrivate) {
        this.chkPrivate = chkPrivate;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public IClientThread getThread() {
        return thread;
    }

    public DefaultComboBoxModel<Usuario> getComboBoxModel() {
        return comboBoxModel;
    }
}
