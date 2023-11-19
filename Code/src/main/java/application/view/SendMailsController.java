package application.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import application.control.Configuration;
import application.tools.AlertUtilities;
import application.visualEffects.Animations;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.ConfigurationSave;
import model.MailSender;
import model.SaveManagement;

/**
 * Controller class handling the sending of emails and managing UI elements for
 * sending emails.
 */
public class SendMailsController {

    // Fields:
    private Stage primaryStage; // The primary stage of the application.
    private ConfigurationSave conf; // The configuration settings for email sending.

    // Observable lists to track emails.
    private ObservableList<String> observableListLeft = FXCollections.observableArrayList();
    private ObservableList<String> observableListDone = FXCollections.observableArrayList();

    // Regular expression for email validation
    private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Stacks to store email lists.
    private Stack<String> listLeft = new Stack<>();
    private Stack<String> listDone = new Stack<>();

    private Task<Void> sendingTask; // Task responsible for sending emails.

    private boolean isAllEmpty = true; // Flag indicating if all email lists are empty.

    // FXML elements:
    @FXML
    private ImageView loadingIcon; // Icon representing the loading state.

    private RotateTransition loadingIconAnimation; // Animation for the loading icon.

    @FXML
    private ListView<String> listViewLeft; // ListView for emails pending to be sent.

    @FXML
    private ListView<String> listViewDone; // ListView for emails that have been sent.

    @FXML
    private Label labCurrent; // Label displaying the currently processing email.

    @FXML
    private Label labNext; // Label displaying the next email to be processed.

    @FXML
    private Label labLeft; // Label displaying the number of emails left to be processed.

    @FXML
    private Button butStart; // Button to start the email sending process.

    @FXML
    private Button butStop; // Button to stop the email sending process.

    /**
     * Initializes the controller context with the primary stage and loads the
     * configuration.
     * Initializes view elements, sets up the sending task, and starts sending
     * emails.
     * 
     * @param primaryStage The primary stage of the application.
     */
    public void initContext(Stage _primaryStage) {
        this.primaryStage = _primaryStage;
        this.conf = SaveManagement.loadConf();

        this.initViewElements();
        this.initSendingTask();

        this.sendEmails();

    }

    /**
     * Initializes the UI elements such as ListViews, sets the stage's close request
     * action,
     * and initializes other necessary elements for the view.
     */
    private void initViewElements() {
        this.listViewLeft.setItems(observableListLeft);
        this.listViewDone.setItems(observableListDone);

        this.primaryStage.setOnCloseRequest(e -> {
            this.doLeave();
        });
    }

    /**
     * Updates the UI elements with the latest information from the lists of emails.
     * Refreshes the ListView content and updates labels based on the current state
     * of the email lists.
     */
    private void updateViewElements() {
        this.observableListLeft.clear();
        this.observableListDone.clear();
        this.observableListLeft.addAll(this.listLeft);
        this.observableListDone.addAll(this.listDone);
        if (this.listLeft.size() == 0) {
            this.butStart.setText("Relancer");
            if (this.sendingTask.isRunning()) {
                this.sendingTask.cancel(true);
            }
            if (this.isAllEmpty) {
                this.butStart.setText("Lancer");
                this.labNext.setText("Vide");
                this.labCurrent.setText("Vide");
                this.labLeft.setText("0");
                AlertUtilities.showAlert(primaryStage, "Aucun élement.", "Aucun élément n'a été trouvé.",
                        "Merci de vérifier l'index de la colonne et de la ligne.", AlertType.INFORMATION);
            } else {
                this.labNext.setText("Vide");
                this.labCurrent.setText("Vide");
                this.labLeft.setText("0");
                AlertUtilities.showAlert(primaryStage, "Terminé.", "Plus aucun élément restant.",
                        "Merci de re-configurer à nouveau pour des nouveaux envois.", AlertType.INFORMATION);
            }
        } else {
            this.labNext.setText(this.listLeft.size() <= 1 ? "Aucun" : this.listLeft.get(1));
            this.labCurrent.setText(this.listLeft.get(0));
            this.labLeft.setText("" + this.listLeft.size());
        }
    }

