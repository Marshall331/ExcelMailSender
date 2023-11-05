package application.tools;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Animations {

    public static RotateTransition startLoadingAnimation(ImageView _img) {
        _img.setVisible(true);
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), _img);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);

        // Démarrer l'animation de rotation
        rotateTransition.play();

        return rotateTransition;
    }

    public static void stopLoadingAnimation(ImageView _img, RotateTransition _loadingAnimation) {
        _img.setVisible(false);
        _loadingAnimation.stop();
    }

}