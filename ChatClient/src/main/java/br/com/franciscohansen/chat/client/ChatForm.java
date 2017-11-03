package br.com.franciscohansen.chat.client;

import br.com.franciscohansen.chat.client.interfaces.IChatScreen;
import br.com.franciscohansen.chat.client.interfaces.IClientCallback;
import br.com.franciscohansen.chat.client.interfaces.IClientThread;
import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;
import br.com.franciscohansen.chat.model.enums.ETipoMensagem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatForm implements ActionListener, IClientCallback, IChatScreen {

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

    public ChatForm(Usuario usuario, Sala sala, IClientThread thread) {
        this.usuario = usuario;
        this.sala = sala;
        this.thread = thread;
        this.cbUsuario.setModel(this.comboBoxModel);
    }

    @Override
    public void addMessage(Mensagem msg) {
        if (ETipoMensagem.USUARIO_PRIVADA.equals(msg.getTipoMensagem()) &&
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
        txChat.append(line);
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

    }

    @Override
    public Sala getSala() {
        return this.sala;
    }

    @Override
    public void chamaAcao(Acao acao) {
        switch (acao.getTipoAcao()) {
            case MENSAGEM: {
                Mensagem msg = (Mensagem) acao.getObjetoAcao();
                if (msg.getSala().equals(getSala())) {
                    addMessage(msg);
                }
                break;
            }
            case LISTA_USUARIOS: {
                List<Usuario> usuarios = (List<Usuario>) acao.getObjetoAcao();
                atualizaUsuarios(usuarios);
                break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((edtMsg.getText() == null) || edtMsg.getText().isEmpty()) {
            return;
        }

        Mensagem msg = new Mensagem();
        msg.setMensagem(edtMsg.getText());
        msg.setUsuarioDe(this.usuario);
        msg.setSala(getSala());
        Usuario usuario = (Usuario) cbUsuario.getSelectedItem();
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
