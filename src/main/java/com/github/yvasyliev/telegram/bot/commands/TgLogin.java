package com.github.yvasyliev.telegram.bot.commands;

import com.github.yvasyliev.telegram.client.interaction.QRCodeSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service("/tglogin")
public class TgLogin extends SingleChatCommand {
    @Value("""
            On your smartphone go to
                        
            *Settings* — *Devices* — *Link Desktop Device*
                        
            and scan the QR code below\\. 👇👇👇""")
    private String reply;

    @Autowired
    private QRCodeSender qrCodeSender;

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
        managerBot.execute(sendMessage);
        managerBot.execute(sendChatAction);
        managerBot.setChatId(chatId);
        qrCodeSender.sendQRCode();
    }
}
