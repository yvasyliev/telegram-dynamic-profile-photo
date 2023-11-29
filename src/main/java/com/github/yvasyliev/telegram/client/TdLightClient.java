package com.github.yvasyliev.telegram.client;

import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class TdLightClient {
    @Autowired
    private SimpleTelegramClient client;

    // TODO: 11/29/2023 remove @PreDestroy
    @PreDestroy
    public void logout() {
        client.execute(new TdApi.LogOut());
    }

    public CompletableFuture<TdApi.AuthorizationState> getAuthorizationState() {
        return client.send(new TdApi.GetAuthorizationState());
    }
}
