package com.github.yvasyliev.telegram.bot;

import com.github.yvasyliev.telegram.bot.commands.Command;
import com.github.yvasyliev.telegram.bot.commands.UnknownCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.starter.AfterBotRegistration;

import java.util.Map;

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
    private Map<Long, String> pendingCommands;

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
            if (message.isUserMessage()) {
                var command = getCommand(message);
                if (command != null) {
                    try {
                        command.acceptWithException(message);
                    } catch (Exception e) {
                        LOGGER.error("Failed to perform command: {}", command, e);
                    }
                }
            }
        }
    }

    private Command getCommand(Message message) {
        var commandName = getCommandName(message);
        return commandName != null
                ? getCommand(commandName)
                : null;
    }

    private String getCommandName(Message message) {
        return message.isCommand()
                ? message.getText().split("\\s+")[0]
                : pendingCommands.remove(message.getChatId());
    }

    private Command getCommand(String commandName) {
        return context.containsBean(commandName)
                ? context.getBean(commandName, Command.class)
                : context.getBean(UnknownCommand.class);
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

    public boolean hasChatId() {
        return chatId != null;
    }

    public void unsetChatId() {
        chatId = null;
    }
}
