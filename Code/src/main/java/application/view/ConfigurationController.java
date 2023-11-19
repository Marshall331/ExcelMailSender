package application.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import application.control.Configuration;
import application.control.SendMails;
import application.tools.AlertUtilities;
import application.tools.StageManagement;
import application.visualEffects.Animations;
import application.visualEffects.Style;
import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ConfigurationSave;
import model.FileReader;
import model.MailSender;
import model.SaveManagement;
import model.ServerBaseConfiguration;

/**
 * Controller class managing the configuration view.
 */
public class ConfigurationController {

    // Reference to the Configuration view
    private Configuration confView;

    // Reference to the primary stage
    private Stage primaryStage;

    // Configuration saves
    private ConfigurationSave oldConfiguration;
    private ConfigurationSave newConfiguration;

    // FXML elements:
    @FXML
    private TextArea txtHost; // TextArea for entering the email host.

    @FXML
    private ImageView imgUndefinedHost; // ImageView to indicate an undefined host.

    @FXML
    private TextArea txtPort; // TextArea for entering the email port.

    @FXML
    private ImageView imgUndefinedPort; // ImageView to indicate an undefined port.

    @FXML
    private CheckBox cbAuth; // CheckBox for email authentication.

    @FXML
    private CheckBox cbtls; // CheckBox for enabling TLS.

    @FXML
    private TextArea txtMail; // TextArea for entering the email address.

    @FXML
    private ImageView imgUndefinedMail; // ImageView to indicate an undefined email address.

    @FXML
    private TextArea txtPassword; // TextArea for entering the email password.

    @FXML
    private ImageView imgUndefinedPasswd; // ImageView to indicate an undefined password.

    @FXML
    private Button buttConnectionTest; // Button to test email connection.
    private final Tooltip tooltipTestConnection = new Tooltip("Envoi un mail de test.");

    @FXML
    private ImageView loadingIcon; // ImageView for loading icon during tasks.
    private RotateTransition loadingIconAnimation;

    @FXML
    private TextArea txtPathxlsx; // TextArea for entering the path to an Excel file.

    @FXML
    private Button buttExcelFileTest; // Button to test an Excel file.
    private final Tooltip tooltipTestExcelFile = new Tooltip(
            "Permet de récupérer la valeur de la case à la colonne et ligne de début entré.");

    @FXML
    private TextArea txtPathpdf; // TextArea for entering the path to a PDF file.

    @FXML
    private Button buttSelectExcelFile; // Button to select an Excel file.

    @FXML
    private Button buttDeleteExcelFile; // Button to delete the Excel File path.
    @FXML
    private Button buttDeletePDFFile; // Button to delete the PDF File path.
    private final Tooltip tooltipDeleteFile = new Tooltip("Supprime le(s) fichier(s)");

    @FXML
    private Button buttSelectPDFFile; // Button to select a PDF file.

    @FXML
    private TextArea txtColumnIndex; // TextArea for entering the column index.

    @FXML
    private TextArea txtLineStartIndex; // TextArea for entering the start line index.

    @FXML
    private TextArea txtLineEndIndex; // TextArea for entering the end line index.

    @FXML
    private Label labPDFFileCount; // Label to display the count of PDF files.

    @FXML
    private TextArea txtMailSubject; // TextArea for entering the email subject.

    @FXML
    private TextArea txtMailContent; // TextArea for entering the email content.

    @FXML
    private Button buttStart; // Button to initiate a task.

    // Task for sending mails
    private Task<Void> sendingTask;

    // FileChoosers for selecting files
    private FileChooser excelFile;
    private FileChooser pdfFiles;

    // Boolean properties for field validations
    private BooleanProperty hostIsFilled = new SimpleBooleanProperty();
    private BooleanProperty portIsFilled = new SimpleBooleanProperty();
    private BooleanProperty mailIsFilled = new SimpleBooleanProperty();
    private BooleanProperty passwdIsFilled = new SimpleBooleanProperty();

