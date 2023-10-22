package app.telegram.commands;

import app.telegram.clients.SyncTelegramClient;
import it.tdlight.jni.TdApi;
import it.tdlight.util.ScannerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Logs in into Telegram.
 */
public class LoginTelegram implements Command {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginTelegram.class);

    /**
     * Telegram client.
     */
    @Autowired
    private SyncTelegramClient telegramClient;

    @Override
    public void execute() {
        if (telegramClient.send(new TdApi.GetAuthorizationState()) instanceof TdApi.AuthorizationStateReady) {
            throw new IllegalStateException("The user is already logged in!");
        }

        var phoneNumber = ScannerUtils.askParameter("phone number", "Please enter phone number");
        telegramClient.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null));

        var code = ScannerUtils.askParameter(phoneNumber, "Please enter code");
        telegramClient.send(new TdApi.CheckAuthenticationCode(code));

        if (telegramClient.send(new TdApi.GetAuthorizationState()) instanceof TdApi.AuthorizationStateWaitPassword) {
            var password = ScannerUtils.askParameter(phoneNumber, "Please enter password");
            telegramClient.send(new TdApi.CheckAuthenticationPassword(password));
        }

        LOGGER.info("Logged in into Telegram.");
    }
}
