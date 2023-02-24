package app.telegram.factories;

import it.tdlight.common.Init;
import it.tdlight.common.TelegramClient;
import it.tdlight.common.UpdatesHandler;
import it.tdlight.tdlight.ClientManager;
import org.springframework.beans.factory.FactoryBean;

/**
 * Creates TDLib Telegram client wrapper.
 */
public class TelegramClientFactory implements FactoryBean<TelegramClient> {
    @Override
    public TelegramClient getObject() throws Exception {
        Init.start();
        TelegramClient telegramClient = ClientManager.create();
        telegramClient.initialize((UpdatesHandler) null, null, null);
        return telegramClient;
    }

    @Override
    public Class<?> getObjectType() {
        return TelegramClient.class;
    }
}