    private BooleanProperty minConfIsFilled = new SimpleBooleanProperty();

    /**
     * Initializes the context for the configuration view.
     *
     * @param _primaryStage The primary stage of the application.
     * @param _confView     The configuration view.
     */
    public void initContext(Stage _primaryStage, Configuration _confView) {
        this.confView = _confView;
        this.primaryStage = _primaryStage;
        this.oldConfiguration = SaveManagement.loadConf();
        this.newConfiguration = SaveManagement.loadConf();

        this.initMinConfIsFilledObserver();
        this.initViewElements();
        this.initTestConnectionTask();
    }

    /**
     * Initialize observer for minimal configuration validation.
     */
    private void initMinConfIsFilledObserver() {
        BooleanBinding combinedBinding = Bindings.and(hostIsFilled, portIsFilled);
        combinedBinding = Bindings.and(combinedBinding, mailIsFilled);
        combinedBinding = Bindings.and(combinedBinding, passwdIsFilled);

        this.minConfIsFilled.bind(combinedBinding);

        this.hostIsFilled.set(true);
        this.portIsFilled.set(true);
        this.mailIsFilled.set(true);
        this.passwdIsFilled.set(true);
    }

    /**
     * Displays the primary stage for the configuration view.
     */
    public void displayDialog() {
        primaryStage.show();
    }

    /**
     * Initializes the view elements and their respective functionalities.
     */
    private void initViewElements() {
        // Sets action on closing the primary stage
        this.primaryStage.setOnCloseRequest(e -> {
            this.doLeave();
        });

        // Tooltip settings and installations
        this.tooltipTestConnection.setStyle("-fx-font-size: 18px;");
        this.tooltipTestConnection.setShowDelay(Duration.ZERO);
        Tooltip.install(this.buttConnectionTest, this.tooltipTestConnection);

        this.tooltipDeleteFile.setStyle("-fx-font-size: 18px;");
        this.tooltipDeleteFile.setShowDelay(Duration.ZERO);
        Tooltip.install(this.buttDeleteExcelFile, this.tooltipDeleteFile);
        Tooltip.install(this.buttDeletePDFFile, this.tooltipDeleteFile);

        this.tooltipTestExcelFile.setStyle("-fx-font-size: 18px;");
        this.tooltipTestExcelFile.setShowDelay(Duration.ZERO);
        Tooltip.install(this.buttExcelFileTest, this.tooltipTestExcelFile);

        // Initialize listeners for text areas, file choosers, and other elements
        this.initTxtAreaListeners();
        this.initFileChoosers();
        this.setElementsByConf();
    }

    /**
     * Checks if the minimum configuration is correct before proceeding.
     *
     * @return true if the minimum configuration is correct, false otherwise.
     */
    private boolean checkMinConfIsCorrect() {
        if (this.sendingTask != null && this.sendingTask.isRunning()) {
            // Display alert for ongoing test
            AlertUtilities.showAlert(this.primaryStage, "Erreur.", "Un test est déjà en cours. Veuillez patienter.",
                    null,
                    AlertType.INFORMATION);
        } else {
            if (this.oldConfiguration.isConfOk) {
                try {
                    // Check if the configuration is the same as the existing one and if minimum
                    // configuration is filled
                    if (ServerBaseConfiguration.isSameConf(this.oldConfiguration.serverConf,
                            this.newConfiguration.serverConf) && this.minConfIsFilled.getValue()) {
                        this.newConfiguration.isConfOk = true;
                        setNewIcon("SuccesIcon.png");
                        return true;
                    } else {
                        this.newConfiguration.isConfOk = false;
                        setNewIcon("FailedIcon.png");
                    }
                } catch (Exception e) {
                    this.newConfiguration.isConfOk = false;
                    setNewIcon("FailedIcon.png");
                }
            } else {
                this.newConfiguration.isConfOk = false;
                setNewIcon("FailedIcon.png");
            }
        }
        return false;
    }

