package app.telegram.commands;

import api.deezer.DeezerApi;
import app.telegram.clients.SyncTelegramClient;
import app.telegram.services.ImageProcessor;
import app.telegram.suppliers.TrackToPrint;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Updates Telegram profile photo.
 */
public class UpdateProfilePhoto implements Command {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProfilePhoto.class);

    /**
     * Deezer client.
     */
    @Autowired
    private DeezerApi deezerApi;

    /**
     * Application properties.
     */
    @Autowired
    @Qualifier("appProperties")
    private Properties appProperties;

    /**
     * Last track ID.
     */
    @Value("${deezer.last_track:0}")
    private long lastTrackId;

    /**
     * Track to print.
     */
    @Autowired
    private TrackToPrint trackToPrint;

    /**
     * Image processors.
     */
    @Autowired
    @Qualifier("imageProcessorQueue")
    private Queue<ImageProcessor> imageProcessorQueue;

    /**
     * Telegram client.
     */
    @Autowired
    private SyncTelegramClient telegramClient;

    /**
     * Profile photo.
     */
    @Autowired
    private File photo;

    @Value("https://e-cdn-images.dzcdn.net/images/cover/%s/1000x1000-000000-80-0-0.jpg")
    private String coverUrlTemplate;

    @Override
    public void execute() throws Exception {
        var lastTrack = deezerApi.user().getMyHistory().limit(1).execute().getData().get(0);
        if (!lastTrack.getId().equals(lastTrackId)) {
            trackToPrint.setTrack(lastTrack);

            var album = lastTrack.getAlbum();
            var coverUrl = album.getCoverXl();
            if (coverUrl == null) {
                coverUrl = coverUrlTemplate.formatted(album.getMd5Image());
            }

            var cover = ImageIO.read(new URL(coverUrl));
            imageProcessorQueue.forEach(imageProcessor -> imageProcessor.process(cover));

            ImageIO.write(cover, "png", photo);

            deleteCurrentPhoto();

            telegramClient.send(new TdApi.SetProfilePhoto(
                    new TdApi.InputChatPhotoStatic(new TdApi.InputFileLocal(photo.getPath())),
                    true
            ));

            appProperties.setProperty("deezer.last_track", lastTrack.getId().toString());
            LOGGER.info("Updated Telegram profile photo.");
        }
    }

    /**
     * Deletes current Telegram profile photo.
     *
     * @throws ExecutionException   if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    private void deleteCurrentPhoto() throws ExecutionException, InterruptedException, TimeoutException {
        var me = telegramClient.send(new TdApi.GetMe());
        var photos = telegramClient.send(new TdApi.GetUserProfilePhotos(me.id, 0, 1)).photos;
        if (photos != null && photos.length > 0) {
            telegramClient.send(new TdApi.DeleteProfilePhoto(photos[0].id));
        }
    }
}
