package com.github.yvasyliev.model;

import org.telegram.telegrambots.meta.api.objects.Message;

public record CommandReceived(Message message) {
}
