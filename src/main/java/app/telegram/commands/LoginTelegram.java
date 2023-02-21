package app.telegram.commands;

import app.telegram.client.SyncTelegramClient;
import it.tdlight.common.utils.ScannerUtils;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginTelegram implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginTelegram.class);

    @Autowired
    private SyncTelegramClient telegramClient;

    @Override
    public void execute() {
        TdApi.AuthorizationState authorizationState = telegramClient.send(new TdApi.GetAuthorizationState());
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            throw new IllegalStateException("The user is already logged in!");
        }

        String phoneNumber = ScannerUtils.askParameter("phone number", "Please enter phone number");
        telegramClient.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null));

        String code = ScannerUtils.askParameter(phoneNumber, "Please enter code");
        telegramClient.send(new TdApi.CheckAuthenticationCode(code));

        LOGGER.info("Logged in into Telegram.");
    }
}
