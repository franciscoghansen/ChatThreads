package br.com.franciscohansen.chat.client.interfaces;

import br.com.franciscohansen.chat.model.Acao;
import br.com.franciscohansen.chat.model.Sala;
import br.com.franciscohansen.chat.model.Usuario;

import javax.swing.*;

public interface IAcaoListener {

    IAcaoListener setUsuario( Usuario u );
    IAcaoListener setSala( Sala s );
    IAcaoListener setTextArea(JTextArea tx );
    IAcaoListener setComboUsers(JComboBox<Usuario> cb );
    IAcaoListener setListSalas(JList<Sala> lst );
    IAcaoListener setBtnEnviar( JButton btn );
    IAcaoListener setChkPrivado( JCheckBox chk );
    IAcaoListener setEdtMsg( JTextField edt );
    IAcaoListener setClientThread( IClientThread clientThread );

    void actionPerformed( Acao acao );


}
