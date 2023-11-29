package com.github.yvasyliev.telegram.bot.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class UnknownCommand extends Command {
    @Value("🤷‍♀️ Unknown command.")
    private String reply;

    @Override
    public void acceptWithException(@NonNull Message message) throws TelegramApiException {
        sender.execute(new SendMessage(message.getChatId().toString(), reply));
    }
}
