package application.tools;

import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * Utility class for managing stage elements.
 */
public class StageManagement {

    /**
     * Disables or enables all nodes within a scene.
     *
     * @param _scene   The scene containing the nodes.
     * @param _disable If true, disables all nodes; if false, enables them.
     */
    public static void disableItems(Scene _scene, boolean _disable) {
        for (Node node : _scene.getRoot().getChildrenUnmodifiable()) {
            node.setDisable(_disable);
        }
    }
}