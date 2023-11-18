package application.visualEffects;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * Utility class for managing styles of Button and TextArea elements.
 */
public class Style {

    /**
     * Adds a specific style to the given Button.
     *
     * @param _butt     The Button to style.
     * @param _color    The color for the border.
     * @param _fontSize The width of the border.
     */
    public static void addButtonStyle(Button _butt, String _color, double _fontSize) {
        _butt.setStyle(_butt.getStyle() + "-fx-border-color: " + _color + "; -fx-border-width: " + _fontSize + ";");
    }

    /**
     * Sets a specific style for an undefined TextArea.
     *
     * @param _txt The TextArea to style as undefined.
     */
    public static void setUndefinedTextAreaStyle(TextArea _txt) {
        _txt.getStyleClass().setAll("undefined-txtArea");
    }

    /**
     * Resets the style for a TextArea.
     *
     * @param _txt The TextArea to reset the style.
     */
    public static void resetTextAreaStyle(TextArea _txt) {
        _txt.getStyleClass().setAll("txtArea");
    }
}