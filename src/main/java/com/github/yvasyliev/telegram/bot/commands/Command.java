package com.github.yvasyliev.telegram.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.function.ThrowingConsumer;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public abstract class Command implements ThrowingConsumer<Message> {
    @Autowired
    protected AbsSender sender;
}
