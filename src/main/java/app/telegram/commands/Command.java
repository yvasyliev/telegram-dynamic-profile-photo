package app.telegram.commands;

/**
 * Console command.
 */
@FunctionalInterface
public interface Command {
    /**
     * Execute command.
     *
     * @throws Exception if errors occur.
     */
    void execute() throws Exception;
}
