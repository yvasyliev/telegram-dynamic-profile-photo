package app.telegram.services;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Makes image darker.
 */
public class MakeDarker implements ImageProcessor {
    /**
     * Darker color.
     */
    @Autowired
    private Color darkerColor;

    @Override
    public void process(BufferedImage image) {
        var graphics = image.getGraphics();
        graphics.setColor(darkerColor);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.dispose();
    }
}
