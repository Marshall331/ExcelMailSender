package model;

import java.io.Serializable;

public class ServerBaseConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    public String host;
    public int port;
    public boolean authentification;
    public boolean tlsenable;
    public String mail;
    public String password;

    public ServerBaseConfiguration() {
        this.host = "";
        this.port = 0;
        this.authentification = false;
        this.tlsenable = false;
        this.mail = "";
        this.password = "";
    }

    public ServerBaseConfiguration(String _host, int _port, boolean _authentification, boolean _tlsenable, String _mail,
            String _password) {
        this.host = _host;
        this.port = _port;
        this.authentification = _authentification;
        this.tlsenable = _tlsenable;

        this.mail = _mail;
        this.password = _password;
    }

    public static boolean isSameConf(ServerBaseConfiguration _previousConf, ServerBaseConfiguration _newConf) {
        return (_previousConf.host.equals(_newConf.host) && _previousConf.port == _newConf.port
                && _previousConf.authentification == _newConf.authentification
                && _previousConf.tlsenable == _newConf.tlsenable && _previousConf.mail.equals(_newConf.mail)
                && _previousConf.password.equals(_newConf.password));
    }
}