    /**
     * Initializes the sending task for email dispatch.
     * The task handles sending emails from a list until it's empty or canceled.
     */
    private void initSendingTask() {
        this.sendingTask = new Task<Void>() {
            @Override
            protected Void call() {
                while (!sendingTask.isCancelled() && !listLeft.isEmpty()) {
                    sendEmailsFromList();
                }
                return null;
            }
        };
        this.sendingTask.setOnSucceeded(e ->

        {
            this.butStop.setDisable(true);
            this.butStart.setDisable(false);
            Animations.stopLoadingAnimation(loadingIcon, this.loadingIconAnimation);
        });
        this.sendingTask.setOnFailed(e -> {
            this.butStop.setDisable(true);
            this.butStart.setDisable(false);
            Animations.stopLoadingAnimation(loadingIcon, this.loadingIconAnimation);
        });
        this.sendingTask.setOnCancelled(e -> {
            this.butStop.setDisable(true);
            this.butStart.setDisable(false);
            Animations.stopLoadingAnimation(loadingIcon, this.loadingIconAnimation);
        });
        this.sendingTask.setOnRunning(e -> {
            this.butStop.setDisable(false);
            this.butStart.setDisable(true);
            this.loadingIconAnimation = Animations.startLoadingAnimation(this.loadingIcon);
        });
    }

    /**
     * Displays the primary stage associated with this email manager.
     */
    public void displayDialog() {
        primaryStage.show();
    }

