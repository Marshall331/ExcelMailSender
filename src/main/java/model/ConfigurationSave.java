package model;

import java.io.Serializable;

import application.tools.ConfigurationManager;

public class ConfigurationSave implements Serializable {

    private static final long serialVersionUID = 1L;

    // public String host;
    // public int port;
    // public boolean authentification;
    // public boolean tlsenable;
    // public String mail;
    // public String password;

    public ServerBaseConfiguration serverConf;
    public boolean isConfOk;

    public String pathFilexlsx;
    public String pathFilepdf;

    public int columnIndex;
    public int lineStartIndex;
    public int lineEndIndex;

    public String mailSubject;
    public String mailContent;

    public ConfigurationSave() {
        this.serverConf = new ServerBaseConfiguration();

        this.pathFilexlsx = "";
        this.pathFilepdf = "";

        this.columnIndex = 0;
        this.lineStartIndex = 0;
        this.lineEndIndex = 0;

        this.mailSubject = "";
        this.mailContent = "";

        ConfigurationManager.saveConf(this);
    }

    // public void setNewConf(String _host, int _port, boolean _authentification, boolean _tlsenable, String _mail,
    //         String _password, String _pathFilexlsx, String _pathFilepdf, int _columnIndex, int _lineStartIndex,
    //         int _lineEndIndex, String _mailSubject, String _mailContent, Boolean _isConfOk) {
    //     // this.serverConf.host = _host;
    //     // this.serverConf.port = _port;
    //     // this.serverConf.authentification = _authentification;
    //     // this.serverConf.tlsenable = _tlsenable;
    //     // this.serverConf.mail = _mail;
    //     // this.serverConf.password = _password;
    //     this.serverConf = new ServerBaseConfiguration(_host, _port, _authentification, _tlsenable, _mail,
    //             _password);

    //     this.pathFilexlsx = _pathFilexlsx;
    //     this.pathFilepdf = _pathFilepdf;

    //     this.columnIndex = _columnIndex;
    //     this.lineStartIndex = _lineStartIndex;
    //     this.lineEndIndex = _lineEndIndex;

    //     this.mailSubject = _mailSubject;
    //     this.mailContent = _mailContent;

    //     this.isConfOk = _isConfOk;
        
    //     ConfigurationManager.saveConf(this);
    // }
}
