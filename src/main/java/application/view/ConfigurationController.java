package application.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.control.Configuration;
import application.control.SendMails;
import application.tools.AlertUtilities;
import application.tools.Animations;
import application.tools.ConfigurationManager;
import application.tools.FileReader;
import application.tools.MailSender;
import application.tools.StyleManager;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ConfigurationSave;
import model.ServerBaseConfiguration;

public class ConfigurationController {

    Configuration mailAutoApp;
    private Stage primaryStage;
    ConfigurationSave oldConfiguration;
    ConfigurationSave newConfiguration;

    @FXML
    private TextArea txtHost;

    @FXML
    private ImageView imgUndefinedHost;

    @FXML
    private TextArea txtPort;

    @FXML
    private ImageView imgUndefinedPort;

    @FXML
    private CheckBox cbAuth;

    @FXML
    private CheckBox cbtls;

    @FXML
    private TextArea txtMail;

    @FXML
    private ImageView imgUndefinedMail;

    @FXML
    private TextArea txtPassword;

    @FXML
    private ImageView imgUndefinedPasswd;

    @FXML
    private Button buttConnectionTest;
    private Tooltip tooltipTestConnection = new Tooltip(
            "Merci de tester la connection au serveur de messagerie avant.");

    @FXML
    private ImageView loadingIcon;
    private RotateTransition loadingIconAnimation;

    @FXML
    private TextArea txtPathxlsx;

    @FXML
    private Button buttExcelFileTest;
    private Tooltip tooltipTestExcelFile = new Tooltip(
            "Merci de sélectionner un fichier avant de le tester.");

    @FXML
    private TextArea txtPathpdf;

    @FXML
    private Button btnFilexlsx;

    @FXML
    private Button btnFilepdf;

    @FXML
    private TextArea txtColumnIndex;

    @FXML
    private TextArea txtLineStartIndex;

    @FXML
    private TextArea txtLineEndIndex;

    @FXML
    private TextArea txtMailSubject;

    @FXML
    private TextArea txtMailContent;

    @FXML
    private Button buttStart;

    private Task<Void> sendingTask;
    private boolean isCurrentlyConnected;

    private BooleanProperty hostIsFilled = new SimpleBooleanProperty();
    private BooleanProperty portIsFilled = new SimpleBooleanProperty();
    private BooleanProperty mailIsFilled = new SimpleBooleanProperty();
    private BooleanProperty passwdIsFilled = new SimpleBooleanProperty();

    private BooleanProperty minConfIsFilled = new SimpleBooleanProperty();

    public void initContext(Stage _primaryStage, Configuration _mailAutoApp) {
        this.mailAutoApp = _mailAutoApp;
        this.primaryStage = _primaryStage;
        oldConfiguration = ConfigurationManager.loadConf();
        newConfiguration = oldConfiguration;

        this.initMinConfIsFilledObserver();
        this.initViewElements();
        this.initTestConnectionTask();
        this.updateViewElements();
    }

    private void initMinConfIsFilledObserver() {
        BooleanBinding combinedBinding = Bindings.and(hostIsFilled, portIsFilled);
        combinedBinding = Bindings.and(combinedBinding, mailIsFilled);
        combinedBinding = Bindings.and(combinedBinding, passwdIsFilled);

        minConfIsFilled.bind(combinedBinding);

        hostIsFilled.set(true);
        portIsFilled.set(true);
        mailIsFilled.set(true);
        passwdIsFilled.set(true);
    }

    public void displayDialog() {
        primaryStage.show();
    }

    private void initViewElements() {
        this.primaryStage.setOnCloseRequest(e -> {
            this.doLeave();
        });

        this.tooltipTestConnection.setShowDelay(Duration.ZERO);
        // Tooltip.install(this.buttStart, tooltipTestConnection);

        this.tooltipTestExcelFile.setShowDelay(Duration.ZERO);
        Tooltip.install(this.buttExcelFileTest, tooltipTestExcelFile);

        this.initListeners();
        this.initFileChoosers();
        this.setElementsByConf();
    }

    private void initListeners() {
        this.initButtonsListeners();
        this.initTxtAreaListeners();
    }

