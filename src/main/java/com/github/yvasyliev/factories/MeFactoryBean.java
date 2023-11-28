package com.github.yvasyliev.factories;

import com.github.yvasyliev.telegram.bot.TgPhotoManagerBot;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MeFactoryBean implements FactoryBean<User> {
    @Autowired
    private TgPhotoManagerBot managerBot;

    @Override
    public User getObject() throws TelegramApiException {
        return managerBot.execute(new GetMe());
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }
}
