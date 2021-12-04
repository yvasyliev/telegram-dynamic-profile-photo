package app.telegram.graphics;

import api.deezer.objects.Track;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Prints outlined track.
 */
public class OutlinedImageTrack implements ImageTrack {
    /**
     * Base font size.
     */
    private static final int FONT_SIZE = 110;

    /**
     * Base font.
     */
    private static final Font BASE_FONT = new Font(Font.SERIF, Font.BOLD, FONT_SIZE);

    /**
     * Outline color.
     */
    private static final Color OUTLINE_COLOR = new Color(38, 38, 38);

    /**
     * Outline stroke.
     */
    private static final Stroke OUTLINE_STROKE = new BasicStroke(20);

    /**
     * Text color.
     */
    private static final Color TEXT_COLOR = new Color(227, 225, 225);

    /**
     * X offset.
     */
    private int offsetX = 0;

    @Override
    public void printTrack(BufferedImage bufferedImage, Track track) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setStroke(OUTLINE_STROKE);

        print(graphics, "Just listened to", bufferedImage.getWidth(), 350);
        print(graphics, "\"" + track.getTitle() + "\"", bufferedImage.getWidth(), 150);
        print(graphics, "by " + track.getArtist().getName(), bufferedImage.getWidth(), 150);

        graphics.dispose();
    }

    /**
     * Prints outlined text to image.
     *
     * @param graphics   image graphics.
     * @param text       text to print.
     * @param imageWidth image width.
     * @param offsetY    Y offset.
     */
    protected void print(Graphics2D graphics, String text, int imageWidth, int offsetY) {
        graphics.setFont(BASE_FONT.deriveFont(getFontSize(text)));

        Shape outline = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), text).getOutline();

        graphics.translate(-offsetX, 0);
        offsetX = (imageWidth - outline.getBounds().width) / 2;
        graphics.translate(offsetX, offsetY);

        graphics.setColor(OUTLINE_COLOR);
        graphics.draw(outline);

        graphics.setColor(TEXT_COLOR);
        graphics.fill(outline);
    }

    /**
     * Calculates font size based on text length.
     *
     * @param text text to print.
     * @return font size.
     */
    protected float getFontSize(String text) {
        return text.length() < 15
                ? FONT_SIZE
                : FONT_SIZE / (text.length() / 12f);
    }
}
