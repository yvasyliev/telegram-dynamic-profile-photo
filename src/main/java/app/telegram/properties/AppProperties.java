package app.telegram.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * TelegramDeezerClient properties.
 */
public class AppProperties extends Properties {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppProperties.class);

    @Value("app.properties")
    private String propertiesPath;

    /**
     * Default properties path.
     */
    private static final String DEFAULT_PATH = "app.properties";

    public AppProperties() {
        this(DEFAULT_PATH);
    }

    public AppProperties(String path) {
    }

    @PostConstruct
    public void load() {
        try (InputStream inputStream = Files.newInputStream(Paths.get(propertiesPath))) {
            load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Failed to read properties {}.", propertiesPath, e);
        }
    }

    @PreDestroy
    public void store() {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(propertiesPath))) {
            store(outputStream, null);
        } catch (IOException e) {
            LOGGER.error("Failed to write properties {}.", propertiesPath, e);
        }
    }
}
