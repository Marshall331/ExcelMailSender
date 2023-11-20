package model;

import java.io.Serializable;
import java.util.Stack;

/**
 * Serializable class representing a configuration for email sending.
 * Contains various settings such as server configuration, file paths,
 * email content, and indices.
 */
public class ConfigurationSave implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Server configuration settings. */
    public ServerBaseConfiguration serverConf;

    /** Flag indicating whether the configuration is valid. */
    public boolean isConfOk;

    /** Path to the XLSX file. */
    public String pathFilexlsx;

    /** Paths to PDF files stored in a stack. */
    public Stack<String> pathFilepdf;

    /** Index of the column. */
    public int columnIndex;

    /** Start index of the line. */
    public int lineStartIndex;

    /** End index of the line. */
    public int lineEndIndex;

    /** Subject of the email. */
    public String mailSubject;

    /** Content of the email. */
    public String mailContent;

    /**
     * Constructor for ConfigurationSave class.
     * Initializes default values and saves the configuration.
     */
    public ConfigurationSave() {
        // Initializing default values for configuration settings
        this.serverConf = new ServerBaseConfiguration();
        this.pathFilexlsx = "";
        this.pathFilepdf = new Stack<>();
        this.columnIndex = 0;
        this.lineStartIndex = 0;
        this.lineEndIndex = 0;
        this.mailSubject = "";
        this.mailContent = "";

        // Saving the configuration 
        SaveManagement.saveConf(this);
    }
}