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
import application.tools.Animations;
import application.tools.ConfigurationManager;
import application.tools.FileReader;
import application.tools.MailSender;
import application.tools.StageManagement;
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
import javafx.scene.control.Label;
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

    @FXML
    private ImageView loadingIcon;
    private RotateTransition loadingIconAnimation;

    @FXML
    private TextArea txtPathxlsx;

    @FXML
    private Button buttExcelFileTest;
    private final Tooltip tooltipTestExcelFile = new Tooltip(
            "Permet de récupérer la valeur de la case à la colonne et ligne de début entré.");

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
    private Label labPDFFileCount;

    @FXML
    private TextArea txtMailSubject;

    @FXML
    private TextArea txtMailContent;

    @FXML
    private Button buttStart;

    private Task<Void> sendingTask;

    private FileChooser excelFile;
    private FileChooser pdfFiles;

    private BooleanProperty hostIsFilled = new SimpleBooleanProperty();
    private BooleanProperty portIsFilled = new SimpleBooleanProperty();
    private BooleanProperty mailIsFilled = new SimpleBooleanProperty();
    private BooleanProperty passwdIsFilled = new SimpleBooleanProperty();

    private BooleanProperty minConfIsFilled = new SimpleBooleanProperty();

    public void initContext(Stage _primaryStage, Configuration _mailAutoApp) {
        this.mailAutoApp = _mailAutoApp;
        this.primaryStage = _primaryStage;
        oldConfiguration = ConfigurationManager.loadConf();
        newConfiguration = ConfigurationManager.loadConf();

        this.initMinConfIsFilledObserver();
        this.initViewElements();
        this.initTestConnectionTask();
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
        if (this.sendingTask != null && this.sendingTask.isRunning()) {
            AlertUtilities.showAlert(primaryStage, "Erreur.", "Un test est déjà en cours, veuillez attendre.", null,
                    AlertType.INFORMATION);
        } else {
            if (this.oldConfiguration.isConfOk) {
                try {
                    if (ServerBaseConfiguration.isSameConf(this.oldConfiguration.serverConf,
                            this.newConfiguration.serverConf) && this.minConfIsFilled.getValue()) {
                        this.newConfiguration.isConfOk = true;
                        setNewIcon("SuccesIcon.png");
                        // this.isCurrentlyConnected = true;
                        return true;
                    } else {
                        this.newConfiguration.isConfOk = false;
                        setNewIcon("FailedIcon.png");
                    }
                } catch (Exception e) {
                    this.newConfiguration.isConfOk = false;
                    setNewIcon("FailedIcon.png");
                    // this.isCurrentlyConnected = false;
                }
            } else {
                this.newConfiguration.isConfOk = false;
                setNewIcon("FailedIcon.png");
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
        // this.isCurrentlyConnected = this.oldConfiguration.isConfOk;
        this.checkMinConfIsCorrect();
    }

    private void initTestConnectionTask() {
        this.sendingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
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
        this.sendingTask.setOnSucceeded(e -> {
            loadingIconAnimation.stop();
            if (newConfiguration.isConfOk) {
                this.saveConf();
                this.setNewIcon("SuccesIcon.png");
                AlertUtilities.showAlert(primaryStage, "Connexion établie.",
                        "Connexion réussie !", "La connexion au service de messagerie a bien été établie !",
                        AlertType.INFORMATION);
            } else {
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

    private void disableMinConfWhileTest(boolean _disable) {
        this.txtHost.setDisable(_disable);
        this.txtPort.setDisable(_disable);
        this.cbAuth.setDisable(_disable);
        this.cbtls.setDisable(_disable);
        this.txtMail.setDisable(_disable);
        this.txtPassword.setDisable(_disable);
    }

    @FXML
    private void doConnectionTest() {
        if (this.sendingTask.isRunning()) {
            AlertUtilities.showAlert(primaryStage, "Erreur.", "Un test est déjà en cours, veuillez attendre.",
                    "Merci de patientez jusqu'à la fin du test en cours.",
                    AlertType.INFORMATION);
        } else {
            if (this.minConfIsFilled.getValue()) {
                this.initTestConnectionTask();
                new Thread(sendingTask).start();
                this.saveConf();
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
        this.checkMinConfIsCorrect();
        if (this.sendingTask.isRunning()) {
            AlertUtilities.showAlert(primaryStage, "Opération impossible.",
                    "Connexion au serveur de messagerie en cours.",
                    "Le test de connexion au serveur est en cours, veuillez patientez.",
                    AlertType.INFORMATION);
        } else {
            if (this.newConfiguration.isConfOk) {
                this.saveConf();
                SendMails sM = new SendMails(primaryStage);
            } else {
                AlertUtilities.showAlert(primaryStage, "Opération impossible.",
                        "Veuillez tester la connextion au serveur de messagerie d'abord !",
                        "Merci d'effectuer le test de connexion au serveur de messagerie avant !",
                        AlertType.ERROR);
            }
        }
    }

    @FXML
    private void doLeave() {
        if (this.sendingTask.isRunning()) {
            this.sendingTask.cancel(true);
        }
        this.saveConf();
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

    private void saveConf() {
        // this.newConfiguration.isConfOk = isCurrentlyConnected;
        ConfigurationManager.saveConf(newConfiguration);
        this.oldConfiguration = ConfigurationManager.loadConf();
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
                                + e,
                        AlertType.ERROR);
            } catch (NumberFormatException e) {
                AlertUtilities.showAlert(primaryStage, "Erreur", "Numéro de colonne ou de ligne incorrect !",
                        "Merci de vérifier le bon numéro de colonne et de ligne de la valeur à récupérer.\nCode d'erreur : "
                                + e,
                        AlertType.ERROR);
            } catch (IOException e) {
                AlertUtilities.showAlert(primaryStage, "Erreur", "Erreur lors de la lecture du fichier Excel",
                        "Quelque chose d'anormale s'est produit durant la lecture du fichier, merci de réessayer en vérifiant le type du fichier et le numéro de colonne / ligne saisi.\nCode d'erreur : "
                                + e,
                        AlertType.ERROR);
            } catch (Exception e) {
                AlertUtilities.showAlert(primaryStage, "Erreur", "Une erreur inattendue s'est produite",
                        "\"Quelque chose d'anormale s'est produit, merci de réessayer en vérifiant le type du fichier et le numéro de colonne / ligne saisi.\nCode d'erreur : "
                                + e,
                        AlertType.ERROR);
            }
        }
    }

    @FXML
    private void doChooseExcelFile() {
        StageManagement.disableItems(this.primaryStage.getScene(), true);
        File selectedFile = excelFile.showOpenDialog(new Stage());
        if (selectedFile != null) {
            this.txtPathxlsx.setText(selectedFile.getAbsolutePath());
        }
        StageManagement.disableItems(this.primaryStage.getScene(), false);
    }

    @FXML
    private void doChoosePdfFiles() throws InterruptedException {
        StageManagement.disableItems(this.primaryStage.getScene(), true);
        List<File> selectedFiles = this.pdfFiles.showOpenMultipleDialog(primaryStage);
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

    @FXML
    private void doRemoveExcelFile() {
        this.txtPathxlsx.setText("");
    }

    @FXML
    private void doRemovePdflFile() {
        this.newConfiguration.pathFilepdf.clear();
        this.labPDFFileCount.setText("" + 0);
        this.txtPathpdf.setText("");
    }

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
            tooltipTestExcelFile.hide();
        });
    }

    private void initTxtAreaListeners() {

        txtHost.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newValue = newValue.trim();
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
                    StyleManager.setUndefinedTextAreaStyle(txtMail);
                    imgUndefinedMail.setVisible(true);
                } else {
                    mailIsFilled.setValue(true);
                    StyleManager.resetTextAreaStyle(txtMail);
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
                    StyleManager.setUndefinedTextAreaStyle(txtPassword);
                    imgUndefinedPasswd.setVisible(true);
                } else {
                    passwdIsFilled.setValue(true);
                    StyleManager.resetTextAreaStyle(txtPassword);
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