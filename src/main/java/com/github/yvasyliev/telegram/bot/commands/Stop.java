package com.github.yvasyliev.telegram.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component("/stop")
public class Stop extends SingleChatCommand {
    @Value("🛑 Stopping the app.")
    private String reply;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void execute(Message message) throws TelegramApiException {
        bot.execute(new SendMessage(message.getChatId().toString(), reply));
        context.close();
    }
}
