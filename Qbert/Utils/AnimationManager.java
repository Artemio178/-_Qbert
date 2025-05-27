package Utils;

import java.awt.Graphics2D;

public class AnimationManager {
    public AnimationManager() {
        // Initialize animation manager
    }

    public void update() {
        // Update animations
    }

    public void drawFade(Graphics2D g2, java.awt.image.BufferedImage image, int x, int y, int width, int height) {
        // Draw image with fade effect
        g2.drawImage(image, x, y, width, height, null);
    }

    public void reset() {
        // Reset animations
    }
} 