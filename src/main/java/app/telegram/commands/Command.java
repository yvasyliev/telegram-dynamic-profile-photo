package app.telegram.commands;

@FunctionalInterface
public interface Command {
    void execute();
}