    private boolean checkMinConfIsCorrect() {
        if (this.oldConfiguration.isConfOk) {
            try {
                if (ServerBaseConfiguration.isSameConf(this.oldConfiguration.serverConf,
                        this.newConfiguration.serverConf)) {
                    setNewIcon("SuccesIcon.png");
                    return true;
                } else {
                    setNewIcon("FailedIcon.png");
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    private void setElementsByConf() {
        if (this.oldConfiguration.serverConf.host.length() > 0) {
            this.txtHost.setText(this.oldConfiguration.serverConf.host);
        } else {
            StyleManager.setUndefinedTextAreaStyle(this.txtHost);
        }
        if (this.oldConfiguration.serverConf.port > 0) {
            this.txtPort.setText("" + this.oldConfiguration.serverConf.port);
        } else {
            StyleManager.setUndefinedTextAreaStyle(this.txtHost);
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
            StyleManager.setUndefinedTextAreaStyle(this.txtMail);
        }
        if (this.oldConfiguration.serverConf.password.length() > 0) {
            this.txtPassword.setText(this.oldConfiguration.serverConf.password);
        } else {
            StyleManager.setUndefinedTextAreaStyle(this.txtPassword);
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
        if (this.oldConfiguration.pathFilepdf.length() > 0) {
            this.txtPathpdf.setText("" + this.oldConfiguration.pathFilepdf);
        } else {
            StyleManager.setUndefinedTextAreaStyle(txtPathxlsx);
        }
        if (this.oldConfiguration.mailSubject.length() > 0) {
            this.txtMailSubject.setText(this.oldConfiguration.mailSubject);
        }
        if (this.oldConfiguration.mailContent.length() > 0) {
            this.txtMailContent.setText(this.oldConfiguration.mailContent);
        }

        if (this.oldConfiguration.isConfOk) {
            setNewIcon("SuccesIcon.png");
        } else {
            setNewIcon("FailedIcon.png");
        }
    }

    private void initTestConnectionTask() {
        this.sendingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    isCurrentlyConnected = MailSender.sendingTest(txtMail.getText().trim(), txtPassword.getText(),
                            txtHost.getText().trim(), Integer.valueOf(txtPort.getText().trim()),
                            cbAuth.isSelected() == true ? true : false, cbtls.isSelected() == true ? true : false);
                } catch (Exception e) {
                    cancel();
                    System.out.println(e);
                }
                return null;
            }
        };
        this.sendingTask.setOnSucceeded(e -> {
            loadingIconAnimation.stop();
            if (isCurrentlyConnected) {
                this.newConfiguration.isConfOk = true;
                this.setNewIcon("SuccesIcon.png");
                AlertUtilities.showAlert(primaryStage, "Connexion établie.",
                        "Connexion réussie !", "La connexion au service de messagerie a bien été établie !",
                        AlertType.INFORMATION);
            } else {
                this.newConfiguration.isConfOk = false;
                this.setNewIcon("FailedIcon.png");
                AlertUtilities.showAlert(primaryStage, "Echec de la connexion.", "Echec de la connexion !",
                        "Merci de saisir les bon paramètres de votre messagerie.", AlertType.ERROR);
            }
        });
        this.sendingTask.setOnFailed(e -> {
            loadingIconAnimation.stop();
            this.setNewIcon("FailedIcon.png");
            Animations.stopLoadingAnimation(loadingIcon, this.loadingIconAnimation);
            AlertUtilities.showAlert(primaryStage, "Connexion établie.",
                    "Connexion réussie !", "La connexion au service de messagerie a bien été établie !",
                    AlertType.INFORMATION);
        });
        this.sendingTask.setOnCancelled(e -> {
            loadingIconAnimation.stop();
        });
        this.sendingTask.setOnRunning(e -> {
            this.setNewIcon("LoadingIcon.jpg");
            this.loadingIconAnimation = Animations.startLoadingAnimation(this.loadingIcon);
        });
    }

    @FXML
    private void doConnectionTest() {
        if (this.sendingTask.isRunning()) {
            AlertUtilities.showAlert(primaryStage, "Erreur.", "Un test est déjà en cours, veuillez attendre.", null,
                    AlertType.INFORMATION);
        } else {
            if (this.minConfIsFilled.getValue()) {
                this.initTestConnectionTask();
                new Thread(sendingTask).start();
            } else {
                AlertUtilities.showAlert(primaryStage, "Opération impossible.",
                        "Impossible de lancer le test de connexion.",
                        "Merci de compléter tous les champs requis pour le test !\n(en rouge)",
                        AlertType.INFORMATION);
            }
        }
    }

    @FXML
    private void doStart() {
        if (this.checkMinConfIsCorrect()) {
            this.saveNewConf();
            SendMails sM = new SendMails(primaryStage);
        } else {
            AlertUtilities.showAlert(primaryStage, "Opération impossible.",
                    "Veuillez vérifier la connextion au serveur de messagerie d'abord !",
                    "Merci d'effectuer le test de connexion au serveur de messagerie avant de passer à la suite.",
                    AlertType.ERROR);
        }
    }

    @FXML
    private void doLeave() {
        if (this.sendingTask.isRunning()) {
            this.sendingTask.cancel(true);
        }
        // System.out.println("FIN : " + this.newConfiguration.isConfOk);
        this.primaryStage.close();
        System.exit(0);
    }

    @FXML
    private void doReset() {
        if (AlertUtilities.confirmYesCancel(primaryStage, "Réinitialiser ?",
                "Voulez vous vraiment réinitialiser la configuration ?", null, AlertType.CONFIRMATION)) {
            this.newConfiguration = new ConfigurationSave();
            Configuration c = new Configuration(this.primaryStage);
        }
    }

    @FXML
    private void doSaveConf() {
        this.saveNewConf();
        AlertUtilities.showAlert(primaryStage, "Opération réussie.", "Sauvegarde effectué !",
                "La configuration a bien été sauvegardé.", AlertType.INFORMATION);
    }

    private void saveNewConf() {
        ConfigurationManager.saveConf(newConfiguration);
    }

    private void updateViewElements() {
    }

    private void setNewIcon(String _imgName) {
        this.loadingIcon.setRotate(0);
        String imagePath = "/application/view/images/" + _imgName;
        this.loadingIcon.setImage(new Image(getClass().getResourceAsStream(imagePath),
                this.loadingIcon.getFitWidth(), this.loadingIcon.getFitHeight(), true,
                true));
        this.loadingIcon.setVisible(true);
    }

    @FXML
    private void doGetCellValue() {
        if (this.txtPathxlsx.getText().trim().isEmpty()) {
            AlertUtilities.showAlert(primaryStage, "Opération échoué.", "Aucun fichier sélectionné.",
                    "Merci de sélectionner un fichier avant de procéder au test.", AlertType.INFORMATION);
        } else {
            try {
                String value = FileReader.getCellValue(this.txtPathxlsx.getText(),
                        Integer.valueOf(this.txtColumnIndex.getText().trim()),
                        Integer.valueOf(this.txtLineStartIndex.getText().trim()));

                if (value.equals("")) {
                    AlertUtilities.showAlert(primaryStage, "Résultat vide.", "La valeur retournée est vide.",
                            "Assurez vous d'avoir correctement choisi la colonne et la ligne.", AlertType.INFORMATION);
                } else {
                    AlertUtilities.showAlert(primaryStage, "Résultat trouvé.", "La valeur retournée est : " + value,
                            null,
                            AlertType.INFORMATION);
                }
            } catch (FileNotFoundException e) {
                AlertUtilities.showAlert(primaryStage, "Erreur", "Fichier Excel introuvable",
                        "Le fichier Excel spécifié n'a pas été trouvé, merci de réessayer avec le bon fichier .xlsx.\nCode d'erreur : "
                                + e.getMessage(),
                        AlertType.ERROR);
            } catch (NumberFormatException e) {
                AlertUtilities.showAlert(primaryStage, "Erreur", "Numéro de colonne ou de ligne incorrect !",
                        "Merci de vérifier le bon numéro de colonne et de ligne de la valeur à récupérer.\nCode d'erreur : "
                                + e.getMessage(),
                        AlertType.ERROR);
            } catch (IOException e) {
                AlertUtilities.showAlert(primaryStage, "Erreur", "Erreur lors de la lecture du fichier Excel",
                        "Quelque chose d'anormale s'est produit durant la lecture du fichier, merci de réessayer en vérifiant le type du fichier et le numéro de colonne / ligne saisi.\nCode d'erreur : "
                                + e.getMessage(),
                        AlertType.ERROR);
            } catch (Exception e) {
                AlertUtilities.showAlert(primaryStage, "Erreur", "Une erreur inattendue s'est produite",
                        "\"Quelque chose d'anormale s'est produit, merci de réessayer en vérifiant le type du fichier et le numéro de colonne / ligne saisi.\nCode d'erreur : "
                                + e.getMessage(),
                        AlertType.ERROR);
            }
        }
    }

    private void initFileChoosers() {
        FileChooser filexlsx = new FileChooser();
        filexlsx.setTitle("Sélectionnez le fichier xlsx");
        filexlsx.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File initialDirectory = new File(System.getProperty("user.home"));
        filexlsx.setInitialDirectory(initialDirectory);

        this.btnFilexlsx.setOnAction(e -> {
            File selectedFile = filexlsx.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                this.txtPathxlsx.setText(selectedFile.getAbsolutePath());
            }
        });

        FileChooser filepdf = new FileChooser();
        filepdf.setTitle("Sélectionnez le fichier pdf");
        filepdf.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        filepdf.setInitialDirectory(initialDirectory);

        this.btnFilepdf.setOnAction(e -> {
            File selectedFile = filepdf.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                this.txtPathpdf.setText(selectedFile.getAbsolutePath());
            }
        });
    }

