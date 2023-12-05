package com.github.yvasyliev.telegram.bot.commands;

import com.github.yvasyliev.model.AuthorizationStateWaitOtherDeviceConfirmationEvent;
import com.github.yvasyliev.model.QrCode;
import com.github.yvasyliev.telegram.client.TdLightClient;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingFunction;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

@Component("/tglogin")
public class TgLogin extends SingleChatCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(TgLogin.class);

    @Value("""
            On your smartphone go to
                        
            *Settings* — *Devices* — *Link Desktop Device*
                        
            and scan the QR code below\\. 👇👇👇""")
    private String reply;

    @Autowired
    private TdLightClient client;

    @Autowired
    private ThrowingFunction<String, QrCode> qrCodeFactory;

    private boolean qrCodeRequested;

    private Message lastMessage;

    @Override
    public void execute(Message message) throws Exception {
        var chatId = message.getChatId();
        var sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(reply)
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        var sendChatAction = SendChatAction
                .builder()
                .chatId(chatId)
                .action(ActionType.UPLOADPHOTO.toString())
                .build();
        bot.execute(sendMessage);
        bot.execute(sendChatAction);
        qrCodeRequested = true;
        bot.setChatId(chatId);
        var authorizationState = client.getAuthorizationState().get();
        if (authorizationState instanceof TdApi.AuthorizationStateWaitPhoneNumber) {
            client.requestQrCodeAuthentication();
        } else if (authorizationState instanceof TdApi.AuthorizationStateWaitOtherDeviceConfirmation state) {
            sendQrCode(state);
        }
    }

    @EventListener
    public void onAuthorizationStateWaitOtherDeviceConfirmation(AuthorizationStateWaitOtherDeviceConfirmationEvent event) {
        if (qrCodeRequested) {
            try {
                sendQrCode(event.state());
            } catch (Exception e) {
                LOGGER.error("Failed to send QR code.", e);
            }
        }
    }

    private void sendQrCode(TdApi.AuthorizationStateWaitOtherDeviceConfirmation state) throws Exception {
        var qrCode = qrCodeFactory.applyWithException(state.link);
        try (var inputStream = qrCode.inputStream()) {
            if (lastMessage == null) {
                lastMessage = sendQrCode(inputStream, qrCode.filename());
            } else {
                editQrCode(inputStream, qrCode.filename());
            }
        }
    }

    private Message sendQrCode(InputStream inputStream, String filename) throws TelegramApiException {
        var sendPhoto = SendPhoto
                .builder()
                .chatId(bot.getChatId())
                .photo(new InputFile(inputStream, filename))
                .build();
        return bot.execute(sendPhoto);
    }

    private void editQrCode(InputStream inputStream, String filename) throws TelegramApiException {
        var inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(inputStream, filename);
        var editMessageMedia = EditMessageMedia
                .builder()
                .chatId(lastMessage.getChatId())
                .messageId(lastMessage.getMessageId())
                .media(inputMediaPhoto)
                .build();
        bot.execute(editMessageMedia);
    }
}
