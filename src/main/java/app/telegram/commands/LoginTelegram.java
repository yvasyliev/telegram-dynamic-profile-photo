package app.telegram.commands;

import it.tdlight.client.AuthenticationData;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class LoginTelegram implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginTelegram.class);

    @Value("${telegram.phone_number}")
    private String phoneNumber;

    @Autowired
    private SimpleTelegramClient telegramClient;

    @Autowired
    private AuthenticationData authenticationData;

    @Override
    public void execute() {
        try {
            Init.start();
            telegramClient.start(authenticationData);
        } catch (CantLoadLibrary e) {
            LOGGER.error("Failed to execute LoginTelegram command.", e);
        } finally {
            telegramClient.sendClose();
        }
    }
}
