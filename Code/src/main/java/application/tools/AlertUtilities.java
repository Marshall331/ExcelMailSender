package application.tools;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Utility class to display message or confirmation dialogs.
 */
public class AlertUtilities {

	/**
	 * Displays a confirmation message with Yes/No buttons.
	 *
	 * @param _fen     Stage on which the dialog centers and is modal.
	 * @param _title   Title of the dialog.
	 * @param _message Message to confirm.
	 * @param _content Information detail.
	 * @param _at      Type of alert (associated icon) (constant defined by
	 *                 AlertType).
	 * @return true if confirmed, false otherwise.
	 */
	public static boolean confirmYesCancel(Stage _fen, String _title, String _message, String _content, AlertType _at) {
		if (_at == null) {
			_at = AlertType.INFORMATION;
		}
		Alert alert = new Alert(_at);
		alert.initOwner(_fen);
		alert.setTitle(_title);
		if (_message == null || !_message.equals("")) {
			alert.setHeaderText(_message);
		}
		alert.setContentText(_content);
		Optional<ButtonType> option = alert.showAndWait();
		return option.isPresent() && option.get() == ButtonType.OK;
	}

	/**
	 * Displays a simple message with a close button.
	 *
	 * @param _fen     Stage on which the dialog centers and is modal.
	 * @param _title   Title of the dialog.
	 * @param _message Message to display.
	 * @param _content Information detail.
	 * @param _at      Type of alert (associated icon) (constant defined by
	 *                 AlertType).
	 */
	public static void showAlert(Stage _fen, String _title, String _message, String _content, AlertType _at) {
		if (_at == null) {
			_at = AlertType.INFORMATION;
		}
		Alert alert = new Alert(_at);
		alert.initOwner(_fen);
		alert.setTitle(_title);
		if (_message == null || !_message.equals("")) {
			alert.setHeaderText(_message);
		}
		alert.setContentText(_content);
		alert.showAndWait();
	}

	/**
	 * Displays a simple message with a close button.
	 *
	 * @param _title   Title of the dialog.
	 * @param _message Message to display.
	 * @param _at      Type of alert (associated icon) (constant defined by
	 *                 AlertType).
	 */
	public static void showAlert(String _title, String _message, AlertType _at) {
		if (_at == null) {
			_at = AlertType.INFORMATION;
		}
		Alert alert = new Alert(_at);
		alert.setTitle(_title);
		if (_message == null || !_message.equals("")) {
			alert.setHeaderText(_message);
		}
		alert.showAndWait();
	}
}