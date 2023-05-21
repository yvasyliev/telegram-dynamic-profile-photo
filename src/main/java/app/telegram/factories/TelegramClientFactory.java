package app.telegram.factories;

import it.tdlight.ClientFactory;
import it.tdlight.TelegramClient;
import it.tdlight.UpdatesHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Creates TDLib Telegram client wrapper.
 */
public class TelegramClientFactory implements FactoryBean<TelegramClient> {
    @Autowired
    private ClientFactory clientFactory;

    @Override
    public TelegramClient getObject() {
        var telegramClient = clientFactory.createClient();
        telegramClient.initialize((UpdatesHandler) null, null, null);
        return telegramClient;
    }

    @Override
    public Class<?> getObjectType() {
        return TelegramClient.class;
    }
}
