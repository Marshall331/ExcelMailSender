package model;

import java.io.Serializable;
import java.util.Stack;

import application.tools.ConfigurationManager;

public class ConfigurationSave implements Serializable {

    private static final long serialVersionUID = 1L;

    public ServerBaseConfiguration serverConf;
    public boolean isConfOk;

    public String pathFilexlsx;
    public Stack<String> pathFilepdf;

    public int columnIndex;
    public int lineStartIndex;
    public int lineEndIndex;

    public String mailSubject;
    public String mailContent;

    public ConfigurationSave() {
        this.serverConf = new ServerBaseConfiguration();

        this.pathFilexlsx = "";
        this.pathFilepdf = new Stack<>();

        this.columnIndex = 0;
        this.lineStartIndex = 0;
        this.lineEndIndex = 0;

        this.mailSubject = "";
        this.mailContent = "";

        ConfigurationManager.saveConf(this);
    }
}