    /**
     * Sets the elements in the configuration view based on the existing
     * configuration data.
     * Also performs validations for certain fields.
     */
    private void setElementsByConf() {
        if (this.oldConfiguration.serverConf.host.length() > 0) {
            // Set host text if available; otherwise, set undefined style and show icon
            this.txtHost.setText(this.oldConfiguration.serverConf.host);
        } else {
            Style.setUndefinedTextAreaStyle(this.txtHost);
            imgUndefinedHost.setVisible(true);
        }
        // Similar logic for other fields...
        if (this.oldConfiguration.serverConf.port >= 0) {
            this.txtPort.setText("" + this.oldConfiguration.serverConf.port);
        } else {
            Style.setUndefinedTextAreaStyle(this.txtHost);
            imgUndefinedPort.setVisible(true);
        }
        if (this.oldConfiguration.serverConf.authentification) {
            this.cbAuth.setSelected(true);
        } else {
            this.cbAuth.setSelected(false);
        }
        if (this.oldConfiguration.serverConf.tlsenable) {
            this.cbtls.setSelected(true);
        } else {
            this.cbtls.setSelected(false);
        }
        if (this.oldConfiguration.serverConf.mail.length() > 0) {
            this.txtMail.setText(this.oldConfiguration.serverConf.mail);
        } else {
            Style.setUndefinedTextAreaStyle(this.txtMail);
            imgUndefinedMail.setVisible(true);
        }
        if (this.oldConfiguration.serverConf.password.length() > 0) {
            this.txtPassword.setText(this.oldConfiguration.serverConf.password);
        } else {
            Style.setUndefinedTextAreaStyle(this.txtPassword);
            imgUndefinedPasswd.setVisible(true);
        }
        if (this.oldConfiguration.columnIndex > 0) {
            this.txtColumnIndex.setText("" + this.oldConfiguration.columnIndex);
        }
        if (this.oldConfiguration.lineStartIndex > 0) {
            this.txtLineStartIndex.setText("" + this.oldConfiguration.lineStartIndex);
        }
        if (this.oldConfiguration.lineEndIndex > 0) {
            this.txtLineEndIndex.setText("" + this.oldConfiguration.lineEndIndex);
        }
        if (this.oldConfiguration.pathFilexlsx.length() > 0) {
            this.txtPathxlsx.setText("" + this.oldConfiguration.pathFilexlsx);
        }
        if (this.oldConfiguration.pathFilepdf.size() > 0) {
            this.txtPathpdf.setText("" + this.oldConfiguration.pathFilepdf);
            this.labPDFFileCount.setText("" + this.oldConfiguration.pathFilepdf.size());
        } else {
            this.labPDFFileCount.setText("" + 0);
        }
        if (this.oldConfiguration.mailSubject.length() > 0) {
            this.txtMailSubject.setText(this.oldConfiguration.mailSubject);
        }
        if (this.oldConfiguration.mailContent.length() > 0) {
            this.txtMailContent.setText(this.oldConfiguration.mailContent);
        }
        // Check for correctness of minimum configuration
        this.checkMinConfIsCorrect();
    }