    private boolean isMailCorrect(String _mail) {
        final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = EMAIL_PATTERN.matcher(_mail);
        return matcher.matches();
    }

    private void initButtonsListeners() {
        buttExcelFileTest.setOnMouseEntered(event -> {
            if (this.txtPathxlsx.getText().trim().isEmpty()) {
                if (this.txtPathxlsx.getText().length() == 0) {

                }
                tooltipTestExcelFile.show(buttExcelFileTest, event.getScreenX(), event.getScreenY());
            } else {
                tooltipTestExcelFile.hide();
            }
        });

        this.buttExcelFileTest.setOnMouseExited(event -> {
            tooltipTestConnection.hide();
        });
    }

    private void initTxtAreaListeners() {

        txtHost.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                if (oldConfiguration.isConfOk) {
                    checkMinConfIsCorrect();
                }
                if (newValue.isEmpty() || newValue.length() < 1) {
                    hostIsFilled.setValue(false);
                    StyleManager.setUndefinedTextAreaStyle(txtHost);
                    imgUndefinedHost.setVisible(true);

                } else {
                    hostIsFilled.setValue(true);
                    StyleManager.resetTextAreaStyle(txtHost);
                    imgUndefinedHost.setVisible(false);
                }
                newConfiguration.serverConf.host = newValue;
            }
        });

        txtPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newConfiguration.serverConf.port = oldConfiguration.serverConf.port;
                newValue = newValue.trim();
                if (oldConfiguration.isConfOk) {
                    checkMinConfIsCorrect();
                }
                try {
                    newConfiguration.serverConf.port = Integer.valueOf(newValue);
                } catch (Exception e) {
                    newConfiguration.serverConf.port = 0;
                    portIsFilled.setValue(false);
                    StyleManager.setUndefinedTextAreaStyle(txtPort);
                    imgUndefinedPort.setVisible(true);
                }
                if (newValue.isEmpty() || newValue.length() < 1 || newConfiguration.serverConf.port == 0) {
                    portIsFilled.setValue(false);
                    StyleManager.setUndefinedTextAreaStyle(txtPort);
                    imgUndefinedPort.setVisible(true);
                } else {
                    portIsFilled.setValue(true);
                    StyleManager.resetTextAreaStyle(txtPort);
                    imgUndefinedPort.setVisible(false);
                }
            }
        });

        this.cbAuth.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // checkMinConfIsCorrect();
            if (newValue) {
                this.newConfiguration.serverConf.authentification = true;
            } else {
                this.newConfiguration.serverConf.authentification = false;
            }
        });

        this.cbtls.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // checkMinConfIsCorrect();
            if (newValue) {
                this.newConfiguration.serverConf.authentification = true;
            } else {
                this.newConfiguration.serverConf.authentification = false;
            }
        });

        this.txtMail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                if (oldConfiguration.isConfOk) {
                    checkMinConfIsCorrect();
                }
                if (newValue.isEmpty() || !isMailCorrect(newValue)) {
                    mailIsFilled.setValue(false);
                    StyleManager.setUndefinedTextAreaStyle(txtMail);
                    imgUndefinedMail.setVisible(true);
                } else {
                    mailIsFilled.setValue(true);
                    StyleManager.resetTextAreaStyle(txtMail);
                    imgUndefinedMail.setVisible(false);
                }
                newConfiguration.serverConf.mail = newValue;
            }
        });

        txtPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                if (oldConfiguration.isConfOk) {
                    checkMinConfIsCorrect();
                }
                if (newValue.isEmpty() || newValue.length() < 1) {
                    passwdIsFilled.setValue(false);
                    StyleManager.setUndefinedTextAreaStyle(txtPassword);
                    imgUndefinedPasswd.setVisible(true);
                } else {
                    passwdIsFilled.setValue(true);
                    StyleManager.resetTextAreaStyle(txtPassword);
                    imgUndefinedPasswd.setVisible(false);
                }
                newConfiguration.serverConf.password = newValue;
            }
        });

        txtPathxlsx.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                if (newValue.isEmpty()) {
                    StyleManager.setUndefinedTextAreaStyle(txtPathxlsx);
                } else {
                    StyleManager.resetTextAreaStyle(txtPathxlsx);
                }
                newConfiguration.pathFilexlsx = newValue;
            }
        });

        txtPathpdf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
                newConfiguration.pathFilepdf = newValue.trim();
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