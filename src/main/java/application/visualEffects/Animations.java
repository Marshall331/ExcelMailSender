package application.visualEffects;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Utility class for managing animations in the application.
 */
public class Animations {

    /**
     * Starts a loading animation by rotating the specified ImageView.
     *
     * @param _img The ImageView to animate.
     * @return The RotateTransition object controlling the animation.
     */
    public static RotateTransition startLoadingAnimation(ImageView _img) {
        _img.setVisible(true);
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), _img);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);

        // Start the rotation animation
        rotateTransition.play();

        return rotateTransition;
    }

    /**
     * Stops a running loading animation.
     *
     * @param _img              The ImageView being animated.
     * @param _loadingAnimation The RotateTransition object controlling the
     *                          animation.
     */
    public static void stopLoadingAnimation(ImageView _img, RotateTransition _loadingAnimation) {
        _img.setVisible(false);
        _loadingAnimation.stop();
    }
}