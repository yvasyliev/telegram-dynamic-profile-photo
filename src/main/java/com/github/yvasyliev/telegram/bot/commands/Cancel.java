package com.github.yvasyliev.telegram.bot.commands;

import com.github.yvasyliev.telegram.client.TdLightClient;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component("/cancel")
public class Cancel extends Command {
    @Autowired
    private TdLightClient client;

    @Autowired
    private Map<Long, String> pendingCommands;

    @Value("👌 OK.")
    private String reply;

    @Override
    public void acceptWithException(@NonNull Message message) throws ExecutionException, InterruptedException, TelegramApiException {
        var chatId = message.getChatId();
        if (chatId.equals(bot.getChatId()) && client.getAuthorizationState().get() instanceof TdApi.AuthorizationStateWaitPassword) {
            client.logOut();
            bot.unsetChatId();
        }
        pendingCommands.remove(chatId);
        bot.execute(new SendMessage(chatId.toString(), reply));
    }
}
