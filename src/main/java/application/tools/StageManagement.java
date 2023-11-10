package application.tools;

import javafx.scene.Node;
import javafx.scene.Scene;

public class StageManagement {
    public static void disableItems(Scene _scene, boolean _disable) {
        for (Node node : _scene.getRoot().getChildrenUnmodifiable()) {
            node.setDisable(_disable);
        }
    }
}
