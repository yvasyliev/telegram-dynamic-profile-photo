package com.github.yvasyliev.telegram.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.starter.AfterBotRegistration;

@Component
public class TgPhotoManagerBot extends TelegramLongPollingBot {
    @Value("${telegram.bot.username}")
    private String botUsername;

    private BotSession botSession;

    private Long chatId;

    public TgPhotoManagerBot(@Value("${telegram.bot.token}") String botToken) {
        super(botToken);
    }

    @AfterBotRegistration
    public void setBotSession(BotSession botSession) {
        this.botSession = botSession;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.isUserMessage() && message.isCommand() && "/tglogin".equals(message.getText())) {
                chatId = message.getChatId();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public boolean hasChatId() {
        return chatId != null;
    }

    public Long getChatId() {
        return chatId;
    }
}
