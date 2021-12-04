package app.telegram.graphics;

import api.deezer.objects.Track;

import java.awt.image.BufferedImage;

/**
 * Adds a track to image.
 */
public interface ImageTrack {
    /**
     * Adds a track to image.
     *
     * @param bufferedImage source image.
     * @param track         track to print.
     */
    void printTrack(BufferedImage bufferedImage, Track track);
}
