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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class QRCodeSender implements ThrowingConsumer<ParameterInfoNotifyLink> {
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
            var qrCodeDTO = qrCodeFactory.applyWithException(encodeLink(notifyLink));
            try (var inputStream = qrCodeDTO.inputStream()) {
                if (lastMessage != null) {
                    editQRCode(inputStream, qrCodeDTO.filename());
                } else {
                    lastMessage = sendQRCode(inputStream, qrCodeDTO.filename());
                }
            }
        }
    }

    private String encodeLink(ParameterInfoNotifyLink notifyLink) {
        var split = notifyLink.getLink().split("=");
        return encodedLinkTemplate.formatted(
                split[0],
                Base64.getEncoder().encodeToString(split[1].getBytes(StandardCharsets.UTF_8))
        );
    }

    private Message sendQRCode(InputStream inputStream, String filename) throws TelegramApiException {
        var sendPhoto = SendPhoto
                .builder()
                .chatId(managerBot.getChatId())
                .photo(new InputFile(inputStream, filename))
                .caption("Please login")
                .build();
        return managerBot.execute(sendPhoto);
    }

    private void editQRCode(InputStream inputStream, String filename) throws TelegramApiException {
        var inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(inputStream, filename);
        inputMediaPhoto.setCaption(lastMessage.getCaption());
        var editMessageMedia = EditMessageMedia
                .builder()
                .chatId(managerBot.getChatId())
                .messageId(lastMessage.getMessageId())
                .media(inputMediaPhoto)
                .build();
        managerBot.execute(editMessageMedia);
    }
}
