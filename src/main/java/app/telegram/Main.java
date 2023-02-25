package app.telegram;

import app.telegram.commands.Command;
import app.telegram.configs.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/**
 * Program main class.
 */
public class Main {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Main method.
     *
     * @param args console args.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.warn("No arguments passed!");
            return;
        }

        try {
            var context = new AnnotationConfigApplicationContext(AppConfig.class);
            context.registerShutdownHook();

            var commandMap = context.getBean("commandMap", Map.class);
            var command = (Command) commandMap.get(args[0]);

            if (command != null) {
                command.execute();
            } else {
                LOGGER.warn("Unknown command: {}", args[0]);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute command: {}", args[0], e);
        } finally {
            System.exit(1); // W/A to stop tdlib processes.
        }
    }
}
