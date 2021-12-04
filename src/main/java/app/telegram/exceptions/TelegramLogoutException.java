package app.telegram.exceptions;

/**
 * Occurs during logout from Telegram.
 */
public class TelegramLogoutException extends Exception {
    public TelegramLogoutException(Throwable cause) {
        super(cause);
    }
}