    /**
     * Initializes the task for testing the connection to the mail server.
     * Manages the task's state and corresponding actions.
     */
    private void initTestConnectionTask() {
        this.sendingTask = new Task<Void>() {
            // Overrides the call method to execute the test connection task
            @Override
            protected Void call() throws Exception {
                try {
                    // Initiates the test connection with the provided mail server configurations
                    disableMinConfWhileTest(true);
                    newConfiguration.isConfOk = MailSender.sendingTest(newConfiguration.serverConf.mail,
                            newConfiguration.serverConf.password, newConfiguration.serverConf.host,
                            newConfiguration.serverConf.port, newConfiguration.serverConf.authentification,
                            newConfiguration.serverConf.tlsenable);
                    disableMinConfWhileTest(false);
                } catch (Exception e) {
                    cancel();
                }
                return null;
            }
        };
        // Handles task success scenario
        this.sendingTask.setOnSucceeded(e -> {
            // Stops loading animation and displays appropriate alerts based on the
            // connection status
            this.loadingIconAnimation.stop();
            if (this.newConfiguration.isConfOk) {
                this.saveConf();
                this.setNewIcon("SuccesIcon.png");
                AlertUtilities.showAlert(this.primaryStage, "Connexion établie.",
                        "Connexion réussie !", "La connexion au service de messagerie a été établie.",
                        AlertType.INFORMATION);
            } else {
                this.setNewIcon("FailedIcon.png");
                AlertUtilities.showAlert(this.primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                        "Veuillez saisir les paramètres corrects pour votre serveur de messagerie.", AlertType.ERROR);
            }
        });
        // Handles task failure scenario
        this.sendingTask.setOnFailed(e -> {
            this.loadingIconAnimation.stop();
            this.setNewIcon("FailedIcon.png");
            Animations.stopLoadingAnimation(this.loadingIcon, this.loadingIconAnimation);
            AlertUtilities.showAlert(this.primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                    "Veuillez saisir les paramètres corrects pour votre serveur de messagerie.", AlertType.ERROR);
        });
        // Handles task cancellation
        this.sendingTask.setOnCancelled(e -> {
            this.loadingIconAnimation.stop();
        });
        // Sets up actions when the task is running
        this.sendingTask.setOnRunning(e -> {
            this.setNewIcon("LoadingIcon.jpg");
            this.loadingIconAnimation = Animations.startLoadingAnimation(this.loadingIcon);
        });
    }

    /**
     * Disables specified elements during the test connection process.
     *
     * @param _disable Boolean value to enable or disable elements.
     */
    private void disableMinConfWhileTest(boolean _disable) {
        // Disables elements during test connection
        this.txtHost.setDisable(_disable);
        this.txtPort.setDisable(_disable);
        this.cbAuth.setDisable(_disable);
        this.cbtls.setDisable(_disable);
        this.txtMail.setDisable(_disable);
        this.txtPassword.setDisable(_disable);
    }

    /**
     * Initiates the process of testing the connection to the mail server.
     * Handles scenarios based on ongoing or incomplete tests.
     */
    @FXML
    private void doConnectionTest() {
        if (this.sendingTask.isRunning()) {
            // Displays an alert if a test is already in progress
            AlertUtilities.showAlert(this.primaryStage, "Erreur.", "Un test est déjà en cours. Veuillez patienter.",
                    "Veuillez attendre que le test en cours se termine.", AlertType.INFORMATION);
        } else {
            if (this.minConfIsFilled.getValue()) {
                // Initiates test connection task if minimum configuration requirements are met
                this.initTestConnectionTask();
                new Thread(this.sendingTask).start();
                this.saveConf();
            } else {
                // Displays an alert if required fields for the test are incomplete
                AlertUtilities.showAlert(this.primaryStage, "Opération impossible.",
                        "Impossible d'initier le test de connexion.",
                        "Veuillez remplir tous les champs requis pour le test ! (marqués en rouge)",
                        AlertType.INFORMATION);
            }
        }
    }

    /**
     * Initiates the process to start mail transmission.
     * Checks minimum configuration correctness and alerts if the connection test is
     * ongoing.
     */
    @FXML
    private void doStart() {
        this.checkMinConfIsCorrect();
        if (this.sendingTask.isRunning()) {
            // Displays an alert if a connection test is in progress during mail
            // transmission start
            AlertUtilities.showAlert(this.primaryStage, "Opération impossible.",
                    "Connexion au serveur de messagerie.",
                    "Le test de connexion au serveur est en cours. Veuillez patienter.",
                    AlertType.INFORMATION);
        } else {
            if (this.newConfiguration.isConfOk) {
                // Saves configuration and initiates sending mails upon successful connection
                // test
                this.saveConf();
                SendMails sendMails = new SendMails(primaryStage);
                sendMails.showStage();
            } else {
                // Displays an alert if the connection test hasn't been performed before mail
                // transmission
                AlertUtilities.showAlert(this.primaryStage, "Opération impossible.",
                        "Veuillez d'abord tester la connexion au serveur de messagerie !",
                        "Veuillez effectuer le test de connexion au serveur de messagerie en premier !",
                        AlertType.ERROR);
            }
        }
    }

