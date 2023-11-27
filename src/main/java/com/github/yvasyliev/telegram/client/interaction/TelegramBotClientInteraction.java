package com.github.yvasyliev.telegram.client.interaction;

import it.tdlight.client.ClientInteraction;
import it.tdlight.client.InputParameter;
import it.tdlight.client.ParameterInfo;
import it.tdlight.client.ParameterInfoNotifyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingConsumer;

import java.util.concurrent.CompletableFuture;

@Component
public class TelegramBotClientInteraction implements ClientInteraction {
    @Autowired
    private ThrowingConsumer<ParameterInfoNotifyLink> qrCodeSender;

    @Override
    public CompletableFuture<String> onParameterRequest(InputParameter inputParameter, ParameterInfo parameterInfo) {
        try {
            if (parameterInfo instanceof ParameterInfoNotifyLink notifyLink) {
                qrCodeSender.acceptWithException(notifyLink);
            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
