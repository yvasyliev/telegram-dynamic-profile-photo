package app.telegram.properties;

import java.io.*;
import java.util.Properties;

/**
 * TelegramDeezerClient properties.
 */
public class AppProperties extends Properties {
    /**
     * Default properties path.
     */
    private static final String DEFAULT_PATH = "app.properties";

    /**
     * Actual properties path.
     */
    private final String path;

    public AppProperties() {
        this(DEFAULT_PATH);
    }

    public AppProperties(String path) {
        this.path = path;
    }

    /**
     * Loads properties from {@link AppProperties#path}.
     *
     * @throws IOException if reading errors occur.
     */
    public void load() throws IOException {
        try (InputStream inputStream = new FileInputStream(DEFAULT_PATH)) {
            load(inputStream);
        }
    }

    /**
     * Saves properties by {@link AppProperties#path}.
     *
     * @throws IOException if writing errors occur.
     */
    public void save() throws IOException {
        try (OutputStream outputStream = new FileOutputStream(path)) {
            store(outputStream, null);
        }
    }
}
