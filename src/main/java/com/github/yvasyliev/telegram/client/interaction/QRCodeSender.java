package com.github.yvasyliev.telegram.client.interaction;

import com.github.yvasyliev.model.AuthorizationStateUpdate;
import com.github.yvasyliev.model.CommandReceived;
import com.github.yvasyliev.model.QRCode;
import com.github.yvasyliev.model.QRCodeRequested;
import it.tdlight.client.ParameterInfoNotifyLink;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingFunction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class QRCodeSender implements Consumer<ParameterInfoNotifyLink> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QRCodeSender.class);

    @Autowired
    private ThrowingFunction<String, QRCode> qrCodeFactory;

    @Autowired
    private AbsSender sender;

    @Autowired
    private Supplier<Long> chatIdGetter;

    private ParameterInfoNotifyLink notifyLink;

    private boolean qrCodeRequested;

    private Message lastMessage;

    @Override
    public void accept(ParameterInfoNotifyLink notifyLink) {
        this.notifyLink = notifyLink;
        if (qrCodeRequested) {
            sendQRCode();
        }
    }

    @EventListener
    public void onAuthorizationStateUpdate(AuthorizationStateUpdate authorizationStateUpdate) {
        var authorizationState = authorizationStateUpdate.updateAuthorizationState().authorizationState;
        if (!(authorizationState instanceof TdApi.AuthorizationStateWaitOtherDeviceConfirmation)) {
            resetState();
        }
    }

    @EventListener
    public void onCommandReceived(CommandReceived ignoredCommandReceived) {
        resetState();
    }

    @EventListener
    public void onQRCodeRequested(QRCodeRequested ignoredQrCodeRequested) {
        qrCodeRequested = true;
        sendQRCode();
    }

    private void sendQRCode() {
        if (notifyLink != null) {
            try {
                var qrCodeDTO = qrCodeFactory.applyWithException(notifyLink.getLink());
                try (var inputStream = qrCodeDTO.inputStream()) {
                    if (lastMessage != null) {
                        editQRCode(inputStream, qrCodeDTO.filename());
                    } else {
                        lastMessage = sendQRCode(inputStream, qrCodeDTO.filename());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to deliver QR code.", e);
            }
        }
    }

    private Message sendQRCode(InputStream inputStream, String filename) {
        var sendPhoto = SendPhoto
                .builder()
                .chatId(chatIdGetter.get())
                .photo(new InputFile(inputStream, filename))
                .build();
        try {
            return sender.execute(sendPhoto);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send QR code.", e);
            return null;
        }
    }

    private void editQRCode(InputStream inputStream, String filename) {
        var inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(inputStream, filename);
        var editMessageMedia = EditMessageMedia
                .builder()
                .chatId(chatIdGetter.get())
                .messageId(lastMessage.getMessageId())
                .media(inputMediaPhoto)
                .build();
        try {
            sender.execute(editMessageMedia);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to edit QR code.", e);
        }
    }

    private void deleteQRCode() {
        if (lastMessage != null) {
            var deleteMessage = DeleteMessage
                    .builder()
                    .chatId(lastMessage.getChatId())
                    .messageId(lastMessage.getMessageId())
                    .build();
            try {
                sender.execute(deleteMessage);
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to delete QR code: {}", lastMessage, e);
            }
        }
    }

    private void resetState() {
        qrCodeRequested = false;
        deleteQRCode();
        lastMessage = null;
    }
}
