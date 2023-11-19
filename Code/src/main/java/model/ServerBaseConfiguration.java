package model;

import java.io.Serializable;

/**
 * Serializable class representing configuration details.
 */
public class ServerBaseConfiguration implements Serializable {

    // Represents server configuration details.
    private static final long serialVersionUID = 1L;

    // The host address.
    public String host;

    // The port number.
    public int port;

    // Indicates whether authentication is enabled.
    public boolean authentification;

    // Indicates whether TLS is enabled.
    public boolean tlsenable;

    // The email address used for configuration.
    public String mail;

    // The password used for configuration.
    public String password;

    /**
     * Default constructor initializing fields to default values.
     */
    public ServerBaseConfiguration() {
        this.host = "";
        this.port = 0;
        this.authentification = false;
        this.tlsenable = false;
        this.mail = "";
        this.password = "";
    }

    /**
     * Parameterized constructor setting server configuration details.
     *
     * @param _host             The host address.
     * @param _port             The port number.
     * @param _authentification Boolean value indicating authentication status.
     * @param _tlsenable        Boolean value indicating TLS status.
     * @param _mail             The email address.
     * @param _password         The password.
     */
    public ServerBaseConfiguration(String _host, int _port, boolean _authentification, boolean _tlsenable, String _mail,
            String _password) {
        this.host = _host;
        this.port = _port;
        this.authentification = _authentification;
        this.tlsenable = _tlsenable;

        this.mail = _mail;
        this.password = _password;
    }

    /**
     * Checks if two configurations are the same.
     *
     * @param _previousConf The previous configuration to compare.
     * @param _newConf      The new configuration to compare.
     * @return True if configurations are the same, otherwise false.
     */
    public static boolean isSameConf(ServerBaseConfiguration _previousConf, ServerBaseConfiguration _newConf) {
        return (_previousConf.host.equals(_newConf.host) && _previousConf.port == _newConf.port
                && _previousConf.authentification == _newConf.authentification
                && _previousConf.tlsenable == _newConf.tlsenable && _previousConf.mail.equals(_newConf.mail)
                && _previousConf.password.equals(_newConf.password));
    }
}