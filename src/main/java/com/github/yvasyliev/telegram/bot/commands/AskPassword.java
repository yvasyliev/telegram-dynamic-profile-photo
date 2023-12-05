package com.github.yvasyliev.telegram.bot.commands;

import com.github.yvasyliev.model.AuthorizationStateWaitPasswordEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Component("/askpassword")
public class AskPassword extends SingleChatCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(AskPassword.class);

    @Value("""
            Please send me your password:
                        
            ||Your password will not be stored anywhere\\.||""")
    private String askPasswordText;

    @Autowired
    private Map<Long, String> pendingCommands;

    @Override
    public void execute(Message message) throws TelegramApiException {
        askPassword(message.getChatId());
    }

    @EventListener
    public void onAuthorizationStateWaitPassword(AuthorizationStateWaitPasswordEvent ignoredEvent) {
        try {
            askPassword(bot.getChatId());
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to ask password.", e);
        }
    }

    private void askPassword(Long chatId) throws TelegramApiException {
        var sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(askPasswordText)
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        bot.execute(sendMessage);
        pendingCommands.put(chatId, "/acceptpassword");
    }
}