    /**
     * Initiates the process of sending emails.
     * It reads data from an Excel file to create a list of email addresses.
     */
    private void sendEmails() {
        if (this.sendingTask.isRunning()) {
            AlertUtilities.showAlert(primaryStage, "Erreur.", "Des envois sont déjà en cours, veuillez attendre.", null,
                    AlertType.INFORMATION);
        } else {
            this.initSendingTask();
            listLeft = new Stack<>();
            observableListLeft.clear();
            observableListDone.clear();
            try (FileInputStream excelFile = new FileInputStream(this.conf.pathFilexlsx);
                    Workbook workbook = new XSSFWorkbook(excelFile)) {

                Sheet sheet = workbook.getSheetAt(0);

                int index = this.conf.lineStartIndex - 1;
                int left = 0;
                if (this.conf.lineStartIndex > this.conf.lineEndIndex) {
                    left = this.conf.lineStartIndex - this.conf.lineEndIndex;
                } else {
                    left = this.conf.lineEndIndex - this.conf.lineStartIndex;
                }
                String mail = "";
                while (left >= 0) {
                    mail = "";
                    if (index <= sheet.getLastRowNum()) {
                        Row row = sheet.getRow(index);
                        if (row != null) {
                            Cell cell = row.getCell(this.conf.columnIndex);
                            if (cell != null && cell.getCellType() == CellType.STRING) {
                                Matcher matcher = EMAIL_PATTERN.matcher(cell.getStringCellValue().trim());
                                String cellValue = "";
                                if (matcher.matches()) {
                                    cellValue = cell.getStringCellValue().trim();
                                }
                                if (!cellValue.isEmpty()) {
                                    if (cellValue.length() < 50) {
                                        mail = cellValue;
                                    }
                                }
                            }
                        }
                    } else {
                        AlertUtilities.showAlert(primaryStage, "Erreur", "Indice de ligne en dehors de la limite.",
                                "\"L'indice de la ligne est en dehors de la limite, merci de réessayer en vérifiant le numéro de ligne saisi.",
                                AlertType.ERROR);
                    }
                    if (mail.length() > 1) {
                        listLeft.push(mail);
                    }
                    if (this.conf.lineStartIndex > this.conf.lineEndIndex) {
                        index--;
                        left--;
                    } else {
                        index++;
                        left--;
                    }
                }
                this.updateViewElements();
            } catch (FileNotFoundException e) {
                AlertUtilities.showAlert(primaryStage, "Erreur", "Fichier Excel introuvable",
                        "Le fichier Excel spécifié n'a pas été trouvé, merci de réessayer avec le bon fichier .xlsx.\nCode d'erreur : "
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

    /**
     * Checks if an attachment file exists and is readable.
     *
     * @param _filePath The path of the attachment file.
     * @return A message indicating the result of the attachment check.
     */
    private String checkAttachmentIsOk(String _filePath) {
        String res = "";
        try (PDDocument document = PDDocument.load(new File(_filePath))) {
        } catch (FileNotFoundException e) {
            res = "\nPièce jointe introuvable.\nLa pièce jointe spécifié au chemin suivant n'a pas été trouvée, veuillez réessayer.\n"
                    + _filePath + "Code d'erreur : ";
        } catch (IOException e) {
            res = "\nErreur lors de la lecture de la pièce jointe.\nQuelque chose d'anormale s'est produit durant la vérification de la pièce jointe suivante, veuillez réessayer.\n"
                    + _filePath + "\nCode d'erreur : " + e;
        } catch (Exception e) {
            res = "\nErreur lors de la lecture de la pièce jointe.\nQuelque chose d'anormale s'est produit durant la vérification de la pièce jointe suivante, veuillez réessayer.\n"
                    + _filePath + "\nCode d'erreur : " + e;
        }
        return res;
    }

    /**
     * Stops the current email sending task if it's running.
     */
    @FXML
    private void doStop() {
        if (sendingTask != null && sendingTask.isRunning()) {
            sendingTask.cancel(true);
        }
    }

    /**
     * Starts the email sending process if it's not already running.
     * It checks attachment validity and initiates the sending task.
     */
    @FXML
    private void doStart() {
        if (this.sendingTask.isRunning()) {
            AlertUtilities.showAlert(primaryStage, "Erreur.", "Des envois sont déjà en cours, veuillez attendre.", null,
                    AlertType.INFORMATION);
        } else {
            boolean start = true;
            String res = "";
            for (String path : this.conf.pathFilepdf) {
                res += this.checkAttachmentIsOk(path);
            }
            if (!res.equals("")) {
                start = AlertUtilities.confirmYesCancel(primaryStage, "Erreur.",
                        "Plusieurs problèmes rencontrés sur les pièces jointes, envoyer les mails quand même ?", res,
                        AlertType.INFORMATION);
            }
            if (start) {
                if (this.listLeft.size() == 0) {
                    this.sendEmails();
                }
                this.initSendingTask();
                new Thread(sendingTask).start();
            }
        }
    }

    /**
     * Sends emails from the list of addresses obtained from Excel data.
     */
    private void sendEmailsFromList() {
        this.isAllEmpty = false;
        String dest = listLeft.get(0);
        try {
            boolean isSucces = MailSender.sendMail(this.conf, dest);
            listLeft.remove(0);
            if (isSucces) {
                this.listDone.add("Succès : " + dest);
            } else {
                this.listDone.add("Erreur : " + dest);
            }
            Platform.runLater(() -> {
                updateViewElements();
            });
        } catch (Exception e) {
            AlertUtilities.showAlert(primaryStage, "Erreur", "Erreur lors de l'envoi du mail.'",
                    "Quelque chose d'anormale s'est produit lors de l'envoi du mail, merci de réessayer.\nCode d'erreur : "
                            + e,
                    AlertType.ERROR);
        }
    }

    /**
     * Opens the configuration window for settings.
     */
    @FXML
    private void doConfig() {
        if (sendingTask != null && sendingTask.isRunning()) {
            sendingTask.cancel(true);
        }
        Configuration configView = new Configuration();
        configView.start(this.primaryStage);
    }

    /**
     * Closes the application and stops ongoing tasks.
     */
    @FXML
    private void doLeave() {
        if (sendingTask != null && sendingTask.isRunning()) {
            sendingTask.cancel(true);
        }
        this.primaryStage.close();
        System.exit(0);
    }
}