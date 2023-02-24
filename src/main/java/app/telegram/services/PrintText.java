package app.telegram.services;

import api.deezer.objects.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

/**
 * Prints text on image.
 */
public class PrintText implements ImageProcessor {
    /**
     * Track to print.
     */
    @Autowired
    private Supplier<Track> trackToPrint;

    /**
     * Outline stroke.
     */
    @Autowired
    private Stroke outlineStroke;

    /**
     * Outline color.
     */
    @Autowired
    private Color outlineColor;

    /**
     * Text color.
     */
    @Autowired
    private Color textColor;

    /**
     * Font size.
     */
    @Value("110")
    private int fontSize;

    /**
     * Base font.
     */
    @Autowired
    private Font baseFont;

    /**
     * Offset X.
     */
    private int offsetX = 0;

    @Override
    public void process(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setStroke(outlineStroke);

        Track track = trackToPrint.get();
        print(graphics, "Just listened to", image.getWidth(), 350);
        print(graphics, "\"" + track.getTitle() + "\"", image.getWidth(), 150);
        print(graphics, "by " + track.getArtist().getName(), image.getWidth(), 150);

        graphics.dispose();
    }

    /**
     * Prints text on image.
     *
     * @param graphics   image graphics.
     * @param text       text to print.
     * @param imageWidth image width.
     * @param offsetY    offset Y.
     */
    private void print(Graphics2D graphics, String text, int imageWidth, int offsetY) {
        Shape outline = getOutline(graphics, text, imageWidth);

        graphics.translate(-offsetX, 0);
        offsetX = (imageWidth - outline.getBounds().width) / 2;
        graphics.translate(offsetX, offsetY);

        graphics.setColor(outlineColor);
        graphics.draw(outline);

        graphics.setColor(textColor);
        graphics.fill(outline);
    }

    /**
     * Calculates outline shape to fit image width.
     *
     * @param graphics   image graphics.
     * @param text       text to print.
     * @param imageWidth image width.
     * @return outline shape.
     */
    protected Shape getOutline(Graphics2D graphics, String text, int imageWidth) {
        Shape outline;
        float i = 0;

        do {
            graphics.setFont(baseFont.deriveFont(fontSize - i));
            outline = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), text).getOutline();
            i += 10;
        } while (outline.getBounds().width > imageWidth * 0.8);

        return outline;
    }
}
