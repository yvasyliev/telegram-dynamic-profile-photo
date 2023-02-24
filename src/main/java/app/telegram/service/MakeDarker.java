package app.telegram.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class MakeDarker implements ImageProcessor {
    @Autowired
    private Color darkerColor;

    @Override
    public void process(BufferedImage image) {
        Graphics graphics = image.getGraphics();
        graphics.setColor(darkerColor);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.dispose();
    }
}
