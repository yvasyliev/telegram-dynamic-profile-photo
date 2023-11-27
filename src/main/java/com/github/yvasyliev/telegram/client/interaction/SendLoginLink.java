package com.github.yvasyliev.telegram.client.interaction;

import com.github.yvasyliev.model.QRCodeDTO;
import com.github.yvasyliev.telegram.bot.TgPhotoManagerBot;
import it.tdlight.client.ParameterInfoNotifyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingConsumer;
import org.springframework.util.function.ThrowingFunction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class SendLoginLink implements ThrowingConsumer<ParameterInfoNotifyLink> {
    @Value("%s=%s")
    private String encodedLinkTemplate;

    @Autowired
    private ThrowingFunction<String, QRCodeDTO> qrCodeFactory;

    @Autowired
    private TgPhotoManagerBot managerBot;

    private Message lastMessage;

    @Override
    public void acceptWithException(@NonNull ParameterInfoNotifyLink notifyLink) throws Exception {
        if (managerBot.hasChatId()) {
            var split = notifyLink.getLink().split("=");
            var encodedLink = encodedLinkTemplate.formatted(
                    split[0],
                    Base64.getEncoder().encodeToString(split[1].getBytes(StandardCharsets.UTF_8))
            );
            var qrCodeDTO = qrCodeFactory.applyWithException(encodedLink);
            try (var inputStream = qrCodeDTO.inputStream()) {
                if (lastMessage != null) {
                    var inputMediaPhoto = new InputMediaPhoto();
                    inputMediaPhoto.setMedia(inputStream, qrCodeDTO.filename());
                    inputMediaPhoto.setCaption(lastMessage.getCaption());
                    var editMessageMedia = EditMessageMedia
                            .builder()
                            .chatId(managerBot.getChatId())
                            .messageId(lastMessage.getMessageId())
                            .media(inputMediaPhoto)
                            .build();
                    managerBot.execute(editMessageMedia);
                } else {
                    var sendPhoto = SendPhoto
                            .builder()
                            .chatId(managerBot.getChatId())
                            .photo(new InputFile(inputStream, qrCodeDTO.filename()))
                            .caption("Please login")
                            .build();
                    lastMessage = managerBot.execute(sendPhoto);
                }
            }
        }
    }
}
