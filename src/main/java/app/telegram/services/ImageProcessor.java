package app.telegram.services;

import java.awt.image.BufferedImage;

/**
 * Image processor.
 */
public interface ImageProcessor {
    /**
     * Processes image.
     *
     * @param image image to process.
     */
    void process(BufferedImage image);
}
