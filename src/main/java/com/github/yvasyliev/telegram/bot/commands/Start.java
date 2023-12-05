package com.github.yvasyliev.telegram.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component("/start")
public class Start extends Command {
    @Autowired
    private User me;

    @Value("""
            👋 Hi, I'm %s, and I can update your profile photo when you listen to Deezer music.
            
            To do so I need you to perform the next steps:
            /tglogin - to login with your Telegram account
            /deezerlogin - to login with your Deezer account
            
            Send me /help anytime to see available commands.""")
    private String reply;

    @Override
    public void acceptWithException(@NonNull Message message) throws TelegramApiException {
        bot.execute(new SendMessage(
                message.getChatId().toString(),
                reply.formatted(me.getFirstName())
        ));
    }
}
