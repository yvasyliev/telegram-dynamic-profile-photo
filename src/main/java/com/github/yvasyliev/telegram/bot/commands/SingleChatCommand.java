package com.github.yvasyliev.telegram.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.Supplier;

public abstract class SingleChatCommand extends Command {
    @Value("""
            ‍🤷‍♀️ Looks like I'm already in use by someone\\.
                        
            😎 But you can create your own bot if you follow the [instruction](https://github.com/yvasyliev/telegram-dynamic-profile-photo)\\.""")
    private String reply;

    @Autowired
    private Supplier<Long> chatIdGetter;

    @Override
    public void acceptWithException(@NonNull Message message) throws Exception {
        var chatId = chatIdGetter.get();
        if (chatId == null || chatId.equals(message.getChatId())) {
            execute(message);
        } else {
            var sendMessage = SendMessage
                    .builder()
                    .chatId(message.getChatId())
                    .text(reply)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
            sender.execute(sendMessage);
        }
    }

    public abstract void execute(Message message) throws Exception;
}
