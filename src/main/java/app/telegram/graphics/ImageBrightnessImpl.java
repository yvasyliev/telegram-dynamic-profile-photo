package app.telegram.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Changes image brightness.
 */
public class ImageBrightnessImpl implements ImageBrightness {
    /**
     * Brightness percentage.
     */
    private static final float PERCENTAGE = .5f;

    /**
     * Brightness value.
     */
    private static final int BRIGHTNESS = (int) (256 - 256 * PERCENTAGE);

    /**
     * Brightness color.
     */
    private static final Color DARKER_COLOR = new Color(0, 0, 0, BRIGHTNESS);

    @Override
    public void makeDarker(BufferedImage bufferedImage) {
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(DARKER_COLOR);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics.dispose();
    }
}
