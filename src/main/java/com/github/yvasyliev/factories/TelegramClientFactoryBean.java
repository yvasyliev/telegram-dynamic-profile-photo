package com.github.yvasyliev.factories;

import com.github.yvasyliev.telegram.client.TdLightClient;
import it.tdlight.ClientFactory;
import it.tdlight.ResultHandler;
import it.tdlight.TelegramClient;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TelegramClientFactoryBean implements FactoryBean<TelegramClient> {
    @Autowired
    private ApplicationContext context;

    private ClientFactory clientFactory;

    @Override
    public TelegramClient getObject() {
//        if (clientFactory != null) {
//            clientFactory.close();
//        }
        clientFactory = context.getBean(ClientFactory.class);
        var client = clientFactory.createClient();
        client.initialize(
                (ResultHandler<TdApi.Update>) object -> context.getBean(TdLightClient.class).onUpdate(object),
                throwable -> context.getBean(TdLightClient.class).onUpdateError(throwable),
                throwable -> context.getBean(TdLightClient.class).onError(throwable)
        );
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return TelegramClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
