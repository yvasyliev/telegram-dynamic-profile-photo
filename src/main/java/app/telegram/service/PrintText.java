package app.telegram.service;

import api.deezer.objects.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

public class PrintText implements ImageProcessor {
    @Autowired
    private Supplier<Track> trackToPrint;

    @Autowired
    private Stroke outlineStroke;

    @Autowired
    private Color outlineColor;

    @Autowired
    private Color textColor;

    @Value("110")
    private int fontSize;

    @Autowired
    private Font baseFont;

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
