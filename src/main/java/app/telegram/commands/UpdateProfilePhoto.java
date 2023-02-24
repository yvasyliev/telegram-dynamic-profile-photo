package app.telegram.commands;

import api.deezer.DeezerApi;
import api.deezer.objects.Track;
import app.telegram.client.SyncTelegramClient;
import app.telegram.service.ImageProcessor;
import app.telegram.suppliers.TrackToPrint;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class UpdateProfilePhoto implements Command {
    @Autowired
    private DeezerApi deezerApi;

    @Autowired
    @Qualifier("appProperties")
    private Properties appProperties;

    @Value("#{appProperties.contains('deezer.last_track') ? appProperties.getProperty('deezer.last_track') : 0}")
    private long lastTrackId;

    @Autowired
    private TrackToPrint trackToPrint;

    @Resource
    private Queue<ImageProcessor> imageProcessorQueue;

    @Autowired
    private SyncTelegramClient telegramClient;

    @Autowired
    private File photo;

    @Override
    public void execute() throws Exception {
        Track lastTrack = deezerApi.user().getMyHistory().limit(1).execute().getData().get(0);
        if (!lastTrack.getId().equals(lastTrackId)) {
            trackToPrint.setTrack(lastTrack);

            BufferedImage cover = ImageIO.read(new URL(lastTrack.getAlbum().getCoverXl()));
            imageProcessorQueue.forEach(imageProcessor -> imageProcessor.process(cover));

            ImageIO.write(cover, "png", photo);

            deleteCurrentPhoto();

            telegramClient.send(new TdApi.SetProfilePhoto(
                    new TdApi.InputChatPhotoStatic(new TdApi.InputFileLocal(photo.getPath())),
                    true
            ));

            appProperties.setProperty("deezer.last_track", lastTrack.getId().toString());
        }
    }

    private void deleteCurrentPhoto() throws ExecutionException, InterruptedException, TimeoutException {
        TdApi.User me = telegramClient.send(new TdApi.GetMe());
        TdApi.ChatPhoto[] photos = telegramClient.send(new TdApi.GetUserProfilePhotos(me.id, 0, 1)).photos;
        if (photos != null && photos.length > 0) {
            telegramClient.send(new TdApi.DeleteProfilePhoto(photos[0].id));
        }
    }
}
