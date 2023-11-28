package com.github.yvasyliev.telegram.client.interaction;

import com.github.yvasyliev.model.CommandReceived;
import com.github.yvasyliev.model.QRCode;
import com.github.yvasyliev.telegram.bot.TgPhotoManagerBot;
import it.tdlight.client.ParameterInfoNotifyLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingConsumer;
import org.springframework.util.function.ThrowingFunction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

@Component
public class QRCodeSender implements ThrowingConsumer<ParameterInfoNotifyLink> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QRCodeSender.class);

    @Autowired
    private ThrowingFunction<String, QRCode> qrCodeFactory;

    @Autowired
    private TgPhotoManagerBot managerBot;

    private ParameterInfoNotifyLink notifyLink;

    private Message lastMessage;

    @Override
    public void acceptWithException(@NonNull ParameterInfoNotifyLink notifyLink) throws Exception {
        this.notifyLink = notifyLink;
        if (managerBot.hasChatId()) {
            sendQRCode();
        }
    }

    @EventListener
    public void onCommandReceived(CommandReceived commandReceived) {
        if (lastMessage != null && commandReceived.getSource() instanceof Message message) {
            var deleteMessage = DeleteMessage
                    .builder()
                    .chatId(message.getChatId())
                    .messageId(lastMessage.getMessageId())
                    .build();
            try {
                managerBot.execute(deleteMessage);
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to delete message: {}", message, e);
            }
        }
        lastMessage = null;
    }

    public void sendQRCode() throws Exception {
        if (notifyLink != null) {
            var qrCodeDTO = qrCodeFactory.applyWithException(notifyLink.getLink());
            try (var inputStream = qrCodeDTO.inputStream()) {
                if (lastMessage != null) {
                    editQRCode(inputStream, qrCodeDTO.filename());
                } else {
                    lastMessage = sendQRCode(inputStream, qrCodeDTO.filename());
                }
            }
        }
    }

    private Message sendQRCode(InputStream inputStream, String filename) throws TelegramApiException {
        var sendPhoto = SendPhoto
                .builder()
                .chatId(managerBot.getChatId())
                .photo(new InputFile(inputStream, filename))
                .build();
        return managerBot.execute(sendPhoto);
    }

    private void editQRCode(InputStream inputStream, String filename) throws TelegramApiException {
        var inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(inputStream, filename);
        var editMessageMedia = EditMessageMedia
                .builder()
                .chatId(managerBot.getChatId())
                .messageId(lastMessage.getMessageId())
                .media(inputMediaPhoto)
                .build();
        managerBot.execute(editMessageMedia);
    }
}
