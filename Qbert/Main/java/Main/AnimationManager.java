package Main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimationManager {
    private float alpha = 0.0f;
    private float fadeSpeed = 0.05f;
    private boolean isFadingIn = true;

    public void update() {
        if (isFadingIn) {
            alpha += fadeSpeed;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                isFadingIn = false;
            }
        } else {
            alpha -= fadeSpeed;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                isFadingIn = true;
            }
        }
    }

    public void drawFade(Graphics2D g2, BufferedImage image, int x, int y, int width, int height) {
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.drawImage(image, x, y, width, height, null);
        g2.setComposite(originalComposite);
    }

    public void reset() {
        alpha = 0.0f;
        isFadingIn = true;
    }
} 