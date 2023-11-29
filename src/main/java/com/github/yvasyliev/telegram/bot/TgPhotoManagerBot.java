package com.github.yvasyliev.telegram.bot;

import com.github.yvasyliev.model.CommandReceived;
import com.github.yvasyliev.telegram.bot.commands.Command;
import com.github.yvasyliev.telegram.bot.commands.UnknownCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.starter.AfterBotRegistration;

@Component
public class TgPhotoManagerBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(TgPhotoManagerBot.class);

    @Value("${telegram.bot.username}")
    private String botUsername;

    // TODO: 11/28/2023 close session
    private BotSession botSession;

    private Long chatId;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
            if (message.isUserMessage() && message.isCommand()) {
                if (message.getChatId().equals(chatId)) {
                    eventPublisher.publishEvent(new CommandReceived(message));
                }
                var commandName = message.getText().split("\\s+")[0];
                var command = context.containsBean(commandName)
                        ? context.getBean(commandName, Command.class)
                        : context.getBean(UnknownCommand.class);
                try {
                    command.acceptWithException(message);
                } catch (Exception e) {
                    LOGGER.error("Failed to perform command: {}", commandName, e);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
