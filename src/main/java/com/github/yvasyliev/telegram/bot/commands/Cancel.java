package com.github.yvasyliev.telegram.bot.commands;

import com.github.yvasyliev.telegram.client.TdLightClient;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service("/cancel")
public class Cancel extends Command {
    @Autowired
    private TdLightClient tdLightClient;

    @Value("""
            #{Class.forName("it.tdlight.jni.TdApi$AuthorizationStateWaitOtherDeviceConfirmation")}""")
    private Set<Class<? extends TdApi.AuthorizationState>> initialStates;

    @Value("👌 Ok.")
    private String reply;

    @Override
    public void acceptWithException(@NonNull Message message) throws ExecutionException, InterruptedException, TelegramApiException {
        var authorizationState = tdLightClient.getAuthorizationState().get();
        if (initialStates.contains(authorizationState.getClass())) {
            managerBot.setChatId(null);
        }
        managerBot.execute(new SendMessage(message.getChatId().toString(), reply));
    }
}