    /**
     * Performs necessary actions upon exiting the configuration view.
     * Cancels ongoing tasks, saves configuration, and closes the application.
     */
    @FXML
    private void doLeave() {
        if (this.sendingTask.isRunning()) {
            this.sendingTask.cancel(true);
        }
        this.saveConf();
        this.primaryStage.close();
        System.exit(0);
    }

    /**
     * Resets the configuration to default settings after user confirmation.
     * Initiates a new configuration view if the user confirms the reset action.
     */
    @FXML
    private void doReset() {
        if (AlertUtilities.confirmYesCancel(this.primaryStage, "Reset?",
                "Do you really want to reset the configuration?", null, AlertType.CONFIRMATION)) {
            this.newConfiguration = new ConfigurationSave();
            this.confView = new Configuration();
            this.confView.start(this.primaryStage);
        }
    }

    /**
     * Saves the updated configuration data.
     * Loads the saved configuration for further use.
     */
    private void saveConf() {
        SaveManagement.saveConf(this.newConfiguration);
        this.oldConfiguration = SaveManagement.loadConf();
    }

    /**
     * Sets a new icon for the loading icon element based on the provided image
     * name.
     * Updates the image and makes the loading icon visible.
     *
     * @param _imgName Name of the image file to be displayed.
     */
    private void setNewIcon(String _imgName) {
        this.loadingIcon.setRotate(0);
        String imagePath = "/application/view/images/" + _imgName;
        this.loadingIcon.setImage(new Image(getClass().getResourceAsStream(imagePath),
                this.loadingIcon.getFitWidth(), this.loadingIcon.getFitHeight(), true,
                true));
        this.loadingIcon.setVisible(true);
    }

