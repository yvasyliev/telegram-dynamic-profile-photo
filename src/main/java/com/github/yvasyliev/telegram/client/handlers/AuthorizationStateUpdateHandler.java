package com.github.yvasyliev.telegram.client.handlers;

import com.github.yvasyliev.model.AuthorizationStateUpdate;
import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationStateUpdateHandler implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState updateAuthorizationState) {
        eventPublisher.publishEvent(new AuthorizationStateUpdate(updateAuthorizationState));
        // TODO: 11/29/2023 remove
        var authorizationState = updateAuthorizationState.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            System.out.println("Logged in");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            System.out.println("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out...");
        }
    }
}
