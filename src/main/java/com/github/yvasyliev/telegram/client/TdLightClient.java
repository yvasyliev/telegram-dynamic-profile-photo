package com.github.yvasyliev.telegram.client;

import com.github.yvasyliev.model.AuthorizationStateWaitOtherDeviceConfirmationEvent;
import com.github.yvasyliev.model.AuthorizationStateWaitPasswordEvent;
import it.tdlight.TelegramClient;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class TdLightClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TdLightClient.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TdApi.SetTdlibParameters setTdlibParameters;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TelegramClient client;

    public void onUpdate(TdApi.Object object) {
        LOGGER.debug("Received update: {}", object);
        if (object instanceof TdApi.UpdateAuthorizationState updateAuthorizationState) {
            if (updateAuthorizationState.authorizationState instanceof TdApi.AuthorizationStateWaitTdlibParameters) {
                send(setTdlibParameters);
            } else if (updateAuthorizationState.authorizationState instanceof TdApi.AuthorizationStateWaitOtherDeviceConfirmation state) {
                eventPublisher.publishEvent(new AuthorizationStateWaitOtherDeviceConfirmationEvent(state));
            } else if (updateAuthorizationState.authorizationState instanceof TdApi.AuthorizationStateWaitPassword state) {
                eventPublisher.publishEvent(new AuthorizationStateWaitPasswordEvent(state));
            } else if (updateAuthorizationState.authorizationState instanceof TdApi.AuthorizationStateClosed) {
                client = applicationContext.getBean(TelegramClient.class);
            }
        }
    }

    public void onUpdateError(Throwable throwable) {
        LOGGER.error("Received an error from updates.", throwable);
    }

    public void onError(Throwable throwable) {
        LOGGER.error("Received an error.", throwable);
    }

    @SuppressWarnings("unchecked")
    public <T extends TdApi.Object> CompletableFuture<T> send(TdApi.Function<T> function) {
        var result = new CompletableFuture<T>();
        client.send(function, object -> result.complete((T) object), result::completeExceptionally);
        return result;
    }

    public CompletableFuture<TdApi.Ok> requestQrCodeAuthentication() {
        return send(new TdApi.RequestQrCodeAuthentication());
    }

    public CompletableFuture<TdApi.AuthorizationState> getAuthorizationState() {
        return send(new TdApi.GetAuthorizationState());
    }

    public CompletableFuture<TdApi.Ok> logOut() {
        return send(new TdApi.LogOut());
    }
}
