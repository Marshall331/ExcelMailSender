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
import application.control.SendMails;
import application.tools.AlertUtilities;
import application.tools.Animations;
import application.tools.ConfigurationManager;
import application.tools.MailSender;
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

public class SendMailsController {

    private SendMails mailAutoApp;
    private Stage primaryStage;
    private ConfigurationSave conf;

    ObservableList<String> observableListLeft = FXCollections.observableArrayList();

    ObservableList<String> observableListDone = FXCollections.observableArrayList();

    private Stack<String> listLeft = new Stack<>();
    private Stack<String> listDone = new Stack<>();

    private Task<Void> sendingTask;

    private boolean isAllEmpty = true;

    @FXML
    private ImageView loadingIcon;
    private RotateTransition loadingIconAnimation;

    @FXML
    private ListView listViewLeft;

    @FXML
    private ListView listViewDone;

    @FXML
    private Label labCurrent;

    @FXML
    private Label labNext;

    @FXML
    private Label labLeft;

    @FXML
    private Button butStart;

    @FXML
    private Button butStop;

    public void initContext(Stage _primaryStage, SendMails _mailAutoApp) {
        this.mailAutoApp = _mailAutoApp;
        this.primaryStage = _primaryStage;
        this.conf = ConfigurationManager.loadConf();

        this.initViewElements();
        this.initSendingTask();

        this.sendEmails();

    }

    private void initViewElements() {
        this.listViewLeft.setItems(observableListLeft);
        this.listViewDone.setItems(observableListDone);

        this.primaryStage.setOnCloseRequest(e -> {
            this.doLeave();
        });
    }

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

    public void displayDialog() {
        primaryStage.show();

    }

    private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

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

    @FXML
    private void doStop() {
        if (sendingTask != null && sendingTask.isRunning()) {
            sendingTask.cancel(true);
        }
    }

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

    @FXML
    private void doConfig() {
        if (sendingTask != null && sendingTask.isRunning()) {
            sendingTask.cancel(true);
        }
        Configuration c = new Configuration(this.primaryStage);
    }

    @FXML
    private void doLeave() {
        if (sendingTask != null && sendingTask.isRunning()) {
            sendingTask.cancel(true);
        }
        this.primaryStage.close();
        System.exit(0);
    }
}