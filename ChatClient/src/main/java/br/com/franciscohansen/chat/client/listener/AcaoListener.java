package br.com.franciscohansen.chat.client.listener;

import br.com.franciscohansen.chat.client.interfaces.IAcaoListener;
import br.com.franciscohansen.chat.client.interfaces.IClientThread;
import br.com.franciscohansen.chat.client.interfaces.IListenerCallback;
import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Mensagem;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;
import br.com.franciscohansen.chat.model.enums.EAcao;
import br.com.franciscohansen.chat.model.enums.ETipoMensagem;
import com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AcaoListener implements IAcaoListener, ActionListener {

    private JFrame frame;
    private Usuario usuario;
    private Sala sala;
    private JTextArea tx;
    private JComboBox<Usuario> cb;
    private JList lst;
    private IClientThread thd;
    private JButton btnEnviar;
    private JCheckBox chkPrivado;
    private JTextField edtMsg;
    private IListenerCallback callback;


    private final DefaultListModel<Sala> salaModel = new DefaultListModel<>();
    private final DefaultComboBoxModel<Usuario> usuarioModel = new DefaultComboBoxModel<>();


    @Override
    public IAcaoListener setCallback(IListenerCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public IAcaoListener setFrame(JFrame frame) {
        this.frame = frame;
        return this;
    }

    @Override
    public IAcaoListener setUsuario(Usuario u) {
        this.usuario = u;
        return this;
    }

    @Override
    public IAcaoListener setSala(Sala s) {
        this.sala = s;
        return this;
    }

    @Override
    public IAcaoListener setTextArea(JTextArea tx) {
        this.tx = tx;
        return this;
    }

    @Override
    public IAcaoListener setComboUsers(JComboBox cb) {
        this.cb = cb;
        this.cb.setModel(usuarioModel);
        return this;
    }

    @Override
    public IAcaoListener setListSalas(JList lst) {
        this.lst = lst;
        this.lst.setModel(salaModel);
        return this;
    }

    @Override
    public IAcaoListener setBtnEnviar(JButton btn) {
        this.btnEnviar = btn;
        this.btnEnviar.addActionListener(this);
        return this;
    }

    @Override
    public IAcaoListener setChkPrivado(JCheckBox chk) {
        this.chkPrivado = chk;
        return this;
    }

    @Override
    public IAcaoListener setEdtMsg(JTextField edt) {
        this.edtMsg = edt;
        return this;
    }

    @Override
    public IAcaoListener setClientThread(IClientThread clientThread) {
        this.thd = clientThread;
        return this;
    }

    private void addStringToText(String string) {
        tx.append(string + "\n");
    }

    @Override
    public void actionPerformed(Acao acao) {
        switch (acao.getTipoAcao()) {
            case LOGIN:
            case LOGIN_OK: {
                try {
                    thd.enviaAcao(new Acao(EAcao.LISTA_USUARIOS));
                    thd.enviaAcao(new Acao(EAcao.LISTA_SALAS));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case LOGOUT: {
                Usuario u = (Usuario) acao.getObjetoAcao();
                addStringToText("O Usuário " + u.getNick() + " saiu do chat");
                try {
                    thd.enviaAcao(new Acao(EAcao.LISTA_USUARIOS));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case BANIR: {
                Usuario u = (Usuario) acao.getObjetoAcao();
                if (u.equals(this.usuario)) {
                    JOptionPane.showMessageDialog(null, "Você foi banido pelo administrador!");
                    System.exit(1);
                } else {
                    addStringToText("O usuário " + u.getNick() + " foi banido");
                }
                break;
            }
            case MENSAGEM: {
                addMessage((Mensagem) acao.getObjetoAcao());
                break;
            }
            case ENTRA_SALA: {
                break;
            }
            case SAI_SALA: {
                break;
            }
            case CRIA_SALA:
            case LISTA_SALAS: {
                atualizaSalas((List<Sala>) acao.getObjetoAcao());
                break;
            }
            case EXCLUI_SALA:{
                Sala sala = (Sala) acao.getObjetoAcao();
                if( sala.equals( this.sala ) ){
                    JOptionPane.showMessageDialog(null, "A sala atual foi removida pelo administrador!");
                    if( this.callback != null ){
                        this.callback.setDisconnected(true);
                    }
                    this.frame.dispose();
                }
                try {
                    this.thd.enviaAcao(new Acao(EAcao.LISTA_SALAS));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case ERRO: {
                String errorMessage = acao.getObjetoAcao().toString();
                JOptionPane.showMessageDialog(null, errorMessage, "Erro", JOptionPane.ERROR_MESSAGE);
                break;
            }
            case LISTA_USUARIOS: {
                if (acao.getSala().equals(this.sala)) {
                    atualizaUsuarios((List<Usuario>) acao.getObjetoAcao());
                }
                break;
            }
        }
    }

    private void atualizaSalas(List<Sala> salas) {
        salaModel.clear();
        for (Sala sala : salas) {
            salaModel.addElement(sala);
        }
    }

    private void atualizaUsuarios(List<Usuario> usuarios) {
        usuarioModel.removeAllElements();
        usuarios.remove(this.usuario);
        usuarioModel.addElement(new Usuario("TODOS"));
        for (Usuario u : usuarios) {
            usuarioModel.addElement(u);
        }
    }

    private void addMessage(Mensagem msg) {
        if (!msg.getSala().equals(this.sala)) {
            return;
        }
        if (msg.getTipoMensagem().equals(ETipoMensagem.USUARIO_PRIVADA) &&
                !msg.getUsuarioPara().equals(this.usuario) &&
                !msg.getUsuarioDe().equals(this.usuario)) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        String line = sdf.format(Calendar.getInstance().getTime());
        if (!msg.getTipoMensagem().equals(ETipoMensagem.ADMIN)) {
            line += " - " + msg.getUsuarioDe().getNick() + " diz";
        }
        if (!msg.getTipoMensagem().equals(ETipoMensagem.TODOS) &&
                msg.getUsuarioPara() != null &&
                msg.getUsuarioPara().getNick() != null &&
                !msg.getUsuarioPara().getNick().isEmpty()) {
            line += " para " + msg.getUsuarioPara().getNick();
        }
        if (msg.getTipoMensagem().equals(ETipoMensagem.USUARIO_PRIVADA)) {
            line += " [PRIVADO]";
        }
        line += ":\n" + msg.getMensagem();
        addStringToText(line);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (edtMsg.getText() == null || edtMsg.getText().isEmpty()) {
            return;
        }

        Mensagem msg = new Mensagem();
        msg.setMensagem(edtMsg.getText());
        msg.setUsuarioDe(this.usuario);
        msg.setSala(this.sala);
        Usuario usuario = (Usuario) cb.getSelectedItem();
        if (usuario != null && !usuario.getNick().equalsIgnoreCase("TODOS")) {
            msg.setUsuarioPara(usuario);
            msg.setTipoMensagem(ETipoMensagem.USUARIO);
        } else {
            msg.setTipoMensagem(ETipoMensagem.TODOS);
        }
        if (chkPrivado.isSelected() && msg.getTipoMensagem().equals(ETipoMensagem.USUARIO)) {
            msg.setTipoMensagem(ETipoMensagem.USUARIO_PRIVADA);
        }
        thd.enviaMensagem(msg);

        edtMsg.setText("");
    }
}
