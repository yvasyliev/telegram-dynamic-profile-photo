package app.telegram.graphics;

import java.awt.image.BufferedImage;

/**
 * Changes image brightness.
 */
public interface ImageBrightness {
    /**
     * Changes image brightness.
     *
     * @param bufferedImage image to change brightness.
     */
    void makeDarker(BufferedImage bufferedImage);
}