    /**
     * Retrieves a cell value from the specified Excel file based on provided column
     * and line indices.
     * Shows alerts for various scenarios such as empty file path, missing Excel
     * file, or incorrect indices.
     */
    @FXML
    private void doGetCellValue() {
        if (this.txtPathxlsx.getText().trim().isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Opération échoué.", "Aucun fichier sélectionné.",
                    "Merci de sélectionner un fichier avant de procéder au test.", AlertType.INFORMATION);
        } else {
            try {
                String value = FileReader.getCellValue(this.txtPathxlsx.getText(),
                        Integer.valueOf(this.txtColumnIndex.getText().trim()),
                        Integer.valueOf(this.txtLineStartIndex.getText().trim()));

                if (value.equals("")) {
                    AlertUtilities.showAlert(this.primaryStage, "Résultat vide.", "La valeur retournée est vide.",
                            "Assurez vous d'avoir correctement choisi la colonne et la ligne.", AlertType.INFORMATION);
                } else {
                    AlertUtilities.showAlert(this.primaryStage, "Résultat trouvé.",
                            "La valeur retournée est : " + value,
                            null,
                            AlertType.INFORMATION);
                }
            } catch (FileNotFoundException e) {
                AlertUtilities.showAlert(this.primaryStage, "Erreur", "Fichier Excel introuvable",
                        "Le fichier Excel spécifié n'a pas été trouvé, merci de réessayer avec le bon fichier .xlsx.\nCode d'erreur : "
                                + e,
                        AlertType.ERROR);
            } catch (NumberFormatException e) {
                AlertUtilities.showAlert(this.primaryStage, "Erreur", "Numéro de colonne ou de ligne incorrect !",
                        "Merci de vérifier le bon numéro de colonne et de ligne de la valeur à récupérer.\nCode d'erreur : "
                                + e,
                        AlertType.ERROR);
            } catch (IOException e) {
                AlertUtilities.showAlert(this.primaryStage, "Erreur", "Erreur lors de la lecture du fichier Excel",
                        "Quelque chose d'anormale s'est produit durant la lecture du fichier, merci de réessayer en vérifiant le type du fichier et le numéro de colonne / ligne saisi.\nCode d'erreur : "
                                + e,
                        AlertType.ERROR);
            } catch (Exception e) {
                AlertUtilities.showAlert(this.primaryStage, "Erreur", "Une erreur inattendue s'est produite",
                        "\"Quelque chose d'anormale s'est produit, merci de réessayer en vérifiant le type du fichier et le numéro de colonne / ligne saisi.\nCode d'erreur : "
                                + e,
                        AlertType.ERROR);
            }
        }
    }

    /**
     * Allows the user to choose an Excel file and updates the corresponding text
     * field with the file path.
     */
    @FXML
    private void doChooseExcelFile() {
        StageManagement.disableItems(this.primaryStage.getScene(), true);
        File selectedFile = excelFile.showOpenDialog(new Stage());
        if (selectedFile != null) {
            this.txtPathxlsx.setText(selectedFile.getAbsolutePath());
        }
        StageManagement.disableItems(this.primaryStage.getScene(), false);
    }

    /**
     * Allows the user to choose multiple PDF files and updates the UI accordingly.
     */
    @FXML
    private void doChoosePdfFiles() throws InterruptedException {
        StageManagement.disableItems(this.primaryStage.getScene(), true);
        List<File> selectedFiles = this.pdfFiles.showOpenMultipleDialog(this.primaryStage);
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            this.newConfiguration.pathFilepdf.clear();
            for (File file : selectedFiles) {
                this.newConfiguration.pathFilepdf.add(file.getAbsolutePath());
            }
            this.txtPathpdf.setText(selectedFiles.toString());
            this.labPDFFileCount.setText("" + this.newConfiguration.pathFilepdf.size());
        }

        StageManagement.disableItems(this.primaryStage.getScene(), false);
    }

    /**
     * Removes the selected Excel file path from the text field.
     */
    @FXML
    private void doRemoveExcelFile() {
        this.txtPathxlsx.setText("");
    }

    /**
     * Removes all selected PDF files and clears the corresponding text field.
     */
    @FXML
    private void doRemovePdflFile() {
        this.newConfiguration.pathFilepdf.clear();
        this.labPDFFileCount.setText("" + 0);
        this.txtPathpdf.setText("");
    }

    /**
     * Initializes the file choosers for Excel and PDF files with specific filters
     * and initial directories.
     */
    private void initFileChoosers() {
        this.excelFile = new FileChooser();
        this.excelFile.setTitle("Sélectionnez le fichier xlsx");
        this.excelFile.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File initialDirectory = new File(System.getProperty("user.home"));
        this.excelFile.setInitialDirectory(initialDirectory);

        this.pdfFiles = new FileChooser();
        this.pdfFiles.setTitle("Sélectionnez les fichiers PDF");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf");
        this.pdfFiles.getExtensionFilters().add(extFilter);
        this.pdfFiles.setInitialDirectory(initialDirectory);
    }

    /**
     * Checks if the given email address matches a specified pattern.
     * 
     * @param email The email address to be validated.
     * @return {@code true} if the email matches the specified pattern, otherwise
     *         {@code false}.
     */
    private boolean isMailCorrect(String _mail) {
        final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = EMAIL_PATTERN.matcher(_mail);
        return matcher.matches();
    }

    /**
     * Initializes listeners for various text areas in the configuration view.
     * This method listens for changes in different text areas and updates the
     * corresponding
     * properties in the configuration object ('newConfiguration').
     * It validates input data, checks email formats, and ensures fields are
     * properly filled.
     */
    private void initTxtAreaListeners() {
        txtHost.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                if (newValue.isEmpty() || newValue.length() < 1) {
                    hostIsFilled.setValue(false);
                    Style.setUndefinedTextAreaStyle(txtHost);
                    imgUndefinedHost.setVisible(true);

                } else {
                    hostIsFilled.setValue(true);
                    Style.resetTextAreaStyle(txtHost);
                    imgUndefinedHost.setVisible(false);
                }
                newConfiguration.serverConf.host = newValue;
                if (oldConfiguration.isConfOk) {
                    checkMinConfIsCorrect();
                }
            }
        });

        txtPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newConfiguration.serverConf.port = oldConfiguration.serverConf.port;
                newValue = newValue.trim();
                try {
                    newConfiguration.serverConf.port = Integer.valueOf(newValue);
                } catch (Exception e) {
                    System.out.println("test");
                    newConfiguration.serverConf.port = 0;
                    portIsFilled.setValue(false);
                    Style.setUndefinedTextAreaStyle(txtPort);
                    imgUndefinedPort.setVisible(true);
                }
                if (newValue.isEmpty() || newValue.length() < 1 || newConfiguration.serverConf.port <= 0) {
                    portIsFilled.setValue(false);
                    Style.setUndefinedTextAreaStyle(txtPort);
                    imgUndefinedPort.setVisible(true);
                } else {
                    portIsFilled.setValue(true);
                    Style.resetTextAreaStyle(txtPort);
                    imgUndefinedPort.setVisible(false);
                }
                if (oldConfiguration.isConfOk) {
                    checkMinConfIsCorrect();
                }
            }
        });

        this.cbAuth.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.newConfiguration.serverConf.authentification = newValue;
            if (oldConfiguration.isConfOk) {
                checkMinConfIsCorrect();
            }
        });

        this.cbtls.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.newConfiguration.serverConf.tlsenable = newValue;
            if (oldConfiguration.isConfOk) {
                checkMinConfIsCorrect();
            }
        });

        this.txtMail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                if (newValue.isEmpty() || !isMailCorrect(newValue)) {
                    mailIsFilled.setValue(false);
                    Style.setUndefinedTextAreaStyle(txtMail);
                    imgUndefinedMail.setVisible(true);
                } else {
                    mailIsFilled.setValue(true);
                    Style.resetTextAreaStyle(txtMail);
                    imgUndefinedMail.setVisible(false);
                }
                newConfiguration.serverConf.mail = newValue;
                if (oldConfiguration.isConfOk) {
                    checkMinConfIsCorrect();
                }
            }
        });

        txtPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                if (newValue.isEmpty() || newValue.length() < 1) {
                    passwdIsFilled.setValue(false);
                    Style.setUndefinedTextAreaStyle(txtPassword);
                    imgUndefinedPasswd.setVisible(true);
                } else {
                    passwdIsFilled.setValue(true);
                    Style.resetTextAreaStyle(txtPassword);
                    imgUndefinedPasswd.setVisible(false);
                }
                newConfiguration.serverConf.password = newValue;
                if (oldConfiguration.isConfOk) {
                    checkMinConfIsCorrect();
                }
            }
        });

        txtPathxlsx.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                newConfiguration.pathFilexlsx = newValue;
            }
        });

        txtColumnIndex.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                try {
                    newConfiguration.columnIndex = Integer.valueOf(newValue);
                } catch (Exception e) {
                    newConfiguration.columnIndex = oldConfiguration.columnIndex;
                }
            }
        });

        txtLineStartIndex.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                try {
                    newConfiguration.lineStartIndex = Integer.valueOf(newValue);
                } catch (Exception e) {
                    newConfiguration.lineStartIndex = oldConfiguration.lineStartIndex;
                }
            }
        });

        txtLineEndIndex.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                try {
                    newConfiguration.lineEndIndex = Integer.valueOf(newValue);
                } catch (Exception e) {
                    newConfiguration.lineEndIndex = oldConfiguration.lineEndIndex;
                }
            }
        });

        txtMailSubject.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                newConfiguration.mailSubject = newValue.trim();
            }
        });

        txtMailContent.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                newConfiguration.mailContent = newValue.trim();
            }
        });
    }
}