package com.github.yvasyliev.telegram.client.interaction;

import it.tdlight.client.ClientInteraction;
import it.tdlight.client.InputParameter;
import it.tdlight.client.ParameterInfo;
import it.tdlight.client.ParameterInfoNotifyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
public class TelegramBotClientInteraction implements ClientInteraction {
    @Autowired
    private Consumer<ParameterInfoNotifyLink> qrCodeSender;

    @Override
    public CompletableFuture<String> onParameterRequest(InputParameter inputParameter, ParameterInfo parameterInfo) {
        switch (inputParameter) {
            case NOTIFY_LINK -> qrCodeSender.accept((ParameterInfoNotifyLink) parameterInfo);
            case ASK_PASSWORD -> {
            }
        }
        return CompletableFuture.completedFuture(null);
    }
}
