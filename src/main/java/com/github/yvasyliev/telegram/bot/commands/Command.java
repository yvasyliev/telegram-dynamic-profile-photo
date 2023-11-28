package com.github.yvasyliev.telegram.bot.commands;

import com.github.yvasyliev.telegram.bot.TgPhotoManagerBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.function.ThrowingConsumer;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class Command implements ThrowingConsumer<Message> {
    @Autowired
    protected TgPhotoManagerBot managerBot;
}
