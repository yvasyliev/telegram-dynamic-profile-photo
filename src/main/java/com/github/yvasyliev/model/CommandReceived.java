package com.github.yvasyliev.model;

import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.Message;

public class CommandReceived extends ApplicationEvent {
    public CommandReceived(Message message) {
        super(message);
    }
}
