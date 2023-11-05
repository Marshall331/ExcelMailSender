package application.tools;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class StyleManager {

    public static void addButtonStyle(Button _butt, String _color, double _fontSize) {
        _butt.setStyle(_butt.getStyle() + "-fx-border-color: " + _color + "; -fx-border-width: " + _fontSize + ";");
    }

    // public static void addTxtAreaStyle(TextArea _txt, String _color) {
    // // _txt.setStyle(_txt.getStyle() + "-fx-border-color: " + _color + ";");
    // // _txt.getStyleClass().add("-fx-border-color: " + _color + ";");

    // _txt.getStyleClass().setAll("undefined-txtArea");
    // }

    public static void setUndefinedTextAreaStyle(TextArea _txt) {
        _txt.getStyleClass().setAll("undefined-txtArea");
    }

    public static void resetTextAreaStyle(TextArea _txt) {
        _txt.getStyleClass().setAll("txtArea");
    }

    public static void resetButtStyle(Button _butt) {

    }
}
