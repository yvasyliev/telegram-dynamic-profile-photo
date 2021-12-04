package app.telegram;

import api.deezer.DeezerApi;
import api.deezer.exceptions.DeezerException;
import api.deezer.objects.AccessToken;
import api.deezer.objects.Permission;
import api.deezer.objects.Track;
import api.deezer.objects.User;
import app.telegram.exceptions.ChangeTelegramPhotoException;
import app.telegram.exceptions.DeezerLoginException;
import app.telegram.exceptions.TelegramLoginException;
import app.telegram.exceptions.TelegramLogoutException;
import app.telegram.graphics.ImageBrightness;
import app.telegram.graphics.ImageBrightnessImpl;
import app.telegram.graphics.ImageTrack;
import app.telegram.graphics.OutlinedImageTrack;
import app.telegram.properties.AppProperties;
import app.telegram.server.DefaultHttpServer;
import app.telegram.server.Server;
import app.telegram.tg.TgClient;
import it.tdlight.common.utils.CantLoadLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class TelegramDeezerClient {
    /**
     * Logger object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramDeezerClient.class);

    /**
     * Application properties.
     */
    private static final AppProperties APP_PROPERTIES = new AppProperties();

    /**
     * Telegram client.
     */
    private static final TgClient TG_CLIENT = new TgClient();

    /**
     * Start point.
     *
     * @param args running mode.
     */
    public static void main(String[] args) {
        try {
            APP_PROPERTIES.load();

            switch (args[0]) {
                case "deezer.login":
                    loginDeezer();
                    break;

                case "telegram.login":
                    loginTelegram();
                    break;

                case "telegram.logout":
                    logoutTelegram();
                    break;

                case "telegram.change_photo":
                    changeTelegramPhoto();
                    break;
            }

            APP_PROPERTIES.save();
        } catch (DeezerLoginException e) {
            LOGGER.error("Failed to login Deezer.", e);
        } catch (TelegramLoginException e) {
            LOGGER.error("Failed to login Telegram.", e);
        } catch (TelegramLogoutException e) {
            LOGGER.error("Failed to logout Telegram.", e);
        } catch (ChangeTelegramPhotoException e) {
            LOGGER.error("Failed to change Telegram photo.", e);
        } catch (IOException e) {
            LOGGER.error("Failed to read/write AppProperties.", e);
        }
    }

    /**
     * Logins to Deezer.
     *
     * @throws DeezerLoginException if errors occur.
     */
    public static void loginDeezer() throws DeezerLoginException {
        try {
            int appId = Integer.parseInt(APP_PROPERTIES.getProperty("deezer.app_id"));
            String redirectUri = APP_PROPERTIES.getProperty("deezer.redirect_uri");
            String secret = APP_PROPERTIES.getProperty("deezer.secret");
            int port = Integer.parseInt(APP_PROPERTIES.getProperty("deezer.server_port", "7000"));
            int loginTimeout = Integer.parseInt(APP_PROPERTIES.getProperty("deezer.login_timeout", "10000"));

            DeezerApi deezerApi = new DeezerApi();
            String loginUrl = deezerApi.auth().getLoginUrl(appId, redirectUri, Permission.LISTENING_HISTORY);
            System.out.println("Please follow the link:\n" + loginUrl);

            Server server = new DefaultHttpServer(port);
            Map<String, String> urlParams = server.getURLParams(loginTimeout);

            AccessToken accessToken = deezerApi.auth().getAccessToken(appId, secret, urlParams.get("code")).execute();
            deezerApi.setAccessToken(accessToken);
            APP_PROPERTIES.setProperty("deezer.access_token", accessToken.getAccessToken());

            User me = deezerApi.user().getMe().execute();
            APP_PROPERTIES.setProperty("deezer.me", String.valueOf(me.getId()));
        } catch (IOException | DeezerException e) {
            throw new DeezerLoginException(e);
        }
    }

    /**
     * Logins to Telegram.
     *
     * @throws TelegramLoginException if errors occur.
     */
    public static void loginTelegram() throws TelegramLoginException {
        try {
            String phoneNumber = APP_PROPERTIES.getProperty("telegram.phone_number");
            int apiId = Integer.parseInt(APP_PROPERTIES.getProperty("telegram.api_id"));
            String apiHash = APP_PROPERTIES.getProperty("telegram.api_hash");

            TG_CLIENT.init(apiId, apiHash);
            TG_CLIENT.login(phoneNumber);
            TG_CLIENT.close();
        } catch (CantLoadLibrary | ExecutionException | InterruptedException | TimeoutException e) {
            throw new TelegramLoginException(e);
        }
    }

    /**
     * Logouts from Telegram.
     *
     * @throws TelegramLogoutException if errors occur.
     */
    public static void logoutTelegram() throws TelegramLogoutException {
        try {
            int apiId = Integer.parseInt(APP_PROPERTIES.getProperty("telegram.api_id"));
            String apiHash = APP_PROPERTIES.getProperty("telegram.api_hash");

            TG_CLIENT.init(apiId, apiHash);
            TG_CLIENT.logout();
            TG_CLIENT.close();
        } catch (CantLoadLibrary | ExecutionException | InterruptedException | TimeoutException e) {
            throw new TelegramLogoutException(e);
        }
    }

    /**
     * Changes Telegram photo.
     *
     * @throws ChangeTelegramPhotoException if errors occur.
     */
    public static void changeTelegramPhoto() throws ChangeTelegramPhotoException {
        try {
            int apiId = Integer.parseInt(APP_PROPERTIES.getProperty("telegram.api_id"));
            String apiHash = APP_PROPERTIES.getProperty("telegram.api_hash");
            String accessToken = APP_PROPERTIES.getProperty("deezer.access_token");
            String lastTrackId = APP_PROPERTIES.getProperty("deezer.last_track");

            DeezerApi deezerApi = new DeezerApi(accessToken);

            Track lastTrack = deezerApi.user().getMyHistory().limit(1).execute().getData().get(0);

            if (lastTrackId == null || !lastTrackId.equals(String.valueOf(lastTrack.getId()))) {
                BufferedImage cover = ImageIO.read(new URL(lastTrack.getAlbum().getCoverXl()));

                ImageBrightness imageBrightness = new ImageBrightnessImpl();
                imageBrightness.makeDarker(cover);

                ImageTrack imageTrack = new OutlinedImageTrack();
                imageTrack.printTrack(cover, lastTrack);

                File photo = new File("cover.jpg");
                ImageIO.write(cover, "jpg", photo);

                TG_CLIENT.init(apiId, apiHash);
                TG_CLIENT.deleteLastProfilePhoto();
                TG_CLIENT.updateProfilePhoto(photo);
                TG_CLIENT.close();

                APP_PROPERTIES.setProperty("deezer.last_track", String.valueOf(lastTrack.getId()));
            }
        } catch (CantLoadLibrary | IOException | ExecutionException | InterruptedException | DeezerException | TimeoutException e) {
            throw new ChangeTelegramPhotoException(e);
        }
    }
}
