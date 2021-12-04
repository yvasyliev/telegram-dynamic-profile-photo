package app.telegram.exceptions;

/**
 * Occurs during login to Telegram.
 */
public class TelegramLoginException extends Exception {
    public TelegramLoginException(Throwable cause) {
        super(cause);
    }
}
