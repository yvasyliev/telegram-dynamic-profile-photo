package app.telegram.commands;

import app.telegram.clients.SyncTelegramClient;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Logs out from Telegram.
 */
public class LogoutTelegram implements Command {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutTelegram.class);

    /**
     * Telegram client.
     */
    @Autowired
    private SyncTelegramClient telegramClient;

    @Override
    public void execute() throws Exception {
        telegramClient.send(new TdApi.LogOut());
        LOGGER.info("Logged out from Telegram.");
    }
}